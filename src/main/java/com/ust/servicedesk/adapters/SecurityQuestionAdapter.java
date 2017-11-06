package com.ust.servicedesk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.servicedesk.R;
import com.ust.servicedesk.SecurityQuestionActivity;
import com.ust.servicedesk.model.SecurityQuestions;
import com.ust.servicedesk.utils.ObscuredSharedPreferences;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by u50289 on 7/27/2017.
 */

public class SecurityQuestionAdapter extends RecyclerView.Adapter<SecurityQuestionAdapter.MyViewHolder> {


    public static ArrayList<SecurityQuestions> securityQuestionses;
    public static Context context;
    public static Drawable icon;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView label;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.label = (TextView) itemView.findViewById(R.id.label);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos;
                    pos = getAdapterPosition();
                    final SharedPreferences prefs = new ObscuredSharedPreferences(
                            context, context.getSharedPreferences(context.getResources().getString(R.string.sharedprefs_name), Context.MODE_PRIVATE));

                    Intent saveSecurityIntent = new Intent(context, SecurityQuestionActivity.class);
                    saveSecurityIntent.putExtra("id", securityQuestionses.get(pos).getQuestion());
                    saveSecurityIntent.putExtra("questionID", securityQuestionses.get(pos).getId());
                    saveSecurityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    prefs.edit().putString("id", securityQuestionses.get(pos).getQuestion()).apply();
                    Bundle extras = saveSecurityIntent.getExtras();
                    extras.putString("id", securityQuestionses.get(pos).getQuestion());
                    ((Activity) context).setResult(RESULT_OK, saveSecurityIntent);
                    ((Activity) context).finish();
                }
            });
        }

    }

    public SecurityQuestionAdapter(ArrayList<SecurityQuestions> securityQuestionses, Context context) {
        this.securityQuestionses = securityQuestionses;
        this.context = context;
    }

    @Override
    public SecurityQuestionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_listview_security_questions, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final SecurityQuestionAdapter.MyViewHolder holder, final int listPosition) {

        TextView label = holder.label;

        Typeface customfont = Typeface.createFromAsset(context.getAssets(), "font/OpenSans-Regular.ttf");
        holder.label.setTypeface(customfont);

        label.setText(securityQuestionses.get(listPosition).getQuestion());

    }


    @Override
    public int getItemCount() {
        return securityQuestionses.size();
    }


}
