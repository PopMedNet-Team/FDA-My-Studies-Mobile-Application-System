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
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * The persistent class for the question_responsetype_master_info database
 * table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "question_responsetype_master_info")
@NamedQueries({ @NamedQuery(name = "getResponseTypes", query = "from QuestionResponseTypeMasterInfoBo QRTMBO"), })
public class QuestionResponseTypeMasterInfoBo implements Serializable {

	private static final long serialVersionUID = -2666359241071290949L;

	@Column(name = "anchor_date")
	private Boolean anchorDate;

	@Column(name = "choice_based_branching")
	private Boolean choinceBasedBraching;

	@Column(name = "dashboard_allowed")
	private Boolean dashBoardAllowed;

	@Column(name = "data_type")
	private String dataType;

	@Column(name = "description")
	private String description;

	@Column(name = "formula_based_logic")
	private Boolean formulaBasedLogic;

	@Column(name = "healthkit_alternative")
	private Boolean healthkitAlternative;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "response_type")
	private String responseType;

	@Column(name = "response_type_code")
	private String responseTypeCode;

	public Boolean getAnchorDate() {
		return anchorDate;
	}

	public Boolean getChoinceBasedBraching() {
		return choinceBasedBraching;
	}

	public Boolean getDashBoardAllowed() {
		return dashBoardAllowed;
	}

	public String getDataType() {
		return dataType;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getFormulaBasedLogic() {
		return formulaBasedLogic;
	}

	public Boolean getHealthkitAlternative() {
		return healthkitAlternative;
	}

	public Integer getId() {
		return id;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setAnchorDate(Boolean anchorDate) {
		this.anchorDate = anchorDate;
	}

	public void setChoinceBasedBraching(Boolean choinceBasedBraching) {
		this.choinceBasedBraching = choinceBasedBraching;
	}

	public void setDashBoardAllowed(Boolean dashBoardAllowed) {
		this.dashBoardAllowed = dashBoardAllowed;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFormulaBasedLogic(Boolean formulaBasedLogic) {
		this.formulaBasedLogic = formulaBasedLogic;
	}

	public void setHealthkitAlternative(Boolean healthkitAlternative) {
		this.healthkitAlternative = healthkitAlternative;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
}
