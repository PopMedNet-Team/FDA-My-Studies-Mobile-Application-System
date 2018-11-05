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
 * Provides line chart configuration details.
 * 
 * @author BTC
 *
 */
@Entity
@Table(name = "line_chart")
public class LineChartDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8093637693491035141L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "line_chartcol")
	private String lineChartcol;

	@Column(name = "no_data_text")
	private String noDataText;

	@Column(name = "show_ver_hor_line")
	private Integer showVerHorLine;

	@Column(name = "x_axis_color")
	private String xAxisColor;

	@Column(name = "y_axis_color")
	private String yAxisColor;

	@Column(name = "animation_needed")
	private Integer animationNeeded;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLineChartcol() {
		return lineChartcol;
	}

	public void setLineChartcol(String lineChartcol) {
		this.lineChartcol = lineChartcol;
	}

	public String getNoDataText() {
		return noDataText;
	}

	public void setNoDataText(String noDataText) {
		this.noDataText = noDataText;
	}

	public Integer getShowVerHorLine() {
		return showVerHorLine;
	}

	public void setShowVerHorLine(Integer showVerHorLine) {
		this.showVerHorLine = showVerHorLine;
	}

	public String getxAxisColor() {
		return xAxisColor;
	}

	public void setxAxisColor(String xAxisColor) {
		this.xAxisColor = xAxisColor;
	}

	public String getyAxisColor() {
		return yAxisColor;
	}

	public void setyAxisColor(String yAxisColor) {
		this.yAxisColor = yAxisColor;
	}

	public Integer getAnimationNeeded() {
		return animationNeeded;
	}

	public void setAnimationNeeded(Integer animationNeeded) {
		this.animationNeeded = animationNeeded;
	}

}
