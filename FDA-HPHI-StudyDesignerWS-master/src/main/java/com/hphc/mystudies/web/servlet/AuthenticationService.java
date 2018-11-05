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
package com.hphc.mystudies.web.servlet;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hphc.mystudies.util.StudyMetaDataUtil;
import com.sun.jersey.core.util.Base64;

/**
 * Provides authentication to check requesting user is authorized to access
 * data.
 * 
 * @author BTC
 *
 */
public class AuthenticationService {

	public static final Logger LOGGER = Logger
			.getLogger(AuthenticationService.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> authPropMap = StudyMetaDataUtil
			.getAuthorizationProperties();

	/**
	 * Authenticate the provided authorization credentials
	 * 
	 * @author BTC
	 * @param authCredentials
	 *            the Basic Authorization
	 * @return {@link Boolean}
	 */
	public boolean authenticate(String authCredentials) {
		LOGGER.info("INFO: AuthenticationService - authenticate() - Starts");
		boolean authenticationStatus = false;
		String bundleIdAndAppToken = null;
		try {
			if (StringUtils.isNotEmpty(authCredentials)
					&& authCredentials.contains("Basic")) {
				final String encodedUserPassword = authCredentials
						.replaceFirst("Basic" + " ", "");
				byte[] decodedBytes = Base64.decode(encodedUserPassword);
				bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
				if (bundleIdAndAppToken.contains(":")) {
					final StringTokenizer tokenizer = new StringTokenizer(
							bundleIdAndAppToken, ":");
					final String bundleId = tokenizer.nextToken();
					final String appToken = tokenizer.nextToken();
					if (authPropMap.containsKey(bundleId)
							&& authPropMap.containsKey(appToken)) {
						authenticationStatus = true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("AuthenticationService - authenticate() :: ERROR", e);
			return authenticationStatus;
		}
		LOGGER.info("INFO: AuthenticationService - authenticate() - Ends");
		return authenticationStatus;
	}
}
