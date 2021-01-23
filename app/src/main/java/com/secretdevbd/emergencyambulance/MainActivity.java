package com.secretdevbd.emergencyambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    String TAG = "XIAN";

    TextView ET_username;
    Button btn_hospitals, btn_ambulance, btn_maps, btn_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = (FirebaseUser) getIntent().getExtras().get("user");
        //Log.i(TAG, user.getEmail());

        ET_username = findViewById(R.id.ET_username);
        btn_hospitals = findViewById(R.id.btn_hospitals);
        btn_ambulance = findViewById(R.id.btn_ambulance);
        btn_maps = findViewById(R.id.btn_maps);
        btn_admin = findViewById(R.id.btn_admin);

        ET_username.setText(user.getEmail());

        startActivity(new Intent(getApplicationContext(), Maps.class));

        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Maps.class));
            }
        });

    }
}