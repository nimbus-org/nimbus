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

import java.util.*;
import java.io.Serializable;

import jp.ossc.nimbus.ioc.FacadeValue;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link FacadeValue}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class FacadeValueJournalEditorService
 extends UnitOfWorkJournalEditorService
 implements FacadeValueJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -456435540265845118L;
    
    protected static final String HEADER = "[FacadeValue]";
    private static final String HEADERS_HEADER = "Headers : ";
    protected static final String DEFAULT_SECRET_STRING = "******";
    protected static final String HEADER_SEPARATOR = " = ";
    
    protected boolean isOutputHeaders = true;
    protected String[] enabledHeaders;
    protected Set enabledHeaderSet;
    protected String[] secretHeaders;
    protected Set secretHeaderSet;
    protected String secretString = DEFAULT_SECRET_STRING;
    
    public FacadeValueJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputHeaders(boolean isOutput){
        isOutputHeaders = isOutput;
    }
    
    public boolean isOutputHeaders(){
        return isOutputHeaders;
    }
    
    public void setEnabledHeaders(String[] names){
        enabledHeaders = names;
    }
    
    public String[] getEnabledHeaders(){
        return enabledHeaders;
    }
    
    public void setSecretHeaders(String[] names){
        secretHeaders = names;
    }
    
    public String[] getSecretHeaders(){
        return secretHeaders;
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void startService() throws Exception{
        if(enabledHeaders != null){
            if(enabledHeaderSet == null){
                enabledHeaderSet = new HashSet();
            }
            for(int i = 0; i < enabledHeaders.length; i++){
                enabledHeaderSet.add(enabledHeaders[i]);
            }
        }
        if(secretHeaders != null){
            if(secretHeaderSet == null){
                secretHeaderSet = new HashSet();
            }
            for(int i = 0; i < secretHeaders.length; i++){
                secretHeaderSet.add(secretHeaders[i]);
            }
        }
    }
    
    public void stopService() throws Exception{
        if(enabledHeaderSet != null){
            enabledHeaderSet.clear();
        }
        if(secretHeaderSet != null){
            secretHeaderSet.clear();
        }
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        boolean isMake = super.processBlock(
            finder,
            key,
            value,
            buf
        );
        
        final FacadeValue facadeVal = (FacadeValue)value;
        if(isOutputHeaders()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeHeadersFormat(finder, key, facadeVal, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeHeadersFormat(
        EditorFinder finder,
        Object key,
        FacadeValue facadeVal,
        StringBuilder buf
    ){
        buf.append(HEADERS_HEADER);
        final Iterator names = facadeVal.getHederKeys().iterator();
        if(!names.hasNext()){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        while(names.hasNext()){
            final String name = (String)names.next();
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(HEADER_SEPARATOR);
            if(secretHeaderSet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                makeObjectFormat(finder, null, facadeVal.getHeader(name), subBuf);
            }
            if(names.hasNext()){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}