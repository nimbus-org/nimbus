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
package jp.ossc.nimbus.service.scheduler;

import java.util.*;
import java.io.Serializable;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.queue.Queue;

/**
 * タイマースケジュールサービス。<p>
 * {@link TimerSchedulerService}に登録するスケジュールを設定するサービスである。<br>
 * 以下の設定が可能である。<br>
 * <ul>
 *     <li>実行時刻</li>
 *     <li>遅延時間</li>
 *     <li>繰り返し間隔</li>
 *     <li>固定遅延実行</li>
 *     <li>非同期実行</li>
 * </ul>
 *
 * @author M.Takata
 */
public class TimerScheduleService extends ServiceBase
 implements TimerSchedule, Serializable, TimerScheduleServiceMBean{
    
    private static final long serialVersionUID = 8328157884039980522L;
    
    /**
     * スケジュール名。<p>
     */
    protected String name;
    
    /**
     * タスクサービスのサービス名。<p>
     */
    protected ServiceName taskServiceName;
    
    /**
     * タイマータスク。<p>
     */
    protected ManagedTimerTask task;
    
    /**
     * スケジュールタスク。<p>
     */
    protected transient ScheduleTask scheduleTask;
    
    /**
     * 実行開始時刻。<p>
     */
    protected Date startTime;
    
    /**
     * 実行終了時刻。<p>
     */
    protected Date endTime;
    
    /**
     * 遅延時間[ms]。<p>
     */
    protected long delay = -1;
    
    /**
     * 繰り返し間隔[ms]。<p>
     */
    protected long period = -1;
    
    /**
     * 繰り返し回数。<p>
     */
    protected int count = -1;
    
    /**
     * 固定頻度実行フラグ。<p>
     * trueの場合、固定頻度実行。<br>
     */
    protected boolean isFixedRate;
    
    /**
     * 依存するスケジュール名配列。<p>
     */
    protected String[] dependsScheduleNames;
    
    /**
     * 依存するスケジュール待ちのタイムアウト[ms]。<p>
     */
    protected long dependencyTimeout = -1;
    
    /**
     * 依存するスケジュール終了確認の間隔[ms]。<p>
     */
    protected long dependencyConfirmInterval = 1000;
    
    /**
     * エラーログのメッセージID。<p>
     */
    protected String errorLogMessageId = DEFAULT_ERROR_LOG_MESSAGE_ID;
    
    /**
     * エラーログのメッセージID。<p>
     */
    protected String timeoutLogMessageId = DEFAULT_TIMEOUT_LOG_MESSAGE_ID;
    
    /**
     * ジャーナルサービスのサービス名。<p>
     */
    protected ServiceName journalServiceName;
    
    /**
     * ジャーナルサービス。<p>
     */
    protected transient Journal journal;
    
    /**
     * Queueサービスのサービス名。<p>
     */
    protected ServiceName queueServiceName;
    
    /**
     * Queueサービス。<p>
     */
    protected transient Queue queue;
    
    /**
     * スケジュールの終了時にキューに溜まったタスクを処理するかどうかのフラグ。<p>
     */
    protected boolean isGarbageQueue = false;
    
    /**
     * 非同期処理デーモン。<p>
     */
    protected transient Daemon daemon;
    
    /**
     * スケジューラ。<p>
     */
    protected transient Scheduler scheduler;
    
    /**
     * スケジュール状態管理サービスのサービス名。<p>
     */
    protected ServiceName scheduleStateManagerServiceName;
    
    /**
     * スケジュール状態管理。<p>
     */
    protected transient ScheduleStateManager scheduleStateManager;
    
    /**
     * スケジュールを開始する時に、既に実行開始時刻を過ぎていた場合に、タスクを実行するかどうかのフラグ。<p>
     */
    protected boolean isExecuteWhenOverStartTime = true;
    
    /**
     * 空のスケジュールを作成する。<p>
     */
    public TimerScheduleService(){
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setName(String name){
        this.name = name;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setTaskServiceName(ServiceName name){
        taskServiceName = name;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public ServiceName getTaskServiceName(){
        return taskServiceName;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setStartTime(Date time){
        startTime = time;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public Date getStartTime(){
        return startTime;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setExecuteWhenOverStartTime(boolean isExecute){
        isExecuteWhenOverStartTime = isExecute;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public boolean isExecuteWhenOverStartTime(){
        return isExecuteWhenOverStartTime;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setEndTime(Date time){
        endTime = time;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public Date getEndTime(){
        return endTime;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setDelay(long delay){
        this.delay = delay;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public long getDelay(){
        return delay;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setPeriod(long period){
        this.period = period;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public long getPeriod(){
        return period;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setCount(int count){
        this.count = count;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public int getCount(){
        return count;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setFixedRate(boolean isFixedRate){
        this.isFixedRate = isFixedRate;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public boolean isFixedRate(){
        return isFixedRate;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setDependsScheduleNames(String[] names){
        dependsScheduleNames = names;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public String[] getDependsScheduleNames(){
        return dependsScheduleNames;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setDependencyTimeout(long timeout){
        dependencyTimeout = timeout;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public long getDependencyTimeout(){
        return dependencyTimeout;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setDependencyConfirmInterval(long interval){
        dependencyConfirmInterval = interval;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public long getDependencyConfirmInterval(){
        return dependencyConfirmInterval;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setErrorLogMessageId(String id){
        errorLogMessageId = id;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public String getErrorLogMessageId(){
        return errorLogMessageId;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setTimeoutLogMessageId(String id){
        timeoutLogMessageId = id;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public String getTimeoutLogMessageId(){
        return timeoutLogMessageId;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setGarbageQueue(boolean isGarbage){
        isGarbageQueue = isGarbage;
    }
    // TimerScheduleMBeanのJavaDoc
    public boolean isGarbageQueue(){
        return isGarbageQueue;
    }
    
    // TimerScheduleMBeanのJavaDoc
    public void setScheduleStateManagerServiceName(ServiceName name){
        scheduleStateManagerServiceName = name;
    }
    // TimerScheduleMBeanのJavaDoc
    public ServiceName getScheduleStateManagerServiceName(){
        return scheduleStateManagerServiceName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(taskServiceName != null){
            scheduleTask = (ScheduleTask)ServiceManagerFactory
                .getServiceObject(taskServiceName);
        }
        
        if(scheduleTask == null){
            throw new IllegalArgumentException("Task must be specified.");
        }
        task = new ManagedTimerTask(scheduleTask);
        task.isCyclic = period != -1;
        
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory
                .getServiceObject(journalServiceName);
        }
        
        if(queueServiceName != null){
            queue = (Queue)ServiceManagerFactory
                .getServiceObject(queueServiceName);
        }
        
        if(scheduleStateManager == null
             && scheduleStateManagerServiceName != null){
            scheduleStateManager = (ScheduleStateManager)ServiceManagerFactory
                .getServiceObject(scheduleStateManagerServiceName);
        }
        
        if(scheduleStateManager != null
            && ScheduleStateManager.STATE_UNKNOWN
                 == scheduleStateManager.getState(getName())){
            scheduleStateManager.changeState(
                getName(),
                ScheduleStateManager.STATE_INIT
            );
        }
        
        if(queue != null){
            daemon = new Daemon(task);
            daemon.start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        cancel();
        if(queue != null){
            
            daemon.stop();
            
            queue.release();
        }
    }
    
    // TimerScheduleのJavaDoc
    public void setScheduler(Scheduler scheduler){
        this.scheduler = scheduler;
    }
    
    // TimerScheduleのJavaDoc
    public void executeForce(){
        ManagedTimerTask tmpTask = new ManagedTimerTask(scheduleTask);
        tmpTask.isForce = true;
        tmpTask.run();
    }
    
    // TimerScheduleのJavaDoc
    public void executeForce(Timer timer, long delay){
        ManagedTimerTask tmpTask = new ManagedTimerTask(scheduleTask);
        tmpTask.isForce = true;
        timer.schedule(tmpTask, delay);
    }
    
    // TimerScheduleのJavaDoc
    public void executeForce(Timer timer, Date time){
        ManagedTimerTask tmpTask = new ManagedTimerTask(scheduleTask);
        tmpTask.isForce = true;
        timer.schedule(tmpTask, time);
    }
    
    // TimerScheduleのJavaDoc
    public void schedule(Timer timer){
        
        if(scheduleStateManager != null){
            
            switch(scheduleStateManager.getState(getName())){
            case ScheduleStateManager.STATE_CLOSE:
                task.isClosed = true;
                return;
            case ScheduleStateManager.STATE_INVALID:
                task.isValid = false;
            default:
            }
        }
        
        task.isValid = true;
        task.maxCount = count;
        Calendar nowCal = null;
        Calendar tmpCal = null;
        if(endTime != null){
            nowCal = Calendar.getInstance();
            tmpCal = Calendar.getInstance();
            tmpCal.setTime(endTime);
            if(nowCal.after(tmpCal)){
                return;
            }
        }
        
        if(!isExecuteWhenOverStartTime && startTime != null){
            if(nowCal == null){
                nowCal = Calendar.getInstance();
                tmpCal = Calendar.getInstance();
            }else{
                tmpCal.clear();
            }
            tmpCal.setTime(startTime);
            if(tmpCal.before(nowCal)){
                if(task.isCyclic){
                    long startTimeInMillis = startTime.getTime();
                    do{
                        tmpCal.clear();
                        tmpCal.setTimeInMillis(
                            startTimeInMillis += period
                        );
                    }while(tmpCal.before(nowCal));
                    startTime.setTime(startTimeInMillis);
                }else{
                    return;
                }
            }
        }
        
        if(scheduleStateManager != null){
            scheduleStateManager.changeState(
                getName(),
                ScheduleStateManager.STATE_WAIT
            );
        }
        if(isFixedRate){
            if(delay >= 0){
                timer.scheduleAtFixedRate(task, delay, period);
            }else{
                timer.scheduleAtFixedRate(
                    task,
                    startTime == null ? new Date() : startTime,
                    period
                );
            }
        }else{
            if(delay >= 0 && period > 0){
                timer.schedule(task, delay, period);
            }else if(period > 0){
                timer.schedule(
                    task,
                    startTime == null ? new Date() : startTime,
                    period
                );
            }else if(delay >= 0){
                timer.schedule(task, delay);
            }else{
                timer.schedule(
                    task,
                    startTime == null ? new Date() : startTime
                );
            }
        }
        task.isClosed = false;
        if(endTime != null){
            timer.schedule(
                new TimerTask(){
                    public void run(){
                        task.cancel();
                        task.isClosed = true;
                        if(scheduleStateManager != null){
                            scheduleStateManager.changeState(
                                getName(),
                                ScheduleStateManager.STATE_CLOSE
                            );
                        }
                    }
                },
                endTime
            );
        }
    }
    
    // TimerScheduleのJavaDoc
    public String getName(){
         return name == null ? getServiceName() : name;
    }
    
    // TimerScheduleのJavaDoc
    public void cancel(){
        if(task != null){
            task.cancel();
        }
    }
    
    // TimerScheduleのJavaDoc
    public void setValid(boolean isValid){
        if(task != null){
            task.isValid = isValid;
            
            if(!isValid && scheduleStateManager != null){
                scheduleStateManager.changeState(
                    getName(),
                    ScheduleStateManager.STATE_INVALID
                );
            }
        }
    }
    
    // TimerScheduleのJavaDoc
    public boolean isValid(){
        return task == null ? false : task.isValid;
    }
    
    // TimerScheduleのJavaDoc
    public boolean isCyclic(){
        return task == null ? false : task.isCyclic;
    }
    
    // TimerScheduleのJavaDoc
    public boolean isClosed(){
        return task == null ? true : task.isClosed;
    }
    
    // TimerScheduleのJavaDoc
    public boolean isRunning(){
        return task == null ? false : task.isRunning;
    }
    
    // TimerScheduleのJavaDoc
    public boolean isError(){
        return task == null ? false : task.isError;
    }
    
    // TimerScheduleのJavaDoc
    public boolean isWaiting(){
        return task == null ? false : task.isWaiting;
    }
    
    // TimerScheduleのJavaDoc
    public boolean isTimeout(){
        return task == null ? false : task.isTimeout;
    }
    
    // TimerScheduleのJavaDoc
    public Date getLastExecutionTime(){
        return task == null || task.lastExecutionTime == 0 ? null : new Date(task.lastExecutionTime);
    }
    
    // TimerScheduleのJavaDoc
    public Date getScheduledExecutionTime(){
        return task == null ? null : new Date(
            task.scheduledExecutionTime == 0
                 ? task.scheduledExecutionTime() : task.scheduledExecutionTime
        );
    }
    
    // ScheduleのJavaDoc
    public void setScheduleStateManager(ScheduleStateManager manager){
        scheduleStateManager = manager;
        
        if(scheduleStateManager != null
            && getState() == STARTED
            && ScheduleStateManager.STATE_UNKNOWN
                 == scheduleStateManager.getState(getName())){
            scheduleStateManager.changeState(
                getName(),
                ScheduleStateManager.STATE_INIT
            );
        }
    }
    
    // ScheduleのJavaDoc
    public ScheduleStateManager getScheduleStateManager(){
        return scheduleStateManager;
    }
    
    /**
     * タスクを設定する。<p>
     *
     * @param task タスク
     */
    public void setTask(ScheduleTask task){
        if(task == null){
            throw new IllegalArgumentException("Task is null.");
        }
        scheduleTask = task;
    }
    
    /**
     * ジャーナルサービスを設定する。<p>
     *
     * @param journal ジャーナルサービス
     */
    public void setJournal(Journal journal){
        this.journal = journal;
    }
    
    /**
     * Queueサービスを設定する。<p>
     *
     * @param queue Queueサービス
     */
    public void setQueue(Queue queue){
        this.queue = queue;
    }
    
    /**
     * 文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("name=").append(getName());
        buf.append(", startTime=").append(startTime);
        buf.append(", delay=").append(delay);
        buf.append(", period=").append(period);
        buf.append(", count=").append(count);
        buf.append(", isFixedRate=").append(isFixedRate);
        buf.append(", task=").append(task);
        buf.append(", isValid=").append(isValid());
        buf.append(", isClosed=").append(isClosed());
        buf.append(", isRunning=").append(isRunning());
        buf.append(", scheduledExecutionTime=").append(getScheduledExecutionTime());
        buf.append(", lastExecutionTime=").append(getLastExecutionTime());
        buf.append(", queue=").append(queue);
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * 管理可能なTimerTask。<p>
     *
     * @author M.Takata
     */
    protected class ManagedTimerTask extends TimerTask
     implements DaemonRunnable, Serializable{
        
        private static final long serialVersionUID = -8278944414741844753L;
        
        protected transient ScheduleTask task;
        protected boolean isRunning;
        protected boolean isClosed = true;
        protected boolean isCyclic;
        protected boolean isValid;
        protected boolean isWaiting;
        protected boolean isError;
        protected boolean isTimeout;
        protected long lastExecutionTime;
        protected long scheduledExecutionTime;
        protected int executionCount;
        protected int maxCount;
        protected boolean isForce;
        
        public ManagedTimerTask(ScheduleTask task){
            this.task = task;
        }
        
        public void run(){
            if(!isForce){
                if(scheduleStateManager != null){
                    final int state = scheduleStateManager.getState(getName());
                    isValid = !(state == ScheduleStateManager.STATE_INVALID);
                    isClosed = state == ScheduleStateManager.STATE_CLOSE;
                }
                if(!isValid){
                    return;
                }
                if(isClosed){
                    cancel();
                    return;
                }
            }
            if(queue == null || isForce){
                consume(task, null);
            }else{
                queue.push(task);
            }
        }
        
        public boolean onStart() {
            return true;
        }
        
        public boolean onStop() {
            return true;
        }
        
        public boolean onSuspend() {
            return true;
        }
        
        public boolean onResume() {
            return true;
        }
        
        public Object provide(DaemonControl ctrl){
            return queue.get();
        }
        
        public void consume(Object dequeued, DaemonControl ctrl){
            if(dequeued == null){
                return;
            }
            try{
                if(journal != null){
                    journal.startJournal(JOURNAL_KEY_ROOT);
                    journal.addInfo(JOURNAL_KEY_NAME, getName());
                }
                isError = false;
                isTimeout = false;
                if(dependsScheduleNames != null
                    && dependsScheduleNames.length != 0){
                    long start = System.currentTimeMillis();
                    long procTime = 0;
                    while(true){
                        boolean isExistNotClosed = false;
                        for(int i =0; i < dependsScheduleNames.length; i++){
                            final Schedule schedule = scheduler
                                .getSchedule(dependsScheduleNames[i]);
                            if(schedule != null
                                 && !schedule.isCyclic()
                                 && !schedule.isClosed()
                            ){
                                isExistNotClosed = true;
                                break;
                            }
                        }
                        if(isExistNotClosed){
                            isWaiting = true;
                            if(scheduleStateManager != null && !isForce && !isClosed){
                                scheduleStateManager.changeState(
                                    getName(),
                                    ScheduleStateManager.STATE_DEPENDS_WAIT
                                );
                            }
                        }else{
                            break;
                        }
                        try{
                            if(dependencyTimeout > 0){
                                Thread.sleep(
                                    dependencyTimeout - procTime
                                        > dependencyConfirmInterval
                                    ? dependencyConfirmInterval
                                         : dependencyTimeout - procTime
                                );
                            }else{
                                Thread.sleep(dependencyConfirmInterval);
                            }
                        }catch(InterruptedException e){
                            cancel();
                            if(getLogger() != null){
                                getLogger().write(timeoutLogMessageId, e);
                            }
                            if(journal != null){
                                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                                journal.addInfo(
                                    JOURNAL_KEY_STATUS,
                                    JOURNAL_VAL_STATUS_TIMEOUT
                                );
                            }
                            isWaiting = false;
                            isTimeout = true;
                            return;
                        }
                        procTime = System.currentTimeMillis() - start;
                        if(dependencyTimeout > 0
                             && procTime >= dependencyTimeout){
                            cancel();
                            if(getLogger() != null){
                                getLogger().write(timeoutLogMessageId);
                            }
                            if(journal != null){
                                journal.addInfo(
                                    JOURNAL_KEY_STATUS,
                                    JOURNAL_VAL_STATUS_TIMEOUT
                                );
                            }
                            isWaiting = false;
                            isTimeout = true;
                            return;
                        }
                        if(!isForce){
                            if(scheduleStateManager != null){
                                final int state = scheduleStateManager.getState(getName());
                                isValid = !(state == ScheduleStateManager.STATE_INVALID);
                                isClosed = state == ScheduleStateManager.STATE_CLOSE;
                            }
                            if(!isValid){
                                return;
                            }
                            if(isClosed){
                                cancel();
                                return;
                            }
                        }
                    }
                }
                isWaiting = false;
                isRunning = true;
                if(scheduleStateManager != null && !isForce && !isClosed){
                    scheduleStateManager.changeState(
                        getName(),
                        ScheduleStateManager.STATE_RUN
                    );
                }
                lastExecutionTime = System.currentTimeMillis();
                executionCount++;
                task.run();
                if(journal != null){
                    journal.addInfo(
                        JOURNAL_KEY_STATUS,
                        JOURNAL_VAL_STATUS_SUCCESS
                    );
                }
            }catch(Throwable th){
                isError = true;
                if(getLogger() != null){
                    getLogger().write(errorLogMessageId, th);
                }
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
                    journal.addInfo(
                        JOURNAL_KEY_STATUS,
                        JOURNAL_VAL_STATUS_ERROR
                    );
                }
            }finally{
                if(isCyclic){
                    scheduledExecutionTime = scheduledExecutionTime() + period;
                    if(scheduleStateManager != null && !isForce && !isClosed){
                        scheduleStateManager.changeState(
                            getName(),
                            ScheduleStateManager.STATE_WAIT
                        );
                    }
                }else if(!isError && !isTimeout){
                    isClosed = true;
                    if(scheduleStateManager != null && !isForce){
                        scheduleStateManager.changeState(
                            getName(),
                            ScheduleStateManager.STATE_CLOSE
                        );
                    }
                }
                isRunning = false;
                if(maxCount > 0 && executionCount >= maxCount){
                    cancel();
                    isClosed = true;
                    if(scheduleStateManager != null && !isForce){
                        scheduleStateManager.changeState(
                            getName(),
                            ScheduleStateManager.STATE_CLOSE
                        );
                    }
                }
                if(journal != null){
                    journal.endJournal();
                }
            }
        }
        
        public void garbage(){
            if(queue != null && isGarbageQueue){
                while(queue.size() > 0){
                    consume(queue.get(0), null);
                }
            }
        }
        
        public boolean cancel(){
            isRunning = false;
            isWaiting = false;
            return super.cancel();
        }
    }
}
