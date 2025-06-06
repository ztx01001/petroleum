package com.university.springboot_petroleum.result;

import lombok.Data;

@Data
public class ResultData<T> {

	/** 结果状态 ,具体状态码参见ResultData.java*/
    private int status;
    /**响应消息**/
    private String message;
    /**响应数据**/
    private T data;
    
    public static <T> ResultData<T> success(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> success() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        return resultData;
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC999.getCode());
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> fail(int code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static <T>ResultData<T> fail() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC999.getCode());
        resultData.setMessage(ReturnCode.RC999.getMessage());
        return resultData;
    }
    
    
}
