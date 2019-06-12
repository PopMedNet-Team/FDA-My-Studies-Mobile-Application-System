/*******************************************************************************
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" ,WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.fdahpstudydesigner.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.fdahpstudydesigner.bo.AuditLogBO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

@Repository
public class AuditLogDAOImpl implements AuditLogDAO {

	private static Logger logger = Logger.getLogger(AuditLogDAOImpl.class);
	HibernateTemplate hibernateTemplate;

	/**
	 * Get all yesterday's audit logs
	 *
	 * @return {@link List} of {@link AuditLogBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditLogBO> getTodaysAuditLogs() {
		logger.info("AuditLogDAOImpl - getTodaysAuditLogs() - Starts");
		List<AuditLogBO> auditLogs = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String date = new SimpleDateFormat(
					FdahpStudyDesignerConstants.DB_SDF_DATE)
					.format(FdahpStudyDesignerUtil
							.addDaysToDate(new Date(), -1));
			auditLogs = session
					.createQuery(
							"select ALBO.auditLogId AS auditLogId, ALBO.userId AS userId, ALBO.activity AS activity, "
									+ "ALBO.activityDetails AS activityDetails, ALBO.createdDateTime AS createdDateTime, ALBO.classMethodName AS classMethodName, "
									+ "(select UBO from UserBO UBO where UBO.userId = ALBO.userId) As userBO "
									+ "from AuditLogBO ALBO "
									+ "where ALBO.createdDateTime BETWEEN :stDate AND :edDate")
					.setString("stDate", date + " 00:00:00")
					.setString("edDate", date + " 23:59:59")
					.setResultTransformer(
							Transformers.aliasToBean(AuditLogBO.class)).list();
		} catch (Exception e) {
			logger.error("AuditLogDAOImpl - getTodaysAuditLogs() - ERROR", e);
		} finally {
			if (session != null && !session.isOpen()) {
				session.close();
			}
		}
		logger.info("AuditLogDAOImpl - getTodaysAuditLogs() - Ends");
		return auditLogs;
	}

	/**
	 * @param session
	 * @param sessionObject
	 * @param activity
	 * @param activityDetails
	 * @param classsMethodName
	 * @return
	 */
	@Override
	public String saveToAuditLog(Session session, Transaction transaction,
			SessionObject sessionObject, String activity,
			String activityDetails, String classsMethodName) {
		logger.info("AuditLogDAOImpl - saveToAuditLog() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		AuditLogBO auditLog = null;
		Session newSession = null;
		try {
			if (session == null) {
				newSession = hibernateTemplate.getSessionFactory()
						.openSession();
				transaction = newSession.beginTransaction();
			}
			if (FdahpStudyDesignerUtil.isNotEmpty(activity)
					&& FdahpStudyDesignerUtil.isNotEmpty(activityDetails)) {
				auditLog = new AuditLogBO();
				auditLog.setActivity(activity);
				auditLog.setActivityDetails(activityDetails);
				auditLog.setUserId(sessionObject != null ? sessionObject
						.getUserId() : 0);
				auditLog.setClassMethodName(classsMethodName);
				auditLog.setCreatedDateTime(new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
				if (newSession != null) {
					newSession.save(auditLog);
				} else {
					session.save(auditLog);
				}
				message = FdahpStudyDesignerConstants.SUCCESS;
			}
			if (session == null)
				transaction.commit();

		} catch (Exception e) {
			if (session == null && null != transaction) {
				transaction.rollback();
			}
			logger.error("AuditLogDAOImpl - saveToAuditLog - ERROR", e);
		} finally {
			if (null != newSession) {
				newSession.close();
			}
		}
		logger.info("AuditLogDAOImpl - saveToAuditLog - Ends");
		return message;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	/**
	 * @param session
	 * @param sessionObject
	 * @param actionType
	 * @param studyId
	 * @return
	 */
	@Override
	public String updateDraftToEditedStatus(Session session,
			Transaction transaction, Integer userId, String actionType,
			Integer studyId) {
		logger.info("AuditLogDAOImpl - updateDraftToEditedStatus() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session newSession = null;
		String queryString;
		String draftColumn = null;
		try {
			if (session == null) {
				newSession = hibernateTemplate.getSessionFactory()
						.openSession();
				transaction = newSession.beginTransaction();
			}
			if (userId != null && studyId != null) {
				if (actionType != null
						&& (FdahpStudyDesignerConstants.DRAFT_STUDY)
								.equals(actionType)) {
					draftColumn = "hasStudyDraft = 1";
				}else if (actionType != null
						&& (FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE)
								.equals(actionType)) {
					draftColumn = "hasQuestionnaireDraft = 1, hasStudyDraft = 1 ";
				} else if (actionType != null
						&& (FdahpStudyDesignerConstants.DRAFT_ACTIVETASK)
								.equals(actionType)) {
					draftColumn = "hasActivetaskDraft = 1, hasStudyDraft = 1 ";
				} else if (actionType != null
						&& (FdahpStudyDesignerConstants.DRAFT_CONSCENT)
								.equals(actionType)) {
					draftColumn = "hasConsentDraft = 1, hasActivetaskDraft = 1, hasQuestionnaireDraft=1, hasStudyDraft = 1";
				}
				queryString = "Update StudyBo set " + draftColumn
						+ " , modifiedBy =" + userId
						+ " , modifiedOn = now() where id =" + studyId;
				if (newSession != null) {
					newSession.createQuery(queryString).executeUpdate();
				} else {
					session.createQuery(queryString).executeUpdate();
				}
				message = FdahpStudyDesignerConstants.SUCCESS;
			}
			if (session == null)
				transaction.commit();

		} catch (Exception e) {
			if (session == null && null != transaction) {
				transaction.rollback();
			}
			logger.error("AuditLogDAOImpl - updateDraftToEditedStatus - ERROR",
					e);
		} finally {
			if (null != newSession) {
				newSession.close();
			}
		}
		logger.info("AuditLogDAOImpl - updateDraftToEditedStatus - Ends");
		return message;
	}

}
