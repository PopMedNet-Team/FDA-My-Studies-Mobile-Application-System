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
package com.hphc.mystudies.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Provides hibernate configuration details to get session factory.
 * 
 * @author BTC
 *
 */
public class HibernateUtil {

	private static Logger logger = Logger.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory = null;

	/**
	 * 
	 */
	private HibernateUtil() {
		super();
	}

	/**
	 * 
	 * @return
	 */
	public static SessionFactory getSessionFactory() {
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Starts");
		try {
			if (sessionFactory == null) {
				Properties properties = new Properties();
				properties.load(new FileInputStream(ServletContextHolder
						.getServletContext().getInitParameter(
								"property_file_location_path")));
				sessionFactory = new AnnotationConfiguration()
						.addProperties(properties)
						.configure("hibernate.cfg.xml").buildSessionFactory();
			}
		} catch (Exception e) {
			logger.error("HibernateUtil - getSessionFactory() :: ERROR ", e);
		}
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Ends");
		return sessionFactory;
	}

	/**
	 * 
	 * @param sessionFactory
	 */
	public static void setSessionFactory(SessionFactory sessionFactory) {
		HibernateUtil.sessionFactory = sessionFactory;
	}
}
