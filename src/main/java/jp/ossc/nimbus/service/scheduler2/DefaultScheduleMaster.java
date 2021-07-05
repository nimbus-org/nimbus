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
 * デフォルトスケジュールマスタ。<p>
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
    
    protected boolean isDynamicRepeat;
    
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
     * 空のインスタンスを生成する。<p>
     */
    public DefaultScheduleMaster(){
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param id スケジュールマスタID
     * @param groupIds スケジュールマスタグループID
     * @param taskName タスク名
     * @param scheduleType タスク種別
     * @param input 入力データ
     * @param startTime 開始時刻
     * @param isEnabled 有効/無効フラグ
     * @param depends スケジュール依存情報の配列
     * @param dependsOnGroup スケジュールグループ依存情報の配列
     * @param executorKey ScheduleExecutorを特定するキー
     * @param executorType ScheduleExecutorの種類
     * @param isTemplate テンプレートかどうか。テンプレートの場合、true
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
            false,
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
     * インスタンスを生成する。<p>
     *
     * @param id スケジュールマスタID
     * @param groupIds スケジュールマスタグループID
     * @param taskName タスク名
     * @param scheduleType タスク種別
     * @param input 入力データ
     * @param startTime 開始時刻
     * @param retryInterval リトライ実行間隔[ms]
     * @param retryEndTime リトライ終了時刻
     * @param maxDelayTime 最大遅延時間[ms]
     * @param isEnabled 有効/無効フラグ
     * @param depends スケジュール依存情報の配列
     * @param dependsOnGroup スケジュールグループ依存情報の配列
     * @param executorKey ScheduleExecutorを特定するキー
     * @param executorType ScheduleExecutorの種類
     * @param isTemplate テンプレートかどうか。テンプレートの場合、true
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
            false,
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
     * インスタンスを生成する。<p>
     *
     * @param id スケジュールマスタID
     * @param groupIds スケジュールマスタグループID
     * @param taskName タスク名
     * @param scheduleType タスク種別
     * @param input 入力データ
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @param repeatInterval 繰り返し実行間隔[ms]
     * @param isDynamicRepeat 動的繰り返しフラグ
     * @param retryInterval リトライ実行間隔[ms]
     * @param retryEndTime リトライ終了時刻
     * @param maxDelayTime 最大遅延時間[ms]
     * @param isEnabled 有効/無効フラグ
     * @param depends スケジュール依存情報の配列
     * @param dependsOnGroup スケジュールグループ依存情報の配列
     * @param executorKey ScheduleExecutorを特定するキー
     * @param executorType ScheduleExecutorの種類
     * @param isTemplate テンプレートかどうか。テンプレートの場合、true
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
        boolean isDynamicRepeat,
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
        setDynamicRepeat(isDynamicRepeat);
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
    
    // ScheduleMasterのJavaDoc
    public String getId(){
        return id;
    }
    
    /**
     * スケジュールマスタのIDを設定する。<p>
     *
     * @param id スケジュールマスタのID
     */
    public void setId(String id){
        this.id = id;
    }
    
    // ScheduleMasterのJavaDoc
    public String[] getGroupIds(){
        return groupIds;
    }
    
    /**
     * スケジュールマスタのグループIDを設定する。<p>
     *
     * @param id スケジュールマスタのグループID
     */
    public void setGroupIds(String[] id){
        this.groupIds = id;
    }
    
    // ScheduleMasterのJavaDoc
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
    
    // ScheduleMasterのJavaDoc
    public String getScheduleType(){
        return scheduleType;
    }
    
    /**
     * スケジュールされたスケジュール種別を設定する。<p>
     *
     * @param type スケジュール種別
     */
    public void setScheduleType(String type){
        scheduleType = type;
    }
    
    // ScheduleMasterのJavaDoc
    public Object getInput(){
        return input;
    }
    
    // ScheduleMasterのJavaDoc
    public void setInput(Object data){
        input = data;
    }
    
    // ScheduleMasterのJavaDoc
    public Date getStartTime(){
        return startTime;
    }
    
    // ScheduleMasterのJavaDoc
    public void setStartTime(Date time){
        startTime = time;
    }
    
    // ScheduleMasterのJavaDoc
    public Date getEndTime(){
        return endTime;
    }
    
    // ScheduleMasterのJavaDoc
    public void setEndTime(Date time){
        endTime = time;
    }
    
    // ScheduleMasterのJavaDoc
    public long getRepeatInterval(){
        return repeatInterval;
    }
    
    // ScheduleMasterのJavaDoc
    public void setRepeatInterval(long interval){
        repeatInterval = interval;
    }
    
    // ScheduleMasterのJavaDoc
    public void setDynamicRepeat(boolean isDynamic){
        isDynamicRepeat = isDynamic;
    }
    
    // ScheduleMasterのJavaDoc
    public boolean isDynamicRepeat(){
        return isDynamicRepeat;
    }
    
    // ScheduleMasterのJavaDoc
    public long getRetryInterval(){
        return retryInterval;
    }
    
    // ScheduleMasterのJavaDoc
    public void setRetryInterval(long interval){
        retryInterval = interval;
    }
    
    // ScheduleMasterのJavaDoc
    public Date getRetryEndTime(){
        return retryEndTime;
    }
    
    // ScheduleMasterのJavaDoc
    public void setRetryEndTime(Date time){
        retryEndTime = time;
    }
    
    // ScheduleMasterのJavaDoc
    public long getMaxDelayTime(){
        return maxDelayTime;
    }
    
    // ScheduleMasterのJavaDoc
    public void setMaxDelayTime(long time){
        maxDelayTime = time;
    }
    
    // ScheduleMasterのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // ScheduleMasterのJavaDoc
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
    
    // ScheduleMasterのJavaDoc
    public ScheduleDepends[] getDepends(){
        return depends;
    }
    
    /**
     * 依存情報を設定する。<p>
     *
     * @param deps スケジュール依存情報の配列
     */
    public void setDepends(ScheduleDepends[] deps){
        depends = deps;
    }
    
    // ScheduleMasterのJavaDoc
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
    
    // ScheduleMasterのJavaDoc
    public Map getDependsInGroupMap(){
        return dependsInGroupMap;
    }
    
    // ScheduleMasterのJavaDoc
    public ScheduleDepends[] getDependsOnGroup(){
        return dependsOnGroup;
    }
    
    /**
     * グループ依存情報を設定する。<p>
     *
     * @param deps 依存情報の配列
     */
    public void setDependsOnGroup(ScheduleDepends[] deps){
        dependsOnGroup = deps;
    }
    
    // ScheduleMasterのJavaDoc
    public ScheduleDepends[] getGroupDependsOnGroup(String groupId){
        return (ScheduleDepends[])groupDependsOnGroupMap.get(groupId);
    }
    
    /**
     * スケジュールが属するグループと依存するグループの依存情報を設定する。<p>
     *
     * @param groupId スケジュールが属するグループID
     * @param depends 依存するグループの依存情報の配列
     */
    public void setGroupDependsOnGroup(String groupId, ScheduleDepends[] depends){
        groupDependsOnGroupMap.put(groupId, depends);
    }
    
    // ScheduleMasterのJavaDoc
    public Map getGroupDependsOnGroupMap(){
        return groupDependsOnGroupMap;
    }
    
    // ScheduleMasterのJavaDoc
    public void setExecutorKey(String key){
        executorKey = key;
    }
    
    // ScheduleMasterのJavaDoc
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
    
    // ScheduleMasterのJavaDoc
    public boolean isTemplate(){
        return isTemplate;
    }
    
    // ScheduleMasterのJavaDoc
    public void setTemplate(boolean isTemplate){
        this.isTemplate = isTemplate;
    }
    
    // ScheduleMasterのJavaDoc
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
     * timeにdateの日付を適用する。<p>
     *
     * @param date 日付
     * @param time 時刻
     * @param work 作業用カレンダー
     * @return dateの日付とtimeの時刻を持ったDate
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
        if(!(obj instanceof ScheduleMaster)){
            return false;
        }
        ScheduleMaster cmp = (ScheduleMaster)obj;
        return (id == null && cmp.getId() == null)
            || (id != null && id.equals(cmp.getId()));
    }
    
    // ComparableのJavaDoc
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
        buf.append(",isDynamicRepeat=").append(isDynamicRepeat);
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