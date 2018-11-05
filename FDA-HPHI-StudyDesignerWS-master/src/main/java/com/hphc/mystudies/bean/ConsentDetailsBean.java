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
 * Provides brief description about the consent like version of consent,
 * comprehension test details {@link ComprehensionDetailsBean}, sharing details
 * {@link SharingBean} and consent review details {@link ReviewBean}
 * 
 * @author BTC
 *
 */
public class ConsentDetailsBean {

	private String version = "";
	private List<ConsentBean> visualScreens = new ArrayList<>();
	private ComprehensionDetailsBean comprehension = new ComprehensionDetailsBean();
	private SharingBean sharing = new SharingBean();
	private ReviewBean review = new ReviewBean();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<ConsentBean> getVisualScreens() {
		return visualScreens;
	}

	public void setVisualScreens(List<ConsentBean> visualScreens) {
		this.visualScreens = visualScreens;
	}

	public ComprehensionDetailsBean getComprehension() {
		return comprehension;
	}

	public void setComprehension(ComprehensionDetailsBean comprehension) {
		this.comprehension = comprehension;
	}

	public SharingBean getSharing() {
		return sharing;
	}

	public void setSharing(SharingBean sharing) {
		this.sharing = sharing;
	}

	public ReviewBean getReview() {
		return review;
	}

	public void setReview(ReviewBean review) {
		this.review = review;
	}

}
