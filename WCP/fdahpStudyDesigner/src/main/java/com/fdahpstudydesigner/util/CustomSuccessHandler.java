package com.fdahpstudydesigner.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.AuditLogDAO;
import com.fdahpstudydesigner.dao.LoginDAOImpl;
import com.fdahpstudydesigner.service.DashBoardAndProfileService;

/**
 * @author BTC
 * @see {@link SimpleUrlAuthenticationSuccessHandler}
 */
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Autowired
	private AuditLogDAO auditLogDAO;

	@Autowired
	private DashBoardAndProfileService dashBoardAndProfileService;

	private LoginDAOImpl loginDAO;

	/**
	 * Provide landing page URI as per User Role
	 *
	 * @author BTC
	 *
	 * @param authentication
	 *            , {@link Authentication}
	 * @return {@link String} , the URI
	 */
	protected String determineTargetUrl(Authentication authentication) {
		logger.info("CustomSuccessHandler - determineTargetUrl - Starts");
		String url = "";
		try {

			if (authentication != null) {
				url = "/adminDashboard/viewDashBoard.do?action=landing";
			} else {
				url = "/unauthorized.do";
			}
		} catch (Exception e) {
			logger.error("CustomSuccessHandler - determineTargetUrl - ERROR", e);
		}
		logger.info("CustomSuccessHandler - determineTargetUrl - Ends");
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * AbstractAuthenticationTargetUrlRequestHandler
	 * #handle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException {

		String targetUrl = determineTargetUrl(authentication);
		logger.info("targetUrl:" + targetUrl);
		UserBO userdetails;
		SessionObject sesObj;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		String projectName = propMap.get("project.name");
		String activity;
		String activityDetail;
		MasterDataBO masterDataBO;
		userdetails = loginDAO.getValidUserByEmail(authentication.getName());
		if (userdetails.isForceLogout()) {
			userdetails.setForceLogout(false);
			loginDAO.updateUser(userdetails);
		}
		sesObj = new SessionObject();
		sesObj.setUserId(userdetails.getUserId());
		sesObj.setFirstName(userdetails.getFirstName());
		sesObj.setLastName(userdetails.getLastName());
		sesObj.setLoginStatus(true);
		sesObj.setCurrentHomeUrl("/" + projectName + targetUrl);
		sesObj.setEmail(userdetails.getUserEmail());
		sesObj.setUserPermissions(FdahpStudyDesignerUtil.getSessionUserRole());
		sesObj.setPasswordExpairdedDateTime(userdetails
				.getPasswordExpairdedDateTime());
		sesObj.setCreatedDate(userdetails.getCreatedOn());
		sesObj.setRole(userdetails.getRoleName());

//		masterDataBO = dashBoardAndProfileService.getMasterData("terms");
//		sesObj.setTermsText(masterDataBO.getTermsText());
//		sesObj.setPrivacyPolicyText(masterDataBO.getPrivacyPolicyText());

		if (response.isCommitted()) {
			System.out.println("Can't redirect");
			return;
		}

		request.getSession().setAttribute(
				FdahpStudyDesignerConstants.SESSION_OBJECT, sesObj);
		activity = "User login.";
		activityDetail = "User successfully signed in. (Account Details:- First Name = "
				+ userdetails.getFirstName()
				+ ", Last Name = "
				+ userdetails.getLastName()
				+ ", Email ="
				+ userdetails.getUserEmail() + ").";
		auditLogDAO.saveToAuditLog(null, null, sesObj, activity,
				activityDetail, "CustomSuccessHandler - handle");

		if (null != request.getSession(false).getAttribute("sucMsg")) {
			request.getSession(false).removeAttribute("sucMsg");
		}
		if (null != request.getSession(false).getAttribute("errMsg")) {
			request.getSession(false).removeAttribute("errMsg");
		}
		logger.info("loginBackUrl:" + request.getParameter("loginBackUrl"));
		if (StringUtils.isNotBlank(request.getParameter("loginBackUrl"))) {
			String[] uri = request.getParameter("loginBackUrl").split(
					projectName);
			targetUrl = uri[1];
		}
		logger.info("targetUrl:" + targetUrl);
		// redirectStrategy.sendRedirect(request, response, targetUrl);
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		String message = FdahpStudyDesignerConstants.SUCCESS;
		jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
		response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
		out = response.getWriter();
		out.print(jsonobject);
	}

	@Autowired
	public void setLoginDAO(LoginDAOImpl loginDAO) {
		this.loginDAO = loginDAO;
	}
}
