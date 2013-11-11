package detector;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.Pin;


public class irDetector {
	
	private final static GpioController GPIO = GpioFactory.getInstance();
	private final GpioPinDigitalInput Input;

	public irDetector(Pin in){
		
		Input=GPIO.provisionDigitalInputPin(in, "State", PinPullResistance.PULL_DOWN);
		
	}
	
	public boolean inFault(){
		
		if(GPIO.isHigh(Input)){
			return false;
		}
		return true;
	}
}
