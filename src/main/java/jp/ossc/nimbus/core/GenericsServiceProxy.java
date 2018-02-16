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
package jp.ossc.nimbus.core;

import java.util.*;
import java.lang.reflect.*;

import javax.management.*;

import jp.ossc.nimbus.beans.*;

/**
 * 汎用サービスプロキシ。<p>
 *
 * @author M.Takata
 */
public class GenericsServiceProxy extends ServiceBase
 implements DynamicMBean{
    
    private static final long serialVersionUID = -2723473573128599716L;
    
    private static final String MBEAN_SUFFIX = "MBean";
    private static final String MXBEAN_SUFFIX = "MXBean";
    
    protected Set constructorInfos = new HashSet();
    
    protected Map attributeInfos = new HashMap();
    
    protected Set operationInfos = new HashSet();
    
    protected MBeanInfo mbeanInfo;
    
    protected Map attributePropCache = new HashMap();
    
    public GenericsServiceProxy(ServiceBaseSupport support) throws Exception{
        super(support);
        initConstructors();
        initBaseAttributes();
        initBaseOperations();
        if(support != null){
            Class mbeanInterface = findMBeanInterface(support.getClass());
            if(mbeanInterface == null
                && support instanceof ServiceProxy){
                mbeanInterface = findMBeanInterface(
                    ((ServiceProxy)support).getTarget().getClass()
                );
            }
            if(mbeanInterface != null){
                initAttributesOf(mbeanInterface, support.getClass());
                initOperationsOf(mbeanInterface, support.getClass());
            }
        }
        initMBeanInfo();
    }
    
    protected void initConstructors() throws Exception{
        final Constructor[] constructors = getClass().getConstructors();
        for(int i = 0; i < constructors.length; i++){
            if(constructors[i].getParameterTypes().length != 0){
                constructorInfos.add(
                    new MBeanConstructorInfo(null, constructors[i])
                );
            }
        }
    }
    
    protected void initBaseAttributes() throws Exception{
        initAttributesOf(ServiceBaseMBean.class, getClass());
    }
    
    private boolean isDeclaredMethod(Method method, Class clazz){
        try{
            final Method decMethod = clazz.getMethod(
                method.getName(),
                method.getParameterTypes()
            );
            
            // JRockitとSun JVMの動作の違いを吸収するための実装
            // JRockitでは、インタフェースのClassオブジェクトが
            // Objectクラスのサブクラスという扱いになっている
            if(Object.class.equals(decMethod.getDeclaringClass())){
                return false;
            }
        }catch(NoSuchMethodException e){
            return false;
        }
        return true;
    }
    
    protected void initAttributesOf(
        Class mbeanInterface,
        Class target
   ) throws Exception{
        final SimpleProperty[] props
             = SimpleProperty.getProperties(target);
        for(int i = 0; i < props.length; i++){
            if(!attributeInfos.containsKey(props[i].getPropertyName())){
                
                if((props[i].isReadable(target)
                        && isDeclaredMethod(props[i].getReadMethod(target),
                                mbeanInterface))
                    || (props[i].isWritable(target)
                        && isDeclaredMethod(props[i].getWriteMethod(target),
                                mbeanInterface))
                ){
                    String propName = props[i].getPropertyName();
                    if(Character.isLowerCase(propName.charAt(0))){
                        if(propName.length() == 1){
                            propName = Character.toString(
                                Character.toUpperCase(propName.charAt(0))
                            );
                        }else{
                            propName = Character.toUpperCase(propName.charAt(0))
                                + propName.substring(1);
                        }
                    }
                    attributeInfos.put(
                        propName,
                        new MBeanAttributeInfo(
                            propName,
                            null,
                            props[i].isReadable(target)
                                 ? props[i].getReadMethod(target) : null,
                            props[i].isWritable(target)
                                 ? props[i].getWriteMethod(target) : null
                        )
                    );
                }
            }
        }
    }
    
    protected void initBaseOperations() throws Exception{
        initOperationsOf(ServiceBaseMBean.class, getClass());
    }
    
    protected void initOperationsOf(
        Class mbeanInterface,
        Class target
    ) throws Exception{
        final Method[] methods = mbeanInterface.getMethods();
        
        final SimpleProperty[] props
             = SimpleProperty.getProperties(target);
        for(int i = 0; i < methods.length; i++){
            Method method = methods[i];
            boolean isOperationMethod = true;
            for(int j = 0; j < props.length; j++){
                if(props[j].isReadable(target)){
                    Method readMethod = props[j]
                        .getReadMethod(target);
                    if(method.getName().equals(readMethod.getName())
                        && readMethod.getParameterTypes().length == 0){
                        isOperationMethod = false;
                        break;
                    }
                }
                if(props[j].isWritable(target)){
                    Method writeMethod = props[j]
                        .getWriteMethod(target);
                    if(method.getName().equals(writeMethod.getName())
                        && writeMethod.getParameterTypes().length == 1
                        && method.getParameterTypes()[0]
                            .equals(writeMethod.getParameterTypes()[0])
                    ){
                        isOperationMethod = false;
                        break;
                    }
                }
            }
            if(isOperationMethod){
                method = target.getMethod(
                    method.getName(),
                    method.getParameterTypes()
                );
                final MBeanOperationInfo oprationInfo
                     = new MBeanOperationInfo("", method);
                if(!operationInfos.contains(oprationInfo)){
                    operationInfos.add(oprationInfo);
                }
            }
        }
    }
    
    protected void initMBeanInfo(){
        mbeanInfo = new MBeanInfo(
            getClass().getName(),
            null,
            (MBeanAttributeInfo[])attributeInfos.values().toArray(
                new MBeanAttributeInfo[attributeInfos.size()]
            ),
            (MBeanConstructorInfo[])constructorInfos.toArray(
                new MBeanConstructorInfo[constructorInfos.size()]
            ),
            (MBeanOperationInfo[])operationInfos.toArray(
                new MBeanOperationInfo[operationInfos.size()]
            ),
            new MBeanNotificationInfo[0]
        );
    }
    
    // DynamicMBeanのJavaDoc
    public Object getAttribute(String attribute)
     throws AttributeNotFoundException, MBeanException, ReflectionException{
        final MBeanAttributeInfo attributeInfo
             = (MBeanAttributeInfo)attributeInfos.get(attribute);
        if(attributeInfo != null && attributeInfo.isReadable()){
            Property prop = (Property)attributePropCache.get(attribute);
            if(prop == null){
                prop = PropertyFactory.createProperty(attribute);
                attributePropCache.put(attribute, prop);
            }
            try{
                if(prop.isReadable(this)){
                    return prop.getProperty(this);
                }else{
                    if(support == null){
                        return null;
                    }
                    if(prop.isReadable(support)){
                        return prop.getProperty(support);
                    }else{
                        throw new AttributeNotFoundException(attribute);
                    }
                }
            }catch(NoSuchPropertyException e){
                throw new AttributeNotFoundException(attribute);
            }catch(InvocationTargetException e){
                throw new ReflectionException(e);
            }
        }
        throw new AttributeNotFoundException(attribute);
    }
    
    // DynamicMBeanのJavaDoc
    public AttributeList getAttributes(String[] attributes){
        final AttributeList list = new AttributeList();
        for(int i = 0; i < attributes.length; i++){
            try{
                list.add(
                    new Attribute(
                        attributes[i],
                        getAttribute(attributes[i])
                    )
                );
            }catch(AttributeNotFoundException e){
            }catch(MBeanException e){
            }catch(ReflectionException e){
            }
        }
        return list;
    }
    
    // DynamicMBeanのJavaDoc
    public void setAttribute(Attribute attribute)
     throws AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException{
        final MBeanAttributeInfo attributeInfo
             = (MBeanAttributeInfo)attributeInfos.get(attribute.getName());
        if(attributeInfo != null && attributeInfo.isWritable()){
            final SimpleProperty prop = (SimpleProperty)PropertyFactory
                .createProperty(attribute.getName());
            try{
                if(prop.isWritable(getClass())){
                    try{
                        prop.setProperty(this, attribute.getValue());
                    }catch(InvocationTargetException e){
                        throw new ReflectionException(e);
                    }
                }else{
                    if(support == null){
                        return;
                    }
                    try{
                        prop.setProperty(support, attribute.getValue());
                    }catch(InvocationTargetException e){
                        throw new ReflectionException(e);
                    }
                }
            }catch(NoSuchPropertyException e){
                throw new AttributeNotFoundException(
                    "name=" + attribute.getName()
                        + ", value=" + attribute.getValue()
                );
            }
        }
    }
    
    // DynamicMBeanのJavaDoc
    public AttributeList setAttributes(AttributeList attributes){
        final AttributeList list = new AttributeList();
        for(int i = 0, max = attributes.size(); i < max; i++){
            
            try{
                setAttribute((Attribute)attributes.get(i));
                list.add(attributes.get(i));
            }catch(AttributeNotFoundException e){
            }catch(InvalidAttributeValueException e){
            }catch(MBeanException e){
            }catch(ReflectionException e){
            }
        }
        return list;
    }
    
    // DynamicMBeanのJavaDoc
    public Object invoke(
        String actionName,
        Object[] params,
        String[] signature
    ) throws MBeanException, ReflectionException{
        
        Class[] paramTypes = null;
        if(signature != null){
            paramTypes = new Class[signature.length];
            final ClassLoader loader
                 = Thread.currentThread().getContextClassLoader();
            try{
                for(int i = 0; i < signature.length; i++){
                    paramTypes[i] = Class.forName(signature[i], false, loader);
                }
            }catch(ClassNotFoundException e){
                throw new ReflectionException(e);
            }
        }
        Method method = null;
        
        try{
            method = getClass().getMethod(actionName, paramTypes);
            return method.invoke(this, params);
        }catch(NoSuchMethodException e){
        }catch(IllegalAccessException e){
            throw new ReflectionException(e);
        }catch(InvocationTargetException e){
            throw new ReflectionException(e);
        }
        
        if(support == null){
            return null;
        }
        
        Object ret = null;
        try{
            method = support.getClass().getMethod(actionName, paramTypes);
            ret = method.invoke(support, params);
        }catch(NoSuchMethodException e){
            throw new ReflectionException(e);
        }catch(IllegalAccessException e){
            throw new ReflectionException(e);
        }catch(InvocationTargetException e){
            throw new ReflectionException(e);
        }
        return ret;
    }
    
    // DynamicMBeanのJavaDoc
    public MBeanInfo getMBeanInfo(){
        return mbeanInfo;
    }
    
    private static Class findMBeanInterface(Class clazz){
        if(clazz == null){
            return null;
        }
        final String className = clazz.getName();
        final String mbeanInterfaceName = className + MBEAN_SUFFIX;
        final Class[] interfaces = clazz.getInterfaces();
        for(int i = 0, max = interfaces.length; i < max; i++){
            if(interfaces[i].equals(DynamicMBean.class)
                || interfaces[i].getName().equals(mbeanInterfaceName)
                || interfaces[i].getName().endsWith(MXBEAN_SUFFIX)
            ){
                return interfaces[i];
            }
        }
        return findMBeanInterface(clazz.getSuperclass());
    }
}