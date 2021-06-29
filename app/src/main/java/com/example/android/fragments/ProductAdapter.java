package com.example.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.fragments.entities.Product;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Medicitas SAS on 08/01/2016.
 */
public class ProductAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Product> products;
    private LayoutInflater mLayoutInflater = null;

    public ProductAdapter (Activity context, List<Product>  list) {

        mContext = context;
        products = list;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int pos) {
        return products.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.product_list_item, null);
            viewHolder = new ListViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ListViewHolder) v.getTag();
        }

        NumberFormat nf = NumberFormat.getInstance();

        viewHolder.mProductName.setText(products.get(position).getName());
        viewHolder.mProductCount.setText(products.get(position).getCount()+"");
        viewHolder.mProductPrice.setText("$"+nf.format(products.get(position).getUnit_price()));
        viewHolder.mProductTotal.setText("$"+nf.format(products.get(position).getTotal()));

        return v;
    }
}

class ListViewHolder {

    public TextView mProductName;
    public TextView mProductCount;
    public TextView mProductPrice;
    public TextView mProductTotal;

    public ListViewHolder(View base) {

        mProductName  =  (TextView) base.findViewById(R.id.product_name);
        mProductCount =  (TextView) base.findViewById(R.id.product_count);
        mProductPrice =  (TextView) base.findViewById(R.id.product_price);
        mProductTotal =  (TextView) base.findViewById(R.id.product_total);

    }
}
