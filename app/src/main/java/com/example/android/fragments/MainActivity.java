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

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.fragments.entities.Delivery;
import com.example.android.fragments.entities.Motero;
import com.example.android.fragments.entities.Product;
import com.example.android.fragments.entities.Store;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity 
        implements HeadlinesFragment.OnHeadlineSelectedListener,ArticleFragment.OnOrderChangeListener, CompoundButton.OnCheckedChangeListener{


    String url = "http://api.tiendosqui.com/deliveries/";
    String url_open = "http://api.tiendosqui.com/open/";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Store store;
    Switch delivery_switch;
    Spinner spinnerMoteros;
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);

        List<Store> stores = Store.listAll(Store.class);
        spinnerMoteros = (Spinner) findViewById(R.id.spinner);

        if(stores.size() == 0){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            store = stores.get(0);
        }

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            HeadlinesFragment firstFragment = new HeadlinesFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }


        updateDB();
        delivery_switch = (Switch) findViewById(R.id.switch1);
        if(store != null){
            if(store.getOpen() == 0){

                delivery_switch.setChecked(false);
            } else {

                delivery_switch.setChecked(true);
                startService(new Intent(MainActivity.this, BeatService.class));
            }
        }

        delivery_switch.setOnCheckedChangeListener(this);

        boolean online = isOnline();
        if(online){
            findViewById(R.id.change_state_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sin_conexion).setVisibility(View.GONE);
        } else {
            findViewById(R.id.change_state_layout).setVisibility(View.GONE);
            findViewById(R.id.sin_conexion).setVisibility(View.VISIBLE);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        ImageView settingsButton = (ImageView) findViewById(R.id.settings_image);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,PreferenceActivity.class);
                startActivity(i);
            }
        });
        Button btnPayAll = (Button) findViewById(R.id.btn_pay_all);
        btnPayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerMoteros.getCount() > 0){
                    getDeliveriesByMotero(spinnerMoteros.getSelectedItem().toString());
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_info);
                    alertDialog.setMessage("No hay información que cargar.");

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // todo: algo
                                }
                            });
                    alertDialog.show();
                }
            }
        });
        ImageView refreshButton = (ImageView) findViewById(R.id.refresh_image);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delivery.deleteAll(Delivery.class);
                refreshDB();
            }
        });
    }

    public void onArticleSelected(int position, View vv) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
        ArticleFragment articleFrag = (ArticleFragment)
                getSupportFragmentManager().findFragmentById(R.id.article_fragment);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView(position);

        } else {

            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article

            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        }
    }

    @Override
    public void OnOrderChangeListener(int position,int state) {

        HeadlinesFragment headlinesFragment = (HeadlinesFragment)
                getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);

        if (headlinesFragment != null) {
            headlinesFragment.refreshOrderView(position,state);
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        registerReceiver(gpsBRec, new IntentFilter(
                Util.BROADCAST_MESSAGE_NAME));
        registerReceiver(internetBroadcastReceiver, new IntentFilter(
                Util.BROADCAST_INTERNET_NAME));

        boolean online = isOnline();
        if(online){
            findViewById(R.id.change_state_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sin_conexion).setVisibility(View.GONE);
        } else {
            findViewById(R.id.change_state_layout).setVisibility(View.GONE);
            findViewById(R.id.sin_conexion).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(gpsBRec);
            unregisterReceiver(internetBroadcastReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    private BroadcastReceiver gpsBRec = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            updateDB();
        }
    };
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private BroadcastReceiver internetBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean online = isOnline();
            if(online){
                findViewById(R.id.change_state_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.sin_conexion).setVisibility(View.GONE);
            } else {
                findViewById(R.id.change_state_layout).setVisibility(View.GONE);
                findViewById(R.id.sin_conexion).setVisibility(View.VISIBLE);
            }
        }
    };
    private void updateDB(){
        ///////////////////////////////////////////////////////
        getExternalDeliveries();
        ///////////////////////////////////////////////////////
        List<Store> stores = Store.listAll(Store.class);

        if(stores.size() == 0){
            return;
        }

        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Test GSON","Respuesta " +response);
                        Gson gson = new Gson();
                        Delivery[] deliveries = gson.fromJson(response, Delivery[].class);

                        for (Delivery d : deliveries){
                            List<Delivery> exits = Delivery.find(Delivery.class, "ORDERID = ?", new String[] {d.getOrder_id()+""});
                            if(exits.size() > 0){


                            } else {
                                d.save();
                                ArrayList<Product> products = d.getApiProducts();
                                for (Product p : products){

                                    p.setDelivery(d);
                                    p.save();

                                }
                                Log.d("Test GSON",d.getApiProducts().size()+"");
                            }
                        }

                        HeadlinesFragment headlinesFragment = (HeadlinesFragment)
                                getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);

                        if (headlinesFragment != null) {
                            headlinesFragment.updateOrderView(0);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this,
                            "Time out",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this,
                            "AuthFailureError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this,
                            "ServerError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this,
                            "NetworkError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this,
                            "ParseError",
                            Toast.LENGTH_LONG).show();
                }
            }
        }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                List<Store> stores = Store.listAll(Store.class);
                Store store = stores.get(0);
                params.put("shopkeeper_id", store.getProfile_id()+"");
                Log.d("xDeliveries LOG: ", store.getProfile_id()+"");
                return params;
            };
        };

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(myReq);

    }
    private void refreshDB(){
        getExternalDeliveries();
        List<Store> stores = Store.listAll(Store.class);
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Cargando...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if(stores.size() == 0){
            dialog.hide();
            return;
        }

        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Test GSON","Respuesta " +response);
                        Gson gson = new Gson();
                        Delivery[] deliveries = gson.fromJson(response, Delivery[].class);

                        for (Delivery d : deliveries){
                            d.save();
                            Log.d("xDB DeliDate",d.getDate());
                            ArrayList<Product> products = d.getApiProducts();
                            for (Product p : products){

                                p.setDelivery(d);
                                p.save();

                            }
                            Log.d("Test GSON",d.getApiProducts().size()+"");

                        }
                        dialog.hide();
                        HeadlinesFragment headlinesFragment = (HeadlinesFragment)
                                getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);

                        if (headlinesFragment != null) {
                            headlinesFragment.updateOrderView(0);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this,
                            "Time out",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this,
                            "AuthFailureError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this,
                            "ServerError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this,
                            "NetworkError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this,
                            "ParseError",
                            Toast.LENGTH_LONG).show();
                }
            }
        }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                List<Store> stores = Store.listAll(Store.class);
                Store store = stores.get(0);
                params.put("shopkeeper_id", store.getProfile_id()+"");
                return params;
            };
        };

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(myReq);

    }

    private void getDeliveriesByMotero(String moteroName){
        final String moteroNameEx = moteroName;
        if (moteroName.equals("Todos los pedidos")){
            refreshDB();
        }else{
            getExternalDeliveries();

            HeadlinesFragment headlinesFragment = (HeadlinesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);

            if (headlinesFragment != null) {
                headlinesFragment.updateViewMotero(moteroNameEx);
            }
        };
    }
    /**
     * Get Deliveries from external DataBase (serviPunto)
     */
    public void getExternalDeliveries(){
        //OrdersProvider/tbl_orders
        Uri salesUri = Uri.parse("content://com.micaja.servipunto.providers.SalesProvider/tbl_sales");
        ContentProviderClient salesCPC = getContentResolver().acquireContentProviderClient(salesUri);
        //ClientProvider/tblm_client
        Uri clientUri = Uri.parse("content://com.micaja.servipunto.providers.ClientProvider/tblm_client");
        ContentProviderClient clientCPC = getContentResolver().acquireContentProviderClient(clientUri);
        //DeliveryProvider/tbl_delivery
        Uri deliveryUri = Uri.parse("content://com.micaja.servipunto.providers.DeliveryProvider/tbl_delivery");
        ContentProviderClient deliveryCPC = getContentResolver().acquireContentProviderClient(deliveryUri);
        //ProductProvider/tblm_product
        Uri productUri = Uri.parse("content://com.micaja.servipunto.providers.ProductProvider/tblm_product");
        ContentProviderClient productCPC = getContentResolver().acquireContentProviderClient(productUri);
        //SalesDetailsProvider/tbl_sales_details
        Uri salesDetUri = Uri.parse("content://com.micaja.servipunto.providers.SalesDetailsProvider/tbl_sales_details");
        ContentProviderClient salesDetCPC = getContentResolver().acquireContentProviderClient(salesDetUri);

        Cursor salesCursor = null,
                clientCursor = null,
                deliveryCursor = null,
                productCursor = null,
                salesDetCursor = null,
                testProductCursor = null;

        try {
            salesCursor = salesCPC.query(salesUri, null, null, null, null);
            clientCursor = clientCPC.query(clientUri, null, null, null, null);
            deliveryCursor = deliveryCPC.query(deliveryUri, null, null, null, null);
            productCursor = productCPC.query(productUri, null, null, null, null);
            salesDetCursor = salesDetCPC.query(salesDetUri, null, null, null, null);
        }catch (RemoteException | NullPointerException | SQLiteException e){
            Log.d("xDB ERROR:", e.toString());
            return;
        }
        ///////////////////////////////////////////////////////DOMICILIARIOS!!!//////////////////////
        String[] delivery_men = null;
        int cont;
        if (deliveryCursor.moveToFirst()){
            delivery_men = new String[deliveryCursor.getCount()+1];
            delivery_men[0] = "Todos los pedidos";
            cont = 1;
            do{
                Log.d("xDB deliver DEBUG", "\n\t ID: " + deliveryCursor.getString(deliveryCursor.getColumnIndex("delivery_id")) +
                                                 "\n\t name: " + deliveryCursor.getString(deliveryCursor.getColumnIndex("name")));
                                                //"\n\t trans_delivery: " + deliveryCursor.getString(deliveryCursor.getColumnIndex("trans_delivery")));
                delivery_men[cont] =  deliveryCursor.getString(deliveryCursor.getColumnIndex("name"));
                cont++;
            }while(deliveryCursor.moveToNext());
        }
        if (delivery_men != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, delivery_men);
            spinnerMoteros.setAdapter(adapter);
        }else{
            // TODO: poner cuando no trae datos
        }
        /////////////////////////////////////////////////////DOMICILIARIOS!!/////////////////////////
        /*if (salesDetCursor.moveToFirst()){
            do{
                Log.d("xDB saldet DEBUG", "\n\t ID: " + salesDetCursor.getString(salesDetCursor.getColumnIndex("sales_details_id")) +
                        "\n\t sale_id: " + salesDetCursor.getString(salesDetCursor.getColumnIndex("sale_id")) +
                        "\n\t product_id: " + salesDetCursor.getString(salesDetCursor.getColumnIndex("product_id")) +
                        "\n\t count: " + salesDetCursor.getString(salesDetCursor.getColumnIndex("count")) +
                        "\n\t price: " + salesDetCursor.getString(salesDetCursor.getColumnIndex("price")));
            }while(salesDetCursor.moveToNext());
        }*/
        Date now = new Date();
        int order_id = 1000;
        //Log.d("xDB order_id",""+Integer.parseInt(new Timestamp(now.getTime()).toString()));

        if (salesCursor.moveToFirst()){
            do {
                String trans_delivery = salesCursor.getString(salesCursor.getColumnIndex("trans_delivery"));
                if (Integer.parseInt(trans_delivery) > 0) {
                    //Get Selected DATA
                    Delivery deliveryExt = new Delivery();
                    deliveryExt.setTitle("Pedido por ServiPunto"); // "Pedido por ServiPunto" defaullt from external database
                    deliveryExt.setState(1);
                    deliveryExt.setApp_id(2);
                    order_id++;//Integer.parseInt(c.getString(c.getColumnIndex("sale_id"))); // SALE_ID FROM SALES
                    deliveryExt.setOrder_id(order_id);
                    String date = salesCursor.getString(salesCursor.getColumnIndex("date_time")); // DATE_TIME FROM SALES
                    Log.d("xDB datetime",date);
                    deliveryExt.setDate(date);

                    //CLIENT ID FROM SALES
                    String client_id = salesCursor.getString(salesCursor.getColumnIndex("client_id"));
                    //get client provider data from external db
                    if (clientCursor.moveToFirst()) {
                        do {
                            //Select client_id and iterate clientCursor to compare
                            if (client_id.equals(clientCursor.getString(clientCursor.getColumnIndex("cedula")))) {
                                String address = clientCursor.getString(clientCursor.getColumnIndex("address")); // ADDRESS FROM CLIENT
                                deliveryExt.setAddress(address);
                                deliveryExt.setAddress_details("N/A"); // N/A FROM NONE
                                String client_telephone = clientCursor.getString(clientCursor.getColumnIndex("telephone")); // TELEPHONE FROM CLIENT
                                deliveryExt.setClient_telephone(client_telephone);
                                String client_name = clientCursor.getString(clientCursor.getColumnIndex("name")); // NAME FROM CLIENT
                                deliveryExt.setClient_name(client_name);
                            }
                        } while (clientCursor.moveToNext());
                    }

                    deliveryExt.setComments("N/A"); // N/A FROM NONE
                    deliveryExt.setPayment("0"); // N/A FROM NONE
                    deliveryExt.setDelivery_time(5); // N/A DEFAULT 5 FROM NONE

                    deliveryExt.setStart_date(new Date()); // FECHA_INICIAL FROM SALES************ TODO: TRY TO PARSE TEXT TO DATE NEED TO SEE THE DATE FORMAT THAT I GET FROM EXTERNAL DB
                    deliveryExt.setEnd_date(new Date()); // FECHA_FINAL FROM SALES
                    float delivery_cost = Float.parseFloat(salesCursor.getString(salesCursor.getColumnIndex("net_amount"))); // NET_AMOUNT FROM SALES
                    deliveryExt.setDelivery_cost(0);
                    deliveryExt.setPagado(0); // DEFAULT 0 no pagado - TODO: SEND DATA when is payed

                    //DELIVERY_ID FROM SALES
                    String delivery_code = salesCursor.getString(salesCursor.getColumnIndex("delivery_code"));
                    if (deliveryCursor.moveToFirst()) {
                        do {

                            //Select client_id and iterate clientCursor to compare
                            if (delivery_code.equals(deliveryCursor.getString(deliveryCursor.getColumnIndex("cedula")))) {
                                deliveryExt.setMotero(new Motero(deliveryCursor.getString(deliveryCursor.getColumnIndex("name")), "N/A", true, 0));
                            }
                        } while (deliveryCursor.moveToNext());
                    }

                    Motero moteroEx = deliveryExt.getMotero();
                    if (moteroEx.getName() == null) {
                        moteroEx.setName("N/A");
                    }
                    moteroEx.save();
                    //Save delivery before create and save products to assign it to the delivery
                    deliveryExt.save();
                    ///////////////////////////////////////
                    ArrayList<Product> products = new ArrayList<Product>(); // PRODUCT FROM PRODUCT

                    String sale_id_sale = salesCursor.getString(salesCursor.getColumnIndex("invoice_number"));

                    Log.d("xDB sale_id_sale", "from sales: " + sale_id_sale);

                    if (salesDetCursor.moveToFirst()) {
                        do {
                            String sale_id_sal_detail = salesDetCursor.getString(salesDetCursor.getColumnIndex("sale_id"));
                            //Select sale_id from sales and sale_id from sales_details
                            if (sale_id_sale.equals(sale_id_sal_detail)) {
                                String product_id_sal_detail = salesDetCursor.getString(salesDetCursor.getColumnIndex("product_id"));
                                Log.d("xDB product_id", "from sales_detail: " + product_id_sal_detail);
                                //product ID, count, price
                                Product product = new Product();
                                product.setDelivery(deliveryExt);
                                product.setUnit_price(Double.parseDouble(salesDetCursor.getString(salesDetCursor.getColumnIndex("price"))));
                                product.setCount(Integer.parseInt(salesDetCursor.getString(salesDetCursor.getColumnIndex("count"))));

                                try{
                                    testProductCursor = productCPC.query(productUri, null, "barcode == ?", new String[] {product_id_sal_detail}, null);
                                    //Log.d("xDB BARCODE:", testProductCursor + "");
                                    /**for (int i = 0; i < testProductCursor.getColumnCount(); i++){
                                        Log.d("xDB BARCODE:", "Columna: " + i + " " + testProductCursor.getColumnName(i));
                                    }*/
                                }catch (RemoteException ex){
                                    Log.d("xDB ERROR:", ex.toString());
                                }

                                    /*if (testProductCursor != null){
                                        product.setName(productCursor.getString(productCursor.getColumnIndex("name")));
                                        product.save();
                                        products.add(product);
                                    }*/
                                if (productCursor.moveToFirst()) {
                                    do {
                                        String product_id_product = productCursor.getString(productCursor.getColumnIndex("barcode"));
                                        if (product_id_sal_detail.equals(product_id_product)) {
                                            product.setName(productCursor.getString(productCursor.getColumnIndex("name")));
                                            product.save();
                                            products.add(product);
                                        }
                                    } while (productCursor.moveToNext());
                                }
                            }
                        } while (salesDetCursor.moveToNext());
                    }
                    deliveryExt.setProducts(products);
                    deliveryExt.save();
                    Log.d("xDeliveries Save: ", deliveryExt.toString());
                }

                /////////////////////////////////////////////////////////////////////
            }while(salesCursor.moveToNext());
        }

                /*
                    private int order_id;
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
                    //Don Colmado = 1; App = 0;
                    private int app_id;

                    //Motero del domicilio
                    private Motero motero;
                */
        /**
         "products": [
         {
         "count": 4,
         "name": "Agua cristal",
         "unit_price": 1200
         }
         ]
         */

        /*while(c.moveToNext()){
            //String descripcion = c.getString(c.getColumnIndexOrThrow());
            Log.d("xDATABASE C LOG:", "Data de tabla descripcion: " + c.getString(c.getColumnIndex("net_amount")));
        }
        for (int i = 0; i < c.getColumnCount(); i++){
            Log.d("xDATABASE C LOG:", "Columna: " + i + "" + c.getColumnName(i));
        }
        //Log.d("xDATABASE C LOG:", "NotificationUri: " + c.getNotificationUri());
        Log.d("xDATABASE C LOG:", "Cuenta del cursor: " + c.getCount());*/


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int open = 0;
        if(isChecked == true){
            startService(new Intent(MainActivity.this, BeatService.class));
            open = 1;
        } else {
            stopService(new Intent(MainActivity.this, BeatService.class));
        }
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Actualizando estado...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final int finalOpen = open;
        StringRequest myReq = new StringRequest(Request.Method.POST,
                url_open,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response code","Response code: 1");
                        store.setOpen(finalOpen);
                        store.save();
                        dialog.hide();

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("¡Éxito!");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
                        alertDialog.setMessage("Estado actualizado con éxito.");

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.hide();

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Ha ocurrido un error. Vuelva a intentarlo.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if(finalOpen == 0){
                                    delivery_switch.setOnCheckedChangeListener(null);
                                    delivery_switch.setChecked(true);
                                    delivery_switch.setOnCheckedChangeListener(MainActivity.this);
                                } else {
                                    delivery_switch.setOnCheckedChangeListener(null);
                                    delivery_switch.setChecked(false);
                                    delivery_switch.setOnCheckedChangeListener(MainActivity.this);
                                }
                            }
                        });
                alertDialog.show();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this,
                            "Time out",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this,
                            "AuthFailureError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this,
                            "ServerError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this,
                            "NetworkError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this,
                            "ParseError",
                            Toast.LENGTH_LONG).show();
                }
            }
        }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                List<Store> stores = Store.listAll(Store.class);
                Store store = stores.get(0);
                params.put("shopkeeper_id", store.getProfile_id()+"");
                params.put("open", finalOpen+"");
                return params;
            };
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("Response code","Response code: "+mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(myReq);
    }
}