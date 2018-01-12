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

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.invoker.MethodReflectionCallInvokerService;

/**
 * リターンインターセプタ。<p>
 * 以下に、呼び出されたメソッドの第１引数が"hoge"だった場合は100を返し、"fuga"だった場合は200を返すリターンインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ReturnInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.ReturnInterceptorService"&gt;
 *             &lt;attribute name="ReturnValue(@Parameters[0]@=='hoge')" type="int"&gt;100&lt;/attribute&gt;
 *             &lt;attribute name="ReturnValue(@Parameters[0]@=='fuga')" type="int"&gt;200&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class ReturnInterceptorService extends ServiceBase
 implements Interceptor, Serializable, ReturnInterceptorServiceMBean{
    
    private static final long serialVersionUID = 6314705352031037858L;
    
    private Map returnValues;
    private Map returnServiceNames;
    private List returnConditions;
    private Object returnValue;
    private ServiceName returnServiceName;
    private Class returnInterfaceClass;
    private ServiceName interceptorChainListServiceName;
    private ProxyInvocationHandler invocationHandler;
    private Object proxy;
    private boolean isEnabled = true;
    
    public void setEnabled(boolean enabled){
        isEnabled = enabled;
    }
    
    public boolean isEnabled(){
        return isEnabled;
    }
    
    public void setReturnValue(String condition, Object value){
        if(returnValues == null){
            returnValues = new LinkedHashMap();
        }
        returnValues.put(condition, value);
    }
    public Object getReturnValue(String condition){
        return returnValues == null
             ? null : returnValues.get(condition);
    }
    
    public void setReturnValue(Object value){
        returnValue = value;
    }
    public Object getReturnValue(){
        return returnValue;
    }
    
    public void setReturnServiceName(String condition, ServiceName name){
        if(returnServiceNames == null){
            returnServiceNames = new LinkedHashMap();
        }
        returnValues.put(condition, name);
    }
    public ServiceName getReturnServiceName(String condition){
        return returnServiceNames == null
             ? null : (ServiceName)returnServiceNames.get(condition);
    }
    
    public void setReturnServiceName(ServiceName name){
        returnServiceName = name;
    }
    public ServiceName getReturnServiceName(){
        return returnServiceName;
    }
    
    public void setReturnInterfaceClass(Class clazz){
        returnInterfaceClass = clazz;
    }
    public Class getReturnInterfaceClass(){
        return returnInterfaceClass;
    }
    
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    public void startService() throws Exception{
        if(returnConditions != null){
            returnConditions.clear();
        }
        if(returnValues != null
             && returnValues.size() != 0){
            if(returnConditions == null){
                returnConditions = new ArrayList();
            }
            final Iterator entries
                 = returnValues.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String condition = (String)entry.getKey();
                final Object value = entry.getValue();
                returnConditions.add(new Condition(condition, value));
            }
        }
        if(returnServiceNames != null
             && returnServiceNames.size() != 0){
            if(returnConditions == null){
                returnConditions = new ArrayList();
            }
            final Iterator entries
                 = returnServiceNames.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final String condition = (String)entry.getKey();
                final ServiceName name = (ServiceName)entry.getValue();
                returnConditions.add(new Condition(condition, name));
            }
        }
        if(returnInterfaceClass != null
             && interceptorChainListServiceName != null){
            final MethodReflectionCallInvokerService invoker
                 = new MethodReflectionCallInvokerService();
            invoker.create();
            invoker.start();
            invocationHandler = new ProxyInvocationHandler(
                interceptorChainListServiceName,
                invoker
            );
            proxy = Proxy.newProxyInstance(
                NimbusClassLoader.getInstance(),
                new Class[]{returnInterfaceClass},
                invocationHandler
            );
        }
    }
    
    /**
     * 該当するオブジェクトを返す。<p>
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
        if(getState() == STARTED && isEnabled){
            if(returnConditions != null && returnConditions.size() != 0){
                for(int i = 0, imax = returnConditions.size(); i < imax; i++){
                    final Condition condition
                         = (Condition)returnConditions.get(i);
                    if(condition.evaluate(context)){
                        return condition.getReturnValue();
                    }
                }
            }
            if(returnValue != null){
                return returnValue;
            }
            if(returnServiceName != null){
                return ServiceManagerFactory.getServiceObject(returnServiceName);
            }
            if(proxy != null && invocationHandler != null){
                Object ret = chain.invokeNext(context);
                if(ret == null){
                    return null;
                }
                invocationHandler.setTarget(ret);
                return proxy;
            }
        }
        return chain.invokeNext(context);
    }
    
    private class Condition implements Serializable{
        
        private static final long serialVersionUID = 8865216875252550610L;
        
        private transient List properties;
        private transient Expression expression;
        private transient List keyList;
        private String condition;
        private Object returnValue;
        private ServiceName returnServiceName;
        
        private static final String DELIMITER = "@";
        
        Condition(String cond) throws Exception{
            initCondition(cond);
        }
        
        private void initCondition(String cond) throws Exception{
            keyList = new ArrayList();
            properties = new ArrayList();
            
            StringTokenizer token = new StringTokenizer(cond, DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            StringBuffer condBuf = new StringBuffer();
            
            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        condBuf.append(str);
                    }
                }else if(DELIMITER.equals(str)){
                    keyFlg = false;
                    if(beforeToken != null){
                        final String tmpKey = "_conditionKey$" + keyList.size();
                         keyList.add(tmpKey);
                        condBuf.append(tmpKey);
                        Property prop = PropertyFactory.createProperty(beforeToken);
                        prop.setIgnoreNullProperty(true);
                        properties.add(prop);
                    }else{
                        condBuf.append(str);
                    }
                }
                beforeToken = str;    
            }
            
            expression = ExpressionFactory.createExpression(condBuf.toString());
            evaluate("", true);
            condition = cond;
        }
        
        Condition(String cond, Object value) throws Exception{
            this(cond);
            returnValue = value;
        }
        
        Condition(String cond, ServiceName name) throws Exception{
            this(cond);
            returnServiceName = name;
        }
        
        public Object getReturnValue(){
            if(returnValue != null){
                return returnValue;
            }
            if(returnServiceName != null){
                return ServiceManagerFactory.getServiceObject(returnServiceName);
            }
            return null;
        }
        
        public boolean evaluate(Object object){
            return evaluate(object, false);
        }
        
        protected boolean evaluate(Object object, boolean isTest){
            JexlContext jexlContext = JexlHelper.createContext();
            for(int i = 0, size = keyList.size(); i < size; i++){
                final String keyString = (String)keyList.get(i);
                final Property property = (Property)properties.get(i);
                Object val = null;
                try{
                    val = property.getProperty(object);
                }catch(NoSuchPropertyException e){
                }catch(InvocationTargetException e){
                }
                jexlContext.getVars().put(keyString, val);                
            }
            
            try{
                Object exp = expression.evaluate(jexlContext);
                if(exp instanceof Boolean){
                    return ((Boolean)exp).booleanValue();
                }else{
                    if(exp == null && isTest){
                        return true;
                    }
                    throw new IllegalArgumentException(expression.getExpression());
                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        
        private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            try{
                initCondition(condition);
            }catch(Exception e){
                // 起こらないはず
            }
        }
    }
    
    private static class ProxyInvocationHandler
     implements InvocationHandler, Serializable{
        
        private static final long serialVersionUID = 538686435253235644L;
        
        private final InterceptorChain chain;
        private transient ThreadLocal target = new ThreadLocal();
        
        public ProxyInvocationHandler(
            ServiceName interceptorChainListServiceName,
            Invoker invoker
        ){
            DefaultThreadLocalInterceptorChain chain = new DefaultThreadLocalInterceptorChain();
            chain.setInterceptorChainListServiceName(
                interceptorChainListServiceName
            );
            chain.setInvoker(invoker);
            this.chain = chain;
        }
        
        public void setTarget(Object target){
            this.target.set(target);
        }
        
        public Object invoke(
            Object proxy,
            Method method,
            Object[] args
        ) throws Throwable{
            final InvocationContext ctx = new DefaultMethodInvocationContext(
                target.get(),
                method,
                args
            );
            return chain.invokeNext(ctx);
        }
        
        private void writeObject(ObjectOutputStream out) throws IOException{
            out.defaultWriteObject();
            out.writeObject(target.get());
        }
        
        private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            target = new ThreadLocal();
            target.set(in.readObject());
        }
    }
}
