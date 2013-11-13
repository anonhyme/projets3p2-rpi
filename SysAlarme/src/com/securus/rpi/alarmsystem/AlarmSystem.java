package com.securus.rpi.alarmsystem;

//import java.text.SimpleDateFormat;
//import java.util.Date;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.securus.rpi.alarmsystem.detector.*;
import com.securus.rpi.alarmsystem.hid.*;
import com.securus.rpi.lock.*;

/**
 * 
 * 
 * @author Michael Fournier
 *
 */
public class AlarmSystem implements Runnable {
	
	// Initialize matrix keypad its pins
	private final static Pin Interupt = RaspiPin.GPIO_12;		//	Interupt bit 
	private final static Pin KeypadB0 = RaspiPin.GPIO_13;		//	Data Bit 0
	private final static Pin KeypadB1 = RaspiPin.GPIO_14;		//	Data Bit 1
	private final static Pin KeypadB2 = RaspiPin.GPIO_15;		//	Data Bit 2
	private final static Pin KeypadB3 = RaspiPin.GPIO_16;		//	Data Bit 3
	private final Keypad clavier;
	
	// Initialize LCD and its pins
	private final static Pin RS = RaspiPin.GPIO_11;  		// LCD RS pin
    private final static Pin Strobe = RaspiPin.GPIO_10;  	// LCD strobe pin
    private final static Pin LcdB0 = RaspiPin.GPIO_00;  	// LCD data bit 0
    private final static Pin LcdB1 = RaspiPin.GPIO_01;  	// LCD data bit 1
	private final static Pin LcdB2 = RaspiPin.GPIO_02;  	// LCD data bit 2
	private final static Pin LcdB3 = RaspiPin.GPIO_03; 		// LCD data bit 3
	private final SysLcd lcd;
	
	// Initialize door sensor
	private final static Pin door1Pin=RaspiPin.GPIO_05;  	// Door #1 magnet sensor pin
	public final magnetSensor doorSensor;
	
	// Initialize motion sensor
	private final static Pin motion1=RaspiPin.GPIO_04;
	private final irDetector motionSensor1;
	
	// Initialize door lock servos
	private final static Pin doorLock=RaspiPin.GPIO_06;  		// Door lock pin for PWM uses
	private final static int numberOfLock=3;
	public final static Lock door[] = {new Lock(doorLock),new Lock(),new Lock()};
	
	private String state;
	private boolean doorFault,motionFault;
	private volatile boolean armed, alarm;
	char key;
	char[] passwordEntered = {' ',' ',' ',' '};
	char[] password = {'8','4','2','1'};
	int indexPass, wrongPass;
	
	/**
	 *  
	 */
	public AlarmSystem(){
		armed = alarm=doorFault=motionFault=false;
		indexPass=0;
		wrongPass=0;
		
		clavier = new Keypad(Interupt,KeypadB0,KeypadB1,KeypadB2,KeypadB3);
		lcd = new SysLcd(2,16,RS,Strobe,LcdB0,LcdB1,LcdB2,LcdB3); // Initialize LCD with 2 rows and 16 character
		motionSensor1 = new irDetector(motion1);
		doorSensor = new magnetSensor(door1Pin);
		
		writeState();
	}
	
	/**
	 *  Initializes alarm system code
	 */
	public void initSystem(){
		
	}
	
	/**
	 *  Arms the alarm system and writes the new state on the LCD
	 */
	public synchronized void  armSystem(){
		
		if(!doorFault){
			armed = true;
			wrongPass=0;
			deletePass();
		}
		writeState();
	}
	
	/**
	 *  Disarms the alarm system and writes the new state on the LCD
	 */
	public synchronized void disarmSystem(){
		
		armed = false;
		alarmDown();
		wrongPass=0;
		deletePass();
		writeState();
	}
	
	/**
	 *  Sets alarm to true
	 */
	public void alarmUp(){
		alarm = true;
	}
	
	/**
	 *  Sets alarm to false
	 */
	public void alarmDown(){
		alarm = false;
	}
	
	/**
	 * Physically locks the door with this id in the database
	 * 
	 * @param id the id of the door in the database
	 */
	public void lockDoor(int id){
		
		if((0 < id) && (id <= numberOfLock)) {
			door[id].lockDoor();
		}
	}
	
	/**
	 * Physically unlocks the door with this id in the database
	 * 
	 * @param id the id of the door in the database
	 */
	public void unlockDoor(int id){
		
		if((0 < id) && (id <= numberOfLock)){
			door[id].unlockDoor();
		}
	}
	
	/**
	 *  Writes the current alarm system state on the LCD
	 */
	public void writeState(){
		
		if( armed && !alarm) {
			state="System armed    ";
		} 
		else if(armed && alarm) {
			state="Alarm!!         ";
		}
		else if(!armed&& !doorFault) {
			state="System ready    ";
		}
		else {
			state="System not ready";
		}
		
		lcd.LcdWrite(0, state);
		
		if(indexPass == 1) {
			lcd.LcdWrite(1, 12, "   *");
		}
		else if(indexPass == 2) {
			lcd.LcdWrite(1, 12, "  **");
		}
		else if(indexPass == 3) {
			lcd.LcdWrite(1, 12, " ***");
		}
		else {
			lcd.LcdWrite(1, 12, "    ");
		}
	}
	
	/**
	 * Compares the password entered with the correct password
	 * 
	 * @return returns true if the password if correct
	 */
	public boolean comparePass(){
		
		if( (passwordEntered[0] == password[0]) &&
					(passwordEntered[1]==password[1]) &&
					(passwordEntered[2]==password[2]) &&
					(passwordEntered[3]==password[3]) ) {
			return true;
		}
		
		return false;
	}
	
	/**
	 *  Sets the password entered to empty characters
	 */
	public void deletePass(){
		
		indexPass=0;
		passwordEntered[0]=' ';
		passwordEntered[1]=' ';
		passwordEntered[2]=' ';
		passwordEntered[3]=' ';
	}
	
	/**
	 *  Verifies key sent by the keypad on the arduino
	 */
	public void loopKeypad(){
		
		if(clavier.isThereKeyPressed()) {
			key = clavier.getKey();
			
			if( (key=='*') || (key=='#') ) {
				deletePass();
				writeState();
			}
			else if (indexPass < 3){
				passwordEntered[indexPass]=key;
				indexPass++;
				writeState();
			}
			
			if(indexPass > 3) {
				if(comparePass()){
					if(!armed)
						armSystem();
					else
						disarmSystem();
					
				}
				else{
					wrongPass++;
					deletePass();
					if(wrongPass >= 3) {
						if(armed) {
							alarmUp();
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Verifies the proximity sensor state. Alarm is triggered if
	 *  a person is detected when the system is armed.
	 */
	public void loopSensor(){
//		if(armed){
//			
//		}
//		else{
//			
//		}
	}
	
	/**
	 *  Infinite loop to verify the keypad keys and the sensor state
	 */
	public void run(){
		while(true){
			loopKeypad();
			loopSensor();
		}
	}
	
	/**
	 * Tests keypad, LCD. User can press numbers on the keypad
	 * and see the results on the LCD (arm, disarm the alarm system
	 * with the proper code). The password can be reset with * or #
	 * 
	 * @param args not used, not necessary
	 * @throws InterruptedException thrown if the thread cannot be started
	 */
	public static void main(String args[]) throws InterruptedException {
		
		System.out.println("Startng system: ");

        Thread loop=new Thread(new AlarmSystem());
        loop.start();
        
//      SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//      monSystem.Lcd.LcdWrite(1,formatter.format(new Date()));
//      Thread.sleep(100);
	}
	
}
