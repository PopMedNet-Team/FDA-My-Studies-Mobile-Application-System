/*******************************************************************************
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" ,WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
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
