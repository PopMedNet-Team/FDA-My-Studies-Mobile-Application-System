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
