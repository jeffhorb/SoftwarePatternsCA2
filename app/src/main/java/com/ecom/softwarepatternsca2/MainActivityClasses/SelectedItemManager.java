package com.ecom.softwarepatternsca2.MainActivityClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SelectedItemManager {

    public static void simulatePurchase(Context context, String itemName, int purchaseQuantity) {
        // Use the singleton instance of FirestoreManager
        FirestoreManager firestoreManager = FirestoreManager.getInstance();
        firestoreManager.getDocumentId("Stock", "itemName", itemName, new FirestoreManager.OnDocumentIdRetrievedListener() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {
                if (documentId != null) {
                    FirebaseFirestore.getInstance().collection("Stock").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String existingQuantityString = document.getString("quantity");
                                    if (existingQuantityString != null) {
                                        try {
                                            int existingQuantity = Integer.parseInt(existingQuantityString);
                                            if (purchaseQuantity <= existingQuantity) {
                                                int remainingQuantity = existingQuantity - purchaseQuantity;
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("quantity", String.valueOf(remainingQuantity));
                                                firestoreManager.updateDocument("Stock", documentId, data, new FirestoreManager.OnUpdateCompleteListener() {
                                                    @Override
                                                    public void onUpdateComplete(boolean success) {
                                                        if (success) {
                                                            Toast.makeText(context, "Purchase successful", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(context, "Purchase cancelled", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                showInsufficientStockDialog(context, existingQuantity);
                                            }
                                        } catch (NumberFormatException e) {
                                            Toast.makeText(context, "Invalid quantity format", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Existing quantity not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Failed to get document: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Document ID not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void showInsufficientStockDialog(Context context, int remainingQuantity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Insufficient Stock");
        builder.setMessage("The remaining quantity is " + remainingQuantity + " units. You cannot purchase more than the available stock.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showRatings(Context context, String itemId) {
        // Use the singleton instance of FirestoreManager
        FirestoreManager firestoreManager = FirestoreManager.getInstance();
        firestoreManager.firestore.collection("ItemRatings")
                .whereEqualTo("itemId", itemId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "ItemRatings query successful");
                            // Create a dialog to display ratings, comments, and customer names
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Item Ratings & Comments");

                            // Create a LinearLayout to hold the ratings, comments, and customer names
                            LinearLayout layout = new LinearLayout(context);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            ScrollView scrollView = new ScrollView(context);
                            scrollView.addView(layout);

                            // Add each rating, comment, and customer name to the layout
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String ratingId = document.getString("ratingId");
                                Log.d("TAG", "Rating query successful" + ratingId);

                                // Query the Ratings collection to retrieve the rating and comment
                                assert ratingId != null;
                                firestoreManager.firestore.collection("Ratings")
                                        .document(ratingId)
                                        .get()
                                        .addOnSuccessListener(ratingSnapshot -> {
                                            if (ratingSnapshot.exists()) {
                                                Log.d("TAG", "Rating query successful");
                                                int rating = ratingSnapshot.getLong("rating").intValue();
                                                String comment = ratingSnapshot.getString("comment");
                                                String rId = ratingSnapshot.getId();
                                                Log.d("TAG", "Rating query successful" + rId);
                                                // Query the CustomerRatings collection to retrieve the customerId
                                                firestoreManager.firestore.collection("CustomerRatings")
                                                        .whereEqualTo("ratingId", rId)
                                                        .get()
                                                        .addOnSuccessListener(customerQuerySnapshot -> {
                                                            for (QueryDocumentSnapshot customerDocument : customerQuerySnapshot) {
                                                                String customerId = customerDocument.getString("customerId");
                                                                Log.d("TAG", " query successful" + customerId);

                                                                // Query the Customers collection to retrieve the customer name
                                                                firestoreManager.firestore.collection("Customers")
                                                                        .whereEqualTo("customerId", customerId)
                                                                        .get()
                                                                        .addOnSuccessListener(customerQuerySnapshot1 -> {
                                                                            for (QueryDocumentSnapshot customerDocument1 : customerQuerySnapshot1) {
                                                                                String customerName = customerDocument1.getString("customerName");
                                                                                TextView textView = new TextView(context);
                                                                                textView.setText("Customer: " + customerName + "\nRating: " + rating + " star(s)" + "\nComment: " + comment + "\n");
                                                                                layout.addView(textView);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(e -> Log.e("TAG", "Error getting customer name: ", e));
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.e("TAG", "Error getting customer rating: ", e));
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("TAG", "Error getting rating data: ", e));
                            }

                            builder.setView(scrollView);

                            // Add a button to dismiss the dialog
                            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

                            // Show the dialog
                            builder.show();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
