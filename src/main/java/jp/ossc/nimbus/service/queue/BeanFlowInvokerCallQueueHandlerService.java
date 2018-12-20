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
package jp.ossc.nimbus.service.queue;

import java.io.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.ClassMappingTree;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.beancontrol.BeanFlowMonitor;
import jp.ossc.nimbus.service.beancontrol.BeanFlowMonitorImpl;
import jp.ossc.nimbus.service.context.Context;

/**
 * BeanFlow実行QueueHandlerサービス。<p>
 *
 * @author M.Takata
 */
public class BeanFlowInvokerCallQueueHandlerService extends ServiceBase
 implements QueueHandler, BeanFlowInvokerCallQueueHandlerServiceMBean{

    private static final long serialVersionUID = -7498066162472850636L;

    private static final String INPUT_KEY = "input";

    private String beanFlowKeyByInput;
    private Property beanFlowKeyByInputProp;
    private String beanFlowInputByInput;
    private Property beanFlowInputByInputProp;
    private Map classMapping;
    private ClassMappingTree classMap;
    private String[] conditions;
    private List conditionList;
    private String defaultBeanFlowKey;
    private ServiceName beanFlowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory beanFlowInvokerFactory;
    private boolean isThrowOnNotFoundBeanFlow;
    private String errorLogMessageId;
    private String retryOverErrorLogMessageId;
    private ServiceName threadContextServiceName;
    private Context context;
    private boolean isClearThreadContext = true;

    public void setBeanFlowKeyByInput(String prop){
        beanFlowKeyByInput = prop;
    }
    public String getBeanFlowKeyByInput(){
        return beanFlowKeyByInput;
    }

    public void setBeanFlowInputByInput(String prop){
        beanFlowInputByInput = prop;
    }
    public String getBeanFlowInputByInput(){
        return beanFlowInputByInput;
    }

    public void setClassMapping(Map mapping){
        classMapping = mapping;
    }
    public Map getClassMapping(){
        return classMapping;
    }

    public void setConditions(String[] conditions){
        this.conditions = conditions;
    }
    public String[] getConditions(){
        return conditions;
    }

    public void setDefaultBeanFlowKey(String beanFlowKey){
        defaultBeanFlowKey = beanFlowKey;
    }
    public String getDefaultBeanFlowKey(){
        return defaultBeanFlowKey;
    }

    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }

    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    public BeanFlowInvokerFactory getBeanFlowInvokerFactory(){
        return beanFlowInvokerFactory;
    }

    public boolean isThrowOnNotFoundBeanFlow(){
        return isThrowOnNotFoundBeanFlow;
    }
    public void setThrowOnNotFoundBeanFlow(boolean isThrow){
        isThrowOnNotFoundBeanFlow = isThrow;
    }

    public String getErrorLogMessageId(){
        return errorLogMessageId;
    }
    public void setErrorLogMessageId(String id){
        errorLogMessageId = id;
    }

    public String getRetryOverErrorLogMessageId(){
        return retryOverErrorLogMessageId;
    }
    public void setRetryOverErrorLogMessageId(String id){
        retryOverErrorLogMessageId = id;
    }

    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }

    public void setThreadContext(Context context){
        this.context = context;
    }
    public Context getThreadContext(){
        return context;
    }

    public boolean isClearThreadContext(){
        return isClearThreadContext;
    }
    public void setClearThreadContext(boolean isClear){
        isClearThreadContext = isClear;
    }

    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(threadContextServiceName != null){
            context = (Context)ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }

        if(beanFlowKeyByInput != null){
            if(!beanFlowKeyByInput.startsWith(INPUT_KEY)){
                throw new IllegalArgumentException("BeanFlowKeyByInput don't start with 'input' : " + beanFlowKeyByInput);
            }
            if(!beanFlowKeyByInput.equals(INPUT_KEY)){
                beanFlowKeyByInputProp = null;
            }else{
                String tmpProp = beanFlowKeyByInput.substring(INPUT_KEY.length());
                switch(tmpProp.charAt(0)){
                case '.':
                    tmpProp = tmpProp.substring(1);
                    break;
                case '(':
                case '[':
                    break;
                default:
                    throw new IllegalArgumentException("BeanFlowKeyByInput is illegal : " + beanFlowKeyByInput);
                }
                beanFlowKeyByInputProp = PropertyFactory.createProperty(tmpProp);
            }
        }else if(classMapping != null && classMapping.size() != 0){
            classMap = new ClassMappingTree(null);
            final Iterator entries = classMapping.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final Class clazz = Utility.convertStringToClass((String)entry.getKey());
                final String beanFlowKey = (String)entry.getValue();
                classMap.add(clazz, beanFlowKey);
            }
        }else if(conditions != null && conditions.length != 0){
            conditionList = new ArrayList();
            for(int i = 0; i < conditions.length; i++){
                final String condition = conditions[i];
                final int index = condition.lastIndexOf('=');
                if(index == 0 || index == -1
                     || index == condition.length() - 1){
                    throw new IllegalArgumentException("Condition is illegal : " + condition);
                }
                final String cond = condition.substring(0, index);
                final String beanFlowKey = condition.substring(index + 1);
                conditionList.add(new Condition(cond, beanFlowKey));
            }
        }
        if(beanFlowInputByInput != null){
            if(!beanFlowInputByInput.startsWith(INPUT_KEY)){
                throw new IllegalArgumentException("BeanFlowInputByInput don't start with 'input' : " + beanFlowInputByInput);
            }
            if(!beanFlowInputByInput.equals(INPUT_KEY)){
                beanFlowInputByInputProp = null;
            }else{
                String tmpProp = beanFlowInputByInput.substring(INPUT_KEY.length());
                switch(tmpProp.charAt(0)){
                case '.':
                    tmpProp = tmpProp.substring(1);
                    break;
                case '(':
                case '[':
                    break;
                default:
                    throw new IllegalArgumentException("BeanFlowInputByInput is illegal : " + beanFlowInputByInput);
                }
                beanFlowInputByInputProp = PropertyFactory.createProperty(tmpProp);
            }
        }
    }

    // QueueHandlerのJavaDoc
    public void handleDequeuedObject(Object obj) throws Throwable{
        Object input = obj;
        AsynchContext asynchCtx = null;
        BeanFlowInvoker invoker = null;
        BeanFlowMonitor monitor = null;
        String beanFlowKey = null;
        if(isClearThreadContext && context != null){
            context.clear();
        }
        if(obj instanceof AsynchContext){
            asynchCtx = (AsynchContext)obj;
            if(asynchCtx.isCancel()){
                return;
            }
            if(asynchCtx instanceof BeanFlowAsynchContext){
                invoker = ((BeanFlowAsynchContext)asynchCtx).getBeanFlowInvoker();
                monitor = ((BeanFlowAsynchContext)asynchCtx).getBeanFlowMonitor();
                if(invoker == null){
                    beanFlowKey = ((BeanFlowAsynchContext)asynchCtx).getFlowName();
                }
            }
            input = asynchCtx.getInput();
            if(context != null){
                asynchCtx.applyThreadContext(context);
            }
        }

        if(input != null && beanFlowInputByInputProp != null){
            input = beanFlowInputByInputProp.getProperty(input);
        }

        if(invoker == null && beanFlowKey == null && input != null){
            if(beanFlowKeyByInput != null){
                if(beanFlowKeyByInputProp == null){
                    beanFlowKey = (String)input;
                }else{
                    beanFlowKey = (String)beanFlowInputByInputProp.getProperty(input);
                }
            }else if(classMap != null){
                beanFlowKey = (String)classMap.getValue(input.getClass());
            }else if(conditionList != null && conditionList.size() != 0){
                for(int i = 0, imax = conditionList.size(); i < imax; i++){
                    final Condition condition = (Condition)conditionList.get(i);
                    if(condition.evaluate(input)){
                        beanFlowKey = condition.beanFlowKey;
                        break;
                    }
                }
            }
        }
        Object output = null;
        if(invoker == null){
            if(beanFlowKey == null){
                beanFlowKey = defaultBeanFlowKey;
            }
            if(beanFlowKey == null){
                if(isThrowOnNotFoundBeanFlow){
                    throw new BeanFlowNotFoundException("BeanFlow is not found. arg=" + obj);
                }else{
                    output = null;
                }
            }else{
                try{
                    invoker = beanFlowInvokerFactory.createFlow(beanFlowKey);
                }catch(Exception e2){
                    throw new NoSuchBeanFlowException("BeanFlowKey : " + beanFlowKey, e2);
                }
                output = invoker.invokeFlow(input);
            }
        }else{
            output = invoker.invokeFlow(input, monitor);
        }
        if(isClearThreadContext && context != null){
            context.clear();
        }
        if(asynchCtx != null){
            asynchCtx.setOutput(output);
            asynchCtx.response();
            asynchCtx.setInput(null);
            asynchCtx.clearThreadContext();
            if(asynchCtx.getResponseQueue() != null){
                asynchCtx.getResponseQueue().push(asynchCtx);
            }else if(monitor != null && monitor instanceof BeanFlowMonitorImpl){
                BeanFlowMonitorImpl monitorImpl = (BeanFlowMonitorImpl)monitor;
                monitorImpl.removeAsynchContext((BeanFlowAsynchContext)asynchCtx);
            }
        }
    }

    // QueueHandlerのJavaDoc
    public boolean handleError(Object obj, Throwable th) throws Throwable{
        try{
            if(errorLogMessageId != null){
                getLogger().write(errorLogMessageId, obj, th);
            }
            if((th instanceof BeanFlowNotFoundException)
                 || (th instanceof NoSuchBeanFlowException)){
                return false;
            }else{
                return true;
            }
        }finally{
            if(isClearThreadContext && context != null){
                context.clear();
            }
        }
    }

    // QueueHandlerのJavaDoc
    public void handleRetryOver(Object obj, Throwable th) throws Throwable{
        try{
            if(obj instanceof AsynchContext){
                AsynchContext asynchCtx = (AsynchContext)obj;
                asynchCtx.setThrowable(th);
                if(asynchCtx.getResponseQueue() != null){
                    asynchCtx.getResponseQueue().push(asynchCtx);
                }else if(asynchCtx instanceof BeanFlowAsynchContext){
                    BeanFlowMonitorImpl monitor = (BeanFlowMonitorImpl)((BeanFlowAsynchContext)asynchCtx).getBeanFlowMonitor();
                    if(monitor != null){
                        monitor.removeAsynchContext((BeanFlowAsynchContext)asynchCtx);
                    }
                }
                if(retryOverErrorLogMessageId != null){
                    getLogger().write(retryOverErrorLogMessageId, obj, th);
                }
                asynchCtx.setInput(null);
                asynchCtx.clearThreadContext();
            }else{
                if((th instanceof BeanFlowNotFoundException)
                     || (th instanceof NoSuchBeanFlowException)){
                    throw th;
                }
                if(retryOverErrorLogMessageId != null){
                    getLogger().write(retryOverErrorLogMessageId, obj, th);
                }
            }
        }finally{
            if(isClearThreadContext && context != null){
                context.clear();
            }
        }
    }

    public static class BeanFlowNotFoundException extends Exception{

        private static final long serialVersionUID = -3976145224105142118L;

        public BeanFlowNotFoundException(){}
        public BeanFlowNotFoundException(String message){
            super(message);
        }
        public BeanFlowNotFoundException(Throwable cause){
            super(cause);
        }
        public BeanFlowNotFoundException(String message, Throwable cause){
            super(message, cause);
        }
    }

    public static class NoSuchBeanFlowException extends Exception{

        private static final long serialVersionUID = 5781750320267729638L;

        public NoSuchBeanFlowException(){}
        public NoSuchBeanFlowException(String message){
            super(message);
        }
        public NoSuchBeanFlowException(Throwable cause){
            super(cause);
        }
        public NoSuchBeanFlowException(String message, Throwable cause){
            super(message, cause);
        }
    }

    public static class ConditionEvaluateException extends Exception{

        private static final long serialVersionUID = 46810818400899018L;

        public ConditionEvaluateException(){}
        public ConditionEvaluateException(String message){
            super(message);
        }
        public ConditionEvaluateException(Throwable cause){
            super(cause);
        }
        public ConditionEvaluateException(String message, Throwable cause){
            super(message, cause);
        }
    }

    private static class Condition implements Serializable{

        private static final long serialVersionUID = -9155271482408748880L;

        public String beanFlowKey;

        private transient List properties;
        private transient Expression expression;
        private transient List keyList;
        private String condition;

        private static final String DELIMITER = "@";
        private static final String VALUE = "value";

        Condition(String cond, String beanFlowKey) throws Exception{
            initCondition(cond);
            condition = cond;
            this.beanFlowKey = beanFlowKey;
        }

        private void initCondition(String cond) throws Exception{
            keyList = new ArrayList();
            properties = new ArrayList();

            StringTokenizer token = new StringTokenizer(cond, DELIMITER, true);

            boolean keyFlg = false;

            String beforeToken = null;
            StringBuilder condBuf = new StringBuilder();

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
        }

        public boolean evaluate(Object object) throws ConditionEvaluateException{
            return evaluate(object, false);
        }

        protected boolean evaluate(Object object, boolean isTest) throws ConditionEvaluateException{
            JexlContext jexlContext = JexlHelper.createContext();
            jexlContext.getVars().put(VALUE, object);

            if(object != null){
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
            }

            try{
                Object exp = expression.evaluate(jexlContext);
                if(exp instanceof Boolean){
                    return ((Boolean)exp).booleanValue();
                }else{
                    if(exp == null && isTest){
                        return true;
                    }
                    throw new ConditionEvaluateException(expression.getExpression());
                }
            }catch(Exception e){
                throw new ConditionEvaluateException(e);
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
}
