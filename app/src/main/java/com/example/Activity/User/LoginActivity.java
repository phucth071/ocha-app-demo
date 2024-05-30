package com.example.Activity.User;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.Activity.Admin.AdminMenuActivity;
import com.example.Activity.BaseActivity;
import com.example.demo.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#F75564"));
        setVariable();
    }

    private void setVariable() {
        binding.btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editEmail.getText().toString();
                String password = binding.editPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null && currentUser.getEmail().equals("admin@admin.com")) {
                                // Người dùng hiện tại là admin, chuyển hướng đến AdminActivity
                                startActivity(new Intent(LoginActivity.this, AdminMenuActivity.class));
                            } else {
                                // Người dùng không phải là admin, chuyển hướng đến MainActivity hoặc trang khác
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        } else {
                            // Đăng nhập thất bại
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Email hoặc password trống
                    Toast.makeText(LoginActivity.this, "Please fill username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.txtDangKy.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));


    }
}