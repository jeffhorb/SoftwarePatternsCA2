package com.ecom.softwarepatternsca2.MainActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecom.softwarepatternsca2.AdapterClasses.CustomerListAdapter;
import com.ecom.softwarepatternsca2.AdapterClasses.TransactionHistoryAdapter;
import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.ModelClasses.CustomerDetails;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;
import com.ecom.softwarepatternsca2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CustomerTransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionHistoryAdapter adapter;

    ArrayList<TransactionDetails> transactionDetailsArrayList;

    TextView cusName,cusEmail,addr1,addr2,addr3,eircode;

    private final FirestoreManager firestoreManager = FirestoreManager.getInstance();

    LinearLayout cusLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_transaction_history);

        recyclerView = findViewById(R.id.recyclerView);
        cusName = findViewById(R.id.cusName);
        cusEmail = findViewById(R.id.email);
        addr1 = findViewById(R.id.addr1);
        addr2 = findViewById(R.id.addr2);
        addr3 = findViewById(R.id.addr3);
        eircode = findViewById(R.id.eir);
        cusLayout = findViewById(R.id.cusLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        transactionDetailsArrayList = new ArrayList<>();

        adapter = new TransactionHistoryAdapter(transactionDetailsArrayList,this);
        recyclerView.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if(getIntent().hasExtra("customerId")){
            String documentId = getIntent().getStringExtra("customerId");
            fetchCustomersFromFirestore(documentId);
            cusLayout.setVisibility(View.GONE);
        }

        if(getIntent().hasExtra("selectedCustomer")){

            CustomerDetails customerDetails = (CustomerDetails) getIntent().getSerializableExtra("selectedCustomer");
            assert customerDetails != null;
            cusName.setText(customerDetails.getCustomerName());
            cusEmail.setText(customerDetails.getCustomerEmail());
            addr1.setText(customerDetails.getCustomerAddressLine1());
            addr2.setText(customerDetails.getCustomerAddressLine2());
            addr3.setText(customerDetails.getCustomerAddressLine3());
            eircode.setText(customerDetails.getEircode());

            firestoreManager.getDocumentId("Customers", "customerId", customerDetails.getCustomerId(), documentId -> {
                if (documentId != null) {

                    fetchCustomersFromFirestore(documentId);
                }
            });

        }

    }

    private void fetchCustomersFromFirestore(String docId) {

        firestoreManager.firestore.collection("TransactionDetails").whereEqualTo("customerDocumentId", docId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TransactionDetails transactionDetails = document.toObject(TransactionDetails.class);

                                    transactionDetailsArrayList.add(transactionDetails);

                            }
                            adapter.updateList(transactionDetailsArrayList);
                            // Check if the transaction list is empty
                            if (transactionDetailsArrayList.isEmpty()) {
                                // No transactions found, show the "noTransactions" TextView
                                TextView noTrans = findViewById(R.id.noTransactions);
                                noTrans.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE); // Hide the RecyclerView
                            } else {
                                // Transactions found, show the RecyclerView
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }  // Handle errors

                    }
                });
    }
}