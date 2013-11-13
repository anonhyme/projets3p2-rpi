package com.securus.DBObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a user of the web application.
 * Contains information about the user and its house.
 * 
 * @author Hugo Bedard
 *
 */
public class DBUser {

	private int id;
	private String userName;
	private String lastName;
	private String firstName;
	private String address;
	private String description;
	
	public DBHouse house;
	
	/**
	 * Initializes user private members from database data.
	 * 
	 * @param conn 		the database connection to retrieve information from
	 * @param id  		id of the user retrieved with the static function UserLogin
	 * @throws SQLException
	 * @see DBUser.UserLogin
	 */
	public DBUser(DBConnection conn, int id) 
			throws SQLException 
	{	
		this.id = id;
		setUserInfoFromDB(conn);
	}
	
	/**
	 * Returns user id if the user exists and the password is valid.
	 * 
	 * @param conn  	the database to verify for the user
	 * @param userName	the user name to verify
	 * @param passWord	the password associated with the user name
	 * @return 			-1 if there was an error reading the DB, 0 if the userName and passwords are invalid
	 * @throws SQLException
	 */
	public static int UserLogin(DBConnection conn, String userName, String passWord) 
			throws SQLException
	{
		conn.queryStoredProcedure("Login", userName, passWord);
		
		return conn.getNextId();
	}
	
	/**
	 * Retrieves user data from the database with the user ID.
	 * 
	 * @param conn the connection to the database to retrieve user ID from
	 * @throws SQLException 
	 */
	public void setUserInfoFromDB(DBConnection conn) 
			throws SQLException {
		
		conn.queryStoredProcedure("getInfoUser", this.id);
		ResultSet rs = conn.getResultSet();
		if(rs.next()) // if there are values in the Database ResultSet
		{
			userName = rs.getString("userName");
			firstName = rs.getString("FirstName");
			lastName = rs.getString("LastName");
			address = rs.getString("address");
			description = rs.getString("description");
			house = new DBHouse(conn,rs.getInt("idHouse"));
			
		}
		else
		{
			userName = "Failed";
		}
		
	}
	/**
	 * Returns the user house object
	 * 
	 * @return house object of this user
	 */
	public DBHouse getHouse()
	{
		return house;
	}
	
	/* SETTERS AND GETTERS */
	public String getUserName() {
		return userName;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getAddress() {
		return address;
	}
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the JSON representation of the user object.
	 * 
	 * @return JSON string of the private members of the user and its house JSON string.
	 */
	public JSONObject toJSON() {

		JSONObject userObj = new JSONObject();
		userObj.put("userName", userName);
		userObj.put("firstName", firstName);
		userObj.put("lastName", lastName);
		userObj.put("description", description);
		userObj.put("house", house.toJSON());
		
		return userObj; 
	}
	
}
