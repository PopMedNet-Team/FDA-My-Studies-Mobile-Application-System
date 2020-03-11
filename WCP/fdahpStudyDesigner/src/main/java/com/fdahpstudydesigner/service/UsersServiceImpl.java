package com.fdahpstudydesigner.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.AuditLogDAO;
import com.fdahpstudydesigner.dao.UsersDAO;
import com.fdahpstudydesigner.util.EmailNotification;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * 
 * @author BTC
 *
 */
@Service
public class UsersServiceImpl implements UsersService {

	private static Logger logger = Logger.getLogger(UsersServiceImpl.class);

	@Autowired
	private AuditLogDAO auditLogDAO;

	@Autowired
	LoginService loginService;

	@Autowired
	private UsersDAO usersDAO;

	/**
	 * This method is used to activate or deactivate the user
	 *
	 * @author BTC
	 * @param userId
	 * @param userStatus
	 * @param loginUser
	 * @param userSession
	 *            , {@link SessionObject}
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return message, Success/Failure message
	 */
	@Override
	public String activateOrDeactivateUser(int userId, int userStatus,
			int loginUser, SessionObject userSession, HttpServletRequest request) {
		logger.info("UsersServiceImpl - activateOrDeactivateUser() - Starts");
		String msg = FdahpStudyDesignerConstants.FAILURE;
		List<String> superAdminEmailList = null;
		Map<String, String> keyValueForSubject = null;
		String dynamicContent = "";
		UserBO userBo = null;
		UserBO adminFullNameIfSizeOne = null;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		String customerCareMail = "";
		String status = "";

		try {
			msg = usersDAO.activateOrDeactivateUser(userId, userStatus,
					loginUser, userSession);
			superAdminEmailList = usersDAO.getSuperAdminList();
			userBo = usersDAO.getUserDetails(userId);
			if (msg.equals(FdahpStudyDesignerConstants.SUCCESS)) {
				keyValueForSubject = new HashMap<String, String>();
				if (superAdminEmailList != null
						&& !superAdminEmailList.isEmpty()) {
					if (userStatus == 1) {
						status = "Deactivated";
					} else {
						status = "Active";
					}
					if (superAdminEmailList.size() == 1) {
						for (String email : superAdminEmailList) {
							adminFullNameIfSizeOne = usersDAO
									.getSuperAdminNameByEmailId(email);
							keyValueForSubject.put(
									"$admin",
									adminFullNameIfSizeOne.getFirstName()
											+ " "
											+ adminFullNameIfSizeOne
													.getLastName());
						}
					} else {
						keyValueForSubject.put("$admin", "Admin");
					}
					keyValueForSubject.put("$userStatus", status);
					keyValueForSubject.put(
							"$sessionAdminFullName",
							userSession.getFirstName() + " "
									+ userSession.getLastName());
					keyValueForSubject.put("$userEmail", userBo.getUserEmail());
					dynamicContent = FdahpStudyDesignerUtil
							.genarateEmailContent(
									"mailForAdminUserUpdateContent",
									keyValueForSubject);
					EmailNotification.sendEmailNotification(
							"mailForAdminUserUpdateSubject", dynamicContent,
							null, superAdminEmailList, null);
				}
				if (userBo != null && Integer.valueOf(userStatus).equals(0)) {
					if (!userBo.isCredentialsNonExpired()) {
						loginService.sendPasswordResetLinkToMail(request,
								userBo.getUserEmail(), "",
								"ReactivateMailAfterEnforcePassChange");
					} else {
						customerCareMail = propMap
								.get("email.address.customer.service");
						keyValueForSubject.put("$userFirstName",
								userBo.getFirstName());
						keyValueForSubject.put("$customerCareMail",
								customerCareMail);
						dynamicContent = FdahpStudyDesignerUtil
								.genarateEmailContent(
										"mailForReactivatingUserContent",
										keyValueForSubject);
						EmailNotification.sendEmailNotification(
								"mailForReactivatingUserSubject",
								dynamicContent, userBo.getUserEmail(), null,
								null);
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"UsersServiceImpl - activateOrDeactivateUser() - ERROR", e);
		}
		logger.info("UsersServiceImpl - activateOrDeactivateUser() - Ends");
		return msg;
	}

	/**
	 * This method is used to add or update the user details
	 *
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param userBO
	 *            , {@link UserBO}
	 * @param permissions
	 * @param permissionList
	 * @param selectedStudies
	 * @param permissionValues
	 * @param userSession
	 *            , object of {@link SessionObject}
	 * @return {@link ModelAndView}
	 */
	@Override
	public String addOrUpdateUserDetails(HttpServletRequest request,
			UserBO userBO, String permissions, List<Integer> permissionList,
			String selectedStudies, String permissionValues,
			SessionObject userSession) {
		logger.info("UsersServiceImpl - addOrUpdateUserDetails() - Starts");
		UserBO userBO2 = null;
		String msg = FdahpStudyDesignerConstants.FAILURE;
		boolean addFlag = false;
		String activity = "";
		String activityDetail = "";
		boolean emailIdChange = false;
		List<String> superAdminEmailList = null;
		Map<String, String> keyValueForSubject = null;
		String dynamicContent = "";
		UserBO adminFullNameIfSizeOne = null;
		UserBO userBO3 = null;
		try {
			if (null == userBO.getUserId()) {
				addFlag = true;
				userBO2 = new UserBO();
				userBO2.setFirstName(null != userBO.getFirstName() ? userBO
						.getFirstName().trim() : "");
				userBO2.setLastName(null != userBO.getLastName() ? userBO
						.getLastName().trim() : "");
				userBO2.setUserEmail((null != userBO.getUserEmail() ? userBO
						.getUserEmail().trim() : "").toLowerCase());
				userBO2.setPhoneNumber(null != userBO.getPhoneNumber() ? userBO
						.getPhoneNumber().trim() : "");
				userBO2.setRoleId(userBO.getRoleId());
				userBO2.setCreatedBy(userBO.getCreatedBy());
				userBO2.setCreatedOn(userBO.getCreatedOn());
				userBO2.setEnabled(false);
				userBO2.setCredentialsNonExpired(true);
				userBO2.setAccountNonExpired(true);
				userBO2.setAccountNonLocked(true);
			} else {
				addFlag = false;
				userBO2 = usersDAO.getUserDetails(userBO.getUserId());
				userBO3 = usersDAO.getUserDetails(userBO.getUserId());
				userBO2.setFirstName(null != userBO.getFirstName() ? userBO
						.getFirstName().trim() : "");
				userBO2.setLastName(null != userBO.getLastName() ? userBO
						.getLastName().trim() : "");
				if (!userBO2.getUserEmail().equals(userBO.getUserEmail())) {
					emailIdChange = true;
					userBO2.setEmailChanged(true);
				}
				userBO2.setUserEmail((null != userBO.getUserEmail() ? userBO
						.getUserEmail().trim() : "").toLowerCase());
				userBO2.setPhoneNumber(null != userBO.getPhoneNumber() ? userBO
						.getPhoneNumber().trim() : "");
				userBO2.setRoleId(userBO.getRoleId());
				userBO2.setModifiedBy(userBO.getModifiedBy());
				userBO2.setModifiedOn(userBO.getModifiedOn());
				userBO2.setEnabled(userBO.isEnabled());
				if (!userSession.getUserId().equals(userBO.getUserId())) {
					userBO2.setForceLogout(true);
				}
			}
			msg = usersDAO.addOrUpdateUserDetails(userBO2, permissions,
					selectedStudies, permissionValues);
			if (msg.equals(FdahpStudyDesignerConstants.SUCCESS)) {
				if (addFlag) {
					activity = "User account created.";
					activityDetail = "New user created and Invite sent to user to activate account. (Account Details:- First Name = "
							+ userBO.getFirstName()
							+ " Last Name = "
							+ userBO.getLastName()
							+ ", Email ="
							+ userBO.getUserEmail() + ")";
					msg = loginService.sendPasswordResetLinkToMail(request,
							userBO2.getUserEmail(), "", "USER");
				}
				if (!addFlag) {
					activity = "User account updated.";
					activityDetail = "User account details updated. (Previous Account Details:- First Name = "
							+ userBO3.getFirstName()
							+ ", Last Name = "
							+ userBO3.getLastName()
							+ ", Email ="
							+ userBO3.getUserEmail()
							+ ") (New Account Details:- First Name = "
							+ userBO.getFirstName()
							+ ", Last Name = "
							+ userBO.getLastName()
							+ ", Email ="
							+ userBO.getUserEmail() + ")";
					if (emailIdChange) {
						msg = loginService.sendPasswordResetLinkToMail(request,
								userBO2.getUserEmail(), userBO3.getUserEmail(),
								"USER_EMAIL_UPDATE");
					} else {
						msg = loginService.sendPasswordResetLinkToMail(request,
								userBO2.getUserEmail(), "", "USER_UPDATE");
					}
				}
				auditLogDAO.saveToAuditLog(null, null, userSession, activity,
						activityDetail,
						"UsersDAOImpl - addOrUpdateUserDetails()");

				superAdminEmailList = usersDAO.getSuperAdminList();
				if (msg.equals(FdahpStudyDesignerConstants.SUCCESS)
						&& superAdminEmailList != null
						&& !superAdminEmailList.isEmpty()) {
					keyValueForSubject = new HashMap<String, String>();
					if (superAdminEmailList.size() == 1) {
						for (String email : superAdminEmailList) {
							adminFullNameIfSizeOne = usersDAO
									.getSuperAdminNameByEmailId(email);
							keyValueForSubject.put("$admin",
									adminFullNameIfSizeOne.getFirstName());
						}
					} else {
						keyValueForSubject.put("$admin", "Admin");
					}
					keyValueForSubject.put("$userEmail", userBO.getUserEmail());
					keyValueForSubject.put(
							"$sessionAdminFullName",
							userSession.getFirstName() + " "
									+ userSession.getLastName());
					if (addFlag) {
						dynamicContent = FdahpStudyDesignerUtil
								.genarateEmailContent(
										"mailForAdminUserCreateContent",
										keyValueForSubject);
						EmailNotification
								.sendEmailNotification(
										"mailForAdminUserCreateSubject",
										dynamicContent, null,
										superAdminEmailList, null);
					} else {
						String status = "";
						if (FdahpStudyDesignerUtil.isEmpty(userBO2
								.getUserPassword())) {
							status = "Pending Activation";
						} else {
							if (userBO2.isEnabled()) {
								status = "Active";
							} else {
								status = "Deactivated";
							}
						}
						keyValueForSubject.put("$userStatus", status);
						dynamicContent = FdahpStudyDesignerUtil
								.genarateEmailContent(
										"mailForAdminUserUpdateContent",
										keyValueForSubject);
						EmailNotification
								.sendEmailNotification(
										"mailForAdminUserUpdateSubject",
										dynamicContent, null,
										superAdminEmailList, null);
					}
				}

			}
		} catch (Exception e) {
			logger.error("UsersServiceImpl - addOrUpdateUserDetails() - ERROR",
					e);
		}
		logger.info("UsersServiceImpl - addOrUpdateUserDetails() - Ends");
		return msg;
	}

	/**
	 * This method is used to enforce the user to change the password
	 *
	 * @author BTC
	 * @param userId
	 * @param email
	 * @return message, Success/Failure message
	 */
	@Override
	public String enforcePasswordChange(Integer userId, String email) {
		logger.info("UsersServiceImpl - enforcePasswordChange() - Starts");
		String message = FdahpStudyDesignerConstants.FAILURE;
		try {
			message = usersDAO.enforcePasswordChange(userId, email);
		} catch (Exception e) {
			logger.error("UsersServiceImpl - enforcePasswordChange() - ERROR",
					e);
		}
		logger.info("UsersServiceImpl - enforcePasswordChange() - Ends");
		return message;
	}

	/**
	 * This method is to get the list of active user email ids
	 *
	 * @author BTC
	 * @return emails
	 */
	@Override
	public List<String> getActiveUserEmailIds() {
		logger.info("UsersServiceImpl - getActiveUserEmailIds() - Starts");
		List<String> emails = null;
		try {
			emails = usersDAO.getActiveUserEmailIds();
		} catch (Exception e) {
			logger.error("UsersServiceImpl - getActiveUserEmailIds() - ERROR",
					e);
		}
		logger.info("UsersServiceImpl - getActiveUserEmailIds() - Ends");
		return emails;
	}

	/**
	 * This method is used to get the permissions of the user
	 *
	 * @author BTC
	 * @param userId
	 * @return permissions
	 */
	@Override
	public List<Integer> getPermissionsByUserId(Integer userId) {
		logger.info("UsersServiceImpl - permissionsByUserId() - Starts");
		List<Integer> permissions = null;
		try {
			permissions = usersDAO.getPermissionsByUserId(userId);
		} catch (Exception e) {
			logger.error("UsersServiceImpl - permissionsByUserId() - ERROR", e);
		}
		logger.info("UsersServiceImpl - permissionsByUserId() - Ends");
		return permissions;
	}

	/**
	 * This method is used to get the user details
	 *
	 * @author BTC
	 * @param userId
	 * @return {@link UserBO}
	 */
	@Override
	public UserBO getUserDetails(Integer userId) {
		logger.info("UsersServiceImpl - getUserDetails() - Starts");
		UserBO userBO = null;
		try {
			userBO = usersDAO.getUserDetails(userId);
		} catch (Exception e) {
			logger.error("UsersServiceImpl - getUserDetails() - ERROR", e);
		}
		logger.info("UsersServiceImpl - getUserDetails() - Ends");
		return userBO;
	}

	/**
	 * This method is used to get the list of users
	 *
	 * @author BTC
	 * @return List of {@link UserBO}
	 */
	@Override
	public List<UserBO> getUserList() {
		logger.info("UsersServiceImpl - getUserList() - Starts");
		List<UserBO> userList = null;
		try {
			userList = usersDAO.getUserList();
		} catch (Exception e) {
			logger.error("UsersServiceImpl - getUserList() - ERROR", e);
		}
		logger.info("UsersServiceImpl - getUserList() - Ends");
		return userList;
	}

	/**
	 * This method is used to get user permissions
	 *
	 * @author BTC
	 * @param sessionUserId
	 * @return userId
	 */
	@Override
	public Integer getUserPermissionByUserId(Integer sessionUserId) {
		logger.info("UsersServiceImpl - getUserPermissionByUserId() - Starts");
		Integer userId = null;
		try {
			userId = usersDAO.getUserPermissionByUserId(sessionUserId);
		} catch (Exception e) {
			logger.error(
					"UsersServiceImpl - getUserPermissionByUserId() - ERROR", e);
		}
		logger.info("UsersServiceImpl - getUserPermissionByUserId() - Ends");
		return userId;
	}

	/**
	 * This method is used to get the role of the user
	 *
	 * @author BTC
	 * @param roleId
	 * @return {@link RoleBO}
	 *
	 */
	@Override
	public RoleBO getUserRole(int roleId) {
		logger.info("UsersServiceImpl - getUserRole() - Starts");
		RoleBO roleBO = null;
		try {
			roleBO = usersDAO.getUserRole(roleId);
		} catch (Exception e) {
			logger.error("UsersServiceImpl - getUserRole() - ERROR", e);
		}
		logger.info("UsersServiceImpl - getUserRole() - Ends");
		return roleBO;
	}

	/**
	 * This method is to get the list of user roles
	 *
	 * @author BTC
	 * @return List of {@link RoleBO}
	 */
	@Override
	public List<RoleBO> getUserRoleList() {
		logger.info("UsersServiceImpl - getUserRoleList() - Starts");
		List<RoleBO> roleBOList = null;
		try {
			roleBOList = usersDAO.getUserRoleList();
		} catch (Exception e) {
			logger.error("UsersServiceImpl - getUserRoleList() - ERROR", e);
		}
		logger.info("UsersServiceImpl - getUserRoleList() - Ends");
		return roleBOList;
	}
}
