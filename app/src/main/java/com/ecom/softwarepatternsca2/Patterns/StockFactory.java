package com.ecom.softwarepatternsca2.Patterns;


import androidx.annotation.NonNull;

import com.ecom.softwarepatternsca2.ModelClasses.Stock;

import org.jetbrains.annotations.Contract;

public class StockFactory {
    @NonNull
    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    public Stock createStock(String category, String manufacturer, String itemName, String price, String quantity, String imageUrl) {
        return new Stock(category, manufacturer, itemName, price, quantity, imageUrl);
    }
}

