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

package com.harvard.fda;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

public class FDAEventBus extends EventBus {
    private static volatile FDAEventBus defaultInstance;
    private final ScheduledExecutorService mExecutorService;

    private FDAEventBus() {
        super();
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public ScheduledFuture<Object> postDelayed(Object event, long delay) {
        return mExecutorService.schedule(new PostEventCallable(this, event), delay, TimeUnit.MILLISECONDS);
    }

    private class PostEventCallable implements Callable<Object> {
        private final FDAEventBus mEventBus;
        private final Object mEvent;

        public PostEventCallable(FDAEventBus eventBus, Object event) {
            mEventBus = eventBus;
            mEvent = event;
        }

        @Override
        public Object call() throws Exception {
            mEventBus.post(mEvent);
            return null;
        }
    }

    public static FDAEventBus getInstance() {
        if (defaultInstance == null) {
            synchronized (FDAEventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new FDAEventBus();
                }
            }
        }
        return defaultInstance;
    }

    public static void postEvent(Object event) {
        getInstance().post(event);
    }

    public static void postStickyEvent(Object event) {
        getInstance().postSticky(event);
    }

    public static <T> T getSticky(Class<T> eventType) {
        return getInstance().getStickyEvent(eventType);
    }
}