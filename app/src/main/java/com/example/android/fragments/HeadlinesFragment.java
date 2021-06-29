/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.android.fragments.entities.Delivery;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra la lista de pedidos
 */

public class HeadlinesFragment extends ListFragment {
    OnHeadlineSelectedListener mCallback;

    ListView listView;
    List<Delivery> deliveries;
    BaseAdapter adapter;
    public static int selectedItem = 0;
    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(int position, View v);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deliveries = Delivery.listAll(Delivery.class,"ORDERID DESC");
        adapter = new DeliveryAdapter(getActivity(), deliveries);
        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        listView = getListView();
        registerForContextMenu(listView);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
      // getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        mCallback.onArticleSelected(position,v);
        selectedItem = position;
        ((BaseAdapter)getListView().getAdapter()).notifyDataSetChanged();

    }

    /**
     * Al confirmar o rechazar actuliza el tag del pedido
     * @param position
     */
    public void updateOrderView(int position) {

        deliveries = Delivery.listAll(Delivery.class,"ORDERID DESC");
        adapter = new DeliveryAdapter(getActivity(), deliveries);
        setListAdapter(adapter);

    }

    /**
     * Update by Motero
     */
    public void updateViewMotero(String moteroName) {
        Log.d("xDeli name", moteroName);
        deliveries = Delivery.listAll(Delivery.class,"ORDERID DESC");
        List<Delivery> moteroDeliveries = new ArrayList<Delivery>();
        for (Delivery deli : deliveries){
            if (deli.getMotero() != null && deli.getMotero().getName().equals(moteroName)){
                Log.d("xDeli all", deli.toString());
                moteroDeliveries.add(deli);
            }
        }
        if (moteroDeliveries.size() > 0){
            adapter = new DeliveryAdapter(getActivity(), moteroDeliveries);
            setListAdapter(adapter);
        }else {
            new AlertDialog.Builder(this.getContext())
                    .setTitle("No hay resultados")
                    .setMessage("El motero no tiene domicilios asignados")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            adapter = new DeliveryAdapter(getActivity(), deliveries);
                            setListAdapter(adapter);
                        }
                    })/*
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    /**
     * Actualizar toda la lista
     * @param position
     * @param state
     */
    public void refreshOrderView(int position,int state) {

        Delivery d = deliveries.get(position);
        d.setState(state);
        d.save();
        Delivery ddb = Delivery.findById(Delivery.class, d.getId());
        ddb.setState(state);
        ddb.save();
        Log.d("Guardando", "Guardando "+d.getState()+" "+d.getOrder_id()+" "+position);
        ((BaseAdapter)getListView().getAdapter()).notifyDataSetChanged();

    }
}