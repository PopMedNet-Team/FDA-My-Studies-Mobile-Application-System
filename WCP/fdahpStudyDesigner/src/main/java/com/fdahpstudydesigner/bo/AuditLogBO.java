package com.fdahpstudydesigner.bo;

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