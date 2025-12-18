package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.BaiDang;

import java.util.ArrayList;

public class MyArrayAdapter extends ArrayAdapter<BaiDang> {

    private final int idLayout;
    private final ArrayList<BaiDang> myList;

    public MyArrayAdapter(@NonNull Context context, int idLayout, @NonNull ArrayList<BaiDang> myList) {
        super(context, idLayout, myList);
        this.idLayout = idLayout;
        this.myList = myList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(idLayout, parent, false);
        }

        BaiDang myBaiDang = myList.get(position);

        ImageView imgItem = convertView.findViewById(R.id.imageFood);
        Glide.with(getContext())
                .load(myBaiDang.getImage())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.ic_launcher_background)
                .into(imgItem);

        TextView nameItem = convertView.findViewById(R.id.textFoodName);
        TextView tvLikeCount = convertView.findViewById(R.id.tvLikeCount);
        TextView tvViewCount = convertView.findViewById(R.id.tvViewCount);


        nameItem.setText(myBaiDang.getTenMon());
        tvLikeCount.setText(String.valueOf(myBaiDang.getLuotThich()));
        tvViewCount.setText(String.valueOf(myBaiDang.getViews()));



        return convertView;
    }
}