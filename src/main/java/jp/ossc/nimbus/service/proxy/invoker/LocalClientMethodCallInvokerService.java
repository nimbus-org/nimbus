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
package jp.ossc.nimbus.service.proxy.invoker;

import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.performance.ResourceUsage;

/**
 * サービスメソッドリフレクション呼び出しインボーカ。<p>
 * リフレクションAPIを使って、指定されたサービスのメソッド呼び出しを行う。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SampleInvoker"
 *                  code="jp.ossc.nimbus.service.remote.invoker.LocalClientMethodCallInvokerService"&gt;
 *             &lt;attribute name="LocalServiceName"&gt;#SampleService&lt;/attribute&gt;
 *             &lt;depends&gt;SampleService&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="SampleService"
 *                  code="sample.SampleService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class LocalClientMethodCallInvokerService extends ServiceBase
 implements LocalClientMethodCallInvokerServiceMBean, KeepAliveCheckInvoker, java.io.Serializable{
    
    private static final long serialVersionUID = -1650041776482188614L;
    
    private ServiceName localServiceName;
    private Object localService;
    
    private ServiceName resourceUsageServiceName;
    private ResourceUsage resourceUsage;
    
    // LocalClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setLocalServiceName(ServiceName name){
        localServiceName = name;
    }
    // LocalClientMethodCallInvokerServiceMBeanのJavaDoc
    public ServiceName getLocalServiceName(){
        return localServiceName;
    }
    
    // LocalClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setResourceUsageServiceName(ServiceName name){
        resourceUsageServiceName = name;
    }
    // LocalClientMethodCallInvokerServiceMBeanのJavaDoc
    public ServiceName getResourceUsageServiceName(){
        return resourceUsageServiceName;
    }
    
    public void setResourceUsage(ResourceUsage usage){
        resourceUsage = usage;
    }
    
    /**
     * 呼び出し対象となるサービスを設定する。<p>
     *
     * @param localService 呼び出し対象となるサービス
     */
    public void setLocalService(Object localService) {
        this.localService = localService;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(resourceUsage == null && resourceUsageServiceName != null){
            resourceUsage = (ResourceUsage)ServiceManagerFactory
                .getServiceObject(resourceUsageServiceName);
        }
    }
    
    /**
     * リフレクションAPIを使って、メソッド呼び出しを行う。<p>
     * 呼び出しコンテキストの{@link InvocationContext#getTargetObject()}で取得したサービス名のサービスをローカルの{@link ServiceManager}から取得して、リフレクションAPIでメソッド呼び出しを行う。<br>
     * InvocationContext.getTargetObject()でサービス名が取得できない場合は、{@link #setLocalServiceName(ServiceName)}で設定されたサービス名のサービスを取得して、メソッド呼び出しを行う。<br>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final MethodInvocationContext methodContext
             = (MethodInvocationContext)context;
        
        ServiceName targetName = localServiceName;
        if(targetName == null){
            targetName = (ServiceName)context.getTargetObject();
        }
        if(targetName != null) {
            localService = ServiceManagerFactory.getServiceObject(targetName);
        }
        if(localService == null) {
            throw new IllegalArgumentException(
                "LocalServiceName or LocalService must be specified."
            );
        }
        
        try{
            return methodContext.getTargetMethod().invoke(
                localService,
                methodContext.getParameters()
            );
        }catch(InvocationTargetException e){
            throw e.getTargetException();
        }
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public boolean isAlive(){
        if(getState() != Service.STARTED){
            return false;
        }
        if(localServiceName != null){
            try{
                Service service = ServiceManagerFactory.getService(localServiceName);
                return service.getState() == Service.STARTED;
            }catch(ServiceNotFoundException e){
                return false;
            }
        }else{
            return true;
        }
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
