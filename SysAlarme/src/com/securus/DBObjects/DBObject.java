package com.securus.DBObjects;

import org.json.JSONObject;

/**
 * Java representation of a standard database object.
 * Contains setters and getters for all of its private members.
 * 
 * @author Hugo Bedard
 *
 */
public class DBObject {
	protected int id;
	protected int state;
	protected String name;
	protected String description;
	
	public DBObject() {
		this.id = -1;
		this.state = -1;
		this.name = "";
		this.description = "";	
	}
	public DBObject(int id)
	{
		this.id = id;
		this.state = -1;
		this.name = "";
		this.description = "";
	}
	public DBObject(int id,int state)
	{
		this.id = id;
		this.state = state;
		this.name = "";
		this.description = "";
	}
	public DBObject(int id, int state, String name) {
		this.id = id;
		this.state = state;
		this.name = name;
		this.description = "";
	}
	
	public DBObject(int id, int state, String name, String description) {
		this.id = id;
		this.state = state;
		this.name = name;
		this.description = description;		
	}
	// get/set id
	public void setId(int newId)
	{
		id = newId;
	}
	public int getId()
	{
		return id;
	}
	// get/set state
	public void setState(int newState)
	{
		state = newState;
	}
	public int getState()
	{
		return state;
	}
	// get/set Name
	public void setName(String newName)
	{
		name = newName;
	}
	public String getName()
	{
		return name;
	}
	// get/set description
	public void setDescription(String newDescription)
	{
		description = newDescription;
	}
	public String getDescription()
	{
		return description;
	}
	
	public JSONObject toJSON() {
		// to override
		return new JSONObject();
	}  
	
}
