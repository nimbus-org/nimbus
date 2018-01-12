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
package jp.ossc.nimbus.service.writer.db;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link DatabaseWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DatabaseWriterService
 */
public interface DatabaseWriterServiceMBean
 extends ServiceBaseMBean, jp.ossc.nimbus.service.writer.MessageWriter{
    
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
     * INSERTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを設定する。<p>
     * 以下のフォーマットで指定する。<br>
     * INSERTするPreparedStatement用のSQL=埋め込みパラメータのキー名,…<br>
     * 右辺の埋め込みパラメータのキー名とは、{@link DatabaseWriterService#write(jp.ossc.nimbus.service.writer.WritableRecord)}メソッドの入力で渡される{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}に格納されている{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}のキー名である。<br>
     * <pre>
     * 例：
     *   insert into log_table(id, message) values(?, ?)=ID,MESSAGE
     * </pre>
     *
     * @param sqls INSERTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public void setInsertSQL(Map sqls);
    
    /**
     * INSERTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを取得する。<p>
     *
     * @return INSERTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public Map getInsertSQL();
    
    /**
     * UPDATEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを設定する。<p>
     * 以下のフォーマットで指定する。<br>
     * UPDATEするPreparedStatement用のSQL=埋め込みパラメータのキー名,…<br>
     * 右辺の埋め込みパラメータのキー名とは、{@link DatabaseWriterService#write(jp.ossc.nimbus.service.writer.WritableRecord)}メソッドの入力で渡される{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}に格納されている{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}のキー名である。<br>
     * <pre>
     * 例：
     *   update log_table set message=? where id=?=MESSAGE,ID
     * </pre>
     *
     * @param sqls UPDATEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public void setUpdateSQL(Map sqls);
    
    /**
     * UPDATEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを取得する。<p>
     *
     * @return UPDATEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public Map getUpdateSQL();
    
    /**
     * DELETEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを設定する。<p>
     * 以下のフォーマットで指定する。<br>
     * DELETEするPreparedStatement用のSQL=埋め込みパラメータのキー名,…<br>
     * 右辺の埋め込みパラメータのキー名とは、{@link DatabaseWriterService#write(jp.ossc.nimbus.service.writer.WritableRecord)}メソッドの入力で渡される{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}に格納されている{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}のキー名である。<br>
     * <pre>
     * 例：
     *   delete from log_table where id=?=ID
     * </pre>
     *
     * @param sqls DELETEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public void setDeleteSQL(Map sqls);
    
    /**
     * DELETEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを取得する。<p>
     *
     * @return DELETEするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public Map getDeleteSQL();
    
    /**
     * SELECTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを設定する。<p>
     * 以下のフォーマットで指定する。<br>
     * SELECTするPreparedStatement用のSQL=埋め込みパラメータのキー名,…<br>
     * 右辺の埋め込みパラメータのキー名とは、{@link DatabaseWriterService#write(jp.ossc.nimbus.service.writer.WritableRecord)}メソッドの入力で渡される{@link jp.ossc.nimbus.service.writer.WritableRecord WritableRecord}に格納されている{@link jp.ossc.nimbus.service.writer.WritableElement WritableElement}のキー名である。<br>
     * <pre>
     * 例：
     *   select count(*) from log_table where id=?=ID
     * </pre>
     * ここで指定するSELECT文の結果は、レコード件数でなければならない。<br>
     * また、SELECTは、INSERTと、UPDATEまたはDELETEが設定されている場合のみ有効である。<br>
     * SELECTとINSERTが設定されている場合は、SELECTの結果が0件の時だけINSERTされる。<br>
     * SELECTとUPDATEが設定されている場合は、SELECTの結果が0件でない時だけUPDATEされる。<br>
     * SELECTとDELETEが設定されている場合は、SELECTの結果が0件でない時だけDELETEされる。<br>
     * SELECTと、INSERT、UPDATEが設定されている場合は、SELECTの結果が0件の時はINSERTされ、0件でない時はUPDATEされる。<br>
     * SELECTと、INSERT、DELETEが設定されている場合は、SELECTの結果が0件の時はINSERTされ、0件でない時はDELETEした後にINSERTされる。<br>
     *
     * @param sqls SELECTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public void setSelectSQL(Map sqls);
    
    /**
     * SELECTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピングを取得する。<p>
     *
     * @return SELECTするPreparedStatement用のSQLと埋め込みパラメータのキー名とのマッピング
     */
    public Map getSelectSQL();
    
    /**
     * SQLをバッチ実行する際のバッファサイズを設定する。<p>
     * デフォルトは、0でバッチ実行しない。<br>
     * 0以上の値を指定すると、そのサイズ分だけ、{@link DatabaseWriterService#write(jp.ossc.nimbus.service.writer.WritableRecord)}メソッドの呼び出し内容を溜め込み、SQLをバッチ実行する。<br>
     * 
     * @param size バッファサイズ
     */
    public void setBufferSize(int size);
    
    /**
     * SQLをバッチ実行する際のバッファサイズを取得する。<p>
     * 
     * @return バッファサイズ
     */
    public int getBufferSize();
    
    /**
     * SQLをバッチ実行する際のタイムアウトを設定する。<p>
     * デフォルトは、0でタイムアウトしない。<br>
     * 0以上の値を指定すると、{@link DatabaseWriterService#write(jp.ossc.nimbus.service.writer.WritableRecord)}メソッドが最後に呼び出された時刻から指定された時間を経過すると、溜まっているバッチSQLの数に関わらずSQLをバッチ実行する。<br>
     * 但し、{@link #setBufferSize(int)}で0以上の値（バッチ実行が有効になる値）が設定されていない場合は、この設定は無効である。<br>
     * 
     * @param timeout タイムアウト[ms]
     */
    public void setBufferTimeout(long timeout);
    
    /**
     * SQLをバッチ実行する際のタイムアウトを取得する。<p>
     * 
     * @return タイムアウト[ms]
     */
    public long getBufferTimeout();
    
    /**
     * 自動コミットするかどうかを判定する。<p>
     *
     * @return trueの場合、自動コミットする
     */
    public boolean isAutoCommit();
    
    /**
     * 自動コミットするかどうかを設定する。<p>
     * 但し、{@link #setBufferSize(int)}に有効な値を設定している場合は、別スレッドでバッチ実行されるため、この属性に関わらず、自動的にコミットされる。<br>
     * デフォルトは、true。<br>
     *
     * @param isAuto 自動コミットする場合、true
     */
    public void setAutoCommit(boolean isAuto);
}
