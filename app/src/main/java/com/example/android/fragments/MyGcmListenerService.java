
package com.example.android.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
import com.example.android.fragments.entities.Store;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.gcm.GcmListenerService;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para las notificaciones
 */
public class MyGcmListenerService extends GcmListenerService {
    private Socket mSocket;
    private static final String TAG = "MyGcmListenerService";
    {
        try {
            mSocket = IO.socket("http://api.tiendosqui.com:9090");
        } catch (URISyntaxException e) {}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket.connect();
    }
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]



    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        try{
            notified(data.get("order_id")+"");
            mSocket.emit("delivered", data.get("order_id"));
        } catch (Exception e){

        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        sendBroadcastNotification(data);

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sound = prefs.getString("sound", "0");
        int s;
        if(sound.equals("0")){
            s = R.raw.cashregistercoins;
            Log.d("Sound","Sound 0");
        } else if(sound.equals("1")) {
            s = R.raw.woohoo01;
            Log.d("Sound","Sound 1");
        } else {
            s = R.raw.callingwhistle;
            Log.d("Sound","Sound 2");
        }
        Uri defaultSoundUri =  Uri.parse("android.resource://"
                + getBaseContext().getPackageName() + "/" + s);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Tiendosqui")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }

    public void sendBroadcastNotification(Bundle extras) {

        Log.d(TAG, "Sending broadcast notification" );
        Intent intentBroadcast = new Intent(Util.BROADCAST_MESSAGE_NAME);
        sendBroadcast(intentBroadcast);
    }

    public void notified(final String order_id){

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Util.NOTIFIED_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Notified","Notified "+order_id);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                } else if (error instanceof AuthFailureError) {

                } else if (error instanceof ServerError) {

                } else if (error instanceof NetworkError) {

                } else if (error instanceof ParseError) {

                }
            }
        }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                List<Store> stores = Store.listAll(Store.class);
                Store store = stores.get(0);
                params.put("order_id", order_id);
                return params;
            };
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(myReq);
    }
}
