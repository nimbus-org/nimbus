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
package jp.ossc.nimbus.service.ioccall.interceptor;

import java.util.Iterator;

import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;

/**
 * FacadeValueのヘッダ項目をスレッドコンテキストに設定するインターセプタ。<p>
 *
 * @author M.Takata
 */
public class ThreadContextImportInterceptorService extends ServiceBase
 implements ThreadContextImportInterceptorServiceMBean,
            jp.ossc.nimbus.service.aspect.interfaces.Interceptor,
            jp.ossc.nimbus.service.aop.Interceptor{
    
    private static final long serialVersionUID = -7763749050997388727L;
    
    private ServiceName threadContextServiceName;
    private String[] headerKeys;
    
    // ThreadContextImportInterceptorServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // ThreadContextImportInterceptorServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // ThreadContextImportInterceptorServiceMBeanのJavaDoc
    public void setHeaderKeys(String[] keys){
        headerKeys = keys;
    }
    // ThreadContextImportInterceptorServiceMBeanのJavaDoc
    public String[] getHeaderKeys(){
        return headerKeys;
    }
    
    public Object invokeChain(
        Object inputObj,
        jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain interceptChain
    ) throws InterceptorException, TargetCheckedException,
             TargetUncheckedException{
        try{
            return invokeInternal(inputObj, interceptChain, null);
        }catch(InterceptorException e){
            throw e;
        }catch(TargetCheckedException e){
            throw e;
        }catch(TargetUncheckedException e){
            throw e;
        }catch(Throwable th){
            throw new InterceptorException(th);
        }
    }
    
    public Object invoke(
        InvocationContext context,
        jp.ossc.nimbus.service.aop.InterceptorChain chain
    ) throws Throwable{
        return invokeInternal(context, null, chain);
    }
    
    
    protected Object invokeInternal(
        Object inputObj,
        jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain interceptChain,
        jp.ossc.nimbus.service.aop.InterceptorChain chain
    ) throws Throwable{
        Object input = inputObj;
        if(chain != null){
            input = ((MethodInvocationContext)input).getParameters()[0];
        }
        FacadeValue in = (FacadeValue)input;
        if(threadContextServiceName != null){
            final Context context = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
            if(headerKeys == null){
                final Iterator keys = in.getHederKeys().iterator();
                while(keys.hasNext()){
                    final String key = (String)keys.next();
                    context.put(key, in.getHeader(key));
                }
            }else{
                for(int i = 0; i < headerKeys.length; i++){
                    final String key = headerKeys[i];
                    context.put(key, in.getHeader(key));
                }
            }
        }
        if(interceptChain != null){
            return interceptChain.invokeChain(inputObj);
        }else{
            return chain.invokeNext((InvocationContext)inputObj);
        }
    }
}
