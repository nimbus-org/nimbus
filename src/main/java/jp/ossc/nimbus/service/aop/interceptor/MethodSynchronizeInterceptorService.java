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

/**
 * メソッド同期インターセプタ。<p>
 * メソッドの呼び出しに対して、VM単位、クラス単位、メソッド単位、インスタンス単位に同期を取るインターセプタである。<br>
 * 以下に、インスタンス単位で同期するインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodSynchronizeInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService"&gt;
 *             &lt;attribute name="Scope"&gt;INSTANCE&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MethodSynchronizeInterceptorService extends ServiceBase
 implements Interceptor, MethodSynchronizeInterceptorServiceMBean{
    
    private static final long serialVersionUID = -356175995913939992L;
    
    private static final int SCOPE_VALUE_VM = 1;
    private static final int SCOPE_VALUE_CLASS = 2;
    private static final int SCOPE_VALUE_METHOD = 3;
    private static final int SCOPE_VALUE_INSTANCE = 4;
    
    private String synchronizeScope;
    private int scopeValue = SCOPE_VALUE_INSTANCE;
    
    private Map methods;
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        methods = new HashMap();
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        methods.clear();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        methods = null;
    }
    
    // MethodSynchronizeInterceptorServiceMBeanのJavaDoc
    public void setScope(String scope) throws IllegalArgumentException{
        if(SCOPE_VM.equals(scope)){
            scopeValue = SCOPE_VALUE_VM;
        }else if(SCOPE_CLASS.equals(scope)){
            scopeValue = SCOPE_VALUE_CLASS;
        }else if(SCOPE_METHOD.equals(scope)){
            scopeValue = SCOPE_VALUE_METHOD;
        }else if(SCOPE_INSTANCE.equals(scope)){
            scopeValue = SCOPE_VALUE_INSTANCE;
        }else{
            throw new IllegalArgumentException(
                "Illegal synchronize scope : " + scope
            );
        }
        synchronizeScope = scope;
    }
    // MethodSynchronizeInterceptorServiceMBeanのJavaDoc
    public String getScope(){
        return synchronizeScope;
    }
    
    /**
     * {@link #setScope(String)}で指定されたスコープに対して同期して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、同期を行わずに次のインターセプタを呼び出す。<br>
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
        final MethodInvocationContext ctx = (MethodInvocationContext)context;
        if(getState() == STARTED){
            Object target = null;
            switch(scopeValue){
            case SCOPE_VALUE_VM:
                target = getClass();
                break;
            case SCOPE_VALUE_CLASS:
                target = Class.forName(
                    ctx.getTargetMethod().getDeclaringClass().getName(),
                    true,
                    Thread.currentThread().getContextClassLoader()
                );
                break;
            case SCOPE_VALUE_METHOD:
                if(!methods.containsKey(ctx.getTargetMethod())){
                    target = ctx.getTargetMethod();
                    methods.put(target, target);
                }else{
                    target = methods.get(ctx.getTargetMethod());
                }
                break;
            case SCOPE_VALUE_INSTANCE:
                target = ctx.getTargetObject();
                if(target == null){
                    if(!methods.containsKey(ctx.getTargetMethod())){
                        target = ctx.getTargetMethod();
                        methods.put(target, target);
                    }else{
                        target = methods.get(ctx.getTargetMethod());
                    }
                }
                break;
            default:
            }
            synchronized(target){
                return chain.invokeNext(context);
            }
        }else{
            return chain.invokeNext(context);
        }
    }
}
