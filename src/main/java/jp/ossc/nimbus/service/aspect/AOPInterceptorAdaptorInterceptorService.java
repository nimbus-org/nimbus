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
package jp.ossc.nimbus.service.aspect;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainListService;
import jp.ossc.nimbus.service.aop.DefaultThreadLocalInterceptorChain;
import jp.ossc.nimbus.service.aspect.interfaces.*;

public class AOPInterceptorAdaptorInterceptorService extends ServiceBase
 implements Interceptor{
    
    private static final long serialVersionUID = 6602684073777142163L;
    
    private static final String CHAIN_ATTRIBUTE_NAME = "jp.ossc.nimbus.service.aspect.CHAIN";
    private ServiceName interceptorServiceName;
    private jp.ossc.nimbus.service.aop.InterceptorChain chain;
    private jp.ossc.nimbus.service.aop.Interceptor interceptor;
    
    public void setInterceptorServiceName(ServiceName name){
        interceptorServiceName = name;
    }
    public ServiceName getInterceptorServiceName(){
        return interceptorServiceName;
    }
    
    public void setInterceptor(jp.ossc.nimbus.service.aop.Interceptor interceptor){
        this.interceptor = interceptor;
    }
    public jp.ossc.nimbus.service.aop.Interceptor getInterceptor(){
        return interceptor;
    }
    
    public void startService() throws Exception{
        if(interceptorServiceName == null && interceptor == null){
            throw new IllegalArgumentException(
                "InterceptorServiceName or Interceptor must be specified."
            );
        }
        final DefaultInterceptorChainListService chainList
             = new DefaultInterceptorChainListService();
        if(interceptorServiceName != null){
            chainList.setInterceptorServiceNames(
                new ServiceName[]{interceptorServiceName}
            );
        }else if(interceptor != null){
            chainList.setInterceptors(
                new jp.ossc.nimbus.service.aop.Interceptor[]{interceptor}
            );
        }
        chainList.create();
        chainList.start();
        final Invoker invoker = new Invoker();
        chain = new DefaultThreadLocalInterceptorChain(chainList, invoker);
    }
    
    public Object invokeChain(
        Object inputObj,
        InterceptorChain interceptChain
    ) throws InterceptorException, TargetCheckedException,
             TargetUncheckedException{
        InterceptorChainInvokerAccessImpl invoker
             = (InterceptorChainInvokerAccessImpl)interceptChain;
        InvocationContext context = new DefaultMethodInvocationContext(
            invoker.mCallBackObject,
            invoker.mCallBackmethod,
            new Object[]{inputObj}
        );
        context.setAttribute(CHAIN_ATTRIBUTE_NAME, interceptChain);
        try{
            return chain.invokeNext(context);
        }catch(TargetUncheckedException e){
            throw e;
        }catch(RuntimeException e){
            throw e;
        }catch(TargetCheckedException e){
            throw e;
        }catch(InterceptorException e){
            throw e;
        }catch(Exception e){
            throw new InterceptorException(e);
        }catch(Throwable th){
            throw (Error)th;
        }
    }
    
    private static class Invoker implements jp.ossc.nimbus.service.aop.Invoker{
        
        public Object invoke(InvocationContext context) throws Throwable{
            final InterceptorChain interceptChain
                 = (InterceptorChain)context.getAttribute(CHAIN_ATTRIBUTE_NAME);
            final MethodInvocationContext methodContext
                 = (MethodInvocationContext)context;
            final Object[] params = methodContext.getParameters();
            return interceptChain.invokeChain(
                params == null || params.length == 0 ? null : params[0]
            );
        }
    }
}