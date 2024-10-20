package com.example.tennisbooking;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.db.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etMemberName, etPassword;
    private Button btnLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etMemberName = findViewById(R.id.etMemberName);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);

        databaseHelper = new DatabaseHelper(this);

        TextView register = findViewById(R.id.bt_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 导航到注册页面
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // 登录按钮点击事件
        btnLogin.setOnClickListener(view -> {
            String memberName = etMemberName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (memberName.isEmpty() || password.isEmpty()) {
                // 提示用户输入所有字段
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.checkUserCredentials(memberName, password)) {
                // 用户凭证验证成功
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // 更新数据库中的登录状态
                databaseHelper.updateLoginStatus(memberName, true);

                // 获取用户的 accountNo
                Cursor cursor = databaseHelper.getUserDetails(memberName);
                if (cursor != null && cursor.moveToFirst()) {
                    int accountNoIndex = cursor.getColumnIndex("accountNo");
                    if (accountNoIndex != -1) {
                        String accountNo = cursor.getString(accountNoIndex);
                        // 跳转到主页面，并传递用户信息
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("memberName", memberName);
                        intent.putExtra("accountNo", accountNo);
                        startActivity(intent);
                        finish();
                    } else {
                        // 如果未能检索到用户信息
                        Toast.makeText(this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 如果未能检索到用户信息
                    Toast.makeText(this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }

                // 关闭 Cursor 以避免资源泄漏
                if (cursor != null) {
                    cursor.close();
                }
            } else {
                // 用户凭证验证失败
                Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}