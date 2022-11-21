package com.university.springboot_petroleum.domain;

/**
 * 下发请求后，返回的传感器报文
 */
public class ResponseMessage {
	//串口转以太网口 返回报文的字节数组
	private byte[] data;
	//返回报文的长度
	private int len;
	//返回报文的主机
	private String host;
	
	public ResponseMessage(byte[] data,int len,String host) {
		this.data = data;
		this.len = len;
		this.host = host;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	

}
