package com.example.mindkeep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Scrapbook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrapbook);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                retrieveDiaryEntry(dateString);
            }

            private void retrieveDiaryEntry(String dateString) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("diaryEntries")
                        .whereEqualTo("date", dateString)
                        .limit(1) // Assuming only one entry per day
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    String content = document.getString("content");
                                    Double moodDouble = document.getDouble("mood"); // Retrieve the mood as Double
                                    float mood = moodDouble != null ? moodDouble.floatValue() : 0f; // Convert Double to float
                                    displayDiaryEntry(dateString, content, mood);
                                } else {
                                    displayNoEntryFound(dateString);
                                }
                            } else {
                                displayError(task.getException().getMessage());
                            }
                        });
            }


            private void displayDiaryEntry(String date, String content, float mood) {
                TextView tvDiaryDate = findViewById(R.id.tvDiaryDate);
                TextView tvDiaryEntry = findViewById(R.id.tvDiaryEntry);
                TextView tvMoodRating = findViewById(R.id.tvMoodRating);

                tvDiaryDate.setText(date);
                tvDiaryEntry.setText(content);
                tvMoodRating.setText("Mood rating: " + mood);
            }

            private void displayNoEntryFound(String date) {
                // Update UI to indicate no entry was found for the selected date
                TextView tvDiaryDate = findViewById(R.id.tvDiaryDate);
                TextView tvDiaryEntry = findViewById(R.id.tvDiaryEntry);
                TextView tvMoodRating = findViewById(R.id.tvMoodRating);

                tvDiaryDate.setText(date);
                tvDiaryEntry.setText("No entry for this date.");
                tvMoodRating.setText(""); // Clear mood rating as there's no entry
            }

            private void displayError(String errorMessage) {
                // Display an error message, e.g., in a Toast or directly in the UI
                Toast.makeText(Scrapbook.this, "Error retrieving entry: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}