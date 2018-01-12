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

import java.util.*;
import javax.naming.*;
import javax.transaction.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.service.transaction.TransactionManagerFactory;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.system.Time;

/**
 * 抽象スケジューラ。<p>
 * スケジュールされたタスクを実行する責任を負う。<br>
 * {@link ScheduleManager}から実行すべき{@link Schedule}を取得して、{@link ScheduleExecutor}に実行を依頼する。<br>
 *
 * @author M.Takata
 */
public abstract class AbstractSchedulerService extends ServiceBase
 implements AbstractSchedulerServiceMBean, Scheduler{
    
    private static final long serialVersionUID = 6938915052580428501L;
    
    /**
     * TransactionManagerのJNDI名。<p>
     * J2EEの仕様で、予約されているJNDI名である。
     */
    protected static final String TRANSACTION_MANAGER_JNDI_NAME
         = "java:/TransactionManager";
    
    protected long scheduleTickerInterval = 1000l;
    
    protected ServiceName scheduleManagerServiceName;
    protected ScheduleManager scheduleManager;
    
    protected ServiceName[] scheduleExecutorServiceNames;
    protected Map scheduleExecutors;
    
    protected Daemon scheduleTicker;
    
    protected boolean isTransactionControl;
    protected TransactionManager transactionManager;
    protected ServiceName transactionManagerFactoryServiceName;
    
    protected String executorKey;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    
    protected ServiceName clusterServiceName;
    protected ClusterService cluster;
    protected ClusterListener clusterListener;
    
    protected ServiceName timeServiceName;
    protected Time time;
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setScheduleTickerInterval(long interval){
        scheduleTickerInterval = interval;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public long getScheduleTickerInterval(){
        return scheduleTickerInterval;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setScheduleManagerServiceName(ServiceName name){
        scheduleManagerServiceName = name;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getScheduleManagerServiceName(){
        return scheduleManagerServiceName;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setScheduleExecutorServiceName(ServiceName name){
        scheduleExecutorServiceNames = name == null ? null : new ServiceName[]{name};
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getScheduleExecutorServiceName(){
        return scheduleExecutorServiceNames == null || scheduleExecutorServiceNames.length != 1 ? null : scheduleExecutorServiceNames[0];
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setScheduleExecutorServiceNames(ServiceName[] names){
        scheduleExecutorServiceNames = names;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName[] getScheduleExecutorServiceNames(){
        return scheduleExecutorServiceNames;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setTransactionControl(boolean isControl){
        isTransactionControl = isControl;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public boolean isTransactionControl(){
        return isTransactionControl;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setExecutorKey(String key){
        executorKey = key;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public String getExecutorKey(){
        return executorKey;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setTransactionManagerFactoryServiceName(ServiceName name){
        transactionManagerFactoryServiceName = name;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getTransactionManagerFactoryServiceName(){
        return transactionManagerFactoryServiceName;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // AbstractSchedulerServiceMBeanのJavaDoc
    public void setTimeServiceName(ServiceName name){
        timeServiceName = name;
    }
    // AbstractSchedulerServiceMBeanのJavaDoc
    public ServiceName getTimeServiceName(){
        return timeServiceName;
    }
    
    /**
     * サービスの生成前処理を行う。<p>
     *
     * @exception Exception サービスの生成前処理に失敗した場合
     */
    public void preCreateService() throws Exception{
        super.preCreateService();
    }
    
    /**
     * サービスの開始前処理を行う。<p>
     *
     * @exception Exception サービスの開始前処理に失敗した場合
     */
    public void preStartService() throws Exception{
        super.preStartService();
        
        if(isTransactionControl){
            if(transactionManagerFactoryServiceName == null){
                final InitialContext context = new InitialContext();
                transactionManager = (TransactionManager)context.lookup(
                    TRANSACTION_MANAGER_JNDI_NAME
                );
            }else{
                TransactionManagerFactory transactionManagerFactory = (TransactionManagerFactory)ServiceManagerFactory
                    .getServiceObject(transactionManagerFactoryServiceName);
                transactionManager = transactionManagerFactory.getTransactionManager();
            }
        }
        
        if(scheduleExecutorServiceNames != null){
            scheduleExecutors = new HashMap();
            for(int i = 0; i < scheduleExecutorServiceNames.length; i++){
                ScheduleExecutor executor = (ScheduleExecutor)ServiceManagerFactory
                    .getServiceObject(scheduleExecutorServiceNames[i]);
                scheduleExecutors.put(executor.getType(), executor);
            }
        }
        if(scheduleExecutors == null || scheduleExecutors.size() == 0){
            throw new IllegalArgumentException("ScheduleExecutor is null.");
        }
        
        if(scheduleManagerServiceName != null){
            scheduleManager = (ScheduleManager)ServiceManagerFactory
                .getServiceObject(scheduleManagerServiceName);
        }
        if(scheduleManager == null){
            throw new IllegalArgumentException("ScheduleManager is null.");
        }
        
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
        }
        
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(sequenceServiceName);
        }
        
        if(timeServiceName != null){
            time = (Time)ServiceManagerFactory
                .getServiceObject(timeServiceName);
        }
    }
    
    /**
     * サービスの開始後処理を行う。<p>
     *
     * @exception Exception サービスの開始後処理に失敗した場合
     */
    public void postStartService() throws Exception{
        scheduleManager.addScheduleControlListener(this);
        
        scheduleTicker = new Daemon(new ScheduleTicker());
        scheduleTicker.setName(getServiceNameObject() + " ScheduleTicker");
        scheduleTicker.suspend();
        scheduleTicker.start();
        
        if(clusterServiceName != null){
            cluster = (ClusterService)ServiceManagerFactory.getServiceObject(clusterServiceName);
            if(cluster.isJoin()){
                throw new IllegalArgumentException("ClusterService already join.");
            }
            clusterListener = new ClusterListener();
            cluster.addClusterListener(clusterListener);
            cluster.join();
        }else{
            scheduleTicker.resume();
        }
        
        super.postStartService();
    }
    
    /**
     * サービスの停止前処理を行う。<p>
     *
     * @exception Exception サービスの停止前処理に失敗した場合
     */
    public void preStopService() throws Exception{
        
        if(scheduleTicker != null){
            scheduleTicker.stop();
        }
        
        scheduleManager.removeScheduleControlListener(this);
        
        if(cluster != null){
            cluster.removeClusterListener(clusterListener);
            clusterListener = null;
            cluster.leave();
            cluster = null;
        }
        
        super.preStopService();
    }
    
    /**
     * サービスの停止後処理を行う。<p>
     *
     * @exception Exception サービスの停止後処理に失敗した場合
     */
    public void postStopService() throws Exception{
        
        super.postStopService();
    }
    
    /**
     * サービスの破棄後処理を行う。<p>
     *
     * @exception Exception サービスの破棄後処理に失敗した場合
     */
    public void postDestroyService() throws Exception{
        
        scheduleTicker = null;
        
        super.postDestroyService();
    }
    
    /**
     * リクエスト通番を設定する{@link Context}サービスを設定する。<p>
     *
     * @param context Contextサービス
     */
    public void setThreadContext(Context context){
        threadContext = context;
    }
    
    /**
     * リクエスト通番を設定する{@link Context}サービスを取得する。<p>
     *
     * @return Contextサービス
     */
    public Context getThreadContext(){
        return threadContext;
    }
    
    /**
     * リクエスト通番を発行する{@link Sequence}サービスを設定する。<p>
     *
     * @param seq Sequenceサービス
     */
    public void setSequence(Sequence seq){
        sequence = seq;
    }
    
    /**
     * リクエスト通番を発行する{@link Sequence}サービスを取得する。<p>
     *
     * @return Sequenceサービス
     */
    public Sequence getSequence(){
        return sequence;
    }
    
    // SchedulerのJavaDoc
    public ScheduleManager getScheduleManager(){
        return scheduleManager;
    }
    
    // SchedulerのJavaDoc
    public void setScheduleManager(ScheduleManager manager){
        scheduleManager = manager;
    }
    
    // SchedulerのJavaDoc
    public ScheduleExecutor getScheduleExecutor(String type){
        if(scheduleExecutors.size() == 1){
            ScheduleExecutor executor = (ScheduleExecutor)scheduleExecutors.values().iterator().next();
            return type == null || type.equals(executor.getType()) ? executor : null;
        }else{
            return (ScheduleExecutor)scheduleExecutors.get(type);
        }
    }
    
    // SchedulerのJavaDoc
    public void setScheduleExecutor(ScheduleExecutor executor){
        scheduleExecutors.put(executor.getType(), executor);
    }
    
    // SchedulerのJavaDoc
    public Map getScheduleExecutors(){
        return scheduleExecutors;
    }
    
    // SchedulerのJavaDoc
    public void startEntry(){
        if(scheduleTicker != null){
            scheduleTicker.resume();
        }
    }
    
    // SchedulerのJavaDoc
    public boolean isStartEntry(){
        return scheduleTicker == null ? false : scheduleTicker.isRunning() && !scheduleTicker.isSusupend();
    }
    
    // SchedulerのJavaDoc
    public void stopEntry(){
        if(scheduleTicker != null){
            scheduleTicker.suspend();
        }
    }
    
    /**
     * スケジュール制御状態が変更された時に通知される。<p>
     * 実行中のスケジュールの制御状態を制御する。<br>
     *
     * @param id スケジュールID
     * @param state 変更された制御状態
     * @exception ScheduleStateControlException 実行中スケジュールの制御状態の変更に失敗した場合
     */
    public void changedControlState(String id, int state)
     throws ScheduleStateControlException{
        ScheduleExecutor[] executors = (ScheduleExecutor[])scheduleExecutors.values().toArray(
            new ScheduleExecutor[scheduleExecutors.size()]
        );
        for(int i = 0; i < executors.length; i++){
            if(executors[i].controlState(id, state)){
                break;
            }
        }
    }
    
    /**
     * スケジュールを投入するキューがJTAをサポートするかどうかを判定する。<p>
     *
     * @return JTAをサポートする場合は、true
     */
    protected abstract boolean isTransactableQueue();
    
    /**
     * スケジュールをキューに投入する。<p>
     *
     * @param request スケジュールリクエスト
     */
    protected abstract void entrySchedule(ScheduleRequest request)
     throws Throwable;
    
    /**
     * スケジュールを{@link ScheduleExecutor}に実行依頼する。<p>
     * また、{@link ScheduleManager}を使って、スケジュールの状態を変更する。<br>
     * 
     * @param request キューから取り出したスケジュールリクエスト
     */
    protected void dispatchSchedule(ScheduleRequest request){
        if(threadContext != null){
            threadContext.clear();
        }
        if(threadContext != null && request.getRequestId() != null){
            threadContext.put(
                ThreadContextKey.REQUEST_ID,
                request.getRequestId()
            );
        }
        Schedule schedule = request.getSchedule();
        ScheduleExecutor scheduleExecutor = getScheduleExecutor(schedule.getExecutorType());
        if(scheduleExecutor == null){
            getLogger().write(
                MSG_ID_NOT_FOUND_EXECUTOR_ERROR,
                new Object[]{
                    scheduleManagerServiceName,
                    schedule.getId(),
                    schedule.getMasterId()
                }
            );
            try{
                scheduleManager.changeState(
                    schedule.getId(),
                    Schedule.STATE_FAILED
                );
            }catch(ScheduleStateControlException e){
                getLogger().write(
                    MSG_ID_STATE_CHANGE_ERROR,
                    new Object[]{
                        scheduleManagerServiceName,
                        schedule.getId(),
                        schedule.getMasterId(),
                        new Integer(Schedule.STATE_FAILED)
                    },
                    e
                );
            }
            return;
        }
        try{
            schedule = scheduleExecutor.execute(schedule);
        }catch(Throwable th){
            getLogger().write(
                MSG_ID_EXECUTE_ERROR,
                new Object[]{
                    scheduleManagerServiceName,
                    schedule.getId(),
                    schedule.getMasterId()
                },
                th
            );
            try{
                scheduleManager.changeState(
                    schedule.getId(),
                    Schedule.STATE_FAILED
                );
            }catch(ScheduleStateControlException e){
                getLogger().write(
                    MSG_ID_STATE_CHANGE_ERROR,
                    new Object[]{
                        scheduleManagerServiceName,
                        schedule.getId(),
                        schedule.getMasterId(),
                        new Integer(Schedule.STATE_FAILED)
                    },
                    e
                );
            }
            return;
        }
    }
    
    protected class ClusterListener implements jp.ossc.nimbus.service.keepalive.ClusterListener{
        
        public void memberInit(Object myId, List members){}
        
        public void memberChange(List oldMembers, List newMembers){}
        
        public void changeMain() throws Exception{
            startEntry();
        }
        
        public void changeSub(){
            stopEntry();
        }
    }
    
    /**
     * スケジュールティッカー。<p>
     * スケジュールを定期的に取得して、実行キューに投入する。<br>
     *
     * @author M.Takata
     */
    protected class ScheduleTicker implements DaemonRunnable{
        
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
         * 一定時間空ける。<p>
         * 
         * @param ctrl DaemonControlオブジェクト
         * @return スケジュールの配列
         */
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(getScheduleTickerInterval(), true);
            return null;
        }
        
        /**
         * 引数dequeuedで渡されたオブジェクトを引数にQueueHandlerを呼び出す。<p>
         *
         * @param schedules キューから取り出されたオブジェクト
         * @param ctrl DaemonControlオブジェクト
         */
        public void consume(Object schedules, DaemonControl ctrl)
         throws Throwable{
            boolean rollbackMark = false;
            List scheduleList = null;
            List scheduleRequests = null;
            try{
                if(isTransactionControl){
                    transactionManager.begin();
                }
                try{
                    final String[] executorTypes = (String[])scheduleExecutors.keySet().toArray(
                        new String[scheduleExecutors.size()]
                    );
                    if(executorKey == null){
                        scheduleList = scheduleManager.findExecutableSchedules(
                            time == null ? new Date() : new Date(time.currentTimeMillis()),
                            executorTypes
                        );
                    }else{
                        scheduleList = scheduleManager.findExecutableSchedules(
                            time == null ? new Date() : new Date(time.currentTimeMillis()),
                            executorTypes,
                            executorKey
                        );
                    }
                }catch(ScheduleManageException e){
                    getLogger().write(MSG_ID_SCHEDULE_GET_ERROR, getServiceNameObject(), e);
                    rollbackMark = true;
                    return;
                }
                if(scheduleList == null || scheduleList.size() == 0){
                    return;
                }
                scheduleRequests = new ArrayList();
                final Iterator itr = scheduleList.iterator();
                while(itr.hasNext()){
                    Schedule schedule = (Schedule)itr.next();
                    final ScheduleRequest request
                        = new ScheduleRequest(schedule);
                    if(sequence != null){
                        request.setRequestId(sequence.increment());
                        if(threadContext != null){
                            threadContext.put(
                                ThreadContextKey.REQUEST_ID,
                                request.getRequestId()
                            );
                        }
                    }
                    scheduleRequests.add(request);
                    try{
                        final int nowState = scheduleManager.getState(schedule.getId());
                        switch(nowState){
                        case Schedule.STATE_INITIAL:
                        case Schedule.STATE_RETRY:
                            break;
                        default:
                            getLogger().write(
                                MSG_ID_STATE_TRANS_ERROR,
                                new Object[]{
                                    scheduleManagerServiceName,
                                    schedule.getId(),
                                    schedule.getMasterId(),
                                    new Integer(nowState),
                                    new Integer(Schedule.STATE_ENTRY)
                                }
                            );
                            return;
                        }
                        schedule.setRetry(false);
                        schedule.setOutput(null);
                        final boolean isChanged = scheduleManager.changeState(
                            schedule.getId(),
                            nowState,
                            Schedule.STATE_ENTRY,
                            null
                        );
                        if(!isChanged){
                            itr.remove();
                            getLogger().write(
                                MSG_ID_STATE_TRANS_ERROR,
                                new Object[]{
                                    scheduleManagerServiceName,
                                    schedule.getId(),
                                    schedule.getMasterId(),
                                    new Integer(nowState),
                                    new Integer(Schedule.STATE_ENTRY)
                                }
                            );
                            continue;
                        }
                    }catch(ScheduleStateControlException e){
                        getLogger().write(
                            MSG_ID_STATE_CHANGE_ERROR,
                            new Object[]{
                                scheduleManagerServiceName,
                                schedule.getId(),
                                schedule.getMasterId(),
                                new Integer(Schedule.STATE_ENTRY)
                            },
                            e
                        );
                        rollbackMark = true;
                        break;
                    }
                    if(!isTransactionControl || isTransactableQueue()){
                        getLogger().write(
                            MSG_ID_ENTRY,
                            new Object[]{
                                scheduleManagerServiceName,
                                schedule.getId(),
                                schedule.getMasterId(),
                                schedule.getInput()
                            }
                        );
                        try{
                            entrySchedule(request);
                        }catch(Throwable th){
                            getLogger().write(
                                MSG_ID_ENTRY_ERROR,
                                new Object[]{
                                    scheduleManagerServiceName,
                                    schedule.getId(),
                                    schedule.getMasterId(),
                                    schedule.getInput()
                                },
                                th
                            );
                            rollbackMark = true;
                            break;
                        }
                    }
                }
            }catch(Throwable th){
                getLogger().write(MSG_ID_UNEXPEXTED_ERROR, getServiceNameObject(), th);
                if(isTransactionControl){
                    transactionManager.rollback();
                }
                throw th;
            }finally{
                if(isTransactionControl){
                    if(rollbackMark){
                        transactionManager.rollback();
                    }else{
                        transactionManager.commit();
                    }
                }
            }
            if(scheduleRequests != null && scheduleRequests.size() != 0
                 && isTransactionControl && !isTransactableQueue()
                 && !rollbackMark){
                for(int i = 0, imax = scheduleRequests.size(); i < imax; i++){
                    final ScheduleRequest request
                        = (ScheduleRequest)scheduleRequests.get(i);
                    if(threadContext != null && request.getRequestId() != null){
                        threadContext.put(
                            ThreadContextKey.REQUEST_ID,
                            request.getRequestId()
                        );
                    }
                    final Schedule schedule = request.getSchedule();
                    getLogger().write(
                        MSG_ID_ENTRY,
                        new Object[]{
                            scheduleManagerServiceName,
                            schedule.getId(),
                            schedule.getMasterId(),
                            schedule.getInput()
                        }
                    );
                    try{
                        entrySchedule(request);
                    }catch(Throwable th){
                        getLogger().write(
                            MSG_ID_ENTRY_ERROR,
                            new Object[]{
                                scheduleManagerServiceName,
                                schedule.getId(),
                                schedule.getMasterId(),
                                schedule.getInput()
                            },
                            th
                        );
                    }
                }
            }
        }
        
        /**
         * 何もしない。<p>
         */
        public void garbage(){
        }
    }
    
    /**
     * スケジュールリクエスト。<p>
     *
     * @author M.Takata
     */
    protected static class ScheduleRequest implements java.io.Serializable{
        
        private static final long serialVersionUID = 8405850740460011444L;
        protected Schedule schedule;
        protected String requestId;
        
        public ScheduleRequest(Schedule schedule){
            this.schedule = schedule;
        }
        
        public Schedule getSchedule(){
            return schedule;
        }
        public void setSchedule(Schedule schedule){
            this.schedule = schedule;
        }
        
        public String getRequestId(){
            return requestId;
        }
        public void setRequestId(String id){
            requestId = id;
        }
    }
}