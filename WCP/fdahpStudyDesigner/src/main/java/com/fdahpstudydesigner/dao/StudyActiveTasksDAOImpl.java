/**
 *
 */
package com.fdahpstudydesigner.dao;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.fdahpstudydesigner.bean.ActiveStatisticsBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * @author BTC
 *
 */
@Repository
public class StudyActiveTasksDAOImpl implements StudyActiveTasksDAO {

	private static Logger logger = Logger
			.getLogger(StudyActiveTasksDAOImpl.class.getName());
	@Autowired
	private AuditLogDAO auditLogDAO;
	HibernateTemplate hibernateTemplate;
	private Query query = null;
	String queryString = "";
	private Transaction transaction = null;

	public StudyActiveTasksDAOImpl() {
		// Do nothing
	}

	/**
	 * deleting of Active task in Study
	 * 
	 * @author BTC
	 * @param activeTaskBo
	 *            , {@link ActiveTaskBo}
	 * @param sesObj
	 *            , {@link SessionObject}
	 * @param String
	 *            , customStudyId
	 * @return String, SUCCESS or FAILURE
	 */
	@Override
	public String deleteActiveTask(ActiveTaskBo activeTaskBo,
			SessionObject sesObj, String customStudyId) {
		logger.info("StudyActiveTasksDAOImpl - deleteActiveTAsk() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		StudyVersionBo studyVersionBo = null;
		String deleteActQuery = "";
		String deleteQuery = "";
		String activity = "";
		String activityDetails = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (activeTaskBo != null) {
				Integer studyId = activeTaskBo.getStudyId();

				transaction = session.beginTransaction();

				queryString = "DELETE From NotificationBO where activeTaskId="
						+ activeTaskBo.getId() + "AND notificationSent=false";
				session.createQuery(queryString).executeUpdate();
				query = session.getNamedQuery("getStudyByCustomStudyId")
						.setString("customStudyId", customStudyId);
				query.setMaxResults(1);
				studyVersionBo = (StudyVersionBo) query.uniqueResult();
				// get the study version table to check whether study launch or
				// not ,
				// if record exist in version table, then study already launch ,
				// so do soft delete active task
				// if record does not exist , then study has not launched , so
				// do hard delete active task
				if (studyVersionBo != null) {
					// soft delete active task after study launch
					activity = "Active Task was deactivated.";
					activityDetails = "Active Task was deactivated. (Active Task Key = "
							+ activeTaskBo.getShortTitle()
							+ ", Study ID = "
							+ customStudyId + ")";
					deleteActQuery = "update ActiveTaskAtrributeValuesBo set active=0 where activeTaskId="
							+ activeTaskBo.getId();
					deleteQuery = "update ActiveTaskBo set active=0 ,modifiedBy="
							+ sesObj.getUserId()
							+ ",modifiedDate='"
							+ FdahpStudyDesignerUtil.getCurrentDateTime()
							+ "',customStudyId='"
							+ customStudyId
							+ "' where id=" + activeTaskBo.getId();
				} else {
					// hard delete active task before study launch
					session.createSQLQuery(
							"DELETE FROM active_task_frequencies WHERE active_task_id="
									+ activeTaskBo.getId()).executeUpdate();
					session.createSQLQuery(
							"DELETE FROM active_task_custom_frequencies WHERE active_task_id ="
									+ activeTaskBo.getId()).executeUpdate();

					deleteActQuery = "delete ActiveTaskAtrributeValuesBo where activeTaskId="
							+ activeTaskBo.getId();
					deleteQuery = "delete ActiveTaskBo where id="
							+ activeTaskBo.getId();

					activity = "Active Task was deleted.";
					activityDetails = "Active Task was deleted. (Active Task Key = "
							+ activeTaskBo.getShortTitle()
							+ ", Study ID = "
							+ customStudyId + ")";
				}
				query = session.createQuery(deleteActQuery);
				query.executeUpdate();

				query = session
						.createQuery(" UPDATE StudySequenceBo SET studyExcActiveTask =false WHERE studyId = "
								+ studyId);
				query.executeUpdate();

				query = session.createQuery(deleteQuery);
				query.executeUpdate();

				message = FdahpStudyDesignerConstants.SUCCESS;

				auditLogDAO.saveToAuditLog(session, transaction, sesObj,
						activity, activityDetails,
						"StudyActiveTasksDAOImpl - deleteActiveTAsk");
				transaction.commit();
			}
		} catch (Exception e) {
			transaction.rollback();
			logger.error(
					"StudyActiveTasksDAOImpl - deleteActiveTAsk() - ERROR ", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - deleteActiveTAsk() - Ends");
		return message;
	}

	/**
	 * get active task details in Study
	 * 
	 * @author BTC
	 * @param Integer
	 *            , aciveTaskId
	 * @param String
	 *            , customStudyId
	 * @return {@link ActiveTaskBo}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActiveTaskBo getActiveTaskById(Integer activeTaskId,
			String customStudyId) {
		logger.info("StudyActiveTasksDAOImpl - getActiveTaskById() - Starts");
		ActiveTaskBo activeTaskBo = null;
		Session session = null;
		List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			activeTaskBo = (ActiveTaskBo) session.get(ActiveTaskBo.class,
					activeTaskId);
			if (activeTaskBo != null) {
				query = session
						.createQuery("from ActiveTaskAtrributeValuesBo where activeTaskId="
								+ activeTaskBo.getId());
				activeTaskAtrributeValuesBos = query.list();
				if (StringUtils.isNotEmpty(customStudyId)) {
					// to check duplicate short title of active task
					BigInteger shortTitleCount = (BigInteger) session
							.createSQLQuery(
									"select count(*) from active_task a "
											+ "where a.short_title='"
											+ activeTaskBo.getShortTitle()
											+ "' and custom_study_id='"
											+ customStudyId
											+ "' and a.active=1 and a.is_live=1")
							.uniqueResult();
					if (shortTitleCount != null
							&& shortTitleCount.intValue() > 0)
						activeTaskBo.setIsDuplicate(shortTitleCount.intValue());
					else
						activeTaskBo.setIsDuplicate(0);
				} else {
					activeTaskBo.setIsDuplicate(0);
				}

				if (activeTaskAtrributeValuesBos != null
						&& !activeTaskAtrributeValuesBos.isEmpty()) {
					for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo : activeTaskAtrributeValuesBos) {
						if (StringUtils.isNotEmpty(customStudyId)) {
							// to check duplicate short title in dashboard of
							// active task
							BigInteger statTitleCount = (BigInteger) session
									.createSQLQuery(
											"select count(*) from active_task_attrtibutes_values at "
													+ "where at.identifier_name_stat='"
													+ activeTaskAtrributeValuesBo
															.getIdentifierNameStat()
													+ "'"
													+ "and  at.active_task_id in "
													+ "(select a.id from active_task a where a.custom_study_id='"
													+ customStudyId
													+ "' and a.active=1 and a.is_live=1)")
									.uniqueResult();
							if (statTitleCount != null
									&& statTitleCount.intValue() > 0)
								activeTaskAtrributeValuesBo
										.setIsIdentifierNameStatDuplicate(statTitleCount
												.intValue());
							else
								activeTaskAtrributeValuesBo
										.setIsIdentifierNameStatDuplicate(0);
						} else {
							activeTaskAtrributeValuesBo
									.setIsIdentifierNameStatDuplicate(0);
						}
					}
					activeTaskBo
							.setTaskAttributeValueBos(activeTaskAtrributeValuesBos);
				}

				String searchQuery = "";
				if (null != activeTaskBo.getFrequency()) {
					if (activeTaskBo
							.getFrequency()
							.equalsIgnoreCase(
									FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
						searchQuery = "From ActiveTaskCustomScheduleBo ATSBO where ATSBO.activeTaskId="
								+ activeTaskBo.getId();
						query = session.createQuery(searchQuery);
						List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBos = query
								.list();
						activeTaskBo
								.setActiveTaskCustomScheduleBo(activeTaskCustomScheduleBos);
					} else {
						searchQuery = "From ActiveTaskFrequencyBo ATBO where ATBO.activeTaskId="
								+ activeTaskBo.getId();
						query = session.createQuery(searchQuery);
						if (activeTaskBo
								.getFrequency()
								.equalsIgnoreCase(
										FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
							List<ActiveTaskFrequencyBo> activeTaskFrequencyBos = query
									.list();
							activeTaskBo
									.setActiveTaskFrequenciesList(activeTaskFrequencyBos);
						} else {
							ActiveTaskFrequencyBo activeTaskFrequencyBo = (ActiveTaskFrequencyBo) query
									.uniqueResult();
							activeTaskBo
									.setActiveTaskFrequenciesBo(activeTaskFrequencyBo);
						}

					}
				}
				if (activeTaskBo.getVersion() != null) {
					activeTaskBo.setActiveTaskVersion(" (V"
							+ activeTaskBo.getVersion() + ")");
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - getActiveTaskById() - Error", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - getActiveTaskById() - Ends");
		return activeTaskBo;
	}

	/**
	 * to get all static formulas in acive task
	 * 
	 * @author BTC
	 * @return {@link List<ActivetaskFormulaBo>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActivetaskFormulaBo> getActivetaskFormulas() {
		logger.info("StudyActiveTasksDAOImpl - getActivetaskFormulas() - Starts");
		Session session = null;
		List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("from ActivetaskFormulaBo");
			activetaskFormulaList = query.list();
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - getActivetaskFormulas() - ERROR ",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - getActivetaskFormulas() - Ends");
		return activetaskFormulaList;
	}

	/**
	 * get all the field names of active task based on of activeTaskType
	 * 
	 * @author BTC
	 * @return {@link List<ActiveTaskMasterAttributeBo>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
			String activeTaskType) {
		logger.info("StudyActiveTasksDAOImpl - getActiveTaskMasterAttributesByType() - Starts");
		Session session = null;
		List<ActiveTaskMasterAttributeBo> taskMasterAttributeBos = new ArrayList<>();
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createQuery(" from ActiveTaskMasterAttributeBo where taskTypeId="
							+ Integer.parseInt(activeTaskType));
			taskMasterAttributeBos = query.list();
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - getActiveTaskMasterAttributesByType() - ERROR ",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - getActiveTaskMasterAttributesByType() - Ends");
		return taskMasterAttributeBos;
	}

	/**
	 * get all type of activeTask in Study
	 *
	 * @author BTC
	 * @param String
	 *            , platformType
	 * @return {@link List<ActiveTaskListBo>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveTaskListBo> getAllActiveTaskTypes(String platformType) {
		logger.info("StudyActiveTasksDAOImpl - getAllActiveTaskTypes() - Starts");
		Session session = null;
		List<ActiveTaskListBo> activeTaskListBos = new ArrayList<>();
		try {
			session = hibernateTemplate.getSessionFactory().openSession();

			// to get only "Fetal Kick Counter" type of active task based on
			// Android platform
			if (StringUtils.isNotEmpty(platformType)
					&& platformType.contains("A"))
				queryString = "from ActiveTaskListBo a where a.taskName not in('"
						+ FdahpStudyDesignerConstants.TOWER_OF_HANOI
						+ "','"
						+ FdahpStudyDesignerConstants.SPATIAL_SPAN_MEMORY
						+ "')";
			else
				queryString = "from ActiveTaskListBo";
			query = session.createQuery(queryString);
			activeTaskListBos = query.list();
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - getAllActiveTaskTypes() - ERROR ",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - getAllActiveTaskTypes() - Ends");
		return activeTaskListBos;
	}

	/**
	 * to get all static statistic images
	 * 
	 * @author BTC
	 * @return {@link List<StatisticImageListBo>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StatisticImageListBo> getStatisticImages() {
		logger.info("StudyActiveTasksDAOImpl - getStatisticImages() - Starts");
		Session session = null;
		List<StatisticImageListBo> imageListBos = new ArrayList<>();
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery("from StatisticImageListBo");
			imageListBos = query.list();
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - getStatisticImages() - ERROR ",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - getStatisticImages() - Ends");
		return imageListBos;
	}

	/**
	 * return active tasks based on user's Study Id
	 *
	 * @author BTC
	 *
	 * @param studyId
	 *            , studyId of the {@link StudyBo}
	 * @return List of {@link ActiveTaskBo}
	 * @exception Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveTaskBo> getStudyActiveTasksByStudyId(String studyId,
			Boolean isLive) {
		logger.info("StudyActiveTasksDAOImpl - getStudyActiveTasksByStudyId() - Starts");
		Session session = null;
		List<ActiveTaskBo> activeTasks = null;
		List<ActiveTaskListBo> activeTaskListBos = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (StringUtils.isNotEmpty(studyId)) {
				if (isLive) {
					String searchQuery = "SELECT ATB FROM ActiveTaskBo ATB where ATB.active IS NOT NULL and ATB.active=1 and ATB.customStudyId ='"
							+ studyId + "' and ATB.live=1 order by id";
					query = session.createQuery(searchQuery);
				} else {
					query = session.getNamedQuery(
							"ActiveTaskBo.getActiveTasksByByStudyId")
							.setInteger("studyId", Integer.parseInt(studyId));
				}

				activeTasks = query.list();

				query = session.createQuery("from ActiveTaskListBo");
				activeTaskListBos = query.list();

				if (activeTasks != null && !activeTasks.isEmpty()
						&& activeTaskListBos != null
						&& !activeTaskListBos.isEmpty()) {
					for (ActiveTaskBo activeTaskBo : activeTasks) {
						if (activeTaskBo.getTaskTypeId() != null) {
							for (ActiveTaskListBo activeTaskListBo : activeTaskListBos) {
								if (activeTaskListBo.getActiveTaskListId()
										.intValue() == activeTaskBo
										.getTaskTypeId().intValue()) {
									activeTaskBo.setType(activeTaskListBo
											.getTaskName());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - getStudyActiveTasksByStudyId() - ERROR ",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - getStudyActiveTasksByStudyId() - Ends");
		return activeTasks;
	}

	/**
	 * Save or update schedule of active task
	 * 
	 * @author BTC
	 * 
	 * @param activeTaskBo
	 *            , {@link ActiveTaskBo}
	 * @param customStudyId
	 *            , the custom id of study
	 * @return {@link ActiveTaskBo}
	 */
	@Override
	public ActiveTaskBo saveOrUpdateActiveTask(ActiveTaskBo activeTaskBo,
			String customStudyId) {
		logger.info("StudyActiveTasksDAOImpl - saveOrUpdateActiveTask() - Starts");
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			session.saveOrUpdate(activeTaskBo);
			if (activeTaskBo.getType().equalsIgnoreCase(
					FdahpStudyDesignerConstants.SCHEDULE)
					&& activeTaskBo != null && activeTaskBo.getId() != null) {
				if (activeTaskBo.getActiveTaskFrequenciesList() != null
						&& !activeTaskBo.getActiveTaskFrequenciesList()
								.isEmpty()) {
					String deleteQuery = "delete from active_task_custom_frequencies where active_task_id="
							+ activeTaskBo.getId();
					query = session.createSQLQuery(deleteQuery);
					query.executeUpdate();
					String deleteQuery2 = "delete from active_task_frequencies where active_task_id="
							+ activeTaskBo.getId();
					query = session.createSQLQuery(deleteQuery2);
					query.executeUpdate();
					for (ActiveTaskFrequencyBo activeTaskFrequencyBo : activeTaskBo
							.getActiveTaskFrequenciesList()) {
						if (activeTaskFrequencyBo.getFrequencyTime() != null) {
							activeTaskFrequencyBo
									.setFrequencyTime(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskFrequencyBo
													.getFrequencyTime(),
											FdahpStudyDesignerConstants.SDF_TIME,
											FdahpStudyDesignerConstants.UI_SDF_TIME));
							if (activeTaskFrequencyBo.getActiveTaskId() == null) {
								activeTaskFrequencyBo.setId(null);
								activeTaskFrequencyBo
										.setActiveTaskId(activeTaskBo.getId());
							}
							session.saveOrUpdate(activeTaskFrequencyBo);
						}
					}
				}
				if (activeTaskBo.getActiveTaskFrequenciesList() != null) {
					ActiveTaskFrequencyBo activeTaskFrequencyBo = activeTaskBo
							.getActiveTaskFrequenciesBo();
					if (activeTaskFrequencyBo.getFrequencyDate() != null
							|| activeTaskFrequencyBo.getFrequencyTime() != null
							|| activeTaskBo
									.getFrequency()
									.equalsIgnoreCase(
											FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
						if (!activeTaskBo.getFrequency().equalsIgnoreCase(
								activeTaskBo.getPreviousFrequency())) {
							String deleteQuery = "delete from active_task_custom_frequencies where active_task_id="
									+ activeTaskBo.getId();
							query = session.createSQLQuery(deleteQuery);
							query.executeUpdate();
							String deleteQuery2 = "delete from active_task_frequencies where active_task_id="
									+ activeTaskBo.getId();
							query = session.createSQLQuery(deleteQuery2);
							query.executeUpdate();
						}
						if (activeTaskFrequencyBo.getActiveTaskId() == null) {
							activeTaskFrequencyBo.setActiveTaskId(activeTaskBo
									.getId());
						}
						if (activeTaskBo.getActiveTaskFrequenciesBo()
								.getFrequencyDate() != null
								&& !activeTaskBo.getActiveTaskFrequenciesBo()
										.getFrequencyDate().isEmpty()) {
							activeTaskFrequencyBo
									.setFrequencyDate(FdahpStudyDesignerUtil
											.getFormattedDate(
													activeTaskBo
															.getActiveTaskFrequenciesBo()
															.getFrequencyDate(),
													FdahpStudyDesignerConstants.UI_SDF_DATE,
													FdahpStudyDesignerConstants.SD_DATE_FORMAT));
						}
						if (activeTaskBo.getActiveTaskFrequenciesBo()
								.getFrequencyTime() != null
								&& !activeTaskBo.getActiveTaskFrequenciesBo()
										.getFrequencyTime().isEmpty()) {
							activeTaskBo
									.getActiveTaskFrequenciesBo()
									.setFrequencyTime(
											FdahpStudyDesignerUtil
													.getFormattedDate(
															activeTaskBo
																	.getActiveTaskFrequenciesBo()
																	.getFrequencyTime(),
															FdahpStudyDesignerConstants.SDF_TIME,
															FdahpStudyDesignerConstants.UI_SDF_TIME));
						}
						session.saveOrUpdate(activeTaskFrequencyBo);
					}
				}
				if (activeTaskBo.getActiveTaskCustomScheduleBo() != null
						&& activeTaskBo.getActiveTaskCustomScheduleBo().size() > 0) {
					String deleteQuery = "delete from active_task_custom_frequencies where active_task_id="
							+ activeTaskBo.getId();
					query = session.createSQLQuery(deleteQuery);
					query.executeUpdate();
					String deleteQuery2 = "delete from active_task_frequencies where active_task_id="
							+ activeTaskBo.getId();
					query = session.createSQLQuery(deleteQuery2);
					query.executeUpdate();
					for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo : activeTaskBo
							.getActiveTaskCustomScheduleBo()) {
						if (activeTaskCustomScheduleBo
										.getFrequencyTime() != null) {
							if (activeTaskCustomScheduleBo.getActiveTaskId() == null) {
								activeTaskCustomScheduleBo
										.setActiveTaskId(activeTaskBo.getId());
							}
							if(activeTaskCustomScheduleBo.getFrequencyStartDate() != null && !activeTaskCustomScheduleBo.getFrequencyStartDate().isEmpty()) {
								activeTaskCustomScheduleBo
								.setFrequencyStartDate(FdahpStudyDesignerUtil.getFormattedDate(
										activeTaskCustomScheduleBo
												.getFrequencyStartDate(),
										FdahpStudyDesignerConstants.UI_SDF_DATE,
										FdahpStudyDesignerConstants.SD_DATE_FORMAT));
							}
							if(activeTaskCustomScheduleBo.getFrequencyEndDate() != null && !activeTaskCustomScheduleBo.getFrequencyEndDate().isEmpty()) {
							activeTaskCustomScheduleBo
									.setFrequencyEndDate(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskCustomScheduleBo
													.getFrequencyEndDate(),
											FdahpStudyDesignerConstants.UI_SDF_DATE,
											FdahpStudyDesignerConstants.SD_DATE_FORMAT));
							}
							if (activeTaskCustomScheduleBo.getFrequencyTime() != null
									&& !activeTaskCustomScheduleBo
											.getFrequencyTime().isEmpty()) {
								activeTaskCustomScheduleBo
										.setFrequencyTime(FdahpStudyDesignerUtil.getFormattedDate(
												activeTaskCustomScheduleBo
														.getFrequencyTime(),
												FdahpStudyDesignerConstants.SDF_TIME,
												FdahpStudyDesignerConstants.UI_SDF_TIME));
							}
							activeTaskCustomScheduleBo.setxDaysSign(activeTaskCustomScheduleBo.isxDaysSign());
							if(activeTaskCustomScheduleBo.getTimePeriodFromDays()!=null) {
								activeTaskCustomScheduleBo.setTimePeriodFromDays(activeTaskCustomScheduleBo.getTimePeriodFromDays());
							}
							activeTaskCustomScheduleBo.setyDaysSign(activeTaskCustomScheduleBo.isyDaysSign());
							if(activeTaskCustomScheduleBo.getTimePeriodToDays()!=null) {
								activeTaskCustomScheduleBo.setTimePeriodToDays(activeTaskCustomScheduleBo.getTimePeriodToDays());
							}
							session.saveOrUpdate(activeTaskCustomScheduleBo);
						}
					}
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.info(
					"StudyActiveTasksDAOImpl - saveOrUpdateActiveTask() - Error",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - saveOrUpdateActiveTask() - Ends");
		return activeTaskBo;
	}

	/**
	 * Add or update all type of active task content (The Fetal Kick Counter
	 * task/Tower of Hanoi/Spatial Memory Task)
	 *
	 * @author BTC
	 * @param activeTaskBo
	 *            , {@link ActiveTaskBo}
	 * @param sesObj
	 *            , {@link SessionObject}
	 * @param String
	 *            , customStudyId
	 * @return {@link ActiveTaskBo}
	 *
	 */
	@Override
	public ActiveTaskBo saveOrUpdateActiveTaskInfo(ActiveTaskBo activeTaskBo,
			SessionObject sesObj, String customStudyId) {
		logger.info("StudyActiveTasksDAOImpl - saveOrUpdateActiveTaskInfo() - Starts");
		Session session = null;
		StudySequenceBo studySequence = null;
		List<ActiveTaskAtrributeValuesBo> taskAttributeValueBos = new ArrayList<>();
		String activitydetails = "";
		String activity = "";
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (activeTaskBo.getTaskAttributeValueBos() != null
					&& !activeTaskBo.getTaskAttributeValueBos().isEmpty())
				taskAttributeValueBos = activeTaskBo.getTaskAttributeValueBos();
			session.saveOrUpdate(activeTaskBo);
			if (taskAttributeValueBos != null
					&& !taskAttributeValueBos.isEmpty()) {
				for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo : taskAttributeValueBos) {
					if (activeTaskAtrributeValuesBo.isAddToDashboard()) {
						if (!activeTaskAtrributeValuesBo.isAddToLineChart()) {
							activeTaskAtrributeValuesBo.setTimeRangeChart(null);
							activeTaskAtrributeValuesBo.setRollbackChat(null);
							activeTaskAtrributeValuesBo.setTitleChat(null);
						}
						if (!activeTaskAtrributeValuesBo.isUseForStatistic()) {
							activeTaskAtrributeValuesBo
									.setIdentifierNameStat(null);
							activeTaskAtrributeValuesBo
									.setDisplayNameStat(null);
							activeTaskAtrributeValuesBo
									.setDisplayUnitStat(null);
							activeTaskAtrributeValuesBo.setUploadTypeStat(null);
							activeTaskAtrributeValuesBo
									.setFormulaAppliedStat(null);
							activeTaskAtrributeValuesBo.setTimeRangeStat(null);
						}
						activeTaskAtrributeValuesBo
								.setActiveTaskId(activeTaskBo.getId());
						activeTaskAtrributeValuesBo.setActive(1);
						if (activeTaskAtrributeValuesBo.getAttributeValueId() == null) {
							session.save(activeTaskAtrributeValuesBo);
						} else {
							session.update(activeTaskAtrributeValuesBo);
						}
					}
				}
			}

			if (StringUtils.isNotEmpty(activeTaskBo.getButtonText())) {
				studySequence = (StudySequenceBo) session
						.getNamedQuery("getStudySequenceByStudyId")
						.setInteger("studyId", activeTaskBo.getStudyId())
						.uniqueResult();
				if (studySequence != null) {
					studySequence.setStudyExcActiveTask(false);
				}
				session.saveOrUpdate(studySequence);
			}

			if (activeTaskBo.getButtonText().equalsIgnoreCase(
					FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
				activity = "Content saved for Active Task.";
				activitydetails = "Content saved for Active Task. (Active Task Key  = "
						+ activeTaskBo.getShortTitle()
						+ ",  Study ID = "
						+ customStudyId + ")";
			} else {
				activity = "Active Task succesfully checked for minimum content completeness.";
				activitydetails = "Active Task succesfully checked for minimum content completeness and marked 'Done'. (Active Task Key = "
						+ activeTaskBo.getShortTitle()
						+ ", Study ID ="
						+ customStudyId + ")";
				auditLogDAO.updateDraftToEditedStatus(session, transaction,
						sesObj.getUserId(),
						FdahpStudyDesignerConstants.DRAFT_ACTIVETASK,
						activeTaskBo.getStudyId());
				// Notification Purpose needed Started
				queryString = " From StudyBo where customStudyId='"
						+ customStudyId + "' and live=1";
				StudyBo studyBo = (StudyBo) session.createQuery(queryString)
						.uniqueResult();
				if (studyBo != null) {
					queryString = " From StudyBo where id="
							+ activeTaskBo.getStudyId();
					StudyBo draftStudyBo = (StudyBo) session.createQuery(
							queryString).uniqueResult();
					NotificationBO notificationBO = null;
					queryString = "From NotificationBO where activeTaskId="
							+ activeTaskBo.getId();
					notificationBO = (NotificationBO) session.createQuery(
							queryString).uniqueResult();
					if (notificationBO == null) {
						notificationBO = new NotificationBO();
						notificationBO.setStudyId(activeTaskBo.getStudyId());
						notificationBO.setCustomStudyId(studyBo
								.getCustomStudyId());
						if(StringUtils.isNotEmpty(studyBo.getAppId()))
							 notificationBO.setAppId(studyBo.getAppId());
						notificationBO
								.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
						notificationBO
								.setNotificationSubType(FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ACTIVITY);
						notificationBO
								.setNotificationScheduleType(FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
						notificationBO.setActiveTaskId(activeTaskBo.getId());
						notificationBO.setNotificationStatus(false);
						notificationBO.setCreatedBy(sesObj.getUserId());
						notificationBO.setCreatedOn(FdahpStudyDesignerUtil
								.getCurrentDateTime());
						notificationBO.setNotificationSent(false);
					} else {
						notificationBO.setModifiedBy(sesObj.getUserId());
						notificationBO.setModifiedOn(FdahpStudyDesignerUtil
								.getCurrentDateTime());
					}
					notificationBO
							.setNotificationText(FdahpStudyDesignerConstants.NOTIFICATION_ACTIVETASK_TEXT
									.replace("$shortTitle",
											activeTaskBo.getDisplayName())
									.replace("$customId",
											draftStudyBo.getName()));
					if (!notificationBO.isNotificationSent())
						session.saveOrUpdate(notificationBO);
				}
				// Notification Purpose needed End
			}
			auditLogDAO.saveToAuditLog(session, transaction, sesObj, activity,
					activitydetails,
					"StudyActiveTasksDAOImpl - saveOrUpdateActiveTaskInfo");

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error(
					"StudyActiveTasksDAOImpl - saveOrUpdateActiveTaskInfo() - Error",
					e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - saveOrUpdateActiveTaskInfo() - Ends");
		return activeTaskBo;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	/**
	 * validating ShortTitle and chart short title in study activity
	 * 
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param response
	 *            , {@link HttpServletResponse}
	 * @throws IOException
	 * @return boolean
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public boolean validateActiveTaskAttrById(Integer studyId,
			String activeTaskAttName, String activeTaskAttIdVal,
			String activeTaskAttIdName, String customStudyId) {
		logger.info("StudyActiveTasksDAOImpl - validateActiveTaskAttrById() - Starts");
		boolean flag = false;
		Session session = null;
		String queryString = "";
		String subString = "";
		List<ActiveTaskBo> taskBos = null;
		new ArrayList<>();
		List<QuestionnaireBo> questionnaireBo = null;
		List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos = null;
		List<QuestionsBo> questionnairesStepsBo = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (studyId != null && StringUtils.isNotEmpty(activeTaskAttName)
					&& StringUtils.isNotEmpty(activeTaskAttIdVal)) {

				// to check uniqueness of chart short title in activity(active
				// task and questionnaire) of study
				if (activeTaskAttName
						.equalsIgnoreCase(FdahpStudyDesignerConstants.SHORT_NAME_STATISTIC)) {
					if (customStudyId != null && !customStudyId.isEmpty()) {
						if (!activeTaskAttIdName.equals("static")) {
							if (activeTaskAttIdName.contains(",")) {
								List<String> idArr = new ArrayList<String>();
								String[] arr;
								arr = activeTaskAttIdName.split(",");
								if (arr != null && arr.length > 0) {
									for (String id : arr) {
										if (!id.isEmpty())
											idArr.add(id);
									}
								}
								activeTaskAttIdName = StringUtils.join(idArr,
										',');
							}
							subString = " and attributeValueId NOT IN("
									+ activeTaskAttIdName + ")";
						}
						// to check chart short title exist in active task or
						// not
						queryString = "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId IN "
								+ "(select id From StudyBo SBO WHERE customStudyId='"
								+ customStudyId
								+ "')) and identifierNameStat='"
								+ activeTaskAttIdVal + "'" + subString + "";
						activeTaskAtrributeValuesBos = session.createQuery(
								queryString).list();
						if (activeTaskAtrributeValuesBos != null
								&& !activeTaskAtrributeValuesBos.isEmpty()) {
							flag = true;
						} else {
							// to check chart short title exist in question of
							// questionnaire
							queryString = "From QuestionsBo QBO where QBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (select id from QuestionnaireBo Q where Q.studyId in(select id From StudyBo SBO WHERE customStudyId='"
									+ customStudyId
									+ "')) and QSBO.stepType='"
									+ FdahpStudyDesignerConstants.QUESTION_STEP
									+ "') and QBO.statShortName='"
									+ activeTaskAttIdVal + "'";
							query = session.createQuery(queryString);
							questionnairesStepsBo = query.list();
							if (questionnairesStepsBo != null
									&& !questionnairesStepsBo.isEmpty()) {
								flag = true;
							} else {
								// to check chart short title exist in form
								// question of questionnaire
								queryString = "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
										+ "and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.study_id IN(select id From studies SBO WHERE custom_study_id='"
										+ customStudyId
										+ "') and QSBO.step_type='Form' and QBO.stat_short_name='"
										+ activeTaskAttIdVal + "'";
								BigInteger subCount = (BigInteger) session
										.createSQLQuery(queryString)
										.uniqueResult();
								if (subCount != null && subCount.intValue() > 0)
									flag = true;
								else
									flag = false;
							}
						}
					} else {
						if (!activeTaskAttIdName.equals("static")) {
							if (activeTaskAttIdName.contains(",")) {
								List<String> idArr = new ArrayList<String>();
								String[] arr;
								arr = activeTaskAttIdName.split(",");
								if (arr != null && arr.length > 0) {
									for (String id : arr) {
										if (!id.isEmpty())
											idArr.add(id);
									}
								}
								activeTaskAttIdName = StringUtils.join(idArr,
										',');
							}
							subString = " and attributeValueId NOT IN("
									+ activeTaskAttIdName + ")";
						}
						// to check chart short title exist in active task or
						// not
						queryString = "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId="
								+ studyId
								+ ") "
								+ "and identifierNameStat='"
								+ activeTaskAttIdVal + "'" + subString + "";
						activeTaskAtrributeValuesBos = session.createQuery(
								queryString).list();
						if (activeTaskAtrributeValuesBos != null
								&& !activeTaskAtrributeValuesBos.isEmpty()) {
							flag = true;
						} else {
							// to check chart short title exist in question of
							// questionnaire
							queryString = "From QuestionsBo QBO where QBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (select id from QuestionnaireBo Q where Q.studyId="
									+ studyId
									+ ") and QSBO.stepType='"
									+ FdahpStudyDesignerConstants.QUESTION_STEP
									+ "') and QBO.statShortName='"
									+ activeTaskAttIdVal + "'";
							query = session.createQuery(queryString);
							questionnairesStepsBo = query.list();
							if (questionnairesStepsBo != null
									&& !questionnairesStepsBo.isEmpty()) {
								flag = true;
							} else {
								// to check chart short title exist in form
								// question of questionnaire
								queryString = "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
										+ "and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.study_id="
										+ studyId
										+ " and QSBO.step_type='Form' and QBO.stat_short_name='"
										+ activeTaskAttIdVal + "'";
								BigInteger subCount = (BigInteger) session
										.createSQLQuery(queryString)
										.uniqueResult();
								if (subCount != null && subCount.intValue() > 0)
									flag = true;
								else
									flag = false;
							}
						}
					}
				} else if (activeTaskAttName
						.equalsIgnoreCase(FdahpStudyDesignerConstants.SHORT_TITLE)) {
					// to check uniqueness of short title in activity(active
					// task and questionnaire) of study
					if (customStudyId != null && !customStudyId.isEmpty()) {
						// to check short title exist in active task or not
						queryString = "from ActiveTaskBo where studyId IN (select id From StudyBo SBO WHERE customStudyId='"
								+ customStudyId
								+ "') and shortTitle='"
								+ activeTaskAttIdVal + "'";
						taskBos = session.createQuery(queryString).list();
						if (taskBos != null && !taskBos.isEmpty()) {
							flag = true;
						} else {
							// to check short title exist in questionnaire or
							// not
							queryString = "From QuestionnaireBo QBO where QBO.studyId IN(select id From StudyBo SBO WHERE customStudyId='"
									+ customStudyId
									+ "') and QBO.shortTitle='"
									+ activeTaskAttIdVal + "'";
							query = session.createQuery(queryString);
							questionnaireBo = query.list();
							if (questionnaireBo != null
									&& !questionnaireBo.isEmpty()) {
								flag = true;
							} else {
								flag = false;
							}
						}
					} else {
						// to check short title exist in active task or not
						queryString = "from ActiveTaskBo where studyId="
								+ studyId + " and shortTitle='"
								+ activeTaskAttIdVal + "'";
						taskBos = session.createQuery(queryString).list();
						if (taskBos != null && !taskBos.isEmpty()) {
							flag = true;
						} else {
							// to check short title exist in questionnaire or
							// not
							questionnaireBo = session
									.getNamedQuery(
											"checkQuestionnaireShortTitle")
									.setInteger("studyId", studyId)
									.setString("shortTitle", activeTaskAttIdVal)
									.list();
							if (questionnaireBo != null
									&& !questionnaireBo.isEmpty()) {
								flag = true;
							} else {
								flag = false;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - validateActiveTaskAttrById() - ERROR",
					e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - validateActiveTaskAttrById() - Ends");
		return flag;
	}

	/**
	 * validating list of active task chart short title in study
	 * 
	 * @author BTC
	 * @param String
	 *            , customStudyId
	 * @param activeStatisticsBeans
	 *            , {@link List<ActiveStatisticsBean>}
	 * @throws IOException
	 * @return {@link List<ActiveStatisticsBean>}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveStatisticsBean> validateActiveTaskStatIds(
			String customStudyId,
			List<ActiveStatisticsBean> activeStatisticsBeans) {
		logger.info("StudyActiveTasksDAOImpl - validateActiveTaskStatIds() - Starts");
		Session session = null;
		List<String> ids = new ArrayList<>();
		String subString = "";
		String queryString = "";
		List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			if (activeStatisticsBeans != null
					&& !activeStatisticsBeans.isEmpty()
					&& StringUtils.isNotEmpty(customStudyId)) {
				if (!activeStatisticsBeans.get(0).getId().contains("static")) {
					for (ActiveStatisticsBean activeStatisticsBean : activeStatisticsBeans) {
						if (StringUtils.isNotEmpty(activeStatisticsBean
								.getIdname()))
							ids.add(activeStatisticsBean.getIdname());
					}
					if (!ids.isEmpty()) {
						subString = "AND attributeValueId NOT IN("
								+ StringUtils.join(ids, ',') + ")";
					}
				}
				// checking each statistics data validate and get which one have
				// duplicate value
				for (ActiveStatisticsBean activeStatisticsBean : activeStatisticsBeans) {
					if (!activeStatisticsBean.getDbVal().equalsIgnoreCase(
							activeStatisticsBean.getIdVal())) {
						queryString = "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId IN "
								+ "(select id From StudyBo SBO WHERE customStudyId='"
								+ customStudyId
								+ "')) and identifierNameStat ='"
								+ activeStatisticsBean.getIdVal()
								+ "' "
								+ subString + "";
						activeTaskAtrributeValuesBos = session.createQuery(
								queryString).list();
						if (activeTaskAtrributeValuesBos != null
								&& !activeTaskAtrributeValuesBos.isEmpty()) {
							activeStatisticsBean.setType(true);
							break;
						} else {
							activeStatisticsBean.setType(false);
						}
					} else {
						activeStatisticsBean.setType(false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksDAOImpl - validateActiveTaskStatIds() - ERROR",
					e);
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}
		}
		logger.info("StudyActiveTasksDAOImpl - validateActiveTaskStatIds() - Ends");
		return activeStatisticsBeans;
	}

}
