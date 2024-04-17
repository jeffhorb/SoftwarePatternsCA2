package com.ecom.softwarepatternsca2.AppManagerClasses;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

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

    public interface TransactionCompletionListener {
        void onTransactionCompleted(boolean success, String errorMessage);
    }

    public void addDocument(String collectionPath, Map<String, Object> data, TransactionCompletionListener listener) {
        CollectionReference collectionReference = firestore.collection(collectionPath);

        collectionReference.add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Document added successfully");
                            listener.onTransactionCompleted(true, null);
                        } else {
                            Log.e("TAG", "Error adding document: " + task.getException());
                            listener.onTransactionCompleted(false, task.getException().getMessage());
                        }
                    }
                });
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

    public void listenForBasketChanges(String customerId, EventListener<QuerySnapshot> listener) {
        firestore.collection("BasketList")
                .whereEqualTo("customerDocumentId", customerId)
                .addSnapshotListener(listener);
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

    public void removeItems(String customerDocId, TransactionCompletionListener listener) {
        firestore.collection("BasketList")
                .whereEqualTo("customerDocumentId", customerDocId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = firestore.batch();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DocumentReference docRef = document.getReference();
                        batch.delete(docRef);
                    }
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("FirestoreManager", "Batch delete successfully executed");
                                listener.onTransactionCompleted(true, null);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreManager", "Error executing batch delete: " + e.getMessage());
                                listener.onTransactionCompleted(false, e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreManager", "Error fetching documents to delete: " + e.getMessage());
                    listener.onTransactionCompleted(false, e.getMessage());
                });
    }


    public interface OnDocumentIdRetrievedListener {
        void onDocumentIdRetrieved(String documentId);
    }

    public interface OnUpdateCompleteListener {
        void onUpdateComplete(boolean success);
    }

}


