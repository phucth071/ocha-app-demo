package com.example.Activity.User;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Activity.BaseActivity;
import com.example.Adapter.User.FoodListAdapter;
import com.example.Model.Food;
import com.example.demo.databinding.ActivityListFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListFoodActivity extends BaseActivity {
    ActivityListFoodBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;
    private boolean isFound = false;
    private boolean isViewAll;
    private boolean isViewCategory;
    private boolean isDescending;
    private boolean isAscending;

    @Override
    protected void onResume() {
        super.onResume();
        initListFood();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#F75564"));

        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initListFood();
    }

    private void initListFood() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarListFood.setVisibility(View.VISIBLE);
        ArrayList<Food> list = new ArrayList<>();
        Query query = null;
        //ViewAll
        if (isViewAll) {
            binding.txtTitle.setText("All items");
            query = myRef.orderByChild("Id");
        }
        //Search
        String searchTextCapitalized = capitalizeString(searchText); // Chuyển đổi chuỗi tìm kiếm thành dạng có chữ cái viết hoa ở đầu
        if (isSearch) {
            binding.txtTitle.setText("Result for '" + searchText + "'");
            query = myRef.orderByChild("Title").startAt(searchTextCapitalized).endAt(searchTextCapitalized + '\uf8ff');
        }
        //View Category
        if (isViewCategory){
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
        }
        //View Ascending
        if (isDescending){
            query = myRef.orderByChild("Price");
        }
        if (isAscending){
            query = myRef.orderByChild("Price");
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Food.class));
                        isFound = true;
                    }

                    // Sắp xếp danh sách theo thứ tự giảm dần nếu isDescending = true
                    if (isDescending) {
                        Collections.sort(list, new Comparator<Food>() {
                            @Override
                            public int compare(Food food1, Food food2) {
                                return Float.compare((float) food2.getPrice(), (float) food1.getPrice());
                            }
                        });
                    }

                    if (list.size() > 0) {
                        binding.recyclerViewListFood.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                        adapterListFood = new FoodListAdapter(list);
                        binding.recyclerViewListFood.setAdapter(adapterListFood);
                    }
                    binding.progressBarListFood.setVisibility(View.GONE);
                }
                if (!isFound) {
                    binding.progressBarListFood.setVisibility(View.VISIBLE);
                    binding.txtNoti.setVisibility(View.GONE);
                    binding.txtNoti.setText(""); // Xóa nội dung của TextView

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressBarListFood.setVisibility(View.GONE);
                            binding.txtNoti.setVisibility(View.VISIBLE);
                            binding.txtNoti.setText("Not found item for '" + searchText + "'");
                        }
                    }, 2000); // Đặt delay 3 giây (3000 milliseconds)
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        isViewAll = getIntent().getBooleanExtra("isViewAll", false);
        isViewCategory= getIntent().getBooleanExtra("isViewCategory",false);
        isDescending=getIntent().getBooleanExtra("isDescending",false);
        isAscending=getIntent().getBooleanExtra("isAscending",false);

        binding.txtTitle.setText(categoryName);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Hàm chuyển đổi chuỗi thành chuỗi với chữ cái viết hoa ở đầu
    public static String capitalizeString(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}