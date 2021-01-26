package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Admin extends AppCompatActivity {

    Button btn_hospital, btn_ambulance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btn_hospital = findViewById(R.id.btn_hospital);
        btn_ambulance = findViewById(R.id.btn_ambulance);
        
        btn_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddHospital.class));
            }
        });


    }

    /*private void addHospital(){

        // custom dialog
        final Dialog dialog = new Dialog(Admin.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_hospital);
        //dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        final EditText ET_against = dialog.findViewById(R.id.ET_against);
        final EditText ET_details = dialog.findViewById(R.id.ET_details);
        Button btn_fir = dialog.findViewById(R.id.btn_fir);

        final Spinner SP_fir_type = dialog.findViewById(R.id.SP_fir_type);
        final EditText ET_against_address = dialog.findViewById(R.id.ET_against_address);

        // if button is clicked, close the custom dialog
        btn_fir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fir_type = SP_fir_type.getSelectedItem().toString();

                AddNewFIRRequest(USER_NAME, ET_against.getText().toString(), ET_details.getText().toString(), fir_type, ET_against_address.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);

        dialog.show();

    }*/
}