package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.UI.HomeActivity;
import com.example.myapplication.model.User;
import com.example.myapplication.R;

import java.util.ArrayList;

public class SearchFoodByNameFragment extends Fragment {
    private SearchView sv;
    public static final String TAG = "SearchFoodByNameFragment";
    private User user;
    private Button btn;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> searchHistory;

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
        list = view.findViewById(R.id.outfood);
        searchHistory = loadSearchHistory();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, searchHistory);
        list.setAdapter(adapter);

        btn.setOnClickListener(v -> {
            String search = sv.getQuery().toString();
            if (!search.isEmpty() && user != null) {
                addSearchHistory(search);
                ((HomeActivity) requireActivity()).linkFoundFoodFragment(search, user, 2);
            } else if (user == null) {
                Toast.makeText(getContext(), "User null", Toast.LENGTH_SHORT).show();
            }
        });

        list.setOnItemClickListener((parent, view1, position, id) -> {
            String keyword = searchHistory.get(position);
            sv.setQuery(keyword, false);
            ((HomeActivity) requireActivity()).linkFoundFoodFragment(keyword, user, 2);
            addSearchHistory(keyword);
        });
    }

    private void addSearchHistory(String keyword) {
        if (searchHistory.contains(keyword)) searchHistory.remove(keyword);
        searchHistory.add(0, keyword);
        adapter.notifyDataSetChanged();

        StringBuilder sb = new StringBuilder();
        for (String s : searchHistory) {
            sb.append(s).append(",");
        }
        requireContext().getSharedPreferences("search_prefs", 0)
                .edit()
                .putString("history", sb.toString())
                .apply();
    }

    private ArrayList<String> loadSearchHistory() {
        String saved = requireContext().getSharedPreferences("search_prefs", 0)
                .getString("history", "");
        ArrayList<String> list = new ArrayList<>();
        if (!saved.isEmpty()) {
            String[] arr = saved.split(",");
            for (String s : arr) list.add(s);
        }
        return list;
    }
}
