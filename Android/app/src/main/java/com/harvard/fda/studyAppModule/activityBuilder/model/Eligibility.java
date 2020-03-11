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

package com.harvard.fda.studyAppModule.activityBuilder.model;

import com.harvard.fda.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.fda.studyAppModule.consent.model.CorrectAnswers;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Rohit on 2/23/2017.
 */
public class Eligibility extends RealmObject {
    private String type;
    private String tokenTitle;
    private RealmList<Steps> test;
    private RealmList<CorrectAnswers> correctAnswers;

    public String getTokenTitle() {
        return tokenTitle;
    }

    public void setTokenTitle(String tokenTitle) {
        this.tokenTitle = tokenTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmList<Steps> getTest() {
        return test;
    }

    public void setTest(RealmList<Steps> test) {
        this.test = test;
    }

    public RealmList<CorrectAnswers> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(RealmList<CorrectAnswers> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
