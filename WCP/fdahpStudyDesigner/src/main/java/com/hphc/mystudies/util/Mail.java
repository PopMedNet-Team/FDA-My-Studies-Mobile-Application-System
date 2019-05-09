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

import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Mail POJO class
 * 
 * @author BTC
 *
 */
public class Mail {

	/**
	 *
	 */
	private static Logger logger = Logger.getLogger(Mail.class.getName());

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private String attachmentPath;
	private String bccEmail;
	private String ccEmail;
	private Map<?, ?> configMap = FdahpStudyDesignerUtil.getAppProperties();
	private String fromEmailAddress = "";
	private String fromEmailName = "";
	private String fromEmailPassword;
	private String messageBody;
	private String smtpHostname = "";
	private String smtpPortvalue = "";

	private String sslFactory = "";

	private String subject;

	private String toemail;

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public String getBccEmail() {
		return bccEmail;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public String getFromEmailAddress() {
		return fromEmailAddress;
	}

	public String getFromEmailName() {
		return fromEmailName;
	}

	public String getFromEmailPassword() {
		return fromEmailPassword;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public String getSmtpHostname() {
		String hostname;
		if ("".equals(this.smtpHostname)) {
			hostname = Mail.SMTP_HOST_NAME;
		} else {
			hostname = this.smtpHostname;
		}
		return hostname;
	}

	public String getSmtpPortvalue() {
		String portvalue;
		if (("").equals(this.smtpPortvalue)) {
			portvalue = Mail.SMTP_PORT;
		} else {
			portvalue = this.smtpPortvalue;
		}

		return portvalue;
	}

	public String getSslFactory() {

		String sslfactoryvalue;
		if (("").equals(this.sslFactory)) {
			sslfactoryvalue = Mail.SSL_FACTORY;
		} else {
			sslfactoryvalue = this.sslFactory;
		}

		return sslfactoryvalue;

	}

	public String getSubject() {
		return subject;
	}

	public String getToemail() {
		return toemail;
	}

	public boolean sendemail() {
		logger.warn("sendemail()====start");
		boolean sentMail = false;
		Session session = null;
		try {
			final String username = this.getFromEmailAddress();
			final String password = this.getFromEmailPassword();
			Properties props = new Properties();
			props.put("mail.smtp.host", this.getSmtpHostname());
			props.put("mail.smtp.port", this.getSmtpPortvalue());

			if (configMap.get("fda.env") != null
					&& FdahpStudyDesignerConstants.FDA_ENV_LOCAL
							.equals(configMap.get("fda.env"))) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.socketFactory.port",
						this.getSmtpPortvalue());
				props.put("mail.smtp.socketFactory.class", this.getSslFactory());
				session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							@Override
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username,
										password);
							}
						});
			} else {
				props.put("mail.smtp.auth", "false");
				session = Session.getInstance(props);
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			if (StringUtils.isNotBlank(this.getToemail())) {
				if (this.getToemail().indexOf(',') != -1) {
					message.setRecipients(Message.RecipientType.BCC,
							InternetAddress.parse(this.getToemail()));
				} else {
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(this.getToemail()));
				}
			}
			if (StringUtils.isNotBlank(this.getCcEmail())) {
				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(this.getCcEmail()));
			}
			if (StringUtils.isNotBlank(this.getBccEmail())) {
				message.setRecipients(Message.RecipientType.BCC,
						InternetAddress.parse(this.getBccEmail()));
			}
			message.setSubject(this.subject);
			message.setContent(this.getMessageBody(), "text/html");
			Transport.send(message);
			logger.debug("sendemail()====end");
			sentMail = true;
		} catch (Exception e) {
			logger.error("ERROR: sendemail() - ", e);
			sentMail = false;
		}
		logger.info("Mail.sendemail() :: Ends");
		return sentMail;
	}

	public boolean sendMailWithAttachment() {
		logger.debug("sendemail()====start");
		boolean sentMail = false;
		Session session = null;
		BodyPart messageBodyPart = null;
		Multipart multipart = null;

		try {
			final String username = this.getFromEmailAddress();
			final String password = this.getFromEmailPassword();
			Properties props = new Properties();
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.host", this.getSmtpHostname());
			props.put("mail.smtp.port", this.getSmtpPortvalue());

			if (configMap.get("fda.env") != null
					&& FdahpStudyDesignerConstants.FDA_ENV_LOCAL
							.equals(configMap.get("fda.env"))) {
				props.put("mail.smtp.socketFactory.port",
						this.getSmtpPortvalue());
				props.put("mail.smtp.socketFactory.class", this.getSslFactory());
				session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							@Override
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username,
										password);
							}
						});
			} else {
				session = Session.getInstance(props);
			}

			Message message = new MimeMessage(session);
			if (StringUtils.isNotBlank(this.getToemail())) {
				if (this.getToemail().indexOf(',') != -1) {
					message.setRecipients(Message.RecipientType.BCC,
							InternetAddress.parse(this.getToemail()));
				} else {
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(this.getToemail()));
				}
			}
			if (StringUtils.isNotBlank(this.getCcEmail())) {
				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(this.getCcEmail()));
			}
			if (StringUtils.isNotBlank(this.getBccEmail())) {
				message.setRecipients(Message.RecipientType.BCC,
						InternetAddress.parse(this.getBccEmail()));
			}
			message.setSubject(this.subject);
			message.setFrom(new InternetAddress(username));

			// Create the message part
			messageBodyPart = new MimeBodyPart();
			// Create a multipart message
			multipart = new MimeMultipart();

			// String filename = "D:\\temp\\TestLinks.pdf"; // D:\temp\noteb.txt
			DataSource source = new FileDataSource(this.getAttachmentPath());
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(source.getName());
			messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
			messageBodyPart.setHeader("Content-Type", source.getContentType());
			// Send the complete message parts
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(messageBody);
			messageBodyPart.setHeader("MIME-Version", "1.0");
			messageBodyPart.setHeader("Content-Type",
					messageBodyPart.getContentType());
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);
			Transport.send(message);
			sentMail = true;
		} catch (Exception e) {
			logger.error("ERROR:  sendemail() - ", e);
		}
		logger.info("Mail.sendemail() :: Ends");
		return sentMail;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public void setBccEmail(String bccEmail) {
		this.bccEmail = bccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}

	public void setFromEmailName(String fromEmailName) {
		this.fromEmailName = fromEmailName;
	}

	public void setFromEmailPassword(String fromEmailPassword) {
		this.fromEmailPassword = fromEmailPassword;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public void setSmtpHostname(String smtpHostname) {
		this.smtpHostname = smtpHostname;
	}

	public void setSmtpPortvalue(String smtpPortvalue) {
		this.smtpPortvalue = smtpPortvalue;
	}

	public void setSslFactory(String sslFactory) {
		this.sslFactory = sslFactory;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setToemail(String toemail) {
		this.toemail = toemail;
	}

}