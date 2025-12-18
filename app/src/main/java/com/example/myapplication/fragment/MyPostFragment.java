package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.MyArrayAdapter;
import com.example.myapplication.R;
import com.example.myapplication.UI.HomeActivity;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyPostFragment extends Fragment {
    public static final String TAG = "MyPostsFragment";
    private HomeFragment.OnFoodItemSelectedListener listener;
    private User user;
    private GridView gridView;
    private ArrayList<BaiDang> listPosts;
    private MyArrayAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    public static MyPostFragment newInstance(User user) {
        MyPostFragment fragment = new MyPostFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment.OnFoodItemSelectedListener) {
            listener = (HomeFragment.OnFoodItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFoodItemSelectedListener");
        }
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
        return inflater.inflate(R.layout.fragment_my_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = view.findViewById(R.id.gridUserPosts);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        listPosts = new ArrayList<>();
        adapter = new MyArrayAdapter(requireContext(), R.layout.layout_item, listPosts);
        gridView.setAdapter(adapter);
        loadSelfPosts();

        gridView.setOnItemClickListener((parent, v, position, id) -> {
            BaiDang chonBD = listPosts.get(position);
            if (listener != null) {
                listener.onFoodItemSelected(chonBD);
            }
        });
    }

    private void loadSelfPosts() {
        if (user == null) return;
        progressBar.setVisibility(View.VISIBLE);

        String url = getString(R.string.backend_url) + "api/baidang/getSelfPost/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        listPosts.clear();
                        JSONArray jsonArray = response.optJSONArray("posts");

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.optJSONObject(i);
                                if (obj == null) continue;

                                BaiDang baiDang = new BaiDang();

                                baiDang.setId(obj.optString("_id", ""));
                                baiDang.setTenMon(obj.optString("tenMon", "Món chưa đặt tên"));
                                baiDang.setImage(obj.optString("image", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setCachLam(obj.optString("cachLam", "Đang cập nhật công thức..."));
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                                ArrayList<NguyenLieu> nguyenLieuList = new ArrayList<>();
                                JSONArray nlArray = obj.optJSONArray("nguyenLieu");
                                if (nlArray != null) {
                                    for (int j = 0; j < nlArray.length(); j++) {
                                        JSONObject nlObj = nlArray.optJSONObject(j);
                                        if (nlObj != null) {
                                            String idNL = nlObj.optString("_id", "");
                                            String tenNL = nlObj.optString("ten", "");
                                            if (!tenNL.isEmpty()) {
                                                nguyenLieuList.add(new NguyenLieu(idNL, tenNL));
                                            }
                                        }
                                    }
                                }
                                baiDang.setNguyenLieu(nguyenLieuList);
                                listPosts.add(baiDang);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (listPosts.isEmpty()) {
                            if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                        } else {
                            if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Lỗi tải bài viết", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }
}