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
import com.example.myapplication.UI.HomeActivity;
import com.example.myapplication.interfaces.NavigationHost;
import com.example.myapplication.model.User;

import org.json.JSONObject;

import java.util.HashMap;
public class ResetPasswordFragment extends Fragment{
    private String email;
    private EditText password;
    private EditText rePassword;
    private Button btnSave;
    private TextView errorMessage;
    private ImageButton btnBack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            email = getArguments().getString("email");
        }
        return inflater.inflate(R.layout.reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        password = view.findViewById(R.id.edtPassword);
        rePassword = view.findViewById(R.id.edtRePassword);
        btnSave = view.findViewById(R.id.btnSubmit);
        errorMessage = view.findViewById(R.id.errorMessage);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ForgotPasswordFragment.class);
                startActivity(intent);
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = password.getText().toString();
                String password2 = rePassword.getText().toString();
                if(password1.equals(password2)){
                    resetPasswordwithAPI(password1);
                }
                else{
                    displayError("Mật khẩu không khớp");

                }
            }
        });

    }
    private void displayError(String message){
        if(errorMessage != null){
            errorMessage.setText(message);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }
    private void resetPasswordwithAPI(String password){
        String url = getString(R.string.backend_url) + "api/nguoidung/resetpassword";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        HashMap<String,String> params = new HashMap<>();
        params.put("email",email);
        params.put("nPassword",password);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                jsonBody,
                response -> {
                    try{
                        String message = response.getString("message");
                        if(message.equals("Đổi mật khẩu thành công")){
                            Toast.makeText(getContext(), "Đổi mật khẩu thành công",Toast.LENGTH_SHORT).show();
                            if(getActivity() instanceof NavigationHost) {
                                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
                            }
                        }else{
                            Toast.makeText(getContext(), "Không thể thay đổi mật khẩu",Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                },error -> displayError("Không thể thay đổi mật khẩu")
        );
        requestQueue.add(jsonObjectRequest);
    }
}
