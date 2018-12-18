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
package com.hphc.mystudies.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 
 * @author BTC
 *
 */
public abstract class CommonException extends Exception {

	private String errorCode;

	/**
	 * 
	 * @param pErrorCode
	 * @param arg1
	 */
	public CommonException(String pErrorCode, Throwable arg1) {
		super(pErrorCode, arg1);
		errorCode = pErrorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public synchronized Throwable fillInStackTrace() {
		return super.fillInStackTrace();
	}

	@Override
	public Throwable getCause() {
		return super.getCause();
	}

	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage();
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}

	@Override
	public synchronized Throwable initCause(Throwable arg0) {
		return super.initCause(arg0);
	}

	@Override
	public void printStackTrace() {
		super.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream arg0) {
		super.printStackTrace(arg0);
	}

	@Override
	public void printStackTrace(PrintWriter arg0) {
		super.printStackTrace(arg0);
	}

	@Override
	public void setStackTrace(StackTraceElement[] arg0) {
		super.setStackTrace(arg0);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
