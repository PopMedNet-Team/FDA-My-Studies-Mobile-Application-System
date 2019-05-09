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
package com.hphc.mystudies.util;

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

import com.hphc.mystudies.bo.MasterDataBO;
import com.hphc.mystudies.bo.UserBO;
import com.hphc.mystudies.dao.AuditLogDAO;
import com.hphc.mystudies.dao.LoginDAOImpl;
import com.hphc.mystudies.service.DashBoardAndProfileService;

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

		masterDataBO = dashBoardAndProfileService.getMasterData("terms");
		sesObj.setTermsText(masterDataBO.getTermsText());
		sesObj.setPrivacyPolicyText(masterDataBO.getPrivacyPolicyText());

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
