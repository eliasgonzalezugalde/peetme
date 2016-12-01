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

public class VetIndexActivity extends AppCompatActivity {

    private RecyclerView vetList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_index);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("vet");
        mDatabase.keepSynced(true);

        vetList = (RecyclerView) findViewById(R.id.vetList);
        vetList.setHasFixedSize(true);
        vetList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Vet, VetViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Vet, VetViewHolder>(

                Vet.class,
                R.layout.animal_row,
                VetViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(VetViewHolder viewHolder, Vet model, int position) {
                viewHolder.setTitle(model.getName());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };

        vetList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(VetIndexActivity.this, VetCreateActivity.class));
        } else {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
