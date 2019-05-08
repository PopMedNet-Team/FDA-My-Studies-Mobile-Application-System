/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.storageModule.events;

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
