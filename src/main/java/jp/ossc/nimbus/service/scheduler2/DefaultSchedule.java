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
 * デフォルトスケジュール。<p>
 *
 * @author M.Takata
 */
public class DefaultSchedule
 implements Schedule, Serializable, Comparable, Cloneable{
    
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
    
    protected long repeatInterval;
    
    protected Date repeatEndTime;
    
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
     * 空のインスタンスを生成する。<p>
     */
    public DefaultSchedule(){
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param id スケジュールID
     */
    public DefaultSchedule(String id){
        setId(id);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param time スケジュール時刻
     */
    public DefaultSchedule(Date time){
        setTime(time);
        setInitialTime(time);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param masterId スケジュールマスタID
     * @param masterGroupIds スケジュールマスタグループID
     * @param time スケジュール時刻
     * @param taskName タスク名
     * @param input 入力データ
     * @param depends スケジュール依存情報の配列
     * @param dependsInGroupMap グループ内のスケジュール依存情報マップ
     * @param dependsOnGroup スケジュールとグループの依存情報の配列
     * @param groupDependsOnGroupMap スケジュールが属するグループと依存するグループの依存情報の配列
     * @param executorKey ScheduleExecutorを特定するキー
     * @param executorType ScheduleExecutorの種類
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
        this(masterId, masterGroupIds, time, taskName, input, depends, dependsInGroupMap, dependsOnGroup, groupDependsOnGroupMap, executorKey, executorType, 0, null, 0, null, 0);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param masterId スケジュールマスタID
     * @param masterGroupIds スケジュールマスタグループID
     * @param time スケジュール時刻
     * @param taskName タスク名
     * @param input 入力データ
     * @param depends スケジュール依存情報の配列
     * @param dependsInGroupMap グループ内のスケジュール依存情報マップ
     * @param dependsOnGroup スケジュールとグループの依存情報の配列
     * @param groupDependsOnGroupMap スケジュールが属するグループと依存するグループの依存情報の配列
     * @param executorKey ScheduleExecutorを特定するキー
     * @param executorType ScheduleExecutorの種類
     * @param repeatInterval 繰り返し間隔[ms]
     * @param repeatEndTime 繰り返し終了時刻
     * @param retryInterval リトライ間隔[ms]
     * @param retryEndTime リトライ終了時刻
     * @param maxDelayTime 最大遅延時間[ms]
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
        long repeatInterval,
        Date repeatEndTime,
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
        setRepeatInterval(repeatInterval);
        setRepeatEndTime(repeatEndTime);
        setRetryInterval(retryInterval);
        setRetryEndTime(retryEndTime);
        setMaxDelayTime(maxDelayTime);
    }
    
    // ScheduleのJavaDoc
    public String getId(){
        return id;
    }
    
    // ScheduleのJavaDoc
    public void setId(String id){
        this.id = id;
    }
    
    // ScheduleのJavaDoc
    public String getGroupId(String masterGroupId){
        return (String)groupMap.get(masterGroupId);
    }
    
    /**
     * 指定したマスタグループIDに対するグループIDを設定する。<p>
     *
     * @param masterGroupId マスタグループID
     * @param groupId グループID
     */
    public void setGroupId(String masterGroupId, String groupId){
        groupMap.put(masterGroupId, groupId);
    }
    
    // ScheduleのJavaDoc
    public Map getGroupIdMap(){
        return groupMap;
    }
    public void setGroupIdMap(Map map){
        if(map == null){
            groupMap.clear();
        }else{
            groupMap.putAll(map);
        }
    }
    
    // ScheduleのJavaDoc
    public String getMasterId(){
        return masterId;
    }
    
    /**
     * スケジュールマスタのIDを設定する。<p>
     *
     * @param id スケジュールマスタID
     */
    public void setMasterId(String id){
        masterId = id;
    }
    
    // ScheduleのJavaDoc
    public String[] getMasterGroupIds(){
        return masterGroupIds;
    }
    
    /**
     * スケジュールマスタのグループIDを設定する。<p>
     *
     * @param id スケジュールマスタグループID
     */
    public void setMasterGroupIds(String[] id){
        masterGroupIds = id;
    }
    
    // ScheduleのJavaDoc
    public Date getTime(){
        return time;
    }
    
    // ScheduleのJavaDoc
    public void setTime(Date time){
        this.time = time;
    }
    
    // ScheduleのJavaDoc
    public String getTaskName(){
        return taskName;
    }
    
    /**
     * スケジュールされたタスク名を設定する。<p>
     *
     * @param name タスク名
     */
    public void setTaskName(String name){
        taskName = name;
    }
    
    // ScheduleのJavaDoc
    public Object getInput(){
        return input;
    }
    
    /**
     * スケジュールの入力データを設定する。<p>
     *
     * @param data 入力データ
     */
    public void setInput(Object data){
        input = data;
    }
    
    // ScheduleのJavaDoc
    public ScheduleDepends[] getDepends(){
        return depends;
    }
    
    /**
     * 依存するスケジュールの依存情報を設定する。<p>
     *
     * @param deps 依存情報の配列
     */
    public void setDepends(ScheduleDepends[] deps){
        depends = deps;
    }
    
    // ScheduleのJavaDoc
    public ScheduleDepends[] getDependsInGroupMaster(String masterGroupId){
        return (ScheduleDepends[])dependsInGroupMasterMap.get(masterGroupId);
    }
    
    /**
     * グループ内でのスケジュールの依存情報を設定する。<p>
     *
     * @param masterGroupId マスタグループID
     * @param depends スケジュール依存情報の配列
     */
    public void setDependsInGroupMaster(String masterGroupId, ScheduleDepends[] depends){
        dependsInGroupMasterMap.put(masterGroupId, depends);
    }
    
    // ScheduleのJavaDoc
    public Map getDependsInGroupMasterMap(){
        return dependsInGroupMasterMap;
    }
    public void setDependsInGroupMasterMap(Map map){
        if(map == null){
            dependsInGroupMasterMap.clear();
        }else{
            dependsInGroupMasterMap.putAll(map);
        }
    }
    
    // ScheduleのJavaDoc
    public ScheduleDepends[] getDependsInGroup(String groupId){
        return (ScheduleDepends[])dependsInGroupMap.get(groupId);
    }
    
    /**
     * グループ内でのスケジュールの依存情報を設定する。<p>
     *
     * @param groupId グループID
     * @param depends スケジュール依存情報の配列
     */
    public void setDependsInGroup(String groupId, ScheduleDepends[] depends){
        dependsInGroupMap.put(groupId, depends);
    }
    
    // ScheduleのJavaDoc
    public Map getDependsInGroupMap(){
        return dependsInGroupMap;
    }
    
    public void setDependsInGroupMap(Map map){
        if(map == null){
            dependsInGroupMap.clear();
        }else{
            dependsInGroupMap.putAll(map);
        }
    }
    
    // ScheduleのJavaDoc
    public ScheduleDepends[] getDependsOnGroup(){
        return dependsOnGroup;
    }
    
    /**
     * グループ依存情報を設定する。<p>
     *
     * @param deps グループ依存情報の配列
     */
    public void setDependsOnGroup(ScheduleDepends[] deps){
        dependsOnGroup = deps;
    }
    
    // ScheduleのJavaDoc
    public ScheduleDepends[] getGroupDependsOnGroupMaster(String masterGroupId){
        return (ScheduleDepends[])groupDependsOnGroupMasterMap.get(masterGroupId);
    }
    
    /**
     * スケジュールが属するマスタグループと依存するグループの依存情報を設定する。<p>
     *
     * @param masterGroupId マスタグループID
     * @param depends 依存するグループの依存情報の配列
     */
    public void setGroupDependsOnGroupMaster(String masterGroupId, ScheduleDepends[] depends){
        groupDependsOnGroupMasterMap.put(masterGroupId, depends);
    }
    
    // ScheduleのJavaDoc
    public Map getGroupDependsOnGroupMasterMap(){
        return groupDependsOnGroupMasterMap;
    }
    public void setGroupDependsOnGroupMasterMap(Map map){
        if(map == null){
            groupDependsOnGroupMasterMap.clear();
        }else{
            groupDependsOnGroupMasterMap.putAll(map);
        }
    }
    
    // ScheduleのJavaDoc
    public ScheduleDepends[] getGroupDependsOnGroup(String groupId){
        return (ScheduleDepends[])groupDependsOnGroupMap.get(groupId);
    }
    
    /**
     * スケジュールが属するグループと依存するグループの依存情報を設定する。<p>
     *
     * @param groupId グループID
     * @param depends 依存するグループの依存情報の配列
     */
    public void setGroupDependsOnGroup(String groupId, ScheduleDepends[] depends){
        groupDependsOnGroupMap.put(groupId, depends);
    }
    
    // ScheduleのJavaDoc
    public Map getGroupDependsOnGroupMap(){
        return groupDependsOnGroupMap;
    }
    public void setGroupDependsOnGroupMap(Map map){
        if(map == null){
            groupDependsOnGroupMap.clear();
        }else{
            groupDependsOnGroupMap.putAll(map);
        }
    }
    
    // ScheduleのJavaDoc
    public Object getOutput(){
        return output;
    }
    
    // ScheduleのJavaDoc
    public void setOutput(Object out){
        output = out;
    }
    
    // ScheduleのJavaDoc
    public Date getInitialTime(){
        return initialTime == null ? time : initialTime;
    }
    
    /**
     * 最初にスケジュールされた時刻を設定する。<p>
     *
     * @param time 最初にスケジュールされた時刻
     */
    public void setInitialTime(Date time){
        initialTime = time;
    }
    
    // ScheduleのJavaDoc
    public long getRepeatInterval(){
        return repeatInterval;
    }
    
    /**
     * スケジュール繰り返し間隔[ms]を設定する。<p>
     *
     * @param interval 繰り返し間隔
     */
    public void setRepeatInterval(long interval){
        repeatInterval = interval;
    }
    
    // ScheduleのJavaDoc
    public Date getRepeatEndTime(){
        return repeatEndTime;
    }
    
    /**
     * スケジュール繰り返し終了時刻を設定する。<p>
     *
     * @param time スケジュール繰り返し終了時刻
     */
    public void setRepeatEndTime(Date time){
        repeatEndTime = time;
    }
    
    // ScheduleのJavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    /**
     * スケジュールリトライ実行間隔[ms]を設定する。<p>
     *
     * @param interval リトライ実行間隔
     */
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    
    // ScheduleのJavaDoc
    public Date getRetryEndTime(){
        return retryEndTime;
    }
    
    /**
     * スケジュールリトライ終了時刻を設定する。<p>
     *
     * @param time スケジュールリトライ終了時刻
     */
    public void setRetryEndTime(Date time){
        retryEndTime = time;
    }
    
    // ScheduleのJavaDoc
    public boolean isRetry(){
        return isRetry;
    }
    
    // ScheduleのJavaDoc
    public void setRetry(boolean retry){
        isRetry = retry;
    }
    
    // ScheduleのJavaDoc
    public long getMaxDelayTime(){
        return maxDelayTime;
    }
    
    /**
     * スケジュールの最大遅延時間[ms]を設定する。<p>
     *
     * @param time スケジュール最大遅延時間
     */
    public void setMaxDelayTime(long time){
        maxDelayTime = time;
    }
    
    // ScheduleのJavaDoc
    public int getState(){
        return state;
    }
    
    // ScheduleのJavaDoc
    public void setState(int state){
        this.state = state;
    }
    
    // ScheduleのJavaDoc
    public int getControlState(){
        return controlState;
    }
    
    // ScheduleのJavaDoc
    public void setControlState(int state){
        controlState = state;
    }
    
    // ScheduleのJavaDoc
    public int getCheckState(){
        return checkState;
    }
    
    // ScheduleのJavaDoc
    public void setCheckState(int state){
        checkState = state;
    }
    
    // ScheduleのJavaDoc
    public void setExecutorKey(String key){
        executorKey = key;
    }
    
    // ScheduleのJavaDoc
    public String getExecutorKey(){
        return executorKey;
    }
    
    // ScheduleMasterのJavaDoc
    public void setExecutorType(String type){
        executorType = type;
    }
    
    // ScheduleMasterのJavaDoc
    public String getExecutorType(){
        return executorType;
    }
    
    // ScheduleのJavaDoc
    public Date getExecuteStartTime(){
        return executeStartTime;
    }
    
    // ScheduleのJavaDoc
    public void setExecuteStartTime(Date time){
        executeStartTime = time;
    }
    
    // ScheduleのJavaDoc
    public Date getExecuteEndTime(){
        return executeEndTime;
    }
    
    // ScheduleのJavaDoc
    public void setExecuteEndTime(Date time){
        executeEndTime = time;
    }
    
    /**
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return id == null ? 0 : id.hashCode();
    }
    
    /**
     * 指定されたオブジェクトがこのインスタンスと等しいかどうかを判定する。<p>
     *
     * @param obj 比較対象のオブジェクト
     * @return 指定されたオブジェクトがこのインスタンスと等しい場合true
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
    
    // ComparableのJavaDoc
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
     * このインスタンスの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final SimpleDateFormat format
            = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
        final StringBuilder buf = new StringBuilder(super.toString());
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
        buf.append(",repeatInterval=").append(repeatInterval);
        buf.append(",repeatEndTime=")
            .append(repeatEndTime == null ? null : format.format(repeatEndTime));
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
    
    public Object clone(){
        DefaultSchedule schedule = null;
        try{
            schedule = (DefaultSchedule)super.clone();
        }catch(CloneNotSupportedException e){
        }
        schedule.time = time == null ? null : (Date)time.clone();
        schedule.initialTime = initialTime == null ? null : (Date)initialTime.clone();
        schedule.repeatEndTime = repeatEndTime == null ? null : (Date)repeatEndTime.clone();
        schedule.retryEndTime = retryEndTime == null ? null : (Date)retryEndTime.clone();
        schedule.executeStartTime = null;
        schedule.executeEndTime = null;
        schedule.state = Schedule.STATE_INITIAL;
        schedule.controlState = Schedule.CONTROL_STATE_INITIAL;
        schedule.checkState = Schedule.CHECK_STATE_INITIAL;
        schedule.output = null;
        schedule.isRetry = false;
        return schedule;
    }
}