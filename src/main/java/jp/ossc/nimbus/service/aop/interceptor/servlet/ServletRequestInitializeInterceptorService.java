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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;

/**
 * リクエスト初期化インターセプタ。<p>
 *
 * @author M.Takata
 */
public class ServletRequestInitializeInterceptorService
 extends ServletFilterInterceptorService
 implements ServletRequestInitializeInterceptorServiceMBean{
    
    private static final long serialVersionUID = 2753369702347163943L;
    
    protected ServiceName contextServiceName;
    protected Context context;
    protected String[] contextKeys;
    protected Map attributes;
    protected ServiceNameRef[] requestAttributeServiceNames;
    protected boolean isThrowServiceNotFoundException = true;
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public void setContextKeys(String[] keys){
        contextKeys = keys;
    }
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public String[] getContextKeys(){
        return contextKeys;
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public void setRequestAttributeServiceNames(ServiceNameRef[] names){
        requestAttributeServiceNames = names;
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public ServiceNameRef[] getRequestAttributeServiceNames(){
        return requestAttributeServiceNames;
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public void setRequestAttributes(Map attrs){
        attributes.putAll(attrs);
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public Map getRequestAttributes(){
        return attributes;
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public void setRequestAttribute(String name, Object attr){
        attributes.put(name, attr);
    }
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public Object getRequestAttribute(String name){
        return attributes.get(name);
    }
    
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public boolean isThrowServiceNotFoundException(){
        return isThrowServiceNotFoundException;
    }
    // ServletRequestInitializeInterceptorServiceMBeanのJavaDoc
    public void setThrowServiceNotFoundException(boolean isThrow){
        isThrowServiceNotFoundException = isThrow;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception{
        attributes = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory
                .getServiceObject(contextServiceName);
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        attributes = null;
    }
    
    /**
     * リクエストを初期化して、次のインターセプタを呼び出す。<p>
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
        if(getState() == STARTED){
            
            final ServletRequest request = context.getServletRequest();
            if(context != null && contextKeys != null){
                for(int i = 0; i < contextKeys.length; i++){
                    request.setAttribute(
                        contextKeys[i],
                        this.context.get(contextKeys[i])
                    );
                }
            }
            if(attributes != null && attributes.size() != 0){
                final Iterator names = attributes.keySet().iterator();
                while(names.hasNext()){
                    final String name = (String)names.next();
                    request.setAttribute(
                        name,
                        attributes.get(name)
                    );
                }
            }
            if(requestAttributeServiceNames != null){
                for(int i = 0; i < requestAttributeServiceNames.length; i++){
                    try{
                        request.setAttribute(
                            requestAttributeServiceNames[i]
                                .getReferenceServiceName(),
                            ServiceManagerFactory.getServiceObject(
                                requestAttributeServiceNames[i].getServiceName()
                            )
                        );
                    }catch(ServiceNotFoundException e){
                        if(isThrowServiceNotFoundException){
                            throw e;
                        }
                    }
                }
            }
        }
        return chain.invokeNext(context);
    }
}
