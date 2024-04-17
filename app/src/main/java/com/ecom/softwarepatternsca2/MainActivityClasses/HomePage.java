package com.ecom.softwarepatternsca2.MainActivityClasses;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.softwarepatternsca2.AdapterClasses.StockAdapterClass;
import com.ecom.softwarepatternsca2.AppManagerClasses.AdminChecker;
import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.AppManagerClasses.SharedPrefManager;
import com.ecom.softwarepatternsca2.AuthenticationClasses.LoginActivity;
import com.ecom.softwarepatternsca2.Interfaces.AdminCheckCallback;
import com.ecom.softwarepatternsca2.Interfaces.NavigationHandler;
import com.ecom.softwarepatternsca2.Interfaces.StockInteractionListener;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.Patterns.FirestoreDataObserver;
import com.ecom.softwarepatternsca2.Patterns.Factory;
import com.ecom.softwarepatternsca2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity implements  NavigationHandler, StockInteractionListener {

    private DrawerLayout drawerLayout;
    private StockAdapterClass recyclerAdapter;
    private ArrayList<Stock> stockArrayList;
    private FirebaseFirestore db;

    private SharedPrefManager sharedPrefManager;
    private SearchView searchView;
    String itemId;

    // Declare a boolean variable to track whether the "Manage Account" option is clicked
    private boolean manageAccountClicked;

    private FirestoreDataObserver dataObserver;
    private Factory stockFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initializeViews();
        setupSearchView();
        setupCategorySpinner();
        Sort();

        getAllItems();

        setUserDetailsInNavHeader();
    }

    //bottom nav to open project
    public void customerList(MenuItem menuitem) {
        Intent intent = new Intent(HomePage.this, CustomerList.class);
        startActivity(intent);
    }

    private void initializeViews() {
        db = FirebaseFirestore.getInstance();
        stockArrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        manageAccountClicked = false;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new StockAdapterClass(stockArrayList, this,this );
        recyclerView.setAdapter(recyclerAdapter);

        NavigationView navigationView = findViewById(R.id.nav_view);



        // Set the item click listener for NavigationView
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        navigationView.bringToFront();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        sharedPrefManager = new SharedPrefManager(this);
        dataObserver = new FirestoreDataObserver(stockArrayList, recyclerAdapter);
        stockFactory = new Factory();

        // Get a reference to the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminChecker.checkIfAdmin(new AdminCheckCallback() {
            @Override
            public void onResult(boolean isAdmin) {
                if (isAdmin) {
                    // Set the visibility to VISIBLE
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    // Set the visibility to VISIBLE
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        });
    }

    //ADD A SORT BASED ON PRICE lowest to highest - highest to lowest

    @Override
    public void onItemClicked(Stock stock) {
    }

    @Override
    public void onRateIconClicked(Stock stock) {
        // Call the showRatingDialog method of the ItemClickStrategy
        showRatingDialog(stock);
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
        ArrayList<Stock> filteredList = new ArrayList<>();

        for (Stock item : stockArrayList) {
            if (item.getItemName().toLowerCase().contains(query.toLowerCase()) || item.getManufacturer().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        recyclerAdapter.updateList(filteredList);
    }

    private void setupCategorySpinner() {
        Spinner category = findViewById(R.id.category);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                if (selectedCategory.equals("All Items")) {
                    getAllItems();
                } else {
                    getItemsByCategory(selectedCategory);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void Sort() {
        Spinner sort = findViewById(R.id.sort);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                if (selectedCategory.equals("Sort Products")) {
                    getAllItems();
                } else if (selectedCategory.equals("Highest to Lowest Price")) {
                    sortByPriceDescending();
                } else if (selectedCategory.equals("Lowest to Highest Price")) {
                    sortByPriceAscending();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void sortByPriceDescending() {
        stockArrayList.sort((stock1, stock2) -> {
            double price1 = extractPrice(stock1.getPrice());
            double price2 = extractPrice(stock2.getPrice());
            return Double.compare(price2, price1);
        });
        recyclerAdapter.updateList(stockArrayList);
    }

    private void sortByPriceAscending() {
        stockArrayList.sort((stock1, stock2) -> {
            double price1 = extractPrice(stock1.getPrice());
            double price2 = extractPrice(stock2.getPrice());
            return Double.compare(price1, price2);
        });
        recyclerAdapter.updateList(stockArrayList);
    }

    private double extractPrice(@NonNull String priceWithCurrency) {
        // removing the currency symbol
        String priceWithoutCurrency = priceWithCurrency.substring(1);
        return Double.parseDouble(priceWithoutCurrency);
    }
    @Override
    public void openCustomerList() {
        Intent intent = new Intent(HomePage.this, CustomerList.class);
        startActivity(intent);
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
            Intent intent = new Intent(HomePage.this, ShoppingBasket.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getAllItems() {
        db.collection("Stock").addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error getting data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                ArrayList<Stock> updatedData = new ArrayList<>();
                for (DocumentSnapshot document : value) {
                    String category = document.getString("category");
                    String manufacturer = document.getString("manufacturer");
                    String itemName = document.getString("itemName");
                    String price = document.getString("price");
                    String quantity = document.getString("quantity");
                    String imageUrl = document.getString("imageUrl");

                    itemId = document.getId();

                    Stock stock = stockFactory.createStock(category, manufacturer, itemName, price, quantity, imageUrl);
                    updatedData.add(stock);
                }
                dataObserver.onDataUpdated(updatedData);
            }
        });
    }

    private void getItemsByCategory(String category) {
        db.collection("Stock").whereEqualTo("category", category).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error getting data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                ArrayList<Stock> updatedData = new ArrayList<>();
                for (DocumentSnapshot document : value) {
                    String manufacturer = document.getString("manufacturer");
                    String itemName = document.getString("itemName");
                    String price = document.getString("price");
                    String quantity = document.getString("quantity");
                    String imageUrl = document.getString("imageUrl");

                    Stock stock = stockFactory.createStock(category, manufacturer, itemName, price, quantity, imageUrl);
                    updatedData.add(stock);
                }
                dataObserver.onDataUpdated(updatedData);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_logout) {
            sharedPrefManager.clearSession();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(HomePage.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else if (itemId == R.id.nav_manage) {
            // Declare a boolean variable to track whether the "Manage Account" option is clicked
            manageAccountClicked = true;
            // Update the user details in the navigation header
            setUserDetailsInNavHeader();
        }else if (itemId == R.id.viewTransaction){


        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to set user details in the navigation header
    private void setUserDetailsInNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        // Find TextViews in the header layout
        TextView cusName = headerView.findViewById(R.id.cusName);
        TextView cusEmail = headerView.findViewById(R.id.cusEmail);
        TextView addrLine1 = headerView.findViewById(R.id.addrLine1);
        TextView addrLine2 = headerView.findViewById(R.id.addrLine2);
        TextView addrLine3 = headerView.findViewById(R.id.addrLine3);
        TextView eirCodeLine = headerView.findViewById(R.id.eirCodeLine);

        // Get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Define onCompleteListener
            OnCompleteListener<QuerySnapshot> onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Customer document exists, retrieve its details
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String userName = document.getString("customerName");
                            String userEmail = document.getString("customerEmail");
                            String userAddrLine1 = document.getString("customerAddressLine1");
                            String userAddrLine2 = document.getString("customerAddressLine2");
                            String userAddrLine3 = document.getString("customerAddressLine3");
                            String userEirCode = document.getString("eirCode");

                            // Set user details in the TextViews
                            cusName.setText(userName);
                            cusEmail.setText(userEmail);
                            addrLine1.setText(userAddrLine1);
                            addrLine2.setText(userAddrLine2);
                            addrLine3.setText(userAddrLine3);
                            eirCodeLine.setText(userEirCode);

                            if (manageAccountClicked) {
                                // If "Manage Account" is clicked, show the update account dialog
                                showUpdateAccountDialog(document.getId());
                                // Reset manageAccountClicked back to false
                                manageAccountClicked = false;
                            }
                        } else {
                            // Customer document does not exist
                            Log.d("HomePage", "No such document");
                        }
                    } else {
                        // Error occurred while retrieving customer document
                        Log.d("HomePage", "Error getting customer document", task.getException());
                    }
                }
            };

            // Call getCustomerDocumentId with onCompleteListener
            getCustomerDocumentId(userId, onCompleteListener);
        } else {
            // Current user is null
            Log.d("HomePage", "No user signed in");
        }
    }

    private void showUpdateAccountDialog(String customerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_account_dialog, null);
        builder.setView(dialogView);

        // Find EditTexts in the dialog layout
        EditText newNameEditText = dialogView.findViewById(R.id.newNameEditText);
        EditText newEmailEditText = dialogView.findViewById(R.id.newEmailEditText);
        EditText newAddressLine1EditText = dialogView.findViewById(R.id.newAddressLine1EditText);
        EditText newAddressLine2EditText = dialogView.findViewById(R.id.newAddressLine2EditText);
        EditText newAddressLine3EditText = dialogView.findViewById(R.id.newAddressLine3EditText);
        EditText newEirCodeEditText = dialogView.findViewById(R.id.newEirCodeEditText);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            getCustomerDocumentId(userId, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Customer document exists, retrieve its details
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String userName = document.getString("customerName");
                            String userEmail = document.getString("customerEmail");
                            String userAddrLine1 = document.getString("customerAddressLine1");
                            String userAddrLine2 = document.getString("customerAddressLine2");
                            String userAddrLine3 = document.getString("customerAddressLine3");
                            String userEirCode = document.getString("eirCode");

                            // Set current details in EditText fields
                            newNameEditText.setText(userName);
                            newEmailEditText.setText(userEmail);
                            newAddressLine1EditText.setText(userAddrLine1);
                            newAddressLine2EditText.setText(userAddrLine2);
                            newAddressLine3EditText.setText(userAddrLine3);
                            newEirCodeEditText.setText(userEirCode);
                        } else {
                            // Document does not exist
                            Log.d("HomePage", "No such document");
                        }
                    } else {
                        // Error getting document
                        Log.d("HomePage", "Error getting document", task.getException());
                    }
                }
            });
        }

        FirestoreManager firestoreManager = new FirestoreManager();

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update user's details in Firestore
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("customerName", newNameEditText.getText().toString());
                updatedData.put("customerEmail", newEmailEditText.getText().toString());
                updatedData.put("customerAddressLine1", newAddressLine1EditText.getText().toString());
                updatedData.put("customerAddressLine2", newAddressLine2EditText.getText().toString());
                updatedData.put("customerAddressLine3", newAddressLine3EditText.getText().toString());
                updatedData.put("eirCode", newEirCodeEditText.getText().toString());

                firestoreManager.updateDocument("Customers", customerId, updatedData, new FirestoreManager.OnUpdateCompleteListener() {
                    @Override
                    public void onUpdateComplete(boolean success) {
                        if (success) {
                            Toast.makeText(HomePage.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomePage.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getCustomerDocumentId(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference customersRef = db.collection("Customers");

        customersRef.whereEqualTo("customerId", userId)
                .limit(1)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }


    private void showRatingDialog(Stock stock) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate this item");

        // Set up the dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.rating_dialog, null);
        builder.setView(view);

        EditText commentEditText = view.findViewById(R.id.commentEditText);
        android.widget.RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float rating = ratingBar.getRating();
                String comment = commentEditText.getText().toString();

                if (rating > 0 || !comment.isEmpty()) { // Check if either rating or comment is provided
                    // Create a new document in the Ratings collection
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference ratingRef = db.collection("Ratings").document();

                    // Create a map to store the rating data
                    Map<String, Object> ratingData = new HashMap<>();
                    ratingData.put("rating", rating);
                    ratingData.put("comment", comment);

                    // Add the rating document to the Ratings collection
                    ratingRef.set(ratingData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    // Rating document added successfully, now establish relationship with customer
                                    String ratingId = ratingRef.getId();
                                    assert currentUser != null;
                                    String customerId = currentUser.getUid();

                                    // Create a new document in the CustomerRatings collection
                                    DocumentReference customerRatingRef = db.collection("CustomerRatings").document();
                                    Map<String, Object> customerRatingData = new HashMap<>();
                                    customerRatingData.put("customerId", customerId);
                                    customerRatingData.put("ratingId", ratingId);

                                    // Add the customer rating document to the CustomerRatings collection
                                    customerRatingRef.set(customerRatingData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Customer rating document added successfully

                                                    // Now, establish relationship with item in ItemRatings collection
                                                    DocumentReference itemRatingRef = db.collection("ItemRatings").document();
                                                    Map<String, Object> itemRatingData = new HashMap<>();
                                                    itemRatingData.put("itemId", itemId);
                                                    itemRatingData.put("ratingId", ratingId);

                                                    // Add the item rating document to the ItemRatings collection
                                                    itemRatingRef.set(itemRatingData)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Item rating document added successfully
                                                                    Toast.makeText(HomePage.this, "Thanks for your Time", Toast.LENGTH_LONG).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Handle any errors
                                                                    Toast.makeText(HomePage.this, "An error occurred", Toast.LENGTH_LONG).show();
                                                                    Log.e("error", "Error ItemRatings collection", e);
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle any errors
                                                    Toast.makeText(HomePage.this, "An error occurred", Toast.LENGTH_LONG).show();
                                                    Log.e("error", "Error CustomerRatings collection", e);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors
                                    Toast.makeText(HomePage.this, "An error occurred", Toast.LENGTH_LONG).show();
                                    Log.e("error", "Error Ratings collection", e);
                                }
                            });
                } else {
                    // Display a message indicating that either a rating or a comment is required
                    Toast.makeText(HomePage.this, "Please provide a rating or a comment", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

}
