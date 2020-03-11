package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the users database table.
 * 
 * @author BTC
 *
 */

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name = "getUserByEmail", query = "select UBO from UserBO UBO where UBO.userEmail =:email"),
		@NamedQuery(name = "getUserById", query = "SELECT UBO FROM UserBO UBO WHERE UBO.userId =:userId"),
		@NamedQuery(name = "getUserBySecurityToken", query = "select UBO from UserBO UBO where UBO.securityToken =:securityToken"), })
public class UserBO implements Serializable {

	private static final long serialVersionUID = 135353554543L;

	@Column(name = "access_code")
	private String accessCode;

	@Column(name = "accountNonExpired", length = 1)
	private boolean accountNonExpired;

	@Column(name = "accountNonLocked", length = 1)
	private boolean accountNonLocked;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date")
	private String createdOn;

	@Column(name = "credentialsNonExpired", length = 1)
	private boolean credentialsNonExpired;

	@Column(name = "email_changed", columnDefinition = "TINYINT(1)")
	private Boolean emailChanged = false;

	@Column(name = "status", length = 1)
	private boolean enabled;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "force_logout")
	@Type(type = "yes_no")
	private boolean forceLogout = false;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date")
	private String modifiedOn;

	@Column(name = "password_expairded_datetime")
	private String passwordExpairdedDateTime;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JoinTable(name = "user_permission_mapping", joinColumns = { @JoinColumn(name = "user_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "permission_id", nullable = false) })
	private Set<UserPermissions> permissionList = new HashSet<>(0);

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "role_id")
	private Integer roleId;

	@Transient
	private String roleName;

	@Column(name = "security_token")
	private String securityToken;

	@Column(name = "token_expiry_date")
	private String tokenExpiryDate;

	@Column(name = "token_used")
	private Boolean tokenUsed;

	@Column(name = "email")
	private String userEmail;

	@Transient
	private String userFullName;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "user_login_datetime")
	private String userLastLoginDateTime;

	@Column(name = "password")
	private String userPassword;

	/**
	 * @return the accessCode
	 */
	public String getAccessCode() {
		return accessCode;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public Boolean getEmailChanged() {
		return emailChanged;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public String getPasswordExpairdedDateTime() {
		return passwordExpairdedDateTime;
	}

	/**
	 * @return the permissionList
	 */
	public Set<UserPermissions> getPermissionList() {
		return permissionList;
	}

	/**
	 * @return the permissions
	 */
	public Set<UserPermissions> getPermissions() {
		return permissionList;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	/**
	 * @return the securityToken
	 */
	public String getSecurityToken() {
		return securityToken;
	}

	/**
	 * @return the tokenExpiryDate
	 */
	public String getTokenExpiryDate() {
		return tokenExpiryDate;
	}

	/**
	 * @return the tokenUsed
	 */
	public Boolean getTokenUsed() {
		return tokenUsed;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserLastLoginDateTime() {
		return userLastLoginDateTime;
	}

	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @return the accountNonExpired
	 */
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * @return the accountNonLocked
	 */
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * @return the credentialsNonExpired
	 */
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isForceLogout() {
		return forceLogout;
	}

	/**
	 * @param accessCode
	 *            the accessCode to set
	 */
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	/**
	 * @param accountNonExpired
	 *            the accountNonExpired to set
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * @param accountNonLocked
	 *            the accountNonLocked to set
	 */
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @param credentialsNonExpired
	 *            the credentialsNonExpired to set
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setEmailChanged(Boolean emailChanged) {
		this.emailChanged = emailChanged;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setForceLogout(boolean forceLogout) {
		this.forceLogout = forceLogout;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setPasswordExpairdedDateTime(String passwordExpairdedDateTime) {
		this.passwordExpairdedDateTime = passwordExpairdedDateTime;
	}

	/**
	 * @param permissionList
	 *            the permissionList to set
	 */
	public void setPermissionList(Set<UserPermissions> permissionList) {
		this.permissionList = permissionList;
	}

	/**
	 * @param permissions
	 *            the permissions to set
	 */
	public void setPermissions(Set<UserPermissions> permissionList) {
		this.permissionList = permissionList;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @param securityToken
	 *            the securityToken to set
	 */
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	/**
	 * @param tokenExpiryDate
	 *            the tokenExpiryDate to set
	 */
	public void setTokenExpiryDate(String tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}

	/**
	 * @param tokenUsed
	 *            the tokenUsed to set
	 */
	public void setTokenUsed(Boolean tokenUsed) {
		this.tokenUsed = tokenUsed;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setUserLastLoginDateTime(String userLastLoginDateTime) {
		this.userLastLoginDateTime = userLastLoginDateTime;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
}
