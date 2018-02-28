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
package jp.ossc.nimbus.service.aop.invoker;

import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker;
import jp.ossc.nimbus.service.performance.ResourceUsage;

/**
 * メソッドリフレクション呼び出しインボーカ。<p>
 * リフレクションAPIを使って、メソッド呼び出しを行う。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SampleInvoker"
 *                  code="jp.ossc.nimbus.service.aop.invoker.MethodReflectionCallInvokerService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MethodReflectionCallInvokerService extends ServiceBase
 implements KeepAliveCheckInvoker, java.io.Serializable, MethodReflectionCallInvokerServiceMBean{
    
    private static final long serialVersionUID = -2211871699281951334L;
    
    private ServiceName resourceUsageServiceName;
    private ResourceUsage resourceUsage;
    
    // MethodReflectionCallInvokerServiceMBeanのJavaDoc
    public void setResourceUsageServiceName(ServiceName name){
        resourceUsageServiceName = name;
    }
    // MethodReflectionCallInvokerServiceMBeanのJavaDoc
    public ServiceName getResourceUsageServiceName(){
        return resourceUsageServiceName;
    }
    
    public void setResourceUsage(ResourceUsage usage){
        resourceUsage = usage;
    }
    
    public void startService() throws Exception{
        if(resourceUsage == null && resourceUsageServiceName != null){
            resourceUsage = (ResourceUsage)ServiceManagerFactory
                .getServiceObject(resourceUsageServiceName);
        }
    }
    
    /**
     * リフレクションAPIを使って、メソッド呼び出しを行う。<p>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final MethodInvocationContext methodContext
             = (MethodInvocationContext)context;
        try{
            return methodContext.getTargetMethod().invoke(
                methodContext.getTargetObject(),
                methodContext.getParameters()
            );
        }catch(InvocationTargetException e){
            throw e.getTargetException();
        }
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public boolean isAlive(){
        return getState() == Service.STARTED;
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public void addKeepAliveListener(KeepAliveListener listener){
        throw new UnsupportedOperationException();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public void removeKeepAliveListener(KeepAliveListener listener){
        throw new UnsupportedOperationException();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public void clearKeepAliveListener(){
        throw new UnsupportedOperationException();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public Object getHostInfo() {
        try{
            return java.net.InetAddress.getLocalHost();
        }catch(java.net.UnknownHostException e){
            throw new UnsupportedOperationException(e);
        }
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public Comparable getResourceUsage(){
        return resourceUsage == null ? null : resourceUsage.getUsage();
    }
}
