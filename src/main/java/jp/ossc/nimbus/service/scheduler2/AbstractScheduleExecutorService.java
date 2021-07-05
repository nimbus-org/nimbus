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

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.StringStreamConverter;
import jp.ossc.nimbus.util.converter.BindingConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * 抽象スケジュール実行。<p>
 * 実行を依頼されたタスクを実行する。<br>
 *
 * @author M.Takata
 */
public abstract class AbstractScheduleExecutorService extends ServiceBase
 implements ScheduleExecutor, AbstractScheduleExecutorServiceMBean{
    
    private static final long serialVersionUID = 7621829987739712419L;
    
    protected ServiceName scheduleManagerServiceName;
    protected ScheduleManager scheduleManager;
    
    protected String key;
    protected String hostName = "localhost";
    protected String type;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    
    protected ServiceName editorFinderServiceName;
    protected EditorFinder editorFinder;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    
    protected List inputConvertMappings;
    protected List outputConvertMappings;
    
    public void setScheduleManagerServiceName(ServiceName name){
        scheduleManagerServiceName = name;
    }
    public ServiceName getScheduleManagerServiceName(){
        return scheduleManagerServiceName;
    }
    
    public void setKey(String key){
        this.key = key;
    }
    
    public void setType(String type){
        this.type = type;
    }
    
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    public void setEditorFinderServiceName(ServiceName name){
        editorFinderServiceName = name;
    }
    public ServiceName getEditorFinderServiceName(){
        return editorFinderServiceName;
    }
    
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void setJournal(Journal journal){
        this.journal = journal;
    }
    public Journal getJournal(){
        return journal;
    }
    
    public void setEditorFinder(EditorFinder editorFinder){
        this.editorFinder = editorFinder;
    }
    public EditorFinder getEditorFinder(){
        return editorFinder;
    }
    
    public void setThreadContext(Context context){
        threadContext = context;
    }
    public Context getThreadContext(){
        return threadContext;
    }
    
    /**
     * スケジュールの入力を変換する{@link Converter}のマッピング情報を登録する。<p>
     *
     * @param mapping マッピング情報
     */
    public void addInputConvertMapping(ConvertMapping mapping){
        inputConvertMappings.add(mapping);
    }
    
    /**
     * スケジュールの出力を変換する{@link Converter}のマッピング情報を登録する。<p>
     *
     * @param mapping マッピング情報
     */
    public void addOutputConvertMapping(ConvertMapping mapping){
        outputConvertMappings.add(mapping);
    }
    
    /**
     * サービスの生成前処理を行う。<p>
     *
     * @exception Exception サービスの生成前処理に失敗した場合
     */
    public void preCreateService() throws Exception{
        inputConvertMappings = new ArrayList();
        outputConvertMappings = new ArrayList();
    }
    
    /**
     * サービスの開始前処理を行う。<p>
     *
     * @exception Exception サービスの開始前処理に失敗した場合
     */
    public void preStartService() throws Exception{
        super.preStartService();
        
        if(scheduleManagerServiceName != null){
            scheduleManager = (ScheduleManager)ServiceManagerFactory
                .getServiceObject(scheduleManagerServiceName);
        }
        if(scheduleManager == null){
            throw new IllegalArgumentException("ScheduleManager is null.");
        }
        try{
            hostName = java.net.InetAddress.getLocalHost().getHostName();
        }catch(UnknownHostException e){
        }
        
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory
                .getServiceObject(journalServiceName);
        }
        
        if(editorFinderServiceName != null){
            editorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(editorFinderServiceName);
        }
        
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
        }
    }
    
    /**
     * サービスの破棄後処理を行う。<p>
     *
     * @exception Exception サービスの破棄後処理に失敗した場合
     */
    public void postDestroyService() throws Exception{
        super.postDestroyService();
        
        inputConvertMappings = null;
        outputConvertMappings = null;
    }
    
    // ScheduleExecutorのJavaDoc
    public ScheduleManager getScheduleManager(){
        return scheduleManager;
    }
    
    // ScheduleExecutorのJavaDoc
    public void setScheduleManager(ScheduleManager manager){
        scheduleManager = manager;
    }
    
    // ScheduleExecutorのJavaDoc
    public String getKey(){
        return key == null ? hostName : key;
    }
    
    // ScheduleExecutorのJavaDoc
    public String getType(){
        return type;
    }
    
    /**
     * 指定されたスケジュールのタスクが実行可能かチェックする。<p>
     * ここでは、何も実装しないので、必要に応じてオーバーライドすること。<br>
     *
     * @param schedule スケジュール
     * @exception Exception 指定されたスケジュールのタスクが実行できない場合
     */
    protected void checkPreExecute(Schedule schedule) throws Exception{
    }
    
    /**
     * 指定されたスケジュールのタスクを実行する。<p>
     *
     * @param schedule スケジュール
     * @return 実行結果を含むスケジュール
     * @exception Throwable 指定されたスケジュールの実行に失敗した場合
     */
    protected abstract Schedule executeInternal(Schedule schedule)
     throws Throwable;
    
    /**
     * 指定されたスケジュールを実行する。<p>
     * <ol>
     *   <li>{@link #checkPreExecute(Schedule)}でスケジュールのタスクが実行できるかどうかをチェックする。<br>チェックエラーの場合は、ログ{@link #MSG_ID_EXECUTE_ERROR}を出力し、スケジュールの状態を{@link Schedule#STATE_FAILED}に遷移させる。<br>スケジュールの状態遷移に失敗した場合は、ログ{@link #MSG_ID_STATE_CHANGE_ERROR}を出力する。</li>
     *   <li>スケジュールの状態を{@link Schedule#STATE_RUN}に遷移させ、ログ{@link #MSG_ID_RUN}を出力する。<br>スケジュールの状態遷移に失敗した場合は、ログ{@link #MSG_ID_STATE_CHANGE_ERROR}を出力する。</li>
     *   <li>{@link #executeInternal(Schedule)}を呼び出し、スケジュールを実行する。</li>
     *   <li>
     *     スケジュールの実行結果に従って、以下の処理を行う。<br>
     *     <ul>
     *       <li>スケジュールが正常に終了した場合、ログ{@link #MSG_ID_RUN}を出力し、スケジュールの状態を{@link Schedule#STATE_END}に遷移させる。<br>スケジュールの状態遷移に失敗した場合は、ログ{@link #MSG_ID_STATE_CHANGE_ERROR}を出力する。</li>
     *       <li>スケジュールの実行で例外が発生した場合、ログ{@link #MSG_ID_EXECUTE_ERROR}を出力し、スケジュールの状態を{@link Schedule#STATE_FAILED}に遷移させる。<br>スケジュールの状態遷移に失敗した場合は、ログ{@link #MSG_ID_STATE_CHANGE_ERROR}を出力する。</li>
     *       <li>スケジュールが強制終了された場合、ログ{@link #MSG_ID_ABORT}を出力し、スケジュールの状態を{@link Schedule#STATE_ABORT}に遷移させる。<br>スケジュールの状態遷移に失敗した場合は、ログ{@link #MSG_ID_STATE_CHANGE_ERROR}を出力する。</li>
     *       <li>スケジュールがリトライ要求された場合、次のリトライ時刻に再スケジュールして、スケジュールの状態を{@link Schedule#STATE_RETRY}に遷移させる。<br>但し、次のリトライ時刻がリトライ終了時刻を越えていた場合は、ログ{@link #MSG_ID_RETRY_END_ERROR}を出力し、スケジュールの状態を{@link Schedule#STATE_FAILED}に遷移させる。<br>スケジュールの状態遷移に失敗した場合は、ログ{@link #MSG_ID_STATE_CHANGE_ERROR}を出力する。</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param schedule スケジュール
     * @return スケジュール
     */
    public Schedule execute(Schedule schedule){
        
        Schedule result = schedule;
        try{
            checkPreExecute(schedule);
            convertInput(schedule);
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
                    Schedule.STATE_FAILED,
                    th
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
            return result;
        }
        try{
            final boolean isChanged = scheduleManager.changeState(
                schedule.getId(),
                Schedule.STATE_ENTRY,
                Schedule.STATE_RUN
            );
            if(!isChanged){
                getLogger().write(
                    MSG_ID_STATE_TRANS_ERROR,
                    new Object[]{
                        scheduleManagerServiceName,
                        schedule.getId(),
                        schedule.getMasterId(),
                        new Integer(Schedule.STATE_ENTRY),
                        new Integer(Schedule.STATE_RUN)
                    }
                );
                return schedule;
            }
        }catch(ScheduleStateControlException e){
            getLogger().write(
                MSG_ID_STATE_CHANGE_ERROR,
                new Object[]{
                    scheduleManagerServiceName,
                    schedule.getId(),
                    schedule.getMasterId(),
                    new Integer(Schedule.STATE_RUN)
                },
                e
            );
            return schedule;
        }
        getLogger().write(
            MSG_ID_RUN,
            new Object[]{
                scheduleManagerServiceName,
                schedule.getId(),
                schedule.getMasterId(),
                schedule.getInput()
            }
        );
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_EXECUTE, editorFinder);
                if(threadContext != null){
                    journal.setRequestId((String)threadContext.get(ThreadContextKey.REQUEST_ID));
                }
                journal.addInfo(JOURNAL_KEY_INPUT_SCHEDULE, schedule);
            }
            result = executeInternal(schedule);
            convertOutput(result);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_OUTPUT_SCHEDULE, result);
            }
            if(result.getState() == Schedule.STATE_FAILED){
                getLogger().write(
                    MSG_ID_EXECUTE_ERROR,
                    new Object[]{
                        scheduleManagerServiceName,
                        schedule.getId(),
                        schedule.getMasterId()
                    }
                );
                try{
                    scheduleManager.changeState(
                        schedule.getId(),
                        Schedule.STATE_FAILED,
                        result.getOutput()
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
            }else if(result.getState() == Schedule.STATE_ABORT){
                getLogger().write(
                    MSG_ID_ABORT,
                    new Object[]{
                        scheduleManagerServiceName,
                        result.getId(),
                        result.getMasterId(),
                    },
                    result.getOutput() instanceof Throwable ? (Throwable)result.getOutput() : null
                );
                try{
                    scheduleManager.changeState(
                        result.getId(),
                        Schedule.STATE_ABORT,
                        result.getOutput()
                    );
                }catch(ScheduleStateControlException e2){
                    getLogger().write(
                        MSG_ID_STATE_CHANGE_ERROR,
                        new Object[]{
                            scheduleManagerServiceName,
                            result.getId(),
                            result.getMasterId(),
                            new Integer(Schedule.STATE_ABORT)
                        },
                        e2
                    );
                }
            }else if(result.getState() == Schedule.STATE_DISABLE){
                getLogger().write(
                    MSG_ID_DISABLE,
                    new Object[]{
                        scheduleManagerServiceName,
                        result.getId(),
                        result.getMasterId(),
                        result.getOutput()
                    }
                );
                try{
                    scheduleManager.changeState(
                        result.getId(),
                        Schedule.STATE_DISABLE,
                        result.getOutput()
                    );
                }catch(ScheduleStateControlException e2){
                    getLogger().write(
                        MSG_ID_STATE_CHANGE_ERROR,
                        new Object[]{
                            scheduleManagerServiceName,
                            result.getId(),
                            result.getMasterId(),
                            new Integer(Schedule.STATE_DISABLE)
                        },
                        e2
                    );
                }
            }else if(result.getRetryInterval() > 0
                 && result.isRetry()){
                final Date retryTime = calculateRetryTime(
                    result.getRetryInterval(),
                    result.getRetryEndTime()
                );
                if(retryTime == null){
                    getLogger().write(
                        MSG_ID_RETRY_END_ERROR,
                        new Object[]{
                            scheduleManagerServiceName,
                            result.getId(),
                            result.getMasterId(),
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(result.getRetryEndTime())
                        }
                    );
                    try{
                        scheduleManager.changeState(
                            result.getId(),
                            Schedule.STATE_FAILED
                        );
                    }catch(ScheduleStateControlException e){
                        getLogger().write(
                            MSG_ID_STATE_CHANGE_ERROR,
                            new Object[]{
                                scheduleManagerServiceName,
                                result.getId(),
                                result.getMasterId(),
                                new Integer(Schedule.STATE_FAILED)
                            },
                            e
                        );
                    }
                }else{
                    try{
                        final boolean isReschedule = scheduleManager.reschedule(
                            result.getId(),
                            retryTime,
                            result.getOutput()
                        );
                        if(!isReschedule){
                            getLogger().write(
                                MSG_ID_RESCHEDULE_ERROR,
                                new Object[]{
                                    scheduleManagerServiceName,
                                    result.getId(),
                                    result.getMasterId()
                                }
                            );
                            try{
                                scheduleManager.changeState(
                                    result.getId(),
                                    Schedule.STATE_FAILED
                                );
                            }catch(ScheduleStateControlException e){
                                getLogger().write(
                                    MSG_ID_STATE_CHANGE_ERROR,
                                    new Object[]{
                                        scheduleManagerServiceName,
                                        result.getId(),
                                        result.getMasterId(),
                                        new Integer(Schedule.STATE_FAILED)
                                    },
                                    e
                                );
                            }
                            return result;
                        }
                        final int nowState = scheduleManager.getState(result.getId());
                        switch(nowState){
                        case Schedule.STATE_RUN:
                        case Schedule.STATE_PAUSE:
                            break;
                        default:
                            getLogger().write(
                                MSG_ID_STATE_TRANS_ERROR,
                                new Object[]{
                                    scheduleManagerServiceName,
                                    result.getId(),
                                    result.getMasterId(),
                                    new Integer(nowState),
                                    new Integer(Schedule.STATE_RETRY)
                                }
                            );
                            return result;
                        }
                        final boolean isChanged = scheduleManager.changeState(
                            result.getId(),
                            nowState,
                            Schedule.STATE_RETRY
                        );
                        if(!isChanged){
                            getLogger().write(
                                MSG_ID_STATE_TRANS_ERROR,
                                new Object[]{
                                    scheduleManagerServiceName,
                                    result.getId(),
                                    result.getMasterId(),
                                    new Integer(nowState),
                                    new Integer(Schedule.STATE_RETRY)
                                }
                            );
                            return result;
                        }
                        getLogger().write(
                            MSG_ID_RESCHEDULE,
                            new Object[]{
                                scheduleManagerServiceName,
                                result.getId(),
                                result.getMasterId(),
                                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(retryTime)
                            }
                        );
                    }catch(ScheduleManageException e){
                        getLogger().write(
                            MSG_ID_RESCHEDULE_ERROR,
                            new Object[]{
                                scheduleManagerServiceName,
                                result.getId(),
                                result.getMasterId()
                            },
                            e
                        );
                    }catch(ScheduleStateControlException e){
                        getLogger().write(
                            MSG_ID_STATE_CHANGE_ERROR,
                            new Object[]{
                                scheduleManagerServiceName,
                                result.getId(),
                                result.getMasterId(),
                                new Integer(Schedule.STATE_RETRY)
                            },
                            e
                        );
                    }
                }
            }else{
                getLogger().write(
                    MSG_ID_END,
                    new Object[]{
                        scheduleManagerServiceName,
                        result.getId(),
                        result.getMasterId(),
                        result.getOutput()
                    }
                );
                try{
                    scheduleManager.changeState(
                        result.getId(),
                        Schedule.STATE_END,
                        result.getOutput()
                    );
                }catch(ScheduleStateControlException e){
                    getLogger().write(
                        MSG_ID_STATE_CHANGE_ERROR,
                        new Object[]{
                            scheduleManagerServiceName,
                            result.getId(),
                            result.getMasterId(),
                            new Integer(Schedule.STATE_END)
                        },
                        e
                    );
                }
            }
        }catch(Throwable th){
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
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
                    Schedule.STATE_FAILED,
                    th
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
        }finally{
            if(result != null && result.getRepeatInterval() > 0){
                final Date repeatTime = calculateRepeatTime(
                    result.getRepeatInterval(),
                    result.getRepeatEndTime()
                );
                if(repeatTime != null){
                    try{
                        final List ownSchedules = scheduleManager.findSchedules(
                            repeatTime,
                            null,
                            null,
                            result.getMasterId(),
                            null,
                            null,
                            1
                        );
                        if(ownSchedules.isEmpty()){
                            Schedule nextSchedule = (Schedule)result.clone();
                            nextSchedule.setTime(repeatTime);
                            scheduleManager.addSchedule(nextSchedule);
                        }
                    }catch(ScheduleManageException e){
                        getLogger().write(
                            MSG_ID_DYNAMIC_REPEAT_ERROR,
                            new Object[]{
                                scheduleManagerServiceName,
                                schedule.getId(),
                                schedule.getMasterId()
                            },
                            e
                        );
                    }
                }
            }
            if(journal != null){
                journal.endJournal();
            }
        }
        return result;
    }
    
    /**
     * スケジュールの入力を変換する。<p>
     *
     * @param schedule スケジュール
     * @exception ConvertException 変換に失敗した場合
     */
    protected void convertInput(Schedule schedule) throws ConvertException{
        for(int i = 0; i < inputConvertMappings.size(); i++){
            ConvertMapping mapping = (ConvertMapping)inputConvertMappings.get(i);
            if(mapping.isMatch(schedule)){
                schedule.setInput(mapping.convert(schedule.getInput()));
                break;
            }
        }
    }
    
    /**
     * スケジュールの出力を変換する。<p>
     *
     * @param schedule スケジュール
     * @exception ConvertException 変換に失敗した場合
     */
    protected void convertOutput(Schedule schedule) throws ConvertException{
        for(int i = 0; i < outputConvertMappings.size(); i++){
            ConvertMapping mapping = (ConvertMapping)outputConvertMappings.get(i);
            if(mapping.isMatch(schedule)){
                schedule.setOutput(mapping.convert(schedule.getOutput()));
                break;
            }
        }
    }
    
    /**
     * リトライ日時を計算する。<p>
     *
     * @param interval リトライ実行間隔
     * @param endTime リトライ終了時刻
     * @return リトライ日時。リトライ終了時刻を過ぎている場合は、null
     */
    protected Date calculateRetryTime(
        long interval,
        Date endTime
    ){
        final Calendar offset = Calendar.getInstance();
        Calendar end = null;
        if(endTime != null){
            end = Calendar.getInstance();
            end.setTime(endTime);
        }
        if(interval > Integer.MAX_VALUE){
            long offsetInterval = interval;
            int tmpInterval = 0;
            do{
                if(offsetInterval >= Integer.MAX_VALUE){
                    tmpInterval = Integer.MAX_VALUE;
                }else{
                    tmpInterval = (int)offsetInterval;
                }
                offset.add(Calendar.MILLISECOND, tmpInterval);
                offsetInterval -= Integer.MAX_VALUE;
            }while(offsetInterval > 0);
        }else{
            offset.add(Calendar.MILLISECOND, (int)interval);
        }
        if(end != null && offset.after(end)){
            return null;
        }else{
            return offset.getTime();
        }
    }
    
    /**
     * 繰り返し日時を計算する。<p>
     *
     * @param interval 繰り返し間隔
     * @param endTime 繰り返し終了時刻
     * @return 繰り返し日時。繰り返し終了時刻を過ぎている場合は、null
     */
    protected Date calculateRepeatTime(
        long interval,
        Date endTime
    ){
        final Calendar offset = Calendar.getInstance();
        Calendar end = null;
        if(endTime != null){
            end = Calendar.getInstance();
            end.setTime(endTime);
        }
        if(interval > Integer.MAX_VALUE){
            long offsetInterval = interval;
            int tmpInterval = 0;
            do{
                if(offsetInterval >= Integer.MAX_VALUE){
                    tmpInterval = Integer.MAX_VALUE;
                }else{
                    tmpInterval = (int)offsetInterval;
                }
                offset.add(Calendar.MILLISECOND, tmpInterval);
                offsetInterval -= Integer.MAX_VALUE;
            }while(offsetInterval > 0);
        }else{
            offset.add(Calendar.MILLISECOND, (int)interval);
        }
        if(end != null && offset.after(end)){
            return null;
        }else{
            return offset.getTime();
        }
    }
    
    /**
     * 変換マッピング。<p>
     *
     * @author M.Takata
     */
    public static class ConvertMapping implements java.io.Serializable{
        
        private static final long serialVersionUID = 8905574263957259138L;
        
        protected String masterId;
        protected String taskName;
        protected Pattern taskNamePattern;
        protected ServiceName converterServiceName;
        protected Converter converter;
        
        /**
         * 変換対象となるスケジュールのスケジュールマスタIDを設定する。<p>
         *
         * @param id スケジュールマスタID
         */
        public void setMasterId(String id){
            masterId = id;
        }
        
        /**
         * 変換対象となるスケジュールのスケジュールマスタIDを取得する。<p>
         *
         * @return スケジュールマスタID
         */
        public String getMasterId(){
            return masterId;
        }
        
        /**
         * 変換対象となるスケジュールのタスク名を設定する。<p>
         *
         * @param name タスク名
         */
        public void setTaskName(String name){
            taskName = name;
        }
        
        /**
         * 変換対象となるスケジュールのタスク名を取得する。<p>
         *
         * @return タスク名
         */
        public String getTaskName(){
            return taskName;
        }
        
        /**
         * 変換対象となるスケジュールのタスク名正規表現を設定する。<p>
         *
         * @param pattern タスク名正規表現
         */
        public void setTaskNamePattern(String pattern){
            taskNamePattern = Pattern.compile(pattern);
        }
        
        /**
         * 変換対象となるスケジュールのタスク名正規表現を取得する。<p>
         *
         * @return タスク名正規表現
         */
        public String getTaskNamePattern(){
            return taskNamePattern == null ? null : taskNamePattern.toString();
        }
        
        /**
         * 変換を行う{@link Converter}のサービス名を設定する。<p>
         *
         * @param name Converterのサービス名
         */
        public void setConverterServiceName(ServiceName name){
            converterServiceName = name;
        }
        
        /**
         * 変換を行う{@link Converter}のサービス名を取得する。<p>
         *
         * @return Converterのサービス名
         */
        public ServiceName getConverterServiceName(){
            return converterServiceName;
        }
        
        /**
         * 変換を行う{@link Converter}を設定する。<p>
         *
         * @param converter Converter
         */
        public void setConverter(Converter converter){
            this.converter = converter;
        }
        
        /**
         * 変換を行う{@link Converter}を取得する。<p>
         *
         * @return Converter
         */
        public Converter getConverter(){
            return converter;
        }
        
        protected Converter getConverterService(){
            Converter cnv = converter;
            if(converterServiceName != null){
                cnv = (Converter)ServiceManagerFactory.getServiceObject(converterServiceName);
            }
            return cnv;
        }
        
        public boolean isMatch(Schedule schedule){
            boolean result = true;
            if(masterId != null && !masterId.equals(schedule.getMasterId())){
                result = false;
            }
            if(taskName != null && !taskName.equals(schedule.getTaskName())){
                result = false;
            }
            if(schedule.getTaskName() != null && taskNamePattern != null && !taskNamePattern.matcher(schedule.getTaskName()).matches()){
                result = false;
            }
            return result;
        }
        
        public Object convert(Object obj) throws ConvertException{
            Converter cnv = getConverterService();
            if(cnv == null){
                return obj;
            }
            return cnv.convert(obj);
        }
    }
    
    public static class BindingConvertMapping extends ConvertMapping implements java.io.Serializable{
        
        private static final long serialVersionUID = 586825523873882273L;
        
        protected Class bindType;
        protected Object bindObject;
        protected String encoding;
        
        public void setBindType(Class type){
            bindType = type;
        }
        public Class getBindType(){
            return bindType;
        }
        
        public void setBindObject(Object obj){
            bindObject = obj;
        }
        public Object getBindObject(){
            return bindObject;
        }
        
        public void setEncoding(String encoding){
            this.encoding = encoding;
        }
        public String getEncoding(){
            return encoding;
        }
        
        public Object convert(Object obj) throws ConvertException{
            Converter cnv = getConverterService();
            if(cnv == null || obj == null){
                return obj;
            }
            Object output = null;
            if(bindType != null){
                try{
                    output = bindType.newInstance();
                }catch(InstantiationException e){
                    throw new ConvertException(e);
                }catch(IllegalAccessException e){
                    throw new ConvertException(e);
                }
            }else if(bindObject != null){
                if(bindObject instanceof DataSet){
                    output = ((DataSet)bindObject).cloneSchema();
                }else if(bindObject instanceof RecordList){
                    output = ((RecordList)bindObject).cloneSchema();
                }else if(bindObject instanceof Record){
                    output = ((Record)bindObject).cloneSchema();
                }else if(bindObject instanceof Cloneable){
                    try{
                        output = bindObject.getClass().getMethod("clone", (Class[])null).invoke(bindObject, (Object[])null);
                    }catch(NoSuchMethodException e){
                        throw new ConvertException(e);
                    }catch(IllegalAccessException e){
                        throw new ConvertException(e);
                    }catch(InvocationTargetException e){
                        throw new ConvertException(e);
                    }
                }
            }
            if(cnv instanceof BindingConverter){
                return ((BindingConverter)cnv).convert(obj, output);
            }else if(cnv instanceof BindingStreamConverter){
                InputStream is = null;
                if(obj instanceof InputStream){
                    is = (InputStream)obj;
                }else{
                    StringStreamConverter ssc = new StringStreamConverter();
                    if(encoding != null){
                        ssc.setCharacterEncodingToObject(encoding);
                    }
                    is = ssc.convertToStream(obj.toString());
                }
                return ((BindingStreamConverter)cnv).convertToObject(is, output);
            }else{
                return super.convert(obj);
            }
        }
    }
}