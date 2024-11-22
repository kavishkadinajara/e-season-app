package com.example.e_season;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;

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

        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.d("FirebaseCheck", "Firebase is not initialized.");
        } else {
            Log.d("FirebaseCheck", "Firebase successfully initialized.");
        }

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // If user is logged in, navigate to the Dashboard
            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
            finish();  // Close MainActivity to prevent the user from coming back to it
        } else {
            // If user is not logged in, show the MainActivity screen
            Button continueButton = findViewById(R.id.continue_button);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Navigate to LoginActivity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Navigating to Login Screen", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
