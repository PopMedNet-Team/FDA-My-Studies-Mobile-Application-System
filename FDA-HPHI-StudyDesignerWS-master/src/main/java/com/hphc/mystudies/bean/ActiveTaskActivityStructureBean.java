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

import java.util.ArrayList;
import java.util.List;

/**
 * Provides active task details. i.e. type of activity, metadata information
 * {@link ActivityMetadataBean} and steps details
 * {@link ActiveTaskActivityStepsBean}.
 * 
 * @author BTC
 *
 */
public class ActiveTaskActivityStructureBean {

	private String type = "";
	private ActivityMetadataBean metadata = new ActivityMetadataBean();
	private List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ActivityMetadataBean getMetadata() {
		return metadata;
	}

	public void setMetadata(ActivityMetadataBean metadata) {
		this.metadata = metadata;
	}

	public List<ActiveTaskActivityStepsBean> getSteps() {
		return steps;
	}

	public void setSteps(List<ActiveTaskActivityStepsBean> steps) {
		this.steps = steps;
	}

}
