package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.ShortVideoAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.ShortVideo;
import com.example.myapplication.model.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ReelFragment extends Fragment {

    private User user;
    private ViewPager2 viewPager2;
    private ArrayList<ShortVideo> listBaiDang;
    private ShortVideoAdapter adapter;
    private ExoPlayer exoPlayer;

    private HashSet<String> likedVideoIds = new HashSet<>();

    public static ReelFragment newInstance(User user) {
        ReelFragment fragment = new ReelFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public static ReelFragment newInstanceWithData(User user, ArrayList<ShortVideo> list, int position) {
        ReelFragment fragment = new ReelFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        args.putSerializable("list_video", list);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public ReelFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.short_video, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewPager2 = view.findViewById(R.id.viewPagerVideo);
        if (getArguments() != null && getArguments().containsKey("list_video")) {
            listBaiDang = (ArrayList<ShortVideo>) getArguments().getSerializable("list_video");
            int startPosition = getArguments().getInt("position", 0);

            adapter = new ShortVideoAdapter(listBaiDang, (video, pos) -> likeDislike(video, pos));
            viewPager2.setAdapter(adapter);


            viewPager2.setCurrentItem(startPosition, false);


            viewPager2.post(() -> playVideoAt(startPosition));

        } else {

            listBaiDang = new ArrayList<>();
            adapter = new ShortVideoAdapter(listBaiDang, (video, pos) -> likeDislike(video, pos));
            viewPager2.setAdapter(adapter);
            takeBD();
        }

        viewPager2.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        playVideoAt(position);
                    }
                }
        );
    }

    private void takeBD() {
        String url = getString(R.string.backend_url) + "api/reel";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        listBaiDang.clear();
                        likedVideoIds.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            JSONObject nguoidung = obj.getJSONObject("nguoidung");

                            ShortVideo video = new ShortVideo();
                            video.setId(obj.getString("_id"));
                            video.setTieuDe(obj.getString("tieude"));
                            video.setUrl(obj.optString("videoUrl", ""));
                            video.setDescription(obj.optString("description", ""));
                            video.setLikes(obj.optInt("likes", 0));
                            video.setViews(obj.optInt("views", 0));
                            video.setAuthor(nguoidung.getString("name"));
                            video.setIdAuthor(nguoidung.getString("_id"));

                            if (obj.optBoolean("likedByMe", false)) {
                                likedVideoIds.add(video.getId());
                            }

                            listBaiDang.add(video);
                        }

                        adapter.notifyDataSetChanged();
                        viewPager2.post(() -> playVideoAt(0));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error ->{
                    error.printStackTrace();
                }
        );

        queue.add(request);
    }

    private void playVideoAt(int position) {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
        } else {
            exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        }

        ShortVideo video = listBaiDang.get(position);

        ShortVideoAdapter.VideoViewHolder holder =
                adapter.getViewHolder(viewPager2, position);

        if (holder == null) return;

        holder.progressBar.setVisibility(View.VISIBLE);
        holder.playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(video.getUrl());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        exoPlayer.addListener(
                new androidx.media3.common.Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == androidx.media3.common.Player.STATE_READY) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private void likeDislike(ShortVideo video, int position) {
        String url = getString(R.string.backend_url)
                + "api/reel/" + video.getId() + "/like";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", user.getId());

        JSONObject body = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    boolean isLiked = likedVideoIds.contains(video.getId());

                    if (isLiked) {
                        likedVideoIds.remove(video.getId());
                        video.setLikes(video.getLikes() - 1);
                    } else {
                        likedVideoIds.add(video.getId());
                        video.setLikes(video.getLikes() + 1);
                    }

                    adapter.notifyItemChanged(position);
                },
                error -> {
                    error.printStackTrace();
                }
        );

        queue.add(request);
    }

    // 1. Xử lý khi chuyển Tab (Hide/Show)
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (hidden) {
            if (exoPlayer != null) {
                exoPlayer.pause();
            }
        } else {
            if (exoPlayer != null) {
                exoPlayer.play();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer != null && !isHidden()) {
            exoPlayer.play();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}