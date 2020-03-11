package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * The persistent class for the study_sequence database table.
 * 
 * @author BTC
 *
 */

@Entity
@Table(name = "study_sequence")
@NamedQueries({
		@NamedQuery(name = "getStudySequenceById", query = " From StudySequenceBo SSBO WHERE SSBO.studySequenceId =:studySequenceId"),
		@NamedQuery(name = "getStudySequenceByStudyId", query = " From StudySequenceBo SSBO WHERE SSBO.studyId =:studyId") })
public class StudySequenceBo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3573683893623838475L;

	@Column(name = "actions")
	@Type(type = "yes_no")
	private boolean actions = false;

	@Column(name = "basic_info")
	@Type(type = "yes_no")
	private boolean basicInfo = false;

	@Column(name = "check_list")
	@Type(type = "yes_no")
	private boolean checkList = false;

	@Column(name = "comprehension_test")
	@Type(type = "yes_no")
	private boolean comprehensionTest = false;

	@Column(name = "consent_edu_info")
	@Type(type = "yes_no")
	private boolean consentEduInfo = false;

	@Column(name = "e_consent")
	@Type(type = "yes_no")
	private boolean eConsent = false;

	@Column(name = "eligibility")
	@Type(type = "yes_no")
	private boolean eligibility = false;

	@Column(name = "miscellaneous_branding")
	@Type(type = "yes_no")
	private boolean miscellaneousBranding = false;

	@Column(name = "miscellaneous_notification")
	@Type(type = "yes_no")
	private boolean miscellaneousNotification = false;

	@Column(name = "miscellaneous_resources")
	@Type(type = "yes_no")
	private boolean miscellaneousResources = false;

	@Column(name = "over_view")
	@Type(type = "yes_no")
	private boolean overView = false;

	@Column(name = "setting_admins")
	@Type(type = "yes_no")
	private boolean settingAdmins = false;

	@Column(name = "study_dashboard_chart")
	@Type(type = "yes_no")
	private boolean studyDashboardChart = false;

	@Column(name = "study_dashboard_stats")
	@Type(type = "yes_no")
	private boolean studyDashboardStats = false;

	@Column(name = "study_exc_active_task")
	@Type(type = "yes_no")
	private boolean studyExcActiveTask = false;

	@Column(name = "study_exc_questionnaries")
	@Type(type = "yes_no")
	private boolean studyExcQuestionnaries = false;

	@Column(name = "study_id")
	private Integer studyId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_sequence_id")
	private Integer studySequenceId;

	public Integer getStudyId() {
		return studyId;
	}

	public Integer getStudySequenceId() {
		return studySequenceId;
	}

	public boolean isActions() {
		return actions;
	}

	public boolean isBasicInfo() {
		return basicInfo;
	}

	public boolean isCheckList() {
		return checkList;
	}

	public boolean isComprehensionTest() {
		return comprehensionTest;
	}

	public boolean isConsentEduInfo() {
		return consentEduInfo;
	}

	public boolean iseConsent() {
		return eConsent;
	}

	public boolean isEligibility() {
		return eligibility;
	}

	public boolean isMiscellaneousBranding() {
		return miscellaneousBranding;
	}

	public boolean isMiscellaneousNotification() {
		return miscellaneousNotification;
	}

	public boolean isMiscellaneousResources() {
		return miscellaneousResources;
	}

	public boolean isOverView() {
		return overView;
	}

	public boolean isSettingAdmins() {
		return settingAdmins;
	}

	public boolean isStudyDashboardChart() {
		return studyDashboardChart;
	}

	public boolean isStudyDashboardStats() {
		return studyDashboardStats;
	}

	public boolean isStudyExcActiveTask() {
		return studyExcActiveTask;
	}

	public boolean isStudyExcQuestionnaries() {
		return studyExcQuestionnaries;
	}

	public void setActions(boolean actions) {
		this.actions = actions;
	}

	public void setBasicInfo(boolean basicInfo) {
		this.basicInfo = basicInfo;
	}

	public void setCheckList(boolean checkList) {
		this.checkList = checkList;
	}

	public void setComprehensionTest(boolean comprehensionTest) {
		this.comprehensionTest = comprehensionTest;
	}

	public void setConsentEduInfo(boolean consentEduInfo) {
		this.consentEduInfo = consentEduInfo;
	}

	public void seteConsent(boolean eConsent) {
		this.eConsent = eConsent;
	}

	public void setEligibility(boolean eligibility) {
		this.eligibility = eligibility;
	}

	public void setMiscellaneousBranding(boolean miscellaneousBranding) {
		this.miscellaneousBranding = miscellaneousBranding;
	}

	public void setMiscellaneousNotification(boolean miscellaneousNotification) {
		this.miscellaneousNotification = miscellaneousNotification;
	}

	public void setMiscellaneousResources(boolean miscellaneousResources) {
		this.miscellaneousResources = miscellaneousResources;
	}

	public void setOverView(boolean overView) {
		this.overView = overView;
	}

	public void setSettingAdmins(boolean settingAdmins) {
		this.settingAdmins = settingAdmins;
	}

	public void setStudyDashboardChart(boolean studyDashboardChart) {
		this.studyDashboardChart = studyDashboardChart;
	}

	public void setStudyDashboardStats(boolean studyDashboardStats) {
		this.studyDashboardStats = studyDashboardStats;
	}

	public void setStudyExcActiveTask(boolean studyExcActiveTask) {
		this.studyExcActiveTask = studyExcActiveTask;
	}

	public void setStudyExcQuestionnaries(boolean studyExcQuestionnaries) {
		this.studyExcQuestionnaries = studyExcQuestionnaries;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setStudySequenceId(Integer studySequenceId) {
		this.studySequenceId = studySequenceId;
	}
}
