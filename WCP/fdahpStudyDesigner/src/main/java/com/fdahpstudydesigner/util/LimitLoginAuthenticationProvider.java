/**
 *
 */
package com.fdahpstudydesigner.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.AuditLogDAO;
import com.fdahpstudydesigner.dao.LoginDAOImpl;

/**
 * An {@link AuthenticationProvider} implementation that retrieves user details
 * from a {@link UserDetailsService} and count the user fail login.
 * 
 * @author BTC
 *
 *
 */

public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {

	private static Logger logger = Logger
			.getLogger(LimitLoginAuthenticationProvider.class.getName());

	@Autowired
	private AuditLogDAO auditLogDAO;

	private LoginDAOImpl loginDAO;

	Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();

	/**
	 * 
	 *
	 * @see org.springframework.security.authentication.dao.
	 *      AbstractUserDetailsAuthenticationProvider
	 *      #authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) {
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		UserBO userBO;
		final Integer MAX_ATTEMPTS = Integer.valueOf(propMap
				.get("max.login.attempts"));
		final Integer USER_LOCK_DURATION = Integer.valueOf(propMap
				.get("user.lock.duration.in.minutes"));
		final String lockMsg = propMap.get("user.lock.msg");
		try {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.currentRequestAttributes();
			attributes.getRequest();
			String username = (String) authentication.getPrincipal();
			if (StringUtils.isNotEmpty(username)) {
				userBO = loginDAO.getValidUserByEmail(username);
				if (userBO == null) {
					auditLogDAO
							.saveToAuditLog(
									null,
									null,
									null,
									FdahpStudyDesignerConstants.USER_EMAIL_FAIL_ACTIVITY_MESSAGE,
									FdahpStudyDesignerConstants.USER_EMAIL_FAIL_ACTIVITY_DEATILS_MESSAGE,
									"LimitLoginAuthenticationProvider - authenticate()");
				}
			}
			UserAttemptsBo userAttempts = loginDAO
					.getUserAttempts(authentication.getName());

			// Restricting the user to login for specified minutes if the user
			// has max fails attempts
			try {
				if (userAttempts != null
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
					throw new LockedException(lockMsg);
				}
			} catch (ParseException e) {
				logger.error(
						"LimitLoginAuthenticationProvider - authenticate - ERROR",
						e);
			}

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					authentication.getPrincipal(),
					authentication.getCredentials(),
					new ArrayList<GrantedAuthority>());

			// if reach here, means login success, else an exception will be
			// thrown
			// reset the user_attempts
			Authentication auth = super.authenticate(token);
			loginDAO.resetFailAttempts(authentication.getName());
			return auth;

		} catch (BadCredentialsException e) {

			// invalid login, update to user_attempts
			loginDAO.updateFailAttempts(authentication.getName());
			throw e;

		} catch (LockedException e) {

			logger.error(
					"LimitLoginAuthenticationProvider - authenticate - ERROR - this user is locked! ",
					e);
			String error;
			UserAttemptsBo userAttempts = loginDAO
					.getUserAttempts(authentication.getName());

			if (userAttempts != null) {
				error = lockMsg;
			} else {
				error = e.getMessage();
			}

			throw new LockedException(error);
		}
	}

	@Autowired
	public void setLoginDAO(LoginDAOImpl loginDAO) {
		this.loginDAO = loginDAO;
	}

}
