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

public class ForgotPasswordFragment extends Fragment {
    private EditText myEmail;
    private Button sendCode;

    private TextView errorMessage;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myEmail = view.findViewById(R.id.edtEmail);
        sendCode = view.findViewById(R.id.btnSubmit);
        errorMessage = view.findViewById(R.id.errorMessage);
        btnBack = view.findViewById(R.id.btnBack);

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = myEmail.getText().toString();
                if(email.isEmpty()){
                    displayError("Email is required");
                }
                else{
                    sendCodewithAPI(email);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginFragment.class);
                startActivity(intent);
            }
        });
    }
    private void displayError(String message){
        if(errorMessage != null){
            errorMessage.setText(message);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }
    private void sendCodewithAPI(String email){
        String url = getString(R.string.backend_url) + "api/nguoidung/forgotpassword";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        HashMap<String,String> params = new HashMap<>();
        params.put("email",email);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        String message = response.getString("message");
                        if(message.equals("Đã gửi mã OTP đến email")){
                            EnterOTPFragment fragment = new EnterOTPFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            fragment.setArguments(bundle);
                            ((NavigationHost) requireActivity()).navigateTo(fragment, true);
                        }else{
                            Toast.makeText(getContext(), "Email không tồn tại",Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> displayError("Email không tồn tại")
        );

        requestQueue.add(jsonObjectRequest);

    }

}
