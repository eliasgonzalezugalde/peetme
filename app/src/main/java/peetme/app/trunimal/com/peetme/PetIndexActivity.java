package peetme.app.trunimal.com.peetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PetIndexActivity extends AppCompatActivity {

    private RecyclerView petList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_index);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("pet");
        mDatabase.keepSynced(true);

        petList = (RecyclerView) findViewById(R.id.animalList);
        petList.setHasFixedSize(true);
        petList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Pet, PetViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pet, PetViewHolder>(

                Pet.class,
                R.layout.animal_row,
                PetViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(PetViewHolder viewHolder, Pet model, int position) {

                final String pet_id = getRef(position).getKey().toString();

                viewHolder.setTitle(model.getName());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        goSingleActivity(pet_id);

                    }
                });

            }
        };
        petList.setAdapter(firebaseRecyclerAdapter);

    }

    public void goSingleActivity(String pet_id) {

        Intent intent = new Intent(PetIndexActivity.this, PetSingleActivity.class);
        intent.putExtra("pet_id", pet_id);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(PetIndexActivity.this, PetCreateActivity.class));
        } else {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
}
