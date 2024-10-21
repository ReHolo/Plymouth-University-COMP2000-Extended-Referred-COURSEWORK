package com.example.tennisbooking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.db.DatabaseHelper;


public class RegisterActivity extends AppCompatActivity {

    private EditText etMemberName, etPassword, etConfirmPassword;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etMemberName = findViewById(R.id.etMemberName);
        etPassword = findViewById(R.id.editTextPassword);
        etConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.buttonRegister);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.toolbar_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一个活动
            }
        });

        btnRegister.setOnClickListener(view -> {
            // 获取用户输入并去除空格
            String memberName = etMemberName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // 输入验证
            if (memberName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkMemberNameExists(memberName)) {
                Toast.makeText(this, "The username already exists. Please choose another username.", Toast.LENGTH_SHORT).show();
            } else {
                // 注册用户
                boolean isRegistered = dbHelper.registerUser(memberName, password);
                if (isRegistered) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish(); // 注册成功后结束当前活动
                } else {
                    Toast.makeText(this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
