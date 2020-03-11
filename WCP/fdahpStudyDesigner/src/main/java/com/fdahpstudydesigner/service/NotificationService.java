package com.fdahpstudydesigner.service;

import java.util.List;

import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.util.SessionObject;

public interface NotificationService {

	public String deleteNotification(int notificationIdForDelete,
			SessionObject sessionObject, String notificationType);

	public NotificationBO getNotification(int notificationId);

	public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(
			int notificationId);

	public List<NotificationBO> getNotificationList(int studyId, String type);

	public Integer saveOrUpdateOrResendNotification(
			NotificationBO notificationBO, String notificationType,
			String buttonType, SessionObject sessionObject, String customStudyId);
	
	
	public List<String> getGatwayAppList();

}
