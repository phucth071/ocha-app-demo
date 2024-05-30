package com.example.Adapter.User;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.Activity.User.ListFoodActivity;
import com.example.Model.Category;
import com.example.demo.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    ArrayList<Category> items;
    Context context;

    public CategoryAdapter(ArrayList<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());

        switch (position) {
            case 0: {
                holder.pic.setBackgroundResource(R.drawable.cat_0_backround);
                break;
            }
            case 1: {
                holder.pic.setBackgroundResource(R.drawable.cat_1_backround);
                break;
            }
            case 2: {
                holder.pic.setBackgroundResource(R.drawable.cat_2_backround);
                break;
            }
            case 3: {
                holder.pic.setBackgroundResource(R.drawable.cat_3_backround);
                break;
            }
            case 4: {
                holder.pic.setBackgroundResource(R.drawable.cat_4_backround);
                break;
            }
            case 5: {
                holder.pic.setBackgroundResource(R.drawable.cat_5_backround);
                break;
            }
            case 6: {
                holder.pic.setBackgroundResource(R.drawable.cat_6_backround);
                break;
            }
            case 7: {
                holder.pic.setBackgroundResource(R.drawable.cat_7_backround);
                break;
            }
        }
        int drawableResourceId = context.getResources().getIdentifier(items.get(position)
                .getImagePath(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(context)
                .load(drawableResourceId)
                .signature(new ObjectKey(System.currentTimeMillis())) // Sử dụng thời gian hiện tại làm signature
                .into(holder.pic);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ListFoodActivity.class);
                intent.putExtra("isViewCategory",true);
                intent.putExtra("CategoryId", items.get(position).getId());
                intent.putExtra("CategoryName", items.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.txt_CateName);
            pic = itemView.findViewById(R.id.imgCat);
        }
    }
}
