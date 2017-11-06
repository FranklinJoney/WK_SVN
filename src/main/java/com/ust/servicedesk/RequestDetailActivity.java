package com.ust.servicedesk;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ust.servicedesk.adapters.IncidentDetailAdapter;
import com.ust.servicedesk.model.IncidentComments;
import com.ust.servicedesk.model.RequestModel;
import com.ust.servicedesk.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by u50289 on 7/31/2017.
 */

public class RequestDetailActivity extends BaseActivity {

    String number, Result;
    static ArrayList<RequestModel> requests;
    static ArrayList<IncidentComments> incidentComments = new ArrayList<IncidentComments>();
    TextView status, requesttNumber, requestShortDescription, requestDate, contactingCustomer, requestContactNumber,
            requestDetailedDescription, requestAssignedTo, requestAssignmentGroup, txtResolvedTime, comments, requestDetails;
    ImageView statusIcon;
    RecyclerView recyclerView;
    LinearLayout activity_incident_detail, requestCommentsLayout;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    CardView requestDetailCardView;
    ScrollView requestDetailScroll;
    private static final String TAG = "RequestDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        status = (TextView) findViewById(R.id.status);
        requesttNumber = (TextView) findViewById(R.id.requestNumber);
        requestShortDescription = (TextView) findViewById(R.id.requestStatus);
        requestDate = (TextView) findViewById(R.id.requestDate);
        statusIcon = (ImageView) findViewById(R.id.statusIcon);
        contactingCustomer = (TextView) findViewById(R.id.txt_contactingCustomer);
        requestContactNumber = (TextView) findViewById(R.id.txt_callbackNumber);
        requestDetailedDescription = (TextView) findViewById(R.id.txt_detailed_description);
        requestAssignedTo = (TextView) findViewById(R.id.txt_assigned_to);
        //requestAssignmentGroup = (TextView) findViewById(R.id.txt_assignment_group);

        requestCommentsLayout = (LinearLayout) findViewById(R.id.ll_request_comments);



        txtResolvedTime = (TextView) findViewById(R.id.txt_resolvedTime);
        requestDetails = (TextView) findViewById(R.id.noRequestDetails);

        comments = (TextView) findViewById(R.id.request_comments);

        requestDetailCardView = (CardView) findViewById(R.id.requestDetailCard);
        requestDetailScroll = (ScrollView) findViewById(R.id.requestDetailScroll);

        activity_incident_detail = (LinearLayout) findViewById(R.id.activity_request_detail);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(adapter);

        context = getApplicationContext();

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Window window = RequestDetailActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(RequestDetailActivity.this,R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        requests = (ArrayList<RequestModel>) args.getSerializable("ARRAYLIST");
        number = args.getString("incidentNumber");
        getTicketByID();

    }

    public void getTicketByID() {
        String ticketURl = getString(R.string.base_URL_1_5) + getString(R.string.get_ticket_by_id_URL) + number.substring(1);
        Common.progressBar(RequestDetailActivity.this, getString(R.string.loading_details_progress));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, ticketURl,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject userObject = response;
                    Log.i(TAG, "Response" + response);
                    JSONArray data;
                    JSONObject tickets;

                    Common.dismissProgress();

                    incidentComments.clear();

                    Result = userObject.getString("result");
                    if (Result.equalsIgnoreCase(getString(R.string.ok))) {

                        requestDetailCardView.setVisibility(View.GONE);
                        requestDetailScroll.setVisibility(View.VISIBLE);

                        tickets = userObject.getJSONObject("ticket");
                        String ticketid = "#" + tickets.getString("ticketId");
                        if (ticketid.equalsIgnoreCase(number)) {
                            status.setText(tickets.getString("status"));
                            if (!tickets.getString("status").equalsIgnoreCase(getString(R.string.resolved))) {
                                txtResolvedTime.setHint(context.getString(R.string.updated_time));
                            }


                            requesttNumber.setText("#" + tickets.getString("ticketId"));
                            requestShortDescription.setText(tickets.getString("shortDescription"));
                            requestDate.setText(getMonthNumber(tickets.getString("createdTS").substring(0, 3)) + "-" + tickets.getString("createdTS").substring(4, 6) + "-" + tickets.getString("createdTS").substring(8, 12));
                            contactingCustomer.setText(getMonthNumber(tickets.getString("createdTS").substring(0, 3)) + "-" + tickets.getString("createdTS").substring(4, 6) + "-" + tickets.getString("createdTS").substring(8, 12) + " " + tickets.getString("createdTS").substring(12));
                            requestAssignedTo.setText(tickets.getString("techName"));
                            requestContactNumber.setText(getMonthNumber(tickets.getString("updatedTS").substring(0, 3)) + "-" + tickets.getString("updatedTS").substring(4, 6) + "-" + tickets.getString("updatedTS").substring(8, 12) + " " + tickets.getString("updatedTS").substring(12));
                            //requestAssignmentGroup.setText(tickets.getString("assignGroup"));
                            requestDetailedDescription.setText(tickets.getString("detailedDescription"));
                            if (tickets.getString("status").equalsIgnoreCase(getString(R.string.onHold))) {
                                Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_pending_darkblue, null);
                                statusIcon.setImageDrawable(image);
                                status.setText("Pending");
                            } else {
                                if (tickets.getString("status").equalsIgnoreCase(getString(R.string.inProgress))) {
                                    Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_inprogress_purple, null);
                                    statusIcon.setImageDrawable(image);
                                    status.setText(getString(R.string.inProgress));
                                } else {
                                    if (tickets.getString("status").equalsIgnoreCase(getString(R.string.resolved))) {
                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_cancelled_rejected_grey, null);
                                        statusIcon.setImageDrawable(image);
                                        status.setText(getString(R.string.resolved));
                                    } else {
                                        if (tickets.getString("status").equalsIgnoreCase(getString(R.string.failed))) {
                                            Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_rejected_red, null);
                                            statusIcon.setImageDrawable(image);
                                            status.setText("Rejected");
                                        } else {
                                            if (tickets.getString("status").equalsIgnoreCase(getString(R.string.newStatus))) {
                                                Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_open_blue, null);
                                                statusIcon.setImageDrawable(image);
                                                status.setText(getString(R.string.open));
                                            } else {
                                                if (tickets.getString("status").equalsIgnoreCase(getString(R.string.closed))) {
                                                    Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_complete_green, null);
                                                    statusIcon.setImageDrawable(image);
                                                    status.setText(getString(R.string.completed));
                                                } else {
                                                    if (tickets.getString("status").equalsIgnoreCase(getString(R.string.open))) {
                                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_open_blue, null);
                                                        statusIcon.setImageDrawable(image);
                                                        status.setText(getString(R.string.open));
                                                    } else {
                                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_complete_green, null);
                                                        statusIcon.setImageDrawable(image);
                                                        status.setText(getString(R.string.completed));
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                            if (userObject.getJSONObject("ticket").getJSONArray("comments").length() != 0) {
                                requestCommentsLayout.setVisibility(View.GONE);
                                data = userObject.getJSONObject("ticket").getJSONArray("comments");
                                for (int i = 0; i < (data.length()); i++) {
                                    incidentComments.add(new IncidentComments(
                                            data.getJSONObject(i).getString("commentBy"),
                                            getMonthNumber(data.getJSONObject(i).getString("updatedTime").substring(0, 3)) + "-" + data.getJSONObject(i).getString("updatedTime").substring(4, 6) + "-" + data.getJSONObject(i).getString("updatedTime").substring(8, 12),
                                            data.getJSONObject(i).getString("message")
                                    ));
                                }

                            } else {
                                requestCommentsLayout.setVisibility(View.GONE);
                            }
                        } else {
                            Common.dismissProgress();
                            requestDetailCardView.setVisibility(View.VISIBLE);
                            requestDetails.setVisibility(View.VISIBLE);
                            requestDetailScroll.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Common.dismissProgress();
                    requestDetailCardView.setVisibility(View.VISIBLE);
                    requestDetails.setVisibility(View.VISIBLE);
                    requestDetailScroll.setVisibility(View.GONE);
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
                }
                if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                    Common.dismissProgress();
                    requestDetailCardView.setVisibility(View.GONE);
                    requestDetailScroll.setVisibility(View.VISIBLE);
                    adapter = new IncidentDetailAdapter(incidentComments, context);
                    recyclerView.setAdapter(adapter);

                } else {
                    Common.dismissProgress();
                    requestDetailCardView.setVisibility(View.VISIBLE);
                    requestDetails.setVisibility(View.VISIBLE);
                    requestDetailScroll.setVisibility(View.GONE);
                    Toast.makeText(context, getResources().getString(R.string.noIncidents), Toast.LENGTH_LONG);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Common.dismissProgress();
                        requestDetailCardView.setVisibility(View.VISIBLE);
                        requestDetails.setVisibility(View.VISIBLE);
                        requestDetailScroll.setVisibility(View.GONE);
                        Log.e(TAG, "Error:" + error);
                        if (error instanceof NoConnectionError) {
                            Snackbar.make(activity_incident_detail, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(activity_incident_detail, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(activity_incident_detail, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    }
                }) {


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    public String getMonthNumber(String month) {
        String monthNum = "";
        switch (month) {
            case "Jan":
                monthNum = "01";
                break;
            case "Feb":
                monthNum = "02";
                break;
            case "Mar":
                monthNum = "03";
                break;
            case "Apr":
                monthNum = "04";
                break;
            case "May":
                monthNum = "05";
                break;
            case "Jun":
                monthNum = "06";
                break;
            case "Jul":
                monthNum = "07";
                break;
            case "Aug":
                monthNum = "08";
                break;
            case "Sep":
                monthNum = "09";
                break;
            case "Oct":
                monthNum = "10";
                break;
            case "Nov":
                monthNum = "11";
                break;
            case "Dec":
                monthNum = "12";
                break;
            default:
                monthNum = "12";

        }
        return monthNum;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home_page) {
            Intent intent = new Intent(RequestDetailActivity.this, MainActivity.class);
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
