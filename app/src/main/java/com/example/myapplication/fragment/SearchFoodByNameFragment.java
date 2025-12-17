package com.example.myapplication.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.UI.HomeActivity;
import com.example.myapplication.model.User;

import com.example.myapplication.R;

public class SearchFoodByNameFragment extends Fragment{
    private SearchView sv;
    public static final String TAG = "SearchFoodByNameFragment";
    private User user;
    private Button btn;
    public static SearchFoodByNameFragment newInstance(User user) {
        SearchFoodByNameFragment fragment = new SearchFoodByNameFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        return inflater.inflate(R.layout.search_food_byname, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sv = view.findViewById(R.id.searchname);
        btn = view.findViewById(R.id.startsearchbyname);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = sv.getQuery().toString();
                if (!search.isEmpty() && user != null) {
                    ((HomeActivity) requireActivity()).linkFoundFoodFragment(search, user, 2);
                } else if (user == null) {
                    Toast.makeText(getContext(), "User null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
