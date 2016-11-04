package peetme.app.trunimal.com.peetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

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

                viewHolder.setTitle(model.getName());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

            }
        };

        petList.setAdapter(firebaseRecyclerAdapter);

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
