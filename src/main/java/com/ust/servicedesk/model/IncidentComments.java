package com.ust.servicedesk.model;

/**
 * Created by u50289 on 8/22/2017.
 */

public class IncidentComments {

    String commentedUser, commentedDate, comments;

    public IncidentComments(String commentedUser, String commentedDate, String comments) {
        this.commentedUser = commentedUser;
        this.commentedDate = commentedDate;
        this.comments = comments;
    }

    public String getCommentedUser() {
        return commentedUser;
    }

    public void setCommentedUser(String commentedUser) {
        this.commentedUser = commentedUser;
    }

    public String getCommentedDate() {
        return commentedDate;
    }

    public void setCommentedDate(String commentedDate) {
        this.commentedDate = commentedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
