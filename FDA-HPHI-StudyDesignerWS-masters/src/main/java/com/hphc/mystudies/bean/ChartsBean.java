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
package com.hphc.mystudies.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides chart details i.e. configuration information, chart datasource
 * information {@link ChartDataSourceBean} etc
 * 
 * @author BTC
 *
 */
public class ChartsBean {

	private String title = "";
	private String displayName = "";
	private String type = "";
	private Map<String, Object> configuration = new HashMap<>();
	private boolean scrollable = true;
	private ChartDataSourceBean dataSource = new ChartDataSourceBean();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public ChartDataSourceBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(ChartDataSourceBean dataSource) {
		this.dataSource = dataSource;
	}

}
