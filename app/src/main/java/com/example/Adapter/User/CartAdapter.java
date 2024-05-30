package com.example.Adapter.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import com.example.Helper.ChangeNumberItemsListener;
import com.example.Helper.ManagmentCart;
import com.example.Model.Food;
import com.example.demo.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewholder> {
    ArrayList<Food> list;
    private ManagmentCart managmentCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<Food> list, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.list = list;
        managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        String formattedFee = decimalFormat.format(list.get(position).getNumberInCart()) + " * " +
                decimalFormat.format(list.get(position).getPrice());
        String formattedTotal = decimalFormat.format(list.get(position).getNumberInCart() * list.get(position).getPrice());

        holder.titleTxt.setText(list.get(position).getTitle());
        holder.feeEachItem.setText(formattedFee);
        holder.totalEachItem.setText(formattedTotal);
        holder.num.setText(list.get(position).getNumberInCart() + "");

        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.btnTru.setOnClickListener(v -> managmentCart.minusNumberItem(list, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
        holder.btnCong.setOnClickListener(v -> managmentCart.plusNumberItem(list, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, feeEachItem, btnCong, btnTru, num, totalEachItem;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.txt_Title);
            btnCong = itemView.findViewById(R.id.txt_Cong);
            btnTru = itemView.findViewById(R.id.txt_Tru);
            feeEachItem = itemView.findViewById(R.id.txt_PriceItem);
            totalEachItem = itemView.findViewById(R.id.txt_Price);
            pic = itemView.findViewById(R.id.pic);
            num = itemView.findViewById(R.id.txt_Num);
        }
    }
}
