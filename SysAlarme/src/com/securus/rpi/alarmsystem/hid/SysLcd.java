package com.securus.rpi.alarmsystem.hid;

import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.Pin;


public class SysLcd {
	
	final GpioLcdDisplay  lcd;
	
	public SysLcd(int row, int column, Pin RS, Pin Strobe, Pin B0, Pin B1, Pin B2, Pin B3){
		lcd = new GpioLcdDisplay(row, column, RS, Strobe, B0, B1, B2, B3);
	}
	
	public void LcdWrite(int row, String s){
		lcd.write(row, s);
	}
	
	public void LcdWrite(int row, int column, String s){
		lcd.setCursorPosition(row, column);
		lcd.write(s);
	}
	
	public void LcdWrite(int row, int index, char c){
		lcd.write(row, index, c);
	}
	
}
