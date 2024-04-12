package com.ecom.softwarepatternsca2.Patterns;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ecom.softwarepatternsca2.Interfaces.UserDetailsDialogListener;
import com.ecom.softwarepatternsca2.R;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.Contract;


//AlertDialogBuilder encapsulates the creation of AlertDialog instances using the Builder Pattern, enhancing expressiveness and modularity.
public class AlertDialogBuilder {
    private Context context;

    private AlertDialogBuilder(Context context) {
        this.context = context;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static AlertDialogBuilder createInstance(Context context) {
        return new AlertDialogBuilder(context);
    }

    public AlertDialog buildUserDetailsDialog(String email, String password, UserDetailsDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_user_details, null);
        builder.setView(dialogView);

        TextInputEditText customerName = dialogView.findViewById(R.id.customerName);
        TextInputEditText addressLine1 = dialogView.findViewById(R.id.address1);
        TextInputEditText addressLine2 = dialogView.findViewById(R.id.address2);
        TextInputEditText addressLine3 = dialogView.findViewById(R.id.address3);
        TextInputEditText eircode = dialogView.findViewById(R.id.eircode);

        builder.setPositiveButton("Sign Up", null); // Do not provide click listener yet

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String name = customerName.getText().toString();
                String addr1 = addressLine1.getText().toString();
                String addr2 = addressLine2.getText().toString();
                String addr3 = addressLine3.getText().toString();
                String eir = eircode.getText().toString().trim();

                boolean isValid = true;
                if (TextUtils.isEmpty(name)) {
                    customerName.setError("Please Enter your name");
                    isValid = false;
                }
                if (TextUtils.isEmpty(addr1)) {
                    addressLine1.setError("Please enter address line");
                    isValid = false;
                }
                if (TextUtils.isEmpty(addr2)) {
                    addressLine2.setError("Please enter address line 2");
                    isValid = false;
                }
                if (TextUtils.isEmpty(addr3)) {
                    addressLine3.setError("Please enter address line 3");
                    isValid = false;
                }
                if (TextUtils.isEmpty(eir)) {
                    eircode.setError("Please enter Eircode");
                    isValid = false;
                }

                if (isValid) {
                    // All fields are valid, proceed with sign up
                    listener.onSignUp(email, password, name, addr1, addr2, addr3, eir);
                    dialog.dismiss(); // Dismiss dialog only if all fields are valid
                }
            });
        });

        dialog.show();
        return dialog;
    }
}



