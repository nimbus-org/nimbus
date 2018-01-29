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
 * 汎用ファクトリサービスプロキシ。<p>
 *
 * @author M.Takata
 */
public class GenericsFactoryServiceProxy extends FactoryServiceBase
 implements DynamicMBean{
    
    private static final long serialVersionUID = -6799305487173484547L;
    
    private static final String MBEAN_SUFFIX = "MBean";
    private static final String MXBEAN_SUFFIX = "MXBean";
    
    protected Set constructorInfos = new HashSet();
    
    protected Map attributeInfos = new HashMap();
    
    protected Set operationInfos = new HashSet();
    
    protected MBeanInfo mbeanInfo;
    
    protected Object template;
    
    protected ServiceMetaData metaData;
    
    protected Map attributePropCache = new HashMap();
    
    public GenericsFactoryServiceProxy(
        Class clazz,
        boolean isManagement
    ) throws Exception{
        setManagement(isManagement);
        initConstructors();
        initBaseAttributes();
        initBaseOperations();
        if(isManagement){
            final Class mbeanInterface = findMBeanInterface(clazz);
            if(mbeanInterface != null){
                initAttributesOf(mbeanInterface, clazz);
                initOperationsOf(mbeanInterface, clazz);
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
        initAttributesOf(FactoryServiceBaseMBean.class, getClass());
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
        initOperationsOf(FactoryServiceBaseMBean.class, getClass());
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
    
    public void startService() throws Exception{
        final ServiceMetaData serviceData = manager.getServiceMetaData(name);
        metaData = new ServiceMetaData(
            serviceData.getServiceLoader(),
            serviceData,
            serviceData.getManager()
        );
        metaData.setCode(serviceData.getCode());
        metaData.setConstructor(serviceData.getConstructor());
        final Iterator fields = serviceData.getFields().iterator();
        while(fields.hasNext()){
            metaData.addField((FieldMetaData)fields.next());
        }
        final Iterator attrs = serviceData.getAttributes().iterator();
        while(attrs.hasNext()){
            metaData.addAttribute((AttributeMetaData)attrs.next());
        }
        final Iterator invokes = serviceData.getInvokes().iterator();
        while(invokes.hasNext()){
            metaData.addInvoke((InvokeMetaData)invokes.next());
        }
        
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                manager.startService(service, metaData);
            }
        }
    }
    
    public void stopService() throws Exception{
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                manager.stopService(service, metaData);
            }
        }
        if(template != null){
            release(template);
            template = null;
        }
    }
    
    public void destroyService() throws Exception{
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                manager.destroyService(service, metaData);
            }
        }
        metaData = null;
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
                    if(template == null){
                        if(isCreateTemplateOnStart){
                            return null;
                        }else{
                            try{
                                createTemplate();
                            }catch(Exception e){
                                throw new ReflectionException(e);
                            }
                        }
                    }
                    
                    Object target = convertServiceToObject(template);
                    if(prop.isReadable(target)){
                        return prop.getProperty(target);
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
            if(metaData != null){
                AttributeMetaData attrData
                     = metaData.getAttribute(attribute.getName());
                if(attrData == null
                     && attribute.getName().length() > 1
                ){
                    StringBuilder tmpName = new StringBuilder();
                    if(Character.isLowerCase(attribute.getName().charAt(0))){
                        tmpName.append(
                            Character.toUpperCase(attribute.getName().charAt(0))
                        );
                    }else{
                        tmpName.append(
                            Character.toLowerCase(attribute.getName().charAt(0))
                        );
                    }
                    if(attribute.getName().length() > 2){
                        tmpName.append(attribute.getName().substring(1));
                    }
                    attrData = metaData.getAttribute(tmpName.toString());
                }
                if(attrData != null){
                    attrData.setValue(attribute.getValue());
                }
            }
            final SimpleProperty prop = (SimpleProperty)PropertyFactory
                .createProperty(attribute.getName());
            Exception targetException = null;
            try{
                if(prop.isWritable(getClass())){
                    try{
                        prop.setProperty(this, attribute.getValue());
                    }catch(InvocationTargetException e){
                        targetException = e;
                    }
                }else{
                    if(template == null){
                        if(isCreateTemplateOnStart){
                            return;
                        }else{
                            try{
                                createTemplate();
                            }catch(Exception e){
                                throw new ReflectionException(e);
                            }
                        }
                    }
                    final Object target = convertServiceToObject(template);
                    try{
                        prop.setProperty(target, attribute.getValue());
                    }catch(InvocationTargetException e){
                        targetException = e;
                    }
                    if(managedInstances == null
                         || managedInstances.size() == 0){
                        return;
                    }
                    final Iterator instances = managedInstances.iterator();
                    while(instances.hasNext()){
                        final Object instance = convertServiceToObject(
                            (Service)instances.next()
                        );
                        if(prop.isWritable(instance.getClass())){
                            try{
                                prop.setProperty(
                                    instance,
                                    attribute.getValue()
                                );
                            }catch(InvocationTargetException e){
                                targetException = e;
                            }
                        }else{
                            throw new AttributeNotFoundException(
                                "class=" + instance.getClass().getName()
                                    + ", attributename=" + attribute.getName()
                            );
                        }
                    }
                }
            }catch(NoSuchPropertyException e){
                throw new AttributeNotFoundException(
                    "name=" + attribute.getName()
                        + ", value=" + attribute.getValue()
                );
            }finally{
                if(targetException != null){
                    throw new ReflectionException(targetException);
                }
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
            if(!isManagement()){
                return null;
            }
        }catch(IllegalAccessException e){
            throw new ReflectionException(e);
        }catch(InvocationTargetException e){
            throw new ReflectionException(e);
        }
        if(template == null){
            if(isCreateTemplateOnStart){
                return null;
            }else{
                try{
                    createTemplate();
                }catch(Exception e){
                    throw new ReflectionException(e);
                }
            }
        }
        final Object target = convertServiceToObject(template);
        
        Object ret = null;
        Exception targetException = null;
        try{
            method = target.getClass().getMethod(actionName, paramTypes);
            ret = method.invoke(target, params);
        }catch(NoSuchMethodException e){
            throw new ReflectionException(e);
        }catch(IllegalAccessException e){
            throw new ReflectionException(e);
        }catch(InvocationTargetException e){
            targetException = e;
        }
        if(managedInstances == null
             || managedInstances.size() == 0){
            return ret;
        }
        final Iterator instances = managedInstances.iterator();
        while(instances.hasNext()){
            Object instance = convertServiceToObject((Service)instances.next());
            try{
                method = instance.getClass().getMethod(actionName, paramTypes);
                ret = method.invoke(instance, params);
            }catch(NoSuchMethodException e){
                throw new ReflectionException(e);
            }catch(IllegalAccessException e){
                throw new ReflectionException(e);
            }catch(InvocationTargetException e){
                targetException = e;
            }
        }
        if(targetException != null){
            throw new ReflectionException(targetException);
        }
        return ret;
    }
    
    // DynamicMBeanのJavaDoc
    public MBeanInfo getMBeanInfo(){
        return mbeanInfo;
    }
    
    public void release(Object obj){
        if(obj instanceof Service
             && (obj != template
                 || !isManagement()
                 || getState() == STOPPING)
        ){
            final Service service = (Service)obj;
            if(manager == null){
                service.stop();
                service.destroy();
            }else{
                manager.stopService(service, metaData);
                manager.destroyService(service, metaData);
            }
        }
        super.release(obj);
    }
    
    /**
     * このファクトリが提供するオブジェクトのインスタンスを生成する。<p>
     * {@link ServiceManager#instanciateService(ServiceMetaData)}で生成したサービスのインスタンスに対して、以下の処理を行う。<br>
     * <ol>
     *   <li>{@link #isManagement()}がtrueの場合、生成したサービスにサービス名とサービスマネージャ名を設定する。この際、サービス名は、このファクトリサービスのサービス名の後ろに"$" + "管理されている生成したサービスの通し番号"を付与したものである。また、サービスマネージャ名は、このファクトリサービスのサービスマネージャ名と同じである。</li>
     *   <li>生成したサービスの生成処理（{@link Service#create()}）。</li>
     *   <li>生成したサービスの開始処理（{@link Service#start()}）。</li>
     *   <li>生成したサービスが{@link ServiceBase}を継承している場合は、このファクトリサービスに設定されている{@link jp.ossc.nimbus.service.log.Logger Logger}と{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}を、生成したサービスにも設定する。</li>
     * </ol>
     *
     * @return このファクトリが提供するオブジェクトのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected final Object createInstance() throws Exception{
        return createInstance(false);
    }
    
    protected Object createTemplate() throws Exception{
        return createInstance(true);
    }
    
    protected final Object createInstance(boolean isTemplate) throws Exception{
        if(manager == null || metaData == null){
            return null;
        }
        Object obj = null;
        if(isManagement()){
            obj = manager.instanciateService(metaData);
        }else{
            obj = manager.createObject(metaData);
        }
        if(obj == null){
            return null;
        }
        if(obj instanceof Service){
            final Service service = (Service)obj;
            if(!isTemplate && isManagement() && getServiceManagerName() != null){
                service.setServiceManagerName(getServiceManagerName());
                service.setServiceName(
                    getServiceName() + '$' + getManagedInstanceSet().size()
                );
            }else{
                service.setServiceManagerName(null);
                service.setServiceName(null);
            }
            if(service.getState() == DESTROYED){
                manager.createService(service, metaData);
            }
            if(service instanceof ServiceBase){
                final ServiceBase base = (ServiceBase)service;
                if(manager != null){
                    base.logger.setDefaultLogger(manager.getLogger());
                    if(getSystemLoggerServiceName() == null){
                        base.logger.setLogger(manager.getLogger());
                    }
                    base.message.setDefaultMessageRecordFactory(
                        manager.getMessageRecordFactory()
                    );
                    if(getSystemMessageRecordFactoryServiceName() == null){
                        base.message.setMessageRecordFactory(
                            manager.getMessageRecordFactory()
                        );
                    }
                }
                if(getSystemLoggerServiceName() != null){
                    base.setSystemLoggerServiceName(
                        getSystemLoggerServiceName()
                    );
                }
                if(getSystemMessageRecordFactoryServiceName() != null){
                    base.setSystemMessageRecordFactoryServiceName(
                        getSystemMessageRecordFactoryServiceName()
                    );
                }
            }
            if(service.getState() == CREATED){
                manager.startService(service, metaData);
            }
        }
        if(isTemplate && isManagement()){
            template = obj;
        }
        return obj;
    }
    
    private Object convertServiceToObject(Object service){
        Object target = service;
        while(target instanceof ServiceProxy){
            Object child = ((ServiceProxy)target).getTarget();
            if(child == target){
                break;
            }
            target = child;
        }
        return target;
    }
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前を設定する。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、サービスが{@link ServiceBase}のインスタンスであれば、{@link ServiceBase#setSystemLoggerServiceName(ServiceName)}を呼び出す。<br>
     *
     * @param name Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前
     * @see #getSystemLoggerServiceName()
     */
    public void setSystemLoggerServiceName(ServiceName name){
        super.setSystemLoggerServiceName(name);
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                if(service instanceof ServiceBase){
                    ((ServiceBase)service).setSystemLoggerServiceName(name);
                }
            }
        }
    }
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前を設定する。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、サービスが{@link ServiceBase}のインスタンスであれば、{@link ServiceBase#setSystemMessageRecordFactoryServiceName(ServiceName)}を呼び出す。<br>
     *
     * @param name Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前
     * @see #getSystemMessageRecordFactoryServiceName()
     */
    public void setSystemMessageRecordFactoryServiceName(
        final ServiceName name
    ){
        super.setSystemMessageRecordFactoryServiceName(name);
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                if(service instanceof ServiceBase){
                    ((ServiceBase)service)
                        .setSystemMessageRecordFactoryServiceName(name);
                }
            }
        }
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