package com.fdahpstudydesigner.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.fdahpstudydesigner.bean.DynamicBean;
import com.fdahpstudydesigner.bean.DynamicFrequencyBean;
import com.fdahpstudydesigner.bean.StudyIdBean;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bean.StudyPageBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.Checklist;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ComprehensionTestResponseBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.ConsentMasterInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.FormBo;
import com.fdahpstudydesigner.bo.FormMappingBo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionConditionBranchBo;
import com.fdahpstudydesigner.bo.QuestionReponseTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnaireCustomScheduleBo;
import com.fdahpstudydesigner.bo.QuestionnairesFrequenciesBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.ReferenceTablesBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyActivityVersionBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPermissions;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 *
 * @author BTC
 *
 */
@Repository
public class StudyDAOImpl implements StudyDAO {

	private static Logger logger = Logger.getLogger(StudyDAOImpl.class.getName());
	@Autowired
	private AuditLogDAO auditLogDAO;
	HibernateTemplate hibernateTemplate;
	private Query query = null;
	String queryString = "";
	private Transaction transaction = null;

	public StudyDAOImpl() {
		// Unused
	}

	/**
	 * This method is used to validate the activetaskType for android platform
	 * 
	 * @author BTC
	 * @param Integer , studyId
	 * @return String, SUCCESS or FAILURE
	 */
	@Override
	public String checkActiveTaskTypeValidation(Integer studyId) {
		logger.info("StudyDAOImpl - checkActiveTaskTypeValidation() - starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String searchQuery = "select count(*) from active_task a" + " where a.study_id=" + studyId
					+ " and a.task_type_id" + " in(select c.active_task_list_id from active_task_list c"
					+ " where a.active=1 and c.task_name in('" + FdahpStudyDesignerConstants.TOWER_OF_HANOI + "','"
					+ FdahpStudyDesignerConstants.SPATIAL_SPAN_MEMORY + "'));";
			BigInteger count = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
			if (count != null && count.intValue() > 0) {
				message = FdahpStudyDesignerConstants.SUCCESS;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - checkActiveTaskTypeValidation() - ERROR ", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - checkActiveTaskTypeValidation() - Ends");
		return message;
	}

	/**
	 * Describes the order of an comprehension question while creating the new
	 * comprehension question
	 * 
	 * @author BTC
	 * @param studyId {@link StudyBo}
	 * @return int count
	 */
	@Override
	public int comprehensionTestQuestionOrder(Integer studyId) {
		logger.info("StudyDAOImpl - comprehensionTestQuestionOrder() - Starts");
		Session session = null;
		int count = 0;
		ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=" + studyId
					+ " and CTQBO.active=1 order by CTQBO.sequenceNo desc");
			query.setMaxResults(1);
			comprehensionTestQuestionBo = (ComprehensionTestQuestionBo) query.uniqueResult();
			if (comprehensionTestQuestionBo != null) {
				count = comprehensionTestQuestionBo.getSequenceNo() + 1;
			} else {
				count = count + 1;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - comprehensionTestQuestionOrder() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - comprehensionTestQuestionOrder() - Ends");
		return count;
	}

	/**
	 * Describes the order of an consent item while creating the new consent item in
	 * the study consent section
	 * 
	 * @author BTC
	 * @param studyId {@link StudyBo}
	 * @return int count
	 */
	@Override
	public int consentInfoOrder(Integer studyId) {
		logger.info("StudyDAOImpl - consentInfoOrder() - Starts");
		Session session = null;
		int count = 1;
		ConsentInfoBo consentInfoBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("From ConsentInfoBo CIB where CIB.studyId=" + studyId
					+ " and CIB.active=1 order by CIB.sequenceNo DESC");
			query.setMaxResults(1);
			consentInfoBo = ((ConsentInfoBo) query.uniqueResult());
			if (consentInfoBo != null) {
				count = consentInfoBo.getSequenceNo() + 1;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - consentInfoOrder() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - consentInfoOrder() - Ends");
		return count;
	}

	/**
	 * Describes the delete of an comprehension test question from the list of test
	 * question in the study consent section
	 * 
	 * @author BTC
	 * @param Integer , questionId {@link ComprehensionTestQuestionBo}
	 * @return String : SUCCESS or FAILURE
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteComprehensionTestQuestion(Integer questionId, Integer studyId, SessionObject sessionObject) {
		logger.info("StudyDAOImpl - deleteComprehensionTestQuestion() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		String searchQuery = "";
		ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
		StudySequenceBo studySequence = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
			searchQuery = "From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=" + studyId
					+ " and CTQBO.active=1 order by CTQBO.sequenceNo asc";
			comprehensionTestQuestionList = session.createQuery(searchQuery).list();
			if (comprehensionTestQuestionList != null && !comprehensionTestQuestionList.isEmpty()) {
				boolean isValue = false;
				for (ComprehensionTestQuestionBo comprehensionTestQuestion : comprehensionTestQuestionList) {
					if (comprehensionTestQuestion.getId().equals(questionId)) {
						isValue = true;
					}
					if (isValue && !comprehensionTestQuestion.getId().equals(questionId)) {
						comprehensionTestQuestion.setSequenceNo(comprehensionTestQuestion.getSequenceNo() - 1);
						session.update(comprehensionTestQuestion);
					}
				}

			}
			comprehensionTestQuestionBo = (ComprehensionTestQuestionBo) session.get(ComprehensionTestQuestionBo.class,
					questionId);
			if (comprehensionTestQuestionBo != null) {
				comprehensionTestQuestionBo.setActive(false);
				comprehensionTestQuestionBo.setModifiedBy(sessionObject.getUserId());
				comprehensionTestQuestionBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
				session.saveOrUpdate(comprehensionTestQuestionBo);
				message = FdahpStudyDesignerConstants.SUCCESS;
				if (comprehensionTestQuestionBo.getStudyId() != null) {
					studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, comprehensionTestQuestionBo.getStudyId())
							.uniqueResult();
					if (studySequence != null) {
						if (studySequence.isComprehensionTest()) {
							studySequence.setComprehensionTest(false);
						}
						session.saveOrUpdate(studySequence);
					}
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteComprehensionTestQuestion() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteComprehensionTestQuestion() - Ends");
		return message;
	}

	/**
	 * Describes the delete of an consent item from the list of consent items in the
	 * study consent section
	 * 
	 * @author BTC
	 * @param Integer , consentInfoId {@link ConsentInfoBo}
	 * @param Object  , sessionObject {@link SessionObject}
	 * @param String  , customStudyId {@link StudyBo}
	 * @return String :SUCCESS or FAILURE
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteConsentInfo(Integer consentInfoId, Integer studyId, SessionObject sessionObject,
			String customStudyId) {
		logger.info("StudyDAOImpl - deleteConsentInfo() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		int count = 0;
		ConsentInfoBo consentInfo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			List<ConsentInfoBo> consentInfoList = null;
			String searchQuery = "From ConsentInfoBo CIB where CIB.studyId=" + studyId
					+ " and CIB.active=1 order by CIB.sequenceNo asc";
			// String updateQuery = ""
			consentInfoList = session.createQuery(searchQuery).list();
			if (consentInfoList != null && !consentInfoList.isEmpty()) {
				boolean isValue = false;
				for (ConsentInfoBo consentInfoBo : consentInfoList) {
					if (consentInfoBo.getId().equals(consentInfoId)) {
						isValue = true;
					}
					if (isValue && !consentInfoBo.getId().equals(consentInfoId)) {
						consentInfoBo.setSequenceNo(consentInfoBo.getSequenceNo() - 1);
						consentInfoBo.setModifiedBy(sessionObject.getUserId());
						consentInfoBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
						session.update(consentInfoBo);
					}
				}
				StudySequenceBo studySequence = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId).uniqueResult();
				if (studySequence != null) {
					if (consentInfoList.size() == 1) {
						studySequence.setConsentEduInfo(false);
					}
					if (studySequence.iseConsent()) {
						studySequence.seteConsent(false);
					}
					session.saveOrUpdate(studySequence);
				}
			}
			String deleteQuery = "Update ConsentInfoBo CIB set CIB.active=0,CIB.modifiedBy=" + sessionObject.getUserId()
					+ ",CIB.modifiedOn='" + FdahpStudyDesignerUtil.getCurrentDateTime() + "' where CIB.id="
					+ consentInfoId;
			query = session.createQuery(deleteQuery);
			count = query.executeUpdate();
			if (count > 0) {
				message = FdahpStudyDesignerConstants.SUCCESS;
			}
			if (consentInfoId != null) {
				consentInfo = getConsentInfoById(consentInfoId);
				if (consentInfo != null) {
					auditLogDAO
							.saveToAuditLog(session, transaction, sessionObject, "ConsentInfo Deleted.",
									"Consent Section deleted. (Display name = " + consentInfo.getDisplayTitle()
											+ ", Study ID = " + customStudyId + ") ",
									"StudyDAOImpl - deleteConsentInfo");
				}
			}

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteConsentInfo() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteConsentInfo() - Ends");
		return message;
	}

	/**
	 * Delete eligibility test question answer by id
	 *
	 * @author BTC
	 *
	 * @param eligibilityTestId , Id of {@link EligibilityTestBo}
	 * @param studyId           , Id of {@link StudyBo}
	 * @param sessionObject     , Object of {@link SessionObject}
	 * @param customStudyId     , custom study id of {@link StudyBo}
	 * @return {@link FdahpStudyDesignerConstants.SUCCESS} or
	 *         {@link FdahpStudyDesignerConstants.FAILURE} , reorder status
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteEligibilityTestQusAnsById(Integer eligibilityTestId, Integer studyId,
			SessionObject sessionObject, String customStudyId) {
		logger.info("StudyDAOImpl - deleteEligibilityTestQusAnsById - Starts");
		Session session = null;
		Integer eligibilityDeleteResult = 0;
		Transaction trans = null;
		String result = FdahpStudyDesignerConstants.FAILURE;
		String reorderQuery;
		EligibilityTestBo eligibilityTestBo;
		List<EligibilityTestBo> eligibilityTestBos;
		StringBuilder sb = null;
		StudyBo studyBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			trans = session.beginTransaction();
			eligibilityTestBo = (EligibilityTestBo) session.getNamedQuery("EligibilityTestBo.findById")
					.setInteger("eligibilityTestId", eligibilityTestId).uniqueResult();
			studyBo = this.getStudyById(String.valueOf(studyId), sessionObject.getUserId());

			if (null != studyBo && studyBo.getStatus().contains(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH)) {
				session.delete(eligibilityTestBo);
				eligibilityDeleteResult = 1;
			} else {
				eligibilityDeleteResult = session.getNamedQuery("EligibilityTestBo.deleteById")
						.setInteger("eligibilityTestId", eligibilityTestId).executeUpdate();
			}
			sb = new StringBuilder();
			sb.append("select id FROM EligibilityTestBo  WHERE sequenceNo > '")
					.append(eligibilityTestBo.getSequenceNo()).append("' AND active = true AND " + "eligibilityId = '")
					.append(eligibilityTestBo.getEligibilityId()).append("'");
			eligibilityTestBos = session.createQuery(sb.toString()).list();
			if (eligibilityDeleteResult > 0 && !eligibilityTestBos.isEmpty()) {
				reorderQuery = "update EligibilityTestBo  set sequenceNo=sequenceNo-1 where id in " + "("
						+ StringUtils.join(eligibilityTestBos, ",") + ")";
				eligibilityDeleteResult = session.createQuery(reorderQuery).executeUpdate();
			}
			if (eligibilityDeleteResult > 0) {
				result = FdahpStudyDesignerConstants.SUCCESS;
			}
			if (eligibilityTestId > 0) {
				StudySequenceBo studySequence = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId).uniqueResult();
				if (studySequence != null && studySequence.isEligibility()) {
					studySequence.setEligibility(false);
					session.update(studySequence);
				}
			}
			auditLogDAO.saveToAuditLog(session, transaction, sessionObject, "EligibilityQus",
					customStudyId + " -- EligibilityQus deleted", "StudyDAOImpl - deleteEligibilityTestQusAnsById");
			trans.commit();
		} catch (Exception e) {
			if (null != trans)
				trans.rollback();
			logger.error("StudyDAOImpl - deleteEligibilityTestQusAnsById - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteEligibilityTestQusAnsById - Ends");
		return result;
	}

	/**
	 * delete live study by customStudyId
	 * 
	 * @author BTC
	 * @param String , customStudyId
	 * @return boolean
	 */
	@Override
	public boolean deleteLiveStudy(String customStudyId) {
		logger.info("StudyDAOImpl - deleteLiveStudy() - Starts");
		boolean flag = false;
		Session session = null;
		StudyBo liveStudyBo = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		String subQuery = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();

			liveStudyBo = (StudyBo) session.getNamedQuery("getStudyLiveVersion")
					.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId).uniqueResult();
			if (liveStudyBo != null) {
				// deleting the live study
				message = deleteStudyByIdOrCustomstudyId(session, transaction, liveStudyBo.getId().toString(), "");

				// once live study deleted successfully, reseting the new study
				if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
					session.createSQLQuery("DELETE FROM study_version WHERE custom_study_id='" + customStudyId + "'")
							.executeUpdate();
					session.createSQLQuery(
							"DELETE FROM study_activity_version WHERE custom_study_id='" + customStudyId + "'")
							.executeUpdate();
					subQuery = "(SELECT id FROM studies WHERE custom_study_id='" + customStudyId + "')";
					session.createSQLQuery("UPDATE active_task set is_live=0 WHERE study_id in" + subQuery)
							.executeUpdate();
					session.createSQLQuery("UPDATE questionnaires set is_live=0 WHERE study_id in" + subQuery)
							.executeUpdate();
					session.createSQLQuery("UPDATE consent set is_live=0 WHERE study_id in" + subQuery).executeUpdate();
					session.createSQLQuery("UPDATE consent_info set is_live=0 WHERE study_id in" + subQuery)
							.executeUpdate();
					session.createSQLQuery(
							"UPDATE studies set is_live=0 WHERE custom_study_id ='" + customStudyId + "'")
							.executeUpdate();
					flag = true;
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteLiveStudy() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteLiveStudy() - Ends");
		return flag;
	}

	/**
	 * delete the Study Overview Page By Page Id
	 * 
	 * @author BTC
	 * @param String , studyId
	 * @param String , pageId
	 * @return {@link String}
	 */
	@Override
	public String deleteOverviewStudyPageById(String studyId, String pageId) {
		logger.info("StudyDAOImpl - deleteOverviewStudyPageById() - Starts");
		Session session = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		int count = 0;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(pageId)) {
				query = session
						.createQuery("delete from StudyPageBo where studyId=" + studyId + " and pageId=" + pageId);
				count = query.executeUpdate();
				if (count > 0)
					message = FdahpStudyDesignerConstants.SUCCESS;

			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteOverviewStudyPageById() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteOverviewStudyPageById() - Ends");
		return message;
	}

	/**
	 * This method is used to delete the resource
	 * 
	 * @author BTC
	 * @param resourceInfoId
	 * @param resourceVisibility
	 * @param studyId
	 * @return message, Success/Failure message
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteResourceInfo(Integer resourceInfoId, boolean resourceVisibility, int studyId) {
		logger.info("StudyDAOImpl - deleteResourceInfo() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		int resourceCount = 0;
		Query resourceQuery = null;
		Query notificationQuery = null;
		List<ResourceBO> resourceBOList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			String searchQuery = "From ResourceBO RBO where RBO.studyId=" + studyId
					+ " and RBO.status=1 order by RBO.sequenceNo asc";
			resourceBOList = session.createQuery(searchQuery).list();
			if (resourceBOList != null && !resourceBOList.isEmpty()) {
				boolean isValue = false;
				for (ResourceBO resourceBO : resourceBOList) {
					if (resourceBO.getId().equals(resourceInfoId)) {
						isValue = true;
					}
					if (isValue && !resourceBO.getId().equals(resourceInfoId)) {
						resourceBO.setSequenceNo(resourceBO.getSequenceNo() - 1);
						session.update(resourceBO);
					}
				}
			}

			String deleteQuery = " UPDATE ResourceBO RBO SET status = " + false + " WHERE id = " + resourceInfoId;
			resourceQuery = session.createQuery(deleteQuery);
			resourceCount = resourceQuery.executeUpdate();

			if (!resourceVisibility && resourceCount > 0) {
				String deleteNotificationQuery = " UPDATE NotificationBO NBO set NBO.notificationStatus = 1 WHERE NBO.resourceId = "
						+ resourceInfoId;
				notificationQuery = session.createQuery(deleteNotificationQuery);
				notificationQuery.executeUpdate();
			}
			transaction.commit();
			message = FdahpStudyDesignerConstants.SUCCESS;
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteResourceInfo() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteResourceInfo() - Ends");
		return message;
	}

	/**
	 * This method is used to delete Study
	 * 
	 * @author BTC
	 * @param String , customStudyId
	 * @return boolean,{true/false}
	 */
	public boolean deleteStudyByCustomStudyId(String customStudyId) {
		logger.info("StudyDAOImpl - deleteStudyByCustomStudyId() - Starts");
		Session session = null;
		boolean falg = false;
		String message = FdahpStudyDesignerConstants.FAILURE;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			message = deleteStudyByIdOrCustomstudyId(session, transaction, "", customStudyId);
			if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
				falg = true;
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteStudyByCustomStudyId() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteStudyByCustomStudyId() - Ends");
		return falg;
	}

	/**
	 * This method is used to delete Study
	 * 
	 * @author BTC
	 * @param String , customStudyId
	 * @return boolean,{true/false}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteStudyByIdOrCustomstudyId(Session session, Transaction transaction, String studyId,
			String customStudyId) {
		logger.info("StudyDAOImpl - deleteStudyByIdOrCustomstudyId() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		List<StudyBo> studyBOList = null;
		String subQuery = "";
		String studyCustomQuery = "";
		List<Integer> idList = null;
		try {
			if (StringUtils.isNotEmpty(customStudyId)) {
				studyCustomQuery = " FROM StudyBo SBO WHERE SBO.customStudyId ='" + customStudyId + "'";
				subQuery = "(SELECT id FROM studies WHERE custom_study_id='" + customStudyId + "')";
			} else {
				studyCustomQuery = " FROM StudyBo SBO WHERE SBO.id in(" + studyId + ")";
				subQuery = "(" + studyId + ")";
			}
			query = session.createQuery(studyCustomQuery);
			studyBOList = query.list();
			if (studyBOList != null && !studyBOList.isEmpty()) {

				queryString = "SELECT page_id FROM study_page WHERE page_id is not null and study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM study_page WHERE page_id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "select id from eligibility_test e where e.eligibility_id in(select id from eligibility where study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM eligibility_test WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM eligibility WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM eligibility WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM consent WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM consent WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "select r.id from comprehension_test_response r where r.comprehension_test_question_id in(select id from comprehension_test_question c where c.study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM comprehension_test_response WHERE id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM comprehension_test_question WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM comprehension_test_question WHERE id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM consent_info WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM consent_info WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT active_task_id FROM active_task_attrtibutes_values WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM active_task_attrtibutes_values WHERE active_task_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT active_task_id FROM active_task_frequencies WHERE active_task_id IN (SELECT id FROM active_task WHERE study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM active_task_frequencies WHERE active_task_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT active_task_id FROM active_task_custom_frequencies WHERE active_task_id IN(SELECT id FROM active_task WHERE study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM active_task_custom_frequencies WHERE active_task_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM active_task WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM active_task WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				/** Questionnarie Part Start **/
				// Form Step Start .....

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM questions WHERE id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in"
						+ subQuery + ")))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM questions WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT response_sub_type_value_id FROM response_sub_type_value WHERE response_type_id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + ")))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM response_sub_type_value WHERE response_sub_type_value_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT response_type_id FROM response_type_value WHERE questions_response_type_id IN(SELECT question_id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + ")))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM response_type_value WHERE response_type_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				// form_mapping deletion
				idList = null;
				queryString = "";
				queryString = "SELECT id FROM form_mapping WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in"
						+ subQuery + "))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM form_mapping WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				// form deletion
				queryString = "SELECT form_id FROM form WHERE form_id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Form' AND questionnaires_id IN (SELECT id FROM questionnaires q WHERE study_id in"
						+ subQuery + "))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM form WHERE form_id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				// Form Step End......

				// Instruction Step Start .....

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM instructions WHERE id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Instruction' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + "))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM instructions WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}
				// Instruction Step End......

				// Question Step End......

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM questions WHERE id IN (SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + "))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM questions WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT response_sub_type_value_id FROM response_sub_type_value WHERE response_type_id IN(SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + "))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM response_sub_type_value WHERE response_sub_type_value_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT response_type_id FROM response_type_value WHERE questions_response_type_id IN(SELECT instruction_form_id FROM questionnaires_steps WHERE step_type='Question' AND questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + "))";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM response_type_value WHERE response_type_id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				// Question Step End......
				idList = null;
				queryString = "";
				queryString = "SELECT step_id FROM questionnaires_steps WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM questionnaires_steps WHERE step_id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM questionnaires_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM questionnaires_frequencies WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM questionnaires_custom_frequencies WHERE questionnaires_id IN (SELECT id FROM questionnaires WHERE study_id in"
						+ subQuery + ")";
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM questionnaires_custom_frequencies WHERE id in("
							+ StringUtils.join(idList, ",") + ")").executeUpdate();
				}

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM questionnaires WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery(
							"DELETE FROM questionnaires WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				/** Questionnarie Part End *****/

				idList = null;
				queryString = "";
				queryString = "SELECT id FROM resources WHERE study_id in" + subQuery;
				idList = session.createSQLQuery(queryString).list();
				if (idList != null && !idList.isEmpty()) {
					session.createSQLQuery("DELETE FROM resources WHERE id in(" + StringUtils.join(idList, ",") + ")")
							.executeUpdate();
				}

				session.createSQLQuery(
						"DELETE FROM notification_history WHERE notification_id in(SELECT notification_id FROM notification WHERE study_id in "
								+ subQuery + ")")
						.executeUpdate();

				session.createSQLQuery("DELETE FROM notification WHERE study_id in" + subQuery).executeUpdate();

				session.createSQLQuery("DELETE FROM study_checklist WHERE study_id in" + subQuery).executeUpdate();

				session.createSQLQuery("DELETE FROM study_permission WHERE study_id in" + subQuery).executeUpdate();

				session.createSQLQuery("DELETE FROM study_sequence WHERE study_id in" + subQuery).executeUpdate();
				if (StringUtils.isNotEmpty(customStudyId)) {
					session.createSQLQuery("DELETE FROM study_version WHERE custom_study_id='" + customStudyId + "'")
							.executeUpdate();
					session.createSQLQuery("DELETE FROM studies WHERE custom_study_id='" + customStudyId + "'")
							.executeUpdate();

					session.createSQLQuery("DELETE FROM anchordate_type WHERE custom_study_id='" + customStudyId + "'")
							.executeUpdate();
				} else {
					session.createSQLQuery("DELETE FROM studies WHERE id in(" + studyId + ")").executeUpdate();
				}
				message = FdahpStudyDesignerConstants.SUCCESS;
			}
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteStudyByIdOrCustomstudyId() - ERROR ", e);
		}
		logger.info("StudyDAOImpl - deleteStudyByIdOrCustomstudyId() - Ends");
		return message;
	}

	/**
	 * The last order count of questions of a {@link EligibilityBo}
	 *
	 * @author BTC
	 * @param eligibilityId
	 * @return count , the last order count of the {@link EligibilityTestBo}
	 */
	@Override
	public int eligibilityTestOrderCount(Integer eligibilityId) {
		logger.info("StudyDAOImpl - eligibilityTestOrderCount - Starts");
		Session session = null;
		int count = 1;
		EligibilityTestBo eligibilityTestBo = null;
		StringBuilder sb = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			sb = new StringBuilder();
			sb.append("From EligibilityTestBo ETB where ETB.eligibilityId=").append(eligibilityId)
					.append(" and ETB.active=1 order by ETB.sequenceNo DESC");
			query = session.createQuery(sb.toString()).setMaxResults(1);
			eligibilityTestBo = ((EligibilityTestBo) query.uniqueResult());
			if (eligibilityTestBo != null) {
				count = eligibilityTestBo.getSequenceNo() + 1;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - eligibilityTestOrderCount - Error", e);
		}
		logger.info("StudyDAOImpl - eligibilityTestOrderCount - Ends");
		return count;
	}

	/**
	 * This method is used to get the active user list whom are not yet added to the
	 * particular study
	 * 
	 * @author BTC
	 * @param studyId
	 * @param userId
	 * @return List of {@link UserBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserBO> getActiveNonAddedUserList(Integer studyId, Integer userId) {
		logger.info("StudyDAOImpl - getActiveNonAddedUserList() - Starts");
		Session session = null;
		List<UserBO> userList = null;
		List<Object[]> objList = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createSQLQuery(" SELECT u.user_id,u.first_name,u.last_name,u.email,r.role_name "
					+ "FROM users u,roles r WHERE r.role_id = u.role_id AND u.status = 1 "
					+ "AND u.user_id NOT IN (SELECT upm.user_id FROM user_permission_mapping upm "
					+ "WHERE upm.permission_id = (SELECT up.permission_id FROM user_permissions up WHERE up.permissions ='ROLE_SUPERADMIN')) "
			/*
			 * + "AND u.user_id IN (SELECT upm.user_id FROM user_permission_mapping upm " +
			 * "WHERE upm.permission_id = (SELECT up.permission_id FROM user_permissions up WHERE up.permissions ='ROLE_MANAGE_STUDIES')) "
			 */
			/* + "AND u.user_id <> "+userId */
			);
			objList = query.list();
			if (null != objList && !objList.isEmpty()) {
				userList = new ArrayList<>();
				for (Object[] obj : objList) {
					UserBO userBO = new UserBO();
					userBO.setUserId(null != obj[0] ? (Integer) obj[0] : 0);
					userBO.setFirstName(null != obj[1] ? String.valueOf(obj[1]) : "");
					userBO.setLastName(null != obj[2] ? String.valueOf(obj[2]) : "");
					userBO.setUserEmail(null != obj[3] ? String.valueOf(obj[3]) : "");
					userBO.setRoleName(null != obj[4] ? String.valueOf(obj[4]) : "");
					userBO.setUserFullName(userBO.getFirstName() + " " + userBO.getLastName());
					userList.add(userBO);
				}
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getActiveNonAddedUserList() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getActiveNonAddedUserList() - Ends");
		return userList;
	}

	/**
	 * This method is used to get the users whom are already added to the particular
	 * study
	 * 
	 * @author BTC
	 * @param studyId
	 * @param userId
	 * @return List of {@link StudyPermissionBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StudyPermissionBO> getAddedUserListToStudy(Integer studyId, Integer userId) {
		logger.info("StudyDAOImpl - getAddedUserListToStudy() - Starts");
		Session session = null;
		List<Object[]> objList = null;
		Query query = null;
		List<StudyPermissionBO> studyPermissionList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createSQLQuery(" SELECT sp.user_id,u.first_name,u.last_name,sp.view_permission,sp.project_lead "
							+ "FROM users u,study_permission sp "
							+ "WHERE u.user_id = sp.user_id AND u.status = 1 AND sp.study_id = " + studyId
							+ " AND u.user_id NOT IN (SELECT upm.user_id FROM user_permission_mapping upm "
							+ "WHERE upm.permission_id = (SELECT up.permission_id FROM user_permissions up WHERE up.permissions ='ROLE_SUPERADMIN')) "
					/* + "AND u.user_id <> "+userId */
					);
			objList = query.list();
			if (null != objList && !objList.isEmpty()) {
				studyPermissionList = new ArrayList<>();
				for (Object[] obj : objList) {
					StudyPermissionBO studyPermissionBO = new StudyPermissionBO();
					studyPermissionBO.setUserId(null != obj[0] ? (Integer) obj[0] : 0);
					studyPermissionBO.setUserFullName((null != obj[1] ? String.valueOf(obj[1]) : "") + " "
							+ (null != obj[2] ? String.valueOf(obj[2]) : ""));
					studyPermissionBO.setViewPermission((boolean) obj[3] ? true : false);
					studyPermissionBO.setProjectLead(null != obj[4] ? Integer.parseInt(obj[4].toString()) : 0);
					studyPermissionList.add(studyPermissionBO);
				}
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getAddedUserListToStudy() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getAddedUserListToStudy() - Ends");
		return studyPermissionList;
	}

	/**
	 * return all active study List based
	 *
	 * @author BTC
	 *
	 * @return the Study list
	 * @exception Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StudyBo> getAllStudyList() {
		logger.info("StudyDAOImpl - getAllStudyList() - Starts");
		Session session = null;
		List<StudyBo> studyBOList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery(" FROM StudyBo SBO WHERE SBO.version = 0 AND SBO.status <> '"
					+ FdahpStudyDesignerConstants.STUDY_DEACTIVATED + "'");
			studyBOList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getAllStudyList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getAllStudyList() - Ends");
		return studyBOList;
	}

	/**
	 * This method is used to get the checklist info
	 * 
	 * @author BTC
	 * @param studyId
	 * @return {@link Checklist}
	 */
	@Override
	public Checklist getchecklistInfo(Integer studyId) {
		logger.info("StudyDAOImpl - getchecklistInfo() - Starts");
		Checklist checklist = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.getNamedQuery("getchecklistInfo").setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId);
			checklist = (Checklist) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getchecklistInfo() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getchecklistInfo() - Ends");
		return checklist;
	}

	/**
	 * Study Consent have the 0 or more comprehension test question.Each question
	 * contains the question text and answers and make correct response to the
	 * question as being ANY or ALL of the 'correct' answer options.
	 * 
	 * @author BTC
	 * @param Integer , questionId {@link ComprehensionTestQuestionBo}
	 * @return {@link ComprehensionTestQuestionBo}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ComprehensionTestQuestionBo getComprehensionTestQuestionById(Integer questionId) {
		logger.info("StudyDAOImpl - getComprehensionTestQuestionById() - Starts");
		ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
		Session session = null;
		List<ComprehensionTestResponseBo> comprehensionTestResponsList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			comprehensionTestQuestionBo = (ComprehensionTestQuestionBo) session.get(ComprehensionTestQuestionBo.class,
					questionId);
			if (null != comprehensionTestQuestionBo) {
				String searchQuery = "From ComprehensionTestResponseBo CRBO where CRBO.comprehensionTestQuestionId="
						+ comprehensionTestQuestionBo.getId();
				query = session.createQuery(searchQuery);
				comprehensionTestResponsList = query.list();
				comprehensionTestQuestionBo.setResponseList(comprehensionTestResponsList);
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getComprehensionTestQuestionById() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getComprehensionTestQuestionById() - Ends");
		return comprehensionTestQuestionBo;
	}

	/**
	 * Study consent have the 0 or more comprehension test question to check the
	 * understanding of the consent to a participant here will show the list
	 * comprehension test question of consent of a study
	 * 
	 * @author BTC
	 * @param Integer , studyId {@link StudyBo}
	 * @return List : {@link ComprehensionTestQuestionBo}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ComprehensionTestQuestionBo> getComprehensionTestQuestionList(Integer studyId) {
		logger.info("StudyDAOImpl - getComprehensionTestQuestionList() - Starts");
		Session session = null;
		List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId=" + studyId
					+ " and CTQBO.active=1 order by CTQBO.sequenceNo asc");
			comprehensionTestQuestionList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getComprehensionTestQuestionList() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getComprehensionTestQuestionList() - Ends");
		return comprehensionTestQuestionList;
	}

	/**
	 * Describes the get the comprehension test question response
	 * 
	 * @author BTC
	 * @param Integer , comprehensionQuestionId {@link ComprehensionTestQuestionBo}
	 * @return List : {@link ComprehensionTestResponseBo}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(Integer comprehensionQuestionId) {
		logger.info("StudyDAOImpl - deleteComprehensionTestQuestion() - Starts");
		Session session = null;
		List<ComprehensionTestResponseBo> comprehensionTestResponseList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createQuery("From ComprehensionTestResponseBo CTRBO where CTRBO.comprehensionTestQuestionId="
							+ comprehensionQuestionId);
			comprehensionTestResponseList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - deleteComprehensionTestQuestion() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteComprehensionTestQuestion() - Ends");
		return comprehensionTestResponseList;
	}

	/**
	 * Describes the get consent of an study which contains the shara data
	 * permission and e-consent from details
	 * 
	 * @author BTC
	 * @param String : studyId in {@link StudyBo}
	 * @return {@link ConsentBo}
	 */
	@Override
	public ConsentBo getConsentDetailsByStudyId(String studyId) {
		logger.info("INFO: StudyDAOImpl - getConsentDetailsByStudyId() :: Starts");
		ConsentBo consentBo = null;
		Session session = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("from ConsentBo CBO where CBO.studyId=" + studyId + "");
			consentBo = (ConsentBo) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("INFO: StudyDAOImpl - getConsentDetailsByStudyId() :: Ends");
		return consentBo;
	}

	/**
	 * Describes the get the consent of item from the list consent items of an study
	 * in the consent section
	 * 
	 * @author BTC
	 * @param Integer , consentInfoId {@link ConsentInfoBo}
	 * @return {@link ConsentInfoBo}
	 */
	@Override
	public ConsentInfoBo getConsentInfoById(Integer consentInfoId) {
		logger.info("StudyDAOImpl - getConsentInfoById() - Starts");
		ConsentInfoBo consentInfoBo = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			consentInfoBo = (ConsentInfoBo) session.get(ConsentInfoBo.class, consentInfoId);
			if (consentInfoBo != null) {
				consentInfoBo.setDisplayTitle(StringUtils.isEmpty(consentInfoBo.getDisplayTitle()) ? ""
						: consentInfoBo.getDisplayTitle().replaceAll("&#34;", "\"").replaceAll("&#39;", "\'"));
				consentInfoBo.setBriefSummary(StringUtils.isEmpty(consentInfoBo.getBriefSummary()) ? ""
						: consentInfoBo.getBriefSummary().replaceAll("&#34;", "\"").replaceAll("&#39;", "\'"));
				consentInfoBo.setElaborated(StringUtils.isEmpty(consentInfoBo.getElaborated()) ? ""
						: consentInfoBo.getElaborated().replaceAll("&#34;", "\"").replaceAll("&#39;", "\'"));
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getConsentInfoById() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getConsentInfoById() - Ends");
		return consentInfoBo;
	}

	/**
	 * Describes to get the list of consent item which are added in the study
	 * consent section
	 * 
	 * @author BTC
	 * @param studyId {@link StudyBo}
	 * @return List {@link ConsentInfoBo}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ConsentInfoBo> getConsentInfoDetailsListByStudyId(String studyId) {
		logger.info("INFO: StudyDAOImpl - getConsentInfoDetailsListByStudyId() :: Starts");
		Session session = null;
		Query query = null;
		List<ConsentInfoBo> consentInfoBoList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery(" from ConsentInfoBo CIBO where CIBO.studyId=" + studyId
					+ " and CIBO.active=1 ORDER BY CIBO.sequenceNo ");
			consentInfoBoList = query.list();
			if (null != consentInfoBoList && consentInfoBoList.size() > 0) {
				for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
					consentInfoBo.setDisplayTitle(
							consentInfoBo.getDisplayTitle().replaceAll("<", "&#60;").replaceAll(">", "&#62;")
									.replaceAll("/", "&#47;").replaceAll("'", "&#39;").replaceAll("\"", "&#34;"));
					consentInfoBo.setElaborated(consentInfoBo.getElaborated().replaceAll("&#39;", "'")
							.replaceAll("&#34;", "&quot;").replaceAll("em>", "i>")
							.replaceAll("<a", "<a target='_blank' style='text-decoration:underline;color:blue;'"));
				}
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getConsentInfoDetailsListByStudyId() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("INFO: StudyDAOImpl - getConsentInfoDetailsListByStudyId() :: Ends");
		return consentInfoBoList;
	}

	/**
	 * Describes to get the list of consent item which are added in the study
	 * consent section
	 * 
	 * @author BTC
	 * @param Integer , studyId {@link StudyBo}
	 * @return List {@link ConsentInfoBo}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ConsentInfoBo> getConsentInfoList(Integer studyId) {
		logger.info("StudyDAOImpl - getConsentInfoList() - Starts");
		List<ConsentInfoBo> consentInfoList = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String searchQuery = "From ConsentInfoBo CIB where CIB.studyId=" + studyId
					+ " and CIB.active=1 order by CIB.sequenceNo asc";
			query = session.createQuery(searchQuery);
			consentInfoList = query.list();
			System.out.println("consentInfoList:" + consentInfoList.size());
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getConsentInfoList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getConsentInfoList() - Ends");
		return consentInfoList;
	}

	/**
	 * ResearchKit already provides a few Consent Items screens by default.Here
	 * describes the getting the list of research kit consents
	 * 
	 * @author BTC
	 * @return List : {@link ConsentMasterInfoBo}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ConsentMasterInfoBo> getConsentMasterInfoList() {
		logger.info("StudyDAOImpl - getConsentMasterInfoList() - Starts");
		Session session = null;
		List<ConsentMasterInfoBo> consentMasterInfoList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("From ConsentMasterInfoBo CMIB");
			consentMasterInfoList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getConsentMasterInfoList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getConsentMasterInfoList() - Ends");
		return consentMasterInfoList;
	}

	/**
	 * validate all study section completed or not before do Publish (as Upcoming
	 * Study)/Launch/publish update of the study
	 * 
	 * @author BTC
	 * @param String , studyId
	 * @param String , buttonText
	 * @return {@link String}
	 */
	public String getErrorBasedonAction(StudySequenceBo studySequenceBo) {
		String message = FdahpStudyDesignerConstants.SUCCESS;
		if (studySequenceBo != null) {
			if (!studySequenceBo.isBasicInfo()) {
				message = FdahpStudyDesignerConstants.BASICINFO_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isSettingAdmins()) {
				message = FdahpStudyDesignerConstants.SETTING_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isOverView()) {
				message = FdahpStudyDesignerConstants.OVERVIEW_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isEligibility()) {
				message = FdahpStudyDesignerConstants.ELIGIBILITY_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isConsentEduInfo()) {
				message = FdahpStudyDesignerConstants.CONSENTEDUINFO_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isComprehensionTest()) {
				message = FdahpStudyDesignerConstants.COMPREHENSIONTEST_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.iseConsent()) {
				message = FdahpStudyDesignerConstants.ECONSENT_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isStudyExcQuestionnaries()) {
				message = FdahpStudyDesignerConstants.STUDYEXCQUESTIONNARIES_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isStudyExcActiveTask()) {
				message = FdahpStudyDesignerConstants.STUDYEXCACTIVETASK_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isMiscellaneousResources()) {
				message = FdahpStudyDesignerConstants.RESOURCES_ERROR_MSG;
				return message;
			} else if (!studySequenceBo.isCheckList()) {
				message = FdahpStudyDesignerConstants.CHECKLIST_ERROR_MSG;
				return message;
			}
		}
		return message;
	}

	/*------------------------------------Added By Vivek Start---------------------------------------------------*/

	/**
	 * return Study version on customStudyid
	 * 
	 * @author BTC
	 * @param String , customStudyId
	 * @return {@link StudyIdBean}
	 */
	@Override
	public StudyIdBean getLiveVersion(String customStudyId) {
		logger.info("StudyDAOImpl - getLiveVersion() - Starts");
		Session session = null;
		StudyVersionBo studyVersionBo = null;
		Integer consentStudyId = null;
		StudyIdBean studyIdBean = new StudyIdBean();
		Integer activetaskStudyId = null;
		Integer questionnarieStudyId = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(customStudyId)) {
				query = session.getNamedQuery("getStudyByCustomStudyId").setString("customStudyId", customStudyId);
				query.setMaxResults(1);
				studyVersionBo = (StudyVersionBo) query.uniqueResult();
				if (studyVersionBo != null) {
					queryString = "SELECT s.study_id FROM active_task s where s.custom_study_id='" + customStudyId
							+ "' and is_live =1";
					activetaskStudyId = (Integer) session.createSQLQuery(queryString).setMaxResults(1).uniqueResult();

					queryString = "SELECT s.study_id FROM questionnaires s where s.custom_study_id='" + customStudyId
							+ "' and is_live =1";
					questionnarieStudyId = (Integer) session.createSQLQuery(queryString).setMaxResults(1)
							.uniqueResult();

					queryString = "SELECT s.study_id FROM consent s where s.custom_study_id='" + customStudyId
							+ "' and round(s.version, 1) =" + studyVersionBo.getConsentVersion();
					consentStudyId = (Integer) session.createSQLQuery(queryString).setMaxResults(1).uniqueResult();

					if (consentStudyId == null) {
						queryString = "SELECT s.study_id FROM consent_info s where s.custom_study_id='" + customStudyId
								+ "' and round(s.version, 1) =" + studyVersionBo.getConsentVersion();
						consentStudyId = (Integer) session.createSQLQuery(queryString).setMaxResults(1).uniqueResult();
					}

					studyIdBean.setActivetaskStudyId(activetaskStudyId);
					studyIdBean.setQuestionnarieStudyId(questionnarieStudyId);
					studyIdBean.setConsentStudyId(consentStudyId);
				}
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getLiveVersion() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getLiveVersion() - Ends");
		return studyIdBean;
	}

	/**
	 * This method is used to get the notification data
	 * 
	 * @author BTC
	 * @param resourseId
	 * @return {@link NotificationBO}
	 */
	@Override
	public NotificationBO getNotificationByResourceId(Integer resourseId) {
		logger.info("StudyDAOImpl - getNotificationByResourceId() - Starts");
		Session session = null;
		String queryString = null;
		NotificationBO notificationBO = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			queryString = " FROM NotificationBO NBO WHERE NBO.resourceId = " + resourseId;
			query = session.createQuery(queryString);
			notificationBO = (NotificationBO) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getNotificationByResourceId - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getNotificationByResourceId - Ends");
		return notificationBO;
	}

	/**
	 * This method return content(A title,Description,Image) of Overview pages of
	 * the Study
	 * 
	 * @author BTC
	 * @param String , studyId in {@link StudyBo}
	 * @return {@link List<StudyPageBo>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StudyPageBo> getOverviewStudyPagesById(String studyId, Integer userId) {
		logger.info("StudyDAOImpl - getOverviewStudyPagesById() - Starts");
		Session session = null;
		List<StudyPageBo> studyPageBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(studyId)) {
				query = session.createQuery("from StudyPageBo where studyId=" + studyId);
				studyPageBo = query.list();
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getOverviewStudyPagesById() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getOverviewStudyPagesById() - Ends");
		return studyPageBo;
	}

	/**
	 * return reference List based on category
	 * 
	 * @author BTC
	 * @return {@link HashMap<String, List<ReferenceTablesBo>>}
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public HashMap<String, List<ReferenceTablesBo>> getreferenceListByCategory() {
		logger.info("StudyDAOImpl - getreferenceListByCategory() - Starts");
		Session session = null;
		List<ReferenceTablesBo> allReferenceList = null;
		List<ReferenceTablesBo> categoryList = new ArrayList<>();
		List<ReferenceTablesBo> researchSponserList = new ArrayList<>();
		List<ReferenceTablesBo> dataPartnerList = new ArrayList<>();
		HashMap<String, List<ReferenceTablesBo>> referenceMap = new HashMap<>();
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("from ReferenceTablesBo order by category asc,id asc");
			allReferenceList = query.list();
			if (allReferenceList != null && !allReferenceList.isEmpty()) {
				for (ReferenceTablesBo referenceTablesBo : allReferenceList) {
					if (StringUtils.isNotEmpty(referenceTablesBo.getCategory())) {
						switch (referenceTablesBo.getCategory()) {

						case FdahpStudyDesignerConstants.REFERENCE_TYPE_CATEGORIES:
							categoryList.add(referenceTablesBo);
							break;
						case FdahpStudyDesignerConstants.REFERENCE_TYPE_RESEARCH_SPONSORS:
							researchSponserList.add(referenceTablesBo);
							break;
						case FdahpStudyDesignerConstants.REFERENCE_TYPE_DATA_PARTNER:
							dataPartnerList.add(referenceTablesBo);
							break;

						default:
							break;
						}
					}
				}
				referenceMap = new HashMap<>();
				if (!categoryList.isEmpty())
					referenceMap.put(FdahpStudyDesignerConstants.REFERENCE_TYPE_CATEGORIES, categoryList);
				if (!researchSponserList.isEmpty())
					referenceMap.put(FdahpStudyDesignerConstants.REFERENCE_TYPE_RESEARCH_SPONSORS, researchSponserList);
				if (!dataPartnerList.isEmpty())
					referenceMap.put(FdahpStudyDesignerConstants.REFERENCE_TYPE_DATA_PARTNER, dataPartnerList);
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getreferenceListByCategory() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getreferenceListByCategory() - Ends");
		return referenceMap;
	}

	/**
	 * This method is used to get the resource information
	 * 
	 * @author BTC
	 * @param resourceInfoId
	 * @return {@link ResourceBO}
	 */
	@Override
	public ResourceBO getResourceInfo(Integer resourceInfoId) {
		logger.info("StudyDAOImpl - getResourceInfo() - Starts");
		ResourceBO resourceBO = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.getNamedQuery("getResourceInfo").setInteger("resourceInfoId", resourceInfoId);
			resourceBO = (ResourceBO) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getResourceInfo() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getResourceInfo() - Ends");
		return resourceBO;
	}

	/**
	 * This method is used to get the list of resources
	 * 
	 * @author BTC
	 * @param studyId
	 * @return List of {@link ResourceBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceBO> getResourceList(Integer studyId) {
		logger.info("StudyDAOImpl - getResourceList() - Starts");
		List<ResourceBO> resourceBOList = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			String searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId=" + studyId
					+ " AND RBO.status = 1 AND RBO.studyProtocol = false ORDER BY RBO.createdOn ASC ";
			query = session.createQuery(searchQuery);
			resourceBOList = query.list();

			if (resourceBOList != null && !resourceBOList.isEmpty()) {
				int sequenceNo = 1;
				for (ResourceBO rBO : resourceBOList) {
					if (rBO.getSequenceNo() == null || rBO.getSequenceNo().equals(0)) {
						rBO.setSequenceNo(sequenceNo);
						session.update(rBO);
						sequenceNo++;
					}
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - getResourceList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getResourceList() - Ends");
		return resourceBOList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NotificationBO> getSavedNotification(Integer studyId) {
		logger.info("StudyDAOImpl - getSavedNotification() - Starts");
		List<NotificationBO> notificationSavedList = null;
		Session session = null;
		String searchQuery = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			searchQuery = " FROM NotificationBO NBO WHERE NBO.studyId=" + studyId
					+ " AND NBO.notificationAction = 0 AND NBO.notificationType='ST' AND NBO.notificationSubType='Announcement' ";
			query = session.createQuery(searchQuery);
			notificationSavedList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getSavedNotification() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getSavedNotification() - Ends");
		return notificationSavedList;
	}

	/**
	 * get study details
	 * 
	 * @author BTC
	 * @param String  , studyId
	 * @param Integer , userId
	 * @return studyBo, {@link StudyBo}
	 */
	@Override
	public StudyBo getStudyById(String studyId, Integer userId) {
		logger.info("StudyDAOImpl - getStudyById() - Starts");
		Session session = null;
		StudyBo studyBo = null;
		StudySequenceBo studySequenceBo = null;
		StudyPermissionBO permissionBO = null;
		StudyVersionBo studyVersionBo = null;
		StudyBo liveStudyBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(studyId)) {
				studyBo = (StudyBo) session.getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
						.setInteger("id", Integer.parseInt(studyId)).uniqueResult();
				studySequenceBo = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, Integer.parseInt(studyId)).uniqueResult();
				permissionBO = (StudyPermissionBO) session.getNamedQuery("getStudyPermissionById")
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, Integer.parseInt(studyId))
						.setInteger("userId", userId).uniqueResult();
				if (studySequenceBo != null)
					studyBo.setStudySequenceBo(studySequenceBo);
				if (permissionBO != null)
					studyBo.setViewPermission(permissionBO.isViewPermission());
				if (studyBo != null) {
					// To get latest version of study, consent version ,
					// activity version
					query = session.getNamedQuery("getStudyByCustomStudyId").setString("customStudyId",
							studyBo.getCustomStudyId());
					query.setMaxResults(1);
					studyVersionBo = (StudyVersionBo) query.uniqueResult();
					if (studyVersionBo != null) {
						studyVersionBo.setStudyLVersion(" V" + studyVersionBo.getStudyVersion());
						studyVersionBo.setConsentLVersion(" (V" + studyVersionBo.getConsentVersion() + ")");
						studyVersionBo.setActivityLVersion(" (V" + studyVersionBo.getActivityVersion() + ")");
						studyBo.setStudyVersionBo(studyVersionBo);
					}
					// To get the live version of study by passing customstudyId
					liveStudyBo = (StudyBo) session
							.createQuery(
									"FROM StudyBo where customStudyId='" + studyBo.getCustomStudyId() + "' and live=1")
							.uniqueResult();
					if (liveStudyBo != null) {
						studyBo.setLiveStudyBo(liveStudyBo);
					}
				}

			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyById() - Ends");
		return studyBo;
	}

	/**
	 * return eligibility based on user's Study Id
	 *
	 * @author BTC
	 *
	 * @param studyId , studyId of the {@link StudyBo}
	 * @return {@link EligibilityBo}
	 * @exception Exception
	 */
	@Override
	public EligibilityBo getStudyEligibiltyByStudyId(String studyId) {
		logger.info("StudyDAOImpl - getStudyEligibiltyByStudyId() - Starts");
		Session session = null;
		EligibilityBo eligibilityBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(studyId)) {
				query = session.getNamedQuery("getEligibiltyByStudyId").setInteger(FdahpStudyDesignerConstants.STUDY_ID,
						Integer.parseInt(studyId));
				eligibilityBo = (EligibilityBo) query.uniqueResult();
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyEligibiltyByStudyId() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyEligibiltyByStudyId() - Ends");
		return eligibilityBo;
	}

	/************************************
	 * Added By BTC Start
	 *************************************************/
	/**
	 * return study List based on user
	 *
	 * @author BTc
	 * @param Integer , userId in {@link UserBO}
	 * @return {@link List<StudyListBean>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StudyListBean> getStudyList(Integer userId) {
		logger.info("StudyDAOImpl - getStudyList() - Starts");
		Session session = null;
		List<StudyListBean> studyListBeans = null;
		String name = "";
		List<ReferenceTablesBo> referenceTablesBos = null;
		StudyBo liveStudy = null;
		StudyBo studyBo = null;
		try {

			session = hibernateTemplate.getSessionFactory().openSession();
			if (userId != null && userId != 0) {
				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.StudyListBean(s.id,s.customStudyId,s.name,s.category,s.researchSponsor,user.firstName, user.lastName,p.viewPermission,s.status,s.createdOn)"
								+ " from StudyBo s,StudyPermissionBO p, UserBO user" + " where s.id=p.studyId"
								+ " and user.userId = s.createdBy" + " and s.version=0" + " and p.userId=:impValue"
								+ " order by s.createdOn desc");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, userId);
				studyListBeans = query.list();

				if (studyListBeans != null && !studyListBeans.isEmpty()) {
					for (StudyListBean bean : studyListBeans) {
						if (StringUtils.isNotEmpty(name))
							bean.setProjectLeadName(name);
						if (StringUtils.isNotEmpty(bean.getCategory())
								&& StringUtils.isNotEmpty(bean.getResearchSponsor())) {
							// get the Category, Research Sponsor name of the
							// study from categoryIds
							/*
							 * query = session.createQuery("from ReferenceTablesBo where id in(" +
							 * bean.getCategory() + "," + bean.getResearchSponsor() + ")");
							 */
							query = session.createQuery("from ReferenceTablesBo where id in(" + bean.getCategory() + ")");
							referenceTablesBos = query.list();
							if (referenceTablesBos != null && !referenceTablesBos.isEmpty()) {
								bean.setCategory(referenceTablesBos.get(0).getValue());
								/*
								 * bean.setResearchSponsor( referenceTablesBos.size() == 2 ?
								 * referenceTablesBos.get(1).getValue() : "");
								 */
							}
						}
						if (StringUtils.isNotEmpty(bean.getCustomStudyId())) {
							liveStudy = (StudyBo) session.createQuery(
									"from StudyBo where customStudyId='" + bean.getCustomStudyId() + "' and live=1")
									.uniqueResult();
							if (liveStudy != null) {
								bean.setLiveStudyId(liveStudy.getId());
							} else {
								bean.setLiveStudyId(null);
							}
						}
						// if is there any change in study then edit with dot
						// will come
						if (bean.getId() != null && bean.getLiveStudyId() != null) {
							studyBo = (StudyBo) session.createQuery("from StudyBo where id=" + bean.getId())
									.uniqueResult();
							if (studyBo.getHasStudyDraft() == 1)
								bean.setFlag(true);
						}
						// if is there any team lead in that study
						if (bean.getId() != null) {
							String userInfo = (String) session
									.createQuery(
											"SELECT  u.firstName from StudyPermissionBO s , UserBO u where s.studyId="
													+ bean.getId() + " and s.userId=u.userId and s.projectLead=1")
									.setMaxResults(1).uniqueResult();
							if (StringUtils.isNotEmpty(userInfo)) {
								bean.setProjectLeadName(userInfo);
							} else {
								bean.setProjectLeadName("None");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyList() - Ends");
		return studyListBeans;

	}

	/**
	 * This method is used to return study List based on user
	 *
	 * @author BTC
	 * @param Integer , userId
	 * @return {@link StudyListBean}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StudyListBean> getStudyListByUserId(Integer userId) {
		logger.info("StudyDAOImpl - getStudyListByUserId() - Starts");
		Session session = null;
		List<StudyListBean> studyListBeans = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (userId != null && userId != 0) {
				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.StudyListBean(s.id,s.customStudyId,s.name,p.viewPermission)"
								+ " from StudyBo s,StudyPermissionBO p" + " where s.id=p.studyId" + " and s.version = 0"
								+ " and p.userId=:impValue");
				query.setParameter("impValue", userId);
				studyListBeans = query.list();
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyListByUserId() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyListByUserId() - Ends");
		return studyListBeans;
	}

	/**
	 * This method is to get live Study details
	 * 
	 * @author BTC
	 * @param String , customStudyId
	 * @return {@link StudyBo}
	 */
	@Override
	public StudyBo getStudyLiveStatusByCustomId(String customStudyId) {
		logger.info("StudyDAOImpl - getStudyLiveStatusByCustomId() - Starts");
		StudyBo studyLive = null;
		Session session = null;
		String searchQuery = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			searchQuery = "FROM StudyBo SBO WHERE SBO.customStudyId = '" + customStudyId + "' AND SBO.live = 1";
			query = session.createQuery(searchQuery);
			studyLive = (StudyBo) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyLiveStatusByCustomId() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyLiveStatusByCustomId() - Ends");
		return studyLive;
	}

	/**
	 * This method is used to get the a special resource called study protocol
	 * 
	 * @author BTC
	 * @param studyId
	 * @return {@link ResourceBO}
	 */
	@Override
	public ResourceBO getStudyProtocol(Integer studyId) {
		logger.info("StudyDAOImpl - getStudyProtocol() - Starts");
		ResourceBO studyprotocol = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery(
					" FROM ResourceBO RBO WHERE RBO.studyId=" + studyId + " AND RBO.studyProtocol = true ");
			studyprotocol = (ResourceBO) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyProtocol() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyProtocol() - Ends");
		return studyprotocol;
	}

	/**
	 * This method is used to get the super admins user Ids
	 * 
	 * @author BTC
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getSuperAdminUserIds() {
		logger.info("StudyDAOImpl - getSuperAdminUserIds() - Starts");
		Session session = null;
		List<Integer> superAdminUserIds = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createSQLQuery(
					"SELECT u.user_id FROM users u WHERE u.user_id in (SELECT upm.user_id FROM user_permission_mapping upm WHERE upm.permission_id = (SELECT up.permission_id FROM user_permissions up WHERE up.permissions = 'ROLE_SUPERADMIN'))");
			superAdminUserIds = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getSuperAdminUserIds() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getSuperAdminUserIds() - Ends");
		return superAdminUserIds;
	}

	/**
	 * mark as completed of study
	 * 
	 * @author BTC
	 * @param Integer , studyId
	 * @param String  , markCompleted
	 * @param sesObj  , {@link SessionObject}
	 * @param String  , customStudyId {@link StudyBo}
	 * @return String, SUCCES/FAILURE
	 */
	@Override
	public String markAsCompleted(int studyId, String markCompleted, boolean flag, SessionObject sesObj,
			String customStudyId) {
		logger.info("StudyDAOImpl - markAsCompleted() - Starts");
		String msg = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		int count = 0;
		String activity = "";
		String activityDetails = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			/**
			 * match the markCompleted flag and complete the
			 * resource/notification/consent/consent review
			 * /checkList/activeTaskList/questionnaire/comprehenstionTest section of study
			 * before launch
			 **/
			if (markCompleted.equals(FdahpStudyDesignerConstants.NOTIFICATION)) {
				query = session.createQuery(" UPDATE StudySequenceBo SET miscellaneousNotification = " + flag
						+ " WHERE studyId = " + studyId);
				count = query.executeUpdate();
				if (flag) {
					activity = "Study Notifications section successfully  checked for minimum content completeness.";
					activityDetails = "Study Notifications section successfully  checked for minimum content completeness and marked 'Completed'. (Study ID = "
							+ customStudyId + ")";
				}
			} else if (markCompleted.equals(FdahpStudyDesignerConstants.RESOURCE)) {
				if (flag) {
					activity = "Resources section successsfully checked for minimum content completeness.";
					activityDetails = "Resources section successsfully checked for minimum content completeness and marked 'Completed'. (Study ID = "
							+ customStudyId + ")";
					auditLogDAO.updateDraftToEditedStatus(session, transaction, sesObj.getUserId(),
							FdahpStudyDesignerConstants.DRAFT_STUDY, studyId);
				}
				query = session.createQuery(
						" UPDATE StudySequenceBo SET miscellaneousResources = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
			} else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.CONESENT)) {
				query = session.createQuery(
						" UPDATE StudySequenceBo SET consentEduInfo = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
				if (flag) {
					activity = "Study Consent section successsfully checked for minimum content completeness.";
					activityDetails = "Consent Sections successfully checked for minimum content completeness and this ection of the Study is marked 'Completed'.  (Study ID = "
							+ customStudyId + ")";
				}
				auditLogDAO.updateDraftToEditedStatus(session, transaction, sesObj.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_CONSCENT, studyId);
			} else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.CONESENT_REVIEW)) {
				query = session
						.createQuery(" UPDATE StudySequenceBo SET eConsent = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
				auditLogDAO.updateDraftToEditedStatus(session, transaction, sesObj.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_CONSCENT, studyId);
			} else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.CHECK_LIST)) {
				query = session
						.createQuery(" UPDATE StudySequenceBo SET checkList = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
			} else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTIVETASK_LIST)) {
				query = session.createQuery(
						" UPDATE StudySequenceBo SET studyExcActiveTask = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
				if (flag) {
					activity = "Active Tasks section successfully checked for minimum Content Completeness.";
					activityDetails = "Active Tasks section successfully checked for minimum Content Completeness and marked 'Completed'. (Study ID = "
							+ customStudyId + ")";
				}
				auditLogDAO.updateDraftToEditedStatus(session, transaction, sesObj.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_ACTIVETASK, studyId);
			} else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTIONNAIRE)) {
				query = session.createQuery(
						" UPDATE StudySequenceBo SET studyExcQuestionnaries = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
				if (flag) {
					activity = "Questionnaire succesfully checked for minimum content completeness.";
					activityDetails = "Questionnaire succesfully checked for minimum content completeness and marked 'Done'. (Study ID = "
							+ customStudyId + ")";
				}
				auditLogDAO.updateDraftToEditedStatus(session, transaction, sesObj.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE, studyId);
			} else if (markCompleted.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPREHENSION_TEST)) {
				query = session.createQuery(
						" UPDATE StudySequenceBo SET comprehensionTest = " + flag + " WHERE studyId = " + studyId);
				count = query.executeUpdate();
				if (flag) {
					activity = "comprehension test";
					activityDetails = customStudyId + " -- "
							+ FdahpStudyDesignerConstants.QUESTIONNAIRELIST_MARKED_AS_COMPLETED;
				}
				auditLogDAO.updateDraftToEditedStatus(session, transaction, sesObj.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_CONSCENT, studyId);
			}
			if (count > 0) {
				msg = FdahpStudyDesignerConstants.SUCCESS;
			}
			if (sesObj != null && FdahpStudyDesignerUtil.isNotEmpty(activity)
					&& FdahpStudyDesignerUtil.isNotEmpty(activityDetails)) {
				auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activityDetails,
						"StudyDAOImpl - markAsCompleted");
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - markAsCompleted() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - markAsCompleted() - Ends");
		return msg;
	}

	/**
	 * Comprehension question will be created by default in the master order.Admin
	 * can manage the order by drag and drop questions in the list
	 * 
	 * @author BTC
	 * @param Integer , studyId {@link StudyBo}
	 * @param int     , oldOrderNumber
	 * @param int     , newOrderNumber
	 * @return String SUCCESS or FAILURE
	 *
	 */
	@Override
	public String reOrderComprehensionTestQuestion(Integer studyId, int oldOrderNumber, int newOrderNumber) {
		logger.info("StudyDAOImpl - reOrderComprehensionTestQuestion() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		Query query = null;
		int count = 0;
		ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
		StudySequenceBo studySequence = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			String updateQuery = "";
			query = session.createQuery("From ComprehensionTestQuestionBo CTB where CTB.studyId=" + studyId
					+ " and CTB.sequenceNo =" + oldOrderNumber + " and CTB.active=1");
			comprehensionTestQuestionBo = (ComprehensionTestQuestionBo) query.uniqueResult();
			if (comprehensionTestQuestionBo != null) {
				if (oldOrderNumber < newOrderNumber) {
					updateQuery = "update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=CTB.sequenceNo-1 where CTB.studyId="
							+ studyId + " and CTB.sequenceNo <=" + newOrderNumber + " and CTB.sequenceNo >"
							+ oldOrderNumber + " and CTB.active=1";
					query = session.createQuery(updateQuery);
					count = query.executeUpdate();
					if (count > 0) {
						query = session.createQuery(
								"update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=" + newOrderNumber
										+ " where CTB.id=" + comprehensionTestQuestionBo.getId() + " and CTB.active=1");
						count = query.executeUpdate();
						message = FdahpStudyDesignerConstants.SUCCESS;
					}
				} else if (oldOrderNumber > newOrderNumber) {
					updateQuery = "update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=CTB.sequenceNo+1 where CTB.studyId="
							+ studyId + " and CTB.sequenceNo >=" + newOrderNumber + " and CTB.sequenceNo <"
							+ oldOrderNumber + " and CTB.active=1";
					query = session.createQuery(updateQuery);
					count = query.executeUpdate();
					if (count > 0) {
						query = session.createQuery(
								"update ComprehensionTestQuestionBo CTB set CTB.sequenceNo=" + newOrderNumber
										+ " where CTB.id=" + comprehensionTestQuestionBo.getId() + " and CTB.active=1");
						count = query.executeUpdate();
						message = FdahpStudyDesignerConstants.SUCCESS;
					}
				}
				if (comprehensionTestQuestionBo.getStudyId() != null) {
					studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, comprehensionTestQuestionBo.getStudyId())
							.uniqueResult();
					if (studySequence != null) {
						if (studySequence.isComprehensionTest()) {
							studySequence.setComprehensionTest(false);
						}
						session.saveOrUpdate(studySequence);
					}
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - reOrderComprehensionTestQuestion() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - reOrderComprehensionTestQuestion() - Ends");
		return message;
	}

	/**
	 * Consent items will be created by default in master order.Admin can manage the
	 * order by reordering the consents with in a list to make reorder admin can
	 * drag and drop the consent with in a list
	 * 
	 * @author BTC
	 * @param Integer , studyId {@link StudyBo}
	 * @param int     , oldOrderNumber
	 * @param int     , newOrderNumber
	 * @return String SUCCESS or FAILURE
	 */
	@Override
	public String reOrderConsentInfoList(Integer studyId, int oldOrderNumber, int newOrderNumber) {
		logger.info("StudyDAOImpl - reOrderConsentInfoList() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		Query query = null;
		int count = 0;
		ConsentInfoBo consentInfoBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			String updateQuery = "";
			query = session.createQuery("From ConsentInfoBo CIB where CIB.studyId=" + studyId + " and CIB.sequenceNo ="
					+ oldOrderNumber + " and CIB.active=1");
			consentInfoBo = (ConsentInfoBo) query.uniqueResult();
			if (consentInfoBo != null) {
				if (oldOrderNumber < newOrderNumber) {
					updateQuery = "update ConsentInfoBo CIBO set CIBO.sequenceNo=CIBO.sequenceNo-1 where CIBO.studyId="
							+ studyId + " and CIBO.sequenceNo <=" + newOrderNumber + " and CIBO.sequenceNo >"
							+ oldOrderNumber + " and CIBO.active=1";
					query = session.createQuery(updateQuery);
					count = query.executeUpdate();
					if (count > 0) {
						query = session.createQuery("update ConsentInfoBo C set C.sequenceNo=" + newOrderNumber
								+ " where C.id=" + consentInfoBo.getId() + " and C.active=1");
						count = query.executeUpdate();
						message = FdahpStudyDesignerConstants.SUCCESS;
					}
				} else if (oldOrderNumber > newOrderNumber) {
					updateQuery = "update ConsentInfoBo CIBO set CIBO.sequenceNo=CIBO.sequenceNo+1 where CIBO.studyId="
							+ studyId + " and CIBO.sequenceNo >=" + newOrderNumber + " and CIBO.sequenceNo <"
							+ oldOrderNumber + " and CIBO.active=1";
					query = session.createQuery(updateQuery);
					count = query.executeUpdate();
					if (count > 0) {
						query = session.createQuery("update ConsentInfoBo C set C.sequenceNo=" + newOrderNumber
								+ " where C.id=" + consentInfoBo.getId() + " and C.active=1");
						count = query.executeUpdate();
						message = FdahpStudyDesignerConstants.SUCCESS;
					}
				}
				if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
					StudySequenceBo studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId).uniqueResult();
					if (studySequence != null) {
						studySequence.setConsentEduInfo(false);
						if (studySequence.iseConsent()) {
							studySequence.seteConsent(false);
						}
						session.saveOrUpdate(studySequence);
					}
				}

			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - reOrderConsentInfoList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - reOrderConsentInfoList() - Ends");
		return message;
	}

	/**
	 * Reorder the eligibility test questions
	 *
	 * @author BTC
	 *
	 * @param eligibilityId  , Eligibility Id of the study
	 * @param oldOrderNumber , Old order
	 * @param newOrderNumber , New order
	 * @param studyId        , Id of the study
	 * @return {@link FdahpStudyDesignerConstants.SUCCESS} or
	 *         {@link FdahpStudyDesignerConstants.FAILURE} , reorder status
	 */
	@Override
	public String reorderEligibilityTestQusAns(Integer eligibilityId, int oldOrderNumber, int newOrderNumber,
			Integer studyId) {
		logger.info("StudyDAOImpl - reorderEligibilityTestQusAns - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		Query hibQuery = null;
		int count = 0;
		EligibilityTestBo eligibilityTest = null;
		StringBuilder updatenewOrderQuery;
		Transaction trans = null;
		StringBuilder updateQuery = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			trans = session.beginTransaction();
			hibQuery = session.getNamedQuery("EligibilityTestBo.findByEligibilityIdAndSequenceNo")
					.setInteger("eligibilityId", eligibilityId).setInteger("sequenceNo", oldOrderNumber);
			eligibilityTest = (EligibilityTestBo) hibQuery.uniqueResult();
			if (eligibilityTest != null) {
				if (oldOrderNumber < newOrderNumber) {
					updateQuery = new StringBuilder();
					updateQuery.append(
							"UPDATE EligibilityTestBo ETB set ETB.sequenceNo=ETB.sequenceNo-1 WHERE ETB.eligibilityId=")
							.append(eligibilityId).append(" AND ETB.sequenceNo <=").append(newOrderNumber)
							.append(" AND ETB.sequenceNo >").append(oldOrderNumber).append(" AND ETB.active = true");
					hibQuery = session.createQuery(updateQuery.toString());
					count = hibQuery.executeUpdate();
				} else if (oldOrderNumber > newOrderNumber) {
					updateQuery = new StringBuilder();
					updateQuery.append(
							"UPDATE EligibilityTestBo ETB set ETB.sequenceNo=ETB.sequenceNo+1 WHERE ETB.eligibilityId=")
							.append(eligibilityId).append(" AND ETB.sequenceNo >=").append(newOrderNumber)
							.append(" AND ETB.sequenceNo <").append(oldOrderNumber).append(" AND ETB.active = true");
					hibQuery = session.createQuery(updateQuery.toString());
					count = hibQuery.executeUpdate();
				}
				updatenewOrderQuery = new StringBuilder();
				updatenewOrderQuery.append("UPDATE EligibilityTestBo ETB set ETB.sequenceNo=").append(newOrderNumber)
						.append(" WHERE ETB.id=").append(eligibilityTest.getId());
				if (count > 0) {
					hibQuery = session.createQuery(updatenewOrderQuery.toString());
					count = hibQuery.executeUpdate();
					message = FdahpStudyDesignerConstants.SUCCESS;
				}
				if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
					StudySequenceBo studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId).uniqueResult();
					if (studySequence != null && studySequence.isEligibility()) {
						studySequence.setEligibility(false);
						session.update(studySequence);
					}
				}
			}
			trans.commit();
		} catch (Exception e) {
			if (null != trans)
				trans.rollback();
			logger.error("StudyDAOImpl - reorderEligibilityTestQusAns - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - reorderEligibilityTestQusAns - Ends");
		return message;
	}

	@Override
	public String reOrderResourceList(Integer studyId, int oldOrderNumber, int newOrderNumber) {
		logger.info("StudyDAOImpl - reOrderResourceList() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		Query query = null;
		int count = 0;
		ResourceBO resourceBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			String updateQuery = "";
			query = session.createQuery("From ResourceBO RBO where RBO.studyId=" + studyId + " and RBO.sequenceNo ="
					+ oldOrderNumber + " and RBO.status=1");
			resourceBo = (ResourceBO) query.uniqueResult();
			if (resourceBo != null) {
				if (oldOrderNumber < newOrderNumber) {
					updateQuery = "update ResourceBO RBO set RBO.sequenceNo=RBO.sequenceNo-1 where RBO.studyId="
							+ studyId + " and RBO.sequenceNo <=" + newOrderNumber + " and RBO.sequenceNo >"
							+ oldOrderNumber + " and RBO.status=1";
					query = session.createQuery(updateQuery);
					count = query.executeUpdate();
					if (count > 0) {
						query = session.createQuery("update ResourceBO RBO set RBO.sequenceNo=" + newOrderNumber
								+ " where RBO.id=" + resourceBo.getId() + " and RBO.status=1");
						count = query.executeUpdate();
						message = FdahpStudyDesignerConstants.SUCCESS;
					}
				} else if (oldOrderNumber > newOrderNumber) {
					updateQuery = "update ResourceBO RBO set RBO.sequenceNo=RBO.sequenceNo+1 where RBO.studyId="
							+ studyId + " and RBO.sequenceNo >=" + newOrderNumber + " and RBO.sequenceNo <"
							+ oldOrderNumber + " and RBO.status=1";
					query = session.createQuery(updateQuery);
					count = query.executeUpdate();
					if (count > 0) {
						query = session.createQuery("update ResourceBO RBO set RBO.sequenceNo=" + newOrderNumber
								+ " where RBO.id=" + resourceBo.getId() + " and RBO.status=1");
						count = query.executeUpdate();
						message = FdahpStudyDesignerConstants.SUCCESS;
					}
				}
				if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
					StudySequenceBo studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId).uniqueResult();
					if (studySequence != null) {
						studySequence.setMiscellaneousResources(false);
						session.saveOrUpdate(studySequence);
					}
				}

			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - reOrderResourceList() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - reOrderResourceList() - Ends");
		return message;
	}

	/**
	 * reset study by customStudyId
	 * 
	 * @author BTC
	 * @param String , customStudyId
	 * @return boolean
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean resetDraftStudyByCustomStudyId(String customStudyId, String action, SessionObject sesObj) {
		logger.info("StudyDAOImpl - resetDraftStudyByCustomStudyId() - Starts");
		Session session = null;
		boolean flag = false;
		StudyBo liveStudyBo = null;
		List<StudyBo> draftDatas = null;
		List<StudyPageBo> studyPageBo = null;
		EligibilityBo eligibilityBo = null;
		List<ResourceBO> resourceBOList = null;
		List<QuestionnaireBo> questionnaires = null;
		QuestionReponseTypeBo questionReponseTypeBo = null;
		List<Integer> studyIdList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();

			liveStudyBo = (StudyBo) session.getNamedQuery("getStudyLiveVersion")
					.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId).uniqueResult();
			if (liveStudyBo != null) {
				// create new Study and made it draft
				StudyBo studyDreaftBo = SerializationUtils.clone(liveStudyBo);
				studyDreaftBo.setVersion(0f);
				studyDreaftBo.setId(null);
				if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
					studyDreaftBo.setLive(5);
					studyDreaftBo.setThumbnailImage(null);
				} else {
					studyDreaftBo.setCustomStudyId(null);
					studyDreaftBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
					studyDreaftBo.setCreatedBy(sesObj.getUserId());
					studyDreaftBo.setModifiedOn(null);
					studyDreaftBo.setModifiedBy(null);
					studyDreaftBo.setLive(0);
				}
				studyDreaftBo.setHasActivetaskDraft(0);
				studyDreaftBo.setHasQuestionnaireDraft(0);
				studyDreaftBo.setHasConsentDraft(0);
				studyDreaftBo.setHasStudyDraft(0);
				studyDreaftBo.setStatus(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH);
				session.save(studyDreaftBo);

				// Study Permission
				String permissionQuery = "select s.user_id,s.view_permission"
						+ " from study_permission s, user_permission_mapping u, user_permissions p"
						+ " where s.study_id=" + liveStudyBo.getId() + " and s.user_id=u.user_id"
						+ " and u.permission_id = p.permission_id" + " and p.permissions='ROLE_SUPERADMIN'";
				Iterator iterator = session.createSQLQuery(permissionQuery).list().iterator();
				while (iterator.hasNext()) {
					Object[] objects = (Object[]) iterator.next();
					if (objects != null) {
						Integer superadminId = (Integer) objects[0];
						StudyPermissionBO studyPermissionBO = new StudyPermissionBO();
						studyPermissionBO.setUserId((Integer) objects[0]);
						studyPermissionBO.setViewPermission((Boolean) objects[1]);
						studyPermissionBO.setStudyId(studyDreaftBo.getId());
						studyPermissionBO.setStudyPermissionId(null);
						session.save(studyPermissionBO);
						if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)
								&& !superadminId.equals(sesObj.getUserId())) {
							studyPermissionBO = new StudyPermissionBO();
							studyPermissionBO.setUserId(sesObj.getUserId());
							studyPermissionBO.setViewPermission((Boolean) objects[1]);
							studyPermissionBO.setStudyId(studyDreaftBo.getId());
							studyPermissionBO.setStudyPermissionId(null);
							session.save(studyPermissionBO);
						}
					}
				}
				// studySequence
				StudySequenceBo studySequenceBo = new StudySequenceBo();
				studySequenceBo.setStudyId(studyDreaftBo.getId());
				session.save(studySequenceBo);

				// Over View
				query = session.createQuery("from StudyPageBo where studyId=" + liveStudyBo.getId());
				studyPageBo = query.list();
				if (studyPageBo != null && !studyPageBo.isEmpty()) {
					for (StudyPageBo pageBo : studyPageBo) {
						StudyPageBo subPageBo = SerializationUtils.clone(pageBo);
						subPageBo.setStudyId(studyDreaftBo.getId());
						if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
							subPageBo.setImagePath(null);
						}
						subPageBo.setPageId(null);
						session.save(subPageBo);
					}
				}

				// Eligibility
				query = session.getNamedQuery("getEligibiltyByStudyId").setInteger(FdahpStudyDesignerConstants.STUDY_ID,
						liveStudyBo.getId());
				eligibilityBo = (EligibilityBo) query.uniqueResult();
				if (eligibilityBo != null) {
					EligibilityBo bo = SerializationUtils.clone(eligibilityBo);
					bo.setStudyId(studyDreaftBo.getId());
					bo.setId(null);
					session.save(bo);
					List<EligibilityTestBo> eligibilityTestList = null;
					eligibilityTestList = session.getNamedQuery("EligibilityTestBo.findByEligibilityId")
							.setInteger(FdahpStudyDesignerConstants.ELIGIBILITY_ID, eligibilityBo.getId()).list();
					if (eligibilityTestList != null && !eligibilityTestList.isEmpty()) {
						for (EligibilityTestBo eligibilityTestBo : eligibilityTestList) {
							EligibilityTestBo newEligibilityTestBo = SerializationUtils.clone(eligibilityTestBo);
							newEligibilityTestBo.setId(null);
							newEligibilityTestBo.setEligibilityId(bo.getId());
							newEligibilityTestBo.setUsed(false);
							if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
								newEligibilityTestBo.setStatus(false);
							}
							session.save(newEligibilityTestBo);
						}
					}
				}

				// resources
				String searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId=" + liveStudyBo.getId()
						+ " AND RBO.status = 1 ORDER BY RBO.createdOn";
				query = session.createQuery(searchQuery);
				resourceBOList = query.list();
				if (resourceBOList != null && !resourceBOList.isEmpty()) {
					for (ResourceBO bo : resourceBOList) {
						ResourceBO resourceBO = SerializationUtils.clone(bo);
						resourceBO.setStudyId(studyDreaftBo.getId());
						if (StringUtils.isNotEmpty(bo.getPdfUrl()) && StringUtils.isNotEmpty(bo.getPdfName())) {
							resourceBO.setAction(false);
						}
						resourceBO.setId(null);
						if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
							resourceBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
							resourceBO.setCreatedBy(sesObj.getUserId());
							resourceBO.setModifiedBy(null);
							resourceBO.setModifiedOn(null);
						} else {
							resourceBO.setPdfName(null);
							resourceBO.setPdfUrl(null);

						}
						session.save(resourceBO);
					}
				}
				// Questionarries
				query = session.createQuery(
						" From QuestionnaireBo QBO WHERE QBO.live=1 and QBO.active=1 and QBO.customStudyId='"
								+ customStudyId + "' order by QBO.createdDate");
				questionnaires = query.list();
				if (questionnaires != null && !questionnaires.isEmpty()) {
					for (QuestionnaireBo questionnaireBo : questionnaires) {
						boolean draftFlag = false;
						QuestionnaireBo newQuestionnaireBo = SerializationUtils.clone(questionnaireBo);
						newQuestionnaireBo.setId(null);
						newQuestionnaireBo.setStudyId(studyDreaftBo.getId());
						newQuestionnaireBo.setVersion(questionnaireBo.getVersion());
						if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY))
							newQuestionnaireBo.setLive(5);
						else {
							newQuestionnaireBo.setCustomStudyId(null);
							newQuestionnaireBo.setLive(0);
							newQuestionnaireBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
							newQuestionnaireBo.setCreatedBy(sesObj.getUserId());
							newQuestionnaireBo.setModifiedBy(null);
							newQuestionnaireBo.setModifiedDate(null);
							// newQuestionnaireBo.setShortTitle(null);
							newQuestionnaireBo.setStatus(false);
						}
						newQuestionnaireBo.setVersion(0f);
						session.save(newQuestionnaireBo);
						/** Schedule Purpose creating draft Start **/
						if (StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
							if (questionnaireBo.getFrequency()
									.equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
								searchQuery = "From QuestionnaireCustomScheduleBo QCSBO where QCSBO.questionnairesId="
										+ questionnaireBo.getId();
								List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleList = session
										.createQuery(searchQuery).list();
								if (questionnaireCustomScheduleList != null
										&& !questionnaireCustomScheduleList.isEmpty()) {
									for (QuestionnaireCustomScheduleBo customScheduleBo : questionnaireCustomScheduleList) {
										QuestionnaireCustomScheduleBo newCustomScheduleBo = SerializationUtils
												.clone(customScheduleBo);
										newCustomScheduleBo.setQuestionnairesId(newQuestionnaireBo.getId());
										newCustomScheduleBo.setId(null);
										newCustomScheduleBo.setUsed(false);
										session.save(newCustomScheduleBo);
									}
								}
							} else {
								searchQuery = "From QuestionnairesFrequenciesBo QFBO where QFBO.questionnairesId="
										+ questionnaireBo.getId();
								List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList = session
										.createQuery(searchQuery).list();
								if (questionnairesFrequenciesList != null && !questionnairesFrequenciesList.isEmpty()) {
									for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo : questionnairesFrequenciesList) {
										QuestionnairesFrequenciesBo newQuestionnairesFrequenciesBo = SerializationUtils
												.clone(questionnairesFrequenciesBo);
										newQuestionnairesFrequenciesBo.setQuestionnairesId(newQuestionnaireBo.getId());
										newQuestionnairesFrequenciesBo.setId(null);
										session.save(newQuestionnairesFrequenciesBo);
									}
								}
							}
						}
						/** Schedule Purpose creating draft End **/

						/** Content purpose creating draft Start **/

						List<Integer> destinationList = new ArrayList<>();
						Map<Integer, Integer> destionationMapList = new HashMap<>();

						List<QuestionnairesStepsBo> existedQuestionnairesStepsBoList = null;
						List<QuestionnairesStepsBo> newQuestionnairesStepsBoList = new ArrayList<>();
						List<QuestionResponseSubTypeBo> existingQuestionResponseSubTypeList = new ArrayList<>();
						List<QuestionResponseSubTypeBo> newQuestionResponseSubTypeList = new ArrayList<>();
						query = session.getNamedQuery("getQuestionnaireStepList").setInteger("questionnaireId",
								questionnaireBo.getId());
						existedQuestionnairesStepsBoList = query.list();
						if (existedQuestionnairesStepsBoList != null && !existedQuestionnairesStepsBoList.isEmpty()) {
							for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
								Integer destionStep = questionnairesStepsBo.getDestinationStep();
								if (destionStep.equals(0)) {
									destinationList.add(-1);
								} else {
									for (int i = 0; i < existedQuestionnairesStepsBoList.size(); i++) {
										if (existedQuestionnairesStepsBoList.get(i).getStepId() != null && destionStep
												.equals(existedQuestionnairesStepsBoList.get(i).getStepId())) {
											destinationList.add(i);
											break;
										}
									}
								}
								destionationMapList.put(questionnairesStepsBo.getSequenceNo(),
										questionnairesStepsBo.getStepId());
							}
							for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
								if (StringUtils.isNotEmpty(questionnairesStepsBo.getStepType())) {
									QuestionnairesStepsBo newQuestionnairesStepsBo = SerializationUtils
											.clone(questionnairesStepsBo);
									newQuestionnairesStepsBo.setQuestionnairesId(newQuestionnaireBo.getId());
									newQuestionnairesStepsBo.setStepId(null);
									if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
										newQuestionnairesStepsBo
												.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
										newQuestionnairesStepsBo.setCreatedBy(sesObj.getUserId());
										newQuestionnairesStepsBo.setModifiedBy(null);
										newQuestionnairesStepsBo.setModifiedOn(null);
										newQuestionnairesStepsBo.setStatus(false);
										// newQuestionnairesStepsBo.setStepShortTitle(null);
									}
									session.save(newQuestionnairesStepsBo);
									if (questionnairesStepsBo.getStepType()
											.equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
										InstructionsBo instructionsBo = (InstructionsBo) session
												.getNamedQuery("getInstructionStep")
												.setInteger("id", questionnairesStepsBo.getInstructionFormId())
												.uniqueResult();
										if (instructionsBo != null) {
											InstructionsBo newInstructionsBo = SerializationUtils.clone(instructionsBo);
											newInstructionsBo.setId(null);
											if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
												newInstructionsBo
														.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
												newInstructionsBo.setCreatedBy(sesObj.getUserId());
												newInstructionsBo.setModifiedBy(null);
												newInstructionsBo.setModifiedOn(null);
												newInstructionsBo.setStatus(false);
											}
											session.save(newInstructionsBo);

											// updating new InstructionId
											newQuestionnairesStepsBo.setInstructionFormId(newInstructionsBo.getId());
										}
									} else if (questionnairesStepsBo.getStepType()
											.equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
										QuestionsBo questionsBo = (QuestionsBo) session.getNamedQuery("getQuestionStep")
												.setInteger("stepId", questionnairesStepsBo.getInstructionFormId())
												.uniqueResult();
										if (questionsBo != null) {
											boolean questionDraftFlag = false;
											// Question response subType
											List<QuestionResponseSubTypeBo> questionResponseSubTypeList = session
													.getNamedQuery("getQuestionSubResponse")
													.setInteger("responseTypeId", questionsBo.getId()).list();

											List<QuestionConditionBranchBo> questionConditionBranchList = session
													.getNamedQuery("getQuestionConditionBranchList")
													.setInteger("questionId", questionsBo.getId()).list();

											// Question response Type
											questionReponseTypeBo = (QuestionReponseTypeBo) session
													.getNamedQuery("getQuestionResponse")
													.setInteger("questionsResponseTypeId", questionsBo.getId())
													.uniqueResult();

											QuestionsBo newQuestionsBo = SerializationUtils.clone(questionsBo);
											newQuestionsBo.setId(null);
											if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
												newQuestionsBo
														.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
												newQuestionsBo.setCreatedBy(sesObj.getUserId());
												newQuestionsBo.setModifiedBy(null);
												newQuestionsBo.setModifiedOn(null);
												// newQuestionsBo.setShortTitle(null);
												// newQuestionsBo.setStatShortName(null);
												newQuestionsBo.setStatus(false);
											}
											session.save(newQuestionsBo);

											// Question response Type
											if (questionReponseTypeBo != null) {
												QuestionReponseTypeBo newQuestionReponseTypeBo = SerializationUtils
														.clone(questionReponseTypeBo);
												newQuestionReponseTypeBo.setResponseTypeId(null);
												newQuestionReponseTypeBo
														.setQuestionsResponseTypeId(newQuestionsBo.getId());
												session.save(newQuestionReponseTypeBo);
											}

											// Question Condition branching
											// logic
											if (questionConditionBranchList != null
													&& !questionConditionBranchList.isEmpty()) {
												for (QuestionConditionBranchBo questionConditionBranchBo : questionConditionBranchList) {
													QuestionConditionBranchBo newQuestionConditionBranchBo = SerializationUtils
															.clone(questionConditionBranchBo);
													newQuestionConditionBranchBo.setConditionId(null);
													newQuestionConditionBranchBo.setQuestionId(newQuestionsBo.getId());
													session.save(newQuestionConditionBranchBo);
												}
											}

											// Question response subType
											if (questionResponseSubTypeList != null
													&& !questionResponseSubTypeList.isEmpty()) {
												existingQuestionResponseSubTypeList.addAll(questionResponseSubTypeList);

												for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionResponseSubTypeList) {
													if (StringUtils.isNotEmpty(questionResponseSubTypeBo.getImage())
															&& StringUtils.isNotEmpty(
																	questionResponseSubTypeBo.getSelectedImage())) {
														draftFlag = true;
														questionDraftFlag = true;
													}
													QuestionResponseSubTypeBo newQuestionResponseSubTypeBo = SerializationUtils
															.clone(questionResponseSubTypeBo);
													newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
													newQuestionResponseSubTypeBo
															.setResponseTypeId(newQuestionsBo.getId());
													if (!action
															.equalsIgnoreCase(FdahpStudyDesignerConstants.COPY_STUDY)) {
														newQuestionResponseSubTypeBo.setImage(null);
														newQuestionResponseSubTypeBo.setSelectedImage(null);
													}
													newQuestionResponseSubTypeBo.setDestinationStepId(null);
													session.save(newQuestionResponseSubTypeBo);
													newQuestionResponseSubTypeList.add(newQuestionResponseSubTypeBo);
												}
											}
											if (questionDraftFlag) {
												newQuestionnairesStepsBo.setStatus(false);
												newQuestionsBo.setStatus(false);
												session.update(newQuestionsBo);
											}
											// updating new InstructionId
											newQuestionnairesStepsBo.setInstructionFormId(newQuestionsBo.getId());
										}
									} else if (questionnairesStepsBo.getStepType()
											.equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
										FormBo formBo = (FormBo) session.getNamedQuery("getFormBoStep")
												.setInteger("stepId", questionnairesStepsBo.getInstructionFormId())
												.uniqueResult();
										if (formBo != null) {
											boolean formDraftFlag = false;
											FormBo newFormBo = SerializationUtils.clone(formBo);
											newFormBo.setFormId(null);
											session.save(newFormBo);

											List<FormMappingBo> formMappingBoList = session
													.getNamedQuery("getFormByFormId")
													.setInteger("formId", formBo.getFormId()).list();
											if (formMappingBoList != null && !formMappingBoList.isEmpty()) {
												for (FormMappingBo formMappingBo : formMappingBoList) {
													FormMappingBo newMappingBo = SerializationUtils
															.clone(formMappingBo);
													newMappingBo.setFormId(newFormBo.getFormId());
													newMappingBo.setId(null);

													QuestionsBo questionsBo = (QuestionsBo) session
															.getNamedQuery("getQuestionByFormId")
															.setInteger("formId", formMappingBo.getQuestionId())
															.uniqueResult();
													if (questionsBo != null) {
														boolean questionFlag = false;
														// Question response
														// subType
														List<QuestionResponseSubTypeBo> questionResponseSubTypeList = session
																.getNamedQuery("getQuestionSubResponse")
																.setInteger("responseTypeId", questionsBo.getId())
																.list();

														// Question response
														// Type
														questionReponseTypeBo = (QuestionReponseTypeBo) session
																.getNamedQuery("getQuestionResponse")
																.setInteger("questionsResponseTypeId",
																		questionsBo.getId())
																.uniqueResult();

														QuestionsBo newQuestionsBo = SerializationUtils
																.clone(questionsBo);
														newQuestionsBo.setId(null);
														if (action.equalsIgnoreCase(
																FdahpStudyDesignerConstants.COPY_STUDY)) {
															newQuestionsBo.setCreatedOn(
																	FdahpStudyDesignerUtil.getCurrentDateTime());
															newQuestionsBo.setCreatedBy(sesObj.getUserId());
															newQuestionsBo.setModifiedBy(null);
															newQuestionsBo.setModifiedOn(null);
															// newQuestionsBo.setShortTitle(null);
															// newQuestionsBo.setStatShortName(null);
															newQuestionsBo.setStatus(false);
														}
														session.save(newQuestionsBo);

														// Question response
														// Type
														if (questionReponseTypeBo != null) {
															QuestionReponseTypeBo newQuestionReponseTypeBo = SerializationUtils
																	.clone(questionReponseTypeBo);
															newQuestionReponseTypeBo.setResponseTypeId(null);
															newQuestionReponseTypeBo
																	.setQuestionsResponseTypeId(newQuestionsBo.getId());
															session.save(newQuestionReponseTypeBo);
														}

														// Question response
														// subType
														if (questionResponseSubTypeList != null
																&& !questionResponseSubTypeList.isEmpty()) {
															// existingQuestionResponseSubTypeList.addAll(questionResponseSubTypeList);
															for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionResponseSubTypeList) {
																if (StringUtils.isNotEmpty(
																		questionResponseSubTypeBo.getImage())
																		&& StringUtils
																				.isNotEmpty(questionResponseSubTypeBo
																						.getSelectedImage())) {
																	draftFlag = true;
																	formDraftFlag = true;
																	questionFlag = true;
																}
																QuestionResponseSubTypeBo newQuestionResponseSubTypeBo = SerializationUtils
																		.clone(questionResponseSubTypeBo);
																newQuestionResponseSubTypeBo
																		.setResponseSubTypeValueId(null);
																newQuestionResponseSubTypeBo
																		.setResponseTypeId(newQuestionsBo.getId());
																if (!action.equalsIgnoreCase(
																		FdahpStudyDesignerConstants.COPY_STUDY)) {
																	newQuestionResponseSubTypeBo.setImage(null);
																	newQuestionResponseSubTypeBo.setSelectedImage(null);
																}
																session.save(newQuestionResponseSubTypeBo);
																// newQuestionResponseSubTypeList.add(newQuestionResponseSubTypeBo);
															}
														}

														// adding questionId
														newMappingBo.setQuestionId(newQuestionsBo.getId());
														session.save(newMappingBo);
														if (questionFlag) {
															newQuestionsBo.setStatus(false);
															session.update(newQuestionsBo);
														}
													}

												}
											}
											// updating new formId
											if (formDraftFlag) {
												newQuestionnairesStepsBo.setStatus(false);
											}
											newQuestionnairesStepsBo.setInstructionFormId(newFormBo.getFormId());

										}
									}
									session.update(newQuestionnairesStepsBo);
									newQuestionnairesStepsBoList.add(newQuestionnairesStepsBo);
								}
							}
						}
						if (destinationList != null && !destinationList.isEmpty()) {
							for (int i = 0; i < destinationList.size(); i++) {
								int desId = 0;
								if (destinationList.get(i) != -1) {
									desId = newQuestionnairesStepsBoList.get(destinationList.get(i)).getStepId();
								}
								newQuestionnairesStepsBoList.get(i).setDestinationStep(desId);
								session.update(newQuestionnairesStepsBoList.get(i));
							}
						}
						List<Integer> sequenceSubTypeList = new ArrayList<>();
						List<Integer> destinationResList = new ArrayList<>();
						if (existingQuestionResponseSubTypeList != null
								&& !existingQuestionResponseSubTypeList.isEmpty()) {
							for (QuestionResponseSubTypeBo questionResponseSubTypeBo : existingQuestionResponseSubTypeList) {
								if (questionResponseSubTypeBo.getDestinationStepId() == null) {
									sequenceSubTypeList.add(null);
								} else if (questionResponseSubTypeBo.getDestinationStepId() != null
										&& questionResponseSubTypeBo.getDestinationStepId().equals(0)) {
									sequenceSubTypeList.add(-1);
								} else {
									if (existedQuestionnairesStepsBoList != null
											&& !existedQuestionnairesStepsBoList.isEmpty()) {
										for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
											if (questionResponseSubTypeBo.getDestinationStepId() != null
													&& questionResponseSubTypeBo.getDestinationStepId()
															.equals(questionnairesStepsBo.getStepId())) {
												sequenceSubTypeList.add(questionnairesStepsBo.getSequenceNo());
												break;
											}
										}

									}
								}
							}
						}
						if (sequenceSubTypeList != null && !sequenceSubTypeList.isEmpty()) {
							for (int i = 0; i < sequenceSubTypeList.size(); i++) {
								Integer desId = null;
								if (sequenceSubTypeList.get(i) == null) {
									desId = null;
								} else if (sequenceSubTypeList.get(i).equals(-1)) {
									desId = 0;
								} else {
									for (QuestionnairesStepsBo questionnairesStepsBo : newQuestionnairesStepsBoList) {
										if (sequenceSubTypeList.get(i).equals(questionnairesStepsBo.getSequenceNo())) {
											desId = questionnairesStepsBo.getStepId();
											break;
										}
									}
								}
								destinationResList.add(desId);
							}
							for (int i = 0; i < destinationResList.size(); i++) {
								newQuestionResponseSubTypeList.get(i).setDestinationStepId(destinationResList.get(i));
								session.update(newQuestionResponseSubTypeList.get(i));
							}
						}
						if (draftFlag) {
							newQuestionnaireBo.setStatus(false);
							session.update(newQuestionnaireBo);
						}
						/** Content purpose creating draft End **/
					}
				} // If Questionarries updated flag -1 then update End
					// ActiveTasks
				List<ActiveTaskBo> activeTasks = null;
				query = session.createQuery(
						"SELECT ATB FROM ActiveTaskBo ATB where ATB.active IS NOT NULL and ATB.active=1 and ATB.live =1 and customStudyId='"
								+ customStudyId + "' order by id");
				activeTasks = query.list();
				if (activeTasks != null && !activeTasks.isEmpty()) {
					for (ActiveTaskBo activeTaskBo : activeTasks) {
						ActiveTaskBo newActiveTaskBo = SerializationUtils.clone(activeTaskBo);
						newActiveTaskBo.setId(null);
						newActiveTaskBo.setStudyId(studyDreaftBo.getId());
						newActiveTaskBo.setVersion(activeTaskBo.getVersion());
						if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY))
							newActiveTaskBo.setLive(5);
						else {
							newActiveTaskBo.setCustomStudyId(null);
							newActiveTaskBo.setLive(0);
							newActiveTaskBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
							newActiveTaskBo.setCreatedBy(sesObj.getUserId());
							newActiveTaskBo.setModifiedBy(null);
							newActiveTaskBo.setModifiedDate(null);
							// newActiveTaskBo.setShortTitle(null);
							newActiveTaskBo.setAction(false);
						}
						newActiveTaskBo.setVersion(0f);
						session.save(newActiveTaskBo);
						/** Schedule Purpose creating draft Start **/
						if (StringUtils.isNotEmpty(activeTaskBo.getFrequency())) {
							if (activeTaskBo.getFrequency()
									.equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
								searchQuery = "From ActiveTaskCustomScheduleBo QCSBO where QCSBO.activeTaskId="
										+ activeTaskBo.getId();
								List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleList = session
										.createQuery(searchQuery).list();
								if (activeTaskCustomScheduleList != null && !activeTaskCustomScheduleList.isEmpty()) {
									for (ActiveTaskCustomScheduleBo customScheduleBo : activeTaskCustomScheduleList) {
										ActiveTaskCustomScheduleBo newCustomScheduleBo = SerializationUtils
												.clone(customScheduleBo);
										newCustomScheduleBo.setActiveTaskId(newActiveTaskBo.getId());
										newCustomScheduleBo.setUsed(false);
										newCustomScheduleBo.setId(null);
										session.save(newCustomScheduleBo);
									}
								}
							} else {
								searchQuery = "From ActiveTaskFrequencyBo QFBO where QFBO.activeTaskId="
										+ activeTaskBo.getId();
								List<ActiveTaskFrequencyBo> activeTaskFrequenciesList = session.createQuery(searchQuery)
										.list();
								if (activeTaskFrequenciesList != null && !activeTaskFrequenciesList.isEmpty()) {
									for (ActiveTaskFrequencyBo activeTaskFrequenciesBo : activeTaskFrequenciesList) {
										ActiveTaskFrequencyBo newFrequenciesBo = SerializationUtils
												.clone(activeTaskFrequenciesBo);
										newFrequenciesBo.setActiveTaskId(newActiveTaskBo.getId());
										newFrequenciesBo.setId(null);
										session.save(newFrequenciesBo);
									}
								}
							}
						}
						/** Schedule Purpose creating draft End **/

						/** Content Purpose creating draft Start **/
						query = session.getNamedQuery("getAttributeListByActiveTAskId").setInteger("activeTaskId",
								activeTaskBo.getId());
						List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBoList = query.list();
						if (activeTaskAtrributeValuesBoList != null && !activeTaskAtrributeValuesBoList.isEmpty()) {
							for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo : activeTaskAtrributeValuesBoList) {
								ActiveTaskAtrributeValuesBo newActiveTaskAtrributeValuesBo = SerializationUtils
										.clone(activeTaskAtrributeValuesBo);
								newActiveTaskAtrributeValuesBo.setActiveTaskId(newActiveTaskBo.getId());
								newActiveTaskAtrributeValuesBo.setAttributeValueId(null);
								/*
								 * if(action.equalsIgnoreCase( FdahpStudyDesignerConstants.COPY_STUDY)){
								 * newActiveTaskAtrributeValuesBo .setIdentifierNameStat(null); }
								 */
								session.save(newActiveTaskAtrributeValuesBo);
							}

						}
						/** Content Purpose creating draft End **/
					}
				} // Active TAsk End

				// Consent updated update Start
				query = session.createQuery("From ConsentBo CBO WHERE CBO.live =1 and customStudyId='" + customStudyId
						+ "' order by CBO.createdOn DESC");
				ConsentBo consentBo = (ConsentBo) query.uniqueResult();
				if (consentBo != null) {
					ConsentBo newConsentBo = SerializationUtils.clone(consentBo);
					newConsentBo.setId(null);
					newConsentBo.setStudyId(studyDreaftBo.getId());
					newConsentBo.setVersion(0f);
					if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY))
						newConsentBo.setLive(5);
					else {
						newConsentBo.setCustomStudyId(null);
						newConsentBo.setLive(0);
						newConsentBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
						newConsentBo.setCreatedBy(sesObj.getUserId());
						newConsentBo.setModifiedBy(null);
						newConsentBo.setModifiedOn(null);
					}
					newConsentBo.setVersion(0f);
					session.save(newConsentBo);

					// Comprehension test Start
					if (StringUtils.isNotEmpty(consentBo.getNeedComprehensionTest())
							&& consentBo.getNeedComprehensionTest().equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
						List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
						List<Integer> comprehensionIds = new ArrayList<>();
						List<ComprehensionTestResponseBo> comprehensionTestResponseList = null;
						query = session.createQuery("From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId="
								+ consentBo.getStudyId() + " and CTQBO.active=1 order by CTQBO.sequenceNo asc");
						comprehensionTestQuestionList = query.list();
						if (comprehensionTestQuestionList != null && !comprehensionTestQuestionList.isEmpty()) {
							for (ComprehensionTestQuestionBo comprehensionTestQuestionBo : comprehensionTestQuestionList) {
								comprehensionIds.add(comprehensionTestQuestionBo.getId());
							}
							comprehensionTestResponseList = session
									.createQuery("From ComprehensionTestResponseBo CTRBO "
											+ "where CTRBO.comprehensionTestQuestionId IN("
											+ StringUtils.join(comprehensionIds, ",")
											+ ") order by comprehensionTestQuestionId")
									.list();
							for (ComprehensionTestQuestionBo comprehensionTestQuestionBo : comprehensionTestQuestionList) {
								ComprehensionTestQuestionBo newComprehensionTestQuestionBo = SerializationUtils
										.clone(comprehensionTestQuestionBo);
								newComprehensionTestQuestionBo.setStudyId(studyDreaftBo.getId());
								newComprehensionTestQuestionBo.setId(null);
								newComprehensionTestQuestionBo
										.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
								newComprehensionTestQuestionBo.setCreatedBy(sesObj.getUserId());
								newComprehensionTestQuestionBo.setModifiedBy(null);
								newComprehensionTestQuestionBo.setModifiedOn(null);
								session.save(newComprehensionTestQuestionBo);
								if (comprehensionTestResponseList != null && !comprehensionTestResponseList.isEmpty()) {
									for (ComprehensionTestResponseBo comprehensionTestResponseBo : comprehensionTestResponseList) {
										if (comprehensionTestQuestionBo.getId()
												.intValue() == comprehensionTestResponseBo
														.getComprehensionTestQuestionId().intValue()) {
											ComprehensionTestResponseBo newComprehensionTestResponseBo = SerializationUtils
													.clone(comprehensionTestResponseBo);
											newComprehensionTestResponseBo.setId(null);
											newComprehensionTestResponseBo.setComprehensionTestQuestionId(
													newComprehensionTestQuestionBo.getId());
											session.save(newComprehensionTestResponseBo);
										}
									}
								}
							}
						}

					}
					// Comprehension test End
				}

				query = session
						.createQuery(" From ConsentInfoBo CBO WHERE CBO.live =1 and CBO.active=1 and customStudyId='"
								+ customStudyId + "' order by CBO.sequenceNo DESC");
				List<ConsentInfoBo> consentInfoBoList = query.list();
				if (consentInfoBoList != null && !consentInfoBoList.isEmpty()) {
					for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
						ConsentInfoBo newConsentInfoBo = SerializationUtils.clone(consentInfoBo);
						newConsentInfoBo.setId(null);
						newConsentInfoBo.setStudyId(studyDreaftBo.getId());
						newConsentInfoBo.setVersion(0f);
						// newConsentInfoBo.setLive(5);
						if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY))
							newConsentInfoBo.setLive(5);
						else {
							newConsentInfoBo.setLive(0);
							newConsentInfoBo.setCustomStudyId(null);
							newConsentInfoBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
							newConsentInfoBo.setCreatedBy(sesObj.getUserId());
							newConsentInfoBo.setModifiedBy(null);
							newConsentInfoBo.setModifiedOn(null);
							newConsentInfoBo.setStatus(false);
						}
						newConsentInfoBo.setVersion(0f);
						session.save(newConsentInfoBo);
					}
				}
				// Consent updated update End
				// checklist save
				Checklist checklist = new Checklist();
				checklist.setStudyId(studyDreaftBo.getId());
				checklist.setCustomStudyId(
						action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY) ? customStudyId : null);
				session.save(checklist);
				flag = true;
				if (flag && action.equalsIgnoreCase(FdahpStudyDesignerConstants.RESET_STUDY)) {
					draftDatas = session.getNamedQuery("getStudyDraftVersion")
							.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId).list();
					if (draftDatas != null && !draftDatas.isEmpty()) {
						studyIdList = new ArrayList<>();
						for (StudyBo study : draftDatas) {
							studyIdList.add(study.getId());
						}
						deleteStudyByIdOrCustomstudyId(session, transaction, StringUtils.join(studyIdList, ","), "");
					}
				}
				// }
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - resetDraftStudyByCustomStudyId() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - resetDraftStudyByCustomStudyId() - Ends");
		return flag;
	}

	/**
	 * This method is used to get the sequence number to set to the resource
	 * 
	 * @author BTC
	 * @param studyId
	 * @return sequence number
	 */
	@Override
	public int resourceOrder(Integer studyId) {
		logger.info("StudyDAOImpl - resourceOrder() - Starts");
		Session session = null;
		int count = 1;
		ResourceBO resourceBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("From ResourceBO RBO where RBO.studyId=" + studyId
					+ " and RBO.studyProtocol = false and RBO.status=1 order by RBO.sequenceNo DESC");
			query.setMaxResults(1);
			resourceBo = ((ResourceBO) query.uniqueResult());
			if (resourceBo != null) {
				count = resourceBo.getSequenceNo() + 1;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - resourceOrder() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - resourceOrder() - Ends");
		return count;
	}

	/**
	 * This method is used to get the saved resource list
	 * 
	 * @author BTC
	 * @param studyId
	 * @return List of {@link ResourceBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceBO> resourcesSaved(Integer studyId) {
		logger.info("StudyDAOImpl - resourcesSaved() - Starts");
		List<ResourceBO> resourceBOList = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId=" + studyId
					+ " AND RBO.action = 0 AND RBO.status = 1 AND RBO.studyProtocol = 0";
			query = session.createQuery(searchQuery);
			resourceBOList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - resourcesSaved() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - resourcesSaved() - Ends");
		return resourceBOList;
	}

	/**
	 * This method is used to get the list of resources having anchor date
	 * 
	 * @author BTC
	 * @param studyId
	 * @return List of {@link ResourceBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceBO> resourcesWithAnchorDate(Integer studyId) {
		logger.info("StudyDAOImpl - resourcesWithAnchorDate() - Starts");
		List<ResourceBO> resourceList = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId=" + studyId
					+ " AND RBO.resourceType = 1 AND RBO.status = 1 ";
			query = session.createQuery(searchQuery);
			resourceList = query.list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - resourcesWithAnchorDate() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - resourcesWithAnchorDate() - Ends");
		return resourceList;
	}

	/**
	 * Study consent section have the consent review sub section which contain the
	 * share data permission allows the admin to specify if as part of the Consent
	 * process, participants need to be asked to provide permission for their
	 * response data to be shared with 3rd parties and consent document review
	 * section is section is meant for the admin to confirm the content seen by
	 * users in the Review Terms (Consent Document) screen on the mobile app In
	 * e-consent section The admin sees the elements of the e-consent form as
	 * provided to the user in the mobile app
	 * 
	 * @author BTC
	 * @param Object , {@link ConsentBo}
	 * @param Object , {@link SessionObject}
	 * @param String , customStudyId in {@link StudyBo}
	 * @return {@link ConsentBo}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ConsentBo saveOrCompleteConsentReviewDetails(ConsentBo consentBo, SessionObject sesObj,
			String customStudyId) {
		logger.info("INFO: StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: Starts");
		Session session = null;
		StudySequenceBo studySequence = null;
		List<ConsentInfoBo> consentInfoList = null;
		String content = "";
		String activitydetails = "";
		String activity = "";
		StudyVersionBo studyVersionBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			// check whether the consentinfo is saved for this study or not, if
			// not update
			if (consentBo.getId() != null) {
				consentBo.setModifiedOn(FdahpStudyDesignerUtil.getFormattedDate(
						FdahpStudyDesignerUtil.getCurrentDateTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
				consentBo.setModifiedBy(sesObj.getUserId());
			} else {
				consentBo.setCreatedOn(FdahpStudyDesignerUtil.getFormattedDate(
						FdahpStudyDesignerUtil.getCurrentDateTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
				consentBo.setCreatedBy(sesObj.getUserId());
			}

			// get the review content based on the version, studyId and visual
			// step
			if (consentBo != null && StringUtils.isNotEmpty(consentBo.getConsentDocType())
					&& consentBo.getConsentDocType().equalsIgnoreCase("Auto")) {
				query = session.createQuery(
						" from ConsentInfoBo CIBO where CIBO.studyId=" + consentBo.getStudyId() + " and CIBO.active=1");
				consentInfoList = query.list();
				if (consentInfoList != null && consentInfoList.size() > 0) {
					for (ConsentInfoBo consentInfo : consentInfoList) {
						content += "<span style=&#34;font-size:20px;&#34;><strong>" + consentInfo.getDisplayTitle()
								+ "</strong></span><br/>"
								+ "<span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;>"
								+ consentInfo.getElaborated() + "</span><br/>";
					}
					consentBo.setConsentDocContent(content);
				}
			}

			if (consentBo.getType() != null
					&& consentBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
				studySequence = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, consentBo.getStudyId()).uniqueResult();
				if (studySequence != null) {
					studySequence.seteConsent(false);
				} else {
					studySequence = new StudySequenceBo();
					studySequence.seteConsent(false);
					studySequence.setStudyId(consentBo.getStudyId());

				}
				session.saveOrUpdate(studySequence);
			}
			session.saveOrUpdate(consentBo);
			if (customStudyId != null && !customStudyId.isEmpty()) {
				if (consentBo.getType() != null) {
					if (consentBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
						activity = "Study consentReview saved.";
						activitydetails = "Content saved for Consent Review Section. (Study ID = " + customStudyId
								+ ")";
					} else {
						query = session.getNamedQuery("getStudyByCustomStudyId")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId);
						query.setMaxResults(1);
						studyVersionBo = (StudyVersionBo) query.uniqueResult();
						if (studyVersionBo != null) {
							activity = "Study consentReview marked as completed.";
							activitydetails = "Consent Review section successfully checked for minimum content completeness and marked 'Completed'.  (Study ID = "
									+ customStudyId + ", Consent Document Version = "
									+ studyVersionBo.getConsentVersion() + ")";
						}
					}
				} else if (consentBo.getComprehensionTest() != null) {
					if (consentBo.getComprehensionTest()
							.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
						activity = "Study comprehension test saved.";
						activitydetails = "Content saved for Comprehension test Section. (Study ID = " + customStudyId
								+ ")";
					} else {
						query = session.getNamedQuery("getStudyByCustomStudyId")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId);
						query.setMaxResults(1);
						studyVersionBo = (StudyVersionBo) query.uniqueResult();
						if (studyVersionBo != null) {
							activity = "Study comprehension test marked as completed.";
							activitydetails = "Comprehension Test section successfully checked for minimum content completeness and marked 'Completed'.  (Study ID = "
									+ customStudyId + ")";
						}
					}
				}

			}
			auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
					"StudyDAOImpl - saveOrCompleteConsentReviewDetails");
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("INFO: StudyDAOImpl - saveOrCompleteConsentReviewDetails() :: Ends");
		return consentBo;
	}

	/**
	 * This method is used to Save or Done Checklist
	 * 
	 * @author BTC
	 * @param checklist , {@link Checklist}
	 * @return checklist Id
	 */
	@Override
	public Integer saveOrDoneChecklist(Checklist checklist) {
		logger.info("StudyDAOImpl - saveOrDoneChecklist() - Starts");
		Session session = null;
		Integer checklistId = 0;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (checklist.getChecklistId() == null) {
				checklistId = (Integer) session.save(checklist);
			} else {
				session.update(checklist);
				checklistId = checklist.getChecklistId();
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrDoneChecklist() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrDoneChecklist() - Ends");
		return checklistId;
	}

	/**
	 * Study consent can have 0 or more comprehension test questions.Admin can mark
	 * whether or not this is required for the study if required admin will need to
	 * add question text,answer options and correct option and minimum score
	 * 
	 * @author BTC
	 * @param Object , {@link ComprehensionTestQuestionBo}
	 * @return Object , {@link ComprehensionTestQuestionBo}
	 */
	@Override
	public ComprehensionTestQuestionBo saveOrUpdateComprehensionTestQuestion(
			ComprehensionTestQuestionBo comprehensionTestQuestionBo) {
		logger.info("StudyDAOImpl - saveOrUpdateComprehensionTestQuestion() - Starts");
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			session.saveOrUpdate(comprehensionTestQuestionBo);
			if (comprehensionTestQuestionBo.getId() != null && comprehensionTestQuestionBo.getResponseList() != null
					&& !comprehensionTestQuestionBo.getResponseList().isEmpty()) {
				String deleteQuery = "delete from comprehension_test_response where comprehension_test_question_id="
						+ comprehensionTestQuestionBo.getId();
				session.createSQLQuery(deleteQuery).executeUpdate();
				for (ComprehensionTestResponseBo comprehensionTestResponseBo : comprehensionTestQuestionBo
						.getResponseList()) {
					if (comprehensionTestResponseBo.getResponseOption() != null
							&& !comprehensionTestResponseBo.getResponseOption().isEmpty()) {
						if (comprehensionTestResponseBo.getComprehensionTestQuestionId() == null) {
							comprehensionTestResponseBo
									.setComprehensionTestQuestionId(comprehensionTestQuestionBo.getId());
						}
						session.save(comprehensionTestResponseBo);
					}
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateComprehensionTestQuestion() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateComprehensionTestQuestion() - Ends");
		return comprehensionTestQuestionBo;
	}

	/**
	 * Study consent section is mandatory in mobile section admin can add one or
	 * more consent items here.The consent item are two types which are research kit
	 * provided consents and custom defined consent items admin can create the
	 * custom consent items which are not available in research kit
	 * 
	 * @author BTC
	 * @param Object : {@link ConsentInfoBo}
	 * @param Object : {@link SessionObject}
	 * @param String : customStudyId in {@link StudyBo}
	 * @return {@link ConsentInfoBo}
	 */
	@Override
	public ConsentInfoBo saveOrUpdateConsentInfo(ConsentInfoBo consentInfoBo, SessionObject sesObj,
			String customStudyId) {
		logger.info("StudyDAOImpl - saveOrUpdateConsentInfo() - Starts");
		Session session = null;
		StudySequenceBo studySequence = null;
		String activitydetails = "";
		String activity = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (consentInfoBo.getType() != null) {
				studySequence = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, consentInfoBo.getStudyId()).uniqueResult();
				if (consentInfoBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
					consentInfoBo.setStatus(false);
					if (studySequence != null) {
						studySequence.setConsentEduInfo(false);
						if (studySequence.iseConsent()) {
							studySequence.seteConsent(false);
						}
						if (studySequence.isComprehensionTest()) {
							studySequence.setComprehensionTest(false);
						}
					} else {
						studySequence = new StudySequenceBo();
						studySequence.setConsentEduInfo(false);
						studySequence.setStudyId(consentInfoBo.getStudyId());

					}
				} else if (consentInfoBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
					consentInfoBo.setStatus(true);
					if (studySequence.iseConsent()) {
						studySequence.seteConsent(false);
					}
					if (studySequence.isComprehensionTest()) {
						studySequence.setComprehensionTest(false);
					}
				}
				session.saveOrUpdate(studySequence);
			}
			session.saveOrUpdate(consentInfoBo);
			if (consentInfoBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
				activity = "Consent section successfully checked for minimum content completeness.";
				// activitydetails =
				// "Consent section updated successfully (Study ID = "+customStudyId+").";
				activitydetails = "Consent section successfully checked for minimum content completeness and marked 'Done'. (Display Name = "
						+ consentInfoBo.getDisplayTitle() + ", Study ID = " + customStudyId + ")";
			} else {
				activity = "Consent section saved as draft.";
				activitydetails = "Content saved for Consent Section. (Display Name = "
						+ consentInfoBo.getDisplayTitle() + ", Study ID = " + customStudyId + ")";
			}
			auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
					"StudyDAOImpl - saveOrUpdateConsentInfo");
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateConsentInfo() - Error", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateConsentInfo() - Ends");
		return consentInfoBo;
	}

	/**
	 * Save or update eligibility test question and answer
	 *
	 * @param eligibilityTestBo , object of {@link EligibilityTestBo}
	 * @param studyId           , id of the {@link StudyBo}
	 * @param sesObj            , current {@link SessionObject} object
	 * @param customStudyId     , custom study id of {@link StudyBo}
	 * @return eligibilityTestId, Id of {@link EligibilityTestBo}
	 */
	@Override
	public Integer saveOrUpdateEligibilityTestQusAns(EligibilityTestBo eligibilityTestBo, Integer studyId,
			SessionObject sesObj, String customStudyId) {
		logger.info("StudyDAOImpl - saveOrUpdateEligibilityTestQusAns - Starts");
		Session session = null;
		Integer eligibilityTestId = 0;
		Transaction trans = null;
		String activity;
		String activitydetails;
		EligibilityTestBo saveEligibilityTestBo;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			trans = session.beginTransaction();
			StudySequenceBo studySequence = (StudySequenceBo) session
					.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
					.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyId).uniqueResult();
			if (studySequence != null && studySequence.isEligibility()) {
				studySequence.setEligibility(false);
				session.update(studySequence);
			}
			if (eligibilityTestBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
				activity = "EligibilityQus section done";
				activitydetails = customStudyId
						+ " -- EligibilityQus section done and eligible for mark as completed action";
				eligibilityTestBo.setStatus(true);
			} else {
				activity = "EligibilityQus section saved";
				activitydetails = customStudyId
						+ " -- EligibilityQus section saved but not eligible for mark as completed action untill unless it is DONE";
			}
			if (null != eligibilityTestBo.getId()) {
				saveEligibilityTestBo = (EligibilityTestBo) session.get(EligibilityTestBo.class,
						eligibilityTestBo.getId());
				session.evict(saveEligibilityTestBo);
				eligibilityTestBo.setUsed(saveEligibilityTestBo.isUsed());
			}
			session.saveOrUpdate(eligibilityTestBo);
			auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
					"StudyDAOImpl - saveOrUpdateEligibilityTestQusAns");
			eligibilityTestId = eligibilityTestBo.getId();
			trans.commit();
		} catch (Exception e) {
			if (null != trans)
				trans.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateEligibilityTestQusAns - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateEligibilityTestQusAns - Ends");
		return eligibilityTestId;
	}

	/**
	 * save or update content(title,description,image) for the Overview pages of the
	 * Study those pages will reflect on mobile overview screen
	 * 
	 * @author BTC
	 * @param studyPageBean , {@link StudyPageBean}
	 * @return {@link String}
	 */
	@Override
	public String saveOrUpdateOverviewStudyPages(StudyPageBean studyPageBean, SessionObject sesObj) {
		logger.info("StudyDAOImpl - saveOrUpdateOverviewStudyPages() - Starts");
		Session session = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		int titleLength = 0;
		StudySequenceBo studySequence = null;
		StudyBo studyBo = null;
		String activitydetails = "";
		String activity = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (StringUtils.isNotEmpty(studyPageBean.getStudyId())) {
				studyBo = (StudyBo) session.getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
						.setInteger("id", Integer.parseInt(studyPageBean.getStudyId())).uniqueResult();
				if (studyBo != null) {
					studyBo.setMediaLink(studyPageBean.getMediaLink());
					session.update(studyBo);
				}
				// fileArray based on pageId will save/update into particular
				// location
				titleLength = studyPageBean.getTitle().length;
				if (titleLength > 0) {
					// delete the pages which are deleted from front end
					String pageIdArr = null;
					for (int j = 0; j < studyPageBean.getPageId().length; j++) {
						if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getPageId()[j])) {
							if (j == 0)
								pageIdArr = studyPageBean.getPageId()[j];
							else
								pageIdArr = pageIdArr + "," + studyPageBean.getPageId()[j];
						}
					}
					if (pageIdArr != null)
						session.createQuery("delete from StudyPageBo where studyId=" + studyPageBean.getStudyId()
								+ " and pageId not in(" + pageIdArr + ")").executeUpdate();
					else
						session.createQuery("delete from StudyPageBo where studyId=" + studyPageBean.getStudyId())
								.executeUpdate();
					for (int i = 0; i < titleLength; i++) {
						StudyPageBo studyPageBo = null;
						if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getPageId()[i]))
							studyPageBo = (StudyPageBo) session
									.createQuery(
											"from StudyPageBo SPB where SPB.pageId=" + studyPageBean.getPageId()[i])
									.uniqueResult();

						if (studyPageBo == null)
							studyPageBo = new StudyPageBo();

						if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getPageId()[i])) {
							studyPageBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
							studyPageBo.setModifiedBy(studyPageBean.getUserId());
						} else {
							studyPageBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
							studyPageBo.setCreatedBy(studyPageBean.getUserId());
						}
						studyPageBo.setStudyId(FdahpStudyDesignerUtil.isEmpty(studyPageBean.getStudyId()) ? 0
								: Integer.parseInt(studyPageBean.getStudyId()));
						studyPageBo.setTitle(FdahpStudyDesignerUtil.isEmpty(studyPageBean.getTitle()[i]) ? null
								: studyPageBean.getTitle()[i]);
						studyPageBo
								.setDescription(FdahpStudyDesignerUtil.isEmpty(studyPageBean.getDescription()[i]) ? null
										: studyPageBean.getDescription()[i]);
						studyPageBo.setImagePath(FdahpStudyDesignerUtil.isEmpty(studyPageBean.getImagePath()[i]) ? null
								: studyPageBean.getImagePath()[i]);
						session.saveOrUpdate(studyPageBo);
					}
					studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID,
									Integer.parseInt(studyPageBean.getStudyId()))
							.uniqueResult();
					if (studySequence != null) {
						if (studyPageBean.getActionType() != null
								&& studyPageBean.getActionType()
										.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)
								&& !studySequence.isOverView()) {
							studySequence.setOverView(true);
						} else if (studyPageBean.getActionType() != null && studyPageBean.getActionType()
								.equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
							studySequence.setOverView(false);
						}
						session.update(studySequence);
					}
					message = auditLogDAO.updateDraftToEditedStatus(session, transaction, studyPageBean.getUserId(),
							FdahpStudyDesignerConstants.DRAFT_STUDY, Integer.parseInt(studyPageBean.getStudyId()));
					if (studyPageBean.getActionType().equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
						activity = "Study overview marked as Completed.";
						activitydetails = "Section validated for minimum completion required and marked as Completed. (Study ID = "
								+ studyBo.getCustomStudyId() + ")";
					} else {
						activity = "Study overview content saved as draft.";
						activitydetails = "Study overview content saved as draft. (Study ID = "
								+ studyBo.getCustomStudyId() + ")";
					}
					auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
							"StudyDAOImpl - saveOrUpdateOverviewStudyPages");
				}

			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateOverviewStudyPages() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateOverviewStudyPages() - Ends");
		return message;
	}

	/**
	 * This method is used to save or update the Study Resource
	 *
	 * @author BTC
	 * @param resourceBO , {@link ResourceBO}
	 * @return resource Id
	 */
	@Override
	public Integer saveOrUpdateResource(ResourceBO resourceBO) {
		logger.info("StudyDAOImpl - saveOrUpdateResource() - Starts");
		Session session = null;
		Integer resourceId = 0;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (null == resourceBO.getId()) {
				resourceId = (Integer) session.save(resourceBO);
			} else {
				session.update(resourceBO);
				resourceId = resourceBO.getId();
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateResource() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateResource() - Ends");
		return resourceId;
	}

	/**
	 * This method captures basic information about the study basic info like Study
	 * ID, Study name, Study full name, Study Category, Research Sponsor,Data
	 * Partner, Estimated Duration in weeks/months/years, Study Tagline, Study
	 * Description, Study website, Study Type, giving permissions to all super admin
	 * 
	 * @author BTC
	 * @param StudyBo , {@link StudyBo}
	 * @return {@link String}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String saveOrUpdateStudy(StudyBo studyBo, SessionObject sessionObject) {
		logger.info("StudyDAOImpl - saveOrUpdateStudy() - Starts");
		Session session = null;
		String message = FdahpStudyDesignerConstants.SUCCESS;
		StudyPermissionBO studyPermissionBO = null;
		Integer studyId = null;
		Integer userId = null;
		StudySequenceBo studySequenceBo = null;
		StudyBo dbStudyBo = null;
		String activitydetails = "";
		String activity = "";
		List<Integer> userSuperAdminList = null;
		try {
			System.out.println("StudyDAOImpl.saveOrUpdateStudy() ==>> "+studyBo.getResearchSponsor());
			userId = studyBo.getUserId();
			String appId="";
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();

			if (studyBo.getId() == null) {
				studyBo.setCreatedBy(studyBo.getUserId());
				appId=studyBo.getAppId().toUpperCase();
				studyBo.setAppId(appId);
				studyBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
				studyId = (Integer) session.save(studyBo);

				studyPermissionBO = new StudyPermissionBO();
				studyPermissionBO.setUserId(userId);
				studyPermissionBO.setStudyId(studyId);
				studyPermissionBO.setViewPermission(true);
				session.save(studyPermissionBO);

				// give permission to all super admin Start
				query = session
						.createSQLQuery("Select upm.user_id from user_permission_mapping upm where upm.permission_id = "
								+ FdahpStudyDesignerConstants.ROLE_SUPERADMIN);
				userSuperAdminList = query.list();
				if (userSuperAdminList != null && !userSuperAdminList.isEmpty()) {
					for (Integer superAdminId : userSuperAdminList) {
						if (null != userId && !userId.equals(superAdminId)) {
							studyPermissionBO = new StudyPermissionBO();
							studyPermissionBO.setUserId(superAdminId);
							studyPermissionBO.setStudyId(studyId);
							studyPermissionBO.setViewPermission(true);
							session.save(studyPermissionBO);
						}
					}
				}
				// give permission to all super admin End

				// creating table to keep track of each section of study
				// completed or not
				studySequenceBo = new StudySequenceBo();
				studySequenceBo.setStudyId(studyId);
				session.save(studySequenceBo);

				// Phase2a code Start
				// create one record in anchordate_type table to give user to use enrollmentdate
				AnchorDateTypeBo anchorDateTypeBo = new AnchorDateTypeBo();
				anchorDateTypeBo.setCustomStudyId(studyBo.getCustomStudyId());
				anchorDateTypeBo.setStudyId(studyId);
				anchorDateTypeBo.setName(FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE);
				anchorDateTypeBo.setHasAnchortypeDraft(1);
				session.save(anchorDateTypeBo);
				// Phase2a code End
			} else {
				dbStudyBo = (StudyBo) session.getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
						.setInteger("id", studyBo.getId()).uniqueResult();
				if (dbStudyBo != null) {
					dbStudyBo.setCustomStudyId(studyBo.getCustomStudyId());
					dbStudyBo.setName(studyBo.getName());
					dbStudyBo.setFullName(studyBo.getFullName());
					dbStudyBo.setCategory(studyBo.getCategory());
					dbStudyBo.setResearchSponsor(studyBo.getResearchSponsor());
					dbStudyBo.setDataPartner(studyBo.getDataPartner());
					dbStudyBo.setTentativeDuration(studyBo.getTentativeDuration());
					dbStudyBo.setTentativeDurationWeekmonth(studyBo.getTentativeDurationWeekmonth());
					dbStudyBo.setDescription(studyBo.getDescription());
					dbStudyBo.setStudyTagLine(studyBo.getStudyTagLine());
					dbStudyBo.setStudyWebsite(studyBo.getStudyWebsite());
					dbStudyBo.setInboxEmailAddress(studyBo.getInboxEmailAddress());
					dbStudyBo.setType(studyBo.getType());
					dbStudyBo.setThumbnailImage(studyBo.getThumbnailImage());
					dbStudyBo.setModifiedBy(studyBo.getUserId());
					dbStudyBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
					dbStudyBo.setAppId(studyBo.getAppId());
					// dbStudyBo.setOrgId(studyBo.getOrgId());
					session.update(dbStudyBo);

				}
			}
			studySequenceBo = (StudySequenceBo) session.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
					.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId()).uniqueResult();
			if (studySequenceBo != null) {
				if (!studySequenceBo.isBasicInfo() && StringUtils.isNotEmpty(studyBo.getButtonText())
						&& studyBo.getButtonText().equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
					studySequenceBo.setBasicInfo(true);
					activity = "Study marked as completed.";
					activitydetails = "Study basic info section successsfully checked for minimum content completeness and marked 'Completed'. (Study ID = "
							+ studyBo.getCustomStudyId() + ")";
				} else if (StringUtils.isNotEmpty(studyBo.getButtonText())
						&& studyBo.getButtonText().equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
					activity = "Content saved as draft.";
					activitydetails = "Study basic info content saved as draft. (Study ID = "
							+ studyBo.getCustomStudyId() + ")";
					studySequenceBo.setBasicInfo(false);
				}
				session.update(studySequenceBo);
			}
			message = auditLogDAO.updateDraftToEditedStatus(session, transaction, studyBo.getUserId(),
					FdahpStudyDesignerConstants.DRAFT_STUDY, studyBo.getId());
			auditLogDAO.saveToAuditLog(session, transaction, sessionObject, activity, activitydetails,
					"StudyDAOImpl - saveOrUpdateSubAdmin");

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateSubAdmin() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateSubAdmin() - Ends");
		return message;
	}

	/**
	 * Save or update eligibility of study
	 *
	 * @author BTC
	 *
	 * @param eligibilityBo , {@link EligibilityBo}
	 * @return {@link String} , the status FdahpStudyDesignerConstants.SUCCESS or
	 *         FdahpStudyDesignerConstants.FAILURE
	 * @exception Exception
	 */
	@Override
	public String saveOrUpdateStudyEligibilty(EligibilityBo eligibilityBo, SessionObject sesObj, String customStudyId) {
		logger.info("StudyDAOImpl - saveOrUpdateStudyEligibilty() - Starts");
		String result = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		StudySequenceBo studySequence = null;
		EligibilityBo eligibilityBoUpdate = null;
		Boolean updateFlag = false;
		String activitydetails = "";
		String activity = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (null != eligibilityBo) {
				if (eligibilityBo.getId() != null) {
					eligibilityBoUpdate = (EligibilityBo) session.getNamedQuery("getEligibiltyById")
							.setInteger("id", eligibilityBo.getId()).uniqueResult();
					eligibilityBoUpdate.setEligibilityMechanism(eligibilityBo.getEligibilityMechanism());
					eligibilityBoUpdate.setInstructionalText(eligibilityBo.getInstructionalText());
					eligibilityBoUpdate.setModifiedOn(eligibilityBo.getModifiedOn());
					eligibilityBoUpdate.setModifiedBy(eligibilityBo.getModifiedBy());
					updateFlag = true;
				} else {
					eligibilityBoUpdate = eligibilityBo;
				}

				studySequence = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, eligibilityBo.getStudyId()).uniqueResult();
				if (studySequence != null) {
					if (eligibilityBo.getActionType() != null && ("mark").equals(eligibilityBo.getActionType())
							&& !studySequence.isEligibility()) {
						studySequence.setEligibility(true);
					} else if (eligibilityBo.getActionType() != null
							&& !("mark").equals(eligibilityBo.getActionType())) {
						studySequence.setEligibility(false);
					}
					session.saveOrUpdate(eligibilityBoUpdate);
				}
				if (("mark").equals(eligibilityBo.getActionType())) {
					activity = "Study eligibility marked as Completed.";
					activitydetails = "Section validated for minimum completion required and marked as Completed. (Study ID = "
							+ customStudyId + ")";
				} else {
					activity = "Study eligibility content saved as draft.";
					activitydetails = "Study eligibility content saved as draft. (Study ID = " + customStudyId + ")";
				}
				auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
						"StudyDAOImpl - saveOrUpdateStudyEligibilty");
				result = auditLogDAO.updateDraftToEditedStatus(session, transaction,
						(updateFlag ? eligibilityBo.getModifiedBy() : eligibilityBo.getCreatedBy()),
						FdahpStudyDesignerConstants.DRAFT_STUDY, eligibilityBo.getStudyId());
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateStudyEligibilty() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateStudyEligibilty() - Ends");
		return result;
	}

	/**
	 * Save or update settings and admins of study
	 *
	 * @author BTC
	 * @param studyBo     , {@link studyBo}
	 * @param sesObj      , {@link SessionObject}
	 * @param userIds
	 * @param permissions
	 * @param projectLead
	 * @return {@link String} , SUCCESS or FAILURE
	 * @exception Exception
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public String saveOrUpdateStudySettings(StudyBo studyBo, SessionObject sesObj, String userIds, String permissions,
			String projectLead) {
		logger.info("StudyDAOImpl - saveOrUpdateStudySettings() - Starts");
		String result = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		StudySequenceBo studySequence = null;
		StudyBo study = null;
		String activitydetails = "";
		String activity = "";
		String[] userId = null;
		String[] viewPermission = null;
		StudyPermissionBO studyPermissionBO = null;
		List<Integer> superAdminUserIds = null;
		String deleteExceptIds = "";
		String forceLogoutUserIds = "";
		List<Integer> deletingUserIds = new ArrayList<Integer>();
		List<Integer> deletingUserIdsWithoutLoginUser = new ArrayList<Integer>();
		boolean ownUserForceLogout = false;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (null != studyBo) {
				if (studyBo.getId() != null) {
					study = (StudyBo) session.createQuery("from StudyBo where id=" + studyBo.getId()).uniqueResult();
					studySequence = (StudySequenceBo) session
							.createQuery("from StudySequenceBo where studyId=" + studyBo.getId()).uniqueResult();
					if (study != null && studySequence != null) {
						// validation of anchor date
						updateAnchordateForEnrollmentDate(study, studyBo, session, transaction);
						// validation of anchor date
						study.setPlatform(studyBo.getPlatform());
						study.setAllowRejoin(studyBo.getAllowRejoin());
						study.setEnrollingParticipants(studyBo.getEnrollingParticipants());
						study.setRetainParticipant(studyBo.getRetainParticipant());
						study.setAllowRejoin(studyBo.getAllowRejoin());
						study.setAllowRejoinText(studyBo.getAllowRejoinText());
						study.setModifiedBy(studyBo.getUserId());
						study.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
						// Phase2a code Start(adding enrollment date as anchor date(yes/no))
						study.setEnrollmentdateAsAnchordate(studyBo.isEnrollmentdateAsAnchordate());
						// Phase2a code end
						session.saveOrUpdate(study);

						// setting true to setting admins
						// setting and admins section of Study completed or not
						// updated in study sequence table(to keep track of each
						// section of study completed or not)
						if (StringUtils.isNotEmpty(studyBo.getButtonText())
								&& studyBo.getButtonText()
										.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)
								&& !studySequence.isSettingAdmins()) {
							studySequence.setSettingAdmins(true);
						} else if (StringUtils.isNotEmpty(studyBo.getButtonText())
								&& studyBo.getButtonText().equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
							studySequence.setSettingAdmins(false);
						}
						session.update(studySequence);

						// Phase2a code Start(adding enrollment date as anchor date(yes/no))
						if (studyBo.isEnrollmentdateAsAnchordate()) {
							session.createSQLQuery("UPDATE anchordate_type set has_anchortype_draft=1 where study_id='"
									+ study.getId() + "' and has_anchortype_draft=0 and name='"
									+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "'").executeUpdate();
						} else {
							session.createSQLQuery("UPDATE anchordate_type set has_anchortype_draft=0 where study_id='"
									+ study.getId() + "' and has_anchortype_draft=1 and name='"
									+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "'").executeUpdate();
						}
						// Phase2a code end
					}
				}

				/* admin section starts */
				deleteExceptIds = userIds;
				superAdminUserIds = getSuperAdminUserIds();
				if (superAdminUserIds != null) {
					for (Integer id : superAdminUserIds) {
						if (deleteExceptIds == "") {
							deleteExceptIds = String.valueOf(id);
						} else {
							deleteExceptIds += "," + id;
						}
					}
				}
				query = session.createSQLQuery(" SELECT sp.user_id FROM study_permission sp WHERE sp.user_id NOT IN ("
						+ deleteExceptIds + ") AND sp.study_id =" + studyBo.getId());
				deletingUserIds = query.list();
				deletingUserIdsWithoutLoginUser.addAll(deletingUserIds);
				if (deletingUserIds.contains(sesObj.getUserId())) {
					ownUserForceLogout = true;
					deletingUserIdsWithoutLoginUser.remove(sesObj.getUserId());
				}

				if (null != deletingUserIdsWithoutLoginUser && !deletingUserIdsWithoutLoginUser.isEmpty()) {
					for (Integer id : deletingUserIdsWithoutLoginUser) {
						if (forceLogoutUserIds == "") {
							forceLogoutUserIds = String.valueOf(id);
						} else {
							forceLogoutUserIds += "," + id;
						}
					}
				}

				if (!"".equals(userIds) && !"".equals(permissions)) {
					userId = userIds.split(",");
					viewPermission = permissions.split(",");

					if (null != deletingUserIds && !deletingUserIds.isEmpty()) {
						query = session.createSQLQuery(" DELETE FROM study_permission WHERE user_id NOT IN ("
								+ deleteExceptIds + ") AND study_id =" + studyBo.getId());
						query.executeUpdate();
					}

					for (int i = 0; i < userId.length; i++) {
						query = session.createQuery(" FROM StudyPermissionBO UBO WHERE UBO.userId = " + userId[i]
								+ " AND studyId =" + studyBo.getId());
						studyPermissionBO = (StudyPermissionBO) query.uniqueResult();
						if (null != studyPermissionBO) {
							Boolean flag = false;
							if (studyPermissionBO.isViewPermission() != "1".equals(viewPermission[i]) ? true : false) {
								studyPermissionBO.setViewPermission("1".equals(viewPermission[i]) ? true : false);
								flag = true;
							}
							if ((studyPermissionBO.getProjectLead() != null ? studyPermissionBO.getProjectLead()
									: 0) != (projectLead.equals(userId[i]) ? 1 : 0)) {
								studyPermissionBO.setProjectLead(projectLead.equals(userId[i]) ? 1 : 0);
								flag = true;
							}
							if (flag) {
								session.update(studyPermissionBO);
								if (sesObj.getUserId().equals(Integer.parseInt(userId[i]))) {
									ownUserForceLogout = true;
								} else {
									if (forceLogoutUserIds == "") {
										forceLogoutUserIds = userId[i];
									} else {
										forceLogoutUserIds += "," + userId[i];
									}
								}
							}
						} else {
							studyPermissionBO = new StudyPermissionBO();
							studyPermissionBO.setStudyId(studyBo.getId());
							studyPermissionBO.setUserId(Integer.parseInt(userId[i]));
							studyPermissionBO.setViewPermission("1".equals(viewPermission[i]) ? true : false);
							studyPermissionBO.setProjectLead(projectLead.equals(userId[i]) ? 1 : 0);
							session.save(studyPermissionBO);

							UserBO user = null;
							boolean present = false;
							Set<UserPermissions> permissionSet = null;
							query = session.createQuery(" FROM UserBO UBO where UBO.userId = " + userId[i]);
							user = (UserBO) query.uniqueResult();
							if (user != null) {
								String oldPermissions = "";
								for (UserPermissions temp : user.getPermissions()) {
									if (oldPermissions == "") {
										oldPermissions = "'" + temp.getPermissions() + "'";
									} else {
										oldPermissions += ",'" + temp.getPermissions() + "'";
									}
									if (temp.getPermissions().equals("ROLE_MANAGE_STUDIES")) {
										present = true;
									}
								}

								if (!present) {
									if (oldPermissions == "") {
										oldPermissions = "'ROLE_MANAGE_STUDIES'";
									} else {
										oldPermissions += ",'ROLE_MANAGE_STUDIES'";
									}
									permissionSet = new HashSet<UserPermissions>(
											session.createQuery("FROM UserPermissions UPBO WHERE UPBO.permissions IN ("
													+ oldPermissions + ")").list());
									user.setPermissionList(permissionSet);
									user.setModifiedBy(study.getUserId());
									user.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
									session.update(user);
								}
							}

							if (sesObj.getUserId().equals(Integer.parseInt(userId[i]))) {
								ownUserForceLogout = true;
							} else {
								if (forceLogoutUserIds == "") {
									forceLogoutUserIds = userId[i];
								} else {
									forceLogoutUserIds += "," + userId[i];
								}
							}
						}
					}
				} else {
					if (null != deletingUserIds && !deletingUserIds.isEmpty()) {
						query = session.createSQLQuery(" DELETE FROM study_permission WHERE user_id NOT IN ("
								+ deleteExceptIds + ") AND study_id =" + studyBo.getId());
						query.executeUpdate();
					}
				}

				if (forceLogoutUserIds != "") {
					query = session.createSQLQuery(
							" UPDATE users SET force_logout = 'Y' WHERE user_id IN (" + forceLogoutUserIds + ")");
					query.executeUpdate();
				}

				/* admin section ends */

				result = auditLogDAO.updateDraftToEditedStatus(session, transaction, studyBo.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_STUDY, studyBo.getId());

				if (result.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS) && ownUserForceLogout) {
					result = FdahpStudyDesignerConstants.WARNING;
				}

				if (study != null) {
					if (studyBo.getButtonText().equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
						activity = "Study settings marked as Completed.";
						activitydetails = "Section validated for minimum completion required and marked as Completed. (Study ID = "
								+ study.getCustomStudyId() + ")";
					} else {
						activity = "Study settings content saved as draft.";
						activitydetails = "Study settings content saved as draft. (Study ID = "
								+ study.getCustomStudyId() + ")";
					}
				}
				auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
						"StudyDAOImpl - saveOrUpdateStudySettings");
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveOrUpdateStudySettings() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveOrUpdateStudySettings() - Ends");
		return result;
	}

	/**
	 * save the Study Overview Page By PageId
	 * 
	 * @author BTC
	 * @param String , studyId
	 * @return {@link Integer}
	 */
	public Integer saveOverviewStudyPageById(String studyId) {
		Integer pageId = 0;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (StringUtils.isNotEmpty(studyId)) {
				StudyPageBo studyPageBo = new StudyPageBo();
				studyPageBo.setStudyId(Integer.parseInt(studyId));
				pageId = (Integer) session.save(studyPageBo);

			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - deleteOverviewStudyPageById() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - deleteOverviewStudyPageById() - Ends");

		return pageId;
	}

	/**
	 * This method is used to save the resource notification
	 * 
	 * @author BTC
	 * @param notificationBO , {@link NotificationBO}
	 * @return message, Success/Failure message.
	 */
	@Override
	public String saveResourceNotification(NotificationBO notificationBO, boolean notiFlag) {
		logger.info("StudyDAOImpl - saveResourceNotification() - Starts");
		Session session = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (!notiFlag) {
				session.save(notificationBO);
			} else {
				session.update(notificationBO);
			}
			transaction.commit();
			message = FdahpStudyDesignerConstants.SUCCESS;
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - saveResourceNotification() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - saveResourceNotification() - Ends");
		return message;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	/**
	 * create live study from existing study
	 * 
	 * @author BTC
	 * @param studyBo , {@link StudyBo}
	 * @param session , {@link Session}
	 * @return {@link String}
	 */
	@SuppressWarnings("unchecked")
	public String studyDraftCreation(StudyBo studyBo, Session session) {
		logger.info("StudyDAOImpl - studyDraftCreation() - Starts");
		List<StudyPageBo> studyPageBo = null;
		List<StudyPermissionBO> studyPermissionList = null;
		EligibilityBo eligibilityBo = null;
		List<ResourceBO> resourceBOList = null;
		StudyVersionBo studyVersionBo = null;
		StudyVersionBo newstudyVersionBo = null;
		boolean flag = true;
		String message = FdahpStudyDesignerConstants.FAILURE;
		List<QuestionnaireBo> questionnaires = null;
		List<ActiveTaskBo> activeTasks = null;
		String searchQuery = "";
		QuestionReponseTypeBo questionReponseTypeBo = null;
		List<String> objectList = null;
		List<String> questionnarieShorttitleList = null;
		List<AnchorDateTypeBo> anchorDateTypeList = null;
		Map<Integer, Integer> anchordateoldnewMapList = new HashMap<>();
		try {
			if (studyBo != null) {
				logger.info("StudyDAOImpl - studyDraftCreation() getStudyByCustomStudyId- Starts");
				// if already launch and study hasStudyDraft()==1 , then update
				// and create draft version , otherwise not
				query = session.getNamedQuery("getStudyByCustomStudyId")
						.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
				query.setMaxResults(1);
				studyVersionBo = (StudyVersionBo) query.uniqueResult();
				logger.info("StudyDAOImpl - studyDraftCreation() getStudyByCustomStudyId- Ends");
				if (studyVersionBo != null && (studyBo.getHasStudyDraft().equals(0))) {
					flag = false;
				}
				if (flag) {
					// version update in study_version table
					if (studyVersionBo != null) {
						logger.info("StudyDAOImpl - studyDraftCreation() updateStudyVersion- Starts");
						// update all studies to archive (live as 2)
						// pass customstudyId and making all study status belongs to same customstudyId
						// as 2(archive)
						query = session.getNamedQuery("updateStudyVersion")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
						query.executeUpdate();
						logger.info("StudyDAOImpl - studyDraftCreation() updateStudyVersion- Ends");

						newstudyVersionBo = SerializationUtils.clone(studyVersionBo);
						newstudyVersionBo.setStudyVersion(studyVersionBo.getStudyVersion() + 0.1f);
						if (studyBo.getHasConsentDraft().equals(1)) {
							newstudyVersionBo.setConsentVersion(studyVersionBo.getConsentVersion() + 0.1f);
						}
						newstudyVersionBo.setVersionId(null);
						session.save(newstudyVersionBo);
					} else {
						newstudyVersionBo = new StudyVersionBo();
						newstudyVersionBo.setCustomStudyId(studyBo.getCustomStudyId());
						newstudyVersionBo.setActivityVersion(1.0f);
						newstudyVersionBo.setConsentVersion(1.0f);
						newstudyVersionBo.setStudyVersion(1.0f);
						session.save(newstudyVersionBo);
					}

					// create new Study and made it draft study
					StudyBo studyDreaftBo = SerializationUtils.clone(studyBo);
					if (newstudyVersionBo != null) {
						studyDreaftBo.setVersion(newstudyVersionBo.getStudyVersion());
						studyDreaftBo.setLive(1);
					}
					studyDreaftBo.setId(null);
					session.save(studyDreaftBo);

					// clone of Study Permission

					studyPermissionList = session.createQuery("from StudyPermissionBO where studyId=" + studyBo.getId())
							.list();
					if (studyPermissionList != null) {
						logger.info("StudyDAOImpl - studyDraftCreation() StudyPermissionBO- Starts");
						for (StudyPermissionBO permissionBO : studyPermissionList) {
							StudyPermissionBO studyPermissionBO = SerializationUtils.clone(permissionBO);
							studyPermissionBO.setStudyId(studyDreaftBo.getId());
							studyPermissionBO.setStudyPermissionId(null);
							session.save(studyPermissionBO);
						}
						logger.info("StudyDAOImpl - studyDraftCreation() StudyPermissionBO- Ends");
					}

					// clone of Study Sequence
					StudySequenceBo studySequence = (StudySequenceBo) session
							.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId()).uniqueResult();
					StudySequenceBo newStudySequenceBo = SerializationUtils.clone(studySequence);
					newStudySequenceBo.setStudyId(studyDreaftBo.getId());
					newStudySequenceBo.setStudySequenceId(null);
					session.save(newStudySequenceBo);

					// clone of Over View section
					query = session.createQuery("from StudyPageBo where studyId=" + studyBo.getId());
					studyPageBo = query.list();
					if (studyPageBo != null && !studyPageBo.isEmpty()) {
						for (StudyPageBo pageBo : studyPageBo) {
							StudyPageBo subPageBo = SerializationUtils.clone(pageBo);
							subPageBo.setStudyId(studyDreaftBo.getId());
							subPageBo.setPageId(null);
							session.save(subPageBo);
						}
					}

					// clone of Eligibility
					query = session.getNamedQuery("getEligibiltyByStudyId")
							.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
					eligibilityBo = (EligibilityBo) query.uniqueResult();
					if (eligibilityBo != null) {
						EligibilityBo bo = SerializationUtils.clone(eligibilityBo);
						bo.setStudyId(studyDreaftBo.getId());
						bo.setId(null);
						session.save(bo);
						List<EligibilityTestBo> eligibilityTestList = null;
						eligibilityTestList = session.getNamedQuery("EligibilityTestBo.findByEligibilityId")
								.setInteger(FdahpStudyDesignerConstants.ELIGIBILITY_ID, eligibilityBo.getId()).list();
						if (eligibilityTestList != null && !eligibilityTestList.isEmpty()) {
							List<Integer> eligibilityTestIds = new ArrayList<>();
							for (EligibilityTestBo eligibilityTestBo : eligibilityTestList) {
								eligibilityTestIds.add(eligibilityTestBo.getId());
								EligibilityTestBo newEligibilityTestBo = SerializationUtils.clone(eligibilityTestBo);
								newEligibilityTestBo.setId(null);
								newEligibilityTestBo.setEligibilityId(bo.getId());
								newEligibilityTestBo.setUsed(true);
								session.save(newEligibilityTestBo);
							}
							if (!eligibilityTestIds.isEmpty())
								session.createSQLQuery("UPDATE eligibility_test set is_used='Y' where id in("
										+ StringUtils.join(eligibilityTestIds, ",") + ")").executeUpdate();
						}
					}

					// clone anchor date data
					/*
					 * searchQuery = " FROM AnchorDateTypeBo RBO WHERE RBO.studyId=" +
					 * studyBo.getId() + " AND RBO.hasAnchortypeDraft = 1 ORDER BY RBO.id DESC";
					 * query = session.createQuery(searchQuery); anchorDateTypeList = query.list();
					 * if(anchorDateTypeList!=null && !anchorDateTypeList.isEmpty()) {
					 * logger.info("StudyDAOImpl - studyDraftCreation() AnchorDateTypeBo- Starts");
					 * Integer oldAnchorId = null; for (AnchorDateTypeBo bo : anchorDateTypeList) {
					 * oldAnchorId = bo.getId(); AnchorDateTypeBo anchorDateTypeBo =
					 * SerializationUtils .clone(bo);
					 * anchorDateTypeBo.setStudyId(studyDreaftBo.getId());
					 * anchorDateTypeBo.setId(null);
					 * anchorDateTypeBo.setVersion(studyDreaftBo.getVersion());
					 * session.save(anchorDateTypeBo); anchordateoldnewMapList.put(oldAnchorId,
					 * anchorDateTypeBo.getId()); }
					 * logger.info("StudyDAOImpl - studyDraftCreation() AnchorDateTypeBo- Ends"); }
					 */

					// clone of resources
					searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId=" + studyBo.getId()
							+ " AND RBO.status = 1 ORDER BY RBO.createdOn DESC ";
					query = session.createQuery(searchQuery);
					resourceBOList = query.list();
					if (resourceBOList != null && !resourceBOList.isEmpty()) {
						logger.info("StudyDAOImpl - studyDraftCreation() ResourceBO- Starts");
						for (ResourceBO bo : resourceBOList) {
							ResourceBO resourceBO = SerializationUtils.clone(bo);
							resourceBO.setStudyId(studyDreaftBo.getId());
							resourceBO.setId(null);
							// If resource is a target anchor date or not,yes then update new anchordate Id
//							if(resourceBO.getAnchorDateId()!=null) {
//								resourceBO.setAnchorDateId(anchordateoldnewMapList.get(bo.getAnchorDateId()));
//						     }
							// If resource is a target anchor date or not,yes then update anchordate Id
							session.save(resourceBO);
						}
						logger.info("StudyDAOImpl - studyDraftCreation() ResourceBO- Ends");
					}

					// If Questionnaire updated flag -1 then update(clone)
					if (studyVersionBo == null || (studyBo.getHasQuestionnaireDraft() != null
							&& studyBo.getHasQuestionnaireDraft().equals(1))) {
						// Questionarries
						query = session.getNamedQuery("getQuestionariesByStudyId")
								.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
						questionnaires = query.list();
						if (questionnaires != null && !questionnaires.isEmpty()) {
							// short title taking updating to archived which
							// have change start
							questionnarieShorttitleList = new ArrayList<>();
							for (QuestionnaireBo questionnaireBo : questionnaires) {
								if (questionnaireBo.getIsChange() != null && questionnaireBo.getIsChange().equals(1)) {
									questionnarieShorttitleList.add("'" + questionnaireBo.getShortTitle() + "'");
								}
							}
							if (questionnarieShorttitleList != null && !questionnarieShorttitleList.isEmpty()) {
								logger.info(
										"StudyDAOImpl - studyDraftCreation() Questionnarie update is_live=2- Starts");
								queryString = "update questionnaires SET is_live=2 where short_title IN("
										+ StringUtils.join(questionnarieShorttitleList, ",")
										+ ") and is_live=1 and custom_study_id='" + studyBo.getCustomStudyId() + "'";
								query = session.createSQLQuery(queryString);
								query.executeUpdate();
								logger.info("StudyDAOImpl - studyDraftCreation() Questionnarie update is_live=2- Ends");
							}
							// short title taking updating to archived which
							// have change end
							for (QuestionnaireBo questionnaireBo : questionnaires) {
								logger.info("StudyDAOImpl - studyDraftCreation() Questionnarie creation- Starts");
								// creating in study Activity version
								StudyActivityVersionBo studyActivityVersionBo = new StudyActivityVersionBo();
								studyActivityVersionBo.setCustomStudyId(studyBo.getCustomStudyId());
								studyActivityVersionBo.setStudyVersion(newstudyVersionBo.getStudyVersion());
								studyActivityVersionBo.setActivityType("Q");
								studyActivityVersionBo.setShortTitle(questionnaireBo.getShortTitle());
								// is there any change in questionnarie
								if (questionnaireBo.getIsChange() != null && questionnaireBo.getIsChange().equals(1)) {
									Float questionnarieversion = questionnaireBo.getVersion();
									QuestionnaireBo newQuestionnaireBo = SerializationUtils.clone(questionnaireBo);
									newQuestionnaireBo.setId(null);
									newQuestionnaireBo.setStudyId(studyDreaftBo.getId());
									newQuestionnaireBo.setCreatedBy(0);
									newQuestionnaireBo.setModifiedBy(0);
									newQuestionnaireBo.setModifiedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
									if (studyVersionBo == null) {
										newQuestionnaireBo.setVersion(1.0f);
										questionnaireBo.setVersion(1.0f);
									} else {
										if (questionnarieversion.equals(0f)) {
											questionnaireBo.setVersion(1.0f);
											newQuestionnaireBo.setVersion(1.0f);
										} else {
											newQuestionnaireBo.setVersion(questionnaireBo.getVersion() + 0.1f);
											questionnaireBo.setVersion(questionnaireBo.getVersion() + 0.1f);
										}
									}
									newQuestionnaireBo.setLive(1);
									newQuestionnaireBo.setCustomStudyId(studyBo.getCustomStudyId());
									// If questionnaire is a target anchor date or not,yes then update new
									// anchordate Id
//									if(questionnaireBo.getScheduleType()!=null && questionnaireBo.getScheduleType().equals(FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE)) {
//										if(questionnaireBo.getAnchorDateId()!=null) {
//											newQuestionnaireBo.setAnchorDateId(anchordateoldnewMapList.get(questionnaireBo.getAnchorDateId()));
//										}
//									}
									// If questionnaire is a target anchor date or not,yes then update anchordate Id
									session.save(newQuestionnaireBo);
									questionnaireBo.setIsChange(0);
									questionnaireBo.setLive(0);
									session.update(questionnaireBo);
									/** Schedule Purpose creating draft Start **/
									if (StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
										if (questionnaireBo.getFrequency().equalsIgnoreCase(
												FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
											searchQuery = "From QuestionnaireCustomScheduleBo QCSBO where QCSBO.questionnairesId="
													+ questionnaireBo.getId();
											List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleList = session
													.createQuery(searchQuery).list();
											if (questionnaireCustomScheduleList != null
													&& !questionnaireCustomScheduleList.isEmpty()) {
												logger.info(
														"StudyDAOImpl - studyDraftCreation() Questionnarie manual schedule update - Starts");
												for (QuestionnaireCustomScheduleBo customScheduleBo : questionnaireCustomScheduleList) {
													QuestionnaireCustomScheduleBo newCustomScheduleBo = SerializationUtils
															.clone(customScheduleBo);
													newCustomScheduleBo.setQuestionnairesId(newQuestionnaireBo.getId());
													newCustomScheduleBo.setId(null);
													session.save(newCustomScheduleBo);
												}
												// updating draft version of
												// schecule to Yes
												session.createQuery(
														"UPDATE QuestionnaireCustomScheduleBo set used=true where questionnairesId="
																+ questionnaireBo.getId())
														.executeUpdate();
												logger.info(
														"StudyDAOImpl - studyDraftCreation() Questionnarie manual schedule update - Ends");
											}
										} else {
											searchQuery = "From QuestionnairesFrequenciesBo QFBO where QFBO.questionnairesId="
													+ questionnaireBo.getId();
											List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList = session
													.createQuery(searchQuery).list();
											if (questionnairesFrequenciesList != null
													&& !questionnairesFrequenciesList.isEmpty()) {
												logger.info(
														"StudyDAOImpl - studyDraftCreation() Questionnarie except manual schedule other update - Starts");
												for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo : questionnairesFrequenciesList) {
													QuestionnairesFrequenciesBo newQuestionnairesFrequenciesBo = SerializationUtils
															.clone(questionnairesFrequenciesBo);
													newQuestionnairesFrequenciesBo
															.setQuestionnairesId(newQuestionnaireBo.getId());
													newQuestionnairesFrequenciesBo.setId(null);
													session.save(newQuestionnairesFrequenciesBo);
												}
												logger.info(
														"StudyDAOImpl - studyDraftCreation() Questionnarie except manual schedule other update - Ends");
											}
										}
									}
									/** Schedule Purpose creating draft End **/
									/** Content purpose creating draft Start **/

									List<Integer> destinationList = new ArrayList<>();
									Map<Integer, Integer> destionationMapList = new HashMap<>();

									List<QuestionnairesStepsBo> existedQuestionnairesStepsBoList = null;
									List<QuestionnairesStepsBo> newQuestionnairesStepsBoList = new ArrayList<>();
									List<QuestionResponseSubTypeBo> existingQuestionResponseSubTypeList = new ArrayList<>();
									List<QuestionResponseSubTypeBo> newQuestionResponseSubTypeList = new ArrayList<>();

									List<QuestionReponseTypeBo> existingQuestionResponseTypeList = new ArrayList<>();
									List<QuestionReponseTypeBo> newQuestionResponseTypeList = new ArrayList<>();

									query = session.getNamedQuery("getQuestionnaireStepSequenceNo")
											.setInteger("questionnairesId", questionnaireBo.getId());
									existedQuestionnairesStepsBoList = query.list();
									if (existedQuestionnairesStepsBoList != null
											&& !existedQuestionnairesStepsBoList.isEmpty()) {
										for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
											Integer destionStep = questionnairesStepsBo.getDestinationStep();
											if (destionStep.equals(0)) {
												destinationList.add(-1);
											} else {
												for (int i = 0; i < existedQuestionnairesStepsBoList.size(); i++) {
													if (existedQuestionnairesStepsBoList.get(i).getStepId() != null
															&& destionStep.equals(existedQuestionnairesStepsBoList
																	.get(i).getStepId())) {
														destinationList.add(i);
														break;
													}
												}
											}
											destionationMapList.put(questionnairesStepsBo.getSequenceNo(),
													questionnairesStepsBo.getStepId());
										}
										for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
											if (StringUtils.isNotEmpty(questionnairesStepsBo.getStepType())) {
												QuestionnairesStepsBo newQuestionnairesStepsBo = SerializationUtils
														.clone(questionnairesStepsBo);
												newQuestionnairesStepsBo
														.setQuestionnairesId(newQuestionnaireBo.getId());
												newQuestionnairesStepsBo.setStepId(null);
												session.save(newQuestionnairesStepsBo);
												if (questionnairesStepsBo.getStepType().equalsIgnoreCase(
														FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
													logger.info(
															"StudyDAOImpl - studyDraftCreation() Questionnarie instruction step - Starts");
													InstructionsBo instructionsBo = (InstructionsBo) session
															.getNamedQuery("getInstructionStep")
															.setInteger("id",
																	questionnairesStepsBo.getInstructionFormId())
															.uniqueResult();
													if (instructionsBo != null) {
														InstructionsBo newInstructionsBo = SerializationUtils
																.clone(instructionsBo);
														newInstructionsBo.setId(null);
														session.save(newInstructionsBo);

														// updating new
														// InstructionId
														newQuestionnairesStepsBo
																.setInstructionFormId(newInstructionsBo.getId());
													}
													logger.info(
															"StudyDAOImpl - studyDraftCreation() Questionnarie instruction step - Ends");
												} else if (questionnairesStepsBo.getStepType()
														.equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
													logger.info(
															"StudyDAOImpl - studyDraftCreation() Questionnarie Qustion step - Starts");
													QuestionsBo questionsBo = (QuestionsBo) session
															.getNamedQuery("getQuestionStep")
															.setInteger("stepId",
																	questionnairesStepsBo.getInstructionFormId())
															.uniqueResult();
													if (questionsBo != null) {
														// Question response
														// subType
														List<QuestionResponseSubTypeBo> questionResponseSubTypeList = session
																.getNamedQuery("getQuestionSubResponse")
																.setInteger("responseTypeId", questionsBo.getId())
																.list();
														List<QuestionConditionBranchBo> questionConditionBranchList = session
																.getNamedQuery("getQuestionConditionBranchList")
																.setInteger("questionId", questionsBo.getId()).list();

														// Question response
														// Type
														questionReponseTypeBo = (QuestionReponseTypeBo) session
																.getNamedQuery("getQuestionResponse")
																.setInteger("questionsResponseTypeId",
																		questionsBo.getId())
																.setMaxResults(1).uniqueResult();

														QuestionsBo newQuestionsBo = SerializationUtils
																.clone(questionsBo);
														newQuestionsBo.setId(null);
														// update source anchordate id start
														// if(questionsBo.getUseAnchorDate() &&
														// questionsBo.getAnchorDateId()!=null)
														// newQuestionsBo.setAnchorDateId(anchordateoldnewMapList.get(questionsBo.getAnchorDateId()));
														// update source anchor date id end
														session.save(newQuestionsBo);

														// Question response
														// Type
														if (questionReponseTypeBo != null) {

															QuestionReponseTypeBo newQuestionReponseTypeBo = SerializationUtils
																	.clone(questionReponseTypeBo);
															newQuestionReponseTypeBo.setResponseTypeId(null);
															newQuestionReponseTypeBo
																	.setQuestionsResponseTypeId(newQuestionsBo.getId());
															newQuestionReponseTypeBo.setOtherDestinationStepId(null);
															session.save(newQuestionReponseTypeBo);

															if (questionReponseTypeBo.getOtherType() != null
																	&& StringUtils.isNotEmpty(
																			questionReponseTypeBo.getOtherType())
																	&& questionReponseTypeBo.getOtherType()
																			.equals("on")) {
																existingQuestionResponseTypeList
																		.add(questionReponseTypeBo);
																newQuestionResponseTypeList
																		.add(newQuestionReponseTypeBo);
															}
														}

														// Question Condition
														// branching logic
														if (questionConditionBranchList != null
																&& !questionConditionBranchList.isEmpty()) {
															for (QuestionConditionBranchBo questionConditionBranchBo : questionConditionBranchList) {
																QuestionConditionBranchBo newQuestionConditionBranchBo = SerializationUtils
																		.clone(questionConditionBranchBo);
																newQuestionConditionBranchBo.setConditionId(null);
																newQuestionConditionBranchBo
																		.setQuestionId(newQuestionsBo.getId());
																session.save(newQuestionConditionBranchBo);
															}
														}

														// Question response
														// subType
														if (questionResponseSubTypeList != null
																&& !questionResponseSubTypeList.isEmpty()) {
															existingQuestionResponseSubTypeList
																	.addAll(questionResponseSubTypeList);

															for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionResponseSubTypeList) {
																QuestionResponseSubTypeBo newQuestionResponseSubTypeBo = SerializationUtils
																		.clone(questionResponseSubTypeBo);
																newQuestionResponseSubTypeBo
																		.setResponseSubTypeValueId(null);
																newQuestionResponseSubTypeBo
																		.setResponseTypeId(newQuestionsBo.getId());
																newQuestionResponseSubTypeBo.setDestinationStepId(null);
																session.save(newQuestionResponseSubTypeBo);
																newQuestionResponseSubTypeList
																		.add(newQuestionResponseSubTypeBo);
															}
														}

														// updating new
														// InstructionId
														newQuestionnairesStepsBo
																.setInstructionFormId(newQuestionsBo.getId());
													}
													logger.info(
															"StudyDAOImpl - studyDraftCreation() Questionnarie Qustion step - Ends");
												} else if (questionnairesStepsBo.getStepType()
														.equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
													logger.info(
															"StudyDAOImpl - studyDraftCreation() Questionnarie Form step - Starts");
													FormBo formBo = (FormBo) session.getNamedQuery("getFormBoStep")
															.setInteger("stepId",
																	questionnairesStepsBo.getInstructionFormId())
															.uniqueResult();
													if (formBo != null) {
														FormBo newFormBo = SerializationUtils.clone(formBo);
														newFormBo.setFormId(null);
														session.save(newFormBo);

														List<FormMappingBo> formMappingBoList = session
																.getNamedQuery("getFormByFormId")
																.setInteger("formId", formBo.getFormId()).list();
														if (formMappingBoList != null && !formMappingBoList.isEmpty()) {
															for (FormMappingBo formMappingBo : formMappingBoList) {
																FormMappingBo newMappingBo = SerializationUtils
																		.clone(formMappingBo);
																newMappingBo.setFormId(newFormBo.getFormId());
																newMappingBo.setId(null);

																QuestionsBo questionsBo = (QuestionsBo) session
																		.getNamedQuery("getQuestionByFormId")
																		.setInteger("formId",
																				formMappingBo.getQuestionId())
																		.uniqueResult();
																if (questionsBo != null) {
																	// Question
																	// response
																	// subType
																	List<QuestionResponseSubTypeBo> questionResponseSubTypeList = session
																			.getNamedQuery("getQuestionSubResponse")
																			.setInteger("responseTypeId",
																					questionsBo.getId())
																			.list();

																	// Question
																	// response
																	// Type
																	questionReponseTypeBo = (QuestionReponseTypeBo) session
																			.getNamedQuery("getQuestionResponse")
																			.setInteger("questionsResponseTypeId",
																					questionsBo.getId())
																			.setMaxResults(1).uniqueResult();

																	QuestionsBo newQuestionsBo = SerializationUtils
																			.clone(questionsBo);
																	newQuestionsBo.setId(null);
																	// update source anchordate id start
																	// if(questionsBo.getUseAnchorDate() &&
																	// questionsBo.getAnchorDateId()!=null)
																	// newQuestionsBo.setAnchorDateId(anchordateoldnewMapList.get(questionsBo.getAnchorDateId()));
																	// update source anchor date id end
																	session.save(newQuestionsBo);

																	// Question
																	// response
																	// Type
																	if (questionReponseTypeBo != null) {
																		QuestionReponseTypeBo newQuestionReponseTypeBo = SerializationUtils
																				.clone(questionReponseTypeBo);
																		newQuestionReponseTypeBo
																				.setResponseTypeId(null);
																		newQuestionReponseTypeBo
																				.setQuestionsResponseTypeId(
																						newQuestionsBo.getId());
																		session.save(newQuestionReponseTypeBo);
																	}

																	// Question
																	// response
																	// subType
																	if (questionResponseSubTypeList != null
																			&& !questionResponseSubTypeList.isEmpty()) {
																		for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionResponseSubTypeList) {
																			QuestionResponseSubTypeBo newQuestionResponseSubTypeBo = SerializationUtils
																					.clone(questionResponseSubTypeBo);
																			newQuestionResponseSubTypeBo
																					.setResponseSubTypeValueId(null);
																			newQuestionResponseSubTypeBo
																					.setResponseTypeId(
																							newQuestionsBo.getId());
																			session.save(newQuestionResponseSubTypeBo);
																		}
																	}

																	// adding
																	// questionId
																	newMappingBo.setQuestionId(newQuestionsBo.getId());
																	session.save(newMappingBo);
																}

															}
														}
														// updating new formId
														newQuestionnairesStepsBo
																.setInstructionFormId(newFormBo.getFormId());

													}
													logger.info(
															"StudyDAOImpl - studyDraftCreation() Questionnarie Form step - Ends");
												}
												session.update(newQuestionnairesStepsBo);
												newQuestionnairesStepsBoList.add(newQuestionnairesStepsBo);
											}
										}
									}
									if (destinationList != null && !destinationList.isEmpty()) {
										for (int i = 0; i < destinationList.size(); i++) {
											int desId = 0;
											if (destinationList.get(i) != -1) {
												desId = newQuestionnairesStepsBoList.get(destinationList.get(i))
														.getStepId();
											}
											newQuestionnairesStepsBoList.get(i).setDestinationStep(desId);
											session.update(newQuestionnairesStepsBoList.get(i));
										}
									}
									List<Integer> sequenceSubTypeList = new ArrayList<>();
									List<Integer> destinationResList = new ArrayList<>();
									if (existingQuestionResponseSubTypeList != null
											&& !existingQuestionResponseSubTypeList.isEmpty()) {
										for (QuestionResponseSubTypeBo questionResponseSubTypeBo : existingQuestionResponseSubTypeList) {
											if (questionResponseSubTypeBo.getDestinationStepId() == null) {
												sequenceSubTypeList.add(null);
											} else if (questionResponseSubTypeBo.getDestinationStepId() != null
													&& questionResponseSubTypeBo.getDestinationStepId().equals(0)) {
												sequenceSubTypeList.add(-1);
											} else {
												if (existedQuestionnairesStepsBoList != null
														&& !existedQuestionnairesStepsBoList.isEmpty()) {
													for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
														if (questionResponseSubTypeBo.getDestinationStepId() != null
																&& questionResponseSubTypeBo.getDestinationStepId()
																		.equals(questionnairesStepsBo.getStepId())) {
															sequenceSubTypeList
																	.add(questionnairesStepsBo.getSequenceNo());
															break;
														}
													}

												}
											}
										}
									}
									if (sequenceSubTypeList != null && !sequenceSubTypeList.isEmpty()) {
										for (int i = 0; i < sequenceSubTypeList.size(); i++) {
											Integer desId = null;
											if (sequenceSubTypeList.get(i) == null) {
												desId = null;
											} else if (sequenceSubTypeList.get(i).equals(-1)) {
												desId = 0;
											} else {
												for (QuestionnairesStepsBo questionnairesStepsBo : newQuestionnairesStepsBoList) {
													if (sequenceSubTypeList.get(i)
															.equals(questionnairesStepsBo.getSequenceNo())) {
														desId = questionnairesStepsBo.getStepId();
														break;
													}
												}
											}
											destinationResList.add(desId);
										}
										for (int i = 0; i < destinationResList.size(); i++) {
											newQuestionResponseSubTypeList.get(i)
													.setDestinationStepId(destinationResList.get(i));
											session.update(newQuestionResponseSubTypeList.get(i));
										}
									}

									// for other type , update the destination in questionresponsetype table
									/** start **/
									List<Integer> sequenceTypeList = new ArrayList<>();
									List<Integer> destinationResTypeList = new ArrayList<>();
									if (existingQuestionResponseTypeList != null
											&& !existingQuestionResponseTypeList.isEmpty()) {
										for (QuestionReponseTypeBo questionResponseTypeBo : existingQuestionResponseTypeList) {
											if (questionResponseTypeBo.getOtherDestinationStepId() == null) {
												sequenceTypeList.add(null);
											} else if (questionResponseTypeBo.getOtherDestinationStepId() != null
													&& questionResponseTypeBo.getOtherDestinationStepId().equals(0)) {
												sequenceTypeList.add(-1);
											} else {
												if (existedQuestionnairesStepsBoList != null
														&& !existedQuestionnairesStepsBoList.isEmpty()) {
													for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
														if (questionResponseTypeBo.getOtherDestinationStepId() != null
																&& questionResponseTypeBo.getOtherDestinationStepId()
																		.equals(questionnairesStepsBo.getStepId())) {
															sequenceTypeList.add(questionnairesStepsBo.getSequenceNo());
															break;
														}
													}

												}
											}
										}
									}
									if (sequenceTypeList != null && !sequenceTypeList.isEmpty()) {
										for (int i = 0; i < sequenceTypeList.size(); i++) {
											Integer desId = null;
											if (sequenceTypeList.get(i) == null) {
												desId = null;
											} else if (sequenceTypeList.get(i).equals(-1)) {
												desId = 0;
											} else {
												for (QuestionnairesStepsBo questionnairesStepsBo : newQuestionnairesStepsBoList) {
													if (sequenceTypeList.get(i)
															.equals(questionnairesStepsBo.getSequenceNo())) {
														desId = questionnairesStepsBo.getStepId();
														break;
													}
												}
											}
											destinationResTypeList.add(desId);
										}
										for (int i = 0; i < destinationResTypeList.size(); i++) {
											newQuestionResponseTypeList.get(i)
													.setOtherDestinationStepId(destinationResTypeList.get(i));
											session.update(newQuestionResponseTypeList.get(i));
										}
									}
									/*** end ***/
									studyActivityVersionBo.setActivityVersion(newQuestionnaireBo.getVersion());
									/** Content purpose creating draft End **/
								} else {
									studyActivityVersionBo.setActivityVersion(questionnaireBo.getVersion());
								}
								session.save(studyActivityVersionBo);
								logger.info("StudyDAOImpl - studyDraftCreation() Questionnarie creation- Ends");
							}
							// Executing draft version to 0
						} // If Questionarries updated flag -1 then update End

					} // In Questionnarie change or not

					// which are already in live those are deleted in draft to
					// making update those questionnarie to archived and make it
					// inactive(status=0)
					StringBuilder subString = new StringBuilder();
					subString.append("select CONCAT('");
					subString.append('"');
					subString.append("',shortTitle,'");
					subString.append('"');
					subString.append("') from QuestionnaireBo where active=0 and studyId=" + studyBo.getId()
							+ " and shortTitle is NOT NULL");
					query = session.createQuery(subString.toString());
					objectList = query.list();
					if (objectList != null && !objectList.isEmpty()) {
						String subQuery = "update questionnaires SET is_live=2,modified_date='"
								+ FdahpStudyDesignerUtil.getCurrentDateTime() + "', active=0 where short_title IN("
								+ StringUtils.join(objectList, ",") + ") and is_live=1 and custom_study_id='"
								+ studyBo.getCustomStudyId() + "'";
						query = session.createSQLQuery(subQuery);
						query.executeUpdate();
					}

					// In ActiveTask change or not Start
					// is there any change doing clone of active task
					if (studyVersionBo == null
							|| (studyBo.getHasActivetaskDraft() != null && studyBo.getHasActivetaskDraft().equals(1))) {
						// update all ActiveTasks to archive (live as 2)
						query = session.getNamedQuery("updateStudyActiveTaskVersion")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
						query.executeUpdate();

						// ActiveTasks
						query = session.getNamedQuery("ActiveTaskBo.getActiveTasksByByStudyId")
								.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
						activeTasks = query.list();
						if (activeTasks != null && !activeTasks.isEmpty()) {
							for (ActiveTaskBo activeTaskBo : activeTasks) {
								Float activeTaskversion = activeTaskBo.getVersion();
								ActiveTaskBo newActiveTaskBo = SerializationUtils.clone(activeTaskBo);
								newActiveTaskBo.setId(null);
								newActiveTaskBo.setStudyId(studyDreaftBo.getId());
								if (studyVersionBo == null) {
									newActiveTaskBo.setVersion(1.0f);
									activeTaskBo.setVersion(1.0f);
								} else if (activeTaskBo.getIsChange() != null && activeTaskBo.getIsChange().equals(1)) {
									if (activeTaskversion.equals(0f)) {
										activeTaskBo.setVersion(1.0f);
										newActiveTaskBo.setVersion(1.0f);
									} else {
										newActiveTaskBo.setVersion(activeTaskBo.getVersion() + 0.1f);
										activeTaskBo.setVersion(activeTaskBo.getVersion() + 0.1f);
									}
								} else {
									newActiveTaskBo.setVersion(activeTaskBo.getVersion());
									activeTaskBo.setVersion(activeTaskBo.getVersion());
								}
								newActiveTaskBo.setLive(1);
								newActiveTaskBo.setCustomStudyId(studyBo.getCustomStudyId());
								// If activeTask is a target anchor date or not,yes then update new anchordate
								// Id
//								if(activeTaskBo.getScheduleType()!=null && activeTaskBo.getScheduleType().equals(FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE)) {
//									if(activeTaskBo.getAnchorDateId()!=null) {
//										newActiveTaskBo.setAnchorDateId(anchordateoldnewMapList.get(activeTaskBo.getAnchorDateId()));
//									}
//								}
								// If activeTask is a target anchor date or not,yes then update anchordate Id
								session.save(newActiveTaskBo);

								/** Schedule Purpose creating draft Start **/
								if (StringUtils.isNotEmpty(activeTaskBo.getFrequency())) {
									if (activeTaskBo.getFrequency().equalsIgnoreCase(
											FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
										searchQuery = "From ActiveTaskCustomScheduleBo QCSBO where QCSBO.activeTaskId="
												+ activeTaskBo.getId();
										List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleList = session
												.createQuery(searchQuery).list();
										if (activeTaskCustomScheduleList != null
												&& !activeTaskCustomScheduleList.isEmpty()) {
											for (ActiveTaskCustomScheduleBo customScheduleBo : activeTaskCustomScheduleList) {
												ActiveTaskCustomScheduleBo newCustomScheduleBo = SerializationUtils
														.clone(customScheduleBo);
												newCustomScheduleBo.setActiveTaskId(newActiveTaskBo.getId());
												newCustomScheduleBo.setId(null);
												session.save(newCustomScheduleBo);
												session.update(customScheduleBo);
											}
											// updating draft version of
											// schedule to Yes
											session.createQuery(
													"UPDATE ActiveTaskCustomScheduleBo set used=true where activeTaskId="
															+ activeTaskBo.getId())
													.executeUpdate();
										}
									} else {
										searchQuery = "From ActiveTaskFrequencyBo QFBO where QFBO.activeTaskId="
												+ activeTaskBo.getId();
										List<ActiveTaskFrequencyBo> activeTaskFrequenciesList = session
												.createQuery(searchQuery).list();
										if (activeTaskFrequenciesList != null && !activeTaskFrequenciesList.isEmpty()) {
											for (ActiveTaskFrequencyBo activeTaskFrequenciesBo : activeTaskFrequenciesList) {
												ActiveTaskFrequencyBo newFrequenciesBo = SerializationUtils
														.clone(activeTaskFrequenciesBo);
												newFrequenciesBo.setActiveTaskId(newActiveTaskBo.getId());
												newFrequenciesBo.setId(null);
												session.save(newFrequenciesBo);
											}
										}
									}
								}
								/** Schedule Purpose creating draft End **/

								/** Content Purpose creating draft Start **/
								query = session.getNamedQuery("getAttributeListByActiveTAskId")
										.setInteger("activeTaskId", activeTaskBo.getId());
								List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBoList = query.list();
								if (activeTaskAtrributeValuesBoList != null
										&& !activeTaskAtrributeValuesBoList.isEmpty()) {
									for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo : activeTaskAtrributeValuesBoList) {
										ActiveTaskAtrributeValuesBo newActiveTaskAtrributeValuesBo = SerializationUtils
												.clone(activeTaskAtrributeValuesBo);
										newActiveTaskAtrributeValuesBo.setActiveTaskId(newActiveTaskBo.getId());
										newActiveTaskAtrributeValuesBo.setAttributeValueId(null);
										session.save(newActiveTaskAtrributeValuesBo);
									}

								}
								/** Content Purpose creating draft End **/
							}
							// Executing draft version to 0
							session.createQuery(
									"UPDATE ActiveTaskBo set live=0, isChange = 0 where studyId=" + studyBo.getId())
									.executeUpdate();
						} // Active TAsk End
					} // In ActiveTask change or not
						// Activities End

					// If Consent updated flag -1 then update
					query = session.createQuery("from ConsentBo CBO where CBO.studyId=" + studyBo.getId() + "");
					ConsentBo consentBo = (ConsentBo) query.uniqueResult();

					if (studyVersionBo == null || studyBo.getHasConsentDraft().equals(1)) {
						// update all consentBo to archive (live as 2)
						query = session.getNamedQuery("updateStudyConsentVersion")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
						query.executeUpdate();

						// update all consentInfoBo to archive (live as 2)
						query = session.getNamedQuery("updateStudyConsentInfoVersion")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
						query.executeUpdate();

						// If Consent updated flag -1 then update
						if (consentBo != null) {
							ConsentBo newConsentBo = SerializationUtils.clone(consentBo);
							newConsentBo.setId(null);
							newConsentBo.setStudyId(studyDreaftBo.getId());
							newConsentBo.setVersion(newstudyVersionBo.getConsentVersion());
							newConsentBo.setLive(1);
							newConsentBo.setCustomStudyId(studyBo.getCustomStudyId());
							session.save(newConsentBo);
						}
						query = session.getNamedQuery("getConsentInfoByStudyId")
								.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId());
						List<ConsentInfoBo> consentInfoBoList = query.list();
						if (consentInfoBoList != null && !consentInfoBoList.isEmpty()) {
							for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
								ConsentInfoBo newConsentInfoBo = SerializationUtils.clone(consentInfoBo);
								newConsentInfoBo.setId(null);
								newConsentInfoBo.setStudyId(studyDreaftBo.getId());
								newConsentInfoBo.setVersion(newstudyVersionBo.getConsentVersion());
								newConsentInfoBo.setCustomStudyId(studyBo.getCustomStudyId());
								newConsentInfoBo.setLive(1);
								session.save(newConsentInfoBo);
							}
						}
					}

					if (consentBo != null) {
						// Comprehension test Start
						if (StringUtils.isNotEmpty(consentBo.getNeedComprehensionTest()) && consentBo
								.getNeedComprehensionTest().equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
							List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
							List<Integer> comprehensionIds = new ArrayList<>();
							List<ComprehensionTestResponseBo> comprehensionTestResponseList = null;
							query = session.createQuery("From ComprehensionTestQuestionBo CTQBO where CTQBO.studyId="
									+ studyBo.getId() + " and CTQBO.active=1 order by CTQBO.sequenceNo asc");
							comprehensionTestQuestionList = query.list();
							if (comprehensionTestQuestionList != null && !comprehensionTestQuestionList.isEmpty()) {
								for (ComprehensionTestQuestionBo comprehensionTestQuestionBo : comprehensionTestQuestionList) {
									comprehensionIds.add(comprehensionTestQuestionBo.getId());
								}
								comprehensionTestResponseList = session
										.createQuery("From ComprehensionTestResponseBo CTRBO "
												+ "where CTRBO.comprehensionTestQuestionId IN("
												+ StringUtils.join(comprehensionIds, ",")
												+ ") order by comprehensionTestQuestionId")
										.list();
								for (ComprehensionTestQuestionBo comprehensionTestQuestionBo : comprehensionTestQuestionList) {
									ComprehensionTestQuestionBo newComprehensionTestQuestionBo = SerializationUtils
											.clone(comprehensionTestQuestionBo);
									newComprehensionTestQuestionBo.setStudyId(studyDreaftBo.getId());
									newComprehensionTestQuestionBo.setId(null);
									session.save(newComprehensionTestQuestionBo);
									if (comprehensionTestResponseList != null
											&& !comprehensionTestResponseList.isEmpty()) {
										for (ComprehensionTestResponseBo comprehensionTestResponseBo : comprehensionTestResponseList) {
											if (comprehensionTestQuestionBo.getId()
													.intValue() == comprehensionTestResponseBo
															.getComprehensionTestQuestionId().intValue()) {
												ComprehensionTestResponseBo newComprehensionTestResponseBo = SerializationUtils
														.clone(comprehensionTestResponseBo);
												newComprehensionTestResponseBo.setId(null);
												newComprehensionTestResponseBo.setComprehensionTestQuestionId(
														newComprehensionTestQuestionBo.getId());
												session.save(newComprehensionTestResponseBo);
											}
										}
									}
								}
							}

						}
						// Comprehension test End
					}

					// updating the edited study to draft
					if (studyDreaftBo != null && studyDreaftBo.getId() != null) {
						studyBo.setVersion(0f);
						studyBo.setHasActivetaskDraft(0);
						studyBo.setHasQuestionnaireDraft(0);
						studyBo.setHasConsentDraft(0);
						studyBo.setHasStudyDraft(0);
						studyBo.setLive(0);
						session.update(studyBo);

						// Updating Notification and Resources
						session.createQuery("UPDATE NotificationBO set customStudyId='" + studyBo.getCustomStudyId()
								+ "' where studyId=" + studyBo.getId()).executeUpdate();
						session.createQuery("UPDATE Checklist set customStudyId='" + studyBo.getCustomStudyId()
								+ "' where studyId=" + studyBo.getId()).executeUpdate();
					}
					message = FdahpStudyDesignerConstants.SUCCESS;
				}

			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - studyDraftCreation() - ERROR ", e);
		}
		logger.info("StudyDAOImpl - studyDraftCreation() - Ends");

		return message;
	}

	/**
	 * This method is to update status of Study
	 * 
	 * @author BTC
	 * @param string , studyId
	 * @param string , buttonText
	 * @param sesObj , {@link SessionObject}
	 * @return String, SUCCESS/FAILURE
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateStudyActionOnAction(String studyId, String buttonText, SessionObject sesObj) {
		logger.info("StudyDAOImpl - updateStudyActionOnAction() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		StudyBo studyBo = null;
		List<Integer> objectList = null;
		String activitydetails = "";
		String activity = "";
		StudyBo liveStudy = null;
		StudyVersionBo studyVersionBo = null;
		NotificationBO notificationBO = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(buttonText)) {
				studyBo = (StudyBo) session.getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
						.setInteger("id", Integer.parseInt(studyId)).uniqueResult();
				if (studyBo != null) {
					if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_PUBLISH)) {
						studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_PRE_PUBLISH);
						studyBo.setStudyPreActiveFlag(true);
						session.update(studyBo);
						message = FdahpStudyDesignerConstants.SUCCESS;
						activity = "Study Published as Upcoming.";
						activitydetails = "Study Published as Upcoming Study. (Study ID = " + studyBo.getCustomStudyId()
								+ ", Status = Pre-launch(Published))";
						// notification sent to gateway
						notificationBO = new NotificationBO();
						notificationBO.setStudyId(studyBo.getId());
						notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
						if (StringUtils.isNotEmpty(studyBo.getAppId()))
							notificationBO.setAppId(studyBo.getAppId());
						notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
						notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
						notificationBO.setNotificationScheduleType(FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
						notificationBO.setNotificationStatus(false);
						notificationBO.setCreatedBy(sesObj.getUserId());
						notificationBO
								.setNotificationText(FdahpStudyDesignerConstants.NOTIFICATION_UPCOMING_OR_ACTIVE_TEXT);
						notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
						notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
						notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
						notificationBO.setNotificationDone(true);
						session.save(notificationBO);
					} else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UNPUBLISH)) {
						studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH);
						studyBo.setStudyPreActiveFlag(false);
						session.update(studyBo);
						message = FdahpStudyDesignerConstants.SUCCESS;
						activity = "Study Unpublished.";
						activitydetails = "Study Unpublished as Upcoming Study.(Study ID = "
								+ studyBo.getCustomStudyId() + ", Status = Unpublished)";
					} else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)
							|| buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
						studyBo.setStudyPreActiveFlag(false);
						studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_ACTIVE);
						studyBo.setStudylunchDate(FdahpStudyDesignerUtil.getCurrentDateTime());
						session.update(studyBo);
						// getting Questionnaries based on StudyId
						query = session.createQuery("select ab.id"
								+ " from QuestionnairesFrequenciesBo a,QuestionnaireBo ab"
								+ " where a.questionnairesId=ab.id" + " and ab.studyId=:impValue"
								+ " and ab.frequency='" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME + "'"
								+ " and a.isLaunchStudy=1" + " and active=1"
								+ " and ab.shortTitle NOT IN(SELECT shortTitle from QuestionnaireBo WHERE active=1 AND live=1 AND customStudyId='"
								+ studyBo.getCustomStudyId() + "')");
						query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, Integer.valueOf(studyId));
						objectList = query.list();
						if (objectList != null && !objectList.isEmpty()) {
							query = session.createSQLQuery("update questionnaires ab SET ab.study_lifetime_start= '"
									+ studyBo.getStudylunchDate() + "' where id IN(" + StringUtils.join(objectList, ",")
									+ ")");
							query.executeUpdate();
						}
						// getting activeTasks based on StudyId
						query = session.createQuery("select ab.id" + " from ActiveTaskFrequencyBo a,ActiveTaskBo ab"
								+ " where a.activeTaskId=ab.id" + " and ab.studyId=:impValue" + " and ab.frequency='"
								+ FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME + "'" + " and a.isLaunchStudy=1"
								+ " and active=1"
								+ " and ab.shortTitle NOT IN(SELECT shortTitle from ActiveTaskBo WHERE active=1 AND live=1 AND customStudyId='"
								+ studyBo.getCustomStudyId() + "')");
						query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, Integer.valueOf(studyId));
						objectList = query.list();
						if (objectList != null && !objectList.isEmpty()) {
							query = session.createSQLQuery("update active_task ab SET ab.active_task_lifetime_start= '"
									+ studyBo.getStudylunchDate() + "' where id IN(" + StringUtils.join(objectList, ",")
									+ ")");
							query.executeUpdate();
						}
						message = FdahpStudyDesignerConstants.SUCCESS;
						// StudyDraft version creation
						message = studyDraftCreation(studyBo, session);
						if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
							if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)) {
								// notification text --
								activity = "Study Launched.";
								activitydetails = "Study successfully launched. (Study ID = "
										+ studyBo.getCustomStudyId() + ", Status = Launched)";
								// notification sent to gateway
								notificationBO = new NotificationBO();
								notificationBO.setStudyId(studyBo.getId());
								notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
								if (StringUtils.isNotEmpty(studyBo.getAppId()))
									notificationBO.setAppId(studyBo.getAppId());
								notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
								notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
								notificationBO.setNotificationScheduleType(
										FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
								notificationBO.setNotificationStatus(false);
								notificationBO.setCreatedBy(sesObj.getUserId());
								notificationBO.setNotificationText(
										FdahpStudyDesignerConstants.NOTIFICATION_UPCOMING_OR_ACTIVE_TEXT);
								notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
								notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
								notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
								notificationBO.setNotificationDone(true);
								session.save(notificationBO);
							} else {
								// notification text --
								activity = "Study Updates Published.";
								activitydetails = "Study version updated successfully. (Study ID = "
										+ studyBo.getCustomStudyId() + ", Status = version updates)";
							}
							// Update resource startdate and time based on
							// customStudyId
							session.createQuery("UPDATE NotificationBO set scheduleDate='"
									+ FdahpStudyDesignerUtil.getCurrentDate() + "', scheduleTime = '"
									+ FdahpStudyDesignerUtil.getCurrentTime() + "' where customStudyId='"
									+ studyBo.getCustomStudyId()
									+ "' and scheduleDate IS NULL and scheduleTime IS NULL and notificationType='"
									+ FdahpStudyDesignerConstants.NOTIFICATION_ST + "' and notificationSubType='"
									+ FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_RESOURCE
									+ "' and notificationScheduleType='"
									+ FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE + "'").executeUpdate();

							// Update activity startdate and time based on
							// customStudyId
							session.createQuery("UPDATE NotificationBO set scheduleDate='"
									+ FdahpStudyDesignerUtil.getCurrentDate() + "', scheduleTime = '"
									+ FdahpStudyDesignerUtil.getCurrentTime() + "' where customStudyId='"
									+ studyBo.getCustomStudyId()
									+ "' and scheduleDate IS NULL and scheduleTime IS NULL and notificationType='"
									+ FdahpStudyDesignerConstants.NOTIFICATION_ST + "' and notificationSubType='"
									+ FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ACTIVITY
									+ "' and notificationScheduleType='"
									+ FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE + "'").executeUpdate();

						} else {
							throw new IllegalStateException("Error in creating in Study draft");
						}
					} else {
						liveStudy = (StudyBo) session.getNamedQuery("getStudyLiveVersion")
								.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId())
								.uniqueResult();
						if (liveStudy != null) {
							liveStudy.setStudyPreActiveFlag(false);
							query = session.getNamedQuery("getStudyByCustomStudyId")
									.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, studyBo.getCustomStudyId());
							query.setMaxResults(1);
							studyVersionBo = (StudyVersionBo) query.uniqueResult();
							if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_PAUSE)) {
								// notification text --
								if (studyVersionBo != null) {
									activity = "Study paused.";
									activitydetails = "Study Paused. (Study ID = " + studyBo.getCustomStudyId()
											+ ", Study Version = " + studyVersionBo.getStudyVersion()
											+ ", Status = Paused)";
								}
								notificationBO = new NotificationBO();
								notificationBO.setStudyId(liveStudy.getId());
								notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
								if (StringUtils.isNotEmpty(studyBo.getAppId()))
									notificationBO.setAppId(studyBo.getAppId());
								notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
								notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
								notificationBO.setNotificationScheduleType(
										FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
								notificationBO.setNotificationStatus(false);
								notificationBO.setCreatedBy(sesObj.getUserId());
								notificationBO.setNotificationText(FdahpStudyDesignerConstants.NOTIFICATION_PAUSE_TEXT
										.replace("$customId", studyBo.getName()));
								notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
								notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
								notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
								notificationBO.setNotificationDone(true);
								session.save(notificationBO);

								liveStudy.setStatus(FdahpStudyDesignerConstants.STUDY_PAUSED);
								studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_PAUSED);
								studyBo.setStudyPreActiveFlag(false);
							} else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_RESUME)) {
								// notification text --
								activity = "Study Resumed.";
								activitydetails = "Study Resumed. (Study ID = " + studyBo.getCustomStudyId()
										+ ", Study Version = " + studyVersionBo.getStudyVersion()
										+ ", Status = Active)";
								notificationBO = new NotificationBO();
								notificationBO.setStudyId(liveStudy.getId());
								notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
								if (StringUtils.isNotEmpty(studyBo.getAppId()))
									notificationBO.setAppId(studyBo.getAppId());
								notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
								notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
								notificationBO.setNotificationScheduleType(
										FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
								notificationBO.setNotificationStatus(false);
								notificationBO.setCreatedBy(sesObj.getUserId());
								notificationBO.setNotificationText(FdahpStudyDesignerConstants.NOTIFICATION_RESUME_TEXT
										.replace("$customId", studyBo.getName()));
								notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
								notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
								notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
								notificationBO.setNotificationDone(true);
								session.save(notificationBO);
								liveStudy.setStatus(FdahpStudyDesignerConstants.STUDY_ACTIVE);
								studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_ACTIVE);
								studyBo.setStudyPreActiveFlag(false);
							} else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_DEACTIVATE)) {
								// notification text --
								liveStudy.setStatus(FdahpStudyDesignerConstants.STUDY_DEACTIVATED);
								activity = "Study Deactivated.";
								activitydetails = "Study Deactivated. (Study ID = " + studyBo.getCustomStudyId()
										+ ", Last Version = " + studyVersionBo.getStudyVersion()
										+ " ,Status = Deactivated)";
								notificationBO = new NotificationBO();
								notificationBO.setStudyId(liveStudy.getId());
								notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
								if (StringUtils.isNotEmpty(studyBo.getAppId()))
									notificationBO.setAppId(studyBo.getAppId());
								notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
								notificationBO.setNotificationSubType(FdahpStudyDesignerConstants.STUDY_EVENT);
								notificationBO.setNotificationScheduleType(
										FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
								notificationBO.setNotificationStatus(false);
								notificationBO.setCreatedBy(sesObj.getUserId());
								notificationBO
										.setNotificationText(FdahpStudyDesignerConstants.NOTIFICATION_DEACTIVATE_TEXT
												.replace("$customId", studyBo.getName()));
								notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
								notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
								notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
								notificationBO.setNotificationDone(true);
								session.save(notificationBO);
								studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_DEACTIVATED);
								studyBo.setStudyPreActiveFlag(false);
								session.update(studyBo);
							}
							session.update(liveStudy);
							message = FdahpStudyDesignerConstants.SUCCESS;
						}
					}
					if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS))
						session.update(studyBo);
				}
				auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity, activitydetails,
						"StudyDAOImpl - updateStudyActionOnAction");

			}
			transaction.commit();
		} catch (Exception e) {
			message = FdahpStudyDesignerConstants.FAILURE;
			transaction.rollback();
			logger.error("StudyDAOImpl - updateStudyActionOnAction() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - updateStudyActionOnAction() - Ends");
		return message;
	}

	/**
	 * This method is validate the activity(Active task/Questionnaire) done or not
	 * 
	 * @author BTC
	 * @param String , studyId
	 * @param String , action
	 * @return String, {SUCCESS/FAILURE}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String validateActivityComplete(String studyId, String action) {
		logger.info("StudyDAOImpl - validateActivityComplete() - Starts");
		Session session = null;
		boolean questionnarieFlag = true;
		boolean activeTaskEmpty = false;
		boolean questionnarieEmpty = false;
		boolean activeTaskFlag = true;
		List<ActiveTaskBo> completedactiveTasks = null;
		List<QuestionnaireBo> completedquestionnaires = null;
		StudySequenceBo studySequence = null;
		String message = FdahpStudyDesignerConstants.SUCCESS;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(action)) {
				// For checking active task or questionnaire done or not
				query = session.getNamedQuery("ActiveTaskBo.getActiveTasksByByStudyIdDone")
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, Integer.parseInt(studyId));
				completedactiveTasks = query.list();
				query = session.getNamedQuery("getQuestionariesByStudyIdDone")
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, Integer.parseInt(studyId));
				completedquestionnaires = query.list();
				studySequence = (StudySequenceBo) session.getNamedQuery("getStudySequenceByStudyId")
						.setInteger("studyId", Integer.parseInt(studyId)).uniqueResult();
				if ((completedactiveTasks != null && !completedactiveTasks.isEmpty())) {
					for (ActiveTaskBo activeTaskBo : completedactiveTasks) {
						if (!activeTaskBo.isAction()) {
							activeTaskFlag = false;
							break;
						}
					}
				} else {
					activeTaskEmpty = true;
				}
				if (completedquestionnaires != null && !completedquestionnaires.isEmpty()) {
					for (QuestionnaireBo questionnaireBo : completedquestionnaires) {
						if (questionnaireBo.getStatus() != null && !questionnaireBo.getStatus()) {
							questionnarieFlag = false;
							break;
						}
					}
				} else {
					questionnarieEmpty = true;
				}
				// questionnarieFlag, activeTaskFlag will be true, then only
				// will allow to mark as complete
				if (action.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTIVITY_TYPE_QUESTIONNAIRE)) {
					if (!questionnarieFlag) {
						message = FdahpStudyDesignerConstants.MARK_AS_COMPLETE_DONE_ERROR_MSG;
					} else if (studySequence.isStudyExcActiveTask() && (questionnarieEmpty && activeTaskEmpty)) {
						message = FdahpStudyDesignerConstants.ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG;
					}
				} else {
					if (!activeTaskFlag) {
						message = FdahpStudyDesignerConstants.MARK_AS_COMPLETE_DONE_ERROR_MSG;
					} else if (studySequence.isStudyExcQuestionnaries() && (questionnarieEmpty && activeTaskEmpty)) {
						message = FdahpStudyDesignerConstants.ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG;
					}
				}
			} else {
				message = FdahpStudyDesignerConstants.ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - validateActivityComplete() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - validateActivityComplete() - Ends");
		return message;
	}

	/**
	 * validate the date related to Activity and notification before do Publish (as
	 * Upcoming Study)/Launch/publish update of the study
	 * 
	 * @author BTC
	 * @param studyBo , {@link StudyBo}
	 * @param String  , buttonText
	 * @return {@link String}
	 */
	@SuppressWarnings("unchecked")
	public String validateDateForStudyAction(StudyBo studyBo, String buttonText) {
		boolean resourceFlag = true;
		boolean resourceAnchorFlag = true;
		boolean activitiesFalg = true;
		boolean questionarriesFlag = true;
		boolean notificationFlag = true;
		List<DynamicBean> dynamicList = null;
		List<DynamicFrequencyBean> dynamicFrequencyList = null;
		List<NotificationBO> notificationBOs = null;
		List<ResourceBO> resourceBOList = null;
		String searchQuery = "";
		Session session = null;
		String message = FdahpStudyDesignerConstants.SUCCESS;
		boolean isExists = false;
		List<Integer> anchorDateTypeIds = null;
		BigInteger anchorCount = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();

			if (!buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
				// getting based on custom start date resource list
				searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId=" + studyBo.getId()
						+ " AND RBO.status = 1 AND RBO.startDate IS NOT NULL ORDER BY RBO.createdOn DESC ";
				query = session.createQuery(searchQuery);
				resourceBOList = query.list();
				if (resourceBOList != null && !resourceBOList.isEmpty()) {
					for (ResourceBO resourceBO : resourceBOList) {
						boolean flag = false;
						String currentDate = FdahpStudyDesignerUtil.getCurrentDate();
						if (currentDate.equalsIgnoreCase(resourceBO.getStartDate())) {
							flag = true;
						} else {
							flag = FdahpStudyDesignerUtil.compareDateWithCurrentDateResource(resourceBO.getStartDate(),
									"yyyy-MM-dd");
						}
						if (!flag) {
							resourceFlag = false;
							break;
						}
					}
				}
			}
			if (!resourceFlag) {
				message = FdahpStudyDesignerConstants.RESOURCE_DATE_ERROR_MSG;
				return message;
			} else {
				// Ancordate Checking

//				searchQuery = " FROM ResourceBO RBO WHERE RBO.studyId="
//						+ studyBo.getId()
//						+ " AND RBO.resourceType = 1 AND RBO.status = 1 ";
//				query = session.createQuery(searchQuery);
//				resourceBOList = query.list();
				// if resources are available, checking anchor date in
				// questionnaires/form questionnaires
				// else default making true
//				if (resourceBOList != null && !resourceBOList.isEmpty()) {
//					// checking anchordate in questionnaires
//					searchQuery = "select count(q.use_anchor_date) from questions q,questionnaires_steps qsq,questionnaires qq  where q.id=qsq.instruction_form_id and qsq.step_type='Question' "
//							+ "and qsq.active=1 and qsq.questionnaires_id=qq.id and qq.study_id in(select s.id from studies s where s.custom_study_id='"
//							+ studyBo.getCustomStudyId()
//							+ "' and s.is_live=0) and qq.active=1 and q.use_anchor_date=1 and q.active=1;";
//					BigInteger count = (BigInteger) session.createSQLQuery(
//							searchQuery).uniqueResult();
//					if (count.intValue() > 0) {
//						isExists = true;
//					} else {
//						// checking anchordate in form questionnaires
//						String subQuery = "select count(q.use_anchor_date) from questions q,form_mapping fm,form f,questionnaires_steps qsf,questionnaires qq where q.id=fm.question_id and f.form_id=fm.form_id and f.active=1 "
//								+ "and f.form_id=qsf.instruction_form_id and qsf.step_type='Form' and qsf.questionnaires_id=qq.id and study_id in (select s.id from studies s where s.custom_study_id='"
//								+ studyBo.getCustomStudyId()
//								+ "' and s.is_live=0) and q.use_anchor_date=1 and q.active=1";
//						BigInteger subCount = (BigInteger) session
//								.createSQLQuery(subQuery).uniqueResult();
//						if (subCount != null && subCount.intValue() > 0) {
//							isExists = true;
//						}
//					}
//				} else {
//					isExists = true;
//				}
				/** anchor date test **/
//				String subQuery = "";
//				if(!studyBo.isEnrollmentdateAsAnchordate())
//					subQuery = " AND a.name!='"+FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE+"'";
//				query = session
//						.createSQLQuery("SELECT a.id FROM anchordate_type a WHERE a.study_id ="+studyBo.getId()+" AND a.has_anchortype_draft=1"+subQuery);
//				anchorDateTypeIds = query.list();
//				if(anchorDateTypeIds!=null && !anchorDateTypeIds.isEmpty()) {
//					int count = 0;
//					Boolean isAnchorExist = false;
//					for(Integer anchorId: anchorDateTypeIds) {
//					   searchQuery = "select count(*) from questionnaires q where q.schedule_type='"+FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE+"' and q.anchor_date_id="+anchorId;
//					   anchorCount = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
//						if (anchorCount == null || anchorCount.intValue()==0) {
//							searchQuery = "select count(*) from active_task a where a.schedule_type='"+FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE+"' and a.anchor_date_id="+anchorId;
//							anchorCount = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
//							if(anchorCount == null || anchorCount.intValue()==0) {
//								searchQuery = "select count(*) from resources r where r.anchor_date_id="+anchorId;
//								anchorCount = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
//								if(anchorCount != null && anchorCount.intValue()>0) 
//									isAnchorExist =true;
//							}else {
//								isAnchorExist = true;
//							}
//						}else {
//							isAnchorExist = true;
//						}
//						if(isAnchorExist) {
//							count++;
//							isAnchorExist = false;
//						}	
//					}
//					if(anchorDateTypeIds.size() == count)
//						isExists = true;
//				}else {
//					isExists = true;
//				}
//				if (!isExists)
//					resourceAnchorFlag = false;
//
//			}
				// Anchor date Checking
//			if (!resourceAnchorFlag) {
//				//message = FdahpStudyDesignerConstants.RESOURCE_ANCHOR_ERROR_MSG;
//				message = FdahpStudyDesignerConstants.ANCHOR_ERROR_MSG;
//				return message;
//			} else {
				// getting activeTasks based on StudyId
				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.DynamicBean(a.frequencyDate, a.frequencyTime)"
								+ " from ActiveTaskFrequencyBo a,ActiveTaskBo ab" + " where a.activeTaskId=ab.id"
								+ " and ab.active IS NOT NULL" + " and ab.active=1" + " and ab.studyId=:impValue"
								+ " and ab.frequency='" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME + "'"
								+ " and a.isLaunchStudy=false" + " and a.frequencyDate IS NOT NULL"
								+ " and a.frequencyTime IS NOT NULL"
								+ " and ab.shortTitle NOT IN(SELECT shortTitle from ActiveTaskBo WHERE active=1 AND live=1 AND customStudyId='"
								+ studyBo.getCustomStudyId() + "')");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
				dynamicList = query.list();
				if (dynamicList != null && !dynamicList.isEmpty()) {
					// checking active task which have scheduled for One time
					// expired or not
					for (DynamicBean obj : dynamicList) {
						if (obj.getDateTime() != null && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
								obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
							activitiesFalg = false;
							break;
						}
					}
				}

				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.DynamicBean(ab.activeTaskLifetimeStart, a.frequencyTime)"
								+ " from ActiveTaskFrequencyBo a,ActiveTaskBo ab" + " where a.activeTaskId=ab.id"
								+ " and ab.active IS NOT NULL" + " and ab.active=1" + " and ab.studyId=:impValue"
								+ " and ab.frequency not in('" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME
								+ "','" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE + "')"
								+ " and ab.activeTaskLifetimeStart IS NOT NULL" + " and a.frequencyTime IS NOT NULL"
								+ " and ab.shortTitle NOT IN(SELECT shortTitle from ActiveTaskBo WHERE active=1 AND live=1 AND customStudyId='"
								+ studyBo.getCustomStudyId() + "')");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
				dynamicList = query.list();
				if (dynamicList != null && !dynamicList.isEmpty()) {
					// checking active task which have scheduled not in One
					// time,Manually Schedule expired or not
					for (DynamicBean obj : dynamicList) {
						if (obj.getDateTime() != null && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
								obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
							activitiesFalg = false;
							break;
						}
					}
				}

				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.DynamicFrequencyBean(a.frequencyStartDate, a.frequencyTime)"
								+ " from ActiveTaskCustomScheduleBo a,ActiveTaskBo ab" + " where a.activeTaskId=ab.id"
								+ " and ab.active IS NOT NULL" + " and ab.active=1" + " and ab.studyId=:impValue"
								+ " and ab.frequency='" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE
								+ "'" + " and a.frequencyStartDate IS NOT NULL" + " and a.frequencyTime IS NOT NULL"
								+ " and a.used=false");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
				dynamicFrequencyList = query.list();
				if (dynamicFrequencyList != null && !dynamicFrequencyList.isEmpty()) {
					for (DynamicFrequencyBean obj : dynamicFrequencyList) {
						// checking active task which have scheduled for
						// "Manually Schedule" expired or not
						if (obj.getStartDate() != null && obj.getTime() != null) {
							String dateTime = obj.getStartDate() + " " + obj.getTime();
							if (!FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(dateTime,
									"yyyy-MM-dd HH:mm:ss")) {
								activitiesFalg = false;
								break;
							}
						}
					}
				}
			}
			if (!activitiesFalg) {
				message = FdahpStudyDesignerConstants.ACTIVETASK_DATE_ERROR_MSG;
				return message;
			} else {
				// getting Questionnaires based on StudyId
				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.DynamicBean(a.frequencyDate, a.frequencyTime)"
								+ " from QuestionnairesFrequenciesBo a,QuestionnaireBo ab"
								+ " where a.questionnairesId=ab.id" + " and ab.active=1" + " and ab.studyId=:impValue"
								+ " and ab.frequency='" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME + "'"
								+ " and a.frequencyDate IS NOT NULL" + " and a.frequencyTime IS NOT NULL"
								+ " and ab.shortTitle NOT IN(SELECT shortTitle from QuestionnaireBo WHERE active=1 AND live=1 AND customStudyId='"
								+ studyBo.getCustomStudyId() + "')");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
				dynamicList = query.list();
				if (dynamicList != null && !dynamicList.isEmpty()) {
					for (DynamicBean obj : dynamicList) {
						// checking Questionnaires which have scheduled for one
						// time expired or not
						if (obj.getDateTime() != null && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
								obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
							questionarriesFlag = false;
							break;
						}
					}
				}

				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.DynamicBean(ab.studyLifetimeStart, a.frequencyTime)"
								+ " from QuestionnairesFrequenciesBo a,QuestionnaireBo ab"
								+ " where a.questionnairesId=ab.id" + " and ab.active=1" + " and ab.studyId=:impValue"
								+ " and ab.frequency not in('" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME
								+ "','" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE + "')"
								+ " and ab.studyLifetimeStart IS NOT NULL" + " and a.frequencyTime IS NOT NULL"
								+ " and ab.shortTitle NOT IN(SELECT shortTitle from QuestionnaireBo WHERE active=1 AND live=1 AND customStudyId='"
								+ studyBo.getCustomStudyId() + "')");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
				dynamicList = query.list();
				if (dynamicList != null && !dynamicList.isEmpty()) {
					// checking Questionnaires which have scheduled not in one
					// time,manually schedule expired or not
					for (DynamicBean obj : dynamicList) {
						if (obj.getDateTime() != null && !FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
								obj.getDateTime() + " " + obj.getTime(), "yyyy-MM-dd HH:mm:ss")) {
							questionarriesFlag = false;
							break;
						}
					}
				}

				query = session.createQuery(
						"select new com.fdahpstudydesigner.bean.DynamicFrequencyBean(a.frequencyStartDate, a.frequencyTime)"
								+ " from QuestionnaireCustomScheduleBo a,QuestionnaireBo ab"
								+ " where a.questionnairesId=ab.id" + " and ab.active=1" + " and ab.studyId=:impValue"
								+ " and ab.frequency='" + FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE
								+ "'" + " and a.frequencyStartDate IS NOT NULL" + " and a.frequencyTime IS NOT NULL"
								+ " and a.used=false");
				query.setParameter(FdahpStudyDesignerConstants.IMP_VALUE, studyBo.getId());
				dynamicFrequencyList = query.list();
				if (dynamicFrequencyList != null && !dynamicFrequencyList.isEmpty()) {
					// checking Questionnaires which have scheduled
					// "manually schedule" expired or not
					for (DynamicFrequencyBean obj : dynamicFrequencyList) {
						if (obj.getStartDate() != null && obj.getTime() != null) {
							String dateTime = obj.getStartDate() + " " + obj.getTime();
							if (!FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(dateTime,
									"yyyy-MM-dd HH:mm:ss")) {
								questionarriesFlag = false;
								break;
							}
						}
					}
				}
			}
			if (!questionarriesFlag) {
				message = FdahpStudyDesignerConstants.QUESTIONNARIES_ERROR_MSG;
				return message;
			} else {
				// getting based on start date notification list
				searchQuery = " FROM NotificationBO RBO WHERE RBO.studyId=" + studyBo.getId()
						+ " AND RBO.scheduleDate IS NOT NULL AND RBO.scheduleTime IS NOT NULL"
						+ " AND RBO.notificationType='ST' AND RBO.notificationSubType='Announcement' AND RBO.notificationScheduleType='notImmediate' "
						+ " AND RBO.notificationSent=0 AND RBO.notificationStatus=0 ";
				query = session.createQuery(searchQuery);
				notificationBOs = query.list();
				if (notificationBOs != null && !notificationBOs.isEmpty()) {
					// checking notification expired or not
					for (NotificationBO notificationBO : notificationBOs) {
						if (!FdahpStudyDesignerUtil.compareDateWithCurrentDateTime(
								notificationBO.getScheduleDate() + " " + notificationBO.getScheduleTime(),
								"yyyy-MM-dd HH:mm:ss")) {
							notificationFlag = false;
							break;
						}
					}
				}
			}
			if (!notificationFlag) {
				message = FdahpStudyDesignerConstants.NOTIFICATION_ERROR_MSG;

				return message;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - updateStudyActionOnAction() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}

		return message;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String validateEligibilityTestKey(Integer eligibilityTestId, String shortTitle, Integer eligibilityId) {
		logger.info("StudyDAOImpl - getStudyVersionInfo() - Starts");
		Session session = null;
		List<EligibilityTestBo> eligibilityTestBos;
		String result = FdahpStudyDesignerConstants.FAILURE;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.getNamedQuery("EligibilityTestBo.validateShortTitle").setString("shortTitle", shortTitle)
					.setInteger("eligibilityTestId", eligibilityTestId).setInteger("eligibilityId", eligibilityId);
			eligibilityTestBos = query.list();
			if (eligibilityTestBos.isEmpty()) {
				result = FdahpStudyDesignerConstants.SUCCESS;
			}
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyVersionInfo() - ERROR ", e);
		}
		logger.info("StudyDAOImpl - getStudyVersionInfo() - Ends");
		return result;
	}

	/**
	 * validate the below items before do Publish (as Upcoming Study)/Launch/publish
	 * update of the study
	 * 
	 * @author BTC
	 * @param String , studyId
	 * @param String , buttonText
	 * @return {@link String}
	 */
	@Override
	public String validateStudyAction(String studyId, String buttonText) {
		logger.info("StudyDAOImpl - validateStudyAction() - Ends");
		String message = FdahpStudyDesignerConstants.SUCCESS;
		Session session = null;
		boolean enrollementFlag = false;
		boolean studyActivityFlag = false;
		StudySequenceBo studySequenceBo = null;
		StudyBo studyBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(buttonText) && StringUtils.isNotEmpty(studyId)) {

				studyBo = (StudyBo) session.getNamedQuery(FdahpStudyDesignerConstants.STUDY_LIST_BY_ID)
						.setInteger("id", Integer.parseInt(studyId)).uniqueResult();
				studySequenceBo = (StudySequenceBo) session
						.getNamedQuery(FdahpStudyDesignerConstants.STUDY_SEQUENCE_BY_ID)
						.setInteger(FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId()).uniqueResult();

				if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)
						|| buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {

					// 1-all validation mark as completed
					if (studySequenceBo != null) {
						String studyActivity = "";
						studyActivity = getErrorBasedonAction(studySequenceBo);
						if (StringUtils.isNotEmpty(studyActivity)
								&& !(FdahpStudyDesignerConstants.SUCCESS).equalsIgnoreCase(studyActivity))
							return studyActivity;
						else
							studyActivityFlag = true;
					}

					// 2-enrollment validation
					if (studyActivityFlag && StringUtils.isNotEmpty(studyBo.getEnrollingParticipants())
							&& studyBo.getEnrollingParticipants().equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
						enrollementFlag = true;
					}
					// 3-The study must have at least one 'activity' added. This
					// could be a questionnaire or active task.
					if (!enrollementFlag) {
						if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH))
							message = FdahpStudyDesignerConstants.LUNCH_ENROLLMENT_ERROR_MSG;
						else
							message = FdahpStudyDesignerConstants.PUBLISH_ENROLLMENT_ERROR_MSG;
						return message;
					} else {
						// 4-Date validation
						message = validateDateForStudyAction(studyBo, buttonText);
						return message;
					}
				} else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_PUBLISH)) {
					if (studySequenceBo != null) {
						if (!studySequenceBo.isBasicInfo()) {
							message = FdahpStudyDesignerConstants.BASICINFO_ERROR_MSG;
							return message;
						} else if (!studySequenceBo.isSettingAdmins()) {
							message = FdahpStudyDesignerConstants.SETTING_ERROR_MSG;
							return message;
						} else if (!studySequenceBo.isOverView()) {
							message = FdahpStudyDesignerConstants.OVERVIEW_ERROR_MSG;
							return message;
						} else if (!studySequenceBo.isConsentEduInfo()) {
							message = FdahpStudyDesignerConstants.CONSENTEDUINFO_ERROR_MSG;
							return message;
						} else if (!studySequenceBo.iseConsent()) {
							message = FdahpStudyDesignerConstants.ECONSENT_ERROR_MSG;
							return message;
						} else if (StringUtils.isNotEmpty(studyBo.getEnrollingParticipants()) && studyBo
								.getEnrollingParticipants().equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
							message = FdahpStudyDesignerConstants.PRE_PUBLISH_ENROLLMENT_ERROR_MSG;
							return message;
						}
					}
				}
			} else {
				message = "Action is missing";
			}

		} catch (Exception e) {
			logger.error("StudyDAOImpl - validateStudyAction() - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - validateStudyAction() - Ends");
		return message;
	}

	/**
	 * validating study Custom id
	 *
	 * @author BTC
	 *
	 * @return boolean
	 * @exception Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean validateStudyId(String customStudyId) {
		logger.info("StudyDAOImpl - validateStudyId() - Starts");
		boolean flag = false;
		Session session = null;
		List<StudyBo> studyBos = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			studyBos = session.getNamedQuery("StudyBo.getStudyBycustomStudyId")
					.setString(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, customStudyId).list();
			if (studyBos != null && !studyBos.isEmpty())
				flag = true;
		} catch (Exception e) {
			logger.error("StudyDAOImpl - validateStudyId() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - validateStudyId() - Starts");
		return flag;
	}

	/**
	 * View eligibility test question answer by eligibility id
	 *
	 * @author BTC
	 *
	 * @param eligibilityId , Id of {@link EligibilityBo}
	 * @return eligibilityTestList , {@link List} of {@link EligibilityTestBo}
	 *         object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EligibilityTestBo> viewEligibilityTestQusAnsByEligibilityId(Integer eligibilityId) {
		logger.info("StudyDAOImpl - viewEligibilityTestQusAnsByEligibilityId - Starts");
		Session session = null;
		List<EligibilityTestBo> eligibilityTestList = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			eligibilityTestList = session.getNamedQuery("EligibilityTestBo.findByEligibilityId")
					.setInteger("eligibilityId", eligibilityId).list();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - viewEligibilityTestQusAnsByEligibilityId - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - viewEligibilityTestQusAnsByEligibilityId - Ends");
		return eligibilityTestList;
	}

	/**
	 * View eligibility test question answer by id
	 *
	 * @author BTC
	 *
	 * @param eligibilityTestId , Id of {@link EligibilityTestBo}
	 * @return eligibilityTest , Object of {@link EligibilityTestBo}
	 */
	@Override
	public EligibilityTestBo viewEligibilityTestQusAnsById(Integer eligibilityTestId) {
		logger.info("StudyDAOImpl - viewEligibilityTestQusAnsById - Starts");
		Session session = null;
		EligibilityTestBo eligibilityTest = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			eligibilityTest = (EligibilityTestBo) session.getNamedQuery("EligibilityTestBo.findById")
					.setInteger("eligibilityTestId", eligibilityTestId).uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - viewEligibilityTestQusAnsById - ERROR ", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - viewEligibilityTestQusAnsById - Ends");
		return eligibilityTest;
	}

	@Override
	public Boolean isAnchorDateExistForEnrollment(Integer studyId, String customStudyId) {
		logger.info("StudyDAOImpl - isAnchorDateExistForEnrollment - Starts");
		Session session = null;
		Boolean isExist = false;
		String searchQuery = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			// checking in the questionnaire step anchor date is selected or not
			searchQuery = "select count(*) from questionnaires qr" + " where qr.anchor_date_id IS NOT NULL "
					+ "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name='"
					+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
					+ customStudyId + "') " + "and qr.custom_study_id='" + customStudyId + "' and qr.active=1";
			BigInteger count = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
			if (count.intValue() > 0) {
				isExist = true;
			} else {
				// activetask target anchordate
				searchQuery = "select count(*) from active_task qr" + " where qr.anchor_date_id IS NOT NULL "
						+ "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name='"
						+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
						+ customStudyId + "') " + "and qr.custom_study_id='" + customStudyId + "' and qr.active=1";
				BigInteger subCount = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
				if (subCount != null && subCount.intValue() > 0) {
					isExist = true;
				} else {
					searchQuery = "select count(*) from resources qr" + " where qr.anchor_date_id IS NOT NULL "
							+ "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name='"
							+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
							+ customStudyId + "') " + "and qr.custom_study_id='" + customStudyId + "' and qr.status=1";
					BigInteger sub1Count = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
					if (sub1Count != null && sub1Count.intValue() > 0) {
						isExist = true;
					}
				}
			}
		} catch (Exception e) {

		}
		logger.info("StudyDAOImpl - isAnchorDateExistForEnrollment - Ends");
		return isExist;
	}

	@Override
	public Boolean isAnchorDateExistForEnrollmentDraftStudy(Integer studyId, String customStudyId) {
		logger.info("StudyDAOImpl - isAnchorDateExistForEnrollmentDraftStudy - Starts");
		Session session = null;
		Boolean isExist = false;
		String searchQuery = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			// checking in the questionnaire step anchor date is selected or not
			searchQuery = "select count(*) from questionnaires qr" + " where qr.anchor_date_id IS NOT NULL "
					+ "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name='"
					+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
					+ customStudyId + "') " + "and qr.study_id=" + studyId + " and qr.active=1";
			BigInteger count = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
			if (count.intValue() > 0) {
				isExist = true;
			} else {
				// activetask target anchordate
				searchQuery = "select count(*) from active_task qr" + " where qr.anchor_date_id IS NOT NULL "
						+ "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name='"
						+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
						+ customStudyId + "') " + "and qr.study_id=" + studyId + " and qr.active=1";
				BigInteger subCount = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
				if (subCount != null && subCount.intValue() > 0) {
					isExist = true;
				} else {
					searchQuery = "select count(*) from resources qr" + " where qr.anchor_date_id IS NOT NULL "
							+ "and qr.anchor_date_id=(select t.id from anchordate_type t where t.name='"
							+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
							+ customStudyId + "') " + "and qr.study_id=" + studyId + " and qr.status=1";
					BigInteger sub1Count = (BigInteger) session.createSQLQuery(searchQuery).uniqueResult();
					if (sub1Count != null && sub1Count.intValue() > 0) {
						isExist = true;
					}
				}
			}
		} catch (Exception e) {

		}
		logger.info("StudyDAOImpl - isAnchorDateExistForEnrollmentDraftStudy - Ends");
		return isExist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String updateAnchordateForEnrollmentDate(StudyBo oldStudy, StudyBo updatedStudy, Session session,
			Transaction transaction) {
		logger.info("StudyDAOImpl - isAnchorDateExistForEnrollmentDraftStudy - Starts");
		Boolean isAnchorUsed = false;
		String searchQuery = "";
		String message = FdahpStudyDesignerConstants.FAILURE;
		List<Integer> anchorIds = new ArrayList<Integer>();
		List<Integer> anchorExistIds = new ArrayList<Integer>();

		try {
			if (oldStudy.isEnrollmentdateAsAnchordate() && !updatedStudy.isEnrollmentdateAsAnchordate()) {

				anchorIds = session.createSQLQuery("select t.id from anchordate_type t " + "where t.name='"
						+ FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "' and t.custom_study_id='"
						+ oldStudy.getCustomStudyId() + "'").list();
				if (!anchorIds.isEmpty() && anchorIds.size() > 0) {

					searchQuery = "select q.id from questionnaires q where q.schedule_type='"
							+ FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE + "' and q.anchor_date_id in("
							+ StringUtils.join(anchorIds, ",") + ")";
					anchorExistIds = session.createSQLQuery(searchQuery).list();
					if (!anchorExistIds.isEmpty() && anchorExistIds.size() > 0) {
						isAnchorUsed = true;
					} else {
						searchQuery = "select q.id from active_task q where q.schedule_type='"
								+ FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE + "' and q.anchor_date_id in("
								+ StringUtils.join(anchorIds, ",") + ")";
						anchorExistIds = session.createSQLQuery(searchQuery).list();
						if (!anchorExistIds.isEmpty() && anchorExistIds.size() > 0) {
							isAnchorUsed = true;
						} else {
							searchQuery = "select q.id from resources q where q.anchor_date_id in("
									+ StringUtils.join(anchorIds, ",") + ")";
							anchorExistIds = session.createSQLQuery(searchQuery).list();
							if (!anchorExistIds.isEmpty() && anchorExistIds.size() > 0) {
								isAnchorUsed = true;
							}
						}
					}
					if (isAnchorUsed) {

						StudySequenceBo studySequence = (StudySequenceBo) session
								.getNamedQuery("getStudySequenceByStudyId").setInteger("studyId", oldStudy.getId())
								.uniqueResult();
						if (studySequence != null) {
							int count1 = session
									.createSQLQuery("update questionnaires set status=0,anchor_date_id=null,"
											+ "modified_by=" + updatedStudy.getModifiedBy() + ",modified_date='"
											+ FdahpStudyDesignerUtil.getCurrentDateTime()
											+ "' where active=1 and anchor_date_id in("
											+ StringUtils.join(anchorIds, ",") + ")")
									.executeUpdate();
							if (count1 > 0) {
								studySequence.setStudyExcQuestionnaries(false);
								auditLogDAO.updateDraftToEditedStatus(session, transaction,
										updatedStudy.getModifiedBy(), FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE,
										oldStudy.getId());
							}
							int count2 = session
									.createSQLQuery("update active_task set action=0 ,anchor_date_id=null, modified_by="
											+ updatedStudy.getModifiedBy() + ",modified_date='"
											+ FdahpStudyDesignerUtil.getCurrentDateTime()
											+ "' where active=1 and anchor_date_id in("
											+ StringUtils.join(anchorIds, ",") + ")")
									.executeUpdate();
							if (count2 > 0) {
								studySequence.setStudyExcActiveTask(false);
								auditLogDAO.updateDraftToEditedStatus(session, transaction,
										updatedStudy.getModifiedBy(), FdahpStudyDesignerConstants.DRAFT_ACTIVETASK,
										oldStudy.getId());
							}
							int count3 = session.createSQLQuery("update resources set action=0,anchor_date_id=null "
									+ "where status=1 and anchor_date_id in(" + StringUtils.join(anchorIds, ",") + ")")
									.executeUpdate();

							if (count3 > 0) {
								studySequence.setMiscellaneousResources(false);
								auditLogDAO.updateDraftToEditedStatus(session, transaction,
										updatedStudy.getModifiedBy(), FdahpStudyDesignerConstants.DRAFT_STUDY,
										oldStudy.getId());
							}
							session.saveOrUpdate(studySequence);
							message = FdahpStudyDesignerConstants.SUCCESS;
						}
					}
				}
			}
		} catch (Exception e) {
			transaction.rollback();
			logger.error("StudyDAOImpl - isAnchorDateExistForEnrollmentDraftStudy - ERROR ", e);
		}
		logger.info("StudyDAOImpl - isAnchorDateExistForEnrollmentDraftStudy - Ends");
		return message;
	}

	/**
	 * validating study app id
	 *
	 * @author BTC
	 *
	 * @return boolean
	 * @exception Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean validateAppId(String customStudyId, String appId, String studyType) {
		logger.info("StudyDAOImpl - validateAppId() - Starts");
		boolean flag = false;
		Session session = null;
		List<StudyBo> studyBos = null;
		String searchQuery = "";
		String subQry = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (!studyType.isEmpty() && !appId.isEmpty()) {
				if (StringUtils.isNotEmpty(customStudyId)) {
					subQry = " and customStudyId!='" + customStudyId + "'";
				}
				if (studyType.equalsIgnoreCase(FdahpStudyDesignerConstants.STUDY_TYPE_GT)) {
					searchQuery = "From StudyBo WHERE appId='" + appId + "' and type='"
							+ FdahpStudyDesignerConstants.STUDY_TYPE_SD + "'" + subQry;
				} else {
					searchQuery = "From StudyBo WHERE appId='" + appId + "'" + subQry;
				}
				studyBos = session.createQuery(searchQuery).list();
			}

			if (studyBos != null && !studyBos.isEmpty())
				flag = true;
		} catch (Exception e) {
			logger.error("StudyDAOImpl - validateAppId() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - validateAppId() - Ends");
		return flag;
	}

	@Override
	public StudyPermissionBO getStudyPermissionBO(int studyId, int userId) {
		logger.info("StudyDAOImpl - getStudyPermissionBO() - Starts");
		Session session = null;
		StudyPermissionBO studyPermissionBO = null;
		String searchQuery = "";
		Query query=null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			searchQuery = "From StudyPermissionBO WHERE studyId=" + studyId + " and userId=" + userId;
			query = session.createQuery(searchQuery);
			studyPermissionBO=(StudyPermissionBO) query.uniqueResult();
		} catch (Exception e) {
			logger.error("StudyDAOImpl - getStudyPermissionBO() - ERROR", e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyDAOImpl - getStudyPermissionBO() - Ends");
		return studyPermissionBO;
	}
}
