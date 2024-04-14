package com.ecom.softwarepatternsca2.Patterns;


import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;

import org.jetbrains.annotations.Contract;

public class Factory {
    @NonNull
    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    public Stock createStock(String category, String manufacturer, String itemName, String price, String quantity, String imageUrl) {
        return new Stock(category, manufacturer, itemName, price, quantity, imageUrl);
    }

    public TransactionDetails createTransaction(String name, String email, String address1, String address2, String address3, String eircode, String itemName, int quantity, double totalPrice, double discount, String customerDocumentId) {

       return new TransactionDetails ( name,  email,  address1,  address2,  address3,
                 eircode,  itemName,  quantity,  totalPrice,  discount,  customerDocumentId);
    }


}

