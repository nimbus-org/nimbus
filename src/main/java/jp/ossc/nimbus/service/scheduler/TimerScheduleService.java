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
 * �^�C�}�[�X�P�W���[���T�[�r�X�B<p>
 * {@link TimerSchedulerService}�ɓo�^����X�P�W���[����ݒ肷��T�[�r�X�ł���B<br>
 * �ȉ��̐ݒ肪�\�ł���B<br>
 * <ul>
 *     <li>���s����</li>
 *     <li>�x������</li>
 *     <li>�J��Ԃ��Ԋu</li>
 *     <li>�Œ�x�����s</li>
 *     <li>�񓯊����s</li>
 * </ul>
 *
 * @author M.Takata
 */
public class TimerScheduleService extends ServiceBase
 implements TimerSchedule, Serializable, TimerScheduleServiceMBean{
    
    private static final long serialVersionUID = 8328157884039980522L;
    
    /**
     * �X�P�W���[�����B<p>
     */
    protected String name;
    
    /**
     * �^�X�N�T�[�r�X�̃T�[�r�X���B<p>
     */
    protected ServiceName taskServiceName;
    
    /**
     * �^�C�}�[�^�X�N�B<p>
     */
    protected ManagedTimerTask task;
    
    /**
     * �X�P�W���[���^�X�N�B<p>
     */
    protected transient ScheduleTask scheduleTask;
    
    /**
     * ���s�J�n�����B<p>
     */
    protected Date startTime;
    
    /**
     * ���s�I�������B<p>
     */
    protected Date endTime;
    
    /**
     * �x������[ms]�B<p>
     */
    protected long delay = -1;
    
    /**
     * �J��Ԃ��Ԋu[ms]�B<p>
     */
    protected long period = -1;
    
    /**
     * �J��Ԃ��񐔁B<p>
     */
    protected int count = -1;
    
    /**
     * �Œ�p�x���s�t���O�B<p>
     * true�̏ꍇ�A�Œ�p�x���s�B<br>
     */
    protected boolean isFixedRate;
    
    /**
     * �ˑ�����X�P�W���[�����z��B<p>
     */
    protected String[] dependsScheduleNames;
    
    /**
     * �ˑ�����X�P�W���[���҂��̃^�C���A�E�g[ms]�B<p>
     */
    protected long dependencyTimeout = -1;
    
    /**
     * �ˑ�����X�P�W���[���I���m�F�̊Ԋu[ms]�B<p>
     */
    protected long dependencyConfirmInterval = 1000;
    
    /**
     * �G���[���O�̃��b�Z�[�WID�B<p>
     */
    protected String errorLogMessageId = DEFAULT_ERROR_LOG_MESSAGE_ID;
    
    /**
     * �G���[���O�̃��b�Z�[�WID�B<p>
     */
    protected String timeoutLogMessageId = DEFAULT_TIMEOUT_LOG_MESSAGE_ID;
    
    /**
     * �W���[�i���T�[�r�X�̃T�[�r�X���B<p>
     */
    protected ServiceName journalServiceName;
    
    /**
     * �W���[�i���T�[�r�X�B<p>
     */
    protected transient Journal journal;
    
    /**
     * Queue�T�[�r�X�̃T�[�r�X���B<p>
     */
    protected ServiceName queueServiceName;
    
    /**
     * Queue�T�[�r�X�B<p>
     */
    protected transient Queue queue;
    
    /**
     * �X�P�W���[���̏I�����ɃL���[�ɗ��܂����^�X�N���������邩�ǂ����̃t���O�B<p>
     */
    protected boolean isGarbageQueue = false;
    
    /**
     * �񓯊������f�[�����B<p>
     */
    protected transient Daemon daemon;
    
    /**
     * �X�P�W���[���B<p>
     */
    protected transient Scheduler scheduler;
    
    /**
     * �X�P�W���[����ԊǗ��T�[�r�X�̃T�[�r�X���B<p>
     */
    protected ServiceName scheduleStateManagerServiceName;
    
    /**
     * �X�P�W���[����ԊǗ��B<p>
     */
    protected transient ScheduleStateManager scheduleStateManager;
    
    /**
     * �X�P�W���[�����J�n���鎞�ɁA���Ɏ��s�J�n�������߂��Ă����ꍇ�ɁA�^�X�N�����s���邩�ǂ����̃t���O�B<p>
     */
    protected boolean isExecuteWhenOverStartTime = true;
    
    /**
     * ��̃X�P�W���[�����쐬����B<p>
     */
    public TimerScheduleService(){
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setName(String name){
        this.name = name;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setTaskServiceName(ServiceName name){
        taskServiceName = name;
    }
    
    // TimerScheduleMBean��JavaDoc
    public ServiceName getTaskServiceName(){
        return taskServiceName;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setStartTime(Date time){
        startTime = time;
    }
    
    // TimerScheduleMBean��JavaDoc
    public Date getStartTime(){
        return startTime;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setExecuteWhenOverStartTime(boolean isExecute){
        isExecuteWhenOverStartTime = isExecute;
    }
    
    // TimerScheduleMBean��JavaDoc
    public boolean isExecuteWhenOverStartTime(){
        return isExecuteWhenOverStartTime;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setEndTime(Date time){
        endTime = time;
    }
    
    // TimerScheduleMBean��JavaDoc
    public Date getEndTime(){
        return endTime;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setDelay(long delay){
        this.delay = delay;
    }
    
    // TimerScheduleMBean��JavaDoc
    public long getDelay(){
        return delay;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setPeriod(long period){
        this.period = period;
    }
    
    // TimerScheduleMBean��JavaDoc
    public long getPeriod(){
        return period;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setCount(int count){
        this.count = count;
    }
    
    // TimerScheduleMBean��JavaDoc
    public int getCount(){
        return count;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setFixedRate(boolean isFixedRate){
        this.isFixedRate = isFixedRate;
    }
    
    // TimerScheduleMBean��JavaDoc
    public boolean isFixedRate(){
        return isFixedRate;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setDependsScheduleNames(String[] names){
        dependsScheduleNames = names;
    }
    
    // TimerScheduleMBean��JavaDoc
    public String[] getDependsScheduleNames(){
        return dependsScheduleNames;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setDependencyTimeout(long timeout){
        dependencyTimeout = timeout;
    }
    
    // TimerScheduleMBean��JavaDoc
    public long getDependencyTimeout(){
        return dependencyTimeout;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setDependencyConfirmInterval(long interval){
        dependencyConfirmInterval = interval;
    }
    
    // TimerScheduleMBean��JavaDoc
    public long getDependencyConfirmInterval(){
        return dependencyConfirmInterval;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setErrorLogMessageId(String id){
        errorLogMessageId = id;
    }
    
    // TimerScheduleMBean��JavaDoc
    public String getErrorLogMessageId(){
        return errorLogMessageId;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setTimeoutLogMessageId(String id){
        timeoutLogMessageId = id;
    }
    
    // TimerScheduleMBean��JavaDoc
    public String getTimeoutLogMessageId(){
        return timeoutLogMessageId;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    
    // TimerScheduleMBean��JavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    
    // TimerScheduleMBean��JavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setGarbageQueue(boolean isGarbage){
        isGarbageQueue = isGarbage;
    }
    // TimerScheduleMBean��JavaDoc
    public boolean isGarbageQueue(){
        return isGarbageQueue;
    }
    
    // TimerScheduleMBean��JavaDoc
    public void setScheduleStateManagerServiceName(ServiceName name){
        scheduleStateManagerServiceName = name;
    }
    // TimerScheduleMBean��JavaDoc
    public ServiceName getScheduleStateManagerServiceName(){
        return scheduleStateManagerServiceName;
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
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
     * �T�[�r�X�̒�~�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        cancel();
        if(queue != null){
            
            daemon.stop();
            
            queue.release();
        }
    }
    
    // TimerSchedule��JavaDoc
    public void setScheduler(Scheduler scheduler){
        this.scheduler = scheduler;
    }
    
    // TimerSchedule��JavaDoc
    public void executeForce(){
        ManagedTimerTask tmpTask = new ManagedTimerTask(scheduleTask);
        tmpTask.isForce = true;
        tmpTask.run();
    }
    
    // TimerSchedule��JavaDoc
    public void executeForce(Timer timer, long delay){
        ManagedTimerTask tmpTask = new ManagedTimerTask(scheduleTask);
        tmpTask.isForce = true;
        timer.schedule(tmpTask, delay);
    }
    
    // TimerSchedule��JavaDoc
    public void executeForce(Timer timer, Date time){
        ManagedTimerTask tmpTask = new ManagedTimerTask(scheduleTask);
        tmpTask.isForce = true;
        timer.schedule(tmpTask, time);
    }
    
    // TimerSchedule��JavaDoc
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
    
    // TimerSchedule��JavaDoc
    public String getName(){
         return name == null ? getServiceName() : name;
    }
    
    // TimerSchedule��JavaDoc
    public void cancel(){
        if(task != null){
            task.cancel();
        }
    }
    
    // TimerSchedule��JavaDoc
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
    
    // TimerSchedule��JavaDoc
    public boolean isValid(){
        return task == null ? false : task.isValid;
    }
    
    // TimerSchedule��JavaDoc
    public boolean isCyclic(){
        return task == null ? false : task.isCyclic;
    }
    
    // TimerSchedule��JavaDoc
    public boolean isClosed(){
        return task == null ? true : task.isClosed;
    }
    
    // TimerSchedule��JavaDoc
    public boolean isRunning(){
        return task == null ? false : task.isRunning;
    }
    
    // TimerSchedule��JavaDoc
    public boolean isError(){
        return task == null ? false : task.isError;
    }
    
    // TimerSchedule��JavaDoc
    public boolean isWaiting(){
        return task == null ? false : task.isWaiting;
    }
    
    // TimerSchedule��JavaDoc
    public boolean isTimeout(){
        return task == null ? false : task.isTimeout;
    }
    
    // TimerSchedule��JavaDoc
    public Date getLastExecutionTime(){
        return task == null || task.lastExecutionTime == 0 ? null : new Date(task.lastExecutionTime);
    }
    
    // TimerSchedule��JavaDoc
    public Date getScheduledExecutionTime(){
        return task == null ? null : new Date(
            task.scheduledExecutionTime == 0
                 ? task.scheduledExecutionTime() : task.scheduledExecutionTime
        );
    }
    
    // Schedule��JavaDoc
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
    
    // Schedule��JavaDoc
    public ScheduleStateManager getScheduleStateManager(){
        return scheduleStateManager;
    }
    
    /**
     * �^�X�N��ݒ肷��B<p>
     *
     * @param task �^�X�N
     */
    public void setTask(ScheduleTask task){
        if(task == null){
            throw new IllegalArgumentException("Task is null.");
        }
        scheduleTask = task;
    }
    
    /**
     * �W���[�i���T�[�r�X��ݒ肷��B<p>
     *
     * @param journal �W���[�i���T�[�r�X
     */
    public void setJournal(Journal journal){
        this.journal = journal;
    }
    
    /**
     * Queue�T�[�r�X��ݒ肷��B<p>
     *
     * @param queue Queue�T�[�r�X
     */
    public void setQueue(Queue queue){
        this.queue = queue;
    }
    
    /**
     * ������\�����擾����B<p>
     *
     * @return ������\��
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
     * �Ǘ��\��TimerTask�B<p>
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
