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
package com.hphc.mystudies.bean;

/**
 * @author BTC
 *
 */
public class StudySessionBean {
	private String isLive;
	private String permission;
	private Integer sessionStudyCount;
	private String studyId;

	/**
	 * @return the isLive
	 */
	public String getIsLive() {
		return isLive;
	}

	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * @return the sessionStudyCount
	 */
	public Integer getSessionStudyCount() {
		return sessionStudyCount;
	}

	/**
	 * @return the studyId
	 */
	public String getStudyId() {
		return studyId;
	}

	/**
	 * @param isLive
	 *            the isLive to set
	 */
	public void setIsLive(String isLive) {
		this.isLive = isLive;
	}

	/**
	 * @param permission
	 *            the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * @param sessionStudyCount
	 *            the sessionStudyCount to set
	 */
	public void setSessionStudyCount(Integer sessionStudyCount) {
		this.sessionStudyCount = sessionStudyCount;
	}

	/**
	 * @param studyId
	 *            the studyId to set
	 */
	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

}
