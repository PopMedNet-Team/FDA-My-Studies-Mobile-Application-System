package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the eligibility_test_response database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "eligibility_test_response")
public class EligibilityTestResponseBo implements Serializable {

	private static final long serialVersionUID = -6967340852884815498L;

	@Column(name = "destination_question")
	private Integer destinationQuestion;

	@Column(name = "eligibility_test_id")
	private Integer eligibilityTestId;

	@Column(name = "pass_fail")
	private String passFail;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "response_id")
	private Integer responseId;

	@Column(name = "response_option")
	private String responseOption;

	public Integer getDestinationQuestion() {
		return destinationQuestion;
	}

	public Integer getEligibilityTestId() {
		return eligibilityTestId;
	}

	public String getPassFail() {
		return passFail;
	}

	public Integer getResponseId() {
		return responseId;
	}

	public String getResponseOption() {
		return responseOption;
	}

	public void setDestinationQuestion(Integer destinationQuestion) {
		this.destinationQuestion = destinationQuestion;
	}

	public void setEligibilityTestId(Integer eligibilityTestId) {
		this.eligibilityTestId = eligibilityTestId;
	}

	public void setPassFail(String passFail) {
		this.passFail = passFail;
	}

	public void setResponseId(Integer responseId) {
		this.responseId = responseId;
	}

	public void setResponseOption(String responseOption) {
		this.responseOption = responseOption;
	}

}
