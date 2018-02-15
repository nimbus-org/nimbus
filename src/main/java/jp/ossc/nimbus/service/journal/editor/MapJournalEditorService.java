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
package jp.ossc.nimbus.service.journal.editor;

import java.io.Serializable;
import java.util.*;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link Map}オブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class MapJournalEditorService
 extends ImmutableJournalEditorServiceBase
 implements MapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 7556861235467148050L;
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private String startDelimiter = "{";
    private String endDelimiter = "}";
    private String delimiter = ", ";
    private String keyValueDelimiter = "=";
    private String[] secretKeys;
    private Set secretKeySet;
    private String[] enabledKeys;
    private Set enabledKeySet;
    private String secretString = DEFAULT_SECRET_STRING;
    private String startValueDelimiter;
    private String endValueDelimiter;
    
    public void setStartDelimiter(String delim){
        startDelimiter = delim;
    }
    public String getStartDelimiter(){
        return startDelimiter;
    }
    
    public void setEndDelimiter(String delim){
        endDelimiter = delim;
    }
    public String getEndDelimiter(){
        return endDelimiter;
    }
    
    public void setDelimiter(String delim){
        delimiter = delim;
    }
    public String getDelimiter(){
        return delimiter;
    }
    
    public void setKeyValueDelimiter(String delim){
        keyValueDelimiter = delim;
    }
    public String getKeyValueDelimiter(){
        return keyValueDelimiter;
    }
    
    public void setStartValueDelimiter(String delim){
        startValueDelimiter = delim;
    }
    public String getStartValueDelimiter(){
        return startValueDelimiter;
    }
    
    public void setEndValueDelimiter(String delim){
        endValueDelimiter = delim;
    }
    public String getEndValueDelimiter(){
        return endValueDelimiter;
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setSecretKeys(String[] keys){
        secretKeys = keys;
    }
    
    public String[] getSecretKeys(){
        return secretKeys;
    }
    
    public void setEnabledKeys(String[] keys){
        enabledKeys = keys;
    }
    
    public String[] getEnabledKeys(){
        return enabledKeys;
    }
    
    public void startService(){
        if(secretKeys != null){
            secretKeySet = new HashSet();
            for(int i = 0; i < secretKeys.length; i++){
                secretKeySet.add(secretKeys[i]);
            }
        }
        if(enabledKeys != null){
            enabledKeySet = new HashSet();
            for(int i = 0; i < enabledKeys.length; i++){
                enabledKeySet.add(enabledKeys[i]);
            }
        }
    }
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final Map map = (Map)value;
        buf.append(startDelimiter);
        final Iterator keys = map.keySet().iterator();
        boolean isOutput = false;
        while(keys.hasNext()){
            final Object k = keys.next();
            if(enabledKeySet != null
                && !enabledKeySet.contains(k)){
                continue;
            }
            if(isOutput){
                buf.append(delimiter);
            }
            makeObjectFormat(
                finder,
                null,
                k,
                buf
            );
            buf.append(keyValueDelimiter);
            if(secretKeySet != null
                && secretKeySet.contains(k)){
                if(getStartValueDelimiter() != null){
                    buf.append(getStartValueDelimiter());
                }
                makeObjectFormat(
                    finder,
                    null,
                    getSecretString(),
                    buf
                );
                if(getEndValueDelimiter() != null){
                    buf.append(getEndValueDelimiter());
                }
            }else{
                Object val = map.get(k);
                if(val != null && getStartValueDelimiter() != null){
                    buf.append(getStartValueDelimiter());
                }
                makeObjectFormat(
                    finder,
                    null,
                    val,
                    buf
                );
                if(val != null && getEndValueDelimiter() != null){
                    buf.append(getEndValueDelimiter());
                }
            }
            isOutput = true;
        }
        buf.append(endDelimiter);
        return buf.toString();
    }
}
