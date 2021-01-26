package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.secretdevbd.emergencyambulance.models.Hospital;

public class AddHospital extends AppCompatActivity {

    CheckBox chk_diabetics, chk_all, chk_heart, chk_eye;
    Button btn_add;
    EditText ET_name, ET_title, ET_lat, ET_long;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hospital);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Hospital");

        chk_diabetics = findViewById(R.id.chk_diabetics);
        chk_all = findViewById(R.id.chk_all);
        chk_heart = findViewById(R.id.chk_heart);
        chk_eye = findViewById(R.id.chk_eye);
        btn_add = findViewById(R.id.btn_add);

        ET_name = findViewById(R.id.ET_name);
        ET_title = findViewById(R.id.ET_title);
        ET_lat = findViewById(R.id.ET_lat);
        ET_long = findViewById(R.id.ET_long);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "";
                if(chk_diabetics.isChecked()){
                    category += "Diabetics, ";
                }
                if(chk_all.isChecked()){
                    category += "All, ";
                }
                if(chk_heart.isChecked()){
                    category += "Heart, ";
                }
                if(chk_eye.isChecked()){
                    category += "Eye,";
                }
                //Toast.makeText(getApplicationContext(), category, Toast.LENGTH_SHORT).show();
                if (category != ""){
                    addHospitalToFirebase(category, ET_name.getText().toString(), ET_title.getText().toString(), Double.parseDouble(ET_lat.getText().toString()), Double.parseDouble(ET_long.getText().toString()));
                }else {
                    Toast.makeText(getApplicationContext(), "Please Select at least one category", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addHospitalToFirebase(String cat, String name, String title,double lat,double longg){
        long id = System.currentTimeMillis();
        myRef.child(""+id).setValue(new Hospital(id, cat, name, title, lat, longg));
        Toast.makeText(getApplicationContext(),"Hospital Added", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        String str="";
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chk_diabetics:
                str = checked?"chk_diabetics Selected":"chk_diabetics Deselected";
                break;
            case R.id.chk_all:
                str = checked?"chk_all Selected":"chk_all Deselected";
                break;
            case R.id.chk_heart:
                str = checked?"chk_heart Selected":"chk_heart Deselected";
                break;
            case R.id.chk_eye:
                str = checked?"chk_eye Selected":"chk_eye Deselected";
                break;
        }
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}