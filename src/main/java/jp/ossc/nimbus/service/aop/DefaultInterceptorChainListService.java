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
package jp.ossc.nimbus.service.aop;

import jp.ossc.nimbus.core.*;

/**
 * {@link InterceptorChainList}のデフォルト実装サービス。<p>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SampleInterceptorChainList"
 *                  code="jp.ossc.nimbus.service.aop.DefaultInterceptorChainListService"&gt;
 *             &lt;attribute name="InterceptorServiceNames"&gt;
 *                 #MethodMetricsInterceptor
 *                 #MethodSynchronizeInterceptor
 *             &lt;/attribute&gt;
 *             &lt;depends&gt;MethodMetricsInterceptor&lt;/depends&gt;
 *             &lt;depends&gt;MethodSynchronizeInterceptor&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="MethodMetricsInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService"/&gt;
 *         
 *         &lt;service name="MethodSynchronizeInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see Interceptor
 */
public class DefaultInterceptorChainListService extends ServiceBase
 implements DefaultInterceptorChainListServiceMBean, InterceptorChainList,
            java.io.Serializable{
    
    private static final long serialVersionUID = 6878505419696424123L;
    
    private ServiceName[] interceptorServiceNames;
    private Interceptor[] interceptors;
    
    // DefaultInterceptorChainListServiceMBean
    public void setInterceptorServiceNames(ServiceName[] serviceNames){
        interceptorServiceNames = serviceNames;
    }
    
    // DefaultInterceptorChainListServiceMBean
    public ServiceName[] getInterceptorServiceNames(){
        return interceptorServiceNames;
    }
    
    /**
     * Interceptorを設定する。
     */
    public void setInterceptors(Interceptor[] interceptors) {
        this.interceptors = interceptors;
    }
    
    // InterceptorChainListのJavaDoc
    public Interceptor getInterceptor(InvocationContext context, int index){
        if(getState() != STARTED){
            return null;
        }
        
        if(interceptorServiceNames != null) {
            if(interceptorServiceNames.length > index){
                return (Interceptor)ServiceManagerFactory.getServiceObject(interceptorServiceNames[index]);
            }
        } else if (interceptors != null) {
            if(interceptors.length > index){
                return interceptors[index];
            }
        }
        
        return null;
    }
}
