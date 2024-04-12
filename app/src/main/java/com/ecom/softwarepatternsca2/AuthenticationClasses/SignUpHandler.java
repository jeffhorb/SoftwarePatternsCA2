package com.ecom.softwarepatternsca2.AuthenticationClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.ModelClasses.CustomerDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//SignUpHandler is responsible for handling the sign-up process, promoting code reusability and efficiency.
public class SignUpHandler {
    public static void handleSignUp(@NonNull FirebaseAuth authenticate, String email, String password, String customerName, String address1,
                                    String address2, String address3, String eircode, @NonNull ProgressBar pBar, FirebaseFirestore db, Context context) {
        // Perform sign up with email and password
        authenticate.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Hide progress bar
                    pBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Registration successful
                        Toast.makeText(context, "Registration Successful.", Toast.LENGTH_SHORT).show();
                        // Save additional user details to database
                        saveUserDetails(customerName, email, address1, address2, address3, eircode, db);
                        // Redirect to login activity
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        // Registration failed
                        Toast.makeText(context, "Registration Failed.", Toast.LENGTH_SHORT).show();
                        // Display error message
                        if (task.getException() != null) {
                            Log.e("SignUpHandler", "Registration Failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private static void saveUserDetails(String customerName, String customerEmail, String address1, String address2, String address3, String eircode, @NonNull FirebaseFirestore db) {
        // Create a new CustomerDetails object

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String currentCustomerId = currentUser.getUid();
        String role = "";
        CustomerDetails userDetails = new CustomerDetails(customerName, customerEmail, currentCustomerId, address1, address2, address3, eircode,role);

        // Add the user details to the Firestore database
        db.collection("Customers")
                .add(userDetails)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    Log.d("SignUpHandler", "User details added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add the document
                    Log.e("SignUpHandler", "Error adding user details", e);
                });
    }
}


