package com.hphc.mystudies.bean;

/**
 * Provides Error information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:39:11 PM
 */
public class ErrorResponse {

	private ErrorBean error = new ErrorBean();

	public ErrorBean getError() {
		return error;
	}

	public ErrorResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

}
