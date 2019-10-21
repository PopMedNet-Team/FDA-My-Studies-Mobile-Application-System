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
package com.hphc.mystudies.service;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hphc.mystudies.bean.ActiveTaskActivityMetaDataResponse;
import com.hphc.mystudies.bean.ActivityResponse;
import com.hphc.mystudies.bean.AppResponse;
import com.hphc.mystudies.bean.AppUpdatesResponse;
import com.hphc.mystudies.bean.AppVersionInfoBean;
import com.hphc.mystudies.bean.ConsentDocumentResponse;
import com.hphc.mystudies.bean.EligibilityConsentResponse;
import com.hphc.mystudies.bean.EnrollmentTokenResponse;
import com.hphc.mystudies.bean.ErrorResponse;
import com.hphc.mystudies.bean.GatewayInfoResponse;
import com.hphc.mystudies.bean.NotificationsResponse;
import com.hphc.mystudies.bean.QuestionnaireActivityMetaDataResponse;
import com.hphc.mystudies.bean.ResourcesResponse;
import com.hphc.mystudies.bean.StudyDashboardResponse;
import com.hphc.mystudies.bean.StudyInfoResponse;
import com.hphc.mystudies.bean.StudyResponse;
import com.hphc.mystudies.bean.StudyUpdatesResponse;
import com.hphc.mystudies.bean.TermsPolicyResponse;
import com.hphc.mystudies.exception.ErrorCodes;
import com.hphc.mystudies.integration.ActivityMetaDataOrchestration;
import com.hphc.mystudies.integration.AppMetaDataOrchestration;
import com.hphc.mystudies.integration.DashboardMetaDataOrchestration;
import com.hphc.mystudies.integration.StudyMetaDataOrchestration;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;

/**
 * Web Configuration Portal (WCP) service provides access to Gateway, Study and
 * Activities metadata and configurations.
 * 
 * @author BTC
 *
 */
@Path("/")
public class StudyMetaDataService {

	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataService.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

	StudyMetaDataOrchestration studyMetaDataOrchestration = new StudyMetaDataOrchestration();
	ActivityMetaDataOrchestration activityMetaDataOrchestration = new ActivityMetaDataOrchestration();
	DashboardMetaDataOrchestration dashboardMetaDataOrchestration = new DashboardMetaDataOrchestration();
	AppMetaDataOrchestration appMetaDataOrchestration = new AppMetaDataOrchestration();

	/**
	 * Get Gateway info and Gateway resources data
	 * 
	 * @author BTC
	 * @param authorization the Basic Authorization
	 * @param context       {@link ServletContext}
	 * @param response      {@link HttpServletResponse}
	 * @return {@link GatewayInfoResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("gatewayInfo")
	public Object gatewayAppResourcesInfo(@HeaderParam("Authorization") String authorization,
			@Context ServletContext context, @Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - gatewayAppResourcesInfo() :: Starts");
		GatewayInfoResponse gatewayInfo = new GatewayInfoResponse();
		try {
			gatewayInfo = studyMetaDataOrchestration.gatewayAppResourcesInfo(authorization);
			if (!gatewayInfo.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
						StudyMetaDataConstants.FAILURE, response);
				return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.NO_RECORD).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - gatewayAppResourcesInfo() :: ERROR ", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - gatewayAppResourcesInfo() :: Ends");
		return gatewayInfo;
	}

	/**
	 * Get all the configured studies from the WCP
	 * 
	 * @author BTC
	 * @param authorization the Basic Authorization
	 * @param context       {@link ServletContext}
	 * @param response      {@link HttpServletResponse}
	 * @return {@link StudyResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("studyList")
	public Object studyList(@HeaderParam("Authorization") String authorization,
			@HeaderParam("applicationId") String applicationId, @HeaderParam("orgId") String orgId,
			@Context ServletContext context, @Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyList() :: Starts");
		StudyResponse studyResponse = new StudyResponse();
		try {
			if(!StringUtils.isEmpty(authorization) && !StringUtils.isEmpty(applicationId) && !StringUtils.isEmpty(orgId)) {
				studyResponse = studyMetaDataOrchestration.studyList(authorization, applicationId, orgId);
				if (!studyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			}else {
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
						.build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - studyList() :: ERROR ", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyList() :: Ends");
		return studyResponse;
	}

	/**
	 * Get eligibility and consent info for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId  the Study Idetifier
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link EligibilityConsentResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("eligibilityConsent")
	public Object eligibilityConsentMetadata(@QueryParam("studyId") String studyId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - eligibilityConsentMetadata() :: Starts");
		EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				eligibilityConsentResponse = studyMetaDataOrchestration.eligibilityConsentMetadata(studyId);
				if (!eligibilityConsentResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - eligibilityConsentMetadata() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - eligibilityConsentMetadata() :: Ends");
		return eligibilityConsentResponse;
	}

	/**
	 * Get consent document by passing the consent version or the activity id and
	 * activity version for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId         the Study Identifier
	 * @param consentVersion  the Consent Version
	 * @param activityId      the Activity Identifier
	 * @param activityVersion the Activity Version
	 * @param context         {@link ServletContext}
	 * @param response        {@link HttpServletResponse}
	 * @return {@link ConsentDocumentResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("consentDocument")
	public Object consentDocument(@QueryParam("studyId") String studyId,
			@QueryParam("consentVersion") String consentVersion, @QueryParam("activityId") String activityId,
			@QueryParam("activityVersion") String activityVersion, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Starts");
		ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				consentDocumentResponse = studyMetaDataOrchestration.consentDocument(studyId, consentVersion,
						activityId, activityVersion);
				if (!consentDocumentResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - resourcesForStudy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Ends");
		return consentDocumentResponse;
	}

	/**
	 * Get resources metadata for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId  the Study Identifier
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link ResourcesResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("resources")
	public Object resourcesForStudy(@QueryParam("studyId") String studyId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Starts");
		ResourcesResponse resourcesResponse = new ResourcesResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				resourcesResponse = studyMetaDataOrchestration.resourcesForStudy(studyId);
				if (!resourcesResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - resourcesForStudy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Ends");
		return resourcesResponse;
	}

	/**
	 * Get study metadata for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId  the Study Identifier
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link StudyInfoResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("studyInfo")
	public Object studyInfo(@QueryParam("studyId") String studyId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyInfo() :: Starts");
		StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				studyInfoResponse = studyMetaDataOrchestration.studyInfo(studyId);
				if (!studyInfoResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - studyInfo() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyInfo() :: Ends");
		return studyInfoResponse;
	}

	/**
	 * Get all the activities for the provided study identifier
	 * 
	 * @author BTC
	 * @param authorization the Basic Authorization
	 * @param studyId       the Study Identifier
	 * @param context       {@link ServletContext}
	 * @param response      {@link HttpServletResponse}
	 * @return {@link ActivityResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("activityList")
	public Object studyActivityList(@HeaderParam("Authorization") String authorization,
			@QueryParam("studyId") String studyId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				activityResponse = activityMetaDataOrchestration.studyActivityList(studyId, authorization);
				if (!activityResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - studyActivityList() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyActivityList() :: Ends");
		return activityResponse;
	}

	/**
	 * Get the activity metadata for the provided study and activity identifier
	 * 
	 * @author BTC
	 * @param studyId         the Study Identifier
	 * @param activityId      the Activity Identifier
	 * @param activityVersion the Activity Version
	 * @param context         {@link ServletContext}
	 * @param response        {@link HttpServletResponse}
	 * @return {@link ActiveTaskActivityMetaDataResponse} or
	 *         {@link QuestionnaireActivityMetaDataResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("activity")
	public Object studyActivityMetadata(@QueryParam("studyId") String studyId,
			@QueryParam("activityId") String activityId, @QueryParam("activityVersion") String activityVersion,
			@Context ServletContext context, @Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyActivityMetadata() :: Starts");
		QuestionnaireActivityMetaDataResponse questionnaireActivityMetaDataResponse = new QuestionnaireActivityMetaDataResponse();
		ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse = new ActiveTaskActivityMetaDataResponse();
		Boolean isValidFlag = false;
		Boolean isActivityTypeQuestionnaire = false;
		try {
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(activityId)
					&& StringUtils.isNotEmpty(activityVersion)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				isValidFlag = studyMetaDataOrchestration.isValidActivity(activityId, studyId, activityVersion);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_ACTIVITY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_ACTIVITY_ID)
							.build();
				}

				isActivityTypeQuestionnaire = studyMetaDataOrchestration.isActivityTypeQuestionnaire(activityId,
						studyId, activityVersion);
				if (!isActivityTypeQuestionnaire) {
					activeTaskActivityMetaDataResponse = activityMetaDataOrchestration
							.studyActiveTaskActivityMetadata(studyId, activityId, activityVersion);
					if (!activeTaskActivityMetaDataResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
						StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
								StudyMetaDataConstants.FAILURE, response);
						return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD)
								.build();
					}
					return activeTaskActivityMetaDataResponse;
				} else {
					questionnaireActivityMetaDataResponse = activityMetaDataOrchestration
							.studyQuestionnaireActivityMetadata(studyId, activityId, activityVersion);
					if (!questionnaireActivityMetaDataResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
						StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
								StudyMetaDataConstants.FAILURE, response);
						return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD)
								.build();
					}
					return questionnaireActivityMetaDataResponse;
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}

		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - studyActivityMetadata() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
	}

	/**
	 * Get dashboard metadata for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId  the Study Identifier
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link StudyDashboardResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("studyDashboard")
	public Object studyDashboardInfo(@QueryParam("studyId") String studyId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				studyDashboardResponse = dashboardMetaDataOrchestration.studyDashboardInfo(studyId);
				if (!studyDashboardResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - studyDashboardInfo() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}

	/**
	 * Get terms and policy for the app
	 * 
	 * @author BTC
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link TermsPolicyResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("termsPolicy")
	public Object termsPolicy(@Context ServletContext context, @Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try {
			termsPolicyResponse = appMetaDataOrchestration.termsPolicy();
			if (!termsPolicyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
						StudyMetaDataConstants.FAILURE, response);
				return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - termsPolicy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - termsPolicy() :: Ends");
		return termsPolicyResponse;
	}

	/**
	 * Fetch available notifications
	 * 
	 * @author BTC
	 * @param skip          the skip count
	 * @param authorization the Basic Authorization
	 * @param context       {@link ServletContext}
	 * @param response      {@link HttpServletResponse}
	 * @return {@link NotificationsResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("notifications")
	public Object notifications(@QueryParam("skip") String skip, @HeaderParam("Authorization") String authorization,
			@HeaderParam("applicationId") String appId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try {
			if (StringUtils.isNotEmpty(skip)) {
				notificationsResponse = appMetaDataOrchestration.notifications(skip, authorization, appId);
				if (!notificationsResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - notifications() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - notifications() :: Ends");
		return notificationsResponse;
	}

	/**
	 * Provide feedback about the app
	 * 
	 * @author BTC
	 * @param params   the feedback details
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link AppResponse}
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("feedback")
	public Object feedbackDetails(String params, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - feedbackDetails() :: Starts");
		AppResponse appResponse = new AppResponse();
		try {
			JSONObject serviceJson = new JSONObject(params);
			String subject = serviceJson.getString(StudyMetaDataEnum.RP_SUBJECT.value());
			String body = serviceJson.getString(StudyMetaDataEnum.RP_BODY.value());
			if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body)) {
				appResponse = appMetaDataOrchestration.feedback(subject, body);
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
						.build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - feedbackDetails() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - feedbackDetails() :: Ends");
		return appResponse;
	}

	/**
	 * Reach out to app owner
	 * 
	 * @author BTC
	 * @param params   the contact details
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link AppResponse}
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("contactUs")
	public Object contactUsDetails(String params, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - contactUsDetails() :: Starts");
		AppResponse appResponse = new AppResponse();
		try {
			JSONObject serviceJson = new JSONObject(params);
			String subject = serviceJson.getString(StudyMetaDataEnum.RP_SUBJECT.value());
			String body = serviceJson.getString(StudyMetaDataEnum.RP_BODY.value());
			String firstName = serviceJson.getString(StudyMetaDataEnum.RP_FIRST_NAME.value());
			String email = serviceJson.getString(StudyMetaDataEnum.RP_EMAIL.value());
			boolean inputFlag1 = StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body);
			boolean inputFlag2 = StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(email);
			if (inputFlag1 && inputFlag2) {
				appResponse = appMetaDataOrchestration.contactUsDetails(subject, body, firstName, email);
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
						.build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - contactUsDetails() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - contactUsDetails() :: Ends");
		return appResponse;
	}

	/**
	 * Check for app updates
	 * 
	 * @author BTC
	 * @param appVersion    the App Version
	 * @param authorization the Basic Authorization
	 * @param context       {@link ServletContext}
	 * @param response      {@link HttpServletResponse}
	 * @return {@link AppUpdatesResponse}
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("appUpdates")
	public Object appUpdates(@QueryParam("appVersion") String appVersion,
			@HeaderParam("Authorization") String authorization, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - appUpdates() :: Starts");
		AppUpdatesResponse appUpdatesResponse = new AppUpdatesResponse();
		try {
			if (StringUtils.isNotEmpty(appVersion) && StringUtils.isNotEmpty(authorization)) {
				appUpdatesResponse = appMetaDataOrchestration.appUpdates(appVersion, authorization);
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
						.build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - appUpdates() :: Ends");
		return appUpdatesResponse;
	}

	/**
	 * Check for study updates
	 * 
	 * @author BTC
	 * @param studyId      the Study Identifier
	 * @param studyVersion the Study Version
	 * @param context      {@link ServletContext}
	 * @param response     {@link HttpServletResponse}
	 * @return {@link StudyUpdatesResponse}
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("studyUpdates")
	public Object studyUpdates(@QueryParam("studyId") String studyId, @QueryParam("studyVersion") String studyVersion,
			@Context ServletContext context, @Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyUpdates() :: Starts");
		StudyUpdatesResponse studyUpdatesResponse = new StudyUpdatesResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(studyVersion)) {
				isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
				if (!isValidFlag) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_STUDY_ID, response);
					return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.INVALID_STUDY_ID)
							.build();
				}

				studyUpdatesResponse = appMetaDataOrchestration.studyUpdates(studyId, studyVersion);
				if (!studyUpdatesResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
						.build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyUpdates() :: Ends");
		return studyUpdatesResponse;
	}

	/**
	 * Update app version
	 * 
	 * @author BTC
	 * @param params   the App Version Details
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return the success or failure
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("updateAppVersion")
	public Object updateAppVersionDetails(String params, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - updateAppVersionDetails() :: Starts");
		String updateAppVersionResponse = "OOPS! Something went wrong.";
		try {
			JSONObject serviceJson = new JSONObject(params);
			String forceUpdate = serviceJson.getString(StudyMetaDataEnum.RP_FORCE_UPDATE.value());
			String osType = serviceJson.getString(StudyMetaDataEnum.RP_OS_TYPE.value());
			String appVersion = serviceJson.getString(StudyMetaDataEnum.RP_APP_VERSION.value());
			String bundleId = serviceJson.getString(StudyMetaDataEnum.RP_BUNDLE_IDENTIFIER.value());
			String customStudyId = serviceJson.getString(StudyMetaDataEnum.RP_STUDY_IDENTIFIER.value());
			String message = serviceJson.getString(StudyMetaDataEnum.RP_MESSAGE.value());
			if (StringUtils.isNotEmpty(forceUpdate) && StringUtils.isNotEmpty(osType)
					&& StringUtils.isNotEmpty(appVersion) && StringUtils.isNotEmpty(bundleId)
					&& StringUtils.isNotEmpty(message)) {
				if (Integer.parseInt(forceUpdate) > 1) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
							StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
					return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
							.build();
				}

				if (!osType.equals(StudyMetaDataConstants.STUDY_PLATFORM_IOS)
						&& !osType.equals(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
							StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
					return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
							.build();
				}

				if (Float.parseFloat(appVersion) < 1) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
							StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
					return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
							.build();
				}

				updateAppVersionResponse = appMetaDataOrchestration.updateAppVersionDetails(forceUpdate, osType,
						appVersion, bundleId, customStudyId, message);
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT)
						.build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - updateAppVersionDetails() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - updateAppVersionDetails() :: Ends");
		return updateAppVersionResponse;
	}

	/**
	 * Get eligibility and consent info for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId  the Study Idetifier
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link EligibilityConsentResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("validateEnrollmentToken")
	public Object validateEnrollmentToken(@QueryParam("token") String token, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - validateEnrollmentToken() :: Starts");
		EnrollmentTokenResponse enrollmentTokenResponse = new EnrollmentTokenResponse();
		Boolean isValidFlag = false;
		try {
			if (StringUtils.isNotEmpty(token)) {
				isValidFlag = studyMetaDataOrchestration.isValidToken(token);
				if (isValidFlag) {
					enrollmentTokenResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				} else {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
							StudyMetaDataConstants.INVALID_ENROLLMENT_TOKEN, response);
					return Response.status(Response.Status.NOT_FOUND)
							.entity(StudyMetaDataConstants.INVALID_ENROLLMENT_TOKEN).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - validateEnrollmentToken() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - validateEnrollmentToken() :: Ends");
		return enrollmentTokenResponse;
	}

	/**
	 * Ping application
	 * 
	 * @author BTC
	 * @return It Works!
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_XML)
	@Path("ping")
	public String ping() {
		LOGGER.info("INFO: StudyMetaDataService - ping() :: Starts ");
		String response = "It Works!";
		LOGGER.info("INFO: StudyMetaDataService - ping() :: Ends ");
		return response;
	}

	/**
	 * Get all the configured studies from the WCP
	 * 
	 * @author BTC
	 * @param studyId
	 * @param context  {@link ServletContext}
	 * @param response {@link HttpServletResponse}
	 * @return {@link StudyResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("study")
	public Object study(@QueryParam("studyId") String studyId, @Context ServletContext context,
			@Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - studyList() :: Starts");
		StudyResponse studyResponse = new StudyResponse();
		try {
			studyResponse = studyMetaDataOrchestration.study(studyId);
			if (!studyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA,
						StudyMetaDataConstants.FAILURE, response);
				return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.NO_RECORD).build();
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService - studyList() :: ERROR ", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
					StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyList() :: Ends");
		return studyResponse;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("versionInfo")
	public Object getAppVersionInfo() {
		AppVersionInfoBean appVersionInfoBean = null;
		LOGGER.info("INFO: StudyMetaDataService - getAppVersionInfo() :: Starts");
		appVersionInfoBean = appMetaDataOrchestration.getAppVersionInfo();
		LOGGER.info("INFO: StudyMetaDataService - getAppVersionInfo() :: ends");
		return appVersionInfoBean;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("activityResponce")
	public Object storeJsonResponseFile(String params, @HeaderParam("Authorization") String authorization,
			@Context ServletContext context, @Context HttpServletResponse response) throws Exception {
		LOGGER.info("INFO: StudyMetaDataService - storeJsonResponseFile() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		// boolean isValidEnrollmentId = false;
		// ActivityConditionDto activityConditionDto = null;
		try {

			if (StringUtils.isNotEmpty(params)) {
				/*
				 * // authenticate logged In user errorResponse =
				 * userService.authenticateUser(accessToken, userId, response); if
				 * (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) { return new
				 * ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); } else if
				 * (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) { return new
				 * ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); }
				 */

				/*
				 * isValidEnrollmentId = studyService.validateEnrollmentId(enrollmentId); if
				 * (!isValidEnrollmentId) { return
				 * AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code()); }
				 */

				/*
				 * activityConditionDto = activityService.
				 * fetchActivityConditionDetailsByIdandType(activityConditionId,
				 * AppConstants.FIND_BY_TYPE_ACTIVITY_CONDITIONID);
				 */

				errorResponse = appMetaDataOrchestration.storeResponseActivitiesTemp(params);

				if (!errorResponse.getError().getStatus().equals(StudyMetaDataConstants.SUCCESS)) {
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN,
							StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			} else {
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT,
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: StudyMetaDataService - storeJsonResponseFile()", e);
			// return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyMetaDataService - storeJsonResponseFile()", e);
			// return AppUtil.httpResponseForInternalServerError();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StudyMetaDataConstants.FAILURE)
					.build();
		}
		LOGGER.info("INFO: StudyMetaDataService - storeJsonResponseFile() :: ends");
		return errorResponse;
	}
}
