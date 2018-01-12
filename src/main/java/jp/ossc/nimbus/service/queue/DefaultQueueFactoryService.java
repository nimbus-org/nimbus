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

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * デフォルトキューファクトリ。<p>
 * {@link DefaultQueueService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see DefaultQueueService
 */
public class DefaultQueueFactoryService
 extends ServiceFactoryServiceBase
 implements DefaultQueueFactoryServiceMBean{
    
    private static final long serialVersionUID = 6777617585625094732L;
    
    protected final DefaultQueueService template = new DefaultQueueService();
    
    /**
     * {@link DefaultQueueService}サービスを生成する。<p>
     *
     * @return DefaultQueueServiceサービス
     * @exception Exception DefaultQueueServiceの生成・起動に失敗した場合
     * @see DefaultQueueService
     */
    protected Service createServiceInstance() throws Exception{
        final DefaultQueueService queue = new DefaultQueueService();
        queue.setInitialCapacity(getInitialCapacity());
        queue.setCapacityIncrement(getCapacityIncrement());
        queue.setCacheServiceName(getCacheServiceName());
        return queue;
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public void setInitialCapacity(int initial){
        template.setInitialCapacity(initial);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultQueueService queue
                 = (DefaultQueueService)instances.next();
            queue.setInitialCapacity(initial);
        }
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public int getInitialCapacity(){
        return template.getInitialCapacity();
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public void setCapacityIncrement(int increment){
        template.setCapacityIncrement(increment);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultQueueService queue
                 = (DefaultQueueService)instances.next();
            queue.setCapacityIncrement(increment);
        }
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public int getCapacityIncrement(){
        return template.getCapacityIncrement();
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public void setCacheServiceName(ServiceName name){
         template.setCacheServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultQueueService queue
                 = (DefaultQueueService)instances.next();
            queue.setCacheServiceName(name);
        }
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public ServiceName getCacheServiceName(){
        return template.getCacheServiceName();
    }
    
    // DefaultQueueFactoryServiceMBeanのJavaDoc
    public void clear(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultQueueService queue
                 = (DefaultQueueService)instances.next();
            queue.clear();
        }
    }
}
