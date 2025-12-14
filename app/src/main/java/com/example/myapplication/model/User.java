package com.example.myapplication.model;

import java.io.Serializable;

public class User implements Serializable {
    private String _id;
    private String username;
    private String password;
    private String name;
    private String address;
    private String email;
    private String tel;



    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User( String username, String password, String name, String address, String email, String tel) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.tel = tel;
    }

    public User( String _id, String username, String password, String name, String address, String email, String tel) {
        this.username = username;
        this._id = _id;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.tel = tel;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;

    }

    public String getFullname() {
        return name;
    }

    public String getTel() {
        return tel;
    }
    public String getEmail() {
        return email;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.name = fullname;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
