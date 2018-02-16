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

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.general.Dataset;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;

/**
 * データベースデータセットファクトリサービス。<p>
 *
 * @author k2-taniguchi
 */
public abstract class DatabaseDatasetFactoryService
    extends ServiceBase
    implements DatabaseDatasetFactoryServiceMBean, DatasetFactory, java.io.Serializable {
    
    private static final long serialVersionUID = -1040225706936424053L;
    
    // 定数
    /** デフォルトフェッチサイズ */
    public static final int DEFAULT_FETCH_SIZE = 10000;
    /** セパレータ [=] */
    private static final String SEPARATOR = "=";

    /** データセット名 */
    private String name;
    /** コネクションファクトリ */
    private ConnectionFactory connFactory;
    /** [シリーズ名=SQL]の文字列配列 */
    private String[] sqls;
    /** データセット条件のリスト */
    private List dsConditionList;
    /** キーにシリーズ名、値にSQLのマップ */
    private Map seriesSqlMap;
    /** フェッチサイズ */
    private int fetchSize = DEFAULT_FETCH_SIZE;

    // ServiceBaseのJavaDoc
    public void createService() throws Exception {
        dsConditionList = new ArrayList();
        seriesSqlMap = new LinkedHashMap();
    }

    // ServiceBaseのJavaDoc
    public void startService() throws Exception {
        if (name == null || name.length() == 0) {
            // サービス定義で設定されなかった場合
            name = getServiceName();
        }

        if (connFactory == null) {
            throw new IllegalArgumentException(
                "ConnectionFactory is null."
            );
        }

        if (sqls == null || sqls.length == 0) {
            throw new IllegalArgumentException(
                "sqls must be specified."
            );
        }

        for (int i = 0; i < sqls.length; i++) {
            String seriesSql = sqls[i];

            int index = seriesSql.indexOf(SEPARATOR);
            if (index == -1) {
                throw new IllegalArgumentException("sqls is invalid." + seriesSql);
            }

            String seriesName = seriesSql.substring(0, index);
            String sql = seriesSql.substring(index + 1);
            // キーにシリーズ名, 値にSQL
            seriesSqlMap.put(seriesName, sql);
        }
    }

    // ServiceBaseのJavaDoc
    public void stopService() throws Exception {
        dsConditionList.clear();
        seriesSqlMap.clear();
    }

    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception {
        dsConditionList = null;
        seriesSqlMap = null;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public void setName(String name) {
        this.name = name;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public String getName() {
        return this.name;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public void setConnectionFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public ConnectionFactory getConnectionFactory() {
        return connFactory;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public void setSqls(String[] sqls) {
        this.sqls = sqls;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public String[] getSqls() {
        return sqls;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public void setFetchSize(int size) {
        fetchSize = size;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public int getFetchSize() {
        return fetchSize;
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public void addDatasetCondition(DatasetCondition dsCondition) {
        dsConditionList.add(dsCondition);
    }

    // DatabaseDatasetFactoryServiceMBeanのJavaDoc
    public DatasetCondition[] getDatasetConditions() {
        return (DatasetCondition[]) dsConditionList.toArray(
                    new DatasetCondition[dsConditionList.size()]
                );
    }

    // DatasetFactoryのJavaDoc
    public Dataset createDataset(DatasetCondition[] dsConditions)
        throws DatasetCreateException {

        // コネクションを取得
        Connection conn = null;
        try {
            conn = connFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            // コネクション取得失敗
            throw new DatasetCreateException("Dataset [" + name + "]", e);
        }

        DatasetCondition[] conditions = null;
        if (dsConditions != null && dsConditions.length > 0) {
            // 引数のデータセット条件を設定
            conditions = dsConditions;
        }
        if (conditions == null && dsConditionList.size() > 0) {
            // サービス定義で設定されたデータセット条件を設定
            conditions =
                (DatasetCondition[]) dsConditionList.toArray(new DatasetCondition[dsConditionList.size()]);
        }

        Dataset dataset = null;
        // キーにシリーズ名、値にResultSet
        Map seriesRsMap = new LinkedHashMap();

        // すべてのPreparedStatementに適用するデータセット条件
        List allConditions = new ArrayList();
        // シリーズ名にマッピングされたデータセット条件
        Map conditionMap = new HashMap();

        if (conditions != null && conditions.length > 0) {
            // 自分と同じデータセット名のデータセット条件を検索
            for (int i = 0; i < conditions.length; i++) {
                DatasetCondition dsCondition = conditions[i];

                if (dsCondition instanceof DatabaseDatasetCondition
                    && name.equals(dsCondition.getName())
                ) {
                    String seriesName = conditions[i].getSeriesName();
                    if (seriesName == null) {
                        /*
                         * シリーズ名がないデータセット条件は
                         * すべてに適用するデータセット条件
                         */
                        allConditions.add((DatabaseDatasetCondition) dsCondition);
                    } else {
                        if (conditionMap.containsKey(seriesName)) {
                            List list = (List) conditionMap.get(seriesName);
                            list.add(dsCondition);
                        } else {
                            List list = new ArrayList();
                            list.add(dsCondition);
                            // キーにシリーズ名、値にデータセット条件のリスト
                            conditionMap.put(seriesName, list);
                        }
                    }
                }
            }
        }

        try {
            Iterator itr = seriesSqlMap.keySet().iterator();
            while (itr.hasNext()) {
                // シリーズ
                String series = (String) itr.next();
                PreparedStatement pstmt =
                    conn.prepareStatement(
                        (String) seriesSqlMap.get(series),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY 
                    );
                
                pstmt.setFetchSize(fetchSize);
                pstmt.setFetchDirection(ResultSet.FETCH_FORWARD);

                if (allConditions.size() > 0) {
                    /*
                     * シリーズ名なしのデータセット条件を
                     * すべてのPreparedStatementに適用
                     */
                    for (int i = 0; i < allConditions.size(); i++) {
                        DatabaseDatasetCondition condition = (DatabaseDatasetCondition) allConditions.get(i);
                        setObject(pstmt, condition);
                    }
                } else if (conditionMap.containsKey(series)) {
                    // 各シリーズ用のデータセット条件をPreparedStatementに適用
                    List list = (List) conditionMap.get(series);
                    for (int i = 0; i < list.size(); i++) {
                        DatabaseDatasetCondition condition =(DatabaseDatasetCondition) list.get(i);
                        setObject(pstmt, condition);
                    }
                }
                // SQL実行
                ResultSet rs = pstmt.executeQuery();
                seriesRsMap.put(series, rs);
            }

            // シリーズ名の配列
            String[] series = null;
            // 検索結果の配列
            ResultSet[] rSets = null;

            if (seriesRsMap.size() > 0) {
                series =
                    (String[]) seriesRsMap.keySet().toArray(new String[seriesRsMap.size()]);
                rSets =
                    (ResultSet[]) seriesRsMap.values().toArray(new ResultSet[seriesRsMap.size()]);
            }

            // データセットを作る
            dataset = createDataset(dsConditions, series, rSets);
        } catch (SQLException e) {
            // データベース関連
            throw new DatasetCreateException("Dataset [" + name + "]", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

        return dataset;
    }

    /**
     * PreparedStatementに指定されたデータベース条件から値を設定する。<p>
     *
     * @param pstmt PreparedStatement
     * @param dbDsCondition データベースデータセット条件
     * @exception DatasetCreateException
     * @exception SQLException
     */
    private void setObject(
        PreparedStatement pstmt,
        DatabaseDatasetCondition dbDsCondition
    ) throws DatasetCreateException, SQLException {
        // パラメータメタデータ
        ParameterMetaData paramMetaData = pstmt.getParameterMetaData();
        if (paramMetaData == null) {
            throw new DatasetCreateException(
                "ParameterMetaData is null."
            );
        }

        // パラメータカウント
        int paramCnt = paramMetaData.getParameterCount();

        // 値をPreparedStatementに設定
        if (paramCnt > 0) {
            for (int k = 0; k < paramCnt; k++) {
                Object paramObj = dbDsCondition.getParamObject(k);
                if (paramObj != null) {
                    pstmt.setObject(k + 1, paramObj);
                }
            }
        }
    }

    /**
     * データセットを作成する。<p>
     *
     * @param dsConditions データセット条件
     * @param seriesArray シリーズ名の配列
     * @param rSets ResultSetの配列
     * @return データセット
     * @exception DatasetCreateException
     */
    abstract protected Dataset createDataset(
        DatasetCondition[] dsConditions,
        String[] seriesArray,
        ResultSet[] rSets
    ) throws DatasetCreateException;

}
