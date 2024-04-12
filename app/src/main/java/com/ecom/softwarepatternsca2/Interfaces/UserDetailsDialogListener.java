package com.ecom.softwarepatternsca2.Interfaces;

public interface UserDetailsDialogListener {
    void onSignUp(String email, String password, String customerName, String address1, String address2, String address3, String eircode);
}
