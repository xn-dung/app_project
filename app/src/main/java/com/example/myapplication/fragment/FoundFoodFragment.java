package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoundFoodFragment extends Fragment {

    public static final String TAG = "FoundFoodFragment";
    private static final String ARG_TYPE = "type";
    private static final String ARG_DATA_BAIDANG = "data_baidang";
    private static final String ARG_SEARCH_QUERY = "search_query";
    private static final String ARG_USER = "user";

    private int type;
    private String foodName;
    private User user;
    private ArrayList<BaiDang> bd;
    private ArrayList<NguyenLieu> nguyenLieu;

    private ListView lv;
    private FoundFoodAdapter myadapter;
    private TextView texname;

    public interface OnFoodItemSelectedListener {
        void onFoodItemSelected(BaiDang baiDang, User user);
    }

    private OnFoodItemSelectedListener listener;

    public static FoundFoodFragment newInstance(int type, ArrayList<BaiDang> data, User user) {
        FoundFoodFragment fragment = new FoundFoodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putSerializable(ARG_DATA_BAIDANG, data);
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public static FoundFoodFragment newInstance(String searchQuery, User user) {
        FoundFoodFragment fragment = new FoundFoodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, 0);
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public FoundFoodFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFoodItemSelectedListener) {
            listener = (OnFoodItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFoodItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            user = (User) getArguments().getSerializable(ARG_USER);
            if (type == 1) {
                bd = (ArrayList<BaiDang>) getArguments().getSerializable(ARG_DATA_BAIDANG);
            } else {
                foodName = getArguments().getString(ARG_SEARCH_QUERY);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_food, container, false);

        lv = view.findViewById(R.id.outfood);
        texname = view.findViewById(R.id.textFullName);
        bd = bd != null ? bd : new ArrayList<>();

        if(user != null) {
            texname.setText(user.getFullname());
        }

        myadapter = new FoundFoodAdapter(R.layout.food_card_home, requireContext() , bd);
        lv.setAdapter(myadapter);

        if(type == 0 && foodName != null && !foodName.isEmpty()){
            searchFood(foodName);
        } else if (type == 1 && bd != null && !bd.isEmpty()) {
            myadapter.notifyDataSetChanged();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaiDang chonBD = bd.get(position);
                Toast.makeText(requireContext(), "Bạn đã chọn món " + chonBD.getTenMon(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onFoodItemSelected(chonBD, user);
                }
            }
        });

        return view;
    }

    private void searchFood(String foodName) {
        String url = getString(R.string.backend_url) + "api/baidang/search";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("tenMon", foodName);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
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
                                baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setImage(obj.optString("image", ""));
                                JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                                nguyenLieu = new ArrayList<>();
                                for(int j = 0; j < nlArray.length(); j++){
                                    nguyenLieu.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
                                }
                                baiDang.setNguyenLieu(nguyenLieu);

                                bd.add(baiDang);
                            }
                            myadapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Tìm thấy " + bd.size() + " món", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(requireContext(), "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
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

            queue.add(jsonArrayRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi tạo request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}