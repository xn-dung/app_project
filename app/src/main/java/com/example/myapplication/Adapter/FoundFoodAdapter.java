package com.example.myapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.model.BaiDang;

import java.util.ArrayList;
import com.bumptech.glide.Glide;

public class FoundFoodAdapter extends ArrayAdapter<BaiDang>{
    Activity context;
    int idlayout;
    ArrayList<BaiDang> myList;

    public FoundFoodAdapter( int idlayout, Activity context, ArrayList<BaiDang> myList) {
        super(context, idlayout, myList);
        this.idlayout = idlayout;
        this.context = context;
        this.myList = myList;
    }


    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        LayoutInflater myFlacter = context.getLayoutInflater();
        convertView = myFlacter.inflate(idlayout, null);
        BaiDang myBaiDang = myList.get(position);
        TextView txt_title = convertView.findViewById(R.id.textViewTen);
        TextView txt_nguyenlieu = convertView.findViewById(R.id.textViewNguyenLieu);
        ImageView anh = convertView.findViewById(R.id.imgFood);
        txt_title.setText(myBaiDang.getTenMon());
        StringBuilder tmp = new StringBuilder();
        for (int i = 0 ; i < myBaiDang.getNguyenLieu().size(); i++){
            tmp.append(myBaiDang.getNguyenLieu().get(i).getTen());
            if (i != myBaiDang.getNguyenLieu().size() - 1){
                tmp.append(", ");
            }
        }
        txt_nguyenlieu.setText(tmp.toString());
        Glide.with(this.getContext())
                .load(myBaiDang.getImage())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.ic_launcher_background)
                .into(anh);
        return convertView;
    }
}