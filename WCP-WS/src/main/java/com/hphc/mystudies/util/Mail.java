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

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * Provides mail configuration details to send mail.
 * 
 * @author BTC
 *
 */
public class Mail {

	private static final Logger LOGGER = Logger.getLogger(Mail.class.getName());

	@SuppressWarnings("unchecked")
	static HashMap<String, String> propMap = StudyMetaDataUtil.configMap;

	/**
	 * Send email for the provided recipient, subject and content
	 * 
	 * @author BTC
	 * @param email
	 *            the recipient mail id
	 * @param subject
	 *            the mail subject
	 * @param messageBody
	 *            the mail body
	 * @return {@link Boolean}
	 * @throws Exception
	 */
	public static boolean sendemail(String email, String subject,
			String messageBody) throws Exception {
		LOGGER.debug("sendemail()====start");
		boolean sentMail = false;
		try {
			Properties props = new Properties();
			Session session;
			props.put("mail.smtp.host", propMap.get("smtp.hostname"));
			props.put("mail.smtp.port", propMap.get("smtp.portvalue"));

			if (propMap.get("fda.env") != null
					&& propMap.get("fda.env").equalsIgnoreCase("local")) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							@Override
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(propMap
										.get("from.email.address"), propMap
										.get("from.email.password"));
							}
						});
			} else {
				props.put("mail.smtp.auth", "false");
				session = Session.getInstance(props);
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(propMap
					.get("from.email.address")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setSubject(subject);
			message.setContent(messageBody, "text/html");
			Transport.send(message);
			sentMail = true;
		} catch (MessagingException e) {
			LOGGER.error("ERROR:  sendemail() - " + e + " : ");
			sentMail = false;
		} catch (Exception e) {
			LOGGER.error("ERROR:  sendemail() - " + e + " : ");
		}
		LOGGER.info("Mail.sendemail() :: Ends");
		return sentMail;
	}
}