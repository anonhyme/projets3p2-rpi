package com.securus.DBObjects;

public class DBAction {
	
	private String date;
	private String action;
	
	public DBAction(String date, String action) {
		this.date = date;
		this.action = action;
	}
	
	public DBAction() {
		
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date){
		this.date = date;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action){
		this.action = action;
	}
}
