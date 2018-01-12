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

import java.sql.Connection;
import java.util.Map;

/**
 * 永続管理。<p>
 *
 * @author M.Takata
 */
public interface PersistentManager{
    
    /**
     * データベースから読み込む。<p>
     * 指定されたqueryに指定されたinputの情報を埋め込み実行して、実行結果のResultSetを指定されたoutputに詰めて返す。<br>
     * パラメータqueryは、埋め込みクエリで、SQLにinputをどう渡してoutputにどう詰めて返すかを指定する。パラメータinputの情報を埋め込む場合は、"<-{プロパティ名}"で埋め込む。また、実行結果のResultSetから、出力Beanに詰めるには、"->{プロパティ名}"で埋め込む。<br>
     * <pre>
     *   例：select USER.NAME->{Header(user).name}, MAIL.ADDRESS->{RecordList(mail).address}, from USER, MAIL where USER.ID = ?<-{Id} and USER.ID = MAIL.ID
     * </pre>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param input 入力Bean
     * @param output 出力Bean
     * @return データベースから読み込んだ出力Bean
     * @exception PersistentException 読み込みに失敗した場合
     */
    public Object loadQuery(Connection con, String query, Object input, Object output) throws PersistentException;
    
    /**
     * データベースから読み込む。<p>
     * 指定されたqueryに指定されたinputの情報を埋め込み実行して、実行結果のResultSetを指定されたoutputに詰めて返す。<br>
     * パラメータqueryは、埋め込みクエリで、SQLにinputをどう渡してoutputにどう詰めて返すかを指定する。パラメータinputの情報を埋め込む場合は、"<-{プロパティ名}"で埋め込む。また、実行結果のResultSetから、出力Beanに詰めるには、"->{プロパティ名}"で埋め込む。<br>
     * <pre>
     *   例：select USER.NAME->{Header(user).name}, MAIL.ADDRESS->{RecordList(mail).address}, from USER, MAIL where USER.ID = ?<-{Id} and USER.ID = MAIL.ID
     * </pre>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param input 入力Bean
     * @param output 出力Bean
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @param resultSetProps java.sql.ResultSetに対するプロパティのマップ
     * @return データベースから読み込んだ出力Bean
     * @exception PersistentException 読み込みに失敗した場合
     */
    public Object loadQuery(Connection con, String query, Object input, Object output, Map statementProps, Map resultSetProps) throws PersistentException;
    
    /**
     * データベースから読み込む。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param input 入力Bean
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @param output 出力Bean
     * @param outputProps 出力Beanと列名のマッピング。プロパティ文字列のString、String[]、またはList<String>、またはキーが列名で値がプロパティ文字列となるMap<String, String>
     * @return データベースから読み込んだ出力Bean
     * @exception PersistentException 読み込みに失敗した場合
     */
    public Object load(Connection con, String sql, Object input, Object inputProps, Object output, Object outputProps) throws PersistentException;
    
    /**
     * データベースから読み込む。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param input 入力Bean
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @param output 出力Bean
     * @param outputProps 出力Beanと列名のマッピング。プロパティ文字列のString、String[]、またはList<String>、またはキーが列名で値がプロパティ文字列となるMap<String, String>
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @param resultSetProps java.sql.ResultSetに対するプロパティのマップ
     * @return データベースから読み込んだ出力Bean
     * @exception PersistentException 読み込みに失敗した場合
     */
    public Object load(Connection con, String sql, Object input, Object inputProps, Object output, Object outputProps, Map statementProps, Map resultSetProps) throws PersistentException;
    
    /**
     * データベースから読み込むカーソルを生成する。<p>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param input 入力Bean
     * @return カーソル
     * @exception PersistentException カーソルの作成に失敗した場合
     * @see #loadQuery(Connection, String, Object, Object)
     */
    public Cursor createQueryCursor(Connection con, String query, Object input) throws PersistentException;
    
    /**
     * データベースから読み込むカーソルを生成する。<p>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param input 入力Bean
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @param resultSetProps java.sql.ResultSetに対するプロパティのマップ
     * @return カーソル
     * @exception PersistentException カーソルの作成に失敗した場合
     * @see #loadQuery(Connection, String, Object, Object)
     */
    public Cursor createQueryCursor(Connection con, String query, Object input, Map statementProps, Map resultSetProps) throws PersistentException;
    
    /**
     * データベースから読み込むカーソルを生成する。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param input 入力Bean
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @param outputProps 出力Beanと列名のマッピング。プロパティ文字列のString、String[]、またはList<String>、またはキーが列名で値がプロパティ文字列となるMap<String, String>
     * @return カーソル
     * @exception PersistentException カーソルの作成に失敗した場合
     * @see #load(Connection, String, Object, Object, Object, Object)
     */
    public Cursor createCursor(Connection con, String sql, Object input, Object inputProps, Object outputProps) throws PersistentException;
    
    /**
     * データベースから読み込むカーソルを生成する。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param input 入力Bean
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @param outputProps 出力Beanと列名のマッピング。プロパティ文字列のString、String[]、またはList<String>、またはキーが列名で値がプロパティ文字列となるMap<String, String>
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @param resultSetProps java.sql.ResultSetに対するプロパティのマップ
     * @return カーソル
     * @exception PersistentException カーソルの作成に失敗した場合
     * @see #load(Connection, String, Object, Object, Object, Object)
     */
    public Cursor createCursor(Connection con, String sql, Object input, Object inputProps, Object outputProps, Map statementProps, Map resultSetProps) throws PersistentException;
    
    /**
     * データベースに書き込む。<p>
     * 指定されたsqlに指定されたinputの情報を埋め込み実行して、更新件数を返す。<br>
     * パラメータqueryは、埋め込みクエリで、SQLにinputをどう渡すかを、"<-{プロパティ名}"で埋め込む。<br>
     * <pre>
     *   例：update MAIL set ADDRESS = ?<-{RecordList(mail).address} where USER.ID = ?<-{Header(user).Id}
     * </pre>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param input 入力Bean
     * @return 更新件数
     * @exception PersistentException 書き込みに失敗した場合
     */
    public int persistQuery(Connection con, String query, Object input) throws PersistentException;
    
    /**
     * データベースに書き込む。<p>
     * 指定されたsqlに指定されたinputの情報を埋め込み実行して、更新件数を返す。<br>
     * パラメータqueryは、埋め込みクエリで、SQLにinputをどう渡すかを、"<-{プロパティ名}"で埋め込む。<br>
     * <pre>
     *   例：update MAIL set ADDRESS = ?<-{RecordList(mail).address} where USER.ID = ?<-{Header(user).Id}
     * </pre>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param input 入力Bean
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @return 更新件数
     * @exception PersistentException 書き込みに失敗した場合
     */
    public int persistQuery(Connection con, String query, Object input, Map statementProps) throws PersistentException;
    
    /**
     * データベースに書き込む。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param input 入力パラメータの配列
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @return 更新件数
     * @exception PersistentException 書き込みに失敗した場合
     */
    public int persist(Connection con, String sql, Object input, Object inputProps) throws PersistentException;
    
    /**
     * データベースに書き込む。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param input 入力パラメータの配列
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @return 更新件数
     * @exception PersistentException 書き込みに失敗した場合
     */
    public int persist(Connection con, String sql, Object input, Object inputProps, Map statementProps) throws PersistentException;
    
    /**
     * データベースにバッチ実行を行うBatchExecutorを生成する。<p>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @return BatchExecutor
     * @exception PersistentException BatchExecutorの生成に失敗した場合
     */
    public BatchExecutor createQueryBatchExecutor(Connection con, String query) throws PersistentException;
    
    /**
     * データベースにバッチ実行を行うBatchExecutorを生成する。<p>
     *
     * @param con コネクション
     * @param query 埋め込みクエリ
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @return BatchExecutor
     * @exception PersistentException BatchExecutorの生成に失敗した場合
     */
    public BatchExecutor createQueryBatchExecutor(Connection con, String query, Map statementProps) throws PersistentException;
    
    /**
     * データベースにバッチ実行を行うBatchExecutorを生成する。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @return BatchExecutor
     * @exception PersistentException BatchExecutorの生成に失敗した場合
     */
    public BatchExecutor createBatchExecutor(Connection con, String sql, Object inputProps) throws PersistentException;
    
    /**
     * データベースにバッチ実行を行うBatchExecutorを生成する。<p>
     *
     * @param con コネクション
     * @param sql 埋め込みSQL
     * @param statementProps java.sql.Statementに対するプロパティのマップ
     * @param inputProps 入力Beanと入力パラメータのマッピング。プロパティ文字列のString、String[]、またはList<String>
     * @return BatchExecutor
     * @exception PersistentException BatchExecutorの生成に失敗した場合
     */
    public BatchExecutor createBatchExecutor(Connection con, String sql, Object inputProps, Map statementProps) throws PersistentException;
    
    /**
     * 読み込みカーソル。<p>
     *
     * @author M.Takata
     */
    public interface Cursor{
        
        /**
         * 次の行に移動する。<p>
         *
         * @return 次の行が存在した場合は、true
         * @exception PersistentException 移動に失敗した場合
         */
        public boolean next() throws PersistentException;
        
        /**
         * 前の行に移動する。<p>
         *
         * @return 前の行が存在した場合は、true
         * @exception PersistentException 移動に失敗した場合
         */
        public boolean previous() throws PersistentException;
        
        /**
         * 先頭の行に移動する。<p>
         *
         * @return 先頭の行が存在した場合は、true
         * @exception PersistentException 移動に失敗した場合
         */
        public boolean first() throws PersistentException;
        
        /**
         * 最後の行に移動する。<p>
         *
         * @return 最後の行が存在した場合は、true
         * @exception PersistentException 移動に失敗した場合
         */
        public boolean last() throws PersistentException;
        
        /**
         * 先頭の行の前に移動する。<p>
         *
         * @exception PersistentException 移動に失敗した場合
         */
        public void beforeFirst() throws PersistentException;
        
        /**
         * 最後の行の後に移動する。<p>
         *
         * @exception PersistentException 移動に失敗した場合
         */
        public void afterLast() throws PersistentException;
        
        /**
         * 指定された行に移動する。<p>
         *
         * @param row 行番号
         * @exception PersistentException 移動に失敗した場合
         */
        public boolean absolute(int row) throws PersistentException;
        
        /**
         * 指定された行数だけ移動する。<p>
         *
         * @param rows 行数
         * @exception PersistentException 移動に失敗した場合
         */
        public boolean relative(int rows) throws PersistentException;
        
        /**
         * 現在の行が先頭か判定する。<p>
         *
         * @return 先頭の場合、true
         * @exception PersistentException 判定に失敗した場合
         */
        public boolean isFirst() throws PersistentException;
        
        /**
         * 現在の行が最後か判定する。<p>
         *
         * @return 最後の場合、true
         * @exception PersistentException 判定に失敗した場合
         */
        public boolean isLast() throws PersistentException;
        
        /**
         * 現在の行が先頭の前か判定する。<p>
         *
         * @return 先頭の前の場合、true
         * @exception PersistentException 判定に失敗した場合
         */
        public boolean isBeforeFirst() throws PersistentException;
        
        /**
         * 現在の行が最後の後か判定する。<p>
         *
         * @return 最後の後の場合、true
         * @exception PersistentException 判定に失敗した場合
         */
        public boolean isAfterLast() throws PersistentException;
        
        /**
         * フェッチする方向を設定する。<p>
         *
         * @param direction フェッチする方向
         * @exception PersistentException 設定に失敗した場合
         * @see java.sql.ResultSet#FETCH_FORWARD
         * @see java.sql.ResultSet#FETCH_REVERSE
         * @see java.sql.ResultSet#FETCH_UNKNOWN
         */
        public void setFetchDirection(int direction) throws PersistentException;
        
        /**
         * フェッチする方向を取得する。<p>
         *
         * @return フェッチする方向
         * @exception PersistentException 取得に失敗した場合
         * @see java.sql.ResultSet#FETCH_FORWARD
         * @see java.sql.ResultSet#FETCH_REVERSE
         * @see java.sql.ResultSet#FETCH_UNKNOWN
         */
        public int getFetchDirection() throws PersistentException;
        
        /**
         * フェッチする行数を設定する。<p>
         *
         * @param rows フェッチする行数
         * @exception PersistentException 設定に失敗した場合
         */
        public void setFetchSize(int rows) throws PersistentException;
        
        /**
         * フェッチする行数を取得する。<p>
         *
         * @return フェッチする行数
         * @exception PersistentException 取得に失敗した場合
         */
        public int getFetchSize() throws PersistentException;
        
        /**
         * 現在の行番号を取得する。<p>
         *
         * @return 現在の行番号
         * @exception PersistentException 取得に失敗した場合
         */
        public int getRow() throws PersistentException;
        
        /**
         * データベースから読み込む。<p>
         *
         * @param output 出力Bean
         * @return データベースから読み込んだ出力Bean
         * @exception PersistentException 読み込みに失敗した場合
         */
        public Object load(Object output) throws PersistentException;
        
        /**
         * リソースを開放したかどうか判定する。<p>
         * 
         * @return リソースを開放していた場合true
         */
        public boolean isClosed();
        
        /**
         * リソースを開放する。<p>
         */
        public void close();
    }
    
    /**
     * バッチ実行。<p>
     *
     * @author M.Takata
     */
    public interface BatchExecutor{
        
        /**
         * 自動バッチ実行の件数を設定する。<p>
         * バッチ実行時に、指定件数のバッチ登録が溜まると自動的にバッチ実行を行う。<br>
         *
         * @param count 自動バッチ実行件数
         */
        public void setAutoBatchPersistCount(int count);
        
        /**
         * 自動バッチ実行の件数を取得する。<p>
         *
         * @return 自動バッチ実行件数
         */
        public int getAutoBatchPersistCount();
        
        /**
         * バッチ実行時に、自動的にコミットを行うかどうかを設定する。<p>
         * デフォルトはfalseで、自動コミットを行わない。<br>
         *
         * @param isCommit 自動的にコミットを行う場合はtrue
         */
        public void setAutoCommitOnPersist(boolean isCommit);
        
        /**
         * バッチ実行時に、自動的にコミットを行うかどうかを判定する。<p>
         *
         * @return trueの場合、自動的にコミットを行う
         */
        public boolean isAutoCommitOnPersist();
        
        /**
         * バッチ登録を行う。<p>
         *
         * @param input 入力Bean
         * @return 自動バッチ実行の場合で、バッチ実行が行われた時の更新件数
         * @exception PersistentException バッチ登録に失敗した場合
         */
        public int addBatch(Object input) throws PersistentException;
        
        /**
         * データベースにバッチ実行で書き込む。<p>
         *
         * @return 更新件数
         * @exception PersistentException バッチ実行に失敗した場合
         */
        public int persist() throws PersistentException;
        
        /**
         * バッチ登録をクリアする。<p>
         * 
         * @exception PersistentException バッチ登録のクリアに失敗した場合
         */
        public void clearBatch() throws PersistentException;
        
        /**
         * リソースを開放する。<p>
         */
        public void close();
    }
}