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
package com.hphc.mystudies.bo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the comprehension_test_question database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "comprehension_test_question")
public class ComprehensionTestQuestionBo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4092393873968937668L;

	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "question_text")
	private String questionText;

	@Transient
	private List<ComprehensionTestResponseBo> responseList;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "structure_of_correct_ans")
	private Boolean structureOfCorrectAns = true;

	@Column(name = "study_id")
	private Integer studyId;

	public Boolean getActive() {
		return active;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public Integer getId() {
		return id;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public String getQuestionText() {
		return questionText;
	}

	public List<ComprehensionTestResponseBo> getResponseList() {
		return responseList;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public Boolean getStatus() {
		return status;
	}

	public Boolean getStructureOfCorrectAns() {
		return structureOfCorrectAns;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public void setResponseList(List<ComprehensionTestResponseBo> responseList) {
		this.responseList = responseList;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setStructureOfCorrectAns(Boolean structureOfCorrectAns) {
		this.structureOfCorrectAns = structureOfCorrectAns;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

}
