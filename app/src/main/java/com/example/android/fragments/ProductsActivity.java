package com.example.android.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.example.android.fragments.entities.CategoryProduct;
import com.example.android.fragments.entities.Store;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsActivity extends AppCompatActivity {
    ListView listView;
    String category_id;
    ProductCategoryAdapter productCategoryAdapter;
    CategoryProduct currentProduct;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Productos");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category_id = extras.getString("category_id");
            Log.d("Categoria","Categoria; "+category_id);
        }

        listView = (ListView) findViewById(R.id.products_listview);
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Cargando productos...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest myReq = new StringRequest(Request.Method.POST,
                Util.CATEGORY_PRODUCTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        Gson gson = new Gson();
                        CategoryProduct[] productsa = gson.fromJson(response, CategoryProduct[].class);
                        ArrayList<CategoryProduct> listproducts = new ArrayList<>();
                        for (CategoryProduct c : productsa){
                            //c.save();
                            listproducts.add(c);
                        }
                        productCategoryAdapter = new ProductCategoryAdapter(ProductsActivity.this,listproducts);
                        listView.setAdapter(productCategoryAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.hide();

                AlertDialog alertDialog = new AlertDialog.Builder(ProductsActivity.this).create();
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
                    Toast.makeText(ProductsActivity.this,
                            "Time out",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ProductsActivity.this,
                            "AuthFailureError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(ProductsActivity.this,
                            "ServerError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ProductsActivity.this,
                            "NetworkError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ProductsActivity.this,
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
                params.put("category_id", category_id);
                return params;
            };
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("Response code", "Response code: " + mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(myReq);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);
                currentProduct = (CategoryProduct) productCategoryAdapter.getItem(position);
                boolean checked = checkbox.isChecked();
                if(checked){
                    checkbox.setChecked(false);
                    currentProduct.setStock(false);
                    currentProduct.save();
                    setInventory();
                } else {
                    checkbox.setChecked(true);
                    new PriceDialog().show(getSupportFragmentManager(), "");

                }
                Toast.makeText(ProductsActivity.this,
                        position+"",
                        Toast.LENGTH_LONG).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryProduct p = (CategoryProduct) productCategoryAdapter.getItem(position);
                if(p.getStock()){
                    currentProduct = p;
                    CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);
                    checkbox.setChecked(true);

                    new PriceDialog().show(getSupportFragmentManager(), "");
                }

                return true;
            }
        });

    }
    @SuppressLint("ValidFragment")
    public class PriceDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final EditText view = new EditText(ProductsActivity.this);
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            view.setText(((int) currentProduct.getPrice())+"");
            builder.setView(view)
                    // Add action buttons
                    .setTitle("Precio")
                    .setMessage("Precio de: "+currentProduct.getName())
                    .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            currentProduct.setPrice(Double.parseDouble(view.getText().toString()));
                            currentProduct.setStock(true);

                            setInventory();

                        }
                    });

            return builder.create();
        }
    }

    public void setInventory(){
        StringRequest myReq = new StringRequest(Request.Method.POST,
                Util.SET_INVENTORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



                AlertDialog alertDialog = new AlertDialog.Builder(ProductsActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Ha ocurrido un error. Vuelva a intentarlo.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ProductsActivity.this,
                            "Time out",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ProductsActivity.this,
                            "AuthFailureError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(ProductsActivity.this,
                            "ServerError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ProductsActivity.this,
                            "NetworkError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ProductsActivity.this,
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
                params.put("product_id", currentProduct.getProduct_id()+"");
                params.put("price",currentProduct.getPrice()+"");
                params.put("stock",currentProduct.getStock()+"");
                return params;
            };
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("Response code", "Response code: " + mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ProductsActivity.this);
        queue.add(myReq);
    }
}
