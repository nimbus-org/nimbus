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
package jp.ossc.nimbus.beans;

import java.beans.*;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * {@link Date}型のPropertyEditorクラス。<p>
 * 日付文字列（yyyy/MM/dd HH:mm:ss SSS）をjava.util.Date型のオブジェクトに変換する。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;2006/08/15 15:20:11 100<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse("2006/08/15 15:20:11 100")<br>
 * <br>
 * のように変換される。<br>
 * また、設定する必要のないフィールドは空にすると、そのフィールドの最小値に設定される。<br>
 * 例：<br>
 * &nbsp;&nbsp;//15 15::11<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse("1970/01/15 15:00:11 000")<br>
 * <br>
 * のように変換される。<br>
 * また、現在時刻から設定したい場合は、各フィールドに"NOW"を設定する。<br>
 * 例：<br>
 * &nbsp;&nbsp;NOW/NOW/15 15:NOW:11 NOW<br>
 * <br>
 * のような文字列が、現在日付を2006/09/01 13:59:40 150とすると<br>
 * <br>
 * &nbsp;&nbsp;new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").parse("2006/09/15 15:59:11 150")<br>
 * <br>
 * のように変換される。<br>
 * また、単純に現在時刻を設定したい場合は、"NOW"を設定する。<br>
 * 例：<br>
 * &nbsp;&nbsp;NOW<br>
 * <br>
 * のような文字列が、<br>
 * <br>
 * &nbsp;&nbsp;new Date()<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class DateEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -8460152792331112919L;
    
    public static final String NOW = "NOW";
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss SSS";
    private static final String DELIMETER_SPACE = " ";
    private SimpleDateFormat format;
    
    /**
     * 指定された文字列を解析してプロパティ値を設定する。<p>
     *
     * @param text 解析される文字列
     */
    public void setAsText(String text){
        if(text == null){
            setValue(null);
            return;
        }
        String str = Utility.replaceSystemProperty(text).trim();
        
        long offset = 0l;
        final int plusIndex = str.lastIndexOf('+');
        final int minusIndex = str.lastIndexOf('-');
        if(plusIndex != -1){
            try{
                offset = Long.parseLong(str.substring(plusIndex + 1, str.length()).trim());
                str = str.substring(0, plusIndex).trim();
            }catch(NumberFormatException e){}
        }else if(minusIndex != -1){
            try{
                offset = - Long.parseLong(str.substring(minusIndex + 1, str.length()).trim());
                str = str.substring(0, minusIndex).trim();
            }catch(NumberFormatException e){}
        }
        if(NOW.equals(str)){
            setValue(new Date(System.currentTimeMillis() + offset));
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        Calendar now = null;
        if(str.indexOf(NOW) != -1){
            now = Calendar.getInstance();
        }
        StringTokenizer tokens = new StringTokenizer(str, DELIMETER_SPACE);
        final int count = tokens.countTokens();
        if(count > 3 || count == 0){
            throw new IllegalArgumentException(str);
        }
        String token = null;
        boolean isSetDate = false;
        boolean isSetTime = false;
        if(tokens.hasMoreTokens()){
            token = tokens.nextToken();
            if(token.indexOf('/') != -1){
                setDateField(now, calendar, token);
                isSetDate = true;
            }else if(token.indexOf(':') != -1){
                setTimeField(now, calendar, token);
                isSetTime = true;
            }else if(count == 1){
                setMillisField(now, calendar, token);
            }else if(count >= 2){
                setDateField(now, calendar, token);
                isSetDate = true;
            }
        }
        if(tokens.hasMoreTokens()){
            token = tokens.nextToken();
            if(token.indexOf('/') != -1){
                throw new IllegalArgumentException(str);
            }else if(token.indexOf(':') != -1){
                setTimeField(now, calendar, token);
            }else if(isSetDate){
                setTimeField(now, calendar, token);
                isSetTime = true;
            }else if(isSetTime){
                setMillisField(now, calendar, token);
            }
            
            if(tokens.hasMoreTokens()){
                token = tokens.nextToken();
                if(token.indexOf('/') != -1){
                    throw new IllegalArgumentException(str);
                }else if(token.indexOf(':') != -1){
                    throw new IllegalArgumentException(str);
                }else{
                    setMillisField(now, calendar, token);
                }
                
                if(tokens.hasMoreTokens()){
                    throw new IllegalArgumentException(str);
                }
            }
        }
        if(offset != 0){
            calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
        }
        setValue(calendar.getTime());
    }
    
    private void setDateField(Calendar now, Calendar calendar, String str){
        String tmp = str.trim();
        int index = tmp.lastIndexOf('/');
        int year = -1;
        int month = -1;
        int day = -1;
        if(index == -1){
            if(tmp.equals(NOW)){
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DATE);
            }else{
                throw new IllegalArgumentException(
                    "Invalid date field : " + str
                );
            }
        }else{
            if(index != tmp.length() - 1){
                String dayStr = tmp.substring(index + 1).trim();
                if(dayStr.equals(NOW)){
                    day = now.get(Calendar.DATE);
                }else{
                    day = Integer.parseInt(dayStr);
                }
            }
            tmp = tmp.substring(0, index).trim();
            index = tmp.lastIndexOf('/');
            if(index == -1){
                if(tmp.equals(NOW)){
                    month = now.get(Calendar.MONTH) + 1;
                }else{
                    int length = tmp.length();
                    if(length != 0){
                        if(length == 4){
                            year = Integer.parseInt(tmp);
                            month = day;
                            day = -1;
                        }else if(length == 2 || length == 1){
                            month = Integer.parseInt(tmp);
                        }else{
                            throw new IllegalArgumentException(
                                "Invalid date field : " + str
                            );
                        }
                    }
                }
            }else{
                if(index != tmp.length() - 1){
                    String monthStr = tmp.substring(index + 1).trim();
                    if(monthStr.equals(NOW)){
                        month = now.get(Calendar.MONTH) + 1;
                    }else if(monthStr.length() != 0){
                        month = Integer.parseInt(monthStr);
                    }
                }
                tmp = tmp.substring(0, index).trim();
                if(index != tmp.length() - 1){
                    if(tmp.equals(NOW)){
                        year = now.get(Calendar.YEAR);
                    }else if(tmp.length() != 0){
                        year = Integer.parseInt(tmp);
                    }
                }
            }
        }
        if(year != -1){
            if(year < 0){
                throw new IllegalArgumentException(
                    "Invalid date field : " + str
                );
            }
            calendar.set(Calendar.YEAR, year);
        }
        if(month != -1){
            if(month > 12 || month < 1){
                throw new IllegalArgumentException(
                    "Invalid date field : " + str
                );
            }
            calendar.set(Calendar.MONTH, month - 1);
        }
        if(day != -1){
            if(day > 31 || day < 1){
                throw new IllegalArgumentException(
                    "Invalid date field : " + str
                );
            }
            calendar.set(Calendar.DATE, day);
        }
    }
    
    private void setTimeField(Calendar now, Calendar calendar, String str){
        String tmp = str;
        int index = tmp.lastIndexOf(':');
        int hour = -1;
        int minute = -1;
        int second = -1;
        if(index == -1){
            if(tmp.equals(NOW)){
                hour = now.get(Calendar.HOUR_OF_DAY);
                minute = now.get(Calendar.MINUTE);
                second = now.get(Calendar.SECOND);
            }else{
                throw new IllegalArgumentException(
                    "Invalid time field : " + str
                );
            }
        }else{
            if(index != tmp.length() - 1){
                String secondStr = tmp.substring(index + 1).trim();
                if(secondStr.equals(NOW)){
                    second = now.get(Calendar.SECOND);
                }else{
                    second = Integer.parseInt(secondStr);
                }
            }
            tmp = tmp.substring(0, index).trim();
            index = tmp.lastIndexOf(':');
            if(index == -1){
                if(tmp.equals(NOW)){
                    hour = now.get(Calendar.HOUR_OF_DAY);
                }else if(tmp.length() != 0){
                    hour = Integer.parseInt(tmp);
                }
                if(hour != -1){
                    minute = second;
                    second = -1;
                }
            }else{
                if(index != tmp.length() - 1){
                    String minuteStr = tmp.substring(index + 1).trim();
                    if(minuteStr.equals(NOW)){
                        minute = now.get(Calendar.MINUTE);
                    }else if(minuteStr.length() != 0){
                        minute = Integer.parseInt(minuteStr);
                    }
                }
                tmp = tmp.substring(0, index).trim();
                if(index != tmp.length() - 1){
                    if(tmp.equals(NOW)){
                        hour = now.get(Calendar.HOUR_OF_DAY);
                    }else if(tmp.length() != 0){
                        hour = Integer.parseInt(tmp);
                    }
                }
            }
        }
        if(hour != -1){
            if(hour > 24 || hour < 0){
                throw new IllegalArgumentException(
                    "Invalid time field : " + str
                );
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
        }
        if(minute != -1){
            if(minute > 59 || minute < 0){
                throw new IllegalArgumentException(
                    "Invalid time field : " + str
                );
            }
            calendar.set(Calendar.MINUTE, minute);
        }
        if(second != -1){
            if(second > 59 || second < 0){
                throw new IllegalArgumentException(
                    "Invalid time field : " + str
                );
            }
            calendar.set(Calendar.SECOND, second);
        }
    }
    
    private void setMillisField(Calendar now, Calendar calendar, String str){
        String tmp = str.trim();
        if(tmp.length() != 0){
            int millis = 0;
            if(tmp.equals(NOW)){
                millis = now.get(Calendar.MILLISECOND);
            }else{
                millis = Integer.parseInt(tmp);
            }
            if(millis > 999 || millis < 0){
                throw new IllegalArgumentException(
                    "Invalid millis field : " + tmp
                );
            }
            calendar.set(Calendar.MILLISECOND, millis);
        }
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Date date = (Date)getValue();
        if(date == null){
            return null;
        }else{
            if(format == null){
                format = new SimpleDateFormat(DATE_FORMAT);
            }
            return format.format(date);
        }
    }
}
