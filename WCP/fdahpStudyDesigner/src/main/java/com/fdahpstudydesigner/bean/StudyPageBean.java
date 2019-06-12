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
package com.fdahpstudydesigner.bean;

import org.springframework.web.multipart.MultipartFile;

public class StudyPageBean {

	private String actionType = "";

	private String description[];

	private String imagePath[];

	private String mediaLink = "";

	private MultipartFile multipartFiles[];

	private String originalFileName[];

	private String pageId[];

	private String studyId;

	private String title[];

	private Integer userId;

	public String getActionType() {
		return actionType;
	}

	public String[] getDescription() {
		return description;
	}

	public String[] getImagePath() {
		return imagePath;
	}

	public String getMediaLink() {
		return mediaLink;
	}

	public MultipartFile[] getMultipartFiles() {
		return multipartFiles;
	}

	public String[] getOriginalFileName() {
		return originalFileName;
	}

	public String[] getPageId() {
		return pageId;
	}

	public String getStudyId() {
		return studyId;
	}

	public String[] getTitle() {
		return title;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setDescription(String[] description) {
		this.description = description;
	}

	public void setImagePath(String[] imagePath) {
		this.imagePath = imagePath;
	}

	public void setMediaLink(String mediaLink) {
		this.mediaLink = mediaLink;
	}

	public void setMultipartFiles(MultipartFile[] multipartFiles) {
		this.multipartFiles = multipartFiles;
	}

	public void setOriginalFileName(String[] originalFileName) {
		this.originalFileName = originalFileName;
	}

	public void setPageId(String[] pageId) {
		this.pageId = pageId;
	}

	public void setStudyId(String string) {
		this.studyId = string;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
