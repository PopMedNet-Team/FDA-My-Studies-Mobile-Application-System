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
package com.hphc.mystudies.bean;

import java.util.Map;

public class QuestionnaireStepBean {

	private Integer destinationStep;
	private String destinationText;
	Map<Integer, QuestionnaireStepBean> fromMap;
	private String lineChart;
	private Integer questionInstructionId;
	private Integer responseType;
	private String responseTypeText;
	private Integer sequenceNo;
	private String statData;
	private Boolean status;
	private Integer stepId;
	private String stepType;

	private String title;

	private Boolean useAnchorDate;

	public Integer getDestinationStep() {
		return destinationStep;
	}

	public String getDestinationText() {
		return destinationText;
	}

	public Map<Integer, QuestionnaireStepBean> getFromMap() {
		return fromMap;
	}

	public String getLineChart() {
		return lineChart;
	}

	public Integer getQuestionInstructionId() {
		return questionInstructionId;
	}

	public Integer getResponseType() {
		return responseType;
	}

	public String getResponseTypeText() {
		return responseTypeText;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public String getStatData() {
		return statData;
	}

	public Boolean getStatus() {
		return status;
	}

	public Integer getStepId() {
		return stepId;
	}

	public String getStepType() {
		return stepType;
	}

	public String getTitle() {
		return title;
	}

	public Boolean getUseAnchorDate() {
		return useAnchorDate;
	}

	public void setDestinationStep(Integer destinationStep) {
		this.destinationStep = destinationStep;
	}

	public void setDestinationText(String destinationText) {
		this.destinationText = destinationText;
	}

	public void setFromMap(Map<Integer, QuestionnaireStepBean> fromMap) {
		this.fromMap = fromMap;
	}

	public void setLineChart(String lineChart) {
		this.lineChart = lineChart;
	}

	public void setQuestionInstructionId(Integer questionInstructionId) {
		this.questionInstructionId = questionInstructionId;
	}

	public void setResponseType(Integer responseType) {
		this.responseType = responseType;
	}

	public void setResponseTypeText(String responseTypeText) {
		this.responseTypeText = responseTypeText;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public void setStatData(String statData) {
		this.statData = statData;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUseAnchorDate(Boolean useAnchorDate) {
		this.useAnchorDate = useAnchorDate;
	}

}
