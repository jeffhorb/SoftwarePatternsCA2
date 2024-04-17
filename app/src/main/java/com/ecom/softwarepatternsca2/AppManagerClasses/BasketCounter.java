package com.ecom.softwarepatternsca2.AppManagerClasses;

import android.util.Log;

import com.ecom.softwarepatternsca2.Interfaces.BasketCountListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class BasketCounter {

    private static final String COLLECTION_PATH = "BasketList"; // Replace with your actual collection path
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static BasketCounter instance;

    private BasketCounter() {
        // Private constructor to prevent direct instantiation
    }

    public static BasketCounter getInstance() {
        if (instance == null) {
            instance = new BasketCounter();
        }
        return instance;
    }

    // Method to get the count of items in the basket for a specific customer
    public void getBasketItemCountForCustomer(String customerDocId, BasketCountListener listener) {
        firestore.collection(COLLECTION_PATH)
                .whereEqualTo("customerDocumentId", customerDocId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Basket", "Listen failed.", error);
                        return;
                    }

                    int itemCount = value != null ? value.size() : 0;
                    listener.onBasketCountUpdated(itemCount);
                });
    }
}
