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
 *	  this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
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
package jp.ossc.nimbus.service.publish;

import java.util.*;

import jp.ossc.nimbus.core.*;

public class DefaultPublishContainerFactoryService extends ServiceBase
 implements PublishContainerFactory, DefaultPublishContainerFactoryServiceMBean,
            ServiceStateListener{
    
    private static final long serialVersionUID = 3117051157337762342L;
    
    private boolean isRegisterContainer;
    private int maxServantNum;
    private Set containers;
    private ServiceName queueServiceName;
    private int threadPriority = -1;
    private long publishTimeout = -1l;
    
    public void setRegisterContainer(boolean isRegistered){
        isRegisterContainer = isRegistered;
    }
    public boolean isRegisterContainer(){
        return isRegisterContainer;
    }
    
    public void setMaxServantNum(int maxServant){
        maxServantNum = maxServant;
    }
    public int getMaxServantNum(){
        return maxServantNum;
    }
    
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    public void setThreadPriority(int priority){
        threadPriority = priority;
    }
    public int getThreadPriority(){
        return threadPriority;
    }
    
    public void setPublishTimeout(long timeout){
        publishTimeout = timeout;
    }
    public long getPublishTimeout(){
        return publishTimeout;
    }
    
    public void createService() throws Exception{
        containers = new HashSet();
    }
    
    public void startService() throws Exception{
        final Iterator itr = containers.iterator();
        while(itr.hasNext()){
            final ServiceBase container = (ServiceBase)itr.next();
            container.start();
            container.addServiceStateListener(this);
        }
    }
    
    public void stopService() throws Exception{
        final Iterator itr = containers.iterator();
        while(itr.hasNext()){
            final ServiceBase container = (ServiceBase)itr.next();
            container.removeServiceStateListener(this);
            container.stop();
        }
    }
    
    public void destroyService() throws Exception{
        final Iterator itr = containers.iterator();
        while(itr.hasNext()){
            final Service container = (Service)itr.next();
            container.destroy();
        }
        containers.clear();
        containers = null;
    }
    
    public synchronized PublishContainer createContainer() throws Exception{
        final DefaultPublishContainerService container
             = new DefaultPublishContainerService();
        if(isRegisterContainer){
            container.setServiceManagerName(getServiceManagerName());
            container.setServiceName(container.getClass().getName() + containers.size());
        }else{
            if(getSystemLoggerServiceName() != null){
                container.setSystemLoggerServiceName(
                    getSystemLoggerServiceName()
                );
            }
            if(getSystemMessageRecordFactoryServiceName() != null){
                container.setSystemMessageRecordFactoryServiceName(
                    getSystemMessageRecordFactoryServiceName()
                );
            }
        }
        container.create();
        container.setMaxServantNum(maxServantNum);
        container.setQueueServiceName(queueServiceName);
        if(threadPriority > 0){
            container.setThreadPriority(threadPriority);
        }
        if(publishTimeout != -1){
            container.setPublishTimeout(publishTimeout);
        }
        container.start();
        container.addServiceStateListener(this);
        containers.add(container);
        return container;
    }
    
    public void stateChanged(ServiceStateChangeEvent e) throws Exception{
        if(containers == null){
            return;
        }
        containers.remove(e.getService());
    }
    
    public boolean isEnabledState(int state){
        return state == DESTROYED;
    }
}
