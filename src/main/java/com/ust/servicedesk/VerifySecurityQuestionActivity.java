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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by u50289 on 6/27/2017.
 */

public class VerifySecurityQuestionActivity extends BaseActivity {

    Button verifyBtn;
    TextView questionOne, questionTwo, questionThree;
    EditText answerOne, answerTwo, answerThree;
    String status;
    public static final String KEY_ID = "id", KEY_ANSWERS = "answers", KEY_ANSWER = "answer";
    SharedPreferences prefs;
    private static final String TAG = "VerifySecurityQuestionActivity";
    static AlertDialog dialogDetails;
    private boolean backToLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_security_question);
        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backToLogin = bundle.getBoolean("backtologin");
        questionOne = (TextView) findViewById(R.id.questionOne);
        questionTwo = (TextView) findViewById(R.id.questionTwo);
        questionThree = (TextView) findViewById(R.id.questionThree);

        answerOne = (EditText) findViewById(R.id.answerOne);
        answerTwo = (EditText) findViewById(R.id.answerTwo);
        answerThree = (EditText) findViewById(R.id.answerThree);
        invalidateOptionsMenu();
        Window window = VerifySecurityQuestionActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(VerifySecurityQuestionActivity.this,R.color.colorPrimary));
        }

        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

        verifyBtn = (Button) findViewById(R.id.btn_verify);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notEmpty()) {
                    if (!Common.isInternetConnected(VerifySecurityQuestionActivity.this)) {
                        Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                    } else {
                        Common.progressBar(VerifySecurityQuestionActivity.this, getResources().getString(R.string.reset_verify_progress));
                        verifyAnswers(view);
                    }
                } else {

                }

            }
        });
        questionOne.setText(prefs.getString("question0", null));
        questionTwo.setText(prefs.getString("question1", null));
        questionThree.setText(prefs.getString("question2", null));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.home_page);
        if(backToLogin){
            Log.e(TAG,"Home"+backToLogin);
            item.setVisible(false);
        }else {
            Log.e(TAG,"Home"+backToLogin);
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) // Press Back Icon
        {
            finish();
            return true;
        }
        if (id == R.id.home_page) {
            Intent intent = new Intent(VerifySecurityQuestionActivity.this, MainActivity.class);
            startActivity(intent);
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

    public void verifyAnswers(final View view) {

        String loginURL = null;
        String getUserIds = prefs.getString("emailID", null);
        String getUser = prefs.getString("user_id", null);
        String access_token = prefs.getString("token", null);
        String refresh_token = prefs.getString("refresh_token",null);
        boolean withoutToken = false;
        if(access_token == null || refresh_token == null){
            withoutToken = true;
            loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.verify_answers_wot_URL);
        }else {
            withoutToken = false;
            loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.verify_answers_URL);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(VerifySecurityQuestionActivity.this);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject json1 = new JSONObject();
            json1.put(KEY_ID, prefs.getString("questionID0", null));
            json1.put(KEY_ANSWER, answerOne.getText().toString());

            JSONObject json2 = new JSONObject();
            json2.put(KEY_ID, prefs.getString("questionID1", null));
            json2.put(KEY_ANSWER, answerTwo.getText().toString());

            JSONObject json3 = new JSONObject();
            json3.put(KEY_ID, prefs.getString("questionID2", null));
            json3.put(KEY_ANSWER, answerThree.getText().toString());

            jsonArray.put(json1);
            jsonArray.put(json2);
            jsonArray.put(json3);

            jsonObject.put(KEY_ID, prefs.getString("requestId", null));
            jsonObject.put(KEY_ANSWERS, jsonArray);

        } catch (Exception e) {
            Log.e(TAG, getResources().getString(R.string.error_message), e);
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    Log.i(TAG, "Response" + response);
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;

                    if (status.equals(getResources().getString(R.string.ok))) {

                        if (resultObject.getString("state").equalsIgnoreCase("Verified") && resultObject.getString("lock_state").equalsIgnoreCase("Unlocked")) {
                            Intent intent = new Intent(VerifySecurityQuestionActivity.this, PasswordResetActivity.class);
                            intent.putExtra("backtologin",backToLogin);
                            Common.dismissProgress();
                            startActivity(intent);
                        }

                    } else {
                        Common.dismissProgress();
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(VerifySecurityQuestionActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(VerifySecurityQuestionActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(VerifySecurityQuestionActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
//                                Snackbar.make(view, errorObject.getJSONObject(0).getString("error_message"), Snackbar.LENGTH_LONG)
//                                        .setAction(null, null).show();
                                if (errorObject.getJSONObject(0).getString("error_message").contains("inactive")) {
                                    Intent intent = new Intent(VerifySecurityQuestionActivity.this, PasswordResetIdentifyActivity.class);
                                    intent.putExtra("errormessage", errorObject.getJSONObject(0).getString("error_message"));
                                    startActivity(intent);
                                }
                            }
                        } else {
                            if (dataObject.has("error")) {
                                JSONObject errorObjects = dataObject.getJSONObject("error");
                                JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(VerifySecurityQuestionActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(VerifySecurityQuestionActivity.this,errorObjects1.getString("statusCode"), errorObjects1.getString("message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(VerifySecurityQuestionActivity.this);
                                dialogbuilder.setView(dialogview);
                                dialogDetails = dialogbuilder.create();
                                dialogDetails.show();
                                dialogDetails.setCancelable(false);
                                dialogDetails.setCanceledOnTouchOutside(false);
                                Intent intent = new Intent(VerifySecurityQuestionActivity.this, PasswordResetIdentifyActivity.class);
                                startActivity(intent);
                            } else {
                                Snackbar.make(view, getResources().getString(R.string.answers_failed_message), Snackbar.LENGTH_LONG)
                                        .setAction(null, null).show();
                            }
                        }

                    }


                } catch (Exception e) {
                    Log.e(TAG, "Error:" + e);
                    Common.dismissProgress();
                    Snackbar.make(view, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
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

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    public boolean notEmpty() {
        boolean flag = true;

        if (answerOne.getText().toString().length() == 0) {
            answerOne.setError(getString(R.string.security_answer_empty));
            flag = false;
        }
        if (answerTwo.getText().toString().length() == 0) {
            answerTwo.setError(getString(R.string.security_answer_empty));
            flag = false;
        }
        if (answerThree.getText().toString().length() == 0) {
            answerThree.setError(getString(R.string.security_answer_empty));
            flag = false;
        }
        return flag;
    }

}
