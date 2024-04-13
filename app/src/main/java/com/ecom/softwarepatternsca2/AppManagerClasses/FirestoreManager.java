package com.ecom.softwarepatternsca2.AppManagerClasses;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class FirestoreManager {

    public final FirebaseFirestore firestore;

    private static FirestoreManager instance;

    public FirestoreManager() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    //singleton instance
    public static FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    public interface OnQuantityUpdateListener {
        void onQuantityUpdate(String updatedQuantity);
    }


    public void listenForQuantityUpdates(String itemId, OnQuantityUpdateListener listener) {
        FirebaseFirestore.getInstance().collection("Stock").document(itemId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle errors
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Retrieve updated quantity from snapshot
                            String updatedQuantity = snapshot.getString("quantity");

                            // Notify listener
                            if (listener != null) {
                                listener.onQuantityUpdate(updatedQuantity);
                            }
                        }
                    }
                });
    }

    // Method to get the document ID from Firestore
    public void getDocumentId(String collectionPath, String fieldName, String value, final OnDocumentIdRetrievedListener listener) {
        firestore.collection(collectionPath)
                .whereEqualTo(fieldName, value)
                .limit(1)  // Assuming there's only one document with the given field value
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        if (listener != null) {
                            listener.onDocumentIdRetrieved(documentId);
                        }
                    } else {
                        if (listener != null) {
                            listener.onDocumentIdRetrieved(null);
                        }
                    }
                });
    }

    // Method to update document in Firestore
    public void updateDocument(String collectionPath, String documentId, Map<String, Object> data, OnUpdateCompleteListener listener) {
        firestore.collection(collectionPath)
                .document(documentId)
                .update(data)
                .addOnCompleteListener(task -> {
                    if (listener != null) {
                        if (task.isSuccessful()) {
                            listener.onUpdateComplete(true);
                        } else {
                            listener.onUpdateComplete(false);
                        }
                    }
                });
    }

    public interface OnDocumentIdRetrievedListener {
        void onDocumentIdRetrieved(String documentId);
    }

    public interface OnUpdateCompleteListener {
        void onUpdateComplete(boolean success);
    }

}


