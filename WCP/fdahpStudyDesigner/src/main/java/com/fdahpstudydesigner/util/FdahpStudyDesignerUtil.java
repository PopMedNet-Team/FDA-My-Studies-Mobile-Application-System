package com.fdahpstudydesigner.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.objecthunter.exp4j.ExpressionBuilder;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import com.fdahpstudydesigner.bean.FormulaInfoBean;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPermissions;

/**
 * An helper class with static utility methods
 * 
 * @author BTC
 *
 */
public class FdahpStudyDesignerUtil {

	/* Read Properties file */
	private static Logger logger = Logger
			.getLogger(FdahpStudyDesignerUtil.class.getName());
	protected static final Map<String, String> configMap = FdahpStudyDesignerUtil
			.getAppProperties();

	public static Date addDaysToDate(Date date, int days) {
		logger.info("fdahpStudyDesignerUtiltyLinkUtil: addDaysToDate :: Starts");
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, days);
			date = cal.getTime();
		} catch (Exception e) {
			logger.error("ERROR: FdahpStudyDesignerUtil.addDaysToDate() ::", e);
		}
		logger.info("FdahpStudyDesignerUtil: addDaysToDate :: Ends");
		return date;
	}

	public static String addHours(String dtStr, int hours) {
		String newdateStr = "";
		try {
			Date dt = new SimpleDateFormat(
					FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.HOUR, hours);
			Date newDate = cal.getTime();
			newdateStr = new SimpleDateFormat(
					FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
					.format(newDate);
		} catch (Exception e) {
			logger.error("ERROR: FdahpStudyDesignerUtil.addHours()", e);
		}
		return newdateStr;
	}

	/**
	 * This method is used to add minutes to the input dateTime
	 * 
	 * @author BTC
	 * @param dtStr
	 * @param minutes
	 * @return
	 */
	public static String addMinutes(String dtStr, int minutes) {
		logger.info("FdahpStudyDesignerUtil - Entry Point: addMinutes()");
		String newdateStr = "";
		try {
			Date dt = new SimpleDateFormat(
					FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MINUTE, minutes);
			Date newDate = cal.getTime();
			newdateStr = new SimpleDateFormat(
					FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
					.format(newDate);
		} catch (Exception e) {
			logger.error("FdahpStudyDesignerUtil - addMinutes() : ", e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: addMinutes()");
		return newdateStr;
	}

	/**
	 * This method is used to build user's spring security authorities from it's
	 * granted permission
	 * 
	 * @author BTC
	 *
	 * @param userRoles
	 *            , {@link Set} of {@link UserPermissions}
	 * @return {@link List} of {@link GrantedAuthority}
	 */
	public static List<GrantedAuthority> buildUserAuthority(
			Set<UserPermissions> userRoles) {

		Set<GrantedAuthority> setAuths = new HashSet<>();

		// Build user's authorities
		for (UserPermissions userRole : userRoles) {
			setAuths.add(new SimpleGrantedAuthority(userRole.getPermissions()));
		}

		return new ArrayList<>(setAuths);
	}

	/**
	 * This method map database user details to spring user details.
	 *
	 * @author BTC
	 *
	 * @param user
	 *            , Object of {@link UserBO}
	 * @param authorities
	 *            , List of {@link GrantedAuthority}
	 * @return {@link User}
	 */
	public static User buildUserForAuthentication(UserBO user,
			List<GrantedAuthority> authorities) {
		return new User(user.getUserEmail(), user.getUserPassword(),
				user.isEnabled(), user.isAccountNonExpired(),
				user.isCredentialsNonExpired(), user.isAccountNonLocked(),
				authorities);
	}

	public static boolean compareDateWithCurrentDateResource(String inputDate,
			String inputFormat) {
		boolean flag = false;
		final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
		// TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
		// sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		try {
			if (new Date().before(sdf.parse(inputDate))) {
				flag = true;
			}
			/*
			 * if (new Date().equals(sdf.parse(inputDate))) { flag=true; }
			 */
		} catch (ParseException e) {
			logger.error(
					"FdahpStudyDesignerUtil - compareDateWithCurrentDateTime() : ",
					e);
		}
		return flag;
	}

	/**
	 * Comparing user date with current date
	 *
	 * @param inputDate
	 *            , date string
	 * @param inputFormat
	 *            , date string format
	 * @return
	 */
	public static boolean compareDateWithCurrentDateTime(String inputDate,
			String inputFormat) {
		boolean flag = false;
		final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
		try {
			if (new Date().before(sdf.parse(inputDate))) {
				flag = true;
			}
			if (new Date().equals(sdf.parse(inputDate))) {
				flag = true;
			}
		} catch (ParseException e) {
			logger.error(
					"FdahpStudyDesignerUtil - compareDateWithCurrentDateTime() : ",
					e);
		}
		return flag;
	}

	/**
	 * Compare encrypted password with password string
	 * 
	 * @param dbEncryptPassword
	 *            , encrypted password
	 * @param uiPassword
	 *            , password string
	 * @return {@link Boolean}
	 */
	public static Boolean compareEncryptedPassword(String dbEncryptPassword,
			String uiPassword) {
		Boolean isMatch = false;
		logger.info("getEncryptedString start");
		try {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			isMatch = passwordEncoder.matches(uiPassword, dbEncryptPassword);
		} catch (Exception e) {
			logger.error(
					"FdahpStudyDesignerUtil - compairEncryptedPassword() - ERROR",
					e);
		}
		logger.info("getEncryptedString end");
		return isMatch;
	}

	public static boolean fieldsValidation(String... fields) {
		logger.info("FdahpStudyDesignerUtil - Entry Point: formValidation() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		List<String> fieldsList = new ArrayList<>();
		boolean result = true;
		try {
			for (String field : fields) {
				fieldsList.add(field);
			}
			for (int i = 0; i < fieldsList.size(); i++) {
				String tempField = fieldsList.get(i);
				tempField = StringUtils.isEmpty(tempField) != true ? tempField
						.trim() : "";
				if (tempField.length() < 1) {
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("ERROR: FdahpStudyDesignerUtil: formValidation(): ", e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: formValidation() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		return result;
	}

	public static String formatTime(String inputTime, String inputFormat,
			String outputFormat) {
		logger.info("FdahpStudyDesignerUtil.formatTime() :: Starts");
		String finalTime = "";
		SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
		SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat);
		if (inputTime != null && !"".equals(inputTime)
				&& !"null".equalsIgnoreCase(inputTime)) {
			try {
				finalTime = outputSDF.format(inputSDF.parse(inputTime))
						.toLowerCase();
			} catch (Exception e) {
				logger.error("FdahpStudyDesignerUtil.formatTime() ::", e);
			}
		}
		logger.info("FdahpStudyDesignerUtil.formatTime() :: Ends");
		return finalTime;
	}

	/**
	 * This method used to map the dynamic value to it's static email marker to
	 * create a dynamic email contain
	 *
	 * @author BTC
	 *
	 * @param emailContentName
	 *            , the email content name from property file
	 * @param keyValue
	 *            , the key value pair of email content marker and it's value
	 * @return
	 */
	public static String genarateEmailContent(String emailContentName,
			Map<String, String> keyValue) {

		String dynamicContent = configMap.get(emailContentName);

		if (FdahpStudyDesignerUtil.isNotEmpty(dynamicContent)) {
			for (Map.Entry<String, String> entry : keyValue.entrySet()) {
				dynamicContent = dynamicContent.replace(
						entry.getKey(),
						StringUtils.isBlank(entry.getValue()) ? "" : entry
								.getValue());
			}
		}
		return dynamicContent;
	}

	/**
	 * This methods read all property from properties file by using
	 * {@link ResourceBundle} and add it to a map
	 * 
	 * @return {@link Map} of properties
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> getAppProperties() {
		HashMap hm = new HashMap<String, String>();
		logger.warn("FdahpStudyDesignerUtil - getAppProperties() :: Properties Initialization");
		Enumeration<String> keys = null;
		Enumeration<Object> objectKeys = null;
		try {
			ResourceBundle rb = ResourceBundle.getBundle("messageResource");
			keys = rb.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = rb.getString(key);
				hm.put(key, value);
			}
			ServletContext context = ServletContextHolder.getServletContext();
			Properties prop = new Properties();
			prop.load(new FileInputStream(context
					.getInitParameter("property_file_location_path")));
			objectKeys = prop.keys();
			while (objectKeys.hasMoreElements()) {
				String key = (String) objectKeys.nextElement();
				String value = prop.getProperty(key);
				hm.put(key, value);
			}

		} catch (Exception e) {
			logger.error(
					"FdahpStudyDesignerUtil - getAppProperties() - ERROR ", e);
		}
		return hm;
	}

	public static FormulaInfoBean getConditionalFormulaResult(String lhs,
			String rhs, String operator, String trialInput) {
		FormulaInfoBean formulaInfoBean = new FormulaInfoBean();
		String operand1 = "";
		String operand2 = "";
		BigDecimal result = null;
		BigDecimal oprandResult = null;
		if (lhs.contains("x")) {
			if (lhs.contains("!=") || lhs.contains("==") || lhs.contains(">")
					|| lhs.contains("<") || lhs.contains("&&")
					|| lhs.contains("||")) {
				oprandResult = null;
				try {
					oprandResult = new com.udojava.evalex.Expression(lhs).with(
							"x", trialInput).eval();
					if (oprandResult.intValue() == 1)
						operand1 = "true";
					else
						operand1 = "false";
				} catch (Exception e) {
					logger.error(
							"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
							e);
					formulaInfoBean.setStatusMessage("Error in LHS");
				}
			} else {
				try {
					net.objecthunter.exp4j.Expression e;
					if (trialInput.contains(".")) {
						e = new ExpressionBuilder(lhs).variables("x").build()
								.setVariable("x", Float.parseFloat(trialInput));
					} else {
						e = new ExpressionBuilder(lhs).variables("x").build()
								.setVariable("x", Integer.parseInt(trialInput));
					}
					double op = e.evaluate();
					operand1 = Double.toString(Math.round(op * 100.0) / 100.0);
				} catch (Exception e) {
					logger.error(
							"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
							e);
					formulaInfoBean.setStatusMessage("Error in LHS");
				}
			}
		} else {
			try {
				double op = new ExpressionBuilder(lhs).build().evaluate();
				operand1 = Double.toString(Math.round(op * 100.0) / 100.0);
			} catch (Exception e) {
				logger.error(
						"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
						e);
				formulaInfoBean.setStatusMessage("Error in RHS");
			}
		}
		if (rhs.contains("x")) {
			if (rhs.contains("!=") || rhs.contains("==") || rhs.contains(">")
					|| rhs.contains("<") || rhs.contains("&&")
					|| rhs.contains("||")) {
				oprandResult = null;
				try {
					oprandResult = new com.udojava.evalex.Expression(rhs).with(
							"x", trialInput).eval();
					if (oprandResult.intValue() == 1)
						operand2 = "true";
					else
						operand2 = "false";
				} catch (Exception e) {
					logger.error(
							"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
							e);
					formulaInfoBean.setStatusMessage("Error in LHS");
				}
			} else {
				try {
					net.objecthunter.exp4j.Expression e;
					if (trialInput.contains(".")) {
						e = new ExpressionBuilder(rhs).variables("x").build()
								.setVariable("x", Float.parseFloat(trialInput));
					} else {
						e = new ExpressionBuilder(rhs).variables("x").build()
								.setVariable("x", Integer.parseInt(trialInput));
					}

					double op = e.evaluate();
					operand2 = Double.toString(Math.round(op * 100.0) / 100.0);
				} catch (Exception e) {
					logger.error(
							"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
							e);
					formulaInfoBean.setStatusMessage("Error in RHS");
				}
			}
		} else {
			try {
				double op = new ExpressionBuilder(rhs).build().evaluate();
				operand2 = Double.toString(Math.round(op * 100.0) / 100.0);
			} catch (Exception e) {
				logger.error(
						"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
						e);
				formulaInfoBean.setStatusMessage("Error in RHS");
			}
		}
		if (formulaInfoBean.getStatusMessage().isEmpty()) {
			try {
				result = new com.udojava.evalex.Expression("x " + operator
						+ " y").with("x", operand1).with("y", operand2).eval();
			} catch (Exception e) {
				logger.error(
						"FdahpStudyDesignerUtil - getConditionalFormulaResult() : ",
						e);
				formulaInfoBean.setStatusMessage("Error in Result");

			}
			if (result != null) {
				if (result.intValue() == 1) {
					formulaInfoBean.setOutPutData("True");
				} else {
					formulaInfoBean.setOutPutData("False");
				}
				formulaInfoBean.setLhsData(operand1);
				formulaInfoBean.setRhsData(operand2);
				formulaInfoBean.setMessage(FdahpStudyDesignerConstants.SUCCESS);
			}
		}
		return formulaInfoBean;
	}

	/**
	 * Get Current Date as {@link String} with yyyy-MM-dd format
	 * 
	 * @return {@link String}
	 */
	public static String getCurrentDate() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return formatter.format(currentDate.getTime());
	}

	/**
	 * Return Current Date and Time as {@link String} with yyyy-MM-dd HH:mm:ss
	 * format
	 * 
	 * @return {@link String}
	 */
	public static String getCurrentDateTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(
				FdahpStudyDesignerConstants.DB_SDF_DATE_TIME);
		// formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return formatter.format(currentDate.getTime());

	}

	public static Date getCurrentDateTimeAsDate() {
		logger.info("FdahpStudyDesignerUtil - Entry Point: getCurrentDateTimeAsDate() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		Date dateNow = null;
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeZone = "UTC";
		try {
			String strDate = new Date() + "";
			if (strDate.indexOf("IST") != -1) {
				timeZone = "IST";
			}
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			dateNow = sdf.parse(sdf.format(new Date()));
		} catch (Exception e) {
			logger.error("ERROR: getCurrentDateTimeAsDate(): ", e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: getCurrentDateTimeAsDate() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		return dateNow;
	}

	/**
	 * Return Current Time as {@link String} with HH:mm:ss format
	 * 
	 * @return {@link String}
	 */
	public static String getCurrentTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		// formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return formatter.format(currentDate.getTime());
	}

	public static Date getCurrentUtilDateTime() {
		logger.info("FdahpStudyDesignerUtil - Entry Point: getCurrentUtilDateTime() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		Date utilDate = new Date();
		Calendar currentDate = Calendar.getInstance();
		String dateNow = new SimpleDateFormat(
				FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
				.format(currentDate.getTime());
		try {
			utilDate = new SimpleDateFormat(
					FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
					.parse(dateNow);
		} catch (ParseException e) {
			logger.error(
					"FdahpStudyDesignerUtil - getCurrentUtilDateTime() : ", e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: getCurrentUtilDateTime() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		return utilDate;
	}

	public static String getDate(String date, SimpleDateFormat sdf) {
		logger.info("FdahpStudyDesignerUtil.getDate() :: Starts");
		String postedDate = sdf.format(date);
		logger.info("FdahpStudyDesignerUtil.getDate() :: Ends");
		return postedDate;
	}

	/**
	 * @author BTC
	 * @param timeZone
	 *            , currentDateTime
	 * @param userCurrentDateTimeForTimeZone
	 * @return
	 */
	public static String getDateAndTimeBasedOnTimeZone(String timeZone,
			String dateTime) {
		String actualDateTime = null;
		Date fromDate = null;
		try {
			if (StringUtils.isNotEmpty(timeZone)) {
				SimpleDateFormat toDateFormatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				fromDate = toDateFormatter.parse(dateTime);
				toDateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
				logger.info(" Date Time in seconds : " + fromDate.getTime());
				actualDateTime = toDateFormatter.format(fromDate.getTime());
			} else {
				actualDateTime = timeZone;
			}
		} catch (ParseException e) {
			logger.error(
					"FdahpStudyDesignerUtil - getDateAndTimeBasedOnTimeZone() : ",
					e);
		}
		logger.info(" User Date and Time based on the Time Zone : "
				+ actualDateTime);
		return actualDateTime;
	}

	public static String getDecodedStringByBase64(String encodedText) {
		logger.info("FdahpStudyDesignerUtil - Entry Point: getDecodedStringByBase64() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		try {
			// Decrypt data on other side, by processing encoded data
			byte[] valueDecoded = Base64.getDecoder().decode(encodedText);
			return new String(valueDecoded);

		} catch (Exception e) {
			logger.error(
					"FdahpStudyDesignerUtil - getDecodedStringByBase64() : ", e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: getDecodedStringByBase64() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		return "";
	}

	public static boolean getEDTdatetimeAsStringCompare(String timeZone,
			String inputDate, String inputFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		boolean flag = false;
		try {
			if (sdf.parse(inputDate).before(new Date())) {
				// System.out.println("The specified date is in the past");
				flag = false;
			}
			if (sdf.parse(inputDate).after(new Date())) {
				// System.out.println("The specified date is in the future");
				flag = true;
			}
			if (sdf.parse(inputDate).equals(new Date())) {
				// System.out.println("The specified date is now");
				flag = true;
			}
		} catch (Exception e) {
			logger.error(
					"ERROR: FdahpStudyDesignerUtil.getEDTdatetimeAsStringCompare()",
					e);
		}
		return flag;
	}

	public static String getEncodedStringByBase64(String plainText) {
		// if(null!=plainText && !"".equals(plainText)){return "";}
		logger.info("FdahpStudyDesignerUtil - Entry Point: getEncodedStringByBase64() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		try {
			// encrypt data on your side using BASE64
			byte[] bytesEncoded = Base64.getEncoder().encode(
					plainText.getBytes());
			return new String(bytesEncoded);
		} catch (Exception e) {
			logger.error(
					"FdahpStudyDesignerUtil - getEncodedStringByBase64() : ", e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: getEncodedStringByBase64() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		return "";
	}

	public static String getEncryptedFormat(String input) {
		StringBuffer sb = new StringBuffer();
		logger.debug("Password Encryption method==start");
		if (input != null) {
			/* Add the password salt to input parameter */
			input = input + FdahpStudyDesignerConstants.ENCRYPT_SALT;
			try {
				MessageDigest messageDigest = MessageDigest
						.getInstance("SHA-512");
				messageDigest.update(input.getBytes("UTF-8"));
				byte[] digestBytes = messageDigest.digest();
				String hex = null;
				for (int i = 0; i < 8; i++) {
					hex = Integer.toHexString(0xFF & digestBytes[i]);
					if (hex.length() < 2)
						sb.append("0");
					sb.append(hex);
				}
			} catch (Exception e) {
				logger.error(
						"FdahpStudyDesignerUtil - getEncryptedFormat() - ERROR",
						e);
			}
		}
		logger.debug("Password Encryption method==end");
		return sb.toString();
	}

	/**
	 * Return {@link BCryptPasswordEncoder} encrypted {@link String} of given
	 * string
	 * 
	 * @param input
	 *            , password string
	 * @return {@link BCryptPasswordEncoder} encrypted {@link String}
	 */
	/* getEncodedString(String test) method returns Encoded String */
	public static String getEncryptedPassword(String input) {
		String hashedPassword = null;
		logger.info("getEncryptedString start");
		if (input != null) {
			try {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				hashedPassword = passwordEncoder.encode(input);
			} catch (Exception e) {
				logger.error(
						"FdahpStudyDesignerUtil - getEncryptedPassword() - ERROR",
						e);
			}
		}
		logger.info("getEncryptedString end");
		return hashedPassword;
	}

	/**
	 * @param input
	 * @return String
	 */
	/* getEncodedString(String test) method returns Encoded String */
	public static String getEncryptedString(String input) {
		StringBuffer sb = new StringBuffer();
		logger.info("getEncryptedString start");
		if (input != null) {
			/** Add the password salt to input parameter */
			input = input + FdahpStudyDesignerConstants.FDA_SALT;
			try {
				MessageDigest messageDigest = MessageDigest
						.getInstance("SHA-512");
				messageDigest.update(input.getBytes("UTF-8"));
				byte[] digestBytes = messageDigest.digest();
				String hex = null;
				for (int i = 0; i < 8; i++) {
					hex = Integer.toHexString(0xFF & digestBytes[i]);
					if (hex.length() < 2)
						sb.append("0");
					sb.append(hex);
				}
			} catch (Exception e) {
				logger.error(
						"FdahpStudyDesignerUtil - getEncryptedString() - ERROR",
						e);
			}
		}
		logger.info("getEncryptedString end");
		return sb.toString();
	}

	/**
	 * Return last exception in spring security authentication for current
	 * {@link HttpServletRequest}
	 * 
	 * @author BTC
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @param key
	 *            , error key of the current request
	 * @return {@link String}
	 */
	public static String getErrorMessage(HttpServletRequest request, String key) {

		Exception exception = (Exception) request.getSession()
				.getAttribute(key);
		String userLoginFailure = configMap.get("user.login.failure");
		String userInactiveMsg = configMap.get("user.inactive.msg");
		String alreadyLoginMsg = configMap.get("user.alreadylogin.msg");
		String credentialExpiredException = configMap
				.get("user.admin.forcepassword.msg");
		String error = "";
		if (exception instanceof BadCredentialsException) {
			error = userLoginFailure;
		} else if (exception instanceof LockedException) {
			error = exception.getMessage();
		} else if (exception instanceof DisabledException) {
			error = userInactiveMsg;
		} else if (exception instanceof CredentialsExpiredException) {
			error = credentialExpiredException;
		} else if (exception instanceof SessionAuthenticationException) {
			error = alreadyLoginMsg;
		} else if (exception instanceof AccountStatusException) {
			error = exception.getMessage() + "!";
		} else {
			error = userLoginFailure;
		}

		return error;
	}

	/**
	 * Get formatted date of given date string with give output format
	 * 
	 * @author BTC
	 * @param inputDate
	 *            , input date string
	 * @param inputFormat
	 *            , Input date string format
	 * @param outputFormat
	 *            , desired date output format
	 * @return {@link String}
	 */
	public static String getFormattedDate(String inputDate, String inputFormat,
			String outputFormat) {
		String finalDate = "";
		java.sql.Date formattedDate;
		if (inputDate != null && !"".equals(inputDate)
				&& !"null".equalsIgnoreCase(inputDate)) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
				formattedDate = new java.sql.Date(formatter.parse(inputDate)
						.getTime());
				formatter = new SimpleDateFormat(outputFormat);
				finalDate = formatter.format(formattedDate);
			} catch (Exception e) {
				logger.error("Exception in getFormattedDate(): " + e);
			}
		}
		return finalDate;
	}

	public static String getRegExpression(String validCondition,
			String validCharacters, String exceptCharacters) {
		String regEx = "";
		if ((validCharacters != null && StringUtils.isNotEmpty(validCharacters))
				&& (validCondition != null && StringUtils
						.isNotEmpty(validCondition))) {
			if (validCondition
					.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLOW)) {
				regEx += "[";
				if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLCHARACTERS)) {
					regEx += "^.";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALPHABETS)) {
					regEx += "a-zA-Z ";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.NUMBERS)) {
					regEx += "0-9 ";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALPHABETSANDNUMBERS)) {
					regEx += "a-zA-Z0-9 ";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.SPECIALCHARACTERS)) {
					regEx += "^A-Za-z0-9";
				}
				if (exceptCharacters != null
						&& StringUtils.isNotEmpty(exceptCharacters)) {
					String[] exceptChar = exceptCharacters.split("\\|");
					StringBuilder except = new StringBuilder();
					for (int i = 0; i < exceptChar.length; i++) {
						except.append("^(?!.*"
								+ exceptChar[i].trim().replace(" ", "") + ")");
					}
					regEx = except + regEx + "]+";
				} else {
					regEx += "]+";
				}
			} else {
				if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLCHARACTERS)) {
					if (exceptCharacters != null
							&& StringUtils.isNotEmpty(exceptCharacters)) {
						regEx += "^(?:"
								+ exceptCharacters.trim().replace(" ", "")
								+ ")$";
					} else {
						regEx += "[.]";
					}

				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALPHABETS)) {
					regEx += "^([^a-zA-Z]";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.NUMBERS)) {
					regEx += "^([^0-9]";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALPHABETSANDNUMBERS)) {
					regEx += "^([^a-zA-Z0-9]";
				} else if (validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.SPECIALCHARACTERS)) {
					regEx += "^([A-Za-z0-9 ]";
				}
				if (!validCharacters
						.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLCHARACTERS)) {
					if (exceptCharacters != null
							&& StringUtils.isNotEmpty(exceptCharacters)) {
						String[] exceptChar = exceptCharacters.split("\\|");
						StringBuilder except = new StringBuilder();
						if (validCharacters
								.equalsIgnoreCase(FdahpStudyDesignerConstants.SPECIALCHARACTERS)) {
							for (int i = 0; i < exceptChar.length; i++) {
								except.append(exceptChar[i].trim().replace(" ",
										""));
							}
							regEx += "|[" + except + "]*)+$";
						} else {
							for (int i = 0; i < exceptChar.length; i++) {
								except.append("|\\b(\\b"
										+ exceptChar[i].trim().replace(" ", "")
										+ "\\b)");
							}
							regEx += except + "*)+$";
						}

					} else {
						regEx += "*)+$";
					}
				}
			}
		}
		return regEx;
	}

	/**
	 *
	 * @author BTC
	 *
	 * @param request
	 *            , {@link HttpServletRequest}
	 * @return {@link String} , A comma separated roles
	 */
	public static String getSessionUserRole() {
		logger.info("FdahpStudyDesignerUtil - getSessionUser() :: Starts");
		String userRoles = "";
		try {
			SecurityContext securityContext = SecurityContextHolder
					.getContext();
			Authentication authentication = securityContext.getAuthentication();
			if (authentication != null) {
				Collection<? extends GrantedAuthority> authorities = authentication
						.getAuthorities();
				userRoles = StringUtils.join(authorities.iterator(), ",");
			}
		} catch (Exception e) {
			logger.error("FdahpStudyDesignerUtil - getSessionUser() - ERROR ",
					e);
		}
		logger.info("FdahpStudyDesignerUtil - getSessionUser() :: Ends");
		return userRoles;
	}

	/**
	 * This method create a custom unique name for new file
	 * 
	 * @param actualFileName
	 *            , actual name of the uploaded file
	 * @param userFirstName
	 *            , User first name who uploaded the file
	 * @param userLastName
	 *            , User last name who uploaded the file
	 * @return {@link String}
	 */
	public static String getStandardFileName(String actualFileName,
			String userFirstName, String userLastName) {
		String intial = userFirstName.charAt(0) + "" + userLastName.charAt(0);
		String dateTime = new SimpleDateFormat(
				FdahpStudyDesignerConstants.SDF_FILE_NAME_TIMESTAMP)
				.format(new Date());
		return actualFileName + "_" + intial + "_" + dateTime;
	}

	public static String getTimeDiffInDaysHoursMins(Date dateOne, Date dateTwo) {
		String diff = "";
		long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		diff = String.format(
				"%d Day(s) %d hour(s) %d min(s)",
				TimeUnit.MILLISECONDS.toDays(timeDiff),
				TimeUnit.MILLISECONDS.toHours(timeDiff)
						- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
								.toDays(timeDiff)),
				TimeUnit.MILLISECONDS.toMinutes(timeDiff)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(timeDiff)));
		return diff;
	}

	public static String getTimeDiffInMins(Date dateOne, Date dateTwo) {
		String diff = "";
		long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		diff = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(timeDiff));
		return diff;
	}

	public static Integer getTimeDiffToCurrentTimeInHr(Date date) {
		logger.info("FdahpStudyDesignerUtil - Entry Point: getTimeDiffToCurrentTimeInHr() - "
				+ " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
		Integer diffHours = null;
		float diff = 0.0f;
		try {
			Date dt2 = new Date();
			diff = (float) dt2.getTime() - date.getTime();
			diffHours = Math.round(diff / (60 * 60 * 1000));
		} catch (Exception e) {
			logger.error(
					"FdahpStudyDesignerUtil - getTimeDiffToCurrentTimeInHr() : ",
					e);
		}
		logger.info("FdahpStudyDesignerUtil - Exit Point: getTimeDiffToCurrentTimeInHr() - ");
		return diffHours;
	}

	public static List<String> getTimeRangeList(String frequency) {
		List<String> timeRangeList = new ArrayList<>();
		if (StringUtils.isNotEmpty(frequency)) {
			switch (frequency) {
			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME:
				timeRangeList
						.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_WEEK);
				timeRangeList
						.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_MONTH);
				break;
			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY:
				timeRangeList
						.add(FdahpStudyDesignerConstants.MULTIPLE_TIMES_A_DAY);
				break;

			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY:
				timeRangeList
						.add(FdahpStudyDesignerConstants.WEEKS_OF_THE_CURRENT_MONTH);
				break;

			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY:
				timeRangeList
						.add(FdahpStudyDesignerConstants.MONTHS_OF_THE_CURRENT_YEAR);
				break;

			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
				timeRangeList.add(FdahpStudyDesignerConstants.RUN_BASED);
				break;
			}
		} else {
			timeRangeList
					.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_WEEK);
			timeRangeList
					.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_MONTH);
		}
		return timeRangeList;
	}

	public static String getTimeRangeString(String frequency) {
		String timeRange = "";
		if (StringUtils.isNotEmpty(frequency)) {
			switch (frequency) {
			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_WITHIN_A_DAY:
				timeRange = FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_MONTH
						+ "' , '"
						+ FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_WEEK;
				break;
			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY:
				timeRange = FdahpStudyDesignerConstants.MULTIPLE_TIMES_A_DAY;
				break;

			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY:
				timeRange = FdahpStudyDesignerConstants.WEEKS_OF_THE_CURRENT_MONTH;
				break;

			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY:
				timeRange = FdahpStudyDesignerConstants.MONTHS_OF_THE_CURRENT_YEAR;
				break;

			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
				timeRange = FdahpStudyDesignerConstants.RUN_BASED;
				break;
			case FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME:
				timeRange = "";
				break;
			}

		}
		return timeRange;
	}

	/**
	 * Checks if a CharSequence is empty ("") or null.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		logger.info("FdahpStudyDesignerUtil - isEmpty() :: Starts");
		boolean flag = false;
		if (null == str || "".equals(str)) {
			flag = true;
		}
		logger.info("FdahpStudyDesignerUtil - isEmpty() :: Ends");
		return flag;
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		logger.info("FdahpStudyDesignerUtil - isNotEmpty() :: Starts");
		boolean flag = false;
		if (null != str && !"".equals(str.trim())) {
			flag = true;
		}
		logger.info("FdahpStudyDesignerUtil - isNotEmpty() :: Ends");
		return flag;
	}

	/**
	 * check the session for a request
	 *
	 * @author BTC
	 *
	 * @param request
	 * @return {@link boolean}
	 */
	public static boolean isSession(HttpServletRequest request) {
		logger.info("FdahpStudyDesignerUtil - isSession() :: Starts");
		boolean flag = false;
		try {
			SessionObject sesObj = (SessionObject) request.getSession()
					.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
			if (sesObj != null) {
				flag = true;
			}
		} catch (Exception e) {
			logger.error("FdahpStudyDesignerUtil - isSession() - ERROR ", e);
		}
		logger.info("FdahpStudyDesignerUtil - isSession() :: Ends");
		return flag;
	}

	/**
	 * Return Date Time string with subtract minutes from given date time
	 * 
	 * @author BTC
	 * @param sysDateTime
	 *            , Date Time string
	 * @param format
	 *            , Date Time string format
	 * @param min
	 *            , minutes to subtract from given date time.
	 * @return {@link String}
	 */
	public static String privMinDateTime(String sysDateTime, String format,
			int min) {
		String newSysDateTime = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Calendar cal = Calendar.getInstance();
			Date actualDateTime = formatter.parse(sysDateTime);
			cal.setTime(actualDateTime);
			cal.add(Calendar.MINUTE, -min);
			Date modDate = cal.getTime();
			newSysDateTime = formatter.format(modDate);
		} catch (ParseException e) {
			logger.error("FdahpStudyDesignerUtil - privMinDateTime : ", e);
		}
		return newSysDateTime;

	}

	public static String removeLastCommaFromString(String str) {
		if (str.trim().length() > 0 && str.trim().endsWith(",")) {
			str = str.trim();
			str = str.substring(0, str.length() - 1);
			return str;
		} else {
			return str;
		}
	}

	public static String round(double value) {
		logger.info("FdahpStudyDesignerUtil: double round :: Starts");
		String rounded = "0";
		try {
			rounded = String.valueOf(Math.round(value));
		} catch (Exception e) {
			logger.error("FdahpStudyDesignerUtil: double round() :: ERROR: ", e);
		}
		logger.info("FdahpStudyDesignerUtil: double round :: Ends");
		return rounded;
	}

	public static String round(float value) {
		logger.info("FdahpStudyDesignerUtil: float round :: Starts");
		String rounded = "0";
		try {
			rounded = String.valueOf(Math.round(value));
		} catch (Exception e) {
			logger.error("FdahpStudyDesignerUtil: float round() :: ERROR: ", e);
		}
		logger.info("FdahpStudyDesignerUtil: float round :: Ends");
		return rounded;
	}

	public static String round(String value) {
		logger.info("FdahpStudyDesignerUtil: String round :: Starts");
		String rounded = "0";
		try {
			if (StringUtils.isNotEmpty(value)) {
				rounded = String.valueOf(Math.round(Double.parseDouble(value)));
			}
		} catch (Exception e) {
			logger.error("FdahpStudyDesignerUtil: String round() :: ERROR: ", e);
		}
		logger.info("FdahpStudyDesignerUtil: String round :: Ends");
		return rounded;
	}

	/**
	 * Upload the file from {@link MultipartFile}
	 * 
	 * @param file
	 *            , {@link MultipartFile}
	 * @param fileName
	 *            , file name
	 * @param folderName
	 *            , folder name where to store file
	 * @return {@link String} , file name
	 * @throws IOException
	 */
	public static String uploadImageFile(MultipartFile file, String fileName,
			String folderName) throws IOException {
		File serverFile;
		String actulName = null;
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream stream = null;
		if (file != null) {
			try {
				fileName = fileName
						+ "."
						+ FilenameUtils
								.getExtension(file.getOriginalFilename());
				byte[] bytes = file.getBytes();
				String currentPath = configMap.get("fda.currentPath") != null ? System
						.getProperty(configMap.get("fda.currentPath")) : "";
				String rootPath = currentPath.replace('\\', '/')
						+ configMap.get("fda.imgUploadPath");
				File dir = new File(rootPath + File.separator + folderName);
				if (!dir.exists())
					dir.mkdirs();
				serverFile = new File(dir.getAbsolutePath() + File.separator
						+ fileName);
				fileOutputStream = new FileOutputStream(serverFile);
				stream = new BufferedOutputStream(fileOutputStream);
				stream.write(bytes);
				logger.info("Server File Location="
						+ serverFile.getAbsolutePath());
				actulName = fileName;
			} catch (Exception e) {
				logger.error("ERROR: FdahpStudyDesignerUtil.uploadImageFile()",
						e);
			} finally {
				if (null != stream)
					stream.close();
				if (null != fileOutputStream)
					fileOutputStream.close();
			}

		}

		return actulName;
	}

	/**
	 * @param request
	 * @return
	 */
	public static boolean validateUserSession(HttpServletRequest request) {
		boolean flag = false;
		SessionObject sesObj = (SessionObject) request.getSession()
				.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
		if (null != sesObj) {
			flag = true;
		}
		return flag;
	}
}
