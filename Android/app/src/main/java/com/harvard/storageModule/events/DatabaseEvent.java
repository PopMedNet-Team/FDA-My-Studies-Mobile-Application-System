package com.harvard.storageModule.events;

import java.util.HashMap;

/**
 * Created by Rohit on 2/15/2017.
 */

public class DatabaseEvent <E> {
    private String mTableName;
    private String mOperation; //insert, select, delete
    private String mType; // for inserting, copy or copy and update
    private E e;
    private HashMap<String, String> whereParams = new HashMap<>(); //  for select where conditions
    private Class aClass;

    public HashMap<String, String> getWhereParams() {
        return whereParams;
    }

    public void setWhereParams(HashMap<String, String> whereParams) {
        this.whereParams = whereParams;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public E getE() {
        return e;
    }

    public void setE(E e) {
        this.e = e;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getmOperation() {
        return mOperation;
    }

    public void setmOperation(String mOperation) {
        this.mOperation = mOperation;
    }

    public String getmTableName() {
        return mTableName;
    }

    public void setmTableName(String mTableName) {
        this.mTableName = mTableName;
    }
}
