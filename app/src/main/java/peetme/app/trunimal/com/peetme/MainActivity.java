package peetme.app.trunimal.com.peetme;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import android.os.Handler;

import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        //OnPermissionCallback,
        GeoQueryEventListener,
        LocationListener {

    private static final String LOCATION_KEY = "LK";
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private DatabaseReference mDatabasePetLocations;
    private DatabaseReference mDatabaseUsers;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    //private final String PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    //private PermissionHelper permissionHelper;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView emailTextView, nameTextView;
    private Map<String, Marker> markers;
    private Circle searchCircle;
    private LocationRequest locationRequest;
    private static final int REQUEST_LOCATION = 5;
    private static final String TAG = "LOCATION_ACTIVITY";
    private static final int REQUEST_CHECK_SETTINGS = 6;
    public static final long UPDATE_INTERVAL = 1000;
    public static final long UPDATE_FASTEST_INTERVAL = UPDATE_INTERVAL / 2;
    private Marker currentMarker;
    private MarkerOptions currentMarkerOptions;
    private ImageButton locationBtn;
    private double searchRadius;

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

        locationBtn = (ImageButton) findViewById(R.id.locationBtn);

        mDatabasePetLocations = FirebaseDatabase.getInstance().getReference().child("pet_locations");
        mDatabasePetLocations.keepSynced(true);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);
        geoFire = new GeoFire(mDatabasePetLocations);

        View hView = navigationView.getHeaderView(0);
        emailTextView = (TextView) hView.findViewById(R.id.emailTextView);
        nameTextView = (TextView) hView.findViewById(R.id.nameTextView);
        searchRadius = 4; //kilometers

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

        //permissionHelper = PermissionHelper.getInstance(this);
        //permissionHelper.setForceAccepting(false).request(PERMISSION);

        //Google Play Services & Get the Last Known Location
        buildGoogleApiClient();
        //getting location savedInstanceState
        updateValuesFromBundle(savedInstanceState);
        // setup markers
        this.markers = new HashMap<String, Marker>();

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                        @Override
                        public void onCameraMoveStarted(int i) {
                            locationBtn.setImageResource(R.drawable.my_location);
                        }
                    });

                }
            });

        }

        // Crear configuración de peticiones
        locationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(UPDATE_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Crear opciones de peticiones
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        // Verificar ajustes de ubicación actuales
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient, builder.build()
        );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Los ajustes de ubicación satisfacen la configuración.");
                        processLastLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d(TAG, "Los ajustes de ubicación no satisfacen la configuración. " +
                                    "Se mostrará un diálogo de ayuda.");
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "El Intent del diálogo no funcionó.");
                            // Sin operaciones
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Los ajustes de ubicación no son apropiados.");
                        break;

                }
            }
        });


        checkUserExist();

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        mLastLocation.getLatitude(), mLastLocation.getLongitude()), mMap.getCameraPosition().zoom)); //18.0f
                locationBtn.setImageResource(R.drawable.my_location_blue);

            }
        });

    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        Toast.makeText(MainActivity.this, "You need to setup your account", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(MainActivity.this, SetupActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "El usuario permitió el cambio de ajustes de ubicación.");
                        processLastLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "El usuario no permitió el cambio de ajustes de ubicación");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onStart() {

        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();

        if (mLastLocation != null) {
            mMap.addMarker(currentMarkerOptions = new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .title("Current location")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_current_blue)));
        }

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        this.geoQuery.removeAllListeners();
        for (Marker marker : this.markers.values()) {
            marker.remove();
        }
        this.markers.clear();
        mMap.clear();
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
            startActivity(new Intent(MainActivity.this, SetupActivity.class));// return true;
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

            startActivity(new Intent(MainActivity.this, SetupActivity.class));

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

        // Obtenemos la última ubicación al ser la primera vez
        processLastLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));

        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        currentMarkerOptions = new MarkerOptions()
                .position(currentLocation)
                .title("Current location")
                //.snippet("Parque de Cuidad Quesada")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_current_blue));
        //.anchor(0.0f, 1.0f);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //mMap.setMyLocationEnabled(true);

        //circulo
        searchCircle = this.mMap.addCircle(new CircleOptions().center(currentLocation).radius(searchRadius * 1000));
        searchCircle.setFillColor(Color.argb(40, 66, 133, 244));
        searchCircle.setStrokeColor(Color.argb(75, 57, 115, 211));
        //rojo
        //searchCircle.setFillColor(Color.argb(40, 229, 50, 29));
        //searchCircle.setStrokeColor(Color.argb(75, 229, 50, 29));


        // Iniciamos las actualizaciones de ubicación
        startLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, String.format("Nueva ubicación: (%s, %s)",
                location.getLatitude(), location.getLongitude()));
        mLastLocation = location;
        updateLocationUI();
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        } else {
            manageDeniedPermission();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    //mLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
                    //mLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
                    //Toast.makeText(this, String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void processLastLocation() {
        getLastLocation();
        if (mLastLocation != null) {
            updateLocationUI();
        } else {
            Log.i(TAG, "Localización nula");
        }
    }

    private void getLastLocation() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            manageDeniedPermission();
        }
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Aquí muestras confirmación explicativa al usuario
            // por si rechazó los permisos anteriormente
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    private void updateLocationUI() {

        Log.i("Latitude", String.valueOf(mLastLocation.getLatitude()));
        Log.i("Longitude", String.valueOf(mLastLocation.getLongitude()));

        geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), searchRadius);
        this.geoQuery.addGeoQueryEventListener(this);

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //mapa
                    mMap = googleMap;
                    //mMap.clear();
                    LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    if (currentMarker == null) {
                        currentMarker = mMap.addMarker(currentMarkerOptions);
                    } else {
                        currentMarker.setPosition(currentLocation);
                        //borrar circulo
                        searchCircle.setCenter(currentLocation);
                    }

                }

            });

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Protegemos la ubicación actual antes del cambio de configuración
        outState.putParcelable(LOCATION_KEY, mLastLocation);
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LOCATION_KEY)) {
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
                //updateLocationUI();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    //////////////////////////////////////////////////////////GeoQueryEventListener/////////////////////////////////////////////////////////////
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        // Add a new marker to the map
        Marker marker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
        marker.setTag(key); //animal_id
        marker.setIcon((BitmapDescriptorFactory.fromResource(R.mipmap.pin_animal)));
        this.markers.put(key, marker);

    }

    @Override
    public void onKeyExited(String key) {
        // Remove any old marker
        /*
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
        }
        */
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        // Move the marker
        /*
        Marker marker = this.markers.get(key);
        if (marker != null) {
            //marker.setTag(key);
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
        */
    }

    @Override
    public void onGeoQueryReady() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {
                    goSingleActivity(String.valueOf(marker.getTag()));
                }
                return false;
            }
        });
    }

    public void goSingleActivity(String pet_id) {

        Intent intent = new Intent(MainActivity.this, PetSingleActivity.class);
        intent.putExtra("pet_id", pet_id);
        startActivity(intent);

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });

    }

    //////////////////////////////////////////////////////////GeoQueryEventListener/////////////////////////////////////////////////////////////

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    //////////////////////////////////////////////////////////PERMISOS/////////////////////////////////////////////////////////////
    /*
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
    */
    //////////////////////////////////////////////////////////API GOOGLE/////////////////////////////////////////////////////////////

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

}