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
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * The persistent class for the active_task_attrtibutes_values database table.
 *
 * @author BTC
 *
 */
@Entity
@Table(name = "active_task_attrtibutes_values")
@NamedQueries({ @NamedQuery(name = "getAttributeListByActiveTAskId", query = "From ActiveTaskAtrributeValuesBo ABO where ABO.activeTaskId=:activeTaskId and ABO.active IS NOT NULL and ABO.active=1 order by attributeValueId asc"), })
public class ActiveTaskAtrributeValuesBo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "active")
	private Integer active = 0;

	@Column(name = "active_task_id")
	private Integer activeTaskId;

	@Column(name = "active_task_master_attr_id")
	private Integer activeTaskMasterAttrId;

	@Transient
	private boolean addToDashboard = false;

	@Column(name = "add_to_line_chart")
	@Type(type = "yes_no")
	private boolean addToLineChart = false;

	@Column(name = "attribute_val")
	private String attributeVal;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "active_task_attribute_id")
	private Integer attributeValueId;

	@Column(name = "display_name_stat")
	private String displayNameStat;

	@Column(name = "display_units_stat")
	private String displayUnitStat;

	@Column(name = "formula_applied_stat")
	private String formulaAppliedStat;

	@Column(name = "identifier_name_stat")
	private String identifierNameStat;

	@Transient
	private Integer isIdentifierNameStatDuplicate = 0;

	@Column(name = "rollback_chat")
	private String rollbackChat;

	@Column(name = "time_range_chart")
	private String timeRangeChart;

	@Column(name = "time_range_stat")
	private String timeRangeStat;

	@Column(name = "title_chat")
	private String titleChat;

	@Column(name = "upload_type_stat")
	private String uploadTypeStat;

	@Column(name = "use_for_statistic")
	@Type(type = "yes_no")
	private boolean useForStatistic = false;

	public Integer getActive() {
		return active;
	}

	public Integer getActiveTaskId() {
		return activeTaskId;
	}

	public Integer getActiveTaskMasterAttrId() {
		return activeTaskMasterAttrId;
	}

	public String getAttributeVal() {
		return attributeVal;
	}

	public Integer getAttributeValueId() {
		return attributeValueId;
	}

	public String getDisplayNameStat() {
		return displayNameStat;
	}

	public String getDisplayUnitStat() {
		return displayUnitStat;
	}

	public String getFormulaAppliedStat() {
		return formulaAppliedStat;
	}

	public String getIdentifierNameStat() {
		return identifierNameStat;
	}

	public Integer getIsIdentifierNameStatDuplicate() {
		return isIdentifierNameStatDuplicate;
	}

	public String getRollbackChat() {
		return rollbackChat;
	}

	public String getTimeRangeChart() {
		return timeRangeChart;
	}

	public String getTimeRangeStat() {
		return timeRangeStat;
	}

	public String getTitleChat() {
		return titleChat;
	}

	public String getUploadTypeStat() {
		return uploadTypeStat;
	}

	public boolean isAddToDashboard() {
		return addToDashboard;
	}

	public boolean isAddToLineChart() {
		return addToLineChart;
	}

	public boolean isUseForStatistic() {
		return useForStatistic;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public void setActiveTaskMasterAttrId(Integer activeTaskMasterAttrId) {
		this.activeTaskMasterAttrId = activeTaskMasterAttrId;
	}

	public void setAddToDashboard(boolean addToDashboard) {
		this.addToDashboard = addToDashboard;
	}

	public void setAddToLineChart(boolean addToLineChart) {
		this.addToLineChart = addToLineChart;
	}

	public void setAttributeVal(String attributeVal) {
		this.attributeVal = attributeVal;
	}

	public void setAttributeValueId(Integer attributeValueId) {
		this.attributeValueId = attributeValueId;
	}

	public void setDisplayNameStat(String displayNameStat) {
		this.displayNameStat = displayNameStat;
	}

	public void setDisplayUnitStat(String displayUnitStat) {
		this.displayUnitStat = displayUnitStat;
	}

	public void setFormulaAppliedStat(String formulaAppliedStat) {
		this.formulaAppliedStat = formulaAppliedStat;
	}

	public void setIdentifierNameStat(String identifierNameStat) {
		this.identifierNameStat = identifierNameStat;
	}

	public void setIsIdentifierNameStatDuplicate(
			Integer isIdentifierNameStatDuplicate) {
		this.isIdentifierNameStatDuplicate = isIdentifierNameStatDuplicate;
	}

	public void setRollbackChat(String rollbackChat) {
		this.rollbackChat = rollbackChat;
	}

	public void setTimeRangeChart(String timeRangeChart) {
		this.timeRangeChart = timeRangeChart;
	}

	public void setTimeRangeStat(String timeRangeStat) {
		this.timeRangeStat = timeRangeStat;
	}

	public void setTitleChat(String titleChat) {
		this.titleChat = titleChat;
	}

	public void setUploadTypeStat(String uploadTypeStat) {
		this.uploadTypeStat = uploadTypeStat;
	}

	public void setUseForStatistic(boolean useForStatistic) {
		this.useForStatistic = useForStatistic;
	}

}
