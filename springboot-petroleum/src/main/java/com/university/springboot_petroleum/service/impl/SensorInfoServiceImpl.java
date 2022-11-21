package com.university.springboot_petroleum.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.domain.SensorInfo;
import com.university.springboot_petroleum.service.SensorInfoService;
import com.university.springboot_petroleum.vo.AttributeVo;
import com.university.springboot_petroleum.vo.DeviceVo;
@Service
public class SensorInfoServiceImpl implements SensorInfoService{

	private static final Logger log = LoggerFactory.getLogger(SensorInfoServiceImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<SensorInfo> qrySensorInfo(){
		List<SensorInfo> result = new ArrayList<>();
		String sql = "SELECT s.sensor_id,s.sensor_name,s.disp_name,s.attribute_name,s.reg_addr,s.widget_id from sensor_info s;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int sensorID = rows.getInt("sensor_id");
				String sensorName = rows.getString("sensor_name");
				String dispName = rows.getString("disp_name");
				String attributeName = rows.getString("attribute_name");
				int regAddr = rows.getInt("reg_addr");
				BigInteger widgetID = BigInteger.valueOf(rows.getLong("widget_id"));
				log.info("-----------------"+widgetID);
				SensorInfo sensorInfo = new SensorInfo(sensorID,sensorName,dispName,attributeName,regAddr,widgetID);
				result.add(sensorInfo);
			}
		}catch(Exception e) {
			log.error("qrySensorInfo exception="+e.getLocalizedMessage());
		}
		
		return result;
	}
	
	@Override
	public Map<Integer,SensorInfo> qrySensorInfoMap(){
		Map<Integer,SensorInfo> result = new HashMap<>();
		String sql = "SELECT s.sensor_id,s.sensor_name,s.disp_name,s.attribute_name,s.reg_addr,s.widget_id from sensor_info s;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int sensorID = rows.getInt("sensor_id");
				String sensorName = rows.getString("sensor_name");
				String dispName = rows.getString("disp_name");
				String attributeName = rows.getString("attribute_name");
				int regAddr = rows.getInt("reg_addr");
				BigInteger widgetID = BigInteger.valueOf(rows.getLong("widget_id"));
				log.info("-----------------"+widgetID);
				SensorInfo sensorInfo = new SensorInfo(sensorID,sensorName,dispName,attributeName,regAddr,widgetID);
				result.put(Integer.valueOf(sensorID), sensorInfo);
			}
		}catch(Exception e) {
			log.error("qrySensorInfoMap exception="+e.getLocalizedMessage());
		}
		return result;
	}
	@Override
	public List<AttributeVo> qryAttribute(){
		String sql = "SELECT DISTINCT s.attribute_label,s.attribute_name FROM sensor_info s";
		List<AttributeVo> resultList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				String attributeName = rows.getString("attribute_name");
				String attibuteLabel = rows.getString("attribute_label");
				AttributeVo attributeVo = new AttributeVo(attibuteLabel, attributeName);
				resultList.add(attributeVo);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultList;
	}
	
	@Override
	public List<DeviceVo> qryDeviceVoByAttr(String attributeName){
		String sql= "SELECT s.sensor_id,s.sensor_name from sensor_info s WHERE s.attribute_name=?;";
		List<DeviceVo> result = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,attributeName);
			while(rows.next()) {
				int value = rows.getInt("sensor_id");
				String label = rows.getString("sensor_name");
				DeviceVo deviceVo = new DeviceVo(label, value);
				result.add(deviceVo);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	@Override
	public String qrySensorNameByID(int sensorID) {
		String sensorName = "";
		String sql= "SELECT s.sensor_name from sensor_info s WHERE s.sensor_id=?;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,sensorID);
			while(rows.next()) {
				sensorName= rows.getString("sensor_name");
			}
		} catch (Exception e) {
			log.error("qrySensorNameByID exception="+e.getLocalizedMessage());
		}
		return sensorName;
	}
}
