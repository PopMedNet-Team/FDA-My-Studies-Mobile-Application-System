/**
 *
 */
package com.fdahpstudydesigner.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * @author BTC
 *
 */
public class CrossScriptingFilter implements Filter {
	private static Logger logger = Logger.getLogger(CrossScriptingFilter.class);

	@Override
	public void destroy() {
		// Unused
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.info("Inlter CrossScriptingFilter  ...............");
		chain.doFilter(new RequestWrapper((HttpServletRequest) request),
				response);
		logger.info("Outlter CrossScriptingFilter ...............");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Unused
	}
}
