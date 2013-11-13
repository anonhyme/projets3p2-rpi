package com.securus.rpi.lock;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

public class Lock{
	
	private final static GpioController GPIO = GpioFactory.getInstance();
	private final GpioPinDigitalOutput Output;
	
	
	public Lock(Pin out){
		
		Output=GPIO.provisionDigitalOutputPin(out, "PWMoutput");
		
	}
	public Lock(){
		Output=null;
	}
	
	public void unlockDoor(){
		
	}
	
	public void lockDoor(){
		
	}
}
