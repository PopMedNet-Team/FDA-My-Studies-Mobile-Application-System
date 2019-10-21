package com.fdahpstudydesigner.dao;

import java.util.List;

import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPasswordHistory;

/**
 * @author BTC
 *
 */
public interface LoginDAO {

	public String changePassword(Integer userId, String newPassword,
			String oldPassword);

	public List<UserPasswordHistory> getPasswordHistory(Integer userId);

	public UserAttemptsBo getUserAttempts(String userEmailId);

	public UserBO getUserBySecurityToken(String securityToken);

	public UserBO getValidUserByEmail(String email);

	public Boolean isFrocelyLogOutUser(Integer userId);

	public Boolean isUserEnabled(Integer userId);

	public void passwordLoginBlocked();

	public void resetFailAttempts(String userEmailId);

	public void updateFailAttempts(String userEmailId);

	public String updatePasswordHistory(Integer userId, String userPassword);

	public String updateUser(UserBO userBO);

}
