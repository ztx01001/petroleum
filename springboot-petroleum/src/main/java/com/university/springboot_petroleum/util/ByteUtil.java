package com.university.springboot_petroleum.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtil {
	private static Logger log = LoggerFactory.getLogger(ByteUtil.class);
	/**
	 * 字节数组转换成16进制形式的字符串
	 * @param byte[]
	 * @return String
	 */
	//最快字节数组转16进制形式的字符串
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytes2HexStr(byte[] bytes) {
		char[] hexChars = new char[bytes.length*2];
		for(int j=0;j<bytes.length;j++) {
			int v = bytes[j]&0xFF;
			hexChars[j*2]=hexArray[v>>>4];
			hexChars[j*2+1] = hexArray[v &0x0F];
		}
		return new String(hexChars);
	}
	/**
	 * 十六进制字符串转换成byte[]
	 * @param hexString 十六进制字符串
	 * @return byte[]
	 * 
	 */
	public static byte[] hexStr2Bytes(String hexStr) {
		if(hexStr == null || hexStr.equals("")) {
			return null;
		}
		hexStr = hexStr.toLowerCase();
		int length = hexStr.length() / 2;
		char[] hexChars = hexStr.toCharArray();
		byte[] d = new byte[length];
		for(int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i]=(byte)(char2Byte(hexChars[pos]) << 4 | char2Byte(hexChars[pos+1]));
		}
		return d;
	}
	private static byte char2Byte(char c) {
		return (byte)"0123456789abcdef".indexOf(c);
	}
	/**
	 * int 转字节数组
	 */
	public static byte[] int2Bytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte)((i & 0xff000000) >> 24);
		b[1] = (byte)((i & 0x00ff0000) >> 16);
		b[2] = (byte)((i & 0x0000ff00) >> 8);
		b[3] = (byte)(i & 0x000000ff);
		return b;
		
	}
	/**
	 * 打印字节数组
	 * 
	 */
	public static void printHex(byte[] bytes,int len) {
		char[] hexChars = new char[len * 2];
		for(int j = 0;j < len; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j*2] = hexArray[v >>> 4];
			hexChars[j*2 + 1] = hexArray[v & 0x0F];
		}
		log.info("printhex="+new String(hexChars));
	}

}
