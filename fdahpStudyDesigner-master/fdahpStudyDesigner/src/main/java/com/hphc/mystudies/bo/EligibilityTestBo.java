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

import org.hibernate.annotations.Type;

/**
 * Eligibility test question and answer of a {@link EligibilityBo} object of
 * type "Eligibility Test"
 *
 * @author BTC
 *
 */
@Entity
@Table(name = "eligibility_test")
@NamedQueries({
		@NamedQuery(name = "EligibilityTestBo.findAll", query = "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true ORDER BY ETB.sequenceNo"),
		@NamedQuery(name = "EligibilityTestBo.findById", query = "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true AND ETB.id=:eligibilityTestId ORDER BY ETB.sequenceNo"),
		@NamedQuery(name = "EligibilityTestBo.findByEligibilityId", query = "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true AND ETB.eligibilityId=:eligibilityId ORDER BY ETB.sequenceNo"),
		@NamedQuery(name = "EligibilityTestBo.findByEligibilityIdAndSequenceNo", query = "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true AND ETB.eligibilityId=:eligibilityId AND ETB.sequenceNo =:sequenceNo"),
		@NamedQuery(name = "EligibilityTestBo.deleteById", query = "UPDATE EligibilityTestBo SET active = false WHERE id=:eligibilityTestId "),
		@NamedQuery(name = "EligibilityTestBo.validateShortTitle", query = "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.shortTitle =:shortTitle AND ETB.id !=:eligibilityTestId AND ETB.eligibilityId =:eligibilityId ") })
public class EligibilityTestBo implements Serializable {

	private static final long serialVersionUID = -6517033483482921515L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "eligibility_id")
	private Integer eligibilityId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "question")
	private String question;

	@Column(name = "response_format")
	private String responseFormat;

	@Column(name = "response_no_option")
	private Boolean responseNoOption;

	@Column(name = "response_yes_option")
	private Boolean responseYesOption;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "short_title")
	private String shortTitle;

	@Column(name = "status")
	private Boolean status = false;

	@Transient
	private String type;

	@Column(name = "is_used")
	@Type(type = "yes_no")
	private boolean used = false;

	public Boolean getActive() {
		return active;
	}

	public Integer getEligibilityId() {
		return eligibilityId;
	}

	public Integer getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public String getResponseFormat() {
		return responseFormat;
	}

	public Boolean getResponseNoOption() {
		return responseNoOption;
	}

	public Boolean getResponseYesOption() {
		return responseYesOption;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public Boolean getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	/**
	 * @return the used
	 */
	public boolean isUsed() {
		return used;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setEligibilityId(Integer eligibilityId) {
		this.eligibilityId = eligibilityId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
	}

	public void setResponseNoOption(Boolean responseNoOption) {
		this.responseNoOption = responseNoOption;
	}

	public void setResponseYesOption(Boolean responseYesOption) {
		this.responseYesOption = responseYesOption;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param used
	 *            the used to set
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}

}
