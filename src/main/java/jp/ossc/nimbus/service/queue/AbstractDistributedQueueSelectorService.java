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
import java.lang.ref.*;

import jp.ossc.nimbus.core.*;

/**
 * 抽象分散Queueセレクター。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractDistributedQueueSelectorService
 extends ServiceBase
 implements DistributedQueueSelector,
            AbstractDistributedQueueSelectorServiceMBean{

    private static final long serialVersionUID = -7007153954682535513L;

    protected ServiceName[] queueServiceNames;
    protected Queue[] queues;

    protected Map keyMap;
    protected KeyCounter[] keyCounters;

    protected ServiceName queueFactoryServiceName;
    protected int distributedSize = 2;

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

    public void preCreateService() throws Exception{
        super.preCreateService();
        keyMap = new WeakHashMap();
    }

    public void preStartService() throws Exception{
        super.preStartService();
        if(queueServiceNames != null && queueServiceNames.length != 0){
            queues = new Queue[queueServiceNames.length];
            for(int i = 0; i < queueServiceNames.length; i++){
                queues[i] = (Queue)ServiceManagerFactory.getServiceObject(queueServiceNames[i]);
            }
        }else if(distributedSize >= 1){
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
        keyCounters = new KeyCounter[queues.length];
        for(int i = 0; i < queues.length; i++){
            keyCounters[i] = new KeyCounter();
        }
    }

    public void postStopService() throws Exception{
        if((queueServiceNames == null || queueServiceNames.length == 0)
            && queueFactoryServiceName != null
            && queues != null){
            FactoryService factory = (FactoryService)ServiceManagerFactory.getService(queueFactoryServiceName);
            for(int i = 0; i < queues.length; i++){
                factory.release(queues[i]);
                queues[i].release();
            }
        }
        super.postStopService();
    }

    public void postDestroyService() throws Exception{
        keyMap = null;
        keyCounters = null;
        super.postDestroyService();
    }

    public Queue selectQueue(Object obj){
        Object key = getKey(obj);
        Queue queue = null;
        synchronized(keyMap){
            queue = (Queue)keyMap.get(key);
            if(queue == null){
                double minOrder = 0;
                int index = 0;
                for(int i = 0; i < queues.length; i++){
                    final double order = getQueueOrder(i, key, obj);
                    if(i == 0 || minOrder > order){
                        index = i;
                        minOrder = order;
                        queue = queues[i];
                    }
                }
                onNewKey(index, key, obj);
                keyMap.put(key, queue);
            }
        }
        return queue;
    }

    protected void onNewKey(int i, Object key, Object obj){
        keyCounters[i].add(key);
    }

    protected double getQueueOrder(int i, Object key, Object obj){
        return ((double)keyCounters[i].count() + 1) * ((double)queues[i].getCount());
    }

    public Queue[] getQueues(){
        return queues;
    }
    
    public int getKeyCount(){
        if(keyCounters == null){
            return 0;
        }
        int count = 0;
        for(int i = 0; i < keyCounters.length; i++){
            count += keyCounters[i].count();
        }
        return count;
    }
    
    public void clear(){
        if(keyMap != null){
            synchronized(keyMap){
                keyMap.clear();
                for(int i = 0; i < keyCounters.length; i++){
                    keyCounters[i].clear();
                }
            }
        }
    }
    
    protected abstract Object getKey(Object obj);
    
    protected class KeyCounter{
        Set keySet = new HashSet();
        ReferenceQueue referenceQueue = new ReferenceQueue();
        
        public void add(Object key){
            keySet.add(new WeakReference(key, referenceQueue));
        }
        
        public int count(){
            Reference ref = null;
            while((ref = referenceQueue.poll()) != null){
                keySet.remove(ref);
            }
            return keySet.size();
        }
        
        public void clear(){
            keySet.clear();
        }
    }
}