package com.example.android.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.example.android.fragments.entities.Category;
import com.example.android.fragments.entities.Delivery;
import com.example.android.fragments.entities.Store;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    ListView listView;
    CategoryAdapter categoryAdapter;
    ProgressDialog dialog;
    RequestQueue queue;
    StringRequest myReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Inventario");
        queue = Volley.newRequestQueue(InventoryActivity.this);
        listView = (ListView) findViewById(R.id.category_listview);
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Cargando categorias...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        myReq = new StringRequest(Request.Method.POST,
                Util.CATEGORIES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        Gson gson = new Gson();
                        Category[] categories = gson.fromJson(response, Category[].class);

                        for (Category c : categories){
                            c.save();
                        }
                        List<Category> categoriesdb = Delivery.listAll(Category.class, "NAME DESC");
                        categoryAdapter = new CategoryAdapter(InventoryActivity.this,categoriesdb);
                        listView.setAdapter(categoryAdapter);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



                AlertDialog alertDialog = new AlertDialog.Builder(InventoryActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(getString(R.string.nointernet));
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(InventoryActivity.this,
                            "Time out",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(InventoryActivity.this,
                            "AuthFailureError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(InventoryActivity.this,
                            "ServerError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(InventoryActivity.this,
                            "NetworkError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(InventoryActivity.this,
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
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("Response code","Response code: "+mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };


        queue.add(myReq);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(InventoryActivity.this,ProductsActivity.class);
                String category_id =  ((Category) categoryAdapter.getItem(position)).getCategory_id()+"";
                Log.d("Categoriaa","Categoriaa: "+category_id);
                i.putExtra("category_id",category_id);
                startActivity(i);
            }
        });
    }

}
