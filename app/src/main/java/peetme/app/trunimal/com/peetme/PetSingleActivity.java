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
            textViewWormed, textViewAdditionalInfo, textViewSpecies, textViewAdopted;

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

        mDatabaseReference.child(pet_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Picasso.with(PetSingleActivity.this).load((String) dataSnapshot.child("image").getValue()).into(petImage);
                textViewName.setText((String) dataSnapshot.child("name").getValue());
                textViewCreatedDate.setText((String) dataSnapshot.child("createdDate").getValue());
                textViewDescription.setText((String) dataSnapshot.child("description").getValue());
                textViewAge.setText((String) dataSnapshot.child("age").getValue());
                textViewGender.setText((String) dataSnapshot.child("gender").getValue());
                textViewSize.setText((String) dataSnapshot.child("name").getValue());
                textViewHealth.setText((String) dataSnapshot.child("health").getValue());
                textViewAdditionalInfo.setText((String) dataSnapshot.child("info").getValue());
                textViewSpecies.setText((String) dataSnapshot.child("species").getValue());
                getSupportActionBar().setTitle((String) dataSnapshot.child("name").getValue());

                /*
                textViewCastrated.setText((String) dataSnapshot.child("castrated").getValue());
                textViewVaccinated.setText((String) dataSnapshot.child("vaccinated").getValue());
                textViewWormed.setText((String) dataSnapshot.child("wormed").getValue());
                textViewAdopted.setText((String) dataSnapshot.child("adopted").getValue());
                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
