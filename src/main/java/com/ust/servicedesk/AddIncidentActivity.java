package com.ust.servicedesk;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ust.servicedesk.Sqlite.SQLiteHelper;
import com.ust.servicedesk.model.SqliteLocationModel;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddIncidentActivity extends BaseActivity {

    ArrayList<SqliteLocationModel> sqliteLocationModels = new ArrayList<SqliteLocationModel>();
    EditText detailedIncidentDescription, incidentlocation,
            incidentContactNumber, shortIncidentDescription;
    ImageView incidentInfo;
    TextView incidentPriorityStatus, toolboxtext, title;
    EditText shortDes;
    AutoCompleteTextView locationView;
    Spinner incidentPriority;
    TextView contact;
    Button incidentSubmit;
    LinearLayout parentLayout;
    TextView toplistText;
    ArrayList<String> location = new ArrayList<>();
    CheckBox priorityLow, priorityCritical, priorityMedium, priorityHigh;
    SQLiteHelper db = new SQLiteHelper(this);
    public static final String KEY_SHORT = "shortDescription", KEY_DETAILED = "detailedDescription", KEY_CONTACT_NUMBER = "contactNumber",
            KEY_PRIORITY = "priorityValue", KEY_LOCATION = "location", KEY_USERID = "userId";
    SharedPreferences prefs;
    ArrayAdapter<String> adapter;
    static AlertDialog dialogDetails;
    public static final String TAG = AddIncidentActivity.class.getSimpleName();
    private Bundle bundle;
    private boolean requestCall = false;
    private String getToolboxText = "Create Incident";
    private TextView toolbarTextView;
    private String getTitle;
    private String getShortDes;
    private String getCreate;
    private String location_json_string;
    private JSONObject json_locationObject;
    private ArrayAdapter<String> location_adapter;
    private String getParentActivity;
    String sourceString = "<b>" + "+1" + "</b> </t>";
    private String[] item = new String[]{"Search Location here..."};
    public static final String KEY_INTEGRATION_CODE = "u_integration_code",
            KEY_CONTACTIN_CUSTOMER = "u_contacting_customer",
            KEY_SHORT_DESCRIPTION = "u_short_description",
            KEY_AFFECTED_USER = "u_affected_user",
            KEY_CALLBACK_NUMBER = "u_callback_number",
            KEY_DESCRIPTION = "u_description",
            KEY_URGENCY = "u_urgency",
            KEY_IMPACT = "u_impact",
            KEY_BSS_SERVICE = "u_business_service",
            KEY_LOCATION_REQ = "u_location",
            KEY_CONFIG_ITEM = "u_configuration_item",
            KEY_ASSIGNMENT_GROUP = "u_assignment_group"
                    ;
    private boolean changeTexts;
    private TextView codeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_incident);

        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

        incidentInfo = (ImageView) findViewById(R.id.incident_info);
        incidentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDetails = null;
                LayoutInflater inflater = LayoutInflater.from(AddIncidentActivity.this);
                View dialogview = inflater.inflate(R.layout.layout_incident_popup, null);
                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogDetails.dismiss();
                    }
                });
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddIncidentActivity.this);
                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();
                dialogDetails.show();
                dialogDetails.setCancelable(false);
                dialogDetails.setCanceledOnTouchOutside(false);
            }
        });

        Window window = AddIncidentActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(AddIncidentActivity.this, R.color.colorPrimary));
        }
        location_json_string = loadJSONFromAsset();
        try {
            json_locationObject = new JSONObject(location_json_string);
            JSONArray data = json_locationObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                db.create(new SqliteLocationModel(jsonObject.getString("street"),
                        jsonObject.getString("state"), jsonObject.getString("city"),
                        jsonObject.getString("zip"), jsonObject.getString("country"), jsonObject.getString("name")));
                /*sqliteLocationModels.add(new SqliteLocationModel(jsonObject.getString("street"),
                        jsonObject.getString("state"), jsonObject.getString("city"),
                        jsonObject.getString("zip"),jsonObject.getString("country"),jsonObject.getString("name")));*/

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //location = db.getAllTableName();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
        shortIncidentDescription = (EditText) findViewById(R.id.incident_short_description);
        detailedIncidentDescription = (EditText) findViewById(R.id.incident_detailed_description);
        incidentlocation = (EditText) findViewById(R.id.location);
        codeTextView = (TextView)findViewById(R.id.code_text);
        incidentContactNumber = (EditText) findViewById(R.id.preffered_contact_number);
        incidentPriority = (Spinner) findViewById(R.id.priority);
        incidentPriorityStatus = (TextView) findViewById(R.id.txt_priority_status);
        priorityLow = (CheckBox) findViewById(R.id.low);
        priorityCritical = (CheckBox) findViewById(R.id.critical);
        priorityMedium = (CheckBox) findViewById(R.id.medium);
        priorityHigh = (CheckBox) findViewById(R.id.high);
        locationView = (AutoCompleteTextView) findViewById(R.id.location);
        title = (TextView) findViewById(R.id.incidentNumber);
        parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        toplistText = (TextView) findViewById(R.id.toplist_txt);
        shortDes = (EditText) findViewById(R.id.incident_short_description1);
        incidentContactNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //incidentContactNumber.setText(getResources().getString(R.string.defaultContactNumber));
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            incidentlocation .setText(Html.fromHtml(sourceString,Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            incidentlocation.setText(Html.fromHtml(sourceString));
        }*/

        // location_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        // locationView.setAdapter(location_adapter);
        // set our adapter
        location_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        locationView.setAdapter(location_adapter);
        locationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                item = getItemsFromDb(charSequence.toString());
                // update the adapater

                location_adapter.notifyDataSetChanged();
                location_adapter = new ArrayAdapter<String>(AddIncidentActivity.this, android.R.layout.simple_dropdown_item_1line, item);
                locationView.setThreshold(1);
                locationView.setAdapter(location_adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });



         /*adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,location);*/

        //will start working from first character
        // locationView.setAdapter(adapter);
        priorityLow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorityCritical.setChecked(false);
                    priorityMedium.setChecked(false);
                    priorityHigh.setChecked(false);
                } else {

                }
            }
        });
        priorityCritical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorityLow.setChecked(false);
                    priorityMedium.setChecked(false);
                    priorityHigh.setChecked(false);
                } else {

                }
            }
        });

        priorityMedium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorityLow.setChecked(false);
                    priorityCritical.setChecked(false);
                    priorityHigh.setChecked(false);
                } else {

                }
            }
        });
        priorityHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorityLow.setChecked(false);
                    priorityCritical.setChecked(false);
                    priorityMedium.setChecked(false);
                } else {

                }
            }
        });


        final float scale = this.getResources().getDisplayMetrics().density;
       /* priorityLow.setPadding(priorityLow.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                priorityLow.getPaddingTop(),
                priorityLow.getPaddingRight(),
                priorityLow.getPaddingBottom());

        priorityCritical.setPadding(priorityCritical.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                priorityCritical.getPaddingTop(),
                priorityCritical.getPaddingRight(),
                priorityCritical.getPaddingBottom());

        priorityHigh.setPadding(priorityHigh.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                priorityHigh.getPaddingTop(),
                priorityHigh.getPaddingRight(),
                priorityLow.getPaddingBottom());

        priorityLow.setPadding(priorityLow.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                priorityLow.getPaddingTop(),
                priorityLow.getPaddingRight(),
                priorityLow.getPaddingBottom());*/


        incidentSubmit = (Button) findViewById(R.id.buttonIncidentSubmit);
        Bundle bundle = getIntent().getExtras();
        getParentActivity = bundle.getString("from");
        Log.e(TAG, "Incident" + bundle.getBoolean("changeTexts"));
        Log.e(TAG, "Activity" + bundle.getString("from"));
        changeTexts = bundle.getBoolean("changeTexts");
        if (bundle.getBoolean("changeTexts")) {
            requestCall = false;
            if (bundle.getBoolean("Button")) {
                getTitle = "Incident Name";
                toplistText.setText("New Incident");
                getShortDes =  "";
            } else {
                toplistText.setText("Top Incidents");
                getTitle = bundle.getString("incidentTitle");
                getShortDes = bundle.getString("incidentDes");
            }
        } else {
            requestCall = true;
            if (bundle.getBoolean("Button")) {
                getTitle = "Request Name";
                toplistText.setText("New Request");
                getShortDes = "";
            } else {
                toplistText.setText("Top Requests");
                getTitle = bundle.getString("incidentTitle");
                getShortDes = bundle.getString("incidentDes");
            }
        }
        getCreate = bundle.getString("CreateType");
        title.setText("#"+getTitle);
        if (getCreate.equalsIgnoreCase(TopListIncidentsActivity.TAG)) {
            //title.setText("Incident Name");
            shortDes.setEnabled(false);
            shortIncidentDescription.setEnabled(true);
        } else {
            shortDes.setEnabled(false);
            shortIncidentDescription.setEnabled(false);
        }

        shortIncidentDescription.setText(getShortDes);
        shortDes.setText(getShortDes);
        //createincidentCall();
        Log.e(TAG, "ToolBarText" + bundle.getBoolean("changeTexts"));
        if (bundle.getBoolean("changeTexts"))
            getToolboxText = "Create Incident";
        else
            getToolboxText = "Create Request";

        toolbarTextView.setText(getToolboxText);
        String[] paths = {getResources().getString(R.string.critical_priority), getResources().getString(R.string.high_priority), getResources().getString(R.string.medium_priority), getResources().getString(R.string.low_priority)};
        callUserPhone();
        adapter = new ArrayAdapter<String>(AddIncidentActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incidentPriority.setAdapter(adapter);
        incidentPriority.setSelection(3);
        incidentPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        incidentPriorityStatus.setVisibility(View.VISIBLE);
                        incidentPriorityStatus.setText(getString(R.string.priority_high));
                        incidentSubmit.setBackground(getResources().getDrawable(R.drawable.round_disable_button));
                        incidentSubmit.setClickable(false);
                        break;
                    case 1:
                        incidentPriorityStatus.setVisibility(View.VISIBLE);
                        incidentPriorityStatus.setText(getString(R.string.priority_high));
                        incidentSubmit.setBackground(getResources().getDrawable(R.drawable.round_disable_button));
                        incidentSubmit.setClickable(false);
                        break;
                    case 2:
                        incidentPriorityStatus.setVisibility(View.GONE);
                        incidentPriorityStatus.setText("");
                        incidentSubmit.setBackground(getResources().getDrawable(R.drawable.round_corner));
                        incidentSubmit.setClickable(true);
                        break;
                    case 3:
                        incidentPriorityStatus.setVisibility(View.GONE);
                        incidentPriorityStatus.setText("");
                        incidentSubmit.setBackground(getResources().getDrawable(R.drawable.round_corner));
                        incidentSubmit.setClickable(true);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        incidentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (incidentPriority.getSelectedItem() != getString(R.string.select_priority)) {
                    if (notEmpty()) {
                        if (!Common.isInternetConnected(AddIncidentActivity.this)) {
                            Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            if (!priorityLow.isChecked() && !priorityCritical.isChecked() && !priorityMedium.isChecked() && !priorityHigh.isChecked())
                                Toast.makeText(AddIncidentActivity.this, getString(R.string.select_priority_check), Toast.LENGTH_SHORT).show();
                            else {
                                if (requestCall) {
                                    Common.progressBar(AddIncidentActivity.this, getResources().getString(R.string.submit_requests_progress));
                                } else {
                                    Common.progressBar(AddIncidentActivity.this, getResources().getString(R.string.submit_incidents_progress));
                                }
                                /*if(requestCall){
                                    submitRequestString(view);
                                }else*/
                                 submitRequest(view);

//                                submitRequestNew(view);

                                //newsubmit(view);

                            }
                        }
                    }
                } else {
                    Toast.makeText(AddIncidentActivity.this, getString(R.string.select_priority_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = AddIncidentActivity.this.getAssets().open("location_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String[] getItemsFromDb(String searchTerm) {

        // add items on the array dynamically
        List<SqliteLocationModel> locations = db.read(searchTerm);
        int rowCount = locations.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (SqliteLocationModel record : locations) {

            item[x] = record.getName();
            x++;
        }
       /* for(String s:myData){
            //This removes all strings that are equal to s in the adapter
            for(int i=0;i < adapter.getCount();i++){
                if(adapter.getItem(i).equals(s);
                adapter.remove(s);
            }
//Now that there are no more s in the ArrayAdapter, we add a single s, so now we only have one
            adapter.add(s);
        }*/
        return item;
    }

    private void createincidentCall() {

        String requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.get_userphone_URL);

        Map<String, String> params = new HashMap<String, String>();
        params.put("u_integration_code", "UST_Mobile");
        params.put("u_external_id", "10156011");
        params.put("u_contacting_customer", "WK_MobileApp.Test@wolterskluwer.com");
        params.put("u_callback_number", "+48225395833");
        params.put("u_affected_user", "WK_MobileApp.Test@wolterskluwer.com");
        params.put("u_short_description", "Unable to access portal");
        params.put("u_description", "Whenever I try to access portal I get a 401 Unauthorized Error");
        params.put("u_urgency", "1");
        params.put("u_impact", "4");
        params.put("u_business_service", "");
        params.put("u_location", "");
        params.put("u_configuration_item", "");
        params.put("u_assignment_group", "WK_NA_ServiceDesk");
    }

    public void callUserPhone() {
        String requestUserPhoneURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.get_userphone_URL);

        RequestQueue requestQueue = Volley.newRequestQueue(AddIncidentActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, requestUserPhoneURL,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    JSONObject object = new JSONObject();
                    JSONArray data = new JSONArray();
                    JSONObject phone = new JSONObject();
                    Log.i(TAG, "Response:" + response);
                    Common.dismissProgress();
                    if (loginObject.has("Data")) {
                        Snackbar.make(parentLayout, loginObject.getString("Data").toString(), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    } else if (loginObject.getString("status").toString().equalsIgnoreCase(getString(R.string.ok))) {
                        //object = loginObject.getJSONObject("data");
                        data = loginObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            phone = data.optJSONObject(i);
                            incidentContactNumber.setText(phone.getString("phone"));
                        }

                    } else {
                        Snackbar.make(parentLayout, getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    }

                } catch (Exception e) {
                    Common.dismissProgress();
                    Log.e(TAG, "Error:" + e);
                    Snackbar.make(parentLayout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Common.dismissProgress();
                        Log.e(TAG, "Error:" + error);
                        if (error instanceof NoConnectionError) {
                            Snackbar.make(parentLayout, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(parentLayout, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(parentLayout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String[] getUserIds = prefs.getString("emailID", null).split("@");
                String getid = getUserIds[0];
                params.put("user_id", getid);
                params.put("access_token", prefs.getString("token", null));
                params.put("refresh_token", prefs.getString("refreshToken", null));
                params.put("Content-Type", "application/json");
                Log.d(TAG, "ShareRecords" + params);
                return params;
            }

            /*@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }*/
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    private void submitRequestNew(final View view) {

        String requestSubmitURL;
        if (requestCall) {
            requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_request_URL);
        } else
            requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_incident_URL);


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("u_affected_user", "george.joseph@wolterskluwer.com");
            jsonBody.put("u_integration_code", "UST_Mobile");
            jsonBody.put("u_callback_number", "+3 (131) 312-3123");
            jsonBody.put("u_short_description", "Android test 1");
            jsonBody.put("u_description", "Android test 1 incident description");
            jsonBody.put("u_urgency", "1");
            jsonBody.put("u_location", "550 Cochrane Drive, Markham, Ontario, Canada");

            jsonBody.put("u_contacting_customer", "george.joseph@wolterskluwer.com");
            jsonBody.put("u_impact", "");
            jsonBody.put("u_business_service", "");
            jsonBody.put("u_configuration_item", "");
            jsonBody.put("u_assignment_group", "WK_NA_ServiceDesk");

           /* (["u_affected_user": "george.joseph@wolterskluwer.com",
                    "u_integration_code": "UST_Mobile",
                    "u_callback_number": "+3 (131) 312-3123",
                    "u_short_description": "iOS test 5 ",
                    "u_description": "IOS test 5 incident description",
                    "u_urgency": "1",
                    "u_location": "550 Cochrane Drive, Markham, Ontario, Canada"])*/


            final String mRequestBody = jsonBody.toString();


           /* StringRequest stringRequest = new StringRequest(Request.Method.POST, requestSubmitURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);


                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);*/


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, requestSubmitURL,
                    new JSONObject(mRequestBody), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject loginObject = response;
                        Log.i(TAG, "Response:" + response);
                        Common.dismissProgress();
                        if (loginObject.has("Data")) {
                            Snackbar.make(view, loginObject.getString("Data").toString(), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (loginObject.getString("status").toString().equalsIgnoreCase(getString(R.string.ok))) {
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(AddIncidentActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);

                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            if (requestCall) {
                                passwordMessage.setText(getResources().getString(R.string.successfully_created_request));
                            } else
                                passwordMessage.setText(getResources().getString(R.string.successfully_created_incident));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                    Intent intent = new Intent(AddIncidentActivity.this, IncidentActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddIncidentActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                            dialogDetails.getWindow().setLayout(650, 450);
                            shortIncidentDescription.setText("");
                            detailedIncidentDescription.setText("");
                            incidentlocation.setText("");
                            incidentContactNumber.setText("");
                            incidentPriority.setAdapter(adapter);
                            incidentPriorityStatus.setText("");
                        } else {
                            Snackbar.make(view, getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    } catch (Exception e) {
                        Common.dismissProgress();
                        Log.e(TAG, "Error:" + e);
                        Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Common.dismissProgress();
                            Log.e(TAG, "Error:" + error);
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
                    String[] getUserIds = prefs.getString("emailID", null).split("@");
                    String getid = getUserIds[0];
                    params.put("user_id", getid);
                    params.put("access_token", prefs.getString("token", null));
                    params.put("refresh_token", prefs.getString("refreshToken", null));
                    params.put("Content-Type", "application/json");
                    Log.d(TAG, "ShareRecords" + params);
                    return params;
                }

            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void newsubmit(final View view) {

        String requestSubmitURL;
        if (requestCall) {
            requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_request_URL);
        } else
            requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_incident_URL);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("u_integration_code","UST_Mobile");
        params.put("u_contacting_customer","george.joseph@wolterskluwer.com");
        params.put("u_callback_number","+3 (131) 312-3123");
        params.put("u_affected_user","george.joseph@wolterskluwer.com");
        params.put("u_short_description","Android test 1");
        params.put("u_description","Android Test 1 incident description");
        params.put("u_urgency","1");
        params.put("u_impact","");
        params.put("u_business_service","");
        params.put("u_location","550 Cochrane Drive, Markham, Ontario, Canada");
        params.put("u_configuration_item","");
        params.put("u_assignment_group","WK_NA_ServiceDesk");

        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest request_json = new JsonObjectRequest(requestSubmitURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                                Log.e("Response", "--->" + response);
                            try {
                                JSONObject loginObject = response;
                                Log.i(TAG, "Response:" + response);
                                Common.dismissProgress();
                                if (loginObject.has("Data")) {
                                    Snackbar.make(view, loginObject.getString("Data").toString(), Snackbar.LENGTH_LONG)
                                            .setAction(null, null).show();
                                } else if (loginObject.getString("status").toString().equalsIgnoreCase(getString(R.string.ok))) {
                                    dialogDetails = null;
                                    LayoutInflater inflater = LayoutInflater.from(AddIncidentActivity.this);
                                    View dialogview = inflater.inflate(R.layout.alert_popup, null);

                                    TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                    if (requestCall){
                                        passwordMessage.setText(getResources().getString(R.string.successfully_created_request));
                                    }else
                                        passwordMessage.setText(getResources().getString(R.string.successfully_created_incident));
                                    TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogDetails.dismiss();
                                            Intent intent = new Intent(AddIncidentActivity.this, IncidentActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddIncidentActivity.this);
                                    dialogbuilder.setView(dialogview);
                                    dialogDetails = dialogbuilder.create();
                                    dialogDetails.show();
                                    dialogDetails.setCancelable(false);
                                    dialogDetails.setCanceledOnTouchOutside(false);
                                    dialogDetails.getWindow().setLayout(650, 450);
                                    shortIncidentDescription.setText("");
                                    detailedIncidentDescription.setText("");
                                    incidentlocation.setText("");
                                    incidentContactNumber.setText("");
                                    incidentPriority.setAdapter(adapter);
                                    incidentPriorityStatus.setText("");
                                } else {
                                    Snackbar.make(view, getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                            .setAction(null, null).show();
                                }

                            } catch (Exception e) {
                                Common.dismissProgress();
                                Log.e(TAG, "Error:" + e);
                                Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());

                    Common.dismissProgress();
                    Log.e(TAG, "Error:" + error);
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

            }){

                @Override
                public HashMap<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    String[] getUserIds = prefs.getString("emailID",null).split("@");
                    String getid = getUserIds[0];
                    params.put("user_id", getid);
                    params.put("access_token",prefs.getString("token",null));
                    params.put("refresh_token",prefs.getString("refreshToken",null));
                    params.put("Content-Type","application/json");
                    return params;
                }

            };

            request_json.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyRequestQueue.add(request_json);


        }catch (Exception e){

        }


        /*StringRequest MyStringRequest = new StringRequest(Request.Method.POST, requestSubmitURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response","--->"+response);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("u_integration_code","UST_Mobile");
                MyData.put("u_contacting_customer","george.joseph@wolterskluwer.com");
                MyData.put("u_callback_number","+3 (131) 312-3123");
                MyData.put("u_affected_user","george.joseph@wolterskluwer.com");
                MyData.put("u_short_description","Android test 5");
                MyData.put("u_description","IOS test 5 incident description");
                MyData.put("u_urgency","1");
                MyData.put("u_impact","");
                MyData.put("u_business_service","");
                MyData.put("u_location","550 Cochrane Drive, Markham, Ontario, Canada");
                MyData.put("u_configuration_item","");
                MyData.put("u_assignment_group","WK_NA_ServiceDesk");
                return MyData;
            }

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
        MyRequestQueue.add(MyStringRequest);*/



}

   public void submitRequestString(final View view){

       RequestQueue queue = Volley.newRequestQueue(context);
       final String requestSubmitURL;
       String requestBody = null;
       JSONObject jsonBody = new JSONObject();
       try {
           jsonBody.put("u_requested_for", WKPrefs.getString("emailID", null));
           jsonBody.put("u_short_description", shortIncidentDescription.getText().toString());
           jsonBody.put("u_description", detailedIncidentDescription.getText().toString());
           jsonBody.put("u_requested_for", WKPrefs.getString("emailID", null));
           requestBody = jsonBody.toString();
       } catch (JSONException e) {
           e.printStackTrace();
       }
       if(requestCall){
           requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_request_URL);
       }else{
           requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_incident_URL);
       }

       final String finalRequestBody = requestBody;
       StringRequest sr = new StringRequest(Request.Method.POST,requestSubmitURL, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
               Log.d(TAG,"OnResponseRequest"+ response);
              // mPostCommentResponse.requestCompleted();
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.d(TAG,"OnResponseRequestError"+ error);
              // mPostCommentResponse.requestEndedWithError(error);
           }
       }){
          /* @Override
           protected Map<String,String> getParams(){
               Map<String,String> params = new HashMap<String, String>();
               params.put("user",userAccount.getUsername());
               params.put("pass",userAccount.getPassword());
               params.put("comment", Uri.encode(comment));
               params.put("comment_post_ID",String.valueOf(postId));
               params.put("blogId",String.valueOf(blogId));

               return params;
           }*/

           @Override
           public byte[] getBody() throws AuthFailureError {
               try {
                   return finalRequestBody == null ? null : finalRequestBody.getBytes("utf-8");
               } catch (UnsupportedEncodingException uee) {
                   VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                           finalRequestBody, "utf-8");
                   return null;
               }
           }

           @Override
           public String getBodyContentType() {
               return super.getBodyContentType();
           }

           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               Map<String,String> params = new HashMap<String, String>();
               String[] getUserIds = prefs.getString("emailID",null).split("@");
               String getid = getUserIds[0];
               params.put("access_token",prefs.getString("token",null));
               params.put("user_id", getid);
               params.put("refresh_token",prefs.getString("refreshToken",null));
               //params.put("Content-Type","application/json");
               params.put("Content-Type","application/x-www-form-urlencoded");
               return params;
           }
       };
       queue.add(sr);
   }

    public void submitRequest(final View view) {
        String requestSubmitURL;
        final Map<String, String> getBodyparams = new HashMap<String, String>();
        if(requestCall){
        requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_request_URL);
        }else
        requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_incident_URL);
        final Map<String, String> params = new HashMap<String, String>();
        if(requestCall){
            /*JSONObject js = new JSONObject();
            try {
                js.put("u_requested_for",  WKPrefs.getString("emailID", null));
                js.put("u_short_description", shortIncidentDescription.getText().toString());
                js.put("u_description", detailedIncidentDescription.getText().toString());
                js.put("JsonDatadata", js.toString());

            }catch (JSONException e) {
                e.printStackTrace();
            }*/

            params.put("u_short_description", shortIncidentDescription.getText().toString().replace("\n", "").replace("\r", "").trim());
            params.put("u_requested_for", WKPrefs.getString("emailID", null).trim());
            params.put("u_description", detailedIncidentDescription.getText()+" Locations: " + locationView.getText()
                                       + " Contact Number : "+ incidentContactNumber.getText().toString().replace("\n", "").replace("\r", "").trim());
        }else {
            int priority = 0;
            if(priorityCritical.isChecked()){
                priority = 4;
            }else if(priorityHigh.isChecked()){
                priority = 3;
            }else if(priorityMedium.isChecked()){
                priority = 2;
            }else{
                priority = 1;
            }
            params.put("u_integration_code", "UST_Mobile");
            params.put("u_contacting_customer", WKPrefs.getString("emailID", null));
            params.put("u_callback_number", incidentContactNumber.getText().toString());
            params.put("u_affected_user", WKPrefs.getString("emailID", null));
            params.put("u_short_description", shortIncidentDescription.getText().toString());
            params.put("u_description", detailedIncidentDescription.getText().toString());
            params.put("u_urgency", String.valueOf(priority));
            params.put("u_impact", "");
            params.put("u_business_service", "");
            params.put("u_location", incidentlocation.getText().toString());
            params.put("u_configuration_item", "");
            params.put("u_assignment_group", "WK_NA_ServiceDesk");
        }
        Log.e(TAG,"RequestSubmitURL"+ requestSubmitURL);

        try{
            RequestQueue requestQueue = Volley.newRequestQueue(AddIncidentActivity.this);
           // Log.e(TAG,"SubmitBody js"+js.toString());
            Log.e(TAG,"SubmitBody Params: "+params.toString());
            Log.e(TAG,"Request URL "+requestSubmitURL);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,requestSubmitURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject loginObject = response;

                        JSONObject jsonObject = new JSONObject(params);
                        Log.i(TAG, "Response:" + response);
                        Log.e(TAG,"ConvertedBody"+ jsonObject);
                        Common.dismissProgress();
                        if (loginObject.has("Data")) {
                            Snackbar.make(view, loginObject.getString("Data").toString(), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (loginObject.getString("status").toString().equalsIgnoreCase(getString(R.string.ok))) {
                            JSONObject data = new JSONObject();
                            data = loginObject.getJSONObject("data");
                            String statusId = data.getString("import_set");
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(AddIncidentActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);

                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            if (requestCall){
                                passwordMessage.setText(getResources().getString(R.string.successfully_created_request));
                            }else
                            passwordMessage.setText("The Incident # "+statusId + " " +getResources().getString(R.string.successfully_created_incident));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                    Intent intent = new Intent(AddIncidentActivity.this, TopListIncidentsActivity.class);
                                    intent.putExtra("changeTexts",changeTexts);
                                    intent.putExtra("from",getParentActivity);
                                    Log.e(TAG,"Activity"+getParentActivity);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddIncidentActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);

                            shortIncidentDescription.setText("");
                            detailedIncidentDescription.setText("");
                            incidentlocation.setText("");
                            incidentContactNumber.setText("");
                            incidentPriority.setAdapter(adapter);
                            incidentPriorityStatus.setText("");


                        } else {
                            Snackbar.make(view, getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    } catch (Exception e) {
                        Common.dismissProgress();
                        Log.e(TAG, "Error:" + e);
                        Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Common.dismissProgress();
                            Log.e(TAG, "Error:" + error);
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
                    params.put("access_token",prefs.getString("token",null));
                    params.put("user_id", getid);
                    params.put("refresh_token",prefs.getString("refreshToken",null));
                    //params.put("Content-Type","application/json");
                    Log.d(TAG,"ShareRecords"+ params);
                    return params;
                }

              /*  @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    getBodyparams.put("u_requested_for", WKPrefs.getString("emailID", null));
                    getBodyparams.put("u_short_description", shortIncidentDescription.getText().toString());
                    getBodyparams.put("u_description", detailedIncidentDescription.getText().toString());
                    Log.e(TAG,"BodyMaps"+ getBodyparams);
                    return getBodyparams;
                }*/

               /* @Override
                public String getBodyContentType() {
                    return "application/json";
                }*/
                 /*@Override
                public byte[] getBody() {
                    byte[] body = new byte[0];
                    try {
                        body = params.get();
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unable to gets bytes from JSON", e.fillInStackTrace());
                    }
                    return body;
                }*/
/*@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }*/
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);
            Log.d(TAG,"jsonOBJreq-"+ jsonObjReq);
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    public boolean notEmpty() {
        boolean flag = true;
        if (shortIncidentDescription.getText().toString().length() == 0 || detailedIncidentDescription.getText().toString().length() == 0 || incidentlocation.getText().toString().length() == 0) {
            dialogDetails = null;
            LayoutInflater inflater = LayoutInflater.from(AddIncidentActivity.this);
            View dialogview = inflater.inflate(R.layout.alert_popup, null);
            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
            passwordMessage.setText(getResources().getString(R.string.fields_cannot_be_null));
            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDetails.dismiss();
                }
            });
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddIncidentActivity.this);
            dialogbuilder.setView(dialogview);
            dialogDetails = dialogbuilder.create();
            dialogDetails.show();
            dialogDetails.setCancelable(false);
            dialogDetails.setCanceledOnTouchOutside(false);
            flag = false;
        } else if (incidentContactNumber.getText().toString().length() <= 10) {
            incidentContactNumber.setError(getString(R.string.improper_phone_number));
            flag = false;
        }
        return flag;
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
            Intent intent = new Intent(AddIncidentActivity.this, MainActivity.class);
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
