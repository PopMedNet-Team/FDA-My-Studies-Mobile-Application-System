package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.fdahpstudydesigner.bean.StudyListBean;

/**
 * The persistent class for the studies database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "studies")
@NamedQueries({
		@NamedQuery(name = "StudyBo.getStudiesById", query = " From StudyBo SBO WHERE SBO.id =:id"),
		@NamedQuery(name = "updateStudyVersion", query = "UPDATE StudyBo SET live=2 WHERE customStudyId=:customStudyId and live=1"),
		@NamedQuery(name = "getStudyLiveVersion", query = " From StudyBo SBO WHERE SBO.live=1 AND customStudyId=:customStudyId"),
		@NamedQuery(name = "StudyBo.getStudyBycustomStudyId", query = " From StudyBo SBO WHERE customStudyId=:customStudyId"),
		@NamedQuery(name = "getStudyDraftVersion", query = " From StudyBo SBO WHERE SBO.live IN (0,2) AND customStudyId=:customStudyId"), })
public class StudyBo implements Serializable {

	private static final long serialVersionUID = 2147840266295837728L;

	@Column(name = "allow_rejoin")
	private String allowRejoin;

	@Column(name = "allow_rejoin_text")
	private String allowRejoinText;

	@Transient
	private String buttonText;

	@Column(name = "category")
	private String category;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "data_partner")
	private String dataPartner;

	@Column(name = "description")
	private String description;

	@Column(name = "enrolling_participants")
	private String enrollingParticipants;

	@Transient
	private MultipartFile file;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "has_activitetask_draft")
	private Integer hasActivetaskDraft = 0;

	@Column(name = "has_activity_draft")
	private Integer hasActivityDraft = 0;

	@Column(name = "has_consent_draft")
	private Integer hasConsentDraft = 0;

	@Column(name = "has_questionnaire_draft")
	private Integer hasQuestionnaireDraft = 0;

	@Column(name = "has_study_draft")
	private Integer hasStudyDraft = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "inbox_email_address")
	private String inboxEmailAddress;

	@Column(name = "irb_review")
	private String irbReview;

	@Column(name = "is_live")
	private Integer live = 0;

	@Transient
	private StudyBo liveStudyBo = null;

	@Column(name = "media_link")
	private String mediaLink;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "name")
	private String name;

	@Column(name = "platform")
	private String platform;

	@Column(name = "research_sponsor")
	private String researchSponsor;

	@Column(name = "retain_participant")
	private String retainParticipant;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	@Column(name = "status")
	private String status;

	@Column(name = "study_lunched_date")
	private String studylunchDate;

	@Transient
	private List<StudyListBean> studyPermissions = new ArrayList<>();

	@Column(name = "study_pre_active_flag")
	@Type(type = "yes_no")
	private boolean studyPreActiveFlag = false;

	@Transient
	StudySequenceBo studySequenceBo = new StudySequenceBo();

	@Column(name = "study_tagline")
	private String studyTagLine;

	@Transient
	private StudyVersionBo studyVersionBo = null;

	@Column(name = "study_website")
	private String studyWebsite;

	@Column(name = "tentative_duration")
	private Integer tentativeDuration;

	@Column(name = "tentative_duration_weekmonth")
	private String tentativeDurationWeekmonth;

	@Column(name = "thumbnail_image")
	private String thumbnailImage;

	@Column(name = "type")
	private String type;

	@Transient
	private Integer userId;

	@Column(name = "version")
	private Float version = 0f;

	@Transient
	private boolean viewPermission = true;
	
	@Column(name = "enrollmentdate_as_anchordate")
	@Type(type = "yes_no")
	private boolean enrollmentdateAsAnchordate = false;
	
	@Column(name = "app_id")
	private String appId;
	
	@Column(name = "org_id")
	private String orgId = "OrgName";

	public String getAllowRejoin() {
		return allowRejoin;
	}

	public String getAllowRejoinText() {
		return allowRejoinText;
	}

	public String getButtonText() {
		return buttonText;
	}

	public String getCategory() {
		return category;
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

	public String getDataPartner() {
		return dataPartner;
	}

	public String getDescription() {
		return description;
	}

	public String getEnrollingParticipants() {
		return enrollingParticipants;
	}

	public MultipartFile getFile() {
		return file;
	}

	public String getFullName() {
		return fullName;
	}

	public Integer getHasActivetaskDraft() {
		return hasActivetaskDraft;
	}

	public Integer getHasActivityDraft() {
		return hasActivityDraft;
	}

	public Integer getHasConsentDraft() {
		return hasConsentDraft;
	}

	public Integer getHasQuestionnaireDraft() {
		return hasQuestionnaireDraft;
	}

	public Integer getHasStudyDraft() {
		return hasStudyDraft;
	}

	public Integer getId() {
		return id;
	}

	public String getInboxEmailAddress() {
		return inboxEmailAddress;
	}

	public String getIrbReview() {
		return irbReview;
	}

	public Integer getLive() {
		return live;
	}

	public StudyBo getLiveStudyBo() {
		return liveStudyBo;
	}

	public String getMediaLink() {
		return mediaLink;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public String getName() {
		return name;
	}

	public String getPlatform() {
		return platform;
	}

	public String getResearchSponsor() {
		return researchSponsor;
	}

	public String getRetainParticipant() {
		return retainParticipant;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public String getStatus() {
		return status;
	}

	public String getStudylunchDate() {
		return studylunchDate;
	}

	public List<StudyListBean> getStudyPermissions() {
		return studyPermissions;
	}

	public StudySequenceBo getStudySequenceBo() {
		return studySequenceBo;
	}

	public String getStudyTagLine() {
		return studyTagLine;
	}

	public StudyVersionBo getStudyVersionBo() {
		return studyVersionBo;
	}

	public String getStudyWebsite() {
		return studyWebsite;
	}

	public Integer getTentativeDuration() {
		return tentativeDuration;
	}

	public String getTentativeDurationWeekmonth() {
		return tentativeDurationWeekmonth;
	}

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public String getType() {
		return type;
	}

	public Integer getUserId() {
		return userId;
	}

	public Float getVersion() {
		return version;
	}

	public boolean isStudyPreActiveFlag() {
		return studyPreActiveFlag;
	}

	public boolean isViewPermission() {
		return viewPermission;
	}

	public void setAllowRejoin(String allowRejoin) {
		this.allowRejoin = allowRejoin;
	}

	public void setAllowRejoinText(String allowRejoinText) {
		this.allowRejoinText = allowRejoinText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public void setDataPartner(String dataPartner) {
		this.dataPartner = dataPartner;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEnrollingParticipants(String enrollingParticipants) {
		this.enrollingParticipants = enrollingParticipants;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setHasActivetaskDraft(Integer hasActivetaskDraft) {
		this.hasActivetaskDraft = hasActivetaskDraft;
	}

	public void setHasActivityDraft(Integer hasActivityDraft) {
		this.hasActivityDraft = hasActivityDraft;
	}

	public void setHasConsentDraft(Integer hasConsentDraft) {
		this.hasConsentDraft = hasConsentDraft;
	}

	public void setHasQuestionnaireDraft(Integer hasQuestionnaireDraft) {
		this.hasQuestionnaireDraft = hasQuestionnaireDraft;
	}

	public void setHasStudyDraft(Integer hasStudyDraft) {
		this.hasStudyDraft = hasStudyDraft;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setInboxEmailAddress(String inboxEmailAddress) {
		this.inboxEmailAddress = inboxEmailAddress;
	}

	public void setIrbReview(String irbReview) {
		this.irbReview = irbReview;
	}

	public void setLive(Integer live) {
		this.live = live;
	}

	public void setLiveStudyBo(StudyBo liveStudyBo) {
		this.liveStudyBo = liveStudyBo;
	}

	public void setMediaLink(String mediaLink) {
		this.mediaLink = mediaLink;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public void setResearchSponsor(String researchSponsor) {
		this.researchSponsor = researchSponsor;
	}

	public void setRetainParticipant(String retainParticipant) {
		this.retainParticipant = retainParticipant;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStudylunchDate(String studylunchDate) {
		this.studylunchDate = studylunchDate;
	}

	public void setStudyPermissions(List<StudyListBean> studyPermissions) {
		this.studyPermissions = studyPermissions;
	}

	public void setStudyPreActiveFlag(boolean studyPreActiveFlag) {
		this.studyPreActiveFlag = studyPreActiveFlag;
	}

	public void setStudySequenceBo(StudySequenceBo studySequenceBo) {
		this.studySequenceBo = studySequenceBo;
	}

	public void setStudyTagLine(String studyTagLine) {
		this.studyTagLine = studyTagLine;
	}

	public void setStudyVersionBo(StudyVersionBo studyVersionBo) {
		this.studyVersionBo = studyVersionBo;
	}

	public void setStudyWebsite(String studyWebsite) {
		this.studyWebsite = studyWebsite;
	}

	public void setTentativeDuration(Integer tentativeDuration) {
		this.tentativeDuration = tentativeDuration;
	}

	public void setTentativeDurationWeekmonth(String tentativeDurationWeekmonth) {
		this.tentativeDurationWeekmonth = tentativeDurationWeekmonth;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setVersion(Float version) {
		this.version = version;
	}

	public void setViewPermission(boolean viewPermission) {
		this.viewPermission = viewPermission;
	}

	public boolean isEnrollmentdateAsAnchordate() {
		return enrollmentdateAsAnchordate;
	}

	public void setEnrollmentdateAsAnchordate(boolean enrollmentdateAsAnchordate) {
		this.enrollmentdateAsAnchordate = enrollmentdateAsAnchordate;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
}
