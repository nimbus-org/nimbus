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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * サーブレットフィルタインターセプタアダプタ。<p>
 * {@link Interceptor}に、{@link ServletFilterInterceptorService}の機能を付加するアダプタである。<br>
 *
 * @author M.Takata
 */
public class ServletFilterInterceptorAdapterService
 extends ServletFilterInterceptorService
 implements ServletFilterInterceptorAdapterServiceMBean{
    
    private static final long serialVersionUID = -3558138995073714122L;
    
    private ServiceName interceptorServiceName;
    private Interceptor interceptor;
    
    // ServletFilterInterceptorAdapterServiceMBeanのJavaDoc
    public void setInterceptorServiceName(ServiceName name){
        interceptorServiceName = name;
    }
    // ServletFilterInterceptorAdapterServiceMBeanのJavaDoc
    public ServiceName getInterceptorServiceName(){
        return interceptorServiceName;
    }
    
    public void startService() throws Exception{
        if(interceptor == null){
            if(interceptorServiceName == null){
                throw new IllegalArgumentException(
                    "interceptorServiceName must be specified."
                );
            }
            interceptor = (Interceptor)ServiceManagerFactory.getServiceObject(interceptorServiceName);
        }
    }
    
    public void stopService() throws Exception{
        if(interceptorServiceName != null){
            interceptor = null;
        }
    }
    
    public void setInterceptor(Interceptor val){
        interceptor = val;
    }
    
    /**
     * サーブレット呼び出しをインターセプトして、アダプタしているインターセプタを呼び出す。<p>
     *
     * @param context サーブレットフィルタ呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(interceptor != null){
            return interceptor.invoke(context, chain);
        }else{
            return chain.invokeNext(context);
        }
    }
}