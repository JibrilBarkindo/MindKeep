package com.example.mindkeep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class Menu_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        ImageView logout = findViewById(R.id.logoutButton);

        logout.setOnClickListener(new View.OnClickListener()

        {

            public void onClick (View v){
                FirebaseAuth.getInstance().signOut(); // Log out from Firebase

                // Redirect to login screen
                Intent intent = new Intent(Menu_screen.this, StartScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void DiaryButtonClicked(View view) {
        Intent intent = new Intent(this, DiaryScreen.class);
        startActivity(intent);
    }

    public void scrapbookButtonClicked(View view) {
        Intent intent = new Intent(this, Scrapbook.class);
        startActivity(intent);
    }


}