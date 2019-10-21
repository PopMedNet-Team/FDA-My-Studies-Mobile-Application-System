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
 * The persistent class for the notification database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "notification")
public class NotificationBO implements Serializable {

	private static final long serialVersionUID = 3634540541782531200L;

	@Transient
	private String actionPage;

	@Column(name = "active_task_id")
	private Integer activeTaskId;

	@Column(name = "is_anchor_date", length = 1)
	private boolean anchorDate = false;

	@Transient
	private String checkNotificationSendingStatus;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "notification_action", length = 1)
	private boolean notificationAction;

	@Column(name = "notification_done", length = 1)
	private boolean notificationDone = true;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Integer notificationId;

	@Column(name = "notification_schedule_type")
	private String notificationScheduleType;

	@Column(name = "notification_sent", length = 1)
	private boolean notificationSent = false;

	@Column(name = "notification_status", length = 1)
	private boolean notificationStatus = false;

	@Column(name = "notification_subType")
	private String notificationSubType;

	@Column(name = "notification_text")
	private String notificationText;

	@Column(name = "notification_type")
	private String notificationType;

	@Column(name = "questionnarie_id")
	private Integer questionnarieId;

	@Column(name = "resource_id")
	private Integer resourceId;

	@Column(name = "schedule_date")
	private String scheduleDate;

	@Column(name = "schedule_time")
	private String scheduleTime;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "x_days")
	private Integer xDays;
	
	@Column(name = "app_id")
	private String appId;

	public String getActionPage() {
		return actionPage;
	}

	public Integer getActiveTaskId() {
		return activeTaskId;
	}

	public String getCheckNotificationSendingStatus() {
		return checkNotificationSendingStatus;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public Integer getNotificationId() {
		return notificationId;
	}

	public String getNotificationScheduleType() {
		return notificationScheduleType;
	}

	public String getNotificationSubType() {
		return notificationSubType;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public Integer getQuestionnarieId() {
		return questionnarieId;
	}

	public Integer getResourceId() {
		return resourceId;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public Integer getxDays() {
		return xDays;
	}

	public boolean isAnchorDate() {
		return anchorDate;
	}

	public boolean isNotificationAction() {
		return notificationAction;
	}

	public boolean isNotificationDone() {
		return notificationDone;
	}

	public boolean isNotificationSent() {
		return notificationSent;
	}

	public boolean isNotificationStatus() {
		return notificationStatus;
	}

	public void setActionPage(String actionPage) {
		this.actionPage = actionPage;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public void setAnchorDate(boolean anchorDate) {
		this.anchorDate = anchorDate;
	}

	public void setCheckNotificationSendingStatus(
			String checkNotificationSendingStatus) {
		this.checkNotificationSendingStatus = checkNotificationSendingStatus;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setNotificationAction(boolean notificationAction) {
		this.notificationAction = notificationAction;
	}

	public void setNotificationDone(boolean notificationDone) {
		this.notificationDone = notificationDone;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	public void setNotificationScheduleType(String notificationScheduleType) {
		this.notificationScheduleType = notificationScheduleType;
	}

	public void setNotificationSent(boolean notificationSent) {
		this.notificationSent = notificationSent;
	}

	public void setNotificationStatus(boolean notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public void setNotificationSubType(String notificationSubType) {
		this.notificationSubType = notificationSubType;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public void setQuestionnarieId(Integer questionnarieId) {
		this.questionnarieId = questionnarieId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setxDays(Integer xDays) {
		this.xDays = xDays;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
}
