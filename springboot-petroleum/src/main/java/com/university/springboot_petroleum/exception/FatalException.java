package com.university.springboot_petroleum.exception;

public class FatalException extends BaseException{

	private static final long serialVersionUID = 1L;
	 private static final int DEFAULT_ERROR_CODE = 900;

	    public FatalException(String errorMessage){
	        super(DEFAULT_ERROR_CODE,errorMessage);
	    }

	    public FatalException(int errorCode, String errorMessage){
	        super(errorCode,errorMessage);
	    }

	    public FatalException(String errorMessage, Throwable e){
	        super(errorMessage,e);
	    }

	    public FatalException(int errorCode, String errorMessage, Throwable e){
	        super(errorCode,errorMessage,e);
	    }
}
