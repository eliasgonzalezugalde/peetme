package peetme.app.trunimal.com.peetme;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
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
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
