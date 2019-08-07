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
package jp.ossc.nimbus.service.connection;

import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultQuerySearchManagerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultQuerySearchManagerService
 */
public interface DefaultQuerySearchManagerServiceMBean extends ServiceBaseMBean{
    
    /**
     * 検索クエリを設定する。<p>
     * 検索クエリは、{@link PersistentManager}の仕様に準じる。<br>
     *
     * @param query 検索クエリ
     */
    public void setQuery(String query);
    
    /**
     * 検索クエリを取得する。<p>
     *
     * @return 検索クエリ
     */
    public String getQuery();
    
    /**
     * 検索クエリを実行する際のjava.sql.Statementに設定するプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     */
    public void setStatementProperty(String name, Object value);
    
    /**
     * 検索クエリを実行する際のjava.sql.Statementに設定するプロパティを設定する。<p>
     *
     * @param props 検索クエリ実行時のjava.sql.Statementに設定するプロパティ
     */
    public void setStatementProperties(Map props);
    
    /**
     * 検索クエリを実行する際のjava.sql.Statementに設定するプロパティを取得する。<p>
     *
     * @return 検索クエリ実行時のjava.sql.Statementに設定するプロパティ
     */
    public Map getStatementProperties();
    
    /**
     * 検索クエリを実行する際のjava.sql.ResultSetに設定するプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     */
    public void setResultSetProperty(String name, Object value);
    
    /**
     * 検索クエリを実行する際のjava.sql.ResultSetに設定するプロパティを設定する。<p>
     *
     * @param props 検索クエリ実行時のjava.sql.ResultSetに設定するプロパティ
     */
    public void setResultSetProperties(Map props);
    
    /**
     * 検索クエリを実行する際のjava.sql.ResultSetに設定するプロパティを取得する。<p>
     *
     * @return 検索クエリ実行時のjava.sql.ResultSetに設定するプロパティ
     */
    public Map getResultSetProperties();
    
    /**
     * 検索結果をバインドするオブジェクトのクラスを設定する。<p>
     * ここで指定するクラスは、引数なしのコンストラクタを持つ必要がある。<br>
     *
     * @param clazz 検索結果をバインドするオブジェクトのクラス
     */
    public void setOutputClass(Class clazz);
    
    /**
     * 検索結果をバインドするオブジェクトのクラスを取得する。<p>
     *
     * @return 検索結果をバインドするオブジェクトのクラス
     */
    public Class getOutputClass();
    
    /**
     * 検索結果をバインドするオブジェクトを設定する。<p>
     * バインドするオブジェクトは、Cloneableを実装し、publicなclone()メソッドを持つ必要がある。<br>
     *
     * @param obj 検索結果をバインドするオブジェクト
     */
    public void setOutputObject(Cloneable obj);
    
    /**
     * 検索結果が必ず一意になるかどうかを設定する。<p>
     * デフォルトは、falseで、検索結果はリストになる。<br>
     *
     * @param isUnique 一意になる場合true
     */
    public void setUnique(boolean isUnique);
    
    /**
     * 検索結果が必ず一意になるかどうかを判定する。<p>
     *
     * @return trueの場合、一意になる
     */
    public boolean isUnique();
    
    /**
     * 検索クエリを実行する際に使用するJDBC接続を取得する{@link ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 検索クエリを実行する際に使用するJDBC接続を取得する{@link ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * 検索クエリを実行する{@link PersistentManager}サービスのサービス名を設定する。<p>
     *
     * @param name PersistentManagerサービスのサービス名
     */
    public void setPersistentManagerServiceName(ServiceName name);
    
    /**
     * 検索クエリを実行する{@link PersistentManager}サービスのサービス名を取得する。<p>
     *
     * @return PersistentManagerサービスのサービス名
     */
    public ServiceName getPersistentManagerServiceName();
    
    /**
     * 検索結果をキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を設定する。<p>
     * 設定しない場合は、検索結果はキャッシュされない。<br>
     *
     * @param name CacheMapサービスのサービス名
     */
    public void setCacheMapServiceName(ServiceName name);
    
    /**
     * 検索結果をキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を取得する。<p>
     *
     * @return CacheMapサービスのサービス名
     */
    public ServiceName getCacheMapServiceName();
    
    /**
     * 検索結果をストリームに書き出す{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     * 設定しない場合は、{@link jp.ossc.nimbus.util.converter.BeanJSONConverter BeanJSONConverter}が使用される。<br>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setStreamConverterServiceName(ServiceName name);
    
    /**
     * 検索結果をストリームに書き出す{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getStreamConverterServiceName();
    
    /**
     * キャッシュのヒット率を取得する。<p>
     *
     * @return ヒット率
     */
    public float getCacheHitRatio();
    
    /**
     * キャッシュのヒット率をリセットする。<p>
     */
    public void resetCacheHitRatio();
}
