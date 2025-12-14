package com.example.myapplication.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class EnterOTPFragment extends Fragment {
    private EditText myOTP;
    private Button btnSubmit;
    private TextView errorMessage;
    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            email = getArguments().getString("email");
        }
        return inflater.inflate(R.layout.enter_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myOTP = view.findViewById(R.id.edtOTP);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        errorMessage = view.findViewById(R.id.errorMessage);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = myOTP.getText().toString();
                if(otp.isEmpty()){
                    displayError("OTP is required");
                }
                else{
                    checkOTPwithAPI(otp);
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

    private void checkOTPwithAPI(String otp) {
        String url = R.string.backend_url + "api/nguoidung/checkOTP";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        HashMap<String,String> params = new HashMap<>();

        params.put("email",email);
        params.put("otp",otp);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        String message = response.getString("message");
                        if(message.equals("Xác nhận OTP thành công")){
                            ResetPasswordFragment fragment = new ResetPasswordFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            fragment.setArguments(bundle);
                            ((NavigationHost) requireActivity()).navigateTo(fragment, true);

                        }else{
                            Toast.makeText(getContext(), "OTP không hợp lệ",Toast.LENGTH_SHORT).show();
                        }

                    }catch(Exception e){
                        Toast.makeText(getContext(), "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> displayError("OTP không hợp lệ")
        );

        requestQueue.add(jsonObjectRequest);
    }

}
