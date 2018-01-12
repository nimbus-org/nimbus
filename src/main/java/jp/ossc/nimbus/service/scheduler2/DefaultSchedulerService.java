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
package jp.ossc.nimbus.service.scheduler2;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.queue.*;

/**
 * デフォルトスケジューラ。<p>
 * スケジュールを{@link Queue}に投入して、スケジュールディスパッチスレッドで待ち受け、スケジュールを実行する。<br>
 *
 * @author M.Takata
 */
public class DefaultSchedulerService extends AbstractSchedulerService
 implements DefaultSchedulerServiceMBean{
    
    private static final long serialVersionUID = 1536820942062675121L;
    protected QueueHandlerContainerService queueHandlerContainer;
    protected ServiceName queueServiceName;
    protected Queue requestQueue;
    protected int scheduleDispatcherSize = 1;
    protected boolean isDaemonScheduleDispatcher = true;
    protected long stopWaitTimeout = -1;
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    // DefaultSchedulerServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public void setScheduleDispatcherSize(int size){
        scheduleDispatcherSize = size;
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public int getScheduleDispatcherSize(){
        return scheduleDispatcherSize;
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public int getActiveScheduleDispatcherSize(){
        return queueHandlerContainer == null
            ? 0 : queueHandlerContainer.getActiveQueueHandlerSize();
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public void setDaemonScheduleDispatcher(boolean isDaemon){
        isDaemonScheduleDispatcher = isDaemon;
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public boolean isDaemonScheduleDispatcher(){
        return isDaemonScheduleDispatcher;
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public void setStopWaitTimeout(long timeout){
        stopWaitTimeout = timeout;
    }
    
    // DefaultSchedulerServiceMBeanのJavaDoc
    public long getStopWaitTimeout(){
        return stopWaitTimeout;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        queueHandlerContainer = new QueueHandlerContainerService();
        queueHandlerContainer.create();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        queueHandlerContainer.setQueueHandlerSize(scheduleDispatcherSize);
        queueHandlerContainer.setDaemonQueueHandler(isDaemonScheduleDispatcher);
        if(queueServiceName != null){
            queueHandlerContainer.setQueueServiceName(queueServiceName);
        }else if(requestQueue != null){
            queueHandlerContainer.setQueueService(requestQueue);
        }else{
            DefaultQueueService queue = new DefaultQueueService();
            queue.create();
            queue.start();
            queueHandlerContainer.setQueueService(queue);
        }
        queueHandlerContainer.setQueueHandler(new ScheduleDispatcher());
        queueHandlerContainer.setIgnoreNullElement(true);
        queueHandlerContainer.setWaitTimeout(1000l);
        queueHandlerContainer.setStopWaitTimeout(stopWaitTimeout);
        queueHandlerContainer.start();
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        queueHandlerContainer.stop();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        
        queueHandlerContainer.destroy();
        queueHandlerContainer = null;
    }
    
    /**
     * スケジュールを投入する{@link Queue}サービスを設定する。<p>
     *
     * @param queue Queueサービス
     */
    public void setQueue(Queue queue){
        this.requestQueue = queue;
    }
    
    /**
     * スケジュールを投入する{{@link Queue}サービスを取得する。<p>
     *
     * @return Queueサービス
     */
    protected Queue getQueue(){
        return requestQueue;
    }
    
    /**
     * トランザクション参加不可能なのでfalseを返す。<p>
     *
     * @return false
     */
    protected boolean isTransactableQueue(){
        return false;
    }
    
    /**
     * {@link Queue}にスケジュールリクエストを投入する。<p>
     *
     * @param request スケジュールリクエスト
     * @exception Throwable 投入に失敗した場合
     */
    protected void entrySchedule(ScheduleRequest request) throws Throwable{
        queueHandlerContainer.getQueueService().push(request);
    }
    
    /**
     * スケジュールディスパッチャ。<p>
     * スケジュールを{@link ScheduleExecutor}に実行依頼する。また、{@link ScheduleManager}を使って、スケジュールの状態を変更する。<br>
     * 
     * @author M.Takata
     */
    protected class ScheduleDispatcher implements QueueHandler{
        
        /**
         * {@link Queue}から取り出したスケジュールを{@link ScheduleExecutor}に実行依頼する。<p>
         *
         * @param obj {@link Queue}から取り出したスケジュール
         * @exception Throwable
         */
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            dispatchSchedule((ScheduleRequest)obj);
        }
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            throw th;
        }
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            throw th;
        }
    }
}