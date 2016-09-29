package peetme.app.trunimal.com.peetme;

import android.app.Application;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by elias on 25/9/2016.
 */

public class Peetme extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
