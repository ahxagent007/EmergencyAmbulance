package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.secretdevbd.emergencyambulance.models.User;

public class MainActivity extends AppCompatActivity {

    String TAG = "XIAN";

    TextView ET_username;
    Button btn_hospitals, btn_ambulance, btn_maps, btn_admin, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseUser user = (FirebaseUser) getIntent().getExtras().get("user");
        User user = (User) getIntent().getSerializableExtra("user");
        //Log.i(TAG, user.getEmail());

        ET_username = findViewById(R.id.ET_username);
        btn_hospitals = findViewById(R.id.btn_hospitals);
        btn_ambulance = findViewById(R.id.btn_ambulance);
        btn_maps = findViewById(R.id.btn_maps);
        btn_admin = findViewById(R.id.btn_admin);
        btn_logout = findViewById(R.id.btn_logout);

        ET_username.setText(user.getEmail());

        //startActivity(new Intent(getApplicationContext(), Hospital.class));

        btn_hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HospitalActivity.class));
            }
        });
        btn_ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AmbulanceActivity.class));
            }
        });
        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Maps.class));
            }
        });
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Admin.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        if(!user.getRole().equalsIgnoreCase("ADMIN")){
            btn_admin.setVisibility(View.INVISIBLE);
        }
    }
}