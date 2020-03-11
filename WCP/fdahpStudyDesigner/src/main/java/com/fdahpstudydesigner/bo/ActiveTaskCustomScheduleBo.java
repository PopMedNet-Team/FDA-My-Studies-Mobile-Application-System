package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * The persistent class for the active_task_custom_frequencies database table.
 *
 */
@Entity
@Table(name = "active_task_custom_frequencies")
@NamedQuery(name = "ActiveTaskCustomScheduleBo.findAll", query = "SELECT a FROM ActiveTaskCustomScheduleBo a")
public class ActiveTaskCustomScheduleBo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "active_task_id")
	private Integer activeTaskId;

	@Column(name = "frequency_end_date")
	private String frequencyEndDate;

	@Column(name = "frequency_start_date")
	private String frequencyStartDate;

	@Column(name = "frequency_time")
	private String frequencyTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "is_used")
	@Type(type = "yes_no")
	private boolean used = false;
	
	@Column(name = "x_days_sign", length = 1)
	private boolean xDaysSign = false;

	@Column(name = "y_days_sign", length = 1)
	private boolean yDaysSign = false;
	
	@Column(name = "time_period_from_days")
	private Integer timePeriodFromDays;

	@Column(name = "time_period_to_days")
	private Integer timePeriodToDays;

	public ActiveTaskCustomScheduleBo() {
		// Do nothing
	}

	public Integer getActiveTaskId() {
		return this.activeTaskId;
	}

	public String getFrequencyEndDate() {
		return this.frequencyEndDate;
	}

	public String getFrequencyStartDate() {
		return this.frequencyStartDate;
	}

	public String getFrequencyTime() {
		return this.frequencyTime;
	}

	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the used
	 */
	public boolean isUsed() {
		return used;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public void setFrequencyEndDate(String frequencyEndDate) {
		this.frequencyEndDate = frequencyEndDate;
	}

	public void setFrequencyStartDate(String frequencyStartDate) {
		this.frequencyStartDate = frequencyStartDate;
	}

	public void setFrequencyTime(String frequencyTime) {
		this.frequencyTime = frequencyTime;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param used
	 *            the used to set
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isxDaysSign() {
		return xDaysSign;
	}

	public void setxDaysSign(boolean xDaysSign) {
		this.xDaysSign = xDaysSign;
	}

	public boolean isyDaysSign() {
		return yDaysSign;
	}

	public void setyDaysSign(boolean yDaysSign) {
		this.yDaysSign = yDaysSign;
	}

	public Integer getTimePeriodFromDays() {
		return timePeriodFromDays;
	}

	public void setTimePeriodFromDays(Integer timePeriodFromDays) {
		this.timePeriodFromDays = timePeriodFromDays;
	}

	public Integer getTimePeriodToDays() {
		return timePeriodToDays;
	}

	public void setTimePeriodToDays(Integer timePeriodToDays) {
		this.timePeriodToDays = timePeriodToDays;
	}
	
	

}