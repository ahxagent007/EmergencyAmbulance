package com.secretdevbd.emergencyambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.secretdevbd.emergencyambulance.models.Ambulance;
import com.secretdevbd.emergencyambulance.models.Doctor;
import com.secretdevbd.emergencyambulance.models.Hospital;

import java.util.ArrayList;

public class AddDoctorActivity extends AppCompatActivity {
    String TAG = "XIAN";
    DatabaseReference mDatabaseRef, docRef;
    ArrayAdapter<String> spinnerArrayAdapter;
    ArrayList<Hospital> hospitalList = new ArrayList<>();
    ArrayList<String> hospitals_name = new ArrayList<>();

    Spinner SP_hospital;
    EditText ET_name, ET_designation, ET_details;
    Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        SP_hospital = findViewById(R.id.SP_hospital);
        ET_name = findViewById(R.id.ET_name);
        ET_designation = findViewById(R.id.ET_designation);
        ET_details = findViewById(R.id.ET_details);
        btn_add = findViewById(R.id.btn_add);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Hospital");
        docRef = FirebaseDatabase.getInstance().getReference("Doctor");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalList.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Hospital h = ds.getValue(Hospital.class);
                    hospitalList.add(h);
                    hospitals_name.add(h.getName());
                    Log.i(TAG, "Hospital : "+h.getName());
                }
                spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, hospitals_name);
                SP_hospital.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long selected_hos = SP_hospital.getSelectedItemId();
                Long hos_id = hospitalList.get((int)selected_hos).getId();
                adDoctorToFirebase(ET_name.getText().toString(), ET_designation.getText().toString(), ET_details.getText().toString(), hos_id);
            }
        });
    }

    private void adDoctorToFirebase(String name, String designation, String details, long hos_id ){
        long id = System.currentTimeMillis();
        docRef.child(""+id).setValue(new Doctor(id, name, designation, details, hos_id));
        Toast.makeText(getApplicationContext(),"Doctor Added", Toast.LENGTH_SHORT).show();
        finish();
    }

}