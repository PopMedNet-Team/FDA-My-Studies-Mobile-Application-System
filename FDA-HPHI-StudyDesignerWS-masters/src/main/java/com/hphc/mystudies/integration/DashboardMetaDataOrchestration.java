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

import com.hphc.mystudies.bean.StudyDashboardResponse;
import com.hphc.mystudies.dao.DashboardMetaDataDao;
import com.hphc.mystudies.exception.OrchestrationException;
import com.hphc.mystudies.util.StudyMetaDataUtil;

/**
 * Dashboard metadata service that communicates with dashboard metadata
 * {@link DashboardMetaDataDao} repository.
 * 
 * @author BTC
 *
 */
public class DashboardMetaDataOrchestration {

	private static final Logger LOGGER = Logger
			.getLogger(DashboardMetaDataOrchestration.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

	DashboardMetaDataDao dashboardMetaDataDao = new DashboardMetaDataDao();

	/**
	 * Get dashboard metadata for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 *            the study identifier
	 * @return {@link StudyDashboardResponse}
	 * @throws OrchestrationException
	 */
	public StudyDashboardResponse studyDashboardInfo(String studyId)
			throws OrchestrationException {
		LOGGER.info("INFO: DashboardMetaDataOrchestration - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		try {
			studyDashboardResponse = dashboardMetaDataDao
					.studyDashboardInfo(studyId);
		} catch (Exception e) {
			LOGGER.error(
					"DashboardMetaDataOrchestration - studyDashboardInfo() :: ERROR",
					e);
		}
		LOGGER.info("INFO: DashboardMetaDataOrchestration - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
}
