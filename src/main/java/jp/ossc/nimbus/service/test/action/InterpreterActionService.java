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

import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;
import java.io.Reader;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * インタプリタでスクリプトを実行するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class InterpreterActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, InterpreterActionServiceMBean{
    
    private static final long serialVersionUID = 6961467251188456361L;
    protected double expectedCost = Double.NaN;
    
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void startService() throws Exception{
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        if(interpreter == null){
            throw new IllegalArgumentException("Interpreter is null.");
        }
    }
    
    /**
     * リソースの内容を読み込んで、インタプリタでスクリプトを実行する。<p>
     * スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}が渡される。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return インタプリタの実行結果
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、インタプリタでスクリプトを実行する。<p>
     * スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}が、"preResult"で前アクションの結果が渡される。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 更新クエリの引数
     * @param resource リソース
     * @return インタプリタの実行結果
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        StringWriter sw = new StringWriter();
        String script = null;
        try{
            int len = 0;
            char[] buf = new char[1024];
            while((len = resource.read(buf, 0, buf.length)) > 0){
                sw.write(buf, 0, len);
            }
            script = sw.toString();
        }finally{
            resource.close();
        }
        final Map params = new HashMap();
        params.put("context", context);
        params.put("preResult", preResult);
        return interpreter.evaluate(script, params);
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
