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

import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.ftp.FTPClientFactory;
import jp.ossc.nimbus.service.ftp.FTPClient;

/**
 * FTPでGET/PUT/DELETE/LSを行うテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class FTPActionService extends ServiceBase implements TestAction, TestActionEstimation, FTPActionServiceMBean{
    
    private static final long serialVersionUID = -782714823351233622L;
    protected double expectedCost = Double.NaN;
    
    protected ServiceName ftpClientFactoryServiceName;
    protected FTPClientFactory ftpClientFactory;
    
    public void setFTPClientFactoryServiceName(ServiceName name){
        ftpClientFactoryServiceName = name;
    }
    public ServiceName getFTPClientFactoryServiceName(){
        return ftpClientFactoryServiceName;
    }
    
    public void startService() throws Exception{
        if(ftpClientFactoryServiceName != null){
            ftpClientFactory = (FTPClientFactory)ServiceManagerFactory.getServiceObject(ftpClientFactoryServiceName);
        }
        if(ftpClientFactory == null){
            throw new IllegalArgumentException("FTPClientFactory is null.");
        }
    }
    
    /**
     * リソースの内容を読み込んで、FTPでGET/PUT/DELETE/LSを行う。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * actionType
     * filePath
     * </pre>
     * actionTypeは、GET、MGETまたはPUT、DELETE、MDELETE、LSを指定する。<br>
     * filePathは、GETまたはPUTするファイルのパスを指定する。GET、MGET、DELETE、MDELETE、LSの場合は、リモートのファイルパスを指定する。PUTの場合、ローカルのファイル名,リモートのファイルパス。複数指定する場合は、改行して指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return GET、MGETの場合、取得したファイルのリスト。PUT、DELETE、MDELETEの場合、null。LSの場合、パスのリスト。
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        String actionType = null;
        List pathList = new ArrayList();
        try{
            actionType = br.readLine();
            if(actionType == null ){
                throw new Exception("Unexpected EOF on actionType");
            }
            if(!"GET".equals(actionType)
                && !"MGET".equals(actionType)
                && !"PUT".equals(actionType)
                && !"DELETE".equals(actionType)
                && !"MDELETE".equals(actionType)
                && !"LS".equals(actionType)
            ){
                throw new Exception("Illegal actionType : " + actionType);
            }
            String filePath = null;
            while((filePath = br.readLine()) != null){
                if(filePath.length() != 0){
                    if("PUT".equals(actionType)){
                        String[] paths = filePath.split(",");
                        if(paths.length != 2){
                            throw new Exception("Illegal filePath : " + filePath);
                        }
                        pathList.add(paths);
                    }else{
                        pathList.add(filePath);
                    }
                }
            }
            if(pathList.size() == 0){
                throw new Exception("Unexpected EOF on filePath");
            }
        }finally{
            br.close();
            br = null;
        }
        List result = null;
        FTPClient client = ftpClientFactory.createFTPClient();
        try{
            client.lcd(context.getCurrentDirectory().getCanonicalPath());
            if("GET".equals(actionType)){
                result = new ArrayList();
                for(int i = 0; i < pathList.size(); i++){
                    result.add(client.get((String)pathList.get(i)));
                }
            }else if("MGET".equals(actionType)){
                result = new ArrayList();
                for(int i = 0; i < pathList.size(); i++){
                    File[] files = client.mget((String)pathList.get(i));
                    if(files != null){
                        for(int j = 0; j < files.length; j++){
                            result.add(files[j]);
                        }
                    }
                }
            }else if("DELETE".equals(actionType)){
                for(int i = 0; i < pathList.size(); i++){
                    String[] files = client.ls((String)pathList.get(i));
                    if(files != null && files.length != 0){
                        client.delete((String)pathList.get(i));
                    }
                }
            }else if("MDELETE".equals(actionType)){
                for(int i = 0; i < pathList.size(); i++){
                    client.mdelete((String)pathList.get(i));
                }
            }else if("LS".equals(actionType)){
                result = new ArrayList();
                for(int i = 0; i < pathList.size(); i++){
                    String[] paths = client.ls((String)pathList.get(i));
                    if(paths != null){
                        for(int j = 0; j < paths.length; j++){
                            result.add(paths[j]);
                        }
                    }
                }
            }else{
                for(int i = 0; i < pathList.size(); i++){
                    String[] paths = (String[])pathList.get(i);
                    client.put(paths[0], paths[1]);
                }
            }
        }finally{
            client.close();
        }
        return result;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
