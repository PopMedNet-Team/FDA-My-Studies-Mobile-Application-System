/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Provides information about status of the sub tasks for study {@link StudyDto}.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "study_sequence")
@NamedQueries({

@NamedQuery(name = "getStudySequenceDetailsByStudyId", query = "from StudySequenceDto SSDTO"
		+ " where SSDTO.studyId =:studyId "), })
public class StudySequenceDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6095431690838787358L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_sequence_id")
	private Integer studySequenceId;

	@Column(name = "actions")
	private String actions;

	@Column(name = "basic_info")
	private String basicInfo;

	@Column(name = "check_list")
	private String checkList;

	@Column(name = "comprehension_test")
	private String comprehensionTest;

	@Column(name = "consent_edu_info")
	private String consentEduInfo;

	@Column(name = "e_consent")
	private String eConsent;

	@Column(name = "eligibility")
	private String eligibility;

	@Column(name = "miscellaneous_branding")
	private String miscellaneousBranding;

	@Column(name = "miscellaneous_notification")
	private String miscellaneousNotification;

	@Column(name = "miscellaneous_resources")
	private String miscellaneousResources;

	@Column(name = "over_view")
	private String overView;

	@Column(name = "setting_admins")
	private String settingAdmins;

	@Column(name = "study_dashboard_chart")
	private String studyDashboardChart;

	@Column(name = "study_dashboard_stats")
	private String studyDashboardStats;

	@Column(name = "study_exc_active_task")
	private String studyExcActiveTask;

	@Column(name = "study_exc_questionnaries")
	private String studyExcQuestionnaries;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getStudySequenceId() {
		return studySequenceId;
	}

	public void setStudySequenceId(Integer studySequenceId) {
		this.studySequenceId = studySequenceId;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(String basicInfo) {
		this.basicInfo = basicInfo;
	}

	public String getCheckList() {
		return checkList;
	}

	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}

	public String getComprehensionTest() {
		return comprehensionTest;
	}

	public void setComprehensionTest(String comprehensionTest) {
		this.comprehensionTest = comprehensionTest;
	}

	public String getConsentEduInfo() {
		return consentEduInfo;
	}

	public void setConsentEduInfo(String consentEduInfo) {
		this.consentEduInfo = consentEduInfo;
	}

	public String geteConsent() {
		return eConsent;
	}

	public void seteConsent(String eConsent) {
		this.eConsent = eConsent;
	}

	public String getEligibility() {
		return eligibility;
	}

	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}

	public String getMiscellaneousBranding() {
		return miscellaneousBranding;
	}

	public void setMiscellaneousBranding(String miscellaneousBranding) {
		this.miscellaneousBranding = miscellaneousBranding;
	}

	public String getMiscellaneousNotification() {
		return miscellaneousNotification;
	}

	public void setMiscellaneousNotification(String miscellaneousNotification) {
		this.miscellaneousNotification = miscellaneousNotification;
	}

	public String getMiscellaneousResources() {
		return miscellaneousResources;
	}

	public void setMiscellaneousResources(String miscellaneousResources) {
		this.miscellaneousResources = miscellaneousResources;
	}

	public String getOverView() {
		return overView;
	}

	public void setOverView(String overView) {
		this.overView = overView;
	}

	public String getSettingAdmins() {
		return settingAdmins;
	}

	public void setSettingAdmins(String settingAdmins) {
		this.settingAdmins = settingAdmins;
	}

	public String getStudyDashboardChart() {
		return studyDashboardChart;
	}

	public void setStudyDashboardChart(String studyDashboardChart) {
		this.studyDashboardChart = studyDashboardChart;
	}

	public String getStudyDashboardStats() {
		return studyDashboardStats;
	}

	public void setStudyDashboardStats(String studyDashboardStats) {
		this.studyDashboardStats = studyDashboardStats;
	}

	public String getStudyExcActiveTask() {
		return studyExcActiveTask;
	}

	public void setStudyExcActiveTask(String studyExcActiveTask) {
		this.studyExcActiveTask = studyExcActiveTask;
	}

	public String getStudyExcQuestionnaries() {
		return studyExcQuestionnaries;
	}

	public void setStudyExcQuestionnaries(String studyExcQuestionnaries) {
		this.studyExcQuestionnaries = studyExcQuestionnaries;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

}
