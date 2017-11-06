package com.ust.servicedesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.servicedesk.utils.ObscuredSharedPreferences;

/**
 * Created by u50289 on 6/21/2017.
 */

public class ChangePasswordActivity extends BaseActivity {

    TextView passwordChange;
    Button changePasswordBtn, resetPasswordBtn;
    Bundle extras;
    ImageView passwordImage;
    SharedPreferences WKPrefs;
    private static final String TAG = "ChangePasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_layout);

        Window window = ChangePasswordActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(ChangePasswordActivity.this,R.color.colorPrimary));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_change);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        passwordImage = (ImageView) findViewById(R.id.img_password);

        WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelephonyManager telMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                int simState = telMgr.getSimState();
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    String serviceNumber = getServiceNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(serviceNumber));
                    startActivity(intent);
                } else {
                    Snackbar.make(view, getString(R.string.network_check), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        changePasswordBtn = (Button) findViewById(R.id.btn_change_password);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePasswordActivity.this, PasswordChangeActivity.class);
                startActivity(intent);
            }
        });

        resetPasswordBtn = (Button) findViewById(R.id.btn_reset_password);
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePasswordActivity.this, PasswordResetIdentifyActivity.class);
                startActivity(intent);
            }
        });

        passwordChange = (TextView) findViewById(R.id.txt_change_password);
        Intent intent = getIntent();
        extras = intent.getExtras();
        if (extras != null) {
            passwordChange.setText(extras.getString(getResources().getString(R.string.password)));
            if (extras.getString(getResources().getString(R.string.password)).equalsIgnoreCase(getResources().getString(R.string.forgot_password))) {
                passwordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_forgot_pass_new));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String getServiceNumber() {
        Log.i(TAG, "VIP:" + WKPrefs.getBoolean("VIP", false));
        if (WKPrefs.getBoolean("VIP", false))
            return getString(R.string.helpdesk_contact_vip);
        else
            return getString(R.string.helpdesk_contact_normal);
    }
}
