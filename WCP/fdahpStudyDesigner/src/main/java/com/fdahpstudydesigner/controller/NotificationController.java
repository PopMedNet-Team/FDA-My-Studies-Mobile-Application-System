package com.fdahpstudydesigner.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.service.NotificationService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

@Controller
public class NotificationController {

	private static Logger logger = Logger
			.getLogger(NotificationController.class);

	@Autowired
	private NotificationService notificationService;

	/**
	 * Soft delete of notification in the application
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminNotificationEdit/deleteNotification.do")
	public ModelAndView deleteNotification(HttpServletRequest request) {
		logger.info("NotificationController - deleteNotification - Starts");
		ModelAndView mav = new ModelAndView();
		String message = FdahpStudyDesignerConstants.FAILURE;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		try {
			HttpSession session = request.getSession();
			SessionObject sessionObject = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			String notificationId = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID);
			if (null != notificationId) {
				String notificationType = FdahpStudyDesignerConstants.GATEWAYLEVEL;
				// Param "notificationType" define the type of
				// notification(global/study) that has been requested for
				// delete.
				message = notificationService.deleteNotification(
						Integer.parseInt(notificationId), sessionObject,
						notificationType);
				if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.SUC_MSG,
							propMap.get("delete.notification.success.message"));
				} else {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.ERR_MSG,
							propMap.get("delete.notification.error.message"));
				}
				mav = new ModelAndView(
						"redirect:/adminNotificationView/viewNotificationList.do");
			}
		} catch (Exception e) {
			logger.error("NotificationController - deleteNotification - ERROR",
					e);

		}
		return mav;
	}

	/**
	 * Details to edit/resend/addOrCopy of a notification
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminNotificationEdit/getNotificationToEdit.do")
	public ModelAndView getNotificationToEdit(HttpServletRequest request) {
		logger.info("NotificationController - getNotificationToEdit - Starts");
		ModelMap map = new ModelMap();
		NotificationBO notificationBO = null;
		ModelAndView mav = new ModelAndView();
		List<NotificationHistoryBO> notificationHistoryNoDateTime = null;
		List<String> gatewayAppList = null;
		try {
			String notificationId = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID);
			String notificationText = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter("notificationText")) ? "" : request
					.getParameter("notificationText");
			String chkRefreshflag = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.CHKREFRESHFLAG)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.CHKREFRESHFLAG);
			String actionType = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE);
			gatewayAppList = notificationService.getGatwayAppList();
			map.addAttribute("gatewayAppList", gatewayAppList);
			if (!"".equals(chkRefreshflag)) {
				if (!"".equals(notificationId)) {
					// Fetching notification detail from notification table by
					// Id.
					notificationBO = notificationService
							.getNotification(Integer.parseInt(notificationId));
					// Fetching notification history of last sent detail from
					// notification table by Id.
					notificationHistoryNoDateTime = notificationService
							.getNotificationHistoryListNoDateTime(Integer
									.parseInt(notificationId));
					// Spring security reason we have different method for
					// edit/resend/addOrCopy of notification as these section
					// are editable.
					if ("edit".equals(actionType)) {
						notificationBO.setActionPage("edit");
					} else {
						if (notificationBO.isNotificationSent()) {
							notificationBO.setScheduleDate("");
							notificationBO.setScheduleTime("");
						}
						notificationBO.setActionPage("resend");
					}
				} else if (!"".equals(notificationText)
						&& "".equals(notificationId)) {
					notificationBO = new NotificationBO();
					notificationBO.setNotificationText(notificationText);
					notificationBO.setActionPage("addOrCopy");
				} else if ("".equals(notificationText)
						&& "".equals(notificationId)) {
					notificationBO = new NotificationBO();
					notificationBO.setActionPage("addOrCopy");
				}
				map.addAttribute("notificationHistoryNoDateTime",
						notificationHistoryNoDateTime);
				map.addAttribute("notificationBO", notificationBO);
				mav = new ModelAndView("createOrUpdateNotification", map);
			} else {
				mav = new ModelAndView(
						"redirect:/adminNotificationView/viewNotificationList.do");
			}
		} catch (Exception e) {
			logger.error(
					"NotificationController - getNotificationToEdit - ERROR", e);

		}
		logger.info("NotificationController - getNotificationToEdit - Ends");
		return mav;
	}

	/**
	 * Details to view of a notification
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminNotificationView/getNotificationToView.do")
	public ModelAndView getNotificationToView(HttpServletRequest request) {
		logger.info("NotificationController - getNotification - Starts");
		ModelMap map = new ModelMap();
		NotificationBO notificationBO = null;
		ModelAndView mav = new ModelAndView();
		List<NotificationHistoryBO> notificationHistoryNoDateTime = null;
		try {
			String notificationId = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID);
			String chkRefreshflag = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.CHKREFRESHFLAG)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.CHKREFRESHFLAG);
			String actionType = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE)) ? ""
					: request
							.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE);
			if (!"".equals(chkRefreshflag)) {
				if (!"".equals(notificationId)) {
					// Fetching notification detail from notification table by
					// Id
					notificationBO = notificationService
							.getNotification(Integer.parseInt(notificationId));
					// Fetching notification history of last sent detail from
					// notification table by Id
					notificationHistoryNoDateTime = notificationService
							.getNotificationHistoryListNoDateTime(Integer
									.parseInt(notificationId));
					// Spring security reason we have different method for view
					// section
					if ("view".equals(actionType)) {
						notificationBO.setActionPage("view");
					}
				}
				map.addAttribute("notificationBO", notificationBO);
				map.addAttribute("notificationHistoryNoDateTime",
						notificationHistoryNoDateTime);
				mav = new ModelAndView("createOrUpdateNotification", map);
			} else {
				mav = new ModelAndView("redirect:viewNotificationList.do");
			}
		} catch (Exception e) {
			logger.error("NotificationController - getNotification - ERROR", e);

		}
		logger.info("NotificationController - getNotification - Ends");
		return mav;
	}

	/**
	 * Save/Update notification
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param notificationBO
	 *            , {@link NotificationBO}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminNotificationEdit/saveOrUpdateNotification.do")
	public ModelAndView saveOrUpdateOrResendNotification(
			HttpServletRequest request, NotificationBO notificationBO) {
		logger.info("NotificationController - saveOrUpdateOrResendNotification - Starts");
		Integer notificationId = 0;
		ModelAndView mav = new ModelAndView();
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		try {
			HttpSession session = request.getSession();
			SessionObject sessionObject = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			String notificationType = FdahpStudyDesignerConstants.GATEWAYLEVEL;
			String currentDateTime = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter("currentDateTime")) ? "" : request
					.getParameter("currentDateTime");
			String buttonType = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter("buttonType")) ? "" : request
					.getParameter("buttonType");
			if (FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE
					.equals(currentDateTime)) {
				notificationBO.setScheduleDate(FdahpStudyDesignerUtil
						.isNotEmpty(notificationBO.getScheduleDate()) ? String
						.valueOf(FdahpStudyDesignerUtil.getFormattedDate(
								notificationBO.getScheduleDate(),
								FdahpStudyDesignerConstants.UI_SDF_DATE,
								FdahpStudyDesignerConstants.DB_SDF_DATE)) : "");
				notificationBO.setScheduleTime(FdahpStudyDesignerUtil
						.isNotEmpty(notificationBO.getScheduleTime()) ? String
						.valueOf(FdahpStudyDesignerUtil.getFormattedDate(
								notificationBO.getScheduleTime(),
								FdahpStudyDesignerConstants.SDF_TIME,
								FdahpStudyDesignerConstants.DB_SDF_TIME)) : "");
				notificationBO
						.setNotificationScheduleType(FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE);
			} else if (FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE
					.equals(currentDateTime)) {
				notificationBO.setScheduleDate(FdahpStudyDesignerUtil
						.getCurrentDate());
				notificationBO.setScheduleTime(FdahpStudyDesignerUtil
						.getCurrentTime());
				notificationBO
						.setNotificationScheduleType(FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
			} else {
				notificationBO.setScheduleDate("");
				notificationBO.setScheduleTime("");
				notificationBO.setNotificationScheduleType("0");
			}
			if (null == notificationBO.getNotificationId()) {
				notificationBO.setCreatedBy(sessionObject.getUserId());
				notificationBO.setCreatedOn(FdahpStudyDesignerUtil
						.getCurrentDateTime());
			} else {
				notificationBO.setModifiedBy(sessionObject.getUserId());
				notificationBO.setModifiedOn(FdahpStudyDesignerUtil
						.getCurrentDateTime());
			}
			notificationId = notificationService
					.saveOrUpdateOrResendNotification(notificationBO,
							notificationType, buttonType, sessionObject, "");
			if (!notificationId.equals(0)) {
				if (notificationBO.getNotificationId() == null
						&& "add".equalsIgnoreCase(buttonType)) {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.SUC_MSG,
							propMap.get("save.notification.success.message"));
				} else if (notificationBO.getNotificationId() != null
						&& "update".equalsIgnoreCase(buttonType)) {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.SUC_MSG,
							propMap.get("update.notification.success.message"));
				} else {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.SUC_MSG,
							propMap.get("resend.notification.success.message"));
				}
			} else {
				if (notificationBO.getNotificationId() == null
						&& "add".equalsIgnoreCase(buttonType)) {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.ERR_MSG,
							propMap.get("save.notification.error.message"));
				} else if (notificationBO.getNotificationId() != null
						&& "update".equalsIgnoreCase(buttonType)) {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.ERR_MSG,
							propMap.get("resend.notification.error.message"));
				} else {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.ERR_MSG,
							propMap.get("update.notification.error.message"));
				}
			}
			mav = new ModelAndView(
					"redirect:/adminNotificationView/viewNotificationList.do");
		} catch (Exception e) {
			logger.error(
					"NotificationController - saveOrUpdateOrResendNotification - ERROR",
					e);

		}
		logger.info("NotificationController - saveOrUpdateOrResendNotification - Ends");
		return mav;
	}

	/**
	 * Global notification list details
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminNotificationView/viewNotificationList.do")
	public ModelAndView viewNotificationList(HttpServletRequest request) {
		logger.info("NotificationController - viewNotificationList() - Starts");
		String sucMsg = "";
		String errMsg = "";
		ModelMap map = new ModelMap();
		List<NotificationBO> notificationList = null;
		ModelAndView mav = new ModelAndView("login", map);
		try {
			if (null != request.getSession().getAttribute(
					FdahpStudyDesignerConstants.SUC_MSG)) {
				sucMsg = (String) request.getSession().getAttribute(
						FdahpStudyDesignerConstants.SUC_MSG);
				map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
				request.getSession().removeAttribute(
						FdahpStudyDesignerConstants.SUC_MSG);
			}
			if (null != request.getSession().getAttribute(
					FdahpStudyDesignerConstants.ERR_MSG)) {
				errMsg = (String) request.getSession().getAttribute(
						FdahpStudyDesignerConstants.ERR_MSG);
				map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
				request.getSession().removeAttribute(
						FdahpStudyDesignerConstants.ERR_MSG);
			}
			/*
			 * Passing 0 in below param as notifications are independent from
			 * study and empty string to define it is as global notification
			 */
			notificationList = notificationService.getNotificationList(0, "");
			for (NotificationBO notification : notificationList) {
				if (!notification.isNotificationSent()
						&& notification
								.getNotificationScheduleType()
								.equals(FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE)) {
					notification.setCheckNotificationSendingStatus("Not sent");
				} else if (!notification.isNotificationSent()
						&& notification
								.getNotificationScheduleType()
								.equals(FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE)) {
					notification.setCheckNotificationSendingStatus("Sending");
				} else if (notification.isNotificationSent()) {
					notification.setCheckNotificationSendingStatus("Sent");
				}
			}
			map.addAttribute("notificationList", notificationList);
			mav = new ModelAndView("notificationListPage", map);
		} catch (Exception e) {
			logger.error(
					"NotificationController - viewNotificationList() - ERROR ",
					e);
		}
		logger.info("NotificationController - viewNotificationList() - ends");
		return mav;
	}

}
