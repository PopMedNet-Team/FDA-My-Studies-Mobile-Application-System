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
 * Provides activity details like activity identifier, activity version,
 * activity title, activity type, frequency details
 * {@link ActivityFrequencyBean}, start and end time of activity etc
 * 
 * @author BTC
 *
 */
public class ActivitiesBean {

	private String activityId = "";
	private String activityVersion = "";
	private String title = "";
	private String type = "";
	private String startTime = "";
	private String endTime = "";
	private Boolean branching = false;
	private String lastModified = "";
	private String state = "";
	private String taskSubType = "";
	private String schedulingType = "";
	private ActivityAnchorDateBean anchorDate = new ActivityAnchorDateBean();
	private ActivityFrequencyBean frequency = new ActivityFrequencyBean();

	public String getActivityId() {
		return activityId;
	}

	public String getTaskSubType() {
		return taskSubType;
	}

	public void setTaskSubType(String taskSubType) {
		this.taskSubType = taskSubType;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityVersion() {
		return activityVersion;
	}

	public void setActivityVersion(String activityVersion) {
		this.activityVersion = activityVersion;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Boolean getBranching() {
		return branching;
	}

	public void setBranching(Boolean branching) {
		this.branching = branching;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ActivityFrequencyBean getFrequency() {
		return frequency;
	}

	public void setFrequency(ActivityFrequencyBean frequency) {
		this.frequency = frequency;
	}

	public String getSchedulingType() {
		return schedulingType;
	}

	public void setSchedulingType(String schedulingType) {
		this.schedulingType = schedulingType;
	}

	public ActivityAnchorDateBean getAnchorDate() {
		return anchorDate;
	}

	public void setAnchorDate(ActivityAnchorDateBean anchorDate) {
		this.anchorDate = anchorDate;
	}
}
