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
import java.io.*;

import jp.ossc.nimbus.lang.IllegalServiceStateException;
import jp.ossc.nimbus.service.cache.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * デフォルトQueueサービス。<p>
 *
 * @author M.Takata
 */
public class DefaultQueueService extends ServiceBase
 implements Queue, CacheRemoveListener, DefaultQueueServiceMBean, Serializable{
    
    private static final long serialVersionUID = 4603365298600666516L;
    
    protected static final EmptyElement EMPTY = new EmptyElement();
    
    /**
     * キュー要素格納リスト。<p>
     */
    protected List queueElements;
    
    /**
     * キューの初期容量。<p>
     */
    protected int initialCapacity = -1;
    
    /**
     * キューの容量増加数。<p>
     */
    protected int capacityIncrement = -1;
    
    /**
     * キャッシュサービス名。<p>
     */
    protected ServiceName cacheServiceName;
    
    /**
     * キャッシュサービス。<p>
     */
    protected Cache cache;
    
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
    
    // DefaultQueueServiceMBeanのJavaDoc
    public void setSynchronizeMonitorClass(Class clazz){
        synchronizeMonitorClass = clazz;
    }
    // DefaultQueueServiceMBeanのJavaDoc
    public Class getSynchronizeMonitorClass(){
        return synchronizeMonitorClass;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public void setInitialCapacity(int initial){
        initialCapacity = initial;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public int getInitialCapacity(){
        return initialCapacity;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public void setCapacityIncrement(int increment){
        capacityIncrement = increment;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public int getCapacityIncrement(){
        return capacityIncrement;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public void setCacheServiceName(ServiceName name){
        cacheServiceName = name;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public ServiceName getCacheServiceName(){
        return cacheServiceName;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public void setSleepTime(long millis){
        sleepTime = millis;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getSleepTime(){
        return sleepTime;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public void setMaxThresholdSize(int size){
        maxThresholdSize = size;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public int getMaxThresholdSize(){
        return maxThresholdSize;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public boolean isSafeGetOrder(){
        return isSafeGetOrder;
    }
    // DefaultQueueServiceMBeanのJavaDoc
    public void setSafeGetOrder(boolean isSafe){
        isSafeGetOrder = isSafe;
    }
    
    public void startService() throws Exception{
        if(!WaitSynchronizeMonitor.class.equals(synchronizeMonitorClass)){
            pushMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
            getMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
            peekMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
        }
        if(queueElements == null){
            if(initialCapacity >= 0){
                if(capacityIncrement >= 0){
                    queueElements = new Vector(
                        initialCapacity,
                        capacityIncrement
                    );
                }else{
                    queueElements = Collections.synchronizedList(new ArrayList(initialCapacity));
                }
            }else{
                queueElements = Collections.synchronizedList(new ArrayList());
            }
        }
        if(cacheServiceName != null){
            cache = (Cache)ServiceManagerFactory
                .getServiceObject(cacheServiceName);
            if(cache.size() != 0){
                CachedReference[] refs = cache.toArray();
                for(int i = 0; i < refs.length; i++){
                    refs[i].addCacheRemoveListener(this);
                    queueElements.add(refs[i]);
                }
            }
        }
        accept();
    }
    public void stopService() throws Exception{
        release();
    }
    public void destroyService() throws Exception{
        queueElements = null;
        cache = null;
    }
    
    // QueueのJavaDoc
    public void push(Object item){
        push(item, -1l);
    }
    // QueueのJavaDoc
    public boolean push(Object item, long timeout){
        return pushElement(item, timeout);
    }
    
    protected boolean pushElement(Object element, long timeout){
        if(getState() != STARTED || fourceEndFlg){
            throw new IllegalServiceStateException(this);
        }
        if(maxThresholdSize > 0
             && (pushMonitor.isWait()
                    || (size() >= maxThresholdSize))
             && !fourceEndFlg
        ){
            try{
                if(timeout == 0){
                    return false;
                }else if(timeout < 0){
                    pushMonitor.initAndWaitMonitor();
                }else{
                    if(!pushMonitor.initAndWaitMonitor(timeout)){
                        return false;
                    }
                }
            }catch(InterruptedException e){
                return false;
            }finally{
                pushMonitor.releaseMonitor();
            }
        }
        
        if(cache == null){
            queueElements.add(element);
        }else{
            final CachedReference ref = cache.add(element);
            if(ref != null){
                ref.addCacheRemoveListener(this);
                queueElements.add(ref);
            }else{
                queueElements.add(element);
            }
        }
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
    
    // QueueのJavaDoc
    public Object get(long timeOutMs){
        return getQueueElement(timeOutMs, true);
    }
    
    protected Object getQueueElement(long timeOutMs, boolean isRemove){
        long processTime = 0;
        try{
            if(isRemove){
                getMonitor.initMonitor();
            }else{
                peekMonitor.initMonitor();
            }
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
                        // キューから取得する
                        final Object ret = getQueueElement(isRemove);
                        if(ret == EMPTY){
                            continue;
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
                final Object ret = getQueueElement(isRemove);
                if(ret == EMPTY){
                    return null;
                }
                return ret;
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
    
    protected Object getQueueElement(boolean isRemove){
        if(queueElements == null){
            return null;
        }
        synchronized(queueElements){
            if(queueElements == null){
                return null;
            }else if(size() == 0){
                return EMPTY;
            }
            
            Object element = null;
            if(isRemove){
                element = queueElements.remove(0);
            }else{
                element = queueElements.get(0);
            }
            if(element == null){
                return null;
            }
            if(cache == null){
                return element;
            }else{
                if(element instanceof CachedReference){
                    final CachedReference ref = (CachedReference)element;
                    final Object obj = ref.get();
                    if(isRemove){
                        cache.remove(ref);
                    }
                    return obj;
                }else{
                    return element;
                }
            }
        }
    }
    
    // QueueのJavaDoc
    public Object get(){
        return get(-1);
    }
    
    // QueueのJavaDoc
    public Object peek(long timeOutMs){
        return getQueueElement(timeOutMs, false);
    }
    
    // QueueのJavaDoc
    public Object peek(){
        return peek(-1);
    }
    
    // QueueのJavaDoc
    public Object remove(Object item){
        
        Object removed = null;
        if(cache == null){
            if(queueElements.remove(item)){
                removed = item;
            }
        }else{
            final Object[] elements = queueElements.toArray();
            for(int i = 0; i < elements.length; i++){
                final Object element = (Object)elements[i];
                if(element instanceof CachedReference){
                    final CachedReference ref = (CachedReference)element;
                    final Object obj = ref.get(this, false);
                    if((item == null && obj == null)
                        || (item != null && item.equals(obj))){
                        cache.remove(ref);
                        removed = obj;
                        break;
                    }
                }else{
                    if((item == null && element == null)
                        || (item != null && item.equals(element))){
                        if(queueElements.remove(element)){
                            removed = element;
                        }
                        break;
                    }
                }
            }
        }
        return removed;
    }
    
    // QueueのJavaDoc
    public void clear(){
        if(cache == null){
            queueElements.clear();
        }else{
            final Object[] elements = queueElements.toArray();
            for(int i = 0; i < elements.length; i++){
                final Object element = (Object)elements[i];
                if(element instanceof CachedReference){
                    final CachedReference ref = (CachedReference)element;
                    cache.remove(ref);
                }
            }
            queueElements.clear();
        }
    }
    
    // QueueのJavaDoc
    public int size(){
        if(queueElements == null){
            return 0;
        }
        return queueElements.size();
    }
    // QueueのJavaDoc
    public void accept(){
        fourceEndFlg = false;
    }
    
    // QueueのJavaDoc
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
    }
    
    public int getWaitCount(){
        return getMonitor.getWaitCount();
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public List elements(){
        if(queueElements == null){
            return new ArrayList();
        }
        return new ArrayList(queueElements);
    }
    
    public void removed(CachedReference ref){
        if(queueElements == null){
            return;
        }
        queueElements.remove(ref);
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getCount(){
        return count;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getCountDelta(){
        long delta = countDelta;
        countDelta = 0;
        return delta;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getLastPushedTimeMillis(){
        return lastPushedTime;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public Date getLastPushedTime(){
        return new Date(lastPushedTime);
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getDepth(){
        return size();
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getDepthDelta(){
        long depth = size();
        
        long delta = depth - lastDepth;
        lastDepth = depth;
        return delta;
    }
    
    // DefaultQueueServiceMBeanのJavaDoc
    public long getMaxDepth(){
        return maxDepth;
    }
    
    protected static class EmptyElement{}
}
