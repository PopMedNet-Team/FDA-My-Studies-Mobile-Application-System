package com.fdahpstudydesigner.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * The persistent class for the master_data database table.
 * 
 * @author BTC
 *
 */

@Entity
@Table(name = "master_data")
@NamedQueries({ @NamedQuery(name = "getMasterDataByType", query = "select MDBO from MasterDataBO MDBO where MDBO.type =:type"), })
public class MasterDataBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "privacy_policy_text")
	private String privacyPolicyText;

	@Column(name = "terms_text")
	private String termsText;

	@Column(name = "type")
	private String type;

	public Integer getId() {
		return id;
	}

	public String getPrivacyPolicyText() {
		return privacyPolicyText;
	}

	public String getTermsText() {
		return termsText;
	}

	public String getType() {
		return type;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPrivacyPolicyText(String privacyPolicyText) {
		this.privacyPolicyText = privacyPolicyText;
	}

	public void setTermsText(String termsText) {
		this.termsText = termsText;
	}

	public void setType(String type) {
		this.type = type;
	}
}
