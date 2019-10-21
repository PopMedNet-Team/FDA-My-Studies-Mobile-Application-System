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

/**
 * 
 * @author BTC
 *
 */
public interface ErrorCodes {

	String DAO_FACTORY_INIT_EXP = "DAO100";
	String DAO_EXP = "ERRDAO";
	String ORC_FACTORY_INIT_EXP = "ORC100";
	String INVALID_AUTH_CODE = "INVALID_AUTH_CODE";
	String UNKNOWN = "UNKNOWN";
	String INVALID_AUTHENTICATION = "INVALID_AUTHENTICATION";
	String INVALID_AUTHORIZATION = "INVALID_AUTHORIZATION";
	String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
	String NO_RECORD = "NO_RECORD_FOUND";
	String MULTIPLE_RECORDS = "ERRDUP";
	String NO_RECORD_UPDATE = "ERRNRU";
	String NO_RECORD_INSERT = "ERRNRI";
	String INVALID_DATE = "INVALID_DATE";
	String INACTIVE = "INACTIVE";
	String ACTIVE = "ACTIVE";
	String INACTIVE_MSG = "Service unavailable. Please try later.";
	String INVALID_INPUT = "INVALID_INPUT";
	String INVALID_EMAIL = "INVALID_EMAIL";
	String NO_DATA = "NODATA";
	String STATUS_100 = "100"; // OK
	String STATUS_101 = "101"; // Invalid Authentication (authKey is not valid).
	String STATUS_102 = "102"; // Invalid Inputs (If any of the input parameter
								// is missing).
	String STATUS_103 = "103"; // No Data available.
	String STATUS_104 = "104"; // Unknown Error
	String STATUS_105 = "105"; // If there is no data to update.
}
