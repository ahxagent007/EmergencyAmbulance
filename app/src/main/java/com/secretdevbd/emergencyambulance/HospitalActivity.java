package com.secretdevbd.emergencyambulance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.secretdevbd.emergencyambulance.models.Hospital;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HospitalActivity extends AppCompatActivity {

    String TAG = "XIAN";

    RecyclerView RV_hospitals;
    EditText ET_searchHospital;
    Button btn_searchHospital;

    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayList<Hospital> HOSPITALS = new ArrayList<>();
    ArrayList<Hospital> SEARCH_HOSPITALS = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;

    double latitude = 23.737820;
    double longitude = 90.395290;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        checkAndRequestPermissions();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(HospitalActivity.this);

        RV_hospitals = findViewById(R.id.RV_hospitals);
        ET_searchHospital = findViewById(R.id.ET_searchHospital);
        //btn_searchHospital = findViewById(R.id.btn_searchHospital);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Hospital");

        getLocationAndShowHospital();

        /*btn_searchHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    private void getLocationAndShowHospital(){

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

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(HospitalActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Log.i(TAG, "onSuccess: "+latitude+" "+longitude);
                        }else {
                            Log.i(TAG, "NEW LOCATION NULL");
                        }
                        showHospitalList();
                    }

                });

        Log.i(TAG, "ownLocation LOC :: "+latitude+","+longitude);
    }

    public void showHospitalList(){
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HOSPITALS = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Hospital hospital = ds.getValue(Hospital.class);
                    double distance = distance(hospital.getLatitude(), hospital.getLongitude(), latitude, longitude);
                    hospital.setDistance_from_user(distance);
                    Log.i(TAG, hospital.getName());

                    HOSPITALS.add(hospital);
                }

                Collections.sort(HOSPITALS, Comparator.comparing(Hospital::getDistance_from_user));

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                RV_hospitals.setLayoutManager(mLayoutManager);

                RecyclerView.Adapter mRecycleAdapter = new RecycleViewAdapterForAllServer(getApplicationContext(), HOSPITALS);
                RV_hospitals.setAdapter(mRecycleAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        ET_searchHospital.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, s.toString());
                SEARCH_HOSPITALS = new ArrayList<>();
                for (Hospital h : HOSPITALS){
                    if(h.getName().toUpperCase().contains(s.toString().toUpperCase()) || h.getCategory().toUpperCase().contains(s.toString().toUpperCase())
                            || h.getTitle().toUpperCase().contains(s.toString().toUpperCase())){
                        SEARCH_HOSPITALS.add(h);
                    }

                    RecyclerView.Adapter mRecycleAdapter = new RecycleViewAdapterForAllServer(getApplicationContext(), SEARCH_HOSPITALS);
                    RV_hospitals.setAdapter(mRecycleAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public class RecycleViewAdapterForAllServer extends RecyclerView.Adapter<RecycleViewAdapterForAllServer.ViewHolder> {

        String TAG = "XIAN";

        ArrayList<Hospital> Hospitals;
        Context context;

        public RecycleViewAdapterForAllServer(Context context, ArrayList<Hospital> Hospitals) {
            super();
            this.context = context;
            this.Hospitals = Hospitals;
            //Log.i(TAG,"RECYCLE VIEW Constructor");
        }

        @Override
        public RecycleViewAdapterForAllServer.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.single_hospital, viewGroup, false);

            RecycleViewAdapterForAllServer.ViewHolder viewHolder = new RecycleViewAdapterForAllServer.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecycleViewAdapterForAllServer.ViewHolder viewHolder, final int i) {

            //Log.i(TAG,i+" RECYCLE VIEW "+(productList.get(i).getProductName()));
            //!PigeonList.get(i).getPigeonPicture().isEmpty() &&
            /*if(!PigeonList.get(i).getPigeonPicture().equalsIgnoreCase("Null")){
                viewHolder.IV_pigeonPicture.setImageBitmap(decodeBase64Image(PigeonList.get(i).getPigeonPicture()));
            }else{

            }*/

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            viewHolder.TV_hospitalName.setText(Hospitals.get(i).getName());
            viewHolder.TV_hospitalType.setText(Hospitals.get(i).getCategory());
            viewHolder.TV_hospitalDetails.setText(Hospitals.get(i).getTitle());
            viewHolder.TV_distance.setText("Distance: "+df.format(Hospitals.get(i).getDistance_from_user())+" km");

            viewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {

                    } else {
                        //Google Map open
                        String loc = Hospitals.get(i).getLatitude()+","+Hospitals.get(i).getLongitude();
                        if(loc != "0.0,0.0"){
                            String uri = "http://maps.google.com/maps?daddr=" +loc;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(), "No Location Registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return Hospitals.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private TextView TV_hospitalName, TV_hospitalType, TV_hospitalDetails, TV_distance;
            //ImageView IV_server_icon;

            private ItemClickListener clickListener;

            public ViewHolder(View itemView) {
                super(itemView);

                TV_hospitalName = itemView.findViewById(R.id.TV_hospitalName);
                TV_hospitalType = itemView.findViewById(R.id.TV_hospitalType);
                TV_hospitalDetails = itemView.findViewById(R.id.TV_hospitalDetails);
                TV_distance = itemView.findViewById(R.id.TV_distance);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void setClickListener(ItemClickListener itemClickListener) {
                this.clickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                clickListener.onClick(view, getPosition(), false);
            }

            @Override
            public boolean onLongClick(View view) {
                clickListener.onClick(view, getPosition(), true);
                return true;
            }
        }

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist/1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private static final int PERMISSION_REQUES_CODE = 99;
    String[] appPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };


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
            ActivityCompat.requestPermissions(HospitalActivity.this,
                    listPermissinsNeeded.toArray(new String[listPermissinsNeeded.size()]),
                    PERMISSION_REQUES_CODE);
            return false;
        }
        return true;
    }
}