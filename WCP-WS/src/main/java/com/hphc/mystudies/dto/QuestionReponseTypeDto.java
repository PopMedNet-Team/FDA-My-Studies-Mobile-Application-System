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

/**
 * Provides questionnaire {@link QuestionnairesDto} response type information.
 * <p>
 * Response types {@link QuestionResponsetypeMasterInfoDto} mentioned below:
 * <ol>
 * <li>Scale
 * <li>Continuous Scale
 * <li>Text Scale
 * <li>Value Picker
 * <li>Image Choice
 * <li>Text Choice
 * <li>Boolean
 * <li>Numeric
 * <li>Time of the day
 * <li>Date
 * <li>Text
 * <li>Email
 * <li>Time interval
 * <li>Height
 * <li>Location
 * <ol>
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "response_type_value")
public class QuestionReponseTypeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5612905113940249120L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "response_type_id")
	private Integer responseTypeId;

	@Column(name = "questions_response_type_id")
	private Integer questionsResponseTypeId;

	@Column(name = "max_value")
	private String maxValue;

	@Column(name = "min_value")
	private String minValue;

	@Column(name = "default_value")
	private String defaultValue;

	@Column(name = "step")
	private Integer step;

	@Column(name = "vertical")
	private Boolean vertical;

	@Column(name = "max_desc")
	private String maxDescription;

	@Column(name = "min_desc")
	private String minDescription;

	@Column(name = "min_image")
	private String minImage;

	@Column(name = "max_image")
	private String maxImage;

	@Column(name = "max_fraction_digits")
	private Integer maxFractionDigits;

	@Column(name = "text_choices")
	private String textChoices;

	@Column(name = "selection_style")
	private String selectionStyle;

	@Column(name = "image_size")
	private String imageSize;

	@Column(name = "style")
	private String style;

	@Column(name = "unit")
	private String unit;

	@Column(name = "placeholder")
	private String placeholder;

	@Column(name = "min_date")
	private String minDate;

	@Column(name = "max_date")
	private String maxDate;

	@Column(name = "default_date")
	private String defaultDate;

	@Column(name = "max_length")
	private Integer maxLength;

	@Column(name = "validation_regex")
	private String validationRegex;

	@Column(name = "invalid_message")
	private String invalidMessage;

	@Column(name = "multiple_lines")
	private Boolean multipleLines;

	@Column(name = "measurement_system")
	private String measurementSystem;

	@Column(name = "use_current_location")
	private Boolean useCurrentLocation;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "defalut_time")
	private String defalutTime;

	@Column(name = "validation_characters")
	private String validationCharacters;

	@Column(name = "validation_condition")
	private String validationCondition;

	@Column(name = "validation_except_text")
	private String validationExceptText;

	@Column(name = "formula_based_logic")
	private String formulaBasedLogic;

	@Column(name = "condition_formula")
	private String conditionFormula;
	
	
	@Column(name = "other_type")
	private String otherType;
	
	@Column(name = "other_text")
	private String otherText;

	@Column(name = "other_value")
	private String otherValue;
	
	@Column(name = "other_exclusive")
	private String otherExclusive;
	
	@Column(name = "other_destination_step_id")
	private Integer otherDestinationStepId;
	
	@Column(name = "other_description")
	private String otherDescription;
	
	@Column(name = "other_include_text")
	private String otherIncludeText;
	
	@Column(name = "other_placeholder_text")
	private String otherPlaceholderText;
	
	@Column(name = "other_participant_fill")
	private String otherParticipantFill;

	public Integer getResponseTypeId() {
		return responseTypeId;
	}

	public void setResponseTypeId(Integer responseTypeId) {
		this.responseTypeId = responseTypeId;
	}

	public Integer getQuestionsResponseTypeId() {
		return questionsResponseTypeId;
	}

	public void setQuestionsResponseTypeId(Integer questionsResponseTypeId) {
		this.questionsResponseTypeId = questionsResponseTypeId;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public Boolean getVertical() {
		return vertical;
	}

	public void setVertical(Boolean vertical) {
		this.vertical = vertical;
	}

	public String getMaxDescription() {
		return maxDescription;
	}

	public void setMaxDescription(String maxDescription) {
		this.maxDescription = maxDescription;
	}

	public String getMinDescription() {
		return minDescription;
	}

	public void setMinDescription(String minDescription) {
		this.minDescription = minDescription;
	}

	public String getMinImage() {
		return minImage;
	}

	public void setMinImage(String minImage) {
		this.minImage = minImage;
	}

	public String getMaxImage() {
		return maxImage;
	}

	public void setMaxImage(String maxImage) {
		this.maxImage = maxImage;
	}

	public Integer getMaxFractionDigits() {
		return maxFractionDigits;
	}

	public void setMaxFractionDigits(Integer maxFractionDigits) {
		this.maxFractionDigits = maxFractionDigits;
	}

	public String getTextChoices() {
		return textChoices;
	}

	public void setTextChoices(String textChoices) {
		this.textChoices = textChoices;
	}

	public String getSelectionStyle() {
		return selectionStyle;
	}

	public void setSelectionStyle(String selectionStyle) {
		this.selectionStyle = selectionStyle;
	}

	public String getImageSize() {
		return imageSize;
	}

	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getMinDate() {
		return minDate;
	}

	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}

	public String getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	public String getDefaultDate() {
		return defaultDate;
	}

	public void setDefaultDate(String defaultDate) {
		this.defaultDate = defaultDate;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getValidationRegex() {
		return validationRegex;
	}

	public void setValidationRegex(String validationRegex) {
		this.validationRegex = validationRegex;
	}

	public String getInvalidMessage() {
		return invalidMessage;
	}

	public void setInvalidMessage(String invalidMessage) {
		this.invalidMessage = invalidMessage;
	}

	public Boolean getMultipleLines() {
		return multipleLines;
	}

	public void setMultipleLines(Boolean multipleLines) {
		this.multipleLines = multipleLines;
	}

	public String getMeasurementSystem() {
		return measurementSystem;
	}

	public void setMeasurementSystem(String measurementSystem) {
		this.measurementSystem = measurementSystem;
	}

	public Boolean getUseCurrentLocation() {
		return useCurrentLocation;
	}

	public void setUseCurrentLocation(Boolean useCurrentLocation) {
		this.useCurrentLocation = useCurrentLocation;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDefalutTime() {
		return defalutTime;
	}

	public void setDefalutTime(String defalutTime) {
		this.defalutTime = defalutTime;
	}

	public String getValidationCharacters() {
		return validationCharacters;
	}

	public void setValidationCharacters(String validationCharacters) {
		this.validationCharacters = validationCharacters;
	}

	public String getValidationCondition() {
		return validationCondition;
	}

	public void setValidationCondition(String validationCondition) {
		this.validationCondition = validationCondition;
	}

	public String getValidationExceptText() {
		return validationExceptText;
	}

	public void setValidationExceptText(String validationExceptText) {
		this.validationExceptText = validationExceptText;
	}

	public String getFormulaBasedLogic() {
		return formulaBasedLogic;
	}

	public void setFormulaBasedLogic(String formulaBasedLogic) {
		this.formulaBasedLogic = formulaBasedLogic;
	}

	public String getConditionFormula() {
		return conditionFormula;
	}

	public void setConditionFormula(String conditionFormula) {
		this.conditionFormula = conditionFormula;
	}

	public String getOtherType() {
		return otherType;
	}

	public void setOtherType(String otherType) {
		this.otherType = otherType;
	}

	public String getOtherText() {
		return otherText;
	}

	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}

	public String getOtherValue() {
		return otherValue;
	}

	public void setOtherValue(String otherValue) {
		this.otherValue = otherValue;
	}

	public String getOtherExclusive() {
		return otherExclusive;
	}

	public void setOtherExclusive(String otherExclusive) {
		this.otherExclusive = otherExclusive;
	}

	public Integer getOtherDestinationStepId() {
		return otherDestinationStepId;
	}

	public void setOtherDestinationStepId(Integer otherDestinationStepId) {
		this.otherDestinationStepId = otherDestinationStepId;
	}

	public String getOtherDescription() {
		return otherDescription;
	}

	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

	public String getOtherIncludeText() {
		return otherIncludeText;
	}

	public void setOtherIncludeText(String otherIncludeText) {
		this.otherIncludeText = otherIncludeText;
	}

	public String getOtherPlaceholderText() {
		return otherPlaceholderText;
	}

	public void setOtherPlaceholderText(String otherPlaceholderText) {
		this.otherPlaceholderText = otherPlaceholderText;
	}

	public String getOtherParticipantFill() {
		return otherParticipantFill;
	}

	public void setOtherParticipantFill(String otherParticipantFill) {
		this.otherParticipantFill = otherParticipantFill;
	}
}
