package com.securus.DBObjects;

import java.sql.*;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Java class representation of database house data.
 * 
 * @author Hugo Bedard
 *
 */
public class DBHouse extends DBObject {
	
	// Contains every door of the house
	private ArrayList<DBDoor> doorsList;
	
	// Contains every camera of the house
	private ArrayList<DBCamera> cameraList;
	
	// Contains three dbAction on doors
	private ArrayList<DBAction> doorActionList;

	// Contains three dbAction on alarm system
	private ArrayList<DBAction> alarmSystemActionList;
	
	// Last three actions (door or alarm system)
	private ArrayList<DBAction> recentActionList;
	
	// 1 if the alarm system is active, else 0
	private int alarmSystemState;
	
	//  o : no action is being executed on this house
	//  1 : at least an action is being executed on this house
	// -1 : the last action was unsuccessful
	private int stateUpdating;
	
	/**
	 * Initializes the house from the database data.
	 * 
	 * @param conn the database connection to retrieve data from.
	 * @param id the id of the house to retrieve data.
	 * @throws SQLException
	 */
	public DBHouse(DBConnection conn,int id) 
			throws SQLException
	{
		super(id);
		doorsList = new ArrayList<DBDoor>();
		cameraList = new ArrayList<DBCamera>();
		doorActionList = new ArrayList<DBAction>();
		alarmSystemActionList = new ArrayList<DBAction>();
		recentActionList = new ArrayList<DBAction>();
		stateUpdating = 0;
		retrieveDBData(conn);
		
		// TESTS D'AFFICHAGE :
		doorActionList.add(new DBAction("10/21/2013", "Door 1 : Locked"));
		doorActionList.add(new DBAction("10/20/2013", "Door 2 : Locked"));
		doorActionList.add(new DBAction("10/19/2013", "Door 3 : Locked"));
		
		alarmSystemActionList.add(new DBAction("10/21/2013", "Alarm System : ARMED"));
		alarmSystemActionList.add(new DBAction("10/20/2013", "Alarm System : DISARMED"));
		alarmSystemActionList.add(new DBAction("10/19/2013", "Alarm System : ARMED"));		
		
		recentActionList.add(new DBAction("10/19/2013", "Door 3 : Locked"));
		recentActionList.add(new DBAction("10/20/2013", "Alarm System : DISARMED"));
		recentActionList.add(new DBAction("10/19/2013", "Door 4 : Locked"));
	}
	
	/**
	 * Retrieves/Updates current house information from the database.
	 * 
	 * @param 	conn the database connection to retrieve information from
	 * @return	returns true if the information was correctly retrieved
	 * @throws SQLException
	 */
	private boolean retrieveDBData(DBConnection conn)
		throws SQLException
	{
		
		// Retrieve DBData
		conn.queryStoredProcedure("getEtatAlarme", id);
		this.alarmSystemState = conn.getNextId();
		// alarmSystem = new DBAlarmSystem();
		// DBconn.nextObjectRow(alarmSystem);
		
		// Add each door of the house to the door list
		DBDoor door = new DBDoor();
		conn.queryStoredProcedure("getEtatPorte", id);
		while(conn.nextObjectRow(door, true))
		{

			doorsList.add(door);
			door = new DBDoor();
		}
		
		// Add each camera of the house to the cam list
		DBCamera cam = new DBCamera();
		conn.queryStoredProcedure("getEtatCamera", id);
		while(conn.nextObjectRow(cam,false))
		{
			cameraList.add(cam);
			cam = new DBCamera();
		}
		
		
		// action object and result set to retreive different actions 
		DBAction action ;
		ResultSet rs;
		
		//retreive last 3 actions on doors
		conn.queryStoredProcedure("getDoorActions", this.id);
		rs = conn.getResultSet();
		while(rs.next())
		{
			action = new DBAction(rs.getString("date"), rs.getString("action"));
			doorActionList.add(action);
		}
		
		//retreive last 3 Alarm System actions
		conn.queryStoredProcedure("getAlarmActions", this.id);
		rs = conn.getResultSet();
		while(rs.next())
		{
			action = new DBAction(rs.getString("date"), rs.getString("action"));
			this.alarmSystemActionList.add(action);
		}
		// retreive last 3 actions
		conn.queryStoredProcedure("getLastActions", this.id);
		rs = conn.getResultSet();
		while(rs.next())
		{
			action = new DBAction(rs.getString("date"), rs.getString("action"));
			this.recentActionList.add(action);
		}
		
		return true;
	}
	
	/**
	 * Returns the integer representation of the alarm system state.
	 * 1 if armed, 0 if disarmed.
	 * 
	 * @return AlarmState attribute of DBHouse
	 */
	public int getAlarmSystemState()
	{
		return alarmSystemState;
	}
	
	/**
	 * Returns the string representation of the alarm system state
	 * 
	 * @return returns armed if state == 0, disarmed if state == 1
	 */
	public String getAlarmSystemStateString()
	{		
		if (alarmSystemState == 0) {
			return "disarmed";
		}
		else if (alarmSystemState == 1) {
			return "armed";
		}
		
		return "";
	}	
	
	/**
	 * Returns the house list of DBDoors
	 * 
	 * @return returns the list of every door of the house
	 */
	public ArrayList<DBDoor> getDoorsList() {
		return doorsList;
	}
	
	/**
	 * Returns the three last actions on the doors
	 * 
	 * @return list containing at most three door actions (lock, unlock)
	 */
	public ArrayList<DBAction> getDoorActions() {
		return doorActionList;
	}
	
	/**
	 * Returns the three last actions on the AlarmSystem
	 * 
	 * @return list containing at most three alarm system actions (arm, disarm)
	 */
	public ArrayList<DBAction> getAlarmSystemActions() {
		return alarmSystemActionList;
	}
	
	public ArrayList<DBAction> getRecentActions() {
		return recentActionList;
	}
	
	/**
	 * Writes the door lock action in the database for the 
	 * Raspberry Pi to execute it.
	 * 
	 * @param conn				the database connection to write the action to
	 * @param doorId			the id of the door to lock
	 *
	 * @return					the action id if the action was successfully added to DB, -1 if not
	 * @throws SQLException
	 */
	public int lockDoor(DBConnection conn,int doorId) 
			throws SQLException
	{
		conn.queryStoredProcedure("barrerPorte",this.id, doorId);
		return conn.getNextId();
		
	}
	/**
	 * Writes the door unlock action in the database for the 
	 * Raspberry Pi to execute it.
	 * 
	 * @param conn				the database connection to write the action to
	 * @param doorId			the id of the door to unlock
	 * @return					the action id if the action was successfully added to DB, -1 if not
	 * @throws SQLException
	 */
	public int unlockDoor(DBConnection conn,int doorId) 
			throws SQLException
	{
		conn.queryStoredProcedure("debarrerPorte",this.id, doorId);
		return conn.getNextId();
		
	}
	
	/**
	 * Sets a arm alarm system action in the database for the
	 * Raspberry Pi to execute it.
	 * 
	 * @param conn database connection to communicate the action to
	 * @return 1 if action successful, 0 if not successful
	 * @throws SQLException 
	 */
	public int armAlarmSystem(DBConnection conn) throws SQLException {
		
		conn.queryStoredProcedure("armerAlarme",this.id);
		return conn.getNextId(); // if successful
	}
	
	/**
	 * Sets a disarm alarm system action in the database for the
	 * Raspberry Pi to execute it.
	 * 
	 * @param conn database connection to communicate the action to
	 * @return 1 if action successful, 0 if not successful
	 * @throws SQLException 
	 */
	public int disarmAlarmSystem(DBConnection conn) throws SQLException {
		
		conn.queryStoredProcedure("desarmerAlarme",this.id);
		return conn.getNextId(); // if successful
	}
	
	/**
	 * Returns the string representation of the main camera state,
	 * which is the first camera in the list.
	 * 
	 * @return main camera state : active, inactive
	 */
	public String getMainCameraStateString() {
		
		return cameraList.get(0).getStateString();
	}
	
	/**
	 * Returns the integer value of the main camera state.
	 * The main camera is the first in the list.
	 * 
	 * @return camera state value : 1 (active), 0 (inactive)
	 */
	public int getMainCameraState() {
		return cameraList.get(0).getState();
	}
	
	/**
	 * Returns the JSON representation of the private members of the house.
	 * Uses its doors and camera JSON objects.
	 */
	public JSONObject toJSON() {
		// Create doors list
		JSONArray doorsArray = new JSONArray();
		
		for (DBDoor door : doorsList) {
			doorsArray.put(door.toJSON());
		}
			
		// Create camera list
		JSONArray cameraArray = new JSONArray();
		
		for (DBCamera camera : cameraList) {	
			cameraArray.put(camera.toJSON());
		}
			
		// Create house
		JSONObject houseObj = new JSONObject();
		houseObj.put("alarmSystemState", alarmSystemState);
		houseObj.put("alarmSystemStateString", getAlarmSystemStateString());
		houseObj.put("mainCameraStateString", cameraList.get(0).getState());
		houseObj.put("doorsList", doorsArray);
		houseObj.put("cameraList", cameraArray);
		houseObj.put("doorActionList", doorActionList);
		houseObj.put("alarmSystemActionList", alarmSystemActionList);
		houseObj.put("recentActionList", recentActionList);
		houseObj.put("stateUpdating", stateUpdating);

		return houseObj;
	}  
	
}