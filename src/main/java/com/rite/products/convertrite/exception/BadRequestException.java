package com.rite.products.convertrite.exception;

public class BadRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequestException(String errorMsg) {
		
		super(errorMsg);
	}
}
