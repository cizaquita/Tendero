package com.example.android.fragments.entities;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Esta clase es utilizada en el inventario para guardar los productos que pertenecen a una categoria
 * especifica
 */
public class CategoryProduct extends SugarRecord {
    String name;
    @Unique
    Long product_id;
    double price;
    double default_price;
    boolean stock;
    Long category_id;

    public CategoryProduct() {
    }


    public CategoryProduct(String name, Long category_id, boolean stock, double default_price, double price, Long product_id) {
        this.name = name;
        this.category_id = category_id;
        this.stock = stock;
        this.default_price = default_price;
        this.price = price;
        this.product_id = product_id;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDefault_price() {
        return default_price;
    }

    public void setDefault_price(double default_price) {
        this.default_price = default_price;
    }

    public boolean getStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }
}
