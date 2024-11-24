package com.example.e_season;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    // Firebase Authentication
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    // UI Components
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private LinearLayout googleLoginContainer;
    private TextView registerLink;

    // Password pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$]).{8,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Bind UI components
            usernameEditText = findViewById(R.id.usernameEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            googleLoginContainer = findViewById(R.id.googleLoginContainer);
            registerLink = findViewById(R.id.registerLink);
            View passwordResetLink = findViewById(R.id.forgotPasswordLink);

            // Configure Google Sign-In
            configureGoogleSignIn();

            // Login with Email and Password
            loginButton.setOnClickListener(v -> loginWithEmail());

            // Login with Google
            googleLoginContainer.setOnClickListener(v -> signInWithGoogle());

            // Navigate to Register Page
            registerLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Navigating to Register Screen", Toast.LENGTH_SHORT).show();
            });

            // Navigate to Reset Password Page
            passwordResetLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Navigating to reset password Screen", Toast.LENGTH_SHORT).show();
            });
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

    private void loginWithEmail() {
        try {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
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

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            // Navigate to Home Page
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in loginWithEmail", e);
            Toast.makeText(this, "An error occurred while logging in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            // Handle Google Sign-In Result
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

                            // Navigate to Home Page
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Google authentication failed", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in firebaseAuthWithGoogle", e);
            Toast.makeText(this, "An error occurred while authenticating with Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}