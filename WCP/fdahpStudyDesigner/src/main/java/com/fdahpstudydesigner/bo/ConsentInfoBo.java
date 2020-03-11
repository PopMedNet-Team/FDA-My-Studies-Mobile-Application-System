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
 * The persistent class for the consent_info database table.
 * 
 * @author BTC
 */
@Entity
@Table(name = "consent_info")
@NamedQueries({
		@NamedQuery(name = "getConsentInfoByStudyId", query = " From ConsentInfoBo CBO WHERE CBO.studyId =:studyId and CBO.active=1 order by CBO.createdOn DESC"),
		@NamedQuery(name = "updateStudyConsentInfoVersion", query = "UPDATE ConsentInfoBo SET live=2 WHERE customStudyId=:customStudyId and live=1"), })
public class ConsentInfoBo implements Serializable {
	private static final long serialVersionUID = 7994683067825219315L;

	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "brief_summary")
	private String briefSummary;

	@Column(name = "consent_item_title_id")
	private Integer consentItemTitleId;

	@Column(name = "consent_item_type")
	private String consentItemType;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "display_title")
	private String displayTitle;

	@Column(name = "elaborated")
	private String elaborated;

	@Column(name = "html_content")
	private String htmlContent;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "is_live")
	private Integer live = 0;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "study_id")
	private Integer studyId;

	@Transient
	private String type;

	@Column(name = "url")
	private String url;

	@Column(name = "version")
	private Float version = 0f;

	@Column(name = "visual_step")
	private String visualStep;

	public Boolean getActive() {
		return active;
	}

	public String getBriefSummary() {
		return briefSummary;
	}

	public Integer getConsentItemTitleId() {
		return consentItemTitleId;
	}

	public String getConsentItemType() {
		return consentItemType;
	}

	public String getContentType() {
		return contentType;
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

	public String getDisplayTitle() {
		return displayTitle;
	}

	public String getElaborated() {
		return elaborated;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public Integer getId() {
		return id;
	}

	public Integer getLive() {
		return live;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public Boolean getStatus() {
		return status;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public Float getVersion() {
		return version;
	}

	public String getVisualStep() {
		return visualStep;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setBriefSummary(String briefSummary) {
		this.briefSummary = briefSummary;
	}

	public void setConsentItemTitleId(Integer consentItemTitleId) {
		this.consentItemTitleId = consentItemTitleId;
	}

	public void setConsentItemType(String consentItemType) {
		this.consentItemType = consentItemType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
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

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setElaborated(String elaborated) {
		this.elaborated = elaborated;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLive(Integer live) {
		this.live = live;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setVersion(Float version) {
		this.version = version;
	}

	public void setVisualStep(String visualStep) {
		this.visualStep = visualStep;
	}
}
