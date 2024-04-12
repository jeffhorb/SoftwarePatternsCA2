package com.ecom.softwarepatternsca2.Patterns;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

//FirebaseFirestoreFactory are responsible for creating instances of FirebaseFirestore, using the Factory Method Pattern.
public class FirebaseFirestoreFactory {
    @NonNull
    public static FirebaseFirestore createInstance() {
        return FirebaseFirestore.getInstance();
    }
}

