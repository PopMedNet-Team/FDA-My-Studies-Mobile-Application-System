package com.fdahpstudydesigner.bo;

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
