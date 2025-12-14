//package com.example.myapplication.UI;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.content.Intent;
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
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
//import com.google.android.material.navigation.NavigationBarView;
//
//import org.json.JSONObject;
//
//import java.util.*;
//public class ProfileeActivity extends AppCompatActivity{
//
//    private Button btnLogout;
//    private TextView userFullname;
//    private TextView userEmail;
//    private LinearLayout textSettings;
//    private LinearLayout viewPosts;
//    private User user;
//    private BottomNavigationView bottomNavigationView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.user_profile);
//        btnLogout = findViewById(R.id.btnLogout);
//        userFullname = findViewById(R.id.userFullName);
//        userEmail = findViewById(R.id.userEmail);
//        textSettings = findViewById(R.id.editProfileBtn);
//        bottomNavigationView = findViewById(R.id.bottomNavView);
////        viewPosts = findViewById(R.id.postsBtn);    để sau
//        Intent intent = getIntent();
//        user = (User) intent.getSerializableExtra("user");
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProfileeActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        String fullname = user.getFullname();
//        String email = user.getEmail();
//        userFullname.setText(fullname);
//        userEmail.setText(email);
//        textSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProfileeActivity.this, EditProfileActivity.class);
//                intent.putExtra("user",user);
//                startActivity(intent);
//            }
//        });
//
//
//        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
//        botNav.setSelectedItemId(R.id.menuProfile);
//
//        botNav.setOnItemSelectedListener(menuItem -> {
//            int id = menuItem.getItemId();
//            if (id == R.id.menuHome) {
//                Intent intent2 = new Intent(ProfileeActivity.this, HomeActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//
//            } else if (id == R.id.menuProfile){
//                Intent intent2 = new Intent(ProfileeActivity.this, ProfileeActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            } else if (id == R.id.menuSearch){
//                Intent intent2 = new Intent(ProfileeActivity.this, SearchActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            }else if(id == R.id.menuAdd){
//                Intent intent2 = new Intent(ProfileeActivity.this, AddFoodPostActivity.class);
//                intent2.putExtra("user", user);
//                startActivity(intent2);
//                return true;
//            }
//            return false;
//        });
//    }
//
//}