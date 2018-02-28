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
import javax.servlet.ServletResponse;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletResponseオブジェクトをMapフォーマットするエディタ。<p>
 * このエディタによって編集されたMapは、以下の構造を持つ。<br>
 * <table broder="1">
 *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
 *   <tr><td>java.lang.String</td><td>{@link #BUFFER_SIZE_KEY}</td><td>java.lang.Integer</td><td>バッファサイズ</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CONTENT_TYPE_KEY}</td><td>java.lang.String</td><td>コンテント種別</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CHARACTER_ENCODING_KEY}</td><td>java.lang.String</td><td>文字エンコーディング名</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #LOCALE_KEY}</td><td>java.util.Locale</td><td>ロケール情報</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #IS_COMMITTED_KEY}</td><td>java.lang.Boolean</td><td>レスポンスがコミットされているかどうかのフラグ</td></tr>
 * </table>
 * 但し、出力しないように設定されているものや、元のServletResponseに含まれていなかった情報、J2EEのバージョンによって取得できない情報は含まれない。<br>
 * 
 * @author M.Takata
 */
public class ServletResponseMapJournalEditorService
 extends MapJournalEditorServiceBase
 implements ServletResponseMapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -2652120436180972791L;
    
    private boolean isOutputBufferSize = true;
    private boolean isOutputCharacterEncoding = true;
    private boolean isOutputContentType = true;
    private boolean isOutputLocale = true;
    private boolean isOutputIsCommitted = true;
    
    public void setOutputBufferSize(boolean isOutput){
        isOutputBufferSize = isOutput;
    }
    
    public boolean isOutputBufferSize(){
        return isOutputBufferSize;
    }
    
    public void setOutputCharacterEncoding(boolean isOutput){
        isOutputCharacterEncoding = isOutput;
    }
    
    public boolean isOutputCharacterEncoding(){
        return isOutputCharacterEncoding;
    }
    
    public void setOutputContentType(boolean isOutput){
        isOutputContentType = isOutput;
    }
    
    public boolean isOutputContentType(){
        return isOutputContentType;
    }
    
    public void setOutputLocale(boolean isOutput){
        isOutputLocale = isOutput;
    }
    
    public boolean isOutputLocale(){
        return isOutputLocale;
    }
    
    public void setOutputIsCommitted(boolean isOutput){
        isOutputIsCommitted = isOutput;
    }
    
    public boolean isOutputIsCommitted(){
        return isOutputIsCommitted;
    }
    
    /**
     * ジャーナルとして与えられたServletResponse型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public Map toMap(EditorFinder finder, Object key, Object value){
        final ServletResponse response = (ServletResponse)value;
        final Map result = new HashMap();
        if(isOutputBufferSize()){
            makeBufferSizeFormat(finder, key, response, result);
        }
        
        if(isOutputCharacterEncoding()){
            makeCharacterEncodingFormat(finder, key, response, result);
        }
        
        if(isOutputContentType()){
            makeContentTypeFormat(finder, key, response, result);
        }
        
        if(isOutputLocale()){
            makeLocaleFormat(finder, key, response, result);
        }
        
        if(isOutputIsCommitted()){
            makeIsCommittedFormat(finder, key, response, result);
        }
        
        return result;
    }
    
    protected Map makeBufferSizeFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        Map map
    ){
        map.put(BUFFER_SIZE_KEY, new Integer(response.getBufferSize()));
        return map;
    }
    
    protected Map makeCharacterEncodingFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        Map map
    ){
        map.put(CHARACTER_ENCODING_KEY, response.getCharacterEncoding());
        return map;
    }
    
    protected Map makeContentTypeFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        Map map
    ){
        map.put(CONTENT_TYPE_KEY, response.getContentType());
        return map;
    }
    
    protected Map makeLocaleFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        Map map
    ){
        map.put(
            LOCALE_KEY,
            makeObjectFormat(finder, key, response.getLocale())
        );
        return map;
    }
    
    protected Map makeIsCommittedFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        Map map
    ){
        map.put(IS_COMMITTED_KEY, Boolean.valueOf(response.isCommitted()));
        return map;
    }
}
