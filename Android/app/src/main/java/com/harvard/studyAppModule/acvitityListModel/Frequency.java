package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 04/06/2017.
 */

public class Frequency  extends RealmObject {
    private RealmList<FrequencyRuns> runs;

    private RealmList<AnchorRuns> anchorRuns;

    private String type;

    public RealmList<FrequencyRuns> getRuns() {
        return runs;
    }

    public void setRuns(RealmList<FrequencyRuns> runs) {
        this.runs = runs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmList<AnchorRuns> getAnchorRuns() {
        return anchorRuns;
    }

    public void setAnchorRuns(RealmList<AnchorRuns> anchorRuns) {
        this.anchorRuns = anchorRuns;
    }
}
