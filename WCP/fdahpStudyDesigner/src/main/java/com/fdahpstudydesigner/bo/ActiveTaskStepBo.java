package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the active_task_steps database table.
 *
 * @author BTC
 */
@Entity
@Table(name = "active_task_steps")
@NamedQuery(name = "ActiveTaskStepBo.findAll", query = "SELECT a FROM ActiveTaskStepBo a")
public class ActiveTaskStepBo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "active_task_id")
	private Integer activetaskId;

	@Column(name = "active_task_stepscol")
	private String activeTaskStepscol;

	@Column(name = "sd_live_form_id")
	private String sdLiveFormId;

	@Column(name = "sequence_no")
	private int sequenceNo;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "step_id")
	private Integer stepId;

	public ActiveTaskStepBo() {
		// Do nothing
	}

	public Integer getActivetaskId() {
		return activetaskId;
	}

	public String getActiveTaskStepscol() {
		return this.activeTaskStepscol;
	}

	public String getSdLiveFormId() {
		return this.sdLiveFormId;
	}

	public int getSequenceNo() {
		return this.sequenceNo;
	}

	public Integer getStepId() {
		return this.stepId;
	}

	public void setActivetaskId(Integer activetaskId) {
		this.activetaskId = activetaskId;
	}

	public void setActiveTaskStepscol(String activeTaskStepscol) {
		this.activeTaskStepscol = activeTaskStepscol;
	}

	public void setSdLiveFormId(String sdLiveFormId) {
		this.sdLiveFormId = sdLiveFormId;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

}