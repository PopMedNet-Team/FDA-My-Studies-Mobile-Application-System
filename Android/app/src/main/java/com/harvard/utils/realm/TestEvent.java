package com.harvard.utils.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yanis 11/18/16
 */

public class TestEvent extends RealmObject {
    @Index
    @PrimaryKey
    @SerializedName("id")
    private String objId;

    private boolean field1;
    private String field2;
    private int field3;
    private String field4;
    private long field5;

    public boolean isField1() {
        return field1;
    }

    public void setField1(boolean field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public int getField3() {
        return field3;
    }

    public void setField3(int field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public long getField5() {
        return field5;
    }

    public void setField5(long field5) {
        this.field5 = field5;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }
}
