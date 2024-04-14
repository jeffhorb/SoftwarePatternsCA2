package com.ecom.softwarepatternsca2.AdapterClasses;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.softwarepatternsca2.MainActivityClasses.CustomerTransactionHistoryActivity;
import com.ecom.softwarepatternsca2.ModelClasses.CustomerDetails;
import com.ecom.softwarepatternsca2.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerViewHolder> {

    public List<CustomerDetails> customerList;

    private Context context;

    public CustomerListAdapter(List<CustomerDetails> customerList,Context context) {
        this.customerList = customerList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        CustomerDetails customer = customerList.get(position);
        holder.customerNameTextView.setText(customer.getCustomerName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerTransactionHistoryActivity.class);
                intent.putExtra("selectedCustomer", customer);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void updateList(ArrayList<CustomerDetails> list) {
        this.customerList = list;
        notifyDataSetChanged();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
        }
    }
}
