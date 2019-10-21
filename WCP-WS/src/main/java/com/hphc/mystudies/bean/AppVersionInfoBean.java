package com.hphc.mystudies.bean;

public class AppVersionInfoBean {

	private DeviceVersion android;
	private DeviceVersion ios;

	public DeviceVersion getAndroid() {
		return android;
	}

	public void setAndroid(DeviceVersion android) {
		this.android = android;
	}

	public DeviceVersion getIos() {
		return ios;
	}

	public void setIos(DeviceVersion ios) {
		this.ios = ios;
	}

}
