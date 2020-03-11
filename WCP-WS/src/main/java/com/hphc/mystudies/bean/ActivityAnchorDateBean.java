package com.hphc.mystudies.bean;

public class ActivityAnchorDateBean {

	private String sourceType = "";
	private String sourceActivityId = "";
	private String sourceKey = "";
	private String sourceFormKey = "";
	private ActivityAnchorStartBean start = new ActivityAnchorStartBean();
	private ActivityAnchorEndBean end = new ActivityAnchorEndBean();
	
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getSourceActivityId() {
		return sourceActivityId;
	}
	public void setSourceActivityId(String sourceActivityId) {
		this.sourceActivityId = sourceActivityId;
	}
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public ActivityAnchorStartBean getStart() {
		return start;
	}
	public void setStart(ActivityAnchorStartBean start) {
		this.start = start;
	}
	public ActivityAnchorEndBean getEnd() {
		return end;
	}
	public void setEnd(ActivityAnchorEndBean end) {
		this.end = end;
	}
	public String getSourceFormKey() {
		return sourceFormKey;
	}
	public void setSourceFormKey(String sourceFormKey) {
		this.sourceFormKey = sourceFormKey;
	}
}
