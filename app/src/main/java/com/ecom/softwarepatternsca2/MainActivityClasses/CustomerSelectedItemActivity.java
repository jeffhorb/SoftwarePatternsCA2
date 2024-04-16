package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;
import com.ecom.softwarepatternsca2.Patterns.Factory;
import com.ecom.softwarepatternsca2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CustomerSelectedItemActivity extends AppCompatActivity {

    TextView itemName, price, manufacturer, category;

    TextInputEditText customerName, email, addressLine1, addressLine2, addressLine3, eircode;

    private final FirestoreManager firestoreManager = FirestoreManager.getInstance();

    Button makePayment;

    ImageView itemImage;

    Spinner itemQty;

    CheckBox useExistingDetails;

    double totalPrice,moneySaved;

    private LinearLayout ratingReview;
    String itemN,itemId;

    private Factory transactionDetailsFactory;

    String customerDocId,exCusName,exCusEmail,exCusAddr1,exCusAddr2,exCusAddr3,exEir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_selected_item);
        itemName = findViewById(R.id.itemName);
        price = findViewById(R.id.price);
        manufacturer = findViewById(R.id.manufacturer);
        itemImage = findViewById(R.id.itemImage);
        category = findViewById(R.id.category);
        makePayment = findViewById(R.id.makePayment);
        customerName = findViewById(R.id.customerName);
        email = findViewById(R.id.emailAddress);
        addressLine1 = findViewById(R.id.address1);
        addressLine2 = findViewById(R.id.address2);
        addressLine3 = findViewById(R.id.address3);
        eircode = findViewById(R.id.eircode);
        itemQty = findViewById(R.id.selectQty);
        ratingReview = findViewById(R.id.rate);
        useExistingDetails = findViewById(R.id.useExistingDetails);

        transactionDetailsFactory = new Factory();

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
                if (customerDetails.getVisibility() == View.INVISIBLE) {

                    String qty = itemQty.getSelectedItem().toString();
                    if (qty.equals("0")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSelectedItemActivity.this);
                        builder.setMessage("Please select item quantity.")
                                .setTitle("Quantity Selection")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // You can add any action you want when the user clicks OK
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        customerDetails.setVisibility(View.VISIBLE);
                    }
                } else {
                    customerDetails.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (getIntent().hasExtra("selectedItem")) {
            Stock stock = (Stock) getIntent().getSerializableExtra("selectedItem");

            assert stock != null;
            itemN = stock.getItemName();

            itemName.setText(stock.getItemName());
            price.setText(stock.getPrice());
            manufacturer.setText("Manufacturer:  " + stock.getManufacturer());
            category.setText("Category:  " + stock.getCategory());

            Picasso.get()
                    .load(stock.getImageUrl())
                    .fit()
                    .into(itemImage);

            firestoreManager.getDocumentId("Stock", "itemName", stock.getItemName(), documentId -> {
                if (documentId != null) {
                    itemId = documentId;
                    ratingReview.setOnClickListener(v -> SelectedItemManager.showRatings(CustomerSelectedItemActivity.this, itemId));
                }
            });
        }


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

                String qty = itemQty.getSelectedItem().toString();
                if (qty.equals("0")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSelectedItemActivity.this);
                    builder.setMessage("Please select item quantity.")
                            .setTitle("Quantity Selection")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // You can add any action you want when the user clicks OK
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    isAnyFieldEmpty = true;
                }

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

    private void getCustomerDetails(){

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

                    }
                });

    }


    private void showPaymentConfirmationDialog() {
        // Calculate total price
        int quantity = Integer.parseInt(itemQty.getSelectedItem().toString());
        double itemPrice = Double.parseDouble(price.getText().toString().replaceAll("[^0-9.]", ""));
        totalPrice = itemPrice * quantity;

        // Initialize discount and money saved
        double discount = 0;
        moneySaved = 0;

        // Apply discount if quantity is greater than 3
        if (quantity >= 3) {
            discount = totalPrice * 0.15; // 15% discount
            totalPrice -= discount;
            moneySaved = discount;
        }

        // Build dialog message including money saved
        String dialogMessage = "Item: " + itemN +
                "\nPrice: €" + String.format("%.2f", itemPrice) +
                "\nQuantity: " + quantity +
                "\nTotal Price: €" + String.format("%.2f", totalPrice);

        // Include money saved in the dialog message
        if (moneySaved > 0) {
            dialogMessage += "\nDiscount: €" + String.format("%.2f", moneySaved);
        }

        dialogMessage += "\n\nProceed with payment?";

        // Create and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSelectedItemActivity.this);
        builder.setMessage(dialogMessage)
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

        private void storeTransactionDetails(String name, String email, String address1, String address2, String address3, String eircode,
                                         String itemName, int quantity, double totalPrice, double discount,String price) {
        // Create a map to store transaction details
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("name", name);
        transactionDetails.put("email", email);
        transactionDetails.put("address1", address1);
        transactionDetails.put("address2", address2);
        transactionDetails.put("address3", address3);
        transactionDetails.put("eircode", eircode);
        transactionDetails.put("itemName", itemName);
        transactionDetails.put("quantity", quantity);
        transactionDetails.put("totalPrice", totalPrice);
        transactionDetails.put("discount", discount);
        transactionDetails.put("customerDocumentId", customerDocId);
        transactionDetails.put("unitPrice",price);

        ArrayList<TransactionDetails> updatedData = new ArrayList<>();

        TransactionDetails transaction = transactionDetailsFactory.createTransaction( name,  email,  address1,  address2,  address3,
                eircode,  itemName,  quantity,  totalPrice,  discount,  customerDocId,price);
        updatedData.add(transaction);

        // Store the transaction details in Firestore
        firestoreManager.addDocument("TransactionDetails", transactionDetails, new FirestoreManager.TransactionCompletionListener() {
            @Override
            public void onTransactionCompleted(boolean success, String errorMessage) {

            }
        });
    }


    private void openCardDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSelectedItemActivity.this);
        builder.setTitle("Enter Card Details");
        // Inflate custom layout for card details input
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_card_details, null);
        EditText cardNumberEditText = dialogView.findViewById(R.id.cardNumberEditText);
        EditText cardUserNameEditText = dialogView.findViewById(R.id.cardUserNameEditText);
        EditText cardExpiryDateEditText = dialogView.findViewById(R.id.cardExpiryDateEditText);
        EditText cardPinEditText = dialogView.findViewById(R.id.cardPinEditText);
        Spinner cardTypeSpinner = dialogView.findViewById(R.id.cardTypeSpinner);
        // Set up spinner with card types (Debit, Credit, Master)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CustomerSelectedItemActivity.this,
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
                            Toast.makeText(CustomerSelectedItemActivity.this, "Please Select Card Type", Toast.LENGTH_LONG).show();
                            isValid = false;
                        }
                        if (isValid) {
                            // Process valid card details
                            String qty = itemQty.getSelectedItem().toString();
                            String name = customerName.getText().toString();
                            String addr1 = addressLine1.getText().toString();
                            String em = email.getText().toString();
                            String addr2 = addressLine2.getText().toString();
                            String addr3 = addressLine3.getText().toString();
                            String eir = eircode.getText().toString();

                            SelectedItemManager.simulatePurchase(CustomerSelectedItemActivity.this, itemName.getText().toString(), Integer.parseInt(qty));
                            // Call method to store details in customerTransaction Collection
                            storeTransactionDetails(name, em, addr1, addr2, addr3, eir, itemName.getText().toString(), Integer.parseInt(qty), totalPrice, moneySaved,
                                    price.getText().toString());

                            AlertDialog.Builder successDialogBuilder = new AlertDialog.Builder(CustomerSelectedItemActivity.this);
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