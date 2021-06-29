package com.example.android.fragments.entities;

import com.orm.SugarRecord;

import java.math.BigInteger;

/**
 * Información del tendero que ha iniciado sesión en la app
 */
public class Store  extends SugarRecord {

    private Integer profile_id;
    private BigInteger shop_id;
    private String username;
    private String name;
    private String email;
    private int open;
    private String telephone;

    public Store() {
    }

    public Store(Integer profile_id, BigInteger shop_id, String username, String name, String email, int open, String telephone) {
        this.profile_id = profile_id;
        this.shop_id = shop_id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.open = open;
        this.telephone = telephone;
    }

    public Integer getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(Integer profile_id) {
        this.profile_id = profile_id;
    }

    public BigInteger getShop_id() {
        return shop_id;
    }

    public void setShop_id(BigInteger shop_id) {
        this.shop_id = shop_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
