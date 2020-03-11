package com.harvard.studyAppModule.studyModel;

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
