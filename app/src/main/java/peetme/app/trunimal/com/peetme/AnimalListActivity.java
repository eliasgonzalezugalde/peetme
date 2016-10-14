package peetme.app.trunimal.com.peetme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnimalListActivity extends AppCompatActivity {

    private RecyclerView animalList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("pet");

        animalList = (RecyclerView) findViewById(R.id.animalList);
        animalList.setHasFixedSize(true);
        animalList.setLayoutManager(new LinearLayoutManager(this));

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

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

            }
        };

        animalList.setAdapter(firebaseRecyclerAdapter);

    }
}
