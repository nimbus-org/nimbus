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
package jp.ossc.nimbus.service.test.action;

import java.io.Reader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.scheduler2.ScheduleManager;
import jp.ossc.nimbus.service.scheduler2.Schedule;

/**
 * {@link ScheduleManager}を使って、スケジュールを検索するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class ScheduleFindActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, ScheduleFindActionServiceMBean{
    
    protected ServiceName scheduleManagerServiceName;
    protected ScheduleManager scheduleManager;
    protected double expectedCost = Double.NaN;
    
    public void setScheduleManagerServiceName(ServiceName name){
        scheduleManagerServiceName = name;
    }
    public ServiceName getScheduleManagerServiceName(){
        return scheduleManagerServiceName;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
    
    public void startService() throws Exception{
        if(scheduleManagerServiceName != null){
            scheduleManager = (ScheduleManager)ServiceManagerFactory.getServiceObject(scheduleManagerServiceName);
        }
        if(scheduleManager == null){
            throw new IllegalArgumentException("ScheduleManager is null.");
        }
    }
    
    /**
     * リソースの内容を読み込んで、スケジュールを検索する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * scheduleActionId
     * </pre>
     * scheduleActionIdは、スケジュールを作成した{@link ScheduleMakeActionService}のアクションIDを指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 生成されたスケジュールのリスト
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、スケジュールを検索する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * scheduleActionId
     * </pre>
     * scheduleActionIdは、スケジュールを作成した{@link ScheduleMakeActionService}のアクションIDを指定する。指定しない場合は、preResultを使用する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult スケジュールのリスト
     * @param resource リソース
     * @return 検索したスケジュール
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        List findSchedules = new ArrayList();
        try{
            final String scheduleActionId = br.readLine();
            if(scheduleActionId == null || scheduleActionId.length() == 0){
                throw new Exception("Unexpected EOF on scheduleActionId");
            }
            if(preResult == null && (scheduleActionId == null || scheduleActionId.length() == 0)){
                throw new Exception("Unexpected EOF on scheduleActionId");
            }
            if(scheduleActionId != null && scheduleActionId.length() != 0){
                Object actionResult = null;
                if(scheduleActionId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(scheduleActionId);
                }else{
                    String[] ids = scheduleActionId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal scheduleActionId format. id=" + scheduleActionId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + scheduleActionId);
                }
                if(!(actionResult instanceof List) || (((List)actionResult).size() != 0 && !(((List)actionResult).get(0) instanceof Schedule))){
                    throw new Exception("TestActionResult not instance of Schedule List. result=" + actionResult);
                }
                findSchedules.addAll((List)actionResult);
            }else{
                if(preResult == null){
                    throw new Exception("preResult is null.");
                }
                if(!(preResult instanceof List) || (((List)preResult).size() != 0 && !(((List)preResult).get(0) instanceof Schedule))){
                    throw new Exception("preResult not instance of Schedule List. result=" + preResult);
                }
                findSchedules.addAll((List)preResult);
            }
        }finally{
            br.close();
            br = null;
        }
        
        List result = new ArrayList();
        Iterator itr = findSchedules.iterator();
        while(itr.hasNext()){
            String id = ((Schedule)itr.next()).getId();
            Schedule schedule = scheduleManager.findSchedule(id);
            if(schedule != null){
                result.add(schedule);
            }
        }
        return result;
    }
}