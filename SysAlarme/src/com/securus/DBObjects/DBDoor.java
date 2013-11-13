package com.securus.DBObjects;

 import org.json.JSONObject;

/**
 * Implements a door object in the database.
 * 
 * @author Hugo Bedard
 *
 */
public class DBDoor extends DBObject {

	//  o : pas d'action en cours
	//  1 : une action est en cours
	// -1 : la derniere action a echouee
	private int stateUpdating;
	
	public DBDoor() {
		super();
		stateUpdating = 0;
	}
	
	/**
	 * Returns the string representation of the state value.
	 * 
	 * @return locked if state == 0, unlocked if state == 1
	 */
	public String getStateString() {
		if (state == 0) {
			return "locked";
		}
		else if (state == 0) {
			return "unlocked"; 
		}
		return "";
	}
	
	/**
	 * Return the JSON representation of the object :
	 * id, state, name, stateUpdating, description
	 */
	public JSONObject toJSON() {
		
		JSONObject doorObj = new JSONObject();
		
		doorObj.put("id", id);
		doorObj.put("state", state);
		doorObj.put("stateString", getStateString());
		doorObj.put("name", name);
		doorObj.put("stateUpdating", stateUpdating);
		doorObj.put("description", description);
		
		return doorObj;
	} 
}
