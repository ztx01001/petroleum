package com.university.springboot_petroleum.exception;

public class AlarmRuleException extends BaseException{

	private static final long serialVersionUID = 1L;
	 private static final int DEFAULT_ERROR_CODE = 600;
	public AlarmRuleException(String errorMessage) {
		super(DEFAULT_ERROR_CODE,errorMessage);
	}
}
