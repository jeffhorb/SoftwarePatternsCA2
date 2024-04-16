package com.ecom.softwarepatternsca2.ModelClasses;

public class TransactionDetails {

    private String  name;

    private String email;

    private String address1;
    private String address2;
    private  String address3;
    private String eircode;

    private String itemName;
    private  int quantity;
    private double totalPrice;
    private double discount;

    private String unitPrice;

    private String customerDocumentId;

    public TransactionDetails() {
    }

    public TransactionDetails(String name, String email, String address1, String address2, String address3,
                              String eircode, String itemName, int quantity, double totalPrice, double discount, String customerDocumentId,String unitPrice) {
        this.name = name;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.eircode = eircode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.customerDocumentId = customerDocumentId;
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getEircode() {
        return eircode;
    }

    public void setEircode(String eircode) {
        this.eircode = eircode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getCustomerDocumentId() {
        return customerDocumentId;
    }

    public void setCustomerDocumentId(String customerDocumentId) {
        this.customerDocumentId = customerDocumentId;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
