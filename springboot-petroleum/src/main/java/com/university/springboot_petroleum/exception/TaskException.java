package com.university.springboot_petroleum.exception;

public class TaskException extends BaseException{

	private static final long serialVersionUID = 1L;
	 private static final int DEFAULT_ERROR_CODE = 700;
	 public TaskException(String errorMessage) {
		 super(DEFAULT_ERROR_CODE,errorMessage);
	 }
}
