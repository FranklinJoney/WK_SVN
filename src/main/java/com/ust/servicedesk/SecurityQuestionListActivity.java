package com.ust.servicedesk;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ust.servicedesk.adapters.SecurityQuestionAdapter;
import com.ust.servicedesk.model.SecurityQuestions;
import com.ust.servicedesk.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by u50281 on 7/11/2017.
 */

public class SecurityQuestionListActivity extends BaseActivity {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private static ArrayList<SecurityQuestions> securityQuestionsList = new ArrayList<SecurityQuestions>();
    String status;
    CardView cardView;
    Context context;
    FrameLayout questionList_layout;
    String url;
    Bundle bundle;
    private static final String TAG = "SecurityQuestionListActivity";
    static AlertDialog dialogDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        context = SecurityQuestionListActivity.this;
        cardView = (CardView) findViewById(R.id.card_viewX);
        questionList_layout = (FrameLayout) findViewById(R.id.questionList_layout);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        Window window = SecurityQuestionListActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(SecurityQuestionListActivity.this,R.color.colorPrimary));
        }

        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle != null) {
            if (bundle.getSerializable("ARRAYLIST") != null) {
                securityQuestionsList = (ArrayList<SecurityQuestions>) bundle.getSerializable("ARRAYLIST");
            }
        }
        if (securityQuestionsList.size() != 0) {
            cardView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new SecurityQuestionAdapter(securityQuestionsList, context);
            recyclerView.setAdapter(adapter);
        } else {
            Common.progressBar(SecurityQuestionListActivity.this, getString(R.string.not_enrolled_user_progress));
            getSecurityQuestionList();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getSecurityQuestionList() {

        url = getResources().getString(R.string.base_URL) + getResources().getString(R.string.security_questions_list_url);
        RequestQueue requestQueue = Volley.newRequestQueue(SecurityQuestionListActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    Log.i(TAG, "Response:" + response);
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;
                    if (status.equals(getResources().getString(R.string.ok))) {

                        cardView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        securityQuestionsList.clear();
                        adapter.notifyDataSetChanged();
                        recyclerView.getAdapter().notifyDataSetChanged();
                        JSONArray questions = resultObject.getJSONArray("questions");
                        for (int i = 0; i < questions.length(); i++) {
                            securityQuestionsList.add(new SecurityQuestions(
                                    questions.getJSONObject(i).getString("question"),
                                    questions.getJSONObject(i).getString("id")

                            ));
                        }

                        Common.dismissProgress();
                        adapter = new SecurityQuestionAdapter(securityQuestionsList, context);
                        recyclerView.setAdapter(adapter);

                    } else {
                        Common.dismissProgress();
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(SecurityQuestionListActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(SecurityQuestionListActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(SecurityQuestionListActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                                Snackbar.make(questionList_layout, errorObject.getJSONObject(0).getString("error_message"), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error:" + e);
                    Common.dismissProgress();
                    Snackbar.make(questionList_layout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error:" + error);
                        Common.dismissProgress();
                        if (error instanceof NoConnectionError) {
                            Snackbar.make(questionList_layout, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(questionList_layout, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(questionList_layout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }
                    }
                }) {


        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itdesk_main, menu);
        return true;
    }
}