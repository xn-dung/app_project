//package com.example.myapplication.UI;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.SearchView;
//import android.widget.TextView;
//import android.content.Intent;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.widget.EditText;
//import android.widget.Button;
//
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.example.myapplication.model.User;
//import com.example.myapplication.R;
//
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//import org.json.JSONObject;
//
//import java.util.*;
//public class SeacrchFoodByNameActivity extends AppCompatActivity {
//    private SearchView sv;
//
//    private User user;
//    private Button btn;
//    protected void onCreate(Bundle savedInstanceState)  {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.search_food_byname);
//        user = (User) getIntent().getSerializableExtra("user");
//        sv = findViewById(R.id.searchname);
//        btn = findViewById(R.id.startsearchbyname);
//        btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                String search = sv.getQuery().toString();
//                if(!search.isEmpty()){
//                    Intent intent = new Intent(SeacrchFoodByNameActivity.this, FoundFoodActivity.class);
//                    intent.putExtra("type", 2);
//                    intent.putExtra("search", search);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
//        botNav.setSelectedItemId(R.id.menuSearch);
//
//        botNav.setOnItemSelectedListener(menuItem -> {
//            int id = menuItem.getItemId();
//            if (id == R.id.menuHome) {
//                Intent intent2 = new Intent(SeacrchFoodByNameActivity.this, HomeActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//
//            } else if (id == R.id.menuProfile){
//                Intent intent2 = new Intent(SeacrchFoodByNameActivity.this, ProfileeActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            } else if (id == R.id.menuSearch){
//                Intent intent2 = new Intent(SeacrchFoodByNameActivity.this, SearchActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            }
//            return false;
//        });
//    }
//
//}
