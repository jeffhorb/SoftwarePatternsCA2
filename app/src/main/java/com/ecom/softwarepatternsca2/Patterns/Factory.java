package com.ecom.softwarepatternsca2.Patterns;


import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.ModelClasses.BasketList;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;

import org.jetbrains.annotations.Contract;

public class Factory {
    @NonNull
    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    public Stock createStock(String category, String manufacturer, String itemName, String price, String quantity, String imageUrl) {
        return new Stock(category, manufacturer, itemName, price, quantity, imageUrl);
    }

    public TransactionDetails createTransaction(String size,  String itemName, int quantity, double totalPrice, double discount, String customerDocumentId,String unitPrice) {
        return new TransactionDetails ( size, itemName,  quantity,  totalPrice,  discount,  customerDocumentId,unitPrice);
    }

    public BasketList createBasket(String size, String itemName, int quantity, double totalPrice, double discount, String customerDocumentId, String unitPrice) {
        return new BasketList ( size, itemName,  quantity,  totalPrice,  discount,  customerDocumentId,unitPrice);
    }

}

