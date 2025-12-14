package com.example.myapplication.model;
import java.io.Serializable;
import java.util.*;

public class NguyenLieu implements Serializable {

    private String id;
    private String ten;


    public NguyenLieu() {
    }


    public NguyenLieu(String id, String ten) {
        this.id = id;
        this.ten = ten;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String tenMon) {
        this.ten = tenMon;
    }

}
