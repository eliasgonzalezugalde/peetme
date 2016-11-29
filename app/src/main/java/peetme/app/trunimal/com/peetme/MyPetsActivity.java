package peetme.app.trunimal.com.peetme;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyPetsActivity extends AppCompatActivity {

    private RecyclerView petList;
    private DatabaseReference mDatabase, mDatabaseLocations;
    private Query queryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("pet");
        mDatabaseLocations = FirebaseDatabase.getInstance().getReference().child("locations");
        queryRef = mDatabase.orderByChild("uid").equalTo(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()));

        petList = (RecyclerView) findViewById(R.id.animalList);
        petList.setHasFixedSize(true);
        petList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Pet, MyPetViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pet, MyPetViewHolder>(

                Pet.class,
                R.layout.my_animal_row,
                MyPetViewHolder.class,
                queryRef

        ) {
            @Override
            protected void populateViewHolder(final MyPetViewHolder viewHolder, Pet model, int position) {

                final String pet_id = getRef(position).getKey().toString();

                viewHolder.setTitle(model.getName());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.mView.getContext());
                        builder.setMessage(getResources().getString(R.string.are_you_sure_delete));
                        builder.setTitle(getResources().getString(R.string.confirm_delete));

                        builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                //BORRAR
                                mDatabase.child(pet_id).removeValue();
                                mDatabaseLocations.child("pet" + pet_id).removeValue();
                                Toast.makeText(MyPetsActivity.this, getResources().getString(R.string.animal_deleted), Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                            }
                        });

                        builder.create().show();

                    }
                });

                viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MyPetsActivity.this, "Edit", Toast.LENGTH_SHORT).show();
                    }
                });

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

    private void goSingleActivity(String pet_id) {
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