package com.secretdevbd.emergencyambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.secretdevbd.emergencyambulance.models.User;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    String TAG = "XIAN";

    EditText ET_email, ET_pass;
    Button btn_login, btn_signup;
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
        btn_signup = findViewById(R.id.btn_signup);

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

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpForm();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "Checking for login information, Please wait.", Toast.LENGTH_LONG).show();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.i(TAG, "Current user logged in");
            Toast.makeText(getApplicationContext(),"Current user logged already in", Toast.LENGTH_SHORT).show();
            getUserData(currentUser);

        }else {
            Log.i(TAG, "NO user logged in");
            Toast.makeText(getApplicationContext(),"No User logged in, Please login.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserData(FirebaseUser currentUser){
        String uid = currentUser.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("USER");
        dbRef.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_model = null;
                for (DataSnapshot ds:snapshot.getChildren()){
                    user_model = ds.getValue(User.class);
                    Log.i(TAG, "USER FOUND = "+user_model.getId());
                }

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("user", user_model);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                            getUserData(user);

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

    private void SignUpForm(){

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(LoginActivity.this);
        View myView = getLayoutInflater().inflate(R.layout.register_dialog, null);

        final EditText ET_regEmail, ET_pWord1, ET_pWord2;
        Button btn_signUpDone;

        ET_regEmail = myView.findViewById(R.id.ET_regEmail);
        ET_pWord1 = myView.findViewById(R.id.ET_pWord1);
        ET_pWord2 = myView.findViewById(R.id.ET_pWord2);
        btn_signUpDone = myView.findViewById(R.id.btn_signUpDone);

        myBuilder.setView(myView);
        final AlertDialog Dialog = myBuilder.create();
        Dialog.show();


        btn_signUpDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ET_regEmail.getText().toString().trim();
                String pass1 = ET_pWord1.getText().toString().trim();
                String pass2 = ET_pWord2.getText().toString().trim();

                if(pass1.equals(pass2)){
                    signUp(email,pass1,Dialog);

                }else{
                    Toast.makeText(getApplicationContext(),"Password doesn't match",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    ProgressDialog dialog;

    private void signUp(final String Email, final String password, final AlertDialog Dialog){

        //String email = inputEmail.getText().toString().trim();
        //String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_LONG).show();
            return;
        }
        dialog = ProgressDialog.show(LoginActivity.this, "Sign Up",
                "Registration processing. Please wait...", true);
        //progressBar.setVisibility(View.VISIBLE);
        //create user
        mAuth.createUserWithEmailAndPassword(Email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG,"createUserWithEmail:onComplete:" + task.isSuccessful());
                        //progressBar.setVisibility(View.GONE);


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            dialog.cancel();

                        } else {

                            Toast.makeText(getApplicationContext(), "Registration Complete Successfully, Please login now",
                                    Toast.LENGTH_LONG).show();

                            FirebaseUser fu = task.getResult().getUser();
                            String UID = fu.getUid();

                            User user = new User(UID, Email, password, "USER") ;
                            DatabaseReference bdRef =  FirebaseDatabase.getInstance().getReference("USER");
                            bdRef.child(UID).setValue(user);
                            Dialog.cancel();
                            dialog.dismiss();
                            //startActivity(new Intent(Login.this, MainActivity.class));
                            //finish();
                        }
                    }
                });
    }
}