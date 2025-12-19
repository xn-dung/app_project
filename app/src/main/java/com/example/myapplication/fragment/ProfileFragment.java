package com.example.myapplication.fragment;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.UI.EditProfileActivity;
import com.example.myapplication.UI.MainActivity;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import com.example.myapplication.R;
import com.example.myapplication.services.VolleyMultipartRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER = "user";

    private MaterialButton btnLogout;
    private TextView userFullname;
    private TextView userEmail;
    private LinearLayout textSettings;
    private ImageButton btnBack;
    private TextView tvPostNum, tvFavNum, tvRateNum;
    private LinearLayout viewPosts;
    private User user;
    private LinearLayout btnReels;

    private ImageView img;


    public static ProfileFragment newInstance(User user) {
        ProfileFragment f = new ProfileFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user);
        f.setArguments(b);
        return f;
    }

    public ProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            user = (User) args.getSerializable(ARG_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);
        userFullname = view.findViewById(R.id.userFullName);
        userEmail = view.findViewById(R.id.userEmail);
        textSettings = view.findViewById(R.id.editProfileBtn);
        btnBack = view.findViewById(R.id.btnBack);
        tvPostNum = view.findViewById(R.id.postNum);
        tvFavNum = view.findViewById(R.id.favNum);
        btnReels = view.findViewById(R.id.reelsBtn);
        tvRateNum = view.findViewById(R.id.rateNum);
        viewPosts = view.findViewById(R.id.postsBtn);
        img = view.findViewById(R.id.imageView5);
        Glide.with(requireContext())
                .load(user.getAvatar())
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(img);
        img.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });
        if (user != null) {
            userFullname.setText(user.getFullname());
            userEmail.setText(user.getEmail());
            loadUserStats();
        } else {
            Toast.makeText(requireContext(), "Không có thông tin người dùng", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        textSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            intent.putExtra(ARG_USER, user);
            startActivity(intent);
        });

        viewPosts.setOnClickListener(v -> {
            MyPostFragment fragment = MyPostFragment.newInstance(user);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .hide(ProfileFragment.this)
                    .add(R.id.fragment_container, fragment, MyPostFragment.TAG)
                    .addToBackStack(MyPostFragment.TAG)
                    .commit();
        });

        btnReels.setOnClickListener(v -> {
            if (user == null) return;
            UserReelFragment reelFragment = UserReelFragment.newInstance(user);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .hide(ProfileFragment.this)
                    .add(R.id.fragment_container, reelFragment, "UserReelFragment")
                    .addToBackStack("UserReelFragment")
                    .commit();
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri imageUri = data.getData();

            uploadImageToBackend(imageUri);
        }
    }
    private void loadUserStats(){
        if(user == null) return;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String backendUrl = getString(R.string.backend_url);

        String selfPostUrl = backendUrl + "api/baidang/getSelfPost/" + user.getId();

        JsonObjectRequest selfPostRequest = new JsonObjectRequest(Request.Method.GET, selfPostUrl, null,
                response -> {
                    int postNums = response.optInt("postNums", 0);
                    int tongLike = response.optInt("tongLike", 0);

                    tvPostNum.setText(String.valueOf(postNums));
                    tvRateNum.setText(String.valueOf(tongLike));
                },
                error -> {
                    tvPostNum.setText("0");
                    tvRateNum.setText("0");
                }
        );

        String favUrl = backendUrl + "api/nguoidung/fav/" + user.getId();

        JsonArrayRequest favRequest = new JsonArrayRequest(Request.Method.GET, favUrl, null,
                response -> tvFavNum.setText(String.valueOf(response.length())),
                error -> tvFavNum.setText("0")
        );

        queue.add(selfPostRequest);
        queue.add(favRequest);
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(!hidden){
            loadUserStats();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserStats();
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (inputStream == null) throw new IOException();

            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    private void uploadImageToBackend(Uri imageUri) {
        try {
            Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
            byteData.put("image", new VolleyMultipartRequest.DataPart(
                    "avatar.jpg",
                    getBytesFromUri(imageUri),
                    "image/jpeg"
            ));

            String url = getString(R.string.backend_url) + "api/nguoidung/avatar/" + user.getId();

            VolleyMultipartRequest request = new VolleyMultipartRequest(
                    Request.Method.POST,
                    url,
                    byteData,
                    response -> {
                        try {
                            if(response.has("avatar")){
                                String avatar = response.getString("avatar");
                                user.setAvatar(avatar);
                                Glide.with(requireContext())
                                        .load(avatar)
                                        .placeholder(R.drawable.img)
                                        .error(R.drawable.img)
                                        .into(img);
                            }
                        } catch (Exception e) {
                            Glide.with(requireContext())
                                    .load(user.getAvatar())
                                    .placeholder(R.drawable.img)
                                    .error(R.drawable.img)
                                    .into(img);
                        }
                    },
                    error ->{
                        error.printStackTrace();
                    }
            );

            Volley.newRequestQueue(requireContext()).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}