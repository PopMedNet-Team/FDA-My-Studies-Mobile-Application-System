package com.harvard.base;

import android.support.v7.app.AppCompatActivity;

import com.harvard.FDAEventBus;
import com.harvard.FDAEventBusRegistry;

public class BaseEventReportingActivity extends AppCompatActivity implements FDAEventBusRegistry.EventBusSubscriber {

    @Override
    public Object register(FDAEventBus eventBus) {
        eventBus.registerSticky(this);
        return this;
    }

    @Override
    public void unregister(FDAEventBus eventBus) {
        eventBus.unregister(this);
    }

}
