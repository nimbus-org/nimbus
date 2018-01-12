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
 * ジャーナル用メソッド情報。<p>
 * メソッド情報をジャーナルに取りたい時に、このオブジェクトを生成して、メソッド情報を格納し、{@link jp.ossc.nimbus.service.journal.Journal#addInfo(String key, Object value)}のvalueに渡す。<br>
 * MethodJournalDataのエディタは、{@link MethodJournalEditorService}が用意されている。<br>
 * 
 * @author M.Takata
 * @see MethodJournalEditorService
 */
public class MethodJournalData implements java.io.Serializable{
    
    private static final long serialVersionUID = 7842418168453622274L;
    
    protected Object target;
    
    protected Class clazz;
    
    protected String name;
    
    protected final List paramTypes;
    
    protected String message;
    
    public MethodJournalData(Class clazz, String name){
        this(null, clazz, name, (Class[])null);
    }
    
    public MethodJournalData(Object target, Class clazz, String name){
        this(target, clazz, name, (Class[])null);
    }
    
    public MethodJournalData(Class clazz, String name, Class[] paramTypes){
        this(null, clazz, name, paramTypes, null);
    }
    
    public MethodJournalData(Object target, Class clazz, String name, Class[] paramTypes){
        this(target, clazz, name, paramTypes, null);
    }
    
    public MethodJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        String message
    ){
        this(null, clazz, name, paramTypes, message);
    }
    
    public MethodJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        String message
    ){
        this.target = target;
        this.clazz = clazz;
        this.name = name;
        this.paramTypes = new ArrayList();
        if(paramTypes != null){
            for(int i = 0; i < paramTypes.length; i++){
                this.paramTypes.add(paramTypes[i]);
            }
        }
        this.message = message;
    }
    
    public void setMessage(String message){
        this.message = message;
    }
    
    public void addParamaterType(Class type){
        paramTypes.add(type);
    }
    
    public Object getTarget(){
        return target;
    }
    
    public Class getOwnerClass(){
        return clazz;
    }
    
    public String getName(){
        return name;
    }
    
    public Class[] getParameterTypes(){
        return (Class[])paramTypes.toArray(new Class[paramTypes.size()]);
    }
    
    public String getMessage(){
        return message;
    }
}
