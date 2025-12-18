package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.model.ShortVideo;

import java.util.ArrayList;

public class UserReelAdapter extends RecyclerView.Adapter<UserReelAdapter.ReelViewHolder> {

    private Context context;
    private ArrayList<ShortVideo> listVideo;
    private OnItemClickListener listener; // Interface để xử lý click

    // Interface để Fragment lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(ShortVideo video, int position);
    }

    public UserReelAdapter(Context context, ArrayList<ShortVideo> listVideo, OnItemClickListener listener) {
        this.context = context;
        this.listVideo = listVideo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Thổi phồng" layout item_user_reel.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_reel, parent, false);
        return new ReelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        ShortVideo video = listVideo.get(position);

        // 1. Set số views
        holder.tvViews.setText(formatViews(video.getViews()));

        // 2. Load ảnh Thumbnail với Glide
        String urlToLoad = video.getUrl();
        if (urlToLoad == null || urlToLoad.isEmpty()) {
            holder.imgThumb.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định
        } else {
            Glide.with(context)
                    .load(urlToLoad)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.black)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(holder.imgThumb);
        }

        // 3. Bắt sự kiện Click cho toàn bộ ô vuông (ItemView)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(video, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listVideo.size();
    }

    // ViewHolder: Nơi ánh xạ view (Thay thế cho cái túi trong BaseAdapter)
    public static class ReelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvViews;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgReelThumb);
            tvViews = itemView.findViewById(R.id.tvReelViews);
        }
    }

    // Hàm format view (VD: 1.5K)
    private String formatViews(int views) {
        if (views >= 1000000) return String.format("%.1fM", views / 1000000.0);
        if (views >= 1000) return String.format("%.1fK", views / 1000.0);
        return String.valueOf(views);
    }
}