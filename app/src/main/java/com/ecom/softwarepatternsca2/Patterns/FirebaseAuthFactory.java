package com.ecom.softwarepatternsca2.Patterns;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

//FirebaseAuthFactory responsible for creating instances of FirebaseAuth, using the Factory Method Pattern.
public class FirebaseAuthFactory {
    @NonNull
    public static FirebaseAuth createInstance() {
        return FirebaseAuth.getInstance();
    }
}
