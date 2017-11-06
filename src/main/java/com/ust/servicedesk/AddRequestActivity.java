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
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ust.servicedesk.Sqlite.SQLiteHelper;
import com.ust.servicedesk.model.SqliteLocationModel;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRequestActivity extends BaseActivity {


    EditText shortRequestDescription, detailedRequestDescription, requestlocation,
            requestContactNumber,reqlocation;
    ImageView requestInfo;
    TextView requestPriorityStatus;
    Spinner requestPriority;
    Button requestSubmit;
    SQLiteHelper db = new SQLiteHelper(this);
    AutoCompleteTextView locationView;
    public static final String KEY_SHORT = "shortDescription", KEY_DETAILED = "detailedDescription", KEY_CONTACT_NUMBER = "contactNumber",
            KEY_PRIORITY = "priorityValue", KEY_LOCATION = "location", KEY_USERID = "userId";
    SharedPreferences prefs;
    ArrayAdapter<String> adapter;
    static AlertDialog dialogDetails;
    private ArrayAdapter<String> location_adapter;
    private static final String TAG = "AddRequestActivity";
    private String[] item = new String[]{"Search Location here..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));


        Window window = AddRequestActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(AddRequestActivity.this,R.color.colorPrimary));
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        reqlocation = (EditText) findViewById(R.id.location);
        shortRequestDescription = (EditText) findViewById(R.id.et_short_description);
        detailedRequestDescription = (EditText) findViewById(R.id.et_detailed_description);
        requestlocation = (EditText) findViewById(R.id.location);
        locationView = (AutoCompleteTextView) findViewById(R.id.location);
        requestContactNumber = (EditText) findViewById(R.id.contact_number);
        requestPriority = (Spinner) findViewById(R.id.priority);
        requestPriorityStatus = (TextView) findViewById(R.id.priorityStatus);

        requestContactNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        requestContactNumber.setText(getResources().getString(R.string.defaultContactNumber));

        requestInfo = (ImageView) findViewById(R.id.request_info);
        requestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDetails = null;
                LayoutInflater inflater = LayoutInflater.from(AddRequestActivity.this);
                View dialogview = inflater.inflate(R.layout.layout_incident_popup, null);
                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogDetails.dismiss();
                    }
                });
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddRequestActivity.this);
                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();
                dialogDetails.show();
                dialogDetails.setCancelable(false);
                dialogDetails.setCanceledOnTouchOutside(false);
            }
        });

        requestSubmit = (Button) findViewById(R.id.buttonSubmitRequest);

        String[] paths = {getResources().getString(R.string.critical_priority), getResources().getString(R.string.high_priority), getResources().getString(R.string.medium_priority), getResources().getString(R.string.low_priority)};
        adapter = new ArrayAdapter<String>(AddRequestActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestPriority.setAdapter(adapter);
        requestPriority.setSelection(3);
        requestPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        requestPriorityStatus.setVisibility(View.VISIBLE);
                        requestPriorityStatus.setText(getString(R.string.priority_high));
                        requestSubmit.setBackground(getResources().getDrawable(R.drawable.round_disable_button));
                        requestSubmit.setClickable(false);
                        break;
                    case 1:
                        requestPriorityStatus.setVisibility(View.VISIBLE);
                        requestPriorityStatus.setText(getString(R.string.priority_high));
                        requestSubmit.setBackground(getResources().getDrawable(R.drawable.round_disable_button));
                        requestSubmit.setClickable(false);
                        break;
                    case 2:
                        requestPriorityStatus.setVisibility(View.GONE);
                        requestPriorityStatus.setText("");
                        requestSubmit.setBackground(getResources().getDrawable(R.drawable.round_corner));
                        requestSubmit.setClickable(true);
                        break;
                    case 3:
                        requestPriorityStatus.setVisibility(View.GONE);
                        requestPriorityStatus.setText("");
                        requestSubmit.setBackground(getResources().getDrawable(R.drawable.round_corner));
                        requestSubmit.setClickable(true);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                location_adapter = new ArrayAdapter<String>(AddRequestActivity.this, android.R.layout.simple_dropdown_item_1line, item);
                locationView.setThreshold(1);
                locationView.setAdapter(location_adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });



        requestSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPriority.getSelectedItem() != getString(R.string.select_priority)) {
                    if (notEmpty()) {
                        if (!Common.isInternetConnected(AddRequestActivity.this)) {
                            Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Common.progressBar(AddRequestActivity.this, getResources().getString(R.string.submit_requests_progress));
                            //submitRequest(view);
                            //newsubbmitRequest(view);

                           // SubmitRequest(view);
                            newsubmitRequest(view);


                        }
                    }
                } else {
                    Toast.makeText(AddRequestActivity.this, getString(R.string.select_priority_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void SubmitRequest(final View view) {
        String requestSubmitURL;

        requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_request_URL);

        Map<String, String> params = new HashMap<String, String>();
       /* params.put(KEY_INTEGRATION_CODE, "UST_Mobile");
        params.put(KEY_CONTACTIN_CUSTOMER, "george.joseph@wolterskluwer.com");
        params.put(KEY_CALLBACK_NUMBER, "+3 (131) 312-3123");
        params.put(KEY_AFFECTED_USER, "george.joseph@wolterskluwer.com");
        params.put(KEY_SHORT_DESCRIPTION, "Android test New");
        params.put(KEY_DESCRIPTION, "Android Test 4.30 incident description");
        params.put(KEY_URGENCY, "1");
        params.put(KEY_IMPACT, "");
        params.put(KEY_BSS_SERVICE, "");
        params.put(KEY_LOCATION_REQ, "550 Cochrane Drive, Markham, Ontario, Canada");
        params.put(KEY_CONFIG_ITEM, "");
        params.put(KEY_ASSIGNMENT_GROUP, "WK_NA_ServiceDesk");*/

        params.put("u_integration_code","UST_Mobile");
        params.put("u_contacting_customer",WKPrefs.getString("emailID",null));
        params.put("u_callback_number",requestContactNumber.getText().toString());
        params.put("u_affected_user",WKPrefs.getString("emailID",null));
        params.put("u_short_description",shortRequestDescription.getText().toString());
        params.put("u_description",detailedRequestDescription.getText().toString());
        params.put("u_urgency","1");
        params.put("u_impact","");
        params.put("u_business_service","");
        params.put("u_location",requestlocation.getText().toString());
        params.put("u_configuration_item","");
        params.put("u_assignment_group","WK_NA_ServiceDesk");

        Log.e(TAG,"RequestSubmitURL"+ requestSubmitURL);

        try{
            RequestQueue requestQueue = Volley.newRequestQueue(AddRequestActivity.this);
            // Log.e(TAG,"SubmitBody js"+js.toString());
            Log.e(TAG,"SubmitBody Params: "+params.toString());
            Log.e(TAG,"Request URL "+requestSubmitURL);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestSubmitURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

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
                                    LayoutInflater inflater = LayoutInflater.from(AddRequestActivity.this);
                                    View dialogview = inflater.inflate(R.layout.alert_popup, null);

                                    TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);

                                        passwordMessage.setText(getResources().getString(R.string.successfully_created_request));
                                   TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogDetails.dismiss();
                                            Intent intent = new Intent(AddRequestActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddRequestActivity.this);
                                    dialogbuilder.setView(dialogview);
                                    dialogDetails = dialogbuilder.create();
                                    dialogDetails.show();
                                    dialogDetails.setCancelable(false);
                                    dialogDetails.setCanceledOnTouchOutside(false);

                                    shortRequestDescription.setText("");
                                    detailedRequestDescription.setText("");
                                    requestlocation.setText("");
                                    requestContactNumber.setText("");
//                                    incidentPriority.setAdapter(adapter);
//                                    incidentPriorityStatus.setText("");


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
                    // params.put("Content-Type","application/json");
                    Log.d(TAG,"ShareRecords"+ params);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);
            Log.d(TAG,"jsonOBJreq-"+ jsonObjReq);
        }catch (Exception e){
            e.printStackTrace();

        }

    }


    public void newsubmitRequest(final View view) {
        String requestSubmitURL;
        requestSubmitURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.create_request_URL);

        Map<String, String> params = new HashMap<String, String>();
        params.put("u_requested_for", WKPrefs.getString("emailID", null));
        params.put("u_short_description", shortRequestDescription.getText().toString());
        params.put("u_description", detailedRequestDescription.getText().toString());

        Log.e(TAG, "Ad req Params" + params);

        Log.e(TAG, "RequestSubmitURL" + requestSubmitURL);

        try {

            RequestQueue requestQueue = Volley.newRequestQueue(AddRequestActivity.this);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,requestSubmitURL,
                    new JSONObject(params), new Response.Listener<JSONObject>() {

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
                            LayoutInflater inflater = LayoutInflater.from(AddRequestActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);

                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            passwordMessage.setText(getResources().getString(R.string.successfully_created_request));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                    Intent intent = new Intent(AddRequestActivity.this, TopListIncidentsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddRequestActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                            dialogDetails.getWindow().setLayout(650, 450);
                            shortRequestDescription.setText("");
                            detailedRequestDescription.setText("");
                            requestlocation.setText("");
                            requestContactNumber.setText("");
                            requestPriority.setAdapter(adapter);
                            requestPriorityStatus.setText("");

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
                    String[] getUserIds = prefs.getString("emailID", null).split("@");
                    String getid = getUserIds[0];
                    params.put("access_token", prefs.getString("token", null));
                    params.put("user_id", getid);
                    params.put("refresh_token", prefs.getString("refreshToken", null));
                    // params.put("Content-Type","application/json");
                    Log.d(TAG, "ShareRecords" + params);
                    return params;
                }

            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }




    public boolean notEmpty() {
        boolean flag = true;
        if (shortRequestDescription.getText().toString().length() == 0 || detailedRequestDescription.getText().toString().length() == 0 || requestlocation.getText().toString().length() == 0) {
            dialogDetails = null;
            LayoutInflater inflater = LayoutInflater.from(AddRequestActivity.this);
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
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddRequestActivity.this);
            dialogbuilder.setView(dialogview);
            dialogDetails = dialogbuilder.create();
            dialogDetails.show();
            dialogDetails.setCancelable(false);
            dialogDetails.setCanceledOnTouchOutside(false);
            flag = false;
        } else if (requestContactNumber.getText().toString().length() < 10) {
            requestContactNumber.setError(getString(R.string.improper_phone_number));
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
            Intent intent = new Intent(AddRequestActivity.this, MainActivity.class);
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

}
