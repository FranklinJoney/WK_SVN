package com.ust.servicedesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.ust.servicedesk.adapters.RequestAdapter;
import com.ust.servicedesk.model.RequestModel;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by u50289 on 7/24/2017.
 */

public class RequestActivity extends BaseActivity {


    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private static ArrayList<RequestModel> requests = new ArrayList<RequestModel>();
    String Result;
    static String month;
    CardView cardView;
    public static Context context;
    public static Drawable icon;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FloatingActionButton call, create_request;
    SharedPreferences prefs;
    public static final String TAG = "RequestActivity";
    ArrayList<RequestModel> myRequestdata;

    private  String statusNameArray[] = {"Ram Damage","RAM Upgrade","MacAfee Anti-Virus definition is out of date" +
            "on the machine ‘IN27LP551243","Issues encountered with viewing embedded" +
            "PDF files in Excel","Issues encountered with viewing embedded" +
            "PDF files in Excel",
            "Issues encountered with viewing embedded" +
                    "PDF files in Excel","Issues Encounted with view","Machine 'INGMachine'","Embeded Isuess","Other Issues"};
    private String statusArray[] = {"New","In Progress","Resolved","Closed","On Hold","Cancelled",
            "In Progress","Resolved","Closed","On Hold"};
    private String dateArray[] = {"14-10-2017","14-10-2017","14-10-2017","14-10-2017","14-10-2017","14-10-2017",
            "14-10-2017","14-10-2017","14-10-2017","14-10-2017"};
    private String idArray[] = {"#INC8590485","#INC0493939","#INC8409381","#INC8930937","#INC89202938","#INC8909847",
            "#INC9094857","#INC8940697","#INC8940697","#INC0958697"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Window window = RequestActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(RequestActivity.this,R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

        call = (FloatingActionButton) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelephonyManager telMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                int simState = telMgr.getSimState();
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    String serviceNumber = getServiceNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(serviceNumber));
                    startActivity(intent);
                } else {
                    Snackbar.make(view, getString(R.string.network_check), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        create_request = (FloatingActionButton) findViewById(R.id.create_request);
        create_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestActivity.this, TopListIncidentsActivity.class);
                intent.putExtra("changeTexts",false);
                intent.putExtra("from",TAG);
                /*Intent intent = new Intent(RequestActivity.this, AddIncidentActivity.class);
                intent.putExtra("ToolboxText","Create Request");*/
                startActivity(intent);
            }
        });

        cardView = (CardView) findViewById(R.id.card_viewX);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        context = getApplicationContext();

        //adapter = new RequestAdapter(getMyRequestLists(), context);
        recyclerView.setAdapter(adapter);



        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Common.progressBar(RequestActivity.this, getString(R.string.refresh_requests_progress));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRequests();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });


        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);


        if (!Common.isInternetConnected(RequestActivity.this)) {
            Snackbar.make(cardView, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                    .setAction(null, null).show();
            cardView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            Common.progressBar(RequestActivity.this, getString(R.string.loading_requests_progress));
            getRequests();
            /*if (requests.size() != 0) {
                Common.dismissProgress();
                cardView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new RequestAdapter(requests, context);
                recyclerView.setAdapter(adapter);
            } else {
                getRequests();
            }*/
            //getRequests();
        }


    }

    // Get MyRequest List from Web Service's
    /*private List<RequestModel> getMyRequestLists() {

        myRequestdata = new ArrayList<RequestModel>();
        for (int i=0;i<10;i++){
            RequestModel myRequestModel = new RequestModel();
            myRequestModel.setRequestID(idArray[i]);
            myRequestModel.setRequestStatus(statusArray[i]);
            myRequestModel.setRequestShortDescription(statusNameArray[i]);
            myRequestModel.setRequestDate(dateArray[i]);
            myRequestdata.add(myRequestModel);
        }
        return myRequestdata;
    }*/


    private void getRequests() {

       // String newURl = getString(R.string.base_URL_1_5) + getString(R.string.get_request_list);
        String newURl = getString(R.string.base_URL) + getString(R.string.request_summary_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", prefs.getString("emailID", null));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, newURl,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Common.dismissProgress();

                try {
                    JSONObject userObject = response;
                    Log.i(TAG, "Response:" + response);
                    JSONArray data;
                    JSONObject requestObject;

                    Common.dismissProgress();

                    requests.clear();

                    Result = userObject.getString("status");
                    if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                        if (userObject.getJSONArray("data").length() != 0) {
                            data = userObject.getJSONArray("data");
                            Log.i(TAG, "Response:" + userObject.length());
                            for (int i = 0; i < (data.length()); i++) {

                                cardView.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                                requestObject = data.getJSONObject(i);

                                /*switch (requestObject.getString("createdTS").substring(0, 3)) {
                                    case "Jan":
                                        month = "01";
                                        break;
                                    case "Feb":
                                        month = "02";
                                        break;
                                    case "Mar":
                                        month = "03";
                                        break;
                                    case "Apr":
                                        month = "04";
                                        break;
                                    case "May":
                                        month = "05";
                                        break;
                                    case "Jun":
                                        month = "06";
                                        break;
                                    case "Jul":
                                        month = "07";
                                        break;
                                    case "Aug":
                                        month = "08";
                                        break;
                                    case "Sep":
                                        month = "09";
                                        break;
                                    case "Oct":
                                        month = "10";
                                        break;
                                    case "Nov":
                                        month = "11";
                                        break;
                                    case "Dec":
                                        month = "12";
                                        break;
                                    default:
                                        month = "12";

                                }
                                requests.add(new RequestModel(
                                        requestObject.getString("status"),
                                        "#" + requestObject.getString("ticketId"),
                                        requestObject.getString("shortDescription"),
                                        requestObject.getString("detailedDescription"),
                                        month + "-" + requestObject.getString("createdTS").substring(4, 6) + "-" + requestObject.getString("createdTS").substring(8, 12) + " " + requestObject.getString("createdTS").substring(12),
                                        requestObject.getString("contactNumber"),
                                        requestObject.getString("location"),
                                        requestObject.getString("priorityValue"),
                                        requestObject.getString("userId"),
                                        requestObject.getString("techName"),
                                        requestObject.getString("assignGroup")

                                ));*/

                                requests.add(new RequestModel(
                                        "#" + requestObject.getString("requestId"),
                                        requestObject.getString("requestStatus"),
                                        requestObject.getString("requestTitle"),
                                        requestObject.getString("requestDate"),
                                        //.substring(4, 6) + "-" + requestObject.getString("createdTS").substring(8, 12) + " " + requestObject.getString("createdTS").substring(12),
                                        requestObject.getString("requestSysId"),
                                        requestObject.getString("createdBy")));
                                Common.dismissProgress();
                            }
                        } else {
                            Common.dismissProgress();
                            call.setVisibility(View.VISIBLE);
                            create_request.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Common.dismissProgress();
                        cardView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Common.dismissProgress();
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
                }
                if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                    Common.dismissProgress();
                    call.setVisibility(View.VISIBLE);
                    create_request.setVisibility(View.VISIBLE);
                    adapter = new RequestAdapter(requests, context);
                    recyclerView.setAdapter(adapter);

                } else {
                    Common.dismissProgress();
                    Toast.makeText(context, getResources().getString(R.string.noIncidents), Toast.LENGTH_LONG);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Common.dismissProgress();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    public String getServiceNumber() {

        if (WKPrefs.getBoolean("VIP", false))
            return getString(R.string.helpdesk_contact_vip);
        else
            return getString(R.string.helpdesk_contact_normal);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_wo_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       /* if (id == R.id.home_page) {
            Intent intent = new Intent(RequestActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == android.R.id.home) // Press Back Icon
        {
            finish();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}
