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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbangbutton.editcodeview.EditCodeListener;
import com.bigbangbutton.editcodeview.EditCodeView;
import com.bigbangbutton.editcodeview.EditCodeWatcher;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import java.io.File;

/**
 * Created by u50281 on 6/28/2017.
 */

public class EnterPasscodeActivity extends AppCompatActivity {

    TextView loginLink;
    Button buttonSubmit;
    EditText editTextEnterPasscode;
    EditCodeView et_passCode;
    Integer authErrorCounter = 0;
    static AlertDialog dialogDetails;
    ImageView showPass;
    boolean buttonPress=true;
    TextView wrongpasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_passcode);
        loginLink = (TextView) findViewById(R.id.textViewLoginLink);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        editTextEnterPasscode = (EditText) findViewById(R.id.editTextEnterPasscode);
        et_passCode = (EditCodeView) findViewById(R.id.et_passcode);
        showPass = (ImageView) findViewById(R.id.iv_showpass);
        wrongpasscode = (TextView)findViewById(R.id.wrongpassword);
        Window window = EnterPasscodeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(EnterPasscodeActivity.this,R.color.colorPrimary));
        }

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogDetails = null;
                LayoutInflater inflater = LayoutInflater.from(EnterPasscodeActivity.this);
                View dialogview = inflater.inflate(R.layout.confirm_login_again_popup, null);
                TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
                passwordMessage.setText(getResources().getString(R.string.confirm_login_again));
                TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout();

                    }
                });
                TextView no = (TextView) dialogview.findViewById(R.id.no_alert);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogDetails.dismiss();

                    }
                });
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(EnterPasscodeActivity.this);
                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();
                dialogDetails.show();
                dialogDetails.setCancelable(false);
                dialogDetails.setCanceledOnTouchOutside(false);
            }
        });

       /* yourEditTextHere.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    // NOTE: In the author's example, he uses an identifier
                    // called searchBar. If setting this code on your EditText
                    // then use v.getWindowToken() as a reference to your
                    // EditText is passed into this callback as a TextView

                    in.hideSoftInputFromWindow(searchBar
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    userValidateEntry();
                    // Must return true here to consume event
                    return true;

                }
                return false;
            }
        });*/

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notEmpty()) {
                    SharedPreferences WKPrefs = new ObscuredSharedPreferences(
                            getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
                    String passcode = WKPrefs.getString("Passcode", "");
                   // if (passcode.equals(editTextEnterPasscode.getText().toString())) {
                    if (passcode.equals(et_passCode.getCode().toString())) {
                        WKPrefs.edit().putString("AppBackgroundTime", "").apply();
                        if (WKPrefs.getBoolean("setupComplete", false)) {
                            finish();
                            if (WKPrefs.getBoolean("appStart", true)) {
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(homeIntent);
                            }

                        } else {
                            finish();
                            Intent homeIntent = new Intent(getApplicationContext(), SecurityQuestionActivity.class);
                            startActivity(homeIntent);
                        }
                        WKPrefs.edit().putBoolean("appStart", false).apply();
                    } else {
                      //  editTextEnterPasscode.setText("");
                        et_passCode.setCode("");
                        authErrorCounter++;
                        if (authErrorCounter == 3) {
                            Snackbar.make(view, getResources().getString(R.string.too_many_attempts), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                        } else if (authErrorCounter > 3) {
                            logout();
                        } else {
                            Snackbar.make(view, getResources().getString(R.string.wrong_passcode), Snackbar.LENGTH_LONG)
                                    .setAction(null, null).show();
                           // editTextEnterPasscode.setText("");
                            et_passCode.setCode("");
                            et_passCode.clearCode();
                        }

                    }
                }
            }
        });


        et_passCode.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(String code) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_passCode.getWindowToken(),0);

            }
        });

        et_passCode.setEditCodeWatcher(new EditCodeWatcher() {
            @Override
            public void onCodeChanged(String code) {
                Log.e("CodeWatcher", " changed : " + code);
            }
        });

        et_passCode.requestFocus();

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (buttonPress) {
                    showPass.setImageResource(R.drawable.ic_passcode_not_visible_eye);
                    et_passCode.setCodeHiddenMode(false);
                    buttonPress = false;
                } else {
                    showPass.setImageResource(R.drawable.ic_passcode_visible_eye);
                    et_passCode.setCodeHiddenMode(true);
                    buttonPress = true;
                }
            }
        });



    }

    public boolean notEmpty() {
        boolean flag = true;
        //if (editTextEnterPasscode.getText().toString().length() != 6) {
        if (et_passCode.getCode().toString().length() != 6) {
         //   editTextEnterPasscode.setError(getString(R.string.passcode_length_six));
            et_passCode.setCode(getString(R.string.passcode_length_six));
            flag = false;
        }


        return flag;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout() {
        try {
            SharedPreferences settings = getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            clearCookies(getApplicationContext());
            trimCache(getApplicationContext());
            Intent intent = new Intent(EnterPasscodeActivity.this, MicrosoftLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
           CookieManager.getInstance().removeAllCookies(null);
           CookieManager.getInstance().flush();
       } else {
           CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
           CookieManager cookieManager = CookieManager.getInstance();
           cookieManager.removeAllCookie();
           cookieManager.removeSessionCookie();
           cookieSyncMngr.stopSync();
           cookieSyncMngr.sync();
       }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);

            }
        } catch (Exception e) {

        }
    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}