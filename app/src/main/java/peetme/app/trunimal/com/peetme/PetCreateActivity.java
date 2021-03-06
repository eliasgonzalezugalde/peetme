package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PetCreateActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int GALLERY_REQUEST = 1;
    private static final String TAG = "PET_CREATE_ACTIVITY";
    private Date date;
    private CharSequence charSequence;
    private TextView textViewLocationResult;
    private ImageButton selectImage;
    private EditText nameField, descField, additionalInfoField, phoneField;
    private Button submitBtn, locationBtn;
    private Uri imageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabasePet;
    private DatabaseReference mDatabasePetLocations;
    private ProgressDialog mProgress;
    private Spinner spinnerAge;
    private Spinner spinnerHealth;
    private Spinner spinnerSize;
    private Spinner spinnerSpecies;
    private Spinner spinnerGender;
    private Switch switchCastrated, switchVaccinated, switchWormed;
    private AlertDialog.Builder builder = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String imgSaved;
    private boolean imgChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabasePet = FirebaseDatabase.getInstance().getReference().child("pet");
        mDatabasePet.keepSynced(true);
        mDatabasePetLocations = FirebaseDatabase.getInstance().getReference().child("locations");
        mDatabasePetLocations.keepSynced(true);

        textViewLocationResult = (TextView) findViewById(R.id.textViewLocationResult);
        selectImage = (ImageButton) findViewById(R.id.selectImage);
        nameField = (EditText) findViewById(R.id.nameField);
        descField = (EditText) findViewById(R.id.descField);
        additionalInfoField = (EditText) findViewById(R.id.additionalInfoField);
        phoneField = (EditText) findViewById(R.id.phoneField);

        switchCastrated = (Switch) findViewById(R.id.switchCastrated);
        switchVaccinated = (Switch) findViewById(R.id.switchVaccinated);
        switchWormed = (Switch) findViewById(R.id.switchWormed);

        submitBtn = (Button) findViewById(R.id.submitBtn);
        locationBtn = (Button) findViewById(R.id.locationBtn);

        spinnerAge = (Spinner) findViewById(R.id.spinnerAge);
        spinnerHealth = (Spinner) findViewById(R.id.spinnerHealth);
        spinnerSize = (Spinner) findViewById(R.id.spinnerSize);
        spinnerSpecies = (Spinner) findViewById(R.id.spinnerSpecies);
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);

        //dropdown's
        String[] age = {getResources().getString(R.string.select), getResources().getString(R.string.select_age_puppy), getResources().getString(R.string.select_age_young), getResources().getString(R.string.select_age_adult), getResources().getString(R.string.select_age_old)};
        String[] health = {getResources().getString(R.string.select), getResources().getString(R.string.select_health_bad), getResources().getString(R.string.select_health_ok), getResources().getString(R.string.select_health_good)};
        String[] size = {getResources().getString(R.string.select), getResources().getString(R.string.select_size_small), getResources().getString(R.string.select_size_medium), getResources().getString(R.string.select_size_large)};
        String[] species = {getResources().getString(R.string.select), getResources().getString(R.string.dog), getResources().getString(R.string.cat), getResources().getString(R.string.rabbit), getResources().getString(R.string.pig), getResources().getString(R.string.bird), getResources().getString(R.string.rodent), getResources().getString(R.string.other)};
        String[] gender = {getResources().getString(R.string.select), getResources().getString(R.string.male), getResources().getString(R.string.female)};

        final List<String> ageList = new ArrayList<>(Arrays.asList(age));
        final List<String> healthList = new ArrayList<>(Arrays.asList(health));
        final List<String> sizeList = new ArrayList<>(Arrays.asList(size));
        final List<String> speciesList = new ArrayList<>(Arrays.asList(species));
        final List<String> genderList = new ArrayList<>(Arrays.asList(gender));

        createDropdown(ageList, spinnerAge);
        createDropdown(healthList, spinnerHealth);
        createDropdown(sizeList, spinnerSize);
        createDropdown(speciesList, spinnerSpecies);
        createDropdown(genderList, spinnerGender);

        date = new Date();
        charSequence = DateFormat.format("dd/MM/yyyy", date.getTime());
        mProgress = new ProgressDialog(this);
        imgSaved = "";
        imgChange = false;

        //Google Play Services & Get the Last Known Location
        buildGoogleApiClient();

        if (getIntent().hasExtra("pet_id")) {

            fillFields();

        }

        /*
        spinnerSpecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), getResources().getString(R.string.species) + ": " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(CropImage.getPickImageChooserIntent(PetCreateActivity.this, getResources().getString(R.string.add_image_from), true), 200);

            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().hasExtra("pet_id")) {
                    String pet_id = getIntent().getStringExtra("pet_id");
                    startUpdate(pet_id);
                } else {
                    startPosting();
                }


            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLastLocation != null) {
                    locationBtn.setText(getResources().getString(R.string.done));
                    locationBtn.setEnabled(false);
                    //locationBtn.setTransformationMethod(null); //boton en minusculas
                    textViewLocationResult.setText(String.valueOf(getAddress(PetCreateActivity.this, mLastLocation).getAddressLine(0)));
                    textViewLocationResult.setVisibility(View.VISIBLE);
                    Log.i(TAG, String.valueOf(getAddress(PetCreateActivity.this, mLastLocation).getAddressLine(0)) + "n/" + String.valueOf(getAddress(PetCreateActivity.this, mLastLocation).getAddressLine(1)));
                } else {
                    Log.i(TAG, "mLastLocation is null");
                }

            }
        });
        textViewLocationResult.setVisibility(View.GONE);

    }

    private void startUpdate(final String pet_id) {

        mProgress.setMessage(getResources().getString(R.string.updating_info));
        mProgress.show();
        mProgress.setCancelable(false);

        //VALIDATING FIELDS
        if (fieldsAreFilled()) {

            if (imgChange) {

                final StorageReference imgToDeleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgSaved); //IMG TO DELETE
                StorageReference filepath = mStorage.child("Images").child("pet" + randomString());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) { //SI SE SUVE LA NUEVA IMG

                        final Uri donwloadUrl = taskSnapshot.getDownloadUrl();
                        updateFieldsNoImage(pet_id);

                        imgToDeleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) { //SI SE BORRA LA VIEJA IMG
                                mDatabasePet.child(pet_id).child("image").setValue(donwloadUrl.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                mProgress.dismiss();
                                Log.i(TAG, String.valueOf(exception.getMessage()));
                            }
                        });

                        mProgress.dismiss();
                        finish();
                        Toast.makeText(PetCreateActivity.this, getResources().getString(R.string.pet_saved), Toast.LENGTH_SHORT).show();

                    }
                });

            } else {
                updateFieldsNoImage(pet_id);
                mProgress.dismiss();
                finish();
                Toast.makeText(PetCreateActivity.this, getResources().getString(R.string.pet_saved), Toast.LENGTH_SHORT).show();

            }
        } else {
            mProgress.dismiss();
            Toast.makeText(this, getResources().getString(R.string.fields_empty), Toast.LENGTH_SHORT).show();
        }

    }

    private void updateFieldsNoImage(final String pet_id) {

        mDatabasePet.child(pet_id).child("name").setValue(nameField.getText().toString().trim());
        mDatabasePet.child(pet_id).child("phone").setValue(phoneField.getText().toString().trim());
        mDatabasePet.child(pet_id).child("description").setValue(descField.getText().toString().trim());
        mDatabasePet.child(pet_id).child("info").setValue(additionalInfoField.getText().toString().trim());

        mDatabasePet.child(pet_id).child("species").setValue(spinnerSpecies.getSelectedItemPosition());
        mDatabasePet.child(pet_id).child("age").setValue(spinnerAge.getSelectedItemPosition());
        mDatabasePet.child(pet_id).child("gender").setValue(spinnerGender.getSelectedItemPosition());
        mDatabasePet.child(pet_id).child("size").setValue(spinnerSize.getSelectedItemPosition());
        mDatabasePet.child(pet_id).child("health").setValue(spinnerHealth.getSelectedItemPosition());

        if (switchCastrated.isChecked()) {
            mDatabasePet.child(pet_id).child("castrated").setValue(true);
        } else {
            mDatabasePet.child(pet_id).child("castrated").setValue(false);
        }

        if (switchVaccinated.isChecked()) {
            mDatabasePet.child(pet_id).child("vaccinated").setValue(true);
        } else {
            mDatabasePet.child(pet_id).child("vaccinated").setValue(false);
        }

        if (switchWormed.isChecked()) {
            mDatabasePet.child(pet_id).child("wormed").setValue(true);
        } else {
            mDatabasePet.child(pet_id).child("wormed").setValue(false);
        }

    }

    public boolean fieldsAreFilled() {

        if (!TextUtils.isEmpty(nameField.getText().toString().trim())
                && !TextUtils.isEmpty(descField.getText().toString().trim())
                && !TextUtils.isEmpty(phoneField.getText().toString().trim())
                //&& !TextUtils.isEmpty(additionalInfoField.getText().toString().trim())
                && spinnerSpecies.getSelectedItemPosition() != 0
                && spinnerAge.getSelectedItemPosition() != 0
                && spinnerGender.getSelectedItemPosition() != 0
                && spinnerSize.getSelectedItemPosition() != 0
                && spinnerHealth.getSelectedItemPosition() != 0
                ) {
            return true;
        } else {
            return false;
        }

    }

    private void startPosting() {

        mProgress.setMessage(getResources().getString(R.string.saving) + " ...");
        mProgress.show();
        mProgress.setCancelable(false);

        final String name_val = nameField.getText().toString().trim();
        final String desc_val = descField.getText().toString().trim();
        final String additional_info_val = additionalInfoField.getText().toString().trim();
        final String phone_val = phoneField.getText().toString().trim();

        final boolean castrated_val;
        final boolean vaccinated_val;
        final boolean wormed_val;

        if (switchCastrated.isChecked()) {
            castrated_val = true;
        } else {
            castrated_val = false;
        }
        if (switchVaccinated.isChecked()) {
            vaccinated_val = true;
        } else {
            vaccinated_val = false;
        }
        if (switchWormed.isChecked()) {
            wormed_val = true;
        } else {
            wormed_val = false;
        }

        if (fieldsAreFilled() && imageUri != null) {

            //StorageReference filepath = mStorage.child("Pet_Images").child(imageUri.getLastPathSegment());
            StorageReference filepath = mStorage.child("Images").child("pet" + randomString());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri donwloadUrl = taskSnapshot.getDownloadUrl();

                    GeoFire geoFirePet = new GeoFire(mDatabasePetLocations);
                    DatabaseReference newPet = mDatabasePet.push();

                    newPet.child("active").setValue(true);
                    newPet.child("adopted").setValue(false);
                    newPet.child("age").setValue(spinnerAge.getSelectedItemPosition());
                    newPet.child("castrated").setValue(castrated_val);
                    newPet.child("createdDate").setValue(String.valueOf(charSequence));
                    newPet.child("description").setValue(desc_val);
                    newPet.child("gender").setValue(spinnerGender.getSelectedItemPosition());
                    newPet.child("health").setValue(spinnerHealth.getSelectedItemPosition());
                    newPet.child("image").setValue(donwloadUrl.toString());
                    newPet.child("info").setValue(additional_info_val);
                    newPet.child("modifiedDate").setValue(String.valueOf(charSequence));
                    newPet.child("name").setValue(nameField.getText().toString().trim());
                    newPet.child("phone").setValue(phone_val);
                    newPet.child("reports").setValue(0);
                    newPet.child("size").setValue(spinnerSize.getSelectedItemPosition());
                    if (spinnerSpecies.getSelectedItemPosition() == 0) {
                        newPet.child("species").setValue(0);
                    } else {
                        newPet.child("species").setValue(spinnerSpecies.getSelectedItemPosition());
                    }
                    newPet.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    newPet.child("vaccinated").setValue(vaccinated_val);
                    newPet.child("wormed").setValue(wormed_val);

                    geoFirePet.setLocation("pet" + newPet.getKey(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
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
                    Toast.makeText(PetCreateActivity.this, getResources().getString(R.string.pet_saved), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            mProgress.dismiss();
            Toast.makeText(PetCreateActivity.this, getResources().getString(R.string.validation), Toast.LENGTH_SHORT).show();
        }

    }

    private void fillFields() {

        mDatabasePet.child(getIntent().getExtras().getString("pet_id")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //IMG FOR UPDATING
                imgSaved = (String) dataSnapshot.child("image").getValue();

                Picasso.with(PetCreateActivity.this).load(imgSaved).into(selectImage);
                spinnerSpecies.setSelection(((int)(long) dataSnapshot.child("species").getValue()));
                nameField.setText((String) dataSnapshot.child("name").getValue());
                descField.setText((String) dataSnapshot.child("description").getValue());
                phoneField.setText((String) dataSnapshot.child("phone").getValue());
                spinnerAge.setSelection(((int)(long) dataSnapshot.child("age").getValue()));
                spinnerGender.setSelection(((int)(long) dataSnapshot.child("gender").getValue()));
                spinnerSize.setSelection(((int)(long) dataSnapshot.child("size").getValue()));
                spinnerHealth.setSelection(((int)(long) dataSnapshot.child("health").getValue()));

                if ((boolean) dataSnapshot.child("castrated").getValue()) {
                    switchCastrated.setChecked(true);
                }

                if ((boolean) dataSnapshot.child("vaccinated").getValue()) {
                    switchVaccinated.setChecked(true);
                }

                if ((boolean) dataSnapshot.child("wormed").getValue()) {
                    switchWormed.setChecked(true);
                }

                additionalInfoField.setText((String) dataSnapshot.child("info").getValue());

                getSupportActionBar().setTitle((String) dataSnapshot.child("name").getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });

    }

    public static Address getAddress(final Context context, final Location location) {

        if (location == null)
            return null;

        final Geocoder geocoder = new Geocoder(context);
        final List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e) {
            return null;
        }
        if (addresses != null && !addresses.isEmpty())
            return addresses.get(0);
        else
            return null;

    }

    private void createDropdown(List<String> list, Spinner sniner) {

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, list) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sniner.setAdapter(spinnerArrayAdapter);

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
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
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
