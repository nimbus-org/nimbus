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
package jp.ossc.nimbus.service.sequence;

import java.text.*;
import java.util.Date;

/**
 * éûçèí î‘ä«óùÅB<p>
 * 
 * @author M.Takata
 */
public class TimeSequenceVariable implements SequenceVariable, java.io.Serializable{
    
    private static final long serialVersionUID = -2245999458822210565L;

    public static final String FORMAT_KEY = "TIME_SEQ";
    
    private SimpleDateFormat timeFormat;
    private DecimalFormat numberFormat;
    private int digit;
    
    private long currentSequence;
    private String currentTime;
    
    private StringBuilder buf = new StringBuilder();
    private Date date;
    
    private String current;
    
    public TimeSequenceVariable(String format){
        int index = format.indexOf('(');
        if(index == -1 || format.indexOf(')') != format.length() - 1){
            throw new IllegalArgumentException("Illegal format : " + format);
        }
        String paramStr = format.substring(index + 1, format.length() - 1);
        String[] params = paramStr.split(",");
        if(params.length != 2){
            throw new IllegalArgumentException("Illegal format : " + format);
        }
        
        timeFormat = new SimpleDateFormat(params[0]);
        int digit = 0;
        try{
            digit = Integer.parseInt(params[1]);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Illegal format : " + format);
        }
        if(digit <= 0 || digit > 18){
            throw new IllegalArgumentException("Illegal format : " + format);
        }
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < digit; i++){
            buf.append('0');
        }
        this.digit = digit;
        this.numberFormat = new DecimalFormat(buf.toString());
        date = new Date();
        date.setTime(System.currentTimeMillis());
        currentTime = timeFormat.format(date);
        currentSequence = -1;
        increment();
    }
    
    public TimeSequenceVariable(String timeFormat, int digit){
    }
    
    public boolean increment(){
        boolean isOver = false;
        date.setTime(System.currentTimeMillis());
        String time = timeFormat.format(date);
        String seq = null;
        if(time.equals(currentTime)){
            currentSequence++;
            seq = numberFormat.format(currentSequence);
            if(seq.length() != digit){
                currentSequence = 1;
                seq = numberFormat.format(currentSequence);
                isOver = true;
            }
        }else{
            currentSequence = 1;
            seq = numberFormat.format(currentSequence);
        }
        currentTime = time;
        buf.setLength(0);
        buf.append(time);
        buf.append(seq);
        current = buf.toString();
        return isOver;
    }
    
    public void clear(){
        currentSequence = -1;
        increment();
    }
    
    public String getCurrent(){
        return current;
    }
}
