package com.fdahpstudydesigner.util;

import java.io.Serializable;
import java.util.List;

import com.fdahpstudydesigner.bean.StudySessionBean;

/**
 * @author
 *
 */
public class SessionObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 9080727824545069556L;

	private Integer adminstratorId = 0;
	private Integer auditLogUniqueId = 0;
	private String createdDate = "";
	private String currentHomeUrl = "";
	private String email = "";
	private String firstName = "";
	private Boolean isAdminstrating = false;
	private boolean isSuperAdmin = false;
	private String lastName = "";
	private boolean loginStatus = false;
	private String passwordExpairdedDateTime;
	private String phoneNumber = "";
	private String privacyPolicyText = "";
	private List<Integer> studySession;
	private List<StudySessionBean> studySessionBeans;
	private Integer superAdminId = 0;
	private String termsText = "";
	private Integer userId = 0;
	private String userName = "";
	private String userPermissions = "";
	private String userType = "";
	private String role = "";

	public Integer getAdminstratorId() {
		return adminstratorId;
	}

	public Integer getAuditLogUniqueId() {
		return auditLogUniqueId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getCurrentHomeUrl() {
		return currentHomeUrl;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public Boolean getIsAdminstrating() {
		return isAdminstrating;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPasswordExpairdedDateTime() {
		return passwordExpairdedDateTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPrivacyPolicyText() {
		return privacyPolicyText;
	}

	public List<Integer> getStudySession() {
		return studySession;
	}

	public List<StudySessionBean> getStudySessionBeans() {
		return studySessionBeans;
	}

	public Integer getSuperAdminId() {
		return superAdminId;
	}

	public String getTermsText() {
		return termsText;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPermissions() {
		return userPermissions;
	}

	public String getUserType() {
		return userType;
	}

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public boolean isSuperAdmin() {
		return isSuperAdmin;
	}

	public void setAdminstratorId(Integer adminstratorId) {
		this.adminstratorId = adminstratorId;
	}

	public void setAuditLogUniqueId(Integer auditLogUniqueId) {
		this.auditLogUniqueId = auditLogUniqueId;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setCurrentHomeUrl(String currentHomeUrl) {
		this.currentHomeUrl = currentHomeUrl;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setIsAdminstrating(Boolean isAdminstrating) {
		this.isAdminstrating = isAdminstrating;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}

	public void setPasswordExpairdedDateTime(String passwordExpairdedDateTime) {
		this.passwordExpairdedDateTime = passwordExpairdedDateTime;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPrivacyPolicyText(String privacyPolicyText) {
		this.privacyPolicyText = privacyPolicyText;
	}

	public void setStudySession(List<Integer> studySession) {
		this.studySession = studySession;
	}

	public void setStudySessionBeans(List<StudySessionBean> studySessionBeans) {
		this.studySessionBeans = studySessionBeans;
	}

	public void setSuperAdmin(boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public void setSuperAdminId(Integer superAdminId) {
		this.superAdminId = superAdminId;
	}

	public void setTermsText(String termsText) {
		this.termsText = termsText;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPermissions(String userPermissions) {
		this.userPermissions = userPermissions;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
