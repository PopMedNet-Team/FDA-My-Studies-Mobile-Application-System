package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

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
