package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the study_page database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "study_page")
public class StudyPageBo implements Serializable {

	private static final long serialVersionUID = 3736160119532905474L;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "description")
	private String description;

	@Column(name = "image_path")
	private String imagePath;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "page_id")
	private Integer pageId;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "title")
	private String title;

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public String getDescription() {
		return description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public Integer getPageId() {
		return pageId;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public String getTitle() {
		return title;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
