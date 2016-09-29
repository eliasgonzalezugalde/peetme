package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageActivity extends AppCompatActivity {

    private StorageReference mStorage;
    Button btnStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        mStorage = FirebaseStorage.getInstance().getReference();

        btnStorage = (Button) findViewById(R.id.btnStorage);

        mProgres = new ProgressDialog(this);

        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && requestCode == RESULT_OK) {

            Toast.makeText(StorageActivity.this, "Yes", Toast.LENGTH_SHORT).show();

            mProgres.setMessage("Uploadgin image");
            mProgres.show();

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(StorageActivity.this, "Upload done", Toast.LENGTH_SHORT).show();
                    mProgres.dismiss();

                }
            });

        } else {
            Toast.makeText(StorageActivity.this, "No", Toast.LENGTH_SHORT).show();
        }

    }
}
