package com.fdahpstudydesigner.service;

import javax.servlet.http.HttpServletRequest;

import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * @author BTC
 *
 */
public interface LoginService {

	public String authAndAddPassword(String securityToken, String accessCode,
			String password, UserBO userBO, SessionObject sesObj);

	public String changePassword(Integer userId, String newPassword,
			String oldPassword, SessionObject sesObj);

	public UserBO checkSecurityToken(String securityToken);

	public Boolean isFrocelyLogOutUser(SessionObject sessionObject);

	public Boolean isUserEnabled(SessionObject sessionObject);

	public Boolean logUserLogOut(SessionObject sessionObject);

	public String sendPasswordResetLinkToMail(HttpServletRequest request,
			String email, String oldEmail, String type);

}
