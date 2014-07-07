package com.kevin.contactgenerator.Models;

import java.sql.Date;

public class LoggedCall {
	
	int duration;
	String number;
	String date;
	String type;
	
	public LoggedCall(String number, String type, String date, int duration){
		this.duration=duration;
		this.number=number;
		this.date=date;
		this.type=type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString(){
		return this.date + " | " + this.duration + " | " + this.type;
	}

}
