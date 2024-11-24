package com.example.e_season;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    private EditText slipNumberInput, paymentDateInput;
    private Button submitPaymentButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        try {
            slipNumberInput = findViewById(R.id.slipNumberInput);
            paymentDateInput = findViewById(R.id.paymentDateInput);
            submitPaymentButton = findViewById(R.id.submitPaymentButton);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference("seasons");

            paymentDateInput.setOnClickListener(v -> showDatePickerDialog());

            submitPaymentButton.setOnClickListener(v -> submitPayment());
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate", e);
            showError("An error occurred during initialization: " + e.getMessage());
        }
    }

    private void showDatePickerDialog() {
        try {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        paymentDateInput.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in showDatePickerDialog", e);
            showError("An error occurred while showing date picker: " + e.getMessage());
        }
    }

    private void submitPayment() {
        try {
            String slipNumber = slipNumberInput.getText().toString().trim();
            String paymentDate = paymentDateInput.getText().toString().trim();

            if (slipNumber.isEmpty() || paymentDate.isEmpty()) {
                showError("Please enter all details");
                return;
            }

            // Get season data from intent
            String startStation = getIntent().getStringExtra("startStation");
            String endStation = getIntent().getStringExtra("endStation");
            String seasonStartDate = getIntent().getStringExtra("seasonStartDate");
            String seasonEndDate = getIntent().getStringExtra("seasonEndDate");
            String selectedClass = getIntent().getStringExtra("selectedClass");
            String seasonPrice = getIntent().getStringExtra("seasonPrice");
            String userEmail = getIntent().getStringExtra("userEmail");

            // Log the data being saved to the database
            Log.d(TAG, "Start Station: " + startStation);
            Log.d(TAG, "End Station: " + endStation);
            Log.d(TAG, "Season Start Date: " + seasonStartDate);
            Log.d(TAG, "Season End Date: " + seasonEndDate);
            Log.d(TAG, "Selected Class: " + selectedClass);
            Log.d(TAG, "Season Price: " + seasonPrice);
            Log.d(TAG, "User Email: " + userEmail);
            Log.d(TAG, "Slip Number: " + slipNumber);
            Log.d(TAG, "Payment Date: " + paymentDate);

            HashMap<String, Object> seasonData = new HashMap<>();
            seasonData.put("startStation", startStation);
            seasonData.put("endStation", endStation);
            seasonData.put("seasonStartDate", seasonStartDate);
            seasonData.put("seasonEndDate", seasonEndDate);
            seasonData.put("classType", selectedClass);
            seasonData.put("seasonPrice", seasonPrice);
            seasonData.put("userEmail", userEmail);
            seasonData.put("slipNumber", slipNumber);
            seasonData.put("paymentDate", paymentDate);

            databaseReference.push().setValue(seasonData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Snackbar.make(findViewById(android.R.id.content), "Season applied and payment submitted successfully", Snackbar.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    showError("Failed to submit payment");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in submitPayment", e);
            showError("An error occurred while submitting payment: " + e.getMessage());
        }
    }

    private void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG)
                .setAction("Copy", v -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Error Message", errorMessage);
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(findViewById(android.R.id.content), "Error message copied to clipboard", Snackbar.LENGTH_SHORT).show();
                });
        snackbar.show();
    }
}