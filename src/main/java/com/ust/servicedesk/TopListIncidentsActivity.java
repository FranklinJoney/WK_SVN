package com.ust.servicedesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ust.servicedesk.adapters.IncidentsTopListAdapter;
import com.ust.servicedesk.model.IncidentStatus;
import com.ust.servicedesk.model.TopListIncidents;
import com.ust.servicedesk.model.TopListRequests;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jake on 16/10/17.
 */

public class TopListIncidentsActivity extends AppCompatActivity implements View.OnClickListener {

    private List<TopListIncidents> top_incident = new ArrayList<>();
    private List<TopListRequests> top_request = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    LinearLayout activity_incident_detail, incidentCommentsLayout;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    RecyclerView.Adapter adapter;
    private Button createButton;
    private String toolbarText;
    private  String statusNameArray[] = {"Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore"};


            /*,"Web Updated","New Config","MacAntiVirus","PDF Issues",
            "RedHat OS Install","Issues Encounted with view"};*/
    private String statusArray[] = {"Incidents 01","Incidents 02","Incidents 03","Incidents 04","Incidents 05","Incidents 06",
            "Incidents 07"};
    private String requestName[] = {"Mailbox / Email Access","Device Imaging Desktop-Laptop - MAC","Software installation",
                                   "Email - Outlook configuration","VPN configuration","Enable-Disable Active sync",
                                    "User/Network Access - Enable/Revoke","Printer configuration","Domain Account Update",
                                     "Cellular Team requests"};
    private String requestDescriptions [] = {"Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore",
    "Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore","Duis aute irure dolor in reprehenderit in\n" +
            "voluptate velit esse cillum dolore"};
    CardView incidentDetailCardView;
    ScrollView incidentDetailScroll;
    SharedPreferences WKPrefs;
    TextView toolbartext;
    TextView topTextList,listText;
    ArrayList<IncidentStatus> incidentStatuses = new ArrayList<>();
    public static final String TAG = "IncidentDetailActivity";
    private String getParentActivity;
    private String setToolbarText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toplist_incidents);
        listText = (TextView)findViewById(R.id.listText);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        activity_incident_detail = (LinearLayout) findViewById(R.id.activity_incident_detail);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        topTextList = (TextView)findViewById(R.id.toplist_txt);
        createButton = (Button)findViewById(R.id.create_newIncidents);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        Bundle bundle = getIntent().getExtras();
        getParentActivity = bundle.getString("from");
        if (getParentActivity.equalsIgnoreCase(IncidentActivity.TAG)){
            setToolbarText = "Create Incident";
        }
        else{
            setToolbarText = "Create Request";
        }
        Log.d(TAG,"ToolText"+bundle.getBoolean("changeTexts"));
        //incidentStatuses = (ArrayList<IncidentStatus>)bundle.getSerializable("topLists");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Window window = TopListIncidentsActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(TopListIncidentsActivity.this,R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
        context = getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        Log.e(TAG,"CheckIncidents"+bundle.getBoolean("changeTexts"));
        getNewIncidentList();
        getNewRequestList();
        if(bundle.getBoolean("changeTexts")) {
            adapter = new IncidentsTopListAdapter(top_incident, context, bundle.getBoolean("changeTexts"),getParentActivity);
        }else{
            adapter = new IncidentsTopListAdapter(top_request, context, bundle.getBoolean("changeTexts"));
        }
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));


        toolbartext.setText(setToolbarText);
        if(bundle.getBoolean("changeTexts")){
            topTextList.setVisibility(View.VISIBLE);
            createButton.setVisibility(View.VISIBLE);
            listText.setVisibility(View.VISIBLE);
            createButton.setVisibility(View.VISIBLE);
        }else{
            topTextList.setText("Top Requests");
//            listText.setVisibility(View.GONE);
            createButton.setVisibility(View.VISIBLE);
            createButton.setText("Create a New Request");
        }
        createButton.setOnClickListener(this);
        //getTopListIncidents();
    }

    private void getNewRequestList() {
        String json = null;
        try {
            InputStream is = TopListIncidentsActivity.this.getAssets().open("top_request_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject top_list_request = new JSONObject(json);
            JSONArray data = top_list_request.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject incidentObject = data.getJSONObject(i);
                top_request.add(new TopListRequests(
                        incidentObject.getString("requestId"),
                        incidentObject.getString("shortDescriptions"),
                        incidentObject.getString("requestTitle"),
                        incidentObject.getString("requestStatus")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getNewIncidentList(){
        String json = null;
        try {
            InputStream is = TopListIncidentsActivity.this.getAssets().open("top_incidents_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject top_list_incident = new JSONObject(json);
            JSONArray data = top_list_incident.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject incidentObject = data.getJSONObject(i);
                top_incident.add(new TopListIncidents(
                        incidentObject.getString("incidentId"),
                        incidentObject.getString("shortDescriptions"),
                        incidentObject.getString("incidentTitle"),
                        incidentObject.getString("incidentStatus")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private List<TopListIncidents> getTopListIncidents() {
        top_incident = new ArrayList<>();
        for (int i=0;i<7;i++){
            TopListIncidents myIncidentsModel = new TopListIncidents();
            myIncidentsModel.setIncidentTitle(statusArray[i]);
            myIncidentsModel.setDescreption(statusNameArray[i]);
            top_incident.add(myIncidentsModel);
        }
        return top_incident;
    }
    private List<TopListRequests> getTopListRequests() {
        top_request = new ArrayList<>();
        for (int i=0;i<10;i++){
            TopListRequests topListRequests  = new TopListRequests();
            topListRequests.setIncidentTitle(requestName[i]);
            topListRequests.setDescreption(requestDescriptions[i]);
            top_request.add(topListRequests);
        }
        return top_request;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.create_newIncidents :
                 CreateNewIncidents(v);
                break;
            default:

        }
    }

    private void CreateNewIncidents(View v) {
        Intent intent = new Intent(TopListIncidentsActivity.this,AddIncidentActivity.class);
        Bundle bundle = getIntent().getExtras();
        Log.e(TAG,"bundle--->"+bundle.getBoolean("changeTexts"));
        Log.e(TAG,"button--->"+bundle.getBoolean("changeTexts"));
        if(bundle.getBoolean("changeTexts")) {
            intent.putExtra("changeTexts", true);
            intent.putExtra("CreateType", TAG);
            intent.putExtra("from",getParentActivity);
        }
        else {
            intent.putExtra("changeTexts", false);
            intent.putExtra("CreateType", TAG);
            intent.putExtra("from",getParentActivity);
        }
        intent.putExtra("Button",true);
        startActivity(intent);
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
            Intent intent = new Intent(TopListIncidentsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == android.R.id.home) // Press Back Icon
        {
            if (getParentActivity.equalsIgnoreCase(IncidentActivity.TAG)){
                Intent intent = new Intent(TopListIncidentsActivity.this, IncidentActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(TopListIncidentsActivity.this, RequestActivity.class);
                startActivity(intent);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
