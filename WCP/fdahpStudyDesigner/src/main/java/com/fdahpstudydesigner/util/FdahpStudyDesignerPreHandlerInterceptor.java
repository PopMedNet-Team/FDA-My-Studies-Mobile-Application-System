package com.fdahpstudydesigner.util;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.service.UsersService;

/**
 * @author
 *
 */
public class FdahpStudyDesignerPreHandlerInterceptor extends
		HandlerInterceptorAdapter {

	private static final Logger logger = Logger
			.getLogger(FdahpStudyDesignerPreHandlerInterceptor.class);

	@Autowired
	private UsersService usersService;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		logger.info("FdahpStudyDesignerPreHandlerInterceptor - preHandle() - Starts");
		SessionObject session = null;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		String defaultURL = propMap.get("action.default.redirect.url");
		final String excludeActions = propMap.get("interceptor.urls");
		String uri = request.getRequestURI();
		boolean flag = false;
		String passwordExpiredDateTime = null;
		int passwordExpirationInDay = Integer.parseInt(propMap
				.get("password.expiration.in.day"));
		String forceChangePasswordurl = propMap
				.get("action.force.changepassword.url");
		String updatePassword = propMap.get("action.force.updatepassword.url");
		String sessionOutUrl = propMap.get("action.logout.url");
		propMap.get("user.inactive.msg");
		String actionLoginbackUrl = propMap.get("action.loginback.url");
		String timeoutMsg = propMap.get("user.session.timeout");
		try {
			if (null != request.getSession()) {
				session = (SessionObject) request.getSession().getAttribute(
						FdahpStudyDesignerConstants.SESSION_OBJECT);
			}
			// Allow some of the URL
			String list[] = excludeActions.split(",");
			for (int i = 0; i < list.length; i++) {
				if (uri.endsWith(list[i].trim())) {
					flag = true;
				}
			}

			int customSessionExpiredErrorCode = 901;
			boolean ajax = "XMLHttpRequest".equals(request
					.getHeader("X-Requested-With"));
			if (null == session && request.getParameter("error") != null
					&& request.getParameter("error").equals("timeout") && ajax) {
				response.sendError(customSessionExpiredErrorCode);
				logger.info("FdahpStudyDesignerPreHandlerInterceptor - Ajax preHandle(): "
						+ uri + "");
				return false;
			}
			if (!flag) {
				if (null == session) {
					if (uri.contains(actionLoginbackUrl)) {
						request.getSession(false).setAttribute("loginBackUrl",
								request.getScheme() + "://" + // "http" + "://
										request.getServerName() + // "myhost"
										":" + // ":"
										request.getServerPort() + // "8080"
										request.getRequestURI() + // "/people"
										"?" + // "?"
										request.getQueryString());
					}
					response.sendRedirect(defaultURL);
					logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): "
							+ uri);
					return false;
				} else if (!ajax && !uri.contains(sessionOutUrl)) {
					// Checking for password Expired Date Time from current
					// Session
					passwordExpiredDateTime = session
							.getPasswordExpairdedDateTime();
					if (StringUtils.isNotBlank(passwordExpiredDateTime)
							&& FdahpStudyDesignerUtil
									.addDaysToDate(
											new SimpleDateFormat(
													FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
													.parse(passwordExpiredDateTime),
											passwordExpirationInDay)
									.before(new SimpleDateFormat(
											FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
											.parse(FdahpStudyDesignerUtil
													.getCurrentDateTime()))
							&& !uri.contains(forceChangePasswordurl)
							&& !uri.contains(updatePassword)) {
						response.sendRedirect(forceChangePasswordurl);
						logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): force change password");
					}
					// Checking for force logout for current user
					UserBO user = usersService.getUserDetails(session
							.getUserId());
					if (null != user) {
						if (user.isForceLogout()) {
							response.sendRedirect(sessionOutUrl + "?msg="
									+ timeoutMsg);
							logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): force logout");
							return false;
						} else if (user.getEmailChanged()) {
							response.sendRedirect(sessionOutUrl + "?msg="
									+ propMap.get("email.not.varified.error"));
							logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): email change");
							return false;
						}
					}
				}
			} else if (uri.contains(defaultURL) && null != session) {
				response.sendRedirect(session.getCurrentHomeUrl());
			}
		} catch (Exception e) {
			logger.error(
					"FdahpStudyDesignerPreHandlerInterceptor - preHandle()", e);
		}
		logger.info("FdahpStudyDesignerPreHandlerInterceptor - End Point: preHandle() - "
				+ " : "
				+ FdahpStudyDesignerUtil.getCurrentDateTime()
				+ " uri"
				+ uri);
		return true;
	}

}