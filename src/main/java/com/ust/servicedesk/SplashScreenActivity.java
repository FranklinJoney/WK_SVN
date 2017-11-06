package com.ust.servicedesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import java.io.File;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences WKPrefs;
    Intent i;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window window = SplashScreenActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(SplashScreenActivity.this,R.color.colorPrimary));
        }

        context = getApplicationContext();
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(1 * 1000);
                    checkLogin();
                    finish();
                } catch (Exception e) {

                }
            }
        };
        background.start();
    }

    public void checkLogin() {
        if (preferenceFileExist(getResources().getString(R.string.sharedprefs_name))) {
            WKPrefs = new ObscuredSharedPreferences(
                    getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
            if (!WKPrefs.getBoolean("isLogin", false)) {
                i = new Intent(this, MicrosoftLogin.class);
                finish();
                startActivity(i);
            } else {
                WKPrefs.edit().putBoolean("appStart", true).apply();
                WKPrefs.edit().putString("AppBackgroundTime", "").apply();
                i = new Intent(this, MainActivity.class);
                finish();
                startActivity(i);
            }
        } else {
            i = new Intent(this, MicrosoftLogin.class);
            finish();
            startActivity(i);
        }

    }

    public boolean preferenceFileExist(String fileName) {
        File f = new File(context.getApplicationInfo().dataDir + "/shared_prefs/"
                + fileName + ".xml");
        return f.exists();
    }
}
