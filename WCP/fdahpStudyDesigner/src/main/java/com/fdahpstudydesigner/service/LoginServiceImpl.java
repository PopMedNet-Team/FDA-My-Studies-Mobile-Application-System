package com.fdahpstudydesigner.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPasswordHistory;
import com.fdahpstudydesigner.dao.AuditLogDAO;
import com.fdahpstudydesigner.dao.LoginDAOImpl;
import com.fdahpstudydesigner.util.EmailNotification;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * @author BTC
 *
 */
@Service
public class LoginServiceImpl implements LoginService, UserDetailsService {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class
			.getName());

	@Autowired
	private AuditLogDAO auditLogDAO;

	private LoginDAOImpl loginDAO;

	/**
	 * Validate the security token, access code and change the password
	 *
	 * @author BTC
	 *
	 * @param securityToken
	 *            , the security token of the forgot password link
	 * @param accessCode
	 *            , the access code from the forget password email
	 * @param password
	 *            , the new password added by user
	 * @return {@link Boolean} , isValid
	 */
	@Override
	public String authAndAddPassword(String securityToken, String accessCode,
			String password, UserBO userBO2, SessionObject sesObj) {
		UserBO userBO = null;
		logger.info("LoginServiceImpl - checkSecurityToken() - Starts");
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		boolean isValid = false;
		boolean isIntialPasswordSetUp = false;
		Map<String, String> keyValueForSubject = null;
		String dynamicContent = "";
		String result = FdahpStudyDesignerConstants.FAILURE;
		String invalidAccessCodeError = propMap
				.get("invalid.access.code.error.msg");
		String oldPasswordError = propMap.get("old.password.error.msg");
		String passwordCount = propMap.get("password.history.count");
		List<UserPasswordHistory> passwordHistories = null;
		Boolean isValidPassword = true;
		String activity = "";
		String activityDetail = "";
		try {
			userBO = loginDAO.getUserBySecurityToken(securityToken);
			if (null != userBO) {
				if (StringUtils.isBlank(userBO.getUserPassword())) {
					isIntialPasswordSetUp = true;
				}
				if (userBO.getAccessCode().equals(accessCode)) {
					if (password != null
							&& (password
									.contains(FdahpStudyDesignerUtil
											.isNotEmpty(userBO2.getFirstName()) ? userBO2
											.getFirstName() : userBO
											.getFirstName()) || password
									.contains(FdahpStudyDesignerUtil
											.isNotEmpty(userBO2.getLastName()) ? userBO2
											.getLastName() : userBO
											.getLastName()))) {
						isValidPassword = false;
					}
					if (isValidPassword) {
						passwordHistories = loginDAO.getPasswordHistory(userBO
								.getUserId());
						if (passwordHistories != null
								&& !passwordHistories.isEmpty()) {
							for (UserPasswordHistory userPasswordHistory : passwordHistories) {
								if (FdahpStudyDesignerUtil
										.compareEncryptedPassword(
												userPasswordHistory
														.getUserPassword(),
												password)) {
									isValidPassword = false;
									break;
								}
							}
						}
						if (isValidPassword) {
							if (userBO2 != null
									&& StringUtils.isNotEmpty(userBO2
											.getFirstName())) {
								userBO.setFirstName(null != userBO2
										.getFirstName() ? userBO2
										.getFirstName().trim() : "");
								userBO.setLastName(null != userBO2
										.getLastName() ? userBO2.getLastName()
										.trim() : "");
								userBO.setPhoneNumber(null != userBO2
										.getPhoneNumber() ? userBO2
										.getPhoneNumber().trim() : "");
								activity = "User registration.";
								activityDetail = "User named "
										+ userBO2.getFirstName() + " "
										+ userBO2.getLastName()
										+ " is successfully registered";
							} else {
								activity = "Forgot password";
								activityDetail = "User successfully created the new password.";
							}
							userBO.setUserPassword(FdahpStudyDesignerUtil
									.getEncryptedPassword(password));
							userBO.setTokenUsed(true);
							userBO.setEnabled(true);
							userBO.setAccountNonExpired(true);
							userBO.setAccountNonLocked(true);
							userBO.setCredentialsNonExpired(true);
							userBO.setPasswordExpairdedDateTime(new SimpleDateFormat(
									FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
									.format(new Date()));
							result = loginDAO.updateUser(userBO);
							if (result
									.equals(FdahpStudyDesignerConstants.SUCCESS)) {
								loginDAO.updatePasswordHistory(
										userBO.getUserId(),
										userBO.getUserPassword());
								isValid = true;
								SessionObject sessionObject = new SessionObject();
								sessionObject.setUserId(userBO.getUserId());
								auditLogDAO.saveToAuditLog(null, null,
										sessionObject, activity,
										activityDetail,
										"LoginDAOImpl - updateUser()");
							}
						} else {
							result = oldPasswordError.replace("$countPass",
									passwordCount);
						}
					} else {
						result = propMap
								.get("password.name.contains.error.msg");
					}
				} else {
					result = invalidAccessCodeError;
				}
				if (isIntialPasswordSetUp && isValid) {
					List<String> cc = new ArrayList<>();
					cc.add(propMap.get("email.address.cc"));
					keyValueForSubject = new HashMap<>();
					dynamicContent = FdahpStudyDesignerUtil
							.genarateEmailContent(
									"newASPInitialPasswordSetupContent",
									keyValueForSubject);
					EmailNotification.sendEmailNotification(
							"newASPInitialPasswordSetupSubject",
							dynamicContent, propMap.get("email.address.to"),
							cc, null);
				}
			}
		} catch (Exception e) {
			logger.error("LoginServiceImpl - checkSecurityToken() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - checkSecurityToken() - Ends");
		return result;
	}

	/**
	 * Change the user password
	 *
	 * @author BTC
	 *
	 * @param userId
	 *            , The id of the user
	 * @param newPassword
	 *            , The new password added by user
	 * @return {@link String} , the status FdahpStudyDesignerConstants.SUCCESS
	 *         or FdahpStudyDesignerConstants.FAILURE
	 */
	@Override
	public String changePassword(Integer userId, String newPassword,
			String oldPassword, SessionObject sesObj) {
		logger.info("LoginServiceImpl - changePassword() - Starts");
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		String message = FdahpStudyDesignerConstants.FAILURE;
		String oldPasswordError = propMap.get("old.password.error.msg");
		String passwordMaxCharMatchError = propMap
				.get("password.max.char.match.error.msg");
		String passwordCount = propMap.get("password.history.count");
		Integer passwordMaxCharMatchCount = Integer.parseInt(propMap
				.get("password.max.char.match.count"));
		List<UserPasswordHistory> passwordHistories = null;
		Boolean isValidPassword = false;
		String activity = "";
		String activityDetail = "";
		int countPassChar = 0;
		try {
			if (newPassword != null
					&& (newPassword.contains(sesObj.getFirstName()) || newPassword
							.contains(sesObj.getLastName()))) {
				isValidPassword = true;
			}
			if (!isValidPassword) {
				if (null != newPassword && StringUtils.isNotBlank(newPassword)) {
					char[] newPassChar = newPassword.toCharArray();
					List<String> countList = new ArrayList<>();
					for (char c : newPassChar) {
						if (oldPassword != null
								&& (!oldPassword
										.contains(Character.toString(c)))
								&& !countList.contains(Character.toString(c))) {
							countPassChar++;
							countList.add(Character.toString(c));
						}
						if (passwordMaxCharMatchCount != null
								&& countPassChar > passwordMaxCharMatchCount) {
							isValidPassword = true;
							break;
						}
					}
				}
				if (isValidPassword) {
					passwordHistories = loginDAO.getPasswordHistory(userId);
					if (passwordHistories != null
							&& !passwordHistories.isEmpty()) {
						for (UserPasswordHistory userPasswordHistory : passwordHistories) {
							if (FdahpStudyDesignerUtil
									.compareEncryptedPassword(
											userPasswordHistory
													.getUserPassword(),
											newPassword)) {
								isValidPassword = false;
								break;
							}
						}
					}
					if (isValidPassword) {
						message = loginDAO.changePassword(userId, newPassword,
								oldPassword);
						if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
							loginDAO.updatePasswordHistory(userId,
									FdahpStudyDesignerUtil
											.getEncryptedPassword(newPassword));
							activity = "Change password.";
							activityDetail = "User successfully changed his/her password.";
							auditLogDAO.saveToAuditLog(null, null, sesObj,
									activity, activityDetail,
									"LoginDAOImpl - changePassword");
						}
					} else {
						message = oldPasswordError.replace("$countPass",
								passwordCount);
					}
				} else {
					message = passwordMaxCharMatchError.replace("$countMatch",
							passwordMaxCharMatchCount + "");
				}
			} else {
				message = propMap.get("password.name.contains.error.msg");
			}
		} catch (Exception e) {
			logger.error("LoginServiceImpl - changePassword() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - changePassword() - Ends");
		return message;
	}

	/**
	 * Validate the security token for forgot password link before check
	 *
	 * @author BTC
	 *
	 * @param securityToken
	 *            , the security token of the forgot password link
	 * @return {@link Boolean} , isValid
	 */
	@Override
	public UserBO checkSecurityToken(String securityToken) {
		UserBO userBO = null;
		logger.info("LoginServiceImpl - checkSecurityToken() - Starts");
		Date securityTokenExpiredDate = null;
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		UserBO chkBO = null;
		final Integer MAX_ATTEMPTS = Integer.valueOf(propMap
				.get("max.login.attempts"));
		final Integer USER_LOCK_DURATION = Integer.valueOf(propMap
				.get("user.lock.duration.in.minutes"));
		try {
			userBO = loginDAO.getUserBySecurityToken(securityToken);
			if (null != userBO && !userBO.getTokenUsed()) {
				UserAttemptsBo userAttempts = loginDAO.getUserAttempts(userBO
						.getUserEmail());
				if (userAttempts == null
						|| userAttempts.getAttempts() < MAX_ATTEMPTS
						|| new SimpleDateFormat(
								FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
								.parse(FdahpStudyDesignerUtil.addMinutes(
										userAttempts.getLastModified(),
										USER_LOCK_DURATION))
								.before(new SimpleDateFormat(
										FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
										.parse(FdahpStudyDesignerUtil
												.getCurrentDateTime()))) {
					securityTokenExpiredDate = new SimpleDateFormat(
							FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
							.parse(userBO.getTokenExpiryDate());
					if (securityTokenExpiredDate
							.after(new SimpleDateFormat(
									FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
									.parse(FdahpStudyDesignerUtil
											.getCurrentDateTime()))) {
						chkBO = userBO;
					}
				}
			}
		} catch (Exception e) {
			logger.error("LoginServiceImpl - checkSecurityToken() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - checkSecurityToken() - Ends");
		return chkBO;
	}

	/**
	 * Check the current session user is forcefully logout by Admin
	 *
	 * @author BTC
	 *
	 * @param sessionObject
	 *            , {@link SessionObject}
	 * @return {@link Boolean} , isValid
	 */
	@Override
	public Boolean isFrocelyLogOutUser(SessionObject sessionObject) {
		logger.info("LoginServiceImpl - isFrocelyLogOutUser() - Starts");
		Boolean isFrocelyLogOut = false;
		try {
			isFrocelyLogOut = loginDAO.isFrocelyLogOutUser(sessionObject
					.getUserId());
		} catch (Exception e) {
			logger.error("LoginServiceImpl - isFrocelyLogOutUser() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - isFrocelyLogOutUser() - Ends");
		return isFrocelyLogOut;
	}

	/**
	 * Check the current session user is status with respect to it super user
	 *
	 * @author BTC
	 *
	 * @param sessionObject
	 *            , {@link SessionObject}
	 * @return {@link Boolean} , isValid
	 */
	@Override
	public Boolean isUserEnabled(SessionObject sessionObject) {
		logger.info("LoginServiceImpl - isUserEnabled() - Starts");
		Boolean isUserEnabled = true;
		try {
			if (sessionObject.isSuperAdmin()) {
				isUserEnabled = loginDAO.isUserEnabled(sessionObject
						.getUserId());
			} else if (!sessionObject.isSuperAdmin()
					&& sessionObject.getSuperAdminId() != null) {
				if (!(loginDAO.isUserEnabled(sessionObject.getUserId()))
						|| !(loginDAO.isUserEnabled(sessionObject
								.getSuperAdminId()))) {
					isUserEnabled = false;
				}
			}
		} catch (Exception e) {
			logger.error("LoginServiceImpl - isUserEnabled() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - isUserEnabled() - Ends");
		return isUserEnabled;
	}

	/**
	 * Get the spring security user details by user email
	 *
	 * @author BTC
	 *
	 * @param userEmail
	 *            , the user email id
	 * @return {@link UserDetails} , the spring security user details
	 */
	@Override
	public UserDetails loadUserByUsername(String userEmail)
			throws UsernameNotFoundException {
		UserBO user = loginDAO.getValidUserByEmail(userEmail);

		List<GrantedAuthority> authorities = FdahpStudyDesignerUtil
				.buildUserAuthority(user.getPermissions());

		return FdahpStudyDesignerUtil.buildUserForAuthentication(user,
				authorities);
	}

	/**
	 * Log userLogOut Event In DB
	 *
	 * @author BTC
	 *
	 * @param sessionObject
	 *            , {@link SessionObject}
	 * @return {@link Boolean} , isValid
	 */
	@Override
	public Boolean logUserLogOut(SessionObject sessionObject) {
		logger.info("LoginServiceImpl - isFrocelyLogOutUser() - Starts");
		Boolean isLogged = false;
		String activity = "";
		String activityDetail = "";
		try {
			activity = "User logout.";
			activityDetail = "User successfully signed out. (Account Details:- First Name = "
					+ sessionObject.getFirstName()
					+ ", Last Name = "
					+ sessionObject.getLastName()
					+ ", Email ="
					+ sessionObject.getEmail() + ").";
			auditLogDAO.saveToAuditLog(null, null, sessionObject, activity,
					activityDetail,
					"FdahpStudyDesignerPreHandlerInterceptor - preHandle()");
			isLogged = true;
		} catch (Exception e) {
			logger.error("LoginServiceImpl - isFrocelyLogOutUser() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - isFrocelyLogOutUser() - Ends");
		return isLogged;
	}

	/**
	 * Send the user password to user email
	 *
	 * @author BTC
	 *
	 * @param HttpServletRequest
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param email
	 *            , The Email id of user
	 * @param type
	 *            , the type of user
	 * @return {@link String} , the status FdahpStudyDesignerConstants.SUCCESS
	 *         or FdahpStudyDesignerConstants.FAILURE
	 */
	@Override
	public String sendPasswordResetLinkToMail(HttpServletRequest request,
			String email, String oldEmail, String type) {
		logger.info("LoginServiceImpl - sendPasswordResetLinkToMail - Starts");
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		String passwordResetToken = null;
		String message = propMap.get("user.forgot.error.msg");
		boolean flag = false;
		UserBO userdetails = null;
		String accessCode = "";
		Map<String, String> keyValueForSubject = null;
		Map<String, String> keyValueForSubject2 = null;
		String dynamicContent = "";
		String anotherdynamicContent = "";
		String acceptLinkMail = "";
		int passwordResetLinkExpirationInDay = Integer.parseInt(propMap
				.get("password.resetLink.expiration.in.hour"));
		String customerCareMail = "";
		String contact = "";
		final Integer MAX_ATTEMPTS = Integer.valueOf(propMap
				.get("max.login.attempts"));
		final Integer USER_LOCK_DURATION = Integer.valueOf(propMap
				.get("user.lock.duration.in.minutes"));
		final String lockMsg = propMap.get("user.lock.msg");
		try {
			passwordResetToken = RandomStringUtils.randomAlphanumeric(10);
			accessCode = RandomStringUtils.randomAlphanumeric(6);
			if (!StringUtils.isEmpty(passwordResetToken)) {
				userdetails = loginDAO.getValidUserByEmail(email);
				if ("".equals(type) && userdetails.getEmailChanged()) {
					userdetails = null;
				}
				UserAttemptsBo userAttempts = loginDAO.getUserAttempts(email);
				// Restricting the user to login for specified minutes if the
				// user has max fails attempts
				if (type != null
						&& "".equals(type)
						&& userAttempts != null
						&& userAttempts.getAttempts() >= MAX_ATTEMPTS
						&& new SimpleDateFormat(
								FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
								.parse(FdahpStudyDesignerUtil.addMinutes(
										userAttempts.getLastModified(),
										USER_LOCK_DURATION))
								.after(new SimpleDateFormat(
										FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
										.parse(FdahpStudyDesignerUtil
												.getCurrentDateTime()))) {
					message = lockMsg;
					flag = false;
				} else {
					flag = true;
				}

				if (flag) {
					flag = false;
					if (null != userdetails) {
						userdetails.setSecurityToken(passwordResetToken);
						userdetails.setAccessCode(accessCode);
						userdetails.setTokenUsed(false);
						userdetails.setTokenExpiryDate(FdahpStudyDesignerUtil
								.addHours(FdahpStudyDesignerUtil
										.getCurrentDateTime(),
										passwordResetLinkExpirationInDay));

						if (!"USER_UPDATE".equals(type)) {
							/*
							 * if("USER_EMAIL_UPDATE".equals(type)){
							 * userdetails.setUserPassword("");
							 * userdetails.setEnabled(false); }
							 */
							message = loginDAO.updateUser(userdetails);
						} else {
							message = FdahpStudyDesignerConstants.SUCCESS;
						}
						if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
							if ("USER_EMAIL_UPDATE".equalsIgnoreCase(type)) {
								acceptLinkMail = propMap.get("emailChangeLink")
										.trim();
							} else {
								acceptLinkMail = propMap.get("acceptLinkMail")
										.trim();
							}
							keyValueForSubject = new HashMap<String, String>();
							keyValueForSubject2 = new HashMap<String, String>();
							keyValueForSubject.put("$firstName",
									userdetails.getFirstName());
							keyValueForSubject2.put("$firstName",
									userdetails.getFirstName());
							keyValueForSubject.put("$lastName",
									userdetails.getLastName());
							keyValueForSubject.put("$accessCode", accessCode);
							keyValueForSubject.put("$passwordResetLink",
									acceptLinkMail + passwordResetToken);
							customerCareMail = propMap
									.get("email.address.customer.service");
							keyValueForSubject.put("$customerCareMail",
									customerCareMail);
							keyValueForSubject2.put("$customerCareMail",
									customerCareMail);
							keyValueForSubject.put("$newUpdatedMail",
									userdetails.getUserEmail());
							keyValueForSubject2.put("$newUpdatedMail",
									userdetails.getUserEmail());
							keyValueForSubject.put("$oldMail", oldEmail);
							contact = propMap.get("phone.number.to");
							keyValueForSubject.put("$contact", contact);
							if ("USER".equals(type) && !userdetails.isEnabled()) {
								dynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"userRegistrationContent",
												keyValueForSubject);
								flag = EmailNotification.sendEmailNotification(
										"userRegistrationSubject",
										dynamicContent, email, null, null);
							} else if ("USER_UPDATE".equals(type)
									&& userdetails.isEnabled()) {
								dynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"mailForUserUpdateContent",
												keyValueForSubject2);
								flag = EmailNotification.sendEmailNotification(
										"mailForUserUpdateSubject",
										dynamicContent, email, null, null);
							} else if ("USER_EMAIL_UPDATE".equals(type)) {
								// Email to old email address
								dynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"mailToOldEmailForUserEmailUpdateContent",
												keyValueForSubject2);
								flag = EmailNotification
										.sendEmailNotification(
												"mailToOldEmailForUserEmailUpdateSubject",
												dynamicContent, oldEmail, null,
												null);
								// Email to new email address
								anotherdynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"mailToNewEmailForUserEmailUpdateContent",
												keyValueForSubject);
								flag = EmailNotification
										.sendEmailNotification(
												"mailToNewEmailForUserEmailUpdateSubject",
												anotherdynamicContent, email,
												null, null);
							} else if ("enforcePasswordChange".equals(type)) {
								dynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"mailForEnforcePasswordChangeContent",
												keyValueForSubject);
								flag = EmailNotification.sendEmailNotification(
										"mailForEnforcePasswordChangeSubject",
										dynamicContent, email, null, null);
							} else if ("ReactivateMailAfterEnforcePassChange"
									.equals(type) && userdetails.isEnabled()) {
								dynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"mailForReactivatingUserAfterEnforcePassChangeContent",
												keyValueForSubject);
								flag = EmailNotification
										.sendEmailNotification(
												"mailForReactivatingUserAfterEnforcePassChangeSubject",
												dynamicContent, email, null,
												null);
							} else if ("".equals(type)
									&& userdetails.isEnabled()) {
								dynamicContent = FdahpStudyDesignerUtil
										.genarateEmailContent(
												"passwordResetLinkContent",
												keyValueForSubject);
								flag = EmailNotification.sendEmailNotification(
										"passwordResetLinkSubject",
										dynamicContent, email, null, null);
							}
							if (flag) {
								message = FdahpStudyDesignerConstants.SUCCESS;
							}
							if ("".equals(type) && (!userdetails.isEnabled())) {
								message = propMap.get("user.forgot.error.msg");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"LoginServiceImpl - sendPasswordResetLinkToMail - ERROR ",
					e);
		}
		logger.info("LoginServiceImpl - sendPasswordResetLinkToMail - Ends");
		return message;
	}

	/**
	 * Setting DI
	 *
	 * @param loginDAO
	 */
	@Autowired
	public void setLoginDAO(LoginDAOImpl loginDAO) {
		this.loginDAO = loginDAO;
	}

	/**
	 * This method is used to validate access code
	 * 
	 * @author BTC
	 * @param securityToken
	 * @param accessCode
	 * @return result, Success/Failure message
	 */
	public String validateAccessCode(String securityToken, String accessCode) {
		UserBO userBO = null;
		logger.info("LoginServiceImpl - checkSecurityToken() - Starts");
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		String result = FdahpStudyDesignerConstants.FAILURE;
		String invalidAccessCodeError = propMap
				.get("invalid.access.code.error.msg");
		try {
			userBO = loginDAO.getUserBySecurityToken(securityToken);
			if (null != userBO) {
				if (userBO.getAccessCode().equals(accessCode)) {
					userBO.setEmailChanged(false);
					userBO.setTokenUsed(true);
					result = loginDAO.updateUser(userBO);
				} else {
					result = invalidAccessCodeError;
				}
			}
		} catch (Exception e) {
			logger.error("LoginServiceImpl - checkSecurityToken() - ERROR ", e);
		}
		logger.info("LoginServiceImpl - checkSecurityToken() - Ends");
		return result;
	}

}
