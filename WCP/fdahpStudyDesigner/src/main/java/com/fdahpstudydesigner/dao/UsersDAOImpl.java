package com.fdahpstudydesigner.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.ModelAndView;

import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPermissions;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;

/**
 * 
 * @author BTC
 *
 */
@Repository
public class UsersDAOImpl implements UsersDAO {

	private static Logger logger = Logger.getLogger(UsersDAOImpl.class);
	@Autowired
	private AuditLogDAO auditLogDAO;
	HibernateTemplate hibernateTemplate;

	private Transaction transaction = null;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

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
	 */
	@Override
	public String activateOrDeactivateUser(int userId, int userStatus,
			int loginUser, SessionObject userSession) {
		logger.info("UsersDAOImpl - activateOrDeactivateUser() - Starts");
		String msg = FdahpStudyDesignerConstants.FAILURE;
		Session session = null;
		int count = 0;
		Query query = null;
		Boolean forceLogout = false;
		String activity = "";
		String activityDetail = "";
		UserBO userBO = null;
		int userStatusNew;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			userBO = getUserDetails(userId);
			transaction = session.beginTransaction();
			if (userStatus == 0) {
				userStatusNew = 1;
				forceLogout = false;
				activity = "User activated.";
				activityDetail = "User Account activated. (Account Details:- First Name = "
						+ userBO.getFirstName()
						+ " Last Name = "
						+ userBO.getLastName()
						+ ", Email ="
						+ userBO.getUserEmail() + ")";
			} else {
				userStatusNew = 0;
				forceLogout = true;
				activity = "User deactivated.";
				activityDetail = "User account  de-activated. (Account Details:- First Name = "
						+ userBO.getFirstName()
						+ " Last Name = "
						+ userBO.getLastName()
						+ ", Email ="
						+ userBO.getUserEmail() + ")";
			}
			query = session.createQuery(" UPDATE UserBO SET enabled = "
					+ userStatusNew + ", modifiedOn = now(), modifiedBy = "
					+ loginUser + ",forceLogout = " + forceLogout
					+ " WHERE userId = " + userId);
			count = query.executeUpdate();
			if (count > 0) {
				auditLogDAO.saveToAuditLog(session, transaction, userSession,
						activity, activityDetail,
						"UsersDAOImpl - activateOrDeactivateUser()");
				msg = FdahpStudyDesignerConstants.SUCCESS;
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error("UsersDAOImpl - activateOrDeactivateUser() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - activateOrDeactivateUser() - Ends");
		return msg;
	}

	/**
	 * This method is used to add or update the user details
	 *
	 * @author BTC
	 * @param userBO
	 *            , {@link UserBO}
	 * @param permissions
	 * @param selectedStudies
	 * @param permissionValues
	 * @return {@link ModelAndView}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String addOrUpdateUserDetails(UserBO userBO, String permissions,
			String selectedStudies, String permissionValues) {
		logger.info("UsersDAOImpl - addOrUpdateUserDetails() - Starts");
		Session session = null;
		Integer userId = 0;
		String msg = FdahpStudyDesignerConstants.FAILURE;
		Query query = null;
		UserBO userBO2 = null;
		Set<UserPermissions> permissionSet = null;
		StudyPermissionBO studyPermissionBO = null;
		String[] selectedStudy = null;
		String[] permissionValue = null;
		boolean updateFlag = false;

		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (null == userBO.getUserId()) {
				userId = (Integer) session.save(userBO);
			} else {
				session.update(userBO);
				userId = userBO.getUserId();
				updateFlag = true;
			}

			query = session.createQuery(" FROM UserBO UBO where UBO.userId = "
					+ userId);
			userBO2 = (UserBO) query.uniqueResult();
			if (!permissions.isEmpty()) {
				permissionSet = new HashSet<UserPermissions>(session
						.createQuery(
								"FROM UserPermissions UPBO WHERE UPBO.permissions IN ("
										+ permissions + ")").list());
				userBO2.setPermissionList(permissionSet);
				session.update(userBO2);
			} else {
				userBO2.setPermissionList(null);
				session.update(userBO2);
			}

			if (updateFlag && "".equals(selectedStudies)) {
				query = session
						.createSQLQuery(" delete from study_permission where user_id ="
								+ userId);
				query.executeUpdate();
			}

			if (!"".equals(selectedStudies) && !"".equals(permissionValues)) {
				selectedStudy = selectedStudies.split(",");
				permissionValue = permissionValues.split(",");

				if (updateFlag) {
					query = session
							.createSQLQuery(" delete from study_permission where study_id not in ("
									+ selectedStudies
									+ ") and user_id ="
									+ userId);
					query.executeUpdate();
				}

				for (int i = 0; i < selectedStudy.length; i++) {
					query = session
							.createQuery(" FROM StudyPermissionBO UBO where UBO.studyId = "
									+ selectedStudy[i]
									+ " AND UBO.userId ="
									+ userId);
					studyPermissionBO = (StudyPermissionBO) query
							.uniqueResult();
					if (null != studyPermissionBO) {
						studyPermissionBO.setViewPermission("1"
								.equals(permissionValue[i]) ? true : false);
						session.update(studyPermissionBO);
					} else {
						studyPermissionBO = new StudyPermissionBO();
						studyPermissionBO.setStudyId(Integer
								.parseInt(selectedStudy[i]));
						studyPermissionBO.setViewPermission("1"
								.equals(permissionValue[i]) ? true : false);
						studyPermissionBO.setUserId(userId);
						session.save(studyPermissionBO);
					}
				}
			}
			transaction.commit();
			msg = FdahpStudyDesignerConstants.SUCCESS;
		} catch (Exception e) {
			transaction.rollback();
			logger.error("UsersDAOImpl - addOrUpdateUserDetails() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - addOrUpdateUserDetails() - Ends");
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
	@SuppressWarnings("unchecked")
	@Override
	public String enforcePasswordChange(Integer userId, String email) {
		logger.info("UsersDAOImpl - enforcePasswordChange() - Starts");
		Session session = null;
		String message = FdahpStudyDesignerConstants.FAILURE;
		String updateQuery = "";
		String userAttemptQuery = "";
		try {
			// sending activationLink to all active users and send the
			// deactivate users when they active
			session = hibernateTemplate.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			if (userId != null && StringUtils.isNotEmpty(email)) {
				updateQuery = "Update users set force_logout='Y', credentialsNonExpired=false WHERE user_id ="
						+ userId;
				userAttemptQuery = "update user_attempts set attempts = 0 WHERE email_id ='"
						+ email + "'";
			} else {
				updateQuery = "Update users set force_logout='Y' WHERE status=true";
				int count = session.createSQLQuery(updateQuery).executeUpdate();
				if (count > 0) {
					updateQuery = "Update users set credentialsNonExpired=false";
					userAttemptQuery = "update user_attempts set attempts = 0";
				}
			}
			// update password to empty and expiredTime to null
			if (StringUtils.isNotEmpty(updateQuery)) {
				int count = session.createSQLQuery(updateQuery).executeUpdate();
				if (count > 0) {
					session.createSQLQuery(userAttemptQuery).executeUpdate();
					message = FdahpStudyDesignerConstants.SUCCESS;
				}
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			logger.error("UsersDAOImpl - enforcePasswordChange() - ERROR", e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - enforcePasswordChange() - Ends");
		return message;
	}

	/**
	 * This method is to get the list of active user email ids
	 *
	 * @author BTC
	 * @return email ids
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getActiveUserEmailIds() {
		logger.info("UsersDAOImpl - getActiveUserEmailIds() - Starts");
		Session session = null;
		List<String> emails = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			// sending activationLink to all active users and send the
			// deactivate users when they active
			query = session
					.createSQLQuery(" SELECT u.email "
							+ "FROM users u,roles r WHERE r.role_id = u.role_id and u.status=1");
			emails = query.list();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getActiveUserEmailIds() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getActiveUserEmailIds() - Ends");
		return emails;
	}

	/**
	 * This method is used to get the permissions of the user
	 *
	 * @author BTC
	 * @param userId
	 * @return permissions, permissions of the user
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getPermissionsByUserId(Integer userId) {
		logger.info("UsersDAOImpl - getPermissionsByUserId() - Starts");
		Session session = null;
		Query query = null;
		List<Integer> permissions = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createSQLQuery(" SELECT UPM.permission_id FROM user_permission_mapping UPM WHERE UPM.user_id = "
							+ userId + "");
			permissions = query.list();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getPermissionsByUserId() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getPermissionsByUserId() - Ends");
		return permissions;
	}

	/**
	 * This method is used to get the list of super admins email ids
	 *
	 * @author BTC
	 * @return List of super admins email ids
	 */
	@Override
	public List<String> getSuperAdminList() {
		logger.info("UsersDAOImpl - getSuperAdminList() - Starts");
		Session session = null;
		List<String> userSuperAdminList = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createSQLQuery("Select u.email from users u where u.user_id in (select upm.user_id from user_permission_mapping upm where upm.permission_id = (select up.permission_id from user_permissions up where up.permissions = 'ROLE_SUPERADMIN'))");
			userSuperAdminList = query.list();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getSuperAdminList() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getSuperAdminList() - Ends");
		return userSuperAdminList;
	}

	/**
	 * This method is used to get the user by email id
	 *
	 * @author BTC
	 * @param emailId
	 * @return {@link UserBO}
	 */
	@Override
	public UserBO getSuperAdminNameByEmailId(String emailId) {
		logger.info("UsersDAOImpl - getSuperAdminNameByEmailId() - Starts");
		Session session = null;
		UserBO userBo = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery(" from UserBO where userEmail = '"
					+ emailId + "'");
			userBo = (UserBO) query.uniqueResult();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getSuperAdminNameByEmailId() - ERROR",
					e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getSuperAdminNameByEmailId() - Ends");
		return userBo;
	}

	/**
	 * This method is used to get the user details
	 *
	 * @author BTC
	 * @param userId
	 * @return {@link UserBO}
	 */
	@Override
	public UserBO getUserDetails(int userId) {
		logger.info("UsersDAOImpl - getUserDetails() - Starts");
		Session session = null;
		UserBO userBO = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.getNamedQuery("getUserById").setInteger("userId",
					userId);
			userBO = (UserBO) query.uniqueResult();
			if(userBO!=null && userBO.getRoleId()!=null){
				String  roleName = (String) session.createSQLQuery("select role_name from roles where role_id="+userBO.getRoleId()).uniqueResult();
				if(StringUtils.isNotEmpty(roleName))
					userBO.setRoleName(roleName);
			}
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getUserDetails() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getUserDetails() - Ends");
		return userBO;
	}

	/**
	 * This method is used to get the list of users
	 *
	 * @author BTC
	 * @return List of {@link UserBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserBO> getUserList() {
		logger.info("UsersDAOImpl - getUserList() - Starts");
		Session session = null;
		List<UserBO> userList = null;
		List<Object[]> objList = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createSQLQuery(" SELECT u.user_id,u.first_name,u.last_name,u.email,r.role_name,u.status,"
							+ "u.password,u.email_changed FROM users u,roles r WHERE r.role_id = u.role_id and u.user_id "
							+ "not in (select upm.user_id from user_permission_mapping upm where "
							+ "upm.permission_id = (select up.permission_id from user_permissions up "
							+ "where up.permissions ='ROLE_SUPERADMIN')) ORDER BY u.user_id DESC ");
			objList = query.list();
			if (null != objList && !objList.isEmpty()) {
				userList = new ArrayList<>();
				for (Object[] obj : objList) {
					UserBO userBO = new UserBO();
					userBO.setUserId(null != obj[0] ? (Integer) obj[0] : 0);
					userBO.setFirstName(null != obj[1] ? String.valueOf(obj[1])
							: "");
					userBO.setLastName(null != obj[2] ? String.valueOf(obj[2])
							: "");
					userBO.setUserEmail(null != obj[3] ? String.valueOf(obj[3])
							: "");
					userBO.setRoleName(null != obj[4] ? String.valueOf(obj[4])
							: "");
					userBO.setEnabled(null != obj[5] ? (Boolean) obj[5] : false);
					userBO.setUserPassword(null != obj[6] ? String
							.valueOf(obj[6]) : "");
					userBO.setEmailChanged(null != obj[7] ? (Boolean) obj[7]
							: false);
					userBO.setUserFullName(userBO.getFirstName() + " "
							+ userBO.getLastName());
					userList.add(userBO);
				}
			}
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getUserList() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getUserList() - Ends");
		return userList;
	}

	/**
	 * This method is used to get the user id of the super admin
	 * 
	 * @author BTC
	 * @param sessionUserId
	 * @return user id
	 * 
	 */
	@Override
	public Integer getUserPermissionByUserId(Integer sessionUserId) {
		logger.info("UsersDAOImpl - getUserPermissionByUserId() - Starts");
		Session session = null;
		Integer userId = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session
					.createSQLQuery("Select u.user_id from users u where u.user_id in "
							+ "(select upm.user_id from user_permission_mapping upm where upm.permission_id "
							+ "= (select up.permission_id from user_permissions up where "
							+ "up.permissions = 'ROLE_SUPERADMIN')) and u.user_id = "
							+ sessionUserId + "");
			userId = (Integer) query.uniqueResult();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getUserPermissionByUserId() - ERROR",
					e);
		}
		logger.info("UsersDAOImpl - getUserPermissionByUserId() - Ends");
		return userId;
	}

	/**
	 * This method is to get the role of the user
	 *
	 * @author BTC
	 * @param roleId
	 * @return {@link RoleBO}
	 */
	@Override
	public RoleBO getUserRole(int roleId) {
		logger.info("UsersDAOImpl - getUserRole() - Starts");
		Session session = null;
		RoleBO roleBO = null;
		Query query = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.getNamedQuery("getUserRoleByRoleId").setInteger(
					"roleId", roleId);
			roleBO = (RoleBO) query.uniqueResult();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getUserRole() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getUserRole() - Ends");
		return roleBO;
	}

	/**
	 * This method is to get the list of user roles
	 *
	 * @author BTC
	 * @return List of {@link RoleBO}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RoleBO> getUserRoleList() {
		logger.info("UsersDAOImpl - getUserRoleList() - Starts");
		List<RoleBO> roleBOList = null;
		Query query = null;
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			query = session.createQuery(" FROM RoleBO RBO ");
			roleBOList = query.list();
		} catch (Exception e) {
			logger.error("UsersDAOImpl - getUserRoleList() - ERROR", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		logger.info("UsersDAOImpl - getUserRoleList() - Ends");
		return roleBOList;
	}
}
