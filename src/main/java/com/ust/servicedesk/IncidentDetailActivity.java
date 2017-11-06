package com.ust.servicedesk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.ust.servicedesk.adapters.IncidentDetailAdapter;
import com.ust.servicedesk.model.IncidentComments;
import com.ust.servicedesk.model.IncidentStatus;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class  IncidentDetailActivity extends BaseActivity {

    String number, Result;
    static ArrayList<IncidentStatus> incident;
    static ArrayList<IncidentComments> incidentComments = new ArrayList<IncidentComments>();
    TextView status, incidentNumber, incidentShortDescription, incidentDate, contactingCustomer, incidentContactNumber,
            incidentDetailedDescription, incidentAssignmentGroup, incidentAssignedTo, txtResolvedTime, comments, incidentDetails;
    ImageView statusIcon;
    TextView toolbartext,createdTime,createdTime1,updatedTime,updatedTime1;
    RecyclerView recyclerView;
    ImageView dropbtn;
    LinearLayout activity_incident_detail, incidentCommentsLayout,assignemt_details;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    CardView incidentDetailCardView;
    ScrollView incidentDetailScroll;
    SharedPreferences WKPrefs;
    RelativeLayout assignMentHistoryView;
    private static final String TAG = "IncidentDetailActivity";
    private String toolbarText;
    private LinearLayout mainLayout;
    private boolean getTextBox;
    TimeZone tzEST = TimeZone.getTimeZone("EST");
    TimeZone tzUTC = TimeZone.getTimeZone("UTC");
    EditText decription_text,decription_text2;
    private String getIncidentId;
    private String getIncidentSysId,getStatus,getCreatedTime,getShortDes;
    private TableRow[] row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_detail);
        mainLayout = (LinearLayout) findViewById(R.id.activity_incident_detail);
        status = (TextView) findViewById(R.id.status);
       // decription_text = (EditText) findViewById(R.id.details);
        //decription_text2 = (EditText) findViewById(R.id.details2);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        incidentNumber = (TextView) findViewById(R.id.incidentNumber);
        createdTime = (TextView) findViewById(R.id.create_time);
        updatedTime = (TextView) findViewById(R.id.update_date);
        incidentShortDescription = (TextView) findViewById(R.id.incident_short_description);
        incidentDate = (TextView) findViewById(R.id.incidentDate);
        assignMentHistoryView = (RelativeLayout)findViewById(R.id.assign_history_view);
        dropbtn=(ImageView)findViewById(R.id.dropdown_btn);
        statusIcon = (ImageView) findViewById(R.id.statusIcon);
        txtResolvedTime = (TextView) findViewById(R.id.txt_resolvedTime);
        contactingCustomer = (TextView) findViewById(R.id.txt_contactingCustomer);
        incidentContactNumber = (TextView) findViewById(R.id.txt_callbackNumber);
        incidentDetailedDescription = (TextView) findViewById(R.id.incident_detailed_description);
        createdTime1 = (TextView)findViewById(R.id.est_time_createdate);
        updatedTime1 = (TextView)findViewById(R.id.est_time_updatedate);
        //incidentAssignmentGroup = (TextView) findViewById(R.id.txt_assignment_group);
        incidentAssignedTo = (TextView) findViewById(R.id.txt_affectedUser);

        incidentCommentsLayout = (LinearLayout) findViewById(R.id.ll_incident_comments);
        assignemt_details = (LinearLayout) findViewById(R.id.ll_assignment_details);
        incidentDetails = (TextView) findViewById(R.id.noIncidentDetails);

        incidentDetailCardView = (CardView) findViewById(R.id.incidentDetailCard);
        incidentDetailScroll = (ScrollView) findViewById(R.id.incidentDetailScroll);

        comments = (TextView) findViewById(R.id.incident_comments);

        activity_incident_detail = (LinearLayout) findViewById(R.id.activity_incident_detail);


        Window window = IncidentDetailActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(IncidentDetailActivity.this,R.color.colorPrimary));
        }

        String description_text = "<p> Hi Kent, </p> <p> I have checked this machine \\\" MA10LP529294\\\" in EPO\n" +
                "server console and found that the machine has\n" +
                "automatically updated to the latest Antivirus DAT\n" +
                "version \\\"8504\\\". Hence closing this ticket. </p> <p> Thanks and Regards,<br>\n" +
                "Rijo Thomas<brr>\n" +
                "Global Business Services,<br>\n" +
                "Wolters Kluwer </p>"; 
        
        String description_text2 = "<p>Hi Kent , </>\n" +
                "<p>I am part of WK Tech Support. As part of the daily\n" +
                "Antivirus monitoring, I could see that your machine\n" +
                "\\\"MA10LP529294\\\" is not updated with latest antivirus\n" +
                "update. Kindly reply all to this email once you are\n" +
                "available so that we can fix the issue.</p>\n" +
                "<p>Regards,<br>\n" +
                "Sudeep krishnan<br>\n" +
                "Global Business Services,<br>\n" +
                "Wolters Kluwer</p>";


        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            decription_text.setText( Html.fromHtml(description_text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            decription_text.setText(Html.fromHtml(description_text));
        }*/

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            decription_text2.setText( Html.fromHtml(description_text2,Html.FROM_HTML_MODE_LEGACY));
        } else {
            decription_text2.setText(Html.fromHtml(description_text2));
        }*/


       // decription_text.setText(Html.fromHtml(description_text));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbartext = (TextView)findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(adapter);

        context = getApplicationContext();

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        //incident = (ArrayList<IncidentStatus>) args.getSerializable("ARRAYLIST");
       // number = args.getString("incidentNumber");
        getTextBox = args.getBoolean("changeTexts");
        getIncidentId = args.getString("incidentId");
        getIncidentSysId=args.getString("sys_id");
        getStatus=args.getString("status");
        getCreatedTime=args.getString("createdTime");
        getShortDes=args.getString("shortDescription");
        incidentNumber.setText("#"+getIncidentId);
        incidentShortDescription.setText(getShortDes);
        String[] splitDate = getCreatedTime.split(" ");
        incidentDate.setText(splitDate[0]);
        Log.d(TAG,"ToolText"+getTextBox);
        Log.d(TAG,"Short Texts"+getShortDes);
        if(getTextBox){
            toolbartext.setText("Incident Details");
        }
        else{
            toolbartext.setText("Request Details");
        }
        getTicketByID();

        assignMentHistoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(assignemt_details.getVisibility()==View.VISIBLE){
                    assignemt_details.setVisibility(View.GONE);
                    Log.d(TAG,"TestingG");
                }
                else {
                    assignemt_details.setVisibility(View.VISIBLE);
                    Log.d(TAG,"TestingV");
                }

            }
        });
    }



    public void getTicketByID() {
        String ticketURl = null;
        if(getTextBox) {
            ticketURl = getString(R.string.base_URL) + getString(R.string.incident_details_URL);
            //String ticketURl = getString(R.string.base_URL_1_5) + getString(R.string.get_ticket_by_id_URL) + number.substring(1);
        }
        else{
            ticketURl = getString(R.string.base_URL) + getString(R.string.request_details_URL);
        }
            Common.progressBar(IncidentDetailActivity.this, getString(R.string.loading_details_progress));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, ticketURl,
               null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.i(TAG, "DetailsResponse:" + response);
                   JSONObject userObject = response;
                   JSONArray data;
                   JSONObject tickets;
                   JSONObject object = new JSONObject();
                    object = userObject.getJSONObject("data");
                    Common.dismissProgress();

                    incidentComments.clear();

                    Result = userObject.getString("status");
                    if (Result.equalsIgnoreCase(getString(R.string.ok))) {

                        incidentDetailCardView.setVisibility(View.GONE);
                        incidentDetailScroll.setVisibility(View.VISIBLE);

                        tickets = userObject.getJSONObject("data");
                        String ticketid = "#" + "0983748";

                        if (tickets.has("detailedDescription")){
                            incidentDetailedDescription.setText(tickets.getString("detailedDescription"));
                        }

                        if(tickets.has("createdTime")){
                            String convertedTime =  Common.shiftTimeZone(tickets.getString("createdTime"),tzUTC,tzEST);
                            String[] splitDate = convertedTime.split(" ");
                            createdTime.setText(splitDate[0]);
                            createdTime1.setText(splitDate[1]+" EST");
                        }

                        if(tickets.has("updatedTime")){
                            String convertedTime =  Common.shiftTimeZone(tickets.getString("updatedTime"),tzUTC,tzEST);
                            String[] splitDate = convertedTime.split(" ");
                            updatedTime.setText(splitDate[0]);
                            updatedTime1.setText(splitDate[1]+" EST");
                        }

                        if(tickets.has("assignedTo")){
                            incidentAssignedTo.setText(tickets.getString("assignedTo"));
                        }

                       // if (ticketid.equalsIgnoreCase(number)) {
                           // status.setText(getString(R.string.onHold));
                           // if (!tickets.getString("status").equalsIgnoreCase(getString(R.string.resolved))) {
                              //  txtResolvedTime.setHint(context.getString(R.string.updated_time));
                            //}
                           // incidentNumber.setText("#" + "0983748");
                           // incidentShortDescription.setText("hey this the short Incident descriptions");
//                            incidentDate.setText(getMonthNumber(tickets.getString("createdTS").substring(0, 3)) + "-" + tickets.getString("createdTS").substring(4, 6) + "-" + tickets.getString("createdTS").substring(8, 12));
//                            contactingCustomer.setText(getMonthNumber(tickets.getString("createdTS").substring(0, 3)) + "-" + tickets.getString("createdTS").substring(4, 6) + "-" + tickets.getString("createdTS").substring(8, 12) + " " + tickets.getString("createdTS").substring(12));
//                            incidentAssignedTo.setText(tickets.getString("techName"));
//                            incidentContactNumber.setText(getMonthNumber(tickets.getString("updatedTS").substring(0, 3)) + "-" + tickets.getString("updatedTS").substring(4, 6) + "-" + tickets.getString("updatedTS").substring(8, 12) + " " + tickets.getString("updatedTS").substring(12));

//                            incidentDate.setText("24-08-2017");
//                            contactingCustomer.setText("Contact customer name");
//                            incidentAssignedTo.setText("Assigned tO name");
//                            incidentContactNumber.setText("contact 918386823");

                            //incidentAssignmentGroup.setText(tickets.getString("assignGroup"));
                            incidentDetailedDescription.setText(tickets.getString("detailedDescription"));
                        Log.e(TAG,"Status"+getStatus);
                        if(getTextBox) {
                            if (getStatus.equalsIgnoreCase(getString(R.string.onHold))) {
                                Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_onhold_red, null);
                                statusIcon.setImageDrawable(image);
                                status.setText(getString(R.string.onHold));
                            } else {
                                if (getStatus.equalsIgnoreCase(getString(R.string.inProgress))) {
                                    Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_inprogress_purple, null);
                                    statusIcon.setImageDrawable(image);
                                    status.setText(getString(R.string.inProgress));
                                } else {
                                    if (getStatus.equalsIgnoreCase(getString(R.string.resolved))) {
                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_resolved_green, null);
                                        statusIcon.setImageDrawable(image);
                                        status.setText(getString(R.string.resolved));
                                    } else {
                                        if (getStatus.equalsIgnoreCase(getString(R.string.failed))) {
                                            Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_cancelled_darkblue, null);
                                            statusIcon.setImageDrawable(image);
                                            status.setText(getString(R.string.failed));
                                        } else {
                                            if (getStatus.equalsIgnoreCase(getString(R.string.newStatus))) {
                                                Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_new_blue, null);
                                                statusIcon.setImageDrawable(image);
                                                status.setText(getString(R.string.newStatus));
                                            } else {
                                                if (getStatus.equalsIgnoreCase(getString(R.string.closed))) {
                                                    Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_closed_grey, null);
                                                    statusIcon.setImageDrawable(image);
                                                    status.setText(getString(R.string.closed));
                                                } else {
                                                    if (getStatus.equalsIgnoreCase(getString(R.string.open))) {
                                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_new_blue, null);
                                                        statusIcon.setImageDrawable(image);
                                                        status.setText(getString(R.string.open));
                                                    } else {
                                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_incident_closed_grey, null);
                                                        statusIcon.setImageDrawable(image);
                                                        status.setText(getString(R.string.open));
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }else{
                            if (getStatus.equalsIgnoreCase(getString(R.string.onHold))) {
                                Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_pending_darkblue, null);
                                statusIcon.setImageDrawable(image);
                                status.setText("Pending");
                            } else {
                                if (getStatus.equalsIgnoreCase(getString(R.string.inProgress))) {
                                    Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_inprogress_purple, null);
                                    statusIcon.setImageDrawable(image);
                                    status.setText(getString(R.string.inProgress));
                                } else {
                                    if (getStatus.equalsIgnoreCase(getString(R.string.resolved))) {
                                        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_cancelled_rejected_grey, null);
                                        statusIcon.setImageDrawable(image);
                                        status.setText(getString(R.string.resolved));
                                    } else {
                                        if (getStatus.equalsIgnoreCase(getString(R.string.failed))) {
                                            Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_rejected_red, null);
                                            statusIcon.setImageDrawable(image);
                                            status.setText("Rejected");
                                        } else {
                                            if (getStatus.equalsIgnoreCase(getString(R.string.newStatus))) {
                                                Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_open_blue, null);
                                                statusIcon.setImageDrawable(image);
                                                status.setText(getString(R.string.open));
                                            } else {
                                                if (getStatus.equalsIgnoreCase(getString(R.string.closed))) {
                                                    Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_request_complete_green, null);
                                                    statusIcon.setImageDrawable(image);
                                                    status.setText(getString(R.string.completed));
                                                } else {
                                                    if (getStatus.equalsIgnoreCase(getString(R.string.open))) {
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
                        }
                            if (object.getJSONArray("assignmentHistory").length() != 0) {
                                incidentCommentsLayout.setVisibility(View.VISIBLE);
                                data = object.getJSONArray("assignmentHistory");
                                LayoutInflater inflater = LayoutInflater.from(IncidentDetailActivity.this);
                                TableLayout layout = (TableLayout)assignemt_details.findViewById(R.id.assignment_history_table_layout);
                              /*  for(int i = 0; i < 10; i++) {
                                    row = new TableRow(this);
                                    View layout_number = inflater.inflate(R.layout.inflate_number, layout, false);
                                    TextView number = (TextView) layout_number.findViewById(R.id.Number);
                                    number.setTag(i);
                                    number.setText(Integer.toString(i));
                                    row.addView(number);
                                    layout.addView(row);
                                }*/
                                //data = userObject.getJSONObject("ticket").getJSONArray("comments");
                                Log.e(TAG,"DataLenght"+data.length());
                                row = new TableRow[data.length()];
                                for (int i = 0; i < (data.length()); i++) {
                                   // for(int i = 0; i < 10; i++) {
                                    Log.e(TAG,"DataString"+data.getString(i));
                                       JSONObject jsonObject = new JSONObject();
                                       jsonObject = data.getJSONObject(i);
                                        row[i] = new TableRow(IncidentDetailActivity.this);
                                        View layout_row = inflater.inflate(R.layout.indicent_details_list,null);
                                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                        row[i].setLayoutParams(lp);
                                        LinearLayout assignmentHistory_linear;
                                        assignmentHistory_linear = (LinearLayout)layout_row.findViewById(R.id.table_row_linear);
                                        LinearLayout linearLayout = new LinearLayout(IncidentDetailActivity.this);
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        linearLayout.setLayoutParams(layoutParams);
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                        TextView assignment_time = (TextView) layout_row.findViewById(R.id.assignment_time);
                                        TextView assignment_date = (TextView) layout_row.findViewById(R.id.assignment_date);
                                        TextView assignment_createId = (TextView) layout_row.findViewById(R.id.created_by);
                                        EditText assignment_details = (EditText) layout_row.findViewById(R.id.details);
                                        assignment_time.setTag(i);
                                        String date_time =  jsonObject.getString("time").toString();
                                        String[] date_time1 = date_time.split(" ");
                                        assignment_time.setText(date_time1[1]);
                                        assignment_date.setText(date_time1[0]);
                                        assignment_createId.setText(jsonObject.getString("createdBy").toString());
                                        assignment_details.setText(jsonObject.getString("description").toString());
                                    DisplayMetrics metrics = new DisplayMetrics();
                                    //getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                    Display display = getWindowManager().getDefaultDisplay();
                                    /*((Activity) IncidentDetailActivity.this).getWindowManager()
                                            .getDefaultDisplay()
                                            .getMetrics(metrics);*/
                                    Point size = new Point();
                                    display.getRealSize(size);
                                    int screen_width = size.x/160;
                                    int screen_height = size.y;
                                   // int width = metrics.widthPixels;
                                    //  assignment_details.setMaxWidth(screen_width);
                                    Log.e(TAG,"Width"+screen_width);
                                        //row.addView(assignmentHistory_linear);
                                        Log.e(TAG,"TimeString"+jsonObject.getString("time").toString());
                                        /*row.addView(assignment_date);
                                        row.addView(assignment_createId);
                                        row.addView(assignment_details);*/
                                       // linearLayout.addView(assignment_time);
                                        //linearLayout.addView(assignment_date);
                                      //  linearLayout.addView(assignment_createId);
                                      //  linearLayout.addView(assignment_details);
                                    row[i].addView(layout_row);
                                    layout.addView(row[i]);
                                   // }

                                /*    String commentedBy;
                                    if (WKPrefs.getString("emailID", null).equalsIgnoreCase(data.getJSONObject(i).getString("commentBy"))) {
                                        commentedBy = "Me";
                                    } else {
                                        commentedBy = data.getJSONObject(i).getString("commentBy");
                                    }
                                    incidentComments.add(new IncidentComments(
                                            commentedBy,
                                            getMonthNumber(data.getJSONObject(i).getString("updatedTime").substring(0, 3)) + "-" + data.getJSONObject(i).getString("updatedTime").substring(4, 6) + "-" + data.getJSONObject(i).getString("updatedTime").substring(8, 12),
                                            data.getJSONObject(i).getString("message")
                                    ));*/
                                }
                            }else {
                                incidentCommentsLayout.setVisibility(View.GONE);
                                Log.e(TAG,"HistoryGone");
                            }
                        } /*else {
                            Common.dismissProgress();
                            incidentDetailCardView.setVisibility(View.VISIBLE);
                            incidentDetails.setVisibility(View.VISIBLE);
                            incidentDetailScroll.setVisibility(View.GONE);
                        }*/
                   // }
                } catch (Exception e) {
                    Common.dismissProgress();
                    incidentDetailCardView.setVisibility(View.VISIBLE);
                    incidentDetails.setVisibility(View.VISIBLE);
                    incidentDetailScroll.setVisibility(View.GONE);
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
                }
                if (Result.equalsIgnoreCase(getString(R.string.ok))) {
                    Common.dismissProgress();
                    incidentDetailCardView.setVisibility(View.GONE);
                    incidentDetailScroll.setVisibility(View.VISIBLE);
                    adapter = new IncidentDetailAdapter(incidentComments, context);
                    recyclerView.setAdapter(adapter);
                } else {
                    Common.dismissProgress();
                    incidentDetailCardView.setVisibility(View.VISIBLE);
                    incidentDetails.setVisibility(View.VISIBLE);
                    incidentDetailScroll.setVisibility(View.GONE);
                    Toast.makeText(context, getResources().getString(R.string.noIncidents), Toast.LENGTH_LONG);
                }
            }
        },



                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Common.dismissProgress();
                        incidentDetailCardView.setVisibility(View.VISIBLE);
                        incidentDetails.setVisibility(View.VISIBLE);
                        incidentDetailScroll.setVisibility(View.GONE);
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

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String[] getUserIds = WKPrefs.getString("emailID",null).split("@");
                String getid = getUserIds[0];
                params.put("user_id", getid);
                params.put("access_token",WKPrefs.getString("token",null));
                params.put("refresh_token",WKPrefs.getString("refreshToken",null));
                params.put("Content-Type","application/json");
                params.put("sys_id",getIncidentSysId);
                Log.d(TAG,"ShareRecords"+ params);
                return params;
            }

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
            Intent intent = new Intent(IncidentDetailActivity.this, MainActivity.class);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
