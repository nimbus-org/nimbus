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

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletRequestオブジェクトをMapフォーマットするエディタ。<p>
 * このエディタによって編集されたMapは、以下の構造を持つ。<br>
 * <table broder="1">
 *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="5">値</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th colspan="4">内容</th></tr>
 *   <tr><td rowspan="3">java.lang.String</td><td rowspan="3">{@link #ATTRIBUTES_KEY}</td><td rowspan="3">java.util.Map</td><td colspan="4">属性のマップ</td></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
 *   <tr><td>java.lang.String</td><td>属性名</td><td>java.lang.Object</td><td>属性値</td></tr>
 *   <tr><td rowspan="3">java.lang.String</td><td rowspan="3">{@link #PARAMETERS_KEY}</td><td rowspan="3">java.util.Map</td><td colspan="4">パラメータのマップ</td></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
 *   <tr><td>java.lang.String</td><td>パラメータ名</td><td>java.lang.Object</td><td>パラメータ値</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CHARACTER_ENCODING_KEY}</td><td>java.lang.String</td><td colspan="4">文字エンコーディング名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CONTENT_LENGTH_KEY}</td><td>java.lang.Integer</td><td colspan="4">コンテント長</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CONTENT_TYPE_KEY}</td><td>java.lang.String</td><td colspan="4">コンテント種別</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REMOTE_ADDRESS_KEY}</td><td>java.lang.String</td><td colspan="4">リクエスト元のクライアントのIPアドレス。又は、最後に通ったプロキシのIPアドレス。</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REMOTE_PORT_KEY}</td><td>java.lang.Integer</td><td colspan="4">リクエスト元のクライアントのポート番号。又は、最後に通ったプロキシのポート番号。</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REMOTE_HOST_KEY}</td><td>java.lang.String</td><td colspan="4">リクエスト元のクライアントの完全限定名。又は、最後に通ったプロキシの完全限定名。</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #LOCAL_ADDRESS_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストを受け取ったサーバのIPアドレス</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #LOCAL_PORT_KEY}</td><td>java.lang.Integer</td><td colspan="4">リクエストを受け取ったサーバのポート番号</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #LOCAL_NAME_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストを受け取ったサーバの完全限定名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SERVER_NAME_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストの送り先のサーバのホスト名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SERVER_PORT_KEY}</td><td>java.lang.Integer</td><td colspan="4">リクエストの送り先のサーバのポート番号</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #PROTPCOL_KEY}</td><td>java.lang.String</td><td colspan="4">プロトコル名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #SCHEME_KEY}</td><td>java.lang.String</td><td colspan="4">スキーマ名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #LOCALE_KEY}</td><td>java.util.Locale</td><td colspan="4">ロケール</td></tr>
 * </table>
 * 但し、出力しないように設定されているものや、元のServletRequestに含まれていなかった情報、J2EEのバージョンによって取得できない情報は含まれない。<br>
 * 
 * @author M.Takata
 */
public class ServletRequestMapJournalEditorService
 extends MapJournalEditorServiceBase
 implements ServletRequestMapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -2626017130679402298L;
    
    private boolean isOutputRemoteAddress = true;
    private boolean isOutputRemotePort = true;
    private boolean isOutputRemoteHost = true;
    private boolean isOutputLocalAddress = true;
    private boolean isOutputLocalPort = true;
    private boolean isOutputLocalName = true;
    private boolean isOutputServerName = true;
    private boolean isOutputServerPort = true;
    private boolean isOutputProtocol = true;
    private boolean isOutputScheme = true;
    private boolean isOutputLocale = true;
    private boolean isOutputContentType = true;
    private boolean isOutputContentLength = true;
    private boolean isOutputCharacterEncoding = true;
    private boolean isOutputAttributes = true;
    private boolean isOutputParameters = true;
    
    protected String[] secretAttributes;
    private Set secretAttributeSet;
    protected String[] secretParameters;
    private Set secretParameterSet;
    protected String[] enabledAttributes;
    private Set enabledAttributeSet;
    protected String[] enabledParameters;
    private Set enabledParameterSet;
    
    public void setOutputRemoteAddress(boolean isOutput){
        isOutputRemoteAddress = isOutput;
    }
    
    public boolean isOutputRemoteAddress(){
        return isOutputRemoteAddress;
    }
    
    public void setOutputRemotePort(boolean isOutput){
        isOutputRemotePort = isOutput;
    }
    
    public boolean isOutputRemotePort(){
        return isOutputRemotePort;
    }
    
    public void setOutputRemoteHost(boolean isOutput){
        isOutputRemoteHost = isOutput;
    }
    
    public boolean isOutputRemoteHost(){
        return isOutputRemoteHost;
    }
    
    public void setOutputLocalAddress(boolean isOutput){
        isOutputLocalAddress = isOutput;
    }
    
    public boolean isOutputLocalAddress(){
        return isOutputLocalAddress;
    }
    
    public void setOutputLocalPort(boolean isOutput){
        isOutputLocalPort = isOutput;
    }
    
    public boolean isOutputLocalPort(){
        return isOutputLocalPort;
    }
    
    public void setOutputLocalName(boolean isOutput){
        isOutputLocalName = isOutput;
    }
    
    public boolean isOutputLocalName(){
        return isOutputLocalName;
    }
    
    public void setOutputServerName(boolean isOutput){
        isOutputServerName = isOutput;
    }
    
    public boolean isOutputServerName(){
        return isOutputServerName;
    }
    
    public void setOutputServerPort(boolean isOutput){
        isOutputServerPort = isOutput;
    }
    
    public boolean isOutputServerPort(){
        return isOutputServerPort;
    }
    
    public void setOutputProtocol(boolean isOutput){
        isOutputProtocol = isOutput;
    }
    
    public boolean isOutputProtocol(){
        return isOutputProtocol;
    }
    
    public void setOutputScheme(boolean isOutput){
        isOutputScheme = isOutput;
    }
    
    public boolean isOutputScheme(){
        return isOutputScheme;
    }
    
    public void setOutputLocale(boolean isOutput){
        isOutputLocale = isOutput;
    }
    
    public boolean isOutputLocale(){
        return isOutputLocale;
    }
    
    public void setOutputContentType(boolean isOutput){
        isOutputContentType = isOutput;
    }
    
    public boolean isOutputContentType(){
        return isOutputContentType;
    }
    
    public void setOutputContentLength(boolean isOutput){
        isOutputContentLength = isOutput;
    }
    
    public boolean isOutputContentLength(){
        return isOutputContentLength;
    }
    
    public void setOutputCharacterEncoding(boolean isOutput){
        isOutputCharacterEncoding = isOutput;
    }
    
    public boolean isOutputCharacterEncoding(){
        return isOutputCharacterEncoding;
    }
    
    public void setOutputAttributes(boolean isOutput){
        isOutputAttributes = isOutput;
    }
    
    public boolean isOutputAttributes(){
        return isOutputAttributes;
    }
    
    public void setOutputParameters(boolean isOutput){
        isOutputParameters = isOutput;
    }
    
    public boolean isOutputParameters(){
        return isOutputParameters;
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
    
    public void setSecretParameters(String[] names){
        secretParameters = names;
    }
    
    public String[] getSecretParameters(){
        return secretParameters;
    }
    
    public void setEnabledParameters(String[] names){
        enabledParameters = names;
    }
    
    public String[] getEnabledParameters(){
        return enabledParameters;
    }
    
    public void createService(){
        secretAttributeSet = new HashSet();
        secretParameterSet = new HashSet();
        enabledAttributeSet = new HashSet();
        enabledParameterSet = new HashSet();
    }
    
    public void startService(){
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
        if(secretParameters != null){
            for(int i = 0; i < secretParameters.length; i++){
                secretParameterSet.add(secretParameters[i]);
            }
        }
        if(enabledParameters != null){
            for(int i = 0; i < enabledParameters.length; i++){
                enabledParameterSet.add(enabledParameters[i]);
            }
        }
    }
    
    public void stopService(){
        secretAttributeSet.clear();
        enabledAttributeSet.clear();
        secretParameterSet.clear();
        enabledParameterSet.clear();
    }
    
    public void destroyService(){
        secretAttributeSet = null;
        enabledAttributeSet = null;
        secretParameterSet = null;
        enabledParameterSet = null;
    }
    
    /**
     * ジャーナルとして与えられたServletRequest型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public Map toMap(EditorFinder finder, Object key, Object value){
        final ServletRequest request = (ServletRequest)value;
        final Map result = new HashMap();
        if(isOutputRemoteAddress()){
            makeRemoteAddressFormat(finder, key, request, result);
        }
        
        if(isOutputRemotePort()){
            makeRemotePortFormat(finder, key, request, result);
        }
        
        if(isOutputRemoteHost()){
            makeRemoteHostFormat(finder, key, request, result);
        }
        
        if(isOutputLocalAddress()){
            makeLocalAddressFormat(finder, key, request, result);
        }
        
        if(isOutputLocalPort()){
            makeLocalPortFormat(finder, key, request, result);
        }
        
        if(isOutputLocalName()){
            makeLocalNameFormat(finder, key, request, result);
        }
        
        if(isOutputServerName()){
            makeServerNameFormat(finder, key, request, result);
        }
        
        if(isOutputServerPort()){
            makeServerPortFormat(finder, key, request, result);
        }
        
        if(isOutputProtocol()){
            makeProtocolFormat(finder, key, request, result);
        }
        
        if(isOutputScheme()){
            makeSchemeFormat(finder, key, request, result);
        }
        
        if(isOutputLocale()){
            makeLocaleFormat(finder, key, request, result);
        }
        
        if(isOutputContentType()){
            makeContentTypeFormat(finder, key, request, result);
        }
        
        if(isOutputContentLength()){
            makeContentLengthFormat(finder, key, request, result);
        }
        
        if(isOutputCharacterEncoding()){
            makeCharacterEncodingFormat(finder, key, request, result);
        }
        
        if(isOutputAttributes()){
            makeAttributesFormat(finder, key, request, result);
        }
        
        if(isOutputParameters()){
            makeParametersFormat(finder, key, request, result);
        }
        return result;
    }
    
    protected Map makeAttributesFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        final Enumeration attrNames = request.getAttributeNames();
        if(!attrNames.hasMoreElements()){
            return map;
        }
        final Map subMap = new HashMap();
        while(attrNames.hasMoreElements()){
            final String name = (String)attrNames.nextElement();
            if(!enabledAttributeSet.isEmpty()
                 && !enabledAttributeSet.contains(name)){
                continue;
            }
            if(secretAttributeSet.contains(name)){
                subMap.put(name, null);
            }else{
                subMap.put(
                    name,
                    makeObjectFormat(
                        finder,
                        null,
                        request.getAttribute(name)
                    )
                );
            }
        }
        map.put(ATTRIBUTES_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
    
    protected Map makeParametersFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        final Enumeration paramNames = request.getParameterNames();
        if(!paramNames.hasMoreElements()){
            return map;
        }
        final Map subMap = new HashMap();
        while(paramNames.hasMoreElements()){
            final String name = (String)paramNames.nextElement();
            if(!enabledParameterSet.isEmpty()
                 && !enabledParameterSet.contains(name)){
                continue;
            }
            if(secretParameterSet.contains(name)){
                subMap.put(name, null);
            }else{
                subMap.put(
                    name,
                    makeObjectFormat(finder, key, request.getParameterValues(name))
                );
            }
        }
        map.put(PARAMETERS_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
    
    protected Map makeCharacterEncodingFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(CHARACTER_ENCODING_KEY, request.getCharacterEncoding());
        return map;
    }
    
    protected Map makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(CONTENT_LENGTH_KEY, new Integer(request.getContentLength()));
        return map;
    }
    
    protected Map makeContentTypeFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(CONTENT_TYPE_KEY, request.getContentType());
        return map;
    }
    
    protected Map makeRemoteAddressFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(REMOTE_ADDRESS_KEY, request.getRemoteAddr());
        return map;
    }
    
    protected Map makeRemotePortFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        try{
            final int port = request.getRemotePort();
            map.put(REMOTE_PORT_KEY, new Integer(port));
        }catch(NoSuchMethodError e){
        }
        return map;
    }
    
    protected Map makeRemoteHostFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(REMOTE_HOST_KEY, request.getRemoteHost());
        return map;
    }
    
    protected Map makeLocalAddressFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        try{
            map.put(LOCAL_ADDRESS_KEY, request.getLocalAddr());
        }catch(NoSuchMethodError e){
        }
        return map;
    }
    
    protected Map makeLocalPortFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        try{
            final int port = request.getLocalPort();
            map.put(LOCAL_PORT_KEY, new Integer(port));
        }catch(NoSuchMethodError e){
        }
        return map;
    }
    
    protected Map makeLocalNameFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        try{
            map.put(LOCAL_NAME_KEY, request.getLocalName());
        }catch(NoSuchMethodError e){
        }
        return map;
    }
    
    protected Map makeServerNameFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(SERVER_NAME_KEY, request.getServerName());
        return map;
    }
    
    protected Map makeServerPortFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(SERVER_PORT_KEY, new Integer(request.getServerPort()));
        return map;
    }
    
    protected Map makeProtocolFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(PROTPCOL_KEY, request.getProtocol());
        return map;
    }
    
    protected Map makeSchemeFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(SCHEME_KEY, request.getScheme());
        return map;
    }
    
    protected Map makeLocaleFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        Map map
    ){
        map.put(LOCALE_KEY, request.getLocale());
        return map;
    }
}