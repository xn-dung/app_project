package com.example.myapplication.fragment;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.model.ShortVideo;
import com.example.myapplication.model.User;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.services.VolleyMultipartRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private TabLayout changeTab;
    private ScrollView layoutRecipe;
    private ConstraintLayout layoutReels;
    private VideoView chonVideo;
    private TextInputEditText description;
    private Button btnUpload;
    private Uri selectedVideoUri;
    private LinearLayout btnSelectVideo;

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
        changeTab = view.findViewById(R.id.tabLayout);
        layoutRecipe = view.findViewById(R.id.scrollViewContent);
        layoutReels = view.findViewById(R.id.layoutReelsForm);
        tableLayout = view.findViewById(R.id.tablet);
        btnCong = view.findViewById(R.id.buttonCo);
        btnTru = view.findViewById(R.id.buttonTru);
        btnSave = view.findViewById(R.id.btnSave);
        edtTenMon = view.findViewById(R.id.edtTenMon);
        edtCachLam = view.findViewById(R.id.edtCachLam);
        edtLinkYtb = view.findViewById(R.id.edtLinkYtb);
        chonVideo = view.findViewById(R.id.videoPreview);
        description = view.findViewById(R.id.edtReelCaption);
        btnUpload = view.findViewById(R.id.btnPostReel);
        btnSelectVideo = view.findViewById(R.id.btnSelectVideo);

        changeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    layoutRecipe.setVisibility(View.VISIBLE);
                    layoutReels.setVisibility(View.GONE);
                }
                else {
                    layoutRecipe.setVisibility(View.GONE);
                    layoutReels.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        btnCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                TableRow newRow = (TableRow) LayoutInflater.from(tableLayout.getContext())
                        .inflate(R.layout.table_layout, tableLayout, false);

                EditText tv = newRow.findViewById(R.id.textTen);
                EditText et = newRow.findViewById(R.id.editDinhLuong);
                Button btnRemove = newRow.findViewById(R.id.btnRemoveRow1);


                btnRemove.setOnClickListener(v2 -> {
                    if (tableLayout.getChildCount() > 1) {
                        tableLayout.removeView((View) v2.getParent());
                    } else {
                        Toast.makeText(requireContext(),
                                "Không thể xóa hết các dòng!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

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
        btnSelectVideo.setOnClickListener(v -> {
            openVideoPicker();
        });

        btnUpload.setOnClickListener(v -> {
            String descriptionText = description.getText().toString().trim();
            addReel(descriptionText);
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
    private byte[] readBytesFromUri(Uri uri) throws Exception {
        InputStream inputStream =
                requireContext().getContentResolver().openInputStream(uri);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;

        while ((nRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    private void addReel(String descr) {
        if (selectedVideoUri == null) {
            Toast.makeText(getContext(), "Chưa chọn video", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = getString(R.string.backend_url) + "api/reel/upload";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        Map<String, VolleyMultipartRequest.DataPart> fileMap = new HashMap<>();
        Map<String, String> textMap = new HashMap<>();

        try {
            byte[] videoBytes = readBytesFromUri(selectedVideoUri);
            fileMap.put("video", new VolleyMultipartRequest.DataPart(
                    "video.mp4",
                    videoBytes,
                    "video/mp4"
            ));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi đọc video", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Xử lý danh sách nguyên liệu thành chuỗi giống Postman
        StringBuilder nlBuilder = new StringBuilder();
        if (al != null) {
            for (int i = 0; i < al.size(); i++) {
                nlBuilder.append(al.get(i).getTen());
                if (i < al.size() - 1) nlBuilder.append(", ");
            }
        }

        // 2. Đổ dữ liệu vào textMap khớp chính xác với ảnh Postman
        textMap.put("userId", String.valueOf(user.getId()));
        textMap.put("tieude", "hehe");
        textMap.put("description", descr);
        textMap.put("tags", "food, recipe"); // Bạn có thể thêm tag tùy ý
        textMap.put("nguyenLieu", ""); // Chuỗi sạch: "Thịt lợn, Bắp cải"

        // 3. Gọi VolleyMultipartRequest với Constructor MỚI (6 tham số)
        VolleyMultipartRequest request = new VolleyMultipartRequest(
                Request.Method.POST,
                url,
                textMap,      // params (MỚI)
                fileMap,      // byteData
                response -> Toast.makeText(getContext(), "Upload thành công", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getContext(), "Lỗi: " + error.toString(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }



    private final ActivityResultLauncher<Intent> videoPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            selectedVideoUri = result.getData().getData();

                            chonVideo.setVisibility(View.VISIBLE);
                            chonVideo.setVideoURI(selectedVideoUri);
                            chonVideo.start();

                            btnSelectVideo.setVisibility(View.GONE);
                        }
                    }
            );
    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        videoPickerLauncher.launch(intent);
    }

}