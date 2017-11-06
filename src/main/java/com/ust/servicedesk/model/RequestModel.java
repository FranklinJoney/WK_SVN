package com.ust.servicedesk.model;

import java.io.Serializable;

/**
 * Created by u50289 on 8/11/2017.
 */

public class RequestModel implements Serializable {

    String requestStatus, requestID, requestShortDescription, requestDetailedDescription, requestDate, requestContactNumber,
            requestlocation, requestPriority, userID, requestAssignedTo, requestAssignmentGroup;

    public RequestModel(String  requestId,String requestStatus, String requestTitle,String requestDate,
                                                String requestSysId ,String createdBy)
    {
        this.requestID = requestId;
        this.requestStatus = requestStatus;
        this.requestShortDescription = requestTitle;
        this.requestDate = requestDate;
        this.userID = requestSysId;
        this.requestAssignedTo = createdBy;
    }
    public RequestModel(){};
    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestShortDescription() {
        return requestShortDescription;
    }

    public void setRequestShortDescription(String requestShortDescription) {
        this.requestShortDescription = requestShortDescription;
    }

    public String getRequestDetailedDescription() {
        return requestDetailedDescription;
    }

    public void setRequestDetailedDescription(String requestDetailedDescription) {
        this.requestDetailedDescription = requestDetailedDescription;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestContactNumber() {
        return requestContactNumber;
    }

    public void setRequestContactNumber(String requestContactNumber) {
        this.requestContactNumber = requestContactNumber;
    }

    public String getRequestlocation() {
        return requestlocation;
    }

    public void setRequestlocation(String requestlocation) {
        this.requestlocation = requestlocation;
    }

    public String getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(String requestPriority) {
        this.requestPriority = requestPriority;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRequestAssignedTo() {
        return requestAssignedTo;
    }

    public void setRequestAssignedTo(String requestAssignedTo) {
        this.requestAssignedTo = requestAssignedTo;
    }

    public String getRequestAssignmentGroup() {
        return requestAssignmentGroup;
    }

    public void setRequestAssignmentGroup(String requestAssignmentGroup) {
        this.requestAssignmentGroup = requestAssignmentGroup;
    }

    public RequestModel(String requestStatus, String requestID, String requestShortDescription, String requestDetailedDescription, String requestDate, String requestContactNumber, String requestlocation, String requestPriority, String userID, String requestAssignedTo, String requestAssignmentGroup) {

        this.requestStatus = requestStatus;
        this.requestID = requestID;
        this.requestShortDescription = requestShortDescription;
        this.requestDetailedDescription = requestDetailedDescription;
        this.requestDate = requestDate;
        this.requestContactNumber = requestContactNumber;
        this.requestlocation = requestlocation;
        this.requestPriority = requestPriority;
        this.userID = userID;
        this.requestAssignedTo = requestAssignedTo;
        this.requestAssignmentGroup = requestAssignmentGroup;
    }
}
