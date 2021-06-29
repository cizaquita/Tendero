package com.example.android.fragments;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
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
import com.example.android.fragments.entities.Product;
import com.example.android.fragments.entities.Store;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Servicio para mantener el heeart beat activo
 */
public class BeatService extends Service {
    public BeatService() {
    }

    private static Timer timer = new Timer();
    private Context ctx;
    public static boolean IS_SERVICE_RUNNING = false;
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intentt, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Servicio de domicilios activo.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle("Tiendosqui")
                .setContentText("Domicilios activos")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1234, noti);

        return Service.START_STICKY;
    }
    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new mainTask(), 0, 60000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            Log.d("Servicio","Servicio internet");
            StringRequest myReq = new StringRequest(Request.Method.POST,
                    "http://api.tiendosqui.com/online/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Test GSON","Respuesta " +response);



                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                }
            }) {

                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    List<Store> stores = Store.listAll(Store.class);

                    if(stores.size() > 0){
                        Store store = stores.get(0);
                        params.put("shopkeeper_id", store.getProfile_id()+"");
                    }

                    return params;
                };
            };

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(myReq);
        }
    }


    public void onDestroy()
    {
        //Toast.makeText(this, "Service destroyed", Toast.LENGTH_LONG).show();
        Log.d("Servicio","Servicio destroyed");
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Servicio","Servicio onUnbind");
        return super.onUnbind(intent);
    }
}
