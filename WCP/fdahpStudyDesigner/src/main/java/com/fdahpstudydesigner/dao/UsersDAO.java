package com.fdahpstudydesigner.dao;

import java.util.List;

import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;

public interface UsersDAO {

	public String activateOrDeactivateUser(int userId, int userStatus,
			int loginUser, SessionObject userSession);

	public String addOrUpdateUserDetails(UserBO userBO, String permissions,
			String selectedStudies, String permissionValues);

	public String enforcePasswordChange(Integer userId, String email);

	public List<String> getActiveUserEmailIds();

	public List<Integer> getPermissionsByUserId(Integer userId);

	public List<String> getSuperAdminList();

	public UserBO getSuperAdminNameByEmailId(String emailId);

	public UserBO getUserDetails(int userId);

	public List<UserBO> getUserList();

	public Integer getUserPermissionByUserId(Integer sessionUserId);

	public RoleBO getUserRole(int roleId);

	public List<RoleBO> getUserRoleList();
}
