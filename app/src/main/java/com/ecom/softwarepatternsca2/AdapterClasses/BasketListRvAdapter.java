package com.ecom.softwarepatternsca2.AdapterClasses;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.softwarepatternsca2.AppManagerClasses.FirestoreManager;
import com.ecom.softwarepatternsca2.MainActivityClasses.AdminSelectedItemActivity;
import com.ecom.softwarepatternsca2.MainActivityClasses.SelectedItemManager;
import com.ecom.softwarepatternsca2.ModelClasses.BasketList;
import com.ecom.softwarepatternsca2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BasketListRvAdapter extends RecyclerView.Adapter<BasketListRvAdapter.BasketListViewHolder> {

    private ArrayList<BasketList> basketLists;
    Context context;

    public BasketListRvAdapter(ArrayList<BasketList> basketLists, Context context) {
        this.basketLists = basketLists;
        this.context = context;
    }

    @NonNull
    @Override
    public BasketListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_item_view, parent, false);
        return new BasketListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketListViewHolder holder, int position) {
        BasketList basketList = basketLists.get(position);

        holder.itemName.setText(basketList.getItemName());
        holder.quantityPurchased.setText("Quantity Purchased:  "+String.valueOf(basketList.getQuantity()+" unit(s)"));
        holder.totalCost.setText("Total Cost:  "+String.valueOf(basketList.getTotalPrice()));
        holder.discount.setText("Discount:  "+"€"+String.valueOf(basketList.getDiscount()));
        holder.itemPrice.setText("Unit Price:  "+basketList.getUnitPrice());
        holder.size.setText("Size:  "+basketList.getItemSize());

        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProject(holder.getAdapterPosition());
                SelectedItemManager.updateQuantityInFirestore(context, basketList.getItemName(), basketList.getQuantity());

            }
        });

    }

    @Override
    public int getItemCount() {
        return basketLists.size();
    }

    public void updateList(ArrayList<BasketList> list) {
        this.basketLists = list;
        notifyDataSetChanged();
    }

    public static class BasketListViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice;
        TextView itemName;
        TextView totalCost;
        TextView discount;
        TextView size;

        Button removeItem;

        TextView quantityPurchased;

        public BasketListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemN);
            itemPrice = itemView.findViewById(R.id.itemP);
            totalCost = itemView.findViewById(R.id.tCost);
            discount = itemView.findViewById(R.id.itemDiscount);
            quantityPurchased = itemView.findViewById(R.id.itemQty);
            size = itemView.findViewById(R.id.itemS);
            removeItem = itemView.findViewById(R.id.removeFromBasket);

        }

    }

    private void removeProject(int position) {
        BasketList removedItem = basketLists.remove(position);
        notifyItemRemoved(position);

        FirestoreManager.getInstance().getDocumentId("BasketList", "itemName", removedItem.getItemName(), documentId -> {
            if (documentId != null) {
                removeItemFromFirestore(documentId);
            }  // Handle the case where the document ID couldn't be retrieved
        });
    }

    private void removeItemFromFirestore(String documentId) {
        FirebaseFirestore.getInstance().collection("BasketList").document(documentId).delete().addOnCompleteListener(task -> {
            task.isSuccessful();
        });
    }
}


