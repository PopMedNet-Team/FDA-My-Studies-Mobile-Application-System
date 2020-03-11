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
 * The persistent class for the form_mapping database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "form_mapping")
@NamedQueries({
		@NamedQuery(name = "getFormMappingBO", query = "from FormMappingBo FMBO where FMBO.questionId=:questionId"),
		@NamedQuery(name = "updateFromQuestionSequenceNo", query = "update FormMappingBo f set f.sequenceNo=:newOrderNumber where f.id=:id and f.active=1"),
		@NamedQuery(name = "getFromByIdAndSequenceNo", query = "From FormMappingBo FMBO where FMBO.formId=:formId and FMBO.sequenceNo=:oldOrderNumber and FMBO.active=1"),
		@NamedQuery(name = "deleteFormQuestion", query = "delete from FormMappingBo FMBO where FMBO.formId=:formId and FMBO.questionId=:questionId"),
		@NamedQuery(name = "getFormQuestion", query = "from FormMappingBo FMBO where FMBO.formId=:formId and FMBO.questionId=:questionId"),
		@NamedQuery(name = "getFormByFormId", query = "from FormMappingBo FMBO where FMBO.formId=:formId order by id desc"), })
public class FormMappingBo implements Serializable {

	private static final long serialVersionUID = -1590511768535969365L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "question_id")
	private Integer questionId;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "active")
	private Boolean active = true;

	public Boolean getActive() {
		return active;
	}

	public Integer getFormId() {
		return formId;
	}

	public Integer getId() {
		return id;
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

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

}
