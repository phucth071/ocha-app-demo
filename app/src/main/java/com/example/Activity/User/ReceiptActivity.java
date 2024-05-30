package com.example.Activity.User;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Activity.BaseActivity;
import com.example.Adapter.User.ReceiptListAdapter;
import com.example.Model.Receipt;
import com.example.demo.databinding.ActivityAdminReceiptBinding;
import com.example.demo.databinding.ActivityBillBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReceiptActivity extends BaseActivity {
    ActivityBillBinding binding;
    private RecyclerView.Adapter adapterListReceipt;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
        initListReceipt();
    }
    private void setVariable() {
        binding.buttonBack.setOnClickListener(v -> finish());
    }

    private void initListReceipt() {
        String emailUser = currentUser.getEmail();

        DatabaseReference myRef = database.getReference("Receipt");
        binding.progressBarListReceipt.setVisibility(View.VISIBLE);
        ArrayList<Receipt> list = new ArrayList<>();
        Query query = myRef.orderByChild("EmailOrder").equalTo(emailUser);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Receipt receipt = issue.getValue(Receipt.class);
                        assert receipt != null;
                        receipt.setKey(issue.getKey()); // Gán key cho mỗi Receipt
                        list.add(receipt);
                    }

                    if (list.size() > 0) {
                        binding.recyclerViewListReceipt.setLayoutManager(new GridLayoutManager(ReceiptActivity.this, 1));
                        adapterListReceipt = new ReceiptListAdapter(list,myRef);
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