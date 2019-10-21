package com.harvard.studyAppModule.consent.model;

import org.researchstack.backbone.model.ConsentSection;

/**
 * Created by USER on 24-05-2017.
 */

public class ConsentSectionCustomImage extends ConsentSection {
    private String customImageName;

    /**
     * Returns an initialized consent section using the specified type.
     *
     * @param type The consent section type.
     */
    public ConsentSectionCustomImage(Type type) {
        super(type);
    }
    
    public void setCustomImageName(String imageName)
    {
        customImageName = imageName;
    }
    @Override
    public String getCustomImageName()
    {
        return customImageName;
    }
}
