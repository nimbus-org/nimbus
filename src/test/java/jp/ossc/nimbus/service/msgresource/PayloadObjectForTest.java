/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.service.msgresource;

import java.io.*;

/**
 *	JMS ObjectMessageにセットして送るクラスのサンプル
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/19－ y-tokuda<BR>
 *				更新：
 */
public class PayloadObjectForTest implements Serializable{
	
    private static final long serialVersionUID = -5144490649117567746L;
    private int number_int;
	private byte number_byte;
	private short number_short;
	private long number_long;
	private float number_float;
	private double number_double;
	private boolean flag;
	private String message;
	private java.awt.Color awtColor;
	private java.awt.Font awtFont;
	
	public PayloadObjectForTest(){
	}
	
	public void setIntNumber(int num){
		this.number_int = num;
	}
	
	public void setShortNumber(short num){
		this.number_short = num;
	}
	
	public void setLongNumber(long num){
		this.number_long = num;
	}
	
	public void setFloatNumber(float num){
		this.number_float = num;
	}
	
	public void setDoubleNumber(double num){
		this.number_double = num;
	}
	
	public void setBoolean(boolean bool){
		this.flag = bool;
	}
	
	public void setByteNumber(byte num){
		this.number_byte = num;
	}
	
	public void setMessage(String msg){
		this.message = msg;
	}
	
	public void setAwtColor(java.awt.Color color){
		this.awtColor = color;
	}
	
	public void setAwtFont(java.awt.Font font){
		this.awtFont = font;
	}
	
	
	public String toString(){
		 String ret = "PayloadObjectForTest :: Message is " + message 
		 				+ " Number(Int) is " + number_int
		 				+ " Number(Short) is " + number_short
		 				+ " Number(Long) is " + number_long
		 				+ " Number(Float) is" + number_float
		 				+ " Number(Double) is " + number_double
		 				+ " Number(byte) is " + number_byte;
		 if(awtColor != null){
		 	ret = ret + " AWT Color is " + awtColor.getRed()
		 			+ "," + awtColor.getGreen() 
		 			+ "," + awtColor.getBlue();
		 }
		 return ret;
	}
	
}
