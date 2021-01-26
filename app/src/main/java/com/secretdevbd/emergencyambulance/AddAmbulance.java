package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.shape.EdgeTreatment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.secretdevbd.emergencyambulance.models.Ambulance;
import com.secretdevbd.emergencyambulance.models.Hospital;

public class AddAmbulance extends AppCompatActivity {

    EditText ET_reg, ET_phone, ET_lat, ET_long;
    Button btn_add;

    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ambulance);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Ambulance");

        ET_reg = findViewById(R.id.ET_reg);
        ET_phone = findViewById(R.id.ET_phone);
        ET_lat = findViewById(R.id.ET_lat);
        ET_long = findViewById(R.id.ET_long);
        btn_add = findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reg = ET_reg.getText().toString();
                String phone = ET_phone.getText().toString();
                double latt = Double.parseDouble(ET_lat.getText().toString());
                double longg = Double.parseDouble(ET_long.getText().toString());

                if (reg != "" && phone != ""){
                    addAmbulanceToFirebase(reg, phone, latt, longg);
                }
            }
        });
    }

    private void addAmbulanceToFirebase(String reg, String phone, double lat,double longg){
        long id = System.currentTimeMillis();
        myRef.child(""+id).setValue(new Ambulance(id, reg, phone, lat, longg));
        Toast.makeText(getApplicationContext(),"Ambulance Added", Toast.LENGTH_SHORT).show();
        finish();
    }
}