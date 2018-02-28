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
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.queue.*;

/**
 * メソッド非同期呼び出しインターセプタ。<p>
 * メソッドの呼び出しに対する処理を非同期にするインターセプタである。<br>
 * このインターセプタの非同期呼び出しには、3種類の非同期呼び出しがある。<br>
 * １つめは、戻り値を必要としない非同期呼び出し。この場合は、戻り値は必ずnullを返す。<br>
 * 以下に、その場合の非同期呼び出しインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodAsynchronousInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodAsynchronousInterceptorService/"&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * ２つめは、任意の時間だけ非同期呼び出しの応答を待つ非同期呼び出し。時間内に応答が返ってくれば戻り値または例外を返し、時間内に応答が返ってこなければnullを返す。但し、{@link #setFailToWaitResponseTimeout(boolean) setFailToWaitResponseTimeout(true)}に設定すると、{@link AsynchronousTimeoutException}をthrowする。<br>
 * 以下に、その場合の非同期呼び出しインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodAsynchronousInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodAsynchronousInterceptorService"&gt;
 *             &lt;attribute name="ResponseTimeout"&gt;1000&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * ３つめは、メソッドの戻り値やthrowされた例外を任意のタイミングで取得する非同期呼び出し。この場合は、レスポンスを格納する{@link Queue}サービスをこのサービスの属性に設定し、そのQueueサービスから戻り値やthrowされた例外を格納した{@link AsynchronousResponse}を取得できる。<br>
 * 以下に、その場合の非同期呼び出しインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodAsynchronousInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodAsynchronousInterceptorService"&gt;
 *             &lt;attribute name="ResponseQueueServiceName"&gt;#ResponseQueue&lt;/attribute&gt;
 *             &lt;depends&gt;ResponseQueue&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="ResponseQueue"
 *                  code="jp.ossc.nimbus.service.queue.DefaultQueueService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see Queue
 */
public class MethodAsynchronousInterceptorService extends ServiceBase
 implements Interceptor, MethodAsynchronousInterceptorServiceMBean{
    
    private static final long serialVersionUID = 556687756097723606L;
    
    private ServiceName requestQueueServiceName;
    private DefaultQueueService defaultRequestQueue;
    private Queue requestQueue;
    private ServiceName responseQueueServiceName;
    private Queue responseQueue;
    private Daemon[] daemons;
    private Invoker[] invokers;
    private long responseTimeout = -1;
    private boolean isFailToWaitResponseTimeout = true;
    private boolean isReturnResponse = true;
    private int invokerThreadSize = 1;
    private boolean isInvokerThreadDaemon = true;
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setRequestQueueServiceName(ServiceName name){
        requestQueueServiceName = name;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public ServiceName getRequestQueueServiceName(){
        return requestQueueServiceName;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setResponseQueueServiceName(ServiceName name){
        responseQueueServiceName = name;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public ServiceName getResponseQueueServiceName(){
        return responseQueueServiceName;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setResponseTimeout(long timeout){
        responseTimeout = timeout;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public long getResponseTimeout(){
        return responseTimeout;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setFailToWaitResponseTimeout(boolean isThrow){
        isFailToWaitResponseTimeout = isThrow;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public boolean isFailToWaitResponseTimeout(){
        return isFailToWaitResponseTimeout;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setInvokerThreadSize(int size){
        invokerThreadSize = size;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public int getInvokerThreadSize(){
        return invokerThreadSize;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setInvokerThreadDaemon(boolean isDaemon){
        isInvokerThreadDaemon = isDaemon;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public boolean isInvokerThreadDaemon(){
        return isInvokerThreadDaemon;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public int getActiveInvokerThreadSize(){
        if(invokers == null){
            return 0;
        }
        int count = 0;
        for(int i = 0; i < invokers.length; i++){
            if(invokers[i].isActive){
                count++;
            }
        }
        return count;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public void setReturnResponse(boolean isReturn){
        isReturnResponse = isReturn;
    }
    
    // MethodAsynchronousInterceptorServiceMBeanのJavaDoc
    public boolean isReturnResponse(){
        return isReturnResponse;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception Queueサービスの取得に失敗した場合
     */
    public void startService() throws Exception{
        if(getRequestQueueServiceName() == null){
            if(getRequestQueueService() == null) {
                if(getDefaultRequestQueueService() == null){
                    final DefaultQueueService defaultQueue
                         = new DefaultQueueService();
                    defaultQueue.create();
                    defaultQueue.start();
                    setDefaultRequestQueueService(defaultQueue);
                }else{
                    getDefaultRequestQueueService().start();
                }
                setRequestQueueService(getDefaultRequestQueueService());
            }
        }else{
            setRequestQueueService((Queue)ServiceManagerFactory
                .getServiceObject(requestQueueServiceName)
            );
        }
        
        // キュー受付開始
        getRequestQueueService().accept();
        
        // デーモン起動
        if(invokerThreadSize < 0){
            throw new IllegalArgumentException("invokerThreadSize < 0.");
        }
        invokers = new Invoker[invokerThreadSize];
        daemons = new Daemon[invokerThreadSize];
        for(int i = 0; i < invokerThreadSize; i++){
            invokers[i] = new Invoker();
            daemons[i] = new Daemon(invokers[i]);
            daemons[i].setName("Nimbus AsynchInvokerDaemon " + getServiceNameObject());
            daemons[i].setDaemon(isInvokerThreadDaemon);
            daemons[i].start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        // デーモン停止
        for(int i = 0; i < daemons.length; i++){
            daemons[i].stop();
            daemons[i] = null;
            invokers[i] = null;
        }
        
        // キュー受付停止
        getRequestQueueService().release();
        
        daemons = null;
        invokers = null;
        
        if(getRequestQueueService() == getDefaultRequestQueueService()){
            getDefaultRequestQueueService().stop();
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService(){
        
        if(getRequestQueueService() == getDefaultRequestQueueService()
            && getDefaultRequestQueueService() != null){
            getDefaultRequestQueueService().destroy();
            setDefaultRequestQueueService(null);
        }
    }
    
    /**
     * 非同期呼び出しをして、nullを返す。<p>
     * 本来のメソッド呼び出しの戻りは、{@link #setResponseQueueServiceName(ServiceName)}で{@link Queue}サービスが設定されていれば、そのQueueに、{@link AsynchronousResponse}としてキューされているので、そこから取得できる。<br>
     * サービスが開始されていない場合は、非同期呼び出しを行わずに次のインターセプタを呼び出す。<br>
     * 
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return nullを返す
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            InterceptorChain ch = chain;
            if(chain instanceof DefaultThreadLocalInterceptorChain){
                DefaultInterceptorChain tmp = new DefaultInterceptorChain(
                    chain.getInterceptorChainList(),
                    chain.getInvoker()
                );
                tmp.setCurrentInterceptorIndex(
                    chain.getCurrentInterceptorIndex()
                );
                ch = tmp;
            }
            final int currentInterceptorIndex = ch.getCurrentInterceptorIndex();
            final InterceptorChain cloneCh = ch.cloneChain();
            cloneCh.setCurrentInterceptorIndex(currentInterceptorIndex);
            final Queue resQueue = getResponseQueue();
            final InvocationInfo invokeInfo = new InvocationInfo(
                (MethodInvocationContext)context,
                cloneCh,
                resQueue
            );
            getRequestQueueService().push(invokeInfo);
            
            if(resQueue != null && isReturnResponse){
                AsynchronousResponse response = null;
                if(responseTimeout > 0){
                    response = (AsynchronousResponse)resQueue.get(responseTimeout);
                }else{
                    response = (AsynchronousResponse)resQueue.get();
                }
                
                if(response == null){
                    invokeInfo.isTimeout = true;
                    if(isFailToWaitResponseTimeout){
                        throw new AsynchronousTimeoutException();
                    }
                }else{
                    if(response.isThrownException()){
                        throw response.getThrownException();
                    }else{
                        return response.getReturnObject();
                    }
                }
            }
            return null;
        }else{
            return chain.invokeNext(context);
        }
    }
    
    /**
     * 呼び出しを非同期にするための{@link Queue}サービスを設定する。<p>
     *
     * @param queue Queueサービス
     */
    public void setRequestQueueService(Queue queue){
        this.requestQueue = queue;
    }
    
    /**
     * 呼び出しを非同期にするための{@link Queue}サービスを取得する。<p>
     *
     * @return Queueサービス
     */
    protected Queue getRequestQueueService(){
        return requestQueue;
    }
    
    /**
     * 呼び出しを非同期にするためのデフォルトの{@link Queue}サービスを取得する。<p>
     *
     * @return デフォルトのQueueサービス
     */
    protected DefaultQueueService getDefaultRequestQueueService(){
        return defaultRequestQueue;
    }
    
    /**
     * 呼び出しを非同期にするためのデフォルトの{@link Queue}サービスを設定する。<p>
     *
     * @param queue デフォルトのQueueサービス
     */
    protected void setDefaultRequestQueueService(DefaultQueueService queue){
        defaultRequestQueue = queue;
    }
    
    /**
     * 非同期呼び出しの戻りを格納するための{@link Queue}サービスを設定する。<p>
     *
     * @param queue Queueサービス
     */
    public void setResponseQueue(Queue queue){
        this.responseQueue = queue;
    }
    
    /**
     * 非同期呼び出しの戻りを格納する{ための@link Queue}サービスを取得する。<p>
     *
     * @return Queueサービス
     */
    protected Queue getResponseQueue(){
        if(responseQueue != null){
            return responseQueue;
        }
        if(getResponseQueueServiceName() != null){
            return(Queue)ServiceManagerFactory
                .getServiceObject(getResponseQueueServiceName());
        }
        if(responseTimeout > 0){
            final DefaultQueueService tmpQueue = new DefaultQueueService();
            try{
                tmpQueue.create();
                tmpQueue.start();
            }catch(Exception e){
                // 発生しないはず
            }
            return tmpQueue;
        }
        return null;
    }
    
    protected class Invoker implements DaemonRunnable{
        
        /**
         * 実行中かどうかを示すフラグ。<p>
         */
        public boolean isActive;
        
        /**
         * デーモンが開始した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onStart() {
            return true;
        }
        
        /**
         * デーモンが停止した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onStop() {
            return true;
        }
        
        /**
         * デーモンが中断した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onSuspend() {
            return true;
        }
        
        /**
         * デーモンが再開した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onResume() {
            return true;
        }
        
        /**
         * キューから１つ取り出して返す。<p>
         * 
         * @param ctrl DaemonControlオブジェクト
         * @return 呼び出し情報を格納した{@link MethodAsynchronousInterceptorService.InvocationInfo}オブジェクト
         */
        public Object provide(DaemonControl ctrl){
            return getRequestQueueService().get();
        }
        
        /**
         * 引数dequeuedで渡された{@link MethodAsynchronousInterceptorService.InvocationInfo}オブジェクトを使って、次のインターセプタを呼び出す。<p>
         * 呼び出しの戻り（戻り値またはthrowされた例外）は、{@link #setResponseQueueServiceName(ServiceName)}で{@link Queue}サービスが設定されていれば、そのQueueに、{@link AsynchronousResponse}として詰める。<br>
         *
         * @param dequeued キューから取り出されたオブジェクト
         * @param ctrl DaemonControlオブジェクト
         */
        public void consume(Object dequeued, DaemonControl ctrl){
            if(dequeued == null){
                return;
            }
            try{
                isActive = true;
                final InvocationInfo info = (InvocationInfo)dequeued;
                boolean throwException = false;
                Object ret = null;
                try{
                    ret = info.chain.invokeNext(info.context);
                }catch(Throwable e){
                    ret = e;
                    throwException = true;
                }
                final Queue resQueue = info.responseQueue;
                if(resQueue != null){
                    AsynchronousResponse response = null;
                    if(throwException){
                        final Class[] exceptionTypes
                             = info.context.getTargetMethod().getExceptionTypes();
                        boolean isThrowable = false;
                        if(RuntimeException.class.isInstance(ret)
                            || Error.class.isInstance(ret)
                        ){
                            isThrowable = true;
                        }else{
                            for(int i = 0; i < exceptionTypes.length; i++){
                                if(exceptionTypes[i].isInstance(ret)){
                                    isThrowable = true;
                                    break;
                                }
                            }
                        }
                        if(isThrowable){
                            response = new AsynchronousResponse(
                                info.context,
                                ret,
                                true
                            );
                        }
                    }else{
                        response = new AsynchronousResponse(info.context, ret);
                    }
                    if(!info.isTimeout && response != null){
                        resQueue.push(response);
                    }
                }
            }finally{
                isActive = false;
            }
        }
        
        /**
         * キューの中身を吐き出す。<p>
         */
        public void garbage(){
            if(getRequestQueueService() != null){
                while(getRequestQueueService().size() > 0){
                    consume(getRequestQueueService().get(0), null);
                }
            }
        }
    }
    
    /**
     * 呼び出し情報。<p>
     *
     * @author M.Takata
     */
    protected static class InvocationInfo implements java.io.Serializable{
        
        private static final long serialVersionUID = 7784186054966609415L;
        
        /**
         * {@link Interceptor}のメソッド呼び出し情報。<p>
         */
        public MethodInvocationContext context;
        
        /**
         * インターセプタのチェーン。<p>
         */
        public InterceptorChain chain;
        
        /**
         * レスポンス待ちをしてタイムアウトした場合
         */
        public volatile boolean isTimeout;
        
        /**
         * 応答Queue。<p>
         */
        public Queue responseQueue;
        
        /**
         * 呼び出し情報を生成する。<p>
         *
         * @param context {@link Interceptor}のメソッド呼び出し情報
         * @param chain インターセプタのチェーン
         * @param resQueue 応答Queue
         */
        public InvocationInfo(
            MethodInvocationContext context,
            InterceptorChain chain,
            Queue resQueue
        ){
            this.context = context;
            this.chain = chain;
            responseQueue = resQueue;
        }
    }
}
