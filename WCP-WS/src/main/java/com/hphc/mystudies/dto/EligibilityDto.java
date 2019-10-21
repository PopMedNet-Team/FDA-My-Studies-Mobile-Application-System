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
 * Provides eligibility details for study {@link StudyDto}.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "eligibility")
@NamedQueries({

@NamedQuery(name = "eligibilityDtoByStudyId", query = "from EligibilityDto EDTO"
		+ " where EDTO.studyId =:studyId "), })
public class EligibilityDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4520158278072115802L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "eligibility_mechanism")
	private Integer eligibilityMechanism;

	@Column(name = "instructional_text")
	private String instructionalText;

	@Column(name = "failure_outcome_text")
	private String failureOutcomeText;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getEligibilityMechanism() {
		return eligibilityMechanism;
	}

	public void setEligibilityMechanism(Integer eligibilityMechanism) {
		this.eligibilityMechanism = eligibilityMechanism;
	}

	public String getInstructionalText() {
		return instructionalText;
	}

	public void setInstructionalText(String instructionalText) {
		this.instructionalText = instructionalText;
	}

	public String getFailureOutcomeText() {
		return failureOutcomeText;
	}

	public void setFailureOutcomeText(String failureOutcomeText) {
		this.failureOutcomeText = failureOutcomeText;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

}
