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

/**
 * The persistent class for the study_permission database table.
 * 
 * @author Pradyumn
 *
 */

@Entity
@Table(name = "study_permission")
@NamedQueries({ @NamedQuery(name = "getStudyPermissionById", query = " from StudyPermissionBO where studyId=:studyId and userId=:userId"), })
public class StudyPermissionBO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "delFlag")
	private Integer delFlag;

	@Column(name = "project_lead")
	private Integer projectLead;

	@Column(name = "study_id")
	private Integer studyId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer studyPermissionId;

	@Transient
	private String userFullName;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "view_permission", length = 1)
	private boolean viewPermission;

	public Integer getDelFlag() {
		return delFlag;
	}

	public Integer getProjectLead() {
		return projectLead;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public Integer getStudyPermissionId() {
		return studyPermissionId;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public Integer getUserId() {
		return userId;
	}

	public boolean isViewPermission() {
		return viewPermission;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public void setProjectLead(Integer projectLead) {
		this.projectLead = projectLead;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setStudyPermissionId(Integer studyPermissionId) {
		this.studyPermissionId = studyPermissionId;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setViewPermission(boolean viewPermission) {
		this.viewPermission = viewPermission;
	}
}
