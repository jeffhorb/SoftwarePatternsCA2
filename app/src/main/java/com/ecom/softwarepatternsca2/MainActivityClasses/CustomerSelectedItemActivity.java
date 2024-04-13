package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CustomerSelectedItemActivity extends AppCompatActivity {

    TextView itemName, price, manufacturer, quantity, category;

    Button updateStock,simulatePurchase;

    ImageView itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_selected_item);
        itemName = findViewById(R.id.itemName);
        price = findViewById(R.id.price);
        manufacturer = findViewById(R.id.manufacturer);
        quantity = findViewById(R.id.quantity);
        itemImage = findViewById(R.id.itemImage);
        category = findViewById(R.id.category);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        if (getIntent().hasExtra("selectedItem")) {
            Stock stock = (Stock) getIntent().getSerializableExtra("selectedItem");

            assert stock != null;
            itemName.setText(stock.getItemName());
            price.setText(stock.getPrice());
            manufacturer.setText("Manufacturer:  " +stock.getManufacturer());
            category.setText("Category:"   +stock.getCategory());

            Picasso.get()
                    .load(stock.getImageUrl())
                    .fit()
                    .into(itemImage);
        }

    }
}