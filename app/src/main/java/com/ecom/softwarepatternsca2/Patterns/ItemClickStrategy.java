//package com.ecom.softwarepatternsca2.Patterns;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.ecom.softwarepatternsca2.Interfaces.AdminCheckCallback;
//import com.ecom.softwarepatternsca2.MainActivityClasses.AdminSelectedItemActivity;
//import com.ecom.softwarepatternsca2.MainActivityClasses.CustomerSelectedItemActivity;
//import com.ecom.softwarepatternsca2.ModelClasses.Stock;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//public class ItemClickStrategy {
//
//    public void handleItemClick(Context context, Stock stock) {
//        // Default implementation for handling item click
//        // Add custom behavior as needed
//        checkIfAdmin(new AdminCheckCallback() {
//            @Override
//            public void onResult(boolean isAdmin) {
//                // Open appropriate activity based on user role
//                if (isAdmin) {
//                    Intent intent = new Intent(context, AdminSelectedItemActivity.class);
//                    context.startActivity(intent);
//                } else {
//                    Intent intent = new Intent(context, CustomerSelectedItemActivity.class);
//                    context.startActivity(intent);
//                }
//            }
//        });
//    }
//
//    // Method to check if the current user is an admin
//    private void checkIfAdmin(AdminCheckCallback callback) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            // Query the Admin collection to check if the current user's ID is present
//            db.collection("Admin")
//                    .whereEqualTo("adminId", userId)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                boolean isAdmin = false;
//                                for (DocumentSnapshot document : task.getResult()) {
//                                    // If a document is found with the current user's ID, the user is an admin
//                                    isAdmin = true;
//                                    break;
//                                }
//                                // Callback with the result
//                                callback.onResult(isAdmin);
//                            } else {
//                                // Error occurred while querying Admin collection
//                                Log.e("TAG", "Error getting documents: ", task.getException());
//                                // Callback with the result indicating the user is not an admin
//                                callback.onResult(false);
//                            }
//                        }
//                    });
//        } else {
//            // Current user is not logged in, hence not an admin
//            callback.onResult(false);
//        }
//    }
//}
//
