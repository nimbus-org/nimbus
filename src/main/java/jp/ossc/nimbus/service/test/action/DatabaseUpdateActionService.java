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
package jp.ossc.nimbus.service.test.action;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;

/**
 * データベースを更新するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class DatabaseUpdateActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, DatabaseUpdateActionServiceMBean{
    
    private static final long serialVersionUID = -685726604591449818L;
    
    protected ServiceName connectionFactoryServiceName;
    protected ConnectionFactory connectionFactory;
    protected ServiceName persistentManagerServiceName;
    protected PersistentManager persistentManager;
    protected boolean isBatchExecute;
    protected int batchExecuteCount;
    protected double expectedCost = 0d;

    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setPersistentManagerServiceName(ServiceName name){
        persistentManagerServiceName = name;
    }
    public ServiceName getPersistentManagerServiceName(){
        return persistentManagerServiceName;
    }
    
    public void setBatchExecute(boolean isBatch){
        isBatchExecute = isBatch;
    }
    public boolean isBatchExecute(){
        return isBatchExecute;
    }
    
    public void setBatchExecuteCount(int count){
        batchExecuteCount = count;
    }
    public int getBatchExecuteCount(){
        return batchExecuteCount;
    }
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    public void setPersistentManager(PersistentManager manager){
        persistentManager = manager;
    }
    
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(persistentManagerServiceName != null){
            persistentManager = (PersistentManager)ServiceManagerFactory.getServiceObject(persistentManagerServiceName);
        }
        if(persistentManager == null){
            throw new IllegalArgumentException("PersistentManager is null.");
        }
    }
    
    /**
     * リソースの内容を読み込んで、データベースに更新クエリを発行する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * inputId
     * updateQueries
     * </pre>
     * inputIdは、{@link PersistentManager}に渡す更新クエリに対する引数オブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。空行を指定した場合は、引数オブジェクトはnull。<br>
     * updateQueriesは、{@link PersistentManager}に渡す更新クエリを指定する。複数のクエリを指定する場合は、"/"のみの行を区切り行として指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 更新件数
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、データベースに更新クエリを発行する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * inputId
     * updateQueries
     * </pre>
     * inputIdは、{@link PersistentManager}に渡す更新クエリに対する引数オブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。空行を指定した場合は、preResultを使用する。<br>
     * updateQueriesは、{@link PersistentManager}に渡す更新クエリを指定する。複数のクエリを指定する場合は、"/"のみの行を区切り行として指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 更新クエリに対する引数オブジェクト
     * @param resource リソース
     * @return 更新件数
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        String[] updateQueries = null;
        try{
            final String inputId = br.readLine();
            if(inputId == null){
                throw new Exception("Unexpected EOF on inputId");
            }
            if(inputId.length() != 0){
                Object actionResult = null;
                if(inputId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(inputId);
                }else{
                    String[] ids = inputId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal inputId format. id=" + inputId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + inputId);
                }
                preResult = actionResult;
            }
            String line = br.readLine();
            if(line == null){
                throw new Exception("Unexpected EOF on updateQueries");
            }
            final List queries = new ArrayList();
            final StringBuilder buf = new StringBuilder();
            do{
                if("/".equals(line)){
                    String query = buf.toString();
                    buf.setLength(0);
                    query = query.trim();
                    if(query.length() != 0){
                        queries.add(query);
                    }
                }else{
                    line = line.trim();
                    if(line.length() != 0){
                        if(buf.length() != 0){
                            buf.append(' ');
                        }
                        buf.append(line);
                    }
                }
            }while((line = br.readLine()) != null);
            if(buf.length() != 0){
                String query = buf.toString();
                buf.setLength(0);
                query = query.trim();
                if(query.length() != 0){
                    queries.add(query);
                }
            }
            if(queries.size() == 0){
                throw new Exception("Unexpected EOF on updateQueries");
            }
            updateQueries = (String[])queries.toArray(new String[queries.size()]);
        }finally{
            br.close();
            br = null;
        }
        Connection con = connectionFactory.getConnection();
        int updateCount = 0;
        try{
            for(int i = 0; i < updateQueries.length; i++){
                final String updateQuery = updateQueries[i];
                if(preResult == null){
                    updateCount += persistentManager.persistQuery(con, updateQuery, null);
                }else{
                    boolean isArray = (preResult instanceof Collection) || preResult.getClass().isArray();
                    if(isBatchExecute && isArray){
                        Object[] array = null;
                        if(preResult instanceof Collection){
                            array = ((Collection)preResult).toArray();
                        }else{
                            array = (Object[])preResult;
                        }
                        PersistentManager.BatchExecutor be = persistentManager.createQueryBatchExecutor(con, updateQuery);
                        if(batchExecuteCount > 0){
                            be.setAutoBatchPersistCount(batchExecuteCount);
                        }
                        for(int j = 0; j < array.length; j++){
                            updateCount += be.addBatch(array[j]);
                        }
                        updateCount += be.persist();
                    }else{
                        updateCount += persistentManager.persistQuery(con, updateQuery, preResult);
                    }
                }
            }
        }finally{
            con.close();
        }
        return new Integer(updateCount);
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
