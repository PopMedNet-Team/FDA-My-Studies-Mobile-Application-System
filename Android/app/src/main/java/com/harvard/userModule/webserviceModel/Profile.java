package com.harvard.userModule.webserviceModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 3/2/2017.
 */

public class Profile extends RealmObject {
    private String emailId;
    private String firstName;
    private String lastName;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
