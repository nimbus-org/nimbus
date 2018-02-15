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
import javax.servlet.http.Cookie;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * JournalHttpServletResponseWrapperオブジェクトをMapフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpServletResponseWrapperMapJournalEditorService
 extends ServletResponseMapJournalEditorService
 implements HttpServletResponseWrapperMapJournalEditorServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = -5062156173243815339L;
    
    private boolean isOutputContentLength = true;
    private boolean isOutputContent = true;
    private boolean isOutputHeaders = true;
    private boolean isOutputCookies = true;
    private boolean isOutputStatus = true;
    private boolean isOutputStatusMessage = true;
    private boolean isOutputIsSentError = true;
    private boolean isOutputRedirectLocation = true;
    
    private String[] secretHeaders;
    protected Set secretHeaderSet;
    private String[] enabledHeaders;
    protected Set enabledHeaderSet;
    private String[] secretCookies;
    protected Set secretCookieSet;
    private String[] enabledCookies;
    protected Set enabledCookieSet;
    
    public void setOutputContentLength(boolean isOutput){
        isOutputContentLength = isOutput;
    }
    
    public boolean isOutputContentLength(){
        return isOutputContentLength;
    }
    
    public void setOutputContent(boolean isOutput){
        isOutputContent = isOutput;
    }
    
    public boolean isOutputContent(){
        return isOutputContent;
    }
    
    public void setOutputHeaders(boolean isOutput){
        isOutputHeaders = isOutput;
    }
    
    public boolean isOutputHeaders(){
        return isOutputHeaders;
    }
    
    public void setOutputCookies(boolean isOutput){
        isOutputCookies = isOutput;
    }
    
    public boolean isOutputCookies(){
        return isOutputCookies;
    }
    
    public void setOutputStatus(boolean isOutput){
        isOutputStatus = isOutput;
    }
    
    public boolean isOutputStatus(){
        return isOutputStatus;
    }
    
    public void setOutputStatusMessage(boolean isOutput){
        isOutputStatusMessage = isOutput;
    }
    
    public boolean isOutputStatusMessage(){
        return isOutputStatusMessage;
    }
    
    public void setOutputIsSentError(boolean isOutput){
        isOutputIsSentError = isOutput;
    }
    
    public boolean isOutputIsSentError(){
        return isOutputIsSentError;
    }
    
    public void setOutputRedirectLocation(boolean isOutput){
        isOutputRedirectLocation = isOutput;
    }
    
    public boolean isOutputRedirectLocation(){
        return isOutputRedirectLocation;
    }
    
    public void setSecretHeaders(String[] names){
        secretHeaders = names;
    }
    
    public String[] getSecretHeaders(){
        return secretHeaders;
    }
    
    public void setEnabledHeaders(String[] names){
        enabledHeaders = names;
    }
    
    public String[] getEnabledHeaders(){
        return enabledHeaders;
    }
    
    public void setSecretCookies(String[] names){
        secretCookies = names;
    }
    
    public String[] getSecretCookies(){
        return secretCookies;
    }
    
    public void setEnabledCookies(String[] names){
        enabledCookies = names;
    }
    
    public String[] getEnabledCookies(){
        return enabledCookies;
    }
    
    public void createService() throws Exception{
        super.createService();
        secretHeaderSet = new HashSet();
        enabledHeaderSet = new HashSet();
        secretCookieSet = new HashSet();
        enabledCookieSet = new HashSet();
    }
    
    public void startService() throws Exception{
        super.startService();
        if(secretHeaders != null){
            for(int i = 0; i < secretHeaders.length; i++){
                secretHeaderSet.add(secretHeaders[i]);
            }
        }
        if(enabledHeaders != null){
            for(int i = 0; i < enabledHeaders.length; i++){
                enabledHeaderSet.add(enabledHeaders[i]);
            }
        }
        if(secretCookies != null){
            for(int i = 0; i < secretCookies.length; i++){
                secretCookieSet.add(secretCookies[i]);
            }
        }
        if(enabledCookies != null){
            for(int i = 0; i < enabledCookies.length; i++){
                enabledCookieSet.add(enabledCookies[i]);
            }
        }
    }
    
    public void stopService() throws Exception{
        super.stopService();
        secretHeaderSet.clear();
        enabledHeaderSet.clear();
        secretCookieSet.clear();
        enabledCookieSet.clear();
    }
    
    public void destroyService() throws Exception{
        super.destroyService();
        secretHeaderSet = null;
        enabledHeaderSet = null;
        secretCookieSet = null;
        enabledCookieSet = null;
    }
    
    /**
     * ジャーナルとして与えられたHttpServletRequest型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public Map toMap(EditorFinder finder, Object key, Object value){
        final Map result = super.toMap(finder, key, value);
        
        final JournalHttpServletResponseWrapper response
             = (JournalHttpServletResponseWrapper)value;
        
        if(isOutputContentLength()){
            makeContentLengthFormat(finder, key, response, result);
        }
        
        if(isOutputContent()){
            makeContentFormat(finder, key, response, result);
        }
        
        if(isOutputHeaders()){
            makeHeadersFormat(finder, key, response, result);
        }
        
        if(isOutputCookies()){
            makeCookiesFormat(finder, key, response, result);
        }
        
        if(isOutputStatus()){
            makeStatusFormat(finder, key, response, result);
        }
        
        if(isOutputStatusMessage()){
            makeStatusMessageFormat(finder, key, response, result);
        }
        
        if(isOutputIsSentError()){
            makeIsSentErrorFormat(finder, key, response, result);
        }
        
        if(isOutputRedirectLocation()){
            makeRedirectLocationFormat(finder, key, response, result);
        }
        
        return result;
    }
    
    protected Map makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        map.put(CONTENT_LENGTH_KEY, new Integer(response.getContentLength()));
        return map;
    }
    
    protected Map makeContentFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        map.put(CONTENT_KEY, response.getContent());
        return map;
    }
    
    protected Map makeHeadersFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        final Iterator headerNames = response.getHeaderNames();
        if(!headerNames.hasNext()){
            return map;
        }
        final Map subMap = new HashMap();
        while(headerNames.hasNext()){
            final String name = (String)headerNames.next();
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(name)){
                continue;
            }
            if(secretHeaderSet.contains(name)){
                subMap.put(name, null);
            }else{
                subMap.put(
                    name,
                    makeObjectFormat(finder, key, response.getHeaders(name))
                );
            }
        }
        map.put(HTTP_HEADER_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
    
    protected Map makeCookiesFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        final Cookie[] cookies = response.getCookies();
        if(cookies == null || cookies.length == 0){
            return map;
        }
        final Map subMap = new HashMap();
        for(int i = 0; i < cookies.length; i++){
            final String name = (String)cookies[i].getName();
            if(!enabledCookieSet.isEmpty()
                 && !enabledCookieSet.contains(name)){
                continue;
            }
            if(secretCookieSet.contains(name)){
                subMap.put(name, null);
            }else{
                subMap.put(
                    name,
                    makeObjectFormat(finder, key, cookies[i].getValue())
                );
            }
        }
        map.put(COOKIE_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
    
    protected Map makeStatusFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        map.put(STATUS_KEY, new Integer(response.getStatus()));
        return map;
    }
    
    protected Map makeStatusMessageFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        map.put(STATUS_MESSAGE_KEY, response.getStatusMessage());
        return map;
    }
    
    protected Map makeIsSentErrorFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        map.put(IS_SENT_ERROR_KEY, Boolean.valueOf(response.isSentError()));
        return map;
    }
    
    protected Map makeRedirectLocationFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        Map map
    ){
        map.put(REDIRECT_LOCATION_KEY, response.getRedirectLocation());
        return map;
    }
}
