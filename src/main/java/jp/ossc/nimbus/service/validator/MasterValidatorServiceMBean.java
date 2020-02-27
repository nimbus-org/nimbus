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
package jp.ossc.nimbus.service.validator;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.recset.RecordSet;
import jp.ossc.nimbus.beans.dataset.RecordList;

/**
 * {@link MasterValidatorService}サービスMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface MasterValidatorServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link #setBindData(int, String)}のバインド変数を指定する際の検証値自体を現すキー名。<br>
     */
    public static final String BIND_DATA_VALUE_KEY = "VALUE";
    
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
     * クエリ文字列を設定する。<p>
     * {@link jp.ossc.nimbus.service.connection.PersistentManager#loadQuery(java.sql.Connection, String, Object, Object) PersistentManager.loadQuery()}メソッドの第二引数queryに使用する。<br>
     *
     * @param query クエリ文字列
     */
    public void setQuery(String query);
    
    /**
     * クエリ文字列を取得する。<p>
     *
     * @return クエリ文字列
     */
    public String getQuery();
    
    /**
     * マスタの検索に使用するレコードセットを設定する。<p>
     * この設定を行った場合は、検証の都度データベースを検索する。従って、マスタの更新頻度が高い場合に、適している。<br>
     * <p>
     * 検索の際に、コネクションが必要なため、{@link #setConnectionFactoryServiceName(ServiceName)}を設定する必要がある。<br>
     * <p>
     * また、マスタの検索条件に、検証する値を含める必要があるため、{@link RecordSet#setWhere(String)}等を使って、検証する値を埋め込む条件句を設定する必要がある。<br>
     * 条件句は、埋め込み条件句となるべきで、埋め込み条件句の何番目の埋め込みパラメータに検証する値または検証する値のプロパティを埋め込むかを{@link #setBindData(int, String)}で設定する必要がある。<br>
     *
     * @param recset マスタの検索に使用するレコードセット
     */
    public void setRecordSet(RecordSet recset);
    
    /**
     * マスタの検索に使用するレコードセットを取得する。<p>
     *
     * @return マスタの検索に使用するレコードセット
     */
    public RecordSet getRecordSet();
    
    /**
     * 検証する値またはそのプロパティを、マスタの検索条件句の何番目の埋め込みパラメータとするかを設定する。<p>
     * 検証する値は、{@link #BIND_DATA_VALUE_KEY}で参照する。<br>
     * 検証する値のプロパティは、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従い、参照可能である。<br>
     * 例：VALUE.hoge<br>
     *
     * @param index 埋め込みパラメータのインデックス
     * @param valueKey 検証する値またはそのプロパティを表すキー文字列
     */
    public void setBindData(int index, String valueKey);
    
    /**
     * マスタの検索条件句の指定された埋め込みパラメータインデックスに、どのような値をバインドするかを取得する。<p>
     *
     * @param index 埋め込みパラメータのインデックス
     * @return 検証する値またはそのプロパティを表すキー文字列
     */
    public String getBindData(int index);
    
    /**
     * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を設定する。<p>
     * コードマスタがスレッドコンテキスト上に設定されていない場合に、直接CodeMasterFinderを使ってコードマスタを取得し、{@link #setCodeMasterName(String)}で設定された名前のマスタRecordSetを使って、RecordSet内を動的検索して検証する。<br>
     *
     * @param name CodeMasterFinderサービスのサービス名
     */
    public void setCodeMasterFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を取得する。<p>
     *
     * @return CodeMasterFinderサービスのサービス名
     */
    public ServiceName getCodeMasterFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}のサービス名を設定する。<p>
     * {@link #setCodeMasterThreadContextKey(String)}で設定されたキー名で、このスレッドコンテキストからコードマスタを取得し、{@link #setCodeMasterName(String)}で設定された名前のマスタRecordSetを使って、RecordSet内を動的検索して検証する。<br>
     *
     * @param name ThreadContextServiceのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}のサービス名を取得する。<p>
     *
     * @return ThreadContextServiceのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * スレッドコンテキストからコードマスタを取得する際のキー名を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey#CODEMASTER}。<br>
     *
     * @param key コードマスタキー名
     */
    public void setCodeMasterThreadContextKey(String key);
    
    /**
     * スレッドコンテキストからコードマスタを取得する際のキー名を取得する。<p>
     *
     * @return コードマスタキー名
     */
    public String getCodeMasterThreadContextKey();
    
    /**
     * コードマスタからマスタRecordSetを取得する際のマスタ名を設定する。<p>
     *
     * @param name マスタ名
     */
    public void setCodeMasterName(String name);
    
    /**
     * コードマスタからマスタRecordSetを取得する際のマスタ名を取得する。<p>
     *
     * @return マスタ名
     */
    public String getCodeMasterName();
    
    /**
     * マスタRecordSetの動的検索条件を設定する。<p>
     * 動的検索条件は、埋め込み条件となるべきで、埋め込み条件のどの埋め込みパラメータに検証する値または検証する値のプロパティを埋め込むかを{@link #setBindDataMap(String, String)}で設定する必要がある。<br>
     * 例：COLUMN1 &gt; VALUE<br>
     * 動的検索条件の書式は、{@link RecordSet#searchDynamicConditionReal(String, java.util.Map)}を参照。<br>
     *
     * @param condition 動的検索条件
     * @deprecated 別のメソッドに置き換えられました {@link #setSearchCondition(String)}
     */
    public void setRecordSetSearchCondition(String condition);
    
    /**
     * マスタRecordSetの動的検索条件を取得する。<p>
     *
     * @return 動的検索条件
     * @deprecated 別のメソッドに置き換えられました {@link #getSearchCondition()}
     */
    public String getRecordSetSearchCondition();
    
    /**
     * マスタRecordSetまたはRecordListの動的検索条件を設定する。<p>
     * 動的検索条件は、埋め込み条件となるべきで、埋め込み条件のどの埋め込みパラメータに検証する値または検証する値のプロパティを埋め込むかを{@link #setBindDataMap(String, String)}で設定する必要がある。<br>
     * 例：COLUMN1 &gt; VALUE<br>
     * 動的検索条件の書式は、{@link RecordSet#searchDynamicConditionReal(String, java.util.Map)}、{@link RecordList#realSearch(String, java.util.Map)}を参照。<br>
     *
     * @param condition 動的検索条件
     */
    public void setSearchCondition(String condition);
    
    /**
     * マスタRecordSetまたはRecordListの動的検索条件を取得する。<p>
     *
     * @return 動的検索条件
     */
    public String getSearchCondition();
    
    /**
     * 検証する値またはそのプロパティを、マスタRecordSetの動的検索条件のどの埋め込みパラメータとするかを設定する。<p>
     * 検証する値は、{@link #BIND_DATA_VALUE_KEY}で参照する。<br>
     * 検証する値のプロパティは、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従い、参照可能である。<br>
     * 例：VALUE.hoge<br>
     *
     * @param name 埋め込みパラメータ名
     * @param valueKey 検証する値またはそのプロパティを表すキー文字列
     */
    public void setBindDataMap(String name, String valueKey);
    
    /**
     * マスタRecordSetの動的検索条件の指定された埋め込みパラメータ名に、どのような値をバインドするかを取得する。<p>
     *
     * @param name 埋め込みパラメータ名
     * @return 検証する値またはそのプロパティを表すキー文字列
     */
    public String getBindDataMap(String name);
    
    /**
     * 検証する値がnullの場合に、検証結果をtrueとするかどうかを設定する。<p>
     * デフォルトは、falseで、nullを通常の検証処理で検証する。
     *
     * @param isAllow 検証する値がnullの場合に、検証結果をtrueとする場合、true
     */
    public void setNullAllow(boolean isAllow);
    
    /**
     * 検証する値がnullの場合に、検証結果をtrueとするかどうかを判定する。<p>
     *
     * @return trueの場合、検証する値がnullの場合に、検証結果をtrueとする
     */
    public boolean isNullAllow();
}
