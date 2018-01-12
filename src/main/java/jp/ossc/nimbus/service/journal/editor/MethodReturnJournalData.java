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

/**
 * ジャーナル用メソッド戻り情報。<p>
 * メソッド戻り情報をジャーナルに取りたい時に、このオブジェクトを生成して、メソッドの戻り情報を格納し、{@link jp.ossc.nimbus.service.journal.Journal#addInfo(String key, Object value)}のvalueに渡す。<br>
 * MethodReturnJournalDataのエディタは、{@link MethodReturnJournalEditorService}が用意されている。<br>
 * 
 * @author M.Takata
 * @see MethodReturnJournalEditorService
 */
public class MethodReturnJournalData extends MethodJournalData{
    
    private static final long serialVersionUID = 1795031475688376476L;
    
    protected Object returnValue;
    
    public MethodReturnJournalData(Class clazz, String name){
        this(null, clazz, name, null, null, null);
    }
    
    public MethodReturnJournalData(Object target, Class clazz, String name){
        this(target, clazz, name, null, null, null);
    }
    
    public MethodReturnJournalData(Class clazz, String name, Class[] paramTypes){
        this(null, clazz, name, paramTypes, null);
    }
    
    public MethodReturnJournalData(Object target, Class clazz, String name, Class[] paramTypes){
        this(target, clazz, name, paramTypes, null);
    }
    
    public MethodReturnJournalData(Class clazz, String name, Object returnValue){
        this(null, clazz, name, null, returnValue);
    }
    
    public MethodReturnJournalData(Object target, Class clazz, String name, Object returnValue){
        this(target, clazz, name, null, returnValue);
    }
    
    public MethodReturnJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        Object returnValue
    ){
        this(null, clazz, name, paramTypes, returnValue, null);
    }
    
    public MethodReturnJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        Object returnValue
    ){
        this(target, clazz, name, paramTypes, returnValue, null);
    }
    
    public MethodReturnJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        Object returnValue,
        String message
    ){
        this(null, clazz, name, paramTypes, returnValue, message);
    }
    
    public MethodReturnJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        Object returnValue,
        String message
    ){
        super(target, clazz, name, paramTypes, message);
        this.returnValue = returnValue;
    }
    
    public Object getReturnValue(){
        return returnValue;
    }
}
