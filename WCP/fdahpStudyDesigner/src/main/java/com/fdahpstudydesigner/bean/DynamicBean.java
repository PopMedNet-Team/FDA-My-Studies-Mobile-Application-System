package com.fdahpstudydesigner.bean;

public class DynamicBean {

	private String dateTime;

	private String time;

	public DynamicBean(String dateTime, String time) {
		this.dateTime = dateTime;
		this.time = time;
	}

	public String getDateTime() {
		return dateTime;
	}

	public String getTime() {
		return time;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
