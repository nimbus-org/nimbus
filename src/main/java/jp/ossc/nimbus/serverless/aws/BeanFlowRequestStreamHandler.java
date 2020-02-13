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
package jp.ossc.nimbus.serverless.aws;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * LambdaアプリケーションをBeanFlowで実装するためのリクエストストリームハンドラクラス。<p>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>環境変数名</th><th>値</th></tr>
 *   <tr><td>{@link #ENV_BEANFLOW_INVOKER_FACTORY_SERVICE_NAME}</td><td>{@link BeanFlowInvokerFactory}サービスのサービス名を指定する</td></tr>
 *   <tr><td>{@link #ENV_INPUT_FLOW_NAME}</td><td>入力フロー名を指定する。指定しない場合は、リクエスト処理のフロー名の前に"input/"を付けたフロー名</td></tr>
 *   <tr><td>{@link #ENV_VALIDATE_FLOW_NAME}</td><td>入力検証のフロー名を指定する。指定しない場合は、リクエスト処理のフロー名の前に"validate/"を付けたフロー名</td></tr>
 *   <tr><td>{@link #ENV_FLOW_NAME}</td><td>リクエスト処理のフロー名を指定する。指定しない場合は、関数名をフロー名とする</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public class BeanFlowRequestStreamHandler extends RequestStreamHandler<Object,Object>{
    
    protected static final String ENV_BEANFLOW_INVOKER_FACTORY_SERVICE_NAME = "BEANFLOW_INVOKER_FACTORY_SERVICE_NAME";
    protected static final String ENV_INPUT_FLOW_NAME = "INPUT_FLOW_NAME";
    protected static final String ENV_VALIDATE_FLOW_NAME = "VALIDATE_FLOW_NAME";
    protected static final String ENV_FLOW_NAME = "FLOW_NAME";
    
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    protected String inputFlowName;
    protected String validateFlowName;
    protected String flowName;
    
    /**
     * 環境変数から{@link BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    protected ServiceName getBeanFlowInvokerFactoryServiceName(){
        return getEnvServiceName(ENV_BEANFLOW_INVOKER_FACTORY_SERVICE_NAME);
    }
    
    protected String getFlowName(Context context){
        String flowName = getEnvString(ENV_FLOW_NAME);
        if(flowName == null){
            flowName = context.getFunctionName();
        }
        return flowName;
    }
    
    protected String getValidateFlowName(Context context){
        String flowName = getEnvString(ENV_VALIDATE_FLOW_NAME);
        if(flowName == null){
            flowName = "validate/" + getFlowName(context);
        }
        return flowName;
    }
    
    protected String getInputFlowName(Context context){
        String flowName = getEnvString(ENV_INPUT_FLOW_NAME);
        if(flowName == null){
            flowName = "input/" + getFlowName(context);
        }
        return flowName;
    }
    
    protected boolean init(Context context){
        boolean result = super.init(context);
        if(result){
            ServiceName beanFlowInvokerFactoryServiceName = getBeanFlowInvokerFactoryServiceName();
            if(beanFlowInvokerFactoryServiceName == null){
                ServiceManagerFactory.getLogger().write("ERROR", "BeanFlowInvokerFactory not found. name=" + beanFlowInvokerFactoryServiceName);
                result = false;
            }else{
                beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                    .getServiceObject(beanFlowInvokerFactoryServiceName);
                inputFlowName = getInputFlowName(context);
                validateFlowName = getValidateFlowName(context);
                flowName = getFlowName(context);
                if(!beanFlowInvokerFactory.containsFlow(flowName)){
                    ServiceManagerFactory.getLogger().write("ERROR", "BeanFlow not found. flowName=" + flowName);
                    result = false;
                }
            }
        }
        return result;
    }
    
    /**
     * 入力フローを実行して、その戻り値を要求コンテキストの入力として返す。<p>
     * 入力フローが存在しない場合は、nullを返す。<br>
     *
     * @param context 要求コンテキスト
     * @return 要求コンテキストの入力
     * @exception Throwable 入力変換処理で例外が発生した場合
     */
    protected Object processCreateInputObject(RequestContext<Object, Object> context) throws Throwable{
        if(beanFlowInvokerFactory.containsFlow(inputFlowName)){
            final BeanFlowInvoker inputFlow = beanFlowInvokerFactory.createFlow(inputFlowName);
            return inputFlow.invokeFlow(context);
        }else{
            return null;
        }
    }
    
    /**
     * 検証処理のフローを実行する。<p>
     *
     * @param context 要求コンテキスト
     * @return 検証が正しく行われた場合は、true。そうでない場合は、false
     * @exception Throwable 検証処理で例外が発生した場合
     */
    protected boolean processValidate(RequestContext<Object, Object> context) throws Throwable{
        if(beanFlowInvokerFactory.containsFlow(validateFlowName)){
            final BeanFlowInvoker validateFlow = beanFlowInvokerFactory.createFlow(validateFlowName);
            final Object ret = validateFlow.invokeFlow(context);
            boolean result = false;
            if(ret != null && ret instanceof Boolean){
                result = ((Boolean)ret).booleanValue();
            }
            return result;
        }else{
            return true;
        }
    }
    
    /**
     * リクエスト処理のフローを実行する。<p>
     *
     * @param context 要求コンテキスト
     * @exception Throwable リクエスト処理で例外が発生した場合
     */
    protected void processRequest(RequestContext<Object, Object> context) throws Throwable{
        final BeanFlowInvoker flow = beanFlowInvokerFactory.createFlow(flowName);
        final Object ret = flow.invokeFlow(context);
        if(context.getOutput() == null && ret != null){
            context.setOutput(ret);
        }
    }
}