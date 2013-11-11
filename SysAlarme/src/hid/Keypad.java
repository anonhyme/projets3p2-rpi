
package hid;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.Pin;


public class Keypad {
	
	
	private final GpioPinDigitalInput key[]={null,null,null,null,null};
	private final GpioController GPIO;
	private char keyPress;
	private boolean keyPressed;
	
	
	public Keypad(Pin I, Pin B0, Pin B1, Pin B2, Pin B3){
		
		keyPressed=false;
		GPIO=GpioFactory.getInstance();
		
		key[0]=GPIO.provisionDigitalInputPin(I, "Interupt", PinPullResistance.PULL_DOWN);
		key[1]=GPIO.provisionDigitalInputPin(B0, "Bit0", PinPullResistance.PULL_DOWN);
		key[2]=GPIO.provisionDigitalInputPin(B1, "Bit1", PinPullResistance.PULL_DOWN);
		key[3]=GPIO.provisionDigitalInputPin(B2, "Bit2", PinPullResistance.PULL_DOWN);
		key[4]=GPIO.provisionDigitalInputPin(B3, "Bit3", PinPullResistance.PULL_DOWN);
		

	}
	
	
	public boolean isThereKeyPressed(){
		boolean results=false;
		
		// If the key has been pressed and has never been processed
		if(GPIO.isHigh(key[0])&&!keyPressed){
			// Tag the key as already processed and gives the go to input the key into the system
			results=true;
			keyPressed=true;
			
			// Linking the binary map of the input bits to the ascii code; 
			if(GPIO.isHigh(key[4])){
				if(GPIO.isLow(key[3])){
					if(GPIO.isHigh(key[2])){
						if(GPIO.isHigh(key[1]))
							// Input 1011
							keyPress='#';
						else
							// Input 1010
							keyPress='*';	
					}
					else{
						if(GPIO.isHigh(key[1]))
							// Input 1001
							keyPress='9';
						else
							// Input 1000
							keyPress='8';
					}
				}
				else
					// If the input is 11XX, return false, only 12 character accepted
					results=false;	
			}
			else{
				if(GPIO.isHigh(key[3])){
					if(GPIO.isHigh(key[2])){
						if(GPIO.isHigh(key[1]))
							// Input 0111
							keyPress='7';
						else
							// Input 0110
							keyPress='6';	
					}
					else{
						if(GPIO.isHigh(key[1]))
							// Input 0101
							keyPress='5';
						else
							// Input 0100
							keyPress='4';
					}
				}
				else{
					if(GPIO.isHigh(key[2])){
						if(GPIO.isHigh(key[1]))
							// Input 0011
							keyPress='3';
						else
							// Input 0010
							keyPress='2';	
					}
					else{
						if(GPIO.isHigh(key[1]))
							// Input 0001
							keyPress='1';
						else
							// Input 0000
							keyPress='0';
					}
				}	
				
			}
			
		}
		else if(GPIO.isLow(key[0])){
			keyPressed=false;
		}
		return results;
	}
	
	public char getKey(){
		return keyPress;
	}
	
}
