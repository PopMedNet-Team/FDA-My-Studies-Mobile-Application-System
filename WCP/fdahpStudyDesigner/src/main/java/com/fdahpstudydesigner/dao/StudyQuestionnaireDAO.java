package com.fdahpstudydesigner.dao;

import java.util.List;
import java.util.SortedMap;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.QuestionConditionBranchBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.util.SessionObject;

public interface StudyQuestionnaireDAO {
	public String checkFromQuestionShortTitle(Integer questionnaireId,
			String shortTitle, String questionnaireShortTitle,
			String customStudyId);

	public String checkQuestionnaireResponseTypeValidation(Integer studyId,
			String customStudyId);

	public String checkQuestionnaireShortTitle(Integer studyId,
			String shortTitle, String customStudyId);

	public String checkQuestionnaireStepShortTitle(Integer questionnaireId,
			String stepType, String shortTitle, String questionnaireShortTitle,
			String customStudyId);

	public String checkStatShortTitle(Integer studyId, String shortTitle,
			String customStudyId);

	public QuestionnaireBo copyStudyQuestionnaireBo(Integer questionnaireId,
			String customStudyId, SessionObject sessionObject);

	public String deleteFromStepQuestion(Integer formId, Integer questionId,
			SessionObject sessionObject, String customStudyId);

	public String deleteQuestionnaireStep(Integer stepId,
			Integer questionnaireId, String stepType,
			SessionObject sessionObject, String customStudyId);

	public String deleteQuestuionnaireInfo(Integer studyId,
			Integer questionnaireId, SessionObject sessionObject,
			String customStudyId);

	public List<HealthKitKeysInfo> getHeanlthKitKeyInfoList();

	public InstructionsBo getInstructionsBo(Integer instructionId,
			String questionnaireShortTitle, String customStudyId,
			Integer questionnaireId);

	public List<QuestionConditionBranchBo> getQuestionConditionalBranchingLogic(
			Session session, Integer questionId);

	public QuestionnaireBo getQuestionnaireById(Integer questionnaireId,
			String customStudyId);

	public List<QuestionnairesStepsBo> getQuestionnairesStepsList(
			Integer questionnaireId, Integer sequenceNo);

	public QuestionnairesStepsBo getQuestionnaireStep(Integer stepId,
			String stepType, String questionnaireShortTitle,
			String customStudyId, Integer questionnaireId);

	public SortedMap<Integer, QuestionnaireStepBean> getQuestionnaireStepList(
			Integer questionnaireId);

	public List<QuestionResponseTypeMasterInfoBo> getQuestionReponseTypeList();

	public QuestionsBo getQuestionsById(Integer questionId,
			String questionnaireShortTitle, String customStudyId);

	public List<QuestionnaireBo> getStudyQuestionnairesByStudyId(
			String studyId, Boolean isLive);

	public Boolean isAnchorDateExistsForStudy(Integer studyId,
			String customStudyId);

	public Boolean isQuestionnairesCompleted(Integer studyId);

	public String reOrderFormStepQuestions(Integer formId, int oldOrderNumber,
			int newOrderNumber);

	public String reOrderQuestionnaireSteps(Integer questionnaireId,
			int oldOrderNumber, int newOrderNumber);

	public QuestionnairesStepsBo saveOrUpdateFromQuestionnaireStep(
			QuestionnairesStepsBo questionnairesStepsBo, SessionObject sesObj,
			String customStudyId);

	public InstructionsBo saveOrUpdateInstructionsBo(
			InstructionsBo instructionsBo, SessionObject sessionObject,
			String customStudyId);

	public QuestionsBo saveOrUpdateQuestion(QuestionsBo questionsBo);

	public QuestionnaireBo saveORUpdateQuestionnaire(
			QuestionnaireBo questionnaireBo, SessionObject sessionObject,
			String customStudyId);

	public QuestionnairesStepsBo saveOrUpdateQuestionStep(
			QuestionnairesStepsBo questionnairesStepsBo,
			SessionObject sessionObject, String customStudyId);

	public String validateLineChartSchedule(Integer questionnaireId,
			String frequency);

	public String validateRepetableFormQuestionStats(Integer formId);
	
	public String checkUniqueAnchorDateName(String anchordateText, String customStudyId, String anchorDateId);
	
	public Integer getStudyIdByCustomStudy( Session session, String customStudyId);
	
	public List<AnchorDateTypeBo> getAnchorTypesByStudyId(String customStudyId);
	
	public boolean isAnchorDateExistByQuestionnaire(Integer questionnaireId);
	
	public String updateAnchordateInQuestionnaire(Session session, Transaction transaction, StudyVersionBo studyVersionBo, Integer questionnaireId,SessionObject sessionObject,Integer studyId, Integer stepId, Integer questionId, String stepType, boolean isChange);
}
