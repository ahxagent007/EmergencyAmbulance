package com.secretdevbd.emergencyambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    String TAG = "XIAN";

    EditText ET_email, ET_pass;
    Button btn_login;
    ProgressBar PB_loading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        ET_email = findViewById(R.id.ET_email);
        ET_pass = findViewById(R.id.ET_pass);
        btn_login = findViewById(R.id.btn_login);
        PB_loading = findViewById(R.id.PB_loading);

        PB_loading.setVisibility(View.INVISIBLE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PB_loading.setVisibility(View.VISIBLE);
                if (ET_email.getText().toString().length()<1 && ET_pass.getText().toString().length()<1){
                    Toast.makeText(getApplicationContext(), "Enter email and password", Toast.LENGTH_LONG).show();
                    PB_loading.setVisibility(View.INVISIBLE);
                }else {
                    signIn(ET_email.getText().toString(), ET_pass.getText().toString());
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.i(TAG, "Current user logged in");
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("user", currentUser);
            startActivity(i);
            finish();
        }else {
            Log.i(TAG, "NO user logged in");

        }
    }


    public void signIn(String email, String password){
        Log.i(TAG, email+" "+ password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra("user", user);
                            startActivity(i);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            // ...
                        }

                        PB_loading.setVisibility(View.INVISIBLE);

                        // ...
                    }
                });
    }
}