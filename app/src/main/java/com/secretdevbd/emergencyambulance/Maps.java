package com.secretdevbd.emergencyambulance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.secretdevbd.emergencyambulance.models.Doctor;
import com.secretdevbd.emergencyambulance.models.Hospital;
import com.secretdevbd.emergencyambulance.utility.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    String TAG = "XIAN";


    double latitude = 23.737820;
    double longitude = 90.395290;

    String API_KEY = "AIzaSyA7HjUyXHqAN1jjNTTYlbAB2dPhif7UwS0";

    private static final int PERMISSION_REQUES_CODE = 99;
    String[] appPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private GoogleMap mMap;

    FirebaseDatabase database;
    DatabaseReference myRef, doc_Ref;

    ArrayList<Hospital> HOSPITALS = new ArrayList<>();
    ArrayList<Doctor> DOCTORS = new ArrayList<Doctor>();

    private FusedLocationProviderClient fusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkAndRequestPermissions();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Maps.this);

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
                                .title(H.getName())
                                .snippet(""+H.getId()));
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
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.i(TAG, marker.getTitle() +" Clicked");
        DoctorList(Long.parseLong(marker.getSnippet()));
        //LatLng origin = new LatLng(latitude, longitude);
        //LatLng destination = marker.getPosition();
        //drawDirectionLine(origin, destination);
/*
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

            Log.i(TAG, marker.getTitle() +
                    " has been clicked " + clickCount + " times.");
        }*/

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
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
    @RequiresApi(api = Build.VERSION_CODES.M)
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

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Maps.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            zoomToCurrentLoc();
                            Log.i(TAG, "onSuccess: "+latitude+" "+longitude);
                        }else {
                            Log.i(TAG, "NEW LOCATION NULL");
                        }
                    }

                });

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
    ValueEventListener valueEventListener;
    private void DoctorList(long hospital_id){

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(Maps.this);
        View myView = getLayoutInflater().inflate(R.layout.doctor_list, null);

        RecyclerView RV_doctors = myView.findViewById(R.id.RV_doctors);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RV_doctors.setLayoutManager(mLayoutManager);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DOCTORS = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Doctor d = ds.getValue(Doctor.class);
                    Log.i(TAG, d.getName());

                    DOCTORS.add(d);
                }

                RecyclerView.Adapter mRecycleAdapter = new RecycleViewAdapterForDoctors(getApplicationContext(), DOCTORS);
                RV_doctors.setAdapter(mRecycleAdapter);

                if(DOCTORS.size()>0){
                    myBuilder.setView(myView);
                    final AlertDialog Dialog = myBuilder.create();
                    Dialog.show();
                }else {
                    Toast.makeText(getApplicationContext(), "No Doctors available in this Hospital", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_LONG).show();
            }
        };
        doc_Ref = database.getReference("Doctor");
        doc_Ref.orderByChild("hospital_id")
                .equalTo(hospital_id)
                .addValueEventListener(valueEventListener);





    }

    public class RecycleViewAdapterForDoctors extends RecyclerView.Adapter<RecycleViewAdapterForDoctors.ViewHolder> {

        String TAG = "XIAN";

        ArrayList<Doctor> doctors;
        Context context;

        public RecycleViewAdapterForDoctors(Context context, ArrayList<Doctor> doctors) {
            super();
            this.context = context;
            this.doctors = doctors;
            //Log.i(TAG,"RECYCLE VIEW Constructor");
        }

        @Override
        public RecycleViewAdapterForDoctors.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.single_doctor, viewGroup, false);

            RecycleViewAdapterForDoctors.ViewHolder viewHolder = new RecycleViewAdapterForDoctors.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecycleViewAdapterForDoctors.ViewHolder viewHolder, final int i) {

            //Log.i(TAG,i+" RECYCLE VIEW "+(productList.get(i).getProductName()));
            //!PigeonList.get(i).getPigeonPicture().isEmpty() &&
            /*if(!PigeonList.get(i).getPigeonPicture().equalsIgnoreCase("Null")){
                viewHolder.IV_pigeonPicture.setImageBitmap(decodeBase64Image(PigeonList.get(i).getPigeonPicture()));
            }else{

            }*/
            viewHolder.TV_doctorName.setText(doctors.get(i).getName());
            viewHolder.TV_doctorDesignation.setText(doctors.get(i).getDesignation());
            viewHolder.TV_doctorDetails.setText(doctors.get(i).getDetails());

            viewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {

                    } else {

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private TextView TV_doctorName, TV_doctorDesignation, TV_doctorDetails;
            //ImageView IV_server_icon;

            private ItemClickListener clickListener;

            public ViewHolder(View itemView) {
                super(itemView);

                TV_doctorName = itemView.findViewById(R.id.TV_doctorName);
                TV_doctorDesignation = itemView.findViewById(R.id.TV_doctorDesignation);
                TV_doctorDetails = itemView.findViewById(R.id.TV_doctorDetails);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(valueEventListener != null){
            doc_Ref.removeEventListener(valueEventListener);
        }

    }
/*private void drawDirectionLine(LatLng origin, LatLng dest){
        //LatLng origin = (LatLng) markerPoints.get(0);
        //LatLng dest = (LatLng) markerPoints.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }*/

}