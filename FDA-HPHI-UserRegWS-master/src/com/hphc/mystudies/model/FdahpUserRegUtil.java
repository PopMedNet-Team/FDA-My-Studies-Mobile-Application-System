/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.hphc.mystudies.model;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorCode;
import org.labkey.api.mbean.ErrorsMXBean;
import org.labkey.api.util.StringUtilsLabKey;
import com.hphc.mystudies.FdahpUserRegWSModule;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.util.MailHelper;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.hphc.mystudies.bean.NotificationBean;
import org.labkey.api.resource.Resource;
import org.labkey.api.resource.FileResource;


import static org.labkey.api.util.StringUtilsLabKey.DEFAULT_CHARSET;

/**
 * Created by Ravinder on 2/1/2017.
 */
public class FdahpUserRegUtil
{
    private static final Logger _log = Logger.getLogger(FdahpUserRegUtil.class);



    public enum ErrorCodes{
        INVALID_INPUT("INVALID_INPUT"),
        UNKNOWN("UNKNOWN"),
        STATUS_100("100"), // OK
        STATUS_101("101"), // Invalid Authentication (authKey is not valid).
        STATUS_102("102"), // Invalid Inputs (If any of the input parameter is missing).
        STATUS_103("103"), // No Data available.
        STATUS_104("104"), // Unknown Error
        STATUS_105("105"), // If there is no data to update.
        STATUS_106("106"), // Failed to generate token.
        STATUS_107("107"), // Failed to complete transaction.
        SESSION_EXPIRED_MSG("Session expired."),
        INVALID_AUTH_CODE("INVALID_AUTH_CODE"),
        INVALID_EMAIL("Invalid Email"),
        ACCOUNT_DEACTIVATE_ERROR_MSG("Your account has been deactivated"),
        INVALID_USERNAME_PASSWORD_MSG("Invalid username or password"),
        EMAIL_EXISTS("This email has already been used. Please try with different email address."),
        INVALID_INPUT_ERROR_MSG("Invalid input."),
        INACTIVE("INACTIVE"),
        SUCCESS("SUCCESS"),
        FAILURE("FAILURE"),
        JOINED("Joined"),
        COMPLETED("Completed"),
        STARTED("Started"),
        PAUSED("Paused"),
        PROFILE("profile"),
        SETTINGS("settings"),
        MESSAGE("message"),
        PARTICIPANTINFO("participantInfo"),
        STUDIES("studies"),
        ACTIVITIES("activities"),
        WITHDRAWN("Withdrawn"),
        NO_DATA_AVAILABLE("No data available"),
        CONSENT_VERSION_REQUIRED("Consent version is required"),
        CONNECTION_ERROR_MSG("Oops, something went wrong. Please try again after sometime"),
        WITHDRAWN_STUDY("You are already Withdrawn from study"),
        EMAIL_NOT_EXISTS("Email Doesn't Exists"),
        RESEND_EMAIL_NOT_EXISTS("Email Doesn't Exists OR Email Already Verified"),
        USER_NOT_EXISTS("User Doesn't Exists"),
        FAILURE_TO_SENT_MAIL("Oops, something went wrong. Failed to send Email"),
        OLD_PASSWORD_NOT_EXISTS("Invalid old password"),
        OLD_PASSWORD_AND_NEW_PASSWORD_NOT_SAME("Current Password and New Password cannot be same"),
        NEW_PASSWORD_NOT_SAME_LAST_PASSWORD("New Password should not be the same as the last 10 passwords."),
        USER_ALREADY_VERIFIED("User already verified"),
        INVALID_CODE("Invalid code"),
        CODE_EXPIRED("Code Expired"),
        YET_TO_JOIN("yetToJoin"),
        IN_PROGRESS("inProgress"),
        STUDY_LEVEL("ST"),
        GATEWAY_LEVEL("GT"),
        INVALID_CREDENTIALS("Invalid credentials"),
        ACCOUNT_LOCKED("As a security measure, this account has been locked for 15 minutes."),
        ACCOUNT_TEMP_LOCKED("As a security measure, this account has been locked for 15 minutes."),
        EMAIL_VERIFICATION_SUCCESS_MESSAGE("Thanks, your email has been successfully verified! You can now proceed to completing the sign up process on the mobile app."),
        EMAIL_NOT_VERIFIED("Your account is not verified. Please verify your account by clicking on verification link which has been sent to your registered email. If not received, would you like to resend verification link?"),
        LABKEY_HOME("http://192.168.0.6:8081"),
        STUDY("Study"),
        GATEWAY("Gateway"),
        DEVICE_ANDROID("android"),
        DEVICE_IOS("ios"),
        INVALID_REFRESHTOKEN("Invalid refresh token.");
        private final String value;
        ErrorCodes(final String newValue){
            value=newValue;
        }
        public String getValue() { return value; }
    }

    public static String commaSeparatedString(List<String> studyIds){
        if (studyIds.size() > 0) {
            StringBuilder studyBuilder = new StringBuilder();
            for (String n : studyIds) {
                studyBuilder.append("'").append(n.replace("'", "\\'")).append("',");
            }
            studyBuilder.deleteCharAt(studyBuilder.length() - 1);
            return studyBuilder.toString();
        } else {
            return "";
        }
    }

    public static void getFailureResponse(String status, String title, String message ,HttpServletResponse response){
        try {
            response.setHeader("status", status);
            response.setHeader("title", title);
            response.setHeader("StatusMessage", message);
            if(status.equalsIgnoreCase(ErrorCodes.STATUS_104.getValue()))
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            if(status.equalsIgnoreCase(ErrorCodes.STATUS_102.getValue()))
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            if(status.equalsIgnoreCase(ErrorCodes.STATUS_101.getValue()))
                if(message.equalsIgnoreCase(ErrorCodes.SESSION_EXPIRED_MSG.getValue()))
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorCodes.SESSION_EXPIRED_MSG.getValue());
                else
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            if(status.equalsIgnoreCase(ErrorCodes.STATUS_103.getValue()))
                response.sendError(HttpServletResponse.SC_FORBIDDEN, message);

        } catch (Exception e) {
            _log.info("FdahpUserRegUtil - getFailureResponse() :: ERROR " , e);
        }
    }
    public static String getEncryptedString(String input) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isNotEmpty(input)){
           input = input + "StudyGateway";
           try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(input.getBytes("UTF-8"));
            byte[] digestBytes = messageDigest.digest();
            String hex = null;
            for (int i = 0; i < 8; i++) {
             hex = Integer.toHexString(0xFF & digestBytes[i]);
             if (hex.length() < 2)
              sb.append("0");
             sb.append(hex);
            }
           }catch (Exception ex) {
            _log.error(ex.getMessage());
           }
        }
        return sb.toString();
    }

    public static String getCurrentDate() {
        String getToday = "";
        try {
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            getToday = formatter.format(today.getTime());
        } catch (Exception e) {
            _log.error(e);
        }
        return getToday;
    }

    public static String getCurrentDateTime() {
        String getToday = "";
        try {
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            getToday = formatter.format(today.getTime());

        } catch (Exception e) {
            _log.error(e);
        }
        return getToday;
    }

    public static String getFormattedDateTimeZone(String input, String inputFormat, String outputFormat){
        String output = "";
        try{
            if(StringUtils.isNotEmpty(input)){
                SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
                Date inputDate = inputSDF.parse(input);
                SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat); //yyyy-MM-dd'T'hh:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.SSSZ
               output = outputSDF.format(inputDate);
            }
        }catch(Exception e){
            _log.error(e);
        }
        return output;
    }

    public static String getEncodeString(String value){
        byte[] encodedBytes = Base64.encodeBase64(value.getBytes(DEFAULT_CHARSET));
        return new String(encodedBytes, DEFAULT_CHARSET);

    }
    public static String getDecodeString(String values){
        byte[] decodedBytes = Base64.decodeBase64(values);
        return new String(decodedBytes, DEFAULT_CHARSET);
    }

    public static boolean sendemail(String email, String subject, String messageBody) throws Exception{

        boolean sentMail = false;

        Properties configProp = FdahpUserRegUtil.getProperties();
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", (String) configProp.get("hostname"));

            //props.put("mail.smtp.ssl.enable", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.socketFactory.port", (String) configProp.get("port"));
            props.put("mail.smtp.socketFactory.class",(String) configProp.get("factory.value"));
            props.put("mail.smtp.port", (String) configProp.get("port"));

            _log.info("factory - "+(String) configProp.get("factory.value"));
            _log.info("hostName - "+(String) configProp.get("hostname"));
            _log.info("port - "+(String) configProp.get("port"));
            final String username = (String) configProp.get("from.email.address");
            final String password = (String) configProp.get("from.email.password");
            _log.info("username - "+username);
            _log.info("password - "+password);
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("apps@boston-technology.com", "password789");
                        }
                    });
            Message message = new MimeMessage(session);
            _log.info("email - "+email);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setContent(messageBody, "text/html");
            Transport.send(message);
            sentMail = true;
        } catch (MessagingException e) {
            _log.error("ERROR:  sendemail() - ",e);
            sentMail = false;
        } catch (Exception e) {
            _log.error("ERROR:  sendemail() - ",e);
        }

        return sentMail;
    }
    public static Properties getProperties(){
        Properties prop = new Properties();
        InputStream input = null;
        try{

            Module m =  ModuleLoader.getInstance().getModule(FdahpUserRegWSModule.NAME);
            InputStream is = m.getResourceStream("constants/message.properties");
            prop.load(is);

        }catch (Exception e){
            _log.error("ERROR:  getProperties() - ",e);
        }
        return prop;
    }

    public static Date getCurrentUtilDateTime() {
        Date date = new Date();
        Calendar currentDate = Calendar.getInstance();
        String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate.getTime());
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateNow);
        } catch (Exception e) {
            _log.error("FdahpUserRegUtil - getCurrentUtilDateTime() : ",e);

        }
        return date;
    }
    public static Date addMinutes(String currentDate, int minutes) {
        Date futureDate = null;
        try {
            Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.MINUTE, minutes);
            Date newDate = cal.getTime();
            futureDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate));
        } catch (Exception e) {
            _log.error("FdahpUserRegUtil - addHours() : ",e);
        }
        return futureDate;
    }
    public static Date addHours(String currentDate, int hours){
        Date futureDate = null;
        try {
            Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.HOUR, hours);
            Date newDate = cal.getTime();
            futureDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate));
        } catch (Exception e) {
            _log.error("FdahpUserRegUtil - addHours() : ",e);
        }
        return futureDate;
    }

    public static Date addDays(String currentDate, int days){
        Date futureDate = null;
        try {
            Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.DATE, days);
            Date newDate = cal.getTime();
            futureDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate));
        } catch (Exception e) {
            _log.error("FdahpUserRegUtil - addHours() : ",e);
        }
        return futureDate;
    }

    public static void sendMessage(String subject, String bodyHtml, String recipients)
    {
        Properties configProp = FdahpUserRegUtil.getProperties();
        try
        {
            MailHelper.MultipartMessage msg = MailHelper.createMultipartMessage();
            final String username = (String) configProp.get("from.email.address");
            msg.setFrom(username);
            msg.setSubject(subject);
            msg.setRecipients(Message.RecipientType.TO, recipients);
            msg.setEncodedHtmlContent(bodyHtml);
            MailHelper.send(msg,null, null);
        }
        catch (Exception e)
        {
            _log.error("Unable to send email ", e);
        }
    }
    public static void pushNotification(NotificationBean notificationBean){
        Properties configProp = FdahpUserRegUtil.getProperties();
        try{
            _log.info("pushNotification");
            Module m =  ModuleLoader.getInstance().getModule(FdahpUserRegWSModule.NAME);
            Resource r = m.getModuleResource("/constants/"+(String)configProp.get("certificate.name"));
            File f = ((FileResource) r).getFile();
            String path = f.getPath();
            _log.info("path:"+path);
            //ApnsService service = APNS.newService().withCert(path, (String)configProp.get("certificate.password")).withSandboxDestination().build(); //for Test and UAT with dev certificate
           ApnsService service = APNS.newService().withCert(path, (String)configProp.get("certificate.password")).withProductionDestination().build(); //for Production with production certificate

            List<String> tokens = new ArrayList<String>();
            if(notificationBean.getDeviceToken() != null){
               for(int i=0;i<notificationBean.getDeviceToken().length();i++){
                   String token = (String) notificationBean.getDeviceToken().get(i);
                   _log.info("token:"+token);
                   tokens.add(token);
               }
            }
            String customPayload = APNS.newPayload().badge(1).alertTitle("")
                    .alertBody(notificationBean.getNotificationText())
                    .customField("subtype", notificationBean.getNotificationSubType())
                    .customField("type", notificationBean.getNotificationType())
                    .customField("studyId", notificationBean.getCustomStudyId())
                    .sound("default")
                    .build();
            service.push(tokens, customPayload);
            _log.info("pushNotification Ends");
        }catch (Exception e){
            _log.error("pushNotification ", e);
        }
    }
    public static String getStandardFileName(String StudyId, String userId,String version) {
        String dateTime = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
        return StudyId + "_" + userId+ "_"+version+ "_"+ dateTime+".pdf";
    }
}
