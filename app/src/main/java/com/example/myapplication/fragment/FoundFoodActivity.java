package com.example.myapplication.fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myapplication.Adapter.FoundFoodAdapter;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import com.example.myapplication.R;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


public class FoundFoodActivity extends AppCompatActivity{
    String ten;
    ArrayList<NguyenLieu> nguyenLieu;
    ArrayList<BaiDang> bd;
    ListView lv;
    FoundFoodAdapter myadapter;
    TextView texname;
    User user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_food);
        lv = findViewById(R.id.outfood);
        texname = findViewById(R.id.textFullName);
        bd = new ArrayList<>();
        int type = getIntent().getIntExtra("type", 0);
        user = (User) getIntent().getSerializableExtra("user");
        if(type == 1){
            bd = (ArrayList<BaiDang>) getIntent().getSerializableExtra("data");
            myadapter = new FoundFoodAdapter( R.layout.food_card_home, FoundFoodActivity.this, bd);
            lv.setAdapter(myadapter);
        }
        else{
            String foodName = getIntent().getStringExtra("search");
            searchFood(foodName);
            myadapter = new FoundFoodAdapter( R.layout.food_card_home, FoundFoodActivity.this, bd);
            lv.setAdapter(myadapter);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentHehe = new Intent(FoundFoodActivity.this, DetailFoodFragment.class);
                BaiDang chonBD = bd.get(position);
                Toast.makeText(FoundFoodActivity.this, "Bạn đã chọn món " + chonBD.getTenMon(), Toast.LENGTH_SHORT).show();
                intentHehe.putExtra("data", chonBD);
                intentHehe.putExtra("user", user);
                startActivity(intentHehe);
            }
        });

        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
        botNav.setSelectedItemId(R.id.menuHome);

        botNav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menuHome) {
                Intent intent2 = new Intent(FoundFoodActivity.this, HomeFragment.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;

            } else if (id == R.id.menuProfile){
                Intent intent2 = new Intent(FoundFoodActivity.this, ProfileFragment.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            } else if (id == R.id.menuSearch){
                Intent intent2 = new Intent(FoundFoodActivity.this, SearchFragment.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }
            return false;
        });
    }

    private void searchFood(String foodName) {
        String url = getString(R.string.backend_url) + "api/baidang/search";
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("tenMon", foodName);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.POST,
                    url,
                    null, // Đặt null ở đây, vì ta sẽ override body
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
                            Toast.makeText(this, "Tìm thấy " + bd.size() + " món", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Lỗi tạo request", Toast.LENGTH_SHORT).show();
        }
    }

}