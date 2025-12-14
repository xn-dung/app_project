package com.example.myapplication.UI;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.model.User;
import com.example.myapplication.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.*;
public class EditProfileActivity extends AppCompatActivity{
    private EditText myUsername;
    private EditText myPassword;

    private EditText myFullName;
    private EditText myPhone;
    private EditText myEmail;
    private EditText myAddress;
    private Button btnSave;
    private User user;
    private ImageView btnBack;
    private EditText myConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        myUsername = findViewById(R.id.etUsername);
        myPassword = findViewById(R.id.etPassword);
        myAddress = findViewById(R.id.etAddress);
        myEmail = findViewById(R.id.etEmail);
        myPhone = findViewById(R.id.etPhone);
        myFullName = findViewById(R.id.etFullName);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        myConfirmPassword = findViewById(R.id.etConfirmPassword);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        btnBack.setOnClickListener(v -> finish());

        myUsername.setText(user.getUsername());
        myAddress.setText(user.getAddress());
        myEmail.setText(user.getEmail());
        myFullName.setText(user.getFullname());
        myPhone.setText(user.getTel());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = myUsername.getText().toString();
                String password = myPassword.getText().toString();
                String fullname = myFullName.getText().toString();
                String address = myAddress.getText().toString();
                String email = myEmail.getText().toString();
                String phone = myPhone.getText().toString();
                String id = user.getId();

                User user2 = new User(id,username,password,fullname,address,email,phone);
                if(user2.getPassword().isEmpty()){
                    user2.setPassword(user.getPassword());
                }
                if(isSameUser(user,user2)){
                    Intent intent = new Intent(EditProfileActivity.this, ProfileFragment.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }
                else{
                    String confirmPassword = myConfirmPassword.getText().toString();
                    if (!password.isEmpty()) {
                        if (!password.equals(confirmPassword)) {
                            Toast.makeText(EditProfileActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            changeProfile(user2);
                        }
                    }
                    else {
                        user2.setPassword(user.getPassword());
                        changeProfile(user2);
                    }
                }


            }
        });

    }
    private void changeProfile(User user2){
        String url = R.string.backend_url + "api/nguoidung/patch";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<>();
        params.put("_id",user2.getId());
        params.put("username",user2.getUsername());
        params.put("password",user2.getPassword());
        params.put("name",user2.getFullname());
        params.put("address",user2.getAddress());
        params.put("email",user2.getEmail());
        params.put("phone",user2.getTel());
        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                jsonBody,
                response ->{
                    try {
                        String status = response.optString("status","");
                        if (status.equals("success")) {
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditProfileActivity.this, ProfileFragment.class);
                            intent.putExtra("user",user2);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && (error.networkResponse.statusCode == 409 || error.networkResponse.statusCode == 400)) {
                        Toast.makeText(EditProfileActivity.this, "Username này đã tồn tại, vui lòng chọn tên khác.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Lỗi kết nối hoặc server có vấn đề: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);



    }
    private boolean isSameUser(User u1, User u2) {
        return Objects.equals(u1.getUsername(), u2.getUsername()) &&
                Objects.equals(u1.getPassword(), u2.getPassword()) &&
                Objects.equals(u1.getFullname(), u2.getFullname()) &&
                Objects.equals(u1.getAddress(), u2.getAddress()) &&
                Objects.equals(u1.getEmail(), u2.getEmail()) &&
                Objects.equals(u1.getTel(), u2.getTel());
    }

}