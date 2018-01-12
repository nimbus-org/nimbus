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
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * QueueHandlerコンテナサービス。<p>
 * 
 * @author M.Takata
 */
public class QueueHandlerContainerService extends ServiceBase
 implements QueueHandlerContainer, QueueHandlerContainerServiceMBean{
    
    private static final long serialVersionUID = -6527205946658554031L;
    
    protected ServiceName queueServiceName;
    protected Queue requestQueue;
    protected Daemon[] daemons;
    protected QueueReceiver[] invokers;
    protected int queueHandlerSize = 1;
    protected ServiceName queueHandlerServiceName;
    protected QueueHandler queueHandler;
    protected boolean isDaemonQueueHandler = true;
    
    protected long waitTimeout = -1;
    protected int maxRetryCount = 0;
    protected long retryInterval = 1000;
    protected String handlingErrorMessageId = DEFAULT_HANDLING_ERROR_MESSAGE_ID;
    protected String retryOverErrorMessageId = DEFAULT_RETRY_OVER_ERROR_MESSAGE_ID;
    protected int queueHandlerThreadPriority = -1;
    protected boolean isReleaseQueue = true;
    protected long count = 0;
    protected boolean isQueueHandlerNowaitOnStop;
    protected boolean isGarbageQueueOnStop = true;
    protected boolean isSuspend;
    protected SynchronizeMonitor suspendMonitor = new WaitSynchronizeMonitor();
    protected boolean isIgnoreNullElement;
    protected long stopWaitTimeout = -1;
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setQueueHandlerServiceName(ServiceName name){
        if(queueHandlerServiceName == null){
            queueHandlerServiceName = name;
            if(daemons != null){
                for(int i = 0; i < daemons.length; i++){
                    daemons[i].resume();
                }
            }
        }else{
            queueHandlerServiceName = name;
        }
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public ServiceName getQueueHandlerServiceName(){
        return queueHandlerServiceName;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setQueueHandlerSize(int size){
        queueHandlerSize = size;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public int getQueueHandlerSize(){
        return queueHandlerSize;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setReleaseQueue(boolean isRelease){
        isReleaseQueue = isRelease;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public boolean isReleaseQueue(){
        return isReleaseQueue;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setWaitTimeout(long timeout){
        waitTimeout = timeout;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public long getWaitTimeout(){
        return waitTimeout;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setMaxRetryCount(int count){
        maxRetryCount = count;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public int getMaxRetryCount(){
        return maxRetryCount;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setHandlingErrorMessageId(String id){
        handlingErrorMessageId = id;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public String getHandlingErrorMessageId(){
        return handlingErrorMessageId;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setRetryOverErrorMessageId(String id){
        retryOverErrorMessageId = id;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public String getRetryOverErrorMessageId(){
        return retryOverErrorMessageId;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setGarbageQueueOnStop(boolean isGarbage){
        isGarbageQueueOnStop = isGarbage;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public boolean isGarbageQueueOnStop(){
        return isGarbageQueueOnStop;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setIgnoreNullElement(boolean isIgnore){
        isIgnoreNullElement = isIgnore;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public boolean isIgnoreNullElement(){
        return isIgnoreNullElement;
    }
    
    // QueueHandlerContainerのJavaDoc
    public int getActiveQueueHandlerSize(){
        if(invokers == null){
            return 0;
        }
        int count = 0;
        for(int i = 0; i < invokers.length; i++){
            if(invokers[i].isActive){
                count++;
            }
        }
        return count;
    }
    
    // QueueHandlerContainerのJavaDoc
    public int getStandbyQueueHandlerSize(){
        if(invokers == null){
            return 0;
        }
        int count = 0;
        for(int i = 0; i < invokers.length; i++){
            if(!invokers[i].isActive){
                count++;
            }
        }
        return count;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setDaemonQueueHandler(boolean isDaemon){
        isDaemonQueueHandler = isDaemon;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public boolean isDaemonQueueHandler(){
        return isDaemonQueueHandler;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setQueueHandlerThreadPriority(int newPriority){
        queueHandlerThreadPriority = newPriority;
    }
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public int getQueueHandlerThreadPriority(){
        return queueHandlerThreadPriority;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setQueueHandlerNowaitOnStop(boolean isNowait){
        isQueueHandlerNowaitOnStop = isNowait;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public boolean isQueueHandlerNowaitOnStop(){
        return isQueueHandlerNowaitOnStop;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public void setStopWaitTimeout(long timeout){
        stopWaitTimeout = timeout;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public long getStopWaitTimeout(){
        return stopWaitTimeout;
    }
    
    // QueueHandlerContainerServiceMBeanのJavaDoc
    public long getAverageHandleProcessTime(){
        if(invokers == null){
            return 0;
        }
        int time = 0;
        if(invokers.length != 0){
            for(int i = 0; i < invokers.length; i++){
                time += invokers[i].getAverageReceiveProcessTime();
            }
            time /= invokers.length;
        }
        return time;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception Queueサービスの取得に失敗した場合
     */
    public void startService() throws Exception{
        if(getQueueServiceName() != null){
            setQueueService((Queue)ServiceManagerFactory
                .getServiceObject(queueServiceName)
            );
        }
        if(getQueueService() == null && queueHandlerSize > 0){
            DefaultQueueService queue = new DefaultQueueService();
            queue.create();
            queue.start();
            setQueueService(queue);
        }
        if(getQueueService() == null){
            if(getQueueHandler() == null){
                throw new IllegalArgumentException("QueueHandler is null.");
            }
        }else if(queueHandlerSize > 0){
            // キュー受付開始
            getQueueService().accept();
            
            // デーモン起動
            if(queueHandlerSize < 0){
                throw new IllegalArgumentException("queueHandlerSize < 0.");
            }
            invokers = new QueueReceiver[queueHandlerSize];
            daemons = new Daemon[queueHandlerSize];
            for(int i = 0; i < queueHandlerSize; i++){
                invokers[i] = new QueueReceiver();
                invokers[i].handler = getQueueHandler();
                
                daemons[i] = new Daemon(invokers[i]);
                daemons[i].setDaemon(isDaemonQueueHandler);
                daemons[i].setName(getServiceNameObject() + " QueueReceiver" + (i + 1));
                if(queueHandlerThreadPriority > 0){
                    daemons[i].setPriority(queueHandlerThreadPriority);
                }
                if(invokers[i].handler == null){
                    daemons[i].suspend();
                }
                daemons[i].start();
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        if(daemons != null){
            // デーモン停止
            final long startTime = System.currentTimeMillis();
            for(int i = 0; i < daemons.length; i++){
                if(isQueueHandlerNowaitOnStop){
                    daemons[i].stopNoWait();
                }else if(stopWaitTimeout < 0){
                    daemons[i].stop();
                }else{
                    long currentStopWaitTime = stopWaitTimeout - (System.currentTimeMillis() - startTime);
                    if(currentStopWaitTime > 0){
                        daemons[i].stop(currentStopWaitTime);
                    }else{
                        daemons[i].stopNoWait();
                    }
                }
                daemons[i] = null;
                invokers[i] = null;
            }
        }
        
        // キュー受付停止
        if(getQueueService() != null && isReleaseQueue){
            getQueueService().release();
        }
        daemons = null;
        invokers = null;
        count = 0;
    }
    
    /**
     * 呼び出しを非同期にするための{@link Queue}サービスを設定する。<p>
     *
     * @param queue Queueサービス
     */
    public void setQueueService(Queue queue){
        this.requestQueue = queue;
    }
    
    /**
     * 呼び出しを非同期にするための{@link Queue}サービスを取得する。<p>
     *
     * @return Queueサービス
     */
    public Queue getQueueService(){
        return requestQueue;
    }
    
    /**
     * QueueHandlerを設定する。<p>
     *
     * @param handler QueueHandler
     */
    public void setQueueHandler(QueueHandler handler){
        if(queueHandler == null){
            queueHandler = handler;
            if(daemons != null){
                for(int i = 0; i < daemons.length; i++){
                    daemons[i].resume();
                }
            }
        }else{
            queueHandler = handler;
        }
    }
    
    /**
     * QueueHandlerを取得する。<p>
     *
     * @return QueueHandler
     */
    public QueueHandler getQueueHandler(){
        if(queueHandler != null){
            return queueHandler;
        }
        if(queueHandlerServiceName != null){
            return (QueueHandler)ServiceManagerFactory
                .getServiceObject(queueHandlerServiceName);
        }
        return null;
    }
    
    public void push(Object item){
        if(getQueueService() == null || queueHandlerSize == 0){
            count++;
            handleDequeuedObjectWithLock(getQueueHandler(), item, null);
        }else{
            getQueueService().push(item);
        }
    }
    
    public boolean push(Object item, long timeout){
        if(getQueueService() == null || queueHandlerSize == 0){
            count++;
            handleDequeuedObjectWithLock(getQueueHandler(), item, null);
            return true;
        }else{
            return getQueueService().push(item, timeout);
        }
    }
    
    /**
     * キューからデータを取り出す。<p>
     * サポートしません。<br>
     * 
     * @return キュー取得オブジェクト
     */
    public Object get(){
        throw new UnsupportedOperationException();
    }
    
    /**
     * キューからデータを取り出す。<p>
     * サポートしません。<br>
     * 
     * @param timeOutMs タイムアウト[ms]
     * @return キュー取得オブジェクト
     */
    public Object get(long timeOutMs){
        throw new UnsupportedOperationException();
    }
    
    public Object peek(){
        if(getQueueService() == null){
            return null;
        }else{
            return getQueueService().peek();
        }
    }
    
    public Object peek(long timeOutMs){
        if(getQueueService() == null){
            return null;
        }else{
            return getQueueService().peek(timeOutMs);
        }
    }
    
    public Object remove(Object item){
        if(getQueueService() == null){
            return null;
        }else{
            return getQueueService().remove(item);
        }
    }
    
    public void clear(){
        if(getQueueService() == null){
            return;
        }else{
            getQueueService().clear();
        }
    }
    
    public int size(){
        if(getQueueService() == null){
            return 0;
        }else{
            return getQueueService().size();
        }
    }
    
    public long getCount(){
        if(getQueueService() == null){
            return count;
        }else{
            return getQueueService().getCount();
        }
    }
    
    public int getWaitCount(){
        if(getQueueService() == null){
            return 0;
        }else{
            return getQueueService().getWaitCount();
        }
    }
    
    public long getDepth(){
        if(getQueueService() == null){
            return 0;
        }else{
            return getQueueService().size();
        }
    }
    
    public void accept(){
        if(getQueueService() != null){
            getQueueService().accept();
        }
    }
    
    public void release(){
        if(getQueueService() != null){
            getQueueService().release();
        }
        count = 0;
    }
    
    public synchronized void resume(){
        if(!isSuspend){
            return;
        }
        isSuspend = false;
        if(getQueueService() == null){
            suspendMonitor.notifyAllMonitor();
            suspendMonitor.releaseAllMonitor();
        }else{
            if(daemons != null){
                for(int i = 0; i < daemons.length; i++){
                    daemons[i].resume();
                }
            }
        }
    }
    
    public synchronized void suspend(){
        if(isSuspend){
            return;
        }
        if(getQueueService() != null){
            if(daemons != null){
                for(int i = 0; i < daemons.length; i++){
                    daemons[i].suspend();
                }
            }
        }
        isSuspend = true;
    }
    
    public boolean isSuspend(){
        return isSuspend;
    }
    
    protected void handleDequeuedObjectWithLock(QueueHandler handler, Object dequeued, QueueReceiver receiver){
        if(isSuspend){
            try{
                suspendMonitor.initAndWaitMonitor();
            }catch(InterruptedException e){
            }
        }
        handleDequeuedObject(handler, dequeued, receiver, null);
    }
    
    protected void handleDequeuedObject(QueueHandler handler, Object dequeued, QueueReceiver receiver, DaemonControl ctrl){
        if(handler == null){
            return;
        }
        boolean isRetry = false;
        int retryCount = 0;
        do{
            try{
                if(receiver != null){
                    receiver.isActive = true;
                }
                try{
                    handler.handleDequeuedObject(dequeued);
                    isRetry = false;
                }catch(Throwable th){
                    if(maxRetryCount > 0){
                        if(retryCount >= maxRetryCount){
                            isRetry = false;
                            try{
                                handler.handleRetryOver(dequeued, th);
                            }catch(Throwable th2){
                                getLogger().write(
                                    retryOverErrorMessageId,
                                    dequeued,
                                    th
                                );
                            }
                        }else{
                            isRetry = true;
                            try{
                                isRetry = handler.handleError(dequeued, th);
                            }catch(Throwable th2){
                                isRetry = false;
                                getLogger().write(
                                    handlingErrorMessageId,
                                    dequeued,
                                    th
                                );
                            }
                        }
                    }else{
                        isRetry = false;
                        try{
                            handler.handleRetryOver(dequeued, th);
                        }catch(Throwable th2){
                            getLogger().write(
                                retryOverErrorMessageId,
                                dequeued,
                                th
                            );
                        }
                    }
                }
            }finally{
                if(receiver != null){
                    receiver.isActive = false;
                }
                if(ctrl != null && ctrl.isRunning()){
                    Thread.interrupted();
                }
            }
            if(isRetry && retryInterval > 0){
                try{
                    Thread.sleep(retryInterval);
                }catch(InterruptedException e){
                    isRetry = false;
                }
            }
            retryCount++;
        }while(isRetry);
    }
    
    protected class QueueReceiver implements DaemonRunnable{
        
        protected QueueHandler handler;
        
        /**
         * 実行中かどうかを示すフラグ。<p>
         */
        public boolean isActive;
        
        protected long receiveCount;
        protected long receiveProcessTime;
        
        public long getReceiveCount(){
            return receiveCount;
        }
        
        public long getAverageReceiveProcessTime(){
            return receiveCount == 0 ? 0 : (receiveProcessTime / receiveCount);
        }
        
        /**
         * デーモンが開始した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onStart() {
            return true;
        }
        
        /**
         * デーモンが停止した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onStop() {
            return true;
        }
        
        /**
         * デーモンが中断した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onSuspend() {
            return true;
        }
        
        /**
         * デーモンが再開した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onResume() {
            return true;
        }
        
        /**
         * キューから１つ取り出して返す。<p>
         * 
         * @param ctrl DaemonControlオブジェクト
         * @return 入力オブジェクト
         */
        public Object provide(DaemonControl ctrl){
            if(handler == null){
                handler = getQueueHandler();
                if(handler == null){
                    return null;
                }
            }
            return getQueueService().get(waitTimeout);
        }
        
        /**
         * 引数dequeuedで渡されたオブジェクトを引数にQueueHandlerを呼び出す。<p>
         *
         * @param dequeued キューから取り出されたオブジェクト
         * @param ctrl DaemonControlオブジェクト
         */
        public void consume(Object dequeued, DaemonControl ctrl){
            if(dequeued == null && isIgnoreNullElement){
                return;
            }
            receiveCount++;
            long start = System.currentTimeMillis();
            try{
                handleDequeuedObject(handler, dequeued, this, ctrl);
            }finally{
                receiveProcessTime += (System.currentTimeMillis() - start);
            }
        }
        
        /**
         * キューの中身を吐き出す。<p>
         */
        public void garbage(){
            if(getQueueService() != null && isGarbageQueueOnStop){
                while(getQueueService().size() > 0){
                    consume(getQueueService().get(0), null);
                }
            }
        }
    }
}
