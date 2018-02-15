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
package jp.ossc.nimbus.service.ejb;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * リフレクションEJBファクトリファクトリ。<p>
 * {@link InvocationEJBFactoryService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see InvocationEJBFactoryService
 */
public class InvocationEJBFactoryFactoryService
 extends ServiceFactoryServiceBase
 implements InvocationEJBFactoryFactoryServiceMBean{
    
    private static final long serialVersionUID = 4470447512645614869L;
    
    protected InvocationEJBFactoryService template;
    
    /**
     * {@link InvocationEJBFactoryService}サービスを生成する。<p>
     *
     * @return InvocationEJBFactoryサービス
     * @exception Exception InvocationEJBFactoryの生成・起動に失敗した場合
     * @see InvocationEJBFactoryService
     */
    protected Service createServiceInstance() throws Exception{
        InvocationEJBFactoryService ejbFactory = new InvocationEJBFactoryService();
        ejbFactory.setRemoteCacheMapServiceName(
            getRemoteCacheMapServiceName()
        );
        ejbFactory.setJndiFinderServiceName(getJndiFinderServiceName());
        return ejbFactory;
    }
    
    protected synchronized InvocationEJBFactoryService getTemplate(){
        if(template == null){
            template = new InvocationEJBFactoryService();
        }
        return template;
    }
    
    // InvocationEJBFactoryFactoryMBeanのJavaDoc
    public void setRemoteCacheMapServiceName(ServiceName serviceName){
        getTemplate().setRemoteCacheMapServiceName(serviceName);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final InvocationEJBFactoryService ejbFactory
                 = (InvocationEJBFactoryService)instances.next();
            ejbFactory.setRemoteCacheMapServiceName(serviceName);
        }
    }
    
    // InvocationEJBFactoryFactoryMBeanのJavaDoc
    public ServiceName getRemoteCacheMapServiceName(){
        return getTemplate().getRemoteCacheMapServiceName();
    }
    
    // InvocationEJBFactoryFactoryMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName serviceName){
        getTemplate().setJndiFinderServiceName(serviceName);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final InvocationEJBFactoryService ejbFactory
                 = (InvocationEJBFactoryService)instances.next();
            ejbFactory.setJndiFinderServiceName(serviceName);
        }
    }
    
    // InvocationEJBFactoryFactoryMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return getTemplate().getJndiFinderServiceName();
    }
}
