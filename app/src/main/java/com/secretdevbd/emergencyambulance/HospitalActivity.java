package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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
                    .inflate(R.layout.single_server_view, viewGroup, false);

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
            viewHolder.btn_server.setText(Hospitals.get(i).getServer_name()); //.substring(7, ServerList.get(i).getFtp_server().length())


            viewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {

                        //Log.i(TAG, ServerList.get(position));

                        /*Uri uri = Uri.parse(ServerList.get(position).getFtp_server()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);*/


                    } else {

                        Intent i = new Intent(getContext(), WebViewActivity.class);
                        i.putExtra("WEBSITE", "http://"+Hospitals.get(position).getFtp_server().substring(7, ServerList.get(position).getFtp_server().length()));
                        i.putExtra("WEBSITE_NAME", Hospitals.get(position).getServer_name());
                        startActivity(i);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return Hospitals.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private TextView btn_server;
            ImageView IV_server_icon;

            private ItemClickListener clickListener;

            public ViewHolder(View itemView) {
                super(itemView);

                btn_server = itemView.findViewById(R.id.btn_server);
                IV_server_icon = itemView.findViewById(R.id.IV_server_icon);

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