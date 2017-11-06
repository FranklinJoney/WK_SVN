package com.ust.servicedesk.model;

import java.io.Serializable;

/**
 * Created by u50281 on 7/25/2017.
 */

public class SecurityQuestions implements Serializable {


    public SecurityQuestions() {
    }

    String question, Id;

    public String getId() {
        return Id;
    }

    public void setId(String ID) {
        this.Id = ID;
    }

    public SecurityQuestions(String question, String Id) {

        this.question = question;
        this.Id = Id;
    }


    public String getQuestion() {
        return question;
    }

}
