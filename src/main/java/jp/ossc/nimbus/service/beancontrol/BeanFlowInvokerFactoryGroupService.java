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
package jp.ossc.nimbus.service.beancontrol;

import java.util.*;

import jp.ossc.nimbus.core.*;

import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.InvalidConfigurationException;

/**
 * {@link BeanFlowInvokerFactory}グルーピングサービス。<p>
 * 
 * @author M.Takata
 */
public class BeanFlowInvokerFactoryGroupService extends ServiceBase
 implements BeanFlowInvokerFactory, BeanFlowInvokerFactoryGroupServiceMBean{
    
    private static final long serialVersionUID = 5688605624954637499L;
    
    private ServiceName[] beanFlowInvokerFactoryServiceNames;
    private BeanFlowInvokerFactory[] beanFlowInvokerFactories;
    
    // BeanFlowInvokerFactoryGroupServiceMBeanのJavaDoc
    public void setBeanFlowInvokerFactoryServiceNames(ServiceName[] names){
        beanFlowInvokerFactoryServiceNames = names;
    }
    // BeanFlowInvokerFactoryGroupServiceMBeanのJavaDoc
    public ServiceName[] getBeanFlowInvokerFactoryServiceNames(){
        return beanFlowInvokerFactoryServiceNames;
    }
    
    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceNames == null
            || beanFlowInvokerFactoryServiceNames.length == 0){
            throw new IllegalArgumentException(
                "BeanFlowInvokerFactoryServiceNames must be specified."
            );
        }
        beanFlowInvokerFactories = new BeanFlowInvokerFactory[
            beanFlowInvokerFactoryServiceNames.length
        ];
        for(int i = 0; i < beanFlowInvokerFactoryServiceNames.length; i++){
            beanFlowInvokerFactories[i]
                 = (BeanFlowInvokerFactory)ServiceManagerFactory
                    .getServiceObject(beanFlowInvokerFactoryServiceNames[i]);
        }
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public BeanFlowInvoker createFlow(String key){
        for(int i = 0; i < beanFlowInvokerFactories.length; i++){
            if(beanFlowInvokerFactories[i].getBeanFlowKeySet().contains(key)){
                return beanFlowInvokerFactories[i].createFlow(key);
            }
        }
        throw new InvalidConfigurationException(key + " no mapped FLOW");
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public BeanFlowInvoker createFlow(String key, String caller, boolean isOverwride){
        for(int i = 0; i < beanFlowInvokerFactories.length; i++){
            if(beanFlowInvokerFactories[i].getBeanFlowKeySet().contains(key)){
                return beanFlowInvokerFactories[i].createFlow(key, caller, isOverwride);
            }
        }
        throw new InvalidConfigurationException(key + " no mapped FLOW");
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public Set getBeanFlowKeySet(){
        final Set result = new HashSet();
        if(beanFlowInvokerFactories == null){
            return result;
        }
        for(int i = 0; i < beanFlowInvokerFactories.length; i++){
            result.addAll(beanFlowInvokerFactories[i].getBeanFlowKeySet());
        }
        return result;
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public boolean containsFlow(String key){
        for(int i = 0; i < beanFlowInvokerFactories.length; i++){
            if(beanFlowInvokerFactories[i].containsFlow(key)){
                return true;
            }
        }
        return false;
    }
}