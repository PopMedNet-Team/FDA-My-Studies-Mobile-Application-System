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

/**
 * Provides question response type master information.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "question_responsetype_master_info")
public class QuestionResponsetypeMasterInfoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 721954555522068688L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "anchor_date")
	private Boolean anchorDate = false;

	@Column(name = "choice_based_branching")
	private Boolean choiceBasedBranching = false;

	@Column(name = "dashboard_allowed")
	private Boolean dashboardAllowed = false;

	@Column(name = "data_type")
	private String dataType;

	@Column(name = "description")
	private String description;

	@Column(name = "formula_based_logic")
	private Boolean formulaBasedLogic = false;

	@Column(name = "healthkit_alternative")
	private Boolean healthkitAlternative = false;

	@Column(name = "response_type")
	private String responseType;

	@Column(name = "response_type_code")
	private String responseTypeCode;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getAnchorDate() {
		return anchorDate;
	}

	public void setAnchorDate(Boolean anchorDate) {
		this.anchorDate = anchorDate;
	}

	public Boolean getChoiceBasedBranching() {
		return choiceBasedBranching;
	}

	public void setChoiceBasedBranching(Boolean choiceBasedBranching) {
		this.choiceBasedBranching = choiceBasedBranching;
	}

	public Boolean getDashboardAllowed() {
		return dashboardAllowed;
	}

	public void setDashboardAllowed(Boolean dashboardAllowed) {
		this.dashboardAllowed = dashboardAllowed;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getFormulaBasedLogic() {
		return formulaBasedLogic;
	}

	public void setFormulaBasedLogic(Boolean formulaBasedLogic) {
		this.formulaBasedLogic = formulaBasedLogic;
	}

	public Boolean getHealthkitAlternative() {
		return healthkitAlternative;
	}

	public void setHealthkitAlternative(Boolean healthkitAlternative) {
		this.healthkitAlternative = healthkitAlternative;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getResponseTypeCode() {
		return responseTypeCode;
	}

	public void setResponseTypeCode(String responseTypeCode) {
		this.responseTypeCode = responseTypeCode;
	}

}
