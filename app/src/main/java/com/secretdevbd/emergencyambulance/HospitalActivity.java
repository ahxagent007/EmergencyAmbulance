package com.secretdevbd.emergencyambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.secretdevbd.emergencyambulance.models.Hospital;

import java.util.ArrayList;

public class HospitalActivity extends AppCompatActivity {

    String TAG = "XIAN";

    RecyclerView RV_hospitals;
    EditText ET_searchHospital;
    Button btn_searchHospital;

    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayList<Hospital> HOSPITALS = new ArrayList<>();
    ArrayList<Hospital> SEARCH_HOSPITALS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        RV_hospitals = findViewById(R.id.RV_hospitals);
        ET_searchHospital = findViewById(R.id.ET_searchHospital);
        btn_searchHospital = findViewById(R.id.btn_searchHospital);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Hospital");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HOSPITALS = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Hospital hospital = ds.getValue(Hospital.class);
                    Log.i(TAG, hospital.getName());

                    HOSPITALS.add(hospital);
                }

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

        btn_searchHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            viewHolder.TV_hospitalName.setText(Hospitals.get(i).getName());
            viewHolder.TV_hospitalType.setText(Hospitals.get(i).getCategory());
            viewHolder.TV_hospitalDetails.setText(Hospitals.get(i).getTitle());

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

            private TextView TV_hospitalName, TV_hospitalType, TV_hospitalDetails;
            //ImageView IV_server_icon;

            private ItemClickListener clickListener;

            public ViewHolder(View itemView) {
                super(itemView);

                TV_hospitalName = itemView.findViewById(R.id.TV_hospitalName);
                TV_hospitalType = itemView.findViewById(R.id.TV_hospitalType);
                TV_hospitalDetails = itemView.findViewById(R.id.TV_hospitalDetails);

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
}