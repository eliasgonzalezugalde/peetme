package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
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

import java.util.Date;
import java.util.Random;

public class VetCreateActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "VET_CREATE_ACTIVITY";
    private EditText nameField, phoneField;
    private TextView textViewLocationResult;
    private ImageButton selectImage;
    private Button submitBtn, locationBtn;
    private Switch switchOpen247;
    private Uri imageUri = null;
    private static final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseVetLocations;
    private DatabaseReference mDatabaseVet;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Date date;
    private CharSequence charSequence;
    private String imgSaved;
    private boolean imgChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_create);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameField = (EditText) findViewById(R.id.nameField);
        phoneField = (EditText) findViewById(R.id.phoneField);
        selectImage = (ImageButton) findViewById(R.id.selectImage);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        switchOpen247 = (Switch) findViewById(R.id.switchOpen247);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseVetLocations = FirebaseDatabase.getInstance().getReference().child("locations");
        mDatabaseVetLocations.keepSynced(true);
        mDatabaseVet = FirebaseDatabase.getInstance().getReference().child("vet");
        mDatabaseVet.keepSynced(true);
        date = new Date();
        charSequence = DateFormat.format("dd/MM/yyyy", date.getTime());
        mProgress = new ProgressDialog(this);
        imgSaved = "";
        imgChange = false;

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("vet_id")){
                    String vet_id = getIntent().getStringExtra("vet_id");
                    startUpdate(vet_id);
                } else {
                    startPosting();
                }

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(CropImage.getPickImageChooserIntent(VetCreateActivity.this, getResources().getString(R.string.add_image_from), true), 200);
            }
        });

        //Google Play Services & Get the Last Known Location
        buildGoogleApiClient();

        if (getIntent().hasExtra("vet_id")) {

            fillFields();

        }

    }

    private void startUpdate(final String vet_id) {

        mProgress.setMessage(getResources().getString(R.string.updating_info) + " ...");
        mProgress.show();
        mProgress.setCancelable(false);

        //VALIDATING FIELDS
        if (fieldsAreFilled()) {

            if (imgChange) {

                final StorageReference imgToDeleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgSaved); //IMG TO DELETE
                StorageReference filepath = mStorage.child("Images").child("vet" + randomString());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //SI SE SUVE LA NUEVA IMG

                        final Uri donwloadUrl = taskSnapshot.getDownloadUrl();
                        updateFieldsNoImage(vet_id); //UPDATE ALL THE FIELDS, EXCEPT THE IMAGE

                        imgToDeleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) { //SI SE BORRA LA VIEJA IMG
                                mDatabaseVet.child(vet_id).child("image").setValue(donwloadUrl.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                mProgress.dismiss();
                                Log.i(TAG, String.valueOf(exception.getMessage()));
                            }
                        });

                        //HERE
                        mProgress.dismiss();
                        finish();
                        Toast.makeText(VetCreateActivity.this, getResources().getString(R.string.vet_saved), Toast.LENGTH_SHORT).show();

                    }
                });


            } else {
                updateFieldsNoImage(vet_id);
                mProgress.dismiss();
                finish();
                Toast.makeText(VetCreateActivity.this, getResources().getString(R.string.vet_saved), Toast.LENGTH_SHORT).show();
            }


        } else {
            mProgress.dismiss();
            Toast.makeText(this, getResources().getString(R.string.fields_empty), Toast.LENGTH_SHORT).show();
        }

    }

    public boolean fieldsAreFilled() {
        if (!TextUtils.isEmpty(nameField.getText().toString().trim())
                && !TextUtils.isEmpty(phoneField.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

    private void updateFieldsNoImage(final String vet_id) {

        mDatabaseVet.child(vet_id).child("name").setValue(nameField.getText().toString().trim());
        mDatabaseVet.child(vet_id).child("phone").setValue(phoneField.getText().toString().trim());

        if (switchOpen247.isChecked()) {
            mDatabaseVet.child(vet_id).child("open247").setValue(true);
        } else {
            mDatabaseVet.child(vet_id).child("open247").setValue(false);
        }

    }

    private void startPosting() {

        mProgress.setMessage(getResources().getString(R.string.saving) + " ...");
        mProgress.show();
        mProgress.setCancelable(false);

        final String name_val = nameField.getText().toString().trim();
        final String phone_val = phoneField.getText().toString().trim();

        final boolean open247_val;

        if (switchOpen247.isChecked()) {
            open247_val = true;
        } else {
            open247_val = false;
        }

        if (fieldsAreFilled() && imageUri != null) {

            //StorageReference filepath = mStorage.child("Images").child(imageUri.getLastPathSegment());
            StorageReference filepath = mStorage.child("Images").child("vet" + randomString());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri donwloadUrl = taskSnapshot.getDownloadUrl();

                    GeoFire geoFirePet = new GeoFire(mDatabaseVetLocations);
                    DatabaseReference newPet = mDatabaseVet.push();
                    newPet.child("image").setValue(donwloadUrl.toString());
                    newPet.child("name").setValue(nameField.getText().toString().trim());
                    newPet.child("phone").setValue(phone_val);
                    newPet.child("reports").setValue("0");
                    newPet.child("open247").setValue(open247_val);
                    newPet.child("modifiedDate").setValue(String.valueOf(charSequence));
                    newPet.child("createdDate").setValue(String.valueOf(charSequence));
                    newPet.child("active").setValue(true);
                    newPet.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    geoFirePet.setLocation("vet" + newPet.getKey(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                System.err.println("There was an error saving the location to GeoFire: " + error);
                            } else {
                                System.out.println("Location saved on server successfully!");
                            }
                        }
                    });

                    mProgress.dismiss();
                    finish();
                    Toast.makeText(VetCreateActivity.this, getResources().getString(R.string.vet_saved), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            mProgress.dismiss();
            Toast.makeText(VetCreateActivity.this, getResources().getString(R.string.validation), Toast.LENGTH_SHORT).show();
        }

    }

    private void fillFields() {

        mDatabaseVet.child(getIntent().getExtras().getString("vet_id")).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //IMG FOR UPDATING
                imgSaved = (String) dataSnapshot.child("image").getValue();

                Picasso.with(VetCreateActivity.this).load(imgSaved).into(selectImage);
                nameField.setText((String) dataSnapshot.child("name").getValue());
                phoneField.setText((String) dataSnapshot.child("phone").getValue());
                if ((boolean) dataSnapshot.child("open247").getValue()) {
                    switchOpen247.setChecked(true);
                }
                getSupportActionBar().setTitle((String) dataSnapshot.child("name").getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }

        });

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
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            Log.i("Latitude", String.valueOf(mLastLocation.getLatitude()));
            Log.i("Longitude", String.valueOf(mLastLocation.getLongitude()));

        } else {
            Log.i("LOCATION PROBLEM", " --> NULL");
        }

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
