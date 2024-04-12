package com.ecom.softwarepatternsca2.Patterns;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;


//ForgotPasswordDialogBuilder encapsulates the creation of the "Forgot Password" dialog using the Builder Pattern, enhancing expressiveness and modularity.
public class ForgotPasswordDialogBuilder {
    public static void showRecoverPasswordDialog(Context context, FirebaseAuth auth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Email");
        LinearLayout linearLayout = new LinearLayout(context);
        final EditText emailet = new EditText(context);

        // Set up the email input field
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        // Clicking on Recover sends a password reset email
        builder.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailet.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    emailet.setError("Enter Email");
                } else {
                    sendPasswordResetEmail(auth, email, context);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private static void sendPasswordResetEmail(@NonNull FirebaseAuth auth, String email, Context context) {
        ProgressDialog loadingBar = new ProgressDialog(context);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            loadingBar.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(context, "Email sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            loadingBar.dismiss();
            Toast.makeText(context, "Error Failed", Toast.LENGTH_LONG).show();
        });
    }
}

