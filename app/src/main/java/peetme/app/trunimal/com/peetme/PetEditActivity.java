package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static peetme.app.trunimal.com.peetme.PetCreateActivity.getAddress;

public class PetEditActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int GALLERY_REQUEST = 1;
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
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String pet_id;
    private DatabaseReference mDatabaseReference;
    private static final String TAG = "PET_EDIT_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_edit);

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

        Log.i(TAG, String.valueOf(mDatabasePetLocations));

        pet_id = getIntent().getExtras().getString("pet_id");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("pet");
        mDatabaseReference.keepSynced(true);

        mDatabaseReference.child(pet_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Picasso.with(PetEditActivity.this).load((String) dataSnapshot.child("image").getValue()).into(selectImage);
                spinnerSpecies.setSelection(((int)(long) dataSnapshot.child("species").getValue()));
                nameField.setText((String) dataSnapshot.child("name").getValue());
                descField.setText((String) dataSnapshot.child("description").getValue());
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

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(CropImage.getPickImageChooserIntent(PetEditActivity.this, getResources().getString(R.string.add_image_from), true), 200);

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startUpdate();

            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLastLocation != null) {
                    locationBtn.setText(getResources().getString(R.string.done));
                    locationBtn.setEnabled(false);
                    //locationBtn.setTransformationMethod(null); //boton en minusculas
                    textViewLocationResult.setText(String.valueOf(getAddress(PetEditActivity.this, mLastLocation).getAddressLine(0)));
                    textViewLocationResult.setVisibility(View.VISIBLE);
                    Log.i(TAG, String.valueOf(getAddress(PetEditActivity.this, mLastLocation).getAddressLine(0)) + "n/" + String.valueOf(getAddress(PetEditActivity.this, mLastLocation).getAddressLine(1)));
                } else {
                    Log.i(TAG, "mLastLocation is null");
                }

            }
        });
        textViewLocationResult.setVisibility(View.GONE);

    }

    private void startUpdate() {

        //UPDATE


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

    }

}
