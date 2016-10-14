package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

public class CreateActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageButton selectImage;
    private EditText nameField;
    private EditText descField;
    private Button submitBtn;
    private Uri imageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("pet");

        selectImage = (ImageButton) findViewById(R.id.selectImage);
        nameField = (EditText) findViewById(R.id.nameField);
        descField = (EditText) findViewById(R.id.descField);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        mProgress = new ProgressDialog(this);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ADD IMG BTN
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT );
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SUBMIT BTN
                startPosting();

            }
        });

    }

    private void startPosting() {

        mProgress.setMessage("Posting ...");
        mProgress.show();
        final String name_val = nameField.getText().toString().trim();
        final String desc_val = descField.getText().toString().trim();

        if (!TextUtils.isEmpty(name_val) && !TextUtils.isEmpty(desc_val) && imageUri != null ) {

            //StorageReference filepath = mStorage.child("Pet_Images").child(imageUri.getLastPathSegment());
            StorageReference filepath = mStorage.child("Pet_Images").child(random());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri donwloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPet = mDatabase.push();
                    newPet.child("name").setValue(name_val);
                    newPet.child("description").setValue(desc_val);
                    newPet.child("image").setValue(donwloadUrl.toString());
                    //newPet.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mProgress.dismiss();
                    Toast.makeText(CreateActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateActivity.this, AnimalMapsActivity.class));

                }
            });

        }

    }

    public static String random() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(22);
        char tempChar;

        for (int i = 0; i < randomLength; i++){

            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);

        }

        return randomStringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(4,3)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            //aqui imagen
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                selectImage.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}
