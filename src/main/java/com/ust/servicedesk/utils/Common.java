package com.ust.servicedesk.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.ust.servicedesk.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by U39070 on 12/13/2016.
 */
public class Common {
    static ProgressDialog progressDialog;
    private static final String TAG = "Common";
    public static String uniqueID = UUID.randomUUID().toString();

    public static boolean isInternetConnected(Context context) {

        boolean hasInternet = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            hasInternet = true;
        }

        return hasInternet;
    }

    public static void progressBar(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public static void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
 /*   public static boolean  isShowing(){
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        return false;
    }

    public static boolean  isNotShowing(){
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        return true;
    }*/

    public static void showDialog() {

        if(progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public static void hideDialog() {

        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static String encrypt(String input) {
        // Simple encryption, not very strong!
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input.getBytes(), Base64.DEFAULT));
    }

    public static String formatErrorMessage(Context context,String errorCode, String errorMessage) {
        String errormsg = "Unknown error occurred";
        if (errorMessage != null || errorMessage != "") {
            if (errorCode != null || errorCode != "") {
                if(errorCode.equalsIgnoreCase("101")){
                    errormsg = context.getResources().getString(R.string.connection_time_out);
                }
                else if(errorCode.equalsIgnoreCase("103")){
                    errormsg = context.getResources().getString(R.string.security_answer_empty);
                }
                else if(errorCode.equalsIgnoreCase("201")){
                    errormsg = context.getResources().getString(R.string.not_reachable);
                }
                else if(errorCode.equalsIgnoreCase("202")){
                    errormsg = context.getResources().getString(R.string.enroll_login_option);
                }
                else if(errorCode.equalsIgnoreCase("203")){
                    errormsg = context.getResources().getString(R.string.user_blockced);
                }
                else if(errorCode.equalsIgnoreCase("204")){
                    errormsg = context.getResources().getString(R.string.not_reachable);
                }
                else if(errorCode.equalsIgnoreCase("301")) {
                    errormsg = context.getResources().getString(R.string.security_answers_notmatch);
                }
                else if(errorCode.equalsIgnoreCase("302")){
                    errormsg = context.getResources().getString(R.string.security_answer_empty);
                }
                else if(errorCode.equalsIgnoreCase("303")){
                    errormsg = context.getResources().getString(R.string.authentication_error_max_attempt);
                }
                else if(errorCode.equalsIgnoreCase("305")){
                    errormsg = context.getResources().getString(R.string.question_not_same);
                }
                else if(errorCode.equalsIgnoreCase("306")){
                    errormsg = context.getResources().getString(R.string.security_answers_not_same);
                }
                else if(errorCode.equalsIgnoreCase("307")){
                    errormsg = context.getResources().getString(R.string.security_questions_answer_error);
                }
                else if(errorCode.equalsIgnoreCase("308")){
                    errormsg = context.getResources().getString(R.string.security_answer_empty);
                }
                else if(errorCode.equalsIgnoreCase("401")){
                    errormsg = context.getResources().getString(R.string.password_complexity);
                }
                else if(errorCode.equalsIgnoreCase("402")){
                    errormsg = context.getResources().getString(R.string.oldpassword_new_same);
                }
                else if(errorCode.equalsIgnoreCase("601")){
                    errormsg = context.getResources().getString(R.string.security_answer_empty);
                }
                else if(errorCode.equalsIgnoreCase("602")){
                    errormsg = context.getResources().getString(R.string.security_answer_empty);
                }
                else{
                    errormsg = context.getResources().getString(R.string.security_answer_empty);
                }
            } else {
                errormsg = errorMessage;
            }
        }
        return errormsg;
    }

    public static String formattedDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/dd/mm");
        try {
            Date d = dateFormat.parse(date);
            return dateFormat.format(d);
        } catch (Exception e) {
            //java.text.ParseException: Unparseable date: Geting error
            Log.e(TAG, "Exception:" + e);
        }
        return "";
    }
    public static String shiftTimeZone(String string, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        String result = null;
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsed = null;
        try {
            parsed = sourceFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TimeZone tz = TimeZone.getTimeZone("EST");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        destFormat.setTimeZone(tz);
     /*   DateFormat format = new SimpleDateFormat("yyyy-MMMM-dd HH:MM:SS", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);

        Log.e(TAG,"ConvertedDate"+ targetCalendar.getTime());
        return targetCalendar.getTime();*/
        result = destFormat.format(parsed);
        Log.e(TAG,"ConvertedDate"+ result);
        return result;
    }
    public static String formattedDateTime(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/dd/mm HH:MM:SS");
        try {
            Date d = dateFormat.parse(date);
            return dateFormat.format(d);
        } catch (Exception e) {
            //java.text.ParseException: Unparseable date: Geting error
            Log.e(TAG, "Exception:" + e);
        }
        return "";
    }


}
