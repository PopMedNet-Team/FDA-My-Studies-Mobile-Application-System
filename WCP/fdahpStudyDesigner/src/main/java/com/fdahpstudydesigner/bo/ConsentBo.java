package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the consent database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "consent")
@NamedQueries({
		@NamedQuery(name = "getConsentByStudyId", query = " From ConsentBo CBO WHERE CBO.studyId =:studyId order by CBO.createdOn DESC"),
		@NamedQuery(name = "updateStudyConsentVersion", query = "UPDATE ConsentBo SET live=2 WHERE customStudyId=:customStudyId and live=1"), })
public class ConsentBo implements Serializable {

	private static final long serialVersionUID = 5564057544960167010L;

	@Column(name = "aggrement_of_consent")
	private String aggrementOfTheConsent;

	@Column(name = "allow_without_permission")
	private String allowWithoutPermission = "No";

	@Transient
	private String comprehensionTest;

	@Column(name = "comprehension_test_minimum_score")
	private Integer comprehensionTestMinimumScore;

	@Column(name = "consent_doc_content")
	private String consentDocContent;

	@Column(name = "consent_doc_type")
	private String consentDocType;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "e_consent_agree")
	private String eConsentAgree = "Yes";

	@Column(name = "e_consent_datetime")
	private String eConsentDatetime = "Yes";

	@Column(name = "e_consent_firstname")
	private String eConsentFirstName = "Yes";

	@Column(name = "e_consent_lastname")
	private String eConsentLastName = "Yes";

	@Column(name = "e_consent_signature")
	private String eConsentSignature = "Yes";

	@Column(name = "html_consent")
	private String htmlConsent;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "learn_more_text")
	private String learnMoreText;

	@Column(name = "is_live")
	private Integer live = 0;

	@Column(name = "long_description")
	private String longDescription;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "need_comprehension_test")
	private String needComprehensionTest;

	@Column(name = "share_data_permissions")
	private String shareDataPermissions;

	@Column(name = "short_description")
	private String shortDescription;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "tagline_description")
	private String taglineDescription;

	@Column(name = "title")
	private String title;

	@Transient
	private String type;

	@Column(name = "version")
	private Float version = 0f;

	public String getAggrementOfTheConsent() {
		return aggrementOfTheConsent;
	}

	public String getAllowWithoutPermission() {
		return allowWithoutPermission;
	}

	public String getComprehensionTest() {
		return comprehensionTest;
	}

	public Integer getComprehensionTestMinimumScore() {
		return comprehensionTestMinimumScore;
	}

	public String getConsentDocContent() {
		return consentDocContent;
	}

	public String getConsentDocType() {
		return consentDocType;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public String geteConsentAgree() {
		return eConsentAgree;
	}

	public String geteConsentDatetime() {
		return eConsentDatetime;
	}

	public String geteConsentFirstName() {
		return eConsentFirstName;
	}

	public String geteConsentLastName() {
		return eConsentLastName;
	}

	public String geteConsentSignature() {
		return eConsentSignature;
	}

	public String getHtmlConsent() {
		return htmlConsent;
	}

	public Integer getId() {
		return id;
	}

	public String getLearnMoreText() {
		return learnMoreText;
	}

	public Integer getLive() {
		return live;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public String getNeedComprehensionTest() {
		return needComprehensionTest;
	}

	public String getShareDataPermissions() {
		return shareDataPermissions;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public String getTaglineDescription() {
		return taglineDescription;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public Float getVersion() {
		return version;
	}

	public void setAggrementOfTheConsent(String aggrementOfTheConsent) {
		this.aggrementOfTheConsent = aggrementOfTheConsent;
	}

	public void setAllowWithoutPermission(String allowWithoutPermission) {
		this.allowWithoutPermission = allowWithoutPermission;
	}

	public void setComprehensionTest(String comprehensionTest) {
		this.comprehensionTest = comprehensionTest;
	}

	public void setComprehensionTestMinimumScore(
			Integer comprehensionTestMinimumScore) {
		this.comprehensionTestMinimumScore = comprehensionTestMinimumScore;
	}

	public void setConsentDocContent(String consentDocContent) {
		this.consentDocContent = consentDocContent;
	}

	public void setConsentDocType(String consentDocType) {
		this.consentDocType = consentDocType;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void seteConsentAgree(String eConsentAgree) {
		this.eConsentAgree = eConsentAgree;
	}

	public void seteConsentDatetime(String eConsentDatetime) {
		this.eConsentDatetime = eConsentDatetime;
	}

	public void seteConsentFirstName(String eConsentFirstName) {
		this.eConsentFirstName = eConsentFirstName;
	}

	public void seteConsentLastName(String eConsentLastName) {
		this.eConsentLastName = eConsentLastName;
	}

	public void seteConsentSignature(String eConsentSignature) {
		this.eConsentSignature = eConsentSignature;
	}

	public void setHtmlConsent(String htmlConsent) {
		this.htmlConsent = htmlConsent;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLearnMoreText(String learnMoreText) {
		this.learnMoreText = learnMoreText;
	}

	public void setLive(Integer live) {
		this.live = live;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setNeedComprehensionTest(String needComprehensionTest) {
		this.needComprehensionTest = needComprehensionTest;
	}

	public void setShareDataPermissions(String shareDataPermissions) {
		this.shareDataPermissions = shareDataPermissions;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setTaglineDescription(String taglineDescription) {
		this.taglineDescription = taglineDescription;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVersion(Float version) {
		this.version = version;
	}

}
