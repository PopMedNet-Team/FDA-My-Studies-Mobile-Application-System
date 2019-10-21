package com.harvard.userModule.event;

/**
 * Created by Rohit on 2/17/2017.
 */

public class SetPasscodeEvent {
    private String nPasscode;

    public String getnPasscode() {
        return nPasscode;
    }

    public void setnPasscode(String nPasscode) {
        this.nPasscode = nPasscode;
    }
}
