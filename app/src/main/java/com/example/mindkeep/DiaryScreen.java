package com.example.mindkeep;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

public class DiaryScreen extends AppCompatActivity {

    TextView mood_slider_value;
    Slider mood_rating_slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_screen);

        TextView calendartext =findViewById(R.id.calendartext);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy ", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        calendartext.setText(currentDate);

        mood_rating_slider=findViewById(R.id.mood_rating_slider);
        mood_slider_value=findViewById(R.id.mood_slider_value);


        mood_rating_slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mood_slider_value.setText(Float.toString(value));
            }
        });

    }

}