package com.ust.servicedesk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.CacheKey;
import com.microsoft.aad.adal.ITokenCacheStore;
import com.microsoft.aad.adal.ITokenStoreQuery;
import com.microsoft.aad.adal.TokenCacheItem;
import com.ust.servicedesk.utils.Common;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class MicrosoftLogin extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "AzureADLogin";
    SharedPreferences prefs;
    private AuthenticationContext mAuthContext;
    private Button loginButton,changepwdbtn;
    String url, status;
    LinearLayout signInLayout;
    public static final String KEY_LOGIN = "login", KEY_TOKEN = "token", KEY_ANSWERS = "verifications", KEY_ID = "question_id", KEY_ANSWER = "answer",ACCESS_TOKEN="access_token",REFRESH_TOKEN="refresh_token",USER_ID="user_id";
    Toolbar toolbar;
    static AlertDialog dialogDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microsoft_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        signInLayout = (LinearLayout) findViewById(R.id.microsoftLoginLayout);
        loginButton = (Button) findViewById(R.id.login_button);
        changepwdbtn = (Button) findViewById(R.id.btn_reset_password);
        prefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

        prefs.edit().putBoolean("initialSetup", true).apply();
        loginButton.setOnClickListener(this);

        changepwdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MicrosoftLogin.this, PasswordResetIdentifyActivity.class);
                intent.putExtra((getResources().getString(R.string.password)), getResources().getString(R.string.change_reset_password));
                startActivity(intent);
            }
        });
    }


    private void getToken(final AuthenticationCallback callback) {

        // one of the acquireToken overloads
        mAuthContext.acquireToken(MicrosoftLogin.this, Constants.RESOURCE_ID, Constants.CLIENT_ID,
                Constants.REDIRECT_URL, Constants.USER_HINT, "nux=1&" + Constants.EXTRA_QP, callback);
    }

    private AuthenticationResult getLocalToken() {
        return Constants.CURRENT_RESULT;
    }

    private void setLocalToken(AuthenticationResult newToken) {
        Constants.CURRENT_RESULT = newToken;
    }

    @Override
    public void onResume() {
        super.onResume(); // Always call the superclass method first
        SharedPreferences WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        WKPrefs.edit().putBoolean("appStart", false).apply();
        WKPrefs.edit().putString("AppBackgroundTime", "").apply();
        Log.i("AADSampleActivity", "JMJ onResume");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AADSampleActivity", "JMJ onActivityResult data:" + data);
        Log.i("AADSampleActivity", "JMJ onActivityResult requestCode:" + requestCode);
        Log.i("AADSampleActivity", "JMJ onActivityResult resultCode:" + resultCode);
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        if (!Common.isInternetConnected(MicrosoftLogin.this)) {
            Snackbar.make(view, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                    .setAction(null, null).show();
        } else {
            // Ask for token and provide callback
            try {
                Common.progressBar(MicrosoftLogin.this, getString(R.string.login_progress));
                mAuthContext = new AuthenticationContext(MicrosoftLogin.this, Constants.AUTHORITY_URL,
                        false, InMemoryCacheStore.getInstance());
                mAuthContext.getCache().removeAll();

                if (Constants.CORRELATION_ID != null &&
                        Constants.CORRELATION_ID.trim().length() != 0) {
                    mAuthContext.setRequestCorrelationId(UUID.fromString(Constants.CORRELATION_ID));
                }

                mAuthContext.acquireToken(MicrosoftLogin.this, Constants.RESOURCE_ID,
                        Constants.CLIENT_ID, Constants.REDIRECT_URL, Constants.USER_HINT,
                        "nux=1&" + Constants.EXTRA_QP,
                        new AuthenticationCallback<AuthenticationResult>() {

                            @Override
                            public void onError(Exception exc) {
                                Common.dismissProgress();
                                Log.i("AADSampleActivity", " JMJ onError: " + exc.toString());
//                                Toast.makeText(getApplicationContext(), TAG + "Failed to get token", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onSuccess(AuthenticationResult result) {
                                Log.i("AADSampleActivity", " JMJ onSuccess Result: " + result);
                                Log.i("AADSampleActivity", " JMJ onSuccess getStatus: " + result.getStatus());
                                Log.i("AADSampleActivity", " JMJ getAccessToken: " + result.getAccessToken());
                                Log.i("AADSampleActivity", " JMJ getRefreshToken: " + result.getRefreshToken());
                                Log.i("AADSampleActivity", " JMJ getExpiresOn: " + result.getExpiresOn());
                                Log.i("AADSampleActivity", " JMJ getUserId: " + result.getUserInfo().getUserId());
                                Log.i("AADSampleActivity", " JMJ getDisplayableId: " + result.getUserInfo().getDisplayableId());
                                Log.i(TAG, "**************" + result.getAccessToken());
                                int indexOfStar = result.getUserInfo().getDisplayableId().indexOf("@");
                                String message = result.getUserInfo().getDisplayableId().substring(0, indexOfStar);
                                String device_IID = Common.uniqueID;
                                String message1 = result.getUserInfo().getDisplayableId();
                                Log.i(TAG, "**************" + message);
                                Log.i(TAG, "Device_UniqueID" + device_IID);
                                prefs.edit().putString("emailID", result.getUserInfo().getDisplayableId()).apply();
                                prefs.edit().putString("loginUserName", message).apply();
                                prefs.edit().putString("displayId", result.getUserInfo().getDisplayableId()).apply();
                                prefs.edit().putString("userId", result.getUserInfo().getUserId()).apply();
                                prefs.edit().putString("token", result.getAccessToken()).apply();
                                prefs.edit().putString("refreshToken",result.getRefreshToken()).apply();
                                Log.i(TAG, "**************" + prefs.getString("refreshToken",null));
                                Common.dismissProgress();
                               /* Intent  i = new Intent(MicrosoftLogin.this, MainActivity.class);
                                finish();
                                startActivity(i);*/
                                //checkRole(message);
                                getansweredQuestions();

                            }
                        });
            } catch (Exception e) {
                Common.dismissProgress();
                e.printStackTrace();

            }
        }

    }

    public static class Constants {

        public static final String SDK_VERSION = "1.0";

        /**
         * UTF-8 encoding
         */
        public static final String UTF8_ENCODING = "UTF-8";

        public static final String HEADER_AUTHORIZATION = "Authorization";

        public static final String HEADER_AUTHORIZATION_VALUE_PREFIX = "Bearer ";

        // -------------------------------AAD
        // PARAMETERS----------------------------------


        public static String AUTHORITY_URL = "https://login.microsoftonline.com/wolterskluwer.onmicrosoft.com";

        //    public static String CLIENT_ID = "e6d65cc4-f9e8-444a-9ba6-c3c82ce8086b";
        //    public static String RESOURCE_ID = "http://kidventus.com/TodoListService";
        //    public static String REDIRECT_URL = "mstodo://com.microsoft.windowsazure.activedirectory.samples.microsofttasks";


        //    public static String CLIENT_ID = "6ad66ddb-18fe-4b26-bb42-750fc7b064ae";
        //    public static String RESOURCE_ID = "6ad66ddb-18fe-4b26-bb42-750fc7b064ae";
        //    public static String REDIRECT_URL = "http://52.35.41.247:3017/api/myLogin";

        public static String CLIENT_ID = "c5f80b79-b1b9-4ba8-8662-d2ec2573fc04";
        public static String RESOURCE_ID = "https://wolterskluwer.onmicrosoft.com/7d25ba0e-4700-4aeb-9e8a-42a0d1ad35cd";
        public static String REDIRECT_URL = "myapp://com.ust.test.aadsample";


        public static String CORRELATION_ID = "";
        public static String USER_HINT = "";
        public static String EXTRA_QP = "";
        public static boolean FULL_SCREEN = true;
        public static AuthenticationResult CURRENT_RESULT = null;
        // Endpoint we are targeting for the deployed WebAPI service
        //    public static String SERVICE_URL = "http://52.35.41.247:3017/api/login";

        // ------------------------------------------------------------------------------------------

        //    static final String TABLE_WORKITEM = "WorkItem";
        //
        //    public static final String SHARED_PREFERENCE_NAME = "com.example.com.test.settings";
        //
        //    public static final String KEY_NAME_ASK_BROKER_INSTALL = "test.settings.ask.broker";
        //    public static final String KEY_NAME_CHECK_BROKER = "test.settings.check.broker";

    }

    public static class InMemoryCacheStore implements ITokenCacheStore, ITokenStoreQuery {

        private static final long serialVersionUID = 1L;
        private static final String TAG = "InMemoryCacheStore";
        private static Object sLock = new Object();
        HashMap<String, TokenCacheItem> cache = new HashMap<>();

        private static final InMemoryCacheStore INSTANCE = new InMemoryCacheStore();

        private InMemoryCacheStore() {
        }

        public static InMemoryCacheStore getInstance() {
            return INSTANCE;
        }


        @Override
        public TokenCacheItem getItem(String key) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            return cache.get(key);
        }

        @Override
        public void removeItem(String key) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            cache.remove(key);
        }

        @Override
        public void setItem(String key, TokenCacheItem item) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (item == null) {
                throw new IllegalArgumentException("item");
            }

            cache.put(key, item);
        }

        @Override
        public void removeAll() {
            cache = new HashMap<>();
        }

        // Extra helper methods can be implemented here for queries

        /**
         * User can query over iterator values.
         */
        @Override
        public Iterator<TokenCacheItem> getAll() {

            Iterator<TokenCacheItem> values = cache.values().iterator();
            return values;
        }

        /**
         * Unique users with tokens.
         *
         * @return unique users
         */
        @Override
        public HashSet<String> getUniqueUsersWithTokenCache() {
            Iterator<TokenCacheItem> results = this.getAll();
            HashSet<String> users = new HashSet<String>();

            while (results.hasNext()) {
                TokenCacheItem item = results.next();
                if (item.getUserInfo() != null && !users.contains(item.getUserInfo().getUserId())) {
                    users.add(item.getUserInfo().getUserId());
                }
            }

            return users;
        }

        /**
         * Tokens for resource.
         *
         * @param resource Resource identifier
         * @return list of {@link TokenCacheItem}
         */
        @Override
        public ArrayList<TokenCacheItem> getTokensForResource(String resource) {
            Iterator<TokenCacheItem> results = this.getAll();
            ArrayList<TokenCacheItem> tokenItems = new ArrayList<TokenCacheItem>();

            while (results.hasNext()) {
                TokenCacheItem item = results.next();
                if (item.getResource().equals(resource)) {
                    tokenItems.add(item);
                }
            }

            return tokenItems;
        }

        /**
         * Get tokens for user.
         *
         * @param userid Userid
         * @return list of {@link TokenCacheItem}
         */
        @Override
        public ArrayList<TokenCacheItem> getTokensForUser(String userid) {
            Iterator<TokenCacheItem> results = this.getAll();
            ArrayList<TokenCacheItem> tokenItems = new ArrayList<TokenCacheItem>();

            while (results.hasNext()) {
                TokenCacheItem item = results.next();
                if (item.getUserInfo() != null
                        && item.getUserInfo().getUserId().equalsIgnoreCase(userid)) {
                    tokenItems.add(item);
                }
            }

            return tokenItems;
        }

        /**
         * Clear tokens for user without additional retry.
         *
         * @param userid UserId
         */
        @Override
        public void clearTokensForUser(String userid) {
            ArrayList<TokenCacheItem> results = this.getTokensForUser(userid);

            for (TokenCacheItem item : results) {
                if (item.getUserInfo() != null
                        && item.getUserInfo().getUserId().equalsIgnoreCase(userid)) {
                    this.removeItem(CacheKey.createCacheKey(item));
                }
            }
        }

        /**
         * Get tokens about to expire.
         *
         * @return list of {@link TokenCacheItem}
         */
        @Override
        public ArrayList<TokenCacheItem> getTokensAboutToExpire() {
            Iterator<TokenCacheItem> results = this.getAll();
            ArrayList<TokenCacheItem> tokenItems = new ArrayList<TokenCacheItem>();

            while (results.hasNext()) {
                TokenCacheItem item = results.next();
                if (isAboutToExpire(item.getExpiresOn())) {
                    tokenItems.add(item);
                }
            }

            return tokenItems;
        }

        private boolean isAboutToExpire(Date expires) {
            Date validity = getTokenValidityTime().getTime();

            if (expires != null && expires.before(validity)) {
                return true;
            }

            return false;
        }

        private static final int TOKEN_VALIDITY_WINDOW = 10;

        private static Calendar getTokenValidityTime() {
            Calendar timeAhead = Calendar.getInstance();
            timeAhead.add(Calendar.SECOND, TOKEN_VALIDITY_WINDOW);
            return timeAhead;
        }

        @Override
        public boolean contains(String key) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }
            return cache.containsKey(key);
        }
    }

    public void checkRole(String message) {
        Common.progressBar(MicrosoftLogin.this, getString(R.string.login_progress));
        String roleURL = getResources().getString(R.string.base_URL) + getResources().getString(R.string.roleURL) + message;

        RequestQueue requestQueue = Volley.newRequestQueue(MicrosoftLogin.this);
        Log.e("Log","--role url-->"+roleURL);
        Log.e("Log","--req Q-->"+requestQueue);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, roleURL,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONArray resultObject = dataObject.getJSONArray("result");

                    if (status.equals(getResources().getString(R.string.ok))) {

                        if (resultObject.length() != 0) {
                            for (int i = 0; i < resultObject.length(); i++) {
                                if (resultObject.getJSONObject(i).has("country")) {
                                    prefs.edit().putString("country", resultObject.getJSONObject(i).getString("country")).apply();
                                }
                                if (resultObject.getJSONObject(i).has("vip")) {
                                    prefs.edit().putBoolean("VIP", resultObject.getJSONObject(i).getBoolean("vip")).apply();
                                }
                            }

                        }
                        getansweredQuestions();
                    }
                } catch (Exception e) {
                    Common.dismissProgress();
                    Snackbar.make(signInLayout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                            .setAction(null, null).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Common.dismissProgress();
                        if (error instanceof NoConnectionError) {
                            Snackbar.make(signInLayout, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(signInLayout, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(signInLayout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }

                    }
                }) {

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    private void getansweredQuestions() {

        url = getResources().getString(R.string.base_URL) + getResources().getString(R.string.answered_questions_URL);

        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_LOGIN, prefs.getString("loginUserName", null));

        Log.e("get answr url","---->"+url.toString());
        Log.e("getanswerdqts","---->"+params.toString());

        RequestQueue requestQueue = Volley.newRequestQueue(MicrosoftLogin.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject loginObject = response;
                    Log.i(TAG, "Response:" + response);
                    status = loginObject.getString("status");
                    JSONObject dataObject = loginObject.getJSONObject("data");
                    JSONObject resultObject = dataObject.getJSONObject("result");
                    JSONArray errorObject;
                    if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                        Common.dismissProgress();
                        if (errorObject.getJSONObject(0).has("error_message")) {
                            dialogDetails = null;
                            LayoutInflater inflater = LayoutInflater.from(MicrosoftLogin.this);
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
                            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MicrosoftLogin.this);
                            dialogbuilder.setView(dialogview);
                            dialogDetails = dialogbuilder.create();
                            dialogDetails.show();
                            dialogDetails.setCancelable(false);
                            dialogDetails.setCanceledOnTouchOutside(false);
                            clearCookies(getApplicationContext());
                        }
                    } else if (dataObject.has("error")) {
                        Common.dismissProgress();
                        JSONObject errorObjects = dataObject.getJSONObject("error");
                        JSONObject errorObjects1 = errorObjects.getJSONObject("error");
                        dialogDetails = null;
                        LayoutInflater inflater = LayoutInflater.from(MicrosoftLogin.this);
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
                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MicrosoftLogin.this);
                        dialogbuilder.setView(dialogview);
                        dialogDetails = dialogbuilder.create();
                        dialogDetails.show();
                        dialogDetails.setCancelable(false);
                        dialogDetails.setCanceledOnTouchOutside(false);
                    } else if (status.equals(getResources().getString(R.string.ok))) {
                        JSONArray verificationsObject = resultObject.getJSONArray("verifications");
                        if (verificationsObject.getJSONObject(0).getString("question").equals("") || verificationsObject.getJSONObject(1).getString("question").equals("") || verificationsObject.getJSONObject(2).getString("question").equals("")) {
                            Common.dismissProgress();
                        } else {
                            Common.dismissProgress();
                            SharedPreferences prefs = new ObscuredSharedPreferences(
                                    getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
                            prefs.edit().putBoolean("setupComplete", true).apply();
                        }
                        Intent intent = new Intent(MicrosoftLogin.this, SecurityModeActivity.class);
                        startActivity(intent);


                    } else {
                        Common.dismissProgress();
                        if ((errorObject = resultObject.getJSONArray("errors")).length() != 0) {
                            if (errorObject.getJSONObject(0).has("error_message")) {
                                dialogDetails = null;
                                LayoutInflater inflater = LayoutInflater.from(MicrosoftLogin.this);
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
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MicrosoftLogin.this);
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
                                LayoutInflater inflater = LayoutInflater.from(MicrosoftLogin.this);
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
                                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MicrosoftLogin.this);
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
                    Snackbar.make(signInLayout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
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
                            Snackbar.make(signInLayout, getResources().getString(R.string.internet_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (error instanceof TimeoutError) {
                            Snackbar.make(signInLayout, getResources().getString(R.string.connection_time_out), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else {
                            Snackbar.make(signInLayout, getResources().getString(R.string.server_error_message), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        }
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
              //  params.put(KEY_LOGIN, prefs.getString("loginUserName", null));
                params.put(ACCESS_TOKEN, prefs.getString("token", null));
                params.put(REFRESH_TOKEN, prefs.getString("refreshToken", null));
                params.put(REFRESH_TOKEN, prefs.getString("userId", null));

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_wo_home, menu);
        return true;
    }

    public static void clearCookies(Context context) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            CookieManager.getInstance().removeAllCookies(null);
//            CookieManager.getInstance().flush();
//        } else {
//            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
//            cookieSyncMngr.startSync();
//            CookieManager cookieManager = CookieManager.getInstance();
//            cookieManager.removeAllCookie();
//            cookieManager.removeSessionCookie();
//            cookieSyncMngr.stopSync();
//            cookieSyncMngr.sync();
//        }
    }
}