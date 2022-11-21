package com.university.springboot_petroleum.domain;

public class DevCtlCmdMessage {
	//需要发送的控制设备命令的报文
	private byte[] data;
	//报文的状态，0：未发送，1:已发送，2：发送失败，3：发送成功并接收返回消息
	private int state;
	public DevCtlCmdMessage() {
		this.data = null;
		this.state = 0;
	}
	public DevCtlCmdMessage(byte[] data) {
		this.data = data;
		this.state = 0;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getState() {
		return state;
	}

}
