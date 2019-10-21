package com.fdahpstudydesigner.bean;

public class StudyListBean {

	private String category;
	private String createdFirstName;
	private String createdLastName;
	private String createdOn;
	private String customStudyId;
	private boolean flag = false;
	private Integer id;
	private Integer liveStudyId;
	private String name;
	private String projectLeadName;
	private String researchSponsor;
	private String status;
	private boolean viewPermission;

	public StudyListBean(Integer id, String customStudyId, String name,
			boolean viewPermission) {
		super();
		this.id = id;
		this.customStudyId = customStudyId;
		this.name = name;
		this.viewPermission = viewPermission;
	}

	public StudyListBean(Integer id, String customStudyId, String name,
			String category, String researchSponsor, String createdFirstName,
			String createdLastName, boolean viewPermission, String status,
			String createdOn) {
		super();
		this.id = id;
		this.customStudyId = customStudyId;
		this.name = name;
		this.category = category;
		this.researchSponsor = researchSponsor;
		this.createdFirstName = createdFirstName;
		this.createdLastName = createdLastName;
		this.viewPermission = viewPermission;
		this.status = status;
		this.createdOn = createdOn;
	}

	public String getCategory() {
		return category;
	}

	public String getCreatedFirstName() {
		return createdFirstName;
	}

	public String getCreatedLastName() {
		return createdLastName;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getLiveStudyId() {
		return liveStudyId;
	}

	public String getName() {
		return name;
	}

	public String getProjectLeadName() {
		return projectLeadName;
	}

	public String getResearchSponsor() {
		return researchSponsor;
	}

	public String getStatus() {
		return status;
	}

	public boolean isFlag() {
		return flag;
	}

	public boolean isViewPermission() {
		return viewPermission;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCreatedFirstName(String createdFirstName) {
		this.createdFirstName = createdFirstName;
	}

	public void setCreatedLastName(String createdLastName) {
		this.createdLastName = createdLastName;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLiveStudyId(Integer liveStudyId) {
		this.liveStudyId = liveStudyId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProjectLeadName(String projectLeadName) {
		this.projectLeadName = projectLeadName;
	}

	public void setResearchSponsor(String researchSponsor) {
		this.researchSponsor = researchSponsor;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setViewPermission(boolean viewPermission) {
		this.viewPermission = viewPermission;
	}

}
