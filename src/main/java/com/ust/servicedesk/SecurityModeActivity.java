package com.ust.servicedesk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by u50281 on 6/29/2017.
 */

public class SecurityModeActivity extends AppCompatActivity {

    Button buttonPasscode, buttonTouchId;
    RelativeLayout relativeLayout;
    private static final String TAG = SecurityModeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_mode);
        buttonPasscode = (Button) findViewById(R.id.buttonPasscode);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        buttonTouchId = (Button) findViewById(R.id.buttonTouchID);
        Window window = SecurityModeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(SecurityModeActivity.this, R.color.colorPrimary));
        }
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) SecurityModeActivity.this.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                if (!fingerprintManager.isHardwareDetected()) {
                    // Device doesn't support fingerprint authentication
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // User hasn't enrolled any fingerprints to authenticate with
                    buttonTouchId.setVisibility(View.GONE);
                } else {
                    // Everything is ready for fingerprint authentication
                }
                return;
            }
            else {
                Snackbar.make(relativeLayout,"You Don't have FingerPrint Permission",Snackbar.LENGTH_SHORT).show();
            }
        }else{
            buttonTouchId.setVisibility(View.GONE);
        }*/



        buttonTouchId.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 Intent touchIdIntent = new Intent(getApplicationContext(), TouchIdActivity.class);
                                                 startActivity(touchIdIntent);
                                             }

                                         }
        );



        buttonPasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"ButtonPassCode");
                Intent passcodeIntent = new Intent(getApplicationContext(), CreatePasscodeActivity.class);
                startActivity(passcodeIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itdesk_main, menu);
        return true;
    }
}

