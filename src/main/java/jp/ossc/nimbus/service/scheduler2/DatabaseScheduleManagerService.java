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
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.sql.*;
import java.text.*;
import java.io.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.connection.*;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.system.Time;
import jp.ossc.nimbus.util.converter.*;

/**
 * �f�[�^�x�[�X�X�P�W���[���Ǘ��B<p>
 * �X�P�W���[�����f�[�^�x�[�X�ō쐬�E�Ǘ����A���s���ׂ��X�P�W���[����񋟂���B<br>
 * ���̃T�[�r�X���g�p����ɂ́A�\�߃f�[�^�x�[�X�Ɉȉ��̃e�[�u�����K�v�ł���B<br>
 * <ul>
 *   <li>�X�P�W���[���}�X�^�e�[�u��({@link DatabaseScheduleManagerService.ScheduleMasterTableSchema ScheduleMasterTableSchema})</li>
 *   <li>�X�P�W���[���O���[�v�}�X�^�e�[�u��({@link DatabaseScheduleManagerService.ScheduleGroupMasterTableSchema ScheduleGroupMasterTableSchema})</li>
 *   <li>�X�P�W���[���ˑ��֌W�}�X�^�e�[�u��({@link DatabaseScheduleManagerService.ScheduleDependsMasterTableSchema ScheduleDependsMasterTableSchema})</li>
 *   <li>�X�P�W���[���O���[�v�ˑ��֌W�}�X�^�e�[�u��({@link DatabaseScheduleManagerService.ScheduleGroupDependsMasterTableSchema ScheduleGroupDependsMasterTableSchema})</li>
 *   <li>�X�P�W���[���e�[�u��({@link DatabaseScheduleManagerService.ScheduleTableSchema ScheduleTableSchema})</li>
 *   <li>�X�P�W���[���O���[�v�e�[�u��({@link DatabaseScheduleManagerService.ScheduleGroupTableSchema ScheduleGroupTableSchema})</li>
 *   <li>�X�P�W���[���ˑ��֌W�e�[�u��({@link DatabaseScheduleManagerService.ScheduleDependsTableSchema ScheduleDependsTableSchema})</li>
 *   <li>�X�P�W���[���O���[�v�ˑ��֌W�e�[�u��({@link DatabaseScheduleManagerService.ScheduleGroupDependsTableSchema ScheduleGroupDependsTableSchema})</li>
 * </ul>
 *
 * @author M.Takata
 */
public class DatabaseScheduleManagerService extends ServiceBase
 implements ScheduleManager, DatabaseScheduleManagerServiceMBean{
    
    private static final long serialVersionUID = -768179222440496616L;
    protected Properties scheduleMakerTypeMapping;
    protected Map addedScheduleMakerMap;
    protected Map scheduleMakerMap;
    protected boolean isScheduleMakerTypeRegexEnabled;
    protected ServiceName defaultScheduleMakerServiceName;
    protected ScheduleMaker defaultScheduleMaker;
    
    protected ServiceName connectionFactoryServiceName;
    protected ConnectionFactory connectionFactory;
    
    protected ServiceName timeServiceName;
    protected Time time;
    
    protected ScheduleMasterTableSchema scheduleMasterTableSchema = new ScheduleMasterTableSchema();
    protected ScheduleGroupMasterTableSchema scheduleGroupMasterTableSchema = new ScheduleGroupMasterTableSchema();
    protected ScheduleDependsMasterTableSchema scheduleDependsMasterTableSchema = new ScheduleDependsMasterTableSchema();
    protected ScheduleGroupDependsMasterTableSchema scheduleGroupDependsMasterTableSchema = new ScheduleGroupDependsMasterTableSchema();
    protected ScheduleTableSchema scheduleTableSchema = new ScheduleTableSchema();
    protected ScheduleGroupTableSchema scheduleGroupTableSchema = new ScheduleGroupTableSchema();
    protected ScheduleDependsTableSchema scheduleDependsTableSchema = new ScheduleDependsTableSchema();
    protected ScheduleGroupDependsTableSchema scheduleGroupDependsTableSchema = new ScheduleGroupDependsTableSchema();
    
    protected String nextScheduleIdSelectQuery;
    
    protected String dateFormat = DEFAULT_DATE_FORMAT;
    protected String timeFormat = DEFAULT_TIME_FORMAT;
    
    protected String updateUserId;
    
    protected Set scheduleControlListeners;
    
    protected boolean isMakeScheduleOnStart = true;
    
    protected long controlStateCheckInterval = 1000l;
    protected Daemon controlStateChecker;
    
    protected long timeoverCheckInterval = 1000l;
    protected Daemon timeoverChecker;
    
    protected boolean isLockForFindExecutable;
    
    protected ServiceName clusterServiceName;
    protected ClusterService cluster;
    protected ClusterListener clusterListener;
    
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    
    protected boolean isUseConcatFunction;
    protected boolean isJsonInput;
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setDefaultScheduleMakerServiceName(ServiceName name){
        defaultScheduleMakerServiceName = name;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ServiceName getDefaultScheduleMakerServiceName(){
        return defaultScheduleMakerServiceName;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleMakerTypeMapping(Properties mapping){
        scheduleMakerTypeMapping = mapping;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public Properties getScheduleMakerTypeMapping(){
        return scheduleMakerTypeMapping;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleMakerTypeRegexEnabled(boolean isEnable){
        isScheduleMakerTypeRegexEnabled = isEnable;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isScheduleMakerTypeRegexEnabled(){
        return isScheduleMakerTypeRegexEnabled;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setTimeServiceName(ServiceName name){
        timeServiceName = name;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ServiceName getTimeServiceName(){
        return timeServiceName;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setTimeFormat(String format){
        timeFormat = format;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public String getTimeFormat(){
        return timeFormat;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setUpdateUserId(String id){
        updateUserId = id;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public String getUpdateUserId(){
        return updateUserId;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleMasterTableSchema getScheduleMasterTableSchema(){
        return scheduleMasterTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleMasterTableSchema(ScheduleMasterTableSchema schema){
        scheduleMasterTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleGroupMasterTableSchema getScheduleGroupMasterTableSchema(){
        return scheduleGroupMasterTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleGroupMasterTableSchema(ScheduleGroupMasterTableSchema schema){
        scheduleGroupMasterTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleDependsMasterTableSchema getScheduleDependsMasterTableSchema(){
        return scheduleDependsMasterTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleDependsMasterTableSchema(ScheduleDependsMasterTableSchema schema){
        scheduleDependsMasterTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleGroupDependsMasterTableSchema getScheduleGroupDependsMasterTableSchema(){
        return scheduleGroupDependsMasterTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleGroupDependsMasterTableSchema(ScheduleGroupDependsMasterTableSchema schema){
        scheduleGroupDependsMasterTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleTableSchema getScheduleTableSchema(){
        return scheduleTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleTableSchema(ScheduleTableSchema schema){
        scheduleTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleGroupTableSchema getScheduleGroupTableSchema(){
        return scheduleGroupTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleGroupTableSchema(ScheduleGroupTableSchema schema){
        scheduleGroupTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleDependsTableSchema getScheduleDependsTableSchema(){
        return scheduleDependsTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleDependsTableSchema(ScheduleDependsTableSchema schema){
        scheduleDependsTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ScheduleGroupDependsTableSchema getScheduleGroupDependsTableSchema(){
        return scheduleGroupDependsTableSchema;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setScheduleGroupDependsTableSchema(ScheduleGroupDependsTableSchema schema){
        scheduleGroupDependsTableSchema = schema;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setNextScheduleIdSelectQuery(String query){
        nextScheduleIdSelectQuery = query;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public String getNextScheduleIdSelectQuery(){
        return nextScheduleIdSelectQuery;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setMakeScheduleOnStart(boolean isMake){
        isMakeScheduleOnStart = isMake;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isMakeScheduleOnStart(){
        return isMakeScheduleOnStart;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setControlStateCheckInterval(long interval){
        controlStateCheckInterval = interval;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public long getControlStateCheckInterval(){
        return controlStateCheckInterval;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setTimeoverCheckInterval(long interval){
        timeoverCheckInterval = interval;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public long getTimeoverCheckInterval(){
        return timeoverCheckInterval;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setLockForFindExecutable(boolean isLock){
        isLockForFindExecutable = isLock;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isLockForFindExecutable(){
        return isLockForFindExecutable;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setUseConcatFunction(boolean isUse){
        isUseConcatFunction = isUse;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isUseConcatFunction(){
        return isUseConcatFunction;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void setJSONInput(boolean isJson){
        isJsonInput = isJson;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isJSONInput(){
        return isJsonInput;
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void startControlStateCheck(){
        if(controlStateChecker != null){
            controlStateChecker.resume();
        }
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isStartControlStateCheck(){
        return controlStateChecker == null ? false : controlStateChecker.isRunning() && !controlStateChecker.isSusupend();
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void stopControlStateCheck(){
        if(controlStateChecker != null){
            controlStateChecker.suspend();
        }
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void startTimeoverCheck(){
        if(timeoverChecker != null){
            timeoverChecker.resume();
        }
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public boolean isStartTimeoverCheck(){
        return timeoverChecker == null ? false : timeoverChecker.isRunning() && !timeoverChecker.isSusupend();
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void stopTimeoverCheck(){
        if(timeoverChecker != null){
            timeoverChecker.suspend();
        }
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     *
     * @exception Exception �T�[�r�X�̐��������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        scheduleMakerMap = new HashMap();
        addedScheduleMakerMap = null;
        scheduleControlListeners = Collections.synchronizedSet(new LinkedHashSet());
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        
        if(scheduleMakerTypeMapping != null
             && scheduleMakerTypeMapping.size() != 0){
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            final Iterator entries
                = scheduleMakerTypeMapping.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                editor.setAsText((String)entry.getValue());
                final ServiceName scheduleMakerServiceName
                    = (ServiceName)editor.getValue();
                final ScheduleMaker scheduleMaker
                    = (ScheduleMaker)ServiceManagerFactory
                        .getServiceObject(scheduleMakerServiceName);
                if(scheduleMakerMap.containsKey(entry.getKey())){
                    throw new IllegalArgumentException(
                        "Dupulicate scheduleMakerTypeMapping : "
                            + entry.getKey()
                    );
                }
                scheduleMakerMap.put(entry.getKey(), scheduleMaker);
            }
        }
        
        if(defaultScheduleMakerServiceName != null){
            defaultScheduleMaker = (ScheduleMaker)ServiceManagerFactory
                .getServiceObject(defaultScheduleMakerServiceName);
        }
        if(defaultScheduleMaker == null){
            final DefaultScheduleMakerService defaultScheduleMakerService
                = new DefaultScheduleMakerService();
            defaultScheduleMakerService.create();
            defaultScheduleMakerService.start();
            defaultScheduleMaker = defaultScheduleMakerService;
        }
        
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory.getServiceObject(sequenceServiceName);
        }
        
        if(timeServiceName != null){
            time = (Time)ServiceManagerFactory.getServiceObject(timeServiceName);
        }
        
        if(updateUserId == null){
            updateUserId = java.net.InetAddress.getLocalHost().getHostName();
        }
        
        final SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        format.applyPattern(timeFormat);
        
        if(isMakeScheduleOnStart){
            final Date now = time == null ? new Date() : new Date(time.currentTimeMillis());
            final List oldScheduleList = findSchedules(now);
            if(oldScheduleList == null || oldScheduleList.size() == 0){
                makeSchedule(now);
            }
        }
        
        if(controlStateCheckInterval > 0
            && scheduleControlListeners != null
            && scheduleControlListeners.size() != 0
        ){
            controlStateChecker = new Daemon(new ControlStateChecker());
            controlStateChecker.setName("Nimbus SchedulerManagerControlStateChecker " + getServiceNameObject());
            controlStateChecker.suspend();
            controlStateChecker.start();
        }
        
        if(timeoverCheckInterval > 0){
            timeoverChecker = new Daemon(new TimeoverChecker());
            timeoverChecker.setName("Nimbus SchedulerManagerTimeoverChecker " + getServiceNameObject());
            timeoverChecker.suspend();
            timeoverChecker.start();
        }
        if(clusterServiceName != null && (controlStateChecker != null || timeoverChecker != null)){
            cluster = (ClusterService)ServiceManagerFactory.getServiceObject(clusterServiceName);
            clusterListener = new ClusterListener();
            cluster.addClusterListener(clusterListener);
        }else{
            if(controlStateChecker != null){
                controlStateChecker.resume();
            }
            if(timeoverChecker != null){
                timeoverChecker.resume();
            }
        }
    }
    
    /**
     * �T�[�r�X�̒�~�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        
        if(controlStateChecker != null){
            controlStateChecker.stop();
            controlStateChecker = null;
        }
        
        if(timeoverChecker != null){
            timeoverChecker.stop();
            timeoverChecker = null;
        }
        
        if(cluster != null){
            cluster.removeClusterListener(clusterListener);
            clusterListener = null;
            cluster = null;
        }
        
        if(scheduleMakerMap != null){
            scheduleMakerMap.clear();
        }
    }
    
    /**
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        scheduleMakerMap = null;
        addedScheduleMakerMap = null;
        scheduleControlListeners = null;
    }
    
    // ScheduleManager��JavaDoc
    public List makeSchedule(Date date) throws ScheduleMakeException{
        final List masters = new ArrayList();
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleMakeException(e);
        }
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            
            rs = st.executeQuery(
                "select * from " + scheduleMasterTableSchema.table
                    + " where " + scheduleMasterTableSchema.template + "<>'1' and " +  scheduleMasterTableSchema.enable + "='1'"
            );
            while(rs.next()){
                DefaultScheduleMaster scheduleMaster = createScheduleMaster(rs);
                masters.add(scheduleMaster);
            }
            rs.close();
            rs = null;
            setDependsOnScheduleMasters(con, masters);
            setGroupIdsOnScheduleMasters(con, masters);
            setGroupDependsOnGroupOnScheduleMasters(con, masters);
        }catch(ScheduleManageException e){
            throw new ScheduleMakeException(e);
        }catch(ParseException e){
            throw new ScheduleMakeException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleMakeException(e);
        }catch(IOException e){
            throw new ScheduleMakeException(e);
        }catch(SQLException e){
            throw new ScheduleMakeException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        return makeSchedule(date, masters);
    }
    
    // ScheduleManager��JavaDoc
    public List makeSchedule(Date date, ScheduleMaster master) throws ScheduleMakeException{
        if(master == null){
            return new ArrayList();
        }
        final List masters = new ArrayList();
        masters.add(master);
        return makeSchedule(date, masters);
    }
    
    // ScheduleManager��JavaDoc
    public List makeSchedule(Date date, List masters) throws ScheduleMakeException{
        if(masters.size() == 0){
            return new ArrayList();
        }
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleMakeException(e);
        }
        Statement st = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;
        PreparedStatement ps5 = null;
        try{
            st = con.createStatement();
            ps1 = con.prepareStatement(
                "insert into " + scheduleTableSchema.table
                    + " ("
                    + scheduleTableSchema.id + ','
                    + scheduleTableSchema.masterId + ','
                    + scheduleTableSchema.date + ','
                    + scheduleTableSchema.time + ','
                    + scheduleTableSchema.taskName + ','
                    + scheduleTableSchema.input + ','
                    + scheduleTableSchema.output + ','
                    + scheduleTableSchema.initialDate + ','
                    + scheduleTableSchema.initialTime + ','
                    + scheduleTableSchema.retryInterval + ','
                    + scheduleTableSchema.retryEndTime + ','
                    + scheduleTableSchema.maxDelayTime + ','
                    + scheduleTableSchema.state + ','
                    + scheduleTableSchema.controlState + ','
                    + scheduleTableSchema.checkState + ','
                    + scheduleTableSchema.executorKey + ','
                    + scheduleTableSchema.executorType + ','
                    + scheduleTableSchema.executeStartTime + ','
                    + scheduleTableSchema.executeEndTime + ','
                    + scheduleTableSchema.rowVersion + ','
                    + scheduleTableSchema.updateUserId + ','
                    + scheduleTableSchema.updateTime
                    + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'0','" + updateUserId + "',?)"
            );
            ps2 = con.prepareStatement(
                "insert into " + scheduleDependsTableSchema.table
                    + " ("
                    + scheduleDependsTableSchema.id + ','
                    + scheduleDependsTableSchema.dependsId + ','
                    + scheduleDependsTableSchema.dependsGroupId + ','
                    + scheduleDependsTableSchema.groupId + ','
                    + scheduleDependsTableSchema.ignoreError + ','
                    + scheduleDependsTableSchema.rowVersion + ','
                    + scheduleDependsTableSchema.updateUserId + ','
                    + scheduleDependsTableSchema.updateTime
                    + ") values(?,?,?,?,?,'0','" + updateUserId + "',?)"
            );
            ps3 = con.prepareStatement(
                "select " + scheduleGroupDependsMasterTableSchema.dependsGroupId + ','
                    + scheduleGroupDependsMasterTableSchema.ignoreError
                    + " from " + scheduleGroupDependsMasterTableSchema.table
                    + " where " + scheduleGroupDependsMasterTableSchema.groupId + "=?"
            );
            ps4 = con.prepareStatement(
                "insert into " + scheduleGroupDependsTableSchema.table
                    + " ("
                    + scheduleGroupDependsTableSchema.groupId + ','
                    + scheduleGroupDependsTableSchema.dependsGroupId + ','
                    + scheduleGroupDependsTableSchema.ignoreError + ','
                    + scheduleGroupDependsTableSchema.rowVersion + ','
                    + scheduleGroupDependsTableSchema.updateUserId + ','
                    + scheduleGroupDependsTableSchema.updateTime
                    + ") values(?,?,?,'0','" + updateUserId + "',?)"
            );
            ps5 = con.prepareStatement(
                "insert into " + scheduleGroupTableSchema.table
                    + " ("
                    + scheduleGroupTableSchema.id + ','
                    + scheduleGroupTableSchema.groupId + ','
                    + scheduleGroupTableSchema.masterGroupId + ','
                    + scheduleGroupTableSchema.rowVersion + ','
                    + scheduleGroupTableSchema.updateUserId + ','
                    + scheduleGroupTableSchema.updateTime
                    + ") values(?,?,?,'0','" + updateUserId + "',?)"
            );
            List result = new ArrayList();
            Map groupMap = new HashMap();
            for(int i = 0; i < masters.size(); i ++){
                ScheduleMaster scheduleMaster = (ScheduleMaster)masters.get(i);
                ScheduleMaker maker = getScheduleMaker(scheduleMaster.getScheduleType());
                final Schedule[] schedules = maker.makeSchedule(
                    date,
                    scheduleMaster
                );
                if(schedules == null || schedules.length == 0){
                    continue;
                }
                for(int j = 0; j < schedules.length; j++){
                    addSchedule(
                        st,
                        ps1,
                        ps2,
                        ps3,
                        ps4,
                        ps5,
                        schedules[j],
                        groupMap
                    );
                    result.add(schedules[j]);
                }
            }
            return result;
        }catch(ScheduleManageException e){
            throw new ScheduleMakeException(e);
        }catch(SQLException e){
            throw new ScheduleMakeException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(ps1 != null){
                try{
                    ps1.close();
                }catch(SQLException e){
                }
            }
            if(ps2 != null){
                try{
                    ps2.close();
                }catch(SQLException e){
                }
            }
            if(ps3 != null){
                try{
                    ps3.close();
                }catch(SQLException e){
                }
            }
            if(ps4 != null){
                try{
                    ps4.close();
                }catch(SQLException e){
                }
            }
            if(ps5 != null){
                try{
                    ps5.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    /**
     * �X�P�W���[����ǉ�����B<p>
     *
     * @param nextScheduleIdStatement �X�P�W���[��ID���̔Ԃ���Statement
     * @param scheduleInsertStatement �X�P�W���[����INSERT����Statement
     * @param scheduleDependsInsertStatement �X�P�W���[���̈ˑ��֌W��INSERT����Statement
     * @param scheduleGroupDependsMasterSelectStatement �X�P�W���[���̃O���[�v�ˑ��֌W�}�X�^��SELECT����Statement
     * @param scheduleGroupDependsInsertStatement �X�P�W���[���̃O���[�v�ˑ��֌W��INSERT����Statement
     * @param scheduleGroupInsertStatement �X�P�W���[���O���[�v��INSERT����Statement
     * @param schedule �X�P�W���[��
     * @param groupMap �}�X�^�O���[�vID�ɑ΂��Ĕ��s�����O���[�vID�̃}�b�v
     * @exception ScheduleManageException �X�P�W���[���̒ǉ��Ɏ��s�����ꍇ
     */
    protected void addSchedule(
        Statement nextScheduleIdStatement,
        PreparedStatement scheduleInsertStatement,
        PreparedStatement scheduleDependsInsertStatement,
        PreparedStatement scheduleGroupDependsMasterSelectStatement,
        PreparedStatement scheduleGroupDependsInsertStatement,
        PreparedStatement scheduleGroupInsertStatement,
        Schedule schedule,
        Map groupMap
    ) throws ScheduleManageException{
        ResultSet rs = null;
        try{
            if(nextScheduleIdSelectQuery != null){
                rs = nextScheduleIdStatement.executeQuery(
                    nextScheduleIdSelectQuery
                );
                rs.next();
                schedule.setId(rs.getObject(1).toString());
                rs.close();
                rs = null;
            }else if(sequence != null){
                schedule.setId(sequence.increment());
            }
            int index = 0;
            scheduleInsertStatement.setString(
                ++index,
                schedule.getId()
            );
            scheduleInsertStatement.setString(
                ++index,
                schedule.getMasterId()
            );
            final SimpleDateFormat format
                = new SimpleDateFormat(dateFormat);
            if(schedule.getTime() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(schedule.getTime())
                );
            }
            format.applyPattern(timeFormat);
            if(schedule.getTime() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(schedule.getTime())
                );
            }
            scheduleInsertStatement.setString(
                ++index,
                schedule.getTaskName()
            );
            if(schedule.getInput() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.VARCHAR
                );
            }else{
                Object input = schedule.getInput();
                if(isJsonInput && !(input instanceof String)){
                    BeanJSONConverter jsonConverter = new BeanJSONConverter();
                    jsonConverter.setUnicodeEscape(false);
                    StringStreamConverter streamConverter = new StringStreamConverter();
                    input = streamConverter.convertToObject(jsonConverter.convertToStream(input));
                }
                scheduleTableSchema.setInputObject(++index, scheduleInsertStatement, input);
            }
            if(schedule.getOutput() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.VARCHAR
                );
            }else{
                scheduleTableSchema.setOutputObject(++index, scheduleInsertStatement, schedule.getOutput());
            }
            format.applyPattern(dateFormat);
            final Date initialTime = schedule.getInitialTime() == null
                ? schedule.getTime() : schedule.getInitialTime();
            if(initialTime == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(initialTime)
                );
            }
            format.applyPattern(timeFormat);
            if(initialTime == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(initialTime)
                );
            }
            if(schedule.getRetryInterval() > 0){
                scheduleInsertStatement.setLong(
                    ++index,
                    schedule.getRetryInterval()
                );
            }else{
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.DECIMAL
                );
            }
            format.applyPattern(dateFormat + timeFormat);
            if(schedule.getRetryEndTime() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(schedule.getRetryEndTime())
                );
            }
            if(schedule.getMaxDelayTime() > 0){
                scheduleInsertStatement.setLong(
                    ++index,
                    schedule.getMaxDelayTime()
                );
            }else{
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.DECIMAL
                );
            }
            scheduleInsertStatement.setString(
                ++index,
                scheduleTableSchema.getStateString(schedule.getState())
            );
            scheduleInsertStatement.setString(
                ++index,
                scheduleTableSchema.getControlStateString(
                    schedule.getControlState()
                )
            );
            scheduleInsertStatement.setString(
                ++index,
                scheduleTableSchema.getCheckStateString(
                    schedule.getCheckState()
                )
            );
            if(schedule.getExecutorKey() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.VARCHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    schedule.getExecutorKey()
                );
            }
            if(schedule.getExecutorType() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.VARCHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    schedule.getExecutorType()
                );
            }
            if(schedule.getExecuteStartTime() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(schedule.getExecuteStartTime())
                );
            }
            if(schedule.getExecuteEndTime() == null){
                scheduleInsertStatement.setNull(
                    ++index,
                    Types.CHAR
                );
            }else{
                scheduleInsertStatement.setString(
                    ++index,
                    format.format(schedule.getExecuteEndTime())
                );
            }
            Timestamp now = new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis());
            scheduleInsertStatement.setTimestamp(
                ++index,
                now
            );
            scheduleInsertStatement.executeUpdate();
            
            final ScheduleDepends[] depends = schedule.getDepends();
            if(depends != null && depends.length != 0){
                for(int i = 0; i < depends.length; i++){
                    scheduleDependsInsertStatement.setString(
                        1,
                        schedule.getId()
                    );
                    scheduleDependsInsertStatement.setString(
                        2,
                        depends[i].getMasterId()
                    );
                    scheduleDependsInsertStatement.setNull(
                        3,
                        Types.VARCHAR
                    );
                    scheduleDependsInsertStatement.setNull(
                        4,
                        Types.VARCHAR
                    );
                    scheduleDependsInsertStatement.setString(
                        5,
                        depends[i].isIgnoreError() ? "1" : "0"
                    );
                    scheduleDependsInsertStatement.setTimestamp(
                        6,
                        now
                    );
                    scheduleDependsInsertStatement.executeUpdate();
                }
            }
            final ScheduleDepends[] dependsOnGroup = schedule.getDependsOnGroup();
            if(dependsOnGroup != null && dependsOnGroup.length != 0){
                for(int i = 0; i < dependsOnGroup.length; i++){
                    scheduleDependsInsertStatement.setString(
                        1,
                        schedule.getId()
                    );
                    scheduleDependsInsertStatement.setNull(
                        2,
                        Types.VARCHAR
                    );
                    scheduleDependsInsertStatement.setString(
                        3,
                        dependsOnGroup[i].getMasterId()
                    );
                    scheduleDependsInsertStatement.setNull(
                        4,
                        Types.VARCHAR
                    );
                    scheduleDependsInsertStatement.setString(
                        5,
                        dependsOnGroup[i].isIgnoreError() ? "1" : "0"
                    );
                    scheduleDependsInsertStatement.setTimestamp(
                        6,
                        now
                    );
                    scheduleDependsInsertStatement.executeUpdate();
                }
            }
            
            final String[] masterGroupIds = schedule.getMasterGroupIds();
            if(masterGroupIds != null && masterGroupIds.length != 0){
                for(int i = 0; i < masterGroupIds.length; i++){
                    scheduleGroupInsertStatement.setString(
                        1,
                        schedule.getId()
                    );
                    String groupId = groupMap == null ? null : (String)groupMap.get(masterGroupIds[i]);
                    if(groupId == null){
                        if(nextScheduleIdSelectQuery != null){
                            rs = nextScheduleIdStatement.executeQuery(
                                nextScheduleIdSelectQuery
                            );
                            rs.next();
                            groupId = rs.getObject(1).toString();
                            rs.close();
                            rs = null;
                        }else if(sequence != null){
                            groupId = sequence.increment();
                        }
                        if(groupMap != null){
                            groupMap.put(masterGroupIds[i], groupId);
                        }
                        scheduleGroupDependsMasterSelectStatement.setString(1, masterGroupIds[i]);
                        rs = scheduleGroupDependsMasterSelectStatement.executeQuery();
                        while(rs.next()){
                            scheduleGroupDependsInsertStatement.setString(1, groupId);
                            scheduleGroupDependsInsertStatement.setString(2, rs.getString(1));
                            scheduleGroupDependsInsertStatement.setString(3, rs.getString(2));
                            scheduleGroupDependsInsertStatement.setTimestamp(
                                4,
                                now
                            );
                            scheduleGroupDependsInsertStatement.executeUpdate();
                        }
                        rs.close();
                        rs = null;
                    }
                    schedule.setGroupId(masterGroupIds[i], groupId);
                    scheduleGroupInsertStatement.setString(
                        2,
                        groupId
                    );
                    scheduleGroupInsertStatement.setString(
                        3,
                        masterGroupIds[i]
                    );
                    scheduleGroupInsertStatement.setTimestamp(
                        4,
                        now
                    );
                    scheduleGroupInsertStatement.executeUpdate();
                }
            }
            
            Map dependsInGroupMap = schedule.getDependsInGroupMasterMap();
            if(dependsInGroupMap != null && dependsInGroupMap.size() != 0){
                Iterator entries = dependsInGroupMap.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    ScheduleDepends[] deps = (ScheduleDepends[])entry.getValue();
                    for(int i = 0; i < deps.length; i++){
                        scheduleDependsInsertStatement.setString(
                            1,
                            schedule.getId()
                        );
                        scheduleDependsInsertStatement.setString(
                            2,
                            deps[i].getMasterId()
                        );
                        scheduleDependsInsertStatement.setNull(
                            3,
                            Types.VARCHAR
                        );
                        scheduleDependsInsertStatement.setString(
                            4,
                            schedule.getGroupId((String)entry.getKey())
                        );
                        scheduleDependsInsertStatement.setString(
                            5,
                            deps[i].isIgnoreError() ? "1" : "0"
                        );
                        scheduleDependsInsertStatement.setTimestamp(
                            6,
                            now
                        );
                        scheduleDependsInsertStatement.executeUpdate();
                    }
                }
            }
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public void setScheduleMaker(String scheduleType, ScheduleMaker maker)
     throws IllegalArgumentException{
        if(addedScheduleMakerMap == null){
            addedScheduleMakerMap = new HashMap();
        }
        if(addedScheduleMakerMap.containsKey(scheduleType)){
            throw new IllegalArgumentException(
                "Dupulicate scheduleType : " + scheduleType
            );
        }
        addedScheduleMakerMap.put(scheduleType, maker);
    }
    
    // ScheduleManager��JavaDoc
    public ScheduleMaker getScheduleMaker(String scheduleType){
        ScheduleMaker maker = (ScheduleMaker)scheduleMakerMap.get(
            scheduleType
        );
        if(isScheduleMakerTypeRegexEnabled && maker == null){
            Iterator entries = scheduleMakerMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String key = (String)entry.getKey();
                try{
                    if(Pattern.matches(key, scheduleType)){
                        maker = (ScheduleMaker)entry.getValue();
                        break;
                    }
                }catch(PatternSyntaxException e){
                }
            }
        }
        if(maker == null){
            maker = defaultScheduleMaker;
        }
        return maker;
    }
    
    // ScheduleManager��JavaDoc
    public Map getScheduleMakerMap(){
        return scheduleMakerMap;
    }
    
    // ScheduleManager��JavaDoc
    public void setDefaultScheduleMaker(ScheduleMaker maker){
        defaultScheduleMaker = maker;
    }
    
    // ScheduleManager��JavaDoc
    public ScheduleMaker getDefaultScheduleMaker(){
        return defaultScheduleMaker;
    }
    
    // ScheduleManager��JavaDoc
    public List findScheduleMasters(String groupId) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        Statement st = null;
        ResultSet rs = null;
        List result = new ArrayList();
        try{
            StringBuffer buf = new StringBuffer();
            buf.append("select ")
                .append("A.").append(scheduleMasterTableSchema.id).append(" as ").append(scheduleMasterTableSchema.id).append(',')
                .append("A.").append(scheduleMasterTableSchema.taskName).append(" as ").append(scheduleMasterTableSchema.taskName).append(',')
                .append("A.").append(scheduleMasterTableSchema.scheduleType).append(" as ").append(scheduleMasterTableSchema.scheduleType).append(',')
                .append("A.").append(scheduleMasterTableSchema.input).append(" as ").append(scheduleMasterTableSchema.input).append(',')
                .append("A.").append(scheduleMasterTableSchema.startTime).append(" as ").append(scheduleMasterTableSchema.startTime).append(',')
                .append("A.").append(scheduleMasterTableSchema.endTime).append(" as ").append(scheduleMasterTableSchema.endTime).append(',')
                .append("A.").append(scheduleMasterTableSchema.repeatInterval).append(" as ").append(scheduleMasterTableSchema.repeatInterval).append(',')
                .append("A.").append(scheduleMasterTableSchema.retryInterval).append(" as ").append(scheduleMasterTableSchema.retryInterval).append(',')
                .append("A.").append(scheduleMasterTableSchema.retryEndTime).append(" as ").append(scheduleMasterTableSchema.retryEndTime).append(',')
                .append("A.").append(scheduleMasterTableSchema.maxDelayTime).append(" as ").append(scheduleMasterTableSchema.maxDelayTime).append(',')
                .append("A.").append(scheduleMasterTableSchema.enable).append(" as ").append(scheduleMasterTableSchema.enable).append(',')
                .append("A.").append(scheduleMasterTableSchema.executorKey).append(" as ").append(scheduleMasterTableSchema.executorKey).append(',')
                .append("A.").append(scheduleMasterTableSchema.executorType).append(" as ").append(scheduleMasterTableSchema.executorType).append(',')
                .append("A.").append(scheduleMasterTableSchema.template).append(" as ").append(scheduleMasterTableSchema.template)
                .append(" from ").append(scheduleMasterTableSchema.table).append(" A");
            if(groupId == null){
                buf.append(" where not exists (select 1 from ").append(scheduleGroupMasterTableSchema.table)
                    .append(" where ").append("A.").append(scheduleMasterTableSchema.id).append("=B.").append(scheduleGroupMasterTableSchema.id).append(')');
                st = con.createStatement();
                rs = st.executeQuery(buf.toString());
            }else{
                buf.append(",(select ").append(scheduleGroupMasterTableSchema.id).append(" from ").append(scheduleGroupMasterTableSchema.table)
                    .append(" where ").append(scheduleGroupMasterTableSchema.groupId).append("=?) B")
                    .append(" where ").append("A.").append(scheduleMasterTableSchema.id).append("=B.").append(scheduleGroupMasterTableSchema.id);
                st = con.prepareStatement(buf.toString());
                if(groupId != null){
                    ((PreparedStatement)st).setString(1, groupId);
                }
                rs = ((PreparedStatement)st).executeQuery();
            }
            while(rs.next()){
                DefaultScheduleMaster scheduleMaster = createScheduleMaster(rs);
                result.add(scheduleMaster);
            }
            rs.close();
            rs = null;
            st.close();
            st = null;
            setDependsOnScheduleMasters(con, result);
            setGroupIdsOnScheduleMasters(con, result);
            setGroupDependsOnGroupOnScheduleMasters(con, result);
        }catch(ScheduleManageException e){
            throw new ScheduleMakeException(e);
        }catch(ParseException e){
            throw new ScheduleMakeException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleMakeException(e);
        }catch(IOException e){
            throw new ScheduleMakeException(e);
        }catch(SQLException e){
            throw new ScheduleMakeException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        return result;
    }
    
    // ScheduleManager��JavaDoc
    public List findAllScheduleMasters() throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        Statement st = null;
        ResultSet rs = null;
        List result = new ArrayList();
        try{
            st = con.createStatement();
            rs = st.executeQuery(
                "select * from " + scheduleMasterTableSchema.table
            );
            while(rs.next()){
                DefaultScheduleMaster scheduleMaster = createScheduleMaster(rs);
                result.add(scheduleMaster);
            }
            rs.close();
            rs = null;
            setDependsOnScheduleMasters(con, result);
            setGroupIdsOnScheduleMasters(con, result);
            setGroupDependsOnGroupOnScheduleMasters(con, result);
        }catch(ScheduleManageException e){
            throw new ScheduleMakeException(e);
        }catch(ParseException e){
            throw new ScheduleMakeException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleMakeException(e);
        }catch(IOException e){
            throw new ScheduleMakeException(e);
        }catch(SQLException e){
            throw new ScheduleMakeException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        return result;
    }
    
    // ScheduleManager��JavaDoc
    public ScheduleMaster findScheduleMaster(String id) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select * from " + scheduleMasterTableSchema.table
                    + " where " + scheduleMasterTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                return null;
            }
            DefaultScheduleMaster scheduleMaster = createScheduleMaster(rs);
            setDependsOnScheduleMaster(con, scheduleMaster);
            setGroupIdsOnScheduleMaster(con, scheduleMaster);
            setGroupDependsOnGroupOnScheduleMaster(con, scheduleMaster);
            return scheduleMaster;
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }

    /**
     * �X�P�W���[���}�X�^�e�[�u���̌������ʂ���X�P�W���[���}�X�^�𐶐�����B<p>
     *
     * @param rs �X�P�W���[���}�X�^�e�[�u���̌�������
     * @return �X�P�W���[���}�X�^
     * @exception SQLException SQL��O
     * @exception ParseException ���t�Ǝ����̃p�[�X�Ɏ��s�����ꍇ
     * @exception IOException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception ClassNotFoundException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
     */
    protected DefaultScheduleMaster createScheduleMaster(ResultSet rs)
     throws SQLException, ParseException, IOException, ClassNotFoundException{
        final SimpleDateFormat timeFormatter
            = new SimpleDateFormat(timeFormat);
        final DefaultScheduleMaster scheduleMaster
            = new DefaultScheduleMaster();
        scheduleMaster.setId(
            rs.getString(scheduleMasterTableSchema.id)
        );
        scheduleMaster.setTaskName(
            rs.getString(scheduleMasterTableSchema.taskName)
        );
        scheduleMaster.setScheduleType(
            rs.getString(scheduleMasterTableSchema.scheduleType)
        );
        scheduleMaster.setInput(
            scheduleMasterTableSchema.getInputObject(rs)
        );
        String str = rs.getString(scheduleMasterTableSchema.startTime);
        if(str != null && str.length() != 0){
            scheduleMaster.setStartTime(timeFormatter.parse(str));
        }
        str = rs.getString(scheduleMasterTableSchema.endTime);
        if(str != null && str.length() != 0){
            scheduleMaster.setEndTime(timeFormatter.parse(str));
        }
        long longVal = rs.getLong(
            scheduleMasterTableSchema.repeatInterval
        );
        if(!rs.wasNull()){
            scheduleMaster.setRepeatInterval(longVal);
        }
        longVal = rs.getLong(
            scheduleMasterTableSchema.retryInterval
        );
        if(!rs.wasNull()){
            scheduleMaster.setRetryInterval(longVal);
        }
        str = rs.getString(scheduleMasterTableSchema.retryEndTime);
        if(str != null && str.length() != 0){
            scheduleMaster.setRetryEndTime(timeFormatter.parse(str));
        }
        longVal = rs.getLong(
            scheduleMasterTableSchema.maxDelayTime
        );
        if(!rs.wasNull()){
            scheduleMaster.setMaxDelayTime(longVal);
        }
        scheduleMaster.setExecutorKey(
            rs.getString(scheduleMasterTableSchema.executorKey)
        );
        scheduleMaster.setExecutorType(
            rs.getString(scheduleMasterTableSchema.executorType)
        );
        String enableStr = rs.getString(scheduleMasterTableSchema.enable);
        boolean isEnabled = enableStr != null && !"0".equals(enableStr);
        scheduleMaster.setEnabled(isEnabled);
        String templateStr = rs.getString(scheduleMasterTableSchema.template);
        boolean isTemplate = templateStr != null && !"0".equals(templateStr);
        scheduleMaster.setTemplate(isTemplate);
        return scheduleMaster;
    }
    
    protected ScheduleMaster setDependsOnScheduleMaster(Connection con, ScheduleMaster schedule) throws SQLException{
        final List tmp = new ArrayList();
        tmp.add(schedule);
        setDependsOnScheduleMasters(con, tmp);
        return (ScheduleMaster)tmp.get(0);
    }
    
    protected List setDependsOnScheduleMasters(Connection con, List schedules) throws SQLException{
        if(schedules.size() == 0){
            return schedules;
        }
        final StringBuffer buf = new StringBuffer();
        Statement st = con.createStatement();
        ResultSet rs = null;
        try{
            Map scheduleMap = new HashMap();
            Map scheduleDependsMap = new HashMap();
            Map scheduleDependsOnGroupMap = new HashMap();
            Map scheduleDependsInGroupMap = new HashMap();
            int to = schedules.size() % 1000 == 0 ? schedules.size() / 1000 : schedules.size() / 1000 + 1;
            for(int i = 0; i < to; i++){
                scheduleMap.clear();
                scheduleDependsMap.clear();
                buf.setLength(0);
                buf.append("select ")
                    .append(scheduleDependsMasterTableSchema.id).append(',')
                    .append(scheduleDependsMasterTableSchema.dependsId).append(',')
                    .append(scheduleDependsMasterTableSchema.dependsGroupId).append(',')
                    .append(scheduleDependsMasterTableSchema.groupId).append(',')
                    .append(scheduleDependsMasterTableSchema.ignoreError)
                    .append(" from ").append(scheduleDependsMasterTableSchema.table)
                    .append(" where ")
                    .append(scheduleDependsMasterTableSchema.id)
                    .append(" in (");
                int startIndex = i * 1000;
                for(int j = startIndex, jmax = Math.min(startIndex + 1000, schedules.size()); j < jmax; j++){
                    ScheduleMaster schedule = (ScheduleMaster)schedules.get(j);
                    buf.append('\'').append(schedule.getId()).append('\'');
                    if(j != jmax - 1){
                        buf.append(',');
                    }
                    scheduleMap.put(schedule.getId(), schedule);
                }
                buf.append(')');
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    final String id = rs.getString(1);
                    final String masterId = rs.getString(2);
                    final String masterGroupId = rs.getString(3);
                    final String groupId = rs.getString(4);
                    final String ignoreErrorStr = rs.getString(5);
                    boolean isIgnoreError = ignoreErrorStr != null && !"0".equals(ignoreErrorStr);
                    if(masterId == null){
                        List depends = (List)scheduleDependsOnGroupMap.get(id);
                        if(depends == null){
                            depends = new ArrayList();
                            scheduleDependsOnGroupMap.put(id, depends);
                        }
                        depends.add(new DefaultScheduleDepends(masterGroupId, isIgnoreError));
                    }else{
                        if(groupId == null){
                            List depends = (List)scheduleDependsMap.get(id);
                            if(depends == null){
                                depends = new ArrayList();
                                scheduleDependsMap.put(id, depends);
                            }
                            depends.add(new DefaultScheduleDepends(masterId, isIgnoreError));
                        }else{
                            Map dependsMap = (Map)scheduleDependsInGroupMap.get(id);
                            if(dependsMap == null){
                                dependsMap = new HashMap();
                                scheduleDependsInGroupMap.put(id, dependsMap);
                            }
                            List depends = (List)dependsMap.get(groupId);
                            if(depends == null){
                                depends = new ArrayList();
                                dependsMap.put(groupId, depends);
                            }
                            depends.add(new DefaultScheduleDepends(masterId, isIgnoreError));
                        }
                    }
                }
                Iterator itr = scheduleDependsMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultScheduleMaster schedule = (DefaultScheduleMaster)scheduleMap.get(entry.getKey());
                    final List depends = (List)entry.getValue();
                    if(depends.size() != 0){
                        schedule.setDepends((ScheduleDepends[])depends.toArray(new ScheduleDepends[depends.size()]));
                    }
                }
                itr = scheduleDependsOnGroupMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultScheduleMaster schedule = (DefaultScheduleMaster)scheduleMap.get(entry.getKey());
                    final List depends = (List)entry.getValue();
                    if(depends.size() != 0){
                        schedule.setDependsOnGroup((ScheduleDepends[])depends.toArray(new ScheduleDepends[depends.size()]));
                    }
                }
                itr = scheduleDependsInGroupMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultScheduleMaster schedule = (DefaultScheduleMaster)scheduleMap.get(entry.getKey());
                    final Map dependsMap = (Map)entry.getValue();
                    final Iterator itr2 = dependsMap.entrySet().iterator();
                    while(itr2.hasNext()){
                        final Map.Entry entry2 = (Map.Entry)itr2.next();
                        final List depends = (List)entry2.getValue();
                        if(depends.size() != 0){
                            schedule.setDependsInGroup(
                                (String)entry2.getKey(),
                                (ScheduleDepends[])depends.toArray(new ScheduleDepends[depends.size()])
                            );
                        }
                    }
                }
                rs.close();
                rs = null;
            }
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
                st = null;
            }
        }
        return schedules;
    }
    
    protected ScheduleMaster setGroupIdsOnScheduleMaster(Connection con, ScheduleMaster schedule) throws SQLException{
        final List tmp = new ArrayList();
        tmp.add(schedule);
        setGroupIdsOnScheduleMasters(con, tmp);
        return (ScheduleMaster)tmp.get(0);
    }
    
    protected List setGroupIdsOnScheduleMasters(Connection con, List schedules) throws SQLException{
        if(schedules.size() == 0){
            return schedules;
        }
        final StringBuffer buf = new StringBuffer();
        Statement st = con.createStatement();
        ResultSet rs = null;
        try{
            Map scheduleMap = new HashMap();
            Map scheduleGroupIdsMap = new HashMap();
            int to = schedules.size() % 1000 == 0 ? schedules.size() / 1000 : schedules.size() / 1000 + 1;
            for(int i = 0; i < to; i++){
                scheduleMap.clear();
                scheduleGroupIdsMap.clear();
                buf.setLength(0);
                buf.append("select * from ");
                buf.append(scheduleGroupMasterTableSchema.table);
                buf.append(" where ");
                buf.append(scheduleGroupMasterTableSchema.id);
                buf.append(" in (");
                int startIndex = i * 1000;
                for(int j = startIndex, jmax = Math.min(startIndex + 1000, schedules.size()); j < jmax; j++){
                    ScheduleMaster schedule = (ScheduleMaster)schedules.get(j);
                    buf.append('\'').append(schedule.getId()).append('\'');
                    if(j != jmax - 1){
                        buf.append(',');
                    }
                    scheduleMap.put(schedule.getId(), schedule);
                }
                buf.append(')');
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    final String id = rs.getString(scheduleGroupMasterTableSchema.id);
                    List groupIds = (List)scheduleGroupIdsMap.get(id);
                    if(groupIds == null){
                        groupIds = new ArrayList();
                        scheduleGroupIdsMap.put(id, groupIds);
                    }
                    groupIds.add(rs.getString(scheduleGroupMasterTableSchema.groupId));
                }
                final Iterator itr = scheduleGroupIdsMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultScheduleMaster schedule = (DefaultScheduleMaster)scheduleMap.get(entry.getKey());
                    final List groupIds = (List)entry.getValue();
                    if(groupIds.size() != 0){
                        schedule.setGroupIds((String[])groupIds.toArray(new String[groupIds.size()]));
                    }
                }
                rs.close();
                rs = null;
            }
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
                st = null;
            }
        }
        return schedules;
    }
    
    protected ScheduleMaster setGroupDependsOnGroupOnScheduleMaster(Connection con, ScheduleMaster schedule) throws SQLException{
        final List tmp = new ArrayList();
        tmp.add(schedule);
        setGroupDependsOnGroupOnScheduleMasters(con, tmp);
        return (ScheduleMaster)tmp.get(0);
    }
    
    protected List setGroupDependsOnGroupOnScheduleMasters(Connection con, List schedules) throws SQLException{
        if(schedules.size() == 0){
            return schedules;
        }
        final StringBuffer buf = new StringBuffer();
        Statement st = con.createStatement();
        ResultSet rs = null;
        try{
            Map groupScheduleMap = new HashMap();
            for(int i = 0, imax = schedules.size(); i < imax; i++){
                ScheduleMaster schedule = (ScheduleMaster)schedules.get(i);
                String[] groupIds = schedule.getGroupIds();
                if(groupIds == null){
                    continue;
                }
                for(int j = 0; j < groupIds.length; j++){
                    Set scheduleSet = (Set)groupScheduleMap.get(groupIds[j]);
                    if(scheduleSet == null){
                        scheduleSet = new HashSet();
                        groupScheduleMap.put(groupIds[j], scheduleSet);
                    }
                    scheduleSet.add(schedule);
                }
            }
            Map groupDependsMap = new HashMap();
            String[] groupIds = (String[])groupScheduleMap.keySet().toArray(new String[groupScheduleMap.size()]);
            int to = groupIds.length % 1000 == 0 ? groupIds.length / 1000 : groupIds.length / 1000 + 1;
            for(int i = 0; i < to; i++){
                buf.setLength(0);
                buf.append("select * from ").append(scheduleGroupDependsMasterTableSchema.table);
                buf.append(" where ");
                buf.append(scheduleGroupDependsMasterTableSchema.groupId);
                buf.append(" in (");
                int startIndex = i * 1000;
                for(int j = startIndex, jmax = Math.min(startIndex + 1000, groupIds.length); j < jmax; j++){
                    buf.append('\'').append(groupIds[j]).append('\'');
                    if(j != jmax - 1){
                        buf.append(',');
                    }
                }
                buf.append(')');
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    final String groupId = rs.getString(scheduleGroupDependsMasterTableSchema.groupId);
                    final String dependsGroupId = rs.getString(scheduleGroupDependsMasterTableSchema.dependsGroupId);
                    final String ignoreErrorStr = rs.getString(scheduleGroupDependsMasterTableSchema.ignoreError);
                    final boolean isIgnoreError = ignoreErrorStr != null && !"0".equals(ignoreErrorStr);
                    List dependsList = (List)groupDependsMap.get(groupId);
                    if(dependsList == null){
                        dependsList = new ArrayList();
                        groupDependsMap.put(groupId, dependsList);
                    }
                    dependsList.add(new DefaultScheduleDepends(dependsGroupId, isIgnoreError));
                }
                rs.close();
                rs = null;
                final Iterator itr = groupDependsMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final String groupId = (String)entry.getKey();
                    final Set scheduleSet = (Set)groupScheduleMap.get(groupId);
                    final List dependsList = (List)entry.getValue();
                    final ScheduleDepends[] depends = (ScheduleDepends[])dependsList.toArray(new ScheduleDepends[dependsList.size()]);
                    final Iterator itr2 = scheduleSet.iterator();
                    while(itr2.hasNext()){
                        final DefaultScheduleMaster schedule = (DefaultScheduleMaster)itr2.next();
                        schedule.setGroupDependsOnGroup(groupId, depends);
                    }
                }
            }
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
                st = null;
            }
        }
        return schedules;
    }
    
    // ScheduleManager��JavaDoc
    public List findAllSchedules() throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("select * from " + scheduleTableSchema.table);
            final List result = new ArrayList();
            while(rs.next()){
                result.add(createSchedule(rs));
            }
            setDependsOnSchedules(con, result);
            return result;
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }

    /**
     * �X�P�W���[���e�[�u���̌������ʂ���X�P�W���[���𐶐�����B<p>
     *
     * @param rs �X�P�W���[���e�[�u���̌�������
     * @return �X�P�W���[��
     * @exception SQLException SQL��O
     * @exception ParseException ���t�Ǝ����̃p�[�X�Ɏ��s�����ꍇ
     * @exception IOException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception ClassNotFoundException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
     * @exception ConvertException ���̓I�u�W�F�N�g�̕ϊ��Ɏ��s�����ꍇ
     */
    protected Schedule createSchedule(ResultSet rs)
     throws SQLException, ParseException, IOException, ClassNotFoundException{
        final DefaultSchedule schedule = new DefaultSchedule();
        schedule.setId(rs.getString(scheduleTableSchema.id));
        schedule.setMasterId(rs.getString(scheduleTableSchema.masterId));
        final SimpleDateFormat format = new SimpleDateFormat(
            dateFormat + timeFormat);
        String str = rs.getString(scheduleTableSchema.date)
            + rs.getString(scheduleTableSchema.time);
        schedule.setTime(format.parse(str));
        schedule.setTaskName(rs.getString(scheduleTableSchema.taskName));
        Object input = scheduleTableSchema.getInputObject(rs);
        if(isJsonInput && (input instanceof String)){
            BeanJSONConverter jsonConverter = new BeanJSONConverter();
            jsonConverter.setUnicodeEscape(false);
            StringStreamConverter streamConverter = new StringStreamConverter();
            input = jsonConverter.convertToObject(streamConverter.convertToStream(input));
        }
        schedule.setInput(input);
        schedule.setOutput(scheduleTableSchema.getOutputObject(rs));
        str = rs.getString(scheduleTableSchema.initialDate)
            + rs.getString(scheduleTableSchema.initialTime);
        schedule.setInitialTime(format.parse(str));
        long longVal = rs.getLong(scheduleTableSchema.retryInterval);
        if(!rs.wasNull()){
            schedule.setRetryInterval(longVal);
        }
        final String retryEndTimeStr = rs.getString(scheduleTableSchema.retryEndTime);
        if(retryEndTimeStr != null){
            schedule.setRetryEndTime(format.parse(retryEndTimeStr));
        }
        longVal = rs.getLong(scheduleTableSchema.maxDelayTime);
        if(!rs.wasNull()){
            schedule.setMaxDelayTime(longVal);
        }
        schedule.setState(
            scheduleTableSchema.getState(
                rs.getString(scheduleTableSchema.state)
            )
        );
        schedule.setControlState(
            scheduleTableSchema.getControlState(
                rs.getString(scheduleTableSchema.controlState)
            )
        );
        schedule.setCheckState(
            scheduleTableSchema.getCheckState(
                rs.getString(scheduleTableSchema.checkState)
            )
        );
        schedule.setExecutorKey(rs.getString(scheduleTableSchema.executorKey));
        schedule.setExecutorType(rs.getString(scheduleTableSchema.executorType));
        final String executeStartTimeStr = rs.getString(scheduleTableSchema.executeStartTime);
        if(executeStartTimeStr != null){
            schedule.setExecuteStartTime(format.parse(executeStartTimeStr));
        }
        final String executeEndTimeStr = rs.getString(scheduleTableSchema.executeEndTime);
        if(executeEndTimeStr != null){
            schedule.setExecuteEndTime(format.parse(executeEndTimeStr));
        }
        return schedule;
    }
    
    protected Schedule setDependsOnSchedule(Connection con, Schedule schedule) throws SQLException{
        final List tmp = new ArrayList();
        tmp.add(schedule);
        setDependsOnSchedules(con, tmp);
        return (Schedule)tmp.get(0);
    }
    
    protected List setDependsOnSchedules(Connection con, List schedules) throws SQLException{
        if(schedules.size() == 0){
            return schedules;
        }
        final StringBuffer buf = new StringBuffer();
        Statement st = con.createStatement();
        ResultSet rs = null;
        try{
            Map scheduleMap = new HashMap();
            Map scheduleDependsMap = new HashMap();
            Map scheduleDependsOnGroupMap = new HashMap();
            Map scheduleDependsInGroupMap = new HashMap();
            int to = schedules.size() % 1000 == 0 ? schedules.size() / 1000 : schedules.size() / 1000 + 1;
            for(int i = 0; i < to; i++){
                scheduleMap.clear();
                scheduleDependsMap.clear();
                buf.setLength(0);
                buf.append("select ")
                    .append(scheduleDependsTableSchema.id).append(',')
                    .append(scheduleDependsTableSchema.dependsId).append(',')
                    .append(scheduleDependsTableSchema.dependsGroupId).append(',')
                    .append(scheduleDependsTableSchema.groupId).append(',')
                    .append(scheduleDependsTableSchema.ignoreError)
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ")
                    .append(scheduleDependsTableSchema.id)
                    .append(" in (");
                int startIndex = i * 1000;
                for(int j = startIndex, jmax = Math.min(startIndex + 1000, schedules.size()); j < jmax; j++){
                    Schedule schedule = (Schedule)schedules.get(j);
                    buf.append('\'').append(schedule.getId()).append('\'');
                    if(j != jmax - 1){
                        buf.append(',');
                    }
                    scheduleMap.put(schedule.getId(), schedule);
                }
                buf.append(')');
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    final String id = rs.getString(1);
                    final String masterId = rs.getString(2);
                    final String masterGroupId = rs.getString(3);
                    final String groupId = rs.getString(4);
                    final String ignoreErrorStr = rs.getString(5);
                    final boolean isIgnoreError = ignoreErrorStr != null && !"0".equals(ignoreErrorStr);
                    if(masterId == null){
                        List depends = (List)scheduleDependsOnGroupMap.get(id);
                        if(depends == null){
                            depends = new ArrayList();
                            scheduleDependsOnGroupMap.put(id, depends);
                        }
                        depends.add(new DefaultScheduleDepends(masterGroupId, isIgnoreError));
                    }else{
                        if(groupId == null){
                            List depends = (List)scheduleDependsMap.get(id);
                            if(depends == null){
                                depends = new ArrayList();
                                scheduleDependsMap.put(id, depends);
                            }
                            depends.add(new DefaultScheduleDepends(masterId, isIgnoreError));
                        }else{
                            Map dependsMap = (Map)scheduleDependsInGroupMap.get(id);
                            if(dependsMap == null){
                                dependsMap = new HashMap();
                                scheduleDependsInGroupMap.put(id, dependsMap);
                            }
                            List depends = (List)dependsMap.get(groupId);
                            if(depends == null){
                                depends = new ArrayList();
                                dependsMap.put(groupId, depends);
                            }
                            depends.add(new DefaultScheduleDepends(masterId, isIgnoreError));
                        }
                    }
                }
                Iterator itr = scheduleDependsMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultSchedule schedule = (DefaultSchedule)scheduleMap.get(entry.getKey());
                    final List depends = (List)entry.getValue();
                    if(depends.size() != 0){
                        schedule.setDepends((ScheduleDepends[])depends.toArray(new ScheduleDepends[depends.size()]));
                    }
                }
                itr = scheduleDependsOnGroupMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultSchedule schedule = (DefaultSchedule)scheduleMap.get(entry.getKey());
                    final List depends = (List)entry.getValue();
                    if(depends.size() != 0){
                        schedule.setDependsOnGroup((ScheduleDepends[])depends.toArray(new ScheduleDepends[depends.size()]));
                    }
                }
                itr = scheduleDependsInGroupMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final DefaultSchedule schedule = (DefaultSchedule)scheduleMap.get(entry.getKey());
                    final Map dependsMap = (Map)entry.getValue();
                    final Iterator itr2 = dependsMap.entrySet().iterator();
                    while(itr2.hasNext()){
                        final Map.Entry entry2 = (Map.Entry)itr2.next();
                        final List depends = (List)entry2.getValue();
                        if(depends.size() != 0){
                            schedule.setDependsInGroup(
                                (String)entry2.getKey(),
                                (ScheduleDepends[])depends.toArray(new ScheduleDepends[depends.size()])
                            );
                        }
                    }
                }
                rs.close();
                rs = null;
            }
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
                st = null;
            }
        }
        return schedules;
    }
    
    protected Schedule setGroupIdsOnSchedule(Connection con, Schedule schedule) throws SQLException{
        final List tmp = new ArrayList();
        tmp.add(schedule);
        setGroupIdsOnSchedules(con, tmp);
        return (Schedule)tmp.get(0);
    }
    
    protected List setGroupIdsOnSchedules(Connection con, List schedules) throws SQLException{
        if(schedules.size() == 0){
            return schedules;
        }
        final StringBuffer buf = new StringBuffer();
        Statement st = con.createStatement();
        ResultSet rs = null;
        Map scheduleMap = new HashMap();
        Map scheduleGroupIdsMap = new HashMap();
        int to = schedules.size() % 1000 == 0 ? schedules.size() / 1000 : schedules.size() / 1000 + 1;
        for(int i = 0; i < to; i++){
            scheduleMap.clear();
            scheduleGroupIdsMap.clear();
            buf.setLength(0);
            buf.append("select * from ");
            buf.append(scheduleGroupTableSchema.table);
            buf.append(" where ");
            buf.append(scheduleGroupTableSchema.id);
            buf.append(" in (");
            int startIndex = i * 1000;
            for(int j = startIndex, jmax = Math.min(startIndex + 1000, schedules.size()); j < jmax; j++){
                Schedule schedule = (Schedule)schedules.get(j);
                buf.append('\'').append(schedule.getId()).append('\'');
                if(j != jmax - 1){
                    buf.append(',');
                }
                scheduleMap.put(schedule.getId(), schedule);
            }
            buf.append(')');
            rs = st.executeQuery(buf.toString());
            while(rs.next()){
                final String id = rs.getString(scheduleGroupTableSchema.id);
                final String groupId = rs.getString(scheduleGroupTableSchema.groupId);
                final String masterGroupId = rs.getString(scheduleGroupTableSchema.masterGroupId);
                Schedule schedule = (Schedule)scheduleMap.get(id);
                if(schedule != null){
                    ((DefaultSchedule)schedule).setGroupId(masterGroupId, groupId);
                }
                List groupIds = (List)scheduleGroupIdsMap.get(id);
                if(groupIds == null){
                    groupIds = new ArrayList();
                    scheduleGroupIdsMap.put(id, groupIds);
                }
                groupIds.add(groupId);
            }
            Iterator entries = scheduleGroupIdsMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                final String id = (String)entry.getKey();
                Schedule schedule = (Schedule)scheduleMap.get(id);
                if(schedule == null){
                    continue;
                }
                final List groupIds = (List)entry.getValue();
                String[] ids = (String[])groupIds.toArray(new String[groupIds.size()]);
                ((DefaultSchedule)schedule).setMasterGroupIds(ids);
            }
            rs.close();
        }
        return schedules;
    }
    
    protected Schedule setGroupDependsOnGroupOnSchedule(Connection con, Schedule schedule) throws SQLException{
        final List tmp = new ArrayList();
        tmp.add(schedule);
        setGroupDependsOnGroupOnSchedules(con, tmp);
        return (Schedule)tmp.get(0);
    }
    
    protected List setGroupDependsOnGroupOnSchedules(Connection con, List schedules) throws SQLException{
        if(schedules.size() == 0){
            return schedules;
        }
        final StringBuffer buf = new StringBuffer();
        Statement st = con.createStatement();
        ResultSet rs = null;
        try{
            Map groupScheduleMap = new HashMap();
            for(int i = 0, imax = schedules.size(); i < imax; i++){
                final Schedule schedule = (Schedule)schedules.get(i);
                final Map groupIdMap = schedule.getGroupIdMap();
                if(groupIdMap == null || groupIdMap.size() == 0){
                    continue;
                }
                final String[] groupIds = (String[])groupIdMap.values().toArray(new String[groupIdMap.size()]);
                for(int j = 0; j < groupIds.length; j++){
                    Set scheduleSet = (Set)groupScheduleMap.get(groupIds[j]);
                    if(scheduleSet == null){
                        scheduleSet = new HashSet();
                        groupScheduleMap.put(groupIds[j], scheduleSet);
                    }
                    scheduleSet.add(schedule);
                }
            }
            Map groupDependsMap = new HashMap();
            String[] groupIds = (String[])groupScheduleMap.keySet().toArray(new String[groupScheduleMap.size()]);
            int to = groupIds.length % 1000 == 0 ? groupIds.length / 1000 : groupIds.length / 1000 + 1;
            for(int i = 0; i < to; i++){
                buf.setLength(0);
                buf.append("select * from ").append(scheduleGroupDependsTableSchema.table);
                buf.append(" where ");
                buf.append(scheduleGroupDependsTableSchema.groupId);
                buf.append(" in (");
                int startIndex = i * 1000;
                for(int j = startIndex, jmax = Math.min(startIndex + 1000, groupIds.length); j < jmax; j++){
                    buf.append('\'').append(groupIds[j]).append('\'');
                    if(j != jmax - 1){
                        buf.append(',');
                    }
                }
                buf.append(')');
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    final String groupId = rs.getString(scheduleGroupDependsTableSchema.groupId);
                    final String dependsGroupId = rs.getString(scheduleGroupDependsTableSchema.dependsGroupId);
                    final String ignoreErrorStr = rs.getString(scheduleGroupDependsTableSchema.ignoreError);
                    final boolean isIgnoreError = ignoreErrorStr != null && !"0".equals(ignoreErrorStr);
                    List dependsList = (List)groupDependsMap.get(groupId);
                    if(dependsList == null){
                        dependsList = new ArrayList();
                        groupDependsMap.put(groupId, dependsList);
                    }
                    dependsList.add(new DefaultScheduleDepends(dependsGroupId, isIgnoreError));
                }
                rs.close();
                rs = null;
                final Iterator itr = groupDependsMap.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final String groupId = (String)entry.getKey();
                    final Set scheduleSet = (Set)groupScheduleMap.get(groupId);
                    final List dependsList = (List)entry.getValue();
                    final ScheduleDepends[] depends = (ScheduleDepends[])dependsList.toArray(new ScheduleDepends[dependsList.size()]);
                    final Iterator itr2 = scheduleSet.iterator();
                    while(itr2.hasNext()){
                        final DefaultSchedule schedule = (DefaultSchedule)itr2.next();
                        schedule.setGroupDependsOnGroup(groupId, depends);
                    }
                }
            }
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
                st = null;
            }
        }
        return schedules;
    }
    
    // ScheduleManager��JavaDoc
    public Schedule findSchedule(String id) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select * from " + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                return null;
            }
            return setGroupDependsOnGroupOnSchedule(con, setGroupIdsOnSchedule(con, setDependsOnSchedule(con, createSchedule(rs))));
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(String groupId) throws ScheduleManageException{
        List result = new ArrayList();
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            StringBuffer buf = new StringBuffer();
            buf.append("select ")
                .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                .append(" from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleGroupTableSchema.id).append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.groupId).append("=?)")
                .append(" where A.").append(scheduleTableSchema.id).append("=B.").append(scheduleGroupTableSchema.id);
            st = con.prepareStatement(buf.toString());
            st.setString(1, groupId);
            rs = st.executeQuery();
            while(rs.next()){
                result.add(createSchedule(rs));
            }
            setDependsOnSchedules(con, result);
            setGroupIdsOnSchedules(con, result);
            setGroupDependsOnGroupOnSchedules(con, result);
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(String masterId, String masterGroupId) throws ScheduleManageException{
        List result = new ArrayList();
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            if(masterId != null){
                st = con.prepareStatement(
                    "select * from " + scheduleTableSchema.table
                        + " where " + scheduleTableSchema.masterId + "=?"
                );
                st.setString(1, masterId);
            }else{
                StringBuffer buf = new StringBuffer();
                buf.append("select ")
                    .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                    .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                    .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                    .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                    .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                    .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                    .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                    .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                    .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                    .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                    .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                    .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table).append(" A, ")
                    .append("(select ").append(scheduleGroupMasterTableSchema.id).append(" from ").append(scheduleGroupMasterTableSchema.table)
                    .append(" where ").append(scheduleGroupMasterTableSchema.groupId).append("=?) B ")
                    .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleGroupMasterTableSchema.id);
                st = con.prepareStatement(buf.toString());
                st.setString(1, masterGroupId);
            }
            rs = st.executeQuery();
            while(rs.next()){
                result.add(createSchedule(rs));
            }
            setDependsOnSchedules(con, result);
            setGroupIdsOnSchedules(con, result);
            setGroupDependsOnGroupOnSchedules(con, result);
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(Date date) throws ScheduleManageException{
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date from = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        final Date to = cal.getTime();
        return findSchedules(from, to);
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(Date from, Date to)
     throws ScheduleManageException{
        return findSchedules(from, to, null);
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(int[] states) throws ScheduleManageException{
        return findSchedules(null, null, states);
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(Date from, Date to, int[] states)
     throws ScheduleManageException{
        return findSchedules(from, to, states, null, null, null, null, null, false);
    }
    
    // ScheduleManager��JavaDoc
    public List findSchedules(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId) throws ScheduleManageException{
        return findSchedules(from, to, states, masterId, masterGroupId, groupId, null, null, false);
    }
    
    protected StringBuffer concatQuery(StringBuffer buf, String s1, String s2){
        if(isUseConcatFunction){
            buf.append("concat(").append(s1).append(',').append(s2).append(')');
        }else{
            buf.append(s1).append("||").append(s2);
        }
        return buf;
    }
    
    protected List findSchedules(
        Date from,
        Date to,
        int[] states,
        String masterId,
        String masterGroupId,
        String groupId,
        String[] executorTypes,
        String executorKey,
        boolean isLock
    ) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            StringBuffer buf = new StringBuffer();
            buf.append("select ")
                .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                .append(" from ").append(scheduleTableSchema.table).append(" A");
            boolean isAppendWhere = false;
            if(masterGroupId != null || groupId != null){
                buf.append(", (select ").append(scheduleGroupTableSchema.id).append(" from ").append(scheduleGroupTableSchema.table);
                if(masterGroupId != null && groupId != null){
                    buf.append(" where ").append(scheduleGroupTableSchema.masterGroupId).append("=?")
                        .append(" and ").append(scheduleGroupTableSchema.groupId).append("=?) B");
                }else if(masterGroupId != null){
                    buf.append(" where ").append(scheduleGroupTableSchema.masterGroupId).append("=?) B ");
                }else{
                    buf.append(" where ").append(scheduleGroupTableSchema.groupId).append("=?) B ");
                }
                buf.append(" where A.").append(scheduleTableSchema.id).append("=B.").append(scheduleGroupTableSchema.id);
                isAppendWhere = true;
            }
            if(masterId != null){
                if(!isAppendWhere){
                    buf.append(" where (");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                buf.append("A.").append(scheduleTableSchema.masterId).append("=?)");
            }
            if(executorTypes != null && executorTypes.length != 0){
                if(!isAppendWhere){
                    buf.append(" where (");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                buf.append("A.").append(scheduleTableSchema.executorType).append(" is null or ");
                for(int i = 0; i < executorTypes.length; i++){
                    buf.append(scheduleTableSchema.executorType);
                    buf.append("=?");
                    if(i != executorTypes.length - 1){
                        buf.append(" or ");
                    }
                }
                buf.append(')');
            }
            if(executorKey != null){
                if(!isAppendWhere){
                    buf.append(" where (");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                buf.append("A.").append(scheduleTableSchema.executorKey).append(" is null or ")
                    .append(scheduleTableSchema.executorKey).append(" =?)");
            }
            if(states != null && states.length != 0){
                if(!isAppendWhere){
                    buf.append(" where (");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                for(int i = 0; i < states.length; i++){
                    buf.append("A.").append(scheduleTableSchema.state).append("=?");
                    if(i != states.length - 1){
                        buf.append(" or ");
                    }
                }
                buf.append(')');
            }
            if(from != null){
                if(!isAppendWhere){
                    buf.append(" where ");
                    isAppendWhere = true;
                }else{
                    buf.append(" and ");
                }
                concatQuery(buf, "A." + scheduleTableSchema.date, "A." + scheduleTableSchema.time);
                buf.append(">=?");
            }
            if(to != null){
                if(!isAppendWhere){
                    buf.append(" where ");
                    isAppendWhere = true;
                }else{
                    buf.append(" and ");
                }
                concatQuery(buf, "A." + scheduleTableSchema.date, "A." + scheduleTableSchema.time);
                buf.append("<=?");
            }
            if(isLock){
                buf.append(" for update");
            }
            st = con.prepareStatement(buf.toString());
            buf = null;
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            int index = 0;
            if(masterGroupId != null){
                st.setString(++index, masterGroupId);
            }
            if(groupId != null){
                st.setString(++index, groupId);
            }
            if(masterId != null){
                st.setString(++index, masterId);
            }
            if(executorTypes != null && executorTypes.length != 0){
                for(int i = 0; i < executorTypes.length; i++){
                    st.setString(++index, executorTypes[i]);
                }
            }
            if(executorKey != null){
                st.setString(++index, executorKey);
            }
            if(states != null && states.length != 0){
                for(int i = 0; i < states.length; i++){
                    st.setString(
                        ++index,
                        scheduleTableSchema.getStateString(
                            states[i]
                        )
                    );
                }
            }
            if(from != null){
                st.setString(++index, format.format(from));
            }
            if(to != null){
                st.setString(++index, format.format(to));
            }
            rs = st.executeQuery();
            final List result = new ArrayList();
            while(rs.next()){
                result.add(createSchedule(rs));
            }
            setDependsOnSchedules(con, result);
            setGroupIdsOnSchedules(con, result);
            setGroupDependsOnGroupOnSchedules(con, result);
            Collections.sort(result);
            return result;
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public List findExecutableSchedules(Date date, String[] executorTypes)
     throws ScheduleManageException{
        return findExecutableSchedules(date, executorTypes, null);
    }
    
    // ScheduleManager��JavaDoc
    public List findExecutableSchedules(Date date, String[] executorTypes, String executorKey)
     throws ScheduleManageException{
        final List result = findSchedules(
            null,
            date,
            new int[]{Schedule.STATE_INITIAL, Schedule.STATE_RETRY},
            null,
            null,
            null,
            executorTypes,
            executorKey,
            isLockForFindExecutable
        );
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st1 = null;
        PreparedStatement st1Group = null;
        PreparedStatement st1InGroup = null;
        PreparedStatement st1GroupOnGroup = null;
        PreparedStatement st2 = null;
        PreparedStatement st2Group = null;
        PreparedStatement st2InGroup = null;
        PreparedStatement st2GroupOnGroup = null;
        PreparedStatement st3 = null;
        PreparedStatement st3Group = null;
        PreparedStatement st3InGroup = null;
        Statement st3GroupOnGroup = null;
        ResultSet rs = null;
        try{
            StringBuffer buf = new StringBuffer();
            // 1�X�P�W���[�� - 1�X�P�W���[��
            // �������O�̎��ԂŁA�������ˑ�����X�P�W���[���̐���₢���킹��
            buf.append("select count(1) from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId)
                .append(',').append(scheduleDependsTableSchema.ignoreError).append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                .append(" and ");
            concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("<?")
                .append(" and (((B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'")
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (B.").append(scheduleDependsTableSchema.ignoreError).append("='1'")
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                .append(" or (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RUN).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_PAUSE).append("'))");
            st1 = con.prepareStatement(buf.toString());
            // �O���[�v�� 1�X�P�W���[�� - 1�X�P�W���[��
            // �O���[�v���ŁA�������O�̎��ԂŁA�������ˑ�����X�P�W���[���̐���₢���킹��
            buf.setLength(0);
            buf.append("select count(1) from ")
                .append("(select ")
                .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME")
                .append(" from ").append(scheduleTableSchema.table).append(" C,")
                .append("(select ")
                .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.id).append("=?) B")
                .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>?) D")
                .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                .append(" where E.MASTER_ID=F.DEPENDS_ID")
                .append(" and ");
            concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("<?")
                .append(" and (((F.IGNORE_ERROR<>'1'")
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (F.IGNORE_ERROR='1'")
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                .append(" or (E.STATE='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                .append(" or E.STATE='").append(scheduleTableSchema.stateString_RUN).append('\'')
                .append(" or E.STATE='").append(scheduleTableSchema.stateString_PAUSE).append("'))");
            st1InGroup = con.prepareStatement(buf.toString());
            // 1�X�P�W���[�� - �O���[�v
            // �������O�̎��ԂŁA�������ˑ�����O���[�v�X�P�W���[���̐���₢���킹��
            buf.setLength(0);
            buf.append("select count(1) from ")
                .append("(select distinct E.GROUP_ID from ")
                .append("(select A.GROUP_ID, A.MASTER_GROUP_ID from ")
                .append("(select ")
                .append("AA.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                .append("AA.").append(scheduleGroupTableSchema.masterGroupId).append(" as MASTER_GROUP_ID,")
                .append("AB.").append(scheduleTableSchema.state).append(" as STATE")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" AA,").append(scheduleTableSchema.table).append(" AB")
                .append(" where AA.").append(scheduleGroupTableSchema.id).append("=AB.").append(scheduleTableSchema.id).append(") A,")
                .append("(select ")
                .append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID,")
                .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null) B")
                .append(" where A.MASTER_GROUP_ID=B.MASTER_GROUP_ID")
                .append(" and ((B.IGNORE_ERROR<>'1'")
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (B.IGNORE_ERROR='1'")
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append('\'')
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append("'))")
                .append(" group by A.GROUP_ID, A.MASTER_GROUP_ID) E,")
                .append("(select ")
                .append("C.").append(scheduleGroupTableSchema.id).append(" as ID,")
                .append("C.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                .append("C.").append(scheduleGroupTableSchema.masterGroupId).append(" as MASTER_GROUP_ID")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" C,")
                .append("(select ")
                .append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null) D")
                .append(" where C.MASTER_GROUP_ID=D.MASTER_GROUP_ID")
                .append(" and C.ID in (select ")
                .append(scheduleTableSchema.id).append(" from ").append(scheduleTableSchema.table)
                .append(" where ");
            concatQuery(buf, scheduleTableSchema.initialDate, scheduleTableSchema.initialTime).append("<?)) F")
                .append(" where E.GROUP_ID=F.GROUP_ID")
                .append(" and E.MASTER_GROUP_ID=F.MASTER_GROUP_ID)");
            st1Group = con.prepareStatement(buf.toString());
            // �O���[�v - �O���[�v
            // ��������������O���[�v���O�̎��ԂŁA�������ˑ�����O���[�v�X�P�W���[���̐���₢���킹��
            buf.setLength(0);
            buf.append("select count(1) from ")
                .append("(select GROUP_ID, IGNORE_ERROR from ")
                .append("(select AB.GROUP_ID as GROUP_ID,")
                .append("AB.IGNORE_ERROR as IGNORE_ERROR")
                .append(" from ").append(scheduleTableSchema.table).append(" AA,")
                .append("(select ABA.").append(scheduleGroupTableSchema.id).append(" as ID,")
                .append("ABA.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                .append("ABB.MASTER_GROUP_ID as MASTER_GROUP_ID,")
                .append("ABB.IGNORE_ERROR as IGNORE_ERROR")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" ABA,")
                .append("(select ").append(scheduleGroupDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID,")
                .append(scheduleGroupDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                .append(" from ").append(scheduleGroupDependsTableSchema.table)
                .append(" where ").append(scheduleGroupDependsTableSchema.groupId)
                .append(" in (select ").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID")
                .append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.id).append("=?)) ABB")
                .append(" where ABA.MASTER_GROUP_ID=ABB.MASTER_GROUP_ID) AB,")
                .append("(select MAX(INITIAL_DATETIME) as INITIAL_DATETIME from ")
                .append("(select GROUP_ID, MIN(INITIAL_DATETIME) as INITIAL_DATETIME from ")
                .append("(select (");
            concatQuery(buf, "ACA." + scheduleTableSchema.initialDate, "ACA." + scheduleTableSchema.initialTime).append(") as INITIAL_DATETIME,")
                .append("ACB.GROUP_ID as GROUP_ID")
                .append(" from ").append(scheduleTableSchema.table).append(" ACA,")
                .append("(select ").append(scheduleGroupTableSchema.id).append(" as ID,")
                .append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID")
                .append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.groupId)
                .append(" in (select ").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID")
                .append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.id).append("=?)) ACB")
                .append(" where ACA.").append(scheduleTableSchema.id).append("=ACB.ID)")
                .append(" group by GROUP_ID)) AC")
                .append(" where (");
            concatQuery(buf, "AA." + scheduleTableSchema.initialDate, "AA." + scheduleTableSchema.initialTime).append(") < AC.INITIAL_DATETIME")
                .append(" and AA.").append(scheduleTableSchema.id).append("=AB.ID)")
                .append(" group by GROUP_ID, IGNORE_ERROR) A,")
                .append(scheduleTableSchema.table).append(" B,")
                .append(scheduleGroupTableSchema.table).append(" C")
                .append(" where A.GROUP_ID=C.").append(scheduleGroupTableSchema.groupId)
                .append(" and A.GROUP_ID<>")
                .append("(select ").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID")
                .append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.id).append("=?)")
                .append(" and B.").append(scheduleTableSchema.id).append("=C.").append(scheduleGroupTableSchema.id)
                .append(" and ((A.IGNORE_ERROR<>'1'")
                .append(" and B.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and B.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (A.IGNORE_ERROR='1'")
                .append(" and B.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and B.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and B.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and B.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))");
            st1GroupOnGroup = con.prepareStatement(buf.toString());
            // 1�X�P�W���[�� - 1�X�P�W���[��
            // �����Ɠ������ԂŁA�������ˑ�����X�P�W���[����ID�Ǝ�����ID���AID�̏����Ŗ₢���킹��
            buf.setLength(0);
            buf.append("select ID from ((")
                .append("select A.").append(scheduleTableSchema.id).append(" as ID from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId).append(',')
                .append(scheduleDependsTableSchema.ignoreError).append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                .append(" and A.").append(scheduleTableSchema.id).append("<>?")
                .append(" and ");
            concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("=?")
                .append(" and (((B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'")
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (B.").append(scheduleDependsTableSchema.ignoreError).append("='1'")
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                .append(" or (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RUN).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_PAUSE).append("')))")
                .append(" union (select ").append(scheduleTableSchema.id).append(" as ID from ").append(scheduleTableSchema.table)
                .append(" where ").append(scheduleTableSchema.id).append("=?)) C order by ID");
            st2 = con.prepareStatement(buf.toString());
            // �O���[�v���ŁA�����Ɠ������ԂŁA�������ˑ�����X�P�W���[����ID�Ǝ�����ID���AID�̏����Ŗ₢���킹��
            buf.setLength(0);
            buf.append("select ID from (")
                .append("(select E.ID as ID from ")
                .append("(select ")
                .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME")
                .append(" from ").append(scheduleTableSchema.table).append(" C,")
                .append("(select ")
                .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.id).append("=?) B")
                .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>?) D")
                .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                .append(" where E.MASTER_ID=F.DEPENDS_ID")
                .append(" and ");
            concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("=?")
                .append(" and (((F.IGNORE_ERROR<>'1'")
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (F.IGNORE_ERROR='1'")
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                .append(" or (E.STATE='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                .append(" or E.STATE='").append(scheduleTableSchema.stateString_RUN).append('\'')
                .append(" or E.STATE='").append(scheduleTableSchema.stateString_PAUSE).append("')))")
                .append(" union (select ").append(scheduleTableSchema.id).append(" as ID from ").append(scheduleTableSchema.table)
                .append(" where ").append(scheduleTableSchema.id).append("=?))").append(" order by ID");
            st2InGroup = con.prepareStatement(buf.toString());
            // 1�X�P�W���[�� - �O���[�v
            // �����Ɠ������ԂŁA�������ˑ�����O���[�v�X�P�W���[������ID�Ǝ�����ID���AID�̏����Ŗ₢���킹��
            buf.setLength(0);
            buf.append("select ID from ")
                .append("(select F.ID from ")
                .append("(select A.GROUP_ID, A.MASTER_GROUP_ID from ")
                .append("(select ")
                .append("AA.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                .append("AA.").append(scheduleGroupTableSchema.masterGroupId).append(" as MASTER_GROUP_ID,")
                .append("AB.").append(scheduleTableSchema.state).append(" as STATE")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" AA,").append(scheduleTableSchema.table).append(" AB")
                .append(" where AA.").append(scheduleGroupTableSchema.id).append("=AB.").append(scheduleTableSchema.id).append(") A,")
                .append("(select ")
                .append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID,")
                .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null) B")
                .append(" where A.MASTER_GROUP_ID=B.MASTER_GROUP_ID")
                .append(" and ((B.IGNORE_ERROR<>'1'")
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (B.IGNORE_ERROR='1'")
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append('\'')
                .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append("'))")
                .append(" group by A.GROUP_ID, A.MASTER_GROUP_ID) E,")
                .append("(select ")
                .append("C.").append(scheduleGroupTableSchema.id).append(" as ID,")
                .append("C.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                .append("C.").append(scheduleGroupTableSchema.masterGroupId).append(" as MASTER_GROUP_ID")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" C,")
                .append("(select ")
                .append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null) D")
                .append(" where C.MASTER_GROUP_ID=D.MASTER_GROUP_ID")
                .append(" and C.ID in (select ")
                .append(scheduleTableSchema.id).append(" from ").append(scheduleTableSchema.table)
                .append(" where ");
            concatQuery(buf, scheduleTableSchema.initialDate, scheduleTableSchema.initialTime).append("=?)) F")
                .append(" where E.GROUP_ID=F.GROUP_ID")
                .append(" and E.MASTER_GROUP_ID=F.MASTER_GROUP_ID) union (select ")
                .append(scheduleTableSchema.id).append(" as ID from ").append(scheduleTableSchema.table)
                .append(" where ").append(scheduleTableSchema.id).append("=?) order by ID");
            st2Group = con.prepareStatement(buf.toString());
            // �O���[�v - �O���[�v
            // ��������������O���[�v�Ɠ������ԂŁA�������ˑ�����X�P�W���[���O���[�v��ID�Ǝ�����ID���A��������������O���[�v��ID���AID�̏����Ŗ₢���킹��
            buf.setLength(0);
            buf.append("select OWN_GROUP_ID,OWN_GROUP_MASTER_ID,DEPENDS_GROUP_ID ")
                .append("from (")
                .append("select A.OWN_GROUP_ID as OWN_GROUP_ID,")
                .append("A.OWN_GROUP_MASTER_ID as OWN_GROUP_MASTER_ID,")
                .append("A.DEPENDS_GROUP_ID as DEPENDS_GROUP_ID ")
                .append("from (")
                .append("select AA.ID as ID, ")
                .append("AB.OWN_GROUP_ID as OWN_GROUP_ID,")
                .append("AB.DEPENDS_GROUP_ID as DEPENDS_GROUP_ID, ")
                .append("AC.GROUP_MASTER_ID as OWN_GROUP_MASTER_ID,")
                .append("AB.GROUP_MASTER_ID as GROUP_MASTER_ID,")
                .append("AB.IGNORE_ERROR as IGNORE_ERROR ")
                .append("from ").append(scheduleGroupTableSchema.table).append(" AA,")
                .append("(")
                .append("select OWN_GROUP_ID as OWN_GROUP_ID,")
                .append("DEPENDS_GROUP_ID as DEPENDS_GROUP_ID, ")
                .append("GROUP_MASTER_ID as GROUP_MASTER_ID,")
                .append("IGNORE_ERROR as IGNORE_ERROR, ")
                .append("MIN(INITIAL_DATETIME) as INITIAL_DATETIME ")
                .append("from ")
                .append("(")
                .append("select ABA.").append(scheduleGroupTableSchema.id).append(" as ID, ")
                .append("ABB.GROUP_ID as OWN_GROUP_ID, ")
                .append("ABA.").append(scheduleGroupTableSchema.groupId).append(" as DEPENDS_GROUP_ID, ")
                .append("ABB.GROUP_MASTER_ID as GROUP_MASTER_ID, ")
                .append("ABB.IGNORE_ERROR as IGNORE_ERROR,")
                .append("(");
            concatQuery(buf, "ABC.INITIAL_DATE", "ABC.INITIAL_TIME").append(") as INITIAL_DATETIME ")
                .append("from ").append(scheduleGroupTableSchema.table).append(" ABA,")
                .append("(")
                .append("select ").append(scheduleGroupDependsTableSchema.groupId).append(" as GROUP_ID, ")
                .append(scheduleGroupDependsTableSchema.dependsGroupId).append(" as GROUP_MASTER_ID,")
                .append(scheduleGroupDependsTableSchema.ignoreError).append(" as IGNORE_ERROR ")
                .append("from ").append(scheduleGroupDependsTableSchema.table).append(' ')
                .append("where ").append(scheduleGroupDependsTableSchema.groupId).append(" in ")
                .append("(")
                .append("select ").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID ")
                .append("from ").append(scheduleGroupTableSchema.table).append(' ')
                .append("where ").append(scheduleGroupTableSchema.id).append("=?")
                .append(")")
                .append(") ABB,")
                .append(scheduleTableSchema.table).append(" ABC ")
                .append("where ABA.").append(scheduleGroupTableSchema.masterGroupId).append("=ABB.GROUP_MASTER_ID ")
                .append("and ABA.ID=ABC.ID and ABB.GROUP_ID<>ABA.").append(scheduleGroupTableSchema.groupId)
                .append(")")
                .append("group by OWN_GROUP_ID, DEPENDS_GROUP_ID, GROUP_MASTER_ID, IGNORE_ERROR")
                .append(") AB,")
                .append("(")
                .append("select GROUP_ID, GROUP_MASTER_ID, MIN(INITIAL_DATETIME) as INITIAL_DATETIME ")
                .append("from (")
                .append("select (");
            concatQuery(buf, "ACA.INITIAL_DATE", "ACA.INITIAL_TIME").append(") as INITIAL_DATETIME, ")
                .append("ACB.GROUP_ID as GROUP_ID, ACB.GROUP_MASTER_ID as GROUP_MASTER_ID ")
                .append("from ").append(scheduleTableSchema.table).append(" ACA, ")
                .append("(")
                .append("select ").append(scheduleGroupTableSchema.id).append(" as ID,")
                .append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                .append(scheduleGroupTableSchema.masterGroupId).append(" as GROUP_MASTER_ID ")
                .append("from ").append(scheduleGroupTableSchema.table).append(' ')
                .append("where ").append(scheduleGroupTableSchema.groupId).append(" in (")
                .append("select ").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID ")
                .append("from ").append(scheduleGroupTableSchema.table).append(' ')
                .append("where ").append(scheduleGroupTableSchema.id).append("=?")
                .append(")")
                .append(") ACB ")
                .append("where ACA.ID=ACB.ID ")
                .append(")")
                .append("group by GROUP_ID, GROUP_MASTER_ID")
                .append(") AC ")
                .append("where AB.INITIAL_DATETIME=AC.INITIAL_DATETIME ")
                .append("and AA.").append(scheduleGroupTableSchema.groupId).append("=AB.DEPENDS_GROUP_ID")
                .append(") A,")
                .append(scheduleTableSchema.table).append(" B ")
                .append("where A.ID=B.ID ")
                .append("and ")
                .append("(")
                .append("(")
                .append("A.IGNORE_ERROR<>'1' ")
                .append("and B.STATE<>'").append(scheduleTableSchema.stateString_END).append("' ")
                .append("and B.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("') ")
                .append("or (A.IGNORE_ERROR='1' ")
                .append("and B.STATE<>'").append(scheduleTableSchema.stateString_END).append("' ")
                .append("and B.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("' ")
                .append("and B.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append("' ")
                .append("and B.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append("') ")
                .append(") ")
                .append("group by A.OWN_GROUP_ID,A.OWN_GROUP_MASTER_ID,A.DEPENDS_GROUP_ID ")
                .append("union ")
                .append("select ").append(scheduleGroupTableSchema.groupId).append(" as OWN_GROUP_ID,")
                .append(scheduleGroupTableSchema.masterGroupId).append(" as GROUP_MASTER_ID,")
                .append(scheduleGroupTableSchema.groupId).append(" as DEPENDS_GROUP_ID ")
                .append("from ").append(scheduleGroupTableSchema.table).append(' ')
                .append("where ").append(scheduleGroupTableSchema.id).append("=? ")
                .append(")")
                .append("order by OWN_GROUP_ID, DEPENDS_GROUP_ID");
            st2GroupOnGroup = con.prepareStatement(buf.toString());
            // 1�X�P�W���[�� - 1�X�P�W���[��
            // �����Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[���̐���₢���킹��
            buf.setLength(0);
            buf.append("select count(1) from ")
                .append("(select A.").append(scheduleTableSchema.id).append(" from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId).append(',')
                .append(scheduleDependsTableSchema.ignoreError).append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                .append(" and A.").append(scheduleTableSchema.id).append("<>?")
                .append(" and ");
            concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("=?")
                .append(" and (((B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'")
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (B.").append(scheduleDependsTableSchema.ignoreError).append("='1'")
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                .append(" or (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RUN).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_PAUSE).append("'))) C,")
                .append("(select ").append(scheduleDependsTableSchema.id).append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.dependsId).append("=?) D")
                .append(" where C.").append(scheduleTableSchema.id).append("=D.").append(scheduleDependsTableSchema.id);
            st3 = con.prepareStatement(buf.toString());
            // �O���[�v���ŁA�����Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[���̐���₢���킹��
            buf.setLength(0);
            buf.append("select count(1) from ")
                .append("(select E.ID as ID from ")
                .append("(select ")
                .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME")
                .append(" from ").append(scheduleTableSchema.table).append(" C,")
                .append("(select ")
                .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                .append(" where ").append(scheduleGroupTableSchema.id).append("=?) B")
                .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>?) D")
                .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                .append(" where E.MASTER_ID=F.DEPENDS_ID")
                .append(" and E.ID<>?")
                .append(" and ((((F.IGNORE_ERROR<>'1'")
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                .append(" or (F.IGNORE_ERROR='1'")
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                .append(" and ");
            concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("=?)")
                .append(" or (E.STATE='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                .append(" or E.STATE='").append(scheduleTableSchema.stateString_RUN).append('\'')
                .append(" or E.STATE='").append(scheduleTableSchema.stateString_PAUSE).append("'))) G,")
                .append("(select ").append(scheduleDependsTableSchema.id).append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.dependsId).append("=?) H")
                .append(" where G.ID=H.").append(scheduleDependsTableSchema.id);
            st3InGroup = con.prepareStatement(buf.toString());
            // 1�X�P�W���[�� - �O���[�v
            // �����Ɠ������ԂŁA���݂Ɉˑ�����O���[�v�X�P�W���[���̐���₢���킹��
            buf.setLength(0);
            buf.append("select count(1) from ")
                .append("(select A.ID from ")
                .append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID")
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("=?")
                .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null)B,")
                .append(scheduleGroupTableSchema.table).append(" C")
                .append(" where B.MASTER_GROUP_ID=C.").append(scheduleGroupTableSchema.masterGroupId)
                .append(" and A.ID=C.").append(scheduleGroupTableSchema.id)
                .append(" and A.ID<>? ")
                .append(" and ");
            concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("=?")
                .append(" and A.STATE='").append(scheduleTableSchema.stateString_INITIAL).append("')");
            st3Group = con.prepareStatement(buf.toString());
            
            
            st3GroupOnGroup = con.createStatement();
            
            buf = null;
            
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            final Iterator itr = result.iterator();
            String initialTime = null;
            Map isTopGroupMasterIDMap = null;
            Map dependsGroupMap = null;
            while(itr.hasNext()){
                int paramIndex = 1;
                final Schedule schedule = (Schedule)itr.next();
                if(schedule.getDepends() == null
                    && schedule.getDependsInGroupMap().size() == 0
                    && schedule.getDependsOnGroup() == null
                    && schedule.getGroupDependsOnGroupMap().size() == 0){
                    continue;
                }
                initialTime = format.format(
                    schedule.getInitialTime() == null
                        ? schedule.getTime() : schedule.getInitialTime()
                );
                if(schedule.getDepends() != null){
                    st1.setString(paramIndex++, schedule.getId());
                    st1.setString(paramIndex++, initialTime);
                    rs = st1.executeQuery();
                    rs.next();
                    if(rs.getInt(1) != 0){
                        itr.remove();
                        rs.close();
                        continue;
                    }
                    rs.close();
                    
                    paramIndex = 1;
                    st2.setString(paramIndex++, schedule.getId());
                    st2.setString(paramIndex++, schedule.getId());
                    st2.setString(paramIndex++, initialTime);
                    st2.setString(paramIndex++, schedule.getId());
                    rs = st2.executeQuery();
                    rs.next();
                    final boolean isTop = schedule.getId().equals(rs.getString(1));
                    int num = 0;
                    while(rs.next()){
                        num++;
                    }
                    rs.close();
                    if(num != 0){
                        if(!isTop){
                            itr.remove();
                            rs.close();
                            continue;
                        }
                        paramIndex = 1;
                        st3.setString(paramIndex++, schedule.getId());
                        st3.setString(paramIndex++, schedule.getId());
                        st3.setString(paramIndex++, initialTime);
                        st3.setString(paramIndex++, schedule.getMasterId());
                        rs = st3.executeQuery();
                        rs.next();
                        if(rs.getInt(1) < num){
                            itr.remove();
                            rs.close();
                            continue;
                        }
                        rs.close();
                    }
                }
                if(schedule.getDependsInGroupMap().size() != 0){
                    paramIndex = 1;
                    st1InGroup.setString(paramIndex++, schedule.getId());
                    st1InGroup.setString(paramIndex++, initialTime);
                    st1InGroup.setString(paramIndex++, schedule.getId());
                    st1InGroup.setString(paramIndex++, initialTime);
                    rs = st1InGroup.executeQuery();
                    rs.next();
                    if(rs.getInt(1) != 0){
                        itr.remove();
                        rs.close();
                        continue;
                    }
                    rs.close();
                    
                    paramIndex = 1;
                    st2InGroup.setString(paramIndex++, schedule.getId());
                    st2InGroup.setString(paramIndex++, schedule.getId());
                    st2InGroup.setString(paramIndex++, schedule.getId());
                    st2InGroup.setString(paramIndex++, initialTime);
                    st2InGroup.setString(paramIndex++, schedule.getId());
                    rs = st2InGroup.executeQuery();
                    rs.next();
                    final boolean isTop = schedule.getId().equals(rs.getString(1));
                    int num = 0;
                    while(rs.next()){
                        num++;
                    }
                    rs.close();
                    if(num != 0){
                        if(!isTop){
                            itr.remove();
                            rs.close();
                            continue;
                        }
                        paramIndex = 1;
                        st3InGroup.setString(paramIndex++, schedule.getId());
                        st3InGroup.setString(paramIndex++, schedule.getId());
                        st3InGroup.setString(paramIndex++, schedule.getId());
                        st3InGroup.setString(paramIndex++, schedule.getId());
                        st3InGroup.setString(paramIndex++, initialTime);
                        st3InGroup.setString(paramIndex++, schedule.getMasterId());
                        rs = st3InGroup.executeQuery();
                        rs.next();
                        if(rs.getInt(1) < num){
                            itr.remove();
                            rs.close();
                            continue;
                        }
                        rs.close();
                    }
                }
                if(schedule.getDependsOnGroup() != null){
                    paramIndex = 1;
                    st1Group.setString(paramIndex++, schedule.getId());
                    st1Group.setString(paramIndex++, schedule.getId());
                    st1Group.setString(paramIndex++, initialTime);
                    rs = st1Group.executeQuery();
                    rs.next();
                    if(rs.getInt(1) != 0){
                        itr.remove();
                        rs.close();
                        continue;
                    }
                    rs.close();
                    
                    paramIndex = 1;
                    st2Group.setString(paramIndex++, schedule.getId());
                    st2Group.setString(paramIndex++, schedule.getId());
                    st2Group.setString(paramIndex++, initialTime);
                    st2Group.setString(paramIndex++, schedule.getId());
                    rs = st2Group.executeQuery();
                    rs.next();
                    final boolean isTop = schedule.getId().equals(rs.getString(1));
                    int num = 0;
                    while(rs.next()){
                        num++;
                    }
                    rs.close();
                    if(num != 0){
                        if(!isTop){
                            itr.remove();
                            rs.close();
                            continue;
                        }
                        paramIndex = 1;
                        st3Group.setString(paramIndex++, schedule.getId());
                        st3Group.setString(paramIndex++, schedule.getId());
                        st3Group.setString(paramIndex++, initialTime);
                        rs = st3Group.executeQuery();
                        rs.next();
                        if(rs.getInt(1) < num){
                            itr.remove();
                            rs.close();
                            continue;
                        }
                        rs.close();
                    }
                }
                if(schedule.getGroupDependsOnGroupMap().size() != 0){
                    paramIndex = 1;
                    st1GroupOnGroup.setString(paramIndex++, schedule.getId());
                    st1GroupOnGroup.setString(paramIndex++, schedule.getId());
                    st1GroupOnGroup.setString(paramIndex++, schedule.getId());
                    rs = st1GroupOnGroup.executeQuery();
                    rs.next();
                    if(rs.getInt(1) != 0){
                        itr.remove();
                        rs.close();
                        continue;
                    }
                    rs.close();
                    
                    paramIndex = 1;
                    st2GroupOnGroup.setString(paramIndex++, schedule.getId());
                    st2GroupOnGroup.setString(paramIndex++, schedule.getId());
                    st2GroupOnGroup.setString(paramIndex++, schedule.getId());
                    rs = st2GroupOnGroup.executeQuery();
                    String ownGroupId = null;
                    if(isTopGroupMasterIDMap == null){
                        isTopGroupMasterIDMap = new HashMap();
                    }else{
                        isTopGroupMasterIDMap.clear();
                    }
                    if(dependsGroupMap == null){
                        dependsGroupMap = new HashMap();
                    }else{
                        dependsGroupMap.clear();
                    }
                    boolean isIncludeFirst = false;
                    while(rs.next()){
                        String ownGroupIdTmp = rs.getString(1);
                        String dependsGroupId = rs.getString(3);
                        String ownGroupMasterId = rs.getString(2);
                        if(ownGroupId == null || !ownGroupId.equals(ownGroupIdTmp)){
                            ownGroupId = ownGroupIdTmp;
                            if(ownGroupId.equals(dependsGroupId)){
                                isTopGroupMasterIDMap.put(ownGroupMasterId, Boolean.TRUE);
                                isIncludeFirst = true;
                            }else{
                                isTopGroupMasterIDMap.put(ownGroupMasterId, Boolean.FALSE);
                            }
                        }
                        
                        if(!ownGroupId.equals(dependsGroupId)){
                            Map dependsGroupIdMap = (Map)dependsGroupMap.get(ownGroupMasterId);
                            if(dependsGroupIdMap == null){
                                dependsGroupIdMap = new LinkedHashMap();
                            }
                            dependsGroupIdMap.put(dependsGroupId, Boolean.FALSE);
                            dependsGroupMap.put(ownGroupMasterId, dependsGroupIdMap);
                        }
                    }
                    rs.close();
                    rs = null;
                    if(!isIncludeFirst){
                        itr.remove();
                        continue;
                    }
                    
                    buf = new StringBuffer();
                    isTopGroupMasterIDMap.keySet().retainAll(dependsGroupMap.keySet());
                    Iterator itrGroupMasterIdSet = isTopGroupMasterIDMap.keySet().iterator();
                    boolean isRemove = false;
                    while(itrGroupMasterIdSet.hasNext()){
                        if(isRemove){
                            continue;
                        }
                        
                        String groupMasterId = (String)itrGroupMasterIdSet.next();
                        Map dependsGroupIdMap =((Map)dependsGroupMap.get(groupMasterId));
                        Iterator itrDependsGroupID = dependsGroupIdMap.keySet().iterator();
                        // ��������������O���[�v�Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[���O���[�v�̐���₢���킹��
                        buf.setLength(0);
                        buf.append("select ").append(scheduleGroupDependsTableSchema.groupId)
                            .append(" from ").append(scheduleGroupDependsTableSchema.table)
                            .append(" where ").append(scheduleGroupDependsTableSchema.dependsGroupId).append("='").append(groupMasterId).append('\'')
                            .append(" and ").append(scheduleGroupDependsTableSchema.groupId).append(" in (");
                        while(itrDependsGroupID.hasNext()){
                            buf.append('\'').append((String)itrDependsGroupID.next()).append('\'');
                            if(itrDependsGroupID.hasNext()){
                                buf.append(",");
                            }
                        }
                        buf.append(")");
                        rs = st3GroupOnGroup.executeQuery(buf.toString());
                        int bothDependsCount = 0;
                        while(rs.next()){
                            String dependsGroupID = rs.getString(1);
                            ((Map)dependsGroupMap.get(groupMasterId)).put(dependsGroupID, Boolean.TRUE);
                            bothDependsCount++;
                        }
                        rs.close();
                        rs = null;
                        
                        if(((Boolean)isTopGroupMasterIDMap.get(groupMasterId)).booleanValue()){
                            if(bothDependsCount != dependsGroupIdMap.size()){
                                isRemove = true;
                                itr.remove();
                                continue;
                            }
                        }else{
                            Map dependsMap = (Map)dependsGroupMap.get(groupMasterId);
                            Iterator itrDependsID = dependsMap.keySet().iterator();
                            while(itrDependsID.hasNext()){
                                if(!((Boolean)dependsMap.get(itrDependsID.next())).booleanValue()){
                                    continue;
                                }else{
                                    // TODO : 
                                }
                            }
                        }
                    }
                    buf = null;
                }
            }
            Collections.sort(result);
            
            return result;
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st1 != null){
                try{
                    st1.close();
                }catch(SQLException e){
                }
            }
            if(st2 != null){
                try{
                    st2.close();
                }catch(SQLException e){
                }
            }
            if(st3 != null){
                try{
                    st3.close();
                }catch(SQLException e){
                }
            }
            if(st1InGroup != null){
                try{
                    st1InGroup.close();
                }catch(SQLException e){
                }
            }
            if(st2InGroup != null){
                try{
                    st2InGroup.close();
                }catch(SQLException e){
                }
            }
            if(st2Group != null){
                try{
                    st2Group.close();
                }catch(SQLException e){
                }
            }
            if(st3InGroup != null){
                try{
                    st3InGroup.close();
                }catch(SQLException e){
                }
            }
            if(st1Group != null){
                try{
                    st1Group.close();
                }catch(SQLException e){
                }
            }
            if(st2Group != null){
                try{
                    st2Group.close();
                }catch(SQLException e){
                }
            }
            if(st3Group != null){
                try{
                    st3Group.close();
                }catch(SQLException e){
                }
            }
            if(st1GroupOnGroup != null){
                try{
                    st1GroupOnGroup.close();
                }catch(SQLException e){
                }
            }
            if(st2GroupOnGroup != null){
                try{
                    st2GroupOnGroup.close();
                }catch(SQLException e){
                }
            }
            if(st3GroupOnGroup != null){
                try{
                    st3GroupOnGroup.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public List findDependsSchedules(String id) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        Statement st = null;
        ResultSet rs = null;
        try{
            StringBuffer buf = new StringBuffer();
            buf.append("select * from ").append(scheduleTableSchema.table)
                .append(" where ").append(scheduleTableSchema.id).append("='").append(id).append('\'');
            st = con.createStatement();
            rs = st.executeQuery(buf.toString());
            final List result = new ArrayList();
            if(!rs.next()){
                return result;
            }
            Schedule schedule = createSchedule(rs);
            rs.close();
            rs = null;
            if(schedule.getDepends() == null
                && schedule.getDependsInGroupMap().size() == 0
                && schedule.getDependsOnGroup() == null
                && schedule.getGroupDependsOnGroupMap().size() == 0){
                return result;
            }
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            final String initialTime = format.format(
                schedule.getInitialTime() == null
                    ? schedule.getTime() : schedule.getInitialTime()
            );
            if(schedule.getDepends() != null){
                // �������O�̎��ԂŁA�������ˑ�����X�P�W���[����₢���킹��
                buf.setLength(0);
                buf.append("select ")
                    .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                    .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                    .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                    .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                    .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                    .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                    .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                    .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                    .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                    .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                    .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                    .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table).append(" A, ")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(',').append(scheduleDependsTableSchema.ignoreError)
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                    .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                    .append(" and A.").append(scheduleTableSchema.id).append("<>'").append(id).append('\'')
                    .append(" and ((((B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'")
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (B." + scheduleDependsTableSchema.ignoreError).append("='1'")
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                    .append(" and ");
                concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("<'").append(initialTime).append("')")
                    .append(" or (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RUN).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_PAUSE).append("'))");
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    result.add(createSchedule(rs));
                }
                rs.close();
                rs = null;
                
                // �����Ɠ������ԂŁA�������ˑ�����X�P�W���[���Ǝ����̃X�P�W���[�����AID�̏����Ŗ₢���킹��
                buf.setLength(0);
                buf.append("select * from (")
                    .append("(select ")
                    .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                    .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                    .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                    .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                    .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                    .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                    .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                    .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                    .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                    .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                    .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                    .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(',').append(scheduleDependsTableSchema.ignoreError)
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                    .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                    .append(" and ((((B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'")
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (B." + scheduleDependsTableSchema.ignoreError).append("='1'")
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                    .append(" and ");
                concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("='").append(initialTime).append("')")
                    .append(" or (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RUN).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_PAUSE).append("')))")
                    .append(" union (select ")
                    .append(scheduleTableSchema.id).append(',')
                    .append(scheduleTableSchema.masterId).append(',')
                    .append(scheduleTableSchema.date).append(',')
                    .append(scheduleTableSchema.time).append(',')
                    .append(scheduleTableSchema.taskName).append(',')
                    .append(scheduleTableSchema.input).append(',')
                    .append(scheduleTableSchema.output).append(',')
                    .append(scheduleTableSchema.initialDate).append(',')
                    .append(scheduleTableSchema.initialTime).append(',')
                    .append(scheduleTableSchema.retryInterval).append(',')
                    .append(scheduleTableSchema.retryEndTime).append(',')
                    .append(scheduleTableSchema.maxDelayTime).append(',')
                    .append(scheduleTableSchema.state).append(',')
                    .append(scheduleTableSchema.controlState).append(',')
                    .append(scheduleTableSchema.checkState).append(',')
                    .append(scheduleTableSchema.executorKey).append(',')
                    .append(scheduleTableSchema.executorType).append(',')
                    .append(scheduleTableSchema.executeStartTime).append(',')
                    .append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table)
                    .append(" where ").append(scheduleTableSchema.id).append("='").append(id).append("'))")
                    .append(" order by ").append(scheduleTableSchema.id);
                rs = st.executeQuery(buf.toString());
                final Map doubtSchedules = new HashMap();
                boolean findMe = false;
                while(rs.next()){
                    if(!findMe){
                        findMe = id.equals(rs.getString(1));
                        if(!findMe){
                            result.add(createSchedule(rs));
                        }
                    }else{
                        doubtSchedules.put(rs.getString(1), createSchedule(rs));
                    }
                }
                rs.close();
                rs = null;
                
                // �����Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[����ID��₢���킹��
                buf.setLength(0);
                buf.append("select C.").append(scheduleTableSchema.id).append(" from ")
                    .append("(select A.").append(scheduleTableSchema.id).append(" from ").append(scheduleTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(',').append(scheduleDependsTableSchema.ignoreError)
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                    .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                    .append(" and A.").append(scheduleTableSchema.id).append("<>'").append(id).append('\'')
                    .append(" and ((((B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'")
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (B." + scheduleDependsTableSchema.ignoreError).append("='1'")
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                    .append(" and ");
                concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("='").append(initialTime).append("')")
                    .append(" or (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RUN).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_PAUSE).append("'))) C,")
                    .append(" (select ").append(scheduleTableSchema.id).append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append("') D")
                    .append(" where C.").append(scheduleTableSchema.id).append("=D.").append(scheduleDependsTableSchema.id);
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    doubtSchedules.remove(rs.getString(1));
                }
                rs.close();
                rs = null;
                if(doubtSchedules.size() != 0){
                    result.addAll(doubtSchedules.values());
                }
            }
            
            if(schedule.getDependsInGroupMap().size() != 0){
                // �O���[�v���ŁA�������O�̎��ԂŁA�������ˑ�����X�P�W���[����₢���킹��
                buf.setLength(0);
                buf.append("select ")
                    .append("E.ID as ").append(scheduleTableSchema.id).append(',')
                    .append("E.MASTER_ID as ").append(scheduleTableSchema.masterId).append(',')
                    .append("E.S_DATE as ").append(scheduleTableSchema.date).append(',')
                    .append("E.S_TIME as ").append(scheduleTableSchema.time).append(',')
                    .append("E.TASK_NAME as ").append(scheduleTableSchema.taskName).append(',')
                    .append("E.INPUT as ").append(scheduleTableSchema.input).append(',')
                    .append("E.OUTPUT as ").append(scheduleTableSchema.output).append(',')
                    .append("E.INITIAL_DATE as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("E.INITIAL_TIME as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("E.RETRY_INTERVAL as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("E.RETRY_END_TIME as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("E.MAX_DELAY_TIME as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("E.STATE as ").append(scheduleTableSchema.state).append(',')
                    .append("E.CONTROL_STATE as ").append(scheduleTableSchema.controlState).append(',')
                    .append("E.CHECK_STATE as ").append(scheduleTableSchema.checkState).append(',')
                    .append("E.EXECUTOR_KEY as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("E.EXECUTOR_TYPE as ").append(scheduleTableSchema.executorType).append(',')
                    .append("E.EXEC_S_TIME as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("E.EXEC_E_TIME as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ")
                    .append("(select ")
                    .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("C.").append(scheduleTableSchema.date).append(" as S_DATE,")
                    .append("C.").append(scheduleTableSchema.time).append(" as S_TIME,")
                    .append("C.").append(scheduleTableSchema.taskName).append(" as TASK_NAME,")
                    .append("C.").append(scheduleTableSchema.input).append(" as INPUT,")
                    .append("C.").append(scheduleTableSchema.output).append(" as OUTPUT,")
                    .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME,")
                    .append("C.").append(scheduleTableSchema.retryInterval).append(" as RETRY_INTERVAL,")
                    .append("C.").append(scheduleTableSchema.retryEndTime).append(" as RETRY_END_TIME,")
                    .append("C.").append(scheduleTableSchema.maxDelayTime).append(" as MAX_DELAY_TIME,")
                    .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("C.").append(scheduleTableSchema.controlState).append(" as CONTROL_STATE,")
                    .append("C.").append(scheduleTableSchema.checkState).append(" as CHECK_STATE,")
                    .append("C.").append(scheduleTableSchema.executorKey).append(" as EXECUTOR_KEY,")
                    .append("C.").append(scheduleTableSchema.executorType).append(" as EXECUTOR_TYPE,")
                    .append("C.").append(scheduleTableSchema.executeStartTime).append(" as EXEC_S_TIME,")
                    .append("C.").append(scheduleTableSchema.executeEndTime).append(" as EXEC_E_TIME")
                    .append(" from ").append(scheduleTableSchema.table).append(" C,")
                    .append("(select ")
                    .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') B")
                    .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                    .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>'").append(id).append("') D")
                    .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                    .append(" where E.MASTER_ID=F.DEPENDS_ID")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and ((((F.IGNORE_ERROR<>'1'")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (F.IGNORE_ERROR='1'")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                    .append(" and ");
                concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("<'").append(initialTime).append("')")
                    .append(" or (E.STATE='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_RUN).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_PAUSE).append("'))");
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    result.add(createSchedule(rs));
                }
                rs.close();
                rs = null;
                
                // �O���[�v���ŁA�����Ɠ������ԂŁA�������ˑ�����X�P�W���[���Ǝ����̃X�P�W���[�����AID�̏����Ŗ₢���킹��
                buf.setLength(0);
                buf.append("select * from (")
                    .append("(select ")
                    .append("E.ID as ").append(scheduleTableSchema.id).append(',')
                    .append("E.MASTER_ID as ").append(scheduleTableSchema.masterId).append(',')
                    .append("E.S_DATE as ").append(scheduleTableSchema.date).append(',')
                    .append("E.S_TIME as ").append(scheduleTableSchema.time).append(',')
                    .append("E.TASK_NAME as ").append(scheduleTableSchema.taskName).append(',')
                    .append("E.INPUT as ").append(scheduleTableSchema.input).append(',')
                    .append("E.OUTPUT as ").append(scheduleTableSchema.output).append(',')
                    .append("E.INITIAL_DATE as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("E.INITIAL_TIME as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("E.RETRY_INTERVAL as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("E.RETRY_END_TIME as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("E.MAX_DELAY_TIME as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("E.STATE as ").append(scheduleTableSchema.state).append(',')
                    .append("E.CONTROL_STATE as ").append(scheduleTableSchema.controlState).append(',')
                    .append("E.CHECK_STATE as ").append(scheduleTableSchema.checkState).append(',')
                    .append("E.EXECUTOR_KEY as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("E.EXECUTOR_TYPE as ").append(scheduleTableSchema.executorType).append(',')
                    .append("E.EXEC_S_TIME as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("E.EXEC_E_TIME as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ")
                    .append("(select ")
                    .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("C.").append(scheduleTableSchema.date).append(" as S_DATE,")
                    .append("C.").append(scheduleTableSchema.time).append(" as S_TIME,")
                    .append("C.").append(scheduleTableSchema.taskName).append(" as TASK_NAME,")
                    .append("C.").append(scheduleTableSchema.input).append(" as INPUT,")
                    .append("C.").append(scheduleTableSchema.output).append(" as OUTPUT,")
                    .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME,")
                    .append("C.").append(scheduleTableSchema.retryInterval).append(" as RETRY_INTERVAL,")
                    .append("C.").append(scheduleTableSchema.retryEndTime).append(" as RETRY_END_TIME,")
                    .append("C.").append(scheduleTableSchema.maxDelayTime).append(" as MAX_DELAY_TIME,")
                    .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("C.").append(scheduleTableSchema.controlState).append(" as CONTROL_STATE,")
                    .append("C.").append(scheduleTableSchema.checkState).append(" as CHECK_STATE,")
                    .append("C.").append(scheduleTableSchema.executorKey).append(" as EXECUTOR_KEY,")
                    .append("C.").append(scheduleTableSchema.executorType).append(" as EXECUTOR_TYPE,")
                    .append("C.").append(scheduleTableSchema.executeStartTime).append(" as EXEC_S_TIME,")
                    .append("C.").append(scheduleTableSchema.executeEndTime).append(" as EXEC_E_TIME")
                    .append(" from ").append(scheduleTableSchema.table).append(" C,")
                    .append("(select ")
                    .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') B")
                    .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                    .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>'").append(id).append("') D")
                    .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                    .append(" where E.MASTER_ID=F.DEPENDS_ID")
                    .append(" and ((((F.IGNORE_ERROR<>'1'")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (F.IGNORE_ERROR='1'")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                    .append(" and ");
                concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("='").append(initialTime).append("')")
                    .append(" or (E.STATE='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_RUN).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_PAUSE).append("')))")
                    .append(" union (select ")
                    .append(scheduleTableSchema.id).append(',')
                    .append(scheduleTableSchema.masterId).append(',')
                    .append(scheduleTableSchema.date).append(',')
                    .append(scheduleTableSchema.time).append(',')
                    .append(scheduleTableSchema.taskName).append(',')
                    .append(scheduleTableSchema.input).append(',')
                    .append(scheduleTableSchema.output).append(',')
                    .append(scheduleTableSchema.initialDate).append(',')
                    .append(scheduleTableSchema.initialTime).append(',')
                    .append(scheduleTableSchema.retryInterval).append(',')
                    .append(scheduleTableSchema.retryEndTime).append(',')
                    .append(scheduleTableSchema.maxDelayTime).append(',')
                    .append(scheduleTableSchema.state).append(',')
                    .append(scheduleTableSchema.controlState).append(',')
                    .append(scheduleTableSchema.checkState).append(',')
                    .append(scheduleTableSchema.executorKey).append(',')
                    .append(scheduleTableSchema.executorType).append(',')
                    .append(scheduleTableSchema.executeStartTime).append(',')
                    .append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table)
                    .append(" where ").append(scheduleTableSchema.id).append("='").append(id).append("'))")
                    .append(" order by ").append(scheduleTableSchema.id);
                rs = st.executeQuery(buf.toString());
                final Map doubtSchedules = new HashMap();
                boolean findMe = false;
                while(rs.next()){
                    if(!findMe){
                        findMe = id.equals(rs.getString(1));
                        if(!findMe){
                            result.add(createSchedule(rs));
                        }
                    }else{
                        doubtSchedules.put(rs.getString(1), createSchedule(rs));
                    }
                }
                rs.close();
                rs = null;
                
                // �O���[�v���ŁA�����Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[����ID��₢���킹��
                buf.setLength(0);
                buf.append("select G.ID from ")
                    .append("(select E.ID as ID from ")
                    .append("(select ")
                    .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME")
                    .append(" from ").append(scheduleTableSchema.table).append(" C,")
                    .append("(select ")
                    .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') B")
                    .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                    .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>'").append(id).append("') D")
                    .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                    .append(" where E.MASTER_ID=F.DEPENDS_ID")
                    .append(" and E.ID<>'").append(id).append('\'')
                    .append(" and ((((F.IGNORE_ERROR<>'1'")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (F.IGNORE_ERROR='1'")
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_ABORT).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and E.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("'))")
                    .append(" and ");
                concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("='").append(initialTime).append("')")
                    .append(" or (E.STATE='").append(scheduleTableSchema.stateString_ENTRY).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_RUN).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_PAUSE).append("'))) G,")
                    .append("(select ").append(scheduleDependsTableSchema.id).append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append("') H")
                    .append(" where G.ID=H.").append(scheduleDependsTableSchema.id);
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    doubtSchedules.remove(rs.getString(1));
                }
                rs.close();
                rs = null;
                if(doubtSchedules.size() != 0){
                    result.addAll(doubtSchedules.values());
                }
            }
            
            if(schedule.getDependsOnGroup() != null){
                // �������O�̎��ԂŁA�������ˑ�����O���[�v�X�P�W���[����₢���킹��
                buf.setLength(0);
                buf.append("select ")
                    .append("E.ID as ").append(scheduleTableSchema.id).append(',')
                    .append("E.MASTER_ID as ").append(scheduleTableSchema.masterId).append(',')
                    .append("E.S_DATE as ").append(scheduleTableSchema.date).append(',')
                    .append("E.S_TIME as ").append(scheduleTableSchema.time).append(',')
                    .append("E.TASK_NAME as ").append(scheduleTableSchema.taskName).append(',')
                    .append("E.INPUT as ").append(scheduleTableSchema.input).append(',')
                    .append("E.OUTPUT as ").append(scheduleTableSchema.output).append(',')
                    .append("E.INITIAL_DATE as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("E.INITIAL_TIME as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("E.RETRY_INTERVAL as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("E.RETRY_END_TIME as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("E.MAX_DELAY_TIME as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("E.STATE as ").append(scheduleTableSchema.state).append(',')
                    .append("E.CONTROL_STATE as ").append(scheduleTableSchema.controlState).append(',')
                    .append("E.CHECK_STATE as ").append(scheduleTableSchema.checkState).append(',')
                    .append("E.EXECUTOR_KEY as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("E.EXECUTOR_TYPE as ").append(scheduleTableSchema.executorType).append(',')
                    .append("E.EXEC_S_TIME as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("E.EXEC_E_TIME as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ")
                    .append("(select A.GROUP_ID,A.MASTER_GROUP_ID,A.ID,A.MASTER_ID,A.S_DATE,A.S_TIME,")
                    .append("A.TASK_NAME,A.INPUT,A.OUTPUT,A.INITIAL_DATE,A.INITIAL_TIME,")
                    .append("A.RETRY_INTERVAL,A.RETRY_END_TIME,A.MAX_DELAY_TIME,A.STATE,")
                    .append("A.CONTROL_STATE,A.CHECK_STATE,A.EXECUTOR_KEY,")
                    .append("A.EXECUTOR_TYPE,A.EXEC_S_TIME,A.EXEC_E_TIME")
                    .append(" from ")
                    .append("(select ")
                    .append("AA.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                    .append("AA.").append(scheduleGroupTableSchema.masterGroupId).append(" as MASTER_GROUP_ID,")
                    .append("AB.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("AB.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("AB.").append(scheduleTableSchema.date).append(" as S_DATE,")
                    .append("AB.").append(scheduleTableSchema.time).append(" as S_TIME,")
                    .append("AB.").append(scheduleTableSchema.taskName).append(" as TASK_NAME,")
                    .append("AB.").append(scheduleTableSchema.input).append(" as INPUT,")
                    .append("AB.").append(scheduleTableSchema.output).append(" as OUTPUT,")
                    .append("AB.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("AB.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME,")
                    .append("AB.").append(scheduleTableSchema.retryInterval).append(" as RETRY_INTERVAL,")
                    .append("AB.").append(scheduleTableSchema.retryEndTime).append(" as RETRY_END_TIME,")
                    .append("AB.").append(scheduleTableSchema.maxDelayTime).append(" as MAX_DELAY_TIME,")
                    .append("AB.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("AB.").append(scheduleTableSchema.controlState).append(" as CONTROL_STATE,")
                    .append("AB.").append(scheduleTableSchema.checkState).append(" as CHECK_STATE,")
                    .append("AB.").append(scheduleTableSchema.executorKey).append(" as EXECUTOR_KEY,")
                    .append("AB.").append(scheduleTableSchema.executorType).append(" as EXECUTOR_TYPE,")
                    .append("AB.").append(scheduleTableSchema.executeStartTime).append(" as EXEC_S_TIME,")
                    .append("AB.").append(scheduleTableSchema.executeEndTime).append(" as EXEC_E_TIME")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" AA,").append(scheduleTableSchema.table).append(" AB")
                    .append(" where AA.").append(scheduleGroupTableSchema.id).append("=AB.").append(scheduleTableSchema.id).append(") A,")
                    .append("(select ")
                    .append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null) B")
                    .append(" where A.MASTER_GROUP_ID=B.MASTER_GROUP_ID")
                    .append(" and ((B.IGNORE_ERROR<>'1'")
                    .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append("')")
                    .append(" or (B.IGNORE_ERROR='1'")
                    .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_END).append('\'')
                    .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_DISABLE).append('\'')
                    .append(" and A.STATE<>'").append(scheduleTableSchema.stateString_FAILED).append("'))")
                    .append(" group by A.GROUP_ID, A.MASTER_GROUP_ID) E,")
                    .append("(select ")
                    .append("C.").append(scheduleGroupTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleGroupTableSchema.groupId).append(" as GROUP_ID,")
                    .append("C.").append(scheduleGroupTableSchema.masterGroupId).append(" as MASTER_GROUP_ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" C,")
                    .append("(select ")
                    .append(scheduleDependsTableSchema.dependsGroupId).append(" as MASTER_GROUP_ID")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsGroupId).append(" is not null) D")
                    .append(" where C.MASTER_GROUP_ID=D.MASTER_GROUP_ID")
                    .append(" and C.ID in (select ")
                    .append(scheduleTableSchema.id).append(" from ").append(scheduleTableSchema.table)
                    .append(" where ");
                concatQuery(buf, scheduleTableSchema.initialDate, scheduleTableSchema.initialTime).append("<='").append(initialTime).append("')) F")
                    .append(" where E.GROUP_ID=F.GROUP_ID")
                    .append(" and E.MASTER_GROUP_ID=F.MASTER_GROUP_ID");
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    result.add(createSchedule(rs));
                }
                rs.close();
                rs = null;
            }
            if(schedule.getGroupDependsOnGroupMap().size() != 0){
                //TODO : 
            }
            st.close();
            st = null;
            setDependsOnSchedules(con, result);
            setGroupIdsOnSchedules(con, result);
            setGroupDependsOnGroupOnSchedules(con, result);
            Collections.sort(result);
            return result;
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public List findDependedSchedules(String id) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        Statement st = null;
        ResultSet rs = null;
        try{
            StringBuffer buf = new StringBuffer();
            buf.append("select *  from ").append(scheduleTableSchema.table)
                .append(" where ").append(scheduleTableSchema.id).append("='").append(id).append('\'')
                .append(" and ").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_END).append('\'')
                .append(" and ").append(scheduleTableSchema.state).append("<>'").append(scheduleTableSchema.stateString_DISABLE).append('\'');
            st = con.createStatement();
            rs = st.executeQuery(buf.toString());
            final List result = new ArrayList();
            if(!rs.next()){
                return result;
            }
            Schedule schedule = createSchedule(rs);
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            final String initialTime = format.format(
                schedule.getInitialTime() == null
                    ? schedule.getTime() : schedule.getInitialTime()
            );
            rs.close();
            rs = null;
            
            // ��������̎��ԂŁA�����Ɉˑ�����X�P�W���[����₢���킹��
            buf.setLength(0);
            buf.append("select ")
                .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                .append(" from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.id).append(',').append(scheduleDependsTableSchema.ignoreError)
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append('\'')
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null")
                .append(" and ").append(scheduleDependsTableSchema.id).append("<>'").append(id).append("\') B")
                .append(" where A.").append(scheduleTableSchema.id).append("=B.").append(scheduleDependsTableSchema.id)
                .append(" and (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RETRY).append("')");
            if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                buf.append(" and B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'");
            }
            buf.append(" and ");
            concatQuery(buf, scheduleTableSchema.initialDate, scheduleTableSchema.initialTime).append(">'").append(initialTime).append('\'');
            rs = st.executeQuery(buf.toString());
            while(rs.next()){
                result.add(createSchedule(rs));
            }
            rs.close();
            rs = null;
            
            // �����Ɠ������ԂŁA�����Ɉˑ�����X�P�W���[���Ǝ����̃X�P�W���[�����AID�̏����Ŗ₢���킹��
            buf.setLength(0);
            buf.append("select * from (")
                .append("(select ")
                .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                .append(" from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.id).append(',').append(scheduleDependsTableSchema.ignoreError)
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append('\'')
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null")
                .append(" and ").append(scheduleDependsTableSchema.id).append("<>'").append(id).append("\') B")
                .append(" where A.").append(scheduleTableSchema.id).append("=B.").append(scheduleDependsTableSchema.id)
                .append(" and (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RETRY).append("')");
            if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                buf.append(" and B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'");
            }
            buf.append(" and ");
            concatQuery(buf, scheduleTableSchema.initialDate, scheduleTableSchema.initialTime).append("='").append(initialTime).append("')")
                .append(" union (select ")
                .append(scheduleTableSchema.id).append(',')
                .append(scheduleTableSchema.masterId).append(',')
                .append(scheduleTableSchema.date).append(',')
                .append(scheduleTableSchema.time).append(',')
                .append(scheduleTableSchema.taskName).append(',')
                .append(scheduleTableSchema.input).append(',')
                .append(scheduleTableSchema.output).append(',')
                .append(scheduleTableSchema.initialDate).append(',')
                .append(scheduleTableSchema.initialTime).append(',')
                .append(scheduleTableSchema.retryInterval).append(',')
                .append(scheduleTableSchema.retryEndTime).append(',')
                .append(scheduleTableSchema.maxDelayTime).append(',')
                .append(scheduleTableSchema.state).append(',')
                .append(scheduleTableSchema.controlState).append(',')
                .append(scheduleTableSchema.checkState).append(',')
                .append(scheduleTableSchema.executorKey).append(',')
                .append(scheduleTableSchema.executorType).append(',')
                .append(scheduleTableSchema.executeStartTime).append(',')
                .append(scheduleTableSchema.executeEndTime)
                .append(" from ").append(scheduleTableSchema.table)
                .append(" where ").append(scheduleTableSchema.id).append("='").append(id).append("'))")
                .append(" order by ").append(scheduleTableSchema.id);
            rs = st.executeQuery(buf.toString());
            final Map doubtSchedules = new HashMap();
            boolean findMe = false;
            while(rs.next()){
                if(!findMe){
                    findMe = id.equals(rs.getString(1));
                    if(!findMe){
                        doubtSchedules.put(rs.getString(1), createSchedule(rs));
                    }
                }else{
                    result.add(createSchedule(rs));
                }
            }
            rs.close();
            rs = null;
            
            // �����Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[����ID��₢���킹��
            buf.setLength(0);
            buf.append("select C.").append(scheduleTableSchema.id).append(" from ")
                .append("(select A.").append(scheduleTableSchema.id).append(" from ").append(scheduleTableSchema.table).append(" A,")
                .append("(select ").append(scheduleDependsTableSchema.dependsId).append(',').append(scheduleDependsTableSchema.ignoreError)
                .append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) B")
                .append(" where A.").append(scheduleTableSchema.masterId).append("=B.").append(scheduleDependsTableSchema.dependsId)
                .append(" and A.").append(scheduleTableSchema.id).append("<>'").append(id).append('\'')
                .append(" and (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RETRY).append("')");
            if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                buf.append(" and B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'");
            }
            buf.append(" and ");
            concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append("='").append(initialTime).append("') C,")
                .append(" (select ").append(scheduleTableSchema.id).append(" from ").append(scheduleDependsTableSchema.table)
                .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append('\'')
                .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is null) D")
                .append(" where C.").append(scheduleTableSchema.id).append("=D.").append(scheduleDependsTableSchema.id);
            rs = st.executeQuery(buf.toString());
            while(rs.next()){
                doubtSchedules.remove(rs.getString(1));
            }
            rs.close();
            rs = null;
            if(doubtSchedules.size() != 0){
                result.addAll(doubtSchedules.values());
            }
            
            if(schedule.getGroupIdMap().size() != 0){
                // �O���[�v���ŁA��������̎��ԂŁA�����Ɉˑ�����X�P�W���[����₢���킹��
                buf.setLength(0);
                buf.append("select ")
                    .append("E.ID as ").append(scheduleTableSchema.id).append(',')
                    .append("E.MASTER_ID as ").append(scheduleTableSchema.masterId).append(',')
                    .append("E.S_DATE as ").append(scheduleTableSchema.date).append(',')
                    .append("E.S_TIME as ").append(scheduleTableSchema.time).append(',')
                    .append("E.TASK_NAME as ").append(scheduleTableSchema.taskName).append(',')
                    .append("E.INPUT as ").append(scheduleTableSchema.input).append(',')
                    .append("E.OUTPUT as ").append(scheduleTableSchema.output).append(',')
                    .append("E.INITIAL_DATE as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("E.INITIAL_TIME as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("E.RETRY_INTERVAL as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("E.RETRY_END_TIME as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("E.MAX_DELAY_TIME as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("E.STATE as ").append(scheduleTableSchema.state).append(',')
                    .append("E.CONTROL_STATE as ").append(scheduleTableSchema.controlState).append(',')
                    .append("E.CHECK_STATE as ").append(scheduleTableSchema.checkState).append(',')
                    .append("E.EXECUTOR_KEY as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("E.EXECUTOR_TYPE as ").append(scheduleTableSchema.executorType).append(',')
                    .append("E.EXEC_S_TIME as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("E.EXEC_E_TIME as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ")
                    .append("(select ")
                    .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("C.").append(scheduleTableSchema.date).append(" as S_DATE,")
                    .append("C.").append(scheduleTableSchema.time).append(" as S_TIME,")
                    .append("C.").append(scheduleTableSchema.taskName).append(" as TASK_NAME,")
                    .append("C.").append(scheduleTableSchema.input).append(" as INPUT,")
                    .append("C.").append(scheduleTableSchema.output).append(" as OUTPUT,")
                    .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME,")
                    .append("C.").append(scheduleTableSchema.retryInterval).append(" as RETRY_INTERVAL,")
                    .append("C.").append(scheduleTableSchema.retryEndTime).append(" as RETRY_END_TIME,")
                    .append("C.").append(scheduleTableSchema.maxDelayTime).append(" as MAX_DELAY_TIME,")
                    .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("C.").append(scheduleTableSchema.controlState).append(" as CONTROL_STATE,")
                    .append("C.").append(scheduleTableSchema.checkState).append(" as CHECK_STATE,")
                    .append("C.").append(scheduleTableSchema.executorKey).append(" as EXECUTOR_KEY,")
                    .append("C.").append(scheduleTableSchema.executorType).append(" as EXECUTOR_TYPE,")
                    .append("C.").append(scheduleTableSchema.executeStartTime).append(" as EXEC_S_TIME,")
                    .append("C.").append(scheduleTableSchema.executeEndTime).append(" as EXEC_E_TIME")
                    .append(" from ").append(scheduleTableSchema.table).append(" C,")
                    .append("(select ")
                    .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') B")
                    .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                    .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>'").append(id).append("') D")
                    .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                    .append("(select ").append(scheduleDependsTableSchema.id).append(" as ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.id).append("<>'").append(id).append("\') F")
                    .append(" where E.ID=F.ID")
                    .append(" and (E.STATE='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_RETRY).append("')");
                if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                    buf.append(" and F.IGNORE_ERROR<>'1'");
                }
                buf.append(" and ");
                concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append(">'").append(initialTime).append('\'');
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    result.add(createSchedule(rs));
                }
                rs.close();
                rs = null;
                
                // �O���[�v���ŁA�����Ɠ������ԂŁA�����Ɉˑ�����X�P�W���[���Ǝ����̃X�P�W���[�����AID�̏����Ŗ₢���킹��
                buf.setLength(0);
                buf.append("select * from (")
                    .append("(select ")
                    .append("E.ID as ").append(scheduleTableSchema.id).append(',')
                    .append("E.MASTER_ID as ").append(scheduleTableSchema.masterId).append(',')
                    .append("E.S_DATE as ").append(scheduleTableSchema.date).append(',')
                    .append("E.S_TIME as ").append(scheduleTableSchema.time).append(',')
                    .append("E.TASK_NAME as ").append(scheduleTableSchema.taskName).append(',')
                    .append("E.INPUT as ").append(scheduleTableSchema.input).append(',')
                    .append("E.OUTPUT as ").append(scheduleTableSchema.output).append(',')
                    .append("E.INITIAL_DATE as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("E.INITIAL_TIME as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("E.RETRY_INTERVAL as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("E.RETRY_END_TIME as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("E.MAX_DELAY_TIME as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("E.STATE as ").append(scheduleTableSchema.state).append(',')
                    .append("E.CONTROL_STATE as ").append(scheduleTableSchema.controlState).append(',')
                    .append("E.CHECK_STATE as ").append(scheduleTableSchema.checkState).append(',')
                    .append("E.EXECUTOR_KEY as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("E.EXECUTOR_TYPE as ").append(scheduleTableSchema.executorType).append(',')
                    .append("E.EXEC_S_TIME as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("E.EXEC_E_TIME as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ")
                    .append("(select ")
                    .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("C.").append(scheduleTableSchema.date).append(" as S_DATE,")
                    .append("C.").append(scheduleTableSchema.time).append(" as S_TIME,")
                    .append("C.").append(scheduleTableSchema.taskName).append(" as TASK_NAME,")
                    .append("C.").append(scheduleTableSchema.input).append(" as INPUT,")
                    .append("C.").append(scheduleTableSchema.output).append(" as OUTPUT,")
                    .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME,")
                    .append("C.").append(scheduleTableSchema.retryInterval).append(" as RETRY_INTERVAL,")
                    .append("C.").append(scheduleTableSchema.retryEndTime).append(" as RETRY_END_TIME,")
                    .append("C.").append(scheduleTableSchema.maxDelayTime).append(" as MAX_DELAY_TIME,")
                    .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("C.").append(scheduleTableSchema.controlState).append(" as CONTROL_STATE,")
                    .append("C.").append(scheduleTableSchema.checkState).append(" as CHECK_STATE,")
                    .append("C.").append(scheduleTableSchema.executorKey).append(" as EXECUTOR_KEY,")
                    .append("C.").append(scheduleTableSchema.executorType).append(" as EXECUTOR_TYPE,")
                    .append("C.").append(scheduleTableSchema.executeStartTime).append(" as EXEC_S_TIME,")
                    .append("C.").append(scheduleTableSchema.executeEndTime).append(" as EXEC_E_TIME")
                    .append(" from ").append(scheduleTableSchema.table).append(" C,")
                    .append("(select ")
                    .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') B")
                    .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                    .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>'").append(id).append("') D")
                    .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                    .append("(select ").append(scheduleDependsTableSchema.id).append(" as ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.id).append("<>'").append(id).append("\') F")
                    .append(" where E.ID=F.ID")
                    .append(" and (E.STATE='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_RETRY).append("')");
                if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                    buf.append(" and F.IGNORE_ERROR<>'1'");
                }
                buf.append(" and ");
                concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("='").append(initialTime).append("')")
                    .append(" union (select ")
                    .append(scheduleTableSchema.id).append(',')
                    .append(scheduleTableSchema.masterId).append(',')
                    .append(scheduleTableSchema.date).append(',')
                    .append(scheduleTableSchema.time).append(',')
                    .append(scheduleTableSchema.taskName).append(',')
                    .append(scheduleTableSchema.input).append(',')
                    .append(scheduleTableSchema.output).append(',')
                    .append(scheduleTableSchema.initialDate).append(',')
                    .append(scheduleTableSchema.initialTime).append(',')
                    .append(scheduleTableSchema.retryInterval).append(',')
                    .append(scheduleTableSchema.retryEndTime).append(',')
                    .append(scheduleTableSchema.maxDelayTime).append(',')
                    .append(scheduleTableSchema.state).append(',')
                    .append(scheduleTableSchema.controlState).append(',')
                    .append(scheduleTableSchema.checkState).append(',')
                    .append(scheduleTableSchema.executorKey).append(',')
                    .append(scheduleTableSchema.executorType).append(',')
                    .append(scheduleTableSchema.executeStartTime).append(',')
                    .append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table)
                    .append(" where ").append(scheduleTableSchema.id).append("='").append(id).append("'))")
                    .append(" order by ").append(scheduleTableSchema.id);
                rs = st.executeQuery(buf.toString());
                doubtSchedules.clear();
                findMe = false;
                while(rs.next()){
                    if(!findMe){
                        findMe = id.equals(rs.getString(1));
                        if(!findMe){
                            doubtSchedules.put(rs.getString(1), createSchedule(rs));
                        }
                    }else{
                        result.add(createSchedule(rs));
                    }
                }
                rs.close();
                rs = null;
                
                // �O���[�v���ŁA�����Ɠ������ԂŁA���݂Ɉˑ�����X�P�W���[����ID��₢���킹��
                buf.setLength(0);
                buf.append("select G.ID from ")
                    .append("(select E.ID from ")
                    .append("(select ")
                    .append("C.").append(scheduleTableSchema.id).append(" as ID,")
                    .append("C.").append(scheduleTableSchema.masterId).append(" as MASTER_ID,")
                    .append("C.").append(scheduleTableSchema.date).append(" as S_DATE,")
                    .append("C.").append(scheduleTableSchema.time).append(" as S_TIME,")
                    .append("C.").append(scheduleTableSchema.taskName).append(" as TASK_NAME,")
                    .append("C.").append(scheduleTableSchema.input).append(" as INPUT,")
                    .append("C.").append(scheduleTableSchema.output).append(" as OUTPUT,")
                    .append("C.").append(scheduleTableSchema.initialDate).append(" as INITIAL_DATE,")
                    .append("C.").append(scheduleTableSchema.initialTime).append(" as INITIAL_TIME,")
                    .append("C.").append(scheduleTableSchema.retryInterval).append(" as RETRY_INTERVAL,")
                    .append("C.").append(scheduleTableSchema.retryEndTime).append(" as RETRY_END_TIME,")
                    .append("C.").append(scheduleTableSchema.maxDelayTime).append(" as MAX_DELAY_TIME,")
                    .append("C.").append(scheduleTableSchema.state).append(" as STATE,")
                    .append("C.").append(scheduleTableSchema.controlState).append(" as CONTROL_STATE,")
                    .append("C.").append(scheduleTableSchema.checkState).append(" as CHECK_STATE,")
                    .append("C.").append(scheduleTableSchema.executorKey).append(" as EXECUTOR_KEY,")
                    .append("C.").append(scheduleTableSchema.executorType).append(" as EXECUTOR_TYPE,")
                    .append("C.").append(scheduleTableSchema.executeStartTime).append(" as EXEC_S_TIME,")
                    .append("C.").append(scheduleTableSchema.executeEndTime).append(" as EXEC_E_TIME")
                    .append(" from ").append(scheduleTableSchema.table).append(" C,")
                    .append("(select ")
                    .append("A.").append(scheduleGroupTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleGroupTableSchema.table).append(" A,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') B")
                    .append(" where ").append("A.").append(scheduleGroupTableSchema.groupId).append("=B.").append(scheduleGroupTableSchema.groupId)
                    .append(" and ").append("A.").append(scheduleGroupTableSchema.id).append("<>'").append(id).append("') D")
                    .append(" where ").append("C.").append(scheduleTableSchema.id).append("=D.ID) E,")
                    .append("(select ").append(scheduleDependsTableSchema.dependsId).append(" as DEPENDS_ID,")
                    .append(scheduleDependsTableSchema.ignoreError).append(" as IGNORE_ERROR")
                    .append(" from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.id).append("='").append(id).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) F")
                    .append(" where E.MASTER_ID=F.DEPENDS_ID")
                    .append(" and E.ID<>'").append(id).append('\'')
                    .append(" and (E.STATE='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                    .append(" or E.STATE='").append(scheduleTableSchema.stateString_RETRY).append("')");
                if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                    buf.append(" and F.IGNORE_ERROR<>'1'");
                }
                buf.append(" and ");
                concatQuery(buf, "E.INITIAL_DATE", "E.INITIAL_TIME").append("='").append(initialTime).append("') G,")
                    .append(" (select ").append(scheduleTableSchema.id).append(" as ID from ").append(scheduleDependsTableSchema.table)
                    .append(" where ").append(scheduleDependsTableSchema.dependsId).append("='").append(schedule.getMasterId()).append('\'')
                    .append(" and ").append(scheduleDependsTableSchema.dependsId).append(" is not null")
                    .append(" and ").append(scheduleDependsTableSchema.groupId).append(" is not null) H")
                    .append(" where G.ID=H.ID");
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    doubtSchedules.remove(rs.getString(1));
                }
                rs.close();
                rs = null;
                if(doubtSchedules.size() != 0){
                    result.addAll(doubtSchedules.values());
                }
                
                // ��������������O���[�v����̎��ԂŁA��������������O���[�v�Ɉˑ�����X�P�W���[����₢���킹��
                buf.setLength(0);
                buf.append("select ")
                    .append("A.").append(scheduleTableSchema.id).append(" as ").append(scheduleTableSchema.id).append(',')
                    .append("A.").append(scheduleTableSchema.masterId).append(" as ").append(scheduleTableSchema.masterId).append(',')
                    .append("A.").append(scheduleTableSchema.date).append(" as ").append(scheduleTableSchema.date).append(',')
                    .append("A.").append(scheduleTableSchema.time).append(" as ").append(scheduleTableSchema.time).append(',')
                    .append("A.").append(scheduleTableSchema.taskName).append(" as ").append(scheduleTableSchema.taskName).append(',')
                    .append("A.").append(scheduleTableSchema.input).append(" as ").append(scheduleTableSchema.input).append(',')
                    .append("A.").append(scheduleTableSchema.output).append(" as ").append(scheduleTableSchema.output).append(',')
                    .append("A.").append(scheduleTableSchema.initialDate).append(" as ").append(scheduleTableSchema.initialDate).append(',')
                    .append("A.").append(scheduleTableSchema.initialTime).append(" as ").append(scheduleTableSchema.initialTime).append(',')
                    .append("A.").append(scheduleTableSchema.retryInterval).append(" as ").append(scheduleTableSchema.retryInterval).append(',')
                    .append("A.").append(scheduleTableSchema.retryEndTime).append(" as ").append(scheduleTableSchema.retryEndTime).append(',')
                    .append("A.").append(scheduleTableSchema.maxDelayTime).append(" as ").append(scheduleTableSchema.maxDelayTime).append(',')
                    .append("A.").append(scheduleTableSchema.state).append(" as ").append(scheduleTableSchema.state).append(',')
                    .append("A.").append(scheduleTableSchema.controlState).append(" as ").append(scheduleTableSchema.controlState).append(',')
                    .append("A.").append(scheduleTableSchema.checkState).append(" as ").append(scheduleTableSchema.checkState).append(',')
                    .append("A.").append(scheduleTableSchema.executorKey).append(" as ").append(scheduleTableSchema.executorKey).append(',')
                    .append("A.").append(scheduleTableSchema.executorType).append(" as ").append(scheduleTableSchema.executorType).append(',')
                    .append("A.").append(scheduleTableSchema.executeStartTime).append(" as ").append(scheduleTableSchema.executeStartTime).append(',')
                    .append("A.").append(scheduleTableSchema.executeEndTime).append(" as ").append(scheduleTableSchema.executeEndTime)
                    .append(" from ").append(scheduleTableSchema.table).append(" A,")
                    .append("(select B.").append(scheduleDependsTableSchema.id).append(" as ID")
                    .append(" from ").append(scheduleDependsTableSchema.table).append(" B,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId)
                    .append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') C")
                    .append(" where B.").append(scheduleDependsTableSchema.dependsGroupId).append("=C.").append(scheduleGroupTableSchema.groupId);
                if(schedule.getState() == Schedule.STATE_FAILED || schedule.getState() == Schedule.STATE_ABORT){
                    buf.append(" and B.").append(scheduleDependsTableSchema.ignoreError).append("<>'1'");
                }
                buf.append(") D,")
                    .append("(select MIN(INITIAL_DATETIME) as INITIAL_DATETIME")
                    .append(" from (select (");
                concatQuery(buf, scheduleTableSchema.initialDate, scheduleTableSchema.initialTime).append(") as INITIAL_DATETIME")
                    .append(" from ").append(scheduleTableSchema.table)
                    .append(" where ").append(scheduleTableSchema.id).append(" in (")
                    .append("select E.").append(scheduleGroupTableSchema.id).append(" from ").append(scheduleGroupTableSchema.table).append(" E,")
                    .append("(select ").append(scheduleGroupTableSchema.groupId).append(" from ").append(scheduleGroupTableSchema.table)
                    .append(" where ").append(scheduleGroupTableSchema.id).append("='").append(id).append("') F")
                    .append(" where ").append("E.").append(scheduleGroupTableSchema.groupId).append("=F.").append(scheduleGroupTableSchema.groupId).append("))) G")
                    .append(" where A.").append(scheduleTableSchema.id).append("<>'").append(id).append('\'')
                    .append(" and A.").append(scheduleTableSchema.id).append("=D.ID")
                    .append(" and (A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_INITIAL).append('\'')
                    .append(" or A.").append(scheduleTableSchema.state).append("='").append(scheduleTableSchema.stateString_RETRY).append("')")
                    .append(" and ");
                concatQuery(buf, "A." + scheduleTableSchema.initialDate, "A." + scheduleTableSchema.initialTime).append(">=G.INITIAL_DATETIME");
                rs = st.executeQuery(buf.toString());
                while(rs.next()){
                    result.add(createSchedule(rs));
                }
                rs.close();
                rs = null;
            }
            
            st.close();
            st = null;
            
            setDependsOnSchedules(con, result);
            setGroupIdsOnSchedules(con, result);
            setGroupDependsOnGroupOnSchedules(con, result);
            Collections.sort(result);
            return result;
        }catch(ParseException e){
            throw new ScheduleManageException(e);
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }catch(ClassNotFoundException e){
            throw new ScheduleManageException(e);
        }catch(ConvertException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public void addSchedule(Schedule schedule) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        Statement st = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;
        PreparedStatement ps5 = null;
        try{
            st = con.createStatement();
            ps1 = con.prepareStatement(
                "insert into " + scheduleTableSchema.table
                    + " ("
                    + scheduleTableSchema.id + ','
                    + scheduleTableSchema.masterId + ','
                    + scheduleTableSchema.date + ','
                    + scheduleTableSchema.time + ','
                    + scheduleTableSchema.taskName + ','
                    + scheduleTableSchema.input + ','
                    + scheduleTableSchema.output + ','
                    + scheduleTableSchema.initialDate + ','
                    + scheduleTableSchema.initialTime + ','
                    + scheduleTableSchema.retryInterval + ','
                    + scheduleTableSchema.retryEndTime + ','
                    + scheduleTableSchema.maxDelayTime + ','
                    + scheduleTableSchema.state + ','
                    + scheduleTableSchema.controlState + ','
                    + scheduleTableSchema.checkState + ','
                    + scheduleTableSchema.executorKey + ','
                    + scheduleTableSchema.executorType + ','
                    + scheduleTableSchema.executeStartTime + ','
                    + scheduleTableSchema.executeEndTime + ','
                    + scheduleTableSchema.rowVersion + ','
                    + scheduleTableSchema.updateUserId + ','
                    + scheduleTableSchema.updateTime
                    + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'0','" + updateUserId + "',?)"
            );
            ps2 = con.prepareStatement(
                "insert into " + scheduleDependsTableSchema.table
                    + " ("
                    + scheduleDependsTableSchema.id + ','
                    + scheduleDependsTableSchema.dependsId + ','
                    + scheduleDependsTableSchema.dependsGroupId + ','
                    + scheduleDependsTableSchema.groupId + ','
                    + scheduleDependsTableSchema.ignoreError + ','
                    + scheduleDependsTableSchema.rowVersion + ','
                    + scheduleDependsTableSchema.updateUserId + ','
                    + scheduleDependsTableSchema.updateTime
                    + ") values(?,?,?,?,?,'0','" + updateUserId + "',?)"
            );
            ps3 = con.prepareStatement(
                "select " + scheduleGroupDependsMasterTableSchema.dependsGroupId
                    + " from " + scheduleGroupDependsMasterTableSchema.table
                    + " where " + scheduleGroupDependsMasterTableSchema.groupId + "=?"
            );
            ps4 = con.prepareStatement(
                "insert into " + scheduleGroupDependsTableSchema.table
                    + " ("
                    + scheduleGroupDependsTableSchema.groupId + ','
                    + scheduleGroupDependsTableSchema.dependsGroupId + ','
                    + scheduleGroupDependsTableSchema.ignoreError + ','
                    + scheduleGroupDependsTableSchema.rowVersion + ','
                    + scheduleGroupDependsTableSchema.updateUserId + ','
                    + scheduleGroupDependsTableSchema.updateTime
                    + ") values(?,?,?,'0','" + updateUserId + "',?)"
            );
            ps5 = con.prepareStatement(
                "insert into " + scheduleGroupTableSchema.table
                    + " ("
                    + scheduleGroupTableSchema.id + ','
                    + scheduleGroupTableSchema.groupId + ','
                    + scheduleGroupTableSchema.masterGroupId + ','
                    + scheduleGroupTableSchema.rowVersion + ','
                    + scheduleGroupTableSchema.updateUserId + ','
                    + scheduleGroupTableSchema.updateTime
                    + ") values(?,?,?,'0','" + updateUserId + "',?)"
            );
            addSchedule(
                st,
                ps1,
                ps2,
                ps3,
                ps4,
                ps5,
                schedule,
                null
            );
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(ps1 != null){
                try{
                    ps1.close();
                }catch(SQLException e){
                }
            }
            if(ps2 != null){
                try{
                    ps2.close();
                }catch(SQLException e){
                }
            }
            if(ps3 != null){
                try{
                    ps3.close();
                }catch(SQLException e){
                }
            }
            if(ps4 != null){
                try{
                    ps4.close();
                }catch(SQLException e){
                }
            }
            if(ps5 != null){
                try{
                    ps5.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // DatabaseScheduleManagerServiceMBean��JavaDoc
    public void addSchedule(
        String masterId,
        Date time,
        String taskName,
        Object input,
        String[] depends,
        String executorKey,
        String executorType,
        long retryInterval,
        Date retryEndTime,
        long maxDelayTime
    ) throws ScheduleManageException{
        ScheduleDepends[] dependsArray = null;
        if(depends != null){
            dependsArray = new ScheduleDepends[depends.length];
            for(int i = 0; i < dependsArray.length; i++){
                dependsArray[i] = new DefaultScheduleDepends(depends[i], false);
            }
        }
        addSchedule(
            new DefaultSchedule(
                masterId,
                null,
                time,
                taskName,
                input,
                dependsArray,
                null,
                null,
                null,
                executorKey,
                executorType,
                retryInterval,
                retryEndTime,
                maxDelayTime
            )
        );
    }
    
    // ScheduleManager��JavaDoc
    public boolean reschedule(String id, Date time, Object output)
     throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            final int rowVersion = rs.getInt(1);
            st.close();
            st = null;
            rs.close();
            rs = null;
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.date + "=?, "
                    + scheduleTableSchema.time + "=?,"
                    + scheduleTableSchema.checkState + "='"
                    + scheduleTableSchema.getCheckStateString(Schedule.CHECK_STATE_INITIAL) + "',"
                    + scheduleTableSchema.output + "=?,"
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=? and "
                    + scheduleTableSchema.rowVersion + "='" + rowVersion + '\''
            );
            int index = 0;
            final SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            st.setString(++index, format.format(time));
            format.applyPattern(timeFormat);
            st.setString(++index, format.format(time));
            scheduleTableSchema.setOutputObject(++index, st, output);
            st.setTimestamp(++index, new Timestamp(this.time == null ? System.currentTimeMillis() : this.time.currentTimeMillis()));
            st.setString(++index, id);
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean removeSchedule(String id) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        try{
            st = con.prepareStatement(
                "delete from " + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean removeScheduleByMasterId(String masterId, String masterGroupId)
     throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        try{
            if(masterId != null){
                st = con.prepareStatement(
                    "delete from " + scheduleTableSchema.table
                        + " where " + scheduleTableSchema.masterId + "=?"
                );
                st.setString(1, masterId);
            }else{
                StringBuffer buf = new StringBuffer();
                buf.append("delete from ").append(scheduleTableSchema.table)
                    .append(" where ").append(scheduleTableSchema.masterId).append(" in (")
                    .append("select ").append(scheduleGroupMasterTableSchema.id).append(" from ").append(scheduleGroupMasterTableSchema.table)
                    .append(" where ").append(scheduleGroupMasterTableSchema.groupId).append("=?)");
                st = con.prepareStatement(buf.toString());
                st.setString(1, masterGroupId);
            }
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean removeSchedule(Date date) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        try{
            st = con.prepareStatement(
                "delete from " + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.date + "=?"
            );
            st.setString(1, new SimpleDateFormat(dateFormat).format(date));
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean removeSchedule(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        try{
            StringBuffer buf = new StringBuffer();
            buf.append("delete from A.");
            buf.append(scheduleTableSchema.table);
            boolean isAppendWhere = false;
            if(masterGroupId != null || groupId != null){
                buf.append(", (select ").append(scheduleGroupTableSchema.id).append(" from ").append(scheduleGroupTableSchema.table);
                if(masterGroupId != null && groupId != null){
                    buf.append(" where ").append(scheduleGroupTableSchema.masterGroupId).append("=?")
                        .append(" and ").append(scheduleGroupTableSchema.groupId).append("=?) B");
                }else if(masterGroupId != null){
                    buf.append(" where ").append(scheduleGroupTableSchema.masterGroupId).append("=?) B ");
                }else{
                    buf.append(" where ").append(scheduleGroupTableSchema.groupId).append("=?) B ");
                }
                buf.append(" where A.").append(scheduleTableSchema.id).append("=B.").append(scheduleGroupTableSchema.id);
                isAppendWhere = true;
            }
            if(masterId != null){
                if(!isAppendWhere){
                    buf.append(" where (");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                buf.append("A.").append(scheduleTableSchema.masterId).append("=?)");
            }
            if(states != null && states.length != 0){
                if(!isAppendWhere){
                    buf.append(" where (");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                for(int i = 0; i < states.length; i++){
                    buf.append("A.").append(scheduleTableSchema.state);
                    buf.append("=?");
                    if(i != states.length - 1){
                        buf.append(" or ");
                    }
                }
                buf.append(')');
            }
            if(from != null){
                if(!isAppendWhere){
                    buf.append(" where ");
                    isAppendWhere = true;
                }else{
                    buf.append(" and (");
                }
                concatQuery(buf, "A." + scheduleTableSchema.date, "A." + scheduleTableSchema.time);
                buf.append(">=?");
            }
            if(to != null){
                if(!isAppendWhere){
                    buf.append(" where ");
                    isAppendWhere = true;
                }else{
                    buf.append(" and ");
                }
                concatQuery(buf, "A." + scheduleTableSchema.date, "A." + scheduleTableSchema.time);
                buf.append("<=?");
            }
            st = con.prepareStatement(buf.toString());
            buf = null;
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            int index = 0;
            if(masterGroupId != null){
                st.setString(++index, masterGroupId);
            }
            if(groupId != null){
                st.setString(++index, groupId);
            }
            if(masterId != null){
                st.setString(++index, masterId);
            }
            if(states != null && states.length != 0){
                for(int i = 0; i < states.length; i++){
                    st.setString(
                        ++index,
                        scheduleTableSchema.getStateString(
                            states[i]
                        )
                    );
                }
            }
            if(from != null){
                st.setString(++index, format.format(from));
            }
            if(to != null){
                st.setString(++index, format.format(to));
            }
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public void setExecutorKey(String id, String key)
     throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.executorKey + "=?,"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=?"
            );
            if(key == null){
                st.setNull(1, Types.VARCHAR);
            }else{
                st.setString(1, key);
            }
            st.setTimestamp(2, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(3, id);
            st.executeUpdate();
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    public void setRetryEndTime(String id, Date time) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        final SimpleDateFormat format = new SimpleDateFormat(dateFormat + timeFormat);
        try{
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.retryEndTime + "=?,"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=?"
            );
            if(time == null){
                st.setNull(1, Types.CHAR);
            }else{
                st.setString(1, format.format(time));
            }
            st.setTimestamp(2, new Timestamp(this.time == null ? System.currentTimeMillis() : this.time.currentTimeMillis()));
            st.setString(3, id);
            st.executeUpdate();
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    public void setMaxDelayTime(String id, long time) throws ScheduleManageException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleManageException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.maxDelayTime + "=?,"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=?"
            );
            if(time <= 0){
                st.setNull(1, Types.DECIMAL);
            }else{
                st.setLong(1, time);
            }
            st.setTimestamp(2, new Timestamp(this.time == null ? System.currentTimeMillis() : this.time.currentTimeMillis()));
            st.setString(3, id);
            st.executeUpdate();
        }catch(SQLException e){
            throw new ScheduleManageException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public int getState(String id) throws ScheduleStateControlException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.state + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            return scheduleTableSchema.getState(rs.getString(1));
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public int getControlState(String id) throws ScheduleStateControlException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.controlState + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            return scheduleTableSchema.getControlState(rs.getString(1));
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean changeState(String id, int state)
     throws ScheduleStateControlException{
        
        boolean isUpdateExecuteStartTime = false;
        boolean isUpdateExecuteEndTime = false;
        Date executeStartTime = null;
        Date executeEndTime = null;
        switch(state){
        case Schedule.STATE_RUN:
            executeStartTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteStartTime = true;
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            executeEndTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            isUpdateExecuteStartTime = true;
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
        case Schedule.STATE_DISABLE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + state);
        }
        
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.state + ','
                    + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            String oldStateStr = rs.getString(1);
            final int rowVersion = rs.getInt(2);
            st.close();
            st = null;
            rs.close();
            rs = null;
            final String newStateStr = scheduleTableSchema.getStateString(state);
            if(oldStateStr.equals(newStateStr)){
                return false;
            }
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.state + "=?,"
                    + (isUpdateExecuteStartTime ? scheduleTableSchema.executeStartTime + "=?," : "")
                    + (isUpdateExecuteEndTime ? scheduleTableSchema.executeEndTime + "=?," : "")
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=? and "
                    + scheduleTableSchema.rowVersion + "='" + rowVersion + '\''
            );
            int i = 0;
            st.setString(++i, newStateStr);
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            if(isUpdateExecuteStartTime){
                st.setString(++i, executeStartTime == null ? null : format.format(executeStartTime));
            }
            if(isUpdateExecuteEndTime){
                st.setString(++i, executeEndTime == null ? null : format.format(executeEndTime));
            }
            st.setTimestamp(++i, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(++i, id);
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean changeState(String id, int oldState, int newState)
     throws ScheduleStateControlException{
        
        boolean isUpdateExecuteStartTime = false;
        boolean isUpdateExecuteEndTime = false;
        Date executeStartTime = null;
        Date executeEndTime = null;
        switch(newState){
        case Schedule.STATE_RUN:
            executeStartTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteStartTime = true;
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            executeEndTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            isUpdateExecuteStartTime = true;
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
        case Schedule.STATE_DISABLE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + newState);
        }
        
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.state + ','
                    + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            String oldStateStr = rs.getString(1);
            final int rowVersion = rs.getInt(2);
            st.close();
            st = null;
            rs.close();
            rs = null;
            final String newStateStr = scheduleTableSchema.getStateString(newState);
            if(oldStateStr.equals(newStateStr)){
                return false;
            }
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.state + "=?,"
                    + (isUpdateExecuteStartTime ? scheduleTableSchema.executeStartTime + "=?," : "")
                    + (isUpdateExecuteEndTime ? scheduleTableSchema.executeEndTime + "=?," : "")
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=? and "
                    + scheduleTableSchema.state + "=? and "
                    + scheduleTableSchema.rowVersion + "='" + rowVersion + '\''
            );
            int i = 0;
            st.setString(++i, newStateStr);
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            if(isUpdateExecuteStartTime){
                st.setString(++i, executeStartTime == null ? null : format.format(executeStartTime));
            }
            if(isUpdateExecuteEndTime){
                st.setString(++i, executeEndTime == null ? null : format.format(executeEndTime));
            }
            st.setTimestamp(++i, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(++i, id);
            st.setString(++i, scheduleTableSchema.getStateString(oldState));
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean changeState(String id, int oldState, int newState, Object output)
     throws ScheduleStateControlException{
        
        boolean isUpdateExecuteStartTime = false;
        boolean isUpdateExecuteEndTime = false;
        Date executeStartTime = null;
        Date executeEndTime = null;
        switch(newState){
        case Schedule.STATE_RUN:
            executeStartTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteStartTime = true;
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            executeEndTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            isUpdateExecuteStartTime = true;
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
        case Schedule.STATE_DISABLE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + newState);
        }
        
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.state + ','
                    + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            String oldStateStr = rs.getString(1);
            final int rowVersion = rs.getInt(2);
            st.close();
            st = null;
            rs.close();
            rs = null;
            final String newStateStr = scheduleTableSchema.getStateString(newState);
            if(oldStateStr.equals(newStateStr)){
                return false;
            }
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.state + "=?,"
                    + (isUpdateExecuteStartTime ? scheduleTableSchema.executeStartTime + "=?," : "")
                    + (isUpdateExecuteEndTime ? scheduleTableSchema.executeEndTime + "=?," : "")
                    + scheduleTableSchema.output + "=?,"
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=? and "
                    + scheduleTableSchema.state + "=? and "
                    + scheduleTableSchema.rowVersion + "='" + rowVersion + '\''
            );
            int i = 0;
            st.setString(++i, newStateStr);
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            if(isUpdateExecuteStartTime){
                st.setString(++i, executeStartTime == null ? null : format.format(executeStartTime));
            }
            if(isUpdateExecuteEndTime){
                st.setString(++i, executeEndTime == null ? null : format.format(executeEndTime));
            }
            scheduleTableSchema.setOutputObject(++i, st, output);
            st.setTimestamp(++i, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(++i, id);
            st.setString(++i, scheduleTableSchema.getStateString(oldState));
            return st.executeUpdate() != 0;
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean changeState(String id, int state, Object output)
     throws ScheduleStateControlException{
        
        boolean isUpdateExecuteStartTime = false;
        boolean isUpdateExecuteEndTime = false;
        Date executeStartTime = null;
        Date executeEndTime = null;
        switch(state){
        case Schedule.STATE_RUN:
            executeStartTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteStartTime = true;
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            executeEndTime = time == null ? new Date() : new Date(time.currentTimeMillis());
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            isUpdateExecuteStartTime = true;
            isUpdateExecuteEndTime = true;
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
        case Schedule.STATE_DISABLE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + state);
        }
        
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.state + ','
                    + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            final String oldStateStr = rs.getString(1);
            final int rowVersion = rs.getInt(2);
            st.close();
            st = null;
            rs.close();
            rs = null;
            final String newStateStr = scheduleTableSchema.getStateString(state);
            if(oldStateStr.equals(newStateStr)){
                return false;
            }
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.state + "=?,"
                    + scheduleTableSchema.output + "=?,"
                    + (isUpdateExecuteStartTime ? scheduleTableSchema.executeStartTime + "=?," : "")
                    + (isUpdateExecuteEndTime ? scheduleTableSchema.executeEndTime + "=?," : "")
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=? and "
                    + scheduleTableSchema.rowVersion + "='" + rowVersion + '\''
            );
            int i = 0;
            st.setString(++i, newStateStr);
            scheduleTableSchema.setOutputObject(++i, st, output);
            final SimpleDateFormat format = new SimpleDateFormat(
                dateFormat + timeFormat
            );
            if(isUpdateExecuteStartTime){
                st.setString(++i, executeStartTime == null ? null : format.format(executeStartTime));
            }
            if(isUpdateExecuteEndTime){
                st.setString(++i, executeEndTime == null ? null : format.format(executeEndTime));
            }
            st.setTimestamp(++i, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(++i, id);
            return st.executeUpdate() != 0 && !newStateStr.equals(oldStateStr);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
    }
    
    // ScheduleManager��JavaDoc
    public boolean changeControlState(String id, int state)
     throws ScheduleStateControlException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        boolean result = false;
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.controlState + ','
                    + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            final String oldStateStr = rs.getString(1);
            final int nowOldState = scheduleTableSchema.getControlState(oldStateStr);
            final int rowVersion = rs.getInt(2);
            st.close();
            st = null;
            rs.close();
            rs = null;
            
            if(nowOldState == state){
                return false;
            }
            switch(state){
            case Schedule.CONTROL_STATE_PAUSE:
                if(nowOldState != Schedule.STATE_RUN){
                    return false;
                }
                break;
            case Schedule.CONTROL_STATE_RESUME:
                if(nowOldState != Schedule.STATE_PAUSE){
                    return false;
                }
                break;
            case Schedule.CONTROL_STATE_ABORT:
                if(nowOldState != Schedule.STATE_RUN){
                    return false;
                }
                break;
            default:
                throw new ScheduleStateControlException("Unknown state : " + state);
            }
            
            final String newStateStr
                = scheduleTableSchema.getControlStateString(state);
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.controlState + "=?,"
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, newStateStr);
            st.setTimestamp(2, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(3, id);
            result = st.executeUpdate() != 0;
            if(result){
                try{
                    if(scheduleControlListeners != null
                         && scheduleControlListeners.size() != 0){
                        synchronized(scheduleControlListeners){
                            final Iterator itr
                                = scheduleControlListeners.iterator();
                            while(itr.hasNext()){
                                final ScheduleControlListener listener
                                    = (ScheduleControlListener)itr.next();
                                listener.changedControlState(id, state);
                            }
                        }
                    }
                }catch(ScheduleStateControlException e){
                    st.setString(
                        1,
                        scheduleTableSchema.getControlStateString(
                            Schedule.CONTROL_STATE_FAILED
                        )
                    );
                    st.executeUpdate();
                    throw e;
                }
            }
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        return result;
    }
    
    // ScheduleManager��JavaDoc
    public boolean changeControlState(String id, int oldState, int newState)
     throws ScheduleStateControlException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleStateControlException(e);
        }
        boolean result = false;
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(
                "select " + scheduleTableSchema.controlState + ','
                    + scheduleTableSchema.rowVersion + " from "
                    + scheduleTableSchema.table
                    + " where " + scheduleTableSchema.id + "=?"
            );
            st.setString(1, id);
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleStateControlException("Schedule not found : " + id);
            }
            final String oldStateStr = rs.getString(1);
            final int nowOldState = scheduleTableSchema.getControlState(oldStateStr);
            final int rowVersion = rs.getInt(2);
            st.close();
            st = null;
            rs.close();
            rs = null;
            
            if(nowOldState == newState){
                return false;
            }
            switch(newState){
            case Schedule.CONTROL_STATE_PAUSE:
                if(nowOldState != Schedule.STATE_RUN){
                    return false;
                }
                break;
            case Schedule.CONTROL_STATE_RESUME:
                if(nowOldState != Schedule.STATE_PAUSE){
                    return false;
                }
                break;
            case Schedule.CONTROL_STATE_ABORT:
                if(nowOldState != Schedule.STATE_RUN){
                    return false;
                }
                break;
            default:
                throw new ScheduleStateControlException("Unknown state : " + newState);
            }
            
            final String newStateStr
                = scheduleTableSchema.getControlStateString(newState);
            st = con.prepareStatement(
                "update " + scheduleTableSchema.table
                    + " set " + scheduleTableSchema.controlState + "=?,"
                    + scheduleTableSchema.rowVersion + "='" + (rowVersion + 1) + "',"
                    + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                    + scheduleTableSchema.updateTime + "=?"
                    + " where " + scheduleTableSchema.id + "=? and "
                    + scheduleTableSchema.controlState + "=?"
            );
            st.setString(1, newStateStr);
            st.setTimestamp(2, new Timestamp(time == null ? System.currentTimeMillis() : time.currentTimeMillis()));
            st.setString(3, id);
            st.setString(4, scheduleTableSchema.getControlStateString(oldState));
            result = st.executeUpdate() != 0;
            if(result){
                try{
                    if(scheduleControlListeners != null
                         && scheduleControlListeners.size() != 0){
                        synchronized(scheduleControlListeners){
                            final Iterator itr
                                = scheduleControlListeners.iterator();
                            while(itr.hasNext()){
                                final ScheduleControlListener listener
                                    = (ScheduleControlListener)itr.next();
                                listener.changedControlState(id, newState);
                            }
                        }
                    }
                }catch(ScheduleStateControlException e){
                    st.setString(
                        1,
                        scheduleTableSchema.getControlStateString(
                            Schedule.CONTROL_STATE_FAILED
                        )
                    );
                    st.executeUpdate();
                    throw e;
                }
            }
        }catch(SQLException e){
            throw new ScheduleStateControlException(e);
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        return result;
    }
    
    // ScheduleManager��JavaDoc
    public void addScheduleControlListener(ScheduleControlListener listener){
        if((scheduleControlListeners == null
              || scheduleControlListeners.size() == 0)
             && controlStateChecker == null
             && getState() == STARTED
             && controlStateCheckInterval > 0
        ){
            controlStateChecker = new Daemon(new ControlStateChecker());
            controlStateChecker.setName("Nimbus ControlStateChecker " + getServiceNameObject());
            controlStateChecker.start();
        }
        scheduleControlListeners.add(listener);
    }
    
    // ScheduleManager��JavaDoc
    public void removeScheduleControlListener(ScheduleControlListener listener){
        scheduleControlListeners.remove(listener);
    }
    
    private static final Object getInOutObject(int type, String name, ResultSet rs) throws IOException, ClassNotFoundException, SQLException{
        int length = 0;
        switch(type){
        case Types.CLOB:
            Reader reader = rs.getCharacterStream(name);
            StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            while((length = reader.read(chars)) > 0){
                writer.write(chars, 0, length);
            }
            return writer.toString();
        case Types.BLOB:
            InputStream is = rs.getBinaryStream(name);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            while((length = is.read(bytes)) > 0){
                baos.write(bytes, 0, length);
            }
            ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())
            );
            return ois.readObject();
        default:
            return rs.getObject(name);
        }
    }
    
    private static final void setInOutObject(int type, int index, PreparedStatement ps, Object value) throws IOException, SQLException{
        if(value == null){
            ps.setNull(index, type);
            return;
        }
        switch(type){
        case Types.CLOB:
            char[] chars = value.toString().toCharArray();
            ps.setCharacterStream(index, new CharArrayReader(chars), chars.length);
            break;
        case Types.BLOB:
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            ps.setBinaryStream(index, new ByteArrayInputStream(bytes), bytes.length);
            break;
        default:
            ps.setString(index, value.toString());
        }
    }
    
    /**
     * �X�P�W���[���}�X�^�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���}�X�^��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE           = "SCHEDULE_MST";
        
        /**
         * �f�t�H���g�́u�X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_ID              = "ID";
        
        /**
         * �f�t�H���g�́u�^�X�N���v�̃J�������B<p>
         */
        public static final String DEFAULT_TASK_NAME       = "TASK_NAME";
        
        /**
         * �f�t�H���g�́u�X�P�W���[����ʁv�̃J�������B<p>
         */
        public static final String DEFAULT_SCHEDULE_TYPE   = "SCHEDULE_TYPE";
        
        /**
         * �f�t�H���g�́u���́v�̃J�������B<p>
         */
        public static final String DEFAULT_INPUT           = "INPUT";
        
        /**
         * �f�t�H���g�́u�J�n�����v�̃J�������B<p>
         */
        public static final String DEFAULT_START_TIME      = "START_TIME";
        
        /**
         * �f�t�H���g�́u�I�������v�̃J�������B<p>
         */
        public static final String DEFAULT_END_TIME        = "END_TIME";
        
        /**
         * �f�t�H���g�́u�J��Ԃ��Ԋu�v�̃J�������B<p>
         */
        public static final String DEFAULT_REPEAT_INTERVAL = "REPEAT_INTERVAL";
        
        /**
         * �f�t�H���g�́u���g���C�Ԋu�v�̃J�������B<p>
         */
        public static final String DEFAULT_RETRY_INTERVAL  = "RETRY_INTERVAL";
        
        /**
         * �f�t�H���g�́u���g���C�I�������v�̃J�������B<p>
         */
        public static final String DEFAULT_RETRY_END_TIME  = "RETRY_END_TIME";
        
        /**
         * �f�t�H���g�́u�ő�x�����ԁv�̃J�������B<p>
         */
        public static final String DEFAULT_MAX_DELAY_TIME  = "MAX_DELAY_TIME";
        
        /**
         * �f�t�H���g�́u�L���t���O�v�̃J�������B<p>
         */
        public static final String DEFAULT_ENABLE          = "ENABLE";
        
        /**
         * �f�t�H���g�́u���s�L�[�v�̃J�������B<p>
         */
        public static final String DEFAULT_EXECUTOR_KEY    = "EXECUTOR_KEY";
        
        /**
         * �f�t�H���g�́u���s��ށv�̃J�������B<p>
         */
        public static final String DEFAULT_EXECUTOR_TYPE   = "EXECUTOR_TYPE";
        
        /**
         * �f�t�H���g�́u�e���v���[�g�v�̃J�������B<p>
         */
        public static final String DEFAULT_TEMPLATE        = "TEMPLATE";
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        /**
         * �u�X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public String id = DEFAULT_ID;
        
        /**
         * �u�^�X�N���v�̃J�������B<p>
         */
        public String taskName = DEFAULT_TASK_NAME;
        
        /**
         * �u�X�P�W���[����ʁv�̃J�������B<p>
         */
        public String scheduleType = DEFAULT_SCHEDULE_TYPE;
        
        /**
         * �u���́v�̃J�������B<p>
         */
        public String input = DEFAULT_INPUT;
        
        /**
         * �u�J�n�����v�̃J�������B<p>
         */
        public String startTime = DEFAULT_START_TIME;
        
        /**
         * �u�I�������v�̃J�������B<p>
         */
        public String endTime = DEFAULT_END_TIME;
        
        /**
         * �u�J��Ԃ��Ԋu�v�̃J�������B<p>
         */
        public String repeatInterval = DEFAULT_REPEAT_INTERVAL;
        
        /**
         * �u���g���C�Ԋu�v�̃J�������B<p>
         */
        public String retryInterval = DEFAULT_RETRY_INTERVAL;
        
        /**
         * �u���g���C�I�������v�̃J�������B<p>
         */
        public String retryEndTime = DEFAULT_RETRY_END_TIME;
        
        /**
         * �u�ő�x�����ԁv�̃J�������B<p>
         */
        public String maxDelayTime = DEFAULT_MAX_DELAY_TIME;
        
        /**
         * �u�L���t���O�v�̃J�������B<p>
         */
        public String enable = DEFAULT_ENABLE;
        
        /**
         * �u���s�L�[�v�̃J�������B<p>
         */
        public String executorKey = DEFAULT_EXECUTOR_KEY;
        
        /**
         * �u���s��ށv�̃J�������B<p>
         */
        public String executorType = DEFAULT_EXECUTOR_TYPE;
        
        /**
         * �u�e���v���[�g�v�̃J�������B<p>
         */
        public String template = DEFAULT_TEMPLATE;
        
        /**
         * �u���́v�̃J�����̌^�B<p>
         * �f�t�H���g�́Ajava.sq.Types.VARCHAR�B<br>
         */
        public int inputColumnType = Types.VARCHAR;
        
        /**
         * �������ʂ���u���́v�̃J�����̒l���擾����B<p>
         *
         * @param rs ��������
         * @return �u���́v�̃J�����̒l
         * @exception SQLException SQL��O
         * @exception IOException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
         * @exception ClassNotFoundException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
         */
        protected Object getInputObject(ResultSet rs) throws IOException, ClassNotFoundException, SQLException{
            return DatabaseScheduleManagerService.getInOutObject(inputColumnType, input, rs);
        }
    }
    
    /**
     * �X�P�W���[���ˑ��֌W�}�X�^�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���}�X�^�̈ˑ��֌W��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleDependsMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE      = "SCHEDULE_DEPENDS_MST";
        
        /**
         * �f�t�H���g�́u�ˑ������X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_ID         = "ID";
        
        /**
         * �f�t�H���g�́u�ˑ�����X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_DEPENDS_ID = "DEPENDS_ID";
        
        /**
         * �f�t�H���g�́u�ˑ�����X�P�W���[���O���[�v�}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_DEPENDS_GROUP_ID = "DEPENDS_GROUP_ID";
        
        /**
         * �f�t�H���g�́u�O���[�v���ˑ��������ꍇ�̃X�P�W���[���O���[�v�}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_GROUP_ID = "GROUP_ID";
        
        /**
         * �f�t�H���g�́u�G���[�����v�̃J�������B<p>
         */
        public static final String DEFAULT_IGNORE_ERROR = "IGNORE_ERROR";
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        /**
         * �u�ˑ������X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public String id = DEFAULT_ID;
        
        /**
         * �u�ˑ�����X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public String dependsId = DEFAULT_DEPENDS_ID;
        
        /**
         * �u�ˑ�����X�P�W���[���O���[�v�}�X�^ID�v�̃J�������B<p>
         */
        public String dependsGroupId = DEFAULT_DEPENDS_GROUP_ID;
        
        /**
         * �u�O���[�v���ˑ��������ꍇ�̃X�P�W���[���O���[�v�}�X�^ID�v�̃J�������B<p>
         */
        public String groupId = DEFAULT_GROUP_ID;
        
        /**
         * �u�G���[�����v�̃J�������B<p>
         */
        public String ignoreError = DEFAULT_IGNORE_ERROR;
    }
    
    /**
     * �X�P�W���[���O���[�v�}�X�^�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���}�X�^�̃O���[�s���O��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleGroupMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE      = "SCHEDULE_GROUP_MST";
        
        /**
         * �f�t�H���g�́u�X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_ID         = "ID";
        
        /**
         * �f�t�H���g�́u�O���[�vID�v�̃J�������B<p>
         */
        public static final String DEFAULT_GROUP_ID = "GROUP_ID";
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        /**
         * �u�X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public String id = DEFAULT_ID;
        
        /**
         * �u�O���[�vID�v�̃J�������B<p>
         */
        public String groupId = DEFAULT_GROUP_ID;
    }
    
    /**
     * �X�P�W���[���O���[�v�ˑ��֌W�}�X�^�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���O���[�v�}�X�^�̈ˑ��֌W��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleGroupDependsMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE      = "SCHEDULE_GROUP_DEPENDS_MST";
        
        /**
         * �f�t�H���g�́u�ˑ������X�P�W���[���}�X�^�O���[�vID�v�̃J�������B<p>
         */
        public static final String DEFAULT_GROUP_ID         = "GROUP_ID";
        
        /**
         * �f�t�H���g�́u�ˑ�����X�P�W���[���}�X�^�O���[�vID�v�̃J�������B<p>
         */
        public static final String DEFAULT_DEPENDS_GROUP_ID = "DEPENDS_GROUP_ID";
        
        /**
         * �f�t�H���g�́u�G���[�����v�̃J�������B<p>
         */
        public static final String DEFAULT_IGNORE_ERROR = "IGNORE_ERROR";
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        /**
         * �u�ˑ������X�P�W���[���}�X�^�O���[�vID�v�̃J�������B<p>
         */
        public String groupId = DEFAULT_GROUP_ID;
        
        /**
         * �u�ˑ�����X�P�W���[���}�X�^�O���[�vID�v�̃J�������B<p>
         */
        public String dependsGroupId = DEFAULT_DEPENDS_GROUP_ID;
        
        /**
         * �u�G���[�����v�̃J�������B<p>
         */
        public String ignoreError = DEFAULT_IGNORE_ERROR;
    }
    
    /**
     * �X�P�W���[���e�[�u���X�L�[�}���B<p>
     * �X�P�W���[����o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE      = "SCHEDULE";
        
        /**
         * �f�t�H���g�́u�X�P�W���[��ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_ID             = "ID";
        
        /**
         * �f�t�H���g�́u�X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public static final String DEFAULT_MASTER_ID      = "MASTER_ID";
        
        /**
         * �f�t�H���g�́u�X�P�W���[�����t�v�̃J�������B<p>
         */
        public static final String DEFAULT_DATE           = "S_DATE";
        
        /**
         * �f�t�H���g�́u�J�n�����v�̃J�������B<p>
         */
        public static final String DEFAULT_TIME           = "S_TIME";
        
        /**
         * �f�t�H���g�́u�^�X�N���v�̃J�������B<p>
         */
        public static final String DEFAULT_TASK_NAME      = "TASK_NAME";
        
        /**
         * �f�t�H���g�́u���́v�̃J�������B<p>
         */
        public static final String DEFAULT_INPUT          = "INPUT";
        
        /**
         * �f�t�H���g�́u�o�́v�̃J�������B<p>
         */
        public static final String DEFAULT_OUTPUT         = "OUTPUT";
        
        /**
         * �f�t�H���g�́u�����X�P�W���[�����t�v�̃J�������B<p>
         */
        public static final String DEFAULT_INITIAL_DATE   = "INITIAL_DATE";
        
        /**
         * �f�t�H���g�́u�����J�n�����v�̃J�������B<p>
         */
        public static final String DEFAULT_INITIAL_TIME   = "INITIAL_TIME";
        
        /**
         * �f�t�H���g�́u���g���C�Ԋu�v�̃J�������B<p>
         */
        public static final String DEFAULT_RETRY_INTERVAL = "RETRY_INTERVAL";
        
        /**
         * �f�t�H���g�́u���g���C�I�������v�̃J�������B<p>
         */
        public static final String DEFAULT_RETRY_END_TIME = "RETRY_END_TIME";
        
        /**
         * �f�t�H���g�́u�ő�x�����ԁv�̃J�������B<p>
         */
        public static final String DEFAULT_MAX_DELAY_TIME  = "MAX_DELAY_TIME";
        
        /**
         * �f�t�H���g�́u��ԁv�̃J�������B<p>
         */
        public static final String DEFAULT_STATE          = "STATE";
        
        /**
         * �f�t�H���g�́u�����ԁv�̃J�������B<p>
         */
        public static final String DEFAULT_CONTROL_STATE  = "CONTROL_STATE";
        
        /**
         * �f�t�H���g�́u�`�F�b�N��ԁv�̃J�������B<p>
         */
        public static final String DEFAULT_CHECK_STATE  = "CHECK_STATE";
        
        /**
         * �f�t�H���g�́u���s�L�[�v�̃J�������B<p>
         */
        public static final String DEFAULT_EXECUTOR_KEY   = "EXECUTOR_KEY";
        
        /**
         * �f�t�H���g�́u���s��ށv�̃J�������B<p>
         */
        public static final String DEFAULT_EXECUTOR_TYPE   = "EXECUTOR_TYPE";
        
        /**
         * �f�t�H���g�́u���s�J�n�����v�̃J�������B<p>
         */
        public static final String DEFAULT_EXECUTE_START_TIME     = "EXEC_S_TIME";
        
        /**
         * �f�t�H���g�́u���s�I�������v�̃J�������B<p>
         */
        public static final String DEFAULT_EXECUTE_END_TIME     = "EXEC_E_TIME";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public static final String DEFAULT_ROWVERSION     = "ROWVERSION";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATEUSERID   = "UPDATEUSERID";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATETIME     = "UPDATETIME";
        
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F�����B<p>
         */
        public static final String DEFAULT_STATE_STRING_INITIAL = "I";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F�����B<p>
         */
        public static final String DEFAULT_STATE_STRING_ENTRY   = "E";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F���s���B<p>
         */
        public static final String DEFAULT_STATE_STRING_RUN     = "R";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F����I���B<p>
         */
        public static final String DEFAULT_STATE_STRING_END     = "N";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F�ُ�I���B<p>
         */
        public static final String DEFAULT_STATE_STRING_FAILED  = "F";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F���f���B<p>
         */
        public static final String DEFAULT_STATE_STRING_PAUSE   = "P";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F�����I���B<p>
         */
        public static final String DEFAULT_STATE_STRING_ABORT   = "A";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F���g���C���B<p>
         */
        public static final String DEFAULT_STATE_STRING_RETRY   = "T";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F�������B<p>
         */
        public static final String DEFAULT_STATE_STRING_DISABLE = "D";
        
        /**
         * �f�t�H���g�̃J�����u��ԁv�̒l�F�s���B<p>
         */
        public static final String DEFAULT_STATE_STRING_UNKNOWN = "U";
        
        
        /**
         * �f�t�H���g�̃J�����u�����ԁv�̒l�F�����B<p>
         */
        public static final String DEFAULT_CONTROL_STATE_STRING_INITIAL = "I";
        
        /**
         * �f�t�H���g�̃J�����u�����ԁv�̒l�F���f�B<p>
         */
        public static final String DEFAULT_CONTROL_STATE_STRING_PAUSE   = "P";
        
        /**
         * �f�t�H���g�̃J�����u�����ԁv�̒l�F�ĊJ�B<p>
         */
        public static final String DEFAULT_CONTROL_STATE_STRING_RESUME  = "R";
        
        /**
         * �f�t�H���g�̃J�����u�����ԁv�̒l�F�����I���B<p>
         */
        public static final String DEFAULT_CONTROL_STATE_STRING_ABORT   = "A";
        
        /**
         * �f�t�H���g�̃J�����u�����ԁv�̒l�F���䎸�s�B<p>
         */
        public static final String DEFAULT_CONTROL_STATE_STRING_FAILED  = "F";
        
        /**
         * �f�t�H���g�̃J�����u�����ԁv�̒l�F�s���B<p>
         */
        public static final String DEFAULT_CONTROL_STATE_STRING_UNKNOWN = "U";
        
        
        /**
         * �f�t�H���g�̃J�����u�`�F�b�N��ԁv�̒l�F�����B<p>
         */
        public static final String DEFAULT_CHECK_STATE_STRING_INITIAL = "I";
        
        /**
         * �f�t�H���g�̃J�����u�`�F�b�N��ԁv�̒l�F�^�C���I�[�o�[�B<p>
         */
        public static final String DEFAULT_CHECK_STATE_STRING_TIMEOVER   = "O";
        
        /**
         * �f�t�H���g�̃J�����u�`�F�b�N��ԁv�̒l�F�s���B<p>
         */
        public static final String DEFAULT_CHECK_STATE_STRING_UNKNOWN = "U";
        
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        
        /**
         * �u�X�P�W���[��ID�v�̃J�������B<p>
         */
        public String id = DEFAULT_ID;
        
        /**
         * �u�X�P�W���[���}�X�^ID�v�̃J�������B<p>
         */
        public String masterId = DEFAULT_MASTER_ID;
        
        /**
         * �u�X�P�W���[�����t�v�̃J�������B<p>
         */
        public String date = DEFAULT_DATE;
        
        /**
         * �u�J�n�����v�̃J�������B<p>
         */
        public String time = DEFAULT_TIME;
        
        /**
         * �u�^�X�N���v�̃J�������B<p>
         */
        public String taskName = DEFAULT_TASK_NAME;
        
        /**
         * �u���́v�̃J�������B<p>
         */
        public String input = DEFAULT_INPUT;
        
        /**
         * �u�o�́v�̃J�������B<p>
         */
        public String output = DEFAULT_OUTPUT;
        
        /**
         * �u�����X�P�W���[�����t�v�̃J�������B<p>
         */
        public String initialDate = DEFAULT_INITIAL_DATE;
        
        /**
         * �u�����J�n�����v�̃J�������B<p>
         */
        public String initialTime = DEFAULT_INITIAL_TIME;
        
        /**
         * �u���g���C�Ԋu�v�̃J�������B<p>
         */
        public String retryInterval = DEFAULT_RETRY_INTERVAL;
        
        /**
         * �u���g���C�I�������v�̃J�������B<p>
         */
        public String retryEndTime = DEFAULT_RETRY_END_TIME;
        
        /**
         * �u�ő�x�����ԁv�̃J�������B<p>
         */
        public String maxDelayTime = DEFAULT_MAX_DELAY_TIME;
        
        /**
         * �u��ԁv�̃J�������B<p>
         */
        public String state = DEFAULT_STATE;
        
        /**
         * �u�����ԁv�̃J�������B<p>
         */
        public String controlState = DEFAULT_CONTROL_STATE;
        
        /**
         * �u�`�F�b�N��ԁv�̃J�������B<p>
         */
        public String checkState = DEFAULT_CHECK_STATE;
        
        /**
         * �u���s�L�[�v�̃J�������B<p>
         */
        public String executorKey = DEFAULT_EXECUTOR_KEY;
        
        /**
         * �u���s��ށv�̃J�������B<p>
         */
        public String executorType = DEFAULT_EXECUTOR_TYPE;
        
        /**
         * �u���s�J�n�����v�̃J�������B<p>
         */
        public String executeStartTime = DEFAULT_EXECUTE_START_TIME;
        
        /**
         * �u���s�I�������v�̃J�������B<p>
         */
        public String executeEndTime = DEFAULT_EXECUTE_END_TIME;
        
        /**
         * �u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public String rowVersion = DEFAULT_ROWVERSION;
        
        /**
         * �u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public String updateUserId = DEFAULT_UPDATEUSERID;
        
        /**
         * �u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public String updateTime = DEFAULT_UPDATETIME;
        
        
        /**
         * �J�����u��ԁv�̒l�F�����B<p>
         */
        public String stateString_INITIAL = DEFAULT_STATE_STRING_INITIAL;
        
        /**
         * �J�����u��ԁv�̒l�F�����B<p>
         */
        public String stateString_ENTRY = DEFAULT_STATE_STRING_ENTRY;
        
        /**
         * �J�����u��ԁv�̒l�F���s���B<p>
         */
        public String stateString_RUN = DEFAULT_STATE_STRING_RUN;
        
        /**
         * �J�����u��ԁv�̒l�F����I���B<p>
         */
        public String stateString_END = DEFAULT_STATE_STRING_END;
        
        /**
         * �J�����u��ԁv�̒l�F�ُ�I���B<p>
         */
        public String stateString_FAILED = DEFAULT_STATE_STRING_FAILED;
        
        /**
         * �J�����u��ԁv�̒l�F���f���B<p>
         */
        public String stateString_PAUSE = DEFAULT_STATE_STRING_PAUSE;
        
        /**
         * �J�����u��ԁv�̒l�F�����I���B<p>
         */
        public String stateString_ABORT = DEFAULT_STATE_STRING_ABORT;
        
        /**
         * �J�����u��ԁv�̒l�F���g���C���B<p>
         */
        public String stateString_RETRY = DEFAULT_STATE_STRING_RETRY;
        
        /**
         * �J�����u��ԁv�̒l�F�������B<p>
         */
        public String stateString_DISABLE = DEFAULT_STATE_STRING_DISABLE;
        
        /**
         * �J�����u��ԁv�̒l�F�s���B<p>
         */
        public String stateString_UNKNOWN = DEFAULT_STATE_STRING_UNKNOWN;
        
        
        /**
         * �J�����u�����ԁv�̒l�F�����B<p>
         */
        public String controlStateString_INITIAL = DEFAULT_CONTROL_STATE_STRING_INITIAL;
        
        /**
         * �J�����u�����ԁv�̒l�F���f�B<p>
         */
        public String controlStateString_PAUSE = DEFAULT_CONTROL_STATE_STRING_PAUSE;
        
        /**
         * �J�����u�����ԁv�̒l�F�ĊJ�B<p>
         */
        public String controlStateString_RESUME = DEFAULT_CONTROL_STATE_STRING_RESUME;
        
        /**
         * �J�����u�����ԁv�̒l�F�����I���B<p>
         */
        public String controlStateString_ABORT = DEFAULT_CONTROL_STATE_STRING_ABORT;
        
        /**
         * �J�����u�����ԁv�̒l�F���䎸�s�B<p>
         */
        public String controlStateString_FAILED = DEFAULT_CONTROL_STATE_STRING_FAILED;
        
        /**
         * �J�����u�����ԁv�̒l�F�s���B<p>
         */
        public String controlStateString_UNKNOWN = DEFAULT_CONTROL_STATE_STRING_UNKNOWN;
        
        
        /**
         * �J�����u�`�F�b�N��ԁv�̒l�F�����B<p>
         */
        public String checkStateString_INITIAL = DEFAULT_CHECK_STATE_STRING_INITIAL;
        
        /**
         * �J�����u�`�F�b�N��ԁv�̒l�F�^�C���I�[�o�[�B<p>
         */
        public String checkStateString_TIMEOVER = DEFAULT_CHECK_STATE_STRING_TIMEOVER;
        
        /**
         * �J�����u�`�F�b�N��ԁv�̒l�F�s���B<p>
         */
        public String checkStateString_UNKNOWN = DEFAULT_CHECK_STATE_STRING_UNKNOWN;
        
        /**
         * �u���́v�̃J�����̌^�B<p>
         * �f�t�H���g�́Ajava.sq.Types.VARCHAR�B<br>
         */
        public int inputColumnType = Types.VARCHAR;
        
        /**
         * �u�o�́v�̃J�����̌^�B<p>
         * �f�t�H���g�́Ajava.sq.Types.VARCHAR�B<br>
         */
        public int outputColumnType = Types.VARCHAR;
        
        /**
         * �������ʂ���u���́v�̃J�����̒l���擾����B<p>
         *
         * @param rs ��������
         * @return �u���́v�̃J�����̒l
         * @exception SQLException SQL��O
         * @exception IOException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
         * @exception ClassNotFoundException ���̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
         */
        protected Object getInputObject(ResultSet rs) throws IOException, ClassNotFoundException, SQLException{
            return DatabaseScheduleManagerService.getInOutObject(inputColumnType, input, rs);
        }
        
        /**
         * �w�肳�ꂽPreparedStatement�Ɂu���́v�̃J�����̒l��ݒ肷��B<p>
         *
         * @param index �C���f�b�N�X
         * @param ps PreparedStatement
         * @param value �u���́v�̃J�����̒l
         * @exception SQLException SQL��O
         * @exception IOException ���̓I�u�W�F�N�g�̏����݂Ɏ��s�����ꍇ
         */
        protected void setInputObject(int index, PreparedStatement ps, Object value) throws IOException, SQLException{
            DatabaseScheduleManagerService.setInOutObject(inputColumnType, index, ps, value);
        }
        
        /**
         * �������ʂ���u�o�́v�̃J�����̒l���擾����B<p>
         *
         * @param rs ��������
         * @return �u�o�́v�̃J�����̒l
         * @exception SQLException SQL��O
         * @exception IOException �o�̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
         * @exception ClassNotFoundException �o�̓I�u�W�F�N�g�̓ǂݍ��݂Ɏ��s�����ꍇ
         */
        protected Object getOutputObject(ResultSet rs) throws IOException, ClassNotFoundException, SQLException{
            return DatabaseScheduleManagerService.getInOutObject(outputColumnType, output, rs);
        }
        
        /**
         * �w�肳�ꂽPreparedStatement�Ɂu�o�́v�̃J�����̒l��ݒ肷��B<p>
         *
         * @param index �C���f�b�N�X
         * @param ps PreparedStatement
         * @param value �u�o�́v�̃J�����̒l
         * @exception SQLException SQL��O
         * @exception IOException �o�̓I�u�W�F�N�g�̏����݂Ɏ��s�����ꍇ
         */
        protected void setOutputObject(int index, PreparedStatement ps, Object value) throws IOException, SQLException{
            DatabaseScheduleManagerService.setInOutObject(outputColumnType, index, ps, value);
        }
        
        /**
         * �w�肳�ꂽ��Ԓl�ɊY�������ԕ�������擾����B<p>
         *
         * @param state ��Ԓl
         * @return ��ԕ�����
         */
        public String getStateString(int state){
            switch(state){
            case Schedule.STATE_INITIAL:
                return stateString_INITIAL;
            case Schedule.STATE_ENTRY:
                return stateString_ENTRY;
            case Schedule.STATE_RUN:
                return stateString_RUN;
            case Schedule.STATE_END:
                return stateString_END;
            case Schedule.STATE_FAILED:
                return stateString_FAILED;
            case Schedule.STATE_PAUSE:
                return stateString_PAUSE;
            case Schedule.STATE_ABORT:
                return stateString_ABORT;
            case Schedule.STATE_RETRY:
                return stateString_RETRY;
            case Schedule.STATE_DISABLE:
                return stateString_DISABLE;
            default:
                return stateString_UNKNOWN;
            }
        }
        
        /**
         * �w�肳�ꂽ��ԕ�����ɊY�������Ԓl���擾����B<p>
         *
         * @param state ��ԕ�����
         * @return ��Ԓl
         */
        public int getState(String state){
            if(stateString_INITIAL.equals(state)){
                return Schedule.STATE_INITIAL;
            }
            if(stateString_ENTRY.equals(state)){
                return Schedule.STATE_ENTRY;
            }
            if(stateString_RUN.equals(state)){
                return Schedule.STATE_RUN;
            }
            if(stateString_END.equals(state)){
                return Schedule.STATE_END;
            }
            if(stateString_FAILED.equals(state)){
                return Schedule.STATE_FAILED;
            }
            if(stateString_PAUSE.equals(state)){
                return Schedule.STATE_PAUSE;
            }
            if(stateString_ABORT.equals(state)){
                return Schedule.STATE_ABORT;
            }
            if(stateString_RETRY.equals(state)){
                return Schedule.STATE_RETRY;
            }
            if(stateString_DISABLE.equals(state)){
                return Schedule.STATE_DISABLE;
            }else{
                return Schedule.STATE_UNKNOWN;
            }
        }
        
        /**
         * �w�肳�ꂽ�����Ԓl�ɊY�����鐧���ԕ�������擾����B<p>
         *
         * @param state �����Ԓl
         * @return �����ԕ�����
         */
        public String getControlStateString(int state){
            switch(state){
            case Schedule.CONTROL_STATE_INITIAL:
                return controlStateString_INITIAL;
            case Schedule.CONTROL_STATE_PAUSE:
                return controlStateString_PAUSE;
            case Schedule.CONTROL_STATE_RESUME:
                return controlStateString_RESUME;
            case Schedule.CONTROL_STATE_ABORT:
                return controlStateString_ABORT;
            case Schedule.CONTROL_STATE_FAILED:
                return controlStateString_FAILED;
            default:
                return controlStateString_UNKNOWN;
            }
        }
        
        /**
         * �w�肳�ꂽ�����ԕ�����ɊY�����鐧���Ԓl���擾����B<p>
         *
         * @param state �����ԕ�����
         * @return �����Ԓl
         */
        public int getControlState(String state){
            if(controlStateString_INITIAL.equals(state)){
                return Schedule.CONTROL_STATE_INITIAL;
            }
            if(controlStateString_PAUSE.equals(state)){
                return Schedule.CONTROL_STATE_PAUSE;
            }
            if(controlStateString_ABORT.equals(state)){
                return Schedule.CONTROL_STATE_ABORT;
            }
            if(controlStateString_FAILED.equals(state)){
                return Schedule.CONTROL_STATE_FAILED;
            }
            if(controlStateString_RESUME.equals(state)){
                return Schedule.CONTROL_STATE_RESUME;
            }else{
                return Schedule.CONTROL_STATE_UNKNOWN;
            }
        }
        
        /**
         * �w�肳�ꂽ�`�F�b�N��Ԓl�ɊY������`�F�b�N��ԕ�������擾����B<p>
         *
         * @param state �`�F�b�N��Ԓl
         * @return �`�F�b�N��ԕ�����
         */
        public String getCheckStateString(int state){
            switch(state){
            case Schedule.CHECK_STATE_INITIAL:
                return checkStateString_INITIAL;
            case Schedule.CHECK_STATE_TIMEOVER:
                return checkStateString_TIMEOVER;
            default:
                return checkStateString_UNKNOWN;
            }
        }
        
        /**
         * �w�肳�ꂽ�`�F�b�N��ԕ�����ɊY������`�F�b�N��Ԓl���擾����B<p>
         *
         * @param state �`�F�b�N��ԕ�����
         * @return �`�F�b�N��Ԓl
         */
        public int getCheckState(String state){
            if(checkStateString_INITIAL.equals(state)){
                return Schedule.CHECK_STATE_INITIAL;
            }
            if(checkStateString_TIMEOVER.equals(state)){
                return Schedule.CHECK_STATE_TIMEOVER;
            }else{
                return Schedule.CHECK_STATE_UNKNOWN;
            }
        }
    }
    
    /**
     * �X�P�W���[���O���[�v�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���̃O���[�s���O��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleGroupTableSchema extends ScheduleGroupMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE      = "SCHEDULE_GROUP";
        
        /**
         * �f�t�H���g�́u�}�X�^�O���[�vID�v�̃J�������B<p>
         */
        public static final String DEFAULT_MASTER_GROUP_ID = "MASTER_GROUP_ID";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public static final String DEFAULT_ROWVERSION     = "ROWVERSION";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATEUSERID   = "UPDATEUSERID";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATETIME     = "UPDATETIME";
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        /**
         * �u�}�X�^�O���[�vID�v�̃J�������B<p>
         */
        public String masterGroupId = DEFAULT_MASTER_GROUP_ID;
        
        
        /**
         * �u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public String rowVersion = DEFAULT_ROWVERSION;
        
        /**
         * �u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public String updateUserId = DEFAULT_UPDATEUSERID;
        
        /**
         * �u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public String updateTime = DEFAULT_UPDATETIME;
    }
    
    /**
     * �X�P�W���[���ˑ��֌W�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���̈ˑ��֌W��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleDependsTableSchema
     extends ScheduleDependsMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE = "SCHEDULE_DEPENDS";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public static final String DEFAULT_ROWVERSION     = "ROWVERSION";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATEUSERID   = "UPDATEUSERID";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATETIME     = "UPDATETIME";
        
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        
        /**
         * �u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public String rowVersion = DEFAULT_ROWVERSION;
        
        /**
         * �u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public String updateUserId = DEFAULT_UPDATEUSERID;
        
        /**
         * �u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public String updateTime = DEFAULT_UPDATETIME;
    }
    
    /**
     * �X�P�W���[���O���[�v�ˑ��֌W�e�[�u���X�L�[�}���B<p>
     * �X�P�W���[���̃O���[�v�ˑ��֌W��o�^����e�[�u���̃X�L�[�}�����`����N���X�B<br>
     *
     * @author M.Takata
     */
    public static class ScheduleGroupDependsTableSchema
     extends ScheduleGroupDependsMasterTableSchema{
        
        /**
         * �f�t�H���g�̃e�[�u�����B<p>
         */
        public static final String DEFAULT_TABLE = "SCHEDULE_GROUP_DEPENDS";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public static final String DEFAULT_ROWVERSION     = "ROWVERSION";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATEUSERID   = "UPDATEUSERID";
        
        /**
         * �f�t�H���g�́u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public static final String DEFAULT_UPDATETIME     = "UPDATETIME";
        
        
        /**
         * �e�[�u�����B<p>
         */
        public String table = DEFAULT_TABLE;
        
        
        /**
         * �u���R�[�h�X�V�o�[�W�����ԍ��v�̃J�������B<p>
         */
        public String rowVersion = DEFAULT_ROWVERSION;
        
        /**
         * �u���R�[�h�X�V���[�UID�v�̃J�������B<p>
         */
        public String updateUserId = DEFAULT_UPDATEUSERID;
        
        /**
         * �u���R�[�h�X�V�����v�̃J�������B<p>
         */
        public String updateTime = DEFAULT_UPDATETIME;
    }
    
    /**
     * �����ԊĎ��B<p>
     *
     * @author M.Takata
     */
    protected class ControlStateChecker implements DaemonRunnable{
        
        /**
         * �f�[�������J�n�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onStart() {
            return true;
        }
        
        /**
         * �f�[��������~�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onStop() {
            return true;
        }
        
        /**
         * �f�[���������f�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onSuspend() {
            return true;
        }
        
        /**
         * �f�[�������ĊJ�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onResume() {
            return true;
        }
        
        /**
         * ��莞�ԋ󂯂�B<p>
         *
         * @param ctrl DaemonControl�I�u�W�F�N�g
         * @return �X�P�W���[���̔z��
         */
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(getControlStateCheckInterval(), true);
            return null;
        }
        
        /**
         * �����Ԃ̃`�F�b�N������B<p>
         *
         * @param input null
         * @param ctrl DaemonControl�I�u�W�F�N�g
         */
        public void consume(Object input, DaemonControl ctrl)
         throws Throwable{
            if(scheduleControlListeners == null
                || scheduleControlListeners.size() == 0){
                return;
            }
            Connection con = null;
            try{
                con = connectionFactory.getConnection();
            }catch(ConnectionFactoryException e){
                getLogger().write(MSG_ID_CONTROL_STATE_CHECK_ERROR, getServiceNameObject(), e);
                return;
            }
            Statement st = null;
            ResultSet rs = null;
            try{
                st = con.createStatement();
                rs = st.executeQuery(
                    "select " + scheduleTableSchema.id + ','
                        + scheduleTableSchema.controlState
                        + " from " + scheduleTableSchema.table
                        + " where ("
                        + scheduleTableSchema.state + "='"
                        + scheduleTableSchema.getStateString(Schedule.STATE_RUN)
                        + "' or " + scheduleTableSchema.state + "='"
                        + scheduleTableSchema.getStateString(Schedule.STATE_PAUSE)
                        + "') and (" + scheduleTableSchema.controlState + "='"
                        + scheduleTableSchema.getControlStateString(Schedule.CONTROL_STATE_PAUSE)
                        + "' or " + scheduleTableSchema.controlState + "='"
                        + scheduleTableSchema.getControlStateString(Schedule.CONTROL_STATE_RESUME)
                        + "' or " + scheduleTableSchema.controlState + "='"
                        + scheduleTableSchema.getControlStateString(Schedule.CONTROL_STATE_ABORT)
                        + "')"
                );
                while(rs.next()){
                    final String id = rs.getString(1);
                    final String controlStateStr = rs.getString(2);
                    final int controlState = scheduleTableSchema
                        .getControlState(controlStateStr);
                    try{
                        if(scheduleControlListeners != null
                             && scheduleControlListeners.size() != 0){
                            synchronized(scheduleControlListeners){
                                final Iterator itr
                                    = scheduleControlListeners.iterator();
                                while(itr.hasNext()){
                                    final ScheduleControlListener listener
                                        = (ScheduleControlListener)itr.next();
                                    listener.changedControlState(id, controlState);
                                }
                            }
                        }
                    }catch(ScheduleStateControlException e){
                        try{
                            changeControlState(id, Schedule.CONTROL_STATE_FAILED);
                        }catch(ScheduleStateControlException e2){
                        }
                        getLogger().write(MSG_ID_CONTROL_STATE_CHECK_ERROR, getServiceNameObject(), e);
                    }
                }
            }catch(SQLException e){
                getLogger().write(MSG_ID_CONTROL_STATE_CHECK_ERROR, getServiceNameObject(), e);
                return;
            }finally{
                if(rs != null){
                    try{
                        rs.close();
                    }catch(SQLException e){
                    }
                }
                if(st != null){
                    try{
                        st.close();
                    }catch(SQLException e){
                    }
                }
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){
                    }
                }
            }
        }
        
        /**
         * �������Ȃ��B<p>
         */
        public void garbage(){
        }
    }
    
    /**
     * �^�C���I�[�o�[�Ď��B<p>
     *
     * @author M.Takata
     */
    protected class TimeoverChecker implements DaemonRunnable{
        
        /**
         * �f�[�������J�n�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onStart() {
            return true;
        }
        
        /**
         * �f�[��������~�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onStop() {
            return true;
        }
        
        /**
         * �f�[���������f�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onSuspend() {
            return true;
        }
        
        /**
         * �f�[�������ĊJ�������ɌĂяo�����B<p>
         *
         * @return ���true��Ԃ�
         */
        public boolean onResume() {
            return true;
        }
        
        /**
         * ��莞�ԋ󂯂�B<p>
         *
         * @param ctrl DaemonControl�I�u�W�F�N�g
         * @return �X�P�W���[���̔z��
         */
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(getTimeoverCheckInterval(), true);
            return null;
        }
        
        /**
         * �ő�x�����Ԃ��`�F�b�N����B<p>
         *
         * @param dequeued null
         * @param ctrl DaemonControl�I�u�W�F�N�g
         */
        public void consume(Object dequeued, DaemonControl ctrl)
         throws Throwable{
            Connection con = null;
            try{
                con = connectionFactory.getConnection();
            }catch(ConnectionFactoryException e){
                getLogger().write(MSG_ID_TIMEOVER_CHECK_ERROR, getServiceNameObject(), e);
                return;
            }
            Statement st = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                st = con.createStatement();
                final SimpleDateFormat format = new SimpleDateFormat(
                    dateFormat + timeFormat
                );
                final Calendar nowCal = Calendar.getInstance();
                rs = st.executeQuery(
                    "select " + scheduleTableSchema.id
                        + ',' + scheduleTableSchema.date
                        + ',' + scheduleTableSchema.time
                        + ',' + scheduleTableSchema.maxDelayTime
                        + ',' + scheduleTableSchema.state
                        + ',' + scheduleTableSchema.masterId
                        + " from " + scheduleTableSchema.table
                        + " where "
                        + scheduleTableSchema.checkState + "<>'"
                        + scheduleTableSchema.getCheckStateString(Schedule.CHECK_STATE_TIMEOVER)
                        + "' and " + scheduleTableSchema.maxDelayTime + " is not null"
                        + " and " + scheduleTableSchema.maxDelayTime + ">0"
                        + " and " + scheduleTableSchema.state + "<>'"
                        + scheduleTableSchema.getStateString(Schedule.STATE_END)
                        + "' and " + scheduleTableSchema.state + "<>'"
                        + scheduleTableSchema.getStateString(Schedule.STATE_DISABLE)
                        + "' and " + scheduleTableSchema.state + "<>'"
                        + scheduleTableSchema.getStateString(Schedule.STATE_FAILED)
                        + "' and " + scheduleTableSchema.state + "<>'"
                        + scheduleTableSchema.getStateString(Schedule.STATE_ABORT)
                        + "' and " + concatQuery(new StringBuffer(), scheduleTableSchema.date, scheduleTableSchema.time) + "<'"
                        + format.format(nowCal.getTime()) + '\''
                );
                final Calendar tmpCal = Calendar.getInstance();
                while(rs.next()){
                    final Date time = format.parse(rs.getString(2) + rs.getString(3));
                    tmpCal.clear();
                    final long maxDelayTime = rs.getLong(4);
                    tmpCal.setTimeInMillis(time.getTime() + maxDelayTime);
                    if(tmpCal.after(nowCal) || tmpCal.equals(nowCal)){
                        continue;
                    }
                    final String id = rs.getString(1);
                    if(ps == null){
                        String checkStateStr = scheduleTableSchema.getCheckStateString(Schedule.CHECK_STATE_TIMEOVER);
                        ps = con.prepareStatement(
                            "update " + scheduleTableSchema.table
                                + " set " + scheduleTableSchema.checkState + "='"
                                + checkStateStr + "',"
                                + scheduleTableSchema.updateUserId + "='" + updateUserId + "',"
                                + scheduleTableSchema.updateTime + "=?"
                                + " where " + scheduleTableSchema.id + "=?"
                                + " and " + scheduleTableSchema.checkState + "<>'" + checkStateStr + '\''
                        );
                    }
                    ps.setTimestamp(1, new Timestamp(DatabaseScheduleManagerService.this.time == null ? System.currentTimeMillis() : DatabaseScheduleManagerService.this.time.currentTimeMillis()));
                    ps.setString(2, id);
                    
                    if(ps.executeUpdate() != 0){
                        final String state = rs.getString(5);
                        final String masterId = rs.getString(6);
                        getLogger().write(
                            MSG_ID_TIMEOVER_ERROR,
                            new Object[]{getServiceNameObject(), id, masterId, state}
                        );
                    }
                }
            }catch(SQLException e){
                getLogger().write(MSG_ID_TIMEOVER_CHECK_ERROR, getServiceNameObject(), e);
                return;
            }finally{
                if(rs != null){
                    try{
                        rs.close();
                    }catch(SQLException e){
                    }
                }
                if(st != null){
                    try{
                        st.close();
                    }catch(SQLException e){
                    }
                }
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){
                    }
                }
            }
        }
        
        /**
         * �������Ȃ��B<p>
         */
        public void garbage(){
        }
    }
    
    protected class ClusterListener implements jp.ossc.nimbus.service.keepalive.ClusterListener{
        
        public void memberInit(Object myId, List members){}
        
        public void memberChange(List oldMembers, List newMembers){}
        
        public void changeMain() throws Exception{
            if(controlStateChecker != null){
                controlStateChecker.resume();
            }
            if(timeoverChecker != null){
                timeoverChecker.resume();
            }
        }
        
        public void changeSub(){
            if(controlStateChecker != null){
                controlStateChecker.suspend();
            }
            if(timeoverChecker != null){
                timeoverChecker.suspend();
            }
        }
    }
}