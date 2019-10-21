package com.fdahpstudydesigner.bean;

public class DynamicFrequencyBean {

	String startDate;
	String time;

	public DynamicFrequencyBean(String startDate, String time) {
		super();
		this.startDate = startDate;
		this.time = time;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getTime() {
		return time;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
