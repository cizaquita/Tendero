package com.example.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.fragments.entities.Store;

import java.util.List;

/**
 * BroadcastReceiver para las alertas cuando el estado de la conexión cambie
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    Context mContext;
    private Store store;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String status = NetworkUtil.getConnectivityStatusString(context);

        Log.e("Receiver ", "" + status);
        Intent intentBroadcast = new Intent(Util.BROADCAST_INTERNET_NAME);
        context.sendBroadcast(intentBroadcast);
        if (status.equals("Not connected to Internet")) {
            Log.e("Receiver ", "not connction");// your code when internet lost
           // Toast.makeText(mContext,  "Sin conexión a internet",  Toast.LENGTH_LONG).show();
            mContext.stopService(new Intent(context, BeatService.class));



        } else {
            Log.e("Receiver ", "connected to internet");//your code when internet connection come back

            //Toast.makeText(mContext, "Conectado a internet",  Toast.LENGTH_LONG).show();
            List<Store> stores = Store.listAll(Store.class);

            if(stores.size() > 0){
                store = stores.get(0);
                if(store.getOpen() == 1) {
                    mContext.startService(new Intent(context, BeatService.class));

                }
            }

        }

    }
}