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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.ioccall.FacadeCaller;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.queue.Queue;

/**
 * データベーススケジュールファクトリ。<p>
 *
 * @author M.Takata
 */
public class DatabaseTimerScheduleFactoryService extends ServiceBase
 implements DatabaseTimerScheduleFactoryServiceMBean, ScheduleFactory{
    
    private static final long serialVersionUID = 6855784208603292244L;
    
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    private String scheduleMasterQuery;
    private int scheduleKeyQueryIndex = -1;
    private int scheduleNameQueryIndex = -1;
    private int scheduleTaskServiceNameQueryIndex = -1;
    private int scheduleBeanFlowInvokerFactoryServiceNameQueryIndex = -1;
    private int scheduleBeanFlowNameQueryIndex = -1;
    private ServiceName scheduleBeanFlowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory scheduleBeanFlowInvokerFactory;
    private int scheduleFacadeCallerServiceNameQueryIndex = -1;
    private int scheduleBeanFlowNamesQueryIndex = -1;
    private ServiceName scheduleFacadeCallerServiceName;
    private FacadeCaller scheduleFacadeCaller;
    private int scheduleIOCCallTypeQueryIndex = -1;
    private String scheduleIOCCallType;
    private int scheduleStartTimeQueryIndex = -1;
    private String scheduleStartTimeFormat;
    private DateFormat scheduleStartTimeDateFormat;
    private int scheduleExecuteWhenOverStartTimeQueryIndex = -1;
    private int scheduleEndTimeQueryIndex = -1;
    private String scheduleEndTimeFormat;
    private DateFormat scheduleEndTimeDateFormat;
    private int scheduleDelayQueryIndex = -1;
    private int schedulePeriodQueryIndex = -1;
    private int scheduleCountQueryIndex = -1;
    private int scheduleFixedRateQueryIndex = -1;
    private int scheduleDependsScheduleNamesQueryIndex = -1;
    private int scheduleDependencyTimeoutQueryIndex = -1;
    private int scheduleDependencyConfirmIntervalQueryIndex = -1;
    private int scheduleErrorLogMessageIdQueryIndex = -1;
    private String scheduleErrorLogMessageId;
    private int scheduleTimeoutLogMessageIdQueryIndex = -1;
    private String scheduleTimeoutLogMessageId;
    private int scheduleJournalServiceNameQueryIndex = -1;
    private ServiceName scheduleJournalServiceName;
    private Journal scheduleJournal;
    private int scheduleQueueServiceNameQueryIndex = -1;
    private ServiceName scheduleQueueServiceName;
    private Queue scheduleQueue;
    private int scheduleGarbageQueueQueryIndex = -1;
    private int scheduleStateManagerServiceNameQueryIndex = -1;
    private ServiceName scheduleStateManagerServiceName;
    private ScheduleStateManager scheduleStateManager;
    
    private Map scheduleMap;
    
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setScheduleMasterQuery(String query){
        scheduleMasterQuery = query;
    }
    public String getScheduleMasterQuery(){
        return scheduleMasterQuery;
    }
    
    public void setScheduleKeyQueryIndex(int index){
        scheduleKeyQueryIndex = index;
    }
    public int getScheduleKeyQueryIndex(){
        return scheduleKeyQueryIndex;
    }
    
    public void setScheduleNameQueryIndex(int index){
        scheduleNameQueryIndex = index;
    }
    public int getScheduleNameQueryIndex(){
        return scheduleNameQueryIndex;
    }
    
    public void setScheduleTaskServiceNameQueryIndex(int index){
        scheduleTaskServiceNameQueryIndex = index;
    }
    public int getScheduleTaskServiceNameQueryIndex(){
        return scheduleTaskServiceNameQueryIndex;
    }
    
    public void setScheduleBeanFlowInvokerFactoryServiceName(ServiceName name){
        scheduleBeanFlowInvokerFactoryServiceName = name;
    }
    public ServiceName getScheduleBeanFlowInvokerFactoryServiceName(){
        return scheduleBeanFlowInvokerFactoryServiceName;
    }
    
    public void setScheduleBeanFlowInvokerFactoryServiceNameQueryIndex(int index){
        scheduleBeanFlowInvokerFactoryServiceNameQueryIndex = index;
    }
    public int getScheduleBeanFlowInvokerFactoryServiceNameQueryIndex(){
        return scheduleBeanFlowInvokerFactoryServiceNameQueryIndex;
    }
    
    public void setScheduleBeanFlowNameQueryIndex(int index){
        scheduleBeanFlowNameQueryIndex = index;
    }
    public int getScheduleBeanFlowNameQueryIndex(){
        return scheduleBeanFlowNameQueryIndex;
    }
    
    public void setScheduleFacadeCallerServiceNameQueryIndex(int index){
        scheduleFacadeCallerServiceNameQueryIndex = index;
    }
    public int getScheduleFacadeCallerServiceNameQueryIndex(){
        return scheduleFacadeCallerServiceNameQueryIndex;
    }
    
    public void setScheduleBeanFlowNamesQueryIndex(int index){
        scheduleBeanFlowNamesQueryIndex = index;
    }
    public int getScheduleBeanFlowNamesQueryIndex(){
        return scheduleBeanFlowNamesQueryIndex;
    }
    
    public void setScheduleFacadeCallerServiceName(ServiceName name){
        scheduleFacadeCallerServiceName = name;
    }
    public ServiceName getScheduleFacadeCallerServiceName(){
        return scheduleFacadeCallerServiceName;
    }
    
    public void setScheduleIOCCallTypeQueryIndex(int index){
        scheduleIOCCallTypeQueryIndex = index;
    }
    public int getScheduleIOCCallTypeQueryIndex(){
        return scheduleIOCCallTypeQueryIndex;
    }
    
    public void setScheduleIOCCallType(String type){
        scheduleIOCCallType = type;
    }
    public String getScheduleIOCCallType(){
        return scheduleIOCCallType;
    }
    
    public void setScheduleStartTimeQueryIndex(int index){
        scheduleStartTimeQueryIndex = index;
    }
    public int getScheduleStartTimeQueryIndex(){
        return scheduleStartTimeQueryIndex;
    }
    
    public void setScheduleStartTimeFormat(String format){
        scheduleStartTimeFormat = format;
    }
    public String getScheduleStartTimeFormat(){
        return scheduleStartTimeFormat;
    }
    
    public void setScheduleExecuteWhenOverStartTimeQueryIndex(int index){
        scheduleExecuteWhenOverStartTimeQueryIndex = index;
    }
    public int getScheduleExecuteWhenOverStartTimeQueryIndex(){
        return scheduleExecuteWhenOverStartTimeQueryIndex;
    }
    
    public void setScheduleEndTimeQueryIndex(int index){
        scheduleEndTimeQueryIndex = index;
    }
    public int getScheduleEndTimeQueryIndex(){
        return scheduleEndTimeQueryIndex;
    }
    
    public void setScheduleEndTimeFormat(String format){
        scheduleEndTimeFormat = format;
    }
    public String getScheduleEndTimeFormat(){
        return scheduleEndTimeFormat;
    }
    
    public void setScheduleDelayQueryIndex(int index){
        scheduleDelayQueryIndex = index;
    }
    public int getScheduleDelayQueryIndex(){
        return scheduleDelayQueryIndex;
    }
    
    public void setSchedulePeriodQueryIndex(int index){
        schedulePeriodQueryIndex = index;
    }
    public int getSchedulePeriodQueryIndex(){
        return schedulePeriodQueryIndex;
    }
    
    public void setScheduleCountQueryIndex(int index){
        scheduleCountQueryIndex = index;
    }
    public int getScheduleCountQueryIndex(){
        return scheduleCountQueryIndex;
    }
    
    public void setScheduleFixedRateQueryIndex(int index){
        scheduleFixedRateQueryIndex = index;
    }
    public int getScheduleFixedRateQueryIndex(){
        return scheduleFixedRateQueryIndex;
    }
    
    public void setScheduleDependsScheduleNamesQueryIndex(int index){
        scheduleDependsScheduleNamesQueryIndex = index;
    }
    public int getScheduleDependsScheduleNamesQueryIndex(){
        return scheduleDependsScheduleNamesQueryIndex;
    }
    
    public void setScheduleDependencyTimeoutQueryIndex(int index){
        scheduleDependencyTimeoutQueryIndex = index;
    }
    public int getScheduleDependencyTimeoutQueryIndex(){
        return scheduleDependencyTimeoutQueryIndex;
    }
    
    public void setScheduleDependencyConfirmIntervalQueryIndex(int index){
        scheduleDependencyConfirmIntervalQueryIndex = index;
    }
    public int getScheduleDependencyConfirmIntervalQueryIndex(){
        return scheduleDependencyConfirmIntervalQueryIndex;
    }
    
    public void setScheduleErrorLogMessageIdQueryIndex(int index){
        scheduleErrorLogMessageIdQueryIndex = index;
    }
    public int getScheduleErrorLogMessageIdQueryIndex(){
        return scheduleErrorLogMessageIdQueryIndex;
    }
    
    public void setScheduleErrorLogMessageId(String id){
        scheduleErrorLogMessageId = id;
    }
    public String getScheduleErrorLogMessageId(){
        return scheduleErrorLogMessageId;
    }
    
    public void setScheduleTimeoutLogMessageIdQueryIndex(int index){
        scheduleTimeoutLogMessageIdQueryIndex = index;
    }
    public int getScheduleTimeoutLogMessageIdQueryIndex(){
        return scheduleTimeoutLogMessageIdQueryIndex;
    }
    
    public void setScheduleTimeoutLogMessageId(String id){
        scheduleTimeoutLogMessageId = id;
    }
    public String getScheduleTimeoutLogMessageId(){
        return scheduleTimeoutLogMessageId;
    }
    
    public void setScheduleJournalServiceName(ServiceName name){
        scheduleJournalServiceName = name;
    }
    public ServiceName getScheduleJournalServiceName(){
        return scheduleJournalServiceName;
    }
    
    public void setScheduleJournalServiceNameQueryIndex(int index){
        scheduleJournalServiceNameQueryIndex = index;
    }
    public int getScheduleJournalServiceNameQueryIndex(){
        return scheduleJournalServiceNameQueryIndex;
    }
    
    public void setScheduleQueueServiceName(ServiceName name){
        scheduleQueueServiceName = name;
    }
    public ServiceName getScheduleQueueServiceName(){
        return scheduleQueueServiceName;
    }
    
    public void setScheduleQueueServiceNameQueryIndex(int index){
        scheduleQueueServiceNameQueryIndex = index;
    }
    public int getScheduleQueueServiceNameQueryIndex(){
        return scheduleQueueServiceNameQueryIndex;
    }
    
    public void setScheduleGarbageQueueQueryIndex(int index){
        scheduleGarbageQueueQueryIndex = index;
    }
    public int getScheduleGarbageQueueQueryIndex(){
        return scheduleGarbageQueueQueryIndex;
    }
    
    public void setScheduleStateManagerServiceName(ServiceName name){
        scheduleStateManagerServiceName = name;
    }
    public ServiceName getScheduleStateManagerServiceName(){
        return scheduleStateManagerServiceName;
    }
    
    public void setScheduleStateManagerServiceNameQueryIndex(int index){
        scheduleStateManagerServiceNameQueryIndex = index;
    }
    public int getScheduleStateManagerServiceNameQueryIndex(){
        return scheduleStateManagerServiceNameQueryIndex;
    }
    
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    public void setScheduleBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        scheduleBeanFlowInvokerFactory = factory;
    }
    
    public void setScheduleFacadeCaller(FacadeCaller caller){
        scheduleFacadeCaller = caller;
    }
    
    public void setScheduleJournal(Journal journal){
        scheduleJournal = journal;
    }
    
    public void setScheduleQueue(Queue queue){
        scheduleQueue = queue;
    }
    
    public void setScheduleStateManager(ScheduleStateManager manager){
        scheduleStateManager = manager;
    }
    
    public void createService() throws Exception{
        scheduleMap = new HashMap();
    }
    
    public void startService() throws Exception{
        if(connectionFactoryServiceName == null && connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactoryServiceName or  ConnectionFactory must be specified.");
        }
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
        }
        if(scheduleBeanFlowInvokerFactoryServiceName != null){
            scheduleBeanFlowInvokerFactory
                 = (BeanFlowInvokerFactory)ServiceManagerFactory
                    .getServiceObject(scheduleBeanFlowInvokerFactoryServiceName);
        }
        if(scheduleFacadeCallerServiceName != null){
            scheduleFacadeCaller = (FacadeCaller)ServiceManagerFactory
                    .getServiceObject(scheduleFacadeCallerServiceName);
        }
        if(scheduleStartTimeFormat != null){
            scheduleStartTimeDateFormat
                 = new SimpleDateFormat(scheduleStartTimeFormat);
        }
        if(scheduleEndTimeFormat != null){
            scheduleEndTimeDateFormat
                 = new SimpleDateFormat(scheduleEndTimeFormat);
        }
        if(scheduleJournalServiceName != null){
            scheduleJournal = (Journal)ServiceManagerFactory
                    .getServiceObject(scheduleJournalServiceName);
        }
        if(scheduleStateManagerServiceName != null){
            scheduleStateManager = (ScheduleStateManager)ServiceManagerFactory
                    .getServiceObject(scheduleStateManagerServiceName);
        }
        
        if(scheduleMasterQuery == null){
            throw new IllegalArgumentException("ScheduleMasterQuery must be specified.");
        }
        final Connection con = connectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery(scheduleMasterQuery);
            int rowNum = 0;
            while(rs.next()){
                final String key = createScheduleKey(rs);
                Map schedules = (Map)scheduleMap.get(key);
                if(schedules == null){
                    schedules = new HashMap();
                    scheduleMap.put(key, schedules);
                }
                final TimerScheduleService schedule
                     = createSchedule(rs, ++rowNum);
                if(schedule != null){
                    if(schedules.containsKey(schedule.getName())){
                        throw new IllegalArgumentException("Schedule name is duplicated : " + schedule.getName());
                    }
                    schedule.create();
                    schedule.start();
                    schedules.put(schedule.getName(), schedule);
                }
            }
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(stmt != null){
                try{
                    stmt.close();
                }catch(SQLException e){
                }
            }
            con.close();
        }
    }
    
    public void stopService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = null;
        }
        if(scheduleBeanFlowInvokerFactoryServiceName != null){
            scheduleBeanFlowInvokerFactory = null;
        }
        final Iterator nameAndScheduleItr = scheduleMap.values().iterator();
        while(nameAndScheduleItr.hasNext()){
            final Map nameAndSchedule = (Map)nameAndScheduleItr.next();
            final Iterator scheduleItr = nameAndSchedule.values().iterator();
            while(scheduleItr.hasNext()){
                final TimerScheduleService schedule
                     = (TimerScheduleService)scheduleItr.next();
                schedule.stop();
                schedule.destroy();
                scheduleItr.remove();
            }
            nameAndScheduleItr.remove();
        }
    }
    
    public void destoryService() throws Exception{
        connectionFactory = null;
        scheduleMap = null;
    }
    
    protected String createScheduleKey(ResultSet rs) throws Exception{
        String key = null;
        if(scheduleKeyQueryIndex > 0){
            key = rs.getString(scheduleKeyQueryIndex);
        }
        return key;
    }
    
    protected TimerScheduleService createSchedule(ResultSet rs, int rowNum) throws Exception{
        final TimerScheduleService schedule = new TimerScheduleService();
        
        final String name = createScheduleName(rs, rowNum);
        schedule.setServiceManagerName(getServiceManagerName());
        schedule.setServiceName(name);
        schedule.setName(name);
        schedule.setTask(createScheduleTask(name, rs));
        schedule.setStartTime(createStartTime(name, rs));
        final Boolean isExecuteWhenOverStartTime
             = createExecuteWhenOverStartTime(name, rs);
        if(isExecuteWhenOverStartTime != null){
            schedule.setExecuteWhenOverStartTime(
                isExecuteWhenOverStartTime.booleanValue()
            );
        }
        schedule.setEndTime(createEndTime(name, rs));
        final long delay = createDelay(name, rs);
        if(delay > 0){
            schedule.setDelay(delay);
        }
        final long period = createPeriod(name, rs);
        if(period > 0){
            schedule.setPeriod(period);
        }
        final int count = createCount(name, rs);
        if(count > 0){
            schedule.setCount(count);
        }
        final Boolean isFixedRate = createFixedRate(name, rs);
        if(isFixedRate != null){
            schedule.setFixedRate(
                isFixedRate.booleanValue()
            );
        }
        final String[] dependsScheduleNames
             = createDependsScheduleNames(name, rs);
        if(dependsScheduleNames != null){
            schedule.setDependsScheduleNames(
                dependsScheduleNames
            );
        }
        final long dependencyTimeout = createDependencyTimeout(name, rs);
        if(dependencyTimeout > 0){
            schedule.setDependencyTimeout(dependencyTimeout);
        }
        final long dependencyConfirmInterval
             = createDependencyConfirmInterval(name, rs);
        if(dependencyConfirmInterval > 0){
            schedule.setDependencyConfirmInterval(dependencyConfirmInterval);
        }
        final String errorLogMessageId
             = createErrorLogMessageId(name, rs);
        if(errorLogMessageId != null){
            schedule.setErrorLogMessageId(errorLogMessageId);
        }
        final String timeoutLogMessageId
             = createTimeoutLogMessageId(name, rs);
        if(timeoutLogMessageId != null){
            schedule.setTimeoutLogMessageId(timeoutLogMessageId);
        }
        final Journal journal = createJournal(name, rs);
        if(journal != null){
            schedule.setJournal(journal);
        }
        final Queue queue = createQueue(name, rs);
        if(queue != null){
            schedule.setQueue(queue);
        }
        final Boolean isGarbageQueue = createGarbageQueue(name, rs);
        if(isGarbageQueue != null){
            schedule.setGarbageQueue(
                isGarbageQueue.booleanValue()
            );
        }
        final ScheduleStateManager stateManager = createScheduleStateManager(name, rs);
        if(stateManager != null){
            schedule.setScheduleStateManager(stateManager);
        }
        return schedule;
    }
    
    protected String createScheduleName(ResultSet rs, int rowNum) throws Exception{
        String name = null;
        if(scheduleNameQueryIndex > 0){
            name = rs.getString(scheduleNameQueryIndex);
            if(rs.wasNull()){
                throw new IllegalArgumentException("Schedule name was null.");
            }
        }else{
            name = getServiceName() + rowNum;
        }
        return name;
    }
    
    protected ScheduleTask createScheduleTask(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        final ServiceNameEditor serviceNameEditor =  new ServiceNameEditor();
        if(scheduleBeanFlowNamesQueryIndex > 0){
            final String beanFlowNames
                 = rs.getString(scheduleBeanFlowNamesQueryIndex);
            if(beanFlowNames != null && beanFlowNames.length() != 0){
                final StringArrayEditor stringArrayEditor
                     =  new StringArrayEditor();
                stringArrayEditor.setAsText(beanFlowNames);
                final String[] beanFlowNameArray
                     = (String[])stringArrayEditor.getValue();
                FacadeCaller facadeCaller = null;
                if(scheduleFacadeCallerServiceNameQueryIndex > 0){
                    final String serviceNameStr = rs.getString(
                        scheduleFacadeCallerServiceNameQueryIndex
                    );
                    if(serviceNameStr != null){
                        serviceNameEditor.setAsText(serviceNameStr);
                        facadeCaller = (FacadeCaller)ServiceManagerFactory
                                .getServiceObject(
                                    (ServiceName)serviceNameEditor.getValue()
                                );
                    }
                }
                if(facadeCaller == null){
                    facadeCaller = scheduleFacadeCaller;
                }
                if(facadeCaller == null){
                    throw new IllegalArgumentException("FacadeCaller is null : " + scheduleName);
                }
                String iocCallType = null;
                if(scheduleIOCCallTypeQueryIndex > 0){
                    iocCallType = rs.getString(
                        scheduleIOCCallTypeQueryIndex
                    );
                }
                final IOCCallScheduleTaskService iocCallTask
                     = new IOCCallScheduleTaskService();
                iocCallTask.setBeanFlowNames(beanFlowNameArray);
                iocCallTask.setFacadeCaller(facadeCaller);
                if(iocCallType != null){
                    iocCallTask.setIOCCallType(iocCallType);
                }
                iocCallTask.create();
                iocCallTask.start();
                return iocCallTask;
            }
        }
        if(scheduleBeanFlowNameQueryIndex > 0){
            final String beanFlowName
                 = rs.getString(scheduleBeanFlowNameQueryIndex);
            if(beanFlowName != null && beanFlowName.length() != 0){
                BeanFlowInvokerFactory beanFlowInvokerFactory = null;
                if(scheduleBeanFlowInvokerFactoryServiceNameQueryIndex > 0){
                    final String serviceNameStr = rs.getString(
                        scheduleBeanFlowInvokerFactoryServiceNameQueryIndex
                    );
                    if(serviceNameStr != null){
                        serviceNameEditor.setAsText(serviceNameStr);
                        beanFlowInvokerFactory
                             = (BeanFlowInvokerFactory)ServiceManagerFactory
                                .getServiceObject(
                                    (ServiceName)serviceNameEditor.getValue()
                                );
                    }
                }
                if(beanFlowInvokerFactory == null){
                    beanFlowInvokerFactory = scheduleBeanFlowInvokerFactory;
                }
                if(beanFlowInvokerFactory == null){
                    throw new IllegalArgumentException("BeanFlowInvokerFactory is null : " + scheduleName);
                }
                final BeanFlowCallScheduleTaskService beanFlowTask
                     = new BeanFlowCallScheduleTaskService();
                beanFlowTask.setBeanFlowName(beanFlowName);
                beanFlowTask.setBeanFlowInvokerFactory(beanFlowInvokerFactory);
                beanFlowTask.create();
                beanFlowTask.start();
                return beanFlowTask;
            }
        }
        if(scheduleTaskServiceNameQueryIndex > 0){
            final String scheduleTaskServiceNameStr
                 = rs.getString(scheduleTaskServiceNameQueryIndex);
            if(scheduleTaskServiceNameStr != null){
                serviceNameEditor.setAsText(
                    scheduleTaskServiceNameStr
                );
                return (ScheduleTask)ServiceManagerFactory.getServiceObject(
                    (ServiceName)serviceNameEditor.getValue()
                );
            }
        }
        throw new IllegalArgumentException("Task is null : " + scheduleName);
    }
    
    protected Date createStartTime(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        return getDate(
            scheduleName,
            rs,
            scheduleStartTimeQueryIndex,
            scheduleStartTimeDateFormat
        );
    }
    
    private Date getDate(
        String scheduleName,
        ResultSet rs,
        int queryIndex,
        DateFormat format
    ) throws Exception{
        Date result = null;
        if(queryIndex > 0){
            final int sqlType = rs.getMetaData().getColumnType(
                queryIndex
            );
            switch(sqlType){
            case Types.DATE:
            case Types.TIMESTAMP:
                result = rs.getTimestamp(queryIndex);
                break;
            case Types.TIME:
                final Date time = rs.getTime(queryIndex);
                final Calendar now = Calendar.getInstance();
                final Calendar cal = Calendar.getInstance();
                cal.setTime(time);
                cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                cal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
                result = cal.getTime();
                break;
            case Types.CHAR:
            case Types.VARCHAR:
                if(format == null){
                    throw new IllegalArgumentException("DateFormat is null : " + scheduleName);
                }
                String dateStr = rs.getString(queryIndex);
                if(dateStr != null && sqlType == Types.CHAR){
                    dateStr = dateStr.trim();
                }
                if(dateStr != null && dateStr.length() != 0){
                    result = format.parse(dateStr);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported sql type : " + sqlType);
            }
        }
        return result;
    }
    
    protected Boolean createExecuteWhenOverStartTime(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        return getBoolean(
            scheduleName,
            rs,
            scheduleExecuteWhenOverStartTimeQueryIndex
        );
    }
    
    private Boolean getBoolean(
        String scheduleName,
        ResultSet rs,
        int queryIndex
    ) throws Exception{
        Boolean result = null;
        if(queryIndex > 0){
            final int sqlType = rs.getMetaData().getColumnType(
                queryIndex
            );
            switch(sqlType){
            case Types.BOOLEAN:
                final boolean boolVal = rs.getBoolean(
                    queryIndex
                );
                if(!rs.wasNull()){
                    result = boolVal ? Boolean.TRUE : Boolean.FALSE;
                }
                break;
            case Types.CHAR:
            case Types.VARCHAR:
                String strVal = rs.getString(
                    queryIndex
                );
                if(strVal != null && sqlType == Types.CHAR){
                    strVal = strVal.trim();
                }
                if(strVal != null && strVal.length() != 0){
                    result = ("0".equals(strVal) || strVal == null)
                             || !"true".equalsIgnoreCase(strVal)
                                 ? Boolean.FALSE : Boolean.TRUE;
                }
                break;
            case Types.NUMERIC:
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                final int intVal = rs.getInt(
                    queryIndex
                );
                if(!rs.wasNull()){
                    result = intVal == 0 ? Boolean.FALSE : Boolean.TRUE;
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported sql type : " + sqlType);
            }
        }
        return result;
    }
    
    protected Date createEndTime(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        return getDate(
            scheduleName,
            rs,
            scheduleEndTimeQueryIndex,
            scheduleEndTimeDateFormat
        );
    }
    
    protected long createDelay(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        long delay = -1;
        if(scheduleDelayQueryIndex > 0){
            delay = rs.getLong(scheduleDelayQueryIndex);
            if(rs.wasNull()){
                delay = -1;
            }
        }
        return delay;
    }
    
    protected long createPeriod(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        long period = -1;
        if(schedulePeriodQueryIndex > 0){
            period = rs.getLong(schedulePeriodQueryIndex);
            if(rs.wasNull()){
                period = -1;
            }
        }
        return period;
    }
    
    protected int createCount(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        int period = -1;
        if(scheduleCountQueryIndex > 0){
            period = rs.getInt(scheduleCountQueryIndex);
            if(rs.wasNull()){
                period = -1;
            }
        }
        return period;
    }
    
    protected Boolean createFixedRate(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        return getBoolean(
            scheduleName,
            rs,
            scheduleFixedRateQueryIndex
        );
    }
    
    protected String[] createDependsScheduleNames(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        String[] dependsScheduleNames = null;
        if(scheduleDependsScheduleNamesQueryIndex > 0){
            String names = rs.getString(scheduleDependsScheduleNamesQueryIndex);
            if(names != null && names.length() != 0){
                names = names.trim();
            }
            if(names != null && names.length() != 0){
                final StringArrayEditor stringArrayEditor
                     =  new StringArrayEditor();
                stringArrayEditor.setAsText(names);
                dependsScheduleNames = (String[])stringArrayEditor.getValue();
            }
        }
        return dependsScheduleNames;
    }
    
    protected long createDependencyTimeout(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        long timeout = -1;
        if(scheduleDependencyTimeoutQueryIndex > 0){
            timeout = rs.getLong(scheduleDependencyTimeoutQueryIndex);
            if(rs.wasNull()){
                timeout = -1;
            }
        }
        return timeout;
    }
    
    protected long createDependencyConfirmInterval(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        long interval = -1;
        if(scheduleDependencyConfirmIntervalQueryIndex > 0){
            interval = rs.getLong(scheduleDependencyConfirmIntervalQueryIndex);
            if(rs.wasNull()){
                interval = -1;
            }
        }
        return interval;
    }
    
    protected String createErrorLogMessageId(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        String id = null;
        if(scheduleErrorLogMessageIdQueryIndex > 0){
            id = rs.getString(scheduleErrorLogMessageIdQueryIndex);
            if(id != null && id.length() != 0){
                id = id.trim();
            }
        }
        if(id == null || id.length() == 0){
            id = scheduleErrorLogMessageId;
        }
        return id;
    }
    
    protected String createTimeoutLogMessageId(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        String id = null;
        if(scheduleTimeoutLogMessageIdQueryIndex > 0){
            id = rs.getString(scheduleTimeoutLogMessageIdQueryIndex);
            if(id != null && id.length() != 0){
                id = id.trim();
            }
        }
        if(id == null || id.length() == 0){
            id = scheduleTimeoutLogMessageId;
        }
        return id;
    }
    
    protected Journal createJournal(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        Journal journal = null;
        if(scheduleJournalServiceNameQueryIndex > 0){
            final String serviceNameStr = rs.getString(
                scheduleJournalServiceNameQueryIndex
            );
            if(serviceNameStr != null){
                final ServiceNameEditor serviceNameEditor
                     =  new ServiceNameEditor();
                serviceNameEditor.setAsText(serviceNameStr);
                journal = (Journal)ServiceManagerFactory.getServiceObject(
                    (ServiceName)serviceNameEditor.getValue()
                );
            }
        }
        if(journal == null){
            journal = scheduleJournal;
        }
        return journal;
    }
    
    protected Queue createQueue(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        Queue queue = null;
        if(scheduleQueueServiceNameQueryIndex > 0){
            final String serviceNameStr = rs.getString(
                scheduleQueueServiceNameQueryIndex
            );
            if(serviceNameStr != null){
                final ServiceNameEditor serviceNameEditor
                     =  new ServiceNameEditor();
                serviceNameEditor.setAsText(serviceNameStr);
                queue = (Queue)ServiceManagerFactory.getServiceObject(
                    (ServiceName)serviceNameEditor.getValue()
                );
            }
        }
        if(queue == null){
            if(scheduleQueueServiceName != null){
                queue = (Queue)ServiceManagerFactory
                        .getServiceObject(scheduleQueueServiceName);
            }else{
                queue = scheduleQueue;
            }
        }
        return queue;
    }
    
    protected Boolean createGarbageQueue(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        return getBoolean(
            scheduleName,
            rs,
            scheduleGarbageQueueQueryIndex
        );
    }
    
    protected ScheduleStateManager createScheduleStateManager(
        String scheduleName,
        ResultSet rs
    ) throws Exception{
        ScheduleStateManager manager = null;
        if(scheduleStateManagerServiceNameQueryIndex > 0){
            final String serviceNameStr = rs.getString(
                scheduleStateManagerServiceNameQueryIndex
            );
            if(serviceNameStr != null){
                final ServiceNameEditor serviceNameEditor
                     =  new ServiceNameEditor();
                serviceNameEditor.setAsText(serviceNameStr);
                manager = (ScheduleStateManager)ServiceManagerFactory.getServiceObject(
                    (ServiceName)serviceNameEditor.getValue()
                );
            }
        }
        if(manager == null){
            manager = scheduleStateManager;
        }
        return manager;
    }
    
    public Schedule[] getSchedules(Object key){
        Map schedules = (Map)scheduleMap.get(key);
        if(schedules == null && key != null){
            schedules = (Map)scheduleMap.get(null);
        }
        return schedules == null ? new Schedule[0]
             : (Schedule[])schedules.values().toArray(new Schedule[schedules.size()]);
    }
}