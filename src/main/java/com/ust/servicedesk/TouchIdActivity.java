package com.ust.servicedesk;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.ust.servicedesk.utils.FingerprintHandler;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by u50281 on 6/29/2017.
 */

public class TouchIdActivity extends AppCompatActivity {
    private KeyStore keyStore;
    private static final String KEY_NAME = "WKiConnect";
    private Cipher cipher;
    TextView loginLink, fingerprintMsg;
    SharedPreferences WKPrefs;
    String status;
    static AlertDialog dialogDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_id);
        WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_touch_id);
        setSupportActionBar(toolbar);

        Window window = TouchIdActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(TouchIdActivity.this,R.color.colorPrimary));
        }

        if (WKPrefs.getBoolean("initialSetup", false)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            getSupportActionBar().hide();
        }

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        loginLink = (TextView) findViewById(R.id.textViewLoginLink);
        fingerprintMsg = (TextView) findViewById(R.id.textViewFingerPrintMsg);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            iniatilizeFingerPrint();
        } else {
            fingerprintMsg.setTextColor(getResources().getColor(R.color.errorText));
            fingerprintMsg.setText(getResources().getString(R.string.fingerprint_belowmarshmellow));
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @TargetApi(Build.VERSION_CODES.M)
    private void iniatilizeFingerPrint() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (!fingerprintManager.isHardwareDetected()) {
            fingerprintMsg.setTextColor(getResources().getColor(R.color.errorText));
            fingerprintMsg.setText(getResources().getString(R.string.fingerprint_nohardware));
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                fingerprintMsg.setTextColor(getResources().getColor(R.color.errorText));
                fingerprintMsg.setText(getResources().getString(R.string.fingerprint_nopermission));
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fingerprintMsg.setTextColor(getResources().getColor(R.color.errorText));
                    fingerprintMsg.setText(getResources().getString(R.string.fingerprint_nofingerprint));
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        fingerprintMsg.setTextColor(getResources().getColor(R.color.errorText));
                        fingerprintMsg.setText(getResources().getString(R.string.fingerprint_nosecurity));
                    } else {
                        generateKey();

                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerprintHandler helper = new FingerprintHandler(this);
                            helper.startAuth(fingerprintManager, cryptoObject);
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @Override
    public void onBackPressed() {
        if (WKPrefs.getBoolean("initialSetup", false)) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void logout() {

        try {
            SharedPreferences settings = getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            clearCookies(getApplicationContext());
            trimCache(getApplicationContext());
            dialogDetails = null;
            LayoutInflater inflater = LayoutInflater.from(TouchIdActivity.this);
            View dialogview = inflater.inflate(R.layout.confirm_login_again_popup, null);
            TextView passwordMessage = (TextView) dialogview.findViewById(R.id.alert_message);
            passwordMessage.setText(getResources().getString(R.string.confirm_login_again));
            TextView ok = (TextView) dialogview.findViewById(R.id.ok_alert);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDetails.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MicrosoftLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            TextView no = (TextView) dialogview.findViewById(R.id.no_alert);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDetails.dismiss();

                }
            });
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(TouchIdActivity.this);
            dialogbuilder.setView(dialogview);
            dialogDetails = dialogbuilder.create();
            dialogDetails.show();
            dialogDetails.setCancelable(false);
            dialogDetails.setCanceledOnTouchOutside(false);

        } catch (Exception e) {
            e.printStackTrace();
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
