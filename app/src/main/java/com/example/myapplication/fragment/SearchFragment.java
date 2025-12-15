package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    public interface OnSearchByNameClickedListener {
        void onSearchByNameClicked(User user);
    }

    private LinearLayout btnSearch;
    private Button button, buttonTru, buttonBa;
    private TableLayout tableLayout;
    private ArrayList<BaiDang> bd;
    private User user;
    private ArrayList<NguyenLieu> al;
    private OnSearchByNameClickedListener mListener;

    public static SearchFragment newInstance(User user) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchByNameClickedListener) {
            mListener = (OnSearchByNameClickedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSearchByNameClickedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        al = new ArrayList<>();
        bd = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_food_by_ingredient, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tableLayout = view.findViewById(R.id.tablet);
        button = view.findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int emptyRowCount = 0;
                for (int i = 0; i < tableLayout.getChildCount(); i++) {
                    View child = tableLayout.getChildAt(i);
                    if (child instanceof TableRow) {
                        TableRow row = (TableRow) child;
                        EditText etTen = row.findViewById(R.id.textTen);
                        EditText etDinhLuong = row.findViewById(R.id.editDinhLuong);

                        if (etTen != null && etDinhLuong != null) {
                            boolean isTenEmpty = etTen.getText().toString().trim().isEmpty();
                            boolean isDinhLuongEmpty = etDinhLuong.getText().toString().trim().isEmpty();
                            if (isTenEmpty && isDinhLuongEmpty) {
                                emptyRowCount++;
                            }
                        }
                    }
                }

                if (emptyRowCount >= 4) {
                    Toast.makeText(requireContext(), "Đã có 4 dòng trống. Vui lòng điền thông tin trước!", Toast.LENGTH_SHORT).show();
                    return;
                }
                TableRow newRow = (TableRow) LayoutInflater.from(requireContext())
                        .inflate(R.layout.table_layout, tableLayout, false);

                EditText tv = newRow.findViewById(R.id.textTen);
                EditText et = newRow.findViewById(R.id.editDinhLuong);
                tv.setText("");
                et.setText("");

                tableLayout.addView(newRow);
            }
        });

        buttonTru = view.findViewById(R.id.buttonTru);
        buttonTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                if (rowCount > 1) {
                    tableLayout.removeViewAt(rowCount - 1);
                } else {
                    Toast.makeText(requireContext(), "Không thể xóa hết các dòng!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBa = view.findViewById(R.id.button3);
        buttonBa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                al.clear();
                TableRow firstOne = (TableRow) tableLayout.getChildAt(0);
                EditText ten = firstOne.findViewById(R.id.editTen1);
                NguyenLieu tmp = new NguyenLieu();
                tmp.setTen(ten.getText().toString());
                al.add(tmp);
                for (int i = 1; i < rowCount; i++) {
                    TableRow hehe = (TableRow) tableLayout.getChildAt(i);
                    EditText tv = hehe.findViewById(R.id.textTen);
                    NguyenLieu tmp1 = new NguyenLieu();
                    tmp1.setTen(tv.getText().toString());
                    al.add(tmp1);
                }
                searchFood(al);
            }
        });

        btnSearch = view.findViewById(R.id.barSearch);
        btnSearch.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSearchByNameClicked(user);
            }
        });
    }

    private void searchFood(ArrayList<NguyenLieu> a) {
        String url = getString(R.string.backend_url) + "api/baidang/search/ingredient";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        try {
            JSONArray nguyenLieuArray = new JSONArray();
            for (int i = 0; i < a.size(); i++) {
                JSONObject nguyenLieuObject = new JSONObject();
                nguyenLieuObject.put("ten", a.get(i).getTen());
                nguyenLieuArray.put(nguyenLieuObject);
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("nguyenLieu", nguyenLieuArray);

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
                                // ... (toàn bộ code parse JSON của bạn)
                                baiDang.setTenMon(obj.getString("tenMon"));
                                baiDang.setCachLam(obj.getString("cachLam"));
                                baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setImage(obj.optString("image", ""));

                                JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                                ArrayList<NguyenLieu> nguyenLieu = new ArrayList<>();
                                for(int j = 0; j < nlArray.length(); j++){
                                    nguyenLieu.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
                                }
                                baiDang.setNguyenLieu(nguyenLieu);
                                bd.add(baiDang);
                            }

                            if (bd.size() > 0) {
                                Intent intent = new Intent(requireContext(), FoundFoodActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("data", bd);
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), "Không tìm thấy món ăn nào phù hợp!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }) {
                public byte[] getBody() {
                    return requestBody.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };
            queue.add(jsonArrayRequest);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi tạo request", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}