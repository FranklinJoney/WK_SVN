package com.ust.servicedesk.model;

import java.io.Serializable;

/**
 * Created by c60678 on 10/26/2017.
 */

public class TopListRequests implements Serializable {
        String incidentTitle;
        String descreption;
        String incidentId;

    public String getIncidentId() {
        return incidentId;
    }

    public String getIncidentStatus() {
        return incidentStatus;
    }

    String incidentStatus;

    public TopListRequests(){}

    public TopListRequests(String incidentId,String descreption,String incidentTitle, String incidentStatus){
        this.descreption = descreption;
        this.incidentId = incidentId;
        this.incidentStatus = incidentStatus;
        this.incidentTitle = incidentTitle;
    }
    public String getIncidentTitle() {
        return incidentTitle;
    }

    public void setIncidentTitle(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }

    public String getDescreption() {
        return descreption;
    }

    public void setDescreption(String descreption) {
        this.descreption = descreption;
    }
}
