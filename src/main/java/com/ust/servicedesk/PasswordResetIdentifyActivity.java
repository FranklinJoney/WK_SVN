package com.ust.servicedesk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by u50289 on 6/22/2017.
 */

public class PasswordResetIdentifyActivity extends BaseActivity {

    Spinner spinner;
    Button resetBtn;
    EditText loginName;
    String status, passwordType, passwordTypeSubString;
    public static final String KEY_LOGIN = "login";
    LinearLayout mainLayout;
    SharedPreferences prefs;
    private static final String TAG = "PasswordResetIdentifyActivity";
    static AlertDialog dialogDetails;
    private boolean backToLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_identify);

        mainLayout = (LinearLayout) findViewById(R.id.ll_identify_password_main_layout);
        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        resetBtn = (Button) findViewById(R.id.btn_reset_continue);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginName.getText().toString().length() != 0) {
                    if (!Common.isInternetConnected(PasswordResetIdentifyActivity.this)) {
                        Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    } else {
                        Common.progressBar(PasswordResetIdentifyActivity.this, getResources().getString(R.string.reset_identify_progress));
                        loginIdentify(view);
                    }
                } else {
                    loginName.setError(getString(R.string.invalid_credentials));
                }
            }
        });

        Window window = PasswordResetIdentifyActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(PasswordResetIdentifyActivity.this,R.color.colorPrimary));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loginName = (EditText) findViewById(R.id.et_login_username);
        loginName.setText(prefs.getString("loginUserName", null));
        if(loginName.getText().toString().length() > 0){
            loginName.setEnabled(false);
            backToLogin = false;
        }
        else{
            backToLogin = true;
            loginName.setEnabled(true);
        }
        loginName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loginName.setFocusable(true);
                loginName.setFocusableInTouchMode(true);
                return false;
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.get("errormessage") != null) {
                Snackbar.make(mainLayout, bundle.get("errormessage").toString(), Snackbar.LENGTH_LONG)
                        .setAction(null, null).show();
            }
        }


        String[] paths = {getResources().getString(R.string.NAPassword)};

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PasswordResetIdentifyActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
      /*  spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_wo_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) // Press Back Icon
        {
            finish();
            return true;
        }
       /* if (id == R.id.home_page) {
            Intent intent = new Intent(PasswordResetIdentifyActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void loginIdentify(final View view) {
        String loginURL = null;
        String getUserIds = prefs.getString("emailID", null);
        String getUser = prefs.getString("user_id", null);
        String access_token = prefs.getString("token", null);
        String refresh_token = prefs.getString("refresh_token",null);
        boolean withoutToken = false;
        //params.put("refresh_token", prefs.getString("refreshToken", null));
        if( refresh_token == null || access_token == null){
            withoutToken = true;
            loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.login_identify_wot_URL);
        }else {
            withoutToken = false;
            loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.login_identify_URL);
        }
       /* String[] getUserIds = prefs.getString("emailID", null).split("@");
        String getid = getUserIds[0];
        params.put("user_id", getid);
        params.put("access_token", prefs.getString("token", null));
        params.put("refresh_token", prefs.getString("refreshToken", null));*/

        passwordType = spinner.getSelectedItem().toString();
        passwordTypeSubString = (passwordType.substring(passwordType.lastIndexOf("-") + 1)).toLowerCase();

        Map<String, String> params = new HashMap<String, String>();
        if (withoutToken){
            String[] userId = loginName.getText().toString().split("@");
            params.put(KEY_LOGIN, userId[0]);
        }else
        params.put(KEY_LOGIN, passwordTypeSubString + "\\" + loginName.getText().toString());

        RequestQueue requestQueue = Volley.newRequestQueue(PasswordResetIdentifyActivity.this);
        final boolean finalWithoutToken = withoutToken;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL,
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    Log.i(TAG, "Response:" + response);
                    status = loginObject.getString("status");
                    JSONArray errorObjects;

                    if (status.equals(getResources().getString(R.string.ok))) {
                        JSONObject dataObject = loginObject.getJSONObject("data");
                        JSONObject resultObject = dataObject.getJSONObject("result");
                        JSONArray questions = resultObject.getJSONArray("questions");
                        prefs.edit().putString("requestId", dataObject.getString("requestId")).apply();
                        for (int i = 0; i < questions.length(); i++) {
                            prefs.edit().putString("questionID" + i, questions.getJSONObject(i).getString("id")).apply();
                            prefs.edit().putString("question" + i, questions.getJSONObject(i).getString("question")).apply();
                        }
                        Intent intent = new Intent(PasswordResetIdentifyActivity.this, VerifySecurityQuestionActivity.class);
                        intent.putExtra("backtologin", backToLogin);
                        Common.dismissProgress();
                        startActivity(intent);

                    } else {
                        Common.dismissProgress();
                        if ((errorObjects = loginObject.getJSONArray("data")).length() != 0) {
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(PasswordResetIdentifyActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);
                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            passwordMessage.setText(Common.formatErrorMessage(errorObjects.getJSONObject(0).getString("error_code"), errorObjects.getJSONObject(0).getString("error_message")));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordResetIdentifyActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                        } else {
                            Snackbar.make(view, getResources().getString(R.string.login_failed_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    }


                } catch (Exception e) {
                    Common.dismissProgress();
                    Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error:" + error);
                        Common.dismissProgress();
                        if (error instanceof NoConnectionError) {
                            Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(view, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }
                    }
                }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                if(finalWithoutToken){
                    //params.put("Content-Type", "application/json");
                }else {
                    String[] getUserIds = prefs.getString("emailID", null).split("@");
                    String getid = getUserIds[0];
                    params.put("user_id", getid);
                    params.put("access_token", prefs.getString("token", null));
                    params.put("refresh_token", prefs.getString("refreshToken", null));
                }
               // params.put("Content-Type", "application/json");
                Log.d("TAG", "HEADER" + params);
                return params;
            }

            ;
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

}
