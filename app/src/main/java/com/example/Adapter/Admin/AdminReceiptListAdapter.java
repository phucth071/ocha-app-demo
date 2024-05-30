package com.example.Adapter.Admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.Activity.Admin.AdminReceiptActivity;
import com.example.Activity.Admin.UpdateFoodActivity;
import com.example.Activity.User.MainActivity;
import com.example.Model.Food;
import com.example.Model.OrderStatus;
import com.example.Model.Receipt;
import com.example.demo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReceiptListAdapter extends RecyclerView.Adapter<AdminReceiptListAdapter.viewholder> {

    ArrayList<Receipt> items;
    Context context;
    DatabaseReference databaseRef;


    public AdminReceiptListAdapter(ArrayList<Receipt> items, DatabaseReference databaseRef) {
        this.items = items;
        this.databaseRef = databaseRef;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_admin_receipt, parent, false);
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
        holder.itemTxt.setText("" + itemsStringBuilder.toString().trim());

        if (items.get(position).getOrderStatus() == OrderStatus.WAIT_FOR_CONFIRMATION) {
            holder.confirmBtn.setText("Wait for Confirm");
        } else if (items.get(position).getOrderStatus() == OrderStatus.WAIT_FOR_SHIPPER) {
            holder.confirmBtn.setText("Wait for Shipper");
            holder.confirmBtn.setBackgroundColor(Color.parseColor("#7FFF7F"));
        } else if (items.get(position).getOrderStatus() == OrderStatus.DELIVERING) {
            holder.confirmBtn.setText("Delivering");
            holder.confirmBtn.setEnabled(false);
            holder.confirmBtn.setBackgroundColor(Color.parseColor("#F2FAFC"));
            holder.confirmBtn.setTextColor(Color.parseColor("#000000"));
        } else if (items.get(position).getOrderStatus() == OrderStatus.DELIVERED) {
            holder.confirmBtn.setText("Delivered");
            holder.confirmBtn.setBackgroundColor(Color.parseColor("#7ed1e6"));
            holder.confirmBtn.setEnabled(false);
        }

        holder.delBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        String receiptKey = items.get(position).getKey();
                        // Xóa mục từ Firebase
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
            builder.setMessage("Confirm?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        String receiptKey = items.get(position).getKey();
                        OrderStatus newStatus;
                        Log.d("HoaDonAdapter", "onBindViewHolder: " + items.get(position).getOrderStatus().toString());
                        if (items.get(position).getOrderStatus() == OrderStatus.WAIT_FOR_CONFIRMATION) {
                            newStatus = OrderStatus.WAIT_FOR_SHIPPER;
                        } else if (items.get(position).getOrderStatus() == OrderStatus.WAIT_FOR_SHIPPER) {
                            newStatus = OrderStatus.DELIVERING;
                        } else if (items.get(position).getOrderStatus() == OrderStatus.DELIVERING) {
                            newStatus = OrderStatus.DELIVERED;
                        } else {
                            newStatus = items.get(position).getOrderStatus();
                        }

                        // Update the order status in Firebase
                        Map<String, Object> receiptMap = new HashMap<>();
                        receiptMap.put("OrderStatus", newStatus);
                        databaseRef.child(receiptKey).updateChildren(receiptMap);

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
                                holder.confirmBtn.setBackgroundColor(Color.parseColor("#7ed1e6"));
                            }
                        } else {
                            holder.confirmBtn.setText("Unknown status");
                        }
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
        TextView titleTxt, priceTxt, addressTxt, phoneTxt, idTxt, itemTxt,emailTxt;
        Button delBtn, confirmBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            idTxt = itemView.findViewById(R.id.txt_ID);
            titleTxt = itemView.findViewById(R.id.txt_Title);
            priceTxt = itemView.findViewById(R.id.txt_Price);
            addressTxt = itemView.findViewById(R.id.txt_Address);
            phoneTxt = itemView.findViewById(R.id.txt_Phone);
            delBtn = itemView.findViewById(R.id.btn_Delete);
            confirmBtn = itemView.findViewById(R.id.btn_Confirm);
            itemTxt=itemView.findViewById(R.id.txt_Items);
            emailTxt=itemView.findViewById(R.id.txt_Email);

        }
    }


}
