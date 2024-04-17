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

                SelectedItemManager.updateQuantityInFirestore(AdminSelectedItemActivity.this, itemName.getText().toString(), Integer.parseInt(newQuantity));
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
}
