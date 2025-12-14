//package com.example.myapplication.UI;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.DownloadManager;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.myapplication.R;
//import com.example.myapplication.model.NguyenLieu;
//import com.example.myapplication.model.BaiDang;
//import com.example.myapplication.model.User;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.*;
//import android.util.Base64;
//
//public class SearchActivity extends AppCompatActivity {
//
//    private LinearLayout btnSearch;
//    Button button;
//    Button buttonTru;
//    Button buttonBa;
//    TableLayout tableLayout;
//    ArrayList<BaiDang> bd;
//    private User user;
//    ArrayList<NguyenLieu> al;
//
//    Button photos;
//    private ActivityResultLauncher<Intent> cameraLauncher;
//    private Uri imageUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.search_food_by_ingredient);
//        cameraLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                            uploadImageToServer(bitmap);
//                        } catch (Exception e) {
//                            Toast.makeText(this, "Lỗi đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(this, "Không nhận được ảnh", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        user = (User) getIntent().getSerializableExtra("user");
//
//        tableLayout = findViewById(R.id.tablet);
//        button = findViewById(R.id.button2);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int emptyRowCount = 0;
//                for (int i = 0; i < tableLayout.getChildCount(); i++) {
//                    View child = tableLayout.getChildAt(i);
//
//                    if (child instanceof TableRow) {
//                        TableRow row = (TableRow) child;
//
//                        EditText etTen = row.findViewById(R.id.textTen);
//                        EditText etDinhLuong = row.findViewById(R.id.editDinhLuong);
//
//                        if (etTen != null && etDinhLuong != null) {
//                            boolean isTenEmpty = etTen.getText().toString().trim().isEmpty();
//                            boolean isDinhLuongEmpty = etDinhLuong.getText().toString().trim().isEmpty();
//                            if (isTenEmpty && isDinhLuongEmpty) {
//                                emptyRowCount++;
//                            }
//                        }
//                    }
//                }
//
//                if (emptyRowCount >= 4) {
//                    Toast.makeText(SearchActivity.this, "Đã có 4 dòng trống. Vui lòng điền thông tin trước!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                TableRow newRow = (TableRow) LayoutInflater.from(SearchActivity.this)
//                        .inflate(R.layout.table_layout, tableLayout, false);
//
//                EditText tv = newRow.findViewById(R.id.textTen);
//                EditText et = newRow.findViewById(R.id.editDinhLuong);
//
//                tv.setText("");
//                et.setText("");
//
//                tableLayout.addView(newRow);
//            }
//        });
//
//        buttonTru = findViewById(R.id.buttonTru);
//        buttonTru.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int rowCount = tableLayout.getChildCount();
//                if(rowCount > 1) {
//                    tableLayout.removeViewAt(rowCount - 1);
//                }
//                else {
//                    Toast.makeText(SearchActivity.this, "Không thể xóa hết các dòng!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        al = new ArrayList<>();
//        bd = new ArrayList<>();
//        buttonBa = findViewById(R.id.button3);
//        buttonBa.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                int rowCount = tableLayout.getChildCount();
//                al.clear();
//                TableRow firstOne = (TableRow) tableLayout.getChildAt(0);
//                EditText ten = firstOne.findViewById(R.id.editTen1);
//                NguyenLieu tmp = new NguyenLieu();
//                tmp.setTen(ten.getText().toString());
//                al.add(tmp);
//                for(int i = 1; i < rowCount; i++){
//                    TableRow hehe = (TableRow) tableLayout.getChildAt(i);
//                    EditText tv = hehe.findViewById(R.id.textTen);
//
//                    NguyenLieu tmp1 = new NguyenLieu();
//                    tmp1.setTen(tv.getText().toString());
//                    al.add(tmp1);
//                }
//                searchFood(al);
//            }
//        });
//
//        photos = findViewById(R.id.buttonPhotos);
//        photos.setOnClickListener(v -> {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
//            } else {
//                openCamera();
//            }
//        });
//
//        btnSearch = findViewById(R.id.barSearch);
//        btnSearch.setOnClickListener(v -> {
//            Intent a = new Intent(SearchActivity.this, SeacrchFoodByNameActivity.class);
//            a.putExtra("user", user);
//            startActivity(a);
//        });
//
//        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
//        botNav.setSelectedItemId(R.id.menuSearch);
//
//        botNav.setOnItemSelectedListener(menuItem -> {
//            int id = menuItem.getItemId();
//            if (id == R.id.menuHome) {
//                Intent intent2 = new Intent(SearchActivity.this, HomeActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//
//            } else if (id == R.id.menuProfile){
//                Intent intent2 = new Intent(SearchActivity.this, ProfileeActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            } else if (id == R.id.menuSearch){
//                Intent intent2 = new Intent(SearchActivity.this, SearchActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            } else if (id == R.id.menuAdd){
//                Intent intent2 = new Intent(SearchActivity.this, AddFoodPostActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            }
//            return false;
//        });
//    }
//    private void openCamera() {
//        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                "photo_" + System.currentTimeMillis() + ".jpg");
//
//        imageUri = FileProvider.getUriForFile(this,
//                getPackageName() + ".provider",
//                photoFile);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        cameraLauncher.launch(intent);
//    }
//    private void uploadImageToServer(Bitmap bitmap) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
//            String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("image", encodedImage);
//            String url = "https:";
//            RequestQueue queue = Volley.newRequestQueue(this);
//
//            JsonObjectRequest jsonRequest = new JsonObjectRequest(
//                    Request.Method.POST,
//                    url,
//                    requestBody,
//                    response -> {
//                        Toast.makeText(this, "Tải ảnh thành công!", Toast.LENGTH_SHORT).show();
//                    },
//                    error -> {
//                        Toast.makeText(this, "Lỗi tải ảnh: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//            ) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//            };
//            queue.add(jsonRequest);
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Lỗi khi gửi ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//
//
//    private void searchFood(ArrayList<NguyenLieu> a){
//        String url = "https://mobilenodejs.onrender.com/api/baidang/search/ingredient";
//        RequestQueue queue = Volley.newRequestQueue(this);
//        try{
//            JSONArray nguyenLieuArray = new JSONArray();
//            for(int i = 0 ; i < a.size(); i++){
//                JSONObject nguyenLieuObject = new JSONObject();
//                nguyenLieuObject.put("ten", a.get(i).getTen());
//                nguyenLieuArray.put(nguyenLieuObject);
//            }
//
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("nguyenLieu", nguyenLieuArray);
//
//            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                    Request.Method.POST,
//                    url,
//                    null,
//                    response -> {
//                try {
//                   bd.clear();
//                   for(int i = 0; i < response.length(); i++){
//                       JSONObject obj = response.getJSONObject(i);
//
//                       BaiDang baiDang = new BaiDang();
//                       baiDang.setId(obj.getString("_id"));
//                       baiDang.setTenMon(obj.getString("tenMon"));
//                       baiDang.setCachLam(obj.getString("cachLam"));
//                       baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
//                       baiDang.setLinkYtb(obj.optString("linkYtb", ""));
//                       baiDang.setLuotThich(obj.optInt("luotThich", 0));
//                       baiDang.setImage(obj.optString("image", ""));
//
//                       JSONArray nlArray = obj.getJSONArray("nguyenLieu");
//                       ArrayList<NguyenLieu> nguyenLieu = new ArrayList<>();
//                       for(int j = 0; j < nlArray.length(); j++){
//                           nguyenLieu.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
//                       }
//                       baiDang.setNguyenLieu(nguyenLieu);
//
//                       bd.add(baiDang);
//
//                   }
//
//                    if (bd.size() > 0) {
//                        Intent intent = new Intent(SearchActivity.this, FoundFoodActivity.class);
//                        intent.putExtra("type", 1);
//                        intent.putExtra("data", bd);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(SearchActivity.this, "Không tìm thấy món ăn nào phù hợp!", Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    Toast.makeText(this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            },
//                    error -> {
//                        Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
//                    })
//            {
//                public byte[] getBody() {
//                    return requestBody.toString().getBytes();
//                }
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//            };
//            queue.add(jsonArrayRequest);
//        }catch (Exception e) {
//            Toast.makeText(this, "Lỗi tạo request", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//}