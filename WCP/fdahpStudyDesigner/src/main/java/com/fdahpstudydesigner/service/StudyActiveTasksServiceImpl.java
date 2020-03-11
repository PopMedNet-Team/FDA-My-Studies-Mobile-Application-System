/**
 *
 */
package com.fdahpstudydesigner.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdahpstudydesigner.bean.ActiveStatisticsBean;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.dao.StudyActiveTasksDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * @author BTC
 *
 */
@Service
public class StudyActiveTasksServiceImpl implements StudyActiveTasksService {

	private static Logger logger = Logger
			.getLogger(StudyActiveTasksServiceImpl.class);

	@Autowired
	private StudyActiveTasksDAO studyActiveTasksDAO;

	/**
	 * deleting of Active task in Study
	 * 
	 * @author BTC
	 * @param Integer
	 *            , activeTaskInfoId
	 * @param Integer
	 *            , studyId
	 * @return String, SUCCESS or FAILURE
	 */
	@Override
	public String deleteActiveTask(Integer activeTaskInfoId, Integer studyId,
			SessionObject sesObj, String customStudyId) {
		logger.info("StudyServiceImpl - deleteActiveTask() - Starts");
		String message = null;
		ActiveTaskBo activeTaskBo = null;
		try {
			activeTaskBo = studyActiveTasksDAO.getActiveTaskById(
					activeTaskInfoId, customStudyId);
			if (activeTaskBo != null) {
				message = studyActiveTasksDAO.deleteActiveTask(activeTaskBo,
						sesObj, customStudyId);
			}
		} catch (Exception e) {
			logger.error("StudyServiceImpl - deleteActiveTask() - Error", e);
		}
		logger.info("StudyServiceImpl - deleteActiveTask() - Ends");
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
	@Override
	public ActiveTaskBo getActiveTaskById(Integer ativeTaskId,
			String customStudyId) {
		logger.info("StudyActiveTasksServiceImpl - getActiveTaskById() - Starts");
		ActiveTaskBo activeTask = null;
		try {
			activeTask = studyActiveTasksDAO.getActiveTaskById(ativeTaskId,
					customStudyId);
			if (activeTask != null) {
				if (activeTask.getActiveTaskCustomScheduleBo() != null
						&& !activeTask.getActiveTaskCustomScheduleBo()
								.isEmpty()) {
					for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo : activeTask
							.getActiveTaskCustomScheduleBo()) {

						if (StringUtils.isNotBlank(activeTaskCustomScheduleBo
								.getFrequencyStartDate())) {
							activeTaskCustomScheduleBo
									.setFrequencyStartDate(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskCustomScheduleBo
													.getFrequencyStartDate(),
											FdahpStudyDesignerConstants.DB_SDF_DATE,
											FdahpStudyDesignerConstants.UI_SDF_DATE));
						}
						if (StringUtils.isNotBlank(activeTaskCustomScheduleBo
								.getFrequencyEndDate())) {
							activeTaskCustomScheduleBo
									.setFrequencyEndDate(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskCustomScheduleBo
													.getFrequencyEndDate(),
											FdahpStudyDesignerConstants.DB_SDF_DATE,
											FdahpStudyDesignerConstants.UI_SDF_DATE));
						}
						if (StringUtils.isNotBlank(activeTaskCustomScheduleBo
								.getFrequencyTime())) {
							activeTaskCustomScheduleBo
									.setFrequencyTime(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskCustomScheduleBo
													.getFrequencyTime(),
											FdahpStudyDesignerConstants.UI_SDF_TIME,
											FdahpStudyDesignerConstants.SDF_TIME));
						}
					}
				}
				if (activeTask.getActiveTaskFrequenciesList() != null
						&& !activeTask.getActiveTaskFrequenciesList().isEmpty()) {
					for (ActiveTaskFrequencyBo activeTaskFrequencyBo : activeTask
							.getActiveTaskFrequenciesList()) {
						if (StringUtils.isNotBlank(activeTaskFrequencyBo
								.getFrequencyDate())) {
							activeTaskFrequencyBo
									.setFrequencyDate(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskFrequencyBo
													.getFrequencyDate(),
											FdahpStudyDesignerConstants.DB_SDF_DATE,
											FdahpStudyDesignerConstants.UI_SDF_DATE));
						}
						if (StringUtils.isNotBlank(activeTaskFrequencyBo
								.getFrequencyTime())) {
							activeTaskFrequencyBo
									.setFrequencyTime(FdahpStudyDesignerUtil.getFormattedDate(
											activeTaskFrequencyBo
													.getFrequencyTime(),
											FdahpStudyDesignerConstants.UI_SDF_TIME,
											FdahpStudyDesignerConstants.SDF_TIME));
						}
					}
				}
				if (activeTask.getActiveTaskFrequenciesBo() != null
						&& StringUtils.isNotBlank(activeTask
								.getActiveTaskFrequenciesBo()
								.getFrequencyDate())) {
					activeTask.getActiveTaskFrequenciesBo().setFrequencyDate(
							FdahpStudyDesignerUtil.getFormattedDate(activeTask
									.getActiveTaskFrequenciesBo()
									.getFrequencyDate(),
									FdahpStudyDesignerConstants.DB_SDF_DATE,
									FdahpStudyDesignerConstants.UI_SDF_DATE));
				}
				if (activeTask.getActiveTaskFrequenciesBo() != null
						&& StringUtils.isNotBlank(activeTask
								.getActiveTaskFrequenciesBo()
								.getFrequencyTime())) {
					activeTask.getActiveTaskFrequenciesBo().setFrequencyTime(
							FdahpStudyDesignerUtil.getFormattedDate(activeTask
									.getActiveTaskFrequenciesBo()
									.getFrequencyTime(),
									FdahpStudyDesignerConstants.UI_SDF_TIME,
									FdahpStudyDesignerConstants.SDF_TIME));
				}
				if (StringUtils.isNotBlank(activeTask
						.getActiveTaskLifetimeEnd())) {
					activeTask.setActiveTaskLifetimeEnd(FdahpStudyDesignerUtil
							.getFormattedDate(
									activeTask.getActiveTaskLifetimeEnd(),
									FdahpStudyDesignerConstants.DB_SDF_DATE,
									FdahpStudyDesignerConstants.UI_SDF_DATE));
				}
				if (StringUtils.isNotBlank(activeTask
						.getActiveTaskLifetimeStart())) {
					activeTask
							.setActiveTaskLifetimeStart(FdahpStudyDesignerUtil.getFormattedDate(
									activeTask.getActiveTaskLifetimeStart(),
									FdahpStudyDesignerConstants.DB_SDF_DATE,
									FdahpStudyDesignerConstants.UI_SDF_DATE));
				}

			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - getActiveTaskById() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - getActiveTaskById() - Ends");
		return activeTask;
	}

	/**
	 * to get all static formulas in acive task
	 * 
	 * @author BTC
	 * @return {@link List<ActivetaskFormulaBo>}
	 */
	@Override
	public List<ActivetaskFormulaBo> getActivetaskFormulas() {
		logger.info("StudyActiveTasksServiceImpl - getActivetaskFormulas() - Starts");
		List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
		try {
			activetaskFormulaList = studyActiveTasksDAO.getActivetaskFormulas();
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - getActivetaskFormulas() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - getActivetaskFormulas() - Ends");
		return activetaskFormulaList;
	}

	/**
	 * get all the field names of active task based on of activeTaskType
	 * 
	 * @author BTC
	 * @return {@link List<ActiveTaskMasterAttributeBo>}
	 */
	@Override
	public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
			String activeTaskType) {
		logger.info("StudyActiveTasksServiceImpl - getActiveTaskMasterAttributesByType() - Starts");
		List<ActiveTaskMasterAttributeBo> taskMasterAttributeBos = new ArrayList<>();
		try {
			taskMasterAttributeBos = studyActiveTasksDAO
					.getActiveTaskMasterAttributesByType(activeTaskType);
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - getActiveTaskMasterAttributesByType() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - getActiveTaskMasterAttributesByType() - Ends");
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
	@Override
	public List<ActiveTaskListBo> getAllActiveTaskTypes(String platformType) {
		logger.info("StudyActiveTasksServiceImpl - getAllActiveTaskTypes() - Starts");
		List<ActiveTaskListBo> activeTaskListBos = new ArrayList<>();
		try {
			activeTaskListBos = studyActiveTasksDAO
					.getAllActiveTaskTypes(platformType);
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - getAllActiveTaskTypes() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - getAllActiveTaskTypes() - Ends");
		return activeTaskListBos;
	}

	/**
	 * to get all static statistic images
	 * 
	 * @author BTC
	 * @return {@link List<StatisticImageListBo>}
	 */
	@Override
	public List<StatisticImageListBo> getStatisticImages() {
		logger.info("StudyActiveTasksServiceImpl - getStatisticImages() - Starts");
		List<StatisticImageListBo> statisticImageListBos = new ArrayList<>();
		try {
			statisticImageListBos = studyActiveTasksDAO.getStatisticImages();
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - getStatisticImages() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - getStatisticImages() - Ends");
		return statisticImageListBos;
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
	@Override
	public List<ActiveTaskBo> getStudyActiveTasksByStudyId(String studyId,
			Boolean isLive) {
		logger.info("StudyActiveTasksServiceImpl - getStudyActiveTasksByStudyId() - Starts");
		List<ActiveTaskBo> activeTasks = null;
		try {
			activeTasks = studyActiveTasksDAO.getStudyActiveTasksByStudyId(
					studyId, isLive);
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - getStudyActiveTasksByStudyId() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - getStudyActiveTasksByStudyId() - Ends");
		return activeTasks;
	}

	/**
	 * Add or update all type of active task content (The Fetal Kick Counter
	 * task/Tower of Hanoi/Spatial Memory Task)
	 *
	 * @author BTC
	 * @param activeTaskBo
	 *            , {@link ActiveTaskBo}
	 * @param sessionObject
	 *            , {@link SessionObject}
	 * @param String
	 *            , customStudyId
	 * @return {@link ActiveTaskBo}
	 *
	 */
	@Override
	public ActiveTaskBo saveOrUpdateActiveTask(ActiveTaskBo activeTaskBo,
			SessionObject sessionObject, String customStudyId) {
		logger.info("StudyActiveTasksServiceImpl - saveOrUpdateActiveTask() - Starts");
		ActiveTaskBo updateActiveTaskBo = null;
		try {
			if (activeTaskBo != null) {
				if (activeTaskBo.getId() != null) {
					updateActiveTaskBo = studyActiveTasksDAO.getActiveTaskById(
							activeTaskBo.getId(), customStudyId);
					updateActiveTaskBo.setModifiedBy(sessionObject.getUserId());
					updateActiveTaskBo.setModifiedDate(FdahpStudyDesignerUtil
							.getCurrentDateTime());
					if (updateActiveTaskBo.getIsDuplicate() != null) {
						updateActiveTaskBo.setIsDuplicate(updateActiveTaskBo
								.getIsDuplicate());
					}
				} else {
					updateActiveTaskBo = new ActiveTaskBo();
					updateActiveTaskBo.setStudyId(activeTaskBo.getStudyId());
					updateActiveTaskBo.setTaskTypeId(activeTaskBo
							.getTaskTypeId());
					updateActiveTaskBo.setCreatedBy(sessionObject.getUserId());
					updateActiveTaskBo.setCreatedDate(FdahpStudyDesignerUtil
							.getCurrentDateTime());
					updateActiveTaskBo.setDisplayName(StringUtils
							.isEmpty(activeTaskBo.getDisplayName()) ? ""
							: activeTaskBo.getDisplayName());
					updateActiveTaskBo.setShortTitle(StringUtils
							.isEmpty(activeTaskBo.getShortTitle()) ? ""
							: activeTaskBo.getShortTitle());
					updateActiveTaskBo.setInstruction(StringUtils
							.isEmpty(activeTaskBo.getInstruction()) ? ""
							: activeTaskBo.getInstruction());
					updateActiveTaskBo.setTaskAttributeValueBos(activeTaskBo
							.getTaskAttributeValueBos());
				}
				updateActiveTaskBo.setStudyId(activeTaskBo.getStudyId());
				updateActiveTaskBo.setTaskTypeId(activeTaskBo.getTaskTypeId());
				updateActiveTaskBo.setDisplayName(StringUtils
						.isEmpty(activeTaskBo.getDisplayName()) ? ""
						: activeTaskBo.getDisplayName());
				updateActiveTaskBo.setShortTitle(StringUtils
						.isEmpty(activeTaskBo.getShortTitle()) ? ""
						: activeTaskBo.getShortTitle());
				updateActiveTaskBo.setInstruction(StringUtils
						.isEmpty(activeTaskBo.getInstruction()) ? ""
						: activeTaskBo.getInstruction());
				updateActiveTaskBo.setTaskAttributeValueBos(activeTaskBo
						.getTaskAttributeValueBos());
				updateActiveTaskBo.setAction(activeTaskBo.isAction());
				updateActiveTaskBo.setButtonText(activeTaskBo.getButtonText());
				if (activeTaskBo.getButtonText().equalsIgnoreCase(
						FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
					updateActiveTaskBo.setIsChange(1);
				} else {
					updateActiveTaskBo.setIsChange(0);
				}
				updateActiveTaskBo.setActive(1);
				updateActiveTaskBo = studyActiveTasksDAO
						.saveOrUpdateActiveTaskInfo(updateActiveTaskBo,
								sessionObject, customStudyId);
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - saveOrUpdateActiveTask() - Error",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - saveOrUpdateActiveTask() - Ends");
		return updateActiveTaskBo;
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
		logger.info("StudyQuestionnaireServiceImpl - saveORUpdateQuestionnaire - Starts");
		ActiveTaskBo addActiveTaskeBo = null;
		try {
			if (null != activeTaskBo) {
				if (activeTaskBo.getId() != null) {
					addActiveTaskeBo = studyActiveTasksDAO.getActiveTaskById(
							activeTaskBo.getId(), customStudyId);
				} else {
					addActiveTaskeBo = new ActiveTaskBo();
				}
				if (activeTaskBo.getStudyId() != null) {
					addActiveTaskeBo.setStudyId(activeTaskBo.getStudyId());
				}
				if (activeTaskBo.getFrequency() != null) {
					addActiveTaskeBo.setFrequency(activeTaskBo.getFrequency());
				}
				if (activeTaskBo.getScheduleType() != null) {
					addActiveTaskeBo.setScheduleType(activeTaskBo
							.getScheduleType());
				}
				if (activeTaskBo.getAnchorDateId() != null) {
					addActiveTaskeBo.setAnchorDateId(activeTaskBo
							.getAnchorDateId());
				}
				if (activeTaskBo.getFrequency() != null
						&& !activeTaskBo
								.getFrequency()
								.equalsIgnoreCase(
										FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
					if (StringUtils.isNotBlank(activeTaskBo
							.getActiveTaskLifetimeStart())
							&& !("NA").equalsIgnoreCase(activeTaskBo
									.getActiveTaskLifetimeStart())) {
						addActiveTaskeBo
								.setActiveTaskLifetimeStart(FdahpStudyDesignerUtil.getFormattedDate(
										activeTaskBo
												.getActiveTaskLifetimeStart(),
										FdahpStudyDesignerConstants.UI_SDF_DATE,
										FdahpStudyDesignerConstants.SD_DATE_FORMAT));
					} else {
						addActiveTaskeBo.setActiveTaskLifetimeStart(null);
					}
					if (StringUtils.isNotBlank(activeTaskBo
							.getActiveTaskLifetimeEnd())
							&& !("NA").equalsIgnoreCase(activeTaskBo
									.getActiveTaskLifetimeEnd())) {
						addActiveTaskeBo
								.setActiveTaskLifetimeEnd(FdahpStudyDesignerUtil.getFormattedDate(
										activeTaskBo.getActiveTaskLifetimeEnd(),
										FdahpStudyDesignerConstants.UI_SDF_DATE,
										FdahpStudyDesignerConstants.SD_DATE_FORMAT));
					} else {
						addActiveTaskeBo.setActiveTaskLifetimeEnd(null);
					}
				}
				if (activeTaskBo.getTitle() != null) {
					addActiveTaskeBo.setTitle(activeTaskBo.getTitle());
				}
				if (activeTaskBo.getCreatedDate() != null) {
					addActiveTaskeBo.setCreatedDate(activeTaskBo
							.getCreatedDate());
				}
				if (activeTaskBo.getCreatedBy() != null) {
					addActiveTaskeBo.setCreatedBy(activeTaskBo.getCreatedBy());
				}
				if (activeTaskBo.getModifiedDate() != null) {
					addActiveTaskeBo.setModifiedDate(activeTaskBo
							.getModifiedDate());
				}
				if (activeTaskBo.getModifiedBy() != null) {
					addActiveTaskeBo
							.setModifiedBy(activeTaskBo.getModifiedBy());
				}
				if (activeTaskBo.getRepeatActiveTask() != null) {
					addActiveTaskeBo.setRepeatActiveTask(activeTaskBo
							.getRepeatActiveTask());
				}
				if (activeTaskBo.getDayOfTheWeek() != null) {
					addActiveTaskeBo.setDayOfTheWeek(activeTaskBo
							.getDayOfTheWeek());
				}
				if (activeTaskBo.getType() != null) {
					addActiveTaskeBo.setType(activeTaskBo.getType());
				}
				if(activeTaskBo.getScheduleType()!=null && !activeTaskBo.getScheduleType().isEmpty()) {
					addActiveTaskeBo.setScheduleType(activeTaskBo.getScheduleType());
				}
				if (activeTaskBo.getFrequency() != null) {
					if (!activeTaskBo.getFrequency().equalsIgnoreCase(
							activeTaskBo.getPreviousFrequency())) {
						addActiveTaskeBo
								.setActiveTaskCustomScheduleBo(activeTaskBo
										.getActiveTaskCustomScheduleBo());
						addActiveTaskeBo
								.setActiveTaskFrequenciesList(activeTaskBo
										.getActiveTaskFrequenciesList());
						addActiveTaskeBo
								.setActiveTaskFrequenciesBo(activeTaskBo
										.getActiveTaskFrequenciesBo());
						if (activeTaskBo
								.getFrequency()
								.equalsIgnoreCase(
										FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)
								&& activeTaskBo.getActiveTaskFrequenciesBo() != null) {
							if (!activeTaskBo.getActiveTaskFrequenciesBo()
									.getIsStudyLifeTime()) {
								if (StringUtils.isNotBlank(activeTaskBo
										.getActiveTaskLifetimeEnd())
										&& !("NA")
												.equalsIgnoreCase(activeTaskBo
														.getActiveTaskLifetimeEnd())) {
									addActiveTaskeBo
											.setActiveTaskLifetimeEnd(FdahpStudyDesignerUtil.getFormattedDate(
													activeTaskBo
															.getActiveTaskLifetimeEnd(),
													FdahpStudyDesignerConstants.UI_SDF_DATE,
													FdahpStudyDesignerConstants.SD_DATE_FORMAT));
								}
							}
						}
					} else {
						if (activeTaskBo.getActiveTaskCustomScheduleBo() != null
								&& !activeTaskBo
										.getActiveTaskCustomScheduleBo()
										.isEmpty()) {
							addActiveTaskeBo
									.setActiveTaskCustomScheduleBo(activeTaskBo
											.getActiveTaskCustomScheduleBo());
						}
						if (activeTaskBo.getActiveTaskFrequenciesList() != null
								&& !activeTaskBo.getActiveTaskFrequenciesList()
										.isEmpty()) {
							addActiveTaskeBo
									.setActiveTaskFrequenciesList(activeTaskBo
											.getActiveTaskFrequenciesList());
						}
						if (activeTaskBo.getActiveTaskFrequenciesBo() != null) {
							if (activeTaskBo
									.getFrequency()
									.equalsIgnoreCase(
											FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
								if (!activeTaskBo.getActiveTaskFrequenciesBo()
										.getIsStudyLifeTime()) {
									if (StringUtils.isNotBlank(activeTaskBo
											.getActiveTaskLifetimeEnd())
											&& !("NA")
													.equalsIgnoreCase(activeTaskBo
															.getActiveTaskLifetimeEnd())) {
										addActiveTaskeBo
												.setActiveTaskLifetimeEnd(FdahpStudyDesignerUtil.getFormattedDate(
														activeTaskBo
																.getActiveTaskLifetimeEnd(),
														FdahpStudyDesignerConstants.UI_SDF_DATE,
														FdahpStudyDesignerConstants.SD_DATE_FORMAT));
									}
								}
							}
							addActiveTaskeBo
									.setActiveTaskFrequenciesBo(activeTaskBo
											.getActiveTaskFrequenciesBo());
						}
					}
				}
				if (activeTaskBo.getPreviousFrequency() != null) {
					addActiveTaskeBo.setPreviousFrequency(activeTaskBo
							.getPreviousFrequency());
				}
				addActiveTaskeBo.setActive(1);
				addActiveTaskeBo = studyActiveTasksDAO.saveOrUpdateActiveTask(
						addActiveTaskeBo, customStudyId);
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTaskServiceImpl - saveORUpdateQuestionnaire - Error",
					e);
		}
		logger.info("StudyQuestionnaireServiceImpl - saveORUpdateQuestionnaire - Ends");
		return addActiveTaskeBo;
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
	@Override
	public boolean validateActiveTaskAttrById(Integer studyId,
			String activeTaskAttName, String activeTaskAttIdVal,
			String activeTaskAttIdName, String customStudyId) {
		logger.info("StudyActiveTasksServiceImpl - validateActiveTaskAttrById() - Starts");
		boolean valid = false;
		try {
			if (studyId != null && StringUtils.isNotEmpty(activeTaskAttName)
					&& StringUtils.isNotEmpty(activeTaskAttIdVal)
					&& StringUtils.isNotEmpty(activeTaskAttIdName)) {
				valid = studyActiveTasksDAO.validateActiveTaskAttrById(studyId,
						activeTaskAttName, activeTaskAttIdVal,
						activeTaskAttIdName, customStudyId);
			}
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - validateActiveTaskAttrById() - ERROR ",
					e);
		}

		logger.info("StudyActiveTasksServiceImpl - validateActiveTaskAttrById() - Starts");
		return valid;
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
	@Override
	public List<ActiveStatisticsBean> validateActiveTaskStatIds(
			String customStudyId,
			List<ActiveStatisticsBean> activeStatisticsBeans) {
		logger.info("StudyActiveTasksServiceImpl - validateActiveTaskStatIds() - Starts");
		List<ActiveStatisticsBean> statisticsBeans = null;
		try {
			statisticsBeans = studyActiveTasksDAO.validateActiveTaskStatIds(
					customStudyId, activeStatisticsBeans);
		} catch (Exception e) {
			logger.error(
					"StudyActiveTasksServiceImpl - validateActiveTaskStatIds() - ERROR ",
					e);
		}
		logger.info("StudyActiveTasksServiceImpl - validateActiveTaskStatIds() - Ends");
		return statisticsBeans;
	}
}
