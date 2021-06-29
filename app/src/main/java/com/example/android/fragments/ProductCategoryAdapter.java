package com.example.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.android.fragments.entities.CategoryProduct;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by principal on 13/03/2016.
 */
public class ProductCategoryAdapter extends BaseAdapter {
    private Activity mContext;
    private List<CategoryProduct> products;
    private LayoutInflater mLayoutInflater = null;

    public ProductCategoryAdapter (Activity context, List<CategoryProduct>  list) {

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
        ListViewHolderProductCategory viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.products_list_item, null);
            viewHolder = new ListViewHolderProductCategory(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ListViewHolderProductCategory) v.getTag();
        }
        CategoryProduct product = products.get(position);
        NumberFormat nf = NumberFormat.getInstance();
        viewHolder.mProductPrice.setText("$"+nf.format(product.getPrice()));
        viewHolder.mProductName.setText(products.get(position).getName());

        if(product.getStock()){
            viewHolder.checkBox.setChecked(true);
        } else{
            viewHolder.checkBox.setChecked(false);
        }
        return v;
    }


}
class ListViewHolderProductCategory {


    public TextView mProductName;
    public TextView mProductPrice;
    public CheckBox checkBox;
    public ListViewHolderProductCategory(View base) {

        mProductName  =  (TextView) base.findViewById(R.id.product_name);
        mProductPrice =  (TextView) base.findViewById(R.id.product_price);
        checkBox = (CheckBox) base.findViewById(R.id.checkBox);

    }
}
