package peetme.app.trunimal.com.peetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyVetsActivity extends AppCompatActivity {

    private RecyclerView vetList;
    private DatabaseReference mDatabase, mDatabaseLocations;
    private Query queryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vets);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("vet");
        mDatabase.keepSynced(true);
        mDatabaseLocations = FirebaseDatabase.getInstance().getReference().child("locations");
        mDatabaseLocations.keepSynced(true);

        queryRef = mDatabase.orderByChild("uid").equalTo(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()));

        vetList = (RecyclerView) findViewById(R.id.vetList);
        vetList.setHasFixedSize(true);
        vetList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Vet, MyPetViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Vet, MyPetViewHolder>(

                Vet.class,
                R.layout.my_animal_row,
                MyPetViewHolder.class,
                queryRef

        ) {
            @Override
            protected void populateViewHolder(final MyPetViewHolder viewHolder, Vet model, int position) {

                final String vet_id = getRef(position).getKey().toString();

                viewHolder.setTitle(model.getName());
                viewHolder.setDesc(model.getPhone());
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
                                mDatabase.child(vet_id).removeValue();
                                mDatabaseLocations.child("vet" + vet_id).removeValue();
                                Toast.makeText(MyVetsActivity.this, getResources().getString(R.string.vet_deleted), Toast.LENGTH_SHORT).show();
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

                        Intent intent = new Intent(MyVetsActivity.this, VetCreateActivity.class); //VetEditActivity
                        intent.putExtra("vet_id", vet_id);
                        startActivity(intent);

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        goSingleActivity(vet_id);

                    }
                });

            }
        };
        vetList.setAdapter(firebaseRecyclerAdapter);

    }

    private void goSingleActivity(String vet_id) {

        Intent intent = new Intent(MyVetsActivity.this, VetSingleActivity.class);
        intent.putExtra("vet_id", vet_id);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MyVetsActivity.this, VetCreateActivity.class));
        } else {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
