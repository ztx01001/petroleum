package com.university.springboot_petroleum.exception;

public class ExportFileException extends BaseException{

	private static final long serialVersionUID = 1L;
	 private static final int DEFAULT_ERROR_CODE = 800;
	public ExportFileException(String errorMessage) {
		super(DEFAULT_ERROR_CODE,errorMessage);
	}
}
