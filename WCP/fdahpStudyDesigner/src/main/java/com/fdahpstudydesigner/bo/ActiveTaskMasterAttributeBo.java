/*******************************************************************************
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" ,WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * The persistent class for the active_task_master_attribute database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "active_task_master_attribute")
public class ActiveTaskMasterAttributeBo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "add_to_dashboard")
	@Type(type = "yes_no")
	private boolean addToDashboard = false;

	@Column(name = "attribute_data_type")
	private String attributeDataType;

	@Column(name = "attribute_name")
	private String attributeName;

	@Column(name = "attribute_type")
	private String attributeType;

	@Column(name = "display_name")
	private String displayName;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "active_task_master_attr_id")
	private Integer masterId;

	@Column(name = "order_by")
	private Integer orderByTaskType;

	@Column(name = "task_type_id")
	private Integer taskTypeId;

	public String getAttributeDataType() {
		return attributeDataType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Integer getMasterId() {
		return masterId;
	}

	public Integer getOrderByTaskType() {
		return orderByTaskType;
	}

	public Integer getTaskTypeId() {
		return taskTypeId;
	}

	public boolean isAddToDashboard() {
		return addToDashboard;
	}

	public void setAddToDashboard(boolean addToDashboard) {
		this.addToDashboard = addToDashboard;
	}

	public void setAttributeDataType(String attributeDataType) {
		this.attributeDataType = attributeDataType;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setMasterId(Integer masterId) {
		this.masterId = masterId;
	}

	public void setOrderByTaskType(Integer orderByTaskType) {
		this.orderByTaskType = orderByTaskType;
	}

	public void setTaskTypeId(Integer taskTypeId) {
		this.taskTypeId = taskTypeId;
	}
}
