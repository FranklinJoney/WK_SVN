package com.ust.servicedesk.utils;

/**
 * Created by c60678 on 10/25/2017.
 */

public class Miscellaneous {

    public static String[] parseOnCreate(String onCreateDate){
        String[] parseString = onCreateDate.split(" ");
        return parseString;
    }
}
