/**
 *
 */
package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * The persistent class for the user_attempts database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "user_attempts")
@NamedQueries({ @NamedQuery(name = "getUserAttempts", query = "from UserAttemptsBo UABO Where UABO.userEmailId=:userEmailId"),

})
public class UserAttemptsBo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3166967048106586712L;

	@Column(name = "attempts")
	private int attempts;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "last_modified")
	private String lastModified;

	@Column(name = "email_id")
	private String userEmailId;

	/**
	 * @return the attempts
	 */
	public int getAttempts() {
		return attempts;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the lastModified
	 */
	public String getLastModified() {
		return lastModified;
	}

	public String getUserEmail() {
		return userEmailId;
	}

	/**
	 * @param attempts
	 *            the attempts to set
	 */
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public void setUserEmail(String userEmailId) {
		this.userEmailId = userEmailId;
	}

}
