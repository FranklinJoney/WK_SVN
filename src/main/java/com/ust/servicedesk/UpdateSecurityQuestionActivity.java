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
import com.ust.servicedesk.model.SecurityQuestions;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by u50289 on 6/30/2017.
 */

public class UpdateSecurityQuestionActivity extends BaseActivity {

    private static int SECOND_ACTIVITY_RESULT_CODE = 1;
    private static String returnString, status, ID1, ID2, ID3;
    private static ArrayList<SecurityQuestions> securityQuestionsList = new ArrayList<SecurityQuestions>();
    Button buttonSave;
    static Boolean flag = false;
    Toolbar toolbar;
    TextView textViewSecurity1, textViewSecurity2, textViewSecurity3;
    ImageView arrowQuestionOne, arrowQuestionTwo, arrowQuestionThree;
    EditText answer1, answer2, answer3;
    LinearLayout questionList_layout, questionOne_linearLayout, questionTwo_linearLayout, questionThree_linearLayout;
    public static final String KEY_LOGIN = "login", KEY_ID = "question_id", KEY_ANSWERS = "verifications", KEY_ANSWER = "answer";
    SharedPreferences prefs;
    private static final String TAG = "UpdateSecurityQuestion";
    static AlertDialog dialogDetails;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_security_question);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewSecurity1 = (TextView) findViewById(R.id.textViewSecurityQuestion1);
        textViewSecurity2 = (TextView) findViewById(R.id.textViewSecurityQuestion2);
        textViewSecurity3 = (TextView) findViewById(R.id.textViewSecurityQuestion3);
        answer1 = (EditText) findViewById(R.id.editTexAnswerOne);
        answer2 = (EditText) findViewById(R.id.editTexAnswerTwo);
        answer3 = (EditText) findViewById(R.id.editTexAnswerThree);
        questionList_layout = (LinearLayout) findViewById(R.id.ll_update_security_question);

        arrowQuestionOne = (ImageView) findViewById(R.id.arrowQuestionOne);
        arrowQuestionTwo = (ImageView) findViewById(R.id.arrowQuestionTwo);
        arrowQuestionThree = (ImageView) findViewById(R.id.arrowQuestionThree);

        questionOne_linearLayout = (LinearLayout) findViewById(R.id.questionOne_linearLayout);
        questionTwo_linearLayout = (LinearLayout) findViewById(R.id.questionTwo_linearLayout);
        questionThree_linearLayout = (LinearLayout) findViewById(R.id.questionThree_linearLayout);
        setSupportActionBar(toolbar);
        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        Window window = UpdateSecurityQuestionActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(UpdateSecurityQuestionActivity.this,R.color.colorPrimary));
        }

        if (!Common.isInternetConnected(UpdateSecurityQuestionActivity.this)) {
            Snackbar.make(questionList_layout, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                    .setAction(null, null).show();
        } else {
            getAnswers();
        }
        questionOne_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag) {
                    Intent securityIntent = new Intent(getApplicationContext(), SecurityQuestionListActivity.class);
                    Bundle args = new Bundle();
                    if (securityQuestionsList.size() != 0) {
                        args.putSerializable("ARRAYLIST", (Serializable) securityQuestionsList);
                    }
                    securityIntent.putExtra("BUNDLE", args);
                    SECOND_ACTIVITY_RESULT_CODE = 1;
                    startActivityForResult(securityIntent, SECOND_ACTIVITY_RESULT_CODE);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            }
        });

        questionTwo_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag) {
                    Intent securityIntent = new Intent(getApplicationContext(), SecurityQuestionListActivity.class);
                    Bundle args = new Bundle();
                    if (securityQuestionsList.size() != 0) {
                        args.putSerializable("ARRAYLIST", (Serializable) securityQuestionsList);
                    }
                    securityIntent.putExtra("BUNDLE", args);
                    SECOND_ACTIVITY_RESULT_CODE = 2;
                    startActivityForResult(securityIntent, SECOND_ACTIVITY_RESULT_CODE);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            }
        });
        questionThree_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag) {
                    Intent securityIntent = new Intent(getApplicationContext(), SecurityQuestionListActivity.class);
                    Bundle args = new Bundle();
                    if (securityQuestionsList.size() != 0) {
                        args.putSerializable("ARRAYLIST", (Serializable) securityQuestionsList);
                    }
                    securityIntent.putExtra("BUNDLE", args);
                    SECOND_ACTIVITY_RESULT_CODE = 3;
                    startActivityForResult(securityIntent, SECOND_ACTIVITY_RESULT_CODE);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag) {
                    if (notEmpty()) {
                        Common.progressBar(UpdateSecurityQuestionActivity.this, getString(R.string.update_security_question_progress));
                        setAnswers();
                    }
                }
            }

        });
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
                }
                break;

            case 2:

                if (resultCode == RESULT_OK) {
                    returnString = data.getStringExtra("id");
                    ID2 = data.getStringExtra("questionID");
                    textViewSecurity2.setText(returnString.toString());
                    answer2.setText("");
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    returnString = data.getStringExtra("id");
                    ID3 = data.getStringExtra("questionID");
                    textViewSecurity3.setText(returnString.toString());
                    answer3.setText("");
                }
                break;
        }
    }

    private void getAnswers() {
        String loginURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.login_identify_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_LOGIN, prefs.getString("loginUserName", null));
        Common.progressBar(UpdateSecurityQuestionActivity.this, getString(R.string.reset_identify_progress));
        RequestQueue requestQueue = Volley.newRequestQueue(UpdateSecurityQuestionActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL,
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    status = loginObject.getString("status");
                    Log.i(TAG, "Response:" + response);
                    JSONArray errorObjects;

                    if (status.equals(getResources().getString(R.string.ok))) {
                        Common.dismissProgress();
                        buttonSave.setClickable(true);
                        JSONObject dataObject = loginObject.getJSONObject("data");
                        JSONObject resultObject = dataObject.getJSONObject("result");
                        JSONArray questions = resultObject.getJSONArray("questions");

                        for (int i = 0; i < questions.length(); i++) {
                            prefs.edit().putString("updateQuestionID" + i, questions.getJSONObject(i).getString("id")).apply();
                            prefs.edit().putString("updateQuestion" + i, questions.getJSONObject(i).getString("question")).apply();
                        }
                        Common.progressBar(UpdateSecurityQuestionActivity.this, getString(R.string.not_enrolled_user_progress));
                        getID();
                        textViewSecurity1.setText(prefs.getString("updateQuestion0", null));
                        textViewSecurity2.setText(prefs.getString("updateQuestion1", null));
                        textViewSecurity3.setText(prefs.getString("updateQuestion2", null));

                    } else {
                        Common.dismissProgress();
                        buttonSave.setClickable(false);
                        if ((errorObjects = loginObject.getJSONArray("data")).length() != 0) {
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(UpdateSecurityQuestionActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);
                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            passwordMessage.setText(Common.formatErrorMessage(UpdateSecurityQuestionActivity.this,errorObjects.getJSONObject(0).getString("error_code"), errorObjects.getJSONObject(0).getString("error_message")));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(UpdateSecurityQuestionActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                        } else {
                            Snackbar.make(questionList_layout, getResources().getString(R.string.login_failed_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    }


                } catch (Exception e) {
                    Common.dismissProgress();
                    buttonSave.setClickable(false);
                    Snackbar.make(questionList_layout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                    Log.i(TAG, getResources().getString(R.string.error_message), e);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error:" + error);
                        flag = false;
                        buttonSave.setClickable(false);
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


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    private void setAnswers() {

        String url = getResources().getString(R.string.base_URL) + getResources().getString(R.string.set_answers_URL);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject json1 = new JSONObject();
            json1.put(KEY_ID, ID1);
            Log.i(TAG, ID1);
            json1.put(KEY_ANSWER, answer1.getText().toString());

            JSONObject json2 = new JSONObject();
            json2.put(KEY_ID, ID2);
            Log.i(TAG, ID2);
            json2.put(KEY_ANSWER, answer2.getText().toString());

            JSONObject json3 = new JSONObject();
            json3.put(KEY_ID, ID3);
            Log.i(TAG, ID3);
            json3.put(KEY_ANSWER, answer3.getText().toString());

            jsonArray.put(json1);
            jsonArray.put(json2);
            jsonArray.put(json3);

            jsonObject.put(KEY_LOGIN, prefs.getString("loginUserName", null));
            jsonObject.put(KEY_ANSWERS, jsonArray);

            Log.i(TAG, "UpdateSecurity" + jsonObject);
        } catch (Exception e) {
            Log.e(TAG, getResources().getString(R.string.error_message), e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(UpdateSecurityQuestionActivity.this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Response:" + response);
                try {
                    JSONObject loginObject = response;
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;

                    if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                        Common.dismissProgress();
                        if (errorObject.getJSONObject(0).has("error_message")) {
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(UpdateSecurityQuestionActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);
                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            passwordMessage.setText(Common.formatErrorMessage(UpdateSecurityQuestionActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDetails.dismiss();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(UpdateSecurityQuestionActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                        }

                    } else if (dataObject.has("error")) {
                        Common.dismissProgress();
                        JSONObject errorObjects = dataObject.getJSONObject("error");
                        JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                        dialogDetails = null;
                        LayoutInflater inflater = LayoutInflater.from(UpdateSecurityQuestionActivity.this);
                        View dialogview = inflater.inflate(R.layout.alert_popup, null);
                        TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                        passwordMessage.setText(Common.formatErrorMessage(UpdateSecurityQuestionActivity.this,errorObjects1.getString("statusCode"), errorObjects1.getString("message")));
                        TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogDetails.dismiss();
                            }
                        });
                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(UpdateSecurityQuestionActivity.this);
                        dialogbuilder.setView(dialogview);
                        dialogDetails = dialogbuilder.create();
                        dialogDetails.show();
                        dialogDetails.setCancelable(false);
                        dialogDetails.setCanceledOnTouchOutside(false);
                    } else if (resultObject.has("status")) {
                        Common.dismissProgress();
                        Log.e(TAG,"TestTheStatus");
                        if (resultObject.getString("status").equalsIgnoreCase("updated")) {
                            Log.e(TAG,"TestTheStatusEqualsUpdated");
                            LayoutInflater inflater = LayoutInflater.from(UpdateSecurityQuestionActivity.this);
                            View dialogview = inflater.inflate(R.layout.alert_popup, null);
                            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                            passwordMessage.setText(getResources().getString(R.string.you_are_enrolled));
                            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(UpdateSecurityQuestionActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    dialogDetails.dismiss();
                                }
                            });
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(UpdateSecurityQuestionActivity.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                        }
                    }else{
                        Log.e(TAG,"NoStatus");
                    }


                } catch (Exception e) {
                    Common.dismissProgress();
                    Snackbar.make(questionList_layout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                    Log.e(TAG, getResources().getString(R.string.error_message), e);
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String[] getUserIds = prefs.getString("emailID",null).split("@");
                String getid = getUserIds[0];
                headers.put("user_id", getid);
                headers.put("access_token",prefs.getString("token",null));
                headers.put("refresh_token",prefs.getString("refreshToken",null));
                //headers.put("Content-Type", "application/json");
                Log.i(TAG, "SharedHeaders:" + headers);
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    public boolean notEmpty() {
        boolean flag = true;

        if (answer1.getText().toString().length() == 0) {
            answer1.setError(getString(R.string.security_answer_empty));
            flag = false;
        }
        if (answer2.getText().toString().length() == 0) {
            answer2.setError(getString(R.string.security_answer_empty));
            flag = false;
        }
        if (answer3.getText().toString().length() == 0) {
            answer3.setError(getString(R.string.security_answer_empty));
            flag = false;
        }
        return flag;
    }


    public void getID() {

        flag = true;
        RequestQueue requestQueue = Volley.newRequestQueue(UpdateSecurityQuestionActivity.this);
        JSONObject jsonObject = new JSONObject();
        String[] getUserIds = prefs.getString("emailID",null).split("@");
        String getid = getUserIds[0];
        try {
            jsonObject.put("user_id",getid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "GetQuestionsJson:" + jsonObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.base_URL) + getResources().getString(R.string.security_questions_list_url),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Response:" + response);
                Common.dismissProgress();
                try {
                    JSONObject loginObject = response;
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;
                    securityQuestionsList.clear();
                    if (status.equals(getResources().getString(R.string.ok))) {

                        JSONArray questions = resultObject.getJSONArray("questions");
                        for (int i = 0; i < questions.length(); i++) {
                            securityQuestionsList.add(new SecurityQuestions(
                                    questions.getJSONObject(i).getString("question"),
                                    questions.getJSONObject(i).getString("id")

                            ));

                            if (prefs.getString("updateQuestion0", null).equalsIgnoreCase(questions.getJSONObject(i).getString("question"))) {
                                ID1 = questions.getJSONObject(i).getString("id");
                            }
                            if (prefs.getString("updateQuestion1", null).equalsIgnoreCase(questions.getJSONObject(i).getString("question"))) {
                                ID2 = questions.getJSONObject(i).getString("id");
                            }
                            if (prefs.getString("updateQuestion2", null).equalsIgnoreCase(questions.getJSONObject(i).getString("question"))) {
                                ID3 = questions.getJSONObject(i).getString("id");
                            }
                        }


                    } else {
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(UpdateSecurityQuestionActivity.this);
                                View dialogview = inflater.inflate(R.layout.alert_popup, null);
                                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                                passwordMessage.setText(Common.formatErrorMessage(UpdateSecurityQuestionActivity.this,errorObject.getJSONObject(0).getString("error_code"), errorObject.getJSONObject(0).getString("error_message")));
                                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogDetails.dismiss();
                                    }
                                });
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(UpdateSecurityQuestionActivity.this);
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
                    Log.i(TAG, getResources().getString(R.string.error_message), e);
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

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itdesk_main, menu);
        return true;
    }*/
}




