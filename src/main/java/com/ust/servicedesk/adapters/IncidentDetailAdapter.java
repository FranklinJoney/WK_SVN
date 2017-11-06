package com.ust.servicedesk.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.servicedesk.R;
import com.ust.servicedesk.model.IncidentComments;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by u50289 on 8/22/2017.
 */

public class IncidentDetailAdapter extends RecyclerView.Adapter<IncidentDetailAdapter.MyViewHolder> {

    public static ArrayList<IncidentComments> incident;
    public static Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView commentedUser, commentedDate, comment;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.commentedUser = (TextView) itemView.findViewById(R.id.commented_user);
            this.commentedDate = (TextView) itemView.findViewById(R.id.commented_date);
            this.comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }

    public IncidentDetailAdapter(ArrayList<IncidentComments> incident, Context context) {

        Collections.reverse(incident);
        this.incident = incident;
        this.context = context;
    }

    @Override
    public IncidentDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_cardview, parent, false);
        IncidentDetailAdapter.MyViewHolder myViewHolder = new IncidentDetailAdapter.MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final IncidentDetailAdapter.MyViewHolder holder, final int listPosition) {

        TextView commentedUser = holder.commentedUser;
        TextView commentedDate = holder.commentedDate;
        TextView comment = holder.comment;

        Typeface customfont = Typeface.createFromAsset(context.getAssets(), "font/OpenSans-Regular.ttf");
        holder.commentedUser.setTypeface(customfont);
        holder.commentedDate.setTypeface(customfont);
        holder.comment.setTypeface(customfont);

        commentedUser.setText(incident.get(listPosition).getCommentedUser());
        commentedDate.setText(incident.get(listPosition).getCommentedDate());
        comment.setText(incident.get(listPosition).getComments());
    }


    @Override
    public int getItemCount() {
        return incident.size();
    }
}
