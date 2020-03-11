package com.fdahpstudydesigner.dao;

import java.util.HashMap;
/**
 *
 * @author BTC
 *
 */
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fdahpstudydesigner.bean.StudyIdBean;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bean.StudyPageBean;
import com.fdahpstudydesigner.bo.Checklist;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ComprehensionTestResponseBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.ConsentMasterInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.ReferenceTablesBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;

public interface StudyDAO {

	public String checkActiveTaskTypeValidation(Integer studyId);

	public int comprehensionTestQuestionOrder(Integer studyId);

	public int consentInfoOrder(Integer studyId);

	public String deleteComprehensionTestQuestion(Integer questionId,
			Integer studyId, SessionObject sessionObject);

	public String deleteConsentInfo(Integer consentInfoId, Integer studyId,
			SessionObject sessionObject, String customStudyId);

	public String deleteEligibilityTestQusAnsById(Integer eligibilityTestId,
			Integer studyId, SessionObject sessionObject, String customStudyId);

	public boolean deleteLiveStudy(String customStudyId);

	public String deleteOverviewStudyPageById(String studyId, String pageId);

	public String deleteResourceInfo(Integer resourceInfoId,
			boolean resourceVisibility, int studyId);

	public boolean deleteStudyByCustomStudyId(String customStudyId);

	public String deleteStudyByIdOrCustomstudyId(Session session,
			Transaction transaction, String studyId, String customStudyId);

	public int eligibilityTestOrderCount(Integer eligibilityId);

	public List<UserBO> getActiveNonAddedUserList(Integer studyId,
			Integer userId);

	public List<StudyPermissionBO> getAddedUserListToStudy(Integer studyId,
			Integer userId);

	public List<StudyBo> getAllStudyList();

	public Checklist getchecklistInfo(Integer studyId);

	public ComprehensionTestQuestionBo getComprehensionTestQuestionById(
			Integer questionId);

	public List<ComprehensionTestQuestionBo> getComprehensionTestQuestionList(
			Integer studyId);

	public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(
			Integer comprehensionQuestionId);

	public ConsentBo getConsentDetailsByStudyId(String studyId);

	public ConsentInfoBo getConsentInfoById(Integer consentInfoId);

	public List<ConsentInfoBo> getConsentInfoDetailsListByStudyId(String studyId);

	public List<ConsentInfoBo> getConsentInfoList(Integer studyId);

	public List<ConsentMasterInfoBo> getConsentMasterInfoList();

	public StudyIdBean getLiveVersion(String customStudyId);

	public NotificationBO getNotificationByResourceId(Integer resourseId);

	public List<StudyPageBo> getOverviewStudyPagesById(String studyId,
			Integer userId);

	public HashMap<String, List<ReferenceTablesBo>> getreferenceListByCategory();

	public ResourceBO getResourceInfo(Integer resourceInfoId);

	public List<ResourceBO> getResourceList(Integer studyId);

	public List<NotificationBO> getSavedNotification(Integer studyId);

	public StudyBo getStudyById(String studyId, Integer userId);

	public EligibilityBo getStudyEligibiltyByStudyId(String studyId);

	public List<StudyListBean> getStudyList(Integer userId);

	public List<StudyListBean> getStudyListByUserId(Integer userId);

	public StudyBo getStudyLiveStatusByCustomId(String customStudyId);

	public ResourceBO getStudyProtocol(Integer studyId);

	public String markAsCompleted(int studyId, String markCompleted,
			boolean flag, SessionObject sesObj, String customStudyId);

	public String reOrderComprehensionTestQuestion(Integer studyId,
			int oldOrderNumber, int newOrderNumber);

	public String reOrderConsentInfoList(Integer studyId, int oldOrderNumber,
			int newOrderNumber);

	public String reorderEligibilityTestQusAns(Integer eligibilityId,
			int oldOrderNumber, int newOrderNumber, Integer studyId);

	public String reOrderResourceList(Integer studyId, int oldOrderNumber,
			int newOrderNumber);

	public boolean resetDraftStudyByCustomStudyId(String customStudyId,
			String action, SessionObject sesObj);

	public int resourceOrder(Integer studyId);

	public List<ResourceBO> resourcesSaved(Integer studyId);

	public List<ResourceBO> resourcesWithAnchorDate(Integer studyId);

	public ConsentBo saveOrCompleteConsentReviewDetails(ConsentBo consentBo,
			SessionObject sesObj, String customStudyId);

	public Integer saveOrDoneChecklist(Checklist checklist);

	public ComprehensionTestQuestionBo saveOrUpdateComprehensionTestQuestion(
			ComprehensionTestQuestionBo comprehensionTestQuestionBo);

	public ConsentInfoBo saveOrUpdateConsentInfo(ConsentInfoBo consentInfoBo,
			SessionObject sesObj, String customStudyId);

	public Integer saveOrUpdateEligibilityTestQusAns(
			EligibilityTestBo eligibilityTestBo, Integer studyId,
			SessionObject sessionObject, String customStudyId);

	public String saveOrUpdateOverviewStudyPages(StudyPageBean studyPageBean,
			SessionObject sesObj);

	public Integer saveOrUpdateResource(ResourceBO resourceBO);

	public String saveOrUpdateStudy(StudyBo studyBo, SessionObject sessionObject);

	public String saveOrUpdateStudyEligibilty(EligibilityBo eligibilityBo,
			SessionObject sesObj, String customStudyId);

	public String saveOrUpdateStudySettings(StudyBo studyBo,
			SessionObject sesObj, String userIds, String permissions,
			String projectLead);

	public String saveResourceNotification(NotificationBO notificationBO,
			boolean notiFlag);

	public String updateStudyActionOnAction(String studyId, String buttonText,
			SessionObject sesObj);

	public String validateActivityComplete(String studyId, String action);

	public String validateEligibilityTestKey(Integer eligibilityTestId,
			String shortTitle, Integer eligibilityId);

	public String validateStudyAction(String studyId, String buttonText);

	public boolean validateStudyId(String studyId);

	public List<EligibilityTestBo> viewEligibilityTestQusAnsByEligibilityId(
			Integer eligibilityId);

	public EligibilityTestBo viewEligibilityTestQusAnsById(
			Integer eligibilityTestId);
	
	public Boolean isAnchorDateExistForEnrollment(Integer studyId, String customStudyId);
	
	public Boolean isAnchorDateExistForEnrollmentDraftStudy(Integer studyId, String customStudyId);
	
	public String updateAnchordateForEnrollmentDate(StudyBo oldStudyBo,StudyBo updatedStudyBo, Session session, Transaction transaction);
	
	public boolean validateAppId(String customStudyId, String appId, String studyType);
	
	public StudyPermissionBO getStudyPermissionBO(int studyId,int userId);
}
