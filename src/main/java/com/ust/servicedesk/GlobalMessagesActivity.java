package com.ust.servicedesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import com.ust.servicedesk.adapters.GlobalMessageAdapter;
import com.ust.servicedesk.model.GlobalMessageModel;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.Miscellaneous;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class GlobalMessagesActivity extends BaseActivity {

    public static ArrayList<GlobalMessageModel> global_message_list= new ArrayList<GlobalMessageModel>();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    LinearLayout activity_incident_detail, incidentCommentsLayout;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    RecyclerView.Adapter adapter;
    private String toolbarText,Result;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences prefs;
    CardView cardView;
    TimeZone tzEST = TimeZone.getTimeZone("EST");
    TimeZone tzUTC = TimeZone.getTimeZone("UTC");

    private static final String TAG = GlobalMessagesActivity.class.getSimpleName();

    private String setGlobalHeader = "Duis aute irure dolor in reprehenderit";
    private String setGlobalDate = "2017-04-20";
    private String setGlobalTime = "8.45 AM EST";
    private String setGlobalSubText = "Duis aute irure dolor in reprehenderit in voluptate velit\n" +
            "esse cillum dolore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_messages);

        Window window = GlobalMessagesActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(GlobalMessagesActivity.this,R.color.colorPrimary));
        }
        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        setSupportActionBar(toolbar);
        context = getApplicationContext();
        cardView = (CardView) findViewById(R.id.card_viewX);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //adapter = new GlobalMessageAdapter(global_message_list,context);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        Common.progressBar(GlobalMessagesActivity.this, getResources().getString(R.string.loading_global_message));


        getGlobalMessages();

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

                    Common.dismissProgress();

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

                                  cardView.setVisibility(View.GONE);
                                  mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                                    globalObject = data.getJSONObject(i);
                                    String convertedTime =    Common.shiftTimeZone(globalObject.getString("createdOn"),tzUTC,tzEST);
                                    global_message_list.add(new GlobalMessageModel(globalObject.getString("messageId"),
                                            globalObject.getString("shortdescription"), globalObject.getString("description"),
                                            Miscellaneous.parseOnCreate(convertedTime)));


                                }
                            }
                        }else {
                                Common.dismissProgress();

                            }

                    } catch (Exception e) {
                        cardView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        Common.dismissProgress();
                        Log.e(TAG, getResources().getString(R.string.error_message), e);
                    }
                    if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                        //   pDialog.hide();
                        Common.dismissProgress();
                        adapter = new GlobalMessageAdapter(global_message_list, context);
                        recyclerView.setAdapter(adapter);

                    } else {
                        // pDialog.hide();
                        Common.dismissProgress();
                        cardView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        Toast.makeText(context, getResources().getString(R.string.noIncidents), Toast.LENGTH_LONG);
                    }
                }
            },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //  pDialog.hide();
                            //Common.dismissProgress();
                            Log.e(TAG, "Error:" + error);
                            cardView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            if (error instanceof NoConnectionError) {
                                Snackbar.make(cardView, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            } else if (error instanceof TimeoutError) {
                                Snackbar.make(cardView, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            } else {
                                Snackbar.make(cardView, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
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
                    params.put("Content-Type","application/json");
                    Log.d(TAG,"ShareRecords"+ params);
                    return params;
                }

            };

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);

    }

  /*  private List<GlobalMessageModel> getTopListIncidents() {

        global_message_list = new ArrayList<GlobalMessageModel>();
        for (int i=0;i<=10;i++) {
            GlobalMessageModel globalMessageModel = new GlobalMessageModel();
            globalMessageModel.setGlobalmessage_date(setGlobalDate);
            globalMessageModel.setGlobalmessage_time(setGlobalTime);
            globalMessageModel.setGlobalmessage_head(setGlobalHeader);
            globalMessageModel.setGlobalmessage_sub(setGlobalSubText);
            global_message_list.add(globalMessageModel);
        }
        return global_message_list;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_main, menu);
        return true;
    }@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home_page) {
            Intent intent = new Intent(GlobalMessagesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == android.R.id.home) // Press Back Icon
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
