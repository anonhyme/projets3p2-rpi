package com.securus.DBObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.securus.DBObjects.DBConnection;
import com.securus.rpi.alarmsystem.AlarmSystem;

public class DBReadActions implements Runnable
{
	private int idHouse;
	private DBConnection conn;
	private AlarmSystem alarmSystem;

	public DBReadActions(DBConnection conn, int houseId, AlarmSystem alarmSystem) throws SQLException 
	{
		this.idHouse = houseId;
		this.conn = conn;
		this.alarmSystem = alarmSystem;
	}
	
	 public void run() 
	 {
		 while(true)
		 {
			 try 
			{
				getWaitingActionsFromDB();
			} 
			catch (SQLException ex) {
				
				System.err.println("Exception lors de la recherche d'actions a executer: " + ex);
			}
			try {
				Thread.sleep(1000);
			} 
			catch (InterruptedException ex) 
			{
				System.err.println("Exception lors du sleep entre 2 recherches d'actions" + ex);
			}
		 }
	 }
	
	public void getWaitingActionsFromDB() throws SQLException
	{
		boolean actionNoError = true;
		this.conn.queryStoredProcedure("getWaitingActions", this.idHouse);
		ResultSet rs = this.conn.getResultSet();
		
		while (rs.next())
		{
			actionNoError = doAction(Integer.parseInt(rs.getString("id")), 
									Integer.parseInt(rs.getString("idTypeAction")),
									Integer.parseInt(rs.getString("idPorte")));
			if (actionNoError = true)
			{
				this.conn.nonQueryStoredProcedure("setStatutAction", Integer.parseInt(rs.getString("id")), 2);
			}
			else
			{
				this.conn.nonQueryStoredProcedure("setStatutAction", Integer.parseInt(rs.getString("id")), 3);
			}
		}
	}
	
	public boolean doAction(int id, int TypeAction, int idPorte)
	{
		try
		{
			switch (TypeAction)
			{
			case 2:		// Armer le systeme d'alarme
				
				alarmSystem.armSystem();
				
				break;
			case 3:		// Desarmer le systeme d'alarme
				
				alarmSystem.disarmSystem();
				
				break;
			case 4:		// Deverouiller la porte
				
				alarmSystem.unlockDoor(idPorte);
				
				break;
			case 5:		// Verouiller la porte
				
				alarmSystem.lockDoor(idPorte);
				
				break;
			default:
				
				break;
			}
			return true;
		}
		catch (Exception ex)
		{
			System.err.println("Exception lors de l'execution de l'action de type " + TypeAction + " : " + ex);
			return false;
		}
	}
	
}
