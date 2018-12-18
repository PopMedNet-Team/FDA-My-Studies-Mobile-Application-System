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
package com.hphc.mystudies.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.hphc.mystudies.bean.ActiveTaskActivityMetaDataResponse;
import com.hphc.mystudies.bean.ActivityResponse;
import com.hphc.mystudies.bean.QuestionnaireActivityMetaDataResponse;
import com.hphc.mystudies.dao.ActivityMetaDataDao;
import com.hphc.mystudies.exception.OrchestrationException;
import com.hphc.mystudies.util.StudyMetaDataUtil;

/**
 * Activity metadata service that communicates with activity metadata
 * {@link ActivityMetaDataDao} repository.
 * 
 * @author BTC
 *
 */
public class ActivityMetaDataOrchestration {

	private static final Logger LOGGER = Logger
			.getLogger(ActivityMetaDataOrchestration.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

	ActivityMetaDataDao activityMetaDataDao = new ActivityMetaDataDao();

	/**
	 * Get all the activities for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 *            the study identifier
	 * @param authorization
	 *            the Basic Authorization
	 * @return {@link ActivityResponse}
	 * @throws OrchestrationException
	 */
	public ActivityResponse studyActivityList(String studyId,
			String authorization) throws OrchestrationException {
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try {
			activityResponse = activityMetaDataDao.studyActivityList(studyId,
					authorization);
		} catch (Exception e) {
			LOGGER.error(
					"ActivityMetaDataOrchestration - studyActivityList() :: ERROR",
					e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityList() :: Ends");
		return activityResponse;
	}

	/**
	 * Get the active task metadata for the provided activity version, study and
	 * activity identifier
	 * 
	 * @author BTC
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param activityVersion
	 *            the activity version
	 * @return {@link ActiveTaskActivityMetaDataResponse}
	 * @throws OrchestrationException
	 */
	public ActiveTaskActivityMetaDataResponse studyActiveTaskActivityMetadata(
			String studyId, String activityId, String activityVersion)
			throws OrchestrationException {
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActiveTaskActivityMetadata() :: Starts");
		ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse = new ActiveTaskActivityMetaDataResponse();
		try {
			activeTaskActivityMetaDataResponse = activityMetaDataDao
					.studyActiveTaskActivityMetadata(studyId, activityId,
							activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"ActivityMetaDataOrchestration - studyActiveTaskActivityMetadata() :: ERROR",
					e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActiveTaskActivityMetadata() :: Ends");
		return activeTaskActivityMetaDataResponse;
	}

	/**
	 * Get the questionnaire metadata for the provided activity version, study
	 * and activity identifier
	 * 
	 * @author BTC
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param activityVersion
	 *            the activity version
	 * @return {@link QuestionnaireActivityMetaDataResponse}
	 * @throws OrchestrationException
	 */
	public QuestionnaireActivityMetaDataResponse studyQuestionnaireActivityMetadata(
			String studyId, String activityId, String activityVersion)
			throws OrchestrationException {
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyQuestionnaireActivityMetadata() :: Starts");
		QuestionnaireActivityMetaDataResponse questionnaireActivityMetaDataResponse = new QuestionnaireActivityMetaDataResponse();
		try {
			questionnaireActivityMetaDataResponse = activityMetaDataDao
					.studyQuestionnaireActivityMetadata(studyId, activityId,
							activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"ActivityMetaDataOrchestration - studyQuestionnaireActivityMetadata() :: ERROR",
					e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyQuestionnaireActivityMetadata() :: Ends");
		return questionnaireActivityMetaDataResponse;
	}

}
