package com.university.springboot_petroleum.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.domain.SensorInfo;
import com.university.springboot_petroleum.vo.AttributeVo;
import com.university.springboot_petroleum.vo.DeviceVo;

@Service
public interface SensorInfoService {

	public List<SensorInfo> qrySensorInfo();
	public Map<Integer,SensorInfo> qrySensorInfoMap();
	
	public List<AttributeVo> qryAttribute();
	public List<DeviceVo> qryDeviceVoByAttr(String attributeName);
	
	public String qrySensorNameByID(int sensorID);
}
