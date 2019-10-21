package com.harvard;

import android.content.Context;

import com.harvard.base.BaseEventBusRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 2/15/2017.
 */

public class FDAEventBusRegistry extends BaseEventBusRegistry {

    protected FDAEventBusRegistry(Context applicationContext) {
        super(applicationContext);
    }

    @Override
    protected List<EventBusSubscriber> createDefaultSubscribers() {
        List<EventBusSubscriber> subscribers = new ArrayList<>();

        return subscribers;
    }
}