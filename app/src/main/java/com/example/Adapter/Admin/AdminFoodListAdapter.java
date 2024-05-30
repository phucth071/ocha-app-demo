package com.example.Adapter.Admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.Activity.Admin.UpdateFoodActivity;
import com.example.Model.Food;
import com.example.demo.R;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdminFoodListAdapter extends RecyclerView.Adapter<AdminFoodListAdapter.viewholder> {

    ArrayList<Food> items;
    Context context;
    DatabaseReference databaseRef;

    public AdminFoodListAdapter(ArrayList<Food> items, DatabaseReference databaseRef) {
        this.items = items;
        this.databaseRef = databaseRef;
    }

    public AdminFoodListAdapter(ArrayList<Food> items) {
        this.items =items;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_admin_listfood, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        String formattedPrice = decimalFormat.format( items.get(position).getPrice());
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.priceTxt.setText(formattedPrice);

        Glide.with(context)
                .load(items.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);



        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this item?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            // Lấy key của mục từ Food
                            String foodKey = items.get(position).getKey();
                            // Xóa mục từ Firebase
                            databaseRef.child(foodKey).removeValue();
                            // Xóa mục khỏi ArrayList và cập nhật RecyclerView
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            dialog.dismiss();
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, UpdateFoodActivity.class);
                intent.putExtra("object1",items.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt;
        Button editBtn,delBtn;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.txt_Title);
            priceTxt = itemView.findViewById(R.id.txt_Price);
            editBtn=itemView.findViewById(R.id.btn_Edit);
            delBtn=itemView.findViewById(R.id.btn_Delete);

            pic = itemView.findViewById(R.id.img);
        }
    }


}
