package com.example.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.fragments.entities.Category;

import java.util.List;

/**
 * Created by principal on 13/03/2016.
 */
public class CategoryAdapter extends BaseAdapter{
    private Activity mContext;
    private List<Category> categories;
    private LayoutInflater mLayoutInflater = null;

    public CategoryAdapter (Activity context, List<Category>  list) {

        mContext = context;
        categories = list;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int pos) {
        return categories.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListViewHolderCategory viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.category_list_item, null);
            viewHolder = new ListViewHolderCategory(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ListViewHolderCategory) v.getTag();
        }

        viewHolder.mCategoryName.setText(categories.get(position).getName());


        return v;
    }
}

class ListViewHolderCategory {

    public TextView mCategoryName;


    public ListViewHolderCategory(View base) {
        mCategoryName  =  (TextView) base.findViewById(R.id.category_name);

    }
}
