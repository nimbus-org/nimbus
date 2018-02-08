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

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * �f�t�H���g�X�P�W���[���B<p>
 *
 * @author M.Takata
 */
public class DefaultSchedule
 implements Schedule, Serializable, Comparable{
    
    private static final long serialVersionUID = -2668833951199708052L;
    
    protected String id;
    
    protected Map groupMap = new HashMap();
    
    protected String masterId;
    
    protected String[] masterGroupIds;
    
    protected Date time;
    
    protected String taskName;
    
    protected Object input;
    
    protected ScheduleDepends[] depends;
    
    protected Map dependsInGroupMasterMap = new HashMap();
    
    protected Map dependsInGroupMap = new HashMap();
    
    protected ScheduleDepends[] dependsOnGroup;
    
    protected Map groupDependsOnGroupMasterMap = new HashMap();
    
    protected Map groupDependsOnGroupMap = new HashMap();
    
    protected Object output;
    
    protected boolean isRetry;
    
    protected Date initialTime;
    
    protected long retryInterval;
    
    protected Date retryEndTime;
    
    protected long maxDelayTime;
    
    protected int state = Schedule.STATE_INITIAL;
    
    protected int controlState = Schedule.CONTROL_STATE_INITIAL;
    
    protected int checkState = Schedule.CHECK_STATE_INITIAL;
    
    protected String executorKey;
    
    protected String executorType;
    
    protected Date executeStartTime;
    
    protected Date executeEndTime;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public DefaultSchedule(){
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param id �X�P�W���[��ID
     */
    public DefaultSchedule(String id){
        setId(id);
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param time �X�P�W���[������
     */
    public DefaultSchedule(Date time){
        setTime(time);
        setInitialTime(time);
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param masterId �X�P�W���[���}�X�^ID
     * @param masterGroupIds �X�P�W���[���}�X�^�O���[�vID
     * @param time �X�P�W���[������
     * @param taskName �^�X�N��
     * @param input ���̓f�[�^
     * @param depends �X�P�W���[���ˑ����̔z��
     * @param dependsInGroupMap �O���[�v���̃X�P�W���[���ˑ����}�b�v
     * @param dependsOnGroup �X�P�W���[���ƃO���[�v�̈ˑ����̔z��
     * @param groupDependsOnGroupMap �X�P�W���[����������O���[�v�ƈˑ�����O���[�v�̈ˑ����̔z��
     * @param executorKey ScheduleExecutor����肷��L�[
     * @param executorType ScheduleExecutor�̎��
     */
    public DefaultSchedule(
        String masterId,
        String[] masterGroupIds,
        Date time,
        String taskName,
        Object input,
        ScheduleDepends[] depends,
        Map dependsInGroupMap,
        ScheduleDepends[] dependsOnGroup,
        Map groupDependsOnGroupMap,
        String executorKey,
        String executorType
    ){
        this(masterId, masterGroupIds, time, taskName, input, depends, dependsInGroupMap, dependsOnGroup, groupDependsOnGroupMap, executorKey, executorType, 0, null, 0);
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param masterId �X�P�W���[���}�X�^ID
     * @param masterGroupIds �X�P�W���[���}�X�^�O���[�vID
     * @param time �X�P�W���[������
     * @param taskName �^�X�N��
     * @param input ���̓f�[�^
     * @param depends �X�P�W���[���ˑ����̔z��
     * @param dependsInGroupMap �O���[�v���̃X�P�W���[���ˑ����}�b�v
     * @param dependsOnGroup �X�P�W���[���ƃO���[�v�̈ˑ����̔z��
     * @param groupDependsOnGroupMap �X�P�W���[����������O���[�v�ƈˑ�����O���[�v�̈ˑ����̔z��
     * @param executorKey ScheduleExecutor����肷��L�[
     * @param executorType ScheduleExecutor�̎��
     * @param retryInterval ���g���C�Ԋu[ms]
     * @param retryEndTime ���g���C�I������
     * @param maxDelayTime �ő�x������[ms]
     */
    public DefaultSchedule(
        String masterId,
        String[] masterGroupIds,
        Date time,
        String taskName,
        Object input,
        ScheduleDepends[] depends,
        Map dependsInGroupMap,
        ScheduleDepends[] dependsOnGroup,
        Map groupDependsOnGroupMap,
        String executorKey,
        String executorType,
        long retryInterval,
        Date retryEndTime,
        long maxDelayTime
    ){
        setMasterId(masterId);
        setMasterGroupIds(masterGroupIds);
        setTime(time);
        setTaskName(taskName);
        setInput(input);
        setDepends(depends);
        if(dependsInGroupMap != null){
            getDependsInGroupMasterMap().putAll(dependsInGroupMap);
        }
        setDependsOnGroup(dependsOnGroup);
        if(groupDependsOnGroupMap != null){
            getGroupDependsOnGroupMasterMap().putAll(groupDependsOnGroupMap);
        }
        setExecutorKey(executorKey);
        setExecutorType(executorType);
        setInitialTime(time);
        setRetryInterval(retryInterval);
        setRetryEndTime(retryEndTime);
        setMaxDelayTime(maxDelayTime);
    }
    
    // Schedule��JavaDoc
    public String getId(){
        return id;
    }
    
    // Schedule��JavaDoc
    public void setId(String id){
        this.id = id;
    }
    
    // Schedule��JavaDoc
    public String getGroupId(String masterGroupId){
        return (String)groupMap.get(masterGroupId);
    }
    
    /**
     * �w�肵���}�X�^�O���[�vID�ɑ΂���O���[�vID��ݒ肷��B<p>
     *
     * @param masterGroupId �}�X�^�O���[�vID
     * @param groupId �O���[�vID
     */
    public void setGroupId(String masterGroupId, String groupId){
        groupMap.put(masterGroupId, groupId);
    }
    
    // Schedule��JavaDoc
    public Map getGroupIdMap(){
        return groupMap;
    }
    
    // Schedule��JavaDoc
    public String getMasterId(){
        return masterId;
    }
    
    /**
     * �X�P�W���[���}�X�^��ID��ݒ肷��B<p>
     *
     * @param id �X�P�W���[���}�X�^ID
     */
    public void setMasterId(String id){
        masterId = id;
    }
    
    // Schedule��JavaDoc
    public String[] getMasterGroupIds(){
        return masterGroupIds;
    }
    
    /**
     * �X�P�W���[���}�X�^�̃O���[�vID��ݒ肷��B<p>
     *
     * @param id �X�P�W���[���}�X�^�O���[�vID
     */
    public void setMasterGroupIds(String[] id){
        masterGroupIds = id;
    }
    
    // Schedule��JavaDoc
    public Date getTime(){
        return time;
    }
    
    // Schedule��JavaDoc
    public void setTime(Date time){
        this.time = time;
    }
    
    // Schedule��JavaDoc
    public String getTaskName(){
        return taskName;
    }
    
    /**
     * �X�P�W���[�����ꂽ�^�X�N����ݒ肷��B<p>
     *
     * @param name �^�X�N��
     */
    public void setTaskName(String name){
        taskName = name;
    }
    
    // Schedule��JavaDoc
    public Object getInput(){
        return input;
    }
    
    /**
     * �X�P�W���[���̓��̓f�[�^��ݒ肷��B<p>
     *
     * @param data ���̓f�[�^
     */
    public void setInput(Object data){
        input = data;
    }
    
    // Schedule��JavaDoc
    public ScheduleDepends[] getDepends(){
        return depends;
    }
    
    /**
     * �ˑ�����X�P�W���[���̈ˑ�����ݒ肷��B<p>
     *
     * @param deps �ˑ����̔z��
     */
    public void setDepends(ScheduleDepends[] deps){
        depends = deps;
    }
    
    // Schedule��JavaDoc
    public ScheduleDepends[] getDependsInGroupMaster(String masterGroupId){
        return (ScheduleDepends[])dependsInGroupMasterMap.get(masterGroupId);
    }
    
    /**
     * �O���[�v���ł̃X�P�W���[���̈ˑ�����ݒ肷��B<p>
     *
     * @param masterGroupId �}�X�^�O���[�vID
     * @param depends �X�P�W���[���ˑ����̔z��
     */
    public void setDependsInGroupMaster(String masterGroupId, ScheduleDepends[] depends){
        dependsInGroupMasterMap.put(masterGroupId, depends);
    }
    
    // Schedule��JavaDoc
    public Map getDependsInGroupMasterMap(){
        return dependsInGroupMasterMap;
    }
    
    // Schedule��JavaDoc
    public ScheduleDepends[] getDependsInGroup(String groupId){
        return (ScheduleDepends[])dependsInGroupMap.get(groupId);
    }
    
    /**
     * �O���[�v���ł̃X�P�W���[���̈ˑ�����ݒ肷��B<p>
     *
     * @param groupId �O���[�vID
     * @param depends �X�P�W���[���ˑ����̔z��
     */
    public void setDependsInGroup(String groupId, ScheduleDepends[] depends){
        dependsInGroupMap.put(groupId, depends);
    }
    
    // Schedule��JavaDoc
    public Map getDependsInGroupMap(){
        return dependsInGroupMap;
    }
    
    // Schedule��JavaDoc
    public ScheduleDepends[] getDependsOnGroup(){
        return dependsOnGroup;
    }
    
    /**
     * �O���[�v�ˑ�����ݒ肷��B<p>
     *
     * @param deps �O���[�v�ˑ����̔z��
     */
    public void setDependsOnGroup(ScheduleDepends[] deps){
        dependsOnGroup = deps;
    }
    
    // Schedule��JavaDoc
    public ScheduleDepends[] getGroupDependsOnGroupMaster(String masterGroupId){
        return (ScheduleDepends[])groupDependsOnGroupMasterMap.get(masterGroupId);
    }
    
    /**
     * �X�P�W���[����������}�X�^�O���[�v�ƈˑ�����O���[�v�̈ˑ�����ݒ肷��B<p>
     *
     * @param masterGroupId �}�X�^�O���[�vID
     * @param depends �ˑ�����O���[�v�̈ˑ����̔z��
     */
    public void setGroupDependsOnGroupMaster(String masterGroupId, ScheduleDepends[] depends){
        groupDependsOnGroupMasterMap.put(masterGroupId, depends);
    }
    
    // Schedule��JavaDoc
    public Map getGroupDependsOnGroupMasterMap(){
        return groupDependsOnGroupMasterMap;
    }
    
    // Schedule��JavaDoc
    public ScheduleDepends[] getGroupDependsOnGroup(String groupId){
        return (ScheduleDepends[])groupDependsOnGroupMap.get(groupId);
    }
    
    /**
     * �X�P�W���[����������O���[�v�ƈˑ�����O���[�v�̈ˑ�����ݒ肷��B<p>
     *
     * @param groupId �O���[�vID
     * @param depends �ˑ�����O���[�v�̈ˑ����̔z��
     */
    public void setGroupDependsOnGroup(String groupId, ScheduleDepends[] depends){
        groupDependsOnGroupMap.put(groupId, depends);
    }
    
    // Schedule��JavaDoc
    public Map getGroupDependsOnGroupMap(){
        return groupDependsOnGroupMap;
    }
    
    // Schedule��JavaDoc
    public Object getOutput(){
        return output;
    }
    
    // Schedule��JavaDoc
    public void setOutput(Object out){
        output = out;
    }
    
    // Schedule��JavaDoc
    public Date getInitialTime(){
        return initialTime == null ? time : initialTime;
    }
    
    /**
     * �ŏ��ɃX�P�W���[�����ꂽ������ݒ肷��B<p>
     *
     * @param time �ŏ��ɃX�P�W���[�����ꂽ����
     */
    public void setInitialTime(Date time){
        initialTime = time;
    }
    
    // Schedule��JavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    /**
     * �X�P�W���[�����g���C���s�Ԋu[ms]��ݒ肷��B<p>
     *
     * @param interval ���g���C���s�Ԋu
     */
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    
    // Schedule��JavaDoc
    public Date getRetryEndTime(){
        return retryEndTime;
    }
    
    /**
     * �X�P�W���[�����g���C�I��������ݒ肷��B<p>
     *
     * @param time �X�P�W���[�����g���C�I������
     */
    public void setRetryEndTime(Date time){
        retryEndTime = time;
    }
    
    // Schedule��JavaDoc
    public boolean isRetry(){
        return isRetry;
    }
    
    // Schedule��JavaDoc
    public void setRetry(boolean retry){
        isRetry = retry;
    }
    
    // Schedule��JavaDoc
    public long getMaxDelayTime(){
        return maxDelayTime;
    }
    
    /**
     * �X�P�W���[���̍ő�x������[ms]��ݒ肷��B<p>
     *
     * @param time �X�P�W���[���ő�x������
     */
    public void setMaxDelayTime(long time){
        maxDelayTime = time;
    }
    
    // Schedule��JavaDoc
    public int getState(){
        return state;
    }
    
    // Schedule��JavaDoc
    public void setState(int state){
        this.state = state;
    }
    
    // Schedule��JavaDoc
    public int getControlState(){
        return controlState;
    }
    
    // Schedule��JavaDoc
    public void setControlState(int state){
        controlState = state;
    }
    
    // Schedule��JavaDoc
    public int getCheckState(){
        return checkState;
    }
    
    // Schedule��JavaDoc
    public void setCheckState(int state){
        checkState = state;
    }
    
    // Schedule��JavaDoc
    public void setExecutorKey(String key){
        executorKey = key;
    }
    
    // Schedule��JavaDoc
    public String getExecutorKey(){
        return executorKey;
    }
    
    // ScheduleMaster��JavaDoc
    public void setExecutorType(String type){
        executorType = type;
    }
    
    // ScheduleMaster��JavaDoc
    public String getExecutorType(){
        return executorType;
    }
    
    // Schedule��JavaDoc
    public Date getExecuteStartTime(){
        return executeStartTime;
    }
    
    // Schedule��JavaDoc
    public void setExecuteStartTime(Date time){
        executeStartTime = time;
    }
    
    // Schedule��JavaDoc
    public Date getExecuteEndTime(){
        return executeEndTime;
    }
    
    // Schedule��JavaDoc
    public void setExecuteEndTime(Date time){
        executeEndTime = time;
    }
    
    /**
     * �n�b�V���l���擾����B<p>
     *
     * @return �n�b�V���l
     */
    public int hashCode(){
        return id == null ? 0 : id.hashCode();
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g�����̃C���X�^���X�Ɠ��������ǂ����𔻒肷��B<p>
     *
     * @param obj ��r�Ώۂ̃I�u�W�F�N�g
     * @return �w�肳�ꂽ�I�u�W�F�N�g�����̃C���X�^���X�Ɠ������ꍇtrue
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof Schedule)){
            return false;
        }
        Schedule cmp = (Schedule)obj;
        return (id == null && cmp.getId() == null)
            || (id != null && id.equals(cmp.getId()));
    }
    
    // Comparable��JavaDoc
    public int compareTo(Object o){
        if(o == null || !(o instanceof Schedule)){
            return -1;
        }
        if(o == this){
            return 0;
        }
        final Schedule cmp = (Schedule)o;
        if(time != null && cmp.getTime() == null){
            return -1;
        }
        if(time == null && cmp.getTime() != null){
            return 1;
        }
        if(time != null && cmp.getTime() != null){
            int result = time.compareTo(cmp.getTime());
            if(result != 0){
                return result;
            }
        }
        if(id == null && cmp.getId() == null){
            return 0;
        }
        if(id != null && cmp.getId() == null){
            return -1;
        }
        if(id == null && cmp.getId() != null){
            return 1;
        }
        return id.compareTo(cmp.getId());
    }
    
    /**
     * ���̃C���X�^���X�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final SimpleDateFormat format
            = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
        final StringBuffer buf = new StringBuffer(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(",masterId=").append(masterId);
        buf.append(",masterGroupIds=");
        if(masterGroupIds == null || masterGroupIds.length == 0){
            buf.append((Object)null);
        }else{
            buf.append('[');
            for(int i = 0; i < masterGroupIds.length; i++){
                buf.append(masterGroupIds[i]);
                if(i != masterGroupIds.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append(",groupIdMap=").append(groupMap);
        buf.append(",time=").append(time == null ? null : format.format(time));
        buf.append(",taskName=").append(taskName);
        buf.append(",input=").append(input);
        buf.append(",depends=");
        if(depends == null || depends.length == 0){
            buf.append((Object)null);
        }else{
            buf.append('[');
            for(int i = 0; i < depends.length; i++){
                buf.append('{');
                buf.append("masterId=").append(depends[i].getMasterId());
                buf.append(",isIgnoreError=").append(depends[i].isIgnoreError());
                buf.append('}');
                if(i != depends.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append(",dependsInGroupMap=");
        if(dependsInGroupMap.size() == 0){
            buf.append((Object)null);
        }else{
            buf.append('{');
            Iterator entries = dependsInGroupMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                buf.append(entry.getKey()).append('=');
                ScheduleDepends[] deps = (ScheduleDepends[])entry.getValue();
                buf.append('[');
                for(int i = 0; i < deps.length; i++){
                    buf.append('{');
                    buf.append("masterId=").append(deps[i].getMasterId());
                    buf.append(",isIgnoreError=").append(deps[i].isIgnoreError());
                    buf.append('}');
                    if(i != deps.length - 1){
                        buf.append(',');
                    }
                }
                buf.append(']');
                if(entries.hasNext()){
                    buf.append(',');
                }
            }
            buf.append('}');
        }
        buf.append(",dependsOnGroup=");
        if(dependsOnGroup == null || dependsOnGroup.length == 0){
            buf.append((Object)null);
        }else{
            buf.append('[');
            for(int i = 0; i < dependsOnGroup.length; i++){
                buf.append('{');
                buf.append("masterId=").append(dependsOnGroup[i].getMasterId());
                buf.append(",isIgnoreError=").append(dependsOnGroup[i].isIgnoreError());
                buf.append('}');
                if(i != dependsOnGroup.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append(",groupDependsOnGroupMap=");
        if(groupDependsOnGroupMap.size() == 0){
            buf.append((Object)null);
        }else{
            buf.append('{');
            Iterator entries = groupDependsOnGroupMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                buf.append(entry.getKey()).append('=');
                ScheduleDepends[] deps = (ScheduleDepends[])entry.getValue();
                buf.append('[');
                for(int i = 0; i < deps.length; i++){
                    buf.append('{');
                    buf.append("masterId=").append(deps[i].getMasterId());
                    buf.append(",isIgnoreError=").append(deps[i].isIgnoreError());
                    buf.append('}');
                    if(i != deps.length - 1){
                        buf.append(',');
                    }
                }
                buf.append(']');
                if(entries.hasNext()){
                    buf.append(',');
                }
            }
            buf.append('}');
        }
        buf.append(",output=").append(output);
        buf.append(",isRetry=").append(isRetry);
        buf.append(",initialTime=").append(initialTime == null ? null : format.format(initialTime));
        buf.append(",retryInterval=").append(retryInterval);
        buf.append(",retryEndTime=")
            .append(retryEndTime == null ? null : format.format(retryEndTime));
        buf.append(",maxDelayTime=").append(maxDelayTime);
        buf.append(",state=").append(state);
        buf.append(",controlState=").append(controlState);
        buf.append(",checkState=").append(checkState);
        buf.append(",executorKey=").append(executorKey);
        buf.append(",executorType=").append(executorType);
        buf.append(",executeStartTime=")
            .append(executeStartTime == null ? null : format.format(executeStartTime));
        buf.append(",executeEndTime=")
            .append(executeEndTime == null ? null : format.format(executeEndTime));
        buf.append('}');
        return buf.toString();
    }
}