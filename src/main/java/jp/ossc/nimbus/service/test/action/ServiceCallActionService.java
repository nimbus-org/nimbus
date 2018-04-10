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

import java.beans.PropertyEditor;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jp.ossc.nimbus.beans.MethodEditor;
import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;

/**
 * ローカルのサービスを呼び出すテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class ServiceCallActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, ServiceCallActionServiceMBean{
    
    private static final long serialVersionUID = 9132878277934081894L;
    
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    
    protected ServiceName invokerServiceName;
    protected Invoker invoker;
    
    protected double expectedCost = Double.NaN;
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    public ServiceName getInvokerServiceName(){
        return invokerServiceName;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void setInvoker(Invoker invoker){
        this.invoker = invoker;
    }
    
    public void startService() throws Exception{
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        if(invokerServiceName != null){
            invoker = (Invoker)ServiceManagerFactory.getServiceObject(invokerServiceName);
        }
    }
    
    /**
     * ローカルのサービスを呼び出して、戻り値を返す。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * serviceName
     * methodSigniture
     * argumentsType
     * arguments
     * </pre>
     * serviceNameは、呼び出す対象のサービス名を指定する。サービス名のフォーマットは、{@link ServiceNameEditor}の仕様に準じる。<br>
     * methodSignitureは、呼び出すメソッドのシグニチャを指定する。シグニチャのフォーマットは、{@link MethodEditor}の仕様に準じる。<br>
     * argumentTypeは、呼び出すメソッドの引数の指定方法で、"id"、"value"、"interpreter"のいずれかを指定する。<br>
     * argumentは、argumentTypeによって、記述方法が異なる。<br>
     * <ul>
     * <li>argumentTypeが"id"の場合<br>TestActionの戻り値を引数として使用するもので、同一テストケース中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。</li>
     * <li>argumentTypeが"value"の場合<br>引数を文字列で指定する。引数が複数存在する場合は、改行する。引数がnullである事を指定する場合は、"null"と指定する。</li>
     * <li>argumentTypeが"interpreter"の場合<br>引数を生成するスクリプト文字列を記述する。スクリプト文字列は、{@link Interpreter#evaluate(String,java.util.Map)}で評価され、その戻り値が引数として使用される。スクリプト内では、変数"context"で、TestContextが参照できる。スクリプトの終了は、空行。</li>
     * </ul>
     * 引数が複数ある場合は、argumentType、argumentを繰り返す。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return サービスを呼び出した戻り値
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    /**
     * ローカルのサービスを呼び出して、戻り値を返す。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * serviceName
     * methodSigniture
     * argumentType
     * argument
     * </pre>
     * serviceNameは、呼び出す対象のサービス名を指定する。サービス名のフォーマットは、{@link ServiceNameEditor}の仕様に準じる。<br>
     * methodSignitureは、呼び出すメソッドのシグニチャを指定する。シグニチャのフォーマットは、{@link MethodEditor}の仕様に準じる。<br>
     * argumentTypeは、呼び出すメソッドの引数の指定方法で、"id"、"value"、"chain"、"interpreter"のいずれかを指定する。<br>
     * argumentは、argumentTypeによって、記述方法が異なる。<br>
     * <ul>
     * <li>argumentTypeが"id"の場合<br>TestActionの戻り値を引数として使用するもので、同一テストケース中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、引数オブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。</li>
     * <li>argumentTypeが"value"の場合<br>引数を文字列で指定する。引数が複数存在する場合は、改行する。引数がnullである事を指定する場合は、"null"と指定する。</li>
     * <li>argumentTypeが"chain"の場合<br>{@link ChainTestAction$TestActionProcess TestActionProcess}として呼び出され、前アクションから引数を受け取る事を意味する。この場合argumentの行は指定する必要がない。</li>
     * <li>argumentTypeが"interpreter"の場合<br>引数を生成するスクリプト文字列を記述する。スクリプト文字列は、{@link Interpreter#evaluate(String,java.util.Map)}で評価され、その戻り値が引数として使用される。スクリプト内では、変数"context"で、TestContextが参照できる。スクリプトの終了は、空行。</li>
     * </ul>
     * 引数が複数ある場合は、argumentType、argumentを繰り返す。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 1つ前のアクションの戻り値
     * @param resource リソース
     * @return サービスを呼び出した戻り値
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        ServiceName targetServiceName = null;
        Method method = null;
        Object[] arguments = null;
        try{
            final String serviceNameStr = br.readLine();
            if(serviceNameStr == null || serviceNameStr.length() == 0){
                throw new Exception("Unexpected EOF on serviceName");
            }
            final ServiceNameEditor serviceNameEditor = new ServiceNameEditor();
            serviceNameEditor.setAsText(serviceNameStr);
            targetServiceName = (ServiceName)serviceNameEditor.getValue();
            
            final String methodSigniture = br.readLine();
            if(methodSigniture == null || methodSigniture.length() == 0){
                throw new Exception("Unexpected EOF on methodSigniture");
            }
            final MethodEditor methodEditor = new MethodEditor();
            methodEditor.setAsText(methodSigniture);
            method = (Method)methodEditor.getValue();
            final Class[] paramTypes = method.getParameterTypes();
            if(paramTypes.length != 0){
                arguments = paramTypes == null || paramTypes.length == 0 ? null : new Object[paramTypes.length];
                
                String argumentType = null;
                int index = 0;
                while((argumentType = br.readLine()) != null){
                    if(argumentType.length() == 0){
                        continue;
                    }
                    if(index >= paramTypes.length){
                        throw new Exception("Unmatch argument length. signitureParamLength=" + paramTypes.length + ", argumentLength>" + index);
                    }
                    if("chain".equals(argumentType)){
                        arguments[index] = preResult;
                    }else if("id".equals(argumentType)){
                        String line = br.readLine();
                        if(line == null){
                            throw new Exception("Unexpected EOF on argument");
                        }
                        if(line != null && line.length() != 0){
                            if(line.indexOf(",") == -1){
                                arguments[index] = context.getTestActionResult(line);
                            }else{
                                String[] ids = line.split(",");
                                if(ids.length != 2){
                                    throw new Exception("Illegal argument id format. id=" + line);
                                }
                                arguments[index] = context.getTestActionResult(ids[0], ids[1]);
                            }
                        }
                    }else if("value".equals(argumentType)){
                        String line = br.readLine();
                        if(line == null){
                            throw new Exception("Unexpected EOF on argument");
                        }
                        PropertyEditor editor = NimbusPropertyEditorManager.findEditor(paramTypes[index]);
                        if(editor == null){
                            throw new Exception("PropertyEditor not found. type=" + paramTypes[index]);
                        }
                        try{
                            editor.setAsText(line);
                            arguments[index] = editor.getValue();
                        }catch(Exception e){
                            throw new Exception("PropertyEditor can not edit. editor=" + editor + ", value=" + line, e);
                        }
                    }else if("interpreter".equals(argumentType)){
                        if(interpreter == null){
                            throw new UnsupportedOperationException("Interpreter is null.");
                        }
                        String script = null;
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        String line = br.readLine();
                        if(line == null){
                            throw new Exception("Unexpected EOF on argument");
                        }
                        try{
                            do{
                                pw.println(line);
                            }while((line = br.readLine()) != null && line.length() != 0);
                            pw.flush();
                            script = sw.toString();
                        }finally{
                            pw.close();
                        }
                        if(paramTypes != null){
                            final Map params = new HashMap();
                            params.put("context", context);
                            arguments[index] = interpreter.evaluate(script, params);
                        }
                    }else{
                        throw new Exception("Unknown argumentType : " + argumentType);
                    }
                    index++;
                }
            }
        }finally{
            br.close();
            br = null;
        }
        if(invoker == null){
            return method.invoke(ServiceManagerFactory.getServiceObject(targetServiceName), arguments);
        }else{
            try{
                return invoker.invoke(
                    new DefaultMethodInvocationContext(
                        targetServiceName,
                        method,
                        arguments
                    )
                );
            }catch(Throwable th){
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }
        }
    }
}
