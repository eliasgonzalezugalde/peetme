package peetme.app.trunimal.com.peetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /*
    FirebaseListAdapter<Pet> firebaseListAdapter;
    ArrayList<Pet> pets = new ArrayList<>();
    private Firebase mRef;
    private EditText messageTxt;
    private Button sendBtn, btnStorage;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencia de Firebase
        mRef = new Firebase("https://peetme-89af5.firebaseio.com/");

        messageTxt = (EditText) findViewById(R.id.messageTxt);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        btnStorage = (Button) findViewById(R.id.btnStorage);

        DatabaseReference refDB = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refDBPet = refDB.child("pet");

        final Firebase petRef2 = new Firebase("https://peetme-89af5.firebaseio.com/pet");

        recycler = (RecyclerView) findViewById(R.id.lista);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        //boton send
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pet pet = new Pet(messageTxt.getText().toString(), "pet", 1);
                petRef2.push().setValue(pet);
                messageTxt.setText("");
                //Vet vet = new Vet("Cocha",0,0);
                //refDB.push().setValue(vet);
            }
        });

        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StorageActivity.class));
            }
        });

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pet, PetViewHolder>(Pet.class, android.R.layout.two_line_list_item, PetViewHolder.class, refDBPet) {

            @Override
            public void populateViewHolder(PetViewHolder petViewHolder, Pet pet, int position) {
                petViewHolder.setName(pet.getName());
                petViewHolder.setText(pet.getCat());
            }
        };
        recycler.setAdapter(firebaseRecyclerAdapter);

        //refDB.limitToLast(5).addValueEventListener
        petRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot: snapshot.getChildren()) {

                    Pet pet = msgSnapshot.getValue(Pet.class);

                    //comentar
                    pet.setName(msgSnapshot.getValue(Pet.class).getName());
                    pet.setCat(msgSnapshot.getValue(Pet.class).getCat());
                    pet.setPhoto(msgSnapshot.getValue(Pet.class).getPhoto());
                    pets.add(pet);
                    //comentar

                    Log.i("   Name", pet.getName()+", Cat: " + pet.getCat());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Chat", "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    */
}
