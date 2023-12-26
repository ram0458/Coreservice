package com.rite.products.convertrite.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	ConvertException convertException;
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	@ExceptionHandler(value=ConvertRiteException.class)
	public ResponseEntity<ConvertException> handleProcessException(ConvertRiteException e){
		log.info("entering GlobalExceptionHandler#########");
		convertException.setErrorMessage(e.getErrorMessage());
		convertException.setHttpStatus(e.getErrorCode());
		return new ResponseEntity<ConvertException> (convertException,e.getErrorCode());
	}

}
