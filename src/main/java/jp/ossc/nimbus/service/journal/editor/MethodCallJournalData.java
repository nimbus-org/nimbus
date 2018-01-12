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

/**
 * ジャーナル用メソッド呼び出し情報。<p>
 * メソッド呼び出し情報をジャーナルに取りたい時に、このオブジェクトを生成して、メソッドの呼び出し情報を格納し、{@link jp.ossc.nimbus.service.journal.Journal#addInfo(String key, Object value)}のvalueに渡す。<br>
 * MethodCallJournalDataのエディタは、{@link MethodCallJournalEditorService}が用意されている。<br>
 * 
 * @author M.Takata
 * @see MethodCallJournalEditorService
 */
public class MethodCallJournalData extends MethodJournalData{
    
    private static final long serialVersionUID = -805275074471655165L;
    
    protected final List params;
    
    public MethodCallJournalData(Class clazz, String name){
        this(null, clazz, name, null, null, null);
    }
    
    public MethodCallJournalData(Object target, Class clazz, String name){
        this(target, clazz, name, null, null, null);
    }
    
    public MethodCallJournalData(Class clazz, String name, Class[] paramTypes){
        this(null, clazz, name, paramTypes, null);
    }
    
    public MethodCallJournalData(Object target, Class clazz, String name, Class[] paramTypes){
        this(target, clazz, name, paramTypes, null);
    }
    
    public MethodCallJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        Object[] params
    ){
        this(null, clazz, name, paramTypes, params, null);
    }
    
    public MethodCallJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        Object[] params
    ){
        this(target, clazz, name, paramTypes, params, null);
    }
    
    public MethodCallJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        Object[] params,
        String message
    ){
        this(null, clazz, name, paramTypes, params, message);
    }
    
    public MethodCallJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        Object[] params,
        String message
    ){
        super(target, clazz, name, paramTypes, message);
        this.params = new ArrayList();
        if(params != null){
            for(int i = 0; i < params.length; i++){
                this.params.add(params[i]);
            }
        }
    }
    
    public void addParamater(Object param){
        params.add(param);
    }
    
    public Object[] getParameters(){
        return params.toArray();
    }
}
