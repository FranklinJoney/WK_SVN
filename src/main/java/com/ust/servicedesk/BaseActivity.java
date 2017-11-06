package com.ust.servicedesk;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by u33664 on 8/7/2017.
 */

public class BaseActivity extends AppCompatActivity {
    Context context;
    String status;
    SharedPreferences WKPrefs;
    private static final String TAG = "BaseActivity";
    Boolean userValid = true;
    public static boolean cleared = false;

    @Override
    public void onResume() {
        super.onResume();
        this.context = getApplicationContext();
        WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        if (getTimeDifference(WKPrefs.getString("AppBackgroundTime", "")) || WKPrefs.getBoolean("appStart", true)) {
            if (validateUser()) {
                initiateSecurity();
            } else {
                WKPrefs.edit().putBoolean("appinBackground", false).apply();
                WKPrefs.edit().putString("AppBackgroundTime", "").apply();
                WKPrefs.edit().putBoolean("appStart", false).apply();
                SharedPreferences settings = getApplicationContext().getSharedPreferences("WKPrefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(BaseActivity.this, MicrosoftLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            WKPrefs.edit().putBoolean("appStart", false).apply();
            WKPrefs.edit().putBoolean("initialSetup", false).apply();
            WKPrefs.edit().putString("AppBackgroundTime", "").apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG," OnDestroyHappened");
    }

    @Override
    public void onTrimMemory(final int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            SharedPreferences WKPrefs = new ObscuredSharedPreferences(
                    getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
            WKPrefs.edit().putBoolean("appinBackground", true).apply();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            Calendar c = Calendar.getInstance();
            String currentDate = format.format(c.getTime());
            if (WKPrefs.getString("AppBackgroundTime", "").equalsIgnoreCase("")) {
                WKPrefs.edit().putString("AppBackgroundTime", currentDate).apply();
            }
        }
    }

    private boolean validateUser() {
        String loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.validateToken);
        userValid = true;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, loginURL,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    Log.i(TAG, "Response:" + response);
                    status = loginObject.getString("status");

                    if (status.equals(getResources().getString(R.string.ok))) {
                    } else {
                        userValid = false;
                    }
                } catch (Exception e) {
                    Common.dismissProgress();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error:" + error);
                        Common.dismissProgress();
                        if (error instanceof NoConnectionError) {
                        } else if (error instanceof TimeoutError) {
                        } else {
                        }

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", WKPrefs.getString("token", null));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

        return userValid;
    }


    private static boolean getTimeDifference(String pDate) {
        Boolean timeDelayStatus = false;
        if (!pDate.equalsIgnoreCase("")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            Calendar c = Calendar.getInstance();
            String formattedDate = format.format(c.getTime());
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(formattedDate);
                d2 = format.parse(pDate);
                long diff = d1.getTime() - d2.getTime();
                long diffSec = TimeUnit.MILLISECONDS.toSeconds(diff);
                if (diffSec >= 5) {
                    timeDelayStatus = true;
                } else {
                    timeDelayStatus = false;
                }

            } catch (ParseException e) {
            }
        }
        return timeDelayStatus;
    }

    private void initiateSecurity() {
        SharedPreferences WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        if (!WKPrefs.getBoolean("initialSetup", false)) {
            if (WKPrefs.getInt("securityMode", 0) == 0) {
                Intent intent = new Intent(BaseActivity.this, EnterPasscodeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
            if (WKPrefs.getInt("securityMode", 0) == 1) {
                Intent intent = new Intent(BaseActivity.this, TouchIdActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        }

    }
}


