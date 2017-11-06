package com.ust.servicedesk.model;

import java.io.Serializable;

/**
 * Created by u50289 on 7/18/2017.
 */

public class IncidentStatus implements Serializable {

    String incidentStatus, incidentID, incidentShortDescription, incidentDetailedDescription, incidentDate, incidentContactNumber,
            incidentlocation, incidentPriority, userID, incidentAssignedTo, incidentAssignmentGroup;

    String incidentComments;

    public String getIncidentStatus() {
        return incidentStatus;
    }

    public void setIncidentStatus(String incidentStatus) {
        this.incidentStatus = incidentStatus;
    }

    public String getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(String incidentID) {
        this.incidentID = incidentID;
    }

    public String getIncidentShortDescription() {
        return incidentShortDescription;
    }

    public void setIncidentShortDescription(String incidentShortDescription) {
        this.incidentShortDescription = incidentShortDescription;
    }

    public String getIncidentDetailedDescription() {
        return incidentDetailedDescription;
    }

    public void setIncidentDetailedDescription(String incidentDetailedDescription) {
        this.incidentDetailedDescription = incidentDetailedDescription;
    }

    public String getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(String incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentContactNumber() {
        return incidentContactNumber;
    }

    public void setIncidentContactNumber(String incidentContactNumber) {
        this.incidentContactNumber = incidentContactNumber;
    }

    public String getIncidentlocation() {
        return incidentlocation;
    }

    public void setIncidentlocation(String incidentlocation) {
        this.incidentlocation = incidentlocation;
    }

    public String getIncidentPriority() {
        return incidentPriority;
    }

    public void setIncidentPriority(String incidentPriority) {
        this.incidentPriority = incidentPriority;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIncidentAssignedTo() {
        return incidentAssignedTo;
    }

    public void setIncidentAssignedTo(String incidentAssignedTo) {
        this.incidentAssignedTo = incidentAssignedTo;
    }

    public String getIncidentAssignmentGroup() {
        return incidentAssignmentGroup;
    }

    public void setIncidentAssignmentGroup(String incidentAssignmentGroup) {
        this.incidentAssignmentGroup = incidentAssignmentGroup;
    }
    public IncidentStatus(String incidentId,String incidentStatus,String incidentTitle,
            String incidentDate, String incidentSysId){
        this.incidentStatus = incidentStatus;
        this.incidentID = incidentId;
        this.incidentShortDescription = incidentTitle;
        this.incidentDate = incidentDate;
        this.userID = incidentSysId;
    }

    public IncidentStatus() {

    }
    public IncidentStatus(String incidentStatus, String incidentID, String incidentShortDescription, String incidentDetailedDescription, String incidentDate, String incidentContactNumber, String incidentlocation, String incidentPriority, String userID, String incidentAssignedTo, String incidentAssignmentGroup) {
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
    }

    public String getIncidentComments() {
        return incidentComments;
    }

    public void setIncidentComments(String incidentComments) {
        this.incidentComments = incidentComments;
    }
}
