package com.example.mindkeep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
    }

    public void DiaryButtonClicked (View view) {
        Intent intent = new Intent(this, DiaryScreen.class);
        startActivity(intent);
    }
    public void scrapbookButtonClicked (View view) {
        Intent intent = new Intent(this, Scrapbook.class);
        startActivity(intent);
    }
}