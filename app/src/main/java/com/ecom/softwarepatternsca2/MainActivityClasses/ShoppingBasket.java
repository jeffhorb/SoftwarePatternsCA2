package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecom.softwarepatternsca2.AdapterClasses.BasketListRvAdapter;
import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.ModelClasses.BasketList;
import com.ecom.softwarepatternsca2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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

    BasketListRvAdapter adapter;

    LinearLayout expandTextView, totalLayout;

    ArrayList<BasketList> basketLists;

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
        expandTextView = findViewById(R.id.expandImageView);
        totalLayout = findViewById(R.id.totalLinear);
        basketLists = new ArrayList<>();

        basketRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        adapter = new BasketListRvAdapter(basketLists,this);
        basketRecycler.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getCustomerDetails();

        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout customerDetails = findViewById(R.id.customerDetails);
                if (customerDetails.getVisibility() == View.GONE) {
                    customerDetails.setVisibility(View.VISIBLE);
                } else if (customerDetails.getVisibility() == View.VISIBLE)  {
                    customerDetails.setVisibility(View.GONE);

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
    // Method to calculate total amount based on customer ID
    private void calculateTotalAmount(String customerId) {
        AtomicReference<Double> total = new AtomicReference<>((double) 0);

        firestoreManager.firestore.collection("BasketList")
                .whereEqualTo("customerDocumentId", customerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BasketList basketItem = document.toObject(BasketList.class);
                        total.updateAndGet(v -> new Double((double) (v + basketItem.getTotalPrice())));
                    }
                    // Update totalAmount TextView with the calculated total
                    totalAmount.setText("€"+String.valueOf(total.get()));
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

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
                        exCusAddr1 = customerDocument.getString("customerAddressLine1");
                        exCusAddr2 = customerDocument.getString("customerAddressLine2");
                        exCusAddr3 = customerDocument.getString("customerAddressLine3");
                        exEir = customerDocument.getString("eircode");
                        exCusName = customerDocument.getString("customerName");
                        exCusEmail = customerDocument.getString("customerEmail");
                        fetchCustomersFromFirestore(customerDocId);
                        // Calculate total amount after fetching basket data
                        calculateTotalAmount(customerDocId);
                        // Listen for changes in the BasketList collection
                        firestoreManager.listenForBasketChanges(customerDocId, (value, error) -> {
                            if (error != null) {
                                // Handle error
                                return;
                            }
                            // Update the empty bag message visibility based on the basket items
                            assert value != null;
                            updateEmptyBagMessageVisibility(value);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void updateEmptyBagMessageVisibility(@NonNull QuerySnapshot snapshot) {
        if (snapshot.isEmpty()) {
            emptyBagMsg.setVisibility(View.VISIBLE);
            basketRecycler.setVisibility(View.GONE);
            expandTextView.setVisibility(View.GONE);
            totalLayout.setVisibility(View.GONE);
        } else {
            emptyBagMsg.setVisibility(View.GONE);
            basketRecycler.setVisibility(View.VISIBLE);
            expandTextView.setVisibility(View.VISIBLE);
            totalLayout.setVisibility(View.VISIBLE);
        }
    }

    private void fetchCustomersFromFirestore(String docId) {

        firestoreManager.firestore.collection("BasketList").whereEqualTo("customerDocumentId", docId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BasketList basketList = document.toObject(BasketList.class);

                                basketLists.add(basketList);

                            }

                            adapter.updateList(basketLists);
                            // Check if the transaction list is empty
                            if (!basketLists.isEmpty()) {
                                // No transactions found, show the "noTransactions" TextView
                                fullBasket.setVisibility(View.VISIBLE); // Hide the RecyclerView
                                emptyBagMsg.setVisibility(View.GONE);
                            }else {
                                fullBasket.setVisibility(View.GONE);
                                emptyBagMsg.setVisibility(View.VISIBLE);
                                basketRecycler.setVisibility(View.INVISIBLE);
                                expandTextView.setVisibility(View.GONE);
                                totalLayout.setVisibility(View.GONE);
                            }

                        }  // Handle errors

                    }
                });
    }


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
                            dial.dismiss();
                            customerName.setText("");
                            email.setText("");
                            addressLine1.setText("");
                            addressLine2.setText("");
                            addressLine3.setText("");
                            eircode.setText("");
                            emptyBagMsg.setVisibility(View.VISIBLE);
                            basketRecycler.setVisibility(View.INVISIBLE);
                            expandTextView.setVisibility(View.GONE);
                            totalLayout.setVisibility(View.GONE);
                            // Remove all items from Basket that belong to the customer with the customerDocId
                            firestoreManager.removeItems(customerDocId, new FirestoreManager.TransactionCompletionListener() {
                                @Override
                                public void onTransactionCompleted(boolean success, String errorMessage) {
                                    if (success) {
                                        // Purchase successful
                                        // You could save card details
                                        AlertDialog.Builder successDialogBuilder = new AlertDialog.Builder(ShoppingBasket.this);
                                        successDialogBuilder.setMessage("Purchase Successful.")
                                                .setTitle("Confirmation")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Close the dialog when the user clicks OK
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog successDialog = successDialogBuilder.create();
                                        successDialog.show();
                                    } else {
                                        // Error occurred while removing items
                                        // Handle the error
                                        Log.e("ShoppingBasket", "Error removing items from BasketList: " + errorMessage);
                                        // Show an error message to the user if necessary
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        dial.show();
    }
}