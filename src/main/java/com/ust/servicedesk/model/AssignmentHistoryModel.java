package com.ust.servicedesk.model;

/**
 * Created by c60678 on 11/3/2017.
 */

public class AssignmentHistoryModel {

    public String time;
    public String date;
    public String createdBy;
    public String description;
    public AssignmentHistoryModel(){}
    public AssignmentHistoryModel(String time,String date,String createdBy,String description){
        this.time = time;
        this.date = date;
        this.createdBy = createdBy;
        this.description = description;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
