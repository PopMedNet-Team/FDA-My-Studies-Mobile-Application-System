package com.fdahpstudydesigner.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.service.DashBoardAndProfileService;
import com.fdahpstudydesigner.service.LoginService;
import com.fdahpstudydesigner.service.StudyService;
import com.fdahpstudydesigner.service.UsersService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * @author BTC
 *
 */
@Controller
public class DashBoardAndProfileController {
	private static Logger logger = Logger
			.getLogger(DashBoardAndProfileController.class.getName());

	@Autowired
	private DashBoardAndProfileService dashBoardAndProfileService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private StudyService studyService;

	@Autowired
	private UsersService usersService;

	/* DashBoard Start */

	/**
	 * Updating user password from my account page
	 * 
	 * @author BTC
	 *
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param response
	 *            , {@link HttpServletResponse}
	 */
	@RequestMapping("/adminDashboard/changePassword.do")
	public void changePassword(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("DashBoardAndProfileController - changePassword() - Starts");
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		String message = "";
		int userId = 0;
		try {
			HttpSession session = request.getSession();
			SessionObject sessionObject = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (null != sessionObject) {
				userId = sessionObject.getUserId();

				String newPassword = StringUtils.isNotEmpty(request
						.getParameter("newPassword")) ? request
						.getParameter("newPassword") : "";
				String oldPassword = StringUtils.isNotEmpty(request
						.getParameter("oldPassword")) ? request
						.getParameter("oldPassword") : "";
				message = loginService.changePassword(userId, newPassword,
						oldPassword, sessionObject);
				jsonobject.put("message", message);
				response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
				out = response.getWriter();
				out.print(jsonobject);
			}
		} catch (Exception e) {
			response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
			logger.error(
					"DashBoardAndProfileController - changePassword() - ERROR ",
					e);
		}
		logger.info("DashBoardAndProfileController - changePassword() - Ends");
	}

	/**
	 * Navigate to FDA admin dash board page
	 *
	 * @author BTC
	 *
	 * @return {@link ModelAndView} , dashBoardPage page
	 */
	@RequestMapping("/adminDashboard/viewDashBoard.do")
	public ModelAndView getAdminDashboard() {
		logger.info("DashBoardAndProfileController - getAdminDashboard - Starts");
		ModelAndView mav = new ModelAndView();
		try {
			mav = new ModelAndView("fdaAdminDashBoardPage");
		} catch (Exception e) {
			logger.error(
					"DashBoardAndProfileController - getAdminDashboard - ERROR",
					e);
		}
		logger.info("DashBoardAndProfileController - getAdminDashboard - Ends");
		return mav;
	}

	/**
	 * Checking for existing email Ids existing in the DB while creating new
	 * users for the application
	 * 
	 * @author BTC
	 *
	 * @param response
	 *            , {@link HttpServletResponse}
	 * @param email
	 *            ,Email Id for registering into application
	 */
	@RequestMapping("/isEmailValid.do")
	public void isEmailValid(HttpServletResponse response, String email) {
		logger.info("DashBoardAndProfileController - isEmailValid() - Starts ");
		JSONObject jsonobject = new JSONObject();
		PrintWriter out = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		try {
			if (FdahpStudyDesignerUtil.isNotEmpty(email)) {
				message = dashBoardAndProfileService.isEmailValid(email);
			}
			jsonobject.put("message", message);
			response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
			out = response.getWriter();
			out.print(jsonobject);
		} catch (Exception e) {
			response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
			logger.error("DashBoardAndProfileController - isEmailValid() - ERROR "
					+ e);
		}
		logger.info("DashBoardAndProfileController - isEmailValid() - Ends ");
	}

	/**
	 * Updating User Details
	 * 
	 * @author BTC
	 *
	 *
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param userBO
	 *            , {@link UserBO}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminDashboard/updateUserDetails.do")
	public ModelAndView updateProfileDetails(HttpServletRequest request,
			UserBO userBO) {
		logger.info("DashBoardAndProfileController - Entry Point: updateProfileDetails()");
		ModelAndView mav = new ModelAndView();
		Integer userId = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		try {
			HttpSession session = request.getSession();
			SessionObject userSession = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			userBO.setModifiedBy(userSession.getUserId());
			userBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
			userId = userSession.getUserId();
			message = dashBoardAndProfileService.updateProfileDetails(userBO,
					userId, userSession);
			if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
				userSession.setFirstName(FdahpStudyDesignerUtil.isEmpty(userBO
						.getFirstName()) ? userSession.getFirstName() : userBO
						.getFirstName());
				userSession.setLastName(FdahpStudyDesignerUtil.isEmpty(userBO
						.getLastName()) ? userSession.getLastName() : userBO
						.getLastName());
				userSession.setEmail(FdahpStudyDesignerUtil.isEmpty(userBO
						.getUserEmail()) ? userSession.getEmail() : userBO
						.getUserEmail());
				request.setAttribute(
						FdahpStudyDesignerConstants.SESSION_OBJECT, userSession);
			}
			if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
				request.getSession().setAttribute(
						FdahpStudyDesignerConstants.SUC_MSG,
						propMap.get("update.profile.success.message"));
			} else {
				request.getSession().setAttribute(
						FdahpStudyDesignerConstants.ERR_MSG,
						propMap.get("update.profile.error.message"));
			}
			mav = new ModelAndView(
					"redirect:/adminDashboard/viewUserDetails.do");
		} catch (Exception e) {
			logger.error(
					"DashBoardAndProfileController:  updateProfileDetails()' = ",
					e);
		}
		logger.info("DashBoardAndProfileController - Exit Point: updateProfileDetails()");
		return mav;
	}

	/**
	 * Method to view session user Details and respective permission allotted to
	 * them
	 * 
	 * @author BTC
	 *
	 *
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminDashboard/viewUserDetails.do")
	public ModelAndView viewUserDetails(HttpServletRequest request) {
		logger.info("DashBoardAndProfileController - viewUserDetails - Starts");
		ModelMap map = new ModelMap();
		ModelAndView mav = new ModelAndView();
		UserBO userBO = null;
		List<StudyListBean> studyAndPermissionList = null;
		RoleBO roleBO = null;
		String sucMsg = "";
		String errMsg = "";
		Integer userId = 0;
		String accountManager = "";
		try {
			HttpSession session = request.getSession();
			SessionObject userSession = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (userSession != null) {
				if (null != request.getSession().getAttribute(
						FdahpStudyDesignerConstants.SUC_MSG)) {
					sucMsg = (String) request.getSession().getAttribute(
							FdahpStudyDesignerConstants.SUC_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG,
							sucMsg);
					request.getSession().removeAttribute(
							FdahpStudyDesignerConstants.SUC_MSG);
				}
				if (null != request.getSession().getAttribute(
						FdahpStudyDesignerConstants.ERR_MSG)) {
					errMsg = (String) request.getSession().getAttribute(
							FdahpStudyDesignerConstants.ERR_MSG);
					map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG,
							errMsg);
					request.getSession().removeAttribute(
							FdahpStudyDesignerConstants.ERR_MSG);
				}
				if (userSession.getUserId() != null) {
					userBO = usersService.getUserDetails(userSession
							.getUserId());
					userId = usersService.getUserPermissionByUserId(userSession
							.getUserId());
					if (userId != null
							&& userId.equals(userSession.getUserId())) {
						accountManager = "Yes";
					} else {
						accountManager = "No";
					}
					if (null != userBO) {
						studyAndPermissionList = studyService
								.getStudyListByUserId(userBO.getUserId());
						roleBO = usersService.getUserRole(userBO.getRoleId());
						if (null != roleBO) {
							userBO.setRoleName(roleBO.getRoleName());
						}
					}
				}
				map.addAttribute("studyAndPermissionList",
						studyAndPermissionList);
				map.addAttribute("userBO", userBO);
				map.addAttribute("accountManager", accountManager);
				mav = new ModelAndView("myAccount", map);
			}
		} catch (Exception e) {
			logger.error(
					"DashBoardAndProfileController - viewUserDetails - ERROR",
					e);
		}
		logger.info("DashBoardAndProfileController - viewUserDetails - Ends");
		return mav;
	}

}
