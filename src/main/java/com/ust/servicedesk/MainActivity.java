package com.ust.servicedesk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
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
import com.ust.servicedesk.model.GlobalMessageModel;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.Miscellaneous;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.ust.servicedesk.GlobalMessagesActivity.global_message_list;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView incident_size, request_size, global_messages_size;
    LinearLayout changePasswordLayout, incidentLayout, requestLayout, globalMessagesLayout;
    SharedPreferences WKPrefs;
    CoordinatorLayout homeLayout;
    String status;
    int backButtonCount;
    Dialog globalMessageDialog;
    private static final String TAG = "MainActivity";
    Handler handler = new Handler();
    private String Result;
    TimeZone tzEST = TimeZone.getTimeZone("EST");
    TimeZone tzUTC = TimeZone.getTimeZone("UTC");
    static AlertDialog dialogDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backButtonCount = 0;
        WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));


        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimary));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        incident_size = (TextView) findViewById(R.id.incident_size);
        request_size = (TextView) findViewById(R.id.request_size);
        global_messages_size = (TextView) findViewById(R.id.global_size);

          //getIncidents();
//        getRequests();

        homeLayout = (CoordinatorLayout) findViewById(R.id.HomeLayout);
        changePasswordLayout = (LinearLayout) findViewById(R.id.ll_change_password);
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                intent.putExtra((getResources().getString(R.string.password)), getResources().getString(R.string.change_reset_password));
                startActivity(intent);
            }
        });

        incidentLayout = (LinearLayout) findViewById(R.id.ll_incidents);
        incidentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,IncidentActivity.class);
                startActivity(intent);

            }
        });

        requestLayout = (LinearLayout) findViewById(R.id.ll_requests);
        requestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                startActivity(intent);
            }
        });

        globalMessagesLayout = (LinearLayout) findViewById(R.id.ll_global_messages);
        globalMessagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GlobalMessagesActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelephonyManager telMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                int simState = telMgr.getSimState();
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    String serviceNumber = getServiceNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(serviceNumber));
                    startActivity(intent);
                } else {
                    Snackbar.make(v, getString(R.string.network_check), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem nav_user = menu.findItem(R.id.nav_user);

        /*if (WKPrefs.getString("emailID", null).length() > 24) {
            String emailId = WKPrefs.getString("emailID", null).substring(0, 21) + "...";
            Log.i(TAG, emailId);
            nav_user.setTitle(emailId);
        } else {*/
            nav_user.setTitle(WKPrefs.getString("emailID", null));
       // }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Show global messages");
                if((WKPrefs.getBoolean("appStart",true))){
                    Log.e(TAG,"NoIdea");
                    getGlobalMessages();
                }else{
                    Log.e(TAG,"TillNoIdea");
                }
                //showGlobalMessages();
            }
        }, 2000);


    }


    @Override
    public void onBackPressed() {
        backButtonCount++;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backButtonCount >= 2) {
                backButtonCount = 0;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        backButtonCount = 0;
                    }
                }, 5000);
                Toast.makeText(this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_user) {
        } else if (id == R.id.nav_update_question) {
            Intent intent = new Intent(MainActivity.this, UpdateSecurityQuestionActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_change_passsword) {
            Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            intent.putExtra((getResources().getString(R.string.password)), getResources().getString(R.string.change_reset_password));
            startActivity(intent);

        } else if (id == R.id.nav_change_passsword) {

        } else if (id == R.id.nav_logout) {

            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View dialogview = inflater.inflate(R.layout.logout_popup, null);


            TextView text = (TextView) dialogview.findViewById(R.id.alert_message);
            text.setText("Are you sure you want to logout?");

            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MainActivity.this);
            dialogbuilder.setView(dialogview);
            dialogDetails = dialogbuilder.create();
            dialogDetails.show();
            dialogDetails.setCancelable(true);
            dialogDetails.setCanceledOnTouchOutside(true);

            TextView yes = (TextView) dialogview.findViewById(R.id.tv_yes);
            TextView no = (TextView) dialogview.findViewById(R.id.tv_no);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                    dialogDetails.dismiss();
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDetails.dismiss();
                }
            });



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public String getServiceNumber() {

        if (WKPrefs.getBoolean("VIP", false))
            return getString(R.string.helpdesk_contact_vip);
        else
            return getString(R.string.helpdesk_contact_normal);

    }



    public void getGlobalMessages()
    {

        String url= getString(R.string.base_URL) + getString(R.string.global_message_url);

        global_message_list.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.i("TAG", "Response:" + response);
                    JSONObject userObject = response;
                    JSONArray data;
                    JSONObject globalObject, comments;
                    Result = userObject.getString("status");
                    if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                        if (userObject.getJSONArray("data").length() != 0) {
                            data = userObject.getJSONArray("data");
                            for (int i = 0; i < (data.length()); i++) {
                                globalObject = data.getJSONObject(i);
                            String convertedTime =    Common.shiftTimeZone(globalObject.getString("createdOn"),tzUTC,tzEST);
                                global_message_list.add(new GlobalMessageModel(globalObject.getString("messageId"),
                                        globalObject.getString("shortdescription"), globalObject.getString("description"),
                                        Miscellaneous.parseOnCreate(convertedTime)));
                            }
                        }

                        dialogDetails = null;
                        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                        View dialogview = inflater.inflate(R.layout.alert_popup, null);

                        TextView text = (TextView) dialogview.findViewById(R.id.alert_message);
                        String lastMessage = global_message_list.get(global_message_list.size() - global_message_list.size()).getGlobalmessage_head();
                        Log.e(TAG,"GlobalMessage"+lastMessage);
                        text.setText(lastMessage);

                        TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogDetails.dismiss();
                            }
                        });
                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MainActivity.this);
                        dialogbuilder.setView(dialogview);
                        dialogDetails = dialogbuilder.create();
                        dialogDetails.show();
                        dialogDetails.setCancelable(false);
                        dialogDetails.setCanceledOnTouchOutside(false);
                        if(globalMessageDialog != null)
                            globalMessageDialog.show();
                    }else {
                        Common.dismissProgress();

                    }

                } catch (Exception e) {
                    Common.dismissProgress();
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
                }
                if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                    Common.dismissProgress();
                } else {
                    Common.dismissProgress();
                    //Toast.makeText(context, getResources().getString(R.string.noGlobalMessages), Toast.LENGTH_LONG);
                }
            }
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  pDialog.hide();
                        //Common.dismissProgress();
                        Log.e(TAG, "Error:" + error);
                        if (error instanceof NoConnectionError) {

                        } else if (error instanceof TimeoutError) {

                        } else {

                        }

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String[] getUserIds = WKPrefs.getString("emailID",null).split("@");
                String getid = getUserIds[0];
                params.put("user_id", getid);
                params.put("access_token",WKPrefs.getString("token",null));
                params.put("refresh_token",WKPrefs.getString("refreshToken",null));
                params.put("Content-Type","application/json");
                Log.d(TAG,"ShareRecords"+ params);
                return params;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

    }
    @Override
    public void onPause() {
        super.onPause();
        if (globalMessageDialog != null && globalMessageDialog.isShowing()) {
            globalMessageDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (globalMessageDialog != null && globalMessageDialog.isShowing()) {
            globalMessageDialog.dismiss();
        }
    }


    public void logout() {

        try {
            SharedPreferences settings = getApplicationContext().getSharedPreferences("WKPrefs", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            clearCookies(getApplicationContext());
            trimCache(getApplicationContext());
            Intent intent = new Intent(MainActivity.this, MicrosoftLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);

            }
        } catch (Exception e) {

        }
    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_wo_home, menu);

        return true;
    }



    private void getRequests() {
        String newURl = getString(R.string.base_URL_1_5) + getString(R.string.get_request_list);

        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", WKPrefs.getString("emailID", null));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, newURl,
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.i(TAG, "Response:" + response);
                    JSONObject userObject = response;
                    if (userObject.getString("Result").equalsIgnoreCase(getString(R.string.ok))) {
                        if (userObject.getJSONArray("RequestList").length() != 0) {
                            request_size.setText(userObject.getJSONArray("RequestList").length() + " " + getString(R.string.requests));
                        } else {
                            request_size.setText("No " + getString(R.string.requests));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error:" + error);
                    }
                }) {


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }
}
