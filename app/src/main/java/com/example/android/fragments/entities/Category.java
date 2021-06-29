package com.example.android.fragments.entities;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Esta clase es el registro de cada categoría en la base de datos a la que pertenece
 * cada producto
 */
public class Category  extends SugarRecord {
    String name;
    @Unique
    Long category_id;

    public Category() {
    }

    public Category(String name, Long category_id) {
        this.name = name;
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }
}

