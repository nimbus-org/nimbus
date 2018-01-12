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
 * キャッシュサイズあふれ検証ファクトリ。<p>
 * {@link SoftReferenceOverflowActionService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see SoftReferenceOverflowActionService
 */
public class SoftReferenceOverflowActionFactoryService
 extends ServiceFactoryServiceBase
 implements SoftReferenceOverflowActionFactoryServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = 3881895082610167932L;
    
    private final SoftReferenceOverflowActionService template
         = new SoftReferenceOverflowActionService();
    
    /**
     * {@link SoftReferenceOverflowActionService}サービスを生成する。<p>
     *
     * @return SoftReferenceOverflowActionServiceサービス
     * @exception Exception SoftReferenceOverflowActionServiceの生成・起動に失敗した場合
     * @see SoftReferenceOverflowActionService
     */
    protected Service createServiceInstance() throws Exception{
        final SoftReferenceOverflowActionService action
             = new SoftReferenceOverflowActionService();
        action.setPersistCacheServiceName(
            template.getPersistCacheServiceName()
        );
        action.setPersistCacheMapServiceName(
            template.getPersistCacheMapServiceName()
        );
        return action;
    }
    
    // SoftReferenceOverflowActionFactoryServiceMBeanのJavaDoc
    public void setPersistCacheServiceName(ServiceName name){
        template.setPersistCacheServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final SoftReferenceOverflowActionService action
                 = (SoftReferenceOverflowActionService)instances.next();
            action.setPersistCacheServiceName(name);
        }
    }
    
    // SoftReferenceOverflowActionFactoryServiceMBeanのJavaDoc
    public ServiceName getPersistCacheServiceName(){
        return template.getPersistCacheServiceName();
    }
    
    // SoftReferenceOverflowActionFactoryServiceMBeanのJavaDoc
    public void setPersistCacheMapServiceName(ServiceName name){
        template.setPersistCacheMapServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final SoftReferenceOverflowActionService action
                 = (SoftReferenceOverflowActionService)instances.next();
            action.setPersistCacheMapServiceName(name);
        }
    }
    
    // SoftReferenceOverflowActionFactoryServiceMBeanのJavaDoc
    public ServiceName getPersistCacheMapServiceName(){
        return template.getPersistCacheMapServiceName();
    }
    
    // SoftReferenceOverflowActionFactoryServiceMBeanのJavaDoc
    public void reset(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final SoftReferenceOverflowActionService action
                 = (SoftReferenceOverflowActionService)instances.next();
            action.reset();
        }
    }
}
