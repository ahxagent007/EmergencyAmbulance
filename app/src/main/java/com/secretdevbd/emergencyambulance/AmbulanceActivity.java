package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import com.secretdevbd.emergencyambulance.models.Ambulance;

import java.util.ArrayList;
import java.util.List;

public class AmbulanceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "XIAN";
    private GoogleMap googleMapAmbulance;
    ArrayList<Ambulance> ambulanceArray = new ArrayList<Ambulance>();

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Ambulance");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ambulanceArray = new ArrayList<Ambulance>();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Ambulance ambulance = ds.getValue(Ambulance.class);
                    ambulanceArray.add(ambulance);
                }

                if(googleMapAmbulance != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
                    googleMapAmbulance.clear();

                    for (Ambulance a : ambulanceArray){
                        LatLng hospital = new LatLng(a.getLatitude(), a.getLongitude());
                        googleMapAmbulance.addMarker(new MarkerOptions()
                                .position(hospital)
                                .title(a.getPhone()));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                    getLocationAndSetMap();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapAmbulance);
        mapFragment.getMapAsync(this);

    }

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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (Exception e ){
            Toast.makeText(getApplicationContext(),"Can't Access user location", Toast.LENGTH_LONG).show();
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);

        zoomToCurrentLoc();

        Log.i(TAG, "ownLocation LOC :: "+latitude+","+longitude);
    }

    private void zoomToCurrentLoc(){
        LatLng ownLocation = new LatLng(latitude, longitude);
        googleMapAmbulance.addMarker(new MarkerOptions()
                .position(ownLocation)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(ownLocation));
        moveToCurrentLocation(ownLocation);
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        googleMapAmbulance.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        googleMapAmbulance.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMapAmbulance.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);


    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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


    private static final int PERMISSION_REQUES_CODE = 99;
    String[] appPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    double latitude = 23.737820;
    double longitude = 90.395290;

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
            ActivityCompat.requestPermissions(AmbulanceActivity.this,
                    listPermissinsNeeded.toArray(new String[listPermissinsNeeded.size()]),
                    PERMISSION_REQUES_CODE);
            return false;
        }
        return true;
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        return googleMapAmbulance.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconResID)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapAmbulance = googleMap;
        googleMapAmbulance.setOnMarkerClickListener(this);

        Log.i(TAG, "onMapReady : "+ambulanceArray.size());

        for(int i = 0 ; i < ambulanceArray.size() ; i++) {
            createMarker(ambulanceArray.get(i).getLatitude(), ambulanceArray.get(i).getLongitude(), ambulanceArray.get(i).getReg_no()+" "+ambulanceArray.get(i).getPhone(), "Snippet", 0);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        callAmbulance(marker.getTitle());
        return true;
    }
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 22;
    String number;
    public void callAmbulance(String number){
        this.number = number;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Call");
        builder.setMessage("Do you want to call this Ambulance ("+number+")");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+number));
                //startActivity(callIntent);

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(AmbulanceActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AmbulanceActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //You already have permission
                    try {
                        startActivity(callIntent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+number));
                    startActivity(callIntent);

                } else {
                    Toast.makeText(getApplicationContext(), "Please give permission", Toast.LENGTH_LONG).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}