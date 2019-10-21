package com.harvard.studyAppModule.studyModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 5/3/2017.
 */

public class NotificationData extends RealmObject {
    @PrimaryKey
    private int id = 1;
    private String message;
    private RealmList<Notification> notifications;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RealmList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(RealmList<Notification> notifications) {
        this.notifications = notifications;
    }
}
