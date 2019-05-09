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

package com.harvard.fda.studyAppModule.custom.question;

import com.harvard.fda.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.fda.studyAppModule.custom.ChoiceAnswerFormatCustom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Naveen Raj on 04/28/2017.
 */

public class TextAnswerFormatRegex extends ChoiceAnswerFormatCustom {

    private String mRegex;
    public static final int UNLIMITED_LENGTH = 0;

    private boolean isMultipleLines = false;
    private int mMaximumLength;
    private String mInValidMsg;

    public TextAnswerFormatRegex(int maximumLength, String Regex, String InValidMsg) {
        this.mRegex = Regex;
        this.mMaximumLength = maximumLength;
        this.mInValidMsg = InValidMsg;
    }

    public String getInValidMsg() {
        return mInValidMsg;
    }

    /**
     * Creates a TextAnswerFormat with no maximum length
     */
    public TextAnswerFormatRegex() {
        this(UNLIMITED_LENGTH);
    }

    /**
     * Creates a TextAnswerFormat with a specified maximum length
     *
     * @param maximumLength the maximum text length allowed
     */
    public TextAnswerFormatRegex(int maximumLength) {
        this.mMaximumLength = maximumLength;
    }

    /**
     * Returns the maximum length for the answer, <code>UNLIMITED_LENGTH</code> (0) if no maximum
     *
     * @return the maximum length, <code>UNLIMITED_LENGTH</code> (0) if no maximum
     */
    public int getMaximumLength() {
        return mMaximumLength;
    }

    /**
     * Sets whether the EditText should allow multiple lines.
     *
     * @param isMultipleLines boolean indicating if multiple lines are allowed
     */
    public void setIsMultipleLines(boolean isMultipleLines) {
        this.isMultipleLines = isMultipleLines;
    }

    /**
     * Returns whether multiple lines are allowed.
     *
     * @return boolean indicating if multiple lines are allowed
     */
    public boolean isMultipleLines() {
        return isMultipleLines;
    }


    public boolean isAnswerValid(String text) {
        if (text != null && text.length() > 0 && (mMaximumLength == UNLIMITED_LENGTH || text.length() <= mMaximumLength) && validate(text)) {
            return true;
        }
        return false;
    }

    private boolean validate(final String hex) {
        if (mRegex != null && !mRegex.equalsIgnoreCase("")) {
            try {
                Pattern pattern = Pattern.compile(mRegex);
                Matcher matcher = pattern.matcher(hex);
                return matcher.matches();
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.TextRegex;
    }
}
