package com.secretdevbd.emergencyambulance;

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

    double latitude = 0, longitude = 0;
    String API_KEY = "AIzaSyA7HjUyXHqAN1jjNTTYlbAB2dPhif7UwS0";

    private static final int PERMISSION_REQUES_CODE = 99;
    String[] appPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private GoogleMap mMap;

    FirebaseDatabase database;
    DatabaseReference myRef;


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

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Hospital hospital = ds.getValue(Hospital.class);
                    Log.i(TAG, hospital.getName());

                }
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "NO LOCATION PERMISSION");
        }else {
            Log.i(TAG, "LOCATION PERMISSION READY");
            mMap.setMyLocationEnabled(true);
        }
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-34, 150))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet"));

        mMap.setOnMarkerClickListener(this);

        final LatLng perthLocation = new LatLng(-34, 152);
        Marker perth = mMap.addMarker(
                new MarkerOptions()
                        .position(perthLocation)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );

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


}