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

package com.harvard.fda.base;

import android.content.Context;

import com.harvard.fda.FDAEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseEventBusRegistry {

    protected static BaseEventBusRegistry INSTANCE;

    protected final FDAEventBus eventBus = FDAEventBus.getInstance();
    protected final List<EventBusSubscriber> defaultEventSubscribers = new ArrayList<>();
    protected final HashMap<Object, EventBusSubscriber> eventSubscribers = new HashMap<>();
    protected final Context applicationContext;

    protected BaseEventBusRegistry(Context applicationContext) {
        this.applicationContext = applicationContext;
        INSTANCE = this;
    }

    public static interface EventBusSubscriber {
        Object register(FDAEventBus eventBus);
        void unregister(FDAEventBus eventBus);
    }

    public static void setInstance(BaseEventBusRegistry instance) {
        INSTANCE = instance;
    }

    public static BaseEventBusRegistry getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("No Instance of SxEventBusRegistry found. Create a new Instance through your subclass and set this INSTANCE");
        }
        return INSTANCE;
    }

    public void registerDefaultSubscribers() {
        onBeforeRegisterDefaultSubscribers();
        defaultEventSubscribers.clear();
        defaultEventSubscribers.addAll(createDefaultSubscribers());
        for (EventBusSubscriber subscriber : defaultEventSubscribers) {
            registerSubscriber(subscriber);
        }
    }

    public void unregisterAllSubscribers() {
        onBeforeUnregisterAllEventSubscribers();
        for (Object subscriber : eventSubscribers.keySet()) {
            eventBus.unregister(subscriber);
        }
        eventSubscribers.clear();
    }

    public void registerSubscriber(EventBusSubscriber subscriber) {
        if (eventSubscribers.containsValue(subscriber)) {
            return;
        }

        Object registeredSubscriber = subscriber.register(eventBus);
        eventSubscribers.put(registeredSubscriber, subscriber);
    }

    public void unregisterSubscriber(Object subscriber) {
        if (!eventSubscribers.containsKey(subscriber)) {
            return;
        }

        EventBusSubscriber visitor = eventSubscribers.get(subscriber);
        visitor.unregister(eventBus);
        eventSubscribers.remove(subscriber);
    }

    protected abstract List<EventBusSubscriber> createDefaultSubscribers();
    protected void onBeforeRegisterDefaultSubscribers(){}
    protected void onBeforeUnregisterAllEventSubscribers(){}
}
