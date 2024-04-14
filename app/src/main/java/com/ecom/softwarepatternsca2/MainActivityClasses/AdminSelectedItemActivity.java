package com.ecom.softwarepatternsca2.MainActivityClasses;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.Patterns.AlertDialogBuilder;
import com.ecom.softwarepatternsca2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminSelectedItemActivity extends AppCompatActivity {

    private TextView itemName, price, manufacturer, quantity, category;
    private Button updateStock, simulatePurchase;
    private ImageView itemImage;
    private LinearLayout ratingReview;
    private String itemId;

    // Use the singleton instance of FirestoreManager
    private final FirestoreManager firestoreManager = FirestoreManager.getInstance();
    private final AlertDialogBuilder alertDialogBuilder = AlertDialogBuilder.createInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_selected_item);

        // Initialize views
        initViews();

        // Setup toolbar
        setupToolbar();

        // Get selected item details
        getSelectedItemDetails();

        // Set listeners
        setListeners();
    }

    private void initViews() {
        itemName = findViewById(R.id.itemName);
        price = findViewById(R.id.price);
        manufacturer = findViewById(R.id.manufacturer);
        quantity = findViewById(R.id.quantity);
        itemImage = findViewById(R.id.itemImage);
        category = findViewById(R.id.category);
        updateStock = findViewById(R.id.UpdateStock);
        simulatePurchase = findViewById(R.id.simulatePurchase);
        ratingReview = findViewById(R.id.rate);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getSelectedItemDetails() {
        if (getIntent().hasExtra("selectedItem")) {
            Stock stock = (Stock) getIntent().getSerializableExtra("selectedItem");

            assert stock != null;
            itemName.setText(stock.getItemName());
            price.setText(stock.getPrice());
            quantity.setText("Remaining Qty:  "+ stock.getQuantity() + " Units");
            manufacturer.setText("Manufacturer:  " + stock.getManufacturer());
            category.setText("Category:  " + stock.getCategory());

            Picasso.get()
                    .load(stock.getImageUrl())
                    .fit()
                    .into(itemImage);

            firestoreManager.getDocumentId("Stock", "itemName", stock.getItemName(), documentId -> {
                if (documentId != null) {
                    itemId = documentId;
                    listenForQuantityUpdates();
                }
            });
        }
    }

    private void setListeners() {
        updateStock.setOnClickListener(v -> showUpdateDialog());
        simulatePurchase.setOnClickListener(v -> showSimulatePurchaseDialog());
        ratingReview.setOnClickListener(v -> SelectedItemManager.showRatings(AdminSelectedItemActivity.this, itemId));
    }

    private void listenForQuantityUpdates() {
        firestoreManager.listenForQuantityUpdates(itemId, new FirestoreManager.OnQuantityUpdateListener() {
            @Override
            public void onQuantityUpdate(String updatedQuantity) {
                quantity.setText("Remaining Qty: " + updatedQuantity + " Units");
            }
        });
    }

    private void showUpdateDialog() {
        alertDialogBuilder.showUpdateDialog(this, new AlertDialogBuilder.OnUpdateClickListener() {
            @Override
            public void onUpdate(String newQuantity) {
                updateQuantityInFirestore(newQuantity);
            }
        });
    }

    private void showSimulatePurchaseDialog() {
        alertDialogBuilder.showSimulatePurchaseDialog(this, new AlertDialogBuilder.OnPurchaseClickListener() {
            @Override
            public void onPurchase(String purchaseQuantity) {
                SelectedItemManager.simulatePurchase(AdminSelectedItemActivity.this, itemName.getText().toString(), Integer.parseInt(purchaseQuantity));
            }
        });
    }

    private void updateQuantityInFirestore(String newQuantity) {
        // Retrieve the existing quantity from Firestore
                firestoreManager.getDocumentId("Stock", "itemName", itemName.getText().toString(), new FirestoreManager.OnDocumentIdRetrievedListener() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {
                if (documentId != null) {
                    firestoreManager.firestore.collection("Stock").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Get the existing quantity from Firestore
                                    String existingQuantityString = document.getString("quantity");
                                    if (existingQuantityString != null) {
                                        try {
                                            // Convert existing and new quantities to integers
                                            int existingQuantity = Integer.parseInt(existingQuantityString);
                                            int newQuantityInt = Integer.parseInt(newQuantity);

                                            // Calculate the updated quantity
                                            int updatedQuantity = existingQuantity + newQuantityInt;

                                            // Update the quantity in Firestore
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("quantity", String.valueOf(updatedQuantity));

                                            firestoreManager.updateDocument("Stock", documentId, data, new FirestoreManager.OnUpdateCompleteListener() {
                                                @Override
                                                public void onUpdateComplete(boolean success) {
                                                    if (success) {
                                                        // Update UI to reflect the new quantity
                                                        quantity.setText("Updated qty:  "+updatedQuantity + " Units");
                                                        Toast.makeText(AdminSelectedItemActivity.this, "Stock quantity updated successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(AdminSelectedItemActivity.this, "Failed to update stock quantity", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } catch (NumberFormatException e) {
                                            Toast.makeText(AdminSelectedItemActivity.this, "Invalid quantity format", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(AdminSelectedItemActivity.this, "Existing quantity not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(AdminSelectedItemActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AdminSelectedItemActivity.this, "Failed to get document: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AdminSelectedItemActivity.this, "Document ID not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
