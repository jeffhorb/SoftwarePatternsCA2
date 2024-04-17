package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;
import com.ecom.softwarepatternsca2.Patterns.Factory;
import com.ecom.softwarepatternsca2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ShoppingBasket extends AppCompatActivity {

    RecyclerView basketRecycler;
    TextInputEditText customerName, email, addressLine1, addressLine2, addressLine3, eircode;
    CheckBox useExistingDetails;

    Button makePayment;

    TextView emptyBagMsg,totalAmount;

    private final FirestoreManager firestoreManager = FirestoreManager.getInstance();

    String exCusName,exCusEmail,exCusAddr1,exCusAddr2,exCusAddr3,exEir;

    String customerDocId;
    ScrollView fullBasket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_basket);

        basketRecycler = findViewById(R.id.recyclerBasket);
        customerName = findViewById(R.id.customerName);
        email = findViewById(R.id.emailAddress);
        addressLine1 = findViewById(R.id.address1);
        addressLine2 = findViewById(R.id.address2);
        addressLine3 = findViewById(R.id.address3);
        eircode = findViewById(R.id.eircode);
        useExistingDetails = findViewById(R.id.useExistingDetails);
        makePayment = findViewById(R.id.makePayment);
        emptyBagMsg = findViewById(R.id.emptyBasket);
        totalAmount = findViewById(R.id.totalAmaout);
        fullBasket = findViewById(R.id.fullBasket);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getCustomerDetails();

        LinearLayout expandTextView = findViewById(R.id.expandImageView);

        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout customerDetails = findViewById(R.id.customerDetails);
                if (customerDetails.getVisibility() == View.GONE) {

                    customerDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = customerName.getText().toString();
                String eMail = email.getText().toString();
                String addr1 = addressLine1.getText().toString();
                String addr2 = addressLine2.getText().toString();
                String addr3 = addressLine3.getText().toString();
                String eir = eircode.getText().toString().trim();

                // Check if any field is empty
                boolean isAnyFieldEmpty = false;
                if (TextUtils.isEmpty(name)) {
                    customerName.setError("Please Enter your name");
                    isAnyFieldEmpty = true;
                }
                if (TextUtils.isEmpty(eMail)) {
                    email.setError("Please Enter your Email");
                    isAnyFieldEmpty = true;
                }

                if (!eMail.contains("@") || !eMail.contains(".") || eMail.length() <= 7) {
                    email.setError("Invalid Email");
                    isAnyFieldEmpty = true;
                }

                if (TextUtils.isEmpty(addr1)) {
                    addressLine1.setError("Please enter address line");
                    isAnyFieldEmpty = true;
                }
                if (TextUtils.isEmpty(addr2)) {
                    addressLine2.setError("Please enter address line 2");
                    isAnyFieldEmpty = true;
                }
                if (TextUtils.isEmpty(addr3)) {
                    addressLine3.setError("Please enter address line 3");
                    isAnyFieldEmpty = true;
                }
                if (TextUtils.isEmpty(eir)) {
                    eircode.setError("Please enter Eircode");
                    isAnyFieldEmpty = true;
                }

                if (!isAnyFieldEmpty) {
                    // All fields are filled, open dialog for card details
                    showPaymentConfirmationDialog();

                }
            }
        });

        // Listen for changes in the useExistingDetails checkbox
        useExistingDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEditTextFields(); // Call the method to update EditText fields based on the checkbox state
            }
        });

    }

    //needed to calculated the totalAmount
    private void getCustomerDetails() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String customerId = currentUser.getUid();
        // Query the Customers collection to retrieve the customer name
        firestoreManager.firestore.collection("Customers")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener(customerQuerySnapshot1 -> {
                    for (QueryDocumentSnapshot customerDocument : customerQuerySnapshot1) {
                        customerDocId = customerDocument.getId();
                    }
                });
    }

    //when item is rem

    private void setEditTextFields() {
        if (useExistingDetails.isChecked()) {
            customerName.setText("");
            email.setText("");
            addressLine1.setText("");
            addressLine2.setText("");
            addressLine3.setText("");
            eircode.setText("");
            // Use existing details
            customerName.setText(exCusName);
            email.setText(exCusEmail);
            addressLine1.setText(exCusAddr1);
            addressLine2.setText(exCusAddr2);
            addressLine3.setText(exCusAddr3);
            eircode.setText(exEir);
        } else {
            // Clear EditText fields
            customerName.setText("");
            email.setText("");
            addressLine1.setText("");
            addressLine2.setText("");
            addressLine3.setText("");
            eircode.setText("");
        }
    }

    private void showPaymentConfirmationDialog() {
        // Create and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingBasket.this);
        builder.setMessage("Proceed with payment?")
                .setTitle("Payment Confirmation")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Proceed with payment
                        openCardDetailsDialog();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel payment
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCardDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingBasket.this);
        builder.setTitle("Enter Card Details");
        // Inflate custom layout for card details input
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_card_details, null);
        EditText cardNumberEditText = dialogView.findViewById(R.id.cardNumberEditText);
        EditText cardUserNameEditText = dialogView.findViewById(R.id.cardUserNameEditText);
        EditText cardExpiryDateEditText = dialogView.findViewById(R.id.cardExpiryDateEditText);
        EditText cardPinEditText = dialogView.findViewById(R.id.cardPinEditText);
        Spinner cardTypeSpinner = dialogView.findViewById(R.id.cardTypeSpinner);
        // Set up spinner with card types (Debit, Credit, Master)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ShoppingBasket.this,
                R.array.card_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardTypeSpinner.setAdapter(adapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Confirm", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());// Set null OnClickListener initially
        AlertDialog dial = builder.create();
        dial.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dial).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle confirmation of card details
                        String cardNumber = cardNumberEditText.getText().toString();
                        String cardUserName = cardUserNameEditText.getText().toString();
                        String cardExpiryDate = cardExpiryDateEditText.getText().toString();
                        String cardPin = cardPinEditText.getText().toString();
                        String selectedCardType = cardTypeSpinner.getSelectedItem().toString();
                        // Validate card details
                        boolean isValid = true;
                        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
                            cardNumberEditText.setError("Enter a valid 16-digit card number");
                            isValid = false;
                        }
                        if (TextUtils.isEmpty(cardUserName)) {
                            cardUserNameEditText.setError("Enter the name on the card");
                            isValid = false;
                        }
                        if (TextUtils.isEmpty(cardExpiryDate) || !cardExpiryDate.matches("\\d{2}/\\d{2}")) {
                            cardExpiryDateEditText.setError("Enter a valid expiration date (MM/YY)");
                            isValid = false;
                        } else {
                            // Check if the card expiry date is not expired and is in a valid format
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.US);
                            sdf.setLenient(false); // Disable leniency to enforce strict date parsing
                            try {
                                Date expiryDate = sdf.parse(cardExpiryDate);
                                if (expiryDate == null || expiryDate.before(new Date())) {
                                    // Expiry date is either in the past or invalid
                                    cardExpiryDateEditText.setError("Enter a valid expiration date (MM/YY)");
                                    isValid = false;
                                }
                            } catch (ParseException e) {
                                // Date parsing error
                                cardExpiryDateEditText.setError("Enter a valid expiration date (MM/YY)");
                                isValid = false;
                            }
                        }
                        if (TextUtils.isEmpty(cardPin) || cardPin.length() != 3) {
                            cardPinEditText.setError("Enter a valid 3-digit PIN");
                            isValid = false;
                        }
                        if (selectedCardType.equals("Card Type")) {
                            Toast.makeText(ShoppingBasket.this, "Please Select Card Type", Toast.LENGTH_LONG).show();
                            isValid = false;
                        }
                        if (isValid) {

                            //you could save card details
                            AlertDialog.Builder successDialogBuilder = new AlertDialog.Builder(ShoppingBasket.this);
                            successDialogBuilder.setMessage("Purchase Successful.")
                                    .setTitle("Confirmation")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Close the dialog when the user clicks OK
                                            dialog.dismiss();
                                            dial.dismiss();
                                            customerName.setText("");
                                            email.setText("");
                                            addressLine1.setText("");
                                            addressLine2.setText("");
                                            addressLine3.setText("");
                                            eircode.setText("");
                                        }
                                    });
                            AlertDialog successDialog = successDialogBuilder.create();
                            successDialog.show();
                        }
                    }
                });
            }
        });
        dial.show();
    }
}