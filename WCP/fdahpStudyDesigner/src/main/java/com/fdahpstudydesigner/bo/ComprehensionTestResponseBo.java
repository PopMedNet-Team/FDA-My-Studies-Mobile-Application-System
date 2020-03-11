package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the comprehension_test_response database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "comprehension_test_response")
public class ComprehensionTestResponseBo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7739882770594873383L;

	@Column(name = "comprehension_test_question_id")
	private Integer comprehensionTestQuestionId;

	@Column(name = "correct_answer")
	private Boolean correctAnswer;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "response_option")
	private String responseOption;

	public Integer getComprehensionTestQuestionId() {
		return comprehensionTestQuestionId;
	}

	public Boolean getCorrectAnswer() {
		return correctAnswer;
	}

	public Integer getId() {
		return id;
	}

	public String getResponseOption() {
		return responseOption;
	}

	public void setComprehensionTestQuestionId(
			Integer comprehensionTestQuestionId) {
		this.comprehensionTestQuestionId = comprehensionTestQuestionId;
	}

	public void setCorrectAnswer(Boolean correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setResponseOption(String responseOption) {
		this.responseOption = responseOption;
	}

}
