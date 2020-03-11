/**
 *
 */
package com.fdahpstudydesigner.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;

/**
 * @author BTC
 *
 */
public class RequestWrapper extends HttpServletRequestWrapper {
	private static Logger logger = Logger.getLogger(RequestWrapper.class);

	public RequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

	/**
	 * Clean the XSS from request parameters
	 * 
	 * @param value
	 *            , request parameters
	 * @return {@link String}
	 */
	private String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		logger.info("InnXSS RequestWrapper ..............." + value);
		String filteredValue = null;
		try {
			filteredValue = value
					.replaceAll("eval\\((.*)\\)", "")
					.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
							"\"\"")
					.replaceAll("(?i)<script.*?>.*?<script.*?>", "")
					.replaceAll("(?i)<script.*?>.*?</script.*?>", "")
					.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
		} catch (Exception e) {
			logger.error("RequestWrapper - cleanXSS - ERROR", e);
		}
		/* to skip the coverted html content from truncating */
		logger.info("OutnXSS RequestWrapper ........ value ......." + value);
		return filteredValue;
	}

	@Override
	public String getHeader(String name) {
		logger.info("Ineader .. parameter .......");
		String value = super.getHeader(name);
		if (value == null)
			return value;
		logger.info("Ineader RequestWrapper ........... value ....");
		return this.cleanXSS(value);
	}

	@Override
	public String getParameter(String parameter) {
		logger.info("Inarameter .. parameter .......");
		String value = super.getParameter(parameter);
		if (value == null) {
			return value;
		}
		logger.info("Inarameter RequestWrapper ........ value .......");
		return this.cleanXSS(value);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		logger.info("InarameterValues .. parameter .......");
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return values;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = this.cleanXSS(values[i]);
		}
		return encodedValues;
	}

}
