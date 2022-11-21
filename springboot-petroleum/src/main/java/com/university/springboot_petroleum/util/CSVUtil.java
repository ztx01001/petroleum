package com.university.springboot_petroleum.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVUtil {

	// 行尾分隔符定义
	private final static String NEW_LINE_SEPARATOR = "\n";
	
	public static boolean getType(Object c) {
		if(c instanceof Date) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * @return File
	 * @Description 创建CSV文件
	 * @Param fileName 文件名，head 表头，values 表体
	 **/
	public static File makeCSV(String fileName, String path, String[] head, List<Map<String, Object>> dataList) {
        log.info("make csv enter");
		CSVPrinter printer = null;
		BufferedWriter bufferedWriter = null;
		File file = null;
		try {
            //创建文件
			log.info("make temp file start filename="+fileName+" path="+path);
			//file = File.createTempFile(fileName, ".csv", new File(path));
			file = new File(path+File.separator+fileName+".csv");
			if(!file.exists()){
                file.createNewFile();
            }else {
				file.delete();
				file.createNewFile();
			}
			log.info("make temp file end");
			CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			//formator.DEFAULT.withHeader(head);
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));//如果是UTF-8时，WPS打开是正常显示，而微软的excel打开是乱码,
			printer = new CSVPrinter(bufferedWriter, formator);
            // 写入表头
			printer.printRecord(head);
			//写入表数据
			log.info("write data into file start");
			//2022-08-15T01:15:30.1170186Z
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
			if(dataList != null) {
				int dataLen = dataList.size();
				int headLen = head.length;
				log.info("--------data length="+dataLen+" headlength="+headLen);
				for(int i = 0; i < dataLen; i++) {
					String[] content = new String[head.length];
					for(int j = 0; j < headLen; j++) {
						Object c = dataList.get(i).get(head[j]);
						if(c != null) {
							if(j == 0) {
								StringBuffer sbtime = new StringBuffer();
								String timeString = c.toString();
								String[] timeArray = timeString.split("\\.");							
								
								sbtime.append(timeArray[0]);
								sbtime.append("Z");
								Date date = sdfUtc.parse(sbtime.toString());
								String tempString= sdf.format(date);						
								content[j]= " "+tempString;
							}else {
								content[j]=c.toString();
							}
						}else {
							log.info("get data is null");
							content[j]="";
						}
						
					}
					printer.printRecord(content);
				}
			}
			printer.flush();
			log.info("write data into file end");
		}catch (IOException e) {
			log.error("makeCSV io exception ="+e.getLocalizedMessage());
		}catch (Exception e) {
			log.error("makeCSV exception ="+e.getLocalizedMessage());
		}finally {
			if(printer != null) {
				try {
					printer.close();			        
				} catch (Exception e2) {
					log.error("printer.close()");
				}
			}
			if(bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (Exception e2) {
					log.error("bufferedWriter.close() exception");
				}
			}
			
			
		}
		return file;

	}
	
	/**
	 * @return File
	 * @Description 创建CSV文件
	 * @Param fileName 文件名，head 表头，values 表体
	 **/
	public static File makeCSVHead(String fileName, String path, String[] head,String[] head2, List<Map<String, Object>> dataList) {
        log.info("make csv enter");
		CSVPrinter printer = null;
		BufferedWriter bufferedWriter = null;
		File file = null;
		try {
            //创建文件
			log.info("make temp file start filename="+fileName+" path="+path);
			//file = File.createTempFile(fileName, ".csv", new File(path));
			file = new File(path+File.separator+fileName+".csv");
			if(!file.exists()){
                file.createNewFile();
            }else {
				file.delete();
				file.createNewFile();
			}
			log.info("make temp file end");
			CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			//formator.DEFAULT.withHeader(head);
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));//如果是UTF-8时，WPS打开是正常显示，而微软的excel打开是乱码,
			printer = new CSVPrinter(bufferedWriter, formator);
            // 写入表头
			printer.printRecord(head2);
			//写入表数据
			log.info("write data into file start");
			//2022-08-15T01:15:30.1170186Z
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
			if(dataList != null) {
				int dataLen = dataList.size();
				int headLen = head.length;
				log.info("--------data length="+dataLen+" headlength="+headLen);
				for(int i = 0; i < dataLen; i++) {
					String[] content = new String[head.length];
					for(int j = 0; j < headLen; j++) {
						Object c = dataList.get(i).get(head[j]);
						if(c != null) {
							if(j == 0) {
								StringBuffer sbtime = new StringBuffer();
								String timeString = c.toString();
								String[] timeArray = timeString.split("\\.");							
								
								sbtime.append(timeArray[0]);
								sbtime.append("Z");
								Date date = sdfUtc.parse(sbtime.toString());
								String tempString= sdf.format(date);						
								content[j]= " "+tempString;
							}else {
								content[j]=c.toString();
							}
						}else {
							log.info("get data is null");
							content[j]="";
						}
						
					}
					printer.printRecord(content);
				}
			}
			printer.flush();
			log.info("write data into file end");
		}catch (IOException e) {
			log.error("makeCSV io exception ="+e.getLocalizedMessage());
		}catch (Exception e) {
			log.error("makeCSV exception ="+e.getLocalizedMessage());
		}finally {
			if(printer != null) {
				try {
					printer.close();			        
				} catch (Exception e2) {
					log.error("printer.close()");
				}
			}
			if(bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (Exception e2) {
					log.error("bufferedWriter.close() exception");
				}
			}
			
			
		}
		return file;

	}
	
	
	/**
     * @return boolean
     * @Description 下载文件
     * @Param response，file
     **/
    public static boolean downloadFile(HttpServletResponse response, File file) {
    	log.info("downloadFile enter");
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream os = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            os = response.getOutputStream();
            //MS产本头部需要插入BOM
            //如果不写入这几个字节，会导致用Excel打开时，中文显示乱码
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            byte[] buffer = new byte[1024];
            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
            return true;
        } catch (Exception e) {
        	log.error("downloadFile exception"+e.getLocalizedMessage());
            
        } finally {
            //关闭流
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.delete();
        }
        return false;
    }
    
}
