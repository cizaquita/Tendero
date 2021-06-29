
package com.example.android.fragments;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragmento que muestra el detalle del pedido
 */
public class ArticleFragment extends Fragment{
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    private CountDownTimer t;
    private TextView timer;
    private String order_id;
    boolean pagado;
    Button confirmarb ;
    Button rechazarb ;
    Button pagadob;
    Button inconvenienteb;
    Button detailsb;
    Delivery delivery;
    OnOrderChangeListener mCallback;
    private  TextView delivery_costTV;
    private Store store;
    ListView listView;
    static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
    private TextView order_subtotal;
    private TextView order_total;
    private TextView payment;
    private TextView state;
    List<Delivery> deliveries;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://api.tiendosqui.com:9090");
        } catch (URISyntaxException e) {}
    }
    public interface OnOrderChangeListener {
        public void OnOrderChangeListener(int position, int state);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.article_view, container, false);
        List<Delivery> deliveries = Delivery.listAll(Delivery.class,"ORDERID DESC");

        if(deliveries.size() > 0){
            mCurrentPosition = 0;
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Inicializar elementos graficos con su nformación

        confirmarb = (Button) getActivity().findViewById(R.id.confirmar);
        rechazarb = (Button) getActivity().findViewById(R.id.rechazar);
        //pagadob = (Button) getActivity().findViewById(R.id.btn_pagado);
        //pagadob.setEnabled(false);
        listView = (ListView) getActivity().findViewById(R.id.product_list);
        order_subtotal = (TextView) getActivity().findViewById(R.id.order_subtotal);
        order_total = (TextView) getActivity().findViewById(R.id.order_total);
        delivery_costTV = (TextView) getActivity().findViewById(R.id.delivery_cost);
        payment = (TextView) getActivity().findViewById(R.id.payment);
        state = (TextView) getActivity().findViewById(R.id.estado);
        inconvenienteb = (Button) getActivity().findViewById(R.id.inconveniente);
        timer = (TextView) getActivity().findViewById(R.id.order_timer);
        detailsb = (Button) getActivity().findViewById(R.id.details);

        confirmarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeliveryDialogFragment confirmDeliveryDialogFragment = new ConfirmDeliveryDialogFragment();
                confirmDeliveryDialogFragment.show(getFragmentManager(),"g");
            }
        });
        rechazarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RejectDeliveryDialogFragment rejectDeliveryDialogFragment = new RejectDeliveryDialogFragment();
                rejectDeliveryDialogFragment.show(getFragmentManager(),"r");
            }
        });
        /*pagadob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePagado();
                PayConfirmDialogFragment pcdf = new PayConfirmDialogFragment();
                pcdf.show(getFragmentManager(),"p");
            }
        });*/
        inconvenienteb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProblemDialogFragment problemDialogFragment = new ProblemDialogFragment();
                problemDialogFragment.show(getFragmentManager(),"i");
            }
        });

        detailsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsDialogFragment detailsDialogFragment = new DetailsDialogFragment();
                detailsDialogFragment.show(getFragmentManager(),"d");
            }
        });
        Bundle args = getArguments();
        if (args != null) {
            //Vista para telefonos

        } else if (mCurrentPosition != -1) {

            if (mCurrentPosition == 0) {
                updateArticleView(0);
            }
        }

        List<Store> stores = Store.listAll(Store.class);
        if(stores.size() > 0){
            store = stores.get(0);
        }

        mSocket.connect();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            mCallback = (OnOrderChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    /**
     * Trae la informacón del pedido que el usuario ha escogido
     * @param position posición del pedido en la lista
     */

    public void updateArticleView(int position) {

        deliveries = Delivery.listAll(Delivery.class, "ORDERID DESC");
        if(deliveries.size() == 0){
            return;
        }
        delivery = deliveries.get(position);
        Log.d("DELIVERY TOSTRING: ",delivery.toString());
        delivery = Delivery.findById(Delivery.class, delivery.getId());
        Log.d("Delivery","Delivery ID "+delivery.getId());
        order_id = ""+delivery.getOrder_id();

        List<Product> products = delivery.getProducts();
        ProductAdapter productAdapter = new ProductAdapter(getActivity(),products);
        listView.setAdapter(productAdapter);


        double total = 0;

        for (Product p : products){
            total += p.getTotal();
        }



        NumberFormat nf = NumberFormat.getInstance();
        order_subtotal.setText("$"+nf.format(total));
        order_total.setText("$"+nf.format(total));
        payment.setText("$"+nf.format(Integer.parseInt(delivery.getPayment())));
        delivery_costTV.setText("$"+nf.format(delivery.getDelivery_cost()));
        //Hora

        /*try {
            Date date = format.parse(delivery.getDate());

            SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm a");
            String datetime = dateformat.format(date);


            hour.setText(datetime);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        Log.d("Estado",delivery.getState()+" "+delivery.getOrder_id());
        state.setText("Pendiente");
        //pagadob.setEnabled(false);
        if(delivery.getState() == 1 || delivery.getState() == 2){
            confirmarb.setEnabled(false);
            rechazarb.setEnabled(false);
            //pagadob.setEnabled(true);
            if (delivery.getApp_id() == 2){
                inconvenienteb.setEnabled(false);
                //inconvenienteb.setBackgroundColor();
                Log.d("xDB setEnable",""+delivery.toString());
            }else
                inconvenienteb.setEnabled(true);

            ///////////////////////
            if(delivery.getState() == 1){
                state.setText("Confirmado");
            } else {
                state.setText("Rechazado");
            }
        } else {
            confirmarb.setEnabled(true);
            rechazarb.setEnabled(true);
            //pagadob.setEnabled(false);
        }

        //Pagado
        /*if (delivery.getPagado() == 0){
            pagadob.setText("No pagado");
        }else
            pagadob.setText("Pagado");*/

        //Timer

        timer.setText("00:00");
        if(t != null){
            t.cancel();
        }
        t = null;
        Log.d("Fecha default","Fecha "+delivery.getStart_date());

        if(delivery.getEnd_date() != null){
            Date end_date = new Date();

            int secondsBetween = (int) ((delivery.getEnd_date().getTime() - end_date.getTime()) / 1000);
            int result = (int) (secondsBetween*1000);
            Log.d("Fecha default","Fecha resultado "+result);
            if(result > 0){
                startTimer(result, 1000);
            }
        }


        mCurrentPosition = position;
    }

    /**
     * Iniciar el contador al confirmar un pedido
     * @param finish
     * @param tick
     */
    public void startTimer(final long finish, long tick) {
        if(t != null){
            t.cancel();
        }
        t = new CountDownTimer(finish, tick) {

            public void onTick(long millisUntilFinished)
            {
                long remainedSecs = millisUntilFinished/1000;
                long min = remainedSecs/60;
                long sec = remainedSecs%60;
                String mins = "";
                String secs = "";

                if(min < 10){  mins = "0"+min; } else { mins = min+""; }
                if(sec < 10){  secs = "0"+sec;  } else {  secs = sec+""; }

                timer.setText(""+(mins)+":"+(secs));
            }

            public void onFinish(){
                timer.setText("00:00");
                cancel();
            }
        }.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    /*public void updatePagado(){
        if (delivery.getPagado() == 0){
            delivery.setPagado(1);
            pagadob.setText("Pagado");
        }else {
            delivery.setPagado(0);
            pagadob.setText("No pagado");
        }
        Log.d("DELIVERY LOG: ", delivery.toString());
        delivery.save();
    }*/

    /**
     * Confirmar pedido
     * @param time tiempo de entrega
     * @return
     */
    public String confirmOrder(final String time){
        final ProgressDialog dialog = new ProgressDialog(getActivity()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Confirmando pedido...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = "http://api.tiendosqui.com/confirm/";
        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int temp_delivery_time = Integer.parseInt(time);
                        dialog.hide();
                        confirmarb.setEnabled(false);
                        rechazarb.setEnabled(false);
                        //pagadob.setEnabled(true);
                        mCallback.OnOrderChangeListener(mCurrentPosition,1);
                        Calendar date = Calendar.getInstance();
                        long t= date.getTimeInMillis();
                        Date end_date =new Date(t + (temp_delivery_time * ONE_MINUTE_IN_MILLIS));
                        Date start_date =new Date();

                        //Delivery dt = Delivery.findById(Delivery.class, delivery.getId());

                        delivery.setStart_date(start_date);
                        delivery.setEnd_date(end_date);
                        delivery.setDelivery_time(temp_delivery_time);
                        delivery.setState(1);
                        delivery.save();

                        startTimer(temp_delivery_time*60000, 1000);

                        try{
                            mSocket.emit("confirmed", delivery.getOrder_id());
                        } catch (Exception e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.hide();
                        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Ha ocurrido un error. Vuelva a intentarlo.");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });
                        alertDialog.show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getActivity(),
                                    "Time out",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getActivity(),
                                    "AuthFailureError",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getActivity(),
                                    "ServerError",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getActivity(),
                                    "NetworkError",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getActivity(),
                                    "ParseError",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id", order_id);
                params.put("time", time);
                return params;

            };
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        queue.add(myReq);
        return null;
    }


    /**
     * Rechazar pedido
     * @param message motivo del rechazo
     * @return
     */

    public String rejectOrder(final String message){
        final ProgressDialog dialog = new ProgressDialog(getActivity()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Actulizando pedido...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = "http://api.tiendosqui.com/reject/";
        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        confirmarb.setEnabled(false);
                        rechazarb.setEnabled(false);
                        mCallback.OnOrderChangeListener(mCurrentPosition, 2);
                        delivery.setState(2);

                        try{
                            mSocket.emit("rejected", delivery.getOrder_id());
                        } catch (Exception e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.hide();
                        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Ha ocurrido un error. Vuelva a intentarlo.");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id", order_id);
                params.put("message", message);
                return params;
            };
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(myReq);
        return null;

    }




    @SuppressLint("ValidFragment")
    /**
     * Dialogo para confirmar
     */
    public class ConfirmDeliveryDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("¿En cuánto tiempo le llegará el pedido al cliente?")
                    .setItems(R.array.time_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ArrayList<String> times = new ArrayList<String>();
                            times.add("1");
                            times.add("5");
                            times.add("10");
                            times.add("15");
                            times.add("20");
                            confirmOrder(times.get(which));

                        }});
            return builder.create();
        }
    }



    @SuppressLint("ValidFragment")
    /**
     * Dialogo para rechazar
     */
    public class RejectDeliveryDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("¿Por qué estas rechazando el pedido?")
                    .setItems(R.array.reject_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String[] array = getResources().getStringArray(R.array.reject_array);
                            String message = array[which];
                            rejectOrder(message);

                        }});
            return builder.create();
        }
    }



    @SuppressLint("ValidFragment")
    /**
     * Confirmar paagdo
     */
    public class PayConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.dialog_pagado, null);
            TextView view1 = (TextView) view.findViewById(R.id.togglePagado);
            if (delivery.getPagado() == 0){
                view1.setText("No pagado");
            } else
                view1.setText("Pagado");
            //Log.d("Telephone","Telephone "+store.getTelephone());
            builder.setView(view)
                    // Add action buttons
                    .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PayConfirmDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }


    @SuppressLint("ValidFragment")
    /**
     * Dialogo para inconvenientes
     */
    public class ProblemDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.dialog_problem, null);
            TextView view1 = (TextView) view.findViewById(R.id.telephone);
            view1.setText(store.getTelephone());
            Log.d("Telephone","Telephone "+store.getTelephone());
            builder.setView(view)
                    // Add action buttons

                    .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ProblemDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }



    @SuppressLint("ValidFragment")
    /**
     * Dialogo para ver los detalles del cliente
     */
    public class DetailsDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.dialog_details, null);
            TextView client_name = (TextView) view.findViewById(R.id.client_name);
            TextView client_telephone = (TextView) view.findViewById(R.id.client_telephone);
            TextView client_address = (TextView) view.findViewById(R.id.client_address);
            TextView client_comments = (TextView) view.findViewById(R.id.client_comments);
            TextView address_details = (TextView) view.findViewById(R.id.address_details);

            client_address.setText(delivery.getAddress());
            client_comments.setText(delivery.getComments());
            address_details.setText(delivery.getAddress_details());
            client_telephone.setText(delivery.getClient_telephone());

            client_name.setText(delivery.getClient_name());
            Log.d("Telephone","Telephone "+delivery.getClient_telephone());
            builder.setView(view)
                    // Add action buttons

                    .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DetailsDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}