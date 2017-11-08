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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by u50289 on 6/22/2017.
 */

public class PasswordChangeActivity extends BaseActivity {

    Spinner spinner;
    Button changedPasswordBtn;
    EditText oldPassword, newPassword, retypePassword;
    int flag;
    static String status;
    public static final String KEY_LOGIN = "login", KEY_OLD_PASSWORD = "old_password", KEY_NEW_PASSWORD = "new_password";
    SharedPreferences prefs;
    private static final String TAG = "PasswordChangeActivity";
    static AlertDialog dialogDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Window window = PasswordChangeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(PasswordChangeActivity.this,R.color.colorPrimary));
        }

        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        oldPassword = (EditText) findViewById(R.id.et_old_password);
        oldPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                oldPassword.setFocusable(true);
                oldPassword.setFocusableInTouchMode(true);
                return false;
            }
        });

        newPassword = (EditText) findViewById(R.id.et_new_password);
        newPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                newPassword.setFocusable(true);
                newPassword.setFocusableInTouchMode(true);
                return false;
            }
        });

        retypePassword = (EditText) findViewById(R.id.et_retype_password);
        retypePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                retypePassword.setFocusable(true);
                retypePassword.setFocusableInTouchMode(true);
                return false;
            }
        });

        String[] paths = {getResources().getString(R.string.NAPassword)};

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PasswordChangeActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        changedPasswordBtn = (Button) findViewById(R.id.btn_changed_password);
        changedPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 0;
                if (oldPassword.getText().toString().length() == 0) {
                    oldPassword.setError(getString(R.string.password_empty));
                    flag = 1;
                }
                if (newPassword.getText().toString().length() == 0) {
                    newPassword.setError(getString(R.string.password_empty));
                    flag = 1;
                }
                if (retypePassword.getText().toString().length() == 0) {
                    retypePassword.setError(getString(R.string.password_empty));
                    flag = 1;
                }
                if (flag == 0) {
                    try {
                        if (!newPassword.getText().toString().matches(retypePassword.getText().toString())) {
                            retypePassword.setError(getString(R.string.password_mismatch));
                        } else {
                            if (!Common.isInternetConnected(PasswordChangeActivity.this)) {
                                Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            } else {
                                Common.progressBar(PasswordChangeActivity.this, getResources().getString(R.string.changing_password_progress));
                                changePassword(view);
                            }
                        }

                    } catch (Exception e) {
                        Snackbar.make(view, getResources().getString(R.string.wrong_message), Snackbar.LENGTH_LONG)
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
            Intent intent = new Intent(PasswordChangeActivity.this, MainActivity.class);
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

    public void changePassword(final View view) {
        String loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.change_password_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_LOGIN, prefs.getString("loginUserName", null));
        params.put(KEY_OLD_PASSWORD, oldPassword.getText().toString());
        params.put(KEY_NEW_PASSWORD, newPassword.getText().toString());
        JSONObject jsonObject = new JSONObject(params);
        Log.e(TAG,"ChangePassWord"+ jsonObject.toString() );
        RequestQueue requestQueue = Volley.newRequestQueue(PasswordChangeActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL,
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    status = loginObject.getString("status");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;
                    Log.i(TAG, "Response:" + response);


                    if (status.equals(getResources().getString(R.string.ok))) {

                        if (resultObject.has("state")) {
                            if (resultObject.getString("state").equalsIgnoreCase("In progress")) {
                                prefs.edit().putString("request_id", resultObject.getString("request_id")).apply();
                                checkpasswordStatus(view);
                            } else {
                                Common.dismissProgress();
                                Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            }
                        }


                        if (resultObject.has("status")) {
                            Common.dismissProgress();
                            if (resultObject.getString("status").equalsIgnoreCase("error")) {
                                if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                                    if (errorObject.getJSONObject(0).has("error_message")) {

                                        dialogDetails = null;
                                        LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                                        View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                        TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                        passwordMessage.setText(Common.formatErrorMessage(PasswordChangeActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                        TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogDetails.dismiss();
                                            }
                                        });
                                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
                                        dialogbuilder.setView(dialogview);
                                        dialogDetails = dialogbuilder.create();
                                        dialogDetails.show();
                                        dialogDetails.setCancelable(false);
                                        dialogDetails.setCanceledOnTouchOutside(false);
                                        if (errorObject.getJSONObject(0).getString("error_message").contains("inactive")) {
                                            Intent intent = new Intent(PasswordChangeActivity.this, PasswordResetIdentifyActivity.class);
                                            intent.putExtra("errormessage", errorObject.getJSONObject(0).getString("error_message"));
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        Common.dismissProgress();
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {

                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(PasswordChangeActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                                if (errorObject.getJSONObject(0).getString("error_message").contains("inactive")) {
                                    Intent intent = new Intent(PasswordChangeActivity.this, PasswordResetIdentifyActivity.class);
                                    intent.putExtra("errormessage", errorObject.getJSONObject(0).getString("error_message"));
                                    startActivity(intent);
                                }
                            }
                        } else {
                            if (dataObject.has("error")) {
                                JSONObject errorObjects = dataObject.getJSONObject("error");
                                JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(PasswordChangeActivity.this,errorObjects1.getString("statusCode"), errorObjects1.getString("message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                        Intent intent = new Intent(PasswordChangeActivity.this, PasswordResetIdentifyActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String[] getUserIds = prefs.getString("emailID",null).split("@");
                String getid = getUserIds[0];
                params.put("user_id", getid);
                params.put("access_token",prefs.getString("token",null));
                params.put("refresh_token",prefs.getString("refreshToken",null));
                //params.put("Content-Type","application/json");
                Log.d(TAG,"ShareRecords"+ params);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    public void checkpasswordStatus(final View view) {
        String loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.status_check_URL);

        JSONObject jsonObject = new JSONObject();
        try {
            String[] getUserIds = prefs.getString("emailID",null).split("@");
            String getid = getUserIds[0];
            jsonObject.put("user_id", getid);
            jsonObject.put("request_id",prefs.getString("request_id", null));
            Log.e(TAG,"Status_Json_Body"+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(PasswordChangeActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;
                    Log.i(TAG, "Response " + response);

                    if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                        Common.dismissProgress();
                        if (errorObject.getJSONObject(0).has("error_message")) {
                            if (errorObject.getJSONObject(0).getString("error_message").contains("*")) {
//                                int indexOfStar = errorObject.getJSONObject(0).getString("error_message").lastIndexOf("*") + 1;
//                                int indexOfStop = errorObject.getJSONObject(0).getString("error_message").indexOf('(');
                                String message = errorObject.getJSONObject(0).getString("error_message");
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(message);
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
//                                        Intent intent = new Intent(PasswordChangeActivity.this, ChangePasswordActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                            } else {
                                Snackbar.make(view, errorObject.getJSONObject(0).getString("error_message"), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            }
                        }

                    } else if (dataObject.has("error")) {
                        JSONObject errorObjects = dataObject.getJSONObject("error");
                        JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                        dialogDetails = null;
                        LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                        View dialogview = inflater.inflate(R.layout.alert_popup, null);
                        TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                        passwordMessage.setText(Common.formatErrorMessage(PasswordChangeActivity.this,errorObjects1.getString("statusCode"), errorObjects1.getString("message")));
                        TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogDetails.dismiss();
                            }
                        });
                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
                        dialogbuilder.setView(dialogview);
                        dialogDetails = dialogbuilder.create();
                        dialogDetails.show();
                        dialogDetails.setCancelable(false);
                        dialogDetails.setCanceledOnTouchOutside(false);
                    } else if (status.equals(getResources().getString(R.string.ok))) {

                        if (resultObject.has("state")) {
                            if (resultObject.getString("state").equalsIgnoreCase("Completed With Failure")) {

                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(PasswordChangeActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                            } else if (resultObject.getString("state").equalsIgnoreCase("Completed With Success")) {

                                Common.dismissProgress();
                                AlertDialog dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(PasswordChangeActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(getResources().getString(R.string.changed_successfully));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(PasswordChangeActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(PasswordChangeActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                            } else if (resultObject.getString("state").contains("Progress") && resultObject.getJSONArray("errors").length() == 0) {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        // this code will be executed after 10 seconds
                                        checkpasswordStatus(view);
                                    }
                                }, 10000);
                            }
                        }
                    }
                } catch (Exception e) {
                    Common.dismissProgress();
                    Log.i(TAG, "Error:" + e);
                    Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String[] getUserIds = prefs.getString("emailID",null).split("@");
                String getid = getUserIds[0];
                headers.put("user_id", getid);
                headers.put("access_token",prefs.getString("token",null));
                headers.put("refresh_token",prefs.getString("refreshToken",null));
                //headers.put("Content-Type", "application/json");
                Log.i(TAG, "SharedHeaders:" + headers);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

}

