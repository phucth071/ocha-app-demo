package com.example.Activity.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.Activity.BaseActivity;
import com.example.Model.Category;
import com.example.Model.Food;
import com.example.demo.R;
import com.example.demo.databinding.ActivityUpdateFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateFoodActivity extends BaseActivity {
    ActivityUpdateFoodBinding binding;
    private Food object;
    EditText editTextTitle, editTextDesc, editTextTime, editTextPrice;
    ImageView pic;
    Uri imageUri;
    Spinner spinCate;
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
        binding = ActivityUpdateFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();

    }

    private void setVariable() {
        binding.buttonBack.setOnClickListener(v -> finish());
        Glide.with(UpdateFoodActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);
        DecimalFormat decimalFormat = new DecimalFormat("####.#");
        String formattedPrice = decimalFormat.format(object.getPrice());
        binding.editTextTitle.setText(object.getTitle());
        binding.editTextDescription.setText(object.getDescription());
        binding.editTextTime.setText(object.getTimeValue() + "");
        binding.editTextPrice.setText(formattedPrice);


        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFood();
            }
        });
        selecteImage();
    }

    private void updateFood() {
        editTextTitle = findViewById(R.id.editText_Title);
        editTextDesc = findViewById(R.id.editText_Description);
        editTextTime = findViewById(R.id.editText_Time);
        editTextPrice = findViewById(R.id.editText_Price);
        spinCate = findViewById(R.id.spin_Cate);

        String title = editTextTitle.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();
        String timeStr = editTextTime.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || timeStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Chuyển đổi chuỗi thành số nguyên và số thực
        int time, cateID;
        double price;
        try {
            time = Integer.parseInt(timeStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageToFirebaseStorage(imageUri, title, desc, time, price);
        }
        else{
            updateFoodToDatabase(title, desc, time, price,null);
        }

    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String title, String desc, int time, double price) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());

        // Upload ảnh lên Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh từ Firebase Storage
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        // Thêm URL của ảnh và thông tin món ăn vào Firebase Realtime Database
                        String imageUrl = downloadUrl.toString();
                        updateFoodToDatabase(title, desc, time, price, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Xử lý nếu quá trình upload thất bại
                    Toast.makeText(UpdateFoodActivity.this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFoodToDatabase(String title, String desc, int time, double price, String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You want to update food?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> foodMap = new HashMap<>();
                        foodMap.put("Title", title);
                        foodMap.put("Description", desc);
                        foodMap.put("TimeValue", time);
                        foodMap.put("Price", price);
                        if(imageUrl!=null) {
                            foodMap.put("ImagePath", imageUrl);
                        }
                        DatabaseReference foodRef = database.getReference("Foods");
                        foodRef.child(object.getKey()).updateChildren(foodMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UpdateFoodActivity.this, "Update food successful", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(UpdateFoodActivity.this, "Failure", Toast.LENGTH_SHORT).show());
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
    private void getIntentExtra() {
        object = (Food) getIntent().getSerializableExtra("object1");
    }
}