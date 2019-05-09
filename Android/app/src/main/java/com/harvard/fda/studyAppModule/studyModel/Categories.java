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

package com.harvard.fda.studyAppModule.studyModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 2/28/2017.
 */

public class Categories extends RealmObject {

    private boolean biologicsSafety;
    private boolean clinicalTrials;
    private boolean cosmeticsSafety;
    private boolean drugSafety;
    private boolean foodSafety;
    private boolean medicalDeviceSafety;
    private boolean observationalStudies;
    private boolean publicHealth;
    private boolean radiationEmittingProducts;
    private boolean tobaccoUse;

    public boolean isFoodSafety() {
        return foodSafety;
    }

    public void setFoodSafety(boolean foodSafety) {
        this.foodSafety = foodSafety;
    }

    public boolean isBiologicsSafety() {
        return biologicsSafety;
    }

    public void setBiologicsSafety(boolean biologicsSafety) {
        this.biologicsSafety = biologicsSafety;
    }

    public boolean isClinicalTrials() {
        return clinicalTrials;
    }

    public void setClinicalTrials(boolean clinicalTrials) {
        this.clinicalTrials = clinicalTrials;
    }

    public boolean isCosmeticsSafety() {
        return cosmeticsSafety;
    }

    public void setCosmeticsSafety(boolean cosmeticsSafety) {
        this.cosmeticsSafety = cosmeticsSafety;
    }

    public boolean isDrugSafety() {
        return drugSafety;
    }

    public void setDrugSafety(boolean drugSafety) {
        this.drugSafety = drugSafety;
    }

    public boolean isMedicalDeviceSafety() {
        return medicalDeviceSafety;
    }

    public void setMedicalDeviceSafety(boolean medicalDeviceSafety) {
        this.medicalDeviceSafety = medicalDeviceSafety;
    }

    public boolean isObservationalStudies() {
        return observationalStudies;
    }

    public void setObservationalStudies(boolean observationalStudies) {
        this.observationalStudies = observationalStudies;
    }

    public boolean isPublicHealth() {
        return publicHealth;
    }

    public void setPublicHealth(boolean publicHealth) {
        this.publicHealth = publicHealth;
    }

    public boolean isRadiationEmittingProducts() {
        return radiationEmittingProducts;
    }

    public void setRadiationEmittingProducts(boolean radiationEmittingProducts) {
        this.radiationEmittingProducts = radiationEmittingProducts;
    }

    public boolean isTobaccoUse() {
        return tobaccoUse;
    }

    public void setTobaccoUse(boolean tobaccoUse) {
        this.tobaccoUse = tobaccoUse;
    }


}
