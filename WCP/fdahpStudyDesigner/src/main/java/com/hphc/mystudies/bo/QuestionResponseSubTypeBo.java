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

import org.springframework.web.multipart.MultipartFile;

/**
 * The persistent class for the response_sub_type_value database table.
 * 
 * @author BTC
 */
@Entity
@Table(name = "response_sub_type_value")
@NamedQueries({ @NamedQuery(name = "getQuestionSubResponse", query = "from QuestionResponseSubTypeBo QRBO where QRBO.responseTypeId=:responseTypeId and QRBO.active=1"), })
public class QuestionResponseSubTypeBo implements Serializable {

	private static final long serialVersionUID = -7853082585280415082L;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "description")
	private String description;

	@Column(name = "destination_step_id")
	private Integer destinationStepId;

	@Column(name = "detail")
	private String detail;

	@Column(name = "exclusive")
	private String exclusive;

	@Column(name = "image")
	private String image;

	@Transient
	private MultipartFile imageFile;

	@Transient
	private Integer imageId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "response_sub_type_value_id")
	private Integer responseSubTypeValueId;

	@Column(name = "response_type_id")
	private Integer responseTypeId;

	@Column(name = "selected_image")
	private String selectedImage;

	@Transient
	private MultipartFile selectImageFile;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "text")
	private String text;

	@Column(name = "value")
	private String value;

	public Boolean getActive() {
		return active;
	}

	public String getDescription() {
		return description;
	}

	public Integer getDestinationStepId() {
		return destinationStepId;
	}

	public String getDetail() {
		return detail;
	}

	public String getExclusive() {
		return exclusive;
	}

	public String getImage() {
		return image;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public Integer getImageId() {
		return imageId;
	}

	public Integer getResponseSubTypeValueId() {
		return responseSubTypeValueId;
	}

	public Integer getResponseTypeId() {
		return responseTypeId;
	}

	public String getSelectedImage() {
		return selectedImage;
	}

	public MultipartFile getSelectImageFile() {
		return selectImageFile;
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDestinationStepId(Integer destinationStepId) {
		this.destinationStepId = destinationStepId;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public void setExclusive(String exclusive) {
		this.exclusive = exclusive;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setResponseSubTypeValueId(Integer responseSubTypeValueId) {
		this.responseSubTypeValueId = responseSubTypeValueId;
	}

	public void setResponseTypeId(Integer responseTypeId) {
		this.responseTypeId = responseTypeId;
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}

	public void setSelectImageFile(MultipartFile selectImageFile) {
		this.selectImageFile = selectImageFile;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
