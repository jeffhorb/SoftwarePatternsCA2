package com.ecom.softwarepatternsca2.Interfaces;

import com.ecom.softwarepatternsca2.ModelClasses.Stock;

public interface StockInteractionListener {
    void onItemClicked(Stock stock);
    void onRateIconClicked(Stock stock);
}
