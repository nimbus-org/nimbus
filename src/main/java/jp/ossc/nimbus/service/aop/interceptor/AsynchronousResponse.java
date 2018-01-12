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
package jp.ossc.nimbus.service.aop.interceptor;

import jp.ossc.nimbus.service.aop.InvocationContext;

/**
 * 非同期呼び出しの戻り情報を格納するクラス。<p>
 *
 * @author M.Takata
 * @see MethodAsynchronousInterceptorService
 */
public class AsynchronousResponse implements java.io.Serializable{
    
    private static final long serialVersionUID = -6354918070435188105L;
    
    private InvocationContext context;
    private Object returnObject;
    private boolean throwException;
    
    /**
     * 指定された戻り値を持つインスタンスを生成する。<p>
     *
     * @param context 呼び出しコンテキスト情報
     * @param ret 戻り値
     */
    protected AsynchronousResponse(InvocationContext context, Object ret){
        this(context, ret, false);
    }
    
    /**
     * 指定された戻り情報を持つインスタンスを生成する。<p>
     *
     * @param context 呼び出しコンテキスト情報
     * @param ret 戻り値またはthrowされた例外
     * @param throwException 例外がthrowされたかどうかのフラグ
     */
    protected AsynchronousResponse(
        InvocationContext context,
        Object ret,
        boolean throwException
    ){
        this.context = context;
        returnObject = ret;
        this.throwException = throwException;
    }
    
    /**
     * 呼び出しコンテキスト情報を取得する。<p>
     *
     * @return 呼び出しコンテキスト情報
     */
    public InvocationContext getInvocationContext(){
        return context;
    }
    
    /**
     * 戻り値を取得する。<p>
     *
     * @return 戻り値。例外がthrowされた場合はnull
     */
    public Object getReturnObject(){
        return throwException ? null : returnObject;
    }
    
    /**
     * 例外がthrowされたかどうかを判定する。<p>
     *
     * @return 例外がthrowされた場合はtrue
     */
    public boolean isThrownException(){
        return throwException;
    }
    
    /**
     * throwされた例外を取得する。<p>
     *
     * @return throwされた例外。例外がthrowされていない場合は、null
     */
    public Throwable getThrownException(){
        return throwException ? (Throwable)returnObject : null;
    }
}
