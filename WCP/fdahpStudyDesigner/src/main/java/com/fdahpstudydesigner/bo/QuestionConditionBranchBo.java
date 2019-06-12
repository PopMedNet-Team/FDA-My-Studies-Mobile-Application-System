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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the questionnaires database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "question_condtion_branching")
@NamedQueries({ @NamedQuery(name = "getQuestionConditionBranchList", query = "from QuestionConditionBranchBo QCBO where QCBO.questionId=:questionId order by QCBO.sequenceNo ASC"), })
public class QuestionConditionBranchBo implements Serializable {

	private static final long serialVersionUID = 8189512029031610252L;

	@Column(name = "active")
	private Boolean active;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "condition_id")
	private Integer conditionId;

	@Column(name = "input_type")
	private String inputType;

	@Column(name = "input_type_value")
	private String inputTypeValue;

	@Column(name = "parent_sequence_no")
	private Integer parentSequenceNo;

	@Transient
	private List<QuestionConditionBranchBo> questionConditionBranchBos;

	@Column(name = "question_id")
	private Integer questionId;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	public Boolean getActive() {
		return active;
	}

	public Integer getConditionId() {
		return conditionId;
	}

	public String getInputType() {
		return inputType;
	}

	public String getInputTypeValue() {
		return inputTypeValue;
	}

	public Integer getParentSequenceNo() {
		return parentSequenceNo;
	}

	public List<QuestionConditionBranchBo> getQuestionConditionBranchBos() {
		return questionConditionBranchBos;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public void setInputTypeValue(String inputTypeValue) {
		this.inputTypeValue = inputTypeValue;
	}

	public void setParentSequenceNo(Integer parentSequenceNo) {
		this.parentSequenceNo = parentSequenceNo;
	}

	public void setQuestionConditionBranchBos(
			List<QuestionConditionBranchBo> questionConditionBranchBos) {
		this.questionConditionBranchBos = questionConditionBranchBos;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

}
