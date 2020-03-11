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
 * The persistent class for the study_version database table.
 *
 * @author BTC
 *
 */
@Entity
@Table(name = "study_version")
@NamedQueries({
		@NamedQuery(name = "StudyVersionBo.findAll", query = "SELECT s FROM StudyVersionBo s"),
		@NamedQuery(name = "getStudyByCustomStudyId", query = " From StudyVersionBo SVBO WHERE SVBO.customStudyId =:customStudyId order by versionId DESC LIMIT 1"),
		@NamedQuery(name = "getStudyVersionsByCustomStudyId", query = " From StudyVersionBo SVBO WHERE SVBO.customStudyId =:customStudyId order by versionId") })
public class StudyVersionBo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String activityLVersion = "";

	@Column(name = "activity_version")
	private Float activityVersion = 0f;

	@Transient
	private String consentLVersion = "";

	@Column(name = "consent_version")
	private Float consentVersion = 0f;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Transient
	private String studyLVersion = "";

	@Column(name = "study_version")
	private Float studyVersion = 0f;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "version_id")
	private Integer versionId;

	public String getActivityLVersion() {
		return activityLVersion;
	}

	public Float getActivityVersion() {
		return activityVersion;
	}

	public String getConsentLVersion() {
		return consentLVersion;
	}

	public Float getConsentVersion() {
		return consentVersion;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public String getStudyLVersion() {
		return studyLVersion;
	}

	public Float getStudyVersion() {
		return studyVersion;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setActivityLVersion(String activityLVersion) {
		this.activityLVersion = activityLVersion;
	}

	public void setActivityVersion(Float activityVersion) {
		this.activityVersion = activityVersion;
	}

	public void setConsentLVersion(String consentLVersion) {
		this.consentLVersion = consentLVersion;
	}

	public void setConsentVersion(Float consentVersion) {
		this.consentVersion = consentVersion;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setStudyLVersion(String studyLVersion) {
		this.studyLVersion = studyLVersion;
	}

	public void setStudyVersion(Float studyVersion) {
		this.studyVersion = studyVersion;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

}
