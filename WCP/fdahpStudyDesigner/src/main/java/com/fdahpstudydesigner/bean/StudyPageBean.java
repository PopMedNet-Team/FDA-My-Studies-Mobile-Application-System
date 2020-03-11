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
