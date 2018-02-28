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
 * デフォルトあふれ制御ファクトリ。<p>
 * {@link DefaultOverflowControllerService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see DefaultOverflowControllerService
 */
public class DefaultOverflowControllerFactoryService
 extends ServiceFactoryServiceBase
 implements DefaultOverflowControllerFactoryServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = 2169297426392809679L;
    
    private final DefaultOverflowControllerService template
         = new DefaultOverflowControllerService();
    
    /**
     * {@link DefaultOverflowControllerService}サービスを生成する。<p>
     *
     * @return DefaultOverflowControllerServiceサービス
     * @exception Exception DefaultOverflowControllerServiceの生成・起動に失敗した場合
     * @see DefaultOverflowControllerService
     */
    protected Service createServiceInstance() throws Exception{
        final DefaultOverflowControllerService controller
             = new DefaultOverflowControllerService();
        controller.setOverflowValidatorServiceName(
            template.getOverflowValidatorServiceName()
        );
        controller.setOverflowAlgorithmServiceName(
            template.getOverflowAlgorithmServiceName()
        );
        controller.setOverflowActionServiceName(
            template.getOverflowActionServiceName()
        );
        return controller;
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public void setOverflowValidatorServiceName(ServiceName name){
        template.setOverflowValidatorServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultOverflowControllerService controller
                 = (DefaultOverflowControllerService)instances.next();
            controller.setOverflowValidatorServiceName(name);
        }
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public ServiceName getOverflowValidatorServiceName(){
        return template.getOverflowValidatorServiceName();
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public void setOverflowAlgorithmServiceName(ServiceName name){
        template.setOverflowAlgorithmServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultOverflowControllerService controller
                 = (DefaultOverflowControllerService)instances.next();
            controller.setOverflowAlgorithmServiceName(name);
        }
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public ServiceName getOverflowAlgorithmServiceName(){
        return template.getOverflowAlgorithmServiceName();
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public void setOverflowActionServiceName(ServiceName name){
        template.setOverflowActionServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultOverflowControllerService controller
                 = (DefaultOverflowControllerService)instances.next();
            controller.setOverflowActionServiceName(name);
        }
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public ServiceName getOverflowActionServiceName(){
        return template.getOverflowActionServiceName();
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        template.setQueueServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultOverflowControllerService controller
                 = (DefaultOverflowControllerService)instances.next();
            controller.setQueueServiceName(name);
        }
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return template.getQueueServiceName();
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public void setPeriodicOverflowIntervalTime(long time){
        template.setPeriodicOverflowIntervalTime(time);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultOverflowControllerService controller
                 = (DefaultOverflowControllerService)instances.next();
            controller.setPeriodicOverflowIntervalTime(time);
        }
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public long getPeriodicOverflowIntervalTime(){
        return template.getPeriodicOverflowIntervalTime();
    }
    
    // DefaultOverflowControllerFactoryServiceMBeanのJavaDoc
    public void reset(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultOverflowControllerService controller
                 = (DefaultOverflowControllerService)instances.next();
            controller.reset();
        }
    }
}
