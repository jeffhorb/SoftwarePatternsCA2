package com.ecom.softwarepatternsca2.ModelClasses;


import java.io.Serializable;

public class CustomerDetails implements Serializable {
    private String customerName;
    private String customerEmail;
    private String customerId;
    private String customerAddressLine1;

    private String customerAddressLine2;
    private String CustomerAddressLine3;

    private String eircode;
    private String role;

    // Default constructor (needed for Firestore deserialization)
    public CustomerDetails() {
    }

    public CustomerDetails(String customerName, String customerEmail, String customerId, String customerAddressLine1,String customerAddressLine2,
                           String customerAddressLine3, String eircode, String role) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerId = customerId;
        this.customerAddressLine1 = customerAddressLine1;
        this.customerAddressLine2 = customerAddressLine2;
        this.CustomerAddressLine3 = customerAddressLine3;
        this.eircode = eircode;
        this.role = role;
    }

    // Getters and setters

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public String getCustomerAddressLine1() {
        return customerAddressLine1;
    }

    public void setCustomerAddressLine1(String customerAddressLine1) {
        this.customerAddressLine1 = customerAddressLine1;
    }

    public String getCustomerAddressLine2() {
        return customerAddressLine2;
    }

    public void setCustomerAddressLine2(String customerAddressLine2) {
        this.customerAddressLine2 = customerAddressLine2;
    }

    public String getCustomerAddressLine3() {
        return CustomerAddressLine3;
    }

    public void setCustomerAddressLine3(String customerAddressLine3) {
        CustomerAddressLine3 = customerAddressLine3;
    }

    public String getEircode() {
        return eircode;
    }

    public void setEircode(String eircode) {
        this.eircode = eircode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

