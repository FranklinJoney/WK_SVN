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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class PasswordResetActivity extends BaseActivity {

    Button resetPasswordBtn;
    EditText resetNewPassword, resetRetypePassword;
    public static final String KEY_ID = "id", KEY_TOKEN = "token", KEY_NEW_PASSWORD = "new_password";
    String status;
    int flag;
    SharedPreferences prefs;
    static AlertDialog dialogDetails;
    private boolean backToLogin = false;
    private static final String TAG = "PasswordResetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        Bundle bundle = getIntent().getExtras();
        backToLogin = bundle.getBoolean("backtologin");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Window window = PasswordResetActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        invalidateOptionsMenu();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(PasswordResetActivity.this,R.color.colorPrimary));
        }

        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        resetNewPassword = (EditText) findViewById(R.id.et_reset_new_password);
        resetNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetNewPassword.setFocusable(true);
                resetNewPassword.setFocusableInTouchMode(true);
                return false;
            }
        });

        resetRetypePassword = (EditText) findViewById(R.id.et_reset_retype_password);
        resetRetypePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetRetypePassword.setFocusable(true);
                resetRetypePassword.setFocusableInTouchMode(true);
                return false;
            }
        });


        resetPasswordBtn = (Button) findViewById(R.id.btn_reset_successfully);
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 0;
                if (resetNewPassword.getText().toString().length() == 0) {
                    resetNewPassword.setError(getString(R.string.password_empty));
                    flag = 1;
                }
                if (resetRetypePassword.getText().toString().length() == 0) {
                    resetRetypePassword.setError(getString(R.string.password_empty));
                    flag = 1;
                }

                if (flag == 0) {
                    try {
                        if (!resetNewPassword.getText().toString().matches(resetRetypePassword.getText().toString())) {
                            resetRetypePassword.setError(getString(R.string.password_mismatch));
                        } else {
                            if (!Common.isInternetConnected(PasswordResetActivity.this)) {
                                Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            } else {
                                Common.progressBar(PasswordResetActivity.this, getResources().getString(R.string.reset_success_progress));
                                resetPassword(view);
                            }
                        }

                    } catch (Exception e) {
                        Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    }
                } else {
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.home_page);
        if(backToLogin){
            Log.e(TAG,"Home"+backToLogin);
            item.setVisible(false);
        }else {
            Log.e(TAG,"Home"+backToLogin);
            item.setVisible(true);
        }
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
       if (id == R.id.home_page) {
            Intent intent = new Intent(PasswordResetActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void resetPassword(final View view) {
        String loginURL = null;
        String getUserIds = prefs.getString("emailID", null);
        String getUser = prefs.getString("user_id", null);
        String access_token = prefs.getString("token", null);
        String refresh_token = prefs.getString("refresh_token",null);
        boolean withoutToken = false;
        if(access_token == null || refresh_token == null){
            withoutToken = true;
            loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.reset_password_wot_URL);
        }else {
            withoutToken = false;
            loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.reset_password_URL);
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_ID, prefs.getString("requestId", null));
        params.put(KEY_NEW_PASSWORD, resetNewPassword.getText().toString());

        RequestQueue requestQueue = Volley.newRequestQueue(PasswordResetActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL,
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    Log.i(TAG, "Response:" + response);
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;

                    if (status.equals(getResources().getString(R.string.ok))) {

                        if (resultObject.getString("state").equalsIgnoreCase("Verified")) {
                            Common.dismissProgress();
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(PasswordResetActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);
                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            passwordMessage.setText(getResources().getString(R.string.reset_successfully));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                    if (backToLogin){
                                        Intent intent = new Intent(PasswordResetActivity.this, MicrosoftLogin.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(PasswordResetActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordResetActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                        } else {
                            Common.dismissProgress();
                            Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    } else {
                        Common.dismissProgress();
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordResetActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(PasswordResetActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordResetActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                                if (errorObject.getJSONObject(0).getString("error_message").contains("inactive")) {
                                    Intent intent = new Intent(PasswordResetActivity.this, PasswordResetIdentifyActivity.class);
                                    intent.putExtra("errormessage", errorObject.getJSONObject(0).getString("error_message"));
                                    startActivity(intent);
                                }
                            }
                        } else {
                            if (dataObject.has("error")) {
                                JSONObject errorObjects = dataObject.getJSONObject("error");
                                JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordResetActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(PasswordResetActivity.this,errorObjects1.getString("statusCode"), errorObjects1.getString("message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                        Intent intent = new Intent(PasswordResetActivity.this, PasswordResetIdentifyActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordResetActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);

                            } else {
                                Snackbar.make(view, getResources().getString(R.string.password_criteria_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            }
                        }


                    }
                } catch (Exception e) {
                    Common.dismissProgress();
                    Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                    Log.e(TAG, "Error:" + e);
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


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }
}
