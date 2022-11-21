package com.university.springboot_petroleum.config;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Configuration
@Setter
@Getter
public class InfluxDBConfig {
	private static Logger log = LoggerFactory.getLogger(InfluxDBConfig.class);
			
    @Value("${spring.influxdb.user}")
	private String userName;
	@Value("${spring.influxdb.password}")
	private String passWord;
	@Value("${spring.influxdb.url}")
	private String url;
	@Value("${spring.influxdb.database}")
	private String dataBase;
	
	//保留策略
	private String retentionPolicy;
	
	private InfluxDB influxDB;
	
	public InfluxDBConfig() {
		log.info("InfluxDBConfig constructor enter database="+dataBase);
	}
	
	public InfluxDBConfig(String userName,String passWord,String url,String dataBase) {
		log.info("InfluxDBConfig constructor username");
		this.userName = userName;
		this.passWord = passWord;
		this.url = url;
		this.dataBase = dataBase;
		// autogen默认的数据保存策略
        this.retentionPolicy = retentionPolicy == null || "".equals(retentionPolicy) ? "autogen" : retentionPolicy;
        this.influxDB = influxDbBuild();
	}
	
	/**
     * 连接时序数据库；获得InfluxDB
     **/
	private InfluxDB influxDbBuild() {
		log.info("InfluxDBConfig  influxDbBuild");
		if(influxDB == null) {
			influxDB = InfluxDBFactory.connect(url, userName, passWord);
			influxDB.setDatabase(dataBase);
			log.info("InfluxDBConfig  connect="+url+" username="+userName+" dataBase="+dataBase);
		}
		return influxDB;
	}
	
	 /**
     * 查询
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command) {
        influxDbBuild();
        return influxDB.query(new Query(command,dataBase));
    }
    /**
     * 查询数据库是否链接
     * 
     */
     public boolean isPing() {
    	 influxDbBuild();
    	 boolean connected = false;
    	 Pong p ;
    	 try {
    		 p = influxDB.ping();
    		 if(null != p) {
    			 connected = true;
        	 }
    	 }catch(Exception e) {
    		 
    	 } 
    	 
    	 return connected;
     }
     
     /**
      * @param dbName 创建数据库
      * @return
      */
     public void createDB(String dbName) {
    	 influxDbBuild();
    	 influxDB.createDatabase(dbName);
    	 
     }
     
     /**
      * 关闭数据库
      */
     public void closeDB() {
    	 influxDbBuild();
    	 influxDB.close();
     }    

	
	 /**
     * 设置数据保存策略 defalut 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1 副本个数为1/ 结尾DEFAULT
     * 表示 设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "defalut", dataBase, "30d", 1);
        this.query(command);
    }
    
    /**
     * 插入
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(String measurement, Map<String,String> tags,Map<String,Object> fields) {
    	influxDbBuild();
    	Point.Builder builder = Point.measurement(measurement);
    	builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    	builder.tag(tags);
    	builder.fields(fields);
    	influxDB.write(dataBase, "", builder.build());
    }
    /**
     * 插入
     * @param measurement 表
     * @param field       字段
     */
    public void insert(String measurement,Map<String,Object> fields) {
    	log.info("InfluxDBConfig insert measurements="+measurement+" start");
    	influxDbBuild();
    	Point.Builder builder = Point.measurement(measurement);
    	builder.fields(fields);
    	influxDB.write(dataBase, "", builder.build());
    	log.info("InfluxDBConfig insert measurements="+measurement+" end");
    }
    /**
     * 批量写入测点
     *
     * @param batchPoints
     */
    public void batchInsert(BatchPoints batchPoints) {
    	influxDbBuild();
    	influxDB.write(batchPoints);
    }
    /**
     * 删除数据库
     * @param dbName
     */
    public void deleteDB(String dbName) {
    	influxDbBuild();
    	influxDB.deleteDatabase(dbName);
    }
    /**
     *删除表 
     * @param command 删除语句
     */
    public String deleteMeasurementData(String command) {
    	influxDbBuild();
    	QueryResult result = influxDB.query(new Query(command,dataBase));
    	return result.getError();
    }
    
    /**
     * @desc 查询结果处理
     * @date 
      *@param queryResult
     */
    public List<Map<String,Object>> queryResultProcess(QueryResult queryResult){
    	List<Map<String,Object>> mapList  = new ArrayList<>();
    	List<QueryResult.Result> resultList = queryResult.getResults();
    	//把查询出的结果集转换成对应的实体对象，聚合成list
    	for(QueryResult.Result query:resultList) {
    		List<QueryResult.Series> seriesList = query.getSeries();
    		if(seriesList != null && seriesList.size() != 0) {
    			for(QueryResult.Series series : seriesList){
    				List<String> columns = series.getColumns();
                    String[] keys =  columns.toArray(new String[columns.size()]);
                    List<List<Object>> values = series.getValues();
                    if(values != null && values.size() != 0) {
                    	for(List<Object> value : values){
                    		 Map<String, Object> map = new HashMap(keys.length);
                    		 for (int i = 0; i < keys.length; i++) {
                    			 map.put(keys[i], value.get(i));
                    		 }
                    		 mapList.add(map);
                    	}
                    }
    			}
    		}
    	}
    	return mapList;
    }
    
    /**
     * @desc InfluxDB 查询 count总条数
     * 
     */
    public long countResultProcess(QueryResult queryResult) {
        long count = 0;
        List<Map<String, Object>> list = queryResultProcess(queryResult);
        if(list != null && list.size() != 0) {
            Map<String, Object> map = list.get(0);
            double num = (Double)map.get("count");
            count = new Double(num).longValue();
        }
        return count;
    }

}
