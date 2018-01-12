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
package jp.ossc.nimbus.service.aop.interceptor;

import java.lang.reflect.Method;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * モックインターセプタ。<p>
 * メソッドの呼び出しに対して、呼び出す対象のオブジェクトを{@link MockFactory}が生成するモック、または設定されたモックにすりかえるインターセプタである。
 * 以下に、モックインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MockInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MockInterceptorService"&gt;
 *             &lt;attribute name="Mock"&gt;
 *                 &lt;object code="sample.MockConnection"/&gt;
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MockInterceptorService extends ServiceBase
 implements Interceptor, MockInterceptorServiceMBean{
    
    private static final long serialVersionUID = 5565234776209550375L;
    
    private ServiceName mockFactoryServiceName;
    private MockFactory mockFactory;
    private ServiceName mockServiceName;
    private Object mock;
    
    // MockInterceptorServiceMBeanのJavaDoc
    public void setMockFactoryServiceName(ServiceName name){
        mockFactoryServiceName = name;
    }
    // MockInterceptorServiceMBeanのJavaDoc
    public ServiceName getMockFactoryServiceName(){
        return mockFactoryServiceName;
    }
    
    // MockInterceptorServiceMBeanのJavaDoc
    public void setMockServiceName(ServiceName name){
        mockServiceName = name;
    }
    // MockInterceptorServiceMBeanのJavaDoc
    public ServiceName getMockServiceName(){
        return mockServiceName;
    }
    
    /**
     * MockFactoryを設定する。<p>
     *
     * @param mockFactory MockFactory
     */
    public void setMockFactory(MockFactory mockFactory) {
        this.mockFactory = mockFactory;
    }
    
    /**
     * モックを設定する。<p>
     *
     * @param mock モック
     */
    public void setMock(Object mock) {
        this.mock = mock;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(mockFactoryServiceName != null){
            mockFactory = (MockFactory)ServiceManagerFactory
                .getServiceObject(mockFactoryServiceName);
        }else if(mockFactory == null
            && mockServiceName == null
            && mock == null
        ){
            throw new IllegalArgumentException("It is necessary to set any of mockFactory or mockFactoryServiceName or mockServiceName or mock.");
        }
    }
    
    /**
     * 設定された{@link MockFactory}を呼び出し対象のオブジェクトとすり替えて、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、すり替えを行わずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        final MethodInvocationContext ctx = (MethodInvocationContext)context;
        final Object target = ctx.getTargetObject();
        final Method targetMethod = ctx.getTargetMethod();
        try{
            if(getState() == STARTED){
                Object mock = null;
                if(mockFactory != null){
                    mock = mockFactory.createMock(ctx);
                }else if(this.mock != null){
                    mock = this.mock;
                }else if(mockServiceName != null){
                    try{
                        mock = ServiceManagerFactory
                            .getServiceObject(mockServiceName);
                    }catch(ServiceNotFoundException e){
                    }
                }
                if(mock != null){
                    Method mockMethod = null;
                    try{
                        mockMethod = mock.getClass().getMethod(
                            targetMethod.getName(),
                            targetMethod.getParameterTypes()
                        );
                        ctx.setTargetObject(mock);
                        ctx.setTargetMethod(mockMethod);
                    }catch(NoSuchMethodException e){
                        // TODO ログ出力
                    }
                }
            }
            return chain.invokeNext(context);
        }finally{
            if(getState() == STARTED){
                ctx.setTargetObject(target);
                ctx.setTargetMethod(targetMethod);
            }
        }
    }
}
