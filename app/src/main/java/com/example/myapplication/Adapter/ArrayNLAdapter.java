package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.model.NguyenLieu;

import java.util.List;

public class ArrayNLAdapter extends ArrayAdapter<NguyenLieu> {

    private final int idLayout;
    private final List<NguyenLieu> nlList;

    public ArrayNLAdapter(@NonNull Context context, int idLayout, @NonNull List<NguyenLieu> nlList) {
        super(context, idLayout, nlList);
        this.idLayout = idLayout;
        this.nlList = nlList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(idLayout, parent, false);
        }

        NguyenLieu nguyenLieu = nlList.get(position);
        TextView txtTenNguyenLieu = convertView.findViewById(R.id.txtTenNguyenLieu);
        txtTenNguyenLieu.setText(nguyenLieu.getTen());

        return convertView;
    }
}