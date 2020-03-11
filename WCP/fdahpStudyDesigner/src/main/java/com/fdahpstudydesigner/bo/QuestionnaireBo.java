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

/**
 * The persistent class for the questionnaires database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "questionnaires")
@NamedQueries({
		@NamedQuery(name = "QuestionnaireBo.findAll", query = "SELECT q FROM QuestionnaireBo q"),
		@NamedQuery(name = "getQuestionariesByStudyId", query = " From QuestionnaireBo QBO WHERE QBO.studyId =:studyId and QBO.active=1 order by QBO.createdDate DESC"),
		@NamedQuery(name = "checkQuestionnaireShortTitle", query = "From QuestionnaireBo QBO where QBO.studyId=:studyId and QBO.shortTitle=:shortTitle"),
		@NamedQuery(name = "getQuestionariesByStudyIdDone", query = " From QuestionnaireBo QBO WHERE QBO.studyId =:studyId and QBO.active=1 order by QBO.createdDate DESC"),
		@NamedQuery(name = "updateStudyQuestionnaireVersion", query = "UPDATE QuestionnaireBo SET live=2 WHERE customStudyId=:customStudyId and live=1"),
		@NamedQuery(name = "updateQuestionnaireStartDate", query = "update QuestionnaireBo SET studyLifetimeStart=:studyLifetimeStart where id=:id"), })
public class QuestionnaireBo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "branching")
	private Boolean branching = false;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date")
	private String createdDate;

	@Transient
	private String currentFrequency;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "day_of_the_week")
	private String dayOfTheWeek;

	@Column(name = "frequency")
	private String frequency;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "is_Change")
	private Integer isChange = 0;

	@Column(name = "is_live")
	private Integer live = 0;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date")
	private String modifiedDate;

	@Transient
	private String previousFrequency;

	@Transient
	private List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleBo = new ArrayList<>();

	@Transient
	private QuestionnairesFrequenciesBo questionnairesFrequenciesBo = new QuestionnairesFrequenciesBo();

	@Transient
	private List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList = new ArrayList<>();

	@Transient
	private String questionnarieVersion = "";

	@Column(name = "repeat_questionnaire")
	private Integer repeatQuestionnaire;

	@Column(name = "short_title")
	private String shortTitle;

	@Transient
	private Integer shortTitleDuplicate = 0;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "study_lifetime_end")
	private String studyLifetimeEnd;

	@Column(name = "study_lifetime_start")
	private String studyLifetimeStart;

	@Column(name = "title")
	private String title;

	@Transient
	private String type;

	@Column(name = "version")
	private Float version = 0f;
	
	@Column(name = "schedule_type")
	private String scheduleType = "";
	
	@Column(name = "anchor_date_id")
	private Integer anchorDateId;
	
	@Transient
	private boolean anchorQuestionnaireExist = false;

	public Boolean getActive() {
		return active;
	}

	public Boolean getBranching() {
		return branching;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getCurrentFrequency() {
		return currentFrequency;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public String getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public String getFrequency() {
		return this.frequency;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIsChange() {
		return isChange;
	}

	public Integer getLive() {
		return live;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getPreviousFrequency() {
		return previousFrequency;
	}

	public List<QuestionnaireCustomScheduleBo> getQuestionnaireCustomScheduleBo() {
		return questionnaireCustomScheduleBo;
	}

	public QuestionnairesFrequenciesBo getQuestionnairesFrequenciesBo() {
		return questionnairesFrequenciesBo;
	}

	public List<QuestionnairesFrequenciesBo> getQuestionnairesFrequenciesList() {
		return questionnairesFrequenciesList;
	}

	public String getQuestionnarieVersion() {
		return questionnarieVersion;
	}

	public Integer getRepeatQuestionnaire() {
		return repeatQuestionnaire;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public Integer getShortTitleDuplicate() {
		return shortTitleDuplicate;
	}

	public Boolean getStatus() {
		return status;
	}

	public Integer getStudyId() {
		return this.studyId;
	}

	public String getStudyLifetimeEnd() {
		return this.studyLifetimeEnd;
	}

	public String getStudyLifetimeStart() {
		return this.studyLifetimeStart;
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return type;
	}

	public Float getVersion() {
		return version;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setBranching(Boolean branching) {
		this.branching = branching;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setCurrentFrequency(String currentFrequency) {
		this.currentFrequency = currentFrequency;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public void setDayOfTheWeek(String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIsChange(Integer isChange) {
		this.isChange = isChange;
	}

	public void setLive(Integer live) {
		this.live = live;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setPreviousFrequency(String previousFrequency) {
		this.previousFrequency = previousFrequency;
	}

	public void setQuestionnaireCustomScheduleBo(
			List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleBo) {
		this.questionnaireCustomScheduleBo = questionnaireCustomScheduleBo;
	}

	public void setQuestionnairesFrequenciesBo(
			QuestionnairesFrequenciesBo questionnairesFrequenciesBo) {
		this.questionnairesFrequenciesBo = questionnairesFrequenciesBo;
	}

	public void setQuestionnairesFrequenciesList(
			List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList) {
		this.questionnairesFrequenciesList = questionnairesFrequenciesList;
	}

	public void setQuestionnarieVersion(String questionnarieVersion) {
		this.questionnarieVersion = questionnarieVersion;
	}

	public void setRepeatQuestionnaire(Integer repeatQuestionnaire) {
		this.repeatQuestionnaire = repeatQuestionnaire;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public void setShortTitleDuplicate(Integer shortTitleDuplicate) {
		this.shortTitleDuplicate = shortTitleDuplicate;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setStudyLifetimeEnd(String studyLifetimeEnd) {
		this.studyLifetimeEnd = studyLifetimeEnd;
	}

	public void setStudyLifetimeStart(String studyLifetimeStart) {
		this.studyLifetimeStart = studyLifetimeStart;
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

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Integer getAnchorDateId() {
		return anchorDateId;
	}

	public void setAnchorDateId(Integer anchorDateId) {
		this.anchorDateId = anchorDateId;
	}

	public boolean isAnchorQuestionnaireExist() {
		return anchorQuestionnaireExist;
	}

	public void setAnchorQuestionnaireExist(boolean anchorQuestionnaireExist) {
		this.anchorQuestionnaireExist = anchorQuestionnaireExist;
	}
}