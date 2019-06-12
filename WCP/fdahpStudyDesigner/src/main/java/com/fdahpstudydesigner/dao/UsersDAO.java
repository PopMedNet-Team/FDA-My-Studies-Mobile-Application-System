/*******************************************************************************
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" ,WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
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
