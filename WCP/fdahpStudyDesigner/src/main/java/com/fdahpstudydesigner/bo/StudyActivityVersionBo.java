package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the study_activity_version database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "study_activity_version")
public class StudyActivityVersionBo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8912773395341094340L;

	@Column(name = "activity_id")
	private Integer activityId;

	@Column(name = "activity_type")
	private String activityType;

	@Column(name = "activity_version")
	private Float activityVersion;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "short_title")
	private String shortTitle;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_activity_id")
	private Integer studyActivityId;

	@Column(name = "study_version")
	private Float studyVersion;

	public Integer getActivityId() {
		return activityId;
	}

	public String getActivityType() {
		return activityType;
	}

	public Float getActivityVersion() {
		return activityVersion;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public Integer getStudyActivityId() {
		return studyActivityId;
	}

	public Float getStudyVersion() {
		return studyVersion;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public void setActivityVersion(Float activityVersion) {
		this.activityVersion = activityVersion;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public void setStudyActivityId(Integer studyActivityId) {
		this.studyActivityId = studyActivityId;
	}

	public void setStudyVersion(Float studyVersion) {
		this.studyVersion = studyVersion;
	}
}
