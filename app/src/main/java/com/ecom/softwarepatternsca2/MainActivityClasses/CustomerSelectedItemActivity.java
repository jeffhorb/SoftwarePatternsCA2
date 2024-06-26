package com.ecom.softwarepatternsca2.MainActivityClasses;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ecom.softwarepatternsca2.AppManagerClasses.BasketCounter;
import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.Interfaces.BasketCountListener;
import com.ecom.softwarepatternsca2.ModelClasses.BasketList;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;
import com.ecom.softwarepatternsca2.Patterns.Factory;
import com.ecom.softwarepatternsca2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomerSelectedItemActivity extends AppCompatActivity implements BasketCountListener {

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

    MaterialToolbar toolbar;

    LinearLayout basket;

    TextView basketCount;


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
        basketCount = findViewById(R.id.basketC);
        basket = findViewById(R.id.myBasket);

        getCustomerDetails();

        transactionDetailsFactory = new Factory();

        toolbar = findViewById(R.id.toolbar);
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

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerSelectedItemActivity.this, ShoppingBasket.class);
                startActivity(intent);
            }
        });

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

    // Method to update the basket count in real time
    public void updateBasket(int itemCount) {
        // Update the basketCount TextView with the new item count
        basketCount.setText(String.valueOf(itemCount));
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
                        BasketCounter.getInstance().getBasketItemCountForCustomer(customerDocId, this);
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

        // Store the transaction details in Firestore
        firestoreManager.addDocument("TransactionDetails", transactionDetails, new FirestoreManager.TransactionCompletionListener() {
            @Override
            public void onTransactionCompleted(boolean success, String errorMessage) {

            }
        });

        Map<String, Object> basketList = new HashMap<>();
        basketList.put("itemSize",size);
        basketList.put("itemName", itemName);
        basketList.put("quantity", quantity);
        basketList.put("totalPrice", totalPrice);
        basketList.put("discount", discount);
        basketList.put("customerDocumentId", customerDocId);
        basketList.put("unitPrice",price);

        ArrayList<BasketList > updateBasket = new ArrayList<>();

        BasketList basket = transactionDetailsFactory.createBasket( size,itemName,  quantity,  totalPrice,  discount,  customerDocId,price);
        updateBasket.add(basket);

        firestoreManager.addDocument("BasketList", basketList, new FirestoreManager.TransactionCompletionListener() {
            @Override
            public void onTransactionCompleted(boolean success, String errorMessage) {

            }
        });
    }

    @Override
    public void onBasketCountUpdated(int itemCount) {
        updateBasket(itemCount);

    }
}