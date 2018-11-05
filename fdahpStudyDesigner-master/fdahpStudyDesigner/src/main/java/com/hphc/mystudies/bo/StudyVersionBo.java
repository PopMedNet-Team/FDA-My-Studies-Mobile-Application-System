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
