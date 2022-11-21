package com.university.springboot_petroleum.vo;


import java.util.List;

import lombok.Data;

@Data
public class AlarmLogWeekCountVo {
	private List<String> date;
	private List<Integer> count;
	
	public AlarmLogWeekCountVo(List<String> date,List<Integer> count) {
		this.date = date;
		this.count = count;
	}

}
