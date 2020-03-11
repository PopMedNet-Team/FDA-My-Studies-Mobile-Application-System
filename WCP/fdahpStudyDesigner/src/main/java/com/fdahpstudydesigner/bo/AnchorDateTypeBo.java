package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the anchordatetype added by admin database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "anchordate_type")
public class AnchorDateTypeBo implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "custom_study_id")
	private String customStudyId;
	
	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "name")
	private String name;
	
	//0-not used and 1- used
	@Column(name = "has_anchortype_draft")
	private Integer hasAnchortypeDraft = 0;
	
	@Column(name = "version")
	private Float version = 0f;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getHasAnchortypeDraft() {
		return hasAnchortypeDraft;
	}

	public void setHasAnchortypeDraft(Integer hasAnchortypeDraft) {
		this.hasAnchortypeDraft = hasAnchortypeDraft;
	}

	public Float getVersion() {
		return version;
	}

	public void setVersion(Float version) {
		this.version = version;
	}
}
