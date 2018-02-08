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
import java.lang.reflect.Array;

import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * オブジェクトをフォーマットするエディタ。<p>
 * 渡されたオブジェクトの型を見て、{@link EditorFinder}に設定された、型とエディタのマッピングを使って、処理を他のエディタに委譲して、その後{@link Object#toString()}を呼んで文字列にする。また、渡されたオブジェクトの型が配列型の場合、各要素に対して同様の処理を行い、','で区切った文字列に連結する。<br>
 * EditorFinderでエディタを検索しても見つからない場合には、{@link Object#toString()}を呼んで文字列にする。<br>
 * 
 * @author M.Takata
 */
public class ObjectJournalEditorService
 extends ImmutableJournalEditorServiceBase
 implements ObjectJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 7578630921939702576L;
    
    private static final String OPEN_BRACKET = "[ ";
    private static final String CLOSE_BRACKET = " ]";
    
    private static final String ELEMENT_SEPARATOR = ", ";
    
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        if(value == null){
            return NULL_STRING;
        }
        if(value.getClass().isArray()){
            return toArrayString(finder, key, value, buf);
        }
        final JournalEditor editor = finder.findEditor(key, value.getClass());
        if(editor != null && editor != this){
            final Object obj = editor.toObject(finder, key, value);
            return buf.append(obj != null ? obj.toString() : null).toString();
        }else{
            return buf.append(value.toString()).toString();
        }
    }
    
    protected String toArrayString(
        EditorFinder finder,
        Object key,
        Object values,
        StringBuilder buf
    ){
        if(values == null){
            return NULL_STRING;
        }
        
        buf.append(OPEN_BRACKET);
        for(int i = 0, max = Array.getLength(values); i < max; i++){
            makeObjectFormat(finder, null, Array.get(values, i), buf);
            if(i != max - 1){
                buf.append(ELEMENT_SEPARATOR);
            }
        }
        buf.append(CLOSE_BRACKET);
        return buf.toString();
    }
}