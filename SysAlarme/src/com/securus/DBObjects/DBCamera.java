package com.securus.DBObjects;

import org.json.JSONObject;

/**
 * This class is a java representation of a camera in the
 * database.
 * 
 * @author Hugo Bedard
 *
 */
public class DBCamera extends DBObject {
	
	//  o : no action is being executed on this house
	//  1 : at least an action is being executed on this house
	// -1 : the last action was unsuccessful
	private int stateUpdating;
	
	/**
	 * Initialize empty camera with default parameters.
	 */
	public DBCamera() {
		super();
		stateUpdating = 0;
	}
	
	/**
	 * Initializes camera without description
	 * 
	 * @param id		camera id in the database
	 * @param state		camera state (1 : active, 0 : inactive)
	 * @param name		camera name, string to recognize the camera
	 */
	public DBCamera(int id, int state, String name) {
		super(id, state, name);
	}
	
	/**
	 * Initializes all arguments of the camera object.
	 * 
	 * @param id			camera id in the database
	 * @param state			camera state (1 : active, 0 : inactive)
	 * @param name			camera name, string to recognize the camera
	 * @param description	text to describe the camera
	 */
	public DBCamera(int id, int state, String name, String description) {
		super(id, state, name, description);		
	}
	
	/**
	 * Returns the string representation of the state variable.
	 * 
	 * @return active if state == 0, inactive if state == 1.
	 */
	public String getStateString() {
		if (state == 0) {
			return "inactive";
		}
		else if (state == 1) {
			return "active";
		}
		else {
			return "";
		}
	}
	
	/**
	 * Returns private member as a JSON Object :
	 * id, state, name, description.
	 */
	public JSONObject toJSON() {
		
		JSONObject cameraObj = new JSONObject();
		
		cameraObj.put("id", id);
		cameraObj.put("state", state);
		cameraObj.put("stateString", getStateString());
		cameraObj.put("name", name);
		cameraObj.put("description", description);
		cameraObj.put("stateUpdating", stateUpdating);
		
		return cameraObj;
	} 
}
