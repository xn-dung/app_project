package com.example.myapplication.model;
import java.io.Serializable;
import java.util.*;

public class BaiDang implements Serializable {

    private String id;
    private String tenMon;
    private ArrayList<NguyenLieu> nguyenLieu;
    private String nguyenLieuDinhLuong;
    private String cachLam;
    private String linkYtb;
    private Integer luotThich;
    private String image;

    public BaiDang() {
    }


    public BaiDang(String id, String tenMon, ArrayList<NguyenLieu> nguyenLieu, String nguyenLieuDinhLuong,
                   String cachLam, String linkYtb, Integer luotThich, String image) {
        this.id = id;
        this.tenMon = tenMon;
        this.nguyenLieu = nguyenLieu;
        this.nguyenLieuDinhLuong = nguyenLieuDinhLuong;
        this.cachLam = cachLam;
        this.linkYtb = linkYtb;
        this.luotThich = luotThich;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public List<NguyenLieu> getNguyenLieu() {
        return nguyenLieu;
    }

    public void setNguyenLieu(ArrayList<NguyenLieu> nguyenLieu) {
        this.nguyenLieu = nguyenLieu;
    }

    public String getNguyenLieuDinhLuong() {
        return nguyenLieuDinhLuong;
    }

    public void setNguyenLieuDinhLuong(String nguyenLieuDinhLuong) {
        this.nguyenLieuDinhLuong = nguyenLieuDinhLuong;
    }

    public String getCachLam() {
        return cachLam;
    }

    public void setCachLam(String cachLam) {
        this.cachLam = cachLam;
    }

    public String getLinkYtb() {
        return linkYtb;
    }

    public void setLinkYtb(String linkYtb) {
        this.linkYtb = linkYtb;
    }

    public Integer getLuotThich() {
        return luotThich;
    }

    public void setLuotThich(Integer luotThich) {
        this.luotThich = luotThich;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
