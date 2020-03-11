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

/**
 * The persistent class for the instructions database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "instructions")
@NamedQueries({ @NamedQuery(name = "getInstructionStep", query = "from InstructionsBo IBO where IBO.id=:id and IBO.active=1"), })
public class InstructionsBo implements Serializable {

	private static final long serialVersionUID = 1389506581768527442L;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "instruction_text", length = 2500)
	private String instructionText;

	@Column(name = "instruction_title", length = 250)
	private String instructionTitle;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Transient
	private Integer questionnaireId;

	@Transient
	private QuestionnairesStepsBo questionnairesStepsBo;

	@Column(name = "status")
	private Boolean status;

	@Transient
	private String type;

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

	public String getInstructionText() {
		return instructionText;
	}

	public String getInstructionTitle() {
		return instructionTitle;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public Integer getQuestionnaireId() {
		return questionnaireId;
	}

	public QuestionnairesStepsBo getQuestionnairesStepsBo() {
		return questionnairesStepsBo;
	}

	public Boolean getStatus() {
		return status;
	}

	public String getType() {
		return type;
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

	public void setInstructionText(String instructionText) {
		this.instructionText = instructionText;
	}

	public void setInstructionTitle(String instructionTitle) {
		this.instructionTitle = instructionTitle;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setQuestionnaireId(Integer questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public void setQuestionnairesStepsBo(
			QuestionnairesStepsBo questionnairesStepsBo) {
		this.questionnairesStepsBo = questionnairesStepsBo;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

}
