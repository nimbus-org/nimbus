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

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link ExceptionWrapInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ExceptionWrapInterceptorService
 */
public interface ExceptionWrapInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * キャッチする例外クラスとラップしてthrowする例外クラスのマッピングを設定する。<p>
     *
     * @param mapping 「キャッチする例外クラス=ラップする例外クラス」のマッピング
     */
    public void setWrapExceptionMapping(Properties mapping);
    
    /**
     * キャッチする例外クラスとラップしてthrowする例外クラスのマッピングを取得する。<p>
     *
     * @return 「キャッチする例外クラス=ラップする例外クラス」のマッピング
     */
    public Properties getWrapExceptionMapping();
    
    /**
     * throwする例外のメッセージを設定する。<p>
     *
     * @param msg throwする例外のメッセージ
     */
    public void setMessage(String msg);
    
    /**
     * throwする例外のメッセージを取得する。<p>
     *
     * @return throwする例外のメッセージ
     */
    public String getMessage();
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスのサービス名を設定する。<p>
     *
     * @param name MessageRecordFactoryサービスのサービス名
     */
    public void setMessageRecordFactoryServiceName(ServiceName name);
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスのサービス名を取得する。<p>
     *
     * @return MessageRecordFactoryサービスのサービス名
     */
    public ServiceName getMessageRecordFactoryServiceName();
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスからメッセージを取得する時のキーを設定する。<p>
     *
     * @param key MessageRecordFactoryサービスからメッセージを取得する時のキー
     */
    public void setMessageKey(String key);
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスからメッセージを取得する時のキーを取得する。<p>
     *
     * @return MessageRecordFactoryサービスからメッセージを取得する時のキー
     */
    public String getMessageKey();
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスからメッセージを取得する時のメッセージへの埋め込み文字列を設定する。<p>
     *
     * @param args MessageRecordFactoryサービスからメッセージを取得する時のメッセージへの埋め込み文字列
     */
    public void setMessageArgs(String[] args);
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスからメッセージを取得する時のメッセージへの埋め込み文字列を取得する。<p>
     *
     * @return MessageRecordFactoryサービスからメッセージを取得する時のメッセージへの埋め込み文字列
     */
    public String[] getMessageArgs();
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスからメッセージを取得する時のメッセージのロケールを設定する。<p>
     *
     * @param locale MessageRecordFactoryサービスからメッセージを取得する時のメッセージのロケール
     */
    public void setMessageLocale(java.util.Locale locale);
    
    /**
     * throwする例外のメッセージを生成する{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}サービスからメッセージを取得する時のメッセージのロケールを取得する。<p>
     *
     * @return MessageRecordFactoryサービスからメッセージを取得する時のメッセージのロケール
     */
    public java.util.Locale getMessageLocale();
}
