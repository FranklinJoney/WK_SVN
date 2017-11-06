package com.ust.servicedesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ust.servicedesk.utils.ObscuredSharedPreferences;

/**
 * Created by u50281 on 6/30/2017.
 */
public class CreatePasscodeActivity extends AppCompatActivity {

    Button buttonSubmit;
    EditText editTextPasscode, editTextConfirmPasscode;
    String status;
    RelativeLayout passcodeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_create_passcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_passcode);

        Window window = CreatePasscodeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(CreatePasscodeActivity.this,R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        editTextPasscode = (EditText) findViewById(R.id.editTextPasscode);
        editTextConfirmPasscode = (EditText) findViewById(R.id.editTextConfirmPasscode);
        passcodeLayout = (RelativeLayout) findViewById(R.id.passcode_relativeLayout);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notEmpty()) {
                    if (!editTextPasscode.getText().toString().matches(editTextConfirmPasscode.getText().toString())) {
                        Snackbar.make(view, getResources().getString(R.string.passcode_mismatch), Snackbar.LENGTH_LONG)
                                .setAction(null, null).show();
                        editTextPasscode.setText("");
                        editTextConfirmPasscode.setText("");
                        editTextPasscode.requestFocus();

                    } else {
                        final SharedPreferences prefs = new ObscuredSharedPreferences(
                                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
                        prefs.edit().putString("Passcode", editTextPasscode.getText().toString()).apply();
                        prefs.edit().putInt("securityMode", 0).apply();
                        prefs.edit().putBoolean("isLogin", true).apply();
                        checkEnrollment();
                    }
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_main, menu);
        return true;
    }@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home_page) {
            Intent intent = new Intent(CreatePasscodeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == android.R.id.home) // Press Back Icon
        {
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

    private void checkEnrollment() {
        SharedPreferences WKPrefs = new ObscuredSharedPreferences(
                getApplicationContext(), getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));
        if (!WKPrefs.getBoolean("setupComplete", false)) {
            Intent intent = new Intent(CreatePasscodeActivity.this, SecurityQuestionActivity.class);
            startActivity(intent);
        } else {
            WKPrefs.edit().putBoolean("initialSetup", false).apply();
            Intent intent = new Intent(CreatePasscodeActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public boolean notEmpty() {
        boolean flag = true;
        if (editTextPasscode.getText().toString().length() != 6) {
            editTextPasscode.setError(getString(R.string.passcode_length_six));
            flag = false;
        }

        if (editTextConfirmPasscode.getText().toString().length() != 6) {
            editTextConfirmPasscode.setError(getString(R.string.passcode_length_six));
            flag = false;
        }
        return flag;
    }
}
