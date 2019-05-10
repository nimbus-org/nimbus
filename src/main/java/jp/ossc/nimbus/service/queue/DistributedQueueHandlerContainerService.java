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

/**
 * 分散QueueHandlerコンテナサービス。<p>
 *
 * @author M.Takata
 */
public class DistributedQueueHandlerContainerService extends ServiceBase
 implements QueueHandlerContainer, DistributedQueueHandlerContainerServiceMBean{

    private static final long serialVersionUID = 4594481433048573418L;

    protected ServiceName distributedQueueSelectorServiceName;
    protected DistributedQueueSelector distributedQueueSelector;

    protected Daemon[] daemons;
    protected QueueReceiver[] invokers;
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
    protected boolean isSuspend;
    protected boolean isIgnoreNullElement;

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setDistributedQueueSelectorServiceName(ServiceName name){
        distributedQueueSelectorServiceName = name;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public ServiceName getDistributedQueueSelectorServiceName(){
        return distributedQueueSelectorServiceName;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
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
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public ServiceName getQueueHandlerServiceName(){
        return queueHandlerServiceName;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setReleaseQueue(boolean isRelease){
        isReleaseQueue = isRelease;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public boolean isReleaseQueue(){
        return isReleaseQueue;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setWaitTimeout(long timeout){
        waitTimeout = timeout;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public long getWaitTimeout(){
        return waitTimeout;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setMaxRetryCount(int count){
        maxRetryCount = count;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public int getMaxRetryCount(){
        return maxRetryCount;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setHandlingErrorMessageId(String id){
        handlingErrorMessageId = id;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public String getHandlingErrorMessageId(){
        return handlingErrorMessageId;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setRetryOverErrorMessageId(String id){
        retryOverErrorMessageId = id;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public String getRetryOverErrorMessageId(){
        return retryOverErrorMessageId;
    }
    
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setIgnoreNullElement(boolean isIgnore){
        isIgnoreNullElement = isIgnore;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public boolean isIgnoreNullElement(){
        return isIgnoreNullElement;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public int getQueueHandlerSize(){
        return invokers == null ? 0 : invokers.length;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public int getActiveQueueHandlerSize(){
        int count = 0;
        if(invokers == null){
            if(distributedQueueSelector != null){
                final Queue[] queues = distributedQueueSelector.getQueues();
                if(queues[0] instanceof QueueHandlerContainer){
                    for(int i = 0; i < queues.length; i++){
                        count += ((QueueHandlerContainer)queues[i]).getActiveQueueHandlerSize();
                    }
                }
            }
        }else{
            for(int i = 0; i < invokers.length; i++){
                if(invokers[i].isActive){
                    count++;
                }
            }
        }
        return count;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public int getStandbyQueueHandlerSize(){
        int count = 0;
        if(invokers == null){
            if(distributedQueueSelector != null){
                final Queue[] queues = distributedQueueSelector.getQueues();
                if(queues[0] instanceof QueueHandlerContainer){
                    for(int i = 0; i < queues.length; i++){
                        count += ((QueueHandlerContainer)queues[i]).getStandbyQueueHandlerSize();
                    }
                }
            }
        }else{
            for(int i = 0; i < invokers.length; i++){
                if(!invokers[i].isActive){
                    count++;
                }
            }
        }
        return count;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setDaemonQueueHandler(boolean isDaemon){
        isDaemonQueueHandler = isDaemon;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public boolean isDaemonQueueHandler(){
        return isDaemonQueueHandler;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public void setQueueHandlerThreadPriority(int newPriority){
        queueHandlerThreadPriority = newPriority;
    }
    // DistributedQueueHandlerContainerServiceのJavaDoc
    public int getQueueHandlerThreadPriority(){
        return queueHandlerThreadPriority;
    }

    // DistributedQueueHandlerContainerServiceのJavaDoc
    public double getAverageHandleProcessTime(){
        double time = 0;
        if(invokers == null){
            if(distributedQueueSelector != null){
                final Queue[] queues = distributedQueueSelector.getQueues();
                if(queues[0] instanceof QueueHandlerContainer){
                    for(int i = 0; i < queues.length; i++){
                        time += ((QueueHandlerContainer)queues[i]).getAverageHandleProcessTime();
                    }
                    time /= (double)queues.length;
                }
            }
        }else{
            if(invokers.length != 0){
                for(int i = 0; i < invokers.length; i++){
                    time += invokers[i].getAverageReceiveProcessTime();
                }
                time /= (double)invokers.length;
            }
        }
        return time;
    }

    public void setDistributedQueueSelector(DistributedQueueSelector selector){
        distributedQueueSelector = selector;
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(distributedQueueSelectorServiceName != null){
            distributedQueueSelector = (DistributedQueueSelector)ServiceManagerFactory.getServiceObject(distributedQueueSelectorServiceName);
        }

        if(distributedQueueSelector == null){
            throw new IllegalArgumentException("DistributedQueueSelector is null.");
        }
        // キュー受付開始
        accept();

        final Queue[] queues = distributedQueueSelector.getQueues();
        if(!(queues[0] instanceof QueueHandlerContainer)){
            invokers = new QueueReceiver[queues.length];
            daemons = new Daemon[invokers.length];
            for(int i = 0; i < invokers.length; i++){
                invokers[i] = new QueueReceiver(queues[i]);
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
            for(int i = 0; i < daemons.length; i++){
                daemons[i].stop();
                daemons[i] = null;
                invokers[i] = null;
            }
        }

        // キュー受付停止
        if(isReleaseQueue){
            release();
        }
        distributedQueueSelector = null;
        daemons = null;
        invokers = null;
    }

    public synchronized void resume(){
        if(!isSuspend){
            return;
        }
        isSuspend = false;
        if(daemons != null){
            for(int i = 0; i < daemons.length; i++){
                daemons[i].resume();
            }
        }
    }

    public synchronized void suspend(){
        if(isSuspend){
            return;
        }
        if(daemons != null){
            for(int i = 0; i < daemons.length; i++){
                daemons[i].suspend();
            }
        }
        isSuspend = true;
    }

    public boolean isSuspend(){
        return isSuspend;
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
            }else if(distributedQueueSelector != null){
                final Queue[] queues = distributedQueueSelector.getQueues();
                if(queues[0] instanceof QueueHandlerContainer){
                    for(int i = 0; i < queues.length; i++){
                        ((QueueHandlerContainer)queues[i]).setQueueHandler(handler);
                    }
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

    protected class QueueReceiver implements DaemonRunnable{

        protected Queue queue;

        protected QueueHandler handler;

        protected long receiveCount;
        protected long receiveProcessTime;

        public QueueReceiver(Queue queue){
            this.queue = queue;
        }

        public long getReceiveCount(){
            return receiveCount;
        }

        public long getAverageReceiveProcessTime(){
            return receiveCount == 0 ? 0 : (receiveProcessTime / receiveCount);
        }

        /**
         * 実行中かどうかを示すフラグ。<p>
         */
        public boolean isActive;

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
            return queue.get(waitTimeout);
        }

        /**
         * 引数dequeuedで渡されたオブジェクトを引数にQueueHandlerを呼び出す。<p>
         *
         * @param dequeued キューから取り出されたオブジェクト
         * @param ctrl DaemonControlオブジェクト
         */
        public void consume(Object dequeued, DaemonControl ctrl){
            if(handler == null){
                return;
            }
            if(dequeued == null && isIgnoreNullElement){
                return;
            }
            boolean isRetry = false;
            int retryCount = 0;
            receiveCount++;
            long start = System.currentTimeMillis();
            do{
                try{
                    isActive = true;
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
                    isActive = false;
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
            receiveProcessTime += (System.currentTimeMillis() - start);
        }

        /**
         * キューの中身を吐き出す。<p>
         */
        public void garbage(){
            if(queue != null){
                while(queue.size() > 0){
                    consume(queue.get(0), null);
                }
            }
        }
    }


    /**
     * 適切な分散キューの１つにデータを投入する。<p>
     *
     * @param item 投入オブジェクト
     */
    public synchronized void push(Object item){
        final Queue queue = distributedQueueSelector.selectQueue(item);
        queue.push(item);
    }

    /**
     * 適切な分散キューの１つにデータを投入する。<p>
     *
     * @param item 投入オブジェクト
     * @param timeout タイムアウト[ms]
     * @return タイムアウトした場合false
     */
    public synchronized boolean push(Object item, long timeout){
        final Queue queue = distributedQueueSelector.selectQueue(item);
        return queue.push(item, timeout);
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

    /**
     * キューからデータを読む。<p>
     * サポートしません。<br>
     *
     * @return キュー取得オブジェクト
     */
    public Object peek(){
        throw new UnsupportedOperationException();
    }

    /**
     * キューからデータを読む。<br>
     * サポートしません。<br>
     *
     * @param timeOutMs タイムアウト[ms]
     * @return キュー取得オブジェクト
     */
    public Object peek(long timeOutMs){
        throw new UnsupportedOperationException();
    }

    /**
     * キューから指定したデータを削除する。<p>
     * サポートしません。<br>
     *
     * @param item 削除対象のオブジェクト
     */
    public Object remove(Object item){
        throw new UnsupportedOperationException();
    }

    /**
     * 全ての分散キューを初期化する。<p>
     */
    public void clear(){
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                queues[i].clear();
            }
        }
    }

    /**
     * 全ての分散キューの合計サイズを取得する。<p>
     *
     * @return キュー格納件数
     */
    public int size(){
        int size = 0;
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                size += queues[i].size();
            }
        }
        return size;
    }

    /**
     * 全ての分散キューに投入された件数を取得する。<p>
     *
     * @return キュー投入件数
     */
    public long getCount(){
        long count = 0;
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                count += queues[i].getCount();
            }
        }
        return count;
    }
    
    public int getWaitCount(){
        int count = 0;
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                count += queues[i].getWaitCount();
            }
        }
        return count;
    }

    public long getDepth(){
        long depth = 0;
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                depth += queues[i].size();
            }
        }
        return depth;
    }

    /**
     * 全ての分散キューのキュー取得待ちを開始する。<p>
     * {@link #release()}呼出し後に、キュー取得待ちを受け付けるようにする。
     */
    public void accept(){
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                queues[i].accept();
            }
        }
    }

    /**
     * 全ての分散キューのキュー取得待ちを開放し、キュー取得待ちを受け付けないようにする。<p>
     */
    public void release(){
        final Queue[] queues = distributedQueueSelector.getQueues();
        if(queues != null){
            for(int i = 0; i < queues.length; i++){
                queues[i].release();
            }
        }
    }
}
