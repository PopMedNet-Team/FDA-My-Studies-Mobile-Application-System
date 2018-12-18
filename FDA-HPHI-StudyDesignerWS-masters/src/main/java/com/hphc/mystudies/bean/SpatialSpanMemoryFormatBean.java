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

/**
 * Provides spatial span memory active task metadata details.
 * 
 * @author BTC
 *
 */
public class SpatialSpanMemoryFormatBean {

	private Integer initialSpan = 0;
	private Integer minimumSpan = 0;
	private Integer maximumSpan = 0;
	private Float playSpeed = 0F;
	private Integer maximumTests = 0;
	private Integer maximumConsecutiveFailures = 0;
	private String customTargetImage = "";
	private String customTargetPluralName = "";
	private boolean requireReversal = false;

	public Integer getInitialSpan() {
		return initialSpan;
	}

	public void setInitialSpan(Integer initialSpan) {
		this.initialSpan = initialSpan;
	}

	public Integer getMinimumSpan() {
		return minimumSpan;
	}

	public void setMinimumSpan(Integer minimumSpan) {
		this.minimumSpan = minimumSpan;
	}

	public Integer getMaximumSpan() {
		return maximumSpan;
	}

	public void setMaximumSpan(Integer maximumSpan) {
		this.maximumSpan = maximumSpan;
	}

	public Float getPlaySpeed() {
		return playSpeed;
	}

	public void setPlaySpeed(Float playSpeed) {
		this.playSpeed = playSpeed;
	}

	public Integer getMaximumTests() {
		return maximumTests;
	}

	public void setMaximumTests(Integer maximumTests) {
		this.maximumTests = maximumTests;
	}

	public Integer getMaximumConsecutiveFailures() {
		return maximumConsecutiveFailures;
	}

	public void setMaximumConsecutiveFailures(Integer maximumConsecutiveFailures) {
		this.maximumConsecutiveFailures = maximumConsecutiveFailures;
	}

	public String getCustomTargetImage() {
		return customTargetImage;
	}

	public void setCustomTargetImage(String customTargetImage) {
		this.customTargetImage = customTargetImage;
	}

	public String getCustomTargetPluralName() {
		return customTargetPluralName;
	}

	public void setCustomTargetPluralName(String customTargetPluralName) {
		this.customTargetPluralName = customTargetPluralName;
	}

	public boolean isRequireReversal() {
		return requireReversal;
	}

	public void setRequireReversal(boolean requireReversal) {
		this.requireReversal = requireReversal;
	}

}
