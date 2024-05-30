package com.example.Activity.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Activity.BaseActivity;
import com.example.Activity.User.LoginActivity;
import com.example.Activity.User.MainActivity;
import com.example.Adapter.Admin.AdminFoodListAdapter;
import com.example.Model.Food;
import com.example.demo.databinding.ActivityAdminMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMenuActivity extends BaseActivity {
    ActivityAdminMainBinding binding;
    Context context;
    private RecyclerView.Adapter adapterListFood;


    @Override
    protected void onResume() {
        super.onResume();
        initListFood();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListFood();
        setVariable();
    }

    private void setVariable() {
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this, AddFoodActivity.class));
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null) {
                    context = getApplicationContext(); // Gán giá trị mặc định nếu context là null
                }
                // Kiểm tra trạng thái của Activity trước khi hiển thị AlertDialog
                if (!isFinishing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminMenuActivity.this);
                    builder.setMessage("You want to log out?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(AdminMenuActivity.this, LoginActivity.class));
                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                dialog.dismiss();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        binding.txtReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this, AdminReceiptActivity.class));
            }
        });
    }

    private void initListFood() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarListFood.setVisibility(View.VISIBLE);
        ArrayList<Food> list = new ArrayList<>();
        Query query = myRef.orderByChild("CategoryId");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Food food = issue.getValue(Food.class);
                        food.setKey(issue.getKey()); // Gán key cho mỗi Food
                        list.add(food);
                    }

                    if (list.size() > 0) {
                        binding.recyclerViewListFood.setLayoutManager(new GridLayoutManager(AdminMenuActivity.this, 1));
                        adapterListFood = new AdminFoodListAdapter(list,myRef);
                        binding.recyclerViewListFood.setAdapter(adapterListFood);
                    }
                    binding.progressBarListFood.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}