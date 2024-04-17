package com.ecom.softwarepatternsca2.Interfaces;


import android.view.MenuItem;

public interface NavigationHandler {
    boolean onNavigationItemSelected(MenuItem item);
    void onBackPressed();
    void openCustomerList();

   // void openCustomerTransaction();
}
