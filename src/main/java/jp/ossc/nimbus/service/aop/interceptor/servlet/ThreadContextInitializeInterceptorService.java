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

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.sequence.*;
import jp.ossc.nimbus.service.codemaster.*;

/**
 * スレッドコンテキスト初期化インターセプタ。<p>
 *
 * @author M.Takata
 */
public class ThreadContextInitializeInterceptorService
 extends ServletFilterInterceptorService
 implements ThreadContextInitializeInterceptorServiceMBean{
    
    private static final long serialVersionUID = -3154621046378825548L;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    protected ServiceName codeMasterFinderServiceName;
    protected CodeMasterFinder codeMasterFinder;
    protected ServiceNameRef[] contextValueServiceNames;
    protected Properties contextValueRequestParameter;
    protected Map contextValueMapping;
    
    protected boolean isOutputContextPath = true;
    protected boolean isOutputServletPath = true;
    protected boolean isOutputSessionID = true;
    protected boolean isNewSession = false;
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
    public void setContextValueRequestParameter(Properties map){
        contextValueRequestParameter = map;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public Properties getContextValueRequestParameter(){
        return contextValueRequestParameter;
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
    public void setOutputContextPath(boolean isOutput){
        isOutputContextPath = isOutput;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isOutputContextPath(){
        return isOutputContextPath;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setOutputServletPath(boolean isOutput){
        isOutputServletPath = isOutput;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isOutputServletPath(){
        return isOutputServletPath;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public void setOutputSessionID(boolean isOutput){
        isOutputSessionID = isOutput;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isOutputSessionID(){
        return isOutputSessionID;
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
    public void setNewSession(boolean isNew){
        isNewSession = isNew;
    }
    
    // ThreadContextInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isNewSession(){
        return isNewSession;
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
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(sequenceServiceName);
        }
        if(codeMasterFinderServiceName != null){
            codeMasterFinder = (CodeMasterFinder)ServiceManagerFactory
                .getServiceObject(codeMasterFinderServiceName);
        }
        if(!isInitializeRecursiveCall){
            callStack = new ThreadLocal(){
                protected Object initialValue(){
                    return new CallStack();
                }
            };
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止に失敗した場合
     */
    public void stopService() throws Exception{
        callStack = null;
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
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED
            && (callStack == null
                 || ((CallStack)callStack.get()).stackIndex == 0)){
            
            final ServletRequest request = context.getServletRequest();
            if(isClear){
                threadContext.clear();
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
            
            if(contextValueRequestParameter != null
                 && contextValueRequestParameter.size() != 0){
                final Iterator keys = contextValueRequestParameter.keySet().iterator();
                while(keys.hasNext()){
                    final String key = (String)keys.next();
                    final String parameterName
                         = contextValueRequestParameter.getProperty(key);
                    String[] params = request.getParameterValues(parameterName);
                    if(params != null){
                        if(params.length == 1){
                            threadContext.put(
                                key,
                                params[0]
                            );
                        }else{
                            threadContext.put(
                                key,
                                params
                            );
                        }
                    }
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
            
            if(request instanceof HttpServletRequest){
                final HttpServletRequest httpReq = (HttpServletRequest)request;
                
                if(isOutputContextPath){
                    threadContext.put(
                        ThreadContextKey.CONTEXT_PATH,
                        httpReq.getContextPath()
                    );
                }
                if(isOutputServletPath){
                    threadContext.put(
                        ThreadContextKey.SERVLET_PATH,
                        httpReq.getServletPath()
                    );
                }
                final HttpSession session = httpReq.getSession(isNewSession);
                if(isOutputSessionID){
                    if(session != null){
                        threadContext.put(
                            ThreadContextKey.SESSION_ID,
                            session.getId()
                        );
                    }
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
    
    /**
     * スレッドコンテキスト呼び出しスタック。<p>
     *
     * @author M.Takata
     */
    protected static class CallStack{
        
        /**
         * スレッドコンテキスト初期化インターセプタが入れ子で呼び出された場合に、その入れ子の深さを示すインデックス。<p>
         */
        public int stackIndex;
    }
}
