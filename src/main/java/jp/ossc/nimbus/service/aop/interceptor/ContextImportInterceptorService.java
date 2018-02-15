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
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;

/**
 * {@link Context}インポートインターセプタ。<p>
 * メソッドの呼び出し時に{@link InvocationContext}の情報を{@link Context}に乗せるインターセプタである。<br>
 * 以下に、Contextインポートインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ContextImportInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ContextImportInterceptorService"&gt;
 *             &lt;attribute name="ContextServiceName"&gt;#Context&lt;/attribute&gt;
 *             &lt;depends&gt;#Context&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="Context"
 *                  code="jp.ossc.nimbus.service.context.ThreadContextService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see Context
 */
public class ContextImportInterceptorService extends ServiceBase
 implements Interceptor, ContextImportInterceptorServiceMBean{
    
    private static final long serialVersionUID = -4327285005307131340L;
    
    private ServiceName contextServiceName;
    private Context context;
    private String attributeName = DEFAULT_ATTRIBUTE_NAME;
    private String[] contextKeys;
    
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    private Context getContext() {
        if(contextServiceName != null) {
            context = (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
        }
        return context;
    }
    
    /**
     * Contextを設定する。
     */
    public void setContext(Context context) {
        this.context = context;
    }
    
    public void setAttributeName(String name){
        attributeName = name;
    }
    public String getAttributeName(){
        return attributeName;
    }
    
    public void setContextKeys(String[] keys){
        contextKeys = keys;
    }
    public String[] getContextKeys(){
        return contextKeys;
    }
    
    /**
     * {@link InvocationContext}の情報を{@link Context}に乗せて、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、次のインターセプタを呼び出す。<br>
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
        if(getState() == STARTED){
            Context ctx = getContext();
            if(ctx != null){
                final Map tmp = (Map)context.getAttribute(attributeName);
                if(tmp != null){
                    if(contextKeys == null){
                        ctx.putAll(tmp);
                    }else{
                        for(int i = 0; i < contextKeys.length; i++){
                            ctx.put(
                                contextKeys[i],
                                tmp.get(contextKeys[i])
                            );
                        }
                    }
                }
            }
        }
        return chain.invokeNext(context);
    }
}
