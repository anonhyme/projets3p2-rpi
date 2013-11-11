
//import java.text.SimpleDateFormat;
//import java.util.Date;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import hid.SysLcd;
import hid.Keypad;






public class SysAlarme {
	
	//Initialyse matrix keypad and the pin
	private final static Pin Interupt=RaspiPin.GPIO_12;		//	Interupt bit 
	private final static Pin KeypadB0=RaspiPin.GPIO_13;		//	Data Bit 0
	private final static Pin KeypadB1=RaspiPin.GPIO_14;		//	Data Bit 1
	private final static Pin KeypadB2=RaspiPin.GPIO_15;		//	Data Bit 2
	private final static Pin KeypadB3=RaspiPin.GPIO_16;		//	Data Bit 3
	private final static Keypad clavier=new Keypad(Interupt,KeypadB0,KeypadB1,KeypadB2,KeypadB3);
	
	//Initialyse Lcd and the pin 
	private final static Pin RS=RaspiPin.GPIO_11;  		// LCD RS pin
    private final static Pin Strobe=RaspiPin.GPIO_10;  	// LCD strobe pin
    private final static Pin LcdB0=RaspiPin.GPIO_00;  	// LCD data bit 0
    private final static Pin LcdB1=RaspiPin.GPIO_01;  	// LCD data bit 1
	private final static Pin LcdB2=RaspiPin.GPIO_02;  	// LCD data bit 2
	private final static Pin LcdB3=RaspiPin.GPIO_03; 	// LCD data bit 3
	private final static SysLcd Lcd=new SysLcd(2,16,RS,Strobe,LcdB0,LcdB1,LcdB2,LcdB3); // Initialyse Lcd with 2 rows and 16 character
	
	private String state;
	private boolean armé, alarm;
	char key;
	char[] passwordEntered={' ',' ',' ',' '};
	char[] password={'8','4','2','1'};
	int indexPass, wrongPass;
	
	
	public SysAlarme(){
		
		armé=alarm=false;
		indexPass=0;
		wrongPass=0;
		

	}
	
	public void initSystem(){
		
	}
	
	
	public void writeState(){
		if(armé&&!alarm)
			state="System armed";
		else if(armé&&alarm)
			state="Alarm!!!!!!!";
		else
			state="System ready";
		Lcd.LcdWrite(0, state);
		
		if(indexPass==1)
			Lcd.LcdWrite(1, 12, "   *");
		else if(indexPass==2)
			Lcd.LcdWrite(1, 12, "  **");
		else if(indexPass==3)
			Lcd.LcdWrite(1, 12, " ***");
		else
			Lcd.LcdWrite(1, 12, "    ");
		
	}
	
	public boolean comparePass(){
		if(passwordEntered[0]==password[0]&&
		passwordEntered[1]==password[1]&&
		passwordEntered[2]==password[2]&&
		passwordEntered[3]==password[3])
			return true;
		return false;
	}
	
	public void deletePass(){
		indexPass=0;
		passwordEntered[0]=' ';
		passwordEntered[1]=' ';
		passwordEntered[2]=' ';
		passwordEntered[3]=' ';
	}
	
	public void loopKeypad(){
		if(clavier.isThereKeyPressed()){
			key=clavier.getKey();
			
			if(key=='*'||key=='#'){
				this.deletePass();
				this.writeState();
			}
			else if(indexPass<3){
				passwordEntered[indexPass]=key;
				indexPass++;
				this.writeState();
				
			}
			
			if(indexPass>3){
				if(this.comparePass()){
					armé^=true;	
					alarm=false;
					this.deletePass();
					wrongPass=0;
				}
				else{
					this.deletePass();
					if(armé){
						wrongPass++;
						
						if(wrongPass>2)
							alarm=true;
					}
				}
				
				this.writeState();
			}
			
		}
	}
	
	public static void main(String args[]) throws InterruptedException {
		
		System.out.println("Startng system: ");

		SysAlarme monSystem= new SysAlarme();
		
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        monSystem.writeState();
        while(true) {
        	monSystem.loopKeypad();
//        	monSystem.Lcd.LcdWrite(1,formatter.format(new Date()));
//        	Thread.sleep(100);

        }
	}
	
}
