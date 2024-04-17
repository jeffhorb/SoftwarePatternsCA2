package com.ecom.softwarepatternsca2.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.softwarepatternsca2.ModelClasses.TransactionDetails;
import com.ecom.softwarepatternsca2.R;

import java.util.ArrayList;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewHolder> {

    private ArrayList<TransactionDetails> transactionDetailsArrayList;
    Context context;

    public TransactionHistoryAdapter(ArrayList<TransactionDetails> transactionDetailsArrayList, Context context) {
        this.transactionDetailsArrayList = transactionDetailsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionDetails transactionDetails = transactionDetailsArrayList.get(position);

        holder.itemName.setText(transactionDetails.getItemName());
        holder.quantityPurchased.setText("Quantity Purchased:  "+String.valueOf(transactionDetails.getQuantity()+" unit(s)"));
        holder.totalCost.setText("Total Cost:  "+String.valueOf(transactionDetails.getTotalPrice()));
        holder.discount.setText("Discount:  "+"€"+String.valueOf(transactionDetails.getDiscount()));
        holder.itemPrice.setText("Unit Price:  "+transactionDetails.getUnitPrice());
        holder.size.setText("Size:  "+transactionDetails.getItemSize());

    }

    @Override
    public int getItemCount() {
        return transactionDetailsArrayList.size();
    }

    public void updateList(ArrayList<TransactionDetails> list) {
        this.transactionDetailsArrayList = list;
        notifyDataSetChanged();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice;
        TextView itemName;
        TextView totalCost;
        TextView discount;
        TextView size;

        TextView quantityPurchased;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            totalCost = itemView.findViewById(R.id.totalCost);
            discount = itemView.findViewById(R.id.discount);
            quantityPurchased = itemView.findViewById(R.id.qty);
            size = itemView.findViewById(R.id.size);

        }

    }
}

