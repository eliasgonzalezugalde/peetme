package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

public class MyAccountActivity extends AppCompatActivity {

    private static final String TAG = "MY_ACCOUNT_ACTIVITY";
    private EditText nameField;
    private TextView emailField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton selectImage;
    Button submitBtn;
    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorage;
    private String imgSaved, userId;
    private boolean imgChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailField = (TextView) findViewById(R.id.emailField);
        nameField = (EditText) findViewById(R.id.nameField);
        selectImage = (ImageButton) findViewById(R.id.selectImage);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        mProgress = new ProgressDialog(this);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
        imgSaved = "";
        imgChange = false;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MyAccountActivity.this, LoginWithActivity.class));
                } else {

                    emailField.setText(firebaseAuth.getCurrentUser().getEmail());
                    userId = firebaseAuth.getCurrentUser().getUid();

                    Log.i(TAG, String.valueOf(mDatabaseUsers.child(userId).getRef()));

                    mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(userId)) { //USER DATA ALREADY STORED IN THE DATABASE

                                //fillFields()
                                imgSaved = (String) dataSnapshot.child(userId).child("image").getValue(); //IMG FOR UPDATING
                                if (!imgChange) {
                                    Picasso.with(MyAccountActivity.this).load(imgSaved).into(selectImage);
                                }
                                nameField.setText((String) dataSnapshot.child(userId).child("name").getValue());

                            } else { //NO USER DATA STORED IN THE DATABASE

                                if (firebaseAuth.getCurrentUser().getDisplayName() != null) {
                                    nameField.setText(firebaseAuth.getCurrentUser().getDisplayName());
                                }

                                if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) { //IF IMG EXIST, SHOW IT
                                    Picasso.with(MyAccountActivity.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(selectImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        };

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(CropImage.getPickImageChooserIntent(MyAccountActivity.this, getResources().getString(R.string.add_image_from), true), 200);
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

        //VALIDATING FIELDS
        if (!TextUtils.isEmpty(nameField.getText().toString().trim())) {

            mProgress.setMessage(getResources().getString(R.string.saving));
            mProgress.show();

            if (imgChange) {

                StorageReference filepath = mStorage.child("User_Images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri donwloadUrl = taskSnapshot.getDownloadUrl();

                        DatabaseReference current_user_db = mDatabaseUsers.child(userId);
                        current_user_db.child("name").setValue(nameField.getText().toString().trim());
                        current_user_db.child("image").setValue(donwloadUrl.toString());
                        Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        //finish();
                        //startActivity(new Intent(MyAccountActivity.this, MainActivity.class));
                    }
                });
            } else {

                mDatabaseUsers.child(userId).child("name").setValue(nameField.getText().toString().trim());
                //mDatabaseUsers.child(userId).child("image").setValue(imgSaved);
                mProgress.dismiss();

            }


        } else {
            Toast.makeText(this, getResources().getString(R.string.validation), Toast.LENGTH_SHORT).show();
        }

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
                imgChange = true;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
