/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.util;

/**
 * Provides enumerations used across the application
 * 
 * @author BTC
 *
 */
public enum StudyMetaDataEnum {

	// Request @param keys
	RP_AUTHORIZATION("Authorization"), 
	
	RP_STUDY_IDENTIFIER("studyId"), 
	
	RP_CONSENT_VERSION("consentVersion"), 
	
	RP_ACTIVITY_IDENTIFIER("activityId"), 
	
	RP_ACTIVITY_VERSION("activityVersion"), 
	
	RP_SKIP("skip"), 
	
	RP_SUBJECT("subject"), 
	
	RP_BODY("body"), 
	
	RP_FIRST_NAME("firstName"), 
	
	RP_EMAIL("email"), 
	
	RP_APP_VERSION("appVersion"), 
	
	RP_STUDY_VERSION("studyVersion"), 
	
	RP_FORCE_UPDATE("forceUpdate"), 
	
	RP_OS_TYPE("osType"), 
	
	RP_BUNDLE_IDENTIFIER("bundleId"), 
	
	RP_MESSAGE("message"), 
	
	RP_QUERY("dbQuery"),

	// Query @param keys
	QF_BUNDLE_ID("bundleId"), 
	
	QF_OS_TYPE("osType"), 
	
	QF_CUSTOM_STUDY_ID("customStudyId"), 
	
	QF_STUDY_VERSION("studyVersion"), 
	
	QF_VERSION("version"), 
	
	QF_LIVE("live"), 
	
	QF_ACTIVE("active"), 
	
	QF_SHORT_TITLE("shortTitle"), 
	
	QF_ACTIVE_TASK_ID("activeTaskId"), 
	
	QF_QUESTIONNAIRE_ID("questionnairesId"), 
	
	QF_RESPONSE_TYPE_ID("responseTypeId"), 
	
	QF_TYPE("type"), 
	
	QF_STATUS("status"), 
	
	QF_STUDY_ID("studyId");

	private final String value;

	/**
	 * @param value
	 */
	private StudyMetaDataEnum(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	/**
	 * @author BTC
	 * @param value
	 * @return {@link StudyMetaDataEnum}
	 */
	public static StudyMetaDataEnum fromValue(String value) {
		for (StudyMetaDataEnum smde : StudyMetaDataEnum.values()) {
			if (smde.value.equals(value)) {
				return smde;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + value
				+ "]");
	}
}
