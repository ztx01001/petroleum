package com.university.springboot_petroleum.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import java.sql.Connection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.university.springboot_petroleum.domain.AlarmAction;


import com.university.springboot_petroleum.domain.AlarmSeverity;
import com.university.springboot_petroleum.dto.AlarmActionDTO;
import com.university.springboot_petroleum.dto.AlarmConditionDTO;
import com.university.springboot_petroleum.dto.AlarmMainInfoDTO;
import com.university.springboot_petroleum.service.AlertService;
import com.university.springboot_petroleum.vo.AlarmActionTypeVo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Service
public class AlertServiceImpl implements AlertService{

	private static final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<AlarmSeverity> qryAlarmSeverity(){
		List<AlarmSeverity> result = new ArrayList<>();
		String sql = "SELECT t.severity_id,t.severity_name,t.severity_chinese,t.severity_value FROM alarm_severity_config t;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int id = rows.getInt("severity_id");
				String name = rows.getString("severity_name");
				String chinese = rows.getString("severity_chinese");
				int value = rows.getInt("severity_value");
				AlarmSeverity severity = new AlarmSeverity(id, name, chinese, value);
				result.add(severity);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("AlertServiceImpl qryAlertSeverity exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public List<AlarmActionTypeVo> qryActionType(){
		List<AlarmActionTypeVo> result = new ArrayList<>();
		String sql = "SELECT action_type_name,action_type_display FROM alarm_action_type;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				String typeName = rows.getString("action_type_name");
				String displayName = rows.getString("action_type_display");
				AlarmActionTypeVo at = new AlarmActionTypeVo(displayName, typeName);
				result.add(at);
			}
		} catch (Exception e) {
			log.error("");
		}
		return result;
	}
	
	@Transactional
	public int addAlarmMainInfo(String alarmName,String alarmLevel,String condition) {
		log.info("addAlarmMainInfo enter");
		int isValid = 0;
		String sql="INSERT INTO alarm_main_info (alarm_name,alarm_level,alarm_condition,create_time,is_valid) VALUES (?,?,?,?,?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				 @Override
			        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					 // 指定主键
			            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"alarm_id"});
			            preparedStatement.setString(1, alarmName);
			            preparedStatement.setString(2,alarmLevel);
			            preparedStatement.setString(3, condition);
			            Date date = new Date();
						Timestamp timeStamp = new Timestamp(date.getTime());
						preparedStatement.setTimestamp(4, timeStamp);
			            preparedStatement.setInt(5, isValid);
			            return preparedStatement;
				 }
			}, keyHolder);
		
		
		return  keyHolder.getKey().intValue();
	}
	
	@Transactional
	public void addAlarmCondition(int alarmID,List<AlarmConditionDTO> conditions) {
		log.info("addAlarmCondition start");
		String sql="INSERT INTO alarm_condition_info (alarm_id,device_type,sensor_id,sensor_name,operator,threshold_value,relation_value) VALUES (?,?,?,?,?,?,?);";
		//不能加try catch 否则插入出现问题后，外面的无法捕捉异常进行事务回滚
		//try {
			jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
				@Override
				public  void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setInt(1, alarmID);
					ps.setString(2, conditions.get(i).getDeviceType());
					ps.setInt(3, conditions.get(i).getSensorID());
					ps.setString(4, conditions.get(i).getSensorName());
					ps.setString(5, conditions.get(i).getOperator());
					ps.setFloat(6, conditions.get(i).getThresholdValue());
					if(conditions.get(i).getRelationValue() == null) {
					
						ps.setString(7, "");
					}else {
						ps.setString(7, conditions.get(i).getRelationValue());
					}
					
				}
				
				@Override
	            public int getBatchSize() {
					return conditions.size();
				}
			});
		
		
		
	}
	@Transactional
	public void addAlarmAction(int alarmID,List<AlarmAction> actions) {
		String sql = "INSERT INTO alarm_action_info (alarm_id,action_type,action_object,action_operation,action_content) VALUES (?,?,?,?,?);";
		jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			@Override
			public  void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, alarmID);
				ps.setString(2, actions.get(i).getActionType());
				ps.setString(3, actions.get(i).getActionObject());
				ps.setString(4, actions.get(i).getOperation());
				if(actions.get(i).getContent() == null) {
					ps.setString(5, "");
				}else {
					ps.setString(5, actions.get(i).getContent());
				}		
				
			}
			
			@Override
            public int getBatchSize() {
				return actions.size();
			}
		});
	}
	
	@Override
	@Transactional
	public void addAlarmRule(String alarmName,String alarmLevel,String condition,List<AlarmConditionDTO> conditions,List<AlarmAction> actions) {
		log.info("addAlarmRule start---");
		//插入告警名称、告警级别、告警条件，并产生告警规则ID
		int alarmID = addAlarmMainInfo(alarmName,alarmLevel,condition);
		//插入告警规则ID的条件
		addAlarmCondition(alarmID,conditions);
		//插入告警规则ID的动作
		addAlarmAction(alarmID,actions);
		log.info("addAlarmRule end----");
	}
	
	
	/*
	 *查询告警主要信息
	 *
	 */
	@Override
	public List<AlarmMainInfoDTO> qryAlarmMainInfo(){
		List<AlarmMainInfoDTO> result = new ArrayList<>();
		String sql = "SELECT m.alarm_id,m.alarm_name,m.alarm_level,m.alarm_condition,m.create_time,m.is_valid from alarm_main_info m;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while (rows.next()) {
				int alarmID = rows.getInt("alarm_id");
				String alarmName = rows.getString("alarm_name");
				String alarmLevel = rows.getString("alarm_level");
				String condition = rows.getString("alarm_condition");
				Timestamp createTime = rows.getTimestamp("create_time");
				int status = rows.getInt("is_valid");
				AlarmMainInfoDTO am = new AlarmMainInfoDTO(alarmID, alarmName, alarmLevel, condition, createTime, status);
				result.add(am);
			}
		} catch (Exception e) {
			log.error(" qryAlarmMainInfo exception ="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public AlarmMainInfoDTO qryAlarmMainInfoByID(int alarmID) {
		String sql = "SELECT m.alarm_id,m.alarm_name,m.alarm_level,m.alarm_condition,m.create_time,m.is_valid from alarm_main_info m where alarm_id=?;";
		AlarmMainInfoDTO am = null;
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,alarmID);
			while (rows.next()) {
				
				String alarmName = rows.getString("alarm_name");
				String alarmLevel = rows.getString("alarm_level");
				String condition = rows.getString("alarm_condition");
				Timestamp createTime = rows.getTimestamp("create_time");
				int status = rows.getInt("is_valid");
				am = new AlarmMainInfoDTO(alarmID, alarmName, alarmLevel, condition, createTime, status);
				
			}
		} catch (Exception e) {
			log.error(" qryAlarmMainInfo exception ="+e.getLocalizedMessage());
		}
		return am;
	}
	
	@Override
	public List<AlarmConditionDTO> qryAlarmConditionByID(int alarmID){
		List<AlarmConditionDTO> result = new ArrayList<>();
		String sql ="SELECT c.condition_id,c.device_type,c.sensor_id,c.sensor_name,c.operator,c.threshold_value,c.relation_value from alarm_condition_info c where c.alarm_id=?";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,alarmID);
			while (rows.next()) {
				int conditionID = rows.getInt("condition_id");
				String deviceType = rows.getString("device_type");
				int sensorID = rows.getInt("sensor_id");
				String sensorName = rows.getString("sensor_name");
				String operator = rows.getString("operator");
				float thresholdValue  = rows.getFloat("threshold_value");
				String relationValue;
				if(rows.getString("relation_value") == null) {
					relationValue = "";
				}else {
					relationValue = rows.getString("relation_value");
				}
				
				AlarmConditionDTO ac = new AlarmConditionDTO(conditionID,alarmID,deviceType,sensorID,sensorName,operator,thresholdValue,relationValue);
				result.add(ac);
			}
		} catch (Exception e) {
			log.error("qryAlarmConditionByID exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public List<AlarmActionDTO> qryAlarmActionByID(int alarmID){
		List<AlarmActionDTO> result = new ArrayList<>();
		String sql ="SELECT c.action_id,c.action_type,c.action_object,c.action_operation,c.action_content from alarm_action_info c where c.alarm_id=?;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,alarmID);
			while (rows.next()) {
				int actionID = rows.getInt("action_id");
				String actionType = rows.getString("action_type");
				
				String actionObject = rows.getString("action_object");
				String operation = rows.getString("action_operation");
				
				String content;
				if(rows.getString("action_content") == null) {
					content = "";
				}else {
					content = rows.getString("action_content");
				}
				
				AlarmActionDTO aa = new AlarmActionDTO(actionID,alarmID,actionType,actionObject,operation,content);
				result.add(aa);
			}
		} catch (Exception e) {
			log.error("qryAlarmConditionByID exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	
	@Override
	public List<AlarmMainInfoDTO> qryRunningAlarmMainInfo(){
		List<AlarmMainInfoDTO> result = new ArrayList<>();
		String sql = "SELECT m.alarm_id,m.alarm_name,m.alarm_level,m.alarm_condition,m.create_time,m.is_valid from alarm_main_info m where is_valid=1;";
		int status = 1;
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while (rows.next()) {
				int alarmID = rows.getInt("alarm_id");
				String alarmName = rows.getString("alarm_name");
				String alarmLevel = rows.getString("alarm_level");
				String condition = rows.getString("alarm_condition");
				Timestamp createTime = rows.getTimestamp("create_time");
				
				AlarmMainInfoDTO am = new AlarmMainInfoDTO(alarmID, alarmName, alarmLevel, condition, createTime, status);
				result.add(am);
			}
		} catch (Exception e) {
			log.error(" qryRunningAlarmMainInfo exception ="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public Map<Integer,List<AlarmConditionDTO>> qryAlarmCondition(){
		Map<Integer, List<AlarmConditionDTO>> result = new HashMap<>();
		String sql ="SELECT c.condition_id,c.alarm_id,c.device_type,c.sensor_id,c.sensor_name,c.operator,c.threshold_value,c.relation_value from alarm_condition_info c;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while (rows.next()) {
				int conditionID = rows.getInt("condition_id");
				int alarmID = rows.getInt("alarm_id");
				String deviceType = rows.getString("device_type");
				int sensorID = rows.getInt("sensor_id");
				String sensorName = rows.getString("sensor_name");
				String operator = rows.getString("operator");
				float thresholdValue  = rows.getFloat("threshold_value");
				String relationValue;
				if(rows.getString("relation_value") == null) {
					relationValue = "";
				}else {
					relationValue = rows.getString("relation_value");
				}
				
				AlarmConditionDTO ac = new AlarmConditionDTO(conditionID,alarmID,deviceType,sensorID,sensorName,operator,thresholdValue,relationValue);
				List<AlarmConditionDTO> acList = result.get(alarmID);
				if(acList == null) {
					acList = new ArrayList<>();								
				}
				acList.add(ac);		
				result.put(alarmID, acList);
				
			}
		} catch (Exception e) {
			log.error("qryAlarmConditionByID exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public Map<Integer,List<AlarmActionDTO>> qryAlarmAction(){
		Map<Integer, List<AlarmActionDTO>> result = new HashMap<>();
		String sql ="SELECT c.action_id,c.alarm_id,c.action_type,c.action_object,c.action_operation,c.action_content from alarm_action_info c;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while (rows.next()) {
				int actionID = rows.getInt("action_id");
				int alarmID = rows.getInt("alarm_id");
				String actionType = rows.getString("action_type");				
				String actionObject = rows.getString("action_object");
				String operation = rows.getString("action_operation");				
				String content;
				if(rows.getString("action_content") == null) {
					content = "";
				}else {
					content = rows.getString("action_content");
				}
				
				AlarmActionDTO aa = new AlarmActionDTO(actionID,alarmID,actionType,actionObject,operation,content);
				List<AlarmActionDTO> aaList = result.get(alarmID);
				if(aaList == null) {
					aaList = new ArrayList<>();
				}
				aaList.add(aa);
				result.put(alarmID, aaList);
			}
		} catch (Exception e) {
			log.error("qryAlarmConditionByID exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	

	
	
	/*根据alertid 删除告警
	 * */
	@Override
	public boolean delAlaramRuleByID(int alarmID) {
		boolean result = false;
		String sqlMain = "DELETE FROM alarm_main_info WHERE alarm_id=?";
		String sqlCondtion = "DELETE FROM alarm_condition_info WHERE alarm_id=?";
		String sqlAction = "DELETE FROM alarm_action_info WHERE alarm_id=?";
		jdbcTemplate.update(sqlCondtion,alarmID);
		jdbcTemplate.update(sqlAction, alarmID);
		int rows = jdbcTemplate.update(sqlMain,alarmID);
			if(rows > 0) {
				result = true;
			}
		
		return result;
	}
	/*
	 * 更新告警运行状态
	 **/	
	@Override
	public boolean updateAlarmStatus(int alarmID,int status) {
		boolean result = false;
		String sql = "UPDATE alarm_main_info SET is_valid =? WHERE alarm_id =?";
		try {
			int rows = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			    @Override
			    public void setValues(PreparedStatement preparedStatement) throws SQLException {
			        preparedStatement.setInt(1, status);
			        preparedStatement.setInt(2, alarmID);
			    }
			});
			if(rows > 0) {
				result = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("AlertServiceImpl update alert status exception="+e.getLocalizedMessage());
		}
		return result;
	}
}
