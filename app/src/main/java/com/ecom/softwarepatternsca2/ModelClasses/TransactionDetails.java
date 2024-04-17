package com.ecom.softwarepatternsca2.ModelClasses;

public class TransactionDetails {

    private String  itemSize;


    private String itemName;
    private  int quantity;
    private double totalPrice;
    private double discount;

    private String unitPrice;

    private String customerDocumentId;

    public TransactionDetails() {
    }

    public TransactionDetails(String itemSize, String itemName, int quantity, double totalPrice, double discount, String customerDocumentId,String unitPrice) {

        this.itemSize = itemSize;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.customerDocumentId = customerDocumentId;
        this.unitPrice = unitPrice;
    }


    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
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
