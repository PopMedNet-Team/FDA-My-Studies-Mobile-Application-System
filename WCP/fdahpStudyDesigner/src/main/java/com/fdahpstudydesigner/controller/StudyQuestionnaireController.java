package com.fdahpstudydesigner.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fdahpstudydesigner.bean.FormulaInfoBean;
import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.service.StudyActiveTasksService;
import com.fdahpstudydesigner.service.StudyQuestionnaireService;
import com.fdahpstudydesigner.service.StudyService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 *
 * @author BTC
 *
 */
@Controller
public class StudyQuestionnaireController {
	private static Logger logger = Logger
			.getLogger(StudyQuestionnaireController.class.getName());

	@Autowired
	private StudyActiveTasksService studyActiveTasksService;

	@Autowired
	private StudyQuestionnaireService studyQuestionnaireService;

	@Autowired
	private StudyService studyService;

	/**
	 * Admin want copy the already existed question into the same study admin
	 * has to click the copy icon in the questionnaire list.It will copy the
	 * existed questionnaire into the study with out questionnaire short title
	 * because the short title will be unique across the study
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 *
	 */
	@RequestMapping("/adminStudies/copyQuestionnaire.do")
	public ModelAndView copyStudyQuestionnaire(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - saveOrUpdateFormQuestion - Starts");
		ModelAndView mav = new ModelAndView("instructionsStepPage");
		ModelMap map = new ModelMap();
		QuestionnaireBo copyQuestionnaireBo = null;
		String customStudyId = "";
		String studyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");

				if (StringUtils.isNotEmpty(questionnaireId)
						&& StringUtils.isNotEmpty(customStudyId)) {
					copyQuestionnaireBo = studyQuestionnaireService
							.copyStudyQuestionnaireBo(
									Integer.valueOf(questionnaireId),
									customStudyId, sesObj);
				}
				if (copyQuestionnaireBo != null) {
					request.getSession().setAttribute(
							sessionStudyCount + "actionType", "edit");
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG,
							"Questionnaire copyied successfully.");
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							String.valueOf(copyQuestionnaireBo.getId()));
					map.addAttribute("_S", sessionStudyCount);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					mav = new ModelAndView(
							"redirect:/adminStudies/viewQuestionnaire.do", map);
				} else {
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG,
							"Questionnaire not copyied successfully.");
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/viewStudyQuestionnaires.do",
							map);
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveOrUpdateFormQuestion - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveOrUpdateFormQuestion - Ends");
		return mav;
	}

	/**
	 * Form step contains group of questions.Admin can delete the questions
	 * using this method and we will return the list of remaining question to
	 * refresh the list
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String : Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/deleteFormQuestion.do", method = RequestMethod.POST)
	public void deleteFormQuestionInfo(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - deleteFormQuestionInfo - Starts");
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		QuestionnairesStepsBo questionnairesStepsBo = null;
		Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject questionnaireJsonObject = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String formId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("formId")) ? "" : request
						.getParameter("formId");
				String questionId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionId")) ? "" : request
						.getParameter("questionId");
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				String questionnairesId = FdahpStudyDesignerUtil
						.isEmpty(request.getParameter("questionnairesId")) ? ""
						: request.getParameter("questionnairesId");
				if (!formId.isEmpty() && !questionId.isEmpty()) {
					message = studyQuestionnaireService.deleteFromStepQuestion(
							Integer.valueOf(formId),
							Integer.valueOf(questionId), sesObj, customStudyId);
					if (message
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						questionnairesStepsBo = studyQuestionnaireService
								.getQuestionnaireStep(Integer.valueOf(formId),
										FdahpStudyDesignerConstants.FORM_STEP,
										null, customStudyId,
										Integer.valueOf(questionnairesId));
						if (questionnairesStepsBo != null) {
							questionnairesStepsBo
									.setType(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE);
							studyQuestionnaireService
									.saveOrUpdateFromStepQuestionnaire(
											questionnairesStepsBo, sesObj,
											customStudyId);
							qTreeMap = questionnairesStepsBo
									.getFormQuestionMap();
							questionnaireJsonObject = new JSONObject(
									mapper.writeValueAsString(qTreeMap));
							jsonobject.put("questionnaireJsonObject",
									questionnaireJsonObject);
							if (qTreeMap != null) {
								boolean isDone = true;
								if (!qTreeMap.isEmpty()) {
									for (Entry<Integer, QuestionnaireStepBean> entryKey : qTreeMap
											.entrySet()) {
										if (!entryKey.getValue().getStatus()) {
											isDone = false;
											break;
										}
									}
								}
								jsonobject.put("isDone", isDone);
							}
						}
						String studyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.STUDY_ID);
						if (StringUtils.isNotEmpty(studyId)) {
							studyService.markAsCompleted(
									Integer.valueOf(studyId),
									FdahpStudyDesignerConstants.QUESTIONNAIRE,
									false, sesObj, customStudyId);
						}
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - deleteFormQuestionInfo - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - deleteFormQuestionInfo - Ends");
	}

	/**
	 * Deleting of an Questionnaire in Study
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 *
	 */
	@RequestMapping(value = "/adminStudies/deleteQuestionnaire.do", method = RequestMethod.POST)
	public void deleteQuestionnaireInfo(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - deleteQuestionnaireInfo - Starts");
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		String message = FdahpStudyDesignerConstants.SUCCESS;
		List<QuestionnaireBo> questionnaires = null;
		JSONArray questionnaireJsonArray = null;
		ObjectMapper mapper = new ObjectMapper();
		String customStudyId = "";
		String actMsg = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				String studyId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) == true ? ""
						: request
								.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				if (!studyId.isEmpty() && !questionnaireId.isEmpty()) {
					message = studyQuestionnaireService.deletQuestionnaire(
							Integer.valueOf(studyId),
							Integer.valueOf(questionnaireId), sesObj,
							customStudyId);
					questionnaires = studyQuestionnaireService
							.getStudyQuestionnairesByStudyId(studyId, false);
					if (questionnaires != null && !questionnaires.isEmpty()) {
						questionnaireJsonArray = new JSONArray(
								mapper.writeValueAsString(questionnaires));
						jsonobject.put(
								FdahpStudyDesignerConstants.QUESTIONNAIRE_LIST,
								questionnaireJsonArray);
					}
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					boolean markAsComplete = true;
					actMsg = studyService
							.validateActivityComplete(
									studyId,
									FdahpStudyDesignerConstants.ACTIVITY_TYPE_QUESTIONNAIRE);
					if (!actMsg
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS))
						markAsComplete = false;
					jsonobject.put("markAsComplete", markAsComplete);
					jsonobject.put(
							FdahpStudyDesignerConstants.ACTIVITY_MESSAGE,
							actMsg);
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - deleteQuestionnaireInfo - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - deleteQuestionnaireInfo - Ends");
	}

	/**
	 * Delete of an questionnaire step(Instruction,Question,Form) which are
	 * listed in questionnaire.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/deleteQuestionnaireStep.do", method = RequestMethod.POST)
	public void deleteQuestionnaireStepInfo(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - deleteQuestionnaireStepInfo - Starts");
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		QuestionnaireBo questionnaireBo = null;
		Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject questionnaireJsonObject = null;
		String customStudyId = "";
		boolean isAnchorQuestionnaire = false;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				String stepId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("stepId")) ? "" : request
						.getParameter("stepId");
				String stepType = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("stepType")) ? "" : request
						.getParameter("stepType");
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				if (!stepId.isEmpty() && !questionnaireId.isEmpty()
						&& !stepType.isEmpty()) {
					message = studyQuestionnaireService
							.deleteQuestionnaireStep(Integer.valueOf(stepId),
									Integer.valueOf(questionnaireId), stepType,
									sesObj, customStudyId);
					if (message
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						questionnaireBo = studyQuestionnaireService
								.getQuestionnaireById(
										Integer.valueOf(questionnaireId),
										customStudyId);
						if (questionnaireBo != null) {
							questionnaireBo.setStatus(false);
							questionnaireBo
									.setType(FdahpStudyDesignerConstants.CONTENT);
							studyQuestionnaireService
									.saveOrUpdateQuestionnaire(questionnaireBo,
											sesObj, customStudyId);
							qTreeMap = studyQuestionnaireService
									.getQuestionnaireStepList(questionnaireBo
											.getId());
							questionnaireJsonObject = new JSONObject(
									mapper.writeValueAsString(qTreeMap));
							jsonobject.put("questionnaireJsonObject",
									questionnaireJsonObject);
							if (qTreeMap != null) {
								boolean isDone = true;
								for (Entry<Integer, QuestionnaireStepBean> entry : qTreeMap
										.entrySet()) {
									QuestionnaireStepBean questionnaireStepBean = entry
											.getValue();
									if (questionnaireStepBean.getStatus() != null
											&& !questionnaireStepBean
													.getStatus()) {
										isDone = false;
										break;
									}
									if (entry.getValue().getFromMap() != null) {
										if (!entry.getValue().getFromMap()
												.isEmpty()) {
											for (Entry<Integer, QuestionnaireStepBean> entryKey : entry
													.getValue().getFromMap()
													.entrySet()) {
												if (!entryKey.getValue()
														.getStatus()) {
													isDone = false;
													break;
												}
											}
										} else {
											isDone = false;
											break;
										}
									}
								}
								jsonobject.put("isDone", isDone);
							}
							isAnchorQuestionnaire = studyQuestionnaireService.isAnchorDateExistByQuestionnaire(Integer.valueOf(questionnaireId));
							jsonobject.put("isAnchorQuestionnaire", isAnchorQuestionnaire);
						}
						String studyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.STUDY_ID);
						if (StringUtils.isNotEmpty(studyId)) {
							studyService.markAsCompleted(
									Integer.valueOf(studyId),
									FdahpStudyDesignerConstants.QUESTIONNAIRE,
									false, sesObj, customStudyId);
						}
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - deleteQuestionnaireStepInfo - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - deleteQuestionnaireStepInfo - Ends");
	}

	/**
	 * A questionnaire contains the form step.form step carries multiple
	 * questions.Here we described to load the form step of an questionnaire
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 *
	 */
	@RequestMapping("/adminStudies/formStep.do")
	public ModelAndView getFormStepPage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - getFormStepPage - Starts");
		ModelAndView mav = new ModelAndView("formStepPage");
		String sucMsg = "";
		String errMsg = "";
		ModelMap map = new ModelMap();
		QuestionnaireBo questionnaireBo = null;
		QuestionnairesStepsBo questionnairesStepsBo = null;
		StudyBo studyBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				request.getSession().removeAttribute(
						sessionStudyCount + "questionId");
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
				}
				request.getSession().removeAttribute(
						sessionStudyCount + "actionTypeForFormStep");
				String actionType = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("actionType")) ? "" : request
						.getParameter("actionType");
				if (StringUtils.isEmpty(actionType)) {
					actionType = (String) request.getSession().getAttribute(
							sessionStudyCount + "actionType");
				}

				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);

				String actionTypeForQuestionPage = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter("actionTypeForQuestionPage")) ? ""
						: request.getParameter("actionTypeForQuestionPage");
				if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
					actionTypeForQuestionPage = (String) request.getSession()
							.getAttribute(
									sessionStudyCount
											+ "actionTypeForQuestionPage");
					if ("edit".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "edit");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"edit");
					} else if ("view".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "view");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"view");
					} else {
						map.addAttribute("actionTypeForQuestionPage", "add");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"add");
					}
				} else {
					map.addAttribute("actionTypeForQuestionPage",
							actionTypeForQuestionPage);
					request.getSession().setAttribute(
							sessionStudyCount + "actionTypeForQuestionPage",
							actionTypeForQuestionPage);
				}

				String formId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("formId")) ? "" : request
						.getParameter("formId");
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) == true ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.STUDY_ID,
							studyId);
				}
				if (StringUtils.isNotEmpty(studyId)) {
					studyBo = studyService.getStudyById(studyId,
							sesObj.getUserId());
					map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO,
							studyBo);
				}
				if (StringUtils.isEmpty(formId)) {
					formId = (String) request.getSession().getAttribute(
							sessionStudyCount + "formId");
					request.getSession().setAttribute(
							sessionStudyCount + "formId", formId);
				}
				if (StringUtils.isEmpty(questionnaireId)) {
					questionnaireId = (String) request
							.getSession()
							.getAttribute(sessionStudyCount + "questionnaireId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				if (StringUtils.isNotEmpty(questionnaireId) && null != studyBo) {
					request.getSession().removeAttribute(
							sessionStudyCount + "actionType");
					questionnaireBo = studyQuestionnaireService
							.getQuestionnaireById(
									Integer.valueOf(questionnaireId),
									studyBo.getCustomStudyId());
					map.addAttribute("questionnaireBo", questionnaireBo);
					if ("edit".equals(actionType)) {
						map.addAttribute("actionType", "edit");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "edit");
					} else if ("view".equals(actionType)) {
						map.addAttribute("actionType", "view");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "view");
					} else {
						map.addAttribute("actionType", "add");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "add");
					}
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				if (formId != null && !formId.isEmpty() && null != studyBo) {
					questionnairesStepsBo = studyQuestionnaireService
							.getQuestionnaireStep(Integer.valueOf(formId),
									FdahpStudyDesignerConstants.FORM_STEP,
									questionnaireBo.getShortTitle(),
									studyBo.getCustomStudyId(),
									questionnaireBo.getId());
					if (questionnairesStepsBo != null) {
						List<QuestionnairesStepsBo> destionationStepList = studyQuestionnaireService
								.getQuestionnairesStepsList(
										questionnairesStepsBo
												.getQuestionnairesId(),
										questionnairesStepsBo.getSequenceNo());
						map.addAttribute("destinationStepList",
								destionationStepList);
						if (!questionnairesStepsBo.getStatus()
								&& StringUtils.isNotEmpty(studyId)) {
							studyService.markAsCompleted(
									Integer.valueOf(studyId),
									FdahpStudyDesignerConstants.QUESTIONNAIRE,
									false, sesObj, customStudyId);
						}
					}
					map.addAttribute("questionnairesStepsBo",
							questionnairesStepsBo);
					request.getSession().setAttribute(
							sessionStudyCount + "formId", formId);
				}

				map.addAttribute(
						FdahpStudyDesignerConstants.QUESTION_STEP,
						request.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.QUESTION_STEP));
				map.addAttribute("questionnaireId", questionnaireId);
				request.getSession().removeAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.QUESTION_STEP);
				map.addAttribute("_S", sessionStudyCount);
				mav = new ModelAndView("formStepPage", map);
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - getFormStepPage - Error", e);
		}
		logger.info("StudyQuestionnaireController - getFormStepPage - Ends");
		return mav;
	}

	/**
	 * Load the Question page of form step inside questionnaire.Question
	 * contains the question level attributes and response level attributes
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 *
	 */
	@RequestMapping("/adminStudies/formQuestion.do")
	public ModelAndView getFormStepQuestionPage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - getFormStepQuestionPage - Starts");
		ModelAndView mav = new ModelAndView("questionPage");
		String sucMsg = "";
		String errMsg = "";
		ModelMap map = new ModelMap();
		QuestionnaireBo questionnaireBo = null;
		QuestionnairesStepsBo questionnairesStepsBo = null;
		StudyBo studyBo = null;
		QuestionsBo questionsBo = null;
		List<String> timeRangeList = new ArrayList<String>();
		List<StatisticImageListBo> statisticImageList = new ArrayList<>();
		List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
		List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList = new ArrayList<>();
		List<HealthKitKeysInfo> healthKitKeysInfo = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
				}

				request.getSession().removeAttribute(
						sessionStudyCount + "actionTypeForFormStep");
				String actionTypeForQuestionPage = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter("actionTypeForQuestionPage")) ? ""
						: request.getParameter("actionTypeForQuestionPage");
				if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
					actionTypeForQuestionPage = (String) request.getSession()
							.getAttribute(
									sessionStudyCount
											+ "actionTypeForQuestionPage");
				}

				String actionTypeForFormStep = FdahpStudyDesignerUtil
						.isEmpty(request.getParameter("actionTypeForFormStep")) ? ""
						: request.getParameter("actionTypeForFormStep");
				if (StringUtils.isEmpty(actionTypeForFormStep)) {
					actionTypeForFormStep = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount + "actionTypeForFormStep");
					if ("edit".equals(actionTypeForFormStep)) {
						map.addAttribute("actionTypeForFormStep", "edit");
						request.getSession().setAttribute(
								sessionStudyCount + "actionTypeForFormStep",
								"edit");
					} else if ("view".equals(actionTypeForFormStep)) {
						map.addAttribute("actionTypeForFormStep", "view");
						request.getSession().setAttribute(
								sessionStudyCount + "actionTypeForFormStep",
								"view");
					} else {
						map.addAttribute("actionTypeForFormStep", "add");
						request.getSession().setAttribute(
								sessionStudyCount + "actionTypeForFormStep",
								"add");
					}
				} else {
					map.addAttribute("actionTypeForFormStep",
							actionTypeForFormStep);
				}

				String formId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("formId")) ? "" : request
						.getParameter("formId");
				String questionId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionId")) ? "" : request
						.getParameter("questionId");
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.STUDY_ID, studyId);
				}
				if (StringUtils.isNotEmpty(studyId)) {
					studyBo = studyService.getStudyById(studyId,
							sesObj.getUserId());
					boolean isExists = studyQuestionnaireService
							.isAnchorDateExistsForStudy(
									Integer.valueOf(studyId),
									studyBo.getCustomStudyId());
					map.addAttribute("isAnchorDate", isExists);
					map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO,
							studyBo);
				}
				if (StringUtils.isEmpty(formId)) {
					formId = (String) request.getSession().getAttribute(
							sessionStudyCount + "formId");
					request.getSession().setAttribute(
							sessionStudyCount + "formId", formId);
				}
				if (StringUtils.isEmpty(questionId)) {
					questionId = (String) request.getSession().getAttribute(
							sessionStudyCount + "questionId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionId", questionId);
				}
				if (StringUtils.isEmpty(questionnaireId)) {
					questionnaireId = (String) request
							.getSession()
							.getAttribute(sessionStudyCount + "questionnaireId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				if (StringUtils.isNotEmpty(questionnaireId)) {
					request.getSession().removeAttribute(
							sessionStudyCount + "actionTypeForQuestionPage");
					questionnaireBo = studyQuestionnaireService
							.getQuestionnaireById(
									Integer.valueOf(questionnaireId),
									studyBo.getCustomStudyId());
					map.addAttribute("questionnaireBo", questionnaireBo);
					if ("edit".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "edit");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"edit");
					} else if ("view".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "view");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"view");
					} else {
						map.addAttribute("actionTypeForQuestionPage", "add");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"add");
					}
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
					if (questionnaireBo != null
							&& StringUtils.isNotEmpty(questionnaireBo
									.getFrequency())) {
						String frequency = questionnaireBo.getFrequency();
						if (questionnaireBo
								.getFrequency()
								.equalsIgnoreCase(
										FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
							if (questionnaireBo
									.getQuestionnairesFrequenciesList() != null
									&& questionnaireBo
											.getQuestionnairesFrequenciesList()
											.size() > 1) {
								frequency = questionnaireBo.getFrequency();
							} else {
								frequency = FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME;
							}
						}
						timeRangeList = FdahpStudyDesignerUtil
								.getTimeRangeList(frequency);
					}
				}
				if (formId != null && !formId.isEmpty()) {
					questionnairesStepsBo = studyQuestionnaireService
							.getQuestionnaireStep(Integer.valueOf(formId),
									FdahpStudyDesignerConstants.FORM_STEP,
									questionnaireBo.getShortTitle(),
									studyBo.getCustomStudyId(),
									questionnaireBo.getId());
					if (questionId != null && !questionId.isEmpty()) {
						questionsBo = studyQuestionnaireService
								.getQuestionsById(Integer.valueOf(questionId),
										questionnaireBo.getShortTitle(),
										studyBo.getCustomStudyId());
						map.addAttribute("questionsBo", questionsBo);
						request.getSession().setAttribute(
								sessionStudyCount + "questionId", questionId);
						if (questionnairesStepsBo != null) {
							List<QuestionnairesStepsBo> destionationStepList = studyQuestionnaireService
									.getQuestionnairesStepsList(
											questionnairesStepsBo
													.getQuestionnairesId(),
											questionnairesStepsBo
													.getSequenceNo());
							map.addAttribute("destinationStepList",
									destionationStepList);
						}
					}
					map.addAttribute("formId", formId);
					map.addAttribute("questionnairesStepsBo",
							questionnairesStepsBo);
					request.getSession().setAttribute(
							sessionStudyCount + "formId", formId);
				}
				statisticImageList = studyActiveTasksService
						.getStatisticImages();
				activetaskFormulaList = studyActiveTasksService
						.getActivetaskFormulas();
				questionResponseTypeMasterInfoList = studyQuestionnaireService
						.getQuestionReponseTypeList();
				if (studyBo != null) {
					if (studyBo.getPlatform().contains(
							FdahpStudyDesignerConstants.IOS)) {
						healthKitKeysInfo = studyQuestionnaireService
								.getHeanlthKitKeyInfoList();
						map.addAttribute("healthKitKeysInfo", healthKitKeysInfo);
					}
				}
				map.addAttribute("timeRangeList", timeRangeList);
				map.addAttribute("statisticImageList", statisticImageList);
				map.addAttribute("activetaskFormulaList", activetaskFormulaList);
				map.addAttribute("questionResponseTypeMasterInfoList",
						questionResponseTypeMasterInfoList);
				request.getSession().setAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.QUESTION_STEP,
						"Yes");
				map.addAttribute("_S", sessionStudyCount);
				mav = new ModelAndView("questionPage", map);
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - getFormStepQuestionPage - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - getFormStepQuestionPage - Ends");
		return mav;
	}

	/**
	 * Instruction Step Page in Questionnaire.Lays down instructions for the
	 * user in mobile app.Which contains the short title instruction title and
	 * text
	 *
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 *
	 */
	@RequestMapping("/adminStudies/instructionsStep.do")
	public ModelAndView getInstructionsPage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - getInstructionsPage - Starts");
		ModelAndView mav = new ModelAndView("instructionsStepPage");
		String sucMsg = "";
		String errMsg = "";
		ModelMap map = new ModelMap();
		InstructionsBo instructionsBo = null;
		QuestionnaireBo questionnaireBo = null;
		StudyBo studyBo = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
				}
				String instructionId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("instructionId")) == true ? "" : request
						.getParameter("instructionId");
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) == true ? ""
						: request.getParameter("questionnaireId");
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);

				request.getSession().removeAttribute(
						sessionStudyCount + "actionTypeForQuestionPage");
				String actionType = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("actionType")) ? "" : request
						.getParameter("actionType");
				if (StringUtils.isEmpty(actionType)) {
					actionType = (String) request.getSession().getAttribute(
							sessionStudyCount + "actionType");
				}

				String actionTypeForQuestionPage = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter("actionTypeForQuestionPage")) ? ""
						: request.getParameter("actionTypeForQuestionPage");
				if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
					actionTypeForQuestionPage = (String) request.getSession()
							.getAttribute(
									sessionStudyCount
											+ "actionTypeForQuestionPage");
					if ("edit".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "edit");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"edit");
					} else if ("view".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "view");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"view");
					} else {
						map.addAttribute("actionTypeForQuestionPage", "add");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"add");
					}
				} else {
					map.addAttribute("actionTypeForQuestionPage",
							actionTypeForQuestionPage);
					request.getSession().setAttribute(
							sessionStudyCount + "actionTypeForQuestionPage",
							actionTypeForQuestionPage);
				}
				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) == true ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.STUDY_ID,
							studyId);
				}
				if (StringUtils.isNotEmpty(studyId)) {
					studyBo = studyService.getStudyById(studyId,
							sesObj.getUserId());
					map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO,
							studyBo);
				}
				if (StringUtils.isEmpty(instructionId)) {
					instructionId = (String) request.getSession().getAttribute(
							sessionStudyCount + "instructionId");
					request.getSession().setAttribute(
							sessionStudyCount + "instructionId", instructionId);
				}
				if (StringUtils.isEmpty(questionnaireId)) {
					questionnaireId = (String) request
							.getSession()
							.getAttribute(sessionStudyCount + "questionnaireId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				if (StringUtils.isNotEmpty(questionnaireId) && null != studyBo) {
					request.getSession().removeAttribute(
							sessionStudyCount + "actionType");
					questionnaireBo = studyQuestionnaireService
							.getQuestionnaireById(
									Integer.valueOf(questionnaireId),
									studyBo.getCustomStudyId());
					if ("edit".equals(actionType)) {
						map.addAttribute("actionType", "edit");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "edit");
					} else if ("view".equals(actionType)) {
						map.addAttribute("actionType", "view");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "view");
					} else {
						map.addAttribute("actionType", "add");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "add");
					}
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
					map.addAttribute("questionnaireBo", questionnaireBo);
				}
				if (instructionId != null && !instructionId.isEmpty()
						&& null != studyBo) {
					instructionsBo = studyQuestionnaireService
							.getInstructionsBo(Integer.valueOf(instructionId),
									questionnaireBo.getShortTitle(),
									studyBo.getCustomStudyId(),
									questionnaireBo.getId());
					if (instructionsBo != null
							&& instructionsBo.getQuestionnairesStepsBo() != null) {
						List<QuestionnairesStepsBo> questionnairesStepsList = studyQuestionnaireService
								.getQuestionnairesStepsList(instructionsBo
										.getQuestionnairesStepsBo()
										.getQuestionnairesId(), instructionsBo
										.getQuestionnairesStepsBo()
										.getSequenceNo());
						map.addAttribute("destinationStepList",
								questionnairesStepsList);
					}
					map.addAttribute("instructionsBo", instructionsBo);
					request.getSession().setAttribute(
							sessionStudyCount + "instructionId", instructionId);
				}
				map.addAttribute("questionnaireId", questionnaireId);
				map.addAttribute("_S", sessionStudyCount);
				mav = new ModelAndView("instructionsStepPage", map);
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - getInstructionsPage - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - getInstructionsPage - Ends");
		return mav;
	}

	/**
	 * 
	 * Load the Questionnaire page of study with all the
	 * steps(instruction,question,form) with schedule information. Each step
	 * corresponds to one screen on the mobile app.There can be multiple types
	 * of QA in a questionnaire depending on the type of response format
	 * selected per QA.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 *
	 */
	@RequestMapping(value = "/adminStudies/viewQuestionnaire.do")
	public ModelAndView getQuestionnairePage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - getQuestionnairePage - Starts");
		ModelAndView mav = new ModelAndView("questionnairePage");
		ModelMap map = new ModelMap();
		String sucMsg = "";
		String errMsg = "";
		StudyBo studyBo = null;
		QuestionnaireBo questionnaireBo = null;
		Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
		String customStudyId = "";
		List<AnchorDateTypeBo> anchorTypeList = new ArrayList<>();
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				request.getSession().removeAttribute(
						sessionStudyCount + "actionTypeForQuestionPage");
				request.getSession().removeAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.INSTRUCTION_ID);
				request.getSession()
						.removeAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.FORM_ID);
				request.getSession().removeAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.QUESTION_ID);
				request.getSession().removeAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.QUESTION_STEP);
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
				}
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String permission = (String) request.getSession().getAttribute(
						sessionStudyCount + "permission");

				String actionType = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("actionType")) ? "" : request
						.getParameter("actionType");
				if (StringUtils.isEmpty(actionType)) {
					actionType = (String) request.getSession().getAttribute(
							sessionStudyCount + "actionType");
				}
				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.STUDY_ID,
							studyId);
				}
				if (StringUtils.isNotEmpty(studyId)) {
					studyBo = studyService.getStudyById(studyId,
							sesObj.getUserId());
					map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO,
							studyBo);
					anchorTypeList = studyQuestionnaireService.getAnchorTypesByStudyId(studyBo.getCustomStudyId());
					map.addAttribute("anchorTypeList", anchorTypeList);
				}
				if (StringUtils.isEmpty(questionnaireId)) {
					questionnaireId = (String) request
							.getSession()
							.getAttribute(sessionStudyCount + "questionnaireId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				customStudyId = (String) request.getSession().getAttribute(
						FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (null != questionnaireId && !questionnaireId.isEmpty()) {
					questionnaireBo = studyQuestionnaireService
							.getQuestionnaireById(
									Integer.valueOf(questionnaireId),
									studyBo.getCustomStudyId());
					if (questionnaireBo != null) {
						map.addAttribute("customCount", questionnaireBo
								.getQuestionnaireCustomScheduleBo().size());
						map.addAttribute("count", questionnaireBo
								.getQuestionnairesFrequenciesList().size());
						qTreeMap = studyQuestionnaireService
								.getQuestionnaireStepList(questionnaireBo
										.getId());
						if (qTreeMap != null) {
							boolean isDone = true;
							for (Entry<Integer, QuestionnaireStepBean> entry : qTreeMap
									.entrySet()) {
								QuestionnaireStepBean questionnaireStepBean = entry
										.getValue();
								if (questionnaireStepBean.getStatus() != null
										&& !questionnaireStepBean.getStatus()) {
									isDone = false;
									break;
								}
								if (entry.getValue().getFromMap() != null) {
									if (!entry.getValue().getFromMap()
											.isEmpty()) {
										for (Entry<Integer, QuestionnaireStepBean> entryKey : entry
												.getValue().getFromMap()
												.entrySet()) {
											if (!entryKey.getValue()
													.getStatus()) {
												isDone = false;
												break;
											}
										}
									} else {
										isDone = false;
										break;
									}
								}
							}
							map.addAttribute("isDone", isDone);
							if (!isDone && StringUtils.isNotEmpty(studyId)) {
								studyService
										.markAsCompleted(
												Integer.valueOf(studyId),
												FdahpStudyDesignerConstants.QUESTIONNAIRE,
												false, sesObj, customStudyId);
							}
						}
					}
					if ("edit".equals(actionType)) {
						map.addAttribute("actionType", "edit");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "edit");
					} else {
						map.addAttribute("actionType", "view");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "view");
					}
					map.addAttribute("permission", permission);
					map.addAttribute("qTreeMap", qTreeMap);
					map.addAttribute("questionnaireBo", questionnaireBo);
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
					
					boolean isAnchorQuestionnaire = studyQuestionnaireService.isAnchorDateExistByQuestionnaire(Integer.valueOf(questionnaireId));
					map.addAttribute("isAnchorQuestionnaire", isAnchorQuestionnaire);
				}
				if ("add".equals(actionType)) {
					map.addAttribute("actionType", "add");
					request.getSession().setAttribute(
							sessionStudyCount + "actionType", "add");
				}
				map.addAttribute("_S", sessionStudyCount);
				mav = new ModelAndView("questionnairePage", map);
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - getQuestionnairePage - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - getQuestionnairePage - Ends");
		return mav;
	}

	/**
	 * Load the Question step page in questionnaire which contains the question
	 * and answer. Which Carries one QA per screen in Mobile app
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminStudies/questionStep.do")
	public ModelAndView getQuestionStepPage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - getQuestionStepPage - starts");
		ModelAndView mav = new ModelAndView("questionStepPage");
		String sucMsg = "";
		String errMsg = "";
		ModelMap map = new ModelMap();
		QuestionnaireBo questionnaireBo = null;
		QuestionnairesStepsBo questionnairesStepsBo = null;
		StudyBo studyBo = null;
		List<String> timeRangeList = new ArrayList<>();
		List<StatisticImageListBo> statisticImageList = new ArrayList<>();
		List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
		List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList = new ArrayList<>();
		List<HealthKitKeysInfo> healthKitKeysInfo = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
				}
				String questionId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionId")) ? "" : request
						.getParameter("questionId");
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String permission = (String) request.getSession().getAttribute(
						sessionStudyCount + "permission");
				request.getSession().removeAttribute(
						sessionStudyCount + "actionTypeForQuestionPage");
				String actionType = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("actionType")) ? "" : request
						.getParameter("actionType");
				if (StringUtils.isEmpty(actionType)) {
					actionType = (String) request.getSession().getAttribute(
							sessionStudyCount + "actionType");
				}

				String actionTypeForQuestionPage = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter("actionTypeForQuestionPage")) ? ""
						: request.getParameter("actionTypeForQuestionPage");
				if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
					actionTypeForQuestionPage = (String) request.getSession()
							.getAttribute(
									sessionStudyCount
											+ "actionTypeForQuestionPage");
					if ("edit".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "edit");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"edit");
					} else if ("view".equals(actionTypeForQuestionPage)) {
						map.addAttribute("actionTypeForQuestionPage", "view");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"view");
					} else {
						map.addAttribute("actionTypeForQuestionPage", "add");
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ "actionTypeForQuestionPage",
										"add");
					}
				} else {
					map.addAttribute("actionTypeForQuestionPage",
							actionTypeForQuestionPage);
					request.getSession().setAttribute(
							sessionStudyCount + "actionTypeForQuestionPage",
							actionTypeForQuestionPage);
				}

				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.STUDY_ID,
							studyId);
				}
				if (StringUtils.isNotEmpty(studyId)) {
					studyBo = studyService.getStudyById(studyId,
							sesObj.getUserId());
					/*boolean isExists = studyQuestionnaireService
							.isAnchorDateExistsForStudy(
									Integer.valueOf(studyId),
									studyBo.getCustomStudyId());
					map.addAttribute("isAnchorDate", isExists);*/
					map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO,
							studyBo);
				}
				if (StringUtils.isEmpty(questionId)) {
					questionId = (String) request.getSession().getAttribute(
							sessionStudyCount + "questionId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionId", questionId);
				}
				if (StringUtils.isEmpty(questionnaireId)) {
					questionnaireId = (String) request
							.getSession()
							.getAttribute(sessionStudyCount + "questionnaireId");
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				if (StringUtils.isNotEmpty(questionnaireId)) {
					request.getSession().removeAttribute(
							sessionStudyCount + "actionType");
					questionnaireBo = studyQuestionnaireService
							.getQuestionnaireById(
									Integer.valueOf(questionnaireId),
									studyBo.getCustomStudyId());
					map.addAttribute("questionnaireBo", questionnaireBo);
					if (questionnaireBo != null
							&& StringUtils.isNotEmpty(questionnaireBo
									.getFrequency())) {
						String frequency = questionnaireBo.getFrequency();
						if (questionnaireBo
								.getFrequency()
								.equalsIgnoreCase(
										FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
							if (questionnaireBo
									.getQuestionnairesFrequenciesList() != null
									&& questionnaireBo
											.getQuestionnairesFrequenciesList()
											.size() > 1) {
								frequency = questionnaireBo.getFrequency();
							} else {
								frequency = FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME;
							}
						}
						timeRangeList = FdahpStudyDesignerUtil
								.getTimeRangeList(frequency);
					}
					if ("edit".equals(actionType)) {
						map.addAttribute("actionType", "edit");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "edit");
					} else if ("view".equals(actionType)) {
						map.addAttribute("actionType", "view");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "view");
					} else {
						map.addAttribute("actionType", "add");
						request.getSession().setAttribute(
								sessionStudyCount + "actionType", "add");
					}
					request.getSession().setAttribute(
							sessionStudyCount + "questionnaireId",
							questionnaireId);
				}
				if (questionId != null && !questionId.isEmpty()) {
					questionnairesStepsBo = studyQuestionnaireService
							.getQuestionnaireStep(Integer.valueOf(questionId),
									FdahpStudyDesignerConstants.QUESTION_STEP,
									questionnaireBo.getShortTitle(),
									studyBo.getCustomStudyId(),
									questionnaireBo.getId());
					if (questionnairesStepsBo != null) {
						List<QuestionnairesStepsBo> destionationStepList = studyQuestionnaireService
								.getQuestionnairesStepsList(
										questionnairesStepsBo
												.getQuestionnairesId(),
										questionnairesStepsBo.getSequenceNo());
						map.addAttribute("destinationStepList",
								destionationStepList);
					}
					map.addAttribute("questionnairesStepsBo",
							questionnairesStepsBo);
					request.getSession().setAttribute(
							sessionStudyCount + "questionId", questionId);
				}
				statisticImageList = studyActiveTasksService
						.getStatisticImages();
				activetaskFormulaList = studyActiveTasksService
						.getActivetaskFormulas();
				questionResponseTypeMasterInfoList = studyQuestionnaireService
						.getQuestionReponseTypeList();
				if (studyBo != null) {
					if (studyBo.getPlatform().contains(
							FdahpStudyDesignerConstants.IOS)) {
						healthKitKeysInfo = studyQuestionnaireService
								.getHeanlthKitKeyInfoList();
						map.addAttribute("healthKitKeysInfo", healthKitKeysInfo);
					}
				}
				map.addAttribute("permission", permission);
				map.addAttribute("timeRangeList", timeRangeList);
				map.addAttribute("statisticImageList", statisticImageList);
				map.addAttribute("activetaskFormulaList", activetaskFormulaList);
				map.addAttribute("questionnaireId", questionnaireId);
				map.addAttribute("questionResponseTypeMasterInfoList",
						questionResponseTypeMasterInfoList);
				map.addAttribute("_S", sessionStudyCount);
				mav = new ModelAndView("questionStepPage", map);
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - getQuestionStepPage - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - getQuestionStepPage - Ends");
		return mav;
	}

	/**
	 * From step contains the list of questions with default admin created
	 * master order.Admin can manage these orders by reordering the question on
	 * drag and drop of a questions in the list
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String : Success/Failure
	 *
	 */
	@RequestMapping(value = "/adminStudies/reOrderFormQuestions.do", method = RequestMethod.POST)
	public void reOrderFromStepQuestionsInfo(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - reOrderQuestionnaireStepInfo - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			int oldOrderNumber;
			int newOrderNumber;
			if (sesObj != null) {
				String formId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("formId")) ? "" : request
						.getParameter("formId");
				String oldOrderNo = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("oldOrderNumber")) ? "" : request
						.getParameter("oldOrderNumber");
				String newOrderNo = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("newOrderNumber")) ? "" : request
						.getParameter("newOrderNumber");
				if (!formId.isEmpty() && !oldOrderNo.isEmpty()
						&& !newOrderNo.isEmpty()) {
					oldOrderNumber = Integer.valueOf(oldOrderNo);
					newOrderNumber = Integer.valueOf(newOrderNo);
					message = studyQuestionnaireService
							.reOrderFormStepQuestions(Integer.valueOf(formId),
									oldOrderNumber, newOrderNumber);
					if (message
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						String studyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.STUDY_ID);
						String customStudyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
						if (StringUtils.isNotEmpty(studyId)) {
							studyService.markAsCompleted(
									Integer.valueOf(studyId),
									FdahpStudyDesignerConstants.QUESTIONNAIRE,
									false, sesObj, customStudyId);
						}
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - reOrderQuestionnaireStepInfo - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - reOrderQuestionnaireStepInfo - Ends");
	}

	/**
	 * A questionnaire is an ordered set of one or more steps (screens on the
	 * mobile app). The questionnaire by default follows the master order of
	 * steps admin can manage the order of an step.Here we can do the reordering
	 * of an questionnaire steps(Instruction,Question,Form) which are listed on
	 * questionnaire content page.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 *
	 */
	@RequestMapping(value = "/adminStudies/reOrderQuestionnaireStepInfo.do", method = RequestMethod.POST)
	public void reOrderQuestionnaireStepInfo(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - reOrderQuestionnaireStepInfo - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject questionnaireJsonObject = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			int oldOrderNumber = 0;
			int newOrderNumber = 0;
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String oldOrderNo = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("oldOrderNumber")) ? "" : request
						.getParameter("oldOrderNumber");
				String newOrderNo = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("newOrderNumber")) ? "" : request
						.getParameter("newOrderNumber");
				if ((questionnaireId != null && !questionnaireId.isEmpty())
						&& !oldOrderNo.isEmpty() && !newOrderNo.isEmpty()) {
					oldOrderNumber = Integer.valueOf(oldOrderNo);
					newOrderNumber = Integer.valueOf(newOrderNo);
					message = studyQuestionnaireService
							.reOrderQuestionnaireSteps(
									Integer.valueOf(questionnaireId),
									oldOrderNumber, newOrderNumber);
					if (message
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						qTreeMap = studyQuestionnaireService
								.getQuestionnaireStepList(Integer
										.valueOf(questionnaireId));
						if (qTreeMap != null) {
							boolean isDone = true;
							for (Entry<Integer, QuestionnaireStepBean> entry : qTreeMap
									.entrySet()) {
								QuestionnaireStepBean questionnaireStepBean = entry
										.getValue();
								if (questionnaireStepBean.getStatus() != null
										&& !questionnaireStepBean.getStatus()) {
									isDone = false;
									break;
								}
								if (entry.getValue().getFromMap() != null) {
									if (!entry.getValue().getFromMap()
											.isEmpty()) {
										for (Entry<Integer, QuestionnaireStepBean> entryKey : entry
												.getValue().getFromMap()
												.entrySet()) {
											if (!entryKey.getValue()
													.getStatus()) {
												isDone = false;
												break;
											}
										}
									} else {
										isDone = false;
										break;
									}
								}
							}
							jsonobject.put("isDone", isDone);
							questionnaireJsonObject = new JSONObject(
									mapper.writeValueAsString(qTreeMap));
						}
						jsonobject.put("questionnaireJsonObject",
								questionnaireJsonObject);
						String studyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.STUDY_ID);
						String customStudyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
						if (StringUtils.isNotEmpty(studyId)) {
							studyService.markAsCompleted(
									Integer.valueOf(studyId),
									FdahpStudyDesignerConstants.QUESTIONNAIRE,
									false, sesObj, customStudyId);
						}
					}

				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - reOrderQuestionnaireStepInfo - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - reOrderQuestionnaireStepInfo - Ends");
	}

	/**
	 * Here admin will add the from step to the questionnaire which contains the
	 * two sets of attributes. which are step level attribute,form level
	 * attribute. Admin has fill the required fields and click on save request
	 * come here
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String : Success or Failure
	 */
	@RequestMapping(value = "/adminStudies/saveFromStep.do")
	public void saveFormStep(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - saveFormStep - starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		QuestionnairesStepsBo questionnairesStepsBo = null;
		ObjectMapper mapper = new ObjectMapper();
		QuestionnairesStepsBo addQuestionnairesStepsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String questionnaireStepInfo = request
						.getParameter("questionnaireStepInfo");
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (null != questionnaireStepInfo) {
					questionnairesStepsBo = mapper.readValue(
							questionnaireStepInfo, QuestionnairesStepsBo.class);
					if (questionnairesStepsBo != null) {
						if (questionnairesStepsBo.getStepId() != null) {
							questionnairesStepsBo.setModifiedBy(sesObj
									.getUserId());
							questionnairesStepsBo
									.setModifiedOn(FdahpStudyDesignerUtil
											.getCurrentDateTime());
						} else {
							questionnairesStepsBo.setCreatedBy(sesObj
									.getUserId());
							questionnairesStepsBo
									.setCreatedOn(FdahpStudyDesignerUtil
											.getCurrentDateTime());
						}
						addQuestionnairesStepsBo = studyQuestionnaireService
								.saveOrUpdateFromStepQuestionnaire(
										questionnairesStepsBo, sesObj,
										customStudyId);
					}
				}
				if (addQuestionnairesStepsBo != null) {
					jsonobject.put("stepId",
							addQuestionnairesStepsBo.getStepId());
					jsonobject.put("formId",
							addQuestionnairesStepsBo.getInstructionFormId());
					message = FdahpStudyDesignerConstants.SUCCESS;
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error("StudyQuestionnaireController - saveFormStep - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveFormStep - Ends");
	}

	/**
	 * Create the instruction step in Questionnaire which lays the instruction
	 * to user in mobile app.Admin would needs to fill the short title
	 * instruction title and instruction text.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/saveInstructionStep.do")
	public void saveInstructionStep(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - saveInstructionStep - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		InstructionsBo instructionsBo = null;
		ObjectMapper mapper = new ObjectMapper();
		InstructionsBo addInstructionsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String instructionsInfo = request
						.getParameter("instructionsInfo");
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (null != instructionsInfo) {
					instructionsBo = mapper.readValue(instructionsInfo,
							InstructionsBo.class);
					if (instructionsBo != null) {
						if (instructionsBo.getId() != null) {
							instructionsBo.setModifiedBy(sesObj.getUserId());
							instructionsBo.setModifiedOn(FdahpStudyDesignerUtil
									.getCurrentDateTime());
						} else {
							instructionsBo.setCreatedBy(sesObj.getUserId());
							instructionsBo.setCreatedOn(FdahpStudyDesignerUtil
									.getCurrentDateTime());
							instructionsBo.setActive(true);
						}
						if (instructionsBo.getQuestionnairesStepsBo() != null) {
							if (instructionsBo.getQuestionnairesStepsBo()
									.getStepId() != null) {
								instructionsBo.getQuestionnairesStepsBo()
										.setModifiedOn(
												FdahpStudyDesignerUtil
														.getCurrentDateTime());
								instructionsBo.getQuestionnairesStepsBo()
										.setModifiedBy(sesObj.getUserId());
							} else {
								instructionsBo.getQuestionnairesStepsBo()
										.setCreatedOn(
												FdahpStudyDesignerUtil
														.getCurrentDateTime());
								instructionsBo.getQuestionnairesStepsBo()
										.setCreatedBy(sesObj.getUserId());
							}
						}
						addInstructionsBo = studyQuestionnaireService
								.saveOrUpdateInstructionsBo(instructionsBo,
										sesObj, customStudyId);
					}
				}
				if (addInstructionsBo != null) {
					jsonobject.put("instructionId", addInstructionsBo.getId());
					jsonobject.put("stepId", addInstructionsBo
							.getQuestionnairesStepsBo().getStepId());
					message = FdahpStudyDesignerConstants.SUCCESS;
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveInstructionStep - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveInstructionStep - Ends");
	}

	/**
	 * Question of a form step contains the two attributes.Question-level
	 * attributes-these are the same set of attributes as that for question
	 * step with the exception of the skippable property and branching logic
	 * based on participant choice of response or the conditional logic based
	 * branching Response-level attributes (same as that for Question Step).
	 * Here we can save or update the form questions.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 *
	 */
	@RequestMapping("/adminStudies/saveOrUpdateFromQuestion.do")
	public ModelAndView saveOrUpdateFormQuestion(HttpServletRequest request,
			HttpServletResponse response, QuestionsBo questionsBo) {
		logger.info("StudyQuestionnaireController - saveOrUpdateFormQuestion - Starts");
		ModelAndView mav = new ModelAndView("instructionsStepPage");
		ModelMap map = new ModelMap();
		QuestionsBo addQuestionsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (questionsBo != null) {
					if (questionsBo.getId() != null) {
						questionsBo.setModifiedBy(sesObj.getUserId());
						questionsBo.setModifiedOn(FdahpStudyDesignerUtil
								.getCurrentDateTime());
					} else {
						questionsBo.setCreatedBy(sesObj.getUserId());
						questionsBo.setCreatedOn(FdahpStudyDesignerUtil
								.getCurrentDateTime());
					}
					addQuestionsBo = studyQuestionnaireService
							.saveOrUpdateQuestion(questionsBo, sesObj,
									customStudyId);
				}
				if (addQuestionsBo != null) {
					if (addQuestionsBo.getId() != null) {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG,
								"Form Question updated successfully.");
					} else {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG,
								"Form Question added successfully.");
					}
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/formStep.do", map);
				} else {
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG,
							"Form not added successfully.");
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/formQuestion.do", map);
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveOrUpdateFormQuestion - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveOrUpdateFormQuestion - Ends");
		return mav;
	}

	/**
	 * Here admin will add the from step to the questionnaire which contains the
	 * two sets of attributes. which are step level attribute,form level
	 * attribute.Admin has fill the required fields and click on done it save
	 * the info here.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminStudies/saveOrUpdateFromStepQuestionnaire.do")
	public ModelAndView saveOrUpdateFormStepQuestionnaire(
			HttpServletRequest request, HttpServletResponse response,
			QuestionnairesStepsBo questionnairesStepsBo) {
		logger.info("StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Starts");
		ModelAndView mav = new ModelAndView("instructionsStepPage");
		ModelMap map = new ModelMap();
		QuestionnairesStepsBo addQuestionnairesStepsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (questionnairesStepsBo != null) {
					if (questionnairesStepsBo.getStepId() != null) {
						questionnairesStepsBo.setModifiedBy(sesObj.getUserId());
						questionnairesStepsBo
								.setModifiedOn(FdahpStudyDesignerUtil
										.getCurrentDateTime());
					} else {
						questionnairesStepsBo.setCreatedBy(sesObj.getUserId());
						questionnairesStepsBo
								.setCreatedOn(FdahpStudyDesignerUtil
										.getCurrentDateTime());
					}
					addQuestionnairesStepsBo = studyQuestionnaireService
							.saveOrUpdateFromStepQuestionnaire(
									questionnairesStepsBo, sesObj,
									customStudyId);
				}
				if (addQuestionnairesStepsBo != null) {
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					if (questionnairesStepsBo.getStepId() != null) {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG,
								"Form Step updated successfully.");
					} else {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG,
								"Form Step added successfully.");
					}
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/viewQuestionnaire.do", map);
				} else {
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG,
							"Form not added successfully.");
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/formStep.do", map);
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Ends");
		return mav;
	}

	/**
	 * Create the instruction step in Questionnaire which lays the instruction
	 * to user in mobile app.Admin would needs to fill the short title
	 * instruction title and instruction text.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param instructionsBo
	 *            {@link InstructionsBo}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminStudies/saveOrUpdateInstructionStep.do")
	public ModelAndView saveOrUpdateInstructionStep(HttpServletRequest request,
			HttpServletResponse response, InstructionsBo instructionsBo) {
		logger.info("StudyQuestionnaireController - saveOrUpdateInstructionStep - Starts");
		ModelAndView mav = new ModelAndView("instructionsStepPage");
		ModelMap map = new ModelMap();
		InstructionsBo addInstructionsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (instructionsBo != null) {
					if (instructionsBo.getId() != null) {
						instructionsBo.setModifiedOn(FdahpStudyDesignerUtil
								.getCurrentDateTime());
						instructionsBo.setModifiedBy(sesObj.getUserId());
					} else {
						instructionsBo.setCreatedOn(FdahpStudyDesignerUtil
								.getCurrentDateTime());
						instructionsBo.setCreatedBy(sesObj.getUserId());
					}
					if (instructionsBo.getQuestionnairesStepsBo() != null) {
						if (instructionsBo.getQuestionnairesStepsBo()
								.getStepId() != null) {
							instructionsBo.getQuestionnairesStepsBo()
									.setModifiedOn(
											FdahpStudyDesignerUtil
													.getCurrentDateTime());
							instructionsBo.getQuestionnairesStepsBo()
									.setModifiedBy(sesObj.getUserId());
						} else {
							instructionsBo.getQuestionnairesStepsBo()
									.setCreatedOn(
											FdahpStudyDesignerUtil
													.getCurrentDateTime());
							instructionsBo.getQuestionnairesStepsBo()
									.setCreatedBy(sesObj.getUserId());
						}
					}
					addInstructionsBo = studyQuestionnaireService
							.saveOrUpdateInstructionsBo(instructionsBo, sesObj,
									customStudyId);
				}
				if (addInstructionsBo != null) {
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					if (instructionsBo.getId() != null) {
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.SUC_MSG,
										FdahpStudyDesignerConstants.INSTRUCTION_UPDATED_SUCCESSFULLY);
					} else {
						request.getSession()
								.setAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.SUC_MSG,
										FdahpStudyDesignerConstants.INSTRUCTION_ADDED_SUCCESSFULLY);
					}
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/viewQuestionnaire.do", map);
				} else {
					request.getSession()
							.setAttribute(
									FdahpStudyDesignerConstants.ERR_MSG,
									FdahpStudyDesignerConstants.INSTRUCTION_UPDATED_SUCCESSFULLY);
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/instructionsStep.do", map);
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveOrUpdateInstructionStep - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveOrUpdateInstructionStep - Ends");
		return mav;
	}

	/**
	 * Create or update of questionnaire in study which contains content and
	 * scheduling which can be managed by the admin.The questionnaire schedule
	 * frequency can be One time,Daily,Weekly,Monthly,Custom and admin has to
	 * select any one frequency.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param questionnaireBo
	 *            {@link QuestionnaireBo}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping(value = "/adminStudies/saveorUpdateQuestionnaireSchedule.do", method = RequestMethod.POST)
	public ModelAndView saveorUpdateQuestionnaireSchedule(
			HttpServletRequest request, HttpServletResponse response,
			QuestionnaireBo questionnaireBo) {
		logger.info("StudyQuestionnaireController - saveorUpdateQuestionnaireSchedule - Starts");
		ModelAndView mav = new ModelAndView("questionnairePage");
		ModelMap map = new ModelMap();
		QuestionnaireBo addQuestionnaireBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (questionnaireBo != null) {
					if (questionnaireBo.getId() != null) {
						questionnaireBo.setModifiedBy(sesObj.getUserId());
						questionnaireBo.setModifiedDate(FdahpStudyDesignerUtil
								.getCurrentDateTime());
						questionnaireBo.setStatus(true);
						questionnaireBo.setIsChange(1);
					} else {
						questionnaireBo.setCreatedBy(sesObj.getUserId());
						questionnaireBo.setCreatedDate(FdahpStudyDesignerUtil
								.getCurrentDateTime());
						questionnaireBo.setStatus(true);
						questionnaireBo.setIsChange(1);
					}
					addQuestionnaireBo = studyQuestionnaireService
							.saveOrUpdateQuestionnaire(questionnaireBo, sesObj,
									customStudyId);
					if (addQuestionnaireBo != null) {
						if (questionnaireBo.getId() != null) {
							request.getSession()
									.setAttribute(
											sessionStudyCount
													+ FdahpStudyDesignerConstants.SUC_MSG,
											"Questionnaire Updated successfully.");
						} else {
							request.getSession()
									.setAttribute(
											sessionStudyCount
													+ FdahpStudyDesignerConstants.SUC_MSG,
											"Questionnaire added successfully.");
						}
						String studyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.STUDY_ID);
						if (StringUtils.isNotEmpty(studyId)) {
							studyService.markAsCompleted(
									Integer.valueOf(studyId),
									FdahpStudyDesignerConstants.QUESTIONNAIRE,
									false, sesObj, customStudyId);
						}
						map.addAttribute("_S", sessionStudyCount);
						mav = new ModelAndView(
								"redirect:/adminStudies/viewStudyQuestionnaires.do",
								map);
					} else {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG,
								"Questionnaire not added successfully.");
						map.addAttribute("_S", sessionStudyCount);
						mav = new ModelAndView(
								"redirect:/adminStudies/viewQuestionnaire.do",
								map);
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveorUpdateQuestionnaireSchedule - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveorUpdateQuestionnaireSchedule - Ends");
		return mav;
	}

	/**
	 * Admin can add the question step to questionnaire here which contains the
	 * 3 subsections admin has to fill the sub section such as step level
	 * attribute,question level attribute,response level attributes.Questions
	 * can be various types as defined by the response format. Depending on the
	 * response format, the attributes of the QA would vary Here we can create
	 * or update the question step in questionnaire
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminStudies/saveOrUpdateQuestionStepQuestionnaire.do")
	public ModelAndView saveOrUpdateQuestionStepQuestionnaire(
			HttpServletRequest request, HttpServletResponse response,
			QuestionnairesStepsBo questionnairesStepsBo) {
		logger.info("StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Starts");
		ModelAndView mav = new ModelAndView("instructionsStepPage");
		ModelMap map = new ModelMap();
		QuestionnairesStepsBo addQuestionnairesStepsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (questionnairesStepsBo != null) {
					if (questionnairesStepsBo.getStepId() != null) {
						questionnairesStepsBo.setModifiedBy(sesObj.getUserId());
						questionnairesStepsBo
								.setModifiedOn(FdahpStudyDesignerUtil
										.getCurrentDateTime());
					} else {
						questionnairesStepsBo.setCreatedBy(sesObj.getUserId());
						questionnairesStepsBo
								.setCreatedOn(FdahpStudyDesignerUtil
										.getCurrentDateTime());
					}
					addQuestionnairesStepsBo = studyQuestionnaireService
							.saveOrUpdateQuestionStep(questionnairesStepsBo,
									sesObj, customStudyId);
				}
				if (addQuestionnairesStepsBo != null) {
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					if (questionnairesStepsBo.getStepId() != null) {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG,
								"Question Step updated successfully.");
					} else {
						request.getSession().setAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG,
								"Question Step added successfully.");
					}
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/viewQuestionnaire.do", map);
				} else {
					request.getSession().setAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG,
							"Form not added successfully.");
					map.addAttribute("_S", sessionStudyCount);
					mav = new ModelAndView(
							"redirect:/adminStudies/questionStep.do", map);
				}
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Ends");
		return mav;
	}

	/**
	 * Question of a form step contains the two attributes .Question-level
	 * attributes-these are the same set of attributes as that for question
	 * step with the exception of the skippable property and branching logic
	 * based on participant choice of response or the conditional logic based
	 * branching Response-level attributes (same as that for Question Step).Here
	 * we can save or update the form questions.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String : Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/saveQuestion.do")
	public void saveQuestion(HttpServletRequest request,
			HttpServletResponse response,
			MultipartHttpServletRequest multipleRequest) {
		logger.info("StudyQuestionnaireController - saveQuestion - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		QuestionsBo questionsBo = null;
		ObjectMapper mapper = new ObjectMapper();
		QuestionsBo addQuestionsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String questionnaireStepInfo = request
						.getParameter("questionInfo");
				Iterator<String> itr = multipleRequest.getFileNames();
				HashMap<String, MultipartFile> fileMap = new HashMap<>();
				while (itr.hasNext()) {
					CommonsMultipartFile mpf = (CommonsMultipartFile) multipleRequest
							.getFile(itr.next());
					fileMap.put(mpf.getFileItem().getFieldName(), mpf);
				}
				if (null != questionnaireStepInfo) {
					questionsBo = mapper.readValue(questionnaireStepInfo,
							QuestionsBo.class);
					if (questionsBo != null) {
						if (questionsBo.getId() != null) {
							questionsBo.setModifiedBy(sesObj.getUserId());
							questionsBo.setModifiedOn(FdahpStudyDesignerUtil
									.getCurrentDateTime());
						} else {
							questionsBo.setCreatedBy(sesObj.getUserId());
							questionsBo.setCreatedOn(FdahpStudyDesignerUtil
									.getCurrentDateTime());
						}
						if (questionsBo.getResponseType() != null
								&& questionsBo.getResponseType() == 5) {
							if (questionsBo.getQuestionResponseSubTypeList() != null
									&& !questionsBo
											.getQuestionResponseSubTypeList()
											.isEmpty()) {
								for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionsBo
										.getQuestionResponseSubTypeList()) {
									String key1 = "imageFile["
											+ questionResponseSubTypeBo
													.getImageId() + "]";
									String key2 = "selectImageFile["
											+ questionResponseSubTypeBo
													.getImageId() + "]";
									if (fileMap != null
											&& fileMap.get(key1) != null) {
										questionResponseSubTypeBo
												.setImageFile(fileMap.get(key1));
									}
									if (fileMap != null
											&& fileMap.get(key2) != null) {
										questionResponseSubTypeBo
												.setSelectImageFile(fileMap
														.get(key2));
									}
								}
							}
						}
						if (questionsBo.getResponseType() != null
								&& questionsBo.getQuestionReponseTypeBo() != null) {
							if (fileMap != null
									&& fileMap.get("minImageFile") != null) {
								questionsBo.getQuestionReponseTypeBo()
										.setMinImageFile(
												fileMap.get("minImageFile"));
							}
							if (fileMap != null
									&& fileMap.get("maxImageFile") != null) {
								questionsBo.getQuestionReponseTypeBo()
										.setMaxImageFile(
												fileMap.get("maxImageFile"));
							}
						}
						addQuestionsBo = studyQuestionnaireService
								.saveOrUpdateQuestion(questionsBo, sesObj,
										customStudyId);
					}
				}
				if (addQuestionsBo != null) {
					jsonobject.put("questionId", addQuestionsBo.getId());
					if (addQuestionsBo.getQuestionReponseTypeBo() != null) {
						jsonobject
								.put("questionResponseId", addQuestionsBo
										.getQuestionReponseTypeBo()
										.getResponseTypeId());
						jsonobject.put("questionsResponseTypeId",
								addQuestionsBo.getQuestionReponseTypeBo()
										.getQuestionsResponseTypeId());
					}
					message = FdahpStudyDesignerConstants.SUCCESS;
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error("StudyQuestionnaireController - saveQuestion - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveQuestion - Ends");
	}

	/**
	 * Create or update of Questionnaire in study which contains content and
	 * scheduling which can be managed.The Questionnaire schedule can be One
	 * time, Daily,Weekly,Monthly,Custom.The schedule decides how often the user
	 * needs to take it
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/saveQuestionnaireSchedule.do", method = RequestMethod.POST)
	public void saveQuestionnaireSchedule(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - saveQuestionnaireSchedule - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		QuestionnaireBo updateQuestionnaireBo = null;
		ObjectMapper mapper = new ObjectMapper();
		QuestionnaireBo questionnaireBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String questionnaireScheduleInfo = request
						.getParameter("questionnaireScheduleInfo");
				if (questionnaireScheduleInfo != null
						&& !questionnaireScheduleInfo.isEmpty()) {
					questionnaireBo = mapper.readValue(
							questionnaireScheduleInfo, QuestionnaireBo.class);
					if (questionnaireBo != null) {
						String studyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.STUDY_ID);
						if (questionnaireBo.getId() != null) {
							questionnaireBo.setModifiedBy(sesObj.getUserId());
							questionnaireBo
									.setModifiedDate(FdahpStudyDesignerUtil
											.getCurrentDateTime());
							if (questionnaireBo.getStatus()) {
								request.getSession()
										.setAttribute(
												sessionStudyCount
														+ FdahpStudyDesignerConstants.SUC_MSG,
												"Questionnaire Updated successfully.");
							}
						} else {
							questionnaireBo.setCreatedBy(sesObj.getUserId());
							questionnaireBo
									.setCreatedDate(FdahpStudyDesignerUtil
											.getCurrentDateTime());
							if (questionnaireBo.getStatus()) {
								request.getSession()
										.setAttribute(
												sessionStudyCount
														+ FdahpStudyDesignerConstants.SUC_MSG,
												"Questionnaire added successfully.");
							}
						}
						customStudyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
						updateQuestionnaireBo = studyQuestionnaireService
								.saveOrUpdateQuestionnaire(questionnaireBo,
										sesObj, customStudyId);
						if (updateQuestionnaireBo != null) {
							jsonobject.put("questionnaireId",
									updateQuestionnaireBo.getId());
							if (updateQuestionnaireBo
									.getQuestionnairesFrequenciesBo() != null) {
								jsonobject
										.put("questionnaireFrequenceId",
												updateQuestionnaireBo
														.getQuestionnairesFrequenciesBo()
														.getId());
							}
							if (StringUtils.isNotEmpty(studyId)) {
								studyService
										.markAsCompleted(
												Integer.valueOf(studyId),
												FdahpStudyDesignerConstants.QUESTIONNAIRE,
												false, sesObj, customStudyId);
							}
							message = FdahpStudyDesignerConstants.SUCCESS;
						}
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveQuestionnaireSchedule - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveQuestionnaireSchedule - Ends");
	}

	/**
	 * Admin can add the question step to questionnaire here which contains the
	 * 3 subsections admin has to fill the sub section such as step level
	 * attribute,question level attribute,response level attributes.Questions
	 * can be various types as defined by the response format. Depending on the
	 * response format, the attributes of the QA would vary
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/saveQuestionStep.do", method = RequestMethod.POST)
	public void saveQuestionStep(HttpServletResponse response,
			MultipartHttpServletRequest multipleRequest,
			HttpServletRequest request) {
		logger.info("StudyQuestionnaireController - saveQuestionStep - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		QuestionnairesStepsBo questionnairesStepsBo = null;
		ObjectMapper mapper = new ObjectMapper();
		QuestionnairesStepsBo addQuestionnairesStepsBo = null;
		String customStudyId = "";
		try {
			SessionObject sesObj = (SessionObject) multipleRequest.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String questionnaireStepInfo = multipleRequest
						.getParameter("questionnaireStepInfo");
				customStudyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				Iterator<String> itr = multipleRequest.getFileNames();
				HashMap<String, MultipartFile> fileMap = new HashMap<>();
				while (itr.hasNext()) {
					CommonsMultipartFile mpf = (CommonsMultipartFile) multipleRequest
							.getFile(itr.next());
					fileMap.put(mpf.getFileItem().getFieldName(), mpf);
				}
				if (null != questionnaireStepInfo) {
					questionnairesStepsBo = mapper.readValue(
							questionnaireStepInfo, QuestionnairesStepsBo.class);
					if (questionnairesStepsBo != null) {
						if (questionnairesStepsBo.getStepId() != null) {
							questionnairesStepsBo.setModifiedBy(sesObj
									.getUserId());
							questionnairesStepsBo
									.setModifiedOn(FdahpStudyDesignerUtil
											.getCurrentDateTime());
						} else {
							questionnairesStepsBo.setCreatedBy(sesObj
									.getUserId());
							questionnairesStepsBo
									.setCreatedOn(FdahpStudyDesignerUtil
											.getCurrentDateTime());
						}
					}
					if (questionnairesStepsBo.getQuestionsBo() != null
							&& questionnairesStepsBo.getQuestionsBo()
									.getResponseType() != null) {
						if (questionnairesStepsBo.getQuestionsBo()
								.getResponseType() == 5) {
							if (questionnairesStepsBo
									.getQuestionResponseSubTypeList() != null
									&& !questionnairesStepsBo
											.getQuestionResponseSubTypeList()
											.isEmpty()) {
								for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionnairesStepsBo
										.getQuestionResponseSubTypeList()) {
									String key1 = "imageFile["
											+ questionResponseSubTypeBo
													.getImageId() + "]";
									String key2 = "selectImageFile["
											+ questionResponseSubTypeBo
													.getImageId() + "]";
									if (fileMap != null
											&& fileMap.get(key1) != null) {
										questionResponseSubTypeBo
												.setImageFile(fileMap.get(key1));
									}
									if (fileMap != null
											&& fileMap.get(key2) != null) {
										questionResponseSubTypeBo
												.setSelectImageFile(fileMap
														.get(key2));
									}
								}
							}
						}
						if (questionnairesStepsBo.getQuestionReponseTypeBo() != null) {
							if (fileMap != null
									&& fileMap.get("minImageFile") != null) {
								questionnairesStepsBo
										.getQuestionReponseTypeBo()
										.setMinImageFile(
												fileMap.get("minImageFile"));
							}
							if (fileMap != null
									&& fileMap.get("maxImageFile") != null) {
								questionnairesStepsBo
										.getQuestionReponseTypeBo()
										.setMaxImageFile(
												fileMap.get("maxImageFile"));
							}
						}
					}
					addQuestionnairesStepsBo = studyQuestionnaireService
							.saveOrUpdateQuestionStep(questionnairesStepsBo,
									sesObj, customStudyId);
				}
				if (addQuestionnairesStepsBo != null) {
					jsonobject.put("stepId",
							addQuestionnairesStepsBo.getStepId());
					String studyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.STUDY_ID);
					if (StringUtils.isNotEmpty(studyId)) {
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
					if (addQuestionnairesStepsBo.getQuestionsBo() != null) {
						jsonobject.put("questionId", addQuestionnairesStepsBo
								.getQuestionsBo().getId());

					}
					if (addQuestionnairesStepsBo.getQuestionReponseTypeBo() != null) {
						jsonobject.put("questionResponseId",
								addQuestionnairesStepsBo
										.getQuestionReponseTypeBo()
										.getResponseTypeId());
						jsonobject.put("questionsResponseTypeId",
								addQuestionnairesStepsBo
										.getQuestionReponseTypeBo()
										.getQuestionsResponseTypeId());
					}
					message = FdahpStudyDesignerConstants.SUCCESS;
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - saveQuestionStep - Error",
					e);
		}
		logger.info("StudyQuestionnaireController - saveQuestionStep - Ends");
	}

	/**
	 * For QA of response type that results in the data type 'double',the admin
	 * can define conditional logic (formula-based) to evaluate with user
	 * response as the input. A condition or formula is to be defined along with
	 * a destination step to navigate to if the result of evaluation is TRUE and
	 * an alternative destination step if FALSE.Admin can check the condition is
	 * valid or not here.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateconditionalFormula.do", method = RequestMethod.POST)
	public void validateconditionalFormula(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateconditionalFormula - Starts");
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		ObjectMapper mapper = new ObjectMapper();
		JSONObject formulaResponseJsonObject = null;
		FormulaInfoBean formulaInfoBean = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (sesObj != null) {
				String left_input = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("left_input")) ? "" : request
						.getParameter("left_input");
				String right_input = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("right_input")) ? "" : request
						.getParameter("right_input");
				String oprator_input = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("oprator_input")) ? "" : request
						.getParameter("oprator_input");
				String trialInputVal = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("trialInput")) ? "" : request
						.getParameter("trialInput");
				if (!left_input.isEmpty() && !right_input.isEmpty()
						&& !oprator_input.isEmpty() && !trialInputVal.isEmpty()) {
					formulaInfoBean = studyQuestionnaireService
							.validateQuestionConditionalBranchingLogic(
									left_input, right_input, oprator_input,
									trialInputVal);
					if (formulaInfoBean != null) {
						formulaResponseJsonObject = new JSONObject(
								mapper.writeValueAsString(formulaInfoBean));
						jsonobject.put("formulaResponseJsonObject",
								formulaResponseJsonObject);
						if (formulaInfoBean.getMessage().equalsIgnoreCase(
								FdahpStudyDesignerConstants.SUCCESS))
							message = FdahpStudyDesignerConstants.SUCCESS;
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateconditionalFormula - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateconditionalFormula - Ends");
	}

	/**
	 * The admin can choose to add a response data element to the study
	 * dashboard in the form of line charts or statistics.Adding a line chart to
	 * the dashboard needs the admin to specify The options time range for the
	 * chart which depend on the scheduling frequency set for the activity.when
	 * admin change the frequency in questionnaire schedule its validate the
	 * options in the time range for chart options.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateLineChartSchedule.do", method = RequestMethod.POST)
	public void validateQuestionnaireLineChartSchedule(
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateQuestionnaireLineChartSchedule - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		ObjectMapper mapper = new ObjectMapper();
		JSONObject questionnaireJsonObject = null;
		Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (sesObj != null) {
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String frequency = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("frequency")) ? "" : request
						.getParameter("frequency");
				if (!questionnaireId.isEmpty() && !frequency.isEmpty()) {
					message = studyQuestionnaireService
							.validateLineChartSchedule(
									Integer.valueOf(questionnaireId), frequency);
					if (message
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						qTreeMap = studyQuestionnaireService
								.getQuestionnaireStepList(Integer
										.valueOf(questionnaireId));
						questionnaireJsonObject = new JSONObject(
								mapper.writeValueAsString(qTreeMap));
						jsonobject.put("questionnaireJsonObject",
								questionnaireJsonObject);
					}
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateQuestionnaireLineChartSchedule - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateQuestionnaireLineChartSchedule - Ends");
	}

	/**
	 * Questionnaire contains the content,schedule as two tabs.Each
	 * questionnaire contains the short title in content tab this will be
	 * created as the column for the questionnaire response in response server
	 * for this we are doing the unique title validation for each questionnaire
	 * in study level
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}\
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateQuestionnaireKey.do", method = RequestMethod.POST)
	public void validateQuestionnaireShortTitle(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateQuestionnaireShortTitle - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String customStudyId = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID)) ? ""
						: request
								.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
				}
				if (StringUtils.isEmpty(customStudyId)) {
					customStudyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				}
				String shortTitle = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("shortTitle")) ? "" : request
						.getParameter("shortTitle");
				if ((studyId != null && !studyId.isEmpty())
						&& !shortTitle.isEmpty()) {
					message = studyQuestionnaireService
							.checkQuestionnaireShortTitle(
									Integer.valueOf(studyId), shortTitle,
									customStudyId);
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateQuestionnaireShortTitle - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateQuestionnaireShortTitle - Ends");
	}

	/**
	 * A questionnaire is an ordered set of one or more steps.Each step contains
	 * the step short title field. Which will be response column for the step in
	 * response server.so it should be the unique.Here validating the unique for
	 * step short title
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateQuestionnaireStepKey.do", method = RequestMethod.POST)
	public void validateQuestionnaireStepShortTitle(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateQuestionnaireStepShortTitle - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String stepType = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("stepType")) ? "" : request
						.getParameter("stepType");
				String shortTitle = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("shortTitle")) ? "" : request
						.getParameter("shortTitle");
				String questionnaireShortTitle = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter("questionnaireShortTitle")) ? ""
						: request.getParameter("questionnaireShortTitle");
				String customStudyId = (String) request
						.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (StringUtils.isEmpty(customStudyId)) {
					customStudyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID)) ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				}
				if (!questionnaireId.isEmpty() && !stepType.isEmpty()
						&& !shortTitle.isEmpty()) {
					message = studyQuestionnaireService
							.checkQuestionnaireStepShortTitle(
									Integer.valueOf(questionnaireId), stepType,
									shortTitle, questionnaireShortTitle,
									customStudyId);
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateQuestionnaireStepShortTitle - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateQuestionnaireStepShortTitle - Ends");
	}

	/**
	 * From step have a one or more question.Each question have the short title
	 * field this will be created the as column in response server so its should
	 * be unique across all the steps.Validateing the Unique of question short
	 * title inside form step
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateQuestionKey.do", method = RequestMethod.POST)
	public void validateQuestionShortTitle(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateQuestionShortTitle - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String questionnaireId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("questionnaireId")) ? "" : request
						.getParameter("questionnaireId");
				String shortTitle = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("shortTitle")) ? "" : request
						.getParameter("shortTitle");
				String questionnaireShortTitle = FdahpStudyDesignerUtil
						.isEmpty(request
								.getParameter("questionnaireShortTitle")) ? ""
						: request.getParameter("questionnaireShortTitle");
				String customStudyId = (String) request
						.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (StringUtils.isEmpty(customStudyId)) {
					customStudyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID)) ? ""
							: request
									.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				}
				if (!questionnaireId.isEmpty() && !shortTitle.isEmpty()) {
					message = studyQuestionnaireService
							.checkFromQuestionShortTitle(
									Integer.valueOf(questionnaireId),
									shortTitle, questionnaireShortTitle,
									customStudyId);
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateQuestionShortTitle - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateQuestionShortTitle - Ends");
	}

	/**
	 * The admin can choose to add a response data element to the study
	 * dashboard in the form of line charts or statistics.Adding a statistic to
	 * the dashboard needs the admin to specify the short name should be unique
	 * across all the state in the study So validating the unique validation for
	 * short name in states.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateStatsShortName.do", method = RequestMethod.POST)
	public void validateQuestionStatsShortTitle(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateQuestionStatsShortTitle - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String customStudyId = (String) request
						.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				String shortTitle = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("shortTitle")) ? "" : request
						.getParameter("shortTitle");
				if (!studyId.isEmpty() && !shortTitle.isEmpty()) {
					message = studyQuestionnaireService
							.checkStatShortTitle(Integer.valueOf(studyId),
									shortTitle, customStudyId);
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateQuestionStatsShortTitle - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateQuestionStatsShortTitle - Ends");
	}

	/**
	 * In Questionnaire form step carries the multiple question and Answers .In
	 * form level attributes we can make form form as repeatable if the form is
	 * repeatable we can not add the line chart and states data to the
	 * dashbord.here we are validating the added line chart and statistics data
	 * before updating the form as repeatable.
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateRepeatableQuestion.do", method = RequestMethod.POST)
	public void validateRepeatableQuestion(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateRepeatableQuestion - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (sesObj != null) {
				String formId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("formId")) ? "" : request
						.getParameter("formId");
				if (!formId.isEmpty()) {
					message = studyQuestionnaireService
							.validateRepetableFormQuestionStats(Integer
									.valueOf(formId));
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateRepeatableQuestion - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateRepeatableQuestion - Ends");
	}

	/**
	 * List of all the Questionnaires of an study.A Study can have 0 or more
	 * Questionnaires and admin can manage a list of questionnaires for the
	 * study
	 *
	 * @author BTC
	 *
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 *
	 *
	 */
	@RequestMapping("/adminStudies/viewStudyQuestionnaires.do")
	public ModelAndView viewStudyQuestionnaires(HttpServletRequest request) {
		logger.info("StudyQuestionnaireController - viewStudyQuestionnaires - Starts");
		ModelAndView mav = new ModelAndView("redirect:viewBasicInfo.do");
		ModelMap map = new ModelMap();
		StudyBo studyBo = null;
		String sucMsg = "";
		String errMsg = "";
		List<QuestionnaireBo> questionnaires = null;
		String activityStudyId = "";
		String customStudyId = "";
		String actMsg = "";
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request
					.getParameter("_S")) ? Integer.parseInt(request
					.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				request.getSession().removeAttribute(
						sessionStudyCount + "questionnaireId");
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							sessionStudyCount
									+ FdahpStudyDesignerConstants.ERR_MSG);
				}

				String studyId = (String) request.getSession().getAttribute(
						sessionStudyCount
								+ FdahpStudyDesignerConstants.STUDY_ID);
				String permission = (String) request.getSession().getAttribute(
						sessionStudyCount + "permission");
				if (StringUtils.isEmpty(studyId)) {
					studyId = FdahpStudyDesignerUtil
							.isEmpty(request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID)) == true ? "0"
							: request
									.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
				}
				// Added for live version Start
				String isLive = (String) request.getSession()
						.getAttribute(
								sessionStudyCount
										+ FdahpStudyDesignerConstants.IS_LIVE);
				if (StringUtils.isNotEmpty(isLive)
						&& isLive
								.equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
					activityStudyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.QUESTIONNARIE_STUDY_ID);
					customStudyId = (String) request
							.getSession()
							.getAttribute(
									sessionStudyCount
											+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				}
				// Added for live version End
				if (StringUtils.isNotEmpty(studyId)) {
					request.getSession().removeAttribute(
							sessionStudyCount + "actionType");
					studyBo = studyService.getStudyById(studyId,
							sesObj.getUserId());
					if (StringUtils.isNotEmpty(activityStudyId)) {
						questionnaires = studyQuestionnaireService
								.getStudyQuestionnairesByStudyId(customStudyId,
										true);
					} else {
						questionnaires = studyQuestionnaireService
								.getStudyQuestionnairesByStudyId(studyId, false);
					}
					boolean markAsComplete = true;
					actMsg = studyService
							.validateActivityComplete(
									studyId,
									FdahpStudyDesignerConstants.ACTIVITY_TYPE_QUESTIONNAIRE);
					if (!actMsg
							.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS))
						markAsComplete = false;
					map.addAttribute("markAsComplete", markAsComplete);
					if (!markAsComplete) {
						customStudyId = (String) request
								.getSession()
								.getAttribute(
										sessionStudyCount
												+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
						studyService.markAsCompleted(Integer.valueOf(studyId),
								FdahpStudyDesignerConstants.QUESTIONNAIRE,
								false, sesObj, customStudyId);
					}
				}
				map.addAttribute("permission", permission);
				map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
				map.addAttribute("questionnaires", questionnaires);
				map.addAttribute(FdahpStudyDesignerConstants.ACTIVITY_MESSAGE,
						actMsg);
				map.addAttribute("_S", sessionStudyCount);
				mav = new ModelAndView("studyQuestionaryListPage", map);
			}
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - viewStudyQuestionnaires - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - viewStudyQuestionnaires - Ends");
		return mav;
	}
	
	/**
	 * A questionnaire is an ordered set of one or more steps.Each step contains
	 * the step short title field. Which will be response column for the step in
	 * response server.so it should be the unique.Here validating the unique for
	 * step short title
	 * 
	 * @author BTC
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @return String Success/Failure
	 */
	@RequestMapping(value = "/adminStudies/validateAnchorDateName.do", method = RequestMethod.POST)
	public void validateAnchorDateName(HttpServletRequest request,HttpServletResponse response) {
		logger.info("StudyQuestionnaireController - validateAnchorDateName - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		try {
			SessionObject sesObj = (SessionObject) request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			Integer sessionStudyCount = StringUtils.isNumeric(request.getParameter("_S")) ? Integer.parseInt(request.getParameter("_S")) : 0;
			if (sesObj != null && sesObj.getStudySession() != null
					&& sesObj.getStudySession().contains(sessionStudyCount)) {
				String anchordateText = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("anchordateText")) ? "" : request
						.getParameter("anchordateText");
				String anchorDateId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("anchorDateId")) ? "" : request
						.getParameter("anchorDateId");
				String customStudyId = (String) request.getSession().getAttribute(sessionStudyCount+ FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				if (StringUtils.isEmpty(customStudyId)) {
					customStudyId = FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID)) ? "": request
									.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
				}
				if (!anchordateText.isEmpty() && !customStudyId.isEmpty()) {
					message = studyQuestionnaireService.checkUniqueAnchorDateName(anchordateText, customStudyId, anchorDateId);
				}
			}
			jsonobject.put("message", message);
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			logger.error(
					"StudyQuestionnaireController - validateAnchorDateName - ERROR",
					e);
		}
		logger.info("StudyQuestionnaireController - validateAnchorDateName - Ends");
	}
}
