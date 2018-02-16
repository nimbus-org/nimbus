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
 * {@link FileCacheService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see FileCacheService
 */
public class FileCacheFactoryService
 extends AbstractCacheFactoryService
 implements FileCacheFactoryServiceMBean, Serializable{
    
    private static final long serialVersionUID = -4354172514890478044L;
    
    // AbstractCacheFactoryServiceのJavaDoc
    protected AbstractCacheService createAbstractCacheService()
     throws Exception{
        return new FileCacheService();
    }
    
    /**
     * {@link FileCacheService}サービスを生成する。<p>
     *
     * @return FileCacheServiceサービス
     * @exception Exception FileCacheServiceの生成・起動に失敗した場合
     * @see FileCacheService
     */
    protected Service createServiceInstance() throws Exception{
        final FileCacheService cache
             = (FileCacheService)super.createServiceInstance();
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        cache.setOutputDirectory(templateCache.getOutputDirectory());
        cache.setOutputPrefix(templateCache.getOutputPrefix());
        cache.setOutputSuffix(templateCache.getOutputSuffix());
        cache.setLoadOnStart(templateCache.isLoadOnStart());
        cache.setDeleteOnExitWithJVM(templateCache.isDeleteOnExitWithJVM());
        cache.setExternalizerServiceName(
            templateCache.getExternalizerServiceName()
        );
        return cache;
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public void setOutputDirectory(String path)
     throws IllegalArgumentException{
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return;
        }
        templateCache.setOutputDirectory(path);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheService cache
                 = (FileCacheService)instances.next();
            cache.setOutputDirectory(path);
        }
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public String getOutputDirectory(){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return null;
        }
        return templateCache.getOutputDirectory();
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public void setOutputPrefix(String prefix){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return;
        }
        templateCache.setOutputPrefix(prefix);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheService cache
                 = (FileCacheService)instances.next();
            cache.setOutputPrefix(prefix);
        }
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public String getOutputPrefix(){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return null;
        }
        return templateCache.getOutputPrefix();
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public void setOutputSuffix(String suffix){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return;
        }
        templateCache.setOutputSuffix(suffix);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheService cache
                 = (FileCacheService)instances.next();
            cache.setOutputSuffix(suffix);
        }
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public String getOutputSuffix(){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return null;
        }
        return templateCache.getOutputSuffix();
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public void setLoadOnStart(boolean isLoad){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return;
        }
        templateCache.setLoadOnStart(isLoad);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheService cache
                 = (FileCacheService)instances.next();
            cache.setLoadOnStart(isLoad);
        }
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public boolean isLoadOnStart(){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return false;
        }
        return templateCache.isLoadOnStart();
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public void setDeleteOnExitWithJVM(boolean isDeleteOnExit){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return;
        }
        templateCache.setDeleteOnExitWithJVM(isDeleteOnExit);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheService cache
                 = (FileCacheService)instances.next();
            cache.setDeleteOnExitWithJVM(isDeleteOnExit);
        }
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public boolean isDeleteOnExitWithJVM(){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return false;
        }
        return templateCache.isDeleteOnExitWithJVM();
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public void setExternalizerServiceName(ServiceName name){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return;
        }
        templateCache.setExternalizerServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final FileCacheService cache
                 = (FileCacheService)instances.next();
            cache.setExternalizerServiceName(name);
        }
    }
    
    // FileCacheFactoryServiceMBeanのJavaDoc
    public ServiceName getExternalizerServiceName(){
        final FileCacheService templateCache
             = (FileCacheService)getTemplate();
        if(templateCache == null){
            return null;
        }
        return templateCache.getExternalizerServiceName();
    }
}
