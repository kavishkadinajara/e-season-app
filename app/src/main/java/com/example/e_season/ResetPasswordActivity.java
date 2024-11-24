package com.example.e_season;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    private EditText emailEditText;
    private Button sendOtpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        try {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Bind UI components
            emailEditText = findViewById(R.id.emailEditText);
            sendOtpButton = findViewById(R.id.sendOtpButton);

            // Set click listener for the Send OTP button
            sendOtpButton.setOnClickListener(v -> sendPasswordResetEmail());
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate", e);
            Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPasswordResetEmail() {
        try {
            String email = emailEditText.getText().toString().trim();

            // Validate email
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send password reset email
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error occurred";
                            Toast.makeText(this, "Failed to send reset email: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in sendPasswordResetEmail", e);
            Toast.makeText(this, "An error occurred while sending reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}