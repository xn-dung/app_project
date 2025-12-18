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
    private GridView gridView;
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

        gridView = view.findViewById(R.id.gridUserReels);
        progressBar = view.findViewById(R.id.progressBarReel);
        layoutEmpty = view.findViewById(R.id.layoutEmptyReel);

        View btnBack = view.findViewById(R.id.btnBack);
        if(btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        listReels = new ArrayList<>();
        adapter = new UserReelAdapter(requireContext(), listReels);
        gridView.setAdapter(adapter);

        if (user != null) {
            loadSelfReels();
        } else {
            Toast.makeText(getContext(), "Lỗi thông tin User", Toast.LENGTH_SHORT).show();
        }

        // Sự kiện click vào item
        gridView.setOnItemClickListener((parent, v, position, id) -> {
            // Chuyển sang màn hình xem chi tiết (ReelFragment full màn hình)
            // ((HomeActivity) requireActivity()).openReelDetail(listReels, position);
            Toast.makeText(getContext(), "Click video " + position, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSelfReels() {
        progressBar.setVisibility(View.VISIBLE);

        // URL API
        String url = getString(R.string.backend_url) + "api/reel/getSelfReels/" + user.getId();
        Log.d("API_REEL", "Calling: " + url); // Log để check link

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        listReels.clear();

                        // JSON bạn gửi có dạng: { "reels": [...] }
                        JSONArray jsonArray = response.optJSONArray("reels");

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.optJSONObject(i);
                                if (obj == null) continue;

                                ShortVideo video = new ShortVideo();
                                video.setId(obj.optString("_id"));

                                // QUAN TRỌNG: API của bạn đang thiếu các trường này
                                // Nên mình để giá trị mặc định để app không bị crash
                                video.setUrl(obj.optString("videoUrl", "")); // Link video để Glide load thumbnail
                                video.setTieuDe(obj.optString("tieude", ""));
                                video.setViews(obj.optInt("views", 0));

                                listReels.add(video);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        // Check trống
                        if (listReels.isEmpty()) {
                            if(layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                        } else {
                            if(layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                        }

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