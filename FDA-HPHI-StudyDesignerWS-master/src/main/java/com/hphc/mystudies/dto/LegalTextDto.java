/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides application privacy and policy, terms of use information.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "legal_text")
public class LegalTextDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6256475646468023254L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "mobile_app_terms")
	private String mobileAppTerms;

	@Column(name = "mobile_app_terms_modified_datetime")
	private String mobileAppTermsModifiedDatetime;

	@Column(name = "mobile_app_privacy_policy")
	private String mobileAppPrivacyPolicy;

	@Column(name = "mobile_app_privacy_policy_modified_datetime")
	private String mobileAppPrivacyPolicyModifiedDatetime;

	@Column(name = "web_app_terms")
	private String webAppTerms;

	@Column(name = "web_app_terms_modified_datetime")
	private String webAppTermsModifiedDatetime;

	@Column(name = "web_app_privacy_policy")
	private String webAppPrivacyPolicy;

	@Column(name = "web_app_privacy_policy_modified_datetime")
	private String webAppPrivacyPolicyModifiedDatetime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMobileAppTerms() {
		return mobileAppTerms;
	}

	public void setMobileAppTerms(String mobileAppTerms) {
		this.mobileAppTerms = mobileAppTerms;
	}

	public String getMobileAppTermsModifiedDatetime() {
		return mobileAppTermsModifiedDatetime;
	}

	public void setMobileAppTermsModifiedDatetime(
			String mobileAppTermsModifiedDatetime) {
		this.mobileAppTermsModifiedDatetime = mobileAppTermsModifiedDatetime;
	}

	public String getMobileAppPrivacyPolicy() {
		return mobileAppPrivacyPolicy;
	}

	public void setMobileAppPrivacyPolicy(String mobileAppPrivacyPolicy) {
		this.mobileAppPrivacyPolicy = mobileAppPrivacyPolicy;
	}

	public String getMobileAppPrivacyPolicyModifiedDatetime() {
		return mobileAppPrivacyPolicyModifiedDatetime;
	}

	public void setMobileAppPrivacyPolicyModifiedDatetime(
			String mobileAppPrivacyPolicyModifiedDatetime) {
		this.mobileAppPrivacyPolicyModifiedDatetime = mobileAppPrivacyPolicyModifiedDatetime;
	}

	public String getWebAppTerms() {
		return webAppTerms;
	}

	public void setWebAppTerms(String webAppTerms) {
		this.webAppTerms = webAppTerms;
	}

	public String getWebAppTermsModifiedDatetime() {
		return webAppTermsModifiedDatetime;
	}

	public void setWebAppTermsModifiedDatetime(
			String webAppTermsModifiedDatetime) {
		this.webAppTermsModifiedDatetime = webAppTermsModifiedDatetime;
	}

	public String getWebAppPrivacyPolicy() {
		return webAppPrivacyPolicy;
	}

	public void setWebAppPrivacyPolicy(String webAppPrivacyPolicy) {
		this.webAppPrivacyPolicy = webAppPrivacyPolicy;
	}

	public String getWebAppPrivacyPolicyModifiedDatetime() {
		return webAppPrivacyPolicyModifiedDatetime;
	}

	public void setWebAppPrivacyPolicyModifiedDatetime(
			String webAppPrivacyPolicyModifiedDatetime) {
		this.webAppPrivacyPolicyModifiedDatetime = webAppPrivacyPolicyModifiedDatetime;
	}

}
