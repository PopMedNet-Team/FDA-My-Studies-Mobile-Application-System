package com.fdahpstudydesigner.util;

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
