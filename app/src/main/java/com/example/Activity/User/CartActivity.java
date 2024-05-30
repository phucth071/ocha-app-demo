package com.example.Activity.User;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.Activity.Admin.AdminMenuActivity;
import com.example.Activity.Admin.AdminReceiptActivity;
import com.example.Activity.BaseActivity;
import com.example.Adapter.User.CartAdapter;
import com.example.Helper.ManagmentCart;
import com.example.Model.Food;
import com.example.Model.OrderStatus;
import com.example.demo.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    private double tax, total = 0;
    double percentTax = 0.05;
    String counponCode; //10%
    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#F75564"));

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        calculateCart();
        initList();
    }

    private void initList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.txtEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.txtEmpty.setVisibility(View.GONE);
            binding.scrollviewCart.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        binding.cartView.setAdapter(adapter);
    }

    private void calculateCart() {
// Tính toán tax
        double tax = managmentCart.getTotalFee() * percentTax;
        DecimalFormat taxFormat = new DecimalFormat("#,###.##");
        taxFormat.setRoundingMode(RoundingMode.DOWN);
        String formattedTax = taxFormat.format(tax);

// Tính toán total
        double counpon = 0;
        total = managmentCart.getTotalFee() + tax;
        if (binding.editTextCounpon.getText().toString().equals(counponCode)) {
            counpon = total * 0.1;
            total = total * 0.9;
        }
        DecimalFormat totalFormat = new DecimalFormat("#,###.##");
        totalFormat.setRoundingMode(RoundingMode.DOWN);
        String formattedTotal = totalFormat.format(total);

        DecimalFormat totalCounpon = new DecimalFormat("#,###.##");
        totalCounpon.setRoundingMode(RoundingMode.DOWN);
        String formattedCounpon = "- " + totalCounpon.format(counpon);

// Lấy giá trị itemTotal
        double itemTotal = managmentCart.getTotalFee();
        DecimalFormat itemTotalFormat = new DecimalFormat("#,###.##");
        itemTotalFormat.setRoundingMode(RoundingMode.DOWN);
        String formattedItemTotal = itemTotalFormat.format(itemTotal);

        // Gán giá trị vào TextViews
        binding.txtSubTotal.setText(formattedItemTotal);
        binding.txtTax.setText(formattedTax);
        binding.txtTotal.setText(formattedTotal);
        binding.txtCounpon.setText(formattedCounpon);

    }

    private void setVariable() {
        binding.btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, ReceiptActivity.class));
            }
        });

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCounpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counponCode = "xincamon";
                if (binding.editTextCounpon.getText().toString().equals(counponCode)) {
                    Toast.makeText(CartActivity.this, "Apply counpon successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Counpon isn't exist", Toast.LENGTH_SHORT).show();
                }
                calculateCart();
            }
        });

        binding.btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null) {
                    context = getApplicationContext();
                }
                if (!isFinishing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("You want to Checkout?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                addReceipt();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                dialog.dismiss();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }


    private void addReceipt() {
        DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference("Receipt");
        String emailOrder = currentUser.getEmail();
        String name = binding.editName.getText().toString().trim();
        String address = binding.editAddress.getText().toString().trim();
        String phoneStr = binding.editPhone.getText().toString().trim();
        if (total == 0) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty() || address.isEmpty() || phoneStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (containsNumber(name)) {
            Toast.makeText(this, "Name cannot contain numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phoneStr.length()!=10){
            Toast.makeText(this, "Phone must 10 number", Toast.LENGTH_SHORT).show();
            return;
        }

        long phone;
        try {
            phone = Long.parseLong(phoneStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
            return;
        }

// Tiếp tục xử lý nếu dữ liệu nhập vào là hợp lệ

        // Retrieve the list of items from the cart
        ManagmentCart cartManager = new ManagmentCart(this);
        ArrayList<Food> cartItems = cartManager.getListCart();

        // Convert the list of Food objects to a list of maps
        ArrayList<Map<String, Object>> itemList = new ArrayList<>();
        for (Food food : cartItems) {
            Map<String, Object> foodMap = new HashMap<>();
            foodMap.put("Title", food.getTitle());
            foodMap.put("Price", food.getPrice());
            foodMap.put("Quantity", food.getNumberInCart());
            itemList.add(foodMap);
        }

        // Tạo một HashMap để lưu thông tin của hóa đơn
        Map<String, Object> receiptMap = new HashMap<>();
        receiptMap.put("Name", name);
        receiptMap.put("EmailOrder",emailOrder);
        receiptMap.put("Address", address);
        receiptMap.put("Phone", phone);
        receiptMap.put("Total", total);
        receiptMap.put("Items", itemList);
        receiptMap.put("OrderStatus", OrderStatus.WAIT_FOR_CONFIRMATION);
        // Thêm hóa đơn vào Firebase Realtime Database
        receiptRef.push().setValue(receiptMap)
                .addOnSuccessListener(aVoid -> {
                    // Xóa nội dung trong các EditText sau khi thêm thành công
                    binding.editName.setText("");
                    binding.editAddress.setText("");
                    binding.editPhone.setText("");
                    Toast.makeText(CartActivity.this, "Add receipt successful", Toast.LENGTH_SHORT).show();
                    managmentCart.resetCart();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Failed to add receipt", Toast.LENGTH_SHORT).show();
                });

    }

    private boolean containsNumber(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true; // Nếu có ký tự số, trả về true
            }
        }
        return false; // Không có ký tự số, trả về false
    }


}