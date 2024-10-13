package com.example.tennisbooking.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tennisbooking.R;
import com.example.tennisbooking.db.UserDbHelper;
import com.example.tennisbooking.entity.UserInfo;


public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername;

    private EditText editPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        editUsername =findViewById(R.id.editUsername);
        editPassword =findViewById(R.id.editPassword);


        //toBack
        findViewById(R.id.toolbar_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //click sign up

        findViewById(R.id.sign_up_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
                }
                else {

                    int row = UserDbHelper.getInstance(RegisterActivity.this).register(username, password);
                    if(row>0){
                        Toast.makeText(RegisterActivity.this, "Successful registration", Toast.LENGTH_SHORT).show();

                        finish();

                    }





                }

            }
        });
    }
}