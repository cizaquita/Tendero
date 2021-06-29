package com.example.android.fragments.entities;

import com.orm.SugarRecord;

/**
 *Información de cada producto (precio, cantidad, pedido al que pertenece)
 */

public class Product extends SugarRecord {
    private int count;
    private String name;
    private double unit_price;
    Delivery delivery;


    public Product() {
    }

    public Product(int count, String name, double unit_price, Delivery delivery) {
        this.count = count;
        this.name = name;
        this.unit_price = unit_price;
        this.delivery = delivery;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public double getTotal() {
        return unit_price*count;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }


}
