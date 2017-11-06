package com.ust.servicedesk.model;

/**
 * Created by c60678 on 10/26/2017.
 */

public class SqliteLocationModel {

    private String street;
    private String state;
    private String city ;
    private String zip;
    private String country;
    private String  name;

    public SqliteLocationModel(String name){
        this.name = name ;
    }
    public  SqliteLocationModel( String street,String state,String city,
                                String zip,String country,String name){
        this.street = street;
        this.state = state;
        this.city = city;
        this.zip = zip;
        this.country = country;
        this.name = name ;

    }

    public String getZip() {
        return zip;
    }

    public String getStreet() {
        return street;
    }

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}
