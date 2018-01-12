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
package jp.ossc.nimbus.service.system;

import java.util.Date;
import java.text.SimpleDateFormat;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * システム時刻。<p>
 *
 * @author M.Takata
 */
public class DefaultTimeService extends ServiceBase implements Time, DefaultTimeServiceMBean{
    
    private static final long serialVersionUID = -55472246209930175L;
    
    private Date time;
    private Date fixedTime;
    private int offsetDays;
    private int offsetHours;
    private int offsetMinutes;
    private int offsetSeconds;
    private long offsetTimeMillis;
    
    private long currentOffsetTimeMillis;
    
    public void setOffsetDays(int days){
        offsetDays = days;
    }
    public int getOffsetDays(){
        return offsetDays;
    }
    
    public void setOffsetHours(int hours){
        offsetHours = hours;
    }
    public int getOffsetHours(){
        return offsetHours;
    }
    
    public void setOffsetMinutes(int minutes){
        offsetMinutes = minutes;
    }
    public int getOffsetMinutes(){
        return offsetMinutes;
    }
    
    public void setOffsetSeconds(int seconds){
        offsetSeconds = seconds;
    }
    public int getOffsetSeconds(){
        return offsetSeconds;
    }
    
    public void setOffsetTimeMillis(long offset){
        offsetTimeMillis = offset;
    }
    public long getOffsetTimeMillis(){
        return offsetTimeMillis;
    }
    
    public void setTime(Date time){
        this.time = time;
    }
    public Date getTime(){
        return time;
    }
    
    public void setFixedTime(Date time){
        fixedTime = time;
    }
    public Date getFixedTime(){
        return fixedTime;
    }
    
    public void startService() throws Exception{
        long tmpOffsetTimeMillis = 0;
        if(time != null){
            tmpOffsetTimeMillis = time.getTime() - System.currentTimeMillis();
        }
        tmpOffsetTimeMillis += (offsetDays * 24 * 60 * 60 * 1000);
        tmpOffsetTimeMillis += (offsetHours * 60 * 60 * 1000);
        tmpOffsetTimeMillis += (offsetMinutes * 60 * 1000);
        tmpOffsetTimeMillis += (offsetSeconds * 1000);
        tmpOffsetTimeMillis += offsetTimeMillis;
        currentOffsetTimeMillis = tmpOffsetTimeMillis;
    }
    
    public long currentTimeMillis(){
        if(fixedTime == null){
            return System.currentTimeMillis() + currentOffsetTimeMillis;
        }else{
            return fixedTime.getTime();
        }
    }
    
    public String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return format.format(new Date(currentTimeMillis()));
    }
}