package com.fdahpstudydesigner.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the statistic_master_images database table.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "statistic_master_images")
public class StatisticImageListBo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "statistic_image_id")
	private Integer statisticImageId;

	@Column(name = "value")
	private String value;

	public Integer getStatisticImageId() {
		return statisticImageId;
	}

	public String getValue() {
		return value;
	}

	public void setStatisticImageId(Integer statisticImageId) {
		this.statisticImageId = statisticImageId;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
