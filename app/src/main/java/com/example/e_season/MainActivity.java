package com.example.e_season;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout from your activity_main.xml
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.d("FirebaseCheck", "Firebase is not initialized.");
        } else {
            Log.d("FirebaseCheck", "Firebase successfully initialized.");
        }

        // Example: Set up a button click listener for the "Continue" button
        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Continue button clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
