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

/**
 * Provide active task selection details.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "active_task_select_options")
public class ActiveTaskSelectOptionsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3788580522549249379L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "active_task_select_options_id")
	private Integer activeTaskSelectOptionsId;

	@Column(name = "active_task_master_attr_id")
	private Integer activeTaskMasterAttrId;

	@Column(name = "option_val")
	private String optionVal;

	public Integer getActiveTaskSelectOptionsId() {
		return activeTaskSelectOptionsId;
	}

	public void setActiveTaskSelectOptionsId(Integer activeTaskSelectOptionsId) {
		this.activeTaskSelectOptionsId = activeTaskSelectOptionsId;
	}

	public Integer getActiveTaskMasterAttrId() {
		return activeTaskMasterAttrId;
	}

	public void setActiveTaskMasterAttrId(Integer activeTaskMasterAttrId) {
		this.activeTaskMasterAttrId = activeTaskMasterAttrId;
	}

	public String getOptionVal() {
		return optionVal;
	}

	public void setOptionVal(String optionVal) {
		this.optionVal = optionVal;
	}

}
