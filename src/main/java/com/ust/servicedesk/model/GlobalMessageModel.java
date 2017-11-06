package com.ust.servicedesk.model;

import java.io.Serializable;

/**
 * Created by u50289 on 7/18/2017.
 */

public class GlobalMessageModel implements Serializable {

    String globalmessage_date;
    String globalmessage_time;
    String[] globalmessage_onCreate;
    String globalmessage_shortDes;
    String globalmessage_des;
    String globalmessageID;

    public  GlobalMessageModel( String messageId,String shortdescription,String description,
                                                    String[] createdOn){
        this.globalmessageID = messageId;
        this.globalmessage_shortDes = shortdescription;
        this.globalmessage_des = description;
        this.globalmessage_onCreate = createdOn;
        this.globalmessage_date = globalmessage_onCreate[0];
        this.globalmessage_time = globalmessage_onCreate[1];

    }

    public String getGlobalmessage_date() {
        return globalmessage_date;
    }

    public void setGlobalmessage_date(String globalmessage_date) {
        this.globalmessage_date = globalmessage_date;
    }

    public String getGlobalmessage_time() {
        return globalmessage_time;
    }

    public void setGlobalmessage_time(String globalmessage_time) {
        this.globalmessage_time = globalmessage_time;
    }

    public String getGlobalmessage_head() {
        return globalmessage_shortDes;
    }

    public void setGlobalmessage_head(String globalmessage_head) {
        this.globalmessage_shortDes = globalmessage_head;
    }

    public String getGlobalmessage_sub() {
        return globalmessage_des;
    }

    public void setGlobalmessage_sub(String globalmessage_sub) {
        this.globalmessage_des = globalmessage_sub;
    }

    public String getGlobalmessageID() {
        return globalmessageID;
    }

    /*public GlobalMessageModel(String incidentStatus, String incidentID, String incidentShortDescription, String incidentDetailedDescription, String incidentDate, String incidentContactNumber, String incidentlocation, String incidentPriority, String userID, String incidentAssignedTo, String incidentAssignmentGroup) {
        this.incidentStatus = incidentStatus;
        this.incidentID = incidentID;
        this.incidentShortDescription = incidentShortDescription;
        this.incidentDetailedDescription = incidentDetailedDescription;
        this.incidentDate = incidentDate;
        this.incidentContactNumber = incidentContactNumber;
        this.incidentlocation = incidentlocation;
        this.incidentPriority = incidentPriority;
        this.userID = userID;
        this.incidentAssignedTo = incidentAssignedTo;
        this.incidentAssignmentGroup = incidentAssignmentGroup;
    }*/


}
