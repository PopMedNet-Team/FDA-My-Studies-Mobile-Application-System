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
package com.hphc.mystudies.dto;

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
 * Provides version details for below:
 * <ol>
 * <li>Study {@link StudyDto}
 * <li>Activity {@link ActiveTaskDto}, {@link QuestionnairesDto}
 * <li>Consent {@link ConsentDto}
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "study_version")
@NamedQueries({

		@NamedQuery(name = "getStudyVersionDetailsByCustomStudyId", query = "from StudyVersionDto SVDTO"
				+ " where SVDTO.customStudyId =:customStudyId"
				+ " ORDER BY SVDTO.versionId DESC"),

		@NamedQuery(name = "getStudyVersionsByCustomStudyId", query = "from StudyVersionDto SVDTO"
				+ " where SVDTO.customStudyId =:customStudyId"),

		@NamedQuery(name = "getLiveVersionDetailsByCustomStudyIdAndVersion", query = "from StudyVersionDto SVDTO"
				+ " where SVDTO.customStudyId =:customStudyId and ROUND(SVDTO.studyVersion, 1)=:studyVersion"
				+ " ORDER BY SVDTO.versionId DESC"),

		@NamedQuery(name = "getStudyUpdatesDetailsByCurrentVersion", query = "from StudyVersionDto SVDTO"
				+ " where SVDTO.customStudyId =:customStudyId and ROUND(SVDTO.studyVersion, 1)>=:studyVersion"), })
public class StudyVersionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4330801191289201775L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "version_id")
	private Integer versionId;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "study_version")
	private Float studyVersion = 0f;

	@Column(name = "activity_version")
	private Float activityVersion = 0f;

	@Column(name = "consent_version")
	private Float consentVersion = 0f;

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public Float getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Float studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Float getActivityVersion() {
		return activityVersion;
	}

	public void setActivityVersion(Float activityVersion) {
		this.activityVersion = activityVersion;
	}

	public Float getConsentVersion() {
		return consentVersion;
	}

	public void setConsentVersion(Float consentVersion) {
		this.consentVersion = consentVersion;
	}
}
