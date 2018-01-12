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

import java.sql.SQLException;
import java.io.IOException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * {@link TableCreatorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see TableCreatorService
 */
public interface TableCreatorServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     * このStreamConverterを使って、{@link #setInsertRecords(String)}や{@link #setInsertRecordsFilePath(String)}で指定された挿入レコード文字列を{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}に変換して、テーブルにINSERTしていく。<br>
     * また、{@link #setBackupFilePath(String)}で指定されたファイルにバックアップする際や、同様にそのファイルから復元する際にも使用する。<br>
     *
     * @param name StreamConverterのサービス名
     */
    public void setRecordListConverterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterのサービス名
     */
    public ServiceName getRecordListConverterServiceName();
    
    /**
     * 対象となるテーブルの存在確認をするSQLを設定する。<p>
     * 結果として、テーブルの件数を返すSQLにする必要がある。<br>
     *
     * @param query テーブルの存在確認をするSQL
     */
    public void setExistsTableQuery(String query);
    
    /**
     * 対象となるテーブルの存在確認をするSQLを取得する。<p>
     *
     * @return テーブルの存在確認をするSQL
     */
    public String getExistsTableQuery();
    
    /**
     * バックアップ対象のレコードを検索するSQLを設定する。<p>
     *
     * @param query バックアップ対象のレコードを検索するSQL
     */
    public void setSelectQuery(String query);
    
    /**
     * バックアップ対象のレコードを検索するSQLを取得する。<p>
     *
     * @return バックアップ対象のレコードを検索するSQL
     */
    public String getSelectQuery();
    
    /**
     * 対象となるテーブルを作成するSQLを設定する。<p>
     *
     * @param query テーブルを作成するSQL
     */
    public void setCreateTableQuery(String query);
    
    /**
     * 対象となるテーブルを作成するSQLを取得する。<p>
     *
     * @return テーブルを作成するSQL
     */
    public String getCreateTableQuery();
    
    /**
     * 対象となるテーブルを作成する直前に実行するSQLを設定する。<p>
     *
     * @param queries テーブルを作成する直前に実行するSQL
     */
    public void setPreCreateTableQueries(String[] queries);
    
    /**
     * 対象となるテーブルを作成する直前に実行するSQLを取得する。<p>
     *
     * @return テーブルを作成する直前に実行するSQL
     */
    public String[] getPreCreateTableQueries();
    
    /**
     * 対象となるテーブルを作成した直後に実行するSQLを設定する。<p>
     *
     * @param queries テーブルを作成した直後に実行するSQL
     */
    public void setPostCreateTableQueries(String[] queries);
    
    /**
     * 対象となるテーブルを作成した直後に実行するSQLを取得する。<p>
     *
     * @return テーブルを作成した直後に実行するSQL
     */
    public String[] getPostCreateTableQueries();
    
    /**
     * 対象となるテーブルを削除するSQLを設定する。<p>
     *
     * @param query テーブルを削除するSQL
     */
    public void setDropTableQuery(String query);
    
    /**
     * 対象となるテーブルを削除するSQLを取得する。<p>
     *
     * @return テーブルを削除するSQL
     */
    public String getDropTableQuery();
    
    /**
     * 対象となるテーブルを削除する直前に実行するSQLを設定する。<p>
     *
     * @param queries テーブルを削除する直前に実行するSQL
     */
    public void setPreDropTableQueries(String[] queries);
    
    /**
     * 対象となるテーブルを削除する直前に実行するSQLを取得する。<p>
     *
     * @return テーブルを削除する直前に実行するSQL
     */
    public String[] getPreDropTableQueries();
    
    /**
     * 対象となるテーブルを削除した直後に実行するSQLを設定する。<p>
     *
     * @param queries テーブルを削除した直後に実行するSQL
     */
    public void setPostDropTableQueries(String[] queries);
    
    /**
     * 対象となるテーブルを削除した直後に実行するSQLを取得する。<p>
     *
     * @return テーブルを削除した直後に実行するSQL
     */
    public String[] getPostDropTableQueries();
    
    /**
     * 対象となるテーブルのレコードを削除するSQLを設定する。<p>
     *
     * @param query テーブルのレコードを削除するSQL
     */
    public void setDeleteQuery(String query);
    
    /**
     * 対象となるテーブルのレコードを削除するSQLを取得する。<p>
     *
     * @return テーブルのレコードを削除するSQL
     */
    public String getDeleteQuery();
    
    /**
     * 対象となるテーブルのレコードを挿入する埋め込みSQLを設定する。<p>
     *
     * @param query テーブルのレコードを挿入する埋め込みSQL
     */
    public void setInsertQuery(String query);
    
    /**
     * 対象となるテーブルのレコードを挿入する埋め込みSQLを取得する。<p>
     *
     * @return テーブルのレコードを挿入する埋め込みSQL
     */
    public String getInsertQuery();
    
    /**
     * 挿入するレコード文字列を設定する。<p>
     * レコード文字列は、バイトストリームに変換され、{@link #setRecordListConverterServiceName(ServiceName)}で設定された{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスによって、{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}に変換される。<br>
     * 変換されたRecordList内の{@link jp.ossc.nimbus.beans.dataset.Record Record}が、{@link #setInsertQuery(String)}で指定された埋め込みSQLによって、テーブルに挿入される。<br>
     * レコード文字列の指定を外部ファイルで行いたい場合は、{@link #setInsertRecordsFilePath(String)}で設定する。<br>
     *
     * @param records レコード文字列
     */
    public void setInsertRecords(String records);
    
    /**
     * 挿入するレコード文字列を取得する。<p>
     *
     * @return レコード文字列
     */
    public String getInsertRecords();
    
    /**
     * 挿入するレコードファイルのパスを設定する。<p>
     * ファイルは、ストリームに変換され、{@link #setRecordListConverterServiceName(ServiceName)}で設定された{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスによって、{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}に変換される。<br>
     * 変換されたRecordList内の{@link jp.ossc.nimbus.beans.dataset.Record Record}が、{@link #setInsertQuery(String)}で指定された埋め込みSQLによって、テーブルに挿入される。<br>
     * レコード文字列の指定を直接行いたい場合は、{@link #setInsertRecords(String)}で設定する。<br>
     *
     * @param path レコードファイルパス
     */
    public void setInsertRecordsFilePath(String path);
    
    /**
     * 挿入するレコードファイルのパスを設定する。<p>
     *
     * @return レコードファイルのパス
     */
    public String getInsertRecordsFilePath();
    
    /**
     * 挿入するレコードファイル及びバックアップファイルの文字エンコーディングを設定する。<p>
     *
     * @param enc 文字エンコーディング
     */
    public void setFileEncoding(String enc);
    
    /**
     * 挿入するレコードファイル及びバックアップファイルの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getFileEncoding();
    
    /**
     * レコード文字列を読み込む{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}のスキーマを設定する。<p>
     *
     * @param schema スキーマ文字列
     */
    public void setRecordListSchema(String schema);
    
    /**
     * レコード文字列を読み込む{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}のスキーマを取得する。<p>
     *
     * @return スキーマ文字列
     */
    public String getRecordListSchema();
    
    /**
     * レコード文字列を読み込む{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}を設定する。<p>
     *
     * @param list RecordList
     */
    public void setRecordList(RecordList list);
    
    /**
     * レコード文字列を読み込む{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}を取得する。<p>
     *
     * @return RecordList
     */
    public RecordList getRecordList();
    
    /**
     * テーブルにレコードをバッチ実行で挿入する場合のバッチ件数を設定する。<p>
     * デフォルトは、0でバッチ実行しない。<br>
     *
     * @param size バッチ件数
     */
    public void setInsertBatchSize(int size);
    
    /**
     * テーブルにレコードをバッチ実行で挿入する場合のバッチ件数を取得する。<p>
     *
     * @return バッチ件数
     */
    public int getInsertBatchSize();
    
    /**
     * テーブルからレコードを検索する際のフェッチサイズを設定する。<p>
     *
     * @param size フェッチサイズ
     */
    public void setFetchSize(int size);
    
    /**
     * テーブルからレコードを検索する際のフェッチサイズを取得する。<p>
     *
     * @return フェッチサイズ
     */
    public int getFetchSize();
    
    /**
     * サービスの開始時に、現在のテーブルの内容をバックアップするかどうかを設定する。<p>
     * デフォルトは、falseでバックアップしない。<br>
     *
     * @param isBackup バックアップする場合は、true
     */
    public void setBackupOnStart(boolean isBackup);
    
    /**
     * サービスの開始時に、現在のテーブルの内容をバックアップするかどうかを判定する。<p>
     *
     * @return trueの場合は、バックアップする
     */
    public boolean isBackupOnStart();
    
    /**
     * サービスの開始時に、現在のテーブルの内容を復元するかどうかを設定する。<p>
     * デフォルトは、falseで復元しない。<br>
     *
     * @param isRestore 復元する場合は、true
     */
    public void setRestoreOnStart(boolean isRestore);
    
    /**
     * サービスの開始時に、現在のテーブルの内容を復元するかどうかを判定する。<p>
     *
     * @return trueの場合は、復元する
     */
    public boolean isRestoreOnStart();
    
    /**
     * サービスの開始時に、テーブルを削除するかどうかを設定する。<p>
     * デフォルトは、falseで削除しない。<br>
     *
     * @param isDrop 削除する場合は、true
     */
    public void setDropTableOnStart(boolean isDrop);
    
    /**
     * サービスの開始時に、テーブルを削除するかどうかを判定する。<p>
     *
     * @return trueの場合は、削除する
     */
    public boolean isDropTableOnStart();
    
    /**
     * サービスの開始時に、テーブルを作成するかどうかを設定する。<p>
     * デフォルトは、falseで作成しない。<br>
     *
     * @param isCreate 作成する場合は、true
     */
    public void setCreateTableOnStart(boolean isCreate);
    
    /**
     * サービスの開始時に、テーブルを作成するかどうかを判定する。<p>
     *
     * @return trueの場合は、作成する
     */
    public boolean isCreateTableOnStart();
    
    /**
     * サービスの開始時に、テーブルのレコードを削除するかどうかを設定する。<p>
     * デフォルトは、falseで削除しない。<br>
     *
     * @param isDelete 削除する場合は、true
     */
    public void setDeleteOnStart(boolean isDelete);
    
    /**
     * サービスの開始時に、テーブルのレコードを削除するかどうかを判定する。<p>
     *
     * @return trueの場合は、削除する
     */
    public boolean isDeleteOnStart();
    
    /**
     * サービスの開始時に、テーブルのレコードを挿入するかどうかを設定する。<p>
     * デフォルトは、falseで挿入しない。<br>
     *
     * @param isInsert 挿入する場合は、true
     */
    public void setInsertOnStart(boolean isInsert);
    
    /**
     * サービスの開始時に、テーブルのレコードを挿入するかどうかを判定する。<p>
     *
     * @return trueの場合は、挿入する
     */
    public boolean isInsertOnStart();
    
    /**
     * サービスの停止時に、テーブルのレコードを削除するかどうかを設定する。<p>
     * デフォルトは、falseで削除しない。<br>
     *
     * @param isDelete 削除する場合は、true
     */
    public void setDeleteOnStop(boolean isDelete);
    
    /**
     * サービスの停止時に、テーブルのレコードを削除するかどうかを判定する。<p>
     *
     * @return trueの場合は、削除する
     */
    public boolean isDeleteOnStop();
    
    /**
     * サービスの停止時に、テーブルのレコードを挿入するかどうかを設定する。<p>
     * デフォルトは、falseで挿入しない。<br>
     *
     * @param isInsert 挿入する場合は、true
     */
    public void setInsertOnStop(boolean isInsert);
    
    /**
     * サービスの停止時に、テーブルのレコードを挿入するかどうかを判定する。<p>
     *
     * @return trueの場合は、挿入する
     */
    public boolean isInsertOnStop();
    
    /**
     * サービスの停止時に、テーブルを削除するかどうかを設定する。<p>
     * デフォルトは、falseで削除しない。<br>
     *
     * @param isDrop 削除する場合は、true
     */
    public void setDropTableOnStop(boolean isDrop);
    
    /**
     * サービスの停止時に、テーブルを削除するかどうかを判定する。<p>
     *
     * @return trueの場合は、削除する
     */
    public boolean isDropTableOnStop();
    
    /**
     * サービスの停止時に、テーブルを復元するかどうかを設定する。<p>
     * デフォルトは、falseで復元しない。<br>
     *
     * @param isRestore 復元する場合は、true
     */
    public void setRestoreOnStop(boolean isRestore);
    
    /**
     * サービスの停止時に、テーブルを復元するかどうかを判定する。<p>
     *
     * @return trueの場合は、復元する
     */
    public boolean isRestoreOnStop();
    
    /**
     * サービスの停止時に、現在のテーブルをバックアップするかどうかを設定する。<p>
     * デフォルトは、falseでバックアップしない。<br>
     *
     * @param isBackup バックアップする場合は、true
     */
    public void setBackupOnStop(boolean isBackup);
    
    /**
     * サービスの停止時に、現在のテーブルをバックアップするかどうかを判定する。<p>
     *
     * @return trueの場合は、バックアップする
     */
    public boolean isBackupOnStop();
    
    /**
     * テーブルを削除する際に無視するSQLExceptionのエラーコードを設定する。<p>
     * デフォルトでは、全てのSQLExceptionを無視しない。<br>
     *
     * @param code エラーコードの配列
     */
    public void setIgnoreSQLExceptionErrorCodeOnDropTable(int[] code);
    
    /**
     * テーブルを削除する際に無視するSQLExceptionのエラーコードを取得する。<p>
     *
     * @return エラーコードの配列
     */
    public int[] getIgnoreSQLExceptionErrorCodeOnDropTable();
    
    /**
     * テーブルのレコードを削除する際に無視するSQLExceptionのエラーコードを設定する。<p>
     * デフォルトでは、全てのSQLExceptionを無視しない。<br>
     *
     * @param code エラーコードの配列
     */
    public void setIgnoreSQLExceptionErrorCodeOnDelete(int[] code);
    
    /**
     * テーブルのレコードを削除する際に無視するSQLExceptionのエラーコードを取得する。<p>
     *
     * @return エラーコードの配列
     */
    public int[] getIgnoreSQLExceptionErrorCodeOnDelete();
    
    /**
     * テーブルを作成する際に無視するSQLExceptionのエラーコードを設定する。<p>
     * デフォルトでは、全てのSQLExceptionを無視しない。<br>
     *
     * @param code エラーコードの配列
     */
    public void setIgnoreSQLExceptionErrorCodeOnCreateTable(int[] code);
    
    /**
     * テーブルを作成する際に無視するSQLExceptionのエラーコードを取得する。<p>
     *
     * @return エラーコードの配列
     */
    public int[] getIgnoreSQLExceptionErrorCodeOnCreateTable();
    
    /**
     * テーブルのレコードを挿入する際に無視するSQLExceptionのエラーコードを設定する。<p>
     * デフォルトでは、全てのSQLExceptionを無視しない。<br>
     *
     * @param code エラーコードの配列
     */
    public void setIgnoreSQLExceptionErrorCodeOnInsert(int[] code);
    
    /**
     * テーブルのレコードを挿入する際に無視するSQLExceptionのエラーコードを取得する。<p>
     *
     * @return エラーコードの配列
     */
    public int[] getIgnoreSQLExceptionErrorCodeOnInsert();
    
    /**
     * 一連のテーブル操作をトランザクション的に実行するかどうかを設定する。<p>
     * 一連のテーブル操作とは、サービスの開始時及び停止時のテーブル操作、{@link #executeAllQuery()}呼び出し時の操作である。<br>
     *
     * @param isTransacted トランザクション的に実行する場合true
     */
    public void setTransacted(boolean isTransacted);
    
    /**
     * 一連のテーブル操作をトランザクション的に実行するかどうかを判定する。<p>
     *
     * @return trueの場合、トランザクション的に実行する
     */
    public boolean isTransacted();
    
    /**
     * Javaの型に対する、JDBCの型を設定する。<p>
     * テーブルにレコードを挿入する時に、値がnullの場合に、このマッピングを用いて、{@link java.sql.PreparedStatement#setNull(int, int)}を呼び出す。<br>
     *
     * @param type Javaの型
     * @param sqlType JDBCの型。{@link java.sql.Types}の定数値
     */
    public void setSqlType(Class type, int sqlType);
    
    /**
     * バックアップファイルのパスを設定する。<p>
     * このパスを指定しない場合は、メモリ中にバックアップされる。<br>
     * メモリを節約したい場合や、バックアップを永続化したい場合などに指定する。<br>
     *
     * @param path バックアップファイルのパス
     */
    public void setBackupFilePath(String path);
    
    /**
     * バックアップファイルのパスを取得する。<p>
     *
     * @return バックアップファイルのパス
     */
    public String getBackupFilePath();
    
    /**
     * テーブルを削除する。<p>
     * {@link #setDropTableQuery(String)}を設定されていない場合は、何もしない。<br>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     */
    public void dropTable() throws ConnectionFactoryException, SQLException;
    
    /**
     * テーブルのレコードを削除する。<p>
     * {@link #setDeleteQuery(String)}を設定されていない場合は、何もしない。<br>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     */
    public void deleteRecords() throws ConnectionFactoryException, SQLException;
    
    /**
     * テーブルを作成する。<p>
     * {@link #setCreateTableQuery(String)}を設定されていない場合は、何もしない。<br>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     */
    public void createTable() throws ConnectionFactoryException, SQLException;
    
    /**
     * テーブルのレコードを挿入する。<p>
     * {@link #setInsertQuery(String)}と、{@link #setInsertRecords(String)}または{@link #setInsertRecordsFilePath(String)}を設定されていない場合は、何もしない。<br>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     * @exception ConvertException レコード文字列の変換に失敗した場合
     * @exception IOException レコードファイルが存在しない場合や、読み込みに失敗した場合
     */
    public void insertRecords()
     throws ConnectionFactoryException, SQLException,
            ConvertException, IOException;
    
    /**
     * テーブルのレコードをバックアップする。<p>
     * {@link #setSelectQuery(String)}と、{@link #setRecordListSchema(String)}または{@link #setRecordList(jp.ossc.nimbus.beans.dataset.RecordList)}を設定されていない場合は、何もしない。<br>
     * 通常、バックアップは、メモリ中に行われるが、{@link #setBackupFilePath(String)}を設定している場合は、ファイルにバックアップされる。<br>
     * バックアップしたレコードは、{@link #restoreRecords()}で復元する事ができる。<br>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     * @exception ConvertException レコード文字列の変換に失敗した場合
     * @exception IOException バックアップファイルのパスが存在しない場合や、書き込みに失敗した場合
     */
    public void backupRecords()
     throws ConnectionFactoryException, SQLException,
            IOException, ConvertException;
    
    /**
     * テーブルのレコードを復元する。<p>
     * {@link #setInsertQuery(String)}と、{@link #setInsertRecords(String)}または{@link #setInsertRecordsFilePath(String)}を設定されていない場合は、何もしない。<br>
     * 通常、復元は、メモリ中から行われるが、{@link #setBackupFilePath(String)}を設定している場合は、ファイルから復元される。<br>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     * @exception ConvertException レコード文字列の変換に失敗した場合
     * @exception IOException バックアップファイルが存在しない場合や、読み込みに失敗した場合
     */
    public void restoreRecords()
     throws ConnectionFactoryException, SQLException,
            IOException, ConvertException;
    
    /**
     * テーブルの削除、レコードの削除、テーブルの作成、レコードの挿入を順次行う。<p>
     *
     * @exception ConnectionFactoryException JDBCコネクションの取得に失敗した場合
     * @exception SQLException SQLの実行に失敗した場合
     * @exception ConvertException レコード文字列の変換に失敗した場合
     * @exception IOException ファイルが存在しない場合や、読み込みに失敗した場合
     */
    public void executeAllQuery()
     throws ConnectionFactoryException, SQLException,
            ConvertException, IOException;
}
