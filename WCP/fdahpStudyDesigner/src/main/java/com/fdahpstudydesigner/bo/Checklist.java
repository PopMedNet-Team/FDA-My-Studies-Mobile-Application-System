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
 * The persistent class for the study_checklist database table.
 * 
 * @author BTC
 *
 */

@Entity
@Table(name = "study_checklist")
@NamedQueries({ @NamedQuery(name = "getchecklistInfo", query = "SELECT CBO FROM Checklist CBO WHERE CBO.studyId =:studyId"), })
public class Checklist implements Serializable {

	private static final long serialVersionUID = 7206666243059395497L;

	@Column(name = "checkbox1", length = 1)
	private boolean checkbox1 = false;

	@Column(name = "checkbox10", length = 1)
	private boolean checkbox10 = false;

	@Column(name = "checkbox11", length = 1)
	private boolean checkbox11 = false;

	@Column(name = "checkbox12", length = 1)
	private boolean checkbox12 = false;

	@Column(name = "checkbox2", length = 1)
	private boolean checkbox2 = false;

	@Column(name = "checkbox3", length = 1)
	private boolean checkbox3 = false;

	@Column(name = "checkbox4", length = 1)
	private boolean checkbox4 = false;

	@Column(name = "checkbox5", length = 1)
	private boolean checkbox5 = false;

	@Column(name = "checkbox6", length = 1)
	private boolean checkbox6 = false;

	@Column(name = "checkbox7", length = 1)
	private boolean checkbox7 = false;

	@Column(name = "checkbox8", length = 1)
	private boolean checkbox8 = false;

	@Column(name = "checkbox9", length = 1)
	private boolean checkbox9 = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "checklist_id")
	private Integer checklistId;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "study_id")
	private Integer studyId;

	public Integer getChecklistId() {
		return checklistId;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public boolean isCheckbox1() {
		return checkbox1;
	}

	public boolean isCheckbox10() {
		return checkbox10;
	}

	public boolean isCheckbox11() {
		return checkbox11;
	}

	public boolean isCheckbox12() {
		return checkbox12;
	}

	public boolean isCheckbox2() {
		return checkbox2;
	}

	public boolean isCheckbox3() {
		return checkbox3;
	}

	public boolean isCheckbox4() {
		return checkbox4;
	}

	public boolean isCheckbox5() {
		return checkbox5;
	}

	public boolean isCheckbox6() {
		return checkbox6;
	}

	public boolean isCheckbox7() {
		return checkbox7;
	}

	public boolean isCheckbox8() {
		return checkbox8;
	}

	public boolean isCheckbox9() {
		return checkbox9;
	}

	public void setCheckbox1(boolean checkbox1) {
		this.checkbox1 = checkbox1;
	}

	public void setCheckbox10(boolean checkbox10) {
		this.checkbox10 = checkbox10;
	}

	public void setCheckbox11(boolean checkbox11) {
		this.checkbox11 = checkbox11;
	}

	public void setCheckbox12(boolean checkbox12) {
		this.checkbox12 = checkbox12;
	}

	public void setCheckbox2(boolean checkbox2) {
		this.checkbox2 = checkbox2;
	}

	public void setCheckbox3(boolean checkbox3) {
		this.checkbox3 = checkbox3;
	}

	public void setCheckbox4(boolean checkbox4) {
		this.checkbox4 = checkbox4;
	}

	public void setCheckbox5(boolean checkbox5) {
		this.checkbox5 = checkbox5;
	}

	public void setCheckbox6(boolean checkbox6) {
		this.checkbox6 = checkbox6;
	}

	public void setCheckbox7(boolean checkbox7) {
		this.checkbox7 = checkbox7;
	}

	public void setCheckbox8(boolean checkbox8) {
		this.checkbox8 = checkbox8;
	}

	public void setCheckbox9(boolean checkbox9) {
		this.checkbox9 = checkbox9;
	}

	public void setChecklistId(Integer checklistId) {
		this.checklistId = checklistId;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}
}
