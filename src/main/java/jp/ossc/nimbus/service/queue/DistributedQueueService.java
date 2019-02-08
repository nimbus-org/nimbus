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
import jp.ossc.nimbus.lang.IllegalServiceStateException;
import jp.ossc.nimbus.service.context.SharedContext;
import jp.ossc.nimbus.service.context.SharedContextValueDifference;
import jp.ossc.nimbus.service.context.SharedContextUpdateListener;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * 分散Queue。<p>
 * 
 * @author M.Takata
 */
public class DistributedQueueService extends ServiceBase
 implements Queue, SharedContextUpdateListener, DistributedQueueServiceMBean{
    
    private static final long serialVersionUID = -752202000609727763L;

    private ServiceName distributedQueueSelectorServiceName;
    
    private DistributedQueueSelector selector;
    
    
    protected long sleepTime = 10000;
    
    protected int maxThresholdSize = -1;
    
    protected SynchronizeMonitor pushMonitor = new WaitSynchronizeMonitor();
    protected SynchronizeMonitor getMonitor = new WaitSynchronizeMonitor();
    protected SynchronizeMonitor peekMonitor = new WaitSynchronizeMonitor();
    
    /**
     * 強制終了フラグ。<p>
     */
    protected volatile boolean fourceEndFlg = false;
    
    protected long count = 0;
    protected long countDelta = 0;
    protected long lastPushedTime = 0;
    protected long lastDepth = 0;
    protected long maxDepth = 0;
    protected boolean isSafeGetOrder = true;
    protected Class synchronizeMonitorClass = WaitSynchronizeMonitor.class;
    protected Random random = new Random();
    
    // DistributedQueueServiceMBeanのJavaDoc
    public void setSynchronizeMonitorClass(Class clazz){
        synchronizeMonitorClass = clazz;
    }
    // DistributedQueueServiceMBeanのJavaDoc
    public Class getSynchronizeMonitorClass(){
        return synchronizeMonitorClass;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public void setDistributedQueueSelectorServiceName(ServiceName name){
        distributedQueueSelectorServiceName = name;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public ServiceName getDistributedQueueSelectorServiceName(){
        return distributedQueueSelectorServiceName;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public void setSleepTime(long millis){
        sleepTime = millis;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public long getSleepTime(){
        return sleepTime;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public void setMaxThresholdSize(int size){
        maxThresholdSize = size;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public int getMaxThresholdSize(){
        return maxThresholdSize;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public boolean isSafeGetOrder(){
        return isSafeGetOrder;
    }
    // DistributedQueueServiceMBeanのJavaDoc
    public void setSafeGetOrder(boolean isSafe){
        isSafeGetOrder = isSafe;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public long getCount(){
        return count;
    }
    
    public int getWaitCount(){
        Queue[] queues = selector == null ? null : selector.getQueues();
        int result = 0;
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                result += queues[i].getWaitCount();
            }
        }
        result += getMonitor.getWaitCount();
        return result;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public long getCountDelta(){
        long delta = countDelta;
        countDelta = 0;
        return delta;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public long getLastPushedTimeMillis(){
        return lastPushedTime;
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public Date getLastPushedTime(){
        return new Date(lastPushedTime);
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public long getDepth(){
        return size();
    }
    
    // DistributedQueueServiceMBeanのJavaDoc
    public long getDepthDelta(){
        long depth = size();
        
        long delta = depth - lastDepth;
        lastDepth = depth;
        return delta;
    }
    
    public long getMaxDepth(){
        return maxDepth;
    }
    
    public void startService() throws Exception{
        if(!WaitSynchronizeMonitor.class.equals(synchronizeMonitorClass)){
            pushMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
            getMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
            peekMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
        }
        selector = (DistributedQueueSelector)ServiceManagerFactory
            .getServiceObject(distributedQueueSelectorServiceName);
        
        Queue[] queues = selector.getQueues();
        for(int i = 0; i < queues.length; i++){
            if(queues[i] instanceof SharedQueueService){
                SharedQueueService sharedQueue = (SharedQueueService)queues[i];
                sharedQueue.addSharedContextUpdateListener(this);
            }
        }
        
        accept();
    }
    
    public void stopService() throws Exception{
        release();
    }
    
    protected Queue getPushQueue(Object item){
        return selector.selectQueue(item);
    }
    
    protected synchronized Queue getGetQueue(){
        Queue[] queues = selector.getQueues();
        Queue queue = null;
        int max = -1;
        List tmpQueues = new ArrayList(queues.length);
        for(int i = 0; i < queues.length; i++){
            int depth = queues[i].size() - queues[i].getWaitCount();
            if(depth > 1){
                tmpQueues.add(queues[i]);
            }
            if(depth > max){
                max = depth;
                queue = queues[i];
            }
        }
        if(tmpQueues.size() != 0){
            if(tmpQueues.size() == 1){
                queue = (Queue)tmpQueues.get(0);
            }else{
                queue = (Queue)tmpQueues.get(random.nextInt(tmpQueues.size() - 1));
            }
        }
        return queue;
    }
    
    public void push(Object item){
        push(item, -1l);
    }
    
    public boolean push(Object item, long timeout){
        return pushElement(item, timeout);
    }
    
    protected boolean pushElement(Object element, long timeout){
        if(getState() != STARTED || fourceEndFlg){
            throw new IllegalServiceStateException(this);
        }
        final long startTime = timeout > 0 ? System.currentTimeMillis() : 0;
        Queue queue = getPushQueue(element);
        while(maxThresholdSize > 0
             && (pushMonitor.isWait()
                    || (size() >= maxThresholdSize))
             && !fourceEndFlg
        ){
            try{
                if(timeout == 0){
                    return false;
                }else if(timeout < 0){
                    pushMonitor.initAndWaitMonitor(sleepTime);
                }else{
                    final long curTimeout = timeout - (System.currentTimeMillis() - startTime);
                    if(curTimeout <= 0 || !pushMonitor.initAndWaitMonitor(curTimeout)){
                        return false;
                    }
                }
            }catch(InterruptedException e){
                return false;
            }finally{
                pushMonitor.releaseMonitor();
            }
        }
        queue.push(element);
        int size = size();
        if(size > maxDepth){
            maxDepth = size;
        }
        count++;
        countDelta++;
        lastPushedTime = System.currentTimeMillis();
        
        peekMonitor.notifyAllMonitor();
        if(isSafeGetOrder){
            getMonitor.notifyMonitor();
        }else{
            getMonitor.notifyAllMonitor();
        }
        if(pushMonitor.isWait() && size() < maxThresholdSize){
            pushMonitor.notifyMonitor();
        }
        return true;
    }
    
    public Object get(){
        return get(-1);
    }
    
    public Object get(long timeOutMs){
        return getQueueElement(timeOutMs, true);
    }
    
    public Object peek(){
        return peek(-1);
    }
    
    public Object peek(long timeOutMs){
        return getQueueElement(timeOutMs, false);
    }
    
    protected Object getQueueElement(long timeOutMs, boolean isRemove){
        final Thread current = Thread.currentThread();
        long processTime = 0;
        try{
            if(isRemove){
                getMonitor.initMonitor();
            }else{
                peekMonitor.initMonitor();
            }
            Queue queue = null;
            // 強制終了でない場合
            while(!fourceEndFlg){
                // キューに溜まっている場合
                if(size() > 0){
                    // 参照するだけの場合
                    // または、このスレッドが一番最初に待っていた場合
                    if(!isRemove
                        || !isSafeGetOrder
                        || getMonitor.isFirst()
                    ){
                        queue = getGetQueue();
                        Object ret = null;
                        if(isRemove){
                            ret = queue.get(timeOutMs);
                        }else{
                            ret = queue.peek(timeOutMs);
                        }
                        if(isRemove){
                            getMonitor.releaseMonitor();
                        }
                        
                        // 参照ではなく、キューに溜まっていて、
                        // 次に待っているスレッドがいる場合
                        if(isRemove && size() > 0 && getMonitor.isWait()){
                            if(isSafeGetOrder){
                                getMonitor.notifyMonitor();
                            }else{
                                getMonitor.notifyAllMonitor();
                            }
                        }
                        if(isRemove){
                            if(pushMonitor.isWait() && size() < maxThresholdSize){
                                pushMonitor.notifyMonitor();
                            }
                        }
                        return ret;
                    }
                    // 参照ではなく、このスレッドよりも前に待っていたスレッドがいる場合
                    else if(getMonitor.isWait()){
                        // 一番最初に待っているスレッドを起こす
                        getMonitor.notifyMonitor();
                    }
                }
                
                // キューに溜まっていない場合
                // または、このスレッドよりも前に待っていたスレッドがいる場合
                
                // 強制終了またはタイムアウトの場合
                if(fourceEndFlg || timeOutMs == 0 || (timeOutMs > 0 && timeOutMs <= processTime)){
                    break;
                }
                
                // タイムアウト指定がある場合は、タイムアウトまでsleepする
                // タイムアウト指定がない場合は、sleepTime分sleepしてみる
                long proc = 0;
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis();
                }
                try{
                    long curSleepTime = timeOutMs >= 0 ? timeOutMs - processTime : sleepTime;
                    if(curSleepTime > 0){
                        if(size() == 0
                            || !isRemove
                            || (isSafeGetOrder && !getMonitor.isFirst())
                        ){
                            if(isRemove){
                                getMonitor.initAndWaitMonitor(curSleepTime);
                            }else{
                                peekMonitor.initAndWaitMonitor(curSleepTime);
                            }
                        }
                    }
                }catch(InterruptedException e){
                    return null;
                }
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis() - proc;
                    processTime += proc;
                }
            }
            
            // 強制終了の場合
            if(fourceEndFlg){
                queue = getGetQueue();
                if(queue == null){
                    return null;
                }else if(isRemove){
                    return queue.get(timeOutMs);
                }else{
                    return queue.peek(timeOutMs);
                }
            }
            // タイムアウトの場合
            else{
                if(isRemove
                    && size() > 0
                    && getMonitor.isWait()
                ){
                    if(isSafeGetOrder){
                        getMonitor.notifyMonitor();
                    }else{
                        getMonitor.notifyAllMonitor();
                    }
                }
                
                return null;
            }
        }finally{
            if(isRemove){
                getMonitor.releaseMonitor();
            }else{
                peekMonitor.releaseMonitor();
            }
        }
    }
    
    public Object remove(Object item){
        Queue[] queues = selector.getQueues();
        Object result = null;
        for(int i = 0; i < queues.length; i++){
            Object removed = queues[i].remove(item);
            if(result == null){
                result = removed;
                break;
            }
        }
        return result;
    }
    
    public void clear(){
        Queue[] queues = selector.getQueues();
        for(int i = 0; i < queues.length; i++){
            queues[i].clear();
        }
    }
    
    public int size(){
        if(selector == null){
            return 0;
        }
        Queue[] queues = selector.getQueues();
        int size = 0;
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                size += queues[i].size();
            }
        }
        return size;
    }
    
    public void accept(){
        Queue[] queues = selector.getQueues();
        for(int i = 0; i < queues.length; i++){
            queues[i].accept();
        }
        fourceEndFlg = false;
    }
    
    public void release(){
        fourceEndFlg = true;
        while(getMonitor.isWait()){
            getMonitor.notifyMonitor();
            Thread.yield();
        }
        peekMonitor.notifyAllMonitor();
        Thread.yield();
        while(pushMonitor.isWait()){
            pushMonitor.notifyMonitor();
            Thread.yield();
        }
        Queue[] queues = selector.getQueues();
        for(int i = 0; i < queues.length; i++){
            queues[i].release();
        }
    }
    
    public boolean onPutBefore(SharedContext context, boolean isLocal, Object key, Object value){return true;}
    public void onPutAfter(SharedContext context, boolean isLocal, Object key, Object value, Object old){
        int size = size();
        if(size > maxDepth){
            maxDepth = size;
        }
        count++;
        countDelta++;
        lastPushedTime = System.currentTimeMillis();
        
        peekMonitor.notifyAllMonitor();
        if(isSafeGetOrder){
            getMonitor.notifyMonitor();
        }else{
            getMonitor.notifyAllMonitor();
        }
        if(pushMonitor.isWait() && size() < maxThresholdSize){
            pushMonitor.notifyMonitor();
        }
    }
    public boolean onPutSynchronize(SharedContext context, Object key, Object value){return true;}
    public boolean onUpdateBefore(SharedContext context, boolean isLocal, Object key, SharedContextValueDifference diff){return true;}
    public void onUpdateAfter(SharedContext context, boolean isLocal, Object key, SharedContextValueDifference diff){}
    public boolean onRemoveBefore(SharedContext context, boolean isLocal, Object key){return true;}
    public void onRemoveAfter(SharedContext context, boolean isLocal, Object key, Object removed){}
    public void onClearSynchronize(SharedContext context){}
    public void onChangeMain(SharedContext context){}
    public void onChangeSub(SharedContext context){}
}
