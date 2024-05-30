package com.example.Activity.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.Activity.BaseActivity;
import com.example.Model.Category;
import com.example.demo.R;
import com.example.demo.databinding.ActivityAddFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddFoodActivity extends BaseActivity {
    ActivityAddFoodBinding binding;
    EditText editTextTitle,editTextDesc,editTextTime,editTextPrice;
    ImageView pic;
    Spinner spinCate;
    Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK){
                imageUri= result.getData().getData();
                binding.pic.setImageURI(imageUri);
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        initCategory();
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(AddFoodActivity.this, R.layout.sp_item_category, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinCate.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setVariable() {
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood();
            }
        });
        selecteImage();

    }

    private void addFood() {
        editTextTitle = findViewById(R.id.editText_Title);
        editTextDesc = findViewById(R.id.editText_Description);
        editTextTime = findViewById(R.id.editText_Time);
        editTextPrice = findViewById(R.id.editText_Price);
        spinCate = findViewById(R.id.spin_Cate);
        pic = findViewById(R.id.pic);

        String title = editTextTitle.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();
        String timeStr = editTextTime.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        Category selectedCategory = (Category) spinCate.getSelectedItem();
        int categoryId = selectedCategory.getId();

        if (title.isEmpty() || desc.isEmpty() || timeStr.isEmpty() || priceStr.isEmpty() ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Chuyển đổi chuỗi thành số nguyên và số thực
        int time;
        double price;
        try {
            time = Integer.parseInt(timeStr);
            price = Double.parseDouble(priceStr);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadImageToFirebaseStorage(imageUri, title, desc, time, price, categoryId);

    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String title, String desc, int time, double price, int categoryId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
        // Upload ảnh lên Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh từ Firebase Storage
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        // Thêm URL của ảnh và thông tin món ăn vào Firebase Realtime Database
                        String imageUrl = downloadUrl.toString();
                        addFoodToDatabase(title, desc, time, price, categoryId, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Xử lý nếu quá trình upload thất bại
                    Toast.makeText(AddFoodActivity.this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
                });
    }
    private void addFoodToDatabase(String title, String desc, int time, double price, int categoryId, String imageUrl) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You want to add food?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> foodMap = new HashMap<>();
                        foodMap.put("Title", title);
                        foodMap.put("Description", desc);
                        foodMap.put("TimeValue", time);
                        foodMap.put("Price", price);
                        foodMap.put("CategoryId", categoryId);
                        foodMap.put("ImagePath", imageUrl);

                        // Thêm món ăn vào Firebase Realtime Database
                        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
                        foodRef.push().setValue(foodMap)
                                .addOnSuccessListener(aVoid -> {
                                    // Xóa nội dung trong EditText sau khi thêm thành công
                                    editTextTitle.setText("");
                                    editTextDesc.setText("");
                                    editTextTime.setText("");
                                    editTextPrice.setText("");
                                    binding.spinCate.setSelection(0);
                                    pic.setImageResource(0);
                                    editTextTitle.requestFocus();

                                    Toast.makeText(AddFoodActivity.this, "Add food successful", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AddFoodActivity.this, "Failed to add food", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selecteImage() {
        binding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để chọn hình ảnh từ thư viện ảnh của thiết bị
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);
            }
        });
    }

}