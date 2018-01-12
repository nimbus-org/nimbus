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

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.Context;

/**
 * メソッドマッピングインターセプタ。<p>
 * メソッドの呼び出しに対して、任意のメソッド毎に異なるインターセプタを呼び出すインターセプタである。<br>
 * 以下に、メソッドマッピングインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodMappingInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodMappingInterceptorService"&gt;
 *             &lt;attribute name="TargetMethodMapping"&gt;
 *                 sample.Sample#hoge(int)=#NullReturnInterceptor
 *                 sample.Sample#fuga(int, java.lang.String)=#UnsupportedOperationExceptionTrowInterceptor
 *             &lt;/attribute&gt;
 *             &lt;depends&gt;NullReturnInterceptor&lt;/depends&gt;
 *             &lt;depends&gt;UnsupportedOperationExceptionTrowInterceptor&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="NullReturnInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.NullReturnInterceptorService"/&gt;
 *         
 *         &lt;service name="UnsupportedOperationExceptionTrowInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ExceptionThrowInterceptorService"&gt;
 *             &lt;attribute name="ExceptionClassName"&gt;java.lang.UnsupportedOperationException&lt;/attribute&gt;
 *             &lt;attribute name="Message"&gt;このメソッドは呼び出してはいけません。&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MethodMappingInterceptorService extends ServiceBase
 implements Interceptor, MethodMappingInterceptorServiceMBean{
    
    private static final long serialVersionUID = -4023805640790206233L;
    
    private Properties targetMethodMapping;
    private Map interceptorMapping;
    private Properties targetMethodReturnMapping;
    private Map contextMapping;
    private ServiceName contextServiceName;
    private Context context;
    
    // MethodMappingInterceptorServiceMBeanのJavaDoc
    public void setTargetMethodMapping(Properties mapping){
        targetMethodMapping = mapping;
    }
    // MethodMappingInterceptorServiceMBeanのJavaDoc
    public Properties getTargetMethodMapping(){
        return targetMethodMapping;
    }
    
    // MethodMappingInterceptorServiceMBeanのJavaDoc
    public void setTargetMethodReturnMapping(Properties mapping){
        targetMethodReturnMapping = mapping;
    }
    // MethodMappingInterceptorServiceMBeanのJavaDoc
    public Properties getTargetMethodReturnMapping(){
        return targetMethodReturnMapping;
    }
    
    // MethodMappingInterceptorServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    // MethodMappingInterceptorServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    public void createService() throws Exception{
        interceptorMapping = new HashMap();
        contextMapping = new HashMap();
    }
    
    /**
     * Contextを設定する。
     */
    public void setContext(Context context) {
        this.context = context;
    }
    
    public void startService() throws Exception{
        if(targetMethodMapping != null){
            final Iterator methods = targetMethodMapping.keySet().iterator();
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            while(methods.hasNext()){
                final String method = (String)methods.next();
                final MethodSignature sig = new MethodSignature(method);
                final String interceptorName = targetMethodMapping.getProperty(method);
                editor.setAsText(interceptorName);
                interceptorMapping.put(sig, editor.getValue());
            }
        }
        if(targetMethodReturnMapping != null){
            if(contextServiceName != null) {
                context = (Context)ServiceManagerFactory
                    .getServiceObject(contextServiceName);
            } else if(context != null) {
                throw new IllegalArgumentException(
                    "contextServiceName must be specified."
                );
            }
            
            final Iterator methods
                 = targetMethodReturnMapping.keySet().iterator();
            while(methods.hasNext()){
                final String method = (String)methods.next();
                final MethodSignature sig = new MethodSignature(method);
                final String ctxName
                     = targetMethodReturnMapping.getProperty(method);
                contextMapping.put(sig, ctxName);
            }
        }
    }
    
    public void stopService() throws Exception{
        interceptorMapping.clear();
        contextMapping.clear();
    }
    
    public void destroyService() throws Exception{
        interceptorMapping = null;
        contextMapping = null;
    }
    
    /**
     * メソッドの呼び出しに対して、マッピングされたインターセプタを呼び出す。<br>
     * 呼び出されたメソッドに対してマッピングされたインターセプタが見つからない場合は、次のインターセプタを呼び出す。<br>
     * サービスが開始されていない場合は、次のインターセプタを呼び出す。<br>
     *
     * @param ctx 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext ctx,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            final Method method
                 = ((MethodInvocationContext)ctx).getTargetMethod();
            if(interceptorMapping != null && interceptorMapping.size() != 0){
                ServiceName name = null;
                Iterator entries = interceptorMapping.entrySet().iterator();
                while(entries.hasNext()){
                    final Map.Entry entry = (Map.Entry)entries.next();
                    if(entry.getKey().equals(method)){
                        name = (ServiceName)entry.getValue();
                        break;
                    }
                }
                Interceptor interceptor = null;
                if(name != null){
                    try{
                        interceptor = (Interceptor)ServiceManagerFactory.getServiceObject(name);
                    }catch(ServiceNotFoundException e){
                    }
                }
                if(interceptor != null){
                    return interceptor.invoke(ctx, chain);
                }
            }
            
            if(contextMapping != null && contextMapping.size() != 0){
                Iterator entries = contextMapping.entrySet().iterator();
                while(entries.hasNext()){
                    final Map.Entry entry = (Map.Entry)entries.next();
                    if(entry.getKey().equals(method)){
                        final String key = (String)entry.getValue();
                        return context.get(key);
                    }
                }
            }
            
            return chain.invokeNext(ctx);
        }else{
            return chain.invokeNext(ctx);
        }
    }
    
    private static class MethodSignature implements java.io.Serializable{
        
        private static final long serialVersionUID = -4023805640790206233L;
        
        private String owner;
        private String methodName;
        private String[] paramTypes;
        private boolean isParamTypesCheck = true;
        
        public MethodSignature(String method) throws IllegalArgumentException{
            String tmp = method;
            int index = tmp.indexOf('#');
            if(index == -1 || index == 0 || index == tmp.length() - 1){
                throw new IllegalArgumentException("Invalid method : " + method);
            }
            owner = tmp.substring(0, index);
            tmp = tmp.substring(index + 1);
            index = tmp.indexOf('(');
            if(index == -1 || index == 0 || index == tmp.length() - 1){
                throw new IllegalArgumentException("Invalid method : " + method);
            }
            methodName = tmp.substring(0, index);
            tmp = tmp.substring(index + 1);
            index = tmp.indexOf(')');
            if(index == -1 || index != tmp.length() - 1){
                throw new IllegalArgumentException("Invalid method : " + method);
            }
            if(index == 0){
                paramTypes = new String[0];
            }else{
                tmp = tmp.substring(0, index);
                if(tmp.equals("*")){
                    isParamTypesCheck = false;
                }else{
                    final StringTokenizer tokens = new StringTokenizer(tmp, ",");
                    final List paramTypeList = new ArrayList();
                    while(tokens.hasMoreTokens()){
                        final String paramType = tokens.nextToken().trim();
                        if(paramType.length() == 0){
                            throw new IllegalArgumentException("Invalid method : " + method);
                        }
                        paramTypeList.add(paramType);
                    }
                    paramTypes = (String[])paramTypeList.toArray(new String[paramTypeList.size()]);
                }
            }
        }
        
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(o instanceof MethodSignature){
                final MethodSignature comp = (MethodSignature)o;
                if(!owner.equals(comp.owner)){
                    return false;
                }
                if(!methodName.equals(comp.methodName)){
                    return false;
                }
                if(isParamTypesCheck != comp.isParamTypesCheck){
                    return false;
                }
                if(paramTypes == comp.paramTypes){
                    return true;
                }
                if((paramTypes == null && comp.paramTypes != null)
                    || (paramTypes != null && comp.paramTypes == null)
                    || (paramTypes.length != comp.paramTypes.length)
                ){
                    return false;
                }
                for(int i = 0; i < paramTypes.length; i++){
                    if(!paramTypes[i].equals(comp.paramTypes[i])){
                        return false;
                    }
                }
                return true;
            }else if(o instanceof Method){
                final Method comp = (Method)o;
                if(!owner.equals(comp.getDeclaringClass().getName())
                    && !Pattern.matches(owner, comp.getDeclaringClass().getName())){
                    return false;
                }
                if(!methodName.equals(comp.getName())
                    && !Pattern.matches(methodName, comp.getName())){
                    return false;
                }
                if(!isParamTypesCheck){
                    return true;
                }
                final Class[] compParamTypes = comp.getParameterTypes();
                if(paramTypes == null && compParamTypes == null){
                    return true;
                }
                if((paramTypes == null && compParamTypes != null)
                    || (paramTypes != null && compParamTypes == null)
                    || (paramTypes.length != compParamTypes.length)
                ){
                    return false;
                }
                for(int i = 0; i < paramTypes.length; i++){
                    if(!paramTypes[i].equals(compParamTypes[i].getName())
                        && !Pattern.matches(paramTypes[i], compParamTypes[i].getName())){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }
        
        public int hashCode(){
            int hashCode = owner.hashCode() + methodName.hashCode();
            if(paramTypes != null){
                for(int i = 0; i < paramTypes.length; i++){
                    hashCode += paramTypes[i].hashCode();
                }
            }
            return hashCode;
        }
    }
}
