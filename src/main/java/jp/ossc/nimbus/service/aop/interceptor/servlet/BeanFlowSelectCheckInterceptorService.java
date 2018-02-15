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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.servlet.*;

/**
 * BeanFlow存在チェックインターセプタ。<p>
 *
 * @author M.Takata
 */
public class BeanFlowSelectCheckInterceptorService extends ServletFilterInterceptorService
 implements BeanFlowSelectCheckInterceptorServiceMBean{
    
    private static final long serialVersionUID = 8739445270816532778L;
    
    protected ServiceName beanFlowSelectorServiceName;
    protected BeanFlowSelector beanFlowSelector;
    
    protected ServiceName beanFlowInvokerFactoryServiceName;
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    // BeanFlowSelectCheckInterceptorServiceMBeanのJavaDoc
    public void setBeanFlowSelectorServiceName(ServiceName name){
        beanFlowSelectorServiceName = name;
    }
    // BeanFlowSelectCheckInterceptorServiceMBeanのJavaDoc
    public ServiceName getBeanFlowSelectorServiceName(){
        return beanFlowSelectorServiceName;
    }
    
    // BeanFlowSelectCheckInterceptorServiceMBeanのJavaDoc
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    // BeanFlowSelectCheckInterceptorServiceMBeanのJavaDoc
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    public void setBeanFlowSelector(BeanFlowSelector selector){
        beanFlowSelector = selector;
    }
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(beanFlowSelectorServiceName != null){
            beanFlowSelector = (BeanFlowSelector)ServiceManagerFactory
                .getServiceObject(beanFlowSelectorServiceName);
        }
        if(beanFlowSelector == null){
            beanFlowSelector = new DefaultBeanFlowSelectorService();
            ((DefaultBeanFlowSelectorService)beanFlowSelector).create();
            ((DefaultBeanFlowSelectorService)beanFlowSelector).start();
        }
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowInvokerFactory is null.");
        }
    }
    
    /**
     * リクエスト情報から、そのリクエストを処理すべき業務フローを探して、みつからない場合は、HTTPステータス404を応答する。見つかった場合は、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            final HttpServletRequest request = (HttpServletRequest)context.getServletRequest();
            final String flowName = beanFlowSelector.selectBeanFlow(request);
            if(!beanFlowInvokerFactory.containsFlow(flowName)){
                final HttpServletResponse response = (HttpServletResponse)context.getServletResponse();
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }
        return chain.invokeNext(context);
    }
}