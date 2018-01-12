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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.Map;
import java.util.Properties;

import jp.ossc.nimbus.core.*;

/**
 * {@link ThreadContextInitializeInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ThreadContextInitializeInterceptorService
 */
public interface ThreadContextInitializeInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * スレッド単位の{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * スレッド単位の{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * コードマスタを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するための{@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を設定する。<p>
     *
     * @param name CodeMasterFinderサービスのサービス名
     * @see ThreadContextKey#CODEMASTER
     */
    public void setCodeMasterFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を取得する。<p>
     *
     * @return CodeMasterFinderサービスのサービス名
     */
    public ServiceName getCodeMasterFinderServiceName();
    
    /**
     * リクエストIDを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するための{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     * @see ThreadContextKey#REQUEST_ID
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するサービスを設定する。<p>
     *
     * @param names Contextサービスに設定するキーとサービスのサービス名のマッピング。コンテキストキー=サービス名
     */
    public void setContextValueServiceNames(ServiceNameRef[] names);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するサービスを取得する。<p>
     *
     * @return Contextサービスに設定するキーとサービスのサービス名のマッピング
     */
    public ServiceNameRef[] getContextValueServiceNames();
    
    /**
     * リクエストパラメータを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するマッピングを設定する。<p>
     *
     * @param map Contextサービスに設定するキーとリクエストパラメータ名のマッピング。コンテキストキー=リクエストパラメータ名
     */
    public void setContextValueRequestParameter(Properties map);
    
    /**
     * リクエストパラメータを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するマッピングを取得する。<p>
>
     *
     * @return Contextサービスに設定するキーとリクエストパラメータ名のマッピング
     */
    public Properties getContextValueRequestParameter();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する値を設定する。<p>
     *
     * @param mapping Contextサービスに設定するキーと値のマッピング。コンテキストキー=値
     */
    public void setContextValueMapping(Map mapping);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する値を取得する。<p>
     *
     * @return Contextサービスに設定するキーと値のマッピング
     */
    public Map getContextValueMapping();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するキーと値を設定する。<p>
     *
     * @param key キー
     * @param value 値
     */
    public void setContextValue(String key, Object value);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスに設定する値を取得する。<p>
     *
     * @param key キー
     * @return 値
     */
    public Object getContextValue(String key);
    
    /**
     * コンテキストパスを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput Contextサービスに設定する場合true
     * @see ThreadContextKey#CONTEXT_PATH
     */
    public void setOutputContextPath(boolean isOutput);
    
    /**
     * コンテキストパスを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを判定する。<p>
     *
     * @return trueの場合、Contextサービスに設定する
     */
    public boolean isOutputContextPath();
    
    /**
     * サーブレットパスを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput Contextサービスに設定する場合true
     * @see ThreadContextKey#SERVLET_PATH
     */
    public void setOutputServletPath(boolean isOutput);
    
    /**
     * サーブレットパスを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを判定する。<p>
     *
     * @return trueの場合、Contextサービスに設定する
     */
    public boolean isOutputServletPath();
    
    /**
     * セッションIDを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput Contextサービスに設定する場合true
     * @see ThreadContextKey#SESSION_ID
     */
    public void setOutputSessionID(boolean isOutput);
    
    /**
     * セッションIDを{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを判定する。<p>
     *
     * @return trueの場合、Contextサービスに設定する
     */
    public boolean isOutputSessionID();
    
    /**
     * スレッドグループ名を{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput Contextサービスに設定する場合true
     * @see ThreadContextKey#THREAD_GROUP_NAME
     */
    public void setOutputThreadGroupName(boolean isOutput);
    
    /**
     * スレッドグループ名を{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを判定する。<p>
     *
     * @return trueの場合、Contextサービスに設定する
     */
    public boolean isOutputThreadGroupName();
    
    /**
     * スレッド名を{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput Contextサービスに設定する場合true
     * @see ThreadContextKey#THREAD_NAME
     */
    public void setOutputThreadName(boolean isOutput);
    
    /**
     * スレッド名を{@link jp.ossc.nimbus.service.context.Context Context}サービスに設定するかどうかを判定する。<p>
     *
     * @return trueの場合、Contextサービスに設定する
     */
    public boolean isOutputThreadName();
    
    /**
     * セッションの情報を取得するためにセッションが存在しない場合は、セッションを生成するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isNew セッションを生成する場合は、true
     */
    public void setNewSession(boolean isNew);
    
    /**
     * セッションの情報を取得するためにセッションが存在しない場合は、セッションを生成するかどうかを判定する。<p>
     *
     * @return trueの場合、セッションを生成する
     */
    public boolean isNewSession();
    
    /**
     * 再帰的に呼び出された場合に、スレッドコンテキストの初期化を行うかどうかを設定する。<p>
     * デフォルトはtrueで初期化する。<br>
     *
     * @param isInitialize 初期化する場合は、true
     */
    public void setInitializeRecursiveCall(boolean isInitialize);
    
    /**
     * 再帰的に呼び出された場合に、スレッドコンテキストの初期化を行うかどうかを判定する。<p>
     *
     * @return trueの場合は、初期化する
     */
    public boolean isInitializeRecursiveCall();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスの{@link jp.ossc.nimbus.service.context.Context#clear() clear()}を呼び出すかどうかを設定する。<br>
     * デフォルトは、trueでclearする。<br>
     *
     * @param isClear clearする場合は、true
     */
    public void setClear(boolean isClear);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスの{@link jp.ossc.nimbus.service.context.Context#clear() clear()}を呼び出すかどうかを判定する。<br>
     *
     * @return trueの場合は、clearする
     */
    public boolean isClear();
}
