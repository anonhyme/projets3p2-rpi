import java.sql.SQLException;

import com.securus.DBObjects.DBConnection;
import com.securus.DBObjects.DBReadActions;
import com.securus.rpi.alarmsystem.AlarmSystem;

/**
 * 
 * @author Hugo Bedard
 *
 */
public class RaspberryPi {

	/**
	 * 
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// Create a new Database Connection
		DBConnection conn = new DBConnection("root", "ROOT", "projetP2S3");
		
		// Create new alarm system
		AlarmSystem alarmSystem = new AlarmSystem();
		
		// Create alarm system thread
        Thread alarmSystemThread = new Thread(alarmSystem);
        
        // Create database action read thread
        Thread dbreadThread = new Thread(new DBReadActions(conn, 2, alarmSystem));
        
        // Start threads
        alarmSystemThread.start();
        dbreadThread.start();
	}

}
