package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.UserReelAdapter; // Import Adapter mới tạo
import com.example.myapplication.R;
import com.example.myapplication.model.ShortVideo;
import com.example.myapplication.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserReelFragment extends Fragment {

    private User user;
    private RecyclerView recyclerView;
    private ArrayList<ShortVideo> listReels;
    private UserReelAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;

    public static UserReelFragment newInstance(User user) {
        UserReelFragment fragment = new UserReelFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_reel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerUserReels);
        progressBar = view.findViewById(R.id.progressBarReel);
        layoutEmpty = view.findViewById(R.id.layoutEmptyReel);

        View btnBack = view.findViewById(R.id.btnBack);
        if(btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        listReels = new ArrayList<>();
        adapter = new UserReelAdapter(requireContext(), listReels, (video, position) -> {
            // Xử lý khi bấm vào video
            if (user == null) return;

            // Mở màn hình xem chi tiết (Code logic cũ của bạn)
            ReelFragment detailFragment = ReelFragment.newInstanceWithData(user, listReels, position);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, detailFragment)
                    .hide(UserReelFragment.this)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        if (user != null) {
            loadSelfReels();
        } else {
            Toast.makeText(getContext(), "Lỗi thông tin User", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSelfReels() {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.backend_url) + "api/reel/getSelfReels/" + user.getId();

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        listReels.clear();
                        JSONArray jsonArray = response.optJSONArray("reels");

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.optJSONObject(i);
                                if (obj == null) continue;

                                ShortVideo video = new ShortVideo();
                                video.setId(obj.optString("_id"));
                                video.setUrl(obj.optString("videoUrl", "")); // Link video để Glide load thumbnail
                                video.setDescription(obj.optString("description", ""));
                                video.setViews(obj.optInt("views", 0));
                                video.setLikes(obj.optInt("likes", 0));
                                video.setAuthor(user.getFullname());
                                video.setIdAuthor(user.getId());
                                listReels.add(video);
                            }
                        }

                        adapter.notifyDataSetChanged();


                    } catch (Exception e) {
                        Log.e("API_REEL", "Error parsing: " + e.getMessage());
                        Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("API_REEL", "Volley Error: " + error.toString());
                    Toast.makeText(requireContext(), "Lỗi tải danh sách", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }
}