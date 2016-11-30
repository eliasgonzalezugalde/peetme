package peetme.app.trunimal.com.peetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PetSingleActivity extends AppCompatActivity {

    private String pet_id;
    private DatabaseReference mDatabaseReference;
    private ImageView petImage;
    private TextView textViewName, textViewCreatedDate, textViewDescription, textViewAge,
            textViewGender, textViewSize, textViewHealth, textViewCastrated, textViewVaccinated,
            textViewWormed, textViewAdditionalInfo, textViewSpecies, textViewAdopted, textViewPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_single);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pet_id = getIntent().getExtras().getString("pet_id");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("pet");
        mDatabaseReference.keepSynced(true);

        petImage = (ImageView) findViewById(R.id.petImage);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewCreatedDate = (TextView) findViewById(R.id.textViewCreatedDate);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewAge = (TextView) findViewById(R.id.textViewAge);
        textViewGender = (TextView) findViewById(R.id.textViewGender);
        textViewSize = (TextView) findViewById(R.id.textViewSize);
        textViewHealth = (TextView) findViewById(R.id.textViewHealth);
        textViewCastrated = (TextView) findViewById(R.id.textViewCastrated);
        textViewVaccinated = (TextView) findViewById(R.id.textViewVaccinated);
        textViewWormed = (TextView) findViewById(R.id.textViewWormed);
        textViewAdditionalInfo = (TextView) findViewById(R.id.textViewAdditionalInfo);
        textViewSpecies = (TextView) findViewById(R.id.textViewSpecies);
        textViewAdopted = (TextView) findViewById(R.id.textViewAdopted);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);

        mDatabaseReference.child(pet_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getSupportActionBar().setTitle((String) dataSnapshot.child("name").getValue());

                Picasso.with(PetSingleActivity.this).load((String) dataSnapshot.child("image").getValue()).into(petImage);
                textViewName.setText((String) dataSnapshot.child("name").getValue());
                textViewCreatedDate.setText((String) dataSnapshot.child("createdDate").getValue());
                textViewDescription.setText((String) dataSnapshot.child("description").getValue());
                textViewAdditionalInfo.setText((String) dataSnapshot.child("info").getValue());
                textViewPhone.setText((String) dataSnapshot.child("phone").getValue());

                long number = 0;
                number = (long) dataSnapshot.child("age").getValue();
                if (number == 1) {
                    textViewAge.setText(getResources().getString(R.string.select_age_puppy));
                } else if (number == 2) {
                    textViewAge.setText(getResources().getString(R.string.select_age_young));
                } else if (number == 3) {
                    textViewAge.setText(getResources().getString(R.string.select_age_adult));
                } else {
                    textViewAge.setText(getResources().getString(R.string.select_age_old));
                }

                number = (long) dataSnapshot.child("gender").getValue();
                if (number == 1) {
                    textViewGender.setText(getResources().getString(R.string.male));
                } else {
                    textViewGender.setText(getResources().getString(R.string.female));
                }

                number = (long) dataSnapshot.child("size").getValue();
                if (number == 1) {
                    textViewSize.setText(getResources().getString(R.string.select_size_small));
                } else if (number == 2) {
                    textViewSize.setText(getResources().getString(R.string.select_size_medium));
                } else {
                    textViewSize.setText(getResources().getString(R.string.select_size_large));
                }

                number = (long) dataSnapshot.child("health").getValue();
                if (number == 1) {
                    textViewHealth.setText(getResources().getString(R.string.select_health_bad));
                } else if (number == 2) {
                    textViewHealth.setText(getResources().getString(R.string.select_health_ok));
                } else {
                    textViewHealth.setText(getResources().getString(R.string.select_health_good));
                }

                number = (long) dataSnapshot.child("species").getValue();
                if (number == 1) {
                    textViewSpecies.setText(getResources().getString(R.string.dog));
                } else if (number == 2) {
                    textViewSpecies.setText(getResources().getString(R.string.cat));
                } else if (number == 3) {
                    textViewSpecies.setText(getResources().getString(R.string.rabbit));
                } else if (number == 4) {
                    textViewSpecies.setText(getResources().getString(R.string.pig));
                } else if (number == 5) {
                    textViewSpecies.setText(getResources().getString(R.string.bird));
                } else if (number == 6) {
                    textViewSpecies.setText(getResources().getString(R.string.rodent));
                } else {
                    textViewSpecies.setText(getResources().getString(R.string.other));
                }

                if ((boolean) dataSnapshot.child("castrated").getValue()) {
                    textViewCastrated.setText(getResources().getString(R.string.yes));
                } else {
                    textViewCastrated.setText(getResources().getString(R.string.no));
                }

                if ((boolean) dataSnapshot.child("vaccinated").getValue()) {
                    textViewVaccinated.setText(getResources().getString(R.string.yes));
                } else {
                    textViewVaccinated.setText(getResources().getString(R.string.no));
                }

                if ((boolean) dataSnapshot.child("wormed").getValue()) {
                    textViewWormed.setText(getResources().getString(R.string.yes));
                } else {
                    textViewWormed.setText(getResources().getString(R.string.no));
                }

                if ((boolean) dataSnapshot.child("adopted").getValue()) {
                    textViewAdopted.setText(getResources().getString(R.string.yes));
                } else {
                    textViewAdopted.setText(getResources().getString(R.string.no));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
