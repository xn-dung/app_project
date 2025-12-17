package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.MyArrayAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public interface OnFoodItemSelectedListener {
        void onFoodItemSelected(BaiDang baiDang);
    }

    private OnFoodItemSelectedListener listener;
    private User user;
    private TextView fullname;
    private GridView gv;
    private TextView tvEmptyState;
    private ArrayList<BaiDang> listBD;
    private MyArrayAdapter myAdapter;
    private MaterialButton btnFavorite;
    private MaterialButton btnRecommend;
    private MaterialButton btnRecent;
    private MaterialButton currentSltBtn;


    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFoodItemSelectedListener) {
            listener = (OnFoodItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFoodItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gv = view.findViewById(R.id.gridFoods);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        listBD = new ArrayList<>();
        btnFavorite = view.findViewById(R.id.btnFavorite);
        btnRecommend = view.findViewById(R.id.btnRecommend);
        btnRecent = view.findViewById(R.id.btnRecent);
        fullname = view.findViewById(R.id.textFullName);
        fullname.setText(user.getFullname());
        myAdapter = new MyArrayAdapter(requireContext(), R.layout.layout_item, listBD);
        gv.setAdapter(myAdapter);

        btnRecommend.setSelected(true);
        currentSltBtn = btnRecommend;
        takeBD();

        View.OnClickListener buttonClickListener = v -> {
            if (currentSltBtn != null) {
                currentSltBtn.setSelected(false);
            }
            v.setSelected(true);
            currentSltBtn = (MaterialButton) v;

            listBD.clear();
            myAdapter.notifyDataSetChanged();
            checkEmptyState();

            if (v.getId() == R.id.btnRecommend) {
                takeBD();
            } else if (v.getId() == R.id.btnFavorite) {
                takeFAV();
            }
        };

        btnRecommend.setOnClickListener(buttonClickListener);
        btnFavorite.setOnClickListener(buttonClickListener);
        btnRecent.setOnClickListener(buttonClickListener);

        gv.setOnItemClickListener((parent, itemView, position, id) -> {
            BaiDang chonBD = listBD.get(position);
            Toast.makeText(requireContext(), "Bạn đã chọn món " + chonBD.getTenMon(), Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onFoodItemSelected(chonBD);
            }
        });
    }

    private void checkEmptyState(){
        if (listBD.isEmpty()) {
            gv.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText("Không có kết quả được tìm thấy");
        } else {
            gv.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void takeBD() {
        String url = "https://mobilenodejs.onrender.com/api/baidang";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        listBD.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            BaiDang baiDang = new BaiDang();
                            baiDang.setId(obj.getString("_id"));
                            baiDang.setTenMon(obj.getString("tenMon"));
                            baiDang.setCachLam(obj.getString("cachLam"));
                            baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                            baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                            baiDang.setLuotThich(obj.optInt("luotThich", 0));
                            baiDang.setImage(obj.optString("image", ""));
                            JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                            ArrayList<NguyenLieu> nguyenLieuList = new ArrayList<>();
                            for (int j = 0; j < nlArray.length(); j++) {
                                nguyenLieuList.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
                            }
                            baiDang.setNguyenLieu(nguyenLieuList);
                            listBD.add(baiDang);
                        }
                        myAdapter.notifyDataSetChanged();
                        checkEmptyState();
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show();
                    checkEmptyState();
                }
        );
        queue.add(jsonArrayRequest);
    }

    private void takeFAV() {
        if (user == null) {
            return;
        }
        String url = getString(R.string.backend_url) + "api/nguoidung/fav/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        listBD.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            BaiDang baiDang = new BaiDang();
                            baiDang.setId(obj.getString("_id"));
                            baiDang.setTenMon(obj.getString("tenMon"));
                            baiDang.setCachLam(obj.getString("cachLam"));
                            baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                            baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                            baiDang.setLuotThich(obj.optInt("luotThich", 0));
                            baiDang.setImage(obj.optString("image", ""));
                            JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                            ArrayList<NguyenLieu> nguyenLieuList = new ArrayList<>();
                            for (int j = 0; j < nlArray.length(); j++) {
                                nguyenLieuList.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
                            }
                            baiDang.setNguyenLieu(nguyenLieuList);
                            listBD.add(baiDang);
                        }
                        myAdapter.notifyDataSetChanged();
                        checkEmptyState();
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
        );
        queue.add(jsonArrayRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(!hidden){
            if(currentSltBtn != null && currentSltBtn.getId() == R.id.btnFavorite){
                listBD.clear();
                myAdapter.notifyDataSetChanged();
                takeFAV();
            }
        }
    }

}