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
import java.text.SimpleDateFormat;

/**
 * �f�t�H���g�X�P�W���[���}�X�^�B<p>
 *
 * @author M.Takata
 */
public class DefaultScheduleMaster
 implements ScheduleMaster, java.io.Serializable, Comparable{
    
    private static final long serialVersionUID = -4016419359156060172L;
    
    protected String id;
    
    protected String[] groupIds;
    
    protected String taskName;
    
    protected String scheduleType;
    
    protected Object input;
    
    protected Date startTime;
    
    protected Date endTime;
    
    protected long repeatInterval;
    
    protected long retryInterval;
    
    protected Date retryEndTime;
    
    protected long maxDelayTime;
    
    protected boolean isEnabled = true;
    
    protected ScheduleDepends[] depends;
    
    protected Map dependsInGroupMap = new HashMap();
    
    protected ScheduleDepends[] dependsOnGroup;
    
    protected Map groupDependsOnGroupMap = new HashMap();
    
    protected String executorKey;
    
    protected String executorType;
    
    protected boolean isTemplate;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public DefaultScheduleMaster(){
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param id �X�P�W���[���}�X�^ID
     * @param groupIds �X�P�W���[���}�X�^�O���[�vID
     * @param taskName �^�X�N��
     * @param scheduleType �^�X�N���
     * @param input ���̓f�[�^
     * @param startTime �J�n����
     * @param isEnabled �L��/�����t���O
     * @param depends �X�P�W���[���ˑ����̔z��
     * @param dependsOnGroup �X�P�W���[���O���[�v�ˑ����̔z��
     * @param executorKey ScheduleExecutor����肷��L�[
     * @param executorType ScheduleExecutor�̎��
     * @param isTemplate �e���v���[�g���ǂ����B�e���v���[�g�̏ꍇ�Atrue
     */
    public DefaultScheduleMaster(
        String id,
        String[] groupIds,
        String taskName,
        String scheduleType,
        Object input,
        Date startTime,
        boolean isEnabled,
        ScheduleDepends[] depends,
        ScheduleDepends[] dependsOnGroup,
        String executorKey,
        String executorType,
        boolean isTemplate
    ){
        this(
            id,
            groupIds,
            taskName,
            scheduleType,
            input,
            startTime,
            null,
            0l,
            0l,
            null,
            0l,
            isEnabled,
            depends,
            dependsOnGroup,
            executorKey,
            executorType,
            isTemplate
        );
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param id �X�P�W���[���}�X�^ID
     * @param groupIds �X�P�W���[���}�X�^�O���[�vID
     * @param taskName �^�X�N��
     * @param scheduleType �^�X�N���
     * @param input ���̓f�[�^
     * @param startTime �J�n����
     * @param retryInterval ���g���C���s�Ԋu[ms]
     * @param retryEndTime ���g���C�I������
     * @param maxDelayTime �ő�x������[ms]
     * @param isEnabled �L��/�����t���O
     * @param depends �X�P�W���[���ˑ����̔z��
     * @param dependsOnGroup �X�P�W���[���O���[�v�ˑ����̔z��
     * @param executorKey ScheduleExecutor����肷��L�[
     * @param executorType ScheduleExecutor�̎��
     * @param isTemplate �e���v���[�g���ǂ����B�e���v���[�g�̏ꍇ�Atrue
     */
    public DefaultScheduleMaster(
        String id,
        String[] groupIds,
        String taskName,
        String scheduleType,
        Object input,
        Date startTime,
        long retryInterval,
        Date retryEndTime,
        long maxDelayTime,
        boolean isEnabled,
        ScheduleDepends[] depends,
        ScheduleDepends[] dependsOnGroup,
        String executorKey,
        String executorType,
        boolean isTemplate
    ){
        this(
            id,
            groupIds,
            taskName,
            scheduleType,
            input,
            startTime,
            null,
            0l,
            retryInterval,
            retryEndTime,
            maxDelayTime,
            isEnabled,
            depends,
            dependsOnGroup,
            executorKey,
            executorType,
            isTemplate
        );
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param id �X�P�W���[���}�X�^ID
     * @param groupIds �X�P�W���[���}�X�^�O���[�vID
     * @param taskName �^�X�N��
     * @param scheduleType �^�X�N���
     * @param input ���̓f�[�^
     * @param startTime �J�n����
     * @param endTime �I������
     * @param repeatInterval �J��Ԃ����s�Ԋu[ms]
     * @param retryInterval ���g���C���s�Ԋu[ms]
     * @param retryEndTime ���g���C�I������
     * @param maxDelayTime �ő�x������[ms]
     * @param isEnabled �L��/�����t���O
     * @param depends �X�P�W���[���ˑ����̔z��
     * @param dependsOnGroup �X�P�W���[���O���[�v�ˑ����̔z��
     * @param executorKey ScheduleExecutor����肷��L�[
     * @param executorType ScheduleExecutor�̎��
     * @param isTemplate �e���v���[�g���ǂ����B�e���v���[�g�̏ꍇ�Atrue
     */
    public DefaultScheduleMaster(
        String id,
        String[] groupIds,
        String taskName,
        String scheduleType,
        Object input,
        Date startTime,
        Date endTime,
        long repeatInterval,
        long retryInterval,
        Date retryEndTime,
        long maxDelayTime,
        boolean isEnabled,
        ScheduleDepends[] depends,
        ScheduleDepends[] dependsOnGroup,
        String executorKey,
        String executorType,
        boolean isTemplate
    ){
        setId(id);
        setGroupIds(groupIds);
        setTaskName(taskName);
        setScheduleType(scheduleType);
        setInput(input);
        setStartTime(startTime);
        setEndTime(endTime);
        setRepeatInterval(repeatInterval);
        setRetryInterval(retryInterval);
        setRetryEndTime(retryEndTime);
        setMaxDelayTime(maxDelayTime);
        setEnabled(isEnabled);
        setDepends(depends);
        setDependsOnGroup(dependsOnGroup);
        setExecutorKey(executorKey);
        setExecutorType(executorType);
        setTemplate(isTemplate);
    }
    
    // ScheduleMaster��JavaDoc
    public String getId(){
        return id;
    }
    
    /**
     * �X�P�W���[���}�X�^��ID��ݒ肷��B<p>
     *
     * @param id �X�P�W���[���}�X�^��ID
     */
    public void setId(String id){
        this.id = id;
    }
    
    // ScheduleMaster��JavaDoc
    public String[] getGroupIds(){
        return groupIds;
    }
    
    /**
     * �X�P�W���[���}�X�^�̃O���[�vID��ݒ肷��B<p>
     *
     * @param id �X�P�W���[���}�X�^�̃O���[�vID
     */
    public void setGroupIds(String[] id){
        this.groupIds = id;
    }
    
    // ScheduleMaster��JavaDoc
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
    
    // ScheduleMaster��JavaDoc
    public String getScheduleType(){
        return scheduleType;
    }
    
    /**
     * �X�P�W���[�����ꂽ�X�P�W���[����ʂ�ݒ肷��B<p>
     *
     * @param type �X�P�W���[�����
     */
    public void setScheduleType(String type){
        scheduleType = type;
    }
    
    // ScheduleMaster��JavaDoc
    public Object getInput(){
        return input;
    }
    
    // ScheduleMaster��JavaDoc
    public void setInput(Object data){
        input = data;
    }
    
    // ScheduleMaster��JavaDoc
    public Date getStartTime(){
        return startTime;
    }
    
    // ScheduleMaster��JavaDoc
    public void setStartTime(Date time){
        startTime = time;
    }
    
    // ScheduleMaster��JavaDoc
    public Date getEndTime(){
        return endTime;
    }
    
    // ScheduleMaster��JavaDoc
    public void setEndTime(Date time){
        endTime = time;
    }
    
    // ScheduleMaster��JavaDoc
    public long getRepeatInterval(){
        return repeatInterval;
    }
    
    // ScheduleMaster��JavaDoc
    public void setRepeatInterval(long interval){
        repeatInterval = interval;
    }
    
    // ScheduleMaster��JavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // ScheduleMaster��JavaDoc
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    
    // ScheduleMaster��JavaDoc
    public Date getRetryEndTime(){
        return retryEndTime;
    }
    
    // ScheduleMaster��JavaDoc
    public void setRetryEndTime(Date time){
        retryEndTime = time;
    }
    
    // ScheduleMaster��JavaDoc
    public long getMaxDelayTime(){
        return maxDelayTime;
    }
    
    // ScheduleMaster��JavaDoc
    public void setMaxDelayTime(long time){
        maxDelayTime = time;
    }
    
    // ScheduleMaster��JavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // ScheduleMaster��JavaDoc
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
    
    // ScheduleMaster��JavaDoc
    public ScheduleDepends[] getDepends(){
        return depends;
    }
    
    /**
     * �ˑ�����ݒ肷��B<p>
     *
     * @param deps �X�P�W���[���ˑ����̔z��
     */
    public void setDepends(ScheduleDepends[] deps){
        depends = deps;
    }
    
    // ScheduleMaster��JavaDoc
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
    
    // ScheduleMaster��JavaDoc
    public Map getDependsInGroupMap(){
        return dependsInGroupMap;
    }
    
    // ScheduleMaster��JavaDoc
    public ScheduleDepends[] getDependsOnGroup(){
        return dependsOnGroup;
    }
    
    /**
     * �O���[�v�ˑ�����ݒ肷��B<p>
     *
     * @param deps �ˑ����̔z��
     */
    public void setDependsOnGroup(ScheduleDepends[] deps){
        dependsOnGroup = deps;
    }
    
    // ScheduleMaster��JavaDoc
    public ScheduleDepends[] getGroupDependsOnGroup(String groupId){
        return (ScheduleDepends[])groupDependsOnGroupMap.get(groupId);
    }
    
    /**
     * �X�P�W���[����������O���[�v�ƈˑ�����O���[�v�̈ˑ�����ݒ肷��B<p>
     *
     * @param groupId �X�P�W���[����������O���[�vID
     * @param depends �ˑ�����O���[�v�̈ˑ����̔z��
     */
    public void setGroupDependsOnGroup(String groupId, ScheduleDepends[] depends){
        groupDependsOnGroupMap.put(groupId, depends);
    }
    
    // ScheduleMaster��JavaDoc
    public Map getGroupDependsOnGroupMap(){
        return groupDependsOnGroupMap;
    }
    
    // ScheduleMaster��JavaDoc
    public void setExecutorKey(String key){
        executorKey = key;
    }
    
    // ScheduleMaster��JavaDoc
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
    
    // ScheduleMaster��JavaDoc
    public boolean isTemplate(){
        return isTemplate;
    }
    
    // ScheduleMaster��JavaDoc
    public void setTemplate(boolean isTemplate){
        this.isTemplate = isTemplate;
    }
    
    // ScheduleMaster��JavaDoc
    public void applyDate(Date date){
        final Calendar cal = Calendar.getInstance();
        if(startTime != null){
            startTime = applyDateToTime(date, startTime, cal);
        }
        if(endTime != null){
            endTime = applyDateToTime(date, endTime, cal);
        }
        if(retryEndTime != null){
            retryEndTime = applyDateToTime(date, retryEndTime, cal);
        }
    }
    
    /**
     * time��date�̓��t��K�p����B<p>
     *
     * @param date ���t
     * @param time ����
     * @param work ��Ɨp�J�����_�[
     * @return date�̓��t��time�̎�����������Date
     */
    protected Date applyDateToTime(Date date, Date time, Calendar work){
        work.clear();
        work.setTime(date);
        final int year = work.get(Calendar.YEAR);
        final int day = work.get(Calendar.DAY_OF_YEAR);
        work.clear();
        work.setTime(time);
        work.set(Calendar.YEAR, year);
        work.set(
            Calendar.DAY_OF_YEAR,
            day + (work.get(Calendar.DAY_OF_YEAR) - 1)
        );
        return work.getTime();
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
        if(!(obj instanceof ScheduleMaster)){
            return false;
        }
        ScheduleMaster cmp = (ScheduleMaster)obj;
        return (id == null && cmp.getId() == null)
            || (id != null && id.equals(cmp.getId()));
    }
    
    // Comparable��JavaDoc
    public int compareTo(Object o){
        if(o == null || !(o instanceof ScheduleMaster)){
            return -1;
        }
        if(o == this){
            return 0;
        }
        final ScheduleMaster cmp = (ScheduleMaster)o;
        if(startTime != null && cmp.getStartTime() == null){
            return -1;
        }
        if(startTime == null && cmp.getStartTime() != null){
            return 1;
        }
        if(startTime != null && cmp.getStartTime() != null){
            int result = startTime.compareTo(cmp.getStartTime());
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
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(",groupIds=");
        if(groupIds == null || groupIds.length == 0){
            buf.append((Object)null);
        }else{
            buf.append('[');
            for(int i = 0; i < groupIds.length; i++){
                buf.append(groupIds[i]);
                if(i != groupIds.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append(",taskName=").append(taskName);
        buf.append(",scheduleType=").append(scheduleType);
        buf.append(",input=").append(input);
        buf.append(",startTime=")
            .append(startTime == null ? null : format.format(startTime));
        buf.append(",endTime=")
            .append(endTime == null ? null : format.format(endTime));
        buf.append(",repeatInterval=").append(repeatInterval);
        buf.append(",retryInterval=").append(retryInterval);
        buf.append(",retryEndTime=")
            .append(retryEndTime == null ? null : format.format(retryEndTime));
        buf.append(",maxDelayTime=").append(maxDelayTime);
        buf.append(",isEnabled=").append(isEnabled);
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
        
        buf.append(",executorKey=").append(executorKey);
        buf.append(",executorType=").append(executorType);
        buf.append(",isTemplate=").append(isTemplate);
        buf.append('}');
        return buf.toString();
    }
    
    public Object clone(){
        DefaultScheduleMaster master = null;
        try{
            master = (DefaultScheduleMaster)super.clone();
        }catch(CloneNotSupportedException e){
        }
        master.startTime = startTime == null ? null : (Date)startTime.clone();
        master.endTime = endTime == null ? null : (Date)endTime.clone();
        master.retryEndTime = retryEndTime == null ? null : (Date)retryEndTime.clone();
        return master;
    }
}