package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.interfaces.OnLikeClickListener;
import com.example.myapplication.model.ShortVideo;

import java.util.List;

public class ShortVideoAdapter extends RecyclerView.Adapter<ShortVideoAdapter.VideoViewHolder> {

    private List<ShortVideo> bd;
    private OnLikeClickListener likeListener;
    private boolean check;
    public ShortVideoAdapter(List<ShortVideo> videoList,
                             OnLikeClickListener likeListener) {
        this.bd = videoList;
        this.likeListener = likeListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_one, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return (bd != null) ? bd.size() : 0;
    }

    public VideoViewHolder getViewHolder(ViewPager2 viewPager2, int position) {
        RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
        return (VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        ShortVideo video = bd.get(position);
        holder.textTitle.setText("@" + video.getAuthor());
        holder.textDescription.setText(video.getDescription());
        holder.textLikeCount.setText(String.valueOf(video.getLikes()));

        holder.btnLike.setOnClickListener(v -> {
            if(likeListener != null){
                likeListener.onLikeClick(video, position);
            }
        });
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        public PlayerView playerView;
        public ProgressBar progressBar;
        public ImageView btnLike;
        public TextView textTitle;
        public TextView textDescription;
        public TextView textLikeCount;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            playerView = itemView.findViewById(R.id.playerView);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnLike = itemView.findViewById(R.id.btnLikeShort);

            textTitle = itemView.findViewById(R.id.textVideoTitle);
            textDescription = itemView.findViewById(R.id.textVideoDescription);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
        }
    }

}