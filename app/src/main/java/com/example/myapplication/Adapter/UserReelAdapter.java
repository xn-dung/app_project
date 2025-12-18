package com.example.myapplication.Adapter;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.model.ShortVideo;

import java.util.ArrayList;

public class UserReelAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShortVideo> listVideo;
    private LayoutInflater inflater;

    public UserReelAdapter(Context context, ArrayList<ShortVideo> listVideo) {
        this.context = context;
        this.listVideo = listVideo;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listVideo.size();
    }

    @Override
    public Object getItem(int position) {
        return listVideo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder {
        ImageView imgThumb;
        TextView tvViews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_reel, parent, false);
            holder = new ViewHolder();
            holder.imgThumb = convertView.findViewById(R.id.imgReelThumb);
            holder.tvViews = convertView.findViewById(R.id.tvReelViews);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShortVideo video = listVideo.get(position);
        holder.tvViews.setText(formatViews(video.getViews()));
        String urlToLoad = video.getUrl();
        if (urlToLoad == null || urlToLoad.isEmpty()) {
            holder.imgThumb.setImageResource(R.drawable.ic_play_circle);
        } else {
            Glide.with(context)
                    .load(urlToLoad)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache để load nhanh hơn
                    .placeholder(R.color.black) // Hiện màu đen trong lúc chờ
                    .error(android.R.drawable.ic_menu_report_image) // Hiện icon lỗi nếu link chết
                    .centerCrop()
                    .into(holder.imgThumb);
        }

        return convertView;
    }

    private String formatViews(int views) {
        if (views >= 1000000) return String.format("%.1fM", views / 1000000.0);
        if (views >= 1000) return String.format("%.1fK", views / 1000.0);
        return String.valueOf(views);
    }
}