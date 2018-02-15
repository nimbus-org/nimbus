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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.semaphore.*;

/**
 * 流量制御インターセプタ。<p>
 * 呼び出しに対して、セマフォサービスを使って流量制御を行うインターセプタである。<br>
 * 以下に、３つまでしか同時にアクセスできないように流量制御するインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="FlowControlInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.FlowControlInterceptorService"&gt;
 *             &lt;attribute name="SemaphoreServiceName"&gt;#Semaphore&lt;/attribute&gt;
 *             &lt;depends&gt;Semaphore&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="Semaphore"
 *                  code="jp.ossc.nimbus.service.semaphore.DefaultSemaphoreService"&gt;
 *             &lt;attribute name="ResourceCapacity"&gt;3&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see Semaphore
 */
public class FlowControlInterceptorService extends ServiceBase
 implements Interceptor, FlowControlInterceptorServiceMBean{
    
    private static final long serialVersionUID = 519397295732596256L;
    
    private ServiceName semaphoreServiceName;
    private Semaphore semaphore;
    private long timeout = -1;
    private boolean isFailToObtainSemaphore = true;
    private int maxWaitingCount = -1;
    private long forceFreeTimeout = -1L;
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public void setSemaphoreServiceName(ServiceName name){
        semaphoreServiceName = name;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public ServiceName getSemaphoreServiceName(){
        return semaphoreServiceName;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public void setTimeout(long timeout){
        this.timeout = timeout;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public long getTimeout(){
        return timeout;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public void setMaxWaitingCount(int count){
        maxWaitingCount = count;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public int getMaxWaitingCount(){
        return maxWaitingCount;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public void setForceFreeTimeout(long timeout){
        forceFreeTimeout = timeout;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public long getForceFreeTimeout(){
        return forceFreeTimeout;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public void setFailToObtainSemaphore(boolean isThrow){
        isFailToObtainSemaphore = isThrow;
    }
    
    // FlowControlInterceptorServiceMBeanのJavaDoc
    public boolean isFailToObtainSemaphore(){
        return isFailToObtainSemaphore;
    }
    
    /**
     * Semaphoreを設定する。<p>
     *
     * @param semaphore Semaphore
     */
    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception {@link Semaphore}サービスの取得に失敗した場合
     */
    public void startService() throws Exception{
        if(semaphore == null && semaphoreServiceName != null){
            semaphore = (Semaphore)ServiceManagerFactory.getServiceObject(
                semaphoreServiceName
            );
        }
        if(semaphore != null){
            semaphore.accept();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(semaphore != null){
            semaphore.release();
        }
    }
    
    /**
     * 流量制御をして、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合、及び{@link Semaphore}サービスが指定されていない場合は、流量制御を行わずに次のインターセプタを呼び出す。<br>
     * 
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
        if(getState() == STARTED && semaphore != null){
            final boolean isSuccess = semaphore.getResource(
                timeout,
                maxWaitingCount,
                forceFreeTimeout
            );
            Thread.interrupted();
            if(!isSuccess){
                if(isFailToObtainSemaphore){
                    throw new FailToObtainSemaphoreException();
                }else{
                    return null;
                }
            }
            try{
                return chain.invokeNext(context);
            }finally{
                semaphore.freeResource();
            }
        }else{
            return chain.invokeNext(context);
        }
    }
}
