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
package jp.ossc.nimbus.service.graph;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.service.connection.ConnectionFactory;

/**
 * {@link DatabaseDatasetFactoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface DatabaseDatasetFactoryServiceMBean
    extends ServiceBaseMBean {

    /**
     * コネクションファクトリを設定する。<p>
     *
     * @param connFactory コネクションファクトリ
     */
    public void setConnectionFactory(ConnectionFactory connFactory);

    /**
     * コネクションファクトリを取得する。<p>
     *
     * @return コネクションファクトリ
     */
    public ConnectionFactory getConnectionFactory();

    /**
     * データセットを作る際に必要なデータを取得するSQL文字列配列を設定する。<p>
     * [シリーズ名=SQL]という文字列で設定されています。
     *
     * @param sqls [シリーズ名=SQL]の配列
     */
    public void setSqls(String[] sqls);

    /**
     * データセットを作る際に必要なデータを取得するSQL文字列配列を設定する。<p>
     * [シリーズ名=SQL]という文字列で設定されています。
     *
     * @return [シリーズ名=SQL]の配列
     */
    public String[] getSqls();

    /**
     * データセット条件を追加する。<p>
     * 
     * @param dsCondition データセット条件
     */
    public void addDatasetCondition(DatasetCondition dsCondition);

    /**
     * データセット条件の配列を取得する。<p>
     * 
     * @return データセット条件の配列
     */
    public DatasetCondition[] getDatasetConditions();

    /**
     * フェッチサイズを設定する。<p>
     * 
     * @param size フェッチサイズ
     */
    public void setFetchSize(int size);
    
    /**
     * フェッチサイズを取得する。<p>
     * 
     * @return フェッチサイズ
     */
    public int getFetchSize();

}
