package com.example.myapplication.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.UI.HomeActivity;
import com.example.myapplication.interfaces.NavigationHost;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import com.example.myapplication.services.VolleyMultipartRequest;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {
    public interface OnSearchByNameClickedListener {
        void onSearchByNameClicked(User user);
    }

    private LinearLayout btnSearch;
    private Button button, buttonTru, buttonBa, photos, buttonRow;
    private TableLayout tableLayout;
    private ArrayList<BaiDang> bd;
    private User user;
    private ArrayList<NguyenLieu> al;
    private OnSearchByNameClickedListener mListener;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri imageUri;



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

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            uploadImageToBackend(imageUri);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        photos = view.findViewById(R.id.buttonPhotos);
        photos.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                openCamera();
            }
        });

        button.setOnClickListener(v -> {
            int emptyRowCount = 0;

            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                View child = tableLayout.getChildAt(i);
                if (child instanceof TableRow) {
                    TableRow row = (TableRow) child;
                    EditText etTen = row.findViewById(R.id.textTen);
                    EditText etSoLuong = row.findViewById(R.id.editDinhLuong);

                    if (etTen != null && etSoLuong != null) {
                        if (etTen.getText().toString().trim().isEmpty()
                                && etSoLuong.getText().toString().trim().isEmpty()) {
                            emptyRowCount++;
                        }
                    }
                }
            }

            if (emptyRowCount >= 4) {
                Toast.makeText(requireContext(),
                        "Đã có 4 dòng trống. Vui lòng điền thông tin trước!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            TableRow newRow = (TableRow) LayoutInflater.from(requireContext())
                    .inflate(R.layout.table_layout, tableLayout, false);

            EditText etTen = newRow.findViewById(R.id.textTen);
            EditText etSoLuong = newRow.findViewById(R.id.editDinhLuong);
            Button btnRemove = newRow.findViewById(R.id.btnRemoveRow1);

            etTen.setText("");
            etSoLuong.setText("");

            btnRemove.setOnClickListener(v2 -> {
                if (tableLayout.getChildCount() > 1) {
                    tableLayout.removeView((View) v2.getParent());
                } else {
                    Toast.makeText(requireContext(),
                            "Không thể xóa hết các dòng!",
                            Toast.LENGTH_SHORT).show();
                }
            });

            tableLayout.addView(newRow);
        });

        buttonTru = view.findViewById(R.id.buttonTru);
        buttonTru.setOnClickListener(v -> {
            int rowCount = tableLayout.getChildCount();
            if (rowCount > 1) {
                tableLayout.removeViewAt(rowCount - 1);
            } else {
                Toast.makeText(requireContext(),
                        "Không thể xóa hết các dòng!",
                        Toast.LENGTH_SHORT).show();
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
                    View child = tableLayout.getChildAt(i);

                    if (child instanceof TableRow) {
                        TableRow row = (TableRow) child;
                        EditText etTen = row.findViewById(R.id.textTen);

                        if (etTen != null) {
                            String tenNL = etTen.getText().toString().trim();
                            if (!tenNL.isEmpty()) {
                                NguyenLieu nl = new NguyenLieu();
                                nl.setTen(tenNL);
                                al.add(nl);
                            }
                        }
                    }
                }

                if (al.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập nguyên liệu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                searchFood(al);
            }
        });

        btnSearch = view.findViewById(R.id.barSearch);
        btnSearch.setOnClickListener(v -> {
            if (mListener != null) {
                if (user != null) {
                    mListener.onSearchByNameClicked(user);
                } else {
                    Toast.makeText(requireContext(), "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
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
                                baiDang.setTenMon(obj.getString("tenMon"));
                                baiDang.setCachLam(obj.getString("cachLam"));
                                baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setImage(obj.optString("image", ""));

                                JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                                ArrayList<NguyenLieu> nguyenLieu = new ArrayList<>();
                                for(int j = 0; j < nlArray.length(); j++){
                                    nguyenLieu.add(new NguyenLieu(nlArray.getJSONObject(j).getString("ten")));
                                }
                                baiDang.setNguyenLieu(nguyenLieu);
                                bd.add(baiDang);
                            }
                            if (bd.size() > 0) {
                                ((HomeActivity) requireActivity()).openFoundFoodFragment(bd, user, 1);
                            }
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
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
           e.printStackTrace();
        }
    }
    private void openCamera() {
        File photoFile = new File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "photo_" + System.currentTimeMillis() + ".jpg"
        );

        imageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                photoFile
        );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cameraLauncher.launch(intent);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream inputStream =
                requireContext().getContentResolver().openInputStream(uri);

        if (inputStream == null) {
            throw new IOException("Không mở được InputStream");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;

        while ((nRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }

        inputStream.close();
        return buffer.toByteArray();
    }
    private void uploadImageToBackend(Uri imageUri) {
        try {
            Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
            byteData.put("image", new VolleyMultipartRequest.DataPart(
                    "photo.jpg",
                    getBytesFromUri(imageUri),
                    "image/jpeg"
            ));

            String url = getString(R.string.backend_url) + "api/image/detect";

            VolleyMultipartRequest request = new VolleyMultipartRequest(
                    Request.Method.POST,
                    url,
                    byteData,
                    response -> {
                        try {
                            JSONArray nguyenLieuArray = response.optJSONArray("nguyenLieu");
                            if (nguyenLieuArray == null || nguyenLieuArray.length() == 0) {
                                Toast.makeText(requireContext(), "Không tìm thấy món ăn nào phù hợp!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ArrayList<NguyenLieu> list = new ArrayList<>();
                            for (int i = 0; i < nguyenLieuArray.length(); i++) {
                                JSONObject obj = nguyenLieuArray.getJSONObject(i);
                                NguyenLieu nl = new NguyenLieu();
                                nl.setTen(obj.optString("ten"));
                                list.add(nl);
                            }
                            fillTableWithNguyenLieu(list);
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                    }
            );
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            Volley.newRequestQueue(requireContext()).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fillTableWithNguyenLieu(ArrayList<NguyenLieu> list) {
        int ingredientIndex = 0;

        while (ingredientIndex < list.size()) {
            if(ingredientIndex == 0){
                TableRow firstRow = (TableRow) tableLayout.getChildAt(0);
                EditText etTen = firstRow.findViewById(R.id.editTen1);
                EditText etSoLuong = firstRow.findViewById(R.id.editSoLuong1);
                Button btnXoa = firstRow.findViewById(R.id.btnRemoveRow);
                etTen.setText(list.get(ingredientIndex).getTen());
                etSoLuong.setText("");
                btnXoa.setOnClickListener(v -> {
                    Toast.makeText(requireContext(),
                            "Không thể xóa hết các dòng!",
                            Toast.LENGTH_SHORT).show();

                });

            }
            else {
                TableRow newRow = (TableRow) LayoutInflater.from(requireContext())
                        .inflate(R.layout.table_layout, tableLayout, false);

                EditText etTen = newRow.findViewById(R.id.textTen);
                EditText etSoLuong = newRow.findViewById(R.id.editDinhLuong);
                Button btnXoa = newRow.findViewById(R.id.btnRemoveRow1);

                etTen.setText(list.get(ingredientIndex).getTen());
                etSoLuong.setText("");

                btnXoa.setOnClickListener(v -> {
                    if (tableLayout.getChildCount() > 1) {
                        tableLayout.removeView(newRow);
                    } else {
                        Toast.makeText(requireContext(),
                                "Không thể xóa hết các dòng!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                tableLayout.addView(newRow);
            }
            ingredientIndex++;
        }
    }




}