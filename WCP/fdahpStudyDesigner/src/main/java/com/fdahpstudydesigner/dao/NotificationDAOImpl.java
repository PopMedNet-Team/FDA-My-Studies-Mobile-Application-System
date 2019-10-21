package com.fdahpstudydesigner.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.fdahpstudydesigner.bean.PushNotificationBean;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

@Repository
public class NotificationDAOImpl implements NotificationDAO {

	private static Logger logger = Logger.getLogger(NotificationDAOImpl.class);
	@Autowired
	private AuditLogDAO auditLogDAO;
	HibernateTemplate hibernateTemplate;
	private Query query = null;
	private Transaction transaction = null;

	/**
	 * Soft Deleting of notification by Id.
	 * 
	 * @author BTC
	 * @param notificationIdForDelete , requested notificationId to delete
	 * @param sessionObject           , Object of {@link SessionObject}
	 * @param notificationType        , global/study notification
	 * @return Object of {@link String}
	 */
	@Override
	public String deleteNotification(int notificationIdForDelete, SessionObject sessionObject,
			String notificationType) {
		logger.info("NotificationDAOImpl - deleteNotification() - Starts");
		Session session = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		String queryString = "";
		int i = 0;
		String activitydetails = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (notificationIdForDelete != 0) {
				queryString = "update NotificationBO NBO set NBO.modifiedBy = " + sessionObject.getUserId()
						+ ", NBO.modifiedOn = now(), NBO.notificationStatus = 1 ,NBO.notificationDone = 1 ,NBO.notificationAction = 1 where NBO.notificationId = "
						+ notificationIdForDelete;
				query = session.createQuery(queryString);
				i = query.executeUpdate();
				if (i > 0) {
					message = FdahpStudyDesignerConstants.SUCCESS;
				}
			}
			if (!notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
				activitydetails = "Gateway (app-level) notification deleted. (Notification ID = "
						+ notificationIdForDelete + ")";
			} else {
				activitydetails = "Notification for Study successfully deleted. (Notification ID = "
						+ notificationIdForDelete + ")";
			}
			// Audit logging of action performed
			message = auditLogDAO.saveToAuditLog(session, transaction, sessionObject, notificationType, activitydetails,
					"NotificationDAOImpl - deleteNotification");
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("NotificationDAOImpl - deleteNotification - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("NotificationDAOImpl - deleteNotification - Ends");
		return message;
	}

	/**
	 * Getting detail of notification by Id.
	 *
	 * @author BTC
	 * @param notificationId
	 * @return Object of {@link NotificationBO}
	 */
	@Override
	public NotificationBO getNotification(int notificationId) {
		logger.info("NotificationDAOImpl - getNotification() - Starts");
		Session session = null;
		String queryString = null;
		NotificationBO notificationBO = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			queryString = "from NotificationBO NBO where NBO.notificationId = " + notificationId;
			query = session.createQuery(queryString);
			notificationBO = (NotificationBO) query.uniqueResult();
			if (null != notificationBO) {
				notificationBO.setNotificationId(
						null != notificationBO.getNotificationId() ? notificationBO.getNotificationId() : 0);
				notificationBO.setNotificationText(
						null != notificationBO.getNotificationText() ? notificationBO.getNotificationText() : "");
				notificationBO.setScheduleDate(
						null != notificationBO.getScheduleDate() ? notificationBO.getScheduleDate() : "");
				notificationBO.setScheduleTime(
						null != notificationBO.getScheduleTime() ? notificationBO.getScheduleTime() : "");
				notificationBO.setNotificationSent(notificationBO.isNotificationSent());
				notificationBO.setNotificationScheduleType(null != notificationBO.getNotificationScheduleType()
						? notificationBO.getNotificationScheduleType()
						: "");
				if (FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE
						.equalsIgnoreCase(notificationBO.getNotificationScheduleType())) {
					notificationBO.setScheduleDate("");
					notificationBO.setScheduleTime("");
				}
			}
		} catch (Exception e) {
			logger.error("NotificationDAOImpl - getNotification - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("NotificationDAOImpl - getNotification - Ends");
		return notificationBO;
	}

	/**
	 * Getting detail of NotificationHistorylist that has been triggered to users
	 * which has dateTime.
	 * 
	 * @author BTC
	 * @param notificationId
	 * @return List of {@link NotificationHistoryBO}
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(int notificationId) {
		logger.info("NotificationDAOImpl - getNotificationHistoryListNoDateTime() - Starts");
		Session session = null;
		String queryString = null;
		List<NotificationHistoryBO> notificationHistoryListNoDateTime = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			queryString = "from NotificationHistoryBO NHBO where NHBO.notificationSentDateTime <> null and NHBO.notificationId = "
					+ notificationId + " order by NHBO.notificationSentDateTime desc";
			query = session.createQuery(queryString);
			notificationHistoryListNoDateTime = query.list();
		} catch (Exception e) {
			logger.error("NotificationDAOImpl - getNotificationHistoryListNoDateTime - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("NotificationDAOImpl - getNotificationHistoryListNoDateTime - Ends");
		return notificationHistoryListNoDateTime;
	}

	/**
	 * This method is used to list study and global notification
	 *
	 * @author BTC
	 * @param studyId
	 * @param type    , global/study notification
	 * @return list of {@link NotificationBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NotificationBO> getNotificationList(int studyId, String type) {
		logger.info("NotificationDAOImpl - getNotificationList() - Starts");
		List<NotificationBO> notificationList = null;
		Session session = null;
		String queryString = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (FdahpStudyDesignerConstants.STUDYLEVEL.equals(type) && studyId != 0) {
				queryString = "from NotificationBO NBO where NBO.studyId = " + studyId
						+ " and NBO.notificationSubType = 'Announcement' and NBO.notificationType = 'ST' and NBO.notificationStatus = 0 "
						+ "order by NBO.notificationId desc";
				query = session.createQuery(queryString);
				notificationList = query.list();
			} else {
				queryString = "from NotificationBO NBO where NBO.studyId = " + studyId
						+ " and NBO.notificationType = 'GT' and NBO.notificationStatus = 0 order by NBO.notificationId desc";
				query = session.createQuery(queryString);
				notificationList = query.list();
			}
		} catch (Exception e) {
			logger.error("NotificationDAOImpl - getNotificationList() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("NotificationDAOImpl - getNotificationList() - Ends");
		return notificationList;
	}

	/**
	 * Get list of notification schedule for given date time.
	 * 
	 * @author BTC
	 * @param date , date string
	 * @param time , time string
	 * @return list of {@link PushNotificationBean}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PushNotificationBean> getPushNotificationList(String date, String time) {
		logger.info("NotificationDAOImpl - getPushNotificationList - Starts");
		Session session = null;
		StringBuilder sb = null;
		List<PushNotificationBean> pushNotificationBeans = null;
		List<Integer> notificationIds;
		Transaction trans = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			trans = session.beginTransaction();
			sb = new StringBuilder(
					"select n.notification_id as notificationId, n.notification_text as notificationText, s.custom_study_id as customStudyId, n.notification_type as notificationType, n.notification_subType as notificationSubType,n.app_id as appId ");
			sb.append("from (notification as n) LEFT OUTER JOIN studies as s ON s.id = n.study_id")
					.append(" where n.schedule_date ='").append(date)
					.append("' AND n.is_anchor_date = false AND n.notification_done = true AND n.schedule_time like '")
					.append(time).append("%'").append(" AND (n.notification_subType='")
					.append(FdahpStudyDesignerConstants.STUDY_EVENT).append("' OR n.notification_type = '")
					.append(FdahpStudyDesignerConstants.NOTIFICATION_GT).append("' OR  s.status = '")
					.append(FdahpStudyDesignerConstants.STUDY_ACTIVE).append("')");
			query = session.createSQLQuery(sb.toString()).addScalar("notificationId").addScalar("notificationText")
					.addScalar("customStudyId").addScalar("notificationType").addScalar("notificationSubType")
					.addScalar("appId");
			pushNotificationBeans = query.setResultTransformer(Transformers.aliasToBean(PushNotificationBean.class))
					.list();
			if (null != pushNotificationBeans && !pushNotificationBeans.isEmpty()) {
				notificationIds = new ArrayList<>();
				for (PushNotificationBean pushNotificationBean : pushNotificationBeans) {
					notificationIds.add(pushNotificationBean.getNotificationId());
					if (pushNotificationBean.getNotificationSubType() == null
							|| (pushNotificationBean.getNotificationSubType() != null
									&& !FdahpStudyDesignerConstants.RESOURCE
											.equals(pushNotificationBean.getNotificationSubType())
									&& !FdahpStudyDesignerConstants.STUDY_EVENT
											.equals(pushNotificationBean.getNotificationSubType()))) {
						NotificationHistoryBO historyBO = new NotificationHistoryBO();
						historyBO.setNotificationId(pushNotificationBean.getNotificationId());
						historyBO.setNotificationSentDateTime(FdahpStudyDesignerUtil.getCurrentDateTime());
						session.save(historyBO);
					}
				}
				sb = new StringBuilder("update NotificationBO NBO set NBO.notificationSent = true");
				sb.append(" where NBO.notificationId in (").append(StringUtils.join(notificationIds, ",")).append(")");
				session.createQuery(sb.toString()).executeUpdate();
			}
			trans.commit();
		} catch (Exception e) {
			if (null != trans)
				trans.rollback();
			logger.error("NotificationDAOImpl - getPushNotificationList - ERROR", e);
		} finally {
			if (null != session)
				session.close();
		}
		logger.info("NotificationDAOImpl - getPushNotificationList - Ends");
		return pushNotificationBeans;
	}

	/**
	 * This method is used to save/update/resend of study/global notification
	 *
	 * @author BTC
	 * @param notificationBO   , {@link NotificationBO}
	 * @param notificationType , to define global/study notification
	 * @param buttonType       , action like add/edit/resend/done to update in audit
	 *                         log for both global/study notification
	 * @param sessionObject    , object of {@link SessionObject}
	 * @return Object of {@link Integer}
	 */
	@Override
	public Integer saveOrUpdateOrResendNotification(NotificationBO notificationBO, String notificationType,
			String buttonType, SessionObject sessionObject) {
		logger.info("NotificationDAOImpl - saveOrUpdateOrResendNotification() - Starts");
		Session session = null;
		NotificationBO notificationBOUpdate = null;
		Integer notificationId = 0;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (notificationBO.getNotificationId() == null) {
				notificationBOUpdate = new NotificationBO();
				notificationBOUpdate.setNotificationText(notificationBO.getNotificationText().trim());
				notificationBOUpdate.setCreatedBy(notificationBO.getCreatedBy());
				notificationBOUpdate.setCreatedOn(notificationBO.getCreatedOn());
				notificationBOUpdate.setNotificationScheduleType(notificationBO.getNotificationScheduleType());
				if ("".equals(notificationBO.getScheduleTime())) {
					notificationBOUpdate.setScheduleTime(null);
				} else {
					notificationBOUpdate.setScheduleTime(notificationBO.getScheduleTime());
				}
				if ("".equals(notificationBO.getScheduleDate())) {
					notificationBOUpdate.setScheduleDate(null);
				} else {
					notificationBOUpdate.setScheduleDate(notificationBO.getScheduleDate());
				}

				if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
					notificationBOUpdate.setNotificationDone(notificationBO.isNotificationDone());
					notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
					notificationBOUpdate.setCustomStudyId(notificationBO.getCustomStudyId());
					notificationBOUpdate.setStudyId(notificationBO.getStudyId());
					notificationBOUpdate.setNotificationAction(notificationBO.isNotificationAction());
				} else {
					notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
					notificationBOUpdate.setStudyId(0);
					notificationBOUpdate.setCustomStudyId("");
					notificationBOUpdate.setNotificationAction(false);
					notificationBOUpdate.setNotificationDone(true);
				}
				if (StringUtils.isNotEmpty(notificationBO.getAppId())) {
					notificationBOUpdate.setAppId(notificationBO.getAppId());
				}
				notificationBOUpdate
						.setNotificationSubType(FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ANNOUNCEMENT);
				notificationId = (Integer) session.save(notificationBOUpdate);
			} else {
				query = session.createQuery(
						" from NotificationBO NBO where NBO.notificationId = " + notificationBO.getNotificationId());
				notificationBOUpdate = (NotificationBO) query.uniqueResult();

				if (StringUtils.isNotBlank(notificationBO.getNotificationText())) {
					notificationBOUpdate.setNotificationText(notificationBO.getNotificationText().trim());
				} else {
					notificationBOUpdate.setNotificationText(notificationBOUpdate.getNotificationText().trim());
				}
				notificationBOUpdate.setModifiedBy(notificationBO.getModifiedBy());
				notificationBOUpdate.setModifiedOn(notificationBO.getModifiedOn());
				notificationBOUpdate.setCustomStudyId(notificationBO.getCustomStudyId());
				notificationBOUpdate.setStudyId(notificationBOUpdate.getStudyId());
				notificationBOUpdate.setNotificationSent(notificationBO.isNotificationSent());
				notificationBOUpdate.setNotificationScheduleType(notificationBO.getNotificationScheduleType());
				if (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime())) {
					notificationBOUpdate.setScheduleTime(notificationBO.getScheduleTime());
				} else {
					notificationBOUpdate.setScheduleTime(null);
				}
				if (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())) {
					notificationBOUpdate.setScheduleDate(notificationBO.getScheduleDate());
				} else {
					notificationBOUpdate.setScheduleDate(null);
				}
				if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
					notificationBOUpdate.setNotificationDone(notificationBO.isNotificationDone());
					notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
					notificationBOUpdate.setNotificationAction(notificationBO.isNotificationAction());
				} else {
					notificationBOUpdate.setNotificationDone(notificationBOUpdate.isNotificationDone());
					notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
					notificationBOUpdate.setNotificationAction(notificationBOUpdate.isNotificationAction());
				}
				notificationBOUpdate
						.setNotificationSubType(FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ANNOUNCEMENT);
				if (StringUtils.isNotEmpty(notificationBO.getAppId())) {
					notificationBOUpdate.setAppId(notificationBO.getAppId());
				}
				session.update(notificationBOUpdate);
				notificationId = notificationBOUpdate.getNotificationId();
				session.flush();
			}
			// Audit log capturing for specified request
			if (notificationId != null) {
				String activitydetails = "";
				if ("add".equals(buttonType)) {
					activitydetails = "Gateway (app-level) notification created. (Notification ID = " + notificationId
							+ ")";
				} else if ("update".equals(buttonType)) {
					activitydetails = "Gateway (app-level) notification updated. (Notification ID = " + notificationId
							+ ")";
				} else if ("resend".equals(buttonType)
						&& !notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
					activitydetails = "Gateway (app-level) notification resend. (Notification ID = " + notificationId
							+ ")";
				} else if ("resend".equals(buttonType)
						&& notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
					activitydetails = "Notification for Study successfully resent. (Study ID = "
							+ notificationBO.getCustomStudyId() + ", Notification ID = " + notificationId + ")";
				} else if ("save".equals(buttonType)) {
					activitydetails = "Notification content saved. (Study ID = " + notificationBO.getCustomStudyId()
							+ ", Notification ID = " + notificationId + ")";
				} else if ("done".equals(buttonType)) {
					activitydetails = "Notification for Study successfully  checked for minimum content completeness and marked 'Done'. (Study ID = "
							+ notificationBO.getCustomStudyId() + ")";
				}
				auditLogDAO.saveToAuditLog(session, transaction, sessionObject, notificationType, activitydetails,
						"NotificationDAOImpl - saveOrUpdateOrResendNotification");
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("NotificationDAOImpl - saveOrUpdateOrResendNotification() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("NotificationDAOImpl - saveOrUpdateOrResendNotification - Ends");
		return notificationId;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getGatwayAppList() {
		logger.info("NotificationDAOImpl - getGatwayAppList() - Starts");
		List<String> gatewayAppList = null;
		Session session = null;
		String queryString = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			queryString = "select s.app_id from studies s where" + " s.type='GT'" + " and s.version=1"
					+ " and s.app_id IS NOT NULL;";
			query = session.createSQLQuery(queryString);
			gatewayAppList = query.list();
		} catch (Exception e) {
			logger.error("NotificationDAOImpl - getGatwayAppList() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("NotificationDAOImpl - getGatwayAppList - Ends");
		return gatewayAppList;
	}

}
