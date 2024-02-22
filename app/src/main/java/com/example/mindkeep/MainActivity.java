package com.example.mindkeep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //User is signed in use an intent to move to another activity
        }
    }

    public void signup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new
                        OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult>
                                                           task) {
                                if (task.isSuccessful()) {
                                    Log.d("MainActivity",
                                            "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this,
                                            "Authentication success. Use an intent to move to a new activity",
                                            Toast.LENGTH_SHORT).show();
                                    //user has been signed in, use an intent to move to; the next; activity
                                    Intent intent = new Intent(MainActivity.this, Login_screen.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                            Log.w("MainActivity",
                                                    "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
    public void signupButtonClicked(View view){
        EditText email = findViewById(R.id.username_input);
        EditText password = findViewById(R.id.password_input);

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        signup(sEmail, sPassword);
    }

}