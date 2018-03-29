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
package jp.ossc.nimbus.service.test.bug;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.test.bug.BugRecord.BugAttribute;

/**
 * {@link DatabaseBugManagerService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see DatabaseBugManagerService
 */
public interface DatabaseBugManagerServiceMBean extends ServiceBaseMBean {
    
    /**
     * ConnectionFactoryサービスのサービス名を取得する。<p>
     * 
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();

    /**
     * ConnectionFactoryサービスのサービス名を設定する。<p>
     * 
     * @param serviceName ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName serviceName);

    /**
     * ConnectionFactoryを取得する。<p>
     * 
     * @return ConnectionFactory
     */
    public ConnectionFactory getConnectionFactory();

    /**
     * ConnectionFactoryを設定する。<p>
     * 
     * @param factory ConnectionFactory
     */
    public void setConnectionFactory(ConnectionFactory factory);

    /**
     * PersistentManagerサービスのサービス名を取得する。<p>
     * 
     * @return PersistentManagerサービスのサービス名
     */
    public ServiceName getPersistentManagerServiceName();

    /**
     * PersistentManagerサービスのサービス名を設定する。<p>
     * 
     * @param serviceName PersistentManagerサービスのサービス名
     */
    public void setPersistentManagerServiceName(ServiceName serviceName);

    /**
     * PersistentManagerを取得する。<p>
     * 
     * @return PersistentManager
     */
    public PersistentManager getPersistentManager();

    /**
     * PersistentManagerを設定する。<p>
     * 
     * @param manager PersistentManager
     */
    public void setPersistentManager(PersistentManager manager);

    /**
     * Sequenceサービスのサービス名を取得する。<p>
     * 
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();

    /**
     * Sequenceサービスのサービス名を設定する。<p>
     * 
     * @param serviceName Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName serviceName);

    /**
     * Sequenceを取得する。<p>
     * 
     * @return Sequence
     */
    public Sequence getSequence();

    /**
     * Sequenceを設定する。<p>
     * 
     * @param sequence Sequence
     */
    public void setSequence(Sequence sequence);

    /**
     * Databaseに不具合を登録する際のSQLを取得する。<p>
     * 
     * @return 不具合を登録する際のSQL
     */
    public String getAddSql();

    /**
     * Databaseに不具合を登録する際のSQLを設定する。<p>
     * 
     * @param sql 不具合を登録する際のSQL
     */
    public void setAddSql(String sql);

    /**
     * Databaseに不具合を更新する際のSQLを取得する。<p>
     * 
     * @return 不具合を更新する際のSQL
     */
    public String getUpdateSql();

    /**
     * Databaseに不具合を更新する際のSQLを設定する。<p>
     * 
     * @param sql 不具合を更新する際のSQL
     */
    public void setUpdateSql(String sql);

    /**
     * Databaseから不具合を削除する際のSQLを取得する。<p>
     * 
     * @return 不具合を削除する際のSQL
     */
    public String getDeleteSql();

    /**
     * Databaseから不具合を削除する際のSQLを設定する。<p>
     * 
     * @param sql 不具合を削除する際のSQL
     */
    public void setDeleteSql(String sql);

    /**
     * Databaseから不具合の一覧を取得する際のSQLを取得する。<p>
     * 
     * @return 不具合の一覧を取得する際のSQL
     */
    public String getListSql();
    
    /**
     * Databaseから不具合の一覧を取得する際のSQLを設定する。<p>
     * 
     * @param sql 不具合の一覧を取得する際のSQL
     */
    public void setListSql(String sql);

    /**
     * Databaseから不具合一件を取得する際のSQLを取得する。<p>
     * 
     * @return 不具合一件を取得する際のSQL
     */
    public String getGetSql();

    /**
     * Databaseから不具合一件を取得する際のSQLを設定する。<p>
     * 
     * @param sql 不具合一件を取得する際のSQL
     */
    public void setGetSql(String sql);
    
    /**
     * 取得した不具合情報を格納するための{@link BugRecord}のテンプレートを取得する。<p>
     * 
     * @return BugRecordのテンプレート
     */
    public BugRecord getTemplateRecord();
    
    /**
     * 取得した不具合情報を格納するための{@link BugRecord}のテンプレートを設定する。<p>
     * テンプレートには必要な{@link BugAttribute}が追加されている状態で設定する必要がある。<p>
     * 
     * @param record BugRecordのテンプレート
     */
    public void setTemplateRecord(BugRecord record);
}
