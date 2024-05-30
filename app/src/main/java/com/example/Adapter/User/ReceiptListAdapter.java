package com.example.Adapter.User;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Model.Food;
import com.example.Model.OrderStatus;
import com.example.Model.Receipt;
import com.example.demo.R;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptListAdapter extends RecyclerView.Adapter<ReceiptListAdapter.viewholder> {

    ArrayList<Receipt> items;
    Context context;
    DatabaseReference databaseRef;


    public ReceiptListAdapter(ArrayList<Receipt> items, DatabaseReference databaseRef) {
        this.items = items;
        this.databaseRef = databaseRef;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_bill, parent, false);
        return new viewholder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        String formattedPrice = decimalFormat.format(items.get(position).getTotal());
        holder.idTxt.setText     ("B/L     : " + items.get(position).getKey());
        holder.titleTxt.setText  ("Name    : " + items.get(position).getName());
        holder.emailTxt.setText  ("Email   : " + items.get(position).getEmailOrder());
        holder.priceTxt.setText  ("Total   : " + formattedPrice +"đ");
        holder.phoneTxt.setText  ("Phone   : " + items.get(position).getPhone());
        holder.addressTxt.setText("Address : " + items.get(position).getAddress());
        // Extract and format the items
        List<Food> itemList = items.get(position).getItems();
        StringBuilder itemsStringBuilder = new StringBuilder();
        for (Food item : itemList) {
            itemsStringBuilder.append(item.getTitle()).append("  x").append(item.getQuantity()).append("\n");
        }
        holder.itemTxt.setText(itemsStringBuilder.toString().trim());
        holder.tvOrderStatus.setEnabled(false);
        holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#F2FAFC"));
        holder.tvOrderStatus.setTextColor(Color.parseColor("#000000"));
        holder.confirmBtn.setVisibility(View.GONE);
        if (items.get(position).getOrderStatus() == OrderStatus.WAIT_FOR_CONFIRMATION) {
            holder.tvOrderStatus.setText("Wait for Confirm");
        } else if (items.get(position).getOrderStatus() == OrderStatus.WAIT_FOR_SHIPPER) {
            holder.tvOrderStatus.setText("Wait for Shipper");
        } else if (items.get(position).getOrderStatus() == OrderStatus.DELIVERING) {
            holder.tvOrderStatus.setText("Delivering");
            holder.tvOrderStatus.setEnabled(true);
            holder.confirmBtn.setVisibility(View.VISIBLE);
            holder.tvOrderStatus.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.tvOrderStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (items.get(position).getOrderStatus() == OrderStatus.DELIVERED) {
            holder.tvOrderStatus.setText("Delivered");
            holder.tvOrderStatus.setEnabled(false);
            holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#F2FAFC"));
            holder.tvOrderStatus.setTextColor(Color.parseColor("#000000"));
        }

        holder.delBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to cancel order ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // Xóa mục từ Firebase
                        String receiptKey = items.get(position).getKey();
                        databaseRef.child(receiptKey).removeValue();
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        dialog.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });

        holder.confirmBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to confirm order ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        String receiptKey = items.get(position).getKey();
                        OrderStatus newStatus;
                        Log.d("ReceiptAdapter", "onBindViewHolder: " + items.get(position).getOrderStatus().toString());
                        if (items.get(position).getOrderStatus() == OrderStatus.DELIVERING) {
                            newStatus = OrderStatus.DELIVERED;
                        } else {
                            newStatus = items.get(position).getOrderStatus();
                        }

                        Log.d("ReceiptAdapter", "onBindViewHolder: " + newStatus.toString());
                        // Update the order status in Firebase
                        Map<String, Object> receiptMap = new HashMap<>();
                        receiptMap.put("OrderStatus", newStatus);
                        databaseRef.child(receiptKey).updateChildren(receiptMap)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Update the local object and UI
                                        items.get(position).setOrderStatus(newStatus);
                                        if (newStatus != null) {
                                            holder.confirmBtn.setText(newStatus.toString());
                                            if (newStatus == OrderStatus.WAIT_FOR_SHIPPER) {
                                                holder.confirmBtn.setBackgroundColor(Color.parseColor("#7FFF7F"));
                                            } else if (newStatus == OrderStatus.DELIVERING) {
                                                holder.confirmBtn.setEnabled(false);
                                                holder.confirmBtn.setBackgroundColor(Color.parseColor("#F2FAFC"));
                                                holder.confirmBtn.setTextColor(Color.parseColor("#000000"));
                                            } else if (newStatus == OrderStatus.DELIVERED) {
                                                holder.confirmBtn.setEnabled(false);
                                                holder.confirmBtn.setBackgroundColor(Color.parseColor("#F2FAFC"));
                                                holder.confirmBtn.setTextColor(Color.parseColor("#000000"));
                                                holder.tvOrderStatus.setText("Delivered");
                                            }
                                        } else {
                                            holder.confirmBtn.setText("Unknown status");
                                        }
                                    } else {
                                        // Handle the error
                                        Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        dialog.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, addressTxt, phoneTxt, idTxt, itemTxt,emailTxt, tvOrderStatus;
        Button delBtn, confirmBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            idTxt = itemView.findViewById(R.id.txt_ID);
            titleTxt = itemView.findViewById(R.id.txt_Title);
            priceTxt = itemView.findViewById(R.id.txt_Price);
            addressTxt = itemView.findViewById(R.id.txt_Address);
            phoneTxt = itemView.findViewById(R.id.txt_Phone);
            delBtn = itemView.findViewById(R.id.btn_Delete);
            tvOrderStatus = itemView.findViewById(R.id.btn_orderStatus);
            itemTxt=itemView.findViewById(R.id.txt_Items);
            emailTxt=itemView.findViewById(R.id.txt_Email);
            confirmBtn = itemView.findViewById(R.id.btn_Confirm);
        }
    }


}
