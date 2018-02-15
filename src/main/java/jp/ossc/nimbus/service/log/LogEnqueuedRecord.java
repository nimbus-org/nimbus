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
package jp.ossc.nimbus.service.log;

import java.util.*;

/**
 * ログのキューに挿入される情報を格納するクラス。<p>
 *
 * @author M.Takata
 */
public class LogEnqueuedRecord implements java.io.Serializable{
    
    private static final long serialVersionUID = 6657674897069127746L;
    
    private LogMessageRecord messageRecord;
    private Map elements;
    private List embeds;
    private Locale locale;
    private Throwable throwable;
    
    public LogEnqueuedRecord(){
        this(null, null, (String)null, null);
    }
    
    public LogEnqueuedRecord(
        LogMessageRecord messageRecord,
        Locale locale,
        String embed,
        Throwable throwable
    ){
        this(messageRecord, locale, (String[])null, throwable);
        addEmbedString(embed);
    }
    
    public LogEnqueuedRecord(
        LogMessageRecord messageRecord,
        Locale locale,
        String[] embeds,
        Throwable throwable
    ){
        this.messageRecord = messageRecord;
        this.elements = new HashMap();
        this.embeds = new ArrayList();
        setEmbedStringArray(embeds);
        this.locale = locale;
        if(locale == null){
            this.locale = Locale.getDefault();
        }
        this.throwable = throwable;
    }
    
    public LogMessageRecord getLogMessageRecord(){
        return messageRecord;
    }
    public void setLogMessageRecord(LogMessageRecord record){
        messageRecord = record;
    }
    public void setEmbedStringArray(String[] embeds){
        if(embeds == null){
            return;
        }
        this.embeds.clear();
        for(int i = 0; i < embeds.length; i++){
            this.embeds.add(embeds[i]);
        }
    }
    public String[] getEmbedStringArray(){
        if(embeds.size() == 0){
            return null;
        }
        return (String[])embeds.toArray(new String[embeds.size()]);
    }
    public void addEmbedString(String embed){
        embeds.add(embed);
    }
    public void addWritableElement(Object key, Object element){
        elements.put(key, element);
    }
    public Map getWritableElements(){
        return elements;
    }
    public void setLocale(Locale locale){
        this.locale = locale;
    }
    public Locale getLocale(){
        return locale;
    }
    public void setThrowable(Throwable throwable){
        this.throwable = throwable;
    }
    public Throwable getThrowable(){
        return throwable;
    }
}
