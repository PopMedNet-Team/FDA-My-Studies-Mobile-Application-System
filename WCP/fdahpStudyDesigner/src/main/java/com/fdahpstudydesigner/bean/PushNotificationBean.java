package com.fdahpstudydesigner.bean;

/**
 * @author BTC
 *
 */
public class PushNotificationBean {

	private String customStudyId = "";

	private Integer notificationId = 0;

	private String notificationSubType = "Announcement";

	private String notificationText = "";

	private String notificationTitle = "";

	private String notificationType = "ST";
	
	private String appId = "";

	// private String notificationTitle =
	// FdahpStudyDesignerUtil.getAppProperties().get("push.notification.title");

	/**
	 * @return the customStudyId
	 */
	public String getCustomStudyId() {
		return customStudyId;
	}

	public Integer getNotificationId() {
		return notificationId;
	}

	/**
	 * @return the notificationSubType
	 */
	public String getNotificationSubType() {
		return notificationSubType;
	}

	/**
	 * @return the notificationText
	 */
	public String getNotificationText() {
		return notificationText;
	}

	/**
	 * @return the notificationTitle
	 */
	public String getNotificationTitle() {
		return notificationTitle;
	}

	/**
	 * @return the notificationType
	 */
	public String getNotificationType() {
		return notificationType;
	}

	/**
	 * @param customStudyId
	 *            the customStudyId to set
	 */
	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	/**
	 * @param notificationSubType
	 *            the notificationSubType to set
	 */
	public void setNotificationSubType(String notificationSubType) {
		this.notificationSubType = notificationSubType;
	}

	/**
	 * @param notificationText
	 *            the notificationText to set
	 */
	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	/**
	 * @param notificationTitle
	 *            the notificationTitle to set
	 */
	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	/**
	 * @param notificationType
	 *            the notificationType to set
	 */
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String toString() {
		return "PushNotificationBean [customStudyId=" + customStudyId + ", notificationId=" + notificationId
				+ ", notificationSubType=" + notificationSubType + ", notificationText=" + notificationText
				+ ", notificationTitle=" + notificationTitle + ", notificationType=" + notificationType + ", appId="
				+ appId + "]";
	}
}
