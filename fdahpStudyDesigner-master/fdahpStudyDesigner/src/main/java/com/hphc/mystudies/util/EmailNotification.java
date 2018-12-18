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

/**
 * @author BTC
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class EmailNotification {

	private static Logger logger = Logger.getLogger(EmailNotification.class
			.getName());

	/**
	 * @param subjectProprtyName
	 * @param content
	 * @param toMail
	 * @param ccMailList
	 * @param bccMailList
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean sendEmailNotification(String subjectProprtyName,
			String content, String toMail, List<String> ccMailList,
			List<String> bccMailList) {
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		logger.info("EmailNotification - Starts: sendLinkToEmail() - Input arg are ServletContext ,  Email = "
				+ toMail
				+ " Subject = "
				+ propMap.get(subjectProprtyName)
				+ " contents =" + content + " : ");
		boolean sentMail = false;
		try {
			Mail mail = new Mail();
			if (toMail != null) {
				toMail = toMail.trim();
				mail.setToemail(toMail.toLowerCase());
			}
			mail.setFromEmailAddress(propMap.get("from.email.address"));
			mail.setFromEmailPassword(propMap.get("from.email.password"));
			mail.setSmtpHostname(propMap.get("smtp.hostname"));
			mail.setSmtpPortvalue(propMap.get("smtp.portvalue"));
			mail.setSslFactory(propMap.get("sslfactory.value"));
			mail.setCcEmail(StringUtils.join(ccMailList, ','));
			mail.setBccEmail(StringUtils.join(bccMailList, ','));
			mail.setSubject(propMap.get(subjectProprtyName));
			mail.setMessageBody(content);
			mail.sendemail();
			sentMail = true;
		} catch (Exception e) {
			logger.error("EmailNotification.sendEmailNotification() :: ERROR ",
					e);
		}
		logger.info("EmailNotification - Ends: sendLinkToEmail() - returning  a List value"
				+ " : ");
		return sentMail;
	}

	/**
	 * @param subjectProprtyName
	 * @param content
	 * @param toMailList
	 * @param ccMailList
	 * @param bccMailList
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean sendEmailNotificationToMany(
			String subjectProprtyName, String content, List<String> toMailList,
			List<String> ccMailList, List<String> bccMailList) {
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		logger.info("EmailNotification - Starts: sendEmailNotificationToMany() - Input arg are ServletContext ");
		boolean sentMail = false;
		List<String> toMailListNew = new ArrayList<>();
		try {
			Mail mail = new Mail();
			if (toMailList != null && !toMailList.isEmpty()) {
				for (String mailId : toMailList) {
					mailId = mailId.trim();
					toMailListNew.add(mailId.toLowerCase());
					logger.info("EmailNotification - Starts: sendEmailNotificationToMany() - Input arg are ServletContext ,  Email = "
							+ mailId
							+ " Subject = "
							+ propMap.get(subjectProprtyName)
							+ " contents ="
							+ content + " : ");
				}
				mail.setToemail(StringUtils.join(toMailListNew, ','));
			}
			mail.setFromEmailAddress(propMap.get("from.email.address"));
			mail.setFromEmailPassword(propMap.get("from.email.password"));
			mail.setSmtpHostname(propMap.get("smtp.hostname"));
			mail.setSmtpPortvalue(propMap.get("smtp.portvalue"));
			mail.setSslFactory(propMap.get("sslfactory.value"));
			mail.setCcEmail(StringUtils.join(ccMailList, ','));
			mail.setBccEmail(StringUtils.join(bccMailList, ','));
			mail.setSubject(propMap.get(subjectProprtyName));
			mail.setMessageBody(content);
			mail.sendemail();
			sentMail = true;
		} catch (Exception e) {
			sentMail = false;
			logger.error(
					"EmailNotification.sendEmailNotificationToMany() :: ERROR ",
					e);
		}
		logger.info("EmailNotification - Ends: sendEmailNotificationToMany() - returning  a List value"
				+ " : ");
		return sentMail;
	}

	/**
	 * @param subjectProprtyName
	 * @param content
	 * @param toMail
	 * @param ccMailList
	 * @param bccMailList
	 * @param attachmentPath
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean sendMailWithAttachment(String subjectProprtyName,
			String content, String toMail, List<String> ccMailList,
			List<String> bccMailList, String attachmentPath) {
		Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
		logger.info("EmailNotification - Starts: sendLinkToEmail() - Input arg are ServletContext ,  Email = "
				+ toMail
				+ " Subject = "
				+ propMap.get(subjectProprtyName)
				+ " contents =" + content + " : ");
		boolean sentMail = false;
		try {
			Mail mail = new Mail();
			if (toMail != null) {
				toMail = toMail.trim();
				mail.setToemail(toMail.toLowerCase());
			}
			mail.setFromEmailAddress(propMap.get("from.email.address"));
			mail.setFromEmailPassword(propMap.get("from.email.password"));
			mail.setSmtpHostname(propMap.get("smtp.hostname"));
			mail.setSmtpPortvalue(propMap.get("smtp.portvalue"));
			mail.setSslFactory(propMap.get("sslfactory.value"));
			mail.setCcEmail(StringUtils.join(ccMailList, ','));
			mail.setBccEmail(StringUtils.join(bccMailList, ','));
			mail.setSubject(propMap.get(subjectProprtyName));
			mail.setMessageBody(content);
			mail.setAttachmentPath(attachmentPath);
			mail.sendMailWithAttachment();
			sentMail = true;
		} catch (Exception e) {
			logger.error("EmailNotification.sendEmailNotification() :: ERROR ",
					e);
		}
		logger.info("EmailNotification - Ends: sendLinkToEmail() - returning  a List value"
				+ " : ");
		return sentMail;
	}
}
