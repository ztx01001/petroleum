package com.university.springboot_petroleum.util;

public class Constants {
	public static final int Zero = 0;
	public static final int InputStreamEnd = -1;
	public static final int PduLen = 128 + 5;
	public static final int MessageLen = 128;
	public static final int SensorCount = 32;
	public static final int HeadLen = 2;
	public static final byte[] MessageHead = {0x01,0x03};
	public static final int PduSPostion = 3;
	
	public static final int Running = 1;
	public static final int Stoped = 0;
	public static final int Deleted = 1;
	public static final int Living = 0;
	
	public static final String DataCollect="dataCollect";
	public static final String DataExport="dataExport";
	

}
