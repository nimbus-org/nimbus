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

import jp.ossc.nimbus.core.*;

/**
 * 単純分散Queueセレクター。<p>
 * 
 * @author M.Takata
 */
public class SimpleDistributedQueueSelectorService
 extends ServiceBase
 implements DistributedQueueSelector,
            SimpleDistributedQueueSelectorServiceMBean{
    
    private static final long serialVersionUID = -5532252767404568375L;
    
    protected ServiceName[] queueServiceNames;
    protected Queue[] queues;
    
    protected ServiceName queueFactoryServiceName;
    protected int distributedSize = 2;
    
    protected int selectMode = SELECT_MODE_COUNT;
    
    public void setQueueServiceNames(ServiceName[] names){
        queueServiceNames = names;
    }
    public ServiceName[] getQueueServiceNames(){
        return queueServiceNames;
    }
    
    public void setQueueFactoryServiceName(ServiceName name){
        queueFactoryServiceName = name;
    }
    public ServiceName getQueueFactoryServiceName(){
        return queueFactoryServiceName;
    }
    
    public void setDistributedSize(int size){
        distributedSize = size;
    }
    public int getDistributedSize(){
        return distributedSize;
    }
    
    public void setSelectMode(int mode){
        selectMode = mode;
    }
    public int getSelectMode(){
        return selectMode;
    }
    
    public void startService() throws Exception{
        if(queueServiceNames != null && queueServiceNames.length != 0){
            queues = new Queue[queueServiceNames.length];
            for(int i = 0; i < queueServiceNames.length; i++){
                queues[i] = (Queue)ServiceManagerFactory.getServiceObject(queueServiceNames[i]);
            }
        }else if(distributedSize > 1){
            queues = new Queue[distributedSize];
            for(int i = 0; i < distributedSize; i++){
                if(queueFactoryServiceName == null){
                    DefaultQueueService queueService = new DefaultQueueService();
                    queueService.create();
                    queueService.start();
                    queues[i] = queueService;
                }else{
                    queues[i] = (Queue)ServiceManagerFactory.getServiceObject(queueFactoryServiceName);
                }
            }
        }else{
            throw new IllegalArgumentException("Queues must be specified.");
        }
    }
    
    public Queue selectQueue(Object obj){
        Queue queue = null;
        long min = Long.MAX_VALUE;
        for(int i = 0; i < queues.length; i++){
            long current = 0;
            switch(selectMode){
            case SELECT_MODE_SIZE:
                current = queues[i].size();
                break;
            case SELECT_MODE_COUNT:
            default:
                current = queues[i].getCount();
            }
            if(queue == null || min > current){
                min = current;
                queue = queues[i];
            }
        }
        return queue;
    }
    
    public Queue[] getQueues(){
        return queues;
    }
    
    public void clear(){
    }
}