package com.example.myapplication.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.UI.EditProfileActivity;
import com.example.myapplication.UI.MainActivity;
import com.example.myapplication.model.User;
import com.example.myapplication.R;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER = "user";

    private Button btnLogout;
    private TextView userFullname;
    private TextView userEmail;
    private LinearLayout textSettings;
    private ImageButton btnBack;
    // private LinearLayout viewPosts;
    private User user;

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
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        // viewPosts = view.findViewById(R.id.postsBtn);

        if (user != null) {
            userFullname.setText(user.getFullname());
            userEmail.setText(user.getEmail());
        } else {
            Toast.makeText(requireContext(), "Không có thông tin người dùng", Toast.LENGTH_SHORT).show();
        }

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

    }
}