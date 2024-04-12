package com.ecom.softwarepatternsca2.Patterns;


import com.ecom.softwarepatternsca2.AdapterClasses.StockAdapterClass;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;

import java.util.ArrayList;

public class FirestoreDataObserver {
    private ArrayList<Stock> stockArrayList;
    private StockAdapterClass recyclerAdapter;

    public FirestoreDataObserver(ArrayList<Stock> stockArrayList, StockAdapterClass recyclerAdapter) {
        this.stockArrayList = stockArrayList;
        this.recyclerAdapter = recyclerAdapter;
    }

    public void onDataUpdated(ArrayList<Stock> updatedData) {
        stockArrayList.clear();
        stockArrayList.addAll(updatedData);
        recyclerAdapter.updateList(stockArrayList);
    }
}

