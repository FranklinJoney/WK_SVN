package com.ust.servicedesk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;

/**
 * Created by u50281 on 6/23/2017.
 */

public class SecurityQuestionActivity extends AppCompatActivity {

    private static int SECOND_ACTIVITY_RESULT_CODE = 0;
    Button buttonSubmit;
    Toolbar toolbar;
    TextView textViewSecurity1, textViewSecurity2, textViewSecurity3;
    ImageView arrowQuestionOne, arrowQuestionTwo, arrowQuestionThree;
    private static final String TAG = "SecurityQuestionActivity";
    EditText answer1, answer2, answer3;
    private static String returnString, ID1, ID2, ID3;
    LinearLayout securityQuestion_Layout, questionOne_linearLayout, questionTwo_linearLayout, questionThree_linearLayout;
    String url, status;
    public static final String KEY_LOGIN = "login", KEY_ANSWERS = "verifications", KEY_ID = "question_id", KEY_ANSWER = "answer";
    SharedPreferences prefs;
    static AlertDialog dialogDetails;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewSecurity1 = (TextView) findViewById(R.id.textViewSecurityQuestion1);
        textViewSecurity2 = (TextView) findViewById(R.id.textViewSecurityQuestion2);
        textViewSecurity3 = (TextView) findViewById(R.id.textViewSecurityQuestion3);
        answer1 = (EditText) findViewById(R.id.editTexAnswerOne);
        answer2 = (EditText) findViewById(R.id.editTexAnswerTwo);
        answer3 = (EditText) findViewById(R.id.editTexAnswerThree);
        arrowQuestionOne = (ImageView) findViewById(R.id.arrowQuestionOne);
        arrowQuestionTwo = (ImageView) findViewById(R.id.arrowQuestionTwo);
        arrowQuestionThree = (ImageView) findViewById(R.id.arrowQuestionThree);
        securityQuestion_Layout = (LinearLayout) findViewById(R.id.ll_securityQuestion);
        questionOne_linearLayout = (LinearLayout) findViewById(R.id.questionOne_linearLayout);
        questionTwo_linearLayout = (LinearLayout) findViewById(R.id.questionTwo_linearLayout);
        arrowQuestionOne = (ImageView) findViewById(R.id.arrowQuestionOne);
        arrowQuestionTwo = (ImageView) findViewById(R.id.arrowQuestionTwo);
        arrowQuestionThree = (ImageView) findViewById(R.id.arrowQuestionThree);
        questionOne_linearLayout = (LinearLayout) findViewById(R.id.questionOne_linearLayout);
        questionTwo_linearLayout = (LinearLayout) findViewById(R.id.questionTwo_linearLayout);
        questionThree_linearLayout = (LinearLayout) findViewById(R.id.questionThree_linearLayout);

        Window window = SecurityQuestionActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(SecurityQuestionActivity.this,R.color.colorPrimary));
        }

        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        questionOne_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SecurityQuestionActivity.this, SecurityQuestionListActivity.class);
                SECOND_ACTIVITY_RESULT_CODE = 1;
                startActivityForResult(i, SECOND_ACTIVITY_RESULT_CODE);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        questionTwo_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent securityIntent = new Intent(SecurityQuestionActivity.this, SecurityQuestionListActivity.class);
                SECOND_ACTIVITY_RESULT_CODE = 2;
                startActivityForResult(securityIntent, SECOND_ACTIVITY_RESULT_CODE);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        questionThree_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent securityIntent = new Intent(SecurityQuestionActivity.this, SecurityQuestionListActivity.class);
                SECOND_ACTIVITY_RESULT_CODE = 3;
                startActivityForResult(securityIntent, SECOND_ACTIVITY_RESULT_CODE);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                securityAnswers();
            }
        });

    }

    public void securityAnswers() {

        String url = getResources().getString(R.string.base_URL) + getResources().getString(R.string.set_answers_URL);
        RequestQueue requestQueue = Volley.newRequestQueue(SecurityQuestionActivity.this);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject json1 = new JSONObject();
            json1.put(KEY_ID, ID1);
            json1.put(KEY_ANSWER, answer1.getText().toString());

            JSONObject json2 = new JSONObject();
            json2.put(KEY_ID, ID2);
            json2.put(KEY_ANSWER, answer2.getText().toString());

            JSONObject json3 = new JSONObject();
            json3.put(KEY_ID, ID3);
            json3.put(KEY_ANSWER, answer3.getText().toString());

            jsonArray.put(json1);
            jsonArray.put(json2);
            jsonArray.put(json3);

            jsonObject.put(KEY_LOGIN, prefs.getString("loginUserName", null));
            jsonObject.put(KEY_ANSWERS, jsonArray);
            System.out.println(jsonArray.toString());

        } catch (Exception e) {
            Log.i(TAG, getResources().getString(R.string.error_message), e);
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Response:" + response.toString());
                try {
                    JSONObject loginObject = response;

                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;

                    if (status.equals(getResources().getString(R.string.ok))) {

                        if (resultObject.getString("status").equals("updated")) {
                            prefs.edit().putBoolean("setupComplete", true).apply();
                            prefs.edit().putBoolean("initialSetup", true).apply();
                            Intent intent = new Intent(SecurityQuestionActivity.this, MainActivity.class);
                            Common.dismissProgress();
                            startActivity(intent);
                        } else {
                            Common.dismissProgress();
                            Snackbar.make(securityQuestion_Layout, getResources().getString(R.string.wrong_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    } else {
                        Common.dismissProgress();
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(SecurityQuestionActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(SecurityQuestionActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);


                            }
                        } else {
                            if (dataObject.has("error")) {
                                JSONObject errorObjects = dataObject.getJSONObject("error");
                                JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(SecurityQuestionActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(errorObjects1.getString("statusCode"), errorObjects1.getString("message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(SecurityQuestionActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                            }
                        }

                    }


                } catch (Exception e) {
                    Common.dismissProgress();
                    Snackbar.make(securityQuestion_Layout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                    Log.e(TAG, "Error:" + e);

                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error:" + error);
                        Common.dismissProgress();
                        if (error instanceof NoConnectionError) {
                            Snackbar.make(securityQuestion_Layout, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(securityQuestion_Layout, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(securityQuestion_Layout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS * 12, DEFAULT_MAX_RETRIES * 0, DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    returnString = data.getStringExtra("id");
                    ID1 = data.getStringExtra("questionID");
                    textViewSecurity1.setText(returnString.toString());
                    answer1.setText("");
                    arrowQuestionOne.setVisibility(GONE);

                }
                break;

            case 2:

                if (resultCode == RESULT_OK) {
                    returnString = data.getStringExtra("id");
                    ID2 = data.getStringExtra("questionID");
                    textViewSecurity2.setText(returnString.toString());
                    answer2.setText("");
                    arrowQuestionTwo.setVisibility(GONE);


                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    returnString = data.getStringExtra("id");
                    ID3 = data.getStringExtra("questionID");
                    textViewSecurity3.setText(returnString.toString());
                    answer3.setText("");
                    arrowQuestionThree.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itdesk_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }
}




