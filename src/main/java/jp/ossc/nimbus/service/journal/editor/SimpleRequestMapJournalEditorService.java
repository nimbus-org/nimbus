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
package jp.ossc.nimbus.service.journal.editor;

import java.io.Serializable;
import java.util.*;

import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link RequestJournal}をMapフォーマットする簡易エディタ。<p>
 * このエディタによって編集されたMapは、以下の構造を持つ。<br>
 * <table broder="1">
 *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="5">値</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th colspan="4">内容</th></tr>
 *   <tr><td>java.lang.String</td><td>{@link #REQUEST_ID_KEY}</td><td>java.lang.String</td><td colspan="4">リクエストID</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #START_TIME_KEY}</td><td>java.util.Date</td><td colspan="4">開始時刻</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #END_TIME_KEY}</td><td>java.util.Date</td><td colspan="4">終了時刻</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link #PERFORMANCE_KEY}</td><td>java.lang.Long</td><td colspan="4">処理時間[ms]</td></tr>
 *   <tr><td rowspan="3">java.lang.String</td><td rowspan="3">{@link #RECORDS_KEY}</td><td rowspan="3">java.util.SortedMap</td><td colspan="4">JournalRecordの編集結果のマップ</td></tr>
 *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
 *   <tr><td>java.lang.String</td><td>JournalRecordのキー</td><td>java.util.List</td><td>JournalRecordの編集結果のObjectのリスト。重複するJournalRecordのキーが存在しない場合は、このリストのサイズは１。</td></tr>
 * </table>
 * 但し、出力しないように設定されているものや、元のRequestJournalに含まれていなかった情報は含まれない。<br>
 * 
 * @author M.Takata
 */
public class SimpleRequestMapJournalEditorService
 extends MapJournalEditorServiceBase
 implements SimpleRequestMapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -7674383604884181656L;
    
    private boolean isOutputJournalKey = true;
    private boolean isOutputRequestId = true;
    private boolean isOutputStartTime = true;
    private boolean isOutputRecords = true;
    private boolean isOutputEndTime = true;
    private boolean isOutputPerformance = true;
    
    private String[] outputRecordKeys;
    
    public void setOutputJournalKey(boolean isOutput){
        isOutputJournalKey = isOutput;
    }
    
    public boolean isOutputJournalKey(){
        return isOutputJournalKey;
    }
    
    public void setOutputRequestId(boolean isOutput){
        isOutputRequestId = isOutput;
    }
    
    public boolean isOutputRequestId(){
        return isOutputRequestId;
    }
    
    public void setOutputStartTime(boolean isOutput){
        isOutputStartTime = isOutput;
    }
    
    public boolean isOutputStartTime(){
        return isOutputStartTime;
    }
    
    public void setOutputRecords(boolean isOutput){
        isOutputRecords = isOutput;
    }
    
    public boolean isOutputRecords(){
        return isOutputRecords;
    }
    
    public void setOutputEndTime(boolean isOutput){
        isOutputEndTime = isOutput;
    }
    
    public boolean isOutputEndTime(){
        return isOutputEndTime;
    }
    
    public void setOutputPerformance(boolean isOutput){
        isOutputPerformance = isOutput;
    }
    
    public boolean isOutputPerformance(){
        return isOutputPerformance;
    }
    
    public void setOutputRecordKeys(String[] keys){
        outputRecordKeys = keys;
    }
    
    public String[] getOutputRecordKeys(){
        return outputRecordKeys;
    }
    
    /**
     * ジャーナルとして与えられたRequestJournal型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public Map toMap(EditorFinder finder, Object key, Object value){
        final RequestJournal request = (RequestJournal)value;
        final Map result = new HashMap();
        
        if(isOutputJournalKey()){
            makeJournalKeyFormat(finder, key, request, result);
        }
        if(isOutputRequestId()){
            makeRequestIdFormat(finder, key, request, result);
        }
        if(isOutputStartTime()){
            makeStartTimeFormat(finder, key, request, result);
        }
        if(isOutputRecords()){
            makeRecordsFormat(finder, key, request, result);
        }
        if(isOutputEndTime()){
            makeEndTimeFormat(finder, key, request, result);
        }
        if(isOutputPerformance()){
            makePerformanceFormat(finder, key, request, result);
        }
        return result;
    }
    
    protected Map makeJournalKeyFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        Map map
    ){
        map.put(JOURNAL_KEY_KEY, request.getKey());
        return map;
    }
    
    protected Map makeRequestIdFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        Map map
    ){
        map.put(REQUEST_ID_KEY, request.getRequestId());
        return map;
    }
    
    protected Map makeStartTimeFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        Map map
    ){
        map.put(
            START_TIME_KEY,
            makeObjectFormat(finder, key, request.getStartTime())
        );
        return map;
    }
    
    protected Map makeRecordsFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        Map map
    ){
        JournalRecord[] records = request.getParamAry();
        if(records.length == 0){
            return map;
        }
        final SortedMap subMap = new TreeMap();
        if(outputRecordKeys == null){
            records = request.getParamAry();
            makeRecordsFormat(finder, records, subMap);
        }else{
            for(int i = 0, max = outputRecordKeys.length; i < max; i++){
                records = request.findParamArys(outputRecordKeys[i]);
                if(records != null && records.length != 0){
                    makeRecordsFormat(finder, records, subMap);
                }
            }
        }
        map.put(RECORDS_KEY, subMap);
        return map;
    }
    
    private Map makeRecordsFormat(
        EditorFinder finder,
        JournalRecord[] records,
        SortedMap map
    ){
        for(int i = 0, max = records.length; i < max; i++){
            if(!map.containsKey(records[i].getKey())){
                final List list = new ArrayList();
                list.add(records[i].toObject());
                map.put(records[i].getKey(), list);
            }else{
                ((List)map.get(records[i].getKey())).add(records[i].toObject());
            }
        }
        return map;
    }
    
    protected Map makeEndTimeFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        Map map
    ){
        map.put(
            END_TIME_KEY,
            makeObjectFormat(finder, key, request.getEndTime())
        );
        return map;
    }
    
    protected Map makePerformanceFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        Map map
    ){
        map.put(PERFORMANCE_KEY, new Long(request.getPerformance()));
        return map;
    }
}
