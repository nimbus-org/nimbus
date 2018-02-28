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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DatabaseAuthenticateStoreService}のMBeanインタフェース。
 * <p>
 *
 * @author M.Takata
 * @see DatabaseAuthenticateStoreService
 */
public interface DatabaseAuthenticateStoreServiceMBean extends ServiceBaseMBean {
    
    /**
     * PersistentManagerへの入力オブジェクトであるマップに格納される認証情報または認証キーのキー名。
     * <p>
     */
    public static final String INPUT_KEY_AUTH = "Auth";
    
    /**
     * PersistentManagerへの入力オブジェクトであるマップに格納されるHTTPセッションIDのキー名。
     * <p>
     */
    public static final String INPUT_KEY_HTTP_SESSION_ID = "SessionId";
    
    /**
     * PersistentManagerへの入力オブジェクトであるマップに格納されるタイムスタンプのキー名。
     * <p>
     */
    public static final String INPUT_KEY_TIMESTAMP = "Timestamp";
    
    /**
     * PersistentManagerへの入力オブジェクトであるマップに格納されるホスト名のキー名。
     * <p>
     */
    public static final String INPUT_KEY_HOST = "Host";
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory
     * ConnectionFactory}サービスのサービス名を設定する。
     * <p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory
     * ConnectionFactory}サービスのサービス名を取得する。
     * <p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.connection.PersistentManager
     * PersistentManager}サービスのサービス名を設定する。
     * <p>
     *
     * @param name PersistentManagerサービスのサービス名
     */
    public void setPersistentManagerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.connection.PersistentManager
     * PersistentManager}サービスのサービス名を取得する。
     * <p>
     *
     * @return PersistentManagerサービスのサービス名
     */
    public ServiceName getPersistentManagerServiceName();
    
    /**
     * データベースから復元する認証情報のクラスオブジェクトを設定する。
     * <p>
     *
     * @param clazz 認証情報のクラスオブジェクト
     */
    public void setAuthenticatedInfoClass(Class clazz);
    
    /**
     * データベースから復元する認証情報のクラスオブジェクトを取得する。
     * <p>
     *
     * @return 認証情報のクラスオブジェクト
     */
    public Class getAuthenticatedInfoClass();
    
    /**
     * データベースから復元する認証情報のオブジェクトを設定する。
     * <p>
     * このオブジェクトは、Cloneableでpublicなclone()メソッドを実装する必要がある。<br>
     *
     * @param template 認証情報のオブジェクト
     */
    public void setAuthenticatedInfoTemplate(Object template);
    
    /**
     * データベースから復元する認証情報のオブジェクトを取得する。
     * <p>
     *
     * @return 認証情報のオブジェクト
     */
    public Object getAuthenticatedInfoTemplate();
    
    /**
     * PersistentManagerへの入力オブジェクトであるマップに格納するホスト名を設定する。
     * <p>
     * 設定しない場合、自動的にローカルホストのホスト名が使用される。<br>
     *
     * @param name ホスト名
     */
    public void setHostName(String name);
    
    /**
     * PersistentManagerへの入力オブジェクトであるマップに格納するホスト名を取得する。
     * <p>
     *
     * @return ホスト名
     */
    public String getHostName();
    
    /**
     * 認証情報を生成して良いかを検索するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setSelectQueryOnCreateUser(String query);
    
    /**
     * 認証情報を生成して良いかを検索するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getSelectQueryOnCreateUser();
    
    /**
     * 認証情報を検索するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setSelectQueryOnFindUser(String query);
    
    /**
     * 認証情報を検索するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getSelectQueryOnFindUser();
    
    /**
     * 認証情報を挿入するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setInsertQuery(String query);
    
    /**
     * 認証情報を挿入するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getInsertQuery();
    
    /**
     * 認証情報をストアする際に、既に該当する認証情報が存在する場合に、認証情報を更新するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setUpdateQueryOnCreate(String query);
    
    /**
     * 認証情報をストアする際に、既に該当する認証情報が存在する場合に、認証情報を更新するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getUpdateQueryOnCreate();
    
    /**
     * 認証情報をストアする際に、既に該当する認証情報が存在する場合に、認証情報を削除するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setDeleteQueryOnCreate(String query);
    
    /**
     * 認証情報をストアする際に、既に該当する認証情報が存在する場合に、認証情報を削除するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getDeleteQueryOnCreate();
    
    /**
     * 認証情報を復元する際に、認証情報を更新するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setUpdateQueryOnActivate(String query);
    
    /**
     * 認証情報を復元する際に、認証情報を更新するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getUpdateQueryOnActivate();
    
    /**
     * 認証情報を非活性化する際に、認証情報を更新するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setUpdateQueryOnDeactivate(String query);
    
    /**
     * 認証情報を非活性化する際に、認証情報を更新するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getUpdateQueryOnDeactivate();
    
    /**
     * 認証情報を削除するクエリを設定する。
     * <p>
     *
     * @param query クエリ
     */
    public void setDeleteQuery(String query);
    
    /**
     * 認証情報を削除するクエリを取得する。
     * <p>
     *
     * @return クエリ
     */
    public String getDeleteQuery();
    
    /**
     * 認証情報をストアする際に、既に該当する認証情報が存在する場合に、認証情報を削除するかを設定する。
     * <p>
     *
     * @param isDelete 削除要否
     */
    public void setDeleteFindUser(boolean isDelete);
    
    /**
     * 認証情報をストアする際に、既に該当する認証情報が存在する場合に、認証情報を削除するかを取得する。
     * <p>
     *
     * @return 削除要否
     */
    public boolean isDeleteFindUser();
    
}