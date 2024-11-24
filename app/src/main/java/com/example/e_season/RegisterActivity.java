package com.example.e_season;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int RC_SIGN_IN = 9001;

    // Firebase Authentication
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    // UI Components
    private EditText fullNameEditText, emailEditText, createPasswordEditText, confirmPasswordEditText;
    private Button registerButton;
    private LinearLayout googleLoginContainer;

    // Password pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$]).{8,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Ensure layout file name matches

        try {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Bind UI components
            fullNameEditText = findViewById(R.id.fullNameEditText);
            emailEditText = findViewById(R.id.emailEditText);
            createPasswordEditText = findViewById(R.id.createPasswordEditText);
            confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
            registerButton = findViewById(R.id.registerButton);
            googleLoginContainer = findViewById(R.id.googleLoginContainer);

            // Configure Google Sign-In
            configureGoogleSignIn();

            // Register button click listener
            registerButton.setOnClickListener(v -> registerUserWithEmail());

            // Google login button click listener
            googleLoginContainer.setOnClickListener(v -> signInWithGoogle());
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate", e);
            Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void configureGoogleSignIn() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Firebase Web Client ID
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        } catch (Exception e) {
            Log.e(TAG, "Exception in configureGoogleSignIn", e);
            Toast.makeText(this, "An error occurred while configuring Google Sign-In: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUserWithEmail() {
        try {
            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = createPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate input fields
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                Toast.makeText(this, "Password must be at least 8 characters long and include at least one uppercase letter, one number, and one special character (@, #, $).", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register with Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(this, "Welcome " + fullName + "!", Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity or login
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in registerUserWithEmail", e);
            Toast.makeText(this, "An error occurred during registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithGoogle() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            Log.e(TAG, "Exception in signInWithGoogle", e);
            Toast.makeText(this, "An error occurred while signing in with Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign-in failed", e);
                    Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult", e);
            Toast.makeText(this, "An error occurred while handling sign-in result: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity or another screen
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Google authentication failed.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in firebaseAuthWithGoogle", e);
            Toast.makeText(this, "An error occurred while authenticating with Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}