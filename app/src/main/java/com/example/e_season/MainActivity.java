package com.example.e_season;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Declare FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout from your activity_main.xml
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Log Firebase initialization status
        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.d("FirebaseCheck", "Firebase is not initialized.");
        } else {
            Log.d("FirebaseCheck", "Firebase successfully initialized.");
        }

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // If the user is logged in, navigate directly to the HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so it doesn't stay in the back stack
        } else {
            // If the user is not logged in, show the welcome page
            setupWelcomePage();
        }
    }

    // Set up the welcome page with a button to navigate to LoginActivity
    private void setupWelcomePage() {
        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setVisibility(View.VISIBLE); // Ensure the button is visible
        continueButton.setOnClickListener(view -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Navigating to Login Screen", Toast.LENGTH_SHORT).show();
        });
    }
}
