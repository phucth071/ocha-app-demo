package com.example.Activity.User;

import android.graphics.Color;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.Activity.BaseActivity;
import com.example.Helper.ManagmentCart;
import com.example.Model.Food;
import com.example.demo.databinding.ActivityDetailBinding;

import java.text.DecimalFormat;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Food object;
    private ManagmentCart managmentCart;
    private int num =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(Color.parseColor("#F75564"));

        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        managmentCart =new ManagmentCart(this);
        binding.btnBack.setOnClickListener(v -> finish());

        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);

        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        String formattedPrice = decimalFormat.format(object.getPrice());
        binding.txtPrice.setText(formattedPrice);
        binding.txtTitle.setText(object.getTitle());
        binding.txtDescription.setText(object.getDescription());
        binding.txtRate.setText(object.getStar()+" Rating");
        binding.ratingBar.setRating((float)object.getStar());
        binding.txtTime.setText(object.getTimeValue()+" min");

        String formattedTotalPrice = decimalFormat.format(num*object.getPrice());
        binding.txtTotalPrice.setText(formattedTotalPrice);

        binding.txtCong.setOnClickListener(v -> {
            num+=1;
            binding.txtNum.setText(num+"");
            binding.txtTotalPrice.setText(decimalFormat.format(num*object.getPrice()));
        });
        binding.txtTru.setOnClickListener(v -> {
            if(num>1){
                num-=1;
                binding.txtNum.setText(num+"");
                binding.txtTotalPrice.setText(decimalFormat.format(num*object.getPrice()));
            }
        });

        binding.btnAdd.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
            finish();
        });
    }

    private void getIntentExtra() {
        object= (Food) getIntent().getSerializableExtra("object");
    }
}