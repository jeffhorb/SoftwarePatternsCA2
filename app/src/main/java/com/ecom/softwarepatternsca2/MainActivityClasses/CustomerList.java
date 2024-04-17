package com.ecom.softwarepatternsca2.MainActivityClasses;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ecom.softwarepatternsca2.AdapterClasses.CustomerListAdapter;
import com.ecom.softwarepatternsca2.ModelClasses.CustomerDetails;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerListAdapter adapter;

    SearchView searchView;

    ArrayList<CustomerDetails> customerDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        searchView = findViewById(R.id.searchView);
        customerDetails = new ArrayList<>();

        adapter = new CustomerListAdapter(customerDetails,this);
        recyclerView.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        fetchCustomersFromFirestore();
        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
    }

    private void filterData(String query) {
        ArrayList<CustomerDetails> filteredList = new ArrayList<>();

        for (CustomerDetails customer : customerDetails) {
            if (customer.getCustomerName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(customer);
            }
        }

        adapter.updateList(filteredList);
    }

    private void fetchCustomersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Handle case when no customers are fetched
                                Toast.makeText(CustomerList.this, "No customers found.", Toast.LENGTH_SHORT).show();
                                TextView noCus = findViewById(R.id.noCus);
                                noCus.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    CustomerDetails customer = document.toObject(CustomerDetails.class);
                                    String role = document.getString("role");
                                    assert role != null;
                                    if(!role.equals("admin")){
                                        customerDetails.add(customer);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(CustomerList.this, "Failed to fetch customers. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
