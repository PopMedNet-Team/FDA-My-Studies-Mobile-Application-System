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
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the audit_log database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "audit_log")
public class AuditLogBO implements Serializable {

	private static final long serialVersionUID = -1122573644412620653L;

	@Column(name = "activity")
	private String activity;

	@Column(name = "activity_details")
	private String activityDetails;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audit_log_id")
	private Integer auditLogId;

	@Column(name = "class_method_name")
	private String classMethodName;

	@Column(name = "created_date_time")
	private String createdDateTime;

	@Transient
	private UserBO userBO;

	@Column(name = "user_id")
	private Integer userId;

	public String getActivity() {
		return activity;
	}

	public String getActivityDetails() {
		return activityDetails;
	}

	public Integer getAuditLogId() {
		return auditLogId;
	}

	public String getClassMethodName() {
		return classMethodName;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public UserBO getUserBO() {
		return userBO;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setActivityDetails(String activityDetails) {
		this.activityDetails = activityDetails;
	}

	public void setAuditLogId(Integer auditLogId) {
		this.auditLogId = auditLogId;
	}

	public void setClassMethodName(String classMethodName) {
		this.classMethodName = classMethodName;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setUserBO(UserBO userBO) {
		this.userBO = userBO;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}