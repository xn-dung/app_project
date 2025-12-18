package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.FoundFoodAdapter;
import com.example.myapplication.R;
import com.example.myapplication.interfaces.NavigationHost;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoundFoodFragment extends Fragment {

    private ArrayList<BaiDang> bd;
    private ArrayList<NguyenLieu> nguyenLieu;
    private ListView lv;
    private FoundFoodAdapter myadapter;
    private TextView textname;
    private User user;
    private int type;
    public static FoundFoodFragment newInstance(ArrayList<BaiDang> bd,User user,int type) {
        FoundFoodFragment fragment = new FoundFoodFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        args.putSerializable("data",bd);
        args.putInt("type",type);
        fragment.setArguments(args);
        return fragment;
    }
    public static FoundFoodFragment newInstance2(String search,User user,int type) {
        FoundFoodFragment fragment = new FoundFoodFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        args.putString("search",search);
        args.putInt("type",type);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            type = getArguments().getInt("type", 0);
        }
        return inflater.inflate(R.layout.found_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.outfood);



        bd = new ArrayList<>();
        myadapter = new FoundFoodAdapter(R.layout.food_card_home, requireContext(), bd);
        lv.setAdapter(myadapter);

        Bundle args = getArguments();
        if (args == null) return;

        if (type == 1) {
            ArrayList<BaiDang> temp =
                    (ArrayList<BaiDang>) args.getSerializable("data");
            if (temp != null && !temp.isEmpty()) {
                bd.addAll(temp);
            }
            setupAdapter();
        } else {
            String foodName = args.getString("search");
            if (foodName != null && !foodName.isEmpty()) {
                searchFood(foodName);
            }
        }

        lv.setOnItemClickListener((parent, v, position, id) -> {
            BaiDang chonBD = bd.get(position);

            DetailFoodFragment fragment = new DetailFoodFragment();
            Bundle b = new Bundle();
            b.putSerializable("baiDang", chonBD);
            b.putSerializable("user", user);
            fragment.setArguments(b);

            ((NavigationHost) requireActivity())
                    .navigateTo(fragment, true);
        });
        myadapter.notifyDataSetChanged();
    }
    private void setupAdapter() {
        if (myadapter == null) {
            myadapter = new FoundFoodAdapter(R.layout.food_card_home, requireContext(), bd);
            lv.setAdapter(myadapter);
        } else {
            myadapter.notifyDataSetChanged();
        }
    }
    private void searchFood(String foodName) {
        String url = getString(R.string.backend_url) + "api/baidang/search";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("tenMon", foodName);

            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.POST,
                    url,
                    null,
                    response -> {
                        try {
                            bd.clear();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                BaiDang baiDang = new BaiDang();
                                baiDang.setId(obj.getString("_id"));
                                baiDang.setTenMon(obj.getString("tenMon"));
                                baiDang.setCachLam(obj.getString("cachLam"));
                                baiDang.setNguyenLieuDinhLuong(
                                        obj.optString("nguyenLieuDinhLuong", "")
                                );
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setImage(obj.optString("image", ""));
                                baiDang.setViews(obj.optInt("views",0));
                                JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                                nguyenLieu = new ArrayList<>();
                                for (int j = 0; j < nlArray.length(); j++) {
                                    JSONObject nlObj = nlArray.getJSONObject(j);
                                    nguyenLieu.add(
                                            new NguyenLieu(
                                                    nlObj.getString("ten")
                                            )
                                    );
                                }
                                baiDang.setNguyenLieu(nguyenLieu);
                                bd.add(baiDang);
                            }

                            setupAdapter();
                            Toast.makeText(
                                    requireContext(),
                                    "Tìm thấy " + bd.size() + " món",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } catch (Exception e) {
                            Toast.makeText(
                                    requireContext(),
                                    "Lỗi xử lý dữ liệu",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    },
                    error -> Toast.makeText(
                            requireContext(),
                            "Lỗi kết nối",
                            Toast.LENGTH_SHORT
                    ).show()
            ) {
                @Override
                public byte[] getBody() {
                    return jsonBody.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            queue.add(request);

        } catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    "Lỗi tạo request",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}