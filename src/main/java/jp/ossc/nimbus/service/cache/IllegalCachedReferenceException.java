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
package jp.ossc.nimbus.service.cache;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 不正キャッシュ参照例外。<p>
 *
 * @author M.Takata
 */
public class IllegalCachedReferenceException extends Exception{
    
    private static final long serialVersionUID = 7402131639192992549L;
    
    private static final String CAUSED_PREFIX = "Caused by : ";
    
    private static final String METHOD_NAME_GET_CAUSE = "getCause";
    
    private static final boolean isExistsGetCause;
    
    static{
        java.lang.reflect.Method getCause = null;
        try{
            getCause = Exception.class.getMethod(METHOD_NAME_GET_CAUSE, (Class[])null);
        }catch(NoSuchMethodException e){
            // 無視する
        }
        isExistsGetCause = (getCause != null);
    }
    
    /**
     * この例外の原因となった例外。<p>
     */
    private Throwable cause;
    
    /**
     * コンストラクタ。<p>
     */
    public IllegalCachedReferenceException(){
        super();
    }
    
    /**
     * エラーメッセージを持ったインスタンスを生成するコンストラクタ。<p>
     *
     * @param message エラーメッセージ
     */
    public IllegalCachedReferenceException(String message){
        super(message);
    }
    
    /**
     * この例外の原因となった例外を持ったインスタンスを生成するコンストラクタ。<p>
     *
     * @param cause 原因となった例外
     */
    public IllegalCachedReferenceException(Throwable cause){
        this(cause.getMessage(), cause);
    }
    
    /**
     * エラーメッセージと、この例外の原因となった例外を持ったインスタンスを生成するコンストラクタ。<p>
     *
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     */
    public IllegalCachedReferenceException(String message, Throwable cause){
        super(message);
        this.cause = cause;
    }
    
    /**
     * この例外の原因となった例外を取得する。<p>
     *
     * @return この例外の原因となった例外
     */
    public Throwable getCause(){
        return cause;
    }
    
    /**
     * この例外とそのバックトレースを指定された印刷ストリームに出力します。<p>
     *
     * @param s 出力に使用するPrintStream
     */
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if(!isExistsGetCause && cause != null){
            s.print(CAUSED_PREFIX);
            cause.printStackTrace(s);
        }
    }
    
    /**
     * この例外とそのバックトレースを指定されたプリントライターに出力します。<p>
     *
     * @param s 出力に使用するPrintWriter
     */
    public void printStackTrace(PrintWriter s) { 
        super.printStackTrace(s);
        if(!isExistsGetCause && cause != null){
            s.print(CAUSED_PREFIX);
            cause.printStackTrace(s);
        }
    }
}