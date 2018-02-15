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
import javax.servlet.ServletRequest;
import javax.servlet.http.*;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * HttpServletRequestオブジェクトをMapフォーマットするエディタ。<p>
 * このエディタによって編集されたMapは、{@link ServletRequestMapJournalEditorService}の持つMap構造に加えて、以下の構造を持つ。<br>
 * <table broder="1">
 *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="5">値</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th colspan="4">内容</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
 *   <tr><td>java.lang.String</td><td>{@link #AUTH_TYPE_KEY}</td><td>java.lang.String</td><td colspan="4">認証タイプ名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REMOTE_USER_KEY}</td><td>java.lang.String</td><td colspan="4">認証されたリモートのユーザ名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #USER_PRINCIPAL_KEY}</td><td>java.lang.String</td><td colspan="4">認証されたユーザの主体</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REQUEST_URL_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストされたURL</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REQUEST_URI_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストされたURLのプロトコル名からクエリ文字列までの部分</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SERVLET_PATH_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストされたURLのサーブレットを呼び出すURL部分</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SESSION_ID_KEY}</td><td>java.lang.String</td><td colspan="4">セッションID</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SESSION_ID_FROM_COOKIE_KEY}</td><td>java.lang.Boolean</td><td colspan="4">セッションIDがCookieから取得されたかを示すフラグ</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SESSION_ID_FROM_URL_KEY}</td><td>java.lang.Boolean</td><td colspan="4">セッションIDがURLから取得されたかを示すフラグ</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #HTTP_METHOD_KEY}</td><td>java.lang.String</td><td colspan="4">HTTPメソッド名</td></tr>
 *   <tr><td rowspan="3">java.lang.String</td><td rowspan="3">{@link #HTTP_HEADERS_KEY}</td><td rowspan="3">java.util.Map</td><td colspan="4">HTTPヘッダのマップ</td></tr>
 *   <tr><td>java.lang.String</td><td>HTTPヘッダのキー名</td><td>java.util.Enumeration</td><td>HTTPヘッダの値の列挙</td></tr>
 *   <tr><td rowspan="3">java.lang.String</td><td rowspan="3">{@link #COOKIES_KEY}</td><td rowspan="3">java.util.Map</td><td colspan="4">Cookieのマップ</td></tr>
 *   <tr><td>java.lang.String</td><td>Cookieのキー名</td><td>java.lang.String</td><td>Cookieの値</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CONTEXT_PATH_KEY}</td><td>java.lang.String</td><td colspan="4">コンテキストを示すリクエストURIの一部</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #PATH_INFO_KEY}</td><td>java.lang.String</td><td colspan="4">クライアントがURLに関連付けて送信した追加のパス情報</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #PATH_TRAN_KEY}</td><td>java.lang.String</td><td colspan="4">サーブレット名の後でクエリ文字列の前にある追加パス情報を実際のパスに変換したもの</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #QUERY_STRING_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストURLのパスの後ろに含まれているクエリ文字列</td></tr>
 * </table>
 * 但し、出力しないように設定されているものや、元のHttpServletRequestに含まれていなかった情報、J2EEのバージョンによって取得できない情報は含まれない。<br>
 * 
 * @author M.Takata
 */
public class HttpServletRequestMapJournalEditorService
 extends ServletRequestMapJournalEditorService
 implements HttpServletRequestMapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -5560369100004636591L;
    
    private boolean isOutputRequestURL = true;
    private boolean isOutputRequestURI = true;
    private boolean isOutputServletPath = true;
    private boolean isOutputContextPath = true;
    private boolean isOutputPathInfo = true;
    private boolean isOutputPathTranslated = true;
    private boolean isOutputQueryString = true;
    private boolean isOutputSessionID = true;
    private boolean isOutputIsRequestedSessionIdFromCookie = true;
    private boolean isOutputIsRequestedSessionIdFromURL = true;
    private boolean isOutputMethod = true;
    private boolean isOutputAuthType = true;
    private boolean isOutputRemoteUser = true;
    private boolean isOutputUserPrincipal = true;
    private boolean isOutputHeaders = true;
    private boolean isOutputCookies = true;
    
    private String[] secretHeaders;
    protected Set secretHeaderSet;
    private String[] enabledHeaders;
    protected Set enabledHeaderSet;
    private String[] secretCookies;
    protected Set secretCookieSet;
    private String[] enabledCookies;
    protected Set enabledCookieSet;
    
    public void setOutputRequestURL(boolean isOutput){
        isOutputRequestURL = isOutput;
    }
    
    public boolean isOutputRequestURL(){
        return isOutputRequestURL;
    }
    
    public void setOutputRequestURI(boolean isOutput){
        isOutputRequestURI = isOutput;
    }
    
    public boolean isOutputRequestURI(){
        return isOutputRequestURI;
    }
    
    public void setOutputServletPath(boolean isOutput){
        isOutputServletPath = isOutput;
    }
    
    public boolean isOutputServletPath(){
        return isOutputServletPath;
    }
    
    public void setOutputContextPath(boolean isOutput){
        isOutputContextPath = isOutput;
    }
    
    public boolean isOutputContextPath(){
        return isOutputContextPath;
    }
    
    public void setOutputPathInfo(boolean isOutput){
        isOutputPathInfo = isOutput;
    }
    
    public boolean isOutputPathInfo(){
        return isOutputPathInfo;
    }
    
    public void setOutputPathTranslated(boolean isOutput){
        isOutputPathTranslated = isOutput;
    }
    
    public boolean isOutputPathTranslated(){
        return isOutputPathTranslated;
    }
    
    public void setOutputQueryString(boolean isOutput){
        isOutputQueryString = isOutput;
    }
    
    public boolean isOutputQueryString(){
        return isOutputQueryString;
    }
    
    public void setOutputSessionID(boolean isOutput){
        isOutputSessionID = isOutput;
    }
    
    public boolean isOutputSessionID(){
        return isOutputSessionID;
    }
    
    public void setOutputIsRequestedSessionIdFromCookie(boolean isOutput){
        isOutputIsRequestedSessionIdFromCookie = isOutput;
    }
    
    public boolean isOutputIsRequestedSessionIdFromCookie(){
        return isOutputIsRequestedSessionIdFromCookie;
    }
    
    public void setOutputIsRequestedSessionIdFromURL(boolean isOutput){
        isOutputIsRequestedSessionIdFromURL = isOutput;
    }
    
    public boolean isOutputIsRequestedSessionIdFromURL(){
        return isOutputIsRequestedSessionIdFromURL;
    }
    
    public void setOutputMethod(boolean isOutput){
        isOutputMethod = isOutput;
    }
    
    public boolean isOutputMethod(){
        return isOutputMethod;
    }
    
    public void setOutputAuthType(boolean isOutput){
        isOutputAuthType = isOutput;
    }
    
    public boolean isOutputAuthType(){
        return isOutputAuthType;
    }
    
    public void setOutputRemoteUser(boolean isOutput){
        isOutputRemoteUser = isOutput;
    }
    
    public boolean isOutputRemoteUser(){
        return isOutputRemoteUser;
    }
    
    public void setOutputUserPrincipal(boolean isOutput){
        isOutputUserPrincipal = isOutput;
    }
    
    public boolean isOutputUserPrincipal(){
        return isOutputUserPrincipal;
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
    
    public void createService(){
        super.createService();
        secretHeaderSet = new HashSet();
        enabledHeaderSet = new HashSet();
        secretCookieSet = new HashSet();
        enabledCookieSet = new HashSet();
    }
    
    public void startService(){
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
    
    public void stopService(){
        super.stopService();
        secretHeaderSet.clear();
        enabledHeaderSet.clear();
        secretCookieSet.clear();
        enabledCookieSet.clear();
    }
    
    public void destroyService(){
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
        final ServletRequest request = (ServletRequest)value;
        final Map result = super.toMap(finder, key, request);
        
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        if(isOutputRequestURL()){
            makeRequestURLFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputRequestURI()){
            makeRequestURIFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputServletPath()){
            makeServletPathFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputContextPath()){
            makeContextPathFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputPathInfo()){
            makePathInfoFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputPathTranslated()){
            makePathTranslatedFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputQueryString()){
            makeQueryStringFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputSessionID()){
            makeSessionIDFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputIsRequestedSessionIdFromCookie()){
            makeIsRequestedSessionIdFromCookieFormat(
                finder,
                key,
                httpRequest,
                result
            );
        }
        
        if(isOutputIsRequestedSessionIdFromURL()){
            makeIsRequestedSessionIdFromURLFormat(
                finder,
                key,
                httpRequest,
                result
            );
        }
        
        if(isOutputMethod()){
            makeMethodFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputAuthType()){
            makeAuthTypeFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputRemoteUser()){
            makeRemoteUserFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputUserPrincipal()){
            makeUserPrincipalFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputHeaders()){
            makeHeadersFormat(finder, key, httpRequest, result);
        }
        
        if(isOutputCookies()){
            makeCookiesFormat(finder, key, httpRequest, result);
        }
        
        return result;
    }
    
    protected Map makeAuthTypeFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(AUTH_TYPE_KEY, request.getAuthType());
        return map;
    }
    
    protected Map makeRemoteUserFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(REMOTE_USER_KEY, request.getRemoteUser());
        return map;
    }
    
    protected Map makeUserPrincipalFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(USER_PRINCIPAL_KEY, request.getUserPrincipal());
        return map;
    }
    
    protected Map makeRequestURLFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(REQUEST_URL_KEY, request.getRequestURL().toString());
        return map;
    }
    
    protected Map makeRequestURIFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(REQUEST_URI_KEY, request.getRequestURI());
        return map;
    }
    
    protected Map makeServletPathFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(SERVLET_PATH_KEY, request.getServletPath());
        return map;
    }
    
    protected Map makeContextPathFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(CONTEXT_PATH_KEY, request.getContextPath());
        return map;
    }
    
    protected Map makePathInfoFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(PATH_INFO_KEY, request.getPathInfo());
        return map;
    }
    
    protected Map makePathTranslatedFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(PATH_TRAN_KEY, request.getPathTranslated());
        return map;
    }
    
    protected Map makeQueryStringFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(QUERY_STRING_KEY, request.getQueryString());
        return map;
    }
    
    protected Map makeSessionIDFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(SESSION_ID_KEY, request.getRequestedSessionId());
        return map;
    }
    
    protected Map makeIsRequestedSessionIdFromCookieFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(
            SESSION_ID_FROM_COOKIE_KEY,
            new Boolean(request.isRequestedSessionIdFromCookie())
        );
        return map;
    }
    
    protected Map makeIsRequestedSessionIdFromURLFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(
            SESSION_ID_FROM_URL_KEY,
            new Boolean(request.isRequestedSessionIdFromURL())
        );
        return map;
    }
    
    protected Map makeMethodFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        map.put(HTTP_METHOD_KEY, request.getMethod());
        return map;
    }
    
    protected Map makeHeadersFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        final Enumeration headerNames = request.getHeaderNames();
        if(!headerNames.hasMoreElements()){
            return map;
        }
        final Map subMap = new HashMap();
        while(headerNames.hasMoreElements()){
            final String name = (String)headerNames.nextElement();
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(name)){
                continue;
            }
            if(secretHeaderSet.contains(name)){
                subMap.put(name, null);
            }else{
                final Enumeration headers = request.getHeaders(name);
                if(headers != null){
                    final List values = new ArrayList();
                    while(headers.hasMoreElements()){
                        values.add(headers.nextElement());
                    }
                    subMap.put(
                        name,
                        makeObjectFormat(finder, key, values)
                    );
                }
            }
        }
        map.put(HTTP_HEADERS_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
    
    protected Map makeCookiesFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        Map map
    ){
        final Cookie[] cookies = request.getCookies();
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
        map.put(COOKIES_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
}
