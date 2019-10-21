package com.fdahpstudydesigner.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.service.LoginService;
import com.fdahpstudydesigner.service.StudyService;
import com.fdahpstudydesigner.service.UsersService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * 
 * @author BTC
 *
 */
@Controller
public class UsersController {

	private static Logger logger = Logger.getLogger(UsersController.class
			.getName());

	@Autowired
	private LoginService loginService;

	@Autowired
	private StudyService studyService;

	@Autowired
	private UsersService usersService;

	/**
	 * This method is used to activate or deactivate the user
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param response
	 *            , {@link HttpServletResponse}
	 * @param userId
	 * @param userStatus
	 * @throws IOException
	 */
	@RequestMapping("/adminUsersEdit/activateOrDeactivateUser.do")
	public void activateOrDeactivateUser(HttpServletRequest request,
			HttpServletResponse response, String userId, String userStatus)
			throws IOException {
		logger.info("UsersController - activateOrDeactivateUser() - Starts");
		String msg = FdahpStudyDesignerConstants.FAILURE;
		JSONObject jsonobject = new JSONObject();
		PrintWriter out;
		try {
			HttpSession session = request.getSession();
			SessionObject userSession = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (null != userSession) {
				msg = usersService.activateOrDeactivateUser(
						Integer.valueOf(userId), Integer.valueOf(userStatus),
						userSession.getUserId(), userSession, request);
			}
		} catch (Exception e) {
			logger.error(
					"UsersController - activateOrDeactivateUser() - ERROR", e);
		}
		logger.info("UsersController - activateOrDeactivateUser() - Ends");
		jsonobject.put("message", msg);
		response.setContentType("application/json");
		out = response.getWriter();
		out.print(jsonobject);
	}

	/**
	 * This method is used to get data for add or edit the user
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminUsersEdit/addOrEditUserDetails.do")
	public ModelAndView addOrEditUserDetails(HttpServletRequest request) {
		logger.info("UsersController - addOrEditUserDetails() - Starts");
		ModelAndView mav = new ModelAndView();
		ModelMap map = new ModelMap();
		UserBO userBO = null;
		List<StudyListBean> studyBOs = null;
		List<RoleBO> roleBOList = null;
		List<StudyBo> studyBOList = null;
		String actionPage = "";
		List<Integer> permissions = null;
		int usrId = 0;
		try {
			if (FdahpStudyDesignerUtil.isSession(request)) {
				String userId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("userId")) ? "" : request
						.getParameter("userId");
				String checkRefreshFlag = FdahpStudyDesignerUtil
						.isEmpty(request.getParameter("checkRefreshFlag")) ? ""
						: request.getParameter("checkRefreshFlag");
				if (!"".equalsIgnoreCase(checkRefreshFlag)) {
					if (!"".equals(userId)) {
						usrId = Integer.valueOf(userId);
						actionPage = FdahpStudyDesignerConstants.EDIT_PAGE;
						userBO = usersService.getUserDetails(usrId);
						if (null != userBO) {
							studyBOs = studyService.getStudyListByUserId(userBO
									.getUserId());
							permissions = usersService
									.getPermissionsByUserId(userBO.getUserId());
						}
					} else {
						actionPage = FdahpStudyDesignerConstants.ADD_PAGE;
					}
					roleBOList = usersService.getUserRoleList();
					studyBOList = studyService.getAllStudyList();
					map.addAttribute("actionPage", actionPage);
					map.addAttribute("userBO", userBO);
					map.addAttribute("permissions", permissions);
					map.addAttribute("roleBOList", roleBOList);
					map.addAttribute("studyBOList", studyBOList);
					map.addAttribute("studyBOs", studyBOs);
					mav = new ModelAndView("addOrEditUserPage", map);
				} else {
					mav = new ModelAndView(
							"redirect:/adminUsersView/getUserList.do");
				}
			}
		} catch (Exception e) {
			logger.error("UsersController - addOrEditUserDetails() - ERROR", e);
		}
		logger.info("UsersController - addOrEditUserDetails() - Ends");
		return mav;
	}

	/**
	 * This method is used to add or update the user details
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param userBO
	 *            , {@link UserBO}
	 * @param result
	 *            , {@link BindingResult}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminUsersEdit/addOrUpdateUserDetails.do")
	public ModelAndView addOrUpdateUserDetails(HttpServletRequest request,
			UserBO userBO, BindingResult result) {
		logger.info("UsersController - addOrUpdateUserDetails() - Starts");
		ModelAndView mav = new ModelAndView();
		String msg = "";
		String permissions = "";
		int count = 1;
		List<Integer> permissionList = new ArrayList<>();
		boolean addFlag = false;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		try {
			HttpSession session = request.getSession();
			SessionObject userSession = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (null != userSession) {
				String manageUsers = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("manageUsers")) ? "" : request
						.getParameter("manageUsers");
				String manageNotifications = FdahpStudyDesignerUtil
						.isEmpty(request.getParameter("manageNotifications")) ? ""
						: request.getParameter("manageNotifications");
				String manageStudies = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("manageStudies")) ? "" : request
						.getParameter("manageStudies");
				String addingNewStudy = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("addingNewStudy")) ? "" : request
						.getParameter("addingNewStudy");
				String selectedStudies = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("selectedStudies")) ? "" : request
						.getParameter("selectedStudies");
				String permissionValues = FdahpStudyDesignerUtil
						.isEmpty(request.getParameter("permissionValues")) ? ""
						: request.getParameter("permissionValues");
				String ownUser = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("ownUser")) ? "" : request
						.getParameter("ownUser");
				if (null == userBO.getUserId()) {
					addFlag = true;
					userBO.setCreatedBy(userSession.getUserId());
					userBO.setCreatedOn(FdahpStudyDesignerUtil
							.getCurrentDateTime());
				} else {
					addFlag = false;
					userBO.setModifiedBy(userSession.getUserId());
					userBO.setModifiedOn(FdahpStudyDesignerUtil
							.getCurrentDateTime());
				}
				if (!"".equals(manageUsers)) {
					if ("0".equals(manageUsers)) {
						permissions += count > 1 ? ",'ROLE_MANAGE_USERS_VIEW'"
								: "'ROLE_MANAGE_USERS_VIEW'";
						count++;
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_USERS_VIEW);
					} else if ("1".equals(manageUsers)) {
						permissions += count > 1 ? ",'ROLE_MANAGE_USERS_VIEW'"
								: "'ROLE_MANAGE_USERS_VIEW'";
						count++;
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_USERS_VIEW);
						permissions += count > 1 ? ",'ROLE_MANAGE_USERS_EDIT'"
								: "'ROLE_MANAGE_USERS_EDIT'";
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_USERS_EDIT);
					}
				}
				if (!"".equals(manageNotifications)) {
					if ("0".equals(manageNotifications)) {
						permissions += count > 1 ? ",'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW'"
								: "'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW'";
						count++;
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW);
					} else if ("1".equals(manageNotifications)) {
						permissions += count > 1 ? ",'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW'"
								: "'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW'";
						count++;
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW);
						permissions += count > 1 ? ",'ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT'"
								: "'ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT'";
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT);
					}
				}
				if (!"".equals(manageStudies)) {
					if ("1".equals(manageStudies)) {
						permissions += count > 1 ? ",'ROLE_MANAGE_STUDIES'"
								: "'ROLE_MANAGE_STUDIES'";
						count++;
						permissionList
								.add(FdahpStudyDesignerConstants.ROLE_MANAGE_STUDIES);
						if (!"".equals(addingNewStudy)
								&& "1".equals(addingNewStudy)) {
							permissions += count > 1 ? ",'ROLE_CREATE_MANAGE_STUDIES'"
									: "'ROLE_CREATE_MANAGE_STUDIES'";
							permissionList
									.add(FdahpStudyDesignerConstants.ROLE_CREATE_MANAGE_STUDIES);
						}
					} else {
						selectedStudies = "";
						permissionValues = "";
					}
				} else {
					selectedStudies = "";
					permissionValues = "";
				}
				msg = usersService.addOrUpdateUserDetails(request, userBO,
						permissions, permissionList, selectedStudies,
						permissionValues, userSession);
				if (FdahpStudyDesignerConstants.SUCCESS.equals(msg)) {
					if (addFlag) {
						request.getSession().setAttribute(
								FdahpStudyDesignerConstants.SUC_MSG,
								propMap.get("add.user.success.message"));
					} else {
						request.getSession().setAttribute("ownUser", ownUser);
						request.getSession().setAttribute(
								FdahpStudyDesignerConstants.SUC_MSG,
								propMap.get("update.user.success.message"));
					}
				} else {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.ERR_MSG, msg);
				}
				mav = new ModelAndView(
						"redirect:/adminUsersView/getUserList.do");
			}
		} catch (Exception e) {
			logger.error("UsersController - addOrUpdateUserDetails() - ERROR",
					e);
		}
		logger.info("UsersController - addOrUpdateUserDetails() - Ends");
		return mav;
	}

	/**
	 * This method is used to enforce the user to change the password
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminUsersEdit/enforcePasswordChange.do")
	public ModelAndView enforcePasswordChange(HttpServletRequest request) {
		logger.info("UsersController - enforcePasswordChange() - Starts");
		ModelAndView mav = new ModelAndView();
		String msg = "";
		List<String> emails = null;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		try {
			HttpSession session = request.getSession();
			SessionObject userSession = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			String changePassworduserId = FdahpStudyDesignerUtil
					.isEmpty(request.getParameter("changePassworduserId")) ? ""
					: request.getParameter("changePassworduserId");
			String emailId = FdahpStudyDesignerUtil.isEmpty(request
					.getParameter("emailId")) ? "" : request
					.getParameter("emailId");
			if (null != userSession) {
				if (StringUtils.isNotEmpty(emailId)
						&& StringUtils.isNotEmpty(changePassworduserId)) {
					msg = usersService.enforcePasswordChange(
							Integer.parseInt(changePassworduserId), emailId);
					if (StringUtils.isNotEmpty(msg)
							&& msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS))
						loginService.sendPasswordResetLinkToMail(request,
								emailId, "", "enforcePasswordChange");
				} else {
					msg = usersService.enforcePasswordChange(null, "");
					if (StringUtils.isNotEmpty(msg)
							&& msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						emails = usersService.getActiveUserEmailIds();
						if (emails != null && !emails.isEmpty()) {
							for (String email : emails) {
								loginService.sendPasswordResetLinkToMail(
										request, email, "",
										"enforcePasswordChange");
							}

						}
					}
				}
				if (StringUtils.isNotEmpty(msg)
						&& msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
					request.getSession()
							.setAttribute(
									FdahpStudyDesignerConstants.SUC_MSG,
									propMap.get("password.enforce.link.success.message"));
				} else {
					request.getSession().setAttribute(
							FdahpStudyDesignerConstants.ERR_MSG,
							propMap.get("password.enforce.failure.message"));
				}
				mav = new ModelAndView(
						"redirect:/adminUsersView/getUserList.do");
			}
		} catch (Exception e) {
			logger.error("UsersController - enforcePasswordChange() - ERROR", e);
		}
		logger.info("UsersController - enforcePasswordChange() - Ends");
		return mav;
	}

	/**
	 * This method is used to get the list of users
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminUsersView/getUserList.do")
	public ModelAndView getUserList(HttpServletRequest request) {
		logger.info("UsersController - getUserList() - Starts");
		ModelAndView mav = new ModelAndView();
		ModelMap map = new ModelMap();
		List<UserBO> userList = null;
		String sucMsg = "";
		String errMsg = "";
		String ownUser = "";
		List<RoleBO> roleList = null;
		try {
			if (FdahpStudyDesignerUtil.isSession(request)) {
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
				ownUser = (String) request.getSession().getAttribute("ownUser");
				userList = usersService.getUserList();
				roleList = usersService.getUserRoleList();
				map.addAttribute("roleList", roleList);
				map.addAttribute("userList", userList);
				map.addAttribute("ownUser", ownUser);
				mav = new ModelAndView("userListPage", map);
			}
		} catch (Exception e) {
			logger.error("UsersController - getUserList() - ERROR", e);
		}
		logger.info("UsersController - getUserList() - Ends");
		return mav;
	}

	/**
	 * This method is used to resend the activation link to the user
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminUsersEdit/resendActivateDetailsLink.do")
	public ModelAndView resendActivateDetailsLink(HttpServletRequest request) {
		logger.info("UsersController - resendActivateDetailsLink() - Starts");
		ModelAndView mav = new ModelAndView();
		String msg = "";
		UserBO userBo = null;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		try {
			HttpSession session = request.getSession();
			SessionObject userSession = (SessionObject) session
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (null != userSession) {
				String userId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("userId")) ? "" : request
						.getParameter("userId");
				if (StringUtils.isNotEmpty(userId)) {
					userBo = usersService.getUserDetails(Integer
							.parseInt(userId));
					if (userBo != null) {
						msg = loginService.sendPasswordResetLinkToMail(request,
								userBo.getUserEmail(), "", "USER");
					}
					if (msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
						request.getSession().setAttribute(
								FdahpStudyDesignerConstants.SUC_MSG,
								propMap.get("resent.link.success.message"));
					} else {
						request.getSession().setAttribute(
								FdahpStudyDesignerConstants.ERR_MSG, msg);
					}
				}
				mav = new ModelAndView(
						"redirect:/adminUsersView/getUserList.do");
			}
		} catch (Exception e) {
			logger.error(
					"UsersController - resendActivateDetailsLink() - ERROR", e);
		}
		logger.info("UsersController - resendActivateDetailsLink() - Ends");
		return mav;
	}

	/**
	 * This method is used to view the user details
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link ModelAndView}
	 */
	@RequestMapping("/adminUsersView/viewUserDetails.do")
	public ModelAndView viewUserDetails(HttpServletRequest request) {
		logger.info("UsersController - viewUserDetails() - Starts");
		ModelAndView mav = new ModelAndView();
		ModelMap map = new ModelMap();
		UserBO userBO = null;
		List<StudyListBean> studyBOs = null;
		List<RoleBO> roleBOList = null;
		List<StudyBo> studyBOList = null;
		String actionPage = FdahpStudyDesignerConstants.VIEW_PAGE;
		List<Integer> permissions = null;
		try {
			if (FdahpStudyDesignerUtil.isSession(request)) {
				String userId = FdahpStudyDesignerUtil.isEmpty(request
						.getParameter("userId")) ? "" : request
						.getParameter("userId");
				String checkViewRefreshFlag = FdahpStudyDesignerUtil
						.isEmpty(request.getParameter("checkViewRefreshFlag")) ? ""
						: request.getParameter("checkViewRefreshFlag");
				if (!"".equalsIgnoreCase(checkViewRefreshFlag)) {
					if (!"".equals(userId)) {
						userBO = usersService.getUserDetails(Integer
								.valueOf(userId));
						if (null != userBO) {
							studyBOs = studyService.getStudyListByUserId(userBO
									.getUserId());
							permissions = usersService
									.getPermissionsByUserId(userBO.getUserId());
						}
					}
					roleBOList = usersService.getUserRoleList();
					studyBOList = studyService.getAllStudyList();
					map.addAttribute("actionPage", actionPage);
					map.addAttribute("userBO", userBO);
					map.addAttribute("permissions", permissions);
					map.addAttribute("roleBOList", roleBOList);
					map.addAttribute("studyBOList", studyBOList);
					map.addAttribute("studyBOs", studyBOs);
					mav = new ModelAndView("addOrEditUserPage", map);
				} else {
					mav = new ModelAndView("redirect:getUserList.do");
				}
			}
		} catch (Exception e) {
			logger.error("UsersController - viewUserDetails() - ERROR", e);
		}
		logger.info("UsersController - viewUserDetails() - Ends");
		return mav;
	}
}
