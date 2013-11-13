package com.securus.DBObjects;

import java.sql.*;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * 
 * ------------------- BABIN : IL FAUT S'ASSURER D'EVITER LES INJECTIONS SQL EST_CE QUE T"AS FAIT CA ?????==-----------------------------------
 * 
 * Initializes a database connection, is used to retrieve information
 * about a user, its house, doors, cameras.
 * 
 * @author Marc-Antoine Babin
 *
 */
public class DBConnection {
	
	// string to call JDBC connexion class (java/mysql)
	private static final String JDBCclass = "com.mysql.jdbc.Driver";
	
	private static final String dbAddress = "192.168.0.108";
	
	//connexion string without DB name
	private String connStr = "jdbc:mysql://" + dbAddress + ":3306/";
	
	// Database name
	private String dbName;
	
	//Connection object
	private  Connection conn;
	
	// Normal statement
	private Statement statement;
	
	// CallableStatement:  Interface object to be used for SP calls
	private CallableStatement cs;
	
	private ResultSet rs;
		
	/**
	 * Initializes the connection with the database.
	 * 
	 * @param DBuserName 	the user name of the database user
	 * @param DBpassword 	the password of the database user
	 * @param DBname		the name of the database
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public DBConnection(String DBuserName,String DBpassword, String DBname) 
			throws ClassNotFoundException, SQLException
	{
		dbName = DBname;
		connStr.concat(dbName);
		System.out.println(connStr);
		
		// load the JDBC classes and creates drivermanage class
		// This should only happen during app's loading
		Class.forName(JDBCclass);
		
		
		// Create connection
		conn = DriverManager.getConnection(connStr, DBuserName, DBpassword);
		
	}
	
	/**
	 * Gets the result of query Stored Procedure
	 * @param SPName Stored Procedure's Name without the dataBase name 
	 * @throws SQLException raised if the SQL call is incorect (mostly if SPName is incorect
	 */
	public void queryStoredProcedure(String SPName)
		throws SQLException
	{
		 // preparing callable statement
		cs = conn.prepareCall("{call "+dbName+"."+SPName+"()}");
				
		rs = cs.executeQuery();
	}
	/**
	 * Gets the result of query Stored Procedure into object's result set object.
	 * @param SPName Stored Procedure's name
	 * @param param1 first String parameter
	 * @param param2 second String parameter
	 * @throws SQLException if the SQL call is incorect (mostly if SPName is incorect)
	 */
	public void queryStoredProcedure(String SPName,String param1,String param2)
			throws SQLException
		{
			 // preparing callable statement
			cs = conn.prepareCall("{call "+dbName+"."+SPName+"(?,?)}");
			cs.setString(1, param1);
			cs.setString(2, param2);
			
			rs = cs.executeQuery();
		}
	
	
	// get result of the SP querry with 1 int IN param into rs
	/**
	 * Gets the result of query Stored Procedure into object's result set object
	 * @param SPName Stored Procedure's Name without the dataBase name
	 * @param param	integer parameter needed by the SP 
	 * @throws SQLException raised if the SQL call is incorect (mostly if SPName is incorect
	 */
	public void queryStoredProcedure(String SPName,int param)
		throws SQLException
	{
	
	// preparing callable statement
	String call = "{call "+dbName+"."+SPName+"(?)}";
	//	statement.concat(SPName);
	//	statement.concat("(");
	//	statemendt.concat(paramStr);
	//	statements.concat(")}");
		
		cs = conn.prepareCall(call);
		cs.setInt(1,param);
				
		rs = cs.executeQuery();
	}
	public void queryStoredProcedure(String SPName,int param1, int param2)
			throws SQLException
		{
		
		// preparing callable statement
		String call = "{call "+dbName+"."+SPName+"(?,?)}";
		//	statement.concat(SPName);
		//	statement.concat("(");
		//	statemendt.concat(paramStr);
		//	statements.concat(")}");
			
			cs = conn.prepareCall(call);
			cs.setInt(1,param1);
			cs.setInt(2,param2);
					
			rs = cs.executeQuery();
		}
	
	public void nonQueryStoredProcedure(String SPName,int param1,int param2)
			throws SQLException
		{
			 // preparing callable statement
			cs = conn.prepareCall("{call "+dbName+"."+SPName+"(?,?)}");
			cs.setInt(1, param1);
			cs.setInt(2, param2);
			
			cs.executeUpdate();
		}
	
	/**
	 * Test to see if a select statement works well
	 * @throws SQLException Not supposed
	 */
	
	// Executes a stored procedure with parameteres that has no return 
	// (only insert or update statement)
	public void test() throws SQLException
	{
		statement = conn.createStatement();
		rs = statement.executeQuery("SELECT description from projetP2S3.Portes WHERE idHouse = 2");
		while(rs.next())
		{
		System.out.println(rs.getString("description"));
		}
	}
	/**
	 * Executes a stored Procedure that will update Fields in DB
	 * (Ask DBM for SP names)
	 * @param SPName Stores Procedure's name
	 * @param param integer parameter needed by the SP
	 * @return return 1 if update worked
	 * @throws SQLException mostly if SP does not exist in DB
	 */
	public boolean updateStoredProcedure(String SPName,int param)
		throws SQLException
	{
		 // preparing callable statement
		cs = conn.prepareCall("{call "+SPName+"(?)}");
		cs.setInt(1, param);

		return (cs.executeUpdate() == 1);
		
	}
	public boolean nextObjectRow(DBObject obj) throws SQLException
	{
		boolean success;
		success = rs.next();   // FAUT RÉGLER LE NULL POINTER EXCEPTION ICI
		if(success)
		{
			obj = new DBObject();
			obj.setId(rs.getInt("id"));
			obj.setState(rs.getInt("state"));
		}
		
		return success;
	}
	public boolean nextObjectRow(DBObject obj,boolean onlyAddName) throws SQLException
	{
		boolean success;
		success = rs.next();
		if(success)
		{
			//obj = new DBObject();
			obj.setId(rs.getInt("id"));
			obj.setState(rs.getInt("state"));
			obj.setName(rs.getString("name"));
			if(!onlyAddName)
			{
				obj.setDescription(rs.getString("description"));
			}
		}
		
		return success;
	}
	/**
	 * Closes the DBconnection object
	 * @throws SQLException
	 */
	public void close() throws SQLException
	{
		conn.close();
	}
	
	/**
	 * 
	 * @return -1 if it was imposible to retreive next id from DB
	 * @throws SQLException Error will occur if result set contains no values
	 */
	public int getNextId() throws SQLException {
		
		if(rs.next())
		{
			return rs.getInt("id");
			
		}
		else {return -1;}
	}
	/**
	 * 
	 * @return result set that can be used to do the wanted reads from the dataBase
	 */
	public ResultSet getResultSet() {
		
		return rs;
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException
	{	
		DBConnection conn = new DBConnection("rpi","admin","projetP2S3");
		Thread t = new Thread(new DBReadActions(conn,2));
		t.start();
		
		/*int a = DBUser.UserLogin(conn, "Antoine", "Bouchard");
		DBUser user = new DBUser(conn,a);
		System.out.println("Vous êtes bien chez "+user.getFirstName()+" "+user.getLastName()+
				" qui habite au "+user.getAddress());
		DBHouse house = user.getHouse();
		System.out.print("Etat système d'alarme: ");
		System.out.println(house.getAlarmSystemStateString());
		//conn.test();
		
		System.out.println("Code d'Action retourne par la demande de fermeture de porte patio = "
							+house.unlockDoor(conn, 3));
		house.unlockDoor(conn, 3);
		house.lockDoor(conn, 3);
		house.unlockDoor(conn, 3);
		house.lockDoor(conn, 3);
		ArrayList<DBAction> recentActionList = house.getRecentActions();
		
		System.out.println("Taille de la liste d'actions récentes = "+recentActionList.size());
		DBAction action;
		for(int i =0;i<3;i++)
		{
			action= recentActionList.get(i);
			if(action!=null)
			{
				System.out.println("Action: " +action.getAction()+" Date: "+action.getDate());
			}
		}
		conn.close();
		System.out.println("Fin");*/
	}



}
