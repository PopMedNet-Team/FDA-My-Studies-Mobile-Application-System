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

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Provides app versions detais.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "app_versions")
@NamedQueries({

		@NamedQuery(name = "AppVersionDto.findByBundleIdOsType", query = "FROM AppVersionDto AVDTO"
				+ " WHERE AVDTO.bundleId= :bundleId AND AVDTO.osType= :osType"
				+ " ORDER BY AVDTO.avId DESC"),

		@NamedQuery(name = "AppVersionDto.findByBundleIdOsTypeAppVersion", query = "FROM AppVersionDto AVDTO"
				+ " WHERE AVDTO.bundleId= :bundleId AND AVDTO.osType= :osType"
				+ " ORDER BY AVDTO.appVersion DESC"), })
public class AppVersionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2555323540993364916L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "av_id")
	private Integer avId;

	@Column(name = "force_update")
	private Integer forceUpdate = 1;

	@Column(name = "os_type")
	private String osType;

	@Column(name = "app_version")
	private Float appVersion = 1f;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "bundle_id")
	private String bundleId;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "message")
	private String message;

	public Integer getAvId() {
		return avId;
	}

	public void setAvId(Integer avId) {
		this.avId = avId;
	}

	public Integer getForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(Integer forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public Float getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(Float appVersion) {
		this.appVersion = appVersion;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
