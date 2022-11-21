package com.university.springboot_petroleum.dto;



import lombok.Data;

@Data
public class AlarmWeekCountDTO {
	private String date;
	private int count;
	public AlarmWeekCountDTO(String date,int count) {
		this.date = date;
		this.count = count;
	}

}
