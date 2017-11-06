package com.ust.servicedesk.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.servicedesk.IncidentDetailActivity;
import com.ust.servicedesk.R;
import com.ust.servicedesk.model.GlobalMessageModel;
import com.ust.servicedesk.model.IncidentStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by u50289 on 7/18/2017.
 */

public class GlobalMessageAdapter extends RecyclerView.Adapter<GlobalMessageAdapter.MyViewHolder> {


    public static List<GlobalMessageModel> globalmessage, filteredList;
    public static Context context;
    public static Drawable icon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time,date,head_message,sub_message;
        ImageView statusIcon;

        public MyViewHolder(final View itemView) {
            super(itemView);
            this.time = (TextView) itemView.findViewById(R.id.messageTime);
            this.date = (TextView) itemView.findViewById(R.id.messageDate);
            this.head_message = (TextView) itemView.findViewById(R.id.messageHead);
            this.sub_message = (TextView) itemView.findViewById(R.id.messageSub);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }

    public GlobalMessageAdapter(List<GlobalMessageModel> incident, Context context) {
        this.globalmessage = incident;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_cardview, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView date = holder.date;
        TextView time = holder.time;
        TextView messageHeader = holder.head_message;
        TextView messageSub = holder.sub_message;

        date.setText(globalmessage.get(listPosition).getGlobalmessage_date());
        time.setText(globalmessage.get(listPosition).getGlobalmessage_time()+" EST");
        messageHeader.setText(globalmessage.get(listPosition).getGlobalmessage_head());
        messageSub.setText(globalmessage.get(listPosition).getGlobalmessage_sub());
    }


    @Override
    public int getItemCount() {
        return globalmessage.size();
    }
}
