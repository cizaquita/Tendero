package com.example.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.fragments.entities.Delivery;
import com.example.android.fragments.entities.Product;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Adapter de cada pedido
 */
public class DeliveryAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Delivery> deliveries;
    private LayoutInflater mLayoutInflater = null;

    public DeliveryAdapter(Activity context, List<Delivery>  list) {
        mContext = context;
        deliveries = list;
        mLayoutInflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (deliveries.size() > 0){
            return deliveries.size();
        }else return 0;
    }

    @Override
    public Object getItem(int pos) {
        return deliveries.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        CompleteListViewHolder viewHolder;
        if (convertView == null) {

            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.order_item, null);
            viewHolder = new CompleteListViewHolder(v);
            v.setTag(viewHolder);

        } else {
            viewHolder = (CompleteListViewHolder) v.getTag();
        }

        Delivery delivery = deliveries.get(position);
        List<Product> products = delivery.getProducts();

        double total = 0;
        int pNumber = 0;

        for (Product p : products){

            total += p.getTotal();
            pNumber += p.getCount();
        }
        NumberFormat nf = NumberFormat.getInstance();
        viewHolder.mTVItem.setText(delivery.getTitle());
        viewHolder.mTVSubtitle.setText(pNumber + " productos - "+ "$"+nf.format(total));
        viewHolder.mTVID.setText("ID Pedido #"+ delivery.getOrder_id());

        int state = deliveries.get(position).getState();
        String state_text;

        viewHolder.mLYState.setBackgroundColor(Color.TRANSPARENT);
        if(state == 0){
            state_text = "Nuevo Pedido";
            viewHolder.mTVItem.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.mTVSubtitle.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.mTVState.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.mLYState.setBackgroundColor(Color.parseColor("#638cde"));

        } else if(state == 1){
            state_text = "Confirmado";
            viewHolder.mTVItem.setTypeface(Typeface.DEFAULT);
            viewHolder.mTVSubtitle.setTypeface(Typeface.DEFAULT);
            viewHolder.mTVState.setTextColor(Color.parseColor("#2c9c20"));
        } else if(state == 2) {
            state_text = "Rechazado";
            viewHolder.mTVItem.setTypeface(Typeface.DEFAULT);
            viewHolder.mTVSubtitle.setTypeface(Typeface.DEFAULT);
            viewHolder.mTVState.setTextColor(Color.parseColor("#9c2020"));
        } else {
            state_text = "";
        }

        if(HeadlinesFragment.selectedItem == position){
            viewHolder.mTVLayout.setBackgroundColor(Color.parseColor("#e5e5e5"));
        } else{
            viewHolder.mTVLayout.setBackgroundResource(0);
        }

        viewHolder.mTVState.setText(state_text);


        //Hora
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = format.parse(delivery.getDate());
            format.setTimeZone(TimeZone.getDefault());
            SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm a");
   
            String datetime = dateformat.format(date);
            viewHolder.mTVDate.setText(datetime);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return v;
    }


}

class CompleteListViewHolder {


    public TextView mTVItem;
    public TextView mTVSubtitle;
    public TextView mTVID;
    public TextView mTVState;
    public TextView mTVDate;
    public LinearLayout mTVLayout;
    public LinearLayout mLYState;
    public CompleteListViewHolder(View base) {

        mTVItem = (TextView) base.findViewById(R.id.order_title);
        mTVSubtitle = (TextView) base.findViewById(R.id.order_subtitle);
        mTVID = (TextView) base.findViewById(R.id.order_id);
        mTVState = (TextView) base.findViewById(R.id.order_state);
        mLYState = (LinearLayout) base.findViewById(R.id.order_state_bg);
        mTVDate = (TextView) base.findViewById(R.id.order_date);
        mTVLayout = (LinearLayout) base.findViewById(R.id.order_title_layout);

    }


}
