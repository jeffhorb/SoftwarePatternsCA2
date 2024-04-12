package com.ecom.softwarepatternsca2.AuthenticationClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.MainActivityClasses.HomePage;
import com.ecom.softwarepatternsca2.AppManagerClasses.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;


//LoginHandler handles the login process, promoting code reusability and efficiency.
public class LoginHandler {

    public static void handleLogin(@NonNull FirebaseAuth authenticate, String email, String password, ProgressBar pBar, CheckBox checkBoxRememberMe, Context context) {
        authenticate.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    pBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        if (checkBoxRememberMe.isChecked())
                            saveLoginDetails(email, password, context);
                        Intent intent = new Intent(context, HomePage.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        Toast.makeText(context, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void saveLoginDetails(String email, String password, Context context) {
        new SharedPrefManager(context).saveLoginDetails(email, password);
    }
}

