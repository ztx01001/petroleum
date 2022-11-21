package com.university.springboot_petroleum.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DailySeverityCountVo {

	private List<String> date;
	private Map<String, List<Integer>> countMap;
	
	public DailySeverityCountVo(List<String> date,Map<String, List<Integer>> countMap) {
		this.date = date;
		this.countMap = countMap;
	}
}
