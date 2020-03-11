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

import com.harvard.fda.FDAEventBus;

public abstract class BaseSubscriber implements BaseEventBusRegistry.EventBusSubscriber {

        private FDAEventBus mEventBus;

        @Override
        public final Object register(FDAEventBus eventBus) {
            mEventBus = eventBus;
            mEventBus.register(this);
            return this;
        }

        public final void unregister(FDAEventBus eventBus) {
            eventBus.unregister(this);
            mEventBus = null;
        }

        protected void post(Object event) {
            if (mEventBus == null) {
                throw new NullPointerException("PluginController.register() was not called. Is the controller registered in the EventBusRegistry?");
            }
            mEventBus.post(event);
        }

        protected void postSticky(Object event) {
            if (mEventBus == null) {
                throw new NullPointerException("PluginController.register() was not called. Is the controller registered in the EventBusRegistry?");
            }
            mEventBus.postSticky(event);
        }

        protected <T> T removeStickyEvent(Class<T> eventType) {
            if (mEventBus == null) {
                throw new NullPointerException("PluginController.register() was not called. Is the controller registered in the EventBusRegistry?");
            }
            return mEventBus.removeStickyEvent(eventType);
        }

        protected boolean removeStickyEvent(Object event) {
            if (mEventBus == null) {
                throw new NullPointerException("PluginController.register() was not called. Is the controller registered in the EventBusRegistry?");
            }
            return mEventBus.removeStickyEvent(event);
        }

        protected <T> T getStickyEvent(Class<T> eventType) {
            if (mEventBus == null) {
                throw new NullPointerException("PluginController.register() was not called. Is the controller registered in the EventBusRegistry?");
            }
            return mEventBus.getStickyEvent(eventType);
        }
  }