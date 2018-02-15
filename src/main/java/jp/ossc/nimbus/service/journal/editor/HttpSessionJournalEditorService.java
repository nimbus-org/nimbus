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
import javax.servlet.http.HttpSession;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * HttpSessionオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpSessionJournalEditorService
 extends BlockJournalEditorServiceBase
 implements HttpSessionJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -1628012879212995304L;
    
    private static final String ATTRIBUTE_HEADER = "Attribute : ";
    private static final String CREATION_TIME_HEADER = "Creation Time : ";
    private static final String LAST_ACCESSED_TIME_HEADER = "Last Accessed Time : ";
    private static final String MAX_INACTIVE_INTERVAL_HEADER = "Max Inactive Interval : ";
    private static final String IS_NEW_HEADER = "New : ";
    private static final String ID_HEADER = "ID : ";
    
    private static final String ATTRIBUTE_SEPARATOR = " = ";
    
    private static final String HEADER = "[HttpSession]";
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private boolean isOutputCreationTime = true;
    private boolean isOutputLastAccessedTime = true;
    private boolean isOutputMaxInactiveInterval = false;
    private boolean isOutputAttributes = true;
    private boolean isOutputIsNew = true;
    private boolean isOutputId = true;
    
    private String secretString = DEFAULT_SECRET_STRING;
    private String[] secretAttributes;
    private Set secretAttributeSet;
    private String[] enabledAttributes;
    private Set enabledAttributeSet;
    
    public HttpSessionJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputCreationTime(boolean isOutput){
        isOutputCreationTime = isOutput;
    }
    
    public boolean isOutputCreationTime(){
        return isOutputCreationTime;
    }
    
    public void setOutputLastAccessedTime(boolean isOutput){
        isOutputLastAccessedTime = isOutput;
    }
    
    public boolean isOutputLastAccessedTime(){
        return isOutputLastAccessedTime;
    }
    
    public void setOutputMaxInactiveInterval(boolean isOutput){
        isOutputMaxInactiveInterval = isOutput;
    }
    
    public boolean isOutputMaxInactiveInterval(){
        return isOutputMaxInactiveInterval;
    }
    
    public void setOutputIsNew(boolean isOutput){
        isOutputIsNew = isOutput;
    }
    
    public boolean isOutputIsNew(){
        return isOutputIsNew;
    }
    
    public void setOutputId(boolean isOutput){
        isOutputId = isOutput;
    }
    
    public boolean isOutputId(){
        return isOutputId;
    }
    
    public void setOutputAttributes(boolean isOutput){
        isOutputAttributes = isOutput;
    }
    
    public boolean isOutputAttributes(){
        return isOutputAttributes;
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setSecretAttributes(String[] names){
        secretAttributes = names;
    }
    
    public String[] getSecretAttributes(){
        return secretAttributes;
    }
    
    public void setEnabledAttributes(String[] names){
        enabledAttributes = names;
    }
    
    public String[] getEnabledAttributes(){
        return enabledAttributes;
    }
    
    public void createService() throws Exception{
        secretAttributeSet = new HashSet();
        enabledAttributeSet = new HashSet();
    }
    
    public void startService() throws Exception{
        if(secretAttributes != null){
            for(int i = 0; i < secretAttributes.length; i++){
                secretAttributeSet.add(secretAttributes[i]);
            }
        }
        if(enabledAttributes != null){
            for(int i = 0; i < enabledAttributes.length; i++){
                enabledAttributeSet.add(enabledAttributes[i]);
            }
        }
    }
    
    public void stopService() throws Exception{
        secretAttributeSet.clear();
        enabledAttributeSet.clear();
    }
    
    public void destroyService() throws Exception{
        secretAttributeSet = null;
        enabledAttributeSet = null;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final HttpSession session = (HttpSession)value;
        boolean isMake = false;
        if(isOutputId()){
            makeIdFormat(finder, key, session, buf);
            isMake = true;
        }
        
        if(isOutputCreationTime()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCreationTimeFormat(finder, key, session, buf);
            isMake = true;
        }
        
        if(isOutputLastAccessedTime()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeLastAccessedTimeFormat(finder, key, session, buf);
            isMake = true;
        }
        
        if(isOutputMaxInactiveInterval()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeMaxInactiveIntervalFormat(finder, key, session, buf);
            isMake = true;
        }
        
        if(isOutputIsNew()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeIsNewFormat(finder, key, session, buf);
            isMake = true;
        }
        
        if(isOutputAttributes()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeAttributesFormat(finder, key, session, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeIdFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuilder buf
    ){
        return buf.append(ID_HEADER).append(session.getId());
    }
    
    protected StringBuilder makeCreationTimeFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuilder buf
    ){
        buf.append(CREATION_TIME_HEADER);
        makeObjectFormat(
            finder,
            key,
            new Date(session.getCreationTime()),
            buf
        );
        return buf;
    }
    
    protected StringBuilder makeLastAccessedTimeFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuilder buf
    ){
        buf.append(LAST_ACCESSED_TIME_HEADER);
        makeObjectFormat(
            finder,
            key,
            new Date(session.getLastAccessedTime()),
            buf
        );
        return buf;
    }
    
    protected StringBuilder makeMaxInactiveIntervalFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuilder buf
    ){
        return buf.append(MAX_INACTIVE_INTERVAL_HEADER)
            .append(session.getMaxInactiveInterval());
    }
    
    protected StringBuilder makeIsNewFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuilder buf
    ){
        return buf.append(IS_NEW_HEADER)
            .append(session.isNew());
    }
    
    protected StringBuilder makeAttributesFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuilder buf
    ){
        buf.append(ATTRIBUTE_HEADER);
        final Enumeration attrNames = session.getAttributeNames();
        if(attrNames.hasMoreElements()){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuilder subBuf = new StringBuilder();
        while(attrNames.hasMoreElements()){
            final String name = (String)attrNames.nextElement();
            if(!enabledAttributeSet.isEmpty()
                 && !enabledAttributeSet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(ATTRIBUTE_SEPARATOR);
            if(secretAttributeSet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    session.getAttribute(name),
                    subBuf
                );
            }
            if(attrNames.hasMoreElements()){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}
