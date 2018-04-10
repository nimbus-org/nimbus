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
package jp.ossc.nimbus.service.test.evaluate;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.EvaluateTestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * サービス定義をロードするテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class ServiceLoadActionService extends ServiceBase implements EvaluateTestAction, TestActionEstimation, ServiceLoadActionServiceMBean{
    
    protected double expectedCost = Double.NaN;
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
    
    /**
     * サービス定義をロードして、戻り値を返す。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * loadType
     * checkLoadManagerCompleted
     * serviceDefinitionPath
     * </pre>
     * loadTypeは、ロードの場合、LOAD。アンロードの場合、UNLOADを指定する。<br>
     * checkLoadManagerCompletedは、サービス定義をロードした後、{@link ServiceManagerFactory#checkLoadManagerCompleted}を呼び出すかを指定する。呼び出す場合、true。loadTypeがUNLOADの場合は、この行を指定する必要はない。<br>
     * serviceDefinitionPathは、サービス定義ファイルのパスを指定する。複数のサービス定義ファイルをロードまたはアンロードする場合は、改行して指定する。ロードの場合は、指定された順でロードする。アンロードの場合は、指定された順と逆順でアンロードする。<br>
     *
     * @param context テストコンテキスト
     * @param actionId このテストアクションのID
     * @param resource このテストアクションへのリソース
     * @return 実行結果の評価。成功した場合、true
     * @exception Exception テストアクションの実行で例外が発生した場合
     */
    public boolean execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        String loadType = null;
        boolean isCheckLoadManagerCompleted = false;
        List definitionPathList = new ArrayList();
        try{
            loadType = br.readLine();
            if(loadType == null){
                throw new Exception("Unexpected EOF on loadType");
            }else if(!"LOAD".equals(loadType) && !"UNLOAD".equals(loadType)){
                throw new Exception("Unknown loadType : " + loadType);
            }
            if("LOAD".equals(loadType)){
                final String checkLoadManagerCompleted = br.readLine();
                if(checkLoadManagerCompleted == null){
                    throw new Exception("Unexpected EOF on checkLoadManagerCompleted");
                }
                isCheckLoadManagerCompleted = Boolean.valueOf(checkLoadManagerCompleted).booleanValue();
            }
            String serviceDefinitionPath = null;
            while((serviceDefinitionPath = br.readLine()) != null){
                File file = new File(serviceDefinitionPath);
                if(file.exists()){
                    definitionPathList.add(file.getPath());
                }else{
                    file = new File(context.getCurrentDirectory(), serviceDefinitionPath);
                    if(file.exists()){
                        definitionPathList.add(file.getPath());
                    }else{
                        definitionPathList.add(serviceDefinitionPath);
                    }
                }
            }
            if(definitionPathList.size() == 0){
                throw new Exception("Unexpected EOF on serviceDefinitionPath");
            }
        }finally{
            br.close();
            br = null;
        }
        boolean result = true;
        if("LOAD".equals(loadType)){
            for(int i = 0; i < definitionPathList.size(); i++){
                result &= ServiceManagerFactory.loadManager((String)definitionPathList.get(i));
            }
            if(isCheckLoadManagerCompleted){
                result &= ServiceManagerFactory.checkLoadManagerCompleted();
            }
        }else{
            for(int i = definitionPathList.size(); --i >= 0;){
                result &= ServiceManagerFactory.unloadManager((String)definitionPathList.get(i));
            }
        }
        return result;
    }
}