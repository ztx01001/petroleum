package com.university.springboot_petroleum.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.service.AlertLogService;
import com.university.springboot_petroleum.vo.AlarmLogVo;

@Service
public class AlertLogServiceImpl implements AlertLogService{

	private static final Logger log = LoggerFactory.getLogger(AlertLogServiceImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Override
	public boolean addAlarmLog(int alarmID,String alarmName,long time,String severity) {
		log.info("insert alert log enter");
		String sql="INSERT INTO alarm_log(alarm_id,alarm_name,alarm_severity,create_time,alarm_status) VALUES (?, ?, ?,?,?);";
		int status = 1;
		int result = 0;
		try {
			
			result = jdbcTemplate.update(sql,new PreparedStatementSetter() {
				@Override
				 public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setInt(1, alarmID);
					preparedStatement.setString(2,alarmName);
					preparedStatement.setString(3, severity);
					Timestamp timeStamp = new Timestamp(time);
					
					log.info("timestamp------------"+timeStamp.toString());
					preparedStatement.setTimestamp(4,timeStamp);					
					preparedStatement.setInt(5, status);
					
				}
			});
			//log.info("sql="+sql);
		} catch (Exception e) {
			log.error("AlertLogServiceImpl insert alert log exception="+e.getLocalizedMessage()+"="+sql);
		}		
		
		if(result > 0) {
			return true;
		}			
		return false;
	}
	
	@Override
	public List<AlarmLogVo> qryAlarmLogByPage(int pageNum,int pageSize,int statusID,int alarmID) {
		log.info("AlertLogServiceImpl qryAlarmLogByPage enter");
		List<AlarmLogVo> result = new ArrayList<>();
		int starter = (pageNum - 1)*pageSize;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "select l.alarm_log_id,l.alarm_id,l.alarm_name,l.alarm_severity,l.create_time,l.alarm_status,c.alert_status_desc from alarm_log l,alarm_status_config c WHERE l.alarm_status = c.alert_status_id and l.alarm_status="+"?"+" order by l.create_time desc limit "+ starter +" , "+ pageSize;
		String sql2 = "select l.alarm_log_id,l.alarm_id,l.alarm_name,l.alarm_severity,l.create_time,l.alarm_status,c.alert_status_desc from alarm_log l,alarm_status_config c WHERE l.alarm_status = c.alert_status_id and l.alarm_id = "+"?"+" and l.alarm_status="+"?"+" order by l.create_time desc limit "+ starter +" , "+ pageSize;
		try {
			SqlRowSet rows = null;
			if(alarmID == 0) {
				rows = jdbcTemplate.queryForRowSet(sql,statusID);
			}else {
				rows = jdbcTemplate.queryForRowSet(sql2,alarmID,statusID);
			}
			 
			
			while (rows.next()) {
				
				int id = rows.getInt("alarm_log_id");
				int alarmIDr = rows.getInt("alarm_id");
				String alarmName = rows.getString("alarm_name");
				
				String severity = rows.getString("alarm_severity");
				Timestamp time = rows.getTimestamp("create_time");
				String timeStr = df.format(time);
				String statusValue = rows.getString("alert_status_desc");
				int status = rows.getInt("alarm_status");
				AlarmLogVo alarmLogVo = new AlarmLogVo(id,alarmIDr,alarmName,timeStr,severity,status,statusValue);
				result.add(alarmLogVo);	
			}
		} catch (Exception e) {
			log.error("AlertLogServiceImpl qryAlarmLogByPage exception="+e.getLocalizedMessage());
		}
		return result;
		
	}
	@Override 
	public int countAll(int statusID,int alarmID) {
		log.info("AlertLogServiceImpl countAll enter");
		String sqlString = "SELECT COUNT(*) count from alarm_log WHERE alarm_status=? ;";
		String sqlString2 = "SELECT COUNT(*) count from alarm_log WHERE alarm_id=? and alarm_status=?;";
		int count  = 0;
		try {
			SqlRowSet rows = null;
			if(alarmID == 0) {
				rows = jdbcTemplate.queryForRowSet(sqlString,statusID);
			}else {
				rows = jdbcTemplate.queryForRowSet(sqlString2,alarmID,statusID);
			}
			
			while(rows.next()) {
				count = rows.getInt("count");
			}
		} catch (Exception e) {
			log.error("AlertLogServiceImpl countAll exception="+e.getLocalizedMessage());
		}
		return count;
	}
	
	@Override
	public List<AlarmLogVo> qryAlarmLog(){
		log.info("AlertLogServiceImpl qryAlarmLog enter");
		List<AlarmLogVo> result = new ArrayList<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
		String sql = "SELECT a.alarm_log_id,a.alarm_id,a.alarm_name,a.alarm_severity,a.create_time,a.alarm_status from alarm_log a ORDER BY a.create_time desc";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int id = rows.getInt("alarm_log_id");
				int alarmID = rows.getInt("alarm_id");
				String alarmName = rows.getString("alarm_name");
				
				String severity = rows.getString("alarm_severity");
				Timestamp time = rows.getTimestamp("create_time");
				String timeStr = df.format(time);
				int status = rows.getInt("alarm_status");
				AlarmLogVo alarmLogVo = new AlarmLogVo(id,alarmID,alarmName,timeStr,severity,status);
				result.add(alarmLogVo);			
				
			}
			}catch (Exception e) {
				log.error("AlertLogServiceImpl qryAlarmLog exception="+e.getLocalizedMessage());
			}
		return result;
	}
	
	@Override
	public List<AlarmLogVo> qryAlarmLogByID(int alarmID){
		log.info("AlertLogServiceImpl qryAlarmLog enter");
		List<AlarmLogVo> result = new ArrayList<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
		String sql = "SELECT a.alarm_log_id,a.alarm_id,a.alarm_name,a.alarm_severity,a.create_time,a.alarm_status from alarm_log a WHERE a.alarm_id =? ORDER BY a.create_time desc";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,alarmID);
			while(rows.next()) {
				int id = rows.getInt("alarm_log_id");				
				String alarmName = rows.getString("alarm_name");				
				String severity = rows.getString("alarm_severity");
				Timestamp time = rows.getTimestamp("create_time");
				String timeStr = df.format(time);
				int status = rows.getInt("alarm_status");
				AlarmLogVo alarmLogVo = new AlarmLogVo(id,alarmID,alarmName,timeStr,severity,status);
				result.add(alarmLogVo);			
				
			}
			}catch (Exception e) {
				log.error("AlertLogServiceImpl qryAlarmLog exception="+e.getLocalizedMessage());
			}
		return result;
	}
	//批量更新告警日志状态
	@Override
	public boolean batchUpdateAlarmLogStatus(int statusID,List<Integer> alarmLogIDList) {
		boolean result = false;
		log.info("batchUpdateAlarmLogStatus enter");
		String sqlString = "UPDATE alarm_log SET alarm_status =? WHERE alarm_log_id =?";
		try {
			int[] ans = jdbcTemplate.batchUpdate(sqlString,new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
					preparedStatement.setInt(1, statusID);
					preparedStatement.setInt(2, alarmLogIDList.get(i).intValue());
				}
				@Override
				public int getBatchSize() {
					return alarmLogIDList.size();
				}
			});
			if((ans != null) && (ans.length > 0)) {
				result = true;
			}
			
		} catch (Exception e) {
			log.error("batchUpdateAlarmLogStatus exception="+e.getLocalizedMessage());
		}
		
		return result;
	}
	
	@Override
	public boolean updateAlarmLogStatus(int alarmLogId,int status) {
		boolean result = false;
		log.info("AlertLogServiceImpl update alarmlog status enter");
		String sql = "UPDATE alarm_log SET alarm_status =? WHERE alarm_log_id =?";
		try {
			int rows = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			    @Override
			    public void setValues(PreparedStatement preparedStatement) throws SQLException {
			        preparedStatement.setInt(1, status);
			        preparedStatement.setInt(2, alarmLogId);
			    }
			});
			if(rows > 0) {
				result = true;
			}
		} catch (Exception e) {
			
			log.error("update alertlog status exception="+e.getLocalizedMessage());
		}
		
		return result;
	}
	//批量删除告警日志
	@Override
	public boolean batchDelAlarmLog(List<Integer> alarmLogIDList) {
		boolean result = false;
		String sql = "DELETE FROM alarm_log WHERE alarm_log_id=?";
		try {
			int[] ans = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
					
					preparedStatement.setInt(1, alarmLogIDList.get(i).intValue());
				}
				@Override
				public int getBatchSize() {
					return alarmLogIDList.size();
				}
			});
			if((ans != null) && (ans.length > 0)) {
				result = true;
			}
			
		} catch (Exception e) {
			log.error("batchDelAlarmLog exception="+e.getLocalizedMessage());
		}
		return result;
	}
	@Override
	public boolean delAlarmLog(int alarmLogId) {
		log.info("AlertLogServiceImpl delete alarmlog enter");
		boolean result = false;
		String sql = "DELETE FROM alarm_log WHERE alarm_log_id=?";
		try {
			int rows = jdbcTemplate.update(sql,alarmLogId);
			if(rows > 0) {
				result = true;
			}
		}catch(Exception e){
			log.error("AlertLogServiceImpl delete alarmlog id="+alarmLogId+" exception="+e.getLocalizedMessage());
		}
		return result;
		
	}
}
