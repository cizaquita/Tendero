package com.example.android.fragments.entities;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Información general de un pedido (detalles del cliente, precios, fecha, etc.)
 */

public class Delivery extends SugarRecord {


    @Unique
    private int order_id;
    @Ignore
    private ArrayList<Product> products;

    private String title;
    private int state;
    private String date;
    private String address;
    private String address_details;
    private String client_telephone;
    private String client_name;
    private String comments;
    private String payment;
    private Date start_date;
    private Date end_date;
    private int delivery_time;
    private float delivery_cost;
    //Estado Pagado 0, no pa
    private int pagado;
    //Don Colmado = 1; App = 0; - NEW: ServiPunto = 2
    private int app_id;

    //Motero del domicilio
    private Motero motero;

    public Delivery() {

    }

    public Delivery(int order_id, ArrayList<Product> products, String title, int state, String date, String address, String address_details, String client_telephone, String client_name, String comments, String payment, Date start_date, Date end_date, int delivery_time, float delivery_cost, int app_id, int pagado, Motero motero) {
        this.order_id = order_id;
        this.products = products;
        this.title = title;
        this.state = state;
        this.date = date;
        this.address = address;
        this.address_details = address_details;
        this.client_telephone = client_telephone;
        this.client_name = client_name;
        this.comments = comments;
        this.payment = payment;
        this.start_date = start_date;
        this.end_date = end_date;
        this.delivery_time = delivery_time;
        this.delivery_cost = delivery_cost;
        this.app_id = app_id;
        this.pagado = pagado;
        this.motero = motero;
    }

    public String getTitle() {
        return title;
    }

    public int getState() {
        return state;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getDate() {
        return date;
    }

    public int getApp_id() {
        return app_id;
    }

    public int getPagado() {
        return pagado;
    }

    public Motero getMotero() {
        return motero;
    }

    public String getAddreess() {
        return address;
    }

    public List<Product> getProducts() {
        return Product.find(Product.class, "delivery = ?", new String[]{getId() + ""});
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    //pagado
    public void setPagado(int pagado) {
        this.pagado = pagado;
    }

    public void setMotero(Motero motero) {
        this.motero = motero;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getApiProducts() {
        return products;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public int getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(int delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getAddress_details() {
        return address_details;
    }

    public void setAddress_details(String address_details) {
        this.address_details = address_details;
    }

    public String getClient_telephone() {
        return client_telephone;
    }

    public void setClient_telephone(String client_telephone) {
        this.client_telephone = client_telephone;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public float getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(float delivery_cost) {
        this.delivery_cost = delivery_cost;
    }


    @Override
    public String toString() {
        return "Delivery{" +
                "order_id=" + order_id +
                ", products=" + products +
                ", title='" + title + '\'' +
                ", state=" + state +
                ", date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", address_details='" + address_details + '\'' +
                ", client_telephone='" + client_telephone + '\'' +
                ", client_name='" + client_name + '\'' +
                ", comments='" + comments + '\'' +
                ", payment='" + payment + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", delivery_time=" + delivery_time +
                ", delivery_cost=" + delivery_cost +
                ", pagado=" + pagado +
                ", motero=" + motero +
                ", app_id=" + app_id +
                '}';
    }
}