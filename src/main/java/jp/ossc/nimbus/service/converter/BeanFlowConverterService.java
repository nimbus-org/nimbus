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
package jp.ossc.nimbus.service.converter;

import java.util.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.ClassMappingTree;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;

/**
 * BeanFlowサービスを使ったBean変換コンバータ。<p>
 * 
 * @author M.Takata
 */
public class BeanFlowConverterService extends ServiceBase
 implements Converter, BeanFlowConverterServiceMBean{
    
    private static final long serialVersionUID = -5396783198786410663L;
    
    private static final String ARRAY_CLASS_SUFFIX = "[]";
    
    private Map classMapping;
    private ClassMappingTree classMap;
    private String[] conditions;
    private List conditionList;
    private String defaultBeanFlowKey;
    private ServiceName beanFlowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory beanFlowInvokerFactory;
    
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
    
    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowInvoker is null.");
        }
        final Set flowKeySet = beanFlowInvokerFactory.getBeanFlowKeySet();
        
        if(classMapping != null && classMapping.size() != 0){
            classMap = new ClassMappingTree(null);
            final Iterator entries = classMapping.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                final Class clazz = convertStringToClass((String)entry.getKey());
                final String beanFlowKey = (String)entry.getValue();
                if(!flowKeySet.contains(beanFlowKey)){
                    throw new IllegalArgumentException("BeanFlow is not found : " + beanFlowKey);
                }
                classMap.add(clazz, beanFlowKey);
            }
        }else{
            if(classMap != null){
                classMap.clear();
            }
        }
        
        if(conditions != null && conditions.length != 0){
            if(conditionList != null){
                conditionList.clear();
            }else{
                conditionList = new ArrayList();
            }
            for(int i = 0; i < conditions.length; i++){
                final String condition = conditions[i];
                final int index = condition.lastIndexOf('=');
                if(index == 0 || index == -1
                     || index == condition.length() - 1){
                    throw new IllegalArgumentException("Condition is illegal : " + condition);
                }
                final String cond = condition.substring(0, index);
                final String beanFlowKey = condition.substring(index + 1);
                if(!flowKeySet.contains(beanFlowKey)){
                    throw new IllegalArgumentException("BeanFlow is not found : " + beanFlowKey);
                }
                conditionList.add(new Condition(cond, beanFlowKey));
            }
        }else{
            if(conditionList != null){
                conditionList.clear();
            }
        }
        if(defaultBeanFlowKey != null
             && !flowKeySet.contains(defaultBeanFlowKey)){
            throw new IllegalArgumentException("BeanFlow is not found : " + defaultBeanFlowKey);
        }
        if((classMapping == null || classMapping.size() == 0)
             && (conditionList == null || conditionList.size() == 0)
             && defaultBeanFlowKey == null){
            throw new IllegalArgumentException("BeanFlowKey is not specified.");
        }
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        String beanFlowKey = null;
        if(obj != null && classMap != null){
            beanFlowKey = (String)classMap.getValue(obj.getClass());
        }
        if(beanFlowKey == null
             && conditionList != null && conditionList.size() != 0){
            for(int i = 0, imax = conditionList.size(); i < imax; i++){
                final Condition condition = (Condition)conditionList.get(i);
                if(condition.evaluate(obj)){
                    beanFlowKey = condition.beanFlowKey;
                    break;
                }
            }
        }
        if(beanFlowKey == null){
            beanFlowKey = defaultBeanFlowKey;
        }
        if(beanFlowKey == null){
            return obj;
        }
        try{
            return beanFlowInvokerFactory.createFlow(beanFlowKey).invokeFlow(obj);
        }catch(Exception e){
            throw new ConvertException(e);
        }
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
    
    private class Condition{
        
        public String beanFlowKey;
        
        private List properties;
        private Expression expression;
        private List keyList;
        
        private static final String DELIMITER = "@";
        private static final String VALUE = "value";
        
        Condition(String cond, String beanFlowKey) throws Exception{
            this.beanFlowKey = beanFlowKey;
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
        
        public boolean evaluate(Object object) throws ConvertException{
            return evaluate(object, false);
        }
        
        protected boolean evaluate(Object object, boolean isTest) throws ConvertException{
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
                    throw new ConvertException(expression.getExpression());
                }
            }catch(Exception e){
                throw new ConvertException(e);
            }
        }
    }
}
