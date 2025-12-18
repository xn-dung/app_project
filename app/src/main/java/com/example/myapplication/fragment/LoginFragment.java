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

public class LoginFragment extends Fragment {

    private EditText myUsername;
    private EditText myPassword;
    private Button myButton;
    private TextView errorMessage;
    private User user;
    private TextView registerButton;
    private TextView forgotPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myUsername = view.findViewById(R.id.username);
        myPassword  = view.findViewById(R.id.password);
        myButton = view.findViewById(R.id.btnLogin);
        registerButton = view.findViewById(R.id.textViewLinkRegister);
        errorMessage = view.findViewById(R.id.errorMessage);
        forgotPassword = view.findViewById(R.id.textForgotPassword);


        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = myUsername.getText().toString();
                String password = myPassword.getText().toString();
                if(username.isEmpty())
                {
                    displayError("Username is required");
                }
                else if(password.isEmpty()){
                    displayError("Password is required");
                }
                else{
                    loginWithAPI(username,password);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof NavigationHost) {
                    ((NavigationHost) requireActivity()).navigateTo(new RegisterFragment(), true);
                }
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof NavigationHost) {
                    ((NavigationHost) requireActivity()).navigateTo(new ForgotPasswordFragment(), true);
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

    private void loginWithAPI(String username, String password){
        String url = getString(R.string.backend_url) + "api/nguoidung/login";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        HashMap<String,String> params = new HashMap<>();
        params.put("username",username);
        params.put("password",password);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String id = response.getString("_id");
                        String name = response.getString("name");
                        String address = response.getString("address");
                        String email = response.getString("email");
                        String phone = response.getString("phone");
                        String userName = response.getString("username");
//                        String passWord = response.getString("password");
                        User logginUser= new User(id, userName,password,name,address,email,phone);
                        if(response.has("avatar")){
                            logginUser.setAvatar(response.getString("avatar"));
                        }


                        if (!logginUser.getFullname().isEmpty()) {
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.putExtra("user",logginUser);
                            startActivity(intent);
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        } else {
                            Toast.makeText(getContext(),"Sai tài khoản hoặc mật khẩu",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(),"Lỗi parse JSON: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                },
                error -> displayError("Sai tài khoản hoặc mật khẩu")
        );

        requestQueue.add(jsonObjectRequest);
    }
}