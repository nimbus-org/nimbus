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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.text.*;
import java.io.*;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.io.*;
import jp.ossc.nimbus.service.sequence.*;

/**
 * デフォルトスケジュール管理。<p>
 * メモリ中でスケジュールを作成・管理し、実行すべきスケジュールを提供する。<br>
 * {@link #setPersistDir(String)}を設定すれば、スケジュールの状態をファイルに永続化する事ができる。<br>
 *
 * @author M.Takata
 */
public class DefaultScheduleManagerService extends ServiceBase
 implements ScheduleManager, DefaultScheduleManagerServiceMBean{
    
    private static final long serialVersionUID = 3176394103850131069L;
    protected static final String DATE_DIR_FORMAT = "yyyyMMdd";
    protected static final String DATE_CSV_FORMAT = "yyyy/MM/dd HH:mm:ss SSS";
    protected static final String LOCAL_SEQUENCE_NUMBER_FILE = "sequence_number";
    protected static final String ARRAY_CLASS_SUFFIX = "[]";
    
    protected ServiceName[] scheduleMasterServiceNames;
    protected Map addedScheduleMasters;
    protected Map scheduleMasters;
    protected Map scheduleMasterGroupMap;
    
    protected Properties scheduleMakerTypeMapping;
    protected Map addedScheduleMakerMap;
    protected Map scheduleMakerMap;
    protected boolean isScheduleMakerTypeRegexEnabled;
    protected ServiceName defaultScheduleMakerServiceName;
    protected ScheduleMaker defaultScheduleMaker;
    
    protected boolean isMakeScheduleOnStart = true;
    protected Map scheduleDateMap;
    protected Map scheduleMap;
    protected Map scheduleDependedMap;
    protected List scheduleList;
    
    protected Map scheduleMasterMap;
    protected Map scheduleGroupMap;
    protected Map scheduleGroupMasterMap;
    
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    protected long sequenceNumber;
    protected Object sequenceNumberLock = "sequenceNumberLock";
    
    protected Set scheduleControlListeners;
    
    protected String persistDir;
    
    protected long timeoverCheckInterval = 1000l;
    protected Daemon timeoverChecker;
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setDefaultScheduleMakerServiceName(ServiceName name){
        defaultScheduleMakerServiceName = name;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public ServiceName getDefaultScheduleMakerServiceName(){
        return defaultScheduleMakerServiceName;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setScheduleMasterServiceNames(ServiceName[] names){
        scheduleMasterServiceNames = names;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public ServiceName[] getScheduleMasterServiceNames(){
        return scheduleMasterServiceNames;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setScheduleMakerTypeMapping(Properties mapping){
        scheduleMakerTypeMapping = mapping;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public Properties getScheduleMakerTypeMapping(){
        return scheduleMakerTypeMapping;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setScheduleMakerTypeRegexEnabled(boolean isEnable){
        isScheduleMakerTypeRegexEnabled = isEnable;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public boolean isScheduleMakerTypeRegexEnabled(){
        return isScheduleMakerTypeRegexEnabled;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setMakeScheduleOnStart(boolean isMake){
        isMakeScheduleOnStart = isMake;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public boolean isMakeScheduleOnStart(){
        return isMakeScheduleOnStart;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setPersistDir(String dir){
        persistDir = dir;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public String getPersistDir(){
        return persistDir;
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void setTimeoverCheckInterval(long interval){
        timeoverCheckInterval = interval;
    }
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public long getTimeoverCheckInterval(){
        return timeoverCheckInterval;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        scheduleMakerMap = new HashMap();
        scheduleMasters = new HashMap();
        scheduleMasterGroupMap = Collections.synchronizedMap(new HashMap());
        scheduleDateMap = Collections.synchronizedMap(new TreeMap());
        scheduleMap = Collections.synchronizedMap(new HashMap());
        scheduleDependedMap = Collections.synchronizedMap(new HashMap());
        scheduleList = Collections.synchronizedList(new ArrayList());
        scheduleMasterMap = Collections.synchronizedMap(new HashMap());
        scheduleGroupMap = Collections.synchronizedMap(new HashMap());
        scheduleGroupMasterMap = Collections.synchronizedMap(new HashMap());
        scheduleControlListeners = Collections.synchronizedSet(new LinkedHashSet());
        addedScheduleMakerMap = null;
        addedScheduleMasters = null;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(addedScheduleMasters != null){
            scheduleMasters.putAll(addedScheduleMasters);
        }
        if(scheduleMasterServiceNames != null){
            for(int i = 0; i < scheduleMasterServiceNames.length; i++){
                final ScheduleMaster scheduleMaster
                    = (ScheduleMaster)ServiceManagerFactory
                        .getServiceObject(scheduleMasterServiceNames[i]);
                scheduleMasters.put(scheduleMaster.getId(), scheduleMaster);
            }
        }
        if(scheduleMasters.size() != 0){
            final Iterator entries = scheduleMasters.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String[] groupIds = ((ScheduleMaster)entry.getValue()).getGroupIds();
                if(groupIds != null){
                    for(int i = 0; i < groupIds.length; i++){
                        Set masterSet = (Set)scheduleMasterGroupMap.get(groupIds[i]);
                        if(masterSet == null){
                            masterSet = Collections.synchronizedSet(new HashSet());
                            scheduleMasterGroupMap.put(groupIds[i], masterSet);
                        }
                        masterSet.add(entry.getValue());
                    }
                }
            }
        }
        
        if(addedScheduleMakerMap != null){
            scheduleMakerMap.putAll(addedScheduleMakerMap);
        }
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
        
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(sequenceServiceName);
        }
        
        loadLocalSequenceNumber();
        loadSchedules();
        
        if(isMakeScheduleOnStart){
            final Date now = new Date();
            final List oldScheduleList = findSchedules(now);
            if(oldScheduleList == null || oldScheduleList.size() == 0){
                makeSchedule(now);
            }
        }
        
        if(timeoverCheckInterval > 0){
            timeoverChecker = new Daemon(new TimeoverChecker());
            timeoverChecker.setName("Nimbus SchedulerManagerTimeoverChecker " + getServiceNameObject());
            timeoverChecker.start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(timeoverChecker != null){
            timeoverChecker.stop();
            timeoverChecker = null;
        }
        if(scheduleMasters != null){
            scheduleMasters.clear();
        }
        if(scheduleMakerMap != null){
            scheduleMakerMap.clear();
        }
        if(scheduleMap != null){
            synchronized(scheduleMap){
                scheduleMap.clear();
            }
        }
        if(scheduleDependedMap != null){
            synchronized(scheduleDependedMap){
                scheduleDependedMap.clear();
            }
        }
        if(scheduleDateMap != null){
            synchronized(scheduleDateMap){
                scheduleDateMap.clear();
            }
        }
        if(scheduleList != null){
            synchronized(scheduleList){
                scheduleList.clear();
            }
        }
        if(scheduleMasterMap != null){
            synchronized(scheduleMasterMap){
                scheduleMasterMap.clear();
            }
        }
        if(scheduleGroupMap != null){
            synchronized(scheduleGroupMap){
                scheduleGroupMap.clear();
            }
        }
        if(scheduleGroupMasterMap != null){
            synchronized(scheduleGroupMasterMap){
                scheduleGroupMasterMap.clear();
            }
        }
        if(scheduleMasterGroupMap != null){
            synchronized(scheduleMasterGroupMap){
                scheduleMasterGroupMap.clear();
            }
        }
        sequenceNumber = 0;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        scheduleMakerMap = null;
        scheduleMasters = null;
        if(scheduleMap != null){
            scheduleMap = null;
        }
        if(scheduleDependedMap != null){
            scheduleDependedMap = null;
        }
        if(scheduleDateMap != null){
            scheduleDateMap = null;
        }
        if(scheduleList != null){
            scheduleList = null;
        }
        if(scheduleMasterMap != null){
            scheduleMasterMap = null;
        }
        if(scheduleGroupMap != null){
            scheduleGroupMap = null;
        }
        if(scheduleGroupMasterMap != null){
            scheduleGroupMasterMap = null;
        }
        if(scheduleMasterGroupMap != null){
            scheduleMasterGroupMap = null;
        }
        if(scheduleControlListeners != null){
            scheduleControlListeners = null;
        }
        addedScheduleMakerMap = null;
        addedScheduleMasters = null;
    }
    
    /**
     * スケジュールのIDを生成する{@link Sequence}を設定する。<p>
     *
     * @param sequence Sequence
     */
    public void setSequence(Sequence sequence){
        this.sequence = sequence;
    }
    
    /**
     * スケジュールのIDを生成する{@link Sequence}を取得する。<p>
     *
     * @return Sequence
     */
    public Sequence getSequence(){
        return sequence;
    }
    
    /**
     * スケジュールマスタを登録する。<p>
     *
     * @param master スケジュールマスタ
     */
    public void addScheduleMaster(ScheduleMaster master){
        if(addedScheduleMasters == null){
            addedScheduleMasters = new HashMap();
        }
        if(addedScheduleMasters.containsKey(master.getId())){
            throw new IllegalArgumentException(
                "Dupulicate id : " + master.getId()
            );
        }
        addedScheduleMasters.put(master.getId(), master);
    }
    
    // ScheduleManagerのJavaDoc
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
    
    // ScheduleManagerのJavaDoc
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
    
    // ScheduleManagerのJavaDoc
    public Map getScheduleMakerMap(){
        return scheduleMakerMap;
    }
    
    // ScheduleManagerのJavaDoc
    public void setDefaultScheduleMaker(ScheduleMaker maker){
        defaultScheduleMaker = maker;
    }
    
    // ScheduleManagerのJavaDoc
    public ScheduleMaker getDefaultScheduleMaker(){
        return defaultScheduleMaker;
    }
    
    // ScheduleManagerのJavaDoc
    public List makeSchedule(Date date) throws ScheduleMakeException{
        if(scheduleMasters.size() == 0){
            return new ArrayList();
        }
        final List masters = new ArrayList();
        synchronized(scheduleMasters){
            masters.addAll(scheduleMasters.values());
        }
        return makeSchedule(date, masters);
    }
    
    // ScheduleManagerのJavaDoc
    public List makeSchedule(Date date, ScheduleMaster master) throws ScheduleMakeException{
        if(master == null){
            return new ArrayList();
        }
        final List masters = new ArrayList();
        masters.add(master);
        return makeSchedule(date, masters);
    }
    
    // ScheduleManagerのJavaDoc
    public List makeSchedule(Date date, List masters) throws ScheduleMakeException{
        if(masters.size() == 0){
            return new ArrayList();
        }
        final Date standardDate = getStandardTimeDate(
            date == null ? new Date() : date
        );
        final List tmpScheduleList = new ArrayList();
        final Map tmpScheduleMasterMap = new HashMap();
        for(int i = 0; i < masters.size(); i++){
            ScheduleMaster master = (ScheduleMaster)masters.get(i);
            ScheduleMaker maker = getScheduleMaker(master.getScheduleType());
            if(!master.isEnabled()){
                continue;
            }
            master = (ScheduleMaster)master.clone();
            final Schedule[] schedules = maker.makeSchedule(
                standardDate,
                master
            );
            if(schedules == null || schedules.length == 0){
                continue;
            }
            
            final Set scheduleSet = Collections.synchronizedSet(new HashSet());
            for(int j = 0; j < schedules.length; j++){
                tmpScheduleList.add(schedules[j]);
                scheduleSet.add(schedules[j]);
            }
            tmpScheduleMasterMap.put(master.getId(), scheduleSet);
        }
        Collections.sort(tmpScheduleList);
        List result = new ArrayList();
        for(int i = 0, imax = tmpScheduleList.size(); i < imax; i++){
            final Schedule schedule = (Schedule)tmpScheduleList.get(i);
            addSchedule(schedule, true, true);
            result.add(schedule);
        }
        return result;
    }
    
    /**
     * 指定された日付で基準時間のDateを取得する。<p>
     * 
     * @param date 日付
     * @return 基準時間のDate
     */
    protected Date getStandardTimeDate(Date date){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * スケジュールIDを発番する。<p>
     *
     * @return スケジュールID
     * @exception ScheduleManageException スケジュールIDの発番に失敗した場合
     */
    protected String createScheduleId() throws ScheduleManageException{
        if(sequence == null){
            synchronized(sequenceNumberLock){
                sequenceNumber++;
                if(sequenceNumber < 0){
                    sequenceNumber = 1;
                }
            }
            if(persistDir != null){
                final File baseDir = new File(persistDir);
                if(!baseDir.exists()){
                    synchronized(persistDir){
                        if(!baseDir.exists()){
                            if(!baseDir.mkdirs()){
                                throw new ScheduleManageException("PersistDir can't make." + baseDir.getAbsolutePath());
                            }
                        }
                    }
                }
                synchronized(sequenceNumberLock){
                    final File file = new File(
                        baseDir,
                        LOCAL_SEQUENCE_NUMBER_FILE
                    );
                    BufferedWriter writer = null;
                    try{
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        writer = new BufferedWriter(new FileWriter(file));
                        writer.write(String.valueOf(sequenceNumber));
                    }catch(IOException e){
                        throw new ScheduleManageException(e);
                    }finally{
                        if(writer != null){
                            try{
                                writer.close();
                            }catch(IOException e){
                                throw new ScheduleManageException(e);
                            }
                        }
                    }
                }
            }
            return String.valueOf(sequenceNumber);
        }else{
            return sequence.increment();
        }
    }
    
    // ScheduleManagerのJavaDoc
    public List findAllScheduleMasters() throws ScheduleManageException{
        List result = new ArrayList();
        if(scheduleMasters.size() == 0){
            return result;
        }
        synchronized(scheduleMasters){
            result.addAll(scheduleMasters.values());
        }
        for(int i = 0; i < result.size(); i++){
            ScheduleMaster master = (ScheduleMaster)result.get(i);
            if(master != null && master.isTemplate()){
                result.set(i, (ScheduleMaster)master.clone());
            }
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public List findScheduleMasters(String groupId) throws ScheduleManageException{
        List result = new ArrayList();
        Set masters = (Set)scheduleMasterGroupMap.get(groupId);
        if(masters == null || masters.size() == 0){
            return result;
        }
        synchronized(masters){
            result.addAll(masters);
        }
        for(int i = 0; i < result.size(); i++){
            ScheduleMaster master = (ScheduleMaster)result.get(i);
            if(master != null && master.isTemplate()){
                result.set(i, (ScheduleMaster)master.clone());
            }
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public ScheduleMaster findScheduleMaster(String id) throws ScheduleManageException{
        ScheduleMaster master = (ScheduleMaster)scheduleMasters.get(id);
        if(master != null && master.isTemplate()){
            master = (ScheduleMaster)master.clone();
        }
        return master;
    }
    
    // ScheduleManagerのJavaDoc
    public List findAllSchedules() throws ScheduleManageException{
        synchronized(scheduleList){
            return new ArrayList(scheduleList);
        }
    }
    
    // ScheduleManagerのJavaDoc
    public Schedule findSchedule(String id) throws ScheduleManageException{
        return (Schedule)scheduleMap.get(id);
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(String groupId) throws ScheduleManageException{
        final List result = new ArrayList();
        final Set scheduleSet = (Set)scheduleGroupMap.get(groupId);
        if(scheduleSet == null){
            return result;
        }
        synchronized(scheduleSet){
            result.addAll(scheduleSet);
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(String masterId, String masterGroupId) throws ScheduleManageException{
        final List result = new ArrayList();
        Set scheduleSet = null;
        if(masterId != null){
            scheduleSet = (Set)scheduleMasterMap.get(masterId);
        }else{
            scheduleSet = (Set)scheduleGroupMasterMap.get(masterGroupId);
        }
        if(scheduleSet == null){
            return result;
        }
        synchronized(scheduleSet){
            result.addAll(scheduleSet);
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(Date date) throws ScheduleManageException{
        final Date standardDate = getStandardTimeDate(date);
        final List scheduleList = (List)scheduleDateMap.get(standardDate);
        if(scheduleList == null){
            return new ArrayList();
        }else{
            synchronized(scheduleList){
                return new ArrayList(scheduleList);
            }
        }
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(Date from, Date to)
     throws ScheduleManageException{
        return findSchedules(from, to, null);
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(int[] states) throws ScheduleManageException{
        final List result = new ArrayList();
        
        Schedule[] schedules = null;
        synchronized(scheduleList){
            schedules = (Schedule[])scheduleList.toArray(
                new Schedule[scheduleList.size()]
            );
        }
        for(int i = 0; i < schedules.length; i++){
            final Schedule schedule = schedules[i];
            for(int j = 0; j < states.length; j++){
                if(schedule.getState() == states[j]){
                    result.add(schedule);
                    break;
                }
            }
        }
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(Date from, Date to, int[] states)
     throws ScheduleManageException{
        return findSchedules(from, to, states, null, null, null, null, null, -1);
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId) throws ScheduleManageException{
        return findSchedules(from, to, states, masterId, masterGroupId, groupId, null, null, -1);
    }
    
    // ScheduleManagerのJavaDoc
    public List findSchedules(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId, int limit) throws ScheduleManageException{
        return findSchedules(from, to, states, masterId, masterGroupId, groupId, null, null, limit);
    }
    
    /**
     * 指定された期間、指定された状態、指定された実行キーのスケジュールを検索する。<p>
     *
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @param states スケジュール状態の配列
     * @param masterId スケジュールマスタID
     * @param masterGroupId スケジュールマスタグループID
     * @param groupId スケジュールグループID
     * @param executorTypes 実行種別配列
     * @param executorKey 実行キー
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    protected List findSchedules(
        Date from,
        Date to,
        int[] states,
        String masterId,
        String masterGroupId,
        String groupId,
        String[] executorTypes,
        String executorKey,
        int limit
    ) throws ScheduleManageException{
        if(from != null && to != null){
            if(from.after(to)){
                throw new ScheduleManageException(
                    "from > to. from=" + from + ", to=" + to
                );
            }
        }
        if(scheduleList.size() == 0){
            return new ArrayList();
        }
        final DefaultSchedule searchKey =  new DefaultSchedule();
        List schedules = null;
        synchronized(scheduleList){
            int fromIndex = 0;
            if(from != null){
                searchKey.setTime(from);
                fromIndex = Math.abs(
                    Collections.binarySearch(scheduleList, searchKey) + 1
                );
                if(fromIndex != 0){
                    final Schedule schedule
                        = (Schedule)scheduleList.get(fromIndex - 1);
                    if(from.equals(schedule.getTime())){
                        fromIndex = fromIndex - 1;
                    }
                }
                if(fromIndex == scheduleList.size()){
                    return new ArrayList();
                }
                for(int i = fromIndex; --i >= 0;){
                    Schedule cmp = (Schedule)scheduleList.get(i);
                    if(from.equals(cmp.getTime())){
                        fromIndex = i;
                    }else{
                        break;
                    }
                }
            }
            int toIndex = scheduleList.size();
            if(to != null){
                searchKey.setTime(to);
                toIndex = Math.abs(
                    Collections.binarySearch(scheduleList, searchKey) + 1
                );
                if(toIndex == 0){
                    return new ArrayList();
                }
                for(int i = toIndex, imax = scheduleList.size(); i < imax; i++){
                    Schedule cmp = (Schedule)scheduleList.get(i);
                    if(to.equals(cmp.getTime())){
                        toIndex = i + 1;
                    }else{
                        break;
                    }
                }
            }
            schedules = new ArrayList(
                scheduleList.subList(fromIndex, toIndex)
            );
        }
        final Iterator itr = schedules.iterator();
        while(itr.hasNext()){
            final Schedule schedule = (Schedule)itr.next();
            if(masterId != null){
                if(!masterId.equals(schedule.getMasterId())){
                    itr.remove();
                    continue;
                }
            }
            if(masterGroupId != null || groupId != null){
                if(masterGroupId != null && groupId != null){
                    if(!groupId.equals(schedule.getGroupId(masterGroupId))){
                        itr.remove();
                        continue;
                    }
                }else if(masterGroupId != null){
                    String[] groupIds = schedule.getMasterGroupIds();
                    boolean isMatch = false;
                    if(groupIds != null){
                        for(int i = 0; i < groupIds.length; i++){
                            if(masterGroupId.equals(groupIds[i])){
                                isMatch = true;
                                break;
                            }
                        }
                    }
                    if(!isMatch){
                        itr.remove();
                        continue;
                    }
                }else if(groupId != null && !schedule.getGroupIdMap().values().contains(groupId)){
                    itr.remove();
                    continue;
                }
            }
            if(executorKey != null
                && schedule.getExecutorKey() != null
                && !executorKey.equals(schedule.getExecutorKey())){
                itr.remove();
                continue;
            }
            boolean isMatch = false;
            if(schedule.getExecutorType() != null
                && executorTypes != null && executorTypes.length != 0){
                for(int j = 0; j < executorTypes.length; j++){
                    if(schedule.getExecutorType().equals(executorTypes[j])){
                        isMatch = true;
                        break;
                    }
                }
                if(!isMatch){
                    itr.remove();
                    continue;
                }
            }
            if(states != null && states.length != 0){
                isMatch = false;
                for(int j = 0; j < states.length; j++){
                    if(schedule.getState() == states[j]){
                        isMatch = true;
                        break;
                    }
                }
                if(!isMatch){
                    itr.remove();
                }
            }
        }
        if(limit > 0){
            schedules = schedules.subList(0, limit);
        }
        return schedules;
    }
    
    // ScheduleManagerのJavaDoc
    public List findExecutableSchedules(Date date, String[] executorTypes)
     throws ScheduleManageException{
        return findExecutableSchedules(date, executorTypes, null);
    }
    
    // ScheduleManagerのJavaDoc
    public List findExecutableSchedules(Date date, String[] executorTypes, String executorKey)
     throws ScheduleManageException{
        return findExecutableSchedules(date, executorTypes, null, -1);
    }
    
    public List findExecutableSchedules(Date date, String[] executorTypes, String executorKey, int limit)
     throws ScheduleManageException{
        final List schedules = findSchedules(
            null,
            date,
            new int[]{Schedule.STATE_INITIAL, Schedule.STATE_RETRY},
            null,
            null,
            null,
            executorTypes,
            executorKey,
            limit
        );
        if(schedules == null || schedules.size() == 0){
            return schedules;
        }
        final List result = new ArrayList();
        for(int i = 0, imax = schedules.size(); i < imax; i++){
            final Schedule schedule = (Schedule)schedules.get(i);
            final ScheduleDepends[] depends = schedule.getDepends();
            if(depends == null){
                result.add(schedule);
            }else{
                boolean isClear = true;
                for(int j = 0; j < depends.length; j++){
                    final Set scheduleSet
                        = (Set)scheduleMasterMap.get(depends[j].getMasterId());
                    if(scheduleSet == null){
                        continue;
                    }
                    Schedule[] dependsSchedules = null;
                    synchronized(scheduleSet){
                        dependsSchedules = (Schedule[])scheduleSet.toArray(
                            new Schedule[scheduleSet.size()]
                        );
                    }
                    for(int k = 0; k < dependsSchedules.length; k++){
                        if(dependsSchedules[k].getInitialTime()
                                .after(schedule.getInitialTime())){
                            continue;
                        }
                        if(dependsSchedules[k].getState() != Schedule.STATE_END
                            && dependsSchedules[k].getState() != Schedule.STATE_DISABLE
                        ){
                            isClear = false;
                            break;
                        }
                    }
                    if(!isClear){
                        break;
                    }
                }
                if(isClear){
                    result.add(schedule);
                }
            }
        }
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public List findDependsSchedules(String id) throws ScheduleManageException{
        Schedule schedule = findSchedule(id);
        List result = new ArrayList();
        if(schedule == null){
            return result;
        }
        final ScheduleDepends[] depends = schedule.getDepends();
        if(depends == null){
            return result;
        }else{
            for(int i = 0; i < depends.length; i++){
                final Set scheduleSet
                    = (Set)scheduleMasterMap.get(depends[i].getMasterId());
                if(scheduleSet == null){
                    continue;
                }
                Schedule[] dependsSchedules = null;
                synchronized(scheduleSet){
                    dependsSchedules = (Schedule[])scheduleSet.toArray(
                        new Schedule[scheduleSet.size()]
                    );
                }
                for(int j = 0; j < dependsSchedules.length; j++){
                    if(dependsSchedules[j].getInitialTime()
                            .after(schedule.getInitialTime())){
                        if(dependsSchedules[j].getState() != Schedule.STATE_ENTRY
                            && dependsSchedules[j].getState() != Schedule.STATE_RUN
                            && dependsSchedules[j].getState() != Schedule.STATE_PAUSE
                        ){
                            continue;
                        }
                    }else if(dependsSchedules[j].getState() != Schedule.STATE_END
                        && dependsSchedules[j].getState() != Schedule.STATE_DISABLE
                    ){
                        continue;
                    }
                    result.add(dependsSchedules[j]);
                }
            }
        }
        return result;
    }
    
    public List findDependedSchedules(String id) throws ScheduleManageException{
        Schedule schedule = findSchedule(id);
        List result = new ArrayList();
        if(schedule == null
            || schedule.getState() == Schedule.STATE_END
            || schedule.getState() == Schedule.STATE_DISABLE
        ){
            return result;
        }
        Set depended = (Set)scheduleDependedMap.get(schedule.getMasterId());
        if(depended == null || depended.size() == 0){
            return result;
        }
        final Date initialTime = schedule.getInitialTime() == null
            ? schedule.getTime() : schedule.getInitialTime();
        String[] ids = null;
        synchronized(depended){
            ids = (String[])depended.toArray(new String[depended.size()]);
        }
        for(int i = 0; i < ids.length; i++){
            schedule = findSchedule(ids[i]);
            if(schedule != null
                && schedule.getState() != Schedule.STATE_END
                && schedule.getState() != Schedule.STATE_DISABLE
                && schedule.getState() != Schedule.STATE_FAILED
                && schedule.getState() != Schedule.STATE_ABORT
                && initialTime.compareTo(schedule.getInitialTime()) <= 0
            ){
                result.add(schedule);
            }
        }
        Collections.sort(result);
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public void addSchedule(Schedule schedule) throws ScheduleManageException{
        addSchedule(schedule, true, true);
    }
    
    // DefaultScheduleManagerServiceMBeanのJavaDoc
    public void addSchedule(
        String masterId,
        String[] masterGroupIds,
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
                masterGroupIds,
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
    
    /**
     * スケジュールを追加する。<p>
     *
     * @param schedule スケジュール
     * @param isCreateId スケジュールIDを再発番するかどうか
     * @param persist 永続化するかどうか
     * @exception ScheduleManageException スケジュールの追加に失敗した場合
     */
    protected void addSchedule(Schedule schedule, boolean isCreateId, boolean persist)
     throws ScheduleManageException{
        final Date standardDate = getStandardTimeDate(schedule.getTime());
        if(isCreateId){
            schedule.setId(createScheduleId());
        }
        
        synchronized(scheduleDateMap){
            List schedules = (List)scheduleDateMap.get(standardDate);
            if(schedules == null){
                schedules = Collections.synchronizedList(new ArrayList());
                schedules.add(schedule);
                scheduleDateMap.put(standardDate, schedules);
            }else{
                synchronized(schedules){
                    schedules.add(schedule);
                    Collections.sort(schedules);
                }
            }
        }
        scheduleMap.put(schedule.getId(), schedule);
        synchronized(scheduleMasterMap){
            Set scheduleSet = (Set)scheduleMasterMap.get(schedule.getMasterId());
            if(scheduleSet == null){
                scheduleSet = Collections.synchronizedSet(new HashSet());
                scheduleSet.add(schedule);
                scheduleMasterMap.put(schedule.getMasterId(), scheduleSet);
            }else{
                synchronized(scheduleSet){
                    scheduleSet.add(schedule);
                }
            }
        }
        synchronized(scheduleGroupMap){
            Iterator itr = schedule.getGroupIdMap().values().iterator();
            while(itr.hasNext()){
                String groupId = (String)itr.next();
                Set scheduleSet = (Set)scheduleGroupMap.get(groupId);
                if(scheduleSet == null){
                    scheduleSet = Collections.synchronizedSet(new HashSet());
                    scheduleSet.add(schedule);
                    scheduleGroupMap.put(groupId, scheduleSet);
                }else{
                    synchronized(scheduleSet){
                        scheduleSet.add(schedule);
                    }
                }
            }
        }
        synchronized(scheduleGroupMasterMap){
            String[] groupIds = schedule.getMasterGroupIds();
            if(groupIds != null){
                for(int i = 0; i < groupIds.length; i++){
                    Set scheduleSet = (Set)scheduleGroupMasterMap.get(groupIds[i]);
                    if(scheduleSet == null){
                        scheduleSet = Collections.synchronizedSet(new HashSet());
                        scheduleSet.add(schedule);
                        scheduleGroupMasterMap.put(groupIds[i], scheduleSet);
                    }else{
                        synchronized(scheduleSet){
                            scheduleSet.add(schedule);
                        }
                    }
                }
            }
        }
        final ScheduleDepends[] depends = schedule.getDepends();
        if(depends != null){
            synchronized(scheduleDependedMap){
                for(int i = 0; i < depends.length; i++){
                    Set depended = (Set)scheduleDependedMap.get(depends[i].getMasterId());
                    if(depended == null){
                        depended = Collections.synchronizedSet(new HashSet());
                        scheduleDependedMap.put(depends[i].getMasterId(), depended);
                    }
                    depended.add(schedule.getId());
                }
            }
        }
        synchronized(scheduleList){
            scheduleList.add(schedule);
            Collections.sort(scheduleList);
        }
        
        if(persist){
            try{
                persistSchedule(schedule);
            }catch(IOException e){
                removeSchedule(schedule.getId());
                throw new ScheduleManageException(e);
            }
        }
    }
    
    /**
     * スケジュールを永続化する。<p>
     *
     * @param schedule スケジュール
     * @exception IOException スケジュールの追加に失敗した場合
     */
    protected void persistSchedule(Schedule schedule)
     throws IOException{
        if(persistDir == null){
            return;
        }
        final File baseDir = new File(persistDir);
        if(!baseDir.exists()){
            synchronized(persistDir){
                if(!baseDir.exists()){
                    if(!baseDir.mkdirs()){
                        throw new IOException("PersistDir can't make." + baseDir.getAbsolutePath());
                    }
                }
            }
        }
        
        final Date standardDate = getStandardTimeDate(schedule.getTime());
        final SimpleDateFormat format = new SimpleDateFormat(DATE_DIR_FORMAT);
        final File dateDir = new File(baseDir, format.format(standardDate));
        if(!dateDir.exists()){
            synchronized(persistDir){
                if(!dateDir.exists()){
                    if(!dateDir.mkdir()){
                        throw new IOException("Date Directory can't make." + dateDir.getAbsolutePath());
                    }
                }
            }
        }
        
        final File scheduleFile = new File(dateDir, schedule.getId());
        synchronized(schedule){
            CSVWriter writer = null;
            try{
                writer = new CSVWriter(new FileWriter(scheduleFile));
                writer.writeElement(schedule.getId());
                writer.writeElement(schedule.getMasterId());
                if(schedule.getMasterGroupIds() == null){
                    writer.writeElement("");
                }else{
                    final StringArrayEditor editor = new StringArrayEditor();
                    editor.setValue(schedule.getMasterGroupIds());
                    writer.writeElement(editor.getAsText());
                }
                format.applyPattern(DATE_CSV_FORMAT);
                writer.writeElement(format.format(schedule.getTime()));
                writer.writeElement(schedule.getTaskName());
                if(schedule.getInput() == null){
                    writer.writeElement("");
                }else{
                    final StringBuilder buf = new StringBuilder();
                    final PropertyEditor editor
                        = NimbusPropertyEditorManager.findEditor(
                            schedule.getInput().getClass()
                        );
                    buf.append(schedule.getInput().getClass().getName());
                    buf.append(':');
                    if(editor == null){
                        buf.append(schedule.getInput());
                    }else{
                        editor.setValue(schedule.getInput());
                        buf.append(editor.getAsText());
                    }
                    writer.writeElement(buf.toString());
                }
                if(schedule.getDepends() == null){
                    writer.writeElement("");
                    writer.writeElement("");
                }else{
                    ScheduleDepends[] depends = schedule.getDepends();
                    String[] masterIdArray = new String[depends.length];
                    boolean[] isIgnoreErrorArray = new boolean[depends.length];
                    for(int i = 0; i < depends.length; i++){
                        masterIdArray[i] = depends[i].getMasterId();
                        isIgnoreErrorArray[i] = depends[i].isIgnoreError();
                    }
                    PropertyEditor editor = new StringArrayEditor();
                    editor.setValue(masterIdArray);
                    writer.writeElement(editor.getAsText());
                    editor = new BooleanArrayEditor();
                    editor.setValue(isIgnoreErrorArray);
                    writer.writeElement(editor.getAsText());
                }
                if(schedule.getOutput() == null){
                    writer.writeElement("");
                }else{
                    final StringBuilder buf = new StringBuilder();
                    final PropertyEditor editor
                        = NimbusPropertyEditorManager.findEditor(
                            schedule.getOutput().getClass()
                        );
                    buf.append(schedule.getOutput().getClass().getName());
                    buf.append(':');
                    if(editor == null){
                        buf.append(schedule.getOutput());
                    }else{
                        editor.setValue(schedule.getOutput());
                        buf.append(editor.getAsText());
                    }
                    writer.writeElement(buf.toString());
                }
                writer.writeElement(format.format(schedule.getInitialTime()));
                writer.writeElement(schedule.getRetryInterval());
                if(schedule.getRetryEndTime() == null){
                    writer.writeElement("");
                }else{
                    writer.writeElement(format.format(schedule.getRetryEndTime()));
                }
                writer.writeElement(schedule.getMaxDelayTime());
                writer.writeElement(schedule.isRetry());
                writer.writeElement(schedule.getState());
                writer.writeElement(schedule.getControlState());
                writer.writeElement(schedule.getCheckState());
                if(schedule.getExecutorKey() == null){
                    writer.writeElement("");
                }else{
                    writer.writeElement(schedule.getExecutorKey());
                }
                if(schedule.getExecutorType() == null){
                    writer.writeElement("");
                }else{
                    writer.writeElement(schedule.getExecutorType());
                }
                if(schedule.getExecuteStartTime() == null){
                    writer.writeElement("");
                }else{
                    writer.writeElement(format.format(schedule.getExecuteStartTime()));
                }
                if(schedule.getExecuteEndTime() == null){
                    writer.writeElement("");
                }else{
                    writer.writeElement(format.format(schedule.getExecuteEndTime()));
                }
            }finally{
                if(writer != null){
                    writer.close();
                }
            }
        }
    }
    
    /**
     * 永続化されたローカル通番を読み込む。<p>
     *
     * @exception IOException ローカル通番の読み込みに失敗した場合
     */
    protected void loadLocalSequenceNumber() throws IOException{
        if(persistDir == null){
            return;
        }
        final File baseDir = new File(persistDir);
        if(!baseDir.exists()){
            return;
        }
        final File file = new File(baseDir, LOCAL_SEQUENCE_NUMBER_FILE);
        if(!file.exists()){
            return;
        }
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            final String line = reader.readLine();
            if(line != null){
                sequenceNumber = Long.parseLong(line);
            }
        }finally{
            if(reader != null){
                reader.close();
            }
        }
    }
    
    /**
     * 永続化されたスケジュールを読み込む。<p>
     *
     * @exception IOException スケジュールの読み込みに失敗した場合
     */
    protected void loadSchedules() throws Exception{
        if(persistDir == null){
            return;
        }
        final File baseDir = new File(persistDir);
        if(!baseDir.exists()){
            return;
        }
        final File[] dateDirs = baseDir.listFiles();
        if(dateDirs == null || dateDirs.length == 0){
            return;
        }
        final SimpleDateFormat dateDirFormat
            = new SimpleDateFormat(DATE_DIR_FORMAT);
        final SimpleDateFormat dateCsvFormat
            = new SimpleDateFormat(DATE_CSV_FORMAT);
        for(int i = 0; i < dateDirs.length; i++){
            if(!dateDirs[i].isDirectory()){
                continue;
            }
            try{
                dateDirFormat.parse(dateDirs[i].getName());
            }catch(ParseException e){
                continue;
            }
            final File[] scheduleFiles = dateDirs[i].listFiles();
            if(scheduleFiles == null || scheduleFiles.length == 0){
                continue;
            }
            List csv = null;
            for(int j = 0; j < scheduleFiles.length; j++){
                CSVReader reader = null;
                try{
                    reader = new CSVReader(new FileReader(scheduleFiles[j]));
                    csv = reader.readCSVLineList(csv);
                    final DefaultSchedule schedule = new DefaultSchedule();
                    int index = 0;
                    schedule.setId((String)csv.get(index++));
                    schedule.setMasterId((String)csv.get(index++));
                    final String masterGroupIdsStr = (String)csv.get(index++);
                    if(masterGroupIdsStr.length() == 0){
                        schedule.setMasterGroupIds(null);
                    }else{
                        final StringArrayEditor editor = new StringArrayEditor();
                        editor.setAsText(masterGroupIdsStr);
                        schedule.setMasterGroupIds((String[])editor.getValue());
                    }
                    schedule.setTime(dateCsvFormat.parse((String)csv.get(index++)));
                    schedule.setTaskName((String)csv.get(index++));
                    final String inputStr = (String)csv.get(index++);
                    if(inputStr.length() == 0){
                        schedule.setInput(null);
                    }else{
                        final int sepIndex = inputStr.indexOf(':');
                        final Class clazz = convertStringToClass(
                            inputStr.substring(0, sepIndex)
                        );
                        final PropertyEditor editor
                            = NimbusPropertyEditorManager.findEditor(clazz);
                        if(sepIndex == inputStr.length() - 1){
                            schedule.setInput(null);
                        }else{
                            editor.setAsText(
                                inputStr.substring(sepIndex + 1)
                            );
                            schedule.setInput(editor.getValue());
                        }
                    }
                    final String dependsMasterIdStr = (String)csv.get(index++);
                    final String dependsIsIgnoreErrorStr = (String)csv.get(index++);
                    if(dependsMasterIdStr.length() == 0){
                        schedule.setDepends(null);
                    }else{
                        PropertyEditor editor = new StringArrayEditor();
                        editor.setAsText(dependsMasterIdStr);
                        String[] dependsMasterIdArray = (String[])editor.getValue();
                        editor = new BooleanArrayEditor();
                        editor.setAsText(dependsIsIgnoreErrorStr);
                        boolean[] dependsIsIgnoreErrorArray = (boolean[])editor.getValue();
                        ScheduleDepends[] depends = new ScheduleDepends[dependsMasterIdArray.length];
                        for(int k = 0; k < dependsMasterIdArray.length; k++){
                            depends[k] = new DefaultScheduleDepends(dependsMasterIdArray[k], dependsIsIgnoreErrorArray[k]);
                        }
                        schedule.setDepends(depends);
                    }
                    final String outputStr = (String)csv.get(index++);
                    if(outputStr.length() == 0){
                        schedule.setOutput(null);
                    }else{
                        final int sepIndex = outputStr.indexOf(':');
                        Class clazz = null;
                        try{
                            clazz = convertStringToClass(
                                outputStr.substring(0, sepIndex)
                            );
                            final PropertyEditor editor
                                = NimbusPropertyEditorManager.findEditor(clazz);
                            if(sepIndex == outputStr.length() - 1 || editor == null){
                                schedule.setOutput(outputStr);
                            }else{
                                editor.setAsText(
                                    outputStr.substring(sepIndex + 1)
                                );
                                schedule.setOutput(editor.getValue());
                            }
                        }catch(ClassNotFoundException e){
                            schedule.setOutput(outputStr);
                        }
                    }
                    schedule.setInitialTime(
                        dateCsvFormat.parse((String)csv.get(index++))
                    );
                    schedule.setRetryInterval(
                        Long.parseLong((String)csv.get(index++))
                    );
                    final String retryEndTimeStr = (String)csv.get(index++);
                    if(retryEndTimeStr != null
                         && retryEndTimeStr.length() != 0){
                        schedule.setRetryEndTime(
                            dateCsvFormat.parse(retryEndTimeStr)
                        );
                    }
                    schedule.setMaxDelayTime(
                        Long.parseLong((String)csv.get(index++))
                    );
                    schedule.setRetry(
                        Boolean.valueOf((String)csv.get(index++)).booleanValue()
                    );
                    schedule.setState(Integer.parseInt((String)csv.get(index++)));
                    schedule.setControlState(
                        Integer.parseInt((String)csv.get(index++))
                    );
                    schedule.setCheckState(
                        Integer.parseInt((String)csv.get(index++))
                    );
                    final String executorKey = (String)csv.get(index++);
                    if(executorKey != null && executorKey.length() == 0){
                        schedule.setExecutorKey(null);
                    }else{
                        schedule.setExecutorKey(executorKey);
                    }
                    final String executorType = (String)csv.get(index++);
                    if(executorType != null && executorType.length() == 0){
                        schedule.setExecutorType(null);
                    }else{
                        schedule.setExecutorType(executorType);
                    }
                    final String executeStartTimeStr = (String)csv.get(index++);
                    if(executeStartTimeStr != null
                         && executeStartTimeStr.length() != 0){
                        schedule.setExecuteStartTime(
                            dateCsvFormat.parse(executeStartTimeStr)
                        );
                    }
                    final String executeEndTimeStr = (String)csv.get(index++);
                    if(executeEndTimeStr != null
                         && executeEndTimeStr.length() != 0){
                        schedule.setExecuteEndTime(
                            dateCsvFormat.parse(executeEndTimeStr)
                        );
                    }
                    addSchedule(schedule, false, false);
                }finally{
                    if(reader != null){
                        reader.close();
                    }
                }
            }
        }
    }
    
    /**
     * 指定されたクラス名からクラスオブジェクトに変換する。<p>
     *
     * @param typeStr クラス名
     * @return クラスオブジェクト
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    protected Class convertStringToClass(String typeStr)
     throws ClassNotFoundException{
        Class type = null;
        if(typeStr != null){
            if(Byte.TYPE.getName().equals(typeStr)){
                type = Byte.TYPE;
            }else if(Character.TYPE.getName().equals(typeStr)){
                type = Character.TYPE;
            }else if(Short.TYPE.getName().equals(typeStr)){
                type = Short.TYPE;
            }else if(Integer.TYPE.getName().equals(typeStr)){
                type = Integer.TYPE;
            }else if(Long.TYPE.getName().equals(typeStr)){
                type = Long.TYPE;
            }else if(Float.TYPE.getName().equals(typeStr)){
                type = Float.TYPE;
            }else if(Double.TYPE.getName().equals(typeStr)){
                type = Double.TYPE;
            }else if(Boolean.TYPE.getName().equals(typeStr)){
                type = Boolean.TYPE;
            }else{
                if(typeStr.endsWith(ARRAY_CLASS_SUFFIX)
                    && typeStr.length() > 2){
                    final Class elementType = convertStringToClass(
                        typeStr.substring(0, typeStr.length() - 2)
                    );
                    type = Array.newInstance(elementType, 0).getClass();
                }else{
                    type = Class.forName(
                        typeStr,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                }
            }
        }
        return type;
    }
    
    /**
     * 永続化されたスケジュールを削除する。<p>
     *
     * @param schedule スケジュール
     * @exception IOException スケジュールの削除に失敗した場合
     */
    protected void removePersistSchedule(Schedule schedule)
     throws IOException{
        if(persistDir == null){
            return;
        }
        final File baseDir = new File(persistDir);
        if(!baseDir.exists()){
            return;
        }
        
        final Date standardDate = getStandardTimeDate(schedule.getTime());
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final File dateDir = new File(baseDir, format.format(standardDate));
        if(!dateDir.exists()){
            return;
        }
        
        final File scheduleFile = new File(dateDir, schedule.getId());
        synchronized(schedule){
            if(scheduleFile.exists()){
                if(!scheduleFile.delete()){
                    throw new IOException("Persist file can't delete." + scheduleFile.getAbsolutePath());
                }
            }
        }
        final File[] scheduleFiles = dateDir.listFiles();
        if(scheduleFiles == null || scheduleFiles.length == 0){
            dateDir.delete();
        }
    }
    
    // ScheduleManagerのJavaDoc
    public boolean reschedule(String id, Date time, Object output)
     throws ScheduleManageException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            return false;
        }
        if(!removeSchedule(id)){
            return false;
        }
        schedule.setTime(time);
        schedule.setCheckState(Schedule.CHECK_STATE_INITIAL);
        schedule.setOutput(output);
        addSchedule(schedule, false, true);
        return true;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean removeSchedules(List ids) throws ScheduleManageException{
        boolean result = true;
        Iterator itr = ids.iterator();
        while(itr.hasNext()){
            result &= removeSchedule((String)itr.next());
        }
        return result;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean removeSchedule(String id) throws ScheduleManageException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            return false;
        }
        try{
            removePersistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }
        scheduleMap.remove(schedule.getId());
        synchronized(scheduleList){
            scheduleList.remove(schedule);
        }
        synchronized(scheduleMasterMap){
            Set scheduleSet = (Set)scheduleMasterMap.get(schedule.getMasterId());
            if(scheduleSet != null){
                synchronized(scheduleSet){
                    scheduleSet.remove(schedule);
                }
                if(scheduleSet.size() == 0){
                    scheduleMasterMap.remove(schedule.getMasterId());
                }
            }
        }
        synchronized(scheduleGroupMap){
            Iterator itr = schedule.getGroupIdMap().values().iterator();
            while(itr.hasNext()){
                String groupId = (String)itr.next();
                Set scheduleSet = (Set)scheduleGroupMap.get(groupId);
                if(scheduleSet != null){
                    synchronized(scheduleSet){
                        scheduleSet.remove(schedule);
                    }
                    if(scheduleSet.size() == 0){
                        scheduleGroupMap.remove(groupId);
                    }
                }
            }
        }
        synchronized(scheduleGroupMasterMap){
            String[] groupIds = schedule.getMasterGroupIds();
            if(groupIds != null){
                for(int i = 0; i < groupIds.length; i++){
                    Set scheduleSet = (Set)scheduleGroupMasterMap.get(groupIds[i]);
                    if(scheduleSet != null){
                        synchronized(scheduleSet){
                            scheduleSet.remove(schedule);
                        }
                        if(scheduleSet.size() == 0){
                            scheduleGroupMasterMap.remove(groupIds[i]);
                        }
                    }
                }
            }
        }
        synchronized(scheduleDependedMap){
            final Iterator dependedItr = scheduleDependedMap.values().iterator();
            while(dependedItr.hasNext()){
                Set depended = (Set)dependedItr.next();
                depended.remove(schedule.getId());
            }
        }
        final Date standardDate = getStandardTimeDate(schedule.getTime());
        synchronized(scheduleDateMap){
            List schedules = (List)scheduleDateMap.get(standardDate);
            if(schedules != null){
                synchronized(schedules){
                    schedules.remove(schedule);
                }
                if(schedules.size() == 0){
                    scheduleDateMap.remove(standardDate);
                }
            }
        }
        return true;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean removeScheduleByMasterId(String masterId, String masterGroupId) throws ScheduleManageException{
        Set scheduleSet = null;
        if(masterId != null){
            scheduleSet = (Set)scheduleMasterMap.get(masterId);
        }else{
            scheduleSet = (Set)scheduleGroupMap.get(masterGroupId);
        }
        if(scheduleSet == null){
            return false;
        }
        Schedule[] schedules = null;
        synchronized(scheduleSet){
            schedules = (Schedule[])scheduleSet.toArray(
                new Schedule[scheduleSet.size()]
            );
        }
        boolean isRemove = false;
        for(int i = 0; i < schedules.length; i++){
            isRemove |= removeSchedule(schedules[i].getId());
        }
        return isRemove;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean removeSchedule(Date date) throws ScheduleManageException{
        final Date standardDate = getStandardTimeDate(date);
        Schedule[] schedules = null;
        synchronized(scheduleDateMap){
            List scheduleList = (List)scheduleDateMap.get(standardDate);
            if(scheduleList == null || scheduleList.size() == 0){
                return false;
            }
            synchronized(scheduleList){
                schedules = (Schedule[])scheduleList.toArray(
                    new Schedule[scheduleList.size()]
                );
            }
        }
        boolean isRemove = false;
        for(int i = 0; i < schedules.length; i++){
            isRemove |= removeSchedule(schedules[i].getId());
        }
        return isRemove;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean removeSchedule(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId) throws ScheduleManageException{
        List schedules = findSchedules(from, to, states, masterId, masterGroupId, groupId, -1);
        boolean isRemove = false;
        for(int i = 0, imax = schedules.size(); i < imax; i++){
            isRemove |= removeSchedule(((Schedule)schedules.get(i)).getId());
        }
        return isRemove;
    }
    
    // ScheduleManagerのJavaDoc
    public void setExecutorKey(String id, String key) throws ScheduleManageException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleManageException("Schedule not found : " + id);
        }
        schedule.setExecutorKey(key);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }
    }
    
    // ScheduleManagerのJavaDoc
    public void setRetryEndTime(String id, Date time) throws ScheduleManageException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleManageException("Schedule not found : " + id);
        }
        schedule.setRetryEndTime(time);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }
    }
    
    // ScheduleManagerのJavaDoc
    public void setMaxDelayTime(String id, long time) throws ScheduleManageException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleManageException("Schedule not found : " + id);
        }
        schedule.setMaxDelayTime(time);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleManageException(e);
        }
    }
    
    // ScheduleManagerのJavaDoc
    public int getState(String id) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        return schedule.getState();
    }
    
    // ScheduleManagerのJavaDoc
    public int getControlState(String id) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        return schedule.getControlState();
    }
    
    // ScheduleManagerのJavaDoc
    public boolean changeState(String id, int state) throws ScheduleStateControlException{
        return changeState(id, state, null);
    }
    
    // ScheduleManagerのJavaDoc
    public boolean changeState(String id, int oldState, int newState) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        switch(newState){
        case Schedule.STATE_RUN:
            schedule.setExecuteStartTime(new Date());
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            schedule.setExecuteEndTime(new Date());
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            schedule.setExecuteStartTime(null);
            schedule.setExecuteEndTime(null);
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + newState);
        }
        if(oldState != schedule.getState()){
            return false;
        }
        final boolean isChange = schedule.getState() != newState;
        schedule.setState(newState);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }
        return isChange;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean changeState(String id, int oldState, int newState, Object output) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        switch(newState){
        case Schedule.STATE_RUN:
            schedule.setExecuteStartTime(new Date());
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            schedule.setExecuteEndTime(new Date());
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            schedule.setExecuteStartTime(null);
            schedule.setExecuteEndTime(null);
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + newState);
        }
        if(oldState != schedule.getState()){
            return false;
        }
        schedule.setOutput(output);
        final boolean isChange = schedule.getState() != newState;
        schedule.setState(newState);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }
        return isChange;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean changeState(String id, int state, Object output) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        switch(state){
        case Schedule.STATE_RUN:
            schedule.setExecuteStartTime(new Date());
            break;
        case Schedule.STATE_END:
        case Schedule.STATE_FAILED:
        case Schedule.STATE_ABORT:
            schedule.setExecuteEndTime(new Date());
            break;
        case Schedule.STATE_INITIAL:
        case Schedule.STATE_ENTRY:
            schedule.setExecuteStartTime(null);
            schedule.setExecuteEndTime(null);
            break;
        case Schedule.STATE_RETRY:
        case Schedule.STATE_PAUSE:
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + state);
        }
        schedule.setOutput(output);
        final boolean isChange = schedule.getState() != state;
        schedule.setState(state);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }
        return isChange;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean changeControlState(String id, int oldState, int newState) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        switch(newState){
        case Schedule.CONTROL_STATE_PAUSE:
            if(getState(id) != Schedule.STATE_RUN){
                return false;
            }
            break;
        case Schedule.CONTROL_STATE_RESUME:
            if(getState(id) != Schedule.STATE_PAUSE){
                return false;
            }
            break;
        case Schedule.CONTROL_STATE_ABORT:
            if(getState(id) != Schedule.STATE_RUN){
                return false;
            }
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + newState);
        }
        if(oldState != schedule.getControlState()){
            return false;
        }
        final boolean isChange = schedule.getControlState() != newState;
        if(!isChange){
            return false;
        }
        schedule.setControlState(newState);
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }
        try{
            if(scheduleControlListeners != null
                 && scheduleControlListeners.size() != 0){
                synchronized(scheduleControlListeners){
                    final Iterator itr = scheduleControlListeners.iterator();
                    while(itr.hasNext()){
                        final ScheduleControlListener listener
                            = (ScheduleControlListener)itr.next();
                        listener.changedControlState(id, newState);
                    }
                }
            }
        }catch(ScheduleStateControlException e){
            schedule.setControlState(Schedule.CONTROL_STATE_FAILED);
            throw e;
        }
        return isChange;
    }
    
    // ScheduleManagerのJavaDoc
    public boolean changeControlState(String id, int state) throws ScheduleStateControlException{
        final Schedule schedule = (Schedule)scheduleMap.get(id);
        if(schedule == null){
            throw new ScheduleStateControlException("Schedule not found : " + id);
        }
        switch(state){
        case Schedule.CONTROL_STATE_PAUSE:
            if(getState(id) != Schedule.STATE_RUN){
                return false;
            }
            break;
        case Schedule.CONTROL_STATE_RESUME:
            if(getState(id) != Schedule.STATE_PAUSE){
                return false;
            }
            break;
        case Schedule.CONTROL_STATE_ABORT:
            if(getState(id) != Schedule.STATE_RUN){
                return false;
            }
            break;
        default:
            throw new ScheduleStateControlException("Unknown state : " + state);
        }
        final boolean isChange = schedule.getControlState() != state;
        if(!isChange){
            return false;
        }
        try{
            persistSchedule(schedule);
        }catch(IOException e){
            throw new ScheduleStateControlException(e);
        }
        schedule.setControlState(state);
        try{
            if(scheduleControlListeners != null
                 && scheduleControlListeners.size() != 0){
                synchronized(scheduleControlListeners){
                    final Iterator itr = scheduleControlListeners.iterator();
                    while(itr.hasNext()){
                        final ScheduleControlListener listener
                            = (ScheduleControlListener)itr.next();
                        listener.changedControlState(id, state);
                    }
                }
            }
        }catch(ScheduleStateControlException e){
            schedule.setControlState(Schedule.CONTROL_STATE_FAILED);
            throw e;
        }
        return isChange;
    }
    
    // ScheduleManagerのJavaDoc
    public void addScheduleControlListener(ScheduleControlListener listener){
        scheduleControlListeners.add(listener);
    }
    
    // ScheduleManagerのJavaDoc
    public void removeScheduleControlListener(ScheduleControlListener listener){
        scheduleControlListeners.remove(listener);
    }
    
    /**
     * タイムオーバー監視。<p>
     *
     * @author M.Takata
     */
    protected class TimeoverChecker implements DaemonRunnable{
        
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
            ctrl.sleep(getTimeoverCheckInterval(), true);
            return null;
        }
        
        /**
         * 最大遅延時間をチェックする。<p>
         *
         * @param input null
         * @param ctrl DaemonControlオブジェクト
         */
        public void consume(Object input, DaemonControl ctrl)
         throws Throwable{
            
            final Calendar nowCal = Calendar.getInstance();
            List schedules = findSchedules(nowCal.getTime(), (Date)null);
            Calendar tmpCal = Calendar.getInstance();
            for(int i = 0, imax = schedules.size(); i < imax; i++){
                Schedule schedule = (Schedule)schedules.get(i);
                tmpCal.clear();
                tmpCal.setTime(schedule.getTime());
                if(schedule.getCheckState() != Schedule.CHECK_STATE_TIMEOVER
                     && tmpCal.before(nowCal)
                     && schedule.getMaxDelayTime() > 0
                     && schedule.getState() != Schedule.STATE_END
                     && schedule.getState() != Schedule.STATE_DISABLE
                ){
                    tmpCal.clear();
                    tmpCal.setTimeInMillis(
                        schedule.getTime().getTime()
                            + schedule.getMaxDelayTime()
                    );
                    if(tmpCal.after(nowCal) || tmpCal.equals(nowCal)){
                        continue;
                    }
                    schedule.setCheckState(Schedule.CHECK_STATE_TIMEOVER);
                    getLogger().write(
                        MSG_ID_TIMEOVER_ERROR,
                        new Object[]{
                            schedule.getId(),
                            new Integer(schedule.getState())
                        }
                    );
                }
            }
        }
        
        /**
         * 何もしない。<p>
         */
        public void garbage(){
        }
    }
}