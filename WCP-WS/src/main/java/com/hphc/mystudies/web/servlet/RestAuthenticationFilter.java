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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hphc.mystudies.exception.ErrorCodes;
import com.hphc.mystudies.util.StudyMetaDataConstants;

/**
 * Implements {@link Filter} interface to filter the incoming requests.
 * 
 * @author BTC
 * @see javax.servlet.Filter
 */
public class RestAuthenticationFilter implements Filter {

	public static final Logger LOGGER = Logger
			.getLogger(RestAuthenticationFilter.class);
	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		LOGGER.info("INFO: RestAuthenticationFilter - doFilter() - Starts");
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			String authCredentials = httpServletRequest
					.getHeader(AUTHENTICATION_HEADER);

			if (StringUtils.isNotEmpty(authCredentials)) {
				AuthenticationService authenticationService = new AuthenticationService();
				boolean authenticationStatus = authenticationService.authenticate(authCredentials);
				//boolean authenticationStatus = true;
				if (authenticationStatus) {
					filter.doFilter(request, response);
				} else {
					if (response instanceof HttpServletResponse) {
						HttpServletResponse httpServletResponse = (HttpServletResponse) response;
						httpServletResponse
								.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						httpServletResponse.setHeader("status",
								ErrorCodes.STATUS_103);
						httpServletResponse.setHeader("title",
								ErrorCodes.INVALID_AUTHORIZATION);
						httpServletResponse.setHeader("StatusMessage",
								StudyMetaDataConstants.INVALID_AUTHORIZATION);
					}
				}
			} else if (StudyMetaDataConstants.INTERCEPTOR_URL_PING
					.equalsIgnoreCase(httpServletRequest.getPathInfo())
					|| StudyMetaDataConstants.INTERCEPTOR_URL_MAIL
							.equalsIgnoreCase(httpServletRequest.getPathInfo())
					|| StudyMetaDataConstants.INTERCEPTOR_URL_APP_VERSION
							.equalsIgnoreCase(httpServletRequest.getPathInfo())
					|| StudyMetaDataConstants.INTERCEPTOR_URL_DB_QUERY
							.equalsIgnoreCase(httpServletRequest.getPathInfo())) {
				filter.doFilter(request, response);
			} else {
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse
						.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				httpServletResponse.setHeader("status", ErrorCodes.STATUS_102);
				httpServletResponse
						.setHeader("title", ErrorCodes.INVALID_INPUT);
				httpServletResponse.setHeader("StatusMessage",
						StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG);
			}
		}
		LOGGER.info("INFO: RestAuthenticationFilter - doFilter() - Ends");
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
