package com.example.android.fragments.entities;

import com.orm.SugarRecord;

/**
 * Created by Medicitas SAS on 21/04/2016.
 */
public class Motero extends SugarRecord {

    private String name;
    private String plate;
    private boolean duty;
    private int type;

    public Motero(){

    }

    public Motero(String name, String plate, boolean duty, int type) {
        this.name = name;
        this.plate = plate;
        this.duty = duty;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public boolean isDuty() {
        return duty;
    }

    public void setDuty(boolean duty) {
        this.duty = duty;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Motero{" +
                "name='" + name + '\'' +
                ", plate='" + plate + '\'' +
                ", duty=" + duty +
                ", type=" + type +
                '}';
    }
}
