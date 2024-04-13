package com.ecom.softwarepatternsca2.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.softwarepatternsca2.AppManagerClasses.AdminChecker;
import com.ecom.softwarepatternsca2.Interfaces.AdminCheckCallback;
import com.ecom.softwarepatternsca2.Interfaces.StockInteractionListener;
import com.ecom.softwarepatternsca2.MainActivityClasses.AdminSelectedItemActivity;
import com.ecom.softwarepatternsca2.MainActivityClasses.CustomerSelectedItemActivity;
import com.ecom.softwarepatternsca2.ModelClasses.Stock;
import com.ecom.softwarepatternsca2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StockAdapterClass extends RecyclerView.Adapter<StockAdapterClass.ViewHolder> {
    private ArrayList<Stock> stockList;
    private Context context;

    private StockInteractionListener listener;
    //private ItemClickStrategy itemClickStrategy;

    public StockAdapterClass(ArrayList<Stock> stockArrayList, Context context, StockInteractionListener listener) {
        this.stockList = stockArrayList;
        this.context = context;
        this.listener = listener;
        //this.itemClickStrategy = itemClickStrategy;
    }

    public void updateList(ArrayList<Stock> list) {
        this.stockList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        holder.itemName.setText(stock.getItemName());
        holder.itemName.setSelected(true);
        holder.price.setText(stock.getPrice());
        holder.quantity.setText(stock.getQuantity() +" Units");
        holder.manufacturer.setText(stock.getManufacturer());

        AdminChecker.checkIfAdmin(new AdminCheckCallback() {
            @Override
            public void onResult(boolean isAdmin) {
                if (isAdmin) {
                    holder.quantity.setVisibility(View.VISIBLE);
                } else {
                    holder.quantity.setVisibility(View.GONE);
                }
            }
        });

        // Load the image from the URL into the ImageView
        Picasso.get()
                .load(stock.getImageUrl())
                .fit()
                .into(holder.itemImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(stock);
                }

                // Check if the user is admin and show/hide quantity TextView accordingly
                AdminChecker.checkIfAdmin(new AdminCheckCallback() {
                    @Override
                    public void onResult(boolean isAdmin) {
                        if (isAdmin) {
                            Intent intent = new Intent(context, AdminSelectedItemActivity.class);
                            // the selected project as an extra in the Intent
                            intent.putExtra("selectedItem",stock);
                            context.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(context, CustomerSelectedItemActivity.class);
                            intent.putExtra("selectedItem",stock);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });

        // Set onClickListener for rateIcon
        holder.rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRateIconClicked(stock);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, price, manufacturer, quantity;
        CardView cardView;
        ImageView itemImage;
        LinearLayout rate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            cardView = itemView.findViewById(R.id.stockList);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            manufacturer = itemView.findViewById(R.id.manufacturer);
            quantity = itemView.findViewById(R.id.quantity);
            itemImage = itemView.findViewById(R.id.itemImage);
            rate = itemView.findViewById(R.id.rate);

        }
    }


}

