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

import java.util.Map;
import java.io.Serializable;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

import jp.ossc.nimbus.service.journal.JournalEditor;

/**
 * ジャーナルをMapオブジェクトにフォーマットするエディタサービスの基底クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class MapJournalEditorServiceBase extends ServiceBase
 implements MapJournalEditorServiceBaseMBean, Serializable{
    
    private static final long serialVersionUID = 7133997016215000363L;
    
    // JournalEditorのJavaDoc
    public Object toObject(EditorFinder finder, Object key, Object value){
        return toMap(finder, key, value);
    }
    
    /**
     * ジャーナルとして与えられたある型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public abstract Map toMap(EditorFinder finder, Object key, Object value);
    
    /**
     * エディターが不明なオブジェクトを適切なエディタで編集する。<p>
     * finderで、valueの型に対応するエディタを取得して編集する。エディタが見つからない場合は、{@link Object#toString()}で文字列に変換して返す。<br>
     *
     * @param finder EditorFinderサービス
     * @param key キー文字列
     * @param obj ジャーナルオブジェクト
     * @return ジャーナル文字列
     */
    protected Object makeObjectFormat(
        EditorFinder finder,
        Object key,
        Object obj
    ){
        Object value = null;
        if(obj != null){
            final JournalEditor editor = finder.findEditor(key, obj.getClass());
            if(editor != null){
                value = editor.toObject(finder, key, obj);
            }else{
                value = obj.toString();
            }
        }
        return value;
    }
}
