package peetme.app.trunimal.com.peetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnPermissionCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    final String PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    PermissionHelper permissionHelper;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView emailTextView, nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        emailTextView = (TextView)hView.findViewById(R.id.emailTextView);
        nameTextView = (TextView)hView.findViewById(R.id.nameTextView);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginWithActivity.class));
                } else {
                    emailTextView.setText(firebaseAuth.getCurrentUser().getEmail());
                    nameTextView.setText(firebaseAuth.getCurrentUser().getDisplayName());
                }

            }
        };

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        permissionHelper = PermissionHelper.getInstance(this);
        permissionHelper.setForceAccepting(false).request(PERMISSION);

        //Google Play Services & Get the Last Known Location
        buildGoogleApiClient();

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //mapa
                    mMap = googleMap;

                    // Add a marker and move the camera
                    LatLng quesada = new LatLng(10.323266, -84.431012);
                    mMap.addMarker(new MarkerOptions()
                            .position(quesada)
                            .title("Ciudad Quesada")
                            .snippet("Parque de Cuidad Quesada")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_animal))
                            .anchor(0.0f, 1.0f)
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(quesada));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    } else {
                        // Show rationale and request permission.
                    }

                }

            });

        }

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_animals) {

            startActivity(new Intent(MainActivity.this, PetIndexActivity.class));

        } else if (id == R.id.nav_vets) {

            startActivity(new Intent(MainActivity.this, VetIndexActivity.class));

        } else if (id == R.id.nav_animal_shelters) {

            startActivity(new Intent(MainActivity.this, ShelterIndexActivity.class));

        } /*else if (id == R.id.nav_stray_animal) {

            startActivity(new Intent(MainActivity.this, StrayIndexActivity.class));

        }*/ else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sing_out) {

            /*
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            */
            //mAuth.signOut();
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            goLoginActivity();

        }/* else {
            return super.onOptionsItemSelected(item);
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false; //highlight
    }

    private void goLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginWithActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                Log.i("Latitude", String.valueOf(mLastLocation.getLatitude()));
                Log.i("Longitude", String.valueOf(mLastLocation.getLongitude()));
            } else {
                Log.i("LOCATION PROBLEM", " --> NULL");
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    //////////////////////////////////////////////////////////PERMISOS/////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {

    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {

    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {

    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("We need your help")
                .setMessage("We use your location to show animal shelters or veterinary who are near you.")
                .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionHelper.requestAfterExplanation(PERMISSION);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {

    }

    @Override
    public void onNoPermissionNeeded() {

    }
}
