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
/**
 *
 */
package com.fdahpstudydesigner.service;

import java.util.List;
import java.util.SortedMap;

import com.fdahpstudydesigner.bean.FormulaInfoBean;
import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * @author BTC
 *
 */
public interface StudyQuestionnaireService {
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

	public String deletQuestionnaire(Integer studyId, Integer questionnaireId,
			SessionObject sessionObject, String customStudyId);

	public List<HealthKitKeysInfo> getHeanlthKitKeyInfoList();

	public InstructionsBo getInstructionsBo(Integer instructionId,
			String questionnaireShortTitle, String customStudyId,
			Integer questionnaireId);

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

	public QuestionnairesStepsBo saveOrUpdateFromStepQuestionnaire(
			QuestionnairesStepsBo questionnairesStepsBo, SessionObject sesObj,
			String customStudyId);

	public InstructionsBo saveOrUpdateInstructionsBo(
			InstructionsBo instructionsBo, SessionObject sessionObject,
			String customStudyId);

	public QuestionsBo saveOrUpdateQuestion(QuestionsBo questionsBo,
			SessionObject sesObj, String customStudyId);

	public QuestionnaireBo saveOrUpdateQuestionnaire(
			QuestionnaireBo questionnaireBo, SessionObject sessionObject,
			String customStudyId);

	public QuestionnaireBo saveOrUpdateQuestionnaireSchedule(
			QuestionnaireBo questionnaireBo, SessionObject sessionObject,
			String customStudyId);

	public QuestionnairesStepsBo saveOrUpdateQuestionStep(
			QuestionnairesStepsBo questionnairesStepsBo,
			SessionObject sessionObject, String customStudyId);

	public String validateLineChartSchedule(Integer questionnaireId,
			String frequency);

	public FormulaInfoBean validateQuestionConditionalBranchingLogic(
			String lhs, String rhs, String operator, String input);

	public String validateRepetableFormQuestionStats(Integer formId);
	
	public String checkUniqueAnchorDateName(String anchordateText, String customStudyId, String anchorDateId);
	
	public List<AnchorDateTypeBo> getAnchorTypesByStudyId(String customStudyId);
	
	public boolean isAnchorDateExistByQuestionnaire(Integer questionnaireId);
}
