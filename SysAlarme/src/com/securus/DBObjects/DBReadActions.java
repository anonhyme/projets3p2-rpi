package com.securus.DBObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.securus.DBObjects.DBConnection;

public class DBReadActions implements Runnable
{
	private int idHouse;
	private DBConnection conn;
	
	public DBReadActions(DBConnection conn, int houseId) throws SQLException 
	{
		this.idHouse = houseId;
		this.conn = conn;
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
				
				//System.err.println("Exception lors de la recherche d'actions a executer: " + ex);
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
		boolean actionSuccess = true;
		this.conn.queryStoredProcedure("getWaitingActions", this.idHouse);
		ResultSet rs = this.conn.getResultSet();
		
		while (rs.next())
		{
			int id = rs.getInt("id");
			int idTypeAction = rs.getInt("idTypeAction");
			int idPorte = rs.getInt("idPorte");
			
			actionSuccess = doAction(id, idTypeAction, idPorte);
			if (actionSuccess == false)
			{
				this.conn.nonQueryStoredProcedure("setStatutAction", id, 2);
			}
			else
			{
				this.conn.nonQueryStoredProcedure("setStatutAction", id, 3);
			}
		}
	}
	
	public boolean doAction(int id, int TypeAction, int idPorte)
	{
		try
		{
			switch (TypeAction)
			{
			case 1:		// Action nulle
				
				break;
			case 2:		// Armer le systeme d'alarme
				conn.queryStoredProcedure("setAlarmStatut", 2, 1);
				System.out.println("Alarm system armed");
				break;
			case 3:		// Desarmer le systeme d'alarme
				conn.queryStoredProcedure("setAlarmStatut", 2, 0);
				System.out.println("Alarm system disarmed");
				break;
			case 4:		// Deverouiller la porte
				conn.queryStoredProcedure("setLockStatut", idPorte, 0);
				System.out.println("DoorUnlocked : " + idPorte);
				break;
			case 5:		// Verouiller la porte
				conn.queryStoredProcedure("setLockStatut", idPorte, 1);
				System.out.println("DoorLocked : " + idPorte);
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
