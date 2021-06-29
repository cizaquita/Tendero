package com.example.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.fragments.entities.Delivery;
import com.example.android.fragments.entities.Store;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Medicitas SAS on 10/04/2016.
 */

public class RegisterActivity extends AppCompatActivity {
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private UserRegisterTask mAuthTask = null;

    private EditText txtName, txtAddress, txtLat, txtLon, txtPassword,
                     txtConfirmPassword, txtShopName, txtUsername, txtEmail,
                     txtTelephone, txtDeliveryCost, txtSquareRange;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Campos del form
        txtName = (EditText) findViewById(R.id.reg_name);
        txtAddress = (EditText) findViewById(R.id.reg_address);
        txtLat = (EditText) findViewById(R.id.reg_latitude);
        txtLon = (EditText) findViewById(R.id.reg_longitude);
        txtPassword = (EditText) findViewById(R.id.reg_password);
        txtConfirmPassword = (EditText) findViewById(R.id.reg_confirm_password);
        txtShopName = (EditText) findViewById(R.id.reg_shop_name);
        txtUsername = (EditText) findViewById(R.id.reg_username);
        txtEmail = (EditText) findViewById(R.id.reg_email);
        txtTelephone = (EditText) findViewById(R.id.reg_telephone);
        txtDeliveryCost = (EditText) findViewById(R.id.reg_delivery_cost);
        txtSquareRange =  (EditText) findViewById(R.id.reg_square_range);

        Button btnSingUp = (Button) findViewById(R.id.email_sign_up_button);
        Button btnBackToLogin = (Button) findViewById(R.id.btn_back_to_login);;
        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptToRegister();
            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Vuelve a la ventana de login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);

        mProgressView = findViewById(R.id.register_progress);

    }

    private void attemptToRegister(){

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        txtDeliveryCost.setError(null);
        txtAddress.setError(null);
        txtConfirmPassword.setError(null);
        txtEmail.setError(null);
        txtLat.setError(null);
        txtLon.setError(null);
        txtName.setError(null);
        txtPassword.setError(null);
        txtShopName.setError(null);
        txtSquareRange.setError(null);
        txtTelephone.setError(null);
        txtUsername.setError(null);

        // Store values at the time of the login attempt.
        final String deliveryCost = txtDeliveryCost.getText().toString();
        final String address = txtAddress.getText().toString();
        final String confirmPassword = txtConfirmPassword.getText().toString();
        final String password = txtPassword.getText().toString();
        final String email = txtEmail.getText().toString();
        final String lat = txtLat.getText().toString();
        final String lon = txtLon.getText().toString();
        final String name = txtName.getText().toString();
        final String shopName = txtShopName.getText().toString();
        final String squareRange = txtSquareRange.getText().toString();
        final String telephone = txtTelephone.getText().toString();
        final String username = txtUsername.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check all fields are not empty
        if (!TextUtils.isEmpty(deliveryCost)){
            txtDeliveryCost.setError("Campo no puede ser vacio");
            focusView = txtDeliveryCost;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(address)){
            txtAddress.setError("Campo no puede ser vacio");
            focusView = txtAddress;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(confirmPassword)){
            txtConfirmPassword.setError("Campo no puede ser vacio");
            focusView = txtConfirmPassword;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(email)){
            txtEmail.setError("Campo no puede ser vacio");
            focusView = txtEmail;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(lat)){
            txtLat.setError("Campo no puede ser vacio");
            focusView = txtLat;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(lon)){
            txtLon.setError("Campo no puede ser vacio");
            focusView = txtLon;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(name)){
            txtName.setError("Campo no puede ser vacio");
            focusView = txtName;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)){
            txtPassword.setError("Campo no puede ser vacio");
            focusView = txtPassword;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(shopName)){
            txtShopName.setError("Campo no puede ser vacio");
            focusView = txtShopName;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(squareRange)){
            txtSquareRange.setError("Campo no puede ser vacio");
            focusView = txtSquareRange;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(telephone)){
            txtTelephone.setError("Campo no puede ser vacio");
            focusView = txtTelephone;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(username)){
            txtUsername.setError("Campo no puede ser vacio");
            focusView = txtUsername;
            cancel = true;
        }/*


        //Chewck password and confirm password
        if (password.equals(confirmPassword)){
            //New String if the password is OK
            final String passwordOk = confirmPassword;
        }else{
            txtPassword.setError(getString(R.string.error_invalid_password));
            txtConfirmPassword.setError(getString(R.string.error_invalid_password));
            focusView = txtPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            String url = "http://api.tiendosqui.com/shop/";
            StringRequest myReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Test GSON","#Success "+response);

                            try {
                                JSONObject jsonObject = new JSONObject( response );
                                int status = jsonObject.getInt("status");

                                if(status == 0){
                                    Gson gson = new Gson();
                                    Store.deleteAll(Store.class);
                                    Delivery.deleteAll(Delivery.class);
                                    Store store = gson.fromJson(response, Store.class);
                                    store.save();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Log.d("Test","Store "+store.getTelephone());
                                } else {
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            showProgress(false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Test GSON","#Error");
                    showProgress(false);
                    //Log.d("Test GSON",error.getMessage());
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    //mTextView.setText("That didn't work!");


                }
            }) {

                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String token = preferences.getString("token", "");

                    params.put("username", email);
                    params.put("password", password);
                    params.put("device", token);
                    return params;
                };
            };
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

            queue.add(myReq);
        }*/
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                //txtPassword.setError(getString(R.string.error_incorrect_password));
                //txtPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
