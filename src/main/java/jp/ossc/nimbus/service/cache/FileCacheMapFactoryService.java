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

import java.io.Serializable;
import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * ファイルキャッシュマップファクトリ。<p>
 * {@link FileCacheMapService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see FileCacheMapService
 */
public class FileCacheMapFactoryService
 extends AbstractCacheMapFactoryService
 implements FileCacheMapFactoryServiceMBean, Serializable{
    
    private static final long serialVersionUID = 2429673437287340561L;
    
    protected AbstractCacheMapService createAbstractCacheMapService()
     throws Exception{
        return new FileCacheMapService();
    }
    
    /**
     * {@link FileCacheMapService}サービスを生成する。<p>
     *
     * @return FileCacheMapServiceサービス
     * @exception Exception FileCacheMapServiceの生成・起動に失敗した場合
     * @see FileCacheMapService
     */
    protected Service createServiceInstance() throws Exception{
        final FileCacheMapService cacheMap
             = (FileCacheMapService)super.createServiceInstance();
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        cacheMap.setOutputDirectory(templateCacheMap.getOutputDirectory());
        cacheMap.setFileShared(templateCacheMap.isFileShared());
        cacheMap.setOutputPrefix(templateCacheMap.getOutputPrefix());
        cacheMap.setOutputSuffix(templateCacheMap.getOutputSuffix());
        cacheMap.setLoadOnStart(templateCacheMap.isLoadOnStart());
        cacheMap.setDeleteOnExitWithJVM(
            templateCacheMap.isDeleteOnExitWithJVM()
        );
        cacheMap.setExternalizerServiceName(
            templateCacheMap.getExternalizerServiceName()
        );
        return cacheMap;
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public void setOutputDirectory(String path)
     throws IllegalArgumentException{
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setOutputDirectory(path);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setOutputDirectory(path);
        }
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public String getOutputDirectory(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return null;
        }
        return templateCacheMap.getOutputDirectory();
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public void setFileShared(boolean isShared){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setFileShared(isShared);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setFileShared(isShared);
        }
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public boolean isFileShared(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return false;
        }
        return templateCacheMap.isFileShared();
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public void setOutputPrefix(String prefix){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setOutputPrefix(prefix);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setOutputPrefix(prefix);
        }
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public String getOutputPrefix(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return null;
        }
        return templateCacheMap.getOutputPrefix();
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public void setOutputSuffix(String suffix){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setOutputSuffix(suffix);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setOutputSuffix(suffix);
        }
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public String getOutputSuffix(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return null;
        }
        return templateCacheMap.getOutputSuffix();
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public void setLoadOnStart(boolean isLoad){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setLoadOnStart(isLoad);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setLoadOnStart(isLoad);
        }
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public boolean isLoadOnStart(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return false;
        }
        return templateCacheMap.isLoadOnStart();
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public void setDeleteOnExitWithJVM(boolean isDeleteOnExit){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setDeleteOnExitWithJVM(isDeleteOnExit);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setDeleteOnExitWithJVM(isDeleteOnExit);
        }
    }
    
    // FileCacheMapFactoryServiceMBeanのJavaDoc
    public boolean isDeleteOnExitWithJVM(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return false;
        }
        return templateCacheMap.isDeleteOnExitWithJVM();
    }
    
    // FileCacheServiceMBeanのJavaDoc
    public void setExternalizerServiceName(ServiceName name){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return;
        }
        templateCacheMap.setExternalizerServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheMapService cacheMap
                 = (FileCacheMapService)instances.next();
            cacheMap.setExternalizerServiceName(name);
        }
    }
    
    // FileCacheServiceMBeanのJavaDoc
    public ServiceName getExternalizerServiceName(){
        final FileCacheMapService templateCacheMap
             = (FileCacheMapService)getTemplate();
        if(templateCacheMap == null){
            return null;
        }
        return templateCacheMap.getExternalizerServiceName();
    }
}
