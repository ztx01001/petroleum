package com.university.springboot_petroleum.serial;

import lombok.extern.slf4j.Slf4j;
import com.fazecast.jSerialComm.SerialPort;
import com.university.springboot_petroleum.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SerialPortManager {

	// 查找所有可用端口
	public static List<String> findPorts() {
		// 获得当前所有可用串口
		SerialPort[] serialPorts = SerialPort.getCommPorts();
		List<String> portNameList = new ArrayList<String>();
		// 将可用串口名添加到List并返回该List
		for (SerialPort serialPort : serialPorts) {
			portNameList.add(serialPort.getSystemPortName());
			log.info("find ports");
		}
		// 去重
		portNameList = portNameList.stream().distinct().collect(Collectors.toList());
		return portNameList;
	}

	/**
	 * 打开串口
	 *
	 * @param portName 端口名称
	 * @param baudRate 波特率
	 * @return 串口对象
	 */
	public static SerialPort openPort(String portName, int baudRate) {
		SerialPort serialPort = SerialPort.getCommPort(portName);

		serialPort.setBaudRate(baudRate);
		// 设置一下串口的波特率等参数
		// 数据位：8
		// 停止位：1
		// 校验位：None
		serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		//serialPort.setRTS();// 设置RTS。也可以设置DTR
		//serialPort.setDTR();
		serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
		if (!serialPort.isOpen()) {
			serialPort.openPort();
		} else {
			return serialPort;
		}
		return serialPort;
	}
	
	/**
     * 关闭串口
     * @param serialPort    待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null && serialPort.isOpen()){
            serialPort.closePort();
        }
    }
    
    /**
     * 往串口发送数据
     * @param serialPort    串口对象
     * @param content       待发送数据
     */
    public static void sendToPort(SerialPort serialPort,byte[] content) {
        if (!serialPort.isOpen()) {
        	log.error("send data to port error port is not opened");
            return;
        }
        serialPort.writeBytes(content, content.length);
        log.info("send data to port end="+StringUtil.bytes2Hex(content));
        
    }
}
