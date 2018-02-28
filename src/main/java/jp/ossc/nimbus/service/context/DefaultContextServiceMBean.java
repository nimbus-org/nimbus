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
package jp.ossc.nimbus.service.context;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultContextService}のMBeanインタフェース。<p>
 * 
 * @author H.Nakano
 * @see DefaultContextService
 */
public interface DefaultContextServiceMBean extends ServiceBaseMBean, Context{
    
    /**
     * {@link ContextStore}サービスのサービス名を設定する。<p>
     *
     * @param name ContextStoreサービスのサービス名
     */
    public void setContextStoreServiceName(ServiceName name);
    
    /**
     * {@link ContextStore}サービスのサービス名を取得する。<p>
     *
     * @return ContextStoreサービスのサービス名
     */
    public ServiceName getContextStoreServiceName();
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使って読み込み処理を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isLoad 読み込み処理を行う場合、true
     */
    public void setLoadOnStart(boolean isLoad);
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使って読み込み処理を行うかどうかを判定する。<p>
     *
     * @return trueの場合、読み込み処理を行う
     */
    public boolean isLoadOnStart();
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使ってキーの読み込み処理を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isLoad 読み込み処理を行う場合、true
     */
    public void setLoadKeyOnStart(boolean isLoad);
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使ってキーの読み込み処理を行うかどうかを判定する。<p>
     *
     * @return trueの場合、読み込み処理を行う
     */
    public boolean isLoadKeyOnStart();
    
    /**
     * サービスの停止時に、{@link ContextStore}サービスを使って書き込み処理を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isSave 書き込み処理を行う場合、true
     */
    public void setSaveOnStop(boolean isSave);
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使って書き込み処理を行うかどうかを判定する。<p>
     *
     * @return trueの場合、書き込み処理を行う
     */
    public boolean isSaveOnStop();
    
    /**
     * コンテキストの書き込み処理の前にストアをクリアするかどうかを設定する。<p>
     * デフォルトは、trueでクリアする。<br>
     *
     * @param isClear クリアする場合、true
     */
    public void setClearBeforeSave(boolean isClear);
    
    /**
     * コンテキストの書き込み処理の前にストアをクリアするかどうかを判定する。<p>
     *
     * @return trueの場合、クリアする
     */
    public boolean isClearBeforeSave();
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を取得する。<p>
     *
     * @param key キー
     * @return キーに関連付けられたコンテキスト情報。該当するコンテキスト情報がない場合は、null
     */
    public Object get(String key);
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を削除する。<p>
     *
     * @param key キー
     * @return 削除されたコンテキスト情報。削除するコンテキスト情報がない場合は、null
     */
    public Object remove(String key);
    
    /**
     * 「名前(keyのtoString()) : 値(valueのtoString()) 改行」という形式でリスト出力する。<p>
     *
     * @return リスト文字列
     */
    public String list();
    
    /**
     * {@link ContextStore}サービスを使って読み込み処理を行う。<p>
     *
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void load() throws Exception;
    
    /**
     * {@link ContextStore}サービスを使ってキーの読み込み処理を行う。<p>
     *
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void loadKey() throws Exception;
    
    /**
     * 指定されたキーに該当する値を{@link ContextStore}サービスを使って読み込み処理を行う。<p>
     *
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void load(Object key) throws Exception;
    
    /**
     * {@link ContextStore}サービスを使って書き込み処理を行う。<p>
     *
     * @exception Exception 書き込み処理に失敗した場合
     */
    public void save() throws Exception;
    
    /**
     * 指定されたキーに該当する値を{@link ContextStore}サービスを使って書き込み処理を行う。<p>
     *
     * @exception Exception 書き込み処理に失敗した場合
     */
    public void save(Object key) throws Exception;
    
    /**
     * 指定されたコンテキスト情報を指定されたキー情報に関連付けて設定する。<p>
     * 
     * @param key キー
     * @param value コンテキスト情報
     * @return 指定されたキーに関連付けられていたコンテキスト情報。存在しない場合は、null
     */
    public Object put(String key, String value);
    
}
