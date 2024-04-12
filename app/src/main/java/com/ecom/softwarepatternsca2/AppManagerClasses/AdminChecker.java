package com.ecom.softwarepatternsca2.AppManagerClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.Interfaces.AdminCheckCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminChecker {
    public static void checkIfAdmin(AdminCheckCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Query the Customers collection to check if the current user's ID is present
            db.collection("Customers")
                    .whereEqualTo("customerId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean isAdmin = false;
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Check if the role field is not null and set to admin
                                    String role = document.getString("role");
                                    isAdmin = role != null && role.equals("admin");
                                    if (isAdmin) {
                                        break;
                                    }
                                }
                                // Callback with the result
                                callback.onResult(isAdmin);
                            } else {
                                // Error occurred while querying Customers collection
                                Log.e("AdminChecker", "Error getting documents: ", task.getException());
                                // Callback with the result indicating the user is not an admin
                                callback.onResult(false);
                            }
                        }
                    });
        } else {
            // Current user is not logged in, hence not an admin
            callback.onResult(false);
        }
    }
}
