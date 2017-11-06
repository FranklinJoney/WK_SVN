package com.ust.servicedesk.model;

import java.io.Serializable;

/**
 * Created by jake on 15/10/17.
 */

public class TopListIncidents implements Serializable {
    String incidentTitle;
    String incidentStatus;
    String incidentId;
    String descreption;

    public TopListIncidents(){

    }

    public String getIncidentStatus() {
        return incidentStatus;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public TopListIncidents(String incidentId,String descreption, String incidentTitle,String incidentStatus){
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
