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
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * Provides active task attribute details. i.e. chart and statistics
 * configuration settings, task attribute values, task datasource details and
 * formula details to plot charts.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "active_task_attrtibutes_values")
public class ActiveTaskAttrtibutesValuesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725947033876179386L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "active_task_attribute_id")
	private Integer attributeValueId;

	@Column(name = "active_task_id")
	private Integer activeTaskId;

	@Column(name = "active_task_master_attr_id")
	private Integer activeTaskMasterAttrId;

	@Column(name = "attribute_val")
	private String attributeVal;

	@Column(name = "add_to_line_chart")
	@Type(type = "yes_no")
	private boolean addToLineChart = false;

	@Column(name = "time_range_chart")
	private String timeRangeChart;

	@Column(name = "rollback_chat")
	private String rollbackChat;

	@Column(name = "title_chat")
	private String titleChat;

	@Column(name = "use_for_statistic")
	@Type(type = "yes_no")
	private boolean useForStatistic = false;

	@Column(name = "identifier_name_stat")
	private String identifierNameStat;

	@Column(name = "display_name_stat")
	private String displayNameStat;

	@Column(name = "display_units_stat")
	private String displayUnitStat;

	@Column(name = "upload_type_stat")
	private String uploadTypeStat;

	@Column(name = "formula_applied_stat")
	private String formulaAppliedStat;

	@Column(name = "time_range_stat")
	private String timeRangeStat;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "active")
	private Integer active = 0;

	@Transient
	private String activityId;

	@Transient
	private String activityVersion;

	@Transient
	private String activityType;

	@Transient
	private String activityStepKey;

	public Integer getAttributeValueId() {
		return attributeValueId;
	}

	public void setAttributeValueId(Integer attributeValueId) {
		this.attributeValueId = attributeValueId;
	}

	public Integer getActiveTaskId() {
		return activeTaskId;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public Integer getActiveTaskMasterAttrId() {
		return activeTaskMasterAttrId;
	}

	public void setActiveTaskMasterAttrId(Integer activeTaskMasterAttrId) {
		this.activeTaskMasterAttrId = activeTaskMasterAttrId;
	}

	public String getAttributeVal() {
		return attributeVal;
	}

	public void setAttributeVal(String attributeVal) {
		this.attributeVal = attributeVal;
	}

	public boolean isAddToLineChart() {
		return addToLineChart;
	}

	public void setAddToLineChart(boolean addToLineChart) {
		this.addToLineChart = addToLineChart;
	}

	public String getTimeRangeChart() {
		return timeRangeChart;
	}

	public void setTimeRangeChart(String timeRangeChart) {
		this.timeRangeChart = timeRangeChart;
	}

	public String getRollbackChat() {
		return rollbackChat;
	}

	public void setRollbackChat(String rollbackChat) {
		this.rollbackChat = rollbackChat;
	}

	public String getTitleChat() {
		return titleChat;
	}

	public void setTitleChat(String titleChat) {
		this.titleChat = titleChat;
	}

	public boolean isUseForStatistic() {
		return useForStatistic;
	}

	public void setUseForStatistic(boolean useForStatistic) {
		this.useForStatistic = useForStatistic;
	}

	public String getIdentifierNameStat() {
		return identifierNameStat;
	}

	public void setIdentifierNameStat(String identifierNameStat) {
		this.identifierNameStat = identifierNameStat;
	}

	public String getDisplayNameStat() {
		return displayNameStat;
	}

	public void setDisplayNameStat(String displayNameStat) {
		this.displayNameStat = displayNameStat;
	}

	public String getDisplayUnitStat() {
		return displayUnitStat;
	}

	public void setDisplayUnitStat(String displayUnitStat) {
		this.displayUnitStat = displayUnitStat;
	}

	public String getUploadTypeStat() {
		return uploadTypeStat;
	}

	public void setUploadTypeStat(String uploadTypeStat) {
		this.uploadTypeStat = uploadTypeStat;
	}

	public String getFormulaAppliedStat() {
		return formulaAppliedStat;
	}

	public void setFormulaAppliedStat(String formulaAppliedStat) {
		this.formulaAppliedStat = formulaAppliedStat;
	}

	public String getTimeRangeStat() {
		return timeRangeStat;
	}

	public void setTimeRangeStat(String timeRangeStat) {
		this.timeRangeStat = timeRangeStat;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityVersion() {
		return activityVersion;
	}

	public void setActivityVersion(String activityVersion) {
		this.activityVersion = activityVersion;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getActivityStepKey() {
		return activityStepKey;
	}

	public void setActivityStepKey(String activityStepKey) {
		this.activityStepKey = activityStepKey;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

}
