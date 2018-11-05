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
package com.hphc.mystudies.scheduler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.hphc.mystudies.bean.PushNotificationBean;
import com.hphc.mystudies.bo.AuditLogBO;
import com.hphc.mystudies.bo.UserBO;
import com.hphc.mystudies.dao.AuditLogDAO;
import com.hphc.mystudies.dao.LoginDAO;
import com.hphc.mystudies.dao.NotificationDAO;
import com.hphc.mystudies.dao.UsersDAO;
import com.hphc.mystudies.util.EmailNotification;
import com.hphc.mystudies.util.FdahpStudyDesignerConstants;
import com.hphc.mystudies.util.FdahpStudyDesignerUtil;

/**
 * @author BTC
 *
 */
public class FDASchedulerService {
	private static final Map<?, ?> configMap = FdahpStudyDesignerUtil
			.getAppProperties();

	private static Logger logger = Logger.getLogger(FDASchedulerService.class
			.getName());

	@Autowired
	AuditLogDAO auditLogDAO;

	@Autowired
	private LoginDAO loginDAO;

	@Autowired
	private NotificationDAO notificationDAO;

	@Autowired
	private UsersDAO usersDAO;

	/**
	 * This method create a audit log file in every mid night.
	 * 
	 * @author BTC
	 * 
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void createAuditLogs() {
		logger.info("FDASchedulerService - createAuditLogs - Starts");
		List<AuditLogBO> auditLogs = null;
		StringBuilder logString = null;
		try {
			auditLogs = auditLogDAO.getTodaysAuditLogs();
			if (auditLogs != null && !auditLogs.isEmpty()) {
				logString = new StringBuilder();
				for (AuditLogBO auditLogBO : auditLogs) {
					logString.append(auditLogBO.getAuditLogId()).append("\t");
					logString.append(auditLogBO.getCreatedDateTime()).append(
							"\t");
					if (auditLogBO.getUserBO() != null) {
						logString.append(auditLogBO.getUserBO().getFirstName())
								.append(" ")
								.append(auditLogBO.getUserBO().getLastName())
								.append("\t");
					} else {
						logString.append("anonymous user").append("\t");
					}
					logString.append(auditLogBO.getClassMethodName()).append(
							"\t");
					logString.append(auditLogBO.getActivity()).append("\t");
					logString.append(auditLogBO.getActivityDetails()).append(
							"\n");
				}
			}
			if (logString != null
					&& StringUtils.isNotBlank(logString.toString())) {
				String date = new SimpleDateFormat(
						FdahpStudyDesignerConstants.DB_SDF_DATE)
						.format(FdahpStudyDesignerUtil.addDaysToDate(
								new Date(), -1));
				File file = new File(
						((String) configMap.get("fda.logFilePath")).trim()
								+ ((String) configMap.get("fda.logFileIntials"))
										.trim() + "_" + date + ".log");
				FileUtils.writeStringToFile(file, logString.toString());
			}
			// user last login expired locking user
			loginDAO.passwordLoginBlocked();
		} catch (Exception e) {
			logger.error("FDASchedulerService - createAuditLogs - ERROR", e);

			List<String> emailAddresses = usersDAO.getSuperAdminList();
			String failLogBody;
			if (emailAddresses != null && !emailAddresses.isEmpty()) {
				Map<String, String> genarateEmailContentMap = new HashMap<>();
				String date = new SimpleDateFormat(
						FdahpStudyDesignerConstants.DB_SDF_DATE)
						.format(FdahpStudyDesignerUtil.addDaysToDate(
								new Date(), -1));
				if (emailAddresses.size() > 1) {
					genarateEmailContentMap.put("$firstName", "Admin");
				} else {
					UserBO userBO = loginDAO.getValidUserByEmail(emailAddresses
							.get(0));
					genarateEmailContentMap.put("$firstName",
							userBO.getFirstName());
				}
				genarateEmailContentMap.put("$startTime", date + " 00:00:00");
				genarateEmailContentMap.put("$endTime", date + " 23:59:59");
				failLogBody = FdahpStudyDesignerUtil.genarateEmailContent(
						(String) configMap.get("mail.audit.failure.content"),
						genarateEmailContentMap);
				EmailNotification.sendEmailNotificationToMany(
						"mail.audit.failure.subject", failLogBody,
						emailAddresses, null, null);
			}
		}
		logger.info("FDASchedulerService - createAuditLogs - Ends");
	}

	/**
	 * This method sends all notification to registration server.
	 * 
	 * @author BTC
	 * 
	 */

	@Scheduled(cron = "0 0/1 * * * ?")
	public void sendPushNotification() {
		logger.info("FDASchedulerService - sendPushNotification - Starts");
		List<PushNotificationBean> pushNotificationBeans;
		String date;
		String time;
		ObjectMapper objectMapper = new ObjectMapper();
		String responseString = "";
		try {
			date = FdahpStudyDesignerUtil.getCurrentDate();
			time = FdahpStudyDesignerUtil.privMinDateTime(
					new SimpleDateFormat(
							FdahpStudyDesignerConstants.UI_SDF_TIME)
							.format(new Date()),
					FdahpStudyDesignerConstants.UI_SDF_TIME, 1);
			pushNotificationBeans = notificationDAO.getPushNotificationList(
					date, time);
			if (pushNotificationBeans != null
					&& !pushNotificationBeans.isEmpty()) {
				JSONArray arrayToJson = new JSONArray(
						objectMapper.writeValueAsString(pushNotificationBeans));
				logger.warn("FDASchedulerService - sendPushNotification - LABKEY DATA "
						+ arrayToJson);
				JSONObject json = new JSONObject();
				json.put("notifications", arrayToJson);

				HttpClient client = new DefaultHttpClient();
				HttpResponse response;
				HttpPost post = new HttpPost(FdahpStudyDesignerUtil
						.getAppProperties().get("fda.registration.root.url")
						+ FdahpStudyDesignerUtil.getAppProperties().get(
								"push.notification.uri"));
				post.setHeader("Content-type", "application/json");

				StringEntity requestEntity = new StringEntity(json.toString(),
						ContentType.APPLICATION_JSON);
				post.setEntity(requestEntity);

				response = client.execute(post);
				responseString = EntityUtils.toString(response.getEntity());
				JSONObject res = new JSONObject(responseString);
				String result = (String) res.get("message");
				if (result == null
						|| !result
								.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
					logger.error("FDASchedulerService - sendPushNotification - LABKEY DATA SEND ERROR: "
							+ responseString);
				} else {
					logger.warn("FDASchedulerService - sendPushNotification - LABKEY DATA SEND SUCCESS");
				}
			}
		} catch (Exception e) {
			logger.error("FDASchedulerService - sendPushNotification - ERROR",
					e);
		}
		logger.info("FDASchedulerService - sendPushNotification - Ends");
	}

}
