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
 * ジャーナル用メソッド例外情報。<p>
 * メソッド例外情報をジャーナルに取りたい時に、このオブジェクトを生成して、メソッドでthrowされた例外情報を格納し、{@link jp.ossc.nimbus.service.journal.Journal#addInfo(String key, Object value)}のvalueに渡す。<br>
 * MethodThrowJournalDataのエディタは、{@link MethodThrowJournalEditorService}が用意されている。<br>
 * 
 * @author M.Takata
 * @see MethodThrowJournalEditorService
 */
public class MethodThrowJournalData extends MethodReturnJournalData{
    
    private static final long serialVersionUID = 7525572675572566235L;
    
    public MethodThrowJournalData(Class clazz, String name, Throwable throwable){
        this(null, clazz, name, null, throwable);
    }
    
    public MethodThrowJournalData(Object target, Class clazz, String name, Throwable throwable){
        this(target, clazz, name, null, throwable);
    }
    
    public MethodThrowJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        Throwable throwable
    ){
        this(null, clazz, name, paramTypes, throwable, null);
    }
    
    public MethodThrowJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        Throwable throwable
    ){
        this(target, clazz, name, paramTypes, throwable, null);
    }
    
    public MethodThrowJournalData(
        Class clazz,
        String name,
        Class[] paramTypes,
        Throwable throwable,
        String message
    ){
        this(null, clazz, name, paramTypes, throwable, message);
    }
    
    public MethodThrowJournalData(
        Object target,
        Class clazz,
        String name,
        Class[] paramTypes,
        Throwable throwable,
        String message
    ){
        super(target, clazz, name, paramTypes, throwable, message);
    }
    
    public Throwable getThrowable(){
        return (Throwable)getReturnValue();
    }
}
