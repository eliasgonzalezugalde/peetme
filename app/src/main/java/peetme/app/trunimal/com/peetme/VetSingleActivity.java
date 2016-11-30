package peetme.app.trunimal.com.peetme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VetSingleActivity extends AppCompatActivity {

    TextView textViewName, textViewPhone, textViewOpen247, textViewCreatedDate;
    ImageButton vetImage;
    private String vet_id;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_single);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);
        textViewOpen247 = (TextView) findViewById(R.id.textViewOpen247);
        vetImage = (ImageButton) findViewById(R.id.vetImage);
        textViewCreatedDate = (TextView) findViewById(R.id.textViewCreatedDate);

        vet_id = getIntent().getExtras().getString("vet_id");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("vet");
        mDatabaseReference.keepSynced(true);

        mDatabaseReference.child(vet_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Picasso.with(VetSingleActivity.this).load((String) dataSnapshot.child("image").getValue()).into(vetImage);
                textViewName.setText((String) dataSnapshot.child("name").getValue());
                textViewPhone.setText((String) dataSnapshot.child("phone").getValue());
                textViewCreatedDate.setText((String) dataSnapshot.child("createdDate").getValue());
                if ((boolean) dataSnapshot.child("open247").getValue()) {
                    textViewOpen247.setText(getResources().getString(R.string.yes));
                } else {
                    textViewOpen247.setText(getResources().getString(R.string.no));
                }


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
