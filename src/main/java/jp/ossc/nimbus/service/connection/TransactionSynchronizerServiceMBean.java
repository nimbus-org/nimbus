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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link TransactionSynchronizerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see TransactionSynchronizerService
 */
public interface TransactionSynchronizerServiceMBean extends ServiceBaseMBean{
    
    /**
     * トランザクションログテーブルの列「同期したかどうかを示すフラグ」の列名のデフォルト値。<p>
     */
    public static final String DEFAULT_COLUMN_NAME_SYNCHRONIZE = "SYNC";
    
    /**
     * 同期元の{@link ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setSourceConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 同期元の{@link ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getSourceConnectionFactoryServiceName();
    
    /**
     * 同期先の{@link ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setDestinationConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 同期先の{@link ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getDestinationConnectionFactoryServiceName();
    
    /**
     * トランザクションログテーブルのテーブル名を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.util.sql.TransactionLoggingConnection#DEFAULT_TRANSACTION_TABLE_NAME}。<br>
     *
     * @param name テーブル名
     */
    public void setTransactionTableName(String name);
    
    /**
     * トランザクションログテーブルのテーブル名を取得する。<p>
     *
     * @return テーブル名
     */
    public String getTransactionTableName();
    
    /**
     * トランザクションパラメータログテーブルのテーブル名を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.util.sql.TransactionLoggingConnection#DEFAULT_TRANSACTION_PARAM_TABLE_NAME}。<br>
     *
     * @param name テーブル名
     */
    public void setTransactionParamTableName(String name);
    
    /**
     * トランザクションパラメータログテーブルのテーブル名を取得する。<p>
     *
     * @return テーブル名
     */
    public String getTransactionParamTableName();
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory}サービスのサービス名を設定する。<p>
     * 設定した場合、同期先へのトランザクション実行と同期元のトランザクションログの削除を同一トランザクションで実行する。<br>
     *
     * @param name TransactionManagerFactoryサービスのサービス名
     */
    public void setTransactionManagerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory}サービスのサービス名を取得する。<p>
     *
     * @return TransactionManagerFactoryサービスのサービス名
     */
    public ServiceName getTransactionManagerFactoryServiceName();
    
    /**
     * トランザクションログテーブルの列「同期したかどうかを示すフラグ」の列名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_COLUMN_NAME_SYNCHRONIZE}。<br>
     *
     * @param name 列名
     */
    public void setSynchronizeColumnName(String name);
    
    /**
     * トランザクションログテーブルの列「同期したかどうかを示すフラグ」の列名を取得する。<p>
     *
     * @return 列名
     */
    public String getSynchronizeColumnName();
    
    /**
     * 同期時に、同期したトランザクションログを削除するかどうかを設定する。<p>
     * 削除しない場合は、トランザクションログテーブルの列「同期したかどうかを示すフラグ」に、"1"を更新する。<br>
     * デフォルトは、trueで削除する。<br>
     * 
     * @param isDelete 削除する場合は、true
     */
    public void setDeleteOnSynchronize(boolean isDelete);
    
    /**
     * 同期時に、同期したトランザクションログを削除するかどうかを判定する。<p>
     * 
     * @return trueの場合、削除する
     */
    public boolean isDeleteOnSynchronize();
    
    /**
     * 同期したトランザクションログを掃除する際に、削除条件となるトランザクションログテーブルの列「同期したかどうかを示すフラグ」の列名を設定する。<p>
     * 指定されていない場合は、{@link #getSynchronizeColumnName()}のみを対象とする。指定された場合は、{@link #getSynchronizeColumnName()}と指定された列を対象とする。<br>
     *
     * @param names 列名の配列
     */
    public void setGarbageSynchronizeColumnNames(String[] names);
    
    /**
     * 同期したトランザクションログを掃除する際に、削除条件となるトランザクションログテーブルの列「同期したかどうかを示すフラグ」の列名を取得する。<p>
     *
     * @return 列名の配列
     */
    public String[] getGarbageSynchronizeColumnNames();
    
    /**
     * 同期したトランザクションログの有効時間[ms]を設定する。<p>
     * {@link #isDeleteOnSynchronize()}がtrueの場合のみ有効で、トランザクションログの更新時刻が現在時刻よりも指定時間[ms]以上前のログで、同期済みのものを削除する。<br>
     * デフォルトは、-1で掃除しない。<br>
     * 
     * @param millis 有効時間[ms]
     */
    public void setGarbageTime(long millis);
    
    /**
     * 同期したトランザクションログの有効時間[ms]を取得する。<p>
     * 
     * @return 有効時間[ms]
     */
    public long getGarbageTime();
    
    /**
     * 更新ユーザ名を設定する。<p>
     * デフォルトは、ホスト名。<br>
     *
     * @param name 更新ユーザ名
     */
    public void setUpdateUser(String name);
    
    /**
     * 更新ユーザ名を取得する。<p>
     *
     * @return 更新ユーザ名
     */
    public String getUpdateUser();
    
    /**
     * サービスの起動時に同期を行うかどうかを設定する。<p>
     * デフォルトは、falseで、起動時に同期しない。<br>
     * 
     * @param isSynchronize 同期を行う場合は、true
     */
    public void setSynchronizeOnStart(boolean isSynchronize);
    
    /**
     * サービスの起動時に同期を行うかどうかを判定する。<p>
     * 
     * @return trueの場合、同期を行う
     */
    public boolean isSynchronizeOnStart();
    
    /**
     * 最大バッチ実行件数を設定する。<p>
     * 同期処理を行う場合に、同じクエリのトランザクションが続く限りバッチ実行を行うが、
     * ロールバックセグメントが不足する可能性があるので、この最大件数までバッチが溜まると一旦コミットされる。<br>
     * デフォルトは、10件。<br>
     * 
     * @param max 最大バッチ実行件数
     */
    public void setMaxBatchCount(int max);
    
    /**
     * 最大バッチ実行件数を取得する。<p>
     * 
     * @return 最大バッチ実行件数
     */
    public int getMaxBatchCount();
    
    /**
     * データベースを同期する。<p>
     * 同期元のトランザクションログを読み込んで、同期先にトランザクションを実行する。その際、実行したトランザクションログは削除する。<br>
     *
     * @return 同期したトランザクションの件数
     * @exception Exception 同期中に例外が発生した場合
     */
    public int synchronize() throws Exception;
    
    /**
     * 同期元の現在のトランザクションログ件数を取得する。<p>
     *
     * @return トランザクションログ件数
     * @exception Exception トランザクションログ件数の取得に失敗した場合
     */
    public long countTransactionLog() throws Exception;
}