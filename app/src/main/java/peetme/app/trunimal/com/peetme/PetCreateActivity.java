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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    //private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Date date;
    private CharSequence charSequence;

    private TextView textViewLocationResult;
    private ImageButton selectImage;
    private EditText nameField, descField, additionalInfoField;
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

    //private FirebaseAuth mAuth;
    //private FirebaseUser mCurrentUser;

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

        switchCastrated = (Switch) findViewById(R.id.switchCastrated);
        switchVaccinated = (Switch) findViewById(R.id.switchVaccinated);
        switchWormed = (Switch) findViewById(R.id.switchWormed);

        submitBtn = (Button) findViewById(R.id.submitBtn);
        locationBtn = (Button) findViewById(R.id.locationBtn);

        mProgress = new ProgressDialog(this);

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

        //Google Play Services & Get the Last Known Location
        buildGoogleApiClient();

        //mAuth= FirebaseAuth.getInstance();
        //mCurrentUser = mAuth.getCurrentUser();

        Log.i("GEO",String.valueOf(mDatabasePetLocations));


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

                //https://github.com/ArthurHub/Android-Image-Cropper/wiki/Pick-image-for-cropping-from-Camera-or-Gallery
                startActivityForResult(CropImage.getPickImageChooserIntent(PetCreateActivity.this, getResources().getString(R.string.add_image_from), true), 200);

                /*
                //MODAL DE GALERÍA Y CÁMARA
                View view = LayoutInflater.from(PetCreateActivity.this).inflate(R.layout.dialog_update_radius, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(PetCreateActivity.this);
                builder
                        .setTitle(getResources().getString(R.string.add_image_from))
                        .setView(view);
                TextView textView_camera = (TextView) view.findViewById(R.id.textView_camera);
                TextView textView_gallery = (TextView) view.findViewById(R.id.textView_gallery);
                final AlertDialog alert = builder.show();

                textView_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //camara
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                        }
                        //Toast.makeText(PetCreateActivity.this, "Open camera", Toast.LENGTH_SHORT).show();
                        alert.dismiss();

                    }
                });

                textView_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //galería
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT );
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, GALLERY_REQUEST);
                        alert.dismiss();

                    }
                });*/

            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();

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
                    Log.i("GET ADDRESS", String.valueOf(getAddress(PetCreateActivity.this, mLastLocation).getAddressLine(0)) + "n/" + String.valueOf(getAddress(PetCreateActivity.this, mLastLocation).getAddressLine(1)));
                } else {
                    Log.i("", "NULL");
                }

            }
        });

        textViewLocationResult.setVisibility(View.GONE);

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

    private void startPosting() {

        mProgress.setMessage(getResources().getString(R.string.saving) + " ...");
        mProgress.show();
        mProgress.setCancelable(false);

        final String name_val = nameField.getText().toString().trim();
        final String desc_val = descField.getText().toString().trim();
        final String additional_info_val = additionalInfoField.getText().toString().trim();

        final String age_val = spinnerAge.getSelectedItem().toString().trim();
        final String species_val = spinnerSpecies.getSelectedItem().toString().trim();
        final String gender_val = spinnerGender.getSelectedItem().toString().trim();
        final String size_val = spinnerSize.getSelectedItem().toString().trim();
        final String health_val = spinnerHealth.getSelectedItem().toString().trim();

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

        if (!TextUtils.isEmpty(name_val)
                && !TextUtils.isEmpty(desc_val)
                && imageUri != null
                //&& !TextUtils.isEmpty(additional_info_val)
                && !TextUtils.isEmpty(age_val)
                && spinnerAge.getSelectedItemPosition() != 0
                && !TextUtils.isEmpty(species_val)
                && spinnerSpecies.getSelectedItemPosition() != 0
                && !TextUtils.isEmpty(gender_val)
                && spinnerGender.getSelectedItemPosition() != 0
                && !TextUtils.isEmpty(size_val)
                && spinnerSize.getSelectedItemPosition() != 0
                && !TextUtils.isEmpty(health_val)
                && spinnerHealth.getSelectedItemPosition() != 0) {
            
            //StorageReference filepath = mStorage.child("Pet_Images").child(imageUri.getLastPathSegment());
            StorageReference filepath = mStorage.child("Pet_Images").child("pet" + randomString());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri donwloadUrl = taskSnapshot.getDownloadUrl();

                    GeoFire geoFirePet = new GeoFire(mDatabasePetLocations);
                    DatabaseReference newPet = mDatabasePet.push();
                    newPet.child("image").setValue(donwloadUrl.toString());
                    newPet.child("species").setValue(species_val);
                    newPet.child("name").setValue(nameField.getText().toString().trim());
                    newPet.child("description").setValue(desc_val);
                    newPet.child("age").setValue(age_val);
                    newPet.child("gender").setValue(gender_val);
                    newPet.child("size").setValue(size_val);
                    newPet.child("health").setValue(health_val);
                    newPet.child("castrated").setValue(castrated_val);
                    newPet.child("vaccinated").setValue(vaccinated_val);
                    newPet.child("wormed").setValue(wormed_val);
                    newPet.child("info").setValue(additional_info_val);
                    newPet.child("reports").setValue("0");
                    newPet.child("modifiedDate").setValue(String.valueOf(charSequence));
                    newPet.child("createdDate").setValue(String.valueOf(charSequence));
                    newPet.child("adopted").setValue(false);
                    newPet.child("active").setValue(true);
                    newPet.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                    //startActivity(new Intent(PetCreateActivity.this, PetIndexActivity.class));

                }
            });

        } else {
            mProgress.dismiss();
            Toast.makeText(PetCreateActivity.this, getResources().getString(R.string.validation), Toast.LENGTH_SHORT).show();
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

        //requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK //DIALOG
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == this.RESULT_OK) {

            //Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            //imageUri = CropImage.getCaptureImageOutputUri(getApplicationContext());

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
