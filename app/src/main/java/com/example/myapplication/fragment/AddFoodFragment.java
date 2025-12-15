package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.interfaces.NavigationHost;
import com.example.myapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddFoodFragment extends Fragment {
    private User user;
    private ArrayList<NguyenLieu> al;
    private Button btnCong;
    private Button btnTru;
    private Button btnSave;
    private EditText edtTenMon;
    private EditText edtCachLam;
    private EditText edtLinkYtb;
    private TableLayout tableLayout;
    public static AddFoodFragment newInstance(User user) {
        AddFoodFragment fragment = new AddFoodFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public AddFoodFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        return inflater.inflate(R.layout.add_food_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        al = new ArrayList<>();
        tableLayout = view.findViewById(R.id.tablet);
        btnCong = view.findViewById(R.id.buttonCo);
        btnTru = view.findViewById(R.id.buttonTru);
        btnSave = view.findViewById(R.id.btnSave);
        edtTenMon = view.findViewById(R.id.edtTenMon);
        edtCachLam = view.findViewById(R.id.edtCachLam);
        edtLinkYtb = view.findViewById(R.id.edtLinkYtb);


        btnCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                TableRow newRow = (TableRow) LayoutInflater.from(tableLayout.getContext())
                        .inflate(R.layout.table_layout, tableLayout, false);

                EditText tv = newRow.findViewById(R.id.textTen);
                EditText et = newRow.findViewById(R.id.editDinhLuong);

                tv.setText("");
                et.setText("");

                tableLayout.addView(newRow);
            }
        });
        btnTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                if(rowCount > 1) {
                    tableLayout.removeViewAt(rowCount - 1);
                }
                else {
                    Toast.makeText(requireContext(), "Không thể xóa hết các dòng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                al.clear();
                TableRow firstOne = (TableRow) tableLayout.getChildAt(0);
                EditText ten = firstOne.findViewById(R.id.editTen1);
                NguyenLieu tmp = new NguyenLieu();
                if(ten.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Tên nguyên liệu không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                tmp.setTen(ten.getText().toString());
                al.add(tmp);
                for(int i = 1; i < rowCount; i++){
                    TableRow hehe = (TableRow) tableLayout.getChildAt(i);
                    EditText tv = hehe.findViewById(R.id.textTen);

                    if (!tv.getText().toString().isEmpty()) {
                        NguyenLieu tmp1 = new NguyenLieu();
                        tmp1.setTen(tv.getText().toString());
                        al.add(tmp1);
                    }
                }
                BaiDang bd = new BaiDang();
                bd.setTenMon(edtTenMon.getText().toString());
                bd.setCachLam(edtCachLam.getText().toString());
                bd.setNguyenLieu(al);
                bd.setNguyenLieuDinhLuong(al.toString());
                bd.setLinkYtb(edtLinkYtb.getText().toString());
                bd.setLuotThich(0);
                bd.setImage("");
                addfoodpost(bd);
            }
        });



    }
    private void addfoodpost(BaiDang bd){
        String url = getString(R.string.backend_url) +"api/nguoidung/add/" + user.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("tenMon", bd.getTenMon());
            jsonBody.put("cachLam", bd.getCachLam());
            jsonBody.put("nguyenLieuDinhLuong", bd.getNguyenLieuDinhLuong());
            jsonBody.put("linkYtb", bd.getLinkYtb());
            jsonBody.put("image", bd.getImage());
            jsonBody.put("luotThich", bd.getLuotThich());

            JSONArray nguyenLieuArray = new JSONArray();
            for (NguyenLieu nl : bd.getNguyenLieu()) {
                JSONObject nguyenLieuObject = new JSONObject();
                nguyenLieuObject.put("ten", nl.getTen());
                nguyenLieuArray.put(nguyenLieuObject);
            }

            jsonBody.put("nguyenlieu",nguyenLieuArray);

        } catch(Exception e) {
            Toast.makeText(requireContext(), "Lỗi khi tạo dữ liệu JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(requireContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                            ((HomeFragment.OnFoodItemSelectedListener) requireActivity()).onFoodItemSelected(bd);
                        } else {
                            Toast.makeText(requireContext(), "Lỗi thông tin đăng kí", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonObjectRequest);

    }
}