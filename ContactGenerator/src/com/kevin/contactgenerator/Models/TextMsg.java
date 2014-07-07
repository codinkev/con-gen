package com.kevin.contactgenerator.Models;

import java.sql.Date;

public class TextMsg {
	
	String number;
	String contact;
	String date;
	String body;
	
	public TextMsg(String number, String contact, String date, String body) {
		this.number = number;
		this.contact = contact;
		this.date = date;
		this.body = body;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String toString(){
		return this.body;//.substring(1,Math.min(this.body.length(), 20));
	}

}
