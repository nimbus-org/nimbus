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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.TableCreatorService;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * TableCreatorを使用して、DBを操作するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Ishida
 */
public class TableCreatorOperateActionService extends ServiceBase implements TestAction, TestActionEstimation, TableCreatorOperateActionServiceMBean {
    
    private static final long serialVersionUID = 6961467251188456361L;
    protected ServiceName[] tableCreatorServiceNames;
    protected boolean isDropTable = false;
    protected boolean isCreateTable = false;
    protected boolean isRestoreTable = false;
    protected double expectedCost = Double.NaN;
    
    protected Map tableCreatorServices;
    
    public ServiceName[] getTableCreatorServiceNames() {
        return tableCreatorServiceNames;
    }
    
    public void setTableCreatorServiceNames(ServiceName[] serviceNames) {
        tableCreatorServiceNames = serviceNames;
    }
    
    public boolean isDropTable() {
        return isDropTable;
    }

    public void setDropTable(boolean isDrop) {
        isDropTable = isDrop;
    }

    public boolean isCreateTable() {
        return isCreateTable;
    }

    public void setCreateTable(boolean isCreate) {
        isCreateTable = isCreate;
    }

    public boolean isRestoreTable() {
        return isRestoreTable;
    }

    public void setRestoreTable(boolean isRestore) {
        isRestoreTable = isRestore;
    }

    public void createService() throws Exception {
        tableCreatorServices = new HashMap();
    }
    
    public void startService() throws Exception {
        if(tableCreatorServiceNames == null || tableCreatorServiceNames.length == 0){
            throw new IllegalArgumentException("TableCreatorServiceNames is null or empty.");
        }
        for(int i = 0; i < tableCreatorServiceNames.length; i++){
            TableCreatorService tableCreatorService = (TableCreatorService) ServiceManagerFactory.getServiceObject(tableCreatorServiceNames[i]);
            tableCreatorServices.put(tableCreatorService.getTableName(), tableCreatorService);
        }
    }
    
    public void destroyService() throws Exception {
        tableCreatorServices = null;
    }
    
    /**
     * リソースの内容を読み込んで、対象テーブルのTableCreatorを使用して、DBを操作する。<p>
     * 操作するテーブルを複数指定することが可能。<p>
     * isRestoreTableがtrueの場合、指定された順にテーブルをリストアする。<p>
     * isRestoreTableがfalseの場合、指定された順にテーブルをDELETE、DROPし、逆順でCREATE、INSERTする。テーブル間で依存があり、順序を意識する場合はなどはこちらを使用する。<p>
     * <p>
     * リソースのフォーマットは、以下。<br>
     * 初期化対象のテーブル名を改行して必要な分だけ記載する。
     * <pre>
     * Table1
     * Table2
     * Table3
     * ・・・
     * </pre>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return なし
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception {
        BufferedReader br = new BufferedReader(resource);
        List targetTableList = new ArrayList();
        try{
            String tableName = null;
            while ((tableName = br.readLine()) != null){
                targetTableList.add(tableName);
            }
        }finally{
            br.close();
            br = null;
        }
        if(isRestoreTable) {
            for(int i = 0; i < targetTableList.size(); i++){
                String tableName = (String) targetTableList.get(i);
                if(tableCreatorServices.containsKey(tableName)){
                    TableCreatorService tableCreatorService = (TableCreatorService) tableCreatorServices.get(tableName);
                    tableCreatorService.restoreRecords();
                }
            }
        } else {
            for(int i = 0; i < targetTableList.size(); i++){
                String tableName = (String) targetTableList.get(i);
                if(tableCreatorServices.containsKey(tableName)){
                    TableCreatorService tableCreatorService = (TableCreatorService) tableCreatorServices.get(tableName);
                    tableCreatorService.deleteRecords();
                    if(isDropTable) {
                        tableCreatorService.dropTable();
                    }
                }
            }
            for(int i = targetTableList.size() - 1; i >= 0; i--){
                String tableName = (String) targetTableList.get(i);
                if(tableCreatorServices.containsKey(tableName)){
                    TableCreatorService tableCreatorService = (TableCreatorService) tableCreatorServices.get(tableName);
                    if(isCreateTable) {
                        tableCreatorService.createTable();
                    }
                    tableCreatorService.insertRecords();
                }
            }
        }
        return null;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
