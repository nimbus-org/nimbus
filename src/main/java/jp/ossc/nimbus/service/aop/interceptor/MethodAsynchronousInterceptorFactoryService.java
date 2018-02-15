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

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link MethodAsynchronousInterceptorService}のファクトリサービス。<p>
 * 
 * @author M.Takata
 */
public class MethodAsynchronousInterceptorFactoryService
 extends ServiceFactoryServiceBase
 implements MethodAsynchronousInterceptorFactoryServiceMBean{
    
    private static final long serialVersionUID = -353556145704341553L;
    
    private final MethodAsynchronousInterceptorService template
         = new MethodAsynchronousInterceptorService();
    
    /**
     * {@link MethodAsynchronousInterceptorService}サービスを生成する。<p>
     *
     * @return MethodAsynchronousInterceptorServiceサービス
     * @exception Exception MethodAsynchronousInterceptorServiceの生成・起動に失敗した場合
     * @see MethodAsynchronousInterceptorService
     */
    protected Service createServiceInstance() throws Exception{
        final MethodAsynchronousInterceptorService interceptor
             = new MethodAsynchronousInterceptorService();
        interceptor.setRequestQueueServiceName(getRequestQueueServiceName());
        interceptor.setResponseQueueServiceName(
            getResponseQueueServiceName()
        );
        return interceptor;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setRequestQueueServiceName(ServiceName name){
        template.setRequestQueueServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final MethodAsynchronousInterceptorService interceptor
                 = (MethodAsynchronousInterceptorService)instances.next();
            interceptor.setRequestQueueServiceName(name);
        }
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public ServiceName getRequestQueueServiceName(){
        return template.getRequestQueueServiceName();
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setResponseQueueServiceName(ServiceName name){
        template.setResponseQueueServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final MethodAsynchronousInterceptorService interceptor
                 = (MethodAsynchronousInterceptorService)instances.next();
            interceptor.setResponseQueueServiceName(name);
        }
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public ServiceName getResponseQueueServiceName(){
        return template.getResponseQueueServiceName();
    }
}
