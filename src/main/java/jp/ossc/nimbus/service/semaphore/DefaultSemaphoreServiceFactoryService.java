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
package jp.ossc.nimbus.service.semaphore;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * DefaultSemaphoreServiceのファクトリサービス。<p>
 * 
 * @author M.Takata
 */
public class DefaultSemaphoreServiceFactoryService
 extends ServiceFactoryServiceBase
 implements DefaultSemaphoreServiceFactoryServiceMBean{
    
    private static final long serialVersionUID = -9021663656661415644L;
    
    private final DefaultSemaphoreService template
         = new DefaultSemaphoreService();
    
    /**
     * {@link DefaultSemaphoreService}サービスを生成する。<p>
     *
     * @return DefaultSemaphoreServiceサービス
     * @exception Exception DefaultSemaphoreServiceの生成・起動に失敗した場合
     * @see DefaultSemaphoreService
     */
    protected Service createServiceInstance() throws Exception{
        final DefaultSemaphoreService semaphore = new DefaultSemaphoreService();
        semaphore.setSemaphoreClassName(getSemaphoreClassName());
        semaphore.setResourceCapacity(getResourceCapacity());
        return semaphore;
    }
    
    // DefaultSemaphoreServiceMBeanのJavaDoc
    public void setSemaphoreClassName(String name){
        template.setSemaphoreClassName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultSemaphoreService semaphore
                 = (DefaultSemaphoreService)instances.next();
            semaphore.setSemaphoreClassName(name);
        }
    }
    
    // DefaultSemaphoreServiceMBeanのJavaDoc
    public String getSemaphoreClassName(){
        return template.getSemaphoreClassName();
    }
    
    // DefaultSemaphoreServiceMBeanのJavaDoc
    public void setResourceCapacity(int initial){
        template.setResourceCapacity(initial);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultSemaphoreService semaphore
                 = (DefaultSemaphoreService)instances.next();
            semaphore.setResourceCapacity(initial);
        }
    }
    
    // DefaultSemaphoreServiceMBeanのJavaDoc
    public int getResourceCapacity(){
        return template.getResourceCapacity();
    }
    
    // DefaultSemaphoreServiceMBeanのJavaDoc
    public void release(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultSemaphoreService semaphore
                 = (DefaultSemaphoreService)instances.next();
            semaphore.release();
        }
    }
    // DefaultSemaphoreServiceMBeanのJavaDoc
    public void accept(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final DefaultSemaphoreService semaphore
                 = (DefaultSemaphoreService)instances.next();
            semaphore.accept();
        }
    }
}
