package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.UI.MainActivity;
import com.example.myapplication.interfaces.NavigationHost;
import com.example.myapplication.model.User;

import org.json.JSONObject;

import java.util.HashMap;

public class RegisterFragment extends Fragment {

    private TextView errorMessage;
    private EditText username;
    private EditText password;
    private EditText fullname;
    private EditText address;
    private EditText email;
    private EditText tel;
    private EditText confirmPassword;
    private Button btnRegister;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.editRegisterUsername);
        password = view.findViewById(R.id.editRegisterPassword);
        fullname = view.findViewById(R.id.editRegisterName);
        address = view.findViewById(R.id.editRegisterAddress);
        email = view.findViewById(R.id.editRegisterEmail);
        tel = view.findViewById(R.id.editRegisterPhone);
        btnRegister = view.findViewById(R.id.button2);
        confirmPassword = view.findViewById(R.id.editConfirmPassword);
        btnBack = view.findViewById(R.id.btnBack);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString();
                String passWord = password.getText().toString();
                String fullName = fullname.getText().toString();
                String Address = address.getText().toString();
                String Email = email.getText().toString();
                String Tel = tel.getText().toString();
                String confirmpassword = confirmPassword.getText().toString();
                if(userName.isEmpty()){
                    Toast.makeText(getContext(), "Username is required", Toast.LENGTH_SHORT).show();
                }
                else if(passWord.isEmpty()){
                    Toast.makeText(getContext(), "Password is required", Toast.LENGTH_SHORT).show();
                }
                else if(!passWord.equals(confirmpassword)){
                    Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else if(fullName.isEmpty()){
                    Toast.makeText(getContext(), "Fullname is required", Toast.LENGTH_SHORT).show();
                }
                else if(Address.isEmpty()){
                    Toast.makeText(getContext(), "Address is required", Toast.LENGTH_SHORT).show();
                }
                else if(Email.isEmpty()){
                    Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
                }
                else if(Tel.isEmpty()){
                    Toast.makeText(getContext(), "Tel is required", Toast.LENGTH_SHORT).show();
                }
                else{
                    User user = new User(userName,passWord,fullName,Address,Email,Tel);
                    RegisterWithAPI(user);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof NavigationHost) {
                    ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
                }
            }
        });
    }

    private void RegisterWithAPI(User user){
        String url = getString(R.string.backend_url) + "api/nguoidung/register";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        HashMap<String,String> params = new HashMap<>();
        params.put("username",user.getUsername());
        params.put("password",user.getPassword());
        params.put("name",user.getFullname());
        params.put("address",user.getAddress());
        params.put("email",user.getEmail());
        params.put("phone",user.getTel());

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String name = response.getString("name");
                        if (!name.isEmpty()) {
                            Toast.makeText(getContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            if (getActivity() instanceof NavigationHost) {
                                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
                            }
                        } else {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && (error.networkResponse.statusCode == 409 || error.networkResponse.statusCode == 400)) {
                        Toast.makeText(getContext(), "Username này đã tồn tại, vui lòng chọn tên khác.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi kết nối hoặc server có vấn đề: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}