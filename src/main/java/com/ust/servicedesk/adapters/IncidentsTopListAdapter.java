package com.ust.servicedesk.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.servicedesk.AddIncidentActivity;
import com.ust.servicedesk.AddRequestActivity;
import com.ust.servicedesk.IncidentDetailActivity;
import com.ust.servicedesk.R;
import com.ust.servicedesk.RequestActivity;
import com.ust.servicedesk.model.TopListIncidents;
import com.ust.servicedesk.model.TopListRequests;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jake on 15/10/17.
 */

public class IncidentsTopListAdapter extends RecyclerView.Adapter<IncidentsTopListAdapter.MyViewHolder> {

    public static List<TopListIncidents> incident;
    public static List<TopListRequests> request_list;
    public static String check;
    public static Context context;
    public static boolean request;
    public static final String TAG = IncidentsTopListAdapter.class.getSimpleName();

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView topListTitle, toplistDesp;
        ImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.topListTitle = (TextView) itemView.findViewById(R.id.toplist_title);
            this.toplistDesp = (TextView) itemView.findViewById(R.id.toplist_description);
            this.imageView = (ImageView) itemView.findViewById(R.id.toplist_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos;
                    pos = getAdapterPosition();
                    if(!request){
                        Intent i = new Intent().setClass(context, AddIncidentActivity.class);
                        Bundle args = new Bundle();
                      //  args.putSerializable("ARRAYLIST", (Serializable) incident);
                        Log.d(TAG,"Request"+request);
                       // args.putString("incidentNumber", incident.get(pos).get());
                        i.putExtra("BUNDLE", args);
                        i.putExtra("changeTexts",false);
                        //i.putExtra("incidentTitle",request_list.get(pos).getIncidentTitle());
                        i.putExtra("incidentDes",request_list.get(pos).getDescreption());
                        i.putExtra("CreateType",TAG);
                        i.putExtra("from", RequestActivity.TAG);
                         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    /*Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) incident);
                    args.putString("incidentNumber", incident.get(pos).getIncidentID());
                    i.putExtra("BUNDLE", args);*/
                        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // context.startActivity(i);
                    }else {
                        Intent i = new Intent().setClass(context, AddIncidentActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) incident);
                        // args.putString("incidentNumber", incident.get(pos).get());
                        Log.d(TAG,"Request"+request);
                        i.putExtra("BUNDLE", args);
                        args.putBoolean("changeTexts",true);
                        i.putExtra("changeTexts",true);
                        //i.putExtra("incidentTitle",incident.get(pos).getIncidentTitle());
                        i.putExtra("incidentDes",incident.get(pos).getDescreption());
                        i.putExtra("CreateType",TAG);
                        i.putExtra("from",check);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                  //  Intent i = new Intent().setClass(context, AddIncidentActivity.class);
                    /*Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) incident);
                    args.putString("incidentNumber", incident.get(pos).getIncidentID());
                    i.putExtra("BUNDLE", args);*/
                   // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   // context.startActivity(i);


                }
            });
        }
    }

    public IncidentsTopListAdapter(List<TopListIncidents> incident, Context context, boolean request,String check) {

       // Collections.reverse(incident);
        this.incident = incident;
        this.context = context;
        this.request = request;
        this.check = check;
    }
    public IncidentsTopListAdapter(List<TopListRequests> request_list, Context context, boolean request) {

        // Collections.reverse(incident);
        this.request_list = request_list;
        this.context = context;
        this.request = request;
    }
    @Override
    public IncidentsTopListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_toplist_incidents, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final IncidentsTopListAdapter.MyViewHolder holder, final int listPosition) {

        TextView title = holder.topListTitle;
        TextView desp = holder.toplistDesp;
        ImageView imageView = holder.imageView;

        Typeface customfont = Typeface.createFromAsset(context.getAssets(), "font/OpenSans-Regular.ttf");
        holder.topListTitle.setTypeface(customfont);
        holder.toplistDesp.setTypeface(customfont);

        if(request) {
            title.setText(incident.get(listPosition).getIncidentTitle());
            desp.setText(incident.get(listPosition).getDescreption());
            imageView.setImageResource(R.drawable.ic_incident_new_blue);
        }else {
            title.setText(request_list.get(listPosition).getIncidentTitle());
            desp.setText(request_list.get(listPosition).getDescreption());
            imageView.setImageResource(R.drawable.ic_open_blue);
        }
    }


    @Override
    public int getItemCount() {
        if(request){
            return incident.size();
        }else {
            return request_list.size();
        }

    }
}
