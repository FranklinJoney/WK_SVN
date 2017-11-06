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
import com.ust.servicedesk.model.IncidentStatus;

import java.util.ArrayList;

/**
 * Created by u50289 on 7/18/2017.
 */

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.MyViewHolder> {


    public static ArrayList<IncidentStatus> incident, filteredList;
    public static Context context;
    public static Drawable icon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView status, incidentID, incidentShortDescription, incidentDate;
        ImageView statusIcon;

        public MyViewHolder(final View itemView) {
            super(itemView);
            this.status = (TextView) itemView.findViewById(R.id.status);
            this.incidentID = (TextView) itemView.findViewById(R.id.incidentNumber);
            this.incidentShortDescription = (TextView) itemView.findViewById(R.id.incident_short_description);
            this.incidentDate = (TextView) itemView.findViewById(R.id.incidentDate);
            this.statusIcon = (ImageView) itemView.findViewById(R.id.statusIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos;
                    pos = getAdapterPosition();
                    Intent i = new Intent().setClass(context, IncidentDetailActivity.class);
                    Bundle args = new Bundle();
                    String[] getIncidentIds = incident.get(pos).getIncidentID().split("#");
                    //args.putSerializable("ARRAYLIST", (Serializable) incident);
                    //args.putString("incidentNumber", incident.get(pos).getIncidentID());
                    args.putBoolean("changeTexts",true);
                    args.putString("incidentId",getIncidentIds[1]);
                    args.putString("sys_id",incident.get(pos).getUserID());
                    args.putString("createdTime",incident.get(pos).getIncidentDate());
                    args.putString("status",status.getText().toString());
                    args.putString("shortDescription",incident.get(pos).getIncidentShortDescription());
                    i.putExtra("BUNDLE", args);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }
    }

    public IncidentAdapter(ArrayList<IncidentStatus> incident, Context context) {
        this.incident = incident;
        this.filteredList = incident;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.incident_cardview, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView status = holder.status;
        TextView incidentNumber = holder.incidentID;
        TextView incidentShortDescription = holder.incidentShortDescription;
        TextView incidentDate = holder.incidentDate;
        ImageView statusIcon = holder.statusIcon;

        Typeface customfont = Typeface.createFromAsset(context.getAssets(), "font/OpenSans-Regular.ttf");
        holder.status.setTypeface(customfont);
        holder.incidentID.setTypeface(customfont);
        holder.incidentShortDescription.setTypeface(customfont);
        holder.incidentDate.setTypeface(customfont);


        incidentNumber.setText(incident.get(listPosition).getIncidentID());
        incidentShortDescription.setText(incident.get(listPosition).getIncidentShortDescription());
        /*if (incident.get(listPosition).getIncidentShortDescription().length() <= 55) {

        } else {
            incidentShortDescription.setText(incident.get(listPosition).getIncidentShortDescription().substring(0, 55) + "...");
        }*/
        String getDate[] =  incident.get(listPosition).getIncidentDate().split("\\s");
        incidentDate.setText(getDate[0]);
        if (incident.get(listPosition).getIncidentStatus().equalsIgnoreCase("3")) {
           // Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_onhold_red, null);
           // statusIcon.setImageDrawable(image);
            statusIcon.setImageResource(R.drawable.ic_incident_onhold_red);
            status.setText("OnHold");
            status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.red, null));
            //status.setText(incident.get(listPosition).getIncidentStatus());
        } else {
            if (incident.get(listPosition).getIncidentStatus().equalsIgnoreCase("2")) {
              //  Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_inprogress_purple, null);
                statusIcon.setImageResource(R.drawable.ic_incident_inprogress_purple);
                status.setText("Work In Progress");
                status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_progress_color, null));
            } else {
                if (incident.get(listPosition).getIncidentStatus().equalsIgnoreCase("6")) {
                    //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_resolved_green, null);
                    statusIcon.setImageResource(R.drawable.ic_incident_resolved_green);
                    status.setText("Resolved");
                    status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_complete_color, null));
                } else {
                    if (incident.get(listPosition).getIncidentStatus().equalsIgnoreCase("8")) {
                     //   Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_cancelled_darkblue, null);
                        statusIcon.setImageResource(R.drawable.ic_incident_cancelled_darkblue);
                        status.setText("Cancelled");
                        status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_pending_color, null));
                    } else {
                        if (incident.get(listPosition).getIncidentStatus().equalsIgnoreCase("1")) {
                            //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_new_blue, null);
                            statusIcon.setImageResource(R.drawable.ic_incident_new_blue);
                            status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_open_color, null));
                            status.setText("New");
                        } else {
                            if (incident.get(listPosition).getIncidentStatus().equalsIgnoreCase("7")) {
                               // Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_closed_grey, null);
                                statusIcon.setImageResource(R.drawable.ic_incident_closed_grey);
                                status.setText("Closed");
                                status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_cancelled_color, null));
                            } else {
                                //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_incident_new_blue, null);
                                statusIcon.setImageResource(R.drawable.ic_incident_new_blue);
                                status.setText("New");
                                status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_open_color, null));
                            }
                        }
                    }
                }

            }
        }

    }


    @Override
    public int getItemCount() {
        return incident.size();
    }
}
