package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.ModelClasses.BasketList;
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


    private final FirestoreManager firestoreManager = FirestoreManager.getInstance();

    Button addToBasket;

    ImageView itemImage;

    Spinner itemQty,itemSize;

    private Factory transactionDetailsFactory;


    private LinearLayout ratingReview;
    String itemN,itemId;
    double totalPrice,moneySaved;

    String customerDocId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_selected_item);
        itemName = findViewById(R.id.itemName);
        price = findViewById(R.id.price);
        manufacturer = findViewById(R.id.manufacturer);
        itemImage = findViewById(R.id.itemImage);
        category = findViewById(R.id.category);
        addToBasket = findViewById(R.id.addToBasket);
        itemQty = findViewById(R.id.selectQty);
        itemSize = findViewById(R.id.selectSize);
        ratingReview = findViewById(R.id.rate);

        getCustomerDetails();

        transactionDetailsFactory = new Factory();


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


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

        addToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isAnyFieldEmpty = false;
                String qty = itemQty.getSelectedItem().toString();
                String size = itemSize.getSelectedItem().toString();

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

                if (size.equals("Select Size")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSelectedItemActivity.this);
                    builder.setMessage("Please select item size.")
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
                if(!isAnyFieldEmpty){

                    showConfirmationDialog();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.basket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.basket) {
            Intent intent = new Intent(CustomerSelectedItemActivity.this, ShoppingBasket.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    }
                });
    }

    private void showConfirmationDialog() {
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
                .setTitle("Confirmation")
                .setPositiveButton("Add to Basket", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String qty = itemQty.getSelectedItem().toString();

                        SelectedItemManager.simulatePurchase(CustomerSelectedItemActivity.this, itemName.getText().toString(), Integer.parseInt(qty));
                        // Call method to store details in customerTransaction Collection
                        storeTransactionDetails(itemSize.getSelectedItem().toString(), itemName.getText().toString(), Integer.parseInt(qty), totalPrice, moneySaved,
                                price.getText().toString());
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

    private void storeTransactionDetails(String size, String itemName, int quantity, double totalPrice, double discount,String price) {
        // Create a map to store transaction details
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("itemSize",size);
        transactionDetails.put("itemName", itemName);
        transactionDetails.put("quantity", quantity);
        transactionDetails.put("totalPrice", totalPrice);
        transactionDetails.put("discount", discount);
        transactionDetails.put("customerDocumentId", customerDocId);
        transactionDetails.put("unitPrice",price);



        ArrayList<TransactionDetails> updatedData = new ArrayList<>();

        TransactionDetails transaction = transactionDetailsFactory.createTransaction( size,itemName,  quantity,  totalPrice,  discount,  customerDocId,price);
        updatedData.add(transaction);

        Map<String, Object> BasketList = new HashMap<>();
        BasketList.put("itemSize",size);
        BasketList.put("itemName", itemName);
        BasketList.put("quantity", quantity);
        BasketList.put("totalPrice", totalPrice);
        BasketList.put("discount", discount);
        BasketList.put("customerDocumentId", customerDocId);
        BasketList.put("unitPrice",price);



        ArrayList<BasketList > updateBasket = new ArrayList<>();

        BasketList basketList = transactionDetailsFactory.createBasket( size,itemName,  quantity,  totalPrice,  discount,  customerDocId,price);
        updateBasket.add(basketList);

        // Store the transaction details in Firestore
        firestoreManager.addDocument("TransactionDetails", transactionDetails, new FirestoreManager.TransactionCompletionListener() {
            @Override
            public void onTransactionCompleted(boolean success, String errorMessage) {

            }
        });
    }
}