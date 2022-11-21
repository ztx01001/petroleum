package com.university.springboot_petroleum.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

//import com.university.springboot_petroleum.cache.GlobalCache;
//import com.university.springboot_petroleum.domain.SensorInfo;
import com.university.springboot_petroleum.exception.DataEmptyException;
import com.university.springboot_petroleum.service.SensorInfoService;
import com.university.springboot_petroleum.vo.AttributeVo;
import com.university.springboot_petroleum.vo.DeviceVo;

@RestController
public class SensorInfoController {

	private static Logger log = LoggerFactory.getLogger(SensorInfoController.class);
	
	@Autowired
	SensorInfoService sensorServiceImpl;
	
	
	@RequestMapping(value="/api/sensor/attribute", method = RequestMethod.GET)    
    public List<AttributeVo> qryAttribute() {
		log.info("SensorInfoController qryAttribute");
		List<AttributeVo> result = sensorServiceImpl.qryAttribute();
		if(null == result || result.isEmpty()) {
			throw  new DataEmptyException("can't get attribute");
		}
		return result;
		
	}
	
	@GetMapping(value="/api/sensor/attribute/{name}")
	public List<DeviceVo> qryDeviceByAttr( @PathVariable("name") String name) {
		log.info("SensorInfoController qryDeviceByAttr attribute name="+name);
		//List<SensorInfo> result = GlobalCache.AttrSensorMap.get(name);
		List<DeviceVo> result = sensorServiceImpl.qryDeviceVoByAttr(name);
		if(null == result || result.isEmpty()) {
			throw  new DataEmptyException("can't get devices by attributename="+name);
		}
		return result;
	}
}
