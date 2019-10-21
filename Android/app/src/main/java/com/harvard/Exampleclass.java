package com.harvard;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 02/27/2017.
 */
public class Exampleclass extends RealmObject{
    private String firstname;
    private String lastname;
    private String midname;

    public String getMidname() {
        return midname;
    }

    public void setMidname(String midname) {
        this.midname = midname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
