package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ecom.softwarepatternsca2.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

public class CustomerTransactionHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_transaction_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}