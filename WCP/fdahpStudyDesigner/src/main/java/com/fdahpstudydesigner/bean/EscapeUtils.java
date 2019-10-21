package com.fdahpstudydesigner.bean;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class EscapeUtils {
	private static Logger logger = Logger
			.getLogger(EscapeUtils.class.getName());

	protected static final Map<Integer, String> m = new HashMap<>();

	static {
		m.put(34, "&quot;"); // < - less-than
		m.put(60, "&lt;"); // < - less-than
		m.put(62, "&gt;"); // > - greater-than
		// User needs to map all html entities with their corresponding decimal
		// values.
		// Please refer to below table for mapping of entities and integer value
		// of a char
	}

	public static void escape(Writer writer, String str) throws IOException {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int ascii = c;
			String entityName = (String) m.get(ascii);
			if (entityName == null) {
				if (c > 0x7F) {
					writer.write("&#");
					writer.write(Integer.toString(c, 10));
					writer.write(';');
				} else {
					writer.write(c);
				}
			} else {
				writer.write(entityName);
			}
		}
	}

	public static String escapeHtml() {
		logger.info("EscapeUtils - escapeHtml :: Starts");
		String str = "<script>alert(\"abc\")</script>";
		String writerStr = null;
		try {
			StringWriter writer = new StringWriter((int) (str.length() * 1.5));
			escape(writer, str);
			logger.info("EscapeUtils - escapeHtml - encoded string is "
					+ writer.toString());
			writerStr = writer.toString();
		} catch (Exception e) {
			logger.error("EscapeUtils - escapeHtml - ERROR ", e);
		}
		logger.info("EscapeUtils - escapeHtml :: Ends");
		return writerStr;
	}

	public static void main(String[] args) {
		escapeHtml();
	}

	EscapeUtils() {

	}
}
