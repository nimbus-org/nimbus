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
 * JouralServletResponseWrapperオブジェクトをMapフォーマットするエディタ。<p>
 * このエディタによって編集されたMapは、{@link ServletResponseMapJournalEditorService}の持つMap構造に加えて、以下の構造を持つ。<br>
 * <table broder="1">
 *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CONTENT_LENGTH_KEY}</td><td>java.lang.Integer</td><td>コンテント長</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #CONTENT_KEY}</td><td>java.lang.String</td><td>コンテント</td></tr>
 * </table>
 * 但し、出力しないように設定されているものや、元のJournalServletResponseWrapperに含まれていなかった情報、J2EEのバージョンによって取得できない情報は含まれない。<br>
 * 
 * @author M.Takata
 */
public class ServletResponseWrapperMapJournalEditorService
 extends ServletResponseMapJournalEditorService
 implements ServletResponseWrapperMapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 1611548901441049696L;
    
    private boolean isOutputContentLength = true;
    private boolean isOutputContent = true;
    
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
    
    /**
     * ジャーナルとして与えられたJournalServletResponseWrapper型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public Map toMap(EditorFinder finder, Object key, Object value){
        final ServletResponse response = (ServletResponse)value;
        final Map result = super.toMap(finder, key, response);
        
        final JournalServletResponseWrapper responsew
             = (JournalServletResponseWrapper)response;
        
        if(isOutputContentLength()){
            makeContentLengthFormat(finder, key, responsew, result);
        }
        
        if(isOutputContent()){
            makeContentFormat(finder, key, responsew, result);
        }
        
        return result;
    }
    
    protected Map makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        JournalServletResponseWrapper response,
        Map map
    ){
        map.put(CONTENT_LENGTH_KEY, new Integer(response.getContentLength()));
        return map;
    }
    
    protected Map makeContentFormat(
        EditorFinder finder,
        Object key,
        JournalServletResponseWrapper response,
        Map map
    ){
        map.put(CONTENT_KEY, response.getContent());
        return map;
    }
}
