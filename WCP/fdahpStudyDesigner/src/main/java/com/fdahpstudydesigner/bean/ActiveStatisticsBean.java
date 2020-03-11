package com.fdahpstudydesigner.bean;

public class ActiveStatisticsBean {

	String dbVal = "";
	String id = "";
	String idname = "";
	String idVal = "";
	boolean type = false;

	public String getDbVal() {
		return dbVal;
	}

	public String getId() {
		return id;
	}

	public String getIdname() {
		return idname;
	}

	public String getIdVal() {
		return idVal;
	}

	public boolean isType() {
		return type;
	}

	public void setDbVal(String dbVal) {
		this.dbVal = dbVal;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdname(String idname) {
		this.idname = idname;
	}

	public void setIdVal(String idVal) {
		this.idVal = idVal;
	}

	public void setType(boolean type) {
		this.type = type;
	}
}
