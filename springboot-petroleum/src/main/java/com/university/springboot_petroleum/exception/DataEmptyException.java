package com.university.springboot_petroleum.exception;

public class DataEmptyException extends BaseException{

	private static final long serialVersionUID = 1L;
	 private static final int DEFAULT_ERROR_CODE = 500;
	 
	 public DataEmptyException(String errorMessage){
		 super(DEFAULT_ERROR_CODE,errorMessage);
	 }
}
