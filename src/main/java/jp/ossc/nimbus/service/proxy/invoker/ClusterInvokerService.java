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
package jp.ossc.nimbus.service.proxy.invoker;

import java.io.*;
import java.util.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.keepalive.KeepAliveChecker;
import jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector;
import jp.ossc.nimbus.service.proxy.RemoteServiceCallException;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * �N���X�^Invoker�T�[�r�X�B<p>
 * {@link KeepAliveCheckInvoker}�C���^�t�F�[�X����������{@link Invoker}��{@link KeepAliveCheckerSelector}�ɂ���đI�����āA�Ăяo��Invoker�ł���B<br>
 * ����ɂ��A�����T�[�o�Ɍׂ��đ��݂���T�[�r�X���A�����Ď��y�ѕ��ו��U�����Ȃ���Ăяo�������\�ɂȂ�B<br>
 * 
 * @author M.Takata
 */
public class ClusterInvokerService extends ServiceBase
 implements Invoker, ClusterInvokerServiceMBean{
    
    private static final long serialVersionUID = 8638969807676141797L;
    
    private static final String ARRAY_CLASS_SUFFIX = "[]";
    
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    protected ServiceName selectorServiceName;
    protected KeepAliveCheckerSelector selector;
    protected int maxRetryCount = 0;
    protected long retryInterval = 0;
    private String[] exceptionConditions;
    private ClassMappingTree exceptionConditionMap;
    private boolean isBroadcast;
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    
    // ClusterInvokerServiceMBean��JavaDoc
    public void setKeepAliveCheckerSelectorServiceName(ServiceName name){
        selectorServiceName = name;
    }
    // ClusterInvokerServiceMBean��JavaDoc
    public ServiceName getKeepAliveCheckerSelectorServiceName(){
        return selectorServiceName;
    }
    
    // ClusterInvokerServiceMBean��JavaDoc
    public void setExceptionConditions(String[] conditions){
        exceptionConditions = conditions;
    }
    // ClusterInvokerServiceMBean��JavaDoc
    public String[] getExceptionConditions(){
        return exceptionConditions;
    }
    
    // ClusterInvokerServiceMBean��JavaDoc
    public void setMaxRetryCount(int count){
        maxRetryCount = count;
    }
    // ClusterInvokerServiceMBean��JavaDoc
    public int getMaxRetryCount(){
        return maxRetryCount;
    }
    
    // ClusterInvokerServiceMBean��JavaDoc
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    // ClusterInvokerServiceMBean��JavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // ClusterInvokerServiceMBean��JavaDoc
    public void setBroadcast(boolean isBroadcast){
        this.isBroadcast = isBroadcast;
    }
    // ClusterInvokerServiceMBean��JavaDoc
    public boolean isBroadcast(){
        return isBroadcast;
    }
    
    // ClusterInvokerServiceMBean��JavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // ClusterInvokerServiceMBean��JavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void startService() throws Exception{
        
        if(selectorServiceName != null){
            selector = (KeepAliveCheckerSelector)ServiceManagerFactory
                .getServiceObject(selectorServiceName);
        }
        if(selector == null){
            throw new IllegalArgumentException("KeepAliveCheckerSelector is null.");
        }
        
        if(exceptionConditions != null && exceptionConditions.length != 0){
            exceptionConditionMap = new ClassMappingTree(null);
            for(int i = 0; i < exceptionConditions.length; i++){
                String className = exceptionConditions[i];
                final int index = className.lastIndexOf(':');
                String conditionStr = null;
                if(index != -1){
                    if(index != className.length() - 1){
                        conditionStr = className.substring(index + 1);
                    }
                    className = className.substring(0, index);
                }
                final Class clazz = convertStringToClass(className);
                Condition condition = null;
                if(conditionStr == null){
                    condition = new Condition();
                }else{
                    condition = new Condition(conditionStr);
                }
                exceptionConditionMap.add(clazz, condition);
            }
        }else{
            exceptionConditionMap = null;
        }
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
    }
    
    public void setKeepAliveCheckerSelector(KeepAliveCheckerSelector selector){
        this.selector = selector;
    }
    public KeepAliveCheckerSelector getKeepAliveCheckerSelector(){
        return selector;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}�C���^�t�F�[�X����������RMI�I�u�W�F�N�g���Ăяo���B<p>
     * 
     * @param context �Ăяo���̃R���e�L�X�g���
     * @return �Ăяo�����ʂ̖߂�l
     * @exception Throwable �Ăяo����ŗ�O�����������ꍇ
     */
    public Object invoke(InvocationContext context) throws Throwable{
        boolean isBroadcast = this.isBroadcast;
        KeepAliveCheckInvoker targetInvoker = null;
        if(threadContext != null){
            Object isBroadcastObj = threadContext.get(CONTEXT_KEY_INVOKE_BROADCAST);
            if(isBroadcastObj != null){
                if(isBroadcastObj instanceof Boolean){
                    isBroadcast = ((Boolean)isBroadcastObj).booleanValue();
                }else if(isBroadcastObj instanceof String){
                    isBroadcast = Boolean.valueOf((String)isBroadcastObj).booleanValue();
                }
            }
            Object targetObj = threadContext.get(CONTEXT_KEY_INVOKE_TARGET);
            if(targetObj != null){
                KeepAliveChecker[] checkers = selector.getSelectableCheckers();
                if(checkers == null || checkers.length == 0){
                    throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
                }
                StringBuffer selectableCheckers = new StringBuffer();
                if(targetObj instanceof ServiceName){
                    ServiceName targetServiceName = (ServiceName)targetObj;
                    for(int i = 0; i < checkers.length; i++){
                        KeepAliveCheckInvoker invoker = (KeepAliveCheckInvoker)checkers[i];
                        if((invoker instanceof Service) && invoker.isAlive()){
                            selectableCheckers.append(invoker);
                            if(i != checkers.length - 1){
                                selectableCheckers.append(',');
                            }
                            if(targetServiceName.equals(((Service)invoker).getServiceNameObject())){
                                targetInvoker = invoker;
                                break;
                            }
                        }
                    }
                }else{
                    for(int i = 0; i < checkers.length; i++){
                        KeepAliveCheckInvoker invoker = (KeepAliveCheckInvoker)checkers[i];
                        if(invoker.isAlive()){
                            selectableCheckers.append(invoker.getHostInfo());
                            if(i != checkers.length - 1){
                                selectableCheckers.append(',');
                            }
                            if(targetObj.equals(invoker.getHostInfo())){
                                targetInvoker = invoker;
                                break;
                            }
                        }
                    }
                }
                if(targetInvoker == null){
                    throw new RemoteServiceCallException("Not found selectable KeepAliveCheckInvoker. target=" + targetObj + ", selectable=" + selectableCheckers);
                }else{
                    return targetInvoker.invoke(context);
                }
            }
        }
        if(isBroadcast){
            KeepAliveChecker[] checkers = selector.getSelectableCheckers();
            if(checkers == null || checkers.length == 0){
                throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
            }
            Object ret = null;
            for(int i = 0; i < checkers.length; i++){
                KeepAliveCheckInvoker invoker = (KeepAliveCheckInvoker)checkers[i];
                if(invoker.isAlive()){
                    ret = invoker.invoke(context);
                }
            }
            return ret;
        }else{
            
            for(int tryCount = 0; tryCount <= maxRetryCount; tryCount++){
                KeepAliveCheckInvoker invoker
                    = (KeepAliveCheckInvoker)selector.selectChecker();
                if(invoker == null){
                    throw new RemoteServiceCallException("No selectable KeepAliveCheckInvoker.");
                }
                try{
                    return invoker.invoke(context);
                }catch(Throwable th){
                    boolean isRetry = false;
                    if(tryCount < maxRetryCount){
                        if(exceptionConditionMap != null){
                            final Condition condition = (Condition)getTargetCondition(
                                exceptionConditionMap,
                                th
                            );
                            if(condition != null && condition.evaluate(th)){
                                isRetry = true;
                            }
                        }else if(th instanceof RemoteServiceCallException){
                            isRetry = true;
                        }
                        if(isRetry){
                            final KeepAliveChecker[] checkers
                                = selector.getSelectableCheckers();
                            if(checkers == null || checkers.length == 0){
                                isRetry = false;
                            }
                        }
                    }
                    if(isRetry){
                        if(retryInterval > 0){
                            try{
                                Thread.sleep(retryInterval);
                            }catch(InterruptedException e){
                            }
                        }
                    }else{
                        throw th;
                    }
                }
            }
        }
        // �����ɂ͗��Ȃ�
        return null;
    }
    
    /**
     * �w�肳�ꂽ��O�ɑΉ�������������o���B<p>
     * 
     * @param conditions ��O�Ə����̃}�b�v
     * @param th ��O
     * @return ����
     */
    private Condition getTargetCondition(ClassMappingTree conditions, Throwable th) {
        if(conditions == null){
            return null;
        }
        // ��O�N���X�Ɋ֘A�t���Ă���������擾
        Condition condition = (Condition)conditions.getValue(th.getClass());
        if(condition != null){
            return condition;
        }
        
        Throwable cause = getCause(th);
        return cause == null ? null : getTargetCondition(conditions, cause);
    }
    
    /**
     * �w�肳�ꂽ��O���猴�����擾����B<p>
     *
     * @param th ��O
     * @return ����
     */
    private Throwable getCause(Throwable th){
        Throwable cause = null;
        if(th.getClass().getName().equals(SERVLET_EXCEPTION_NAME)){
            // ��O��ServletException�̏ꍇ�́A���[�g�̌������擾
            try{
                cause = (Throwable)th.getClass()
                    .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else if(th.getClass().getName().equals(JMS_EXCEPTION_NAME)){
            // ��O��JMSException�̏ꍇ�́A�����N��O���擾
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    private static Class convertStringToClass(String typeStr)
     throws ClassNotFoundException{
        Class type = null;
        if(typeStr != null){
            if(Byte.TYPE.getName().equals(typeStr)){
                type = Byte.TYPE;
            }else if(Character.TYPE.getName().equals(typeStr)){
                type = Character.TYPE;
            }else if(Short.TYPE.getName().equals(typeStr)){
                type = Short.TYPE;
            }else if(Integer.TYPE.getName().equals(typeStr)){
                type = Integer.TYPE;
            }else if(Long.TYPE.getName().equals(typeStr)){
                type = Long.TYPE;
            }else if(Float.TYPE.getName().equals(typeStr)){
                type = Float.TYPE;
            }else if(Double.TYPE.getName().equals(typeStr)){
                type = Double.TYPE;
            }else if(Boolean.TYPE.getName().equals(typeStr)){
                type = Boolean.TYPE;
            }else{
                if(typeStr.endsWith(ARRAY_CLASS_SUFFIX)
                    && typeStr.length() > 2){
                    final Class elementType = convertStringToClass(
                        typeStr.substring(0, typeStr.length() - 2)
                    );
                    type = Array.newInstance(elementType, 0).getClass();
                }else{
                    type = Class.forName(
                        typeStr,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                }
            }
        }
        return type;
    }
    
    private class Condition implements Serializable{
        
        private static final long serialVersionUID = -6857448672025453285L;
        
        private transient List properties;
        private transient Expression expression;
        private transient List keyList;
        private String condition;
        
        private static final String DELIMITER = "@";
        private static final String VALUE = "value";
        
        Condition() throws Exception{
            this("true");
        }
        
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
        
        public boolean evaluate(Object object){
            return evaluate(object, false);
        }
        
        protected boolean evaluate(Object object, boolean isTest){
            JexlContext jexlContext = JexlHelper.createContext();
            jexlContext.getVars().put(VALUE, object);
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
                // �N����Ȃ��͂�
            }
        }
    }
}
