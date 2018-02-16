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
import java.beans.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.beans.*;

/**
 * 汎用ファクトリサービス。<p>
 *
 * @author M.Takata
 */
public class GenericsFactoryService extends FactoryServiceBase
 implements Map, GenericsFactoryServiceMBean{
    
    private static final long serialVersionUID = 4169448837624942601L;
    
    private static final String GFS__00001 = "GFS__00001";
    private static final String GFS__00002 = "GFS__00002";
    
    protected Class instantiateClass;
    protected Map attributes;
    protected boolean isServiceInjection;
    protected ServiceLoader loader;
    protected Map attributePropCache;
    
    public void setInstantiateClass(Class clazz){
        instantiateClass = clazz;
    }
    public Class getInstantiateClass(){
        return instantiateClass;
    }
    
    public boolean isServiceInjection(){
        return isServiceInjection;
    }
    public void setServiceInjection(boolean flg){
        isServiceInjection = flg;
    }
    
    public void createService() throws Exception{
        attributes = new HashMap();
        attributePropCache = new HashMap();
    }
    public void startService() throws Exception{
        if(instantiateClass == null){
            throw new IllegalArgumentException(
                "instantiateClass must be specified."
            );
        }
        final ServiceMetaData metaData = manager.getServiceMetaData(name);
        loader = metaData.getServiceLoader();
    }
    public void destroyService() throws Exception{
        attributes = null;
        loader = null;
        attributePropCache = null;
    }
    
    /**
     * このファクトリが提供するオブジェクトのインスタンスを生成する。<p>
     *
     * @return このファクトリが提供するオブジェクトのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected Object createInstance() throws Exception{
        final Object instance = instantiateClass.newInstance();
        setAttributes(instance);
        return instance;
    }
    
    /**
     * 指定されたインスタンスに、属性を設定する。<p>
     *
     * @param instance インスタンス
     * @exception Exception 属性の設定に失敗した場合
     */
    protected void setAttributes(Object instance) throws Exception{
        final Iterator attrNames = keySet().iterator();
        while(attrNames.hasNext()){
            final String attrName = (String)attrNames.next();
            Object attrValue = null;
            try{
                Property prop = (Property)attributePropCache.get(attrName);
                if(prop == null){
                    prop = PropertyFactory.createProperty(attrName);
                    attributePropCache.put(attrName, prop);
                }
                attrValue = get(attrName);
                Object val = attrValue;
                if(isServiceInjection){
                    if(attrValue instanceof ServiceName){
                        val = ServiceManagerFactory
                            .getServiceObject((ServiceName)attrValue);
                    }else if(attrValue instanceof ServiceName[]){
                        final ServiceName[] serviceNames 
                            = (ServiceName[])attrValue;
                        final Object[] services
                             = new Object[serviceNames.length];
                        for(int i = 0; i < serviceNames.length; i++){
                            services[i] = ServiceManagerFactory
                                .getServiceObject(serviceNames[i]);
                        }
                        val = services;
                    }
                }else if(attrValue instanceof String){
                    final Class type = prop.getPropertyType(instance);
                    final PropertyEditor editor = loader.findEditor(type);
                    if(editor instanceof ServiceNameEditor){
                        ((ServiceNameEditor)editor).setServiceManagerName(
                            getServiceManagerName()
                        );
                    }else if(editor instanceof ServiceNameArrayEditor){
                        ((ServiceNameArrayEditor)editor).setServiceManagerName(
                            getServiceManagerName()
                        );
                    }else if(editor instanceof ServiceNameRefEditor){
                        ((ServiceNameRefEditor)editor).setServiceManagerName(
                            getServiceManagerName()
                        );
                    }else if(editor instanceof ServiceNameRefArrayEditor){
                        ((ServiceNameRefArrayEditor)editor).setServiceManagerName(
                            getServiceManagerName()
                        );
                    }
                    if(editor == null){
                        throw new IllegalArgumentException(
                            "PropertyEditor not found  : " + attrName
                        );
                    }
                    editor.setAsText((String)attrValue);
                    val = editor.getValue();
                }
                prop.setProperty(instance, val);
            }catch(InvocationTargetException e){
                logger.write(
                    GFS__00001,
                    new Object[]{
                        instantiateClass.getName(),
                        attrName,
                        attrValue
                    },
                    e
                );
            }catch(NoSuchPropertyException e){
                logger.write(
                    GFS__00002,
                    new Object[]{
                        instantiateClass.getName(),
                        attrName
                    }
                );
            }
        }
    }
    
    // GenericsFactoryServiceMBeanのJavaDoc
    public Object getAttribute(String attributeName){
        return attributes.get(attributeName);
    }
    
    // GenericsFactoryServiceMBeanのJavaDoc
    public Service getService(String attributeName)
     throws ServiceNotFoundException{
         return ServiceManagerFactory.getService(
            (ServiceName)getAttribute(attributeName)
         );
    }
    
    // GenericsFactoryServiceMBeanのJavaDoc
    public Object getServiceObject(String attributeName)
     throws ServiceNotFoundException{
         return ServiceManagerFactory.getServiceObject(
            (ServiceName)getAttribute(attributeName)
         );
    }
    
    // MapのJavaDoc
    public Object get(Object key){
        return attributes.get(key);
    }
    
    // MapのJavaDoc
    public Object put(Object key, Object value){
        return attributes.put(key,value);
    }
    
    // MapのJavaDoc
    public Set keySet(){
        return attributes.keySet();
    }
    
    // MapのJavaDoc
    public Collection values(){
        return attributes.values();
    }
    
    // MapのJavaDoc
    public Object remove(Object key){
        return attributes.remove(key);
    }
    
    // MapのJavaDoc
    public void clear(){
        attributes.clear() ;
    }
    
    // MapのJavaDoc
    public boolean isEmpty(){
        return attributes.isEmpty();
    }
    
    // MapのJavaDoc
    public boolean containsKey(Object key){
        return attributes.containsKey(key);
    }
    
    // MapのJavaDoc
    public boolean containsValue(Object value) {
        return attributes.containsValue(value);
    }
    
    // MapのJavaDoc
    public Set entrySet(){
        return attributes.entrySet();
    }
    
    // MapのJavaDoc
    public int size(){
        return attributes.size();
    }
    
    // MapのJavaDoc
    public void putAll(Map t){
        attributes.putAll(t) ;
    }
}