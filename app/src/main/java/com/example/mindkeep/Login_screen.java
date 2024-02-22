package com.example.mindkeep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
public class Login_screen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login_screen.this, "Authentication success. Use an intent to move to a new activity",
                                    Toast.LENGTH_SHORT).show();
                            //user has been signed in, use an intent to move to the next activity


                            Intent intent = new Intent(Login_screen.this, Menu_screen.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginScreen", "signInWithEmail:failure",
                                    task.getException());
                            Toast.makeText(Login_screen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
            public void loginButtonClicked(View view) {
                EditText emailField = findViewById(R.id.username_input);
                EditText passwordField = findViewById(R.id.password_input);

                String sEmail = emailField.getText().toString();
                String sPassword = passwordField.getText().toString();

                login(sEmail, sPassword);



    }}
