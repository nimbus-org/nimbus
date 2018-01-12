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

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DatabaseSharedContextKeyDistributorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DatabaseSharedContextKeyDistributorService
 */
public interface DatabaseSharedContextKeyDistributorServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を設定する。<p>
     * 
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を取得する。<p>
     * 
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.connection.PersistentManager PersistentManager}サービスのサービス名を設定する。<p>
     * 
     * @param name PersistentManagerサービスのサービス名
     */
    public void setPersistentManagerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.connection.PersistentManager PersistentManager}サービスのサービス名を取得する。<p>
     * 
     * @return PersistentManagerサービスのサービス名
     */
    public ServiceName getPersistentManagerServiceName();
    
    /**
     * キーを検索する検索クエリを設定する。<p>
     *
     * @param query クエリ
     */
    public void setKeySelectQuery(String query);
    
    /**
     * キーを検索する検索クエリを取得する。<p>
     *
     * @return クエリ
     */
    public String getKeySelectQuery();
    
    /**
     * データベースから検索したキーを読み込むレコードを設定する。<p>
     * PersistentManagerにレコードにロードさせた後、特定のプロパティを取りだしてキーとする場合は、{@link #setKeyPropertyName(String)}と組み合わせて設定する。<br>
     *
     * @param record 読み込みレコード
     */
    public void setDatabaseRecord(Record record);
    
    /**
     * データベースから検索したキーを読み込むレコードを取得する。<p>
     *
     * @return 読み込みレコード
     */
    public Record getDatabaseRecord();
    
    /**
     * データベースから検索したキーを読み込むBeanのクラスを設定する。<p>
     * PersistentManagerにこのBeanを直接ロードさせる場合は、この属性のみ設定する。<br>
     * PersistentManagerにレコードにロードさせた後、特定のプロパティをレコードから取得して、このBeanに設定したい場合は、{@link #setKeyPropertyMapping(String, String)}と組み合わせて設定する。<br>
     * 
     * @param clazz 読み込みBeanのクラス
     */
    public void setKeyClass(Class clazz);
    
    /**
     * データベースから検索したキーを読み込むBeanのクラスを取得する。<p>
     *
     * @return 読み込みBeanのクラス
     */
    public Class getKeyClass();
    
    /**
     * レコードから取得するキーのプロパティ名を設定する。<p>
     *
     * @param name プロパティ名
     * @see #setDatabaseRecord(Record)
     */
    public void setKeyPropertyName(String name);
    
    /**
     * レコードから取得するキーのプロパティ名を取得する。<p>
     *
     * @return プロパティ名
     */
    public String getKeyPropertyName();
    
    
    /**
     * レコードから取得してBeanに設定するプロパティ名のマッピングを設定する。<p>
     *
     * @param getProperty レコードから取得するプロパティ名
     * @param setProperty Beanに設定するプロパティ名
     * @see #setKeyClass(Class)
     */
    public void setKeyPropertyMapping(String getProperty, String setProperty);
}