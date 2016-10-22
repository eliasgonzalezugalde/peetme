package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PetCreateActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Date date;
    private CharSequence charSequence;

    private ImageButton selectImage;

    private EditText nameField;
    private EditText descField;
    private EditText ageField;
    private EditText additionalInfoField;
    private Button submitBtn;

    private Uri imageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    private Spinner spinnerHealth;
    private Spinner spinnerSize;
    private Spinner spinnerSpecies;
    private Spinner spinnerGender;

    private Switch switchCastrated, switchVaccinated, switchWormed;

    private String[]health = null;
    private final String[]size = null;
    private final String[]species = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //rellenando dropdown'charSequence
        String[]health = { getResources().getString(R.string.select_health), getResources().getString(R.string.select_health_bad), getResources().getString(R.string.select_health_ok), getResources().getString(R.string.select_health_good)};
        String[]size = { getResources().getString(R.string.select_size), getResources().getString(R.string.select_size_small), getResources().getString(R.string.select_size_medium), getResources().getString(R.string.select_size_large)};
        String[]species = { getResources().getString(R.string.select_species), getResources().getString(R.string.dog), getResources().getString(R.string.cat), getResources().getString(R.string.rabbit), getResources().getString(R.string.pig), getResources().getString(R.string.bird), getResources().getString(R.string.rodent), getResources().getString(R.string.other)};
        String[]gender = { getResources().getString(R.string.select_gender), getResources().getString(R.string.male), getResources().getString(R.string.female)};

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("pet");

        selectImage = (ImageButton) findViewById(R.id.selectImage);
        nameField = (EditText) findViewById(R.id.nameField);
        descField = (EditText) findViewById(R.id.descField);
        ageField = (EditText) findViewById(R.id.ageField);
        additionalInfoField = (EditText) findViewById(R.id.additionalInfoField);

        switchCastrated = (Switch) findViewById(R.id.switchCastrated);
        switchVaccinated = (Switch) findViewById(R.id.switchVaccinated);
        switchWormed = (Switch) findViewById(R.id.switchWormed);

        submitBtn = (Button) findViewById(R.id.submitBtn);

        mProgress = new ProgressDialog(this);

        spinnerHealth = (Spinner)findViewById(R.id.spinnerHealth);
        spinnerSize = (Spinner) findViewById(R.id.spinnerSize);
        spinnerSpecies = (Spinner) findViewById(R.id.spinnerSpecies);
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);

        final List<String> healthList = new ArrayList<>(Arrays.asList(health));
        final List<String> sizeList = new ArrayList<>(Arrays.asList(size));
        final List<String> speciesList = new ArrayList<>(Arrays.asList(species));
        final List<String> genderList = new ArrayList<>(Arrays.asList(gender));

        createDropdown(healthList, spinnerHealth);
        createDropdown(sizeList, spinnerSize);
        createDropdown(speciesList, spinnerSpecies);
        createDropdown(genderList, spinnerGender);

        date = new Date();
        charSequence = DateFormat.format("MMMM date, yyyy ", date.getTime());

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

        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), getResources().getString(R.string.size) + ": " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerHealth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), getResources().getString(R.string.health) + ": " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //spinnerHealth.setOnItemSelectedListener(this);

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

    private void createDropdown(List<String> list, Spinner sniner) {

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, list){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sniner.setAdapter(spinnerArrayAdapter);

    }

    private void startPosting() {

        mProgress.setMessage("Posting ...");
        mProgress.show();
        final String name_val = nameField.getText().toString().trim();
        final String desc_val = descField.getText().toString().trim();
        final String age_val = ageField.getText().toString().trim();
        final String additional_info_val = additionalInfoField.getText().toString().trim();

        final String species_val = spinnerSpecies.getSelectedItem().toString().trim();
        final String gender_val = spinnerSpecies.getSelectedItem().toString().trim();
        final String size_val = spinnerSpecies.getSelectedItem().toString().trim();
        final String health_val = spinnerSpecies.getSelectedItem().toString().trim();

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

        if (!TextUtils.isEmpty(name_val) && !TextUtils.isEmpty(desc_val) && imageUri != null ) {

            //StorageReference filepath = mStorage.child("Pet_Images").child(imageUri.getLastPathSegment());
            StorageReference filepath = mStorage.child("Pet_Images").child(random());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri donwloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPet = mDatabase.push();

                    newPet.child("description").setValue(desc_val);
                    newPet.child("image").setValue(donwloadUrl.toString());
                    newPet.child("name").setValue(name_val);
                    newPet.child("info").setValue(additional_info_val);

                    newPet.child("age").setValue(age_val);
                    newPet.child("species").setValue(species_val);
                    newPet.child("gender").setValue(gender_val);
                    newPet.child("size").setValue(size_val);
                    newPet.child("health").setValue(health_val);

                    newPet.child("castrated").setValue(castrated_val);
                    newPet.child("vaccinated").setValue(vaccinated_val);
                    newPet.child("wormed").setValue(wormed_val);

                    //id_user
                    //newPet.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    newPet.child("uid").setValue("1");

                    newPet.child("reports").setValue("0");

                    newPet.child("modifiedDate").setValue(charSequence);
                    newPet.child("createdDate").setValue(charSequence);

                    //Geofire
                    newPet.child("ubication").setValue("10.323266, -84.431012");
                    newPet.child("adopted").setValue(false);
                    newPet.child("active").setValue(true);

                    mProgress.dismiss();
                    Toast.makeText(PetCreateActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PetCreateActivity.this, MainActivity.class));

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
