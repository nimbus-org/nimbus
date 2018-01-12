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

import jp.ossc.nimbus.core.*;

/**
 * {@link ExceptionConsumeInterceptorService}のMBeanインタフェース<p>
 * 
 * @author N.Saisho
 * @see ExceptionConsumeInterceptorService
 */
public interface ExceptionConsumeInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * キャッチする例外クラスを設定する。<p>
     *
     * @param classnames 「キャッチする例外クラス」を設定する。
     */
    public void setExceptionClassNames(String[] classnames);
    
    /**
     * キャッチする例外クラスを取得する。<p>
     *
     * @return キャッチする例外クラス名配列
     */
    public String[] getExceptionClassNames();
    
    /**
     * 戻り値があるメソッド呼び出しの例外を握りつぶした時に返す戻り値を設定する。<p>
     *
     * @param val 戻り値
     */
    public void setReturnValue(Object val);
    
    /**
     * 戻り値があるメソッド呼び出しの例外を握りつぶした時に返す戻り値を取得する。<p>
     *
     * @return 戻り値
     */
    public Object getReturnValue();
    
    /**
     * logメッセージを生成する{@link jp.ossc.nimbus.service.log Logger}サービスのサービス名を設定する。<p>
     *
     * @param name Logサービスのサービス名
     */
    public void setLoggerServiceName(ServiceName name);
    
    /**
     * logメッセージを生成する{@link jp.ossc.nimbus.service.log Logger}サービスのサービス名を取得する。<p>
     *
     * @return Logサービスのサービス名
     */
    public ServiceName getLoggerServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.log Logger}サービスで出力するログメッセージのコードを設定する。<p>
     *
     * @param key ログメッセージのコード
     */
    public void setLoggerMessageCode(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.log Logger}サービスで出力するログメッセージのコードを取得する。<p>
     *
     * @return key ログメッセージのコード
     */
    public String getLoggerMessageCode();
    
    /**
     * {@link jp.ossc.nimbus.service.log Logger}サービスで出力するログメッセージへの埋め込み文字を設定する。<p>
     *
     * @param args ログメッセージへの埋め込み文字列配列
     */
    public void setLoggerMessageArgs(String[] args);
    
    /**
     * {@link jp.ossc.nimbus.service.log Logger}サービスで出力するログメッセージへの埋め込み文字を取得する。<p>
     *
     * @return ログメッセージへの埋め込み文字列配列
     */
    public String[] getLoggerMessageArgs();
    
    /**
     * {@link jp.ossc.nimbus.service.log Logger}サービスで出力するログメッセージのロケールを設定する。<p>
     *
     * @param locale ログメッセージのロケール
     */
    public void setLoggerMessageLocale(java.util.Locale locale);
    
    /**
     * {@link jp.ossc.nimbus.service.log Logger}サービスで出力するログメッセージのロケールを取得する。<p>
     *
     * @return ログメッセージのロケール
     */
    public java.util.Locale getLoggerMessageLocale();
    
    /**
     * 握りつぶした例外をログに出力するかどうかを設定する。<p>
     *
     * @param isLogging 例外をログに出力する場合は、true
     */
    public void setLoggingException(boolean isLogging);
    
    /**
     * 握りつぶした例外をログに出力するかどうかを判定する。<p>
     *
     * @return trueの場合、例外をログに出力する
     */
    public boolean isLoggingException();
}
