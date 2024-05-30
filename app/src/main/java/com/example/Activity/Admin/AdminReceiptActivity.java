package com.example.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.Activity.BaseActivity;
import com.example.Adapter.Admin.AdminFoodListAdapter;
import com.example.Adapter.Admin.AdminReceiptListAdapter;
import com.example.Model.Food;
import com.example.Model.Receipt;
import com.example.demo.R;
import com.example.demo.databinding.ActivityAdminReceiptBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminReceiptActivity extends BaseActivity {
    ActivityAdminReceiptBinding binding;
    private AdminReceiptListAdapter adapterListReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminReceiptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
        initListReceipt();
    }
    private void setVariable() {
        binding.buttonBack.setOnClickListener(v -> finish());
    }

    private void initListReceipt() {
        DatabaseReference myRef = database.getReference("Receipt");
        binding.progressBarListReceipt.setVisibility(View.VISIBLE);
        ArrayList<Receipt> list = new ArrayList<>();
        Query query = myRef.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Receipt receipt = issue.getValue(Receipt.class);
                        receipt.setKey(issue.getKey()); // Gán key cho mỗi Receipt
                        list.add(receipt);
                    }

                    if (list.size() > 0) {
                        binding.recyclerViewListReceipt.setLayoutManager(new GridLayoutManager(AdminReceiptActivity.this, 1));
                        adapterListReceipt = new AdminReceiptListAdapter(list,myRef);
                        binding.recyclerViewListReceipt.setAdapter(adapterListReceipt);
                    }
                    binding.progressBarListReceipt.setVisibility(View.GONE);
                }
                binding.progressBarListReceipt.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}