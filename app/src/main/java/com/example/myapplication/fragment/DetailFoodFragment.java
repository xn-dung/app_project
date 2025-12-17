package com.example.myapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.ArrayNLAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class DetailFoodFragment extends Fragment {
    public static final String TAG = DetailFoodFragment.class.getName();
    private BaiDang baiDang;
    private User user;

    private ArrayNLAdapter adapterNL;
    private YouTubePlayerView youtubePlayerView;
    private TabHost tabHost;
    ImageButton btnBack;
    ImageButton btnFavorite;
    private boolean isFav = false;
    private long startTime;
    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }
    @Override
    public void onPause() {
        super.onPause();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (elapsedTime > 3000) {
            sendRecommendation(baiDang, elapsedTime);
        }
    }
    public static DetailFoodFragment newInstance(BaiDang baiDang, User user) {
        DetailFoodFragment fragment = new DetailFoodFragment();
        Bundle args = new Bundle();
        args.putSerializable("baiDang", baiDang);
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            baiDang = (BaiDang) getArguments().getSerializable("baiDang");
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_food_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (baiDang == null) {
            Toast.makeText(requireContext(), "Không có dữ liệu bài đăng!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        addControl(view);

        ImageView imgBD = view.findViewById(R.id.imgFood);
        Glide.with(this)
                .load(baiDang.getImage())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.ic_launcher_background)
                .into(imgBD);

        TextView txtBD = view.findViewById(R.id.txtFoodName);
        txtBD.setText(baiDang.getTenMon());

        View layoutNL = view.findViewById(R.id.layoutNL);
        ListView lvNL = layoutNL.findViewById(R.id.listNL);
        List<NguyenLieu> nlList = baiDang.getNguyenLieu();
        adapterNL = new ArrayNLAdapter((Activity) requireContext(), R.layout.layout_itemnl, nlList);
        lvNL.setAdapter(adapterNL);

        TextView tvCachLam = view.findViewById(R.id.tvCachLam);
        tvCachLam.setText(baiDang.getCachLam());

        ImageView imgThumb = view.findViewById(R.id.imgYtbThumb);
        ImageView btnPlay = view.findViewById(R.id.btnPlayYtb);

        String linkYtb = baiDang.getLinkYtb();
        String videoId = extractYoutubeVideoId(linkYtb);


        String thumbUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
        Glide.with(this)
                .load(thumbUrl)
                .placeholder(R.drawable.logo_app)
                .into(imgThumb);


        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + videoId));
            startActivity(intent);
        });
        btnBack = view.findViewById(R.id.btnBackF);
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnFavorite = view.findViewById(R.id.btnFavorite);
        if(user != null){
            checkFav();
        }

        btnFavorite.setOnClickListener(v -> {
            if (user != null && baiDang != null) {
                goiFavAPI();
            } else {
                Toast.makeText(requireContext(), "lỗi thông in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goiFavAPI() {
        String url = getString(R.string.backend_url) + "api/nguoidung/patch/addFav";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nguoidungId", user.getId());
            jsonBody.put("baidangId", baiDang.getId());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, jsonBody,
                    response -> {
                        isFav = !isFav;

                        updateFav();
                        if (isFav) {
                            Toast.makeText(requireContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(requireContext(), "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                        Log.e("DetailFood", "Lỗi Toggle Fav: " + error.toString());
                    }
            );
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkFav(){
        String url = getString(R.string.backend_url) + "api/nguoidung/fav/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    isFav = false;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject fav = response.getJSONObject(i);
                            if(fav.getString("_id").equals(baiDang.getId())){
                                isFav = true;
                                break;
                            }
                        }
                        updateFav();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                },error -> Log.e("DetailFood", "Lỗi check fav: " + error.toString()));
        queue.add(request);
    }

    private void updateFav(){
        if(isFav){
            btnFavorite.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorLogoutDark));
        }else{
            btnFavorite.clearColorFilter();
        }
    }

    private void addControl(View view) {
        tabHost = view.findViewById(R.id.tabBD);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("Nguyên liệu");
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("Hướng dẫn");
        tabHost.addTab(tab2);

        updateTabStyles();

        tabHost.setOnTabChangedListener(tabId -> updateTabStyles());
    }

    private void updateTabStyles() {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View tabView = tabHost.getTabWidget().getChildAt(i);
            TextView tv = tabView.findViewById(android.R.id.title);
            if (i == tabHost.getCurrentTab()) {
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
                tabView.setBackgroundResource(R.drawable.tabhost_bg_slt);
            } else {
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
                tabView.setBackgroundResource(R.drawable.tabhost_bg_unslt);
            }
        }
    }

    private String extractYoutubeVideoId(String url) {
        if (url == null || url.isEmpty()) return "";

        if (url.contains("v=")) {
            String id = url.substring(url.indexOf("v=") + 2);
            int ampIndex = id.indexOf("&");
            if (ampIndex != -1) {
                id = id.substring(0, ampIndex);
            }
            return id;
        }


        if (url.contains("youtu.be/")) {
            String id = url.substring(url.indexOf("youtu.be/") + 9);
            int qmIndex = id.indexOf("?");
            if (qmIndex != -1) {
                id = id.substring(0, qmIndex);
            }
            return id;
        }

        return url;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (youtubePlayerView != null) {
            youtubePlayerView.release();
        }
    }
    public void sendRecommendation(BaiDang baiDang, long elapsedTime){

    }
}