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
import com.ust.servicedesk.model.RequestModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by u50289 on 8/11/2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {


    public static List<RequestModel> requests;
    public static Context context;
    public static Drawable icon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView status, requestID, requestShortDescription, requestDate;
        ImageView statusIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.status = (TextView) itemView.findViewById(R.id.status);
            this.requestID = (TextView) itemView.findViewById(R.id.requestID);
            this.requestShortDescription = (TextView) itemView.findViewById(R.id.requestShortDescription);
            this.requestDate = (TextView) itemView.findViewById(R.id.requestDate);
            this.statusIcon = (ImageView) itemView.findViewById(R.id.statusIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos;

                   /* String[] getIncidentIds = incident.get(pos).getIncidentID().split("#");
                    //args.putSerializable("ARRAYLIST", (Serializable) incident);
                    //args.putString("incidentNumber", incident.get(pos).getIncidentID());
                    args.putBoolean("changeTexts",true);
                    args.putString("incidentId",getIncidentIds[1]);
                    args.putString("sys_id",incident.get(pos).getUserID());
                    args.putString("createdTime",incident.get(pos).getIncidentDate());
                    args.putString("status",status.getText().toString());
                    args.putString("shortDescription",incident.get(pos).getIncidentShortDescription());
                    i.putExtra("BUNDLE", args);*/

                    pos = getAdapterPosition();
                    Intent i = new Intent().setClass(context, IncidentDetailActivity.class);
                    Bundle args = new Bundle();
                    String[] getIncidentIds = requests.get(pos).getRequestID().split("#");
                    args.putSerializable("ARRAYLIST", (Serializable) requests);
                    args.putBoolean("changeTexts",false);
                    args.putString("incidentNumber", requests.get(pos).getRequestID());
                    args.putString("incidentId",getIncidentIds[1]);
                    args.putString("sys_id",requests.get(pos).getUserID());
                    args.putString("createdTime",requests.get(pos).getRequestDate());
                    args.putString("status",status.getText().toString());
                    args.putString("shortDescription",requests.get(pos).getRequestShortDescription());
                    i.putExtra("BUNDLE", args);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }
    }

    public RequestAdapter(List<RequestModel> requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_cardview, parent, false);
        RequestAdapter.MyViewHolder myViewHolder = new RequestAdapter.MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RequestAdapter.MyViewHolder holder, final int listPosition) {

        TextView status = holder.status;
        TextView requestID = holder.requestID;
        TextView requestShortDescription = holder.requestShortDescription;
        TextView requestDate = holder.requestDate;
        ImageView statusIcon = holder.statusIcon;

        Typeface customfont = Typeface.createFromAsset(context.getAssets(), "font/OpenSans-Regular.ttf");
        holder.status.setTypeface(customfont);
        holder.requestID.setTypeface(customfont);
        holder.requestShortDescription.setTypeface(customfont);
        holder.requestDate.setTypeface(customfont);


        requestID.setText(requests.get(listPosition).getRequestID());
        requestShortDescription.setText(requests.get(listPosition).getRequestShortDescription());
        /*if (requests.get(listPosition).getRequestShortDescription().length() <= 55) {

        } else {
            requestShortDescription.setText(requests.get(listPosition).getRequestShortDescription().substring(0, 55) + "...");
        }*/
        //requestDate.setText(requests.get(listPosition).getRequestDate());
        String getDate[] =  requests.get(listPosition).getRequestDate().split("\\s");
        requestDate.setText(getDate[0]);
        //requestDate.setText(requests.get(listPosition).getRequestDate().substring(0, 10));
        if (requests.get(listPosition).getRequestStatus().equalsIgnoreCase("-5")) {
            //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_request_pending_darkblue, null);
            status.setText("Pending");
            status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_pending_color, null));
            statusIcon.setImageResource(R.drawable.ic_request_pending_darkblue);
        } else {
            if (requests.get(listPosition).getRequestStatus().equalsIgnoreCase("1")) {
                //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_open_blue, null);
                statusIcon.setImageResource(R.drawable.ic_open_blue);
               // status.setText(context.getResources().getString(R.id.request_open));
                status.setText("Open");
                status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_open_color, null));
            } else {
                if (requests.get(listPosition).getRequestStatus().equalsIgnoreCase("2")) {
                    //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_request_inprogress_purple, null);
                    statusIcon.setImageResource(R.drawable.ic_request_inprogress_purple);
                   // status.setText(context.getResources().getString(R.id.request_progress));
                    status.setText("In Progress");
                    status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_progress_color, null));
                } else {
                    if (requests.get(listPosition).getRequestStatus().equalsIgnoreCase("3")) {
                       // Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_request_complete_green, null);
                        statusIcon.setImageResource(R.drawable.ic_request_complete_green);
                      //  status.setText(context.getResources().getString(R.id.request_closed));
                        status.setText("Completed");
                        status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_complete_color, null));
                    } else {
                        if (requests.get(listPosition).getRequestStatus().equalsIgnoreCase("4")) {
                            //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_request_cancelled_rejected_grey, null);
                            statusIcon.setImageResource(R.drawable.ic_request_cancelled_rejected_grey);
                            //status.setText(context.getResources().getString(R.id.request_cancelled));
                            status.setText("Cancelled");
                            status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_cancelled_color, null));
                        } else {
                            if (requests.get(listPosition).getRequestStatus().equalsIgnoreCase("5")) {
                                //Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_request_rejected_red, null);
                                statusIcon.setImageResource(R.drawable.ic_request_rejected_red);
                                //status.setText(context.getResources().getString(R.id.request_rejected));
                                status.setText("Rejected");
                                status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_rejected_color, null));
                            } else {
                               // Drawable image = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_request_cancelled_rejected_grey, null);
                                statusIcon.setImageResource(R.drawable.ic_request_cancelled_rejected_grey);
                                status.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.request_cancelled_color, null));
                            }
                        }
                    }
                }

            }
        }

    }


    @Override
    public int getItemCount() {
        return requests.size();
    }
}
