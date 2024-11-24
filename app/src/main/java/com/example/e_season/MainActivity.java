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

        try {
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
        } catch (Exception e) {
            Log.e("MainActivity", "Exception in onCreate", e);
            Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Set up the welcome page with a button to navigate to LoginActivity
    private void setupWelcomePage() {
        try {
            Button continueButton = findViewById(R.id.continue_button);
            continueButton.setVisibility(View.VISIBLE); // Ensure the button is visible
            continueButton.setOnClickListener(view -> {
                try {
                    // Navigate to LoginActivity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "Navigating to Login Screen", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("MainActivity", "Exception in continueButton onClick", e);
                    Toast.makeText(MainActivity.this, "An error occurred while navigating to Login Screen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("MainActivity", "Exception in setupWelcomePage", e);
            Toast.makeText(this, "An error occurred while setting up the welcome page: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}