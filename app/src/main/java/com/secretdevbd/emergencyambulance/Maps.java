package com.secretdevbd.emergencyambulance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.secretdevbd.emergencyambulance.models.Hospital;

import java.util.ArrayList;
import java.util.List;

public class Maps extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    String TAG = "XIAN";


    double longitude = 23.737820;
    double latitude = 90.395290;
    String API_KEY = "AIzaSyA7HjUyXHqAN1jjNTTYlbAB2dPhif7UwS0";

    private static final int PERMISSION_REQUES_CODE = 99;
    String[] appPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private GoogleMap mMap;

    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayList<Hospital> HOSPITALS = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkAndRequestPermissions();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Hospital");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HOSPITALS = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Hospital hospital = ds.getValue(Hospital.class);
                    Log.i(TAG, hospital.getName());

                    HOSPITALS.add(hospital);
                }

                if(mMap != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
                    mMap.clear();

                    for (Hospital H : HOSPITALS){
                        LatLng hospital = new LatLng(H.getLatitude(), H.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(hospital)
                                .title(H.getName()));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                }
                getLocationAndSetMap();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public boolean checkAndRequestPermissions() {

        List<String> listPermissinsNeeded = new ArrayList<>();

        for (String perm : appPermissions) {

            if (Build.VERSION.SDK_INT >= 23) {
                if (getApplicationContext().checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                    listPermissinsNeeded.add(perm);
                }
            } else {
                if (PermissionChecker.checkSelfPermission(getApplicationContext(), perm) != PermissionChecker.PERMISSION_GRANTED) {
                    listPermissinsNeeded.add(perm);
                }
            }
        }

        if (!listPermissinsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(Maps.this,
                    listPermissinsNeeded.toArray(new String[listPermissinsNeeded.size()]),
                    PERMISSION_REQUES_CODE);
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Hospital H : HOSPITALS){
            LatLng hospital = new LatLng(H.getLatitude(), H.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(hospital)
                    .title(H.getName()));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                checkAndRequestPermissions();
                return;
            }
        }
        else{
            int permissionLocation = PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionLocation != PackageManager.PERMISSION_GRANTED ){

                checkAndRequestPermissions();
            }
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        try{
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } catch (Exception e ){
            Toast.makeText(getApplicationContext(),"Can't Access user location", Toast.LENGTH_LONG).show();
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);

        zoomToCurrentLoc();

        Log.i(TAG, "EMERGENCY LOC :: "+latitude+","+longitude);
    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private void getLocationAndSetMap(){

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                checkAndRequestPermissions();
                return;
            }
        }
        else{
            int permissionLocation = PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionLocation != PackageManager.PERMISSION_GRANTED ){

                checkAndRequestPermissions();
            }
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        try{
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } catch (Exception e ){
            Toast.makeText(getApplicationContext(),"Can't Access user location", Toast.LENGTH_LONG).show();
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);

        zoomToCurrentLoc();

        Log.i(TAG, "ownLocation LOC :: "+latitude+","+longitude);
    }

    private void zoomToCurrentLoc(){
        LatLng ownLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(ownLocation)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(ownLocation));
        moveToCurrentLocation(ownLocation);
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);


    }


}