package com.example.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.fragments.entities.Delivery;
import com.example.android.fragments.entities.Store;

/**
 * Cierra la sesión borrando todos los datos en la bd
 */
public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Store.deleteAll(Store.class);
        Delivery.deleteAll(Delivery.class);

        Intent intent = new Intent(Logout.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
