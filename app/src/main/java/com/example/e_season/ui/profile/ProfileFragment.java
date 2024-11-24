package com.example.e_season.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.e_season.MainActivity;
import com.example.e_season.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private EditText editFullName, editAddress, editTelephone, editEmail;
    private Button updateProfileButton, changePasswordButton, editProfileButton, logoutButton, removeProfileButton;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference seasonsDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        editFullName = root.findViewById(R.id.editFullName);
        editAddress = root.findViewById(R.id.editAddress);
        editTelephone = root.findViewById(R.id.editTelephone);
        editEmail = root.findViewById(R.id.editEmail);
        editEmail.setEnabled(false); // Make email field read-only
        updateProfileButton = root.findViewById(R.id.updateProfileButton);
        changePasswordButton = root.findViewById(R.id.changePasswordButton);
        editProfileButton = root.findViewById(R.id.editProfileButton);
        logoutButton = root.findViewById(R.id.logoutButton);
        removeProfileButton = root.findViewById(R.id.removeProfileButton);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();

            Log.d(TAG, "User ID: " + userId);
            Log.d(TAG, "User Email: " + userEmail);

            userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            seasonsDatabaseReference = FirebaseDatabase.getInstance().getReference("seasons");
            checkAndInitializeUser();
            loadUserProfile();
        }

        updateProfileButton.setOnClickListener(v -> updateProfile());
        changePasswordButton.setOnClickListener(v -> changePassword());
        editProfileButton.setOnClickListener(v -> toggleEditMode(true));
        logoutButton.setOnClickListener(v -> logout());
        removeProfileButton.setOnClickListener(v -> removeProfile());

        // Initially make all fields read-only
        toggleEditMode(false);

        return root;
    }

    private void checkAndInitializeUser() {
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d(TAG, "User not found in database. Initializing user...");
                    HashMap<String, String> userProfile = new HashMap<>();
                    userProfile.put("email", currentUser.getEmail());
                    userProfile.put("fullName", ""); // Initialize as empty
                    userProfile.put("address", "");
                    userProfile.put("telephone", "");

                    userDatabaseReference.setValue(userProfile).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User initialized successfully in database.");
                            Toast.makeText(getContext(), "User initialized successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to initialize user in database.");
                            Toast.makeText(getContext(), "Failed to initialize user", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "User already exists in database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile() {
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String telephone = snapshot.child("telephone").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    editFullName.setText(fullName);
                    editAddress.setText(address);
                    editTelephone.setText(telephone);
                    editEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String fullName = editFullName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String telephone = editTelephone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(address) || TextUtils.isEmpty(telephone)) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("fullName", fullName);
        updatedFields.put("address", address);
        updatedFields.put("telephone", telephone);

        Log.d(TAG, "Updating profile with: " + updatedFields.toString());

        userDatabaseReference.updateChildren(updatedFields).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Profile updated successfully.");
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                toggleEditMode(false); // Make fields read-only after update
            } else {
                Log.e(TAG, "Failed to update profile.");
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        if (currentUser != null && currentUser.getEmail() != null) {
            Log.d(TAG, "Sending password reset email to: " + currentUser.getEmail());
            firebaseAuth.sendPasswordResetEmail(currentUser.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent successfully.");
                            Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to send password reset email.");
                            Toast.makeText(getContext(), "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(TAG, "Error: Unable to fetch user email.");
            Toast.makeText(getContext(), "Error: Unable to fetch user email", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEditMode(boolean isEditable) {
        editFullName.setEnabled(isEditable);
        editAddress.setEnabled(isEditable);
        editTelephone.setEnabled(isEditable);
        updateProfileButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        editProfileButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
    }

    private void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void removeProfile() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();

            // Remove user profile
            userDatabaseReference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Profile removed successfully.");
                    Toast.makeText(getContext(), "Profile removed successfully", Toast.LENGTH_SHORT).show();

                    // Remove related seasons
                    seasonsDatabaseReference.orderByChild("userEmail").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot seasonSnapshot : snapshot.getChildren()) {
                                seasonSnapshot.getRef().removeValue();
                            }
                            Log.d(TAG, "Related seasons removed successfully.");
                            Toast.makeText(getContext(), "Related seasons removed successfully", Toast.LENGTH_SHORT).show();

                            // Delete the Firebase Authentication user
                            currentUser.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                    Toast.makeText(getContext(), "User account deleted", Toast.LENGTH_SHORT).show();
                                    logout(); // Log out the user after removing the profile and related seasons
                                } else {
                                    Log.e(TAG, "Failed to delete user account.");
                                    Toast.makeText(getContext(), "Failed to delete user account", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to remove related seasons: " + error.getMessage());
                            Toast.makeText(getContext(), "Failed to remove related seasons", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to remove profile.");
                    Toast.makeText(getContext(), "Failed to remove profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Error: Unable to fetch user.");
            Toast.makeText(getContext(), "Error: Unable to fetch user", Toast.LENGTH_SHORT).show();
        }
    }
}