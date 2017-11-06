package com.ust.servicedesk.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.ust.servicedesk.MainActivity;
import com.ust.servicedesk.MicrosoftLogin;
import com.ust.servicedesk.R;
import com.ust.servicedesk.SecurityQuestionActivity;

import java.io.File;

/**
 * Created by whit3hawks on 11/16/16.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    Integer authErrorCounter = 0;

    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update(errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update(helpString.toString());
    }

    @Override
    public void onAuthenticationFailed() {

        authErrorCounter++;
        if (authErrorCounter == 3) {
            this.update(context.getResources().getString(R.string.too_many_attempts));
        } else if (authErrorCounter>3){
            logout();
        } else{
            this.update(context.getResources().getString(R.string.fingerprint_fail));
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        SharedPreferences WKPrefs = new ObscuredSharedPreferences(
                context, context.getSharedPreferences(context.getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        WKPrefs.edit().putString("AppBackgroundTime", "").apply();
        if(authErrorCounter<=3) {
            WKPrefs.edit().putInt("securityMode", 1).apply();
            WKPrefs.edit().putBoolean("isLogin", true).apply();
            ((Activity) context).finish();
            if (WKPrefs.getBoolean("setupComplete", false)) {
                if (WKPrefs.getBoolean("appStart", true) || WKPrefs.getBoolean("initialSetup", true)) {
                    WKPrefs.edit().putBoolean("initialSetup", false).apply();
                    Intent homeIntent = new Intent(context, MainActivity.class);
                    context.startActivity(homeIntent);
                }


            } else {
                Intent intent = new Intent(context, SecurityQuestionActivity.class);
                context.startActivity(intent);
            }
            WKPrefs.edit().putBoolean("appStart", false).apply();
        }else{
            logout();
        }
    }


    private void update(String e) {
        TextView textView = (TextView) ((Activity) context).findViewById(R.id.textViewFingerPrintMsg);
        textView.setTextColor(context.getResources().getColor(R.color.errorText));
        textView.setText(e);
    }

    public void logout() {

        try {
            SharedPreferences settings = context.getSharedPreferences(context.getResources().getString(R.string.sharedprefs_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(context, MicrosoftLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void clearCookies(Context context) {
//
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
//    }

}
