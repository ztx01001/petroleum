package com.university.springboot_petroleum.util;

public class StringUtil {

	/**
	 * byte数组转十六进制字符串
	 * @param b byte数组
	 * @return 十六进制字符串
	 */
	public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
       //return hs;
    }
	
	//这个 最快
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();  
    public static String bytes2Hex(byte[] bytes) {    	
    	
        char[] hexChars = new char[bytes.length * 2];  
        for ( int j = 0; j < bytes.length; j++ ) {  
            int v = bytes[j] & 0xFF;  
            hexChars[j * 2] = hexArray[v >>> 4];  
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];  
        }  
               
        return new String(hexChars);  
    }  
    
}
