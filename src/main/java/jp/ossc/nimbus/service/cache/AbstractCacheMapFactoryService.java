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
package jp.ossc.nimbus.service.cache;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * 抽象キャッシュマップファクトリ。<p>
 * {@link AbstractCacheMapService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see AbstractCacheMapService
 */
public abstract class AbstractCacheMapFactoryService
 extends ServiceFactoryServiceBase
 implements AbstractCacheMapFactoryServiceMBean{
    
    private static final long serialVersionUID = 5089920359298341584L;
    
    private AbstractCacheMapService template;;
    
    /**
     * AbstractCacheMapServiceのサブクラスのインスタンスを生成する。<p>
     *
     * @return AbstractCacheMapServiceのサブクラスのインスタンス
     * @exception Exception AbstractCacheMapServiceのサブクラスのインスタンスの生成に失敗した場合
     */
    protected abstract AbstractCacheMapService createAbstractCacheMapService()
     throws Exception;
    
    /**
     * {@link AbstractCacheMapService}サービスを生成する。<p>
     *
     * @return AbstractCacheMapServiceサービス
     * @exception Exception AbstractCacheMapServiceの生成・起動に失敗した場合
     * @see AbstractCacheMapService
     */
    protected Service createServiceInstance() throws Exception{
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return null;
        }
        final AbstractCacheMapService cacheMap
             = createAbstractCacheMapService();
        if(cacheMap == null){
            return null;
        }
        cacheMap.setOverflowControllerServiceNames(
            templateCacheMap.getOverflowControllerServiceNames()
        );
        cacheMap.setClearOnStop(templateCacheMap.isClearOnStop());
        cacheMap.setClearOnDestroy(templateCacheMap.isClearOnDestroy());
        return cacheMap;
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void setOverflowControllerServiceNames(ServiceName[] names){
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setOverflowControllerServiceNames(names);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            cacheMap.setOverflowControllerServiceNames(names);
        }
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public ServiceName[] getOverflowControllerServiceNames(){
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return null;
        }
        return templateCacheMap.getOverflowControllerServiceNames();
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void setClearOnStop(boolean isClear){
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setClearOnStop(isClear);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            cacheMap.setClearOnStop(isClear);
        }
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public boolean isClearOnStop(){
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return false;
        }
        return templateCacheMap.isClearOnStop();
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void setClearOnDestroy(boolean isClear){
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setClearOnDestroy(isClear);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            cacheMap.setClearOnDestroy(isClear);
        }
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public boolean isClearOnDestroy(){
        final AbstractCacheMapService templateCacheMap = getTemplate();
        if(templateCacheMap == null){
            return false;
        }
        return templateCacheMap.isClearOnDestroy();
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public boolean containsKey(Object key){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            if(cacheMap.containsKey(key)){
                return true;
            }
        }
        return false;
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public boolean containsValue(Object value){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            if(cacheMap.containsValue(value)){
                return true;
            }
        }
        return false;
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public KeyCachedReference getCachedReference(Object key){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            if(cacheMap.containsKey(key)){
                return cacheMap.getCachedReference(key);
            }
        }
        return null;
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public Object get(Object key){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            if(cacheMap.containsKey(key)){
                return cacheMap.get(key);
            }
        }
        return null;
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void put(Object key, Object value){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            cacheMap.put(key, value);
        }
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void putAll(Map map){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            cacheMap.putAll(map);
        }
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void remove(Object key){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            if(cacheMap.containsKey(key)){
                cacheMap.remove(key);
            }
        }
    }
    
    // AbstractCacheMapFactoryServiceMBeanのJavaDoc
    public void clear(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final AbstractCacheMapService cacheMap
                 = (AbstractCacheMapService)instances.next();
            cacheMap.clear();
        }
    }
    
    /**
     * テンプレートとなるAbstractCacheMapServiceのサブクラスのインスタンスを取得する。<p>
     * ここで取得されるテンプレートの生成には、{@link #createAbstractCacheMapService()}が使用される。生成に失敗した場合は、nullを返す。<br>
     *
     * @return テンプレートとなるAbstractCacheMapServiceのサブクラスのインスタンス
     */
    protected synchronized AbstractCacheMapService getTemplate(){
        if(template == null){
            try{
                template = createAbstractCacheMapService();
            }catch(Exception e){
                return null;
            }
        }
        return template;
    }
}
