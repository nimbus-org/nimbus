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

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.sequence.*;
import jp.ossc.nimbus.service.codemaster.*;

/**
 * スレッドコンテキスト初期化インターセプタ。<p>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ThreadContextInitializeInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ThreadContextInitializeInterceptorService"&gt;
 *             &lt;attribute name="ThreadContextServiceName"&gt;#ThreadContext&lt;/attribute&gt;
 *             &lt;depends&gt;ThreadContext&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="ThreadContext"
 *                  code="jp.ossc.nimbus.service.context.ThreadContextService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class ThreadContextInitializeInterceptorService extends ServiceBase
 implements Interceptor, ThreadContextInitializeInterceptorServiceMBean{
    
    private static final long serialVersionUID = 7085572561373638170L;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    protected ServiceName sequenceServiceName;
    protected ServiceName codeMasterFinderServiceName;
    protected ServiceNameRef[] contextValueServiceNames;
    protected Properties contextValueInvocationContext;
    protected Map contextKeyPropMapping;
    protected Map contextValueMapping;
    
    protected boolean isOutputThreadName = true;
    protected boolean isOutputThreadGroupName = true;
    protected boolean isInitializeRecursiveCall = true;
    protected boolean isClear = true;
    
    protected ThreadLocal callStack;
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setCodeMasterFinderServiceName(ServiceName name){
        codeMasterFinderServiceName = name;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public ServiceName getCodeMasterFinderServiceName(){
        return codeMasterFinderServiceName;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setContextValueServiceNames(ServiceNameRef[] names){
        contextValueServiceNames = names;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public ServiceNameRef[] getContextValueServiceNames(){
        return contextValueServiceNames;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setContextValueInvocationContext(Properties map){
        contextValueInvocationContext = map;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public Properties getContextValueInvocationContext(){
        return contextValueInvocationContext;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setOutputThreadName(boolean isOutput){
        isOutputThreadName = isOutput;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isOutputThreadName(){
        return isOutputThreadName;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setOutputThreadGroupName(boolean isOutput){
        isOutputThreadGroupName = isOutput;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isOutputThreadGroupName(){
        return isOutputThreadGroupName;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isInitializeRecursiveCall(){
        return isInitializeRecursiveCall;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setInitializeRecursiveCall(boolean isInitialize){
        isInitializeRecursiveCall = isInitialize;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setContextValueMapping(Map mapping){
        contextValueMapping = mapping;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public Map getContextValueMapping(){
        return contextValueMapping;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setContextValue(String key, Object value){
        if(contextValueMapping == null){
            contextValueMapping = new HashMap();
        }
        contextValueMapping.put(key, value);
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public Object getContextValue(String key){
        if(contextValueMapping == null){
            return null;
        }
        return contextValueMapping.get(key);
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isClear(){
        return isClear;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setClear(boolean isClear){
        this.isClear = isClear;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(threadContextServiceName == null){
            throw new IllegalArgumentException(
                "threadContextServiceName must be specified."
            );
        }
        threadContext = (Context)ServiceManagerFactory
            .getServiceObject(threadContextServiceName);
        if(!isInitializeRecursiveCall){
            callStack = new ThreadLocal(){
                protected Object initialValue(){
                    return new CallStack();
                }
            };
        }
        if(contextValueInvocationContext != null){
            if(contextKeyPropMapping == null){
                contextKeyPropMapping = new HashMap();
            }
            final Iterator keys = contextValueInvocationContext.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                final String propStr
                     = contextValueInvocationContext.getProperty(key);
                final Property prop = PropertyFactory.createProperty(propStr);
                contextKeyPropMapping.put(key, prop);
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止に失敗した場合
     */
    public void stopService() throws Exception{
        callStack = null;
        if(contextKeyPropMapping != null){
            contextKeyPropMapping.clear();
        }
    }
    
    /**
     * スレッドコンテキストを初期化して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
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
        if(getState() == STARTED
            && (callStack == null
                 || ((CallStack)callStack.get()).stackIndex == 0)){
            
            if(isClear){
                threadContext.clear();
            }
            Sequence sequence = null;
            if(sequenceServiceName != null){
                try{
                    sequence = (Sequence)ServiceManagerFactory
                        .getServiceObject(sequenceServiceName);
                }catch(ServiceNotFoundException e){
                }
            }
            
            if(sequence != null){
                final String requestId = sequence.increment();
                threadContext.put(ThreadContextKey.REQUEST_ID, requestId);
            }
            
            if(contextValueServiceNames != null){
                for(int i = 0; i < contextValueServiceNames.length; i++){
                    threadContext.put(
                        contextValueServiceNames[i]
                            .getReferenceServiceName(),
                        ServiceManagerFactory.getServiceObject(
                            contextValueServiceNames[i].getServiceName()
                        )
                    );
                }
            }
            if(contextKeyPropMapping != null
                 && contextKeyPropMapping.size() != 0){
                final Iterator keys = contextKeyPropMapping.keySet().iterator();
                while(keys.hasNext()){
                    final String key = (String)keys.next();
                    final Property prop
                         = (Property)contextKeyPropMapping.get(key);
                    threadContext.put(
                        key,
                        prop.getProperty(context)
                    );
                }
            }
            if(contextValueMapping != null
                && contextValueMapping.size() != 0){
                final Iterator keys = contextValueMapping.keySet().iterator();
                while(keys.hasNext()){
                    final String key = (String)keys.next();
                    threadContext.put(
                        key,
                        contextValueMapping.get(key)
                    );
                }
            }
            
            final Thread thread = Thread.currentThread();
            if(isOutputThreadName){
                threadContext.put(
                    ThreadContextKey.THREAD_NAME,
                    thread.getName()
                );
            }
            if(isOutputThreadGroupName){
                final ThreadGroup threadGroup = thread.getThreadGroup();
                threadContext.put(
                    ThreadContextKey.THREAD_GROUP_NAME,
                    threadGroup.getName()
                );
            }
            CodeMasterFinder codeMasterFinder = null;
            if(codeMasterFinderServiceName != null){
                try{
                    codeMasterFinder = (CodeMasterFinder)ServiceManagerFactory
                        .getServiceObject(codeMasterFinderServiceName);
                }catch(ServiceNotFoundException e){
                }
            }
            if(codeMasterFinder != null){
                final Map codeMasters = codeMasterFinder.getCodeMasters();
                threadContext.put(ThreadContextKey.CODEMASTER, codeMasters);
            }
        }
        try{
            if(callStack != null){
                ((CallStack)callStack.get()).stackIndex++;
            }
            return chain.invokeNext(context);
        }finally{
            if(callStack != null){
                ((CallStack)callStack.get()).stackIndex--;
                if(isClear && ((CallStack)callStack.get()).stackIndex == 0){
                    threadContext.clear();
                }
            }else{
                if(isClear){
                    threadContext.clear();
                }
            }
        }
    }
    
    protected static class CallStack{
        public int stackIndex;
    }
}
