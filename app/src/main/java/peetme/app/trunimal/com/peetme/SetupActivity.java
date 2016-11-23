package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = "SETUP_ACTIVITY";
    private EditText nameField;
    private TextView emailField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton selectImage;
    Button submitBtn;
    private AlertDialog.Builder builder = null;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailField = (TextView) findViewById(R.id.emailField);
        nameField = (EditText) findViewById(R.id.nameField);
        selectImage = (ImageButton) findViewById(R.id.selectImage);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        mProgress = new ProgressDialog(this);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SetupActivity.this, LoginWithActivity.class));
                } else {
                    emailField.setText(firebaseAuth.getCurrentUser().getEmail());
                    nameField.setText(firebaseAuth.getCurrentUser().getDisplayName());
                    if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
                        Log.i(TAG, String.valueOf(firebaseAuth.getCurrentUser().getPhotoUrl()));
                        //selectImage.setImageURI(Uri.parse(String.valueOf(firebaseAuth.getCurrentUser().getPhotoUrl())));
                    }

                }

            }
        };

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(CropImage.getPickImageChooserIntent(SetupActivity.this, getResources().getString(R.string.add_image_from), true), 200);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

    }

    private void saveProfile() {

        //if los campos no estan vacios
        if (!TextUtils.isEmpty(nameField.getText().toString().trim())) {
            mProgress.setMessage(getResources().getString(R.string.saving));
            mProgress.show();

            StorageReference filepath = mStorage.child("User_Images").child(randomString());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri donwloadUrl = taskSnapshot.getDownloadUrl();
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = mDatabaseUsers.child(user_id);
                    current_user_db.child("name").setValue(nameField.getText().toString().trim());
                    current_user_db.child("image").setValue(donwloadUrl.toString());
                    finish();
                    Toast.makeText(SetupActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                    finish();
                    startActivity(new Intent(SetupActivity.this, MainActivity.class));
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.validation), Toast.LENGTH_SHORT).show();
        }


    }

    public static String randomString() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(22);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
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
            startCropImageActivity(imageUri);
        }

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == this.RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(this, data);
            startCropImageActivity(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                selectImage.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 3)
                .start(this);

        //CropImage.getPickImageChooserIntent(getApplicationContext(), getApplicationContext().getString(R.string.pick_image_intent_chooser_title), false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
