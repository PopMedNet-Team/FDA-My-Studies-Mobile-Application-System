package com.fdahpstudydesigner.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;

public interface UsersService {

	public String activateOrDeactivateUser(int userId, int userStatus,
			int loginUser, SessionObject userSession, HttpServletRequest request);

	public String addOrUpdateUserDetails(HttpServletRequest request,
			UserBO userBO, String permissions, List<Integer> permissionList,
			String selectedStudies, String permissionValues,
			SessionObject userSession);

	public String enforcePasswordChange(Integer userId, String email);

	public List<String> getActiveUserEmailIds();

	public List<Integer> getPermissionsByUserId(Integer userId);

	public UserBO getUserDetails(Integer userId);

	public List<UserBO> getUserList();

	public Integer getUserPermissionByUserId(Integer sessionUserId);

	public RoleBO getUserRole(int roleId);

	public List<RoleBO> getUserRoleList();
}
