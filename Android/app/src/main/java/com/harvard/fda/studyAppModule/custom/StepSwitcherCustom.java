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

package com.harvard.fda.studyAppModule.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.harvard.fda.R;

import org.researchstack.backbone.ui.step.layout.StepLayout;

import java.security.InvalidParameterException;

/**
 * Created by Naveen Raj on 08/04/2017.
 */

public class StepSwitcherCustom extends FrameLayout {
    public static final DecelerateInterpolator interpolator = new DecelerateInterpolator(2);

    public static final int SHIFT_LEFT = 1;
    public static final int SHIFT_RIGHT = -1;
    String currentStepId, stepLayoutId;

    private int animationTime;

    /**
     * Creates a new empty StepSwitcher.
     *
     * @param context the application's environment
     */
    public StepSwitcherCustom(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new empty StepSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context the application environment
     * @param attrs   a collection of attributes
     */
    public StepSwitcherCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Creates a new empty StepSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context      the application environment
     * @param attrs        a collection of attributes
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *                     resource that supplies defaults values for the TypedArray.  Can be 0 to
     *                     not look for defaults.
     */
    public StepSwitcherCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        animationTime = 100;
    }

    /**
     * Adds a new step to the view hierarchy. If a step is currently showing, the direction
     * parameter is used to indicate which direction(x-axis) that the views should animate to.
     *
     * @param stepLayout the step you want to switch to
     * @param direction  the direction of the animation in the x direction. This values can either be
     *                   {@link StepSwitcherCustom#SHIFT_LEFT} or {@link StepSwitcherCustom#SHIFT_RIGHT}
     */
    public void show(final StepLayout stepLayout, final int direction) {
        // if layouts originate from the same step, ignore show
        final View currentStep = findViewById(R.id.rsb_current_step);
        if (currentStep != null) {
            currentStepId = (String) currentStep.getTag(R.id.rsb_step_layout_id);
            stepLayoutId = (String) stepLayout.getLayout().getTag(R.id.rsb_step_layout_id);

            Log.e("currentStepId", "" + currentStepId + "   " + stepLayoutId + "   " + direction);

            if (currentStepId.equals(stepLayoutId)) {
                return;
            }
        }

        // Force crash when invalid direction is passed in. The values of the constants are used
        // when calculating the x-traversal distance
        if (direction != StepSwitcherCustom.SHIFT_LEFT && direction != StepSwitcherCustom.SHIFT_RIGHT) {
            throw new InvalidParameterException(
                    "Direction with value: " + direction + " is not supported.");
        }


        post(new Runnable() {
            @Override
            public void run() {
                // Set the id of current as something other than R.id.current_step
                int currentIndex = 0;
                if (currentStep != null) {
                    currentStep.setId(0);
                    currentIndex = StepSwitcherCustom.this.indexOfChild(currentStep);
                }

                // Add the new step to the view stack & set the id as the current step. Set the index
                // in the view hierarchy as the same as the current step on-screen
                FrameLayout.LayoutParams lp = StepSwitcherCustom.this.getLayoutParams(stepLayout);
                StepSwitcherCustom.this.addView(stepLayout.getLayout(), currentIndex, lp);
                stepLayout.getLayout().setId(R.id.rsb_current_step);

                // If the old step is gone, we can go ahead and ignore the following animation code.
                // This will usually happen on start-up of the host (e.g. activity)
                if (currentStep != null) {
                    int newTranslationX = direction * StepSwitcherCustom.this.getWidth();

                    stepLayout.getLayout().setTranslationX(newTranslationX);
                    stepLayout.getLayout()
                            .animate()
                            .setDuration(animationTime)
                            .setInterpolator(interpolator)
                            .translationX(0);

                    currentStep.animate()
                            .setInterpolator(interpolator)
                            .setDuration(animationTime)
                            .translationX(-1 * newTranslationX)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {

                                    StepSwitcherCustom.this.removeView(currentStep);

                                }
                            });
                }
            }
        });
    }

    private LayoutParams getLayoutParams(StepLayout stepLayout) {
        LayoutParams lp = (LayoutParams) stepLayout.getLayout().getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        return lp;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return StepSwitcherCustom.class.getName();
    }

}
