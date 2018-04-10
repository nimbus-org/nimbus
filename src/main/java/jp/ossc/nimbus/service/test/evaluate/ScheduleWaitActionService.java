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
package jp.ossc.nimbus.service.test.evaluate;

import java.io.Reader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.action.ScheduleMakeActionService;
import jp.ossc.nimbus.service.test.ChainEvaluateTestAction;
import jp.ossc.nimbus.service.test.EvaluateTestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.scheduler2.Schedule;
import jp.ossc.nimbus.service.scheduler2.ScheduleManager;

/**
 * {@link ScheduleMakeActionService}で作成したスケジュールの終了を待ち合わせるテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class ScheduleWaitActionService extends ServiceBase implements EvaluateTestAction, TestActionEstimation, ChainEvaluateTestAction.EvaluateTestActionProcess, ScheduleWaitActionServiceMBean{
    
    private static final long serialVersionUID = -6269182775651897011L;
    
    protected ServiceName scheduleManagerServiceName;
    protected ScheduleManager scheduleManager;
    protected long waitInterval = 1000;
    protected double expectedCost = Double.NaN;
    
    public void setScheduleManagerServiceName(ServiceName name){
        scheduleManagerServiceName = name;
    }
    public ServiceName getScheduleManagerServiceName(){
        return scheduleManagerServiceName;
    }
    
    public void setWaitInterval(long interval){
        waitInterval = interval;
    }
    public long getWaitInterval(){
        return waitInterval;
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
     * リソースの内容を読み込んで、{@link ScheduleMakeActionService}で作成したスケジュールの終了を待ち合わせる。<p>
     * 待ち合わせに失敗した場合は、例外をthrowする。<br>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * waitScheduleId
     * timeout
     * waitStates
     * successStates
     * </pre>
     * waitScheduleIdは、スケジュールを作成した{@link ScheduleMakeActionService}のアクションIDを指定する。<br>
     * timeoutは、スケジュールの待ち合わせタイムアウト[ms]を指定する。<br>
     * waitStatesは、スケジュールを待ち合わせするスケジュール状態を指定する。複数指定する場合は、カンマ区切りで指定する。指定可能な、スケジュール状態は、END、FAILED、RETRY。指定しない場合は、END、FAILEDで待ち合わせる。<br>
     * successStatesは、待ち合わせた後、戻り値でtrueを返すスケジュール状態を指定する。複数指定する場合は、カンマ区切りで指定する。指定可能な、スケジュール状態は、END、FAILED、RETRY。指定しない場合は、ENDの場合、trueを返す。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 待ち合わせ結果
     */
    public boolean execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、{@link ScheduleMakeActionService}で作成したスケジュールの終了を待ち合わせる。<p>
     * 待ち合わせに失敗した場合は、例外をthrowする。<br>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * waitScheduleId
     * timeout
     * waitStates
     * successStates
     * </pre>
     * waitScheduleIdは、スケジュールを作成した{@link ScheduleMakeActionService}のアクションIDを指定する。指定しない場合は、preResultを使用する。<br>
     * timeoutは、スケジュールの待ち合わせタイムアウト[ms]を指定する。<br>
     * waitStatesは、スケジュールを待ち合わせするスケジュール状態を指定する。複数指定する場合は、カンマ区切りで指定する。指定可能な、スケジュール状態は、END、FAILED、RETRY。指定しない場合は、END、FAILEDで待ち合わせる。<br>
     * successStatesは、待ち合わせた後、戻り値でtrueを返すスケジュール状態を指定する。複数指定する場合は、カンマ区切りで指定する。指定可能な、スケジュール状態は、END、FAILED、RETRY。指定しない場合は、ENDの場合、trueを返す。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 直前のアクションの結果
     * @param resource リソース
     * @return 待ち合わせ結果
     */
    public boolean execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        List waitSchedules = new ArrayList();
        long timeout = 0;
        int[] waitStates = new int[]{Schedule.STATE_END, Schedule.STATE_FAILED};
        int[] successStates = new int[]{Schedule.STATE_END};
        try{
            final String waitScheduleId = br.readLine();
            if(waitScheduleId == null || waitScheduleId.length() == 0){
                throw new Exception("Unexpected EOF on waitScheduleId");
            }
            if(preResult == null && (waitScheduleId == null || waitScheduleId.length() == 0)){
                throw new Exception("Unexpected EOF on waitScheduleId");
            }
            if(waitScheduleId != null && waitScheduleId.length() != 0){
                Object actionResult = null;
                if(waitScheduleId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(waitScheduleId);
                }else{
                    String[] ids = waitScheduleId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal waitScheduleId format. id=" + waitScheduleId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + waitScheduleId);
                }
                if(!(actionResult instanceof List) || (((List)actionResult).size() != 0 && !(((List)actionResult).get(0) instanceof Schedule))){
                    throw new Exception("TestActionResult not instance of Schedule List. result=" + actionResult);
                }
                waitSchedules.addAll((List)actionResult);
            }else{
                if(preResult == null){
                    throw new Exception("preResult is null.");
                }
                if(!(preResult instanceof List) || (((List)preResult).size() != 0 && !(((List)preResult).get(0) instanceof Schedule))){
                    throw new Exception("preResult not instance of Schedule List. result=" + preResult);
                }
                waitSchedules.addAll((List)preResult);
            }
            if(waitSchedules.size() == 0){
                throw new Exception("Wait Schedule not found.");
            }
            final String timeoutStr = br.readLine();
            if(timeoutStr == null || timeoutStr.length() == 0){
                throw new Exception("Unexpected EOF on timeout");
            }
            try{
                timeout = Long.parseLong(timeoutStr);
            }catch(NumberFormatException e){
                throw new Exception("Illegal timeout format. timeout=" + timeoutStr);
            }
            final String waitStatesStr = br.readLine();
            if(waitStatesStr != null && waitStatesStr.length() != 0){
                String[] waitStatesArray = waitStatesStr.split(",");
                waitStates = new int[waitStatesArray.length];
                for(int i = 0; i < waitStatesArray.length; i++){
                    if("END".equals(waitStatesArray[i])){
                        waitStates[i] = Schedule.STATE_END;
                    }else if("FAILED".equals(waitStatesArray[i])){
                        waitStates[i] = Schedule.STATE_FAILED;
                    }else if("RETRY".equals(waitStatesArray[i])){
                        waitStates[i] = Schedule.STATE_RETRY;
                    }else if("DISABLE".equals(waitStatesArray[i])){
                        waitStates[i] = Schedule.STATE_DISABLE;
                    }else{
                        throw new Exception("Illegal waitState format. waitState=" + waitStatesArray[i]);
                    }
                }
            }
            final String successStatesStr = br.readLine();
            if(successStatesStr != null && successStatesStr.length() != 0){
                String[] successStatesArray = successStatesStr.split(",");
                successStates = new int[successStatesArray.length];
                for(int i = 0; i < successStatesArray.length; i++){
                    if("END".equals(successStatesArray[i])){
                        successStates[i] = Schedule.STATE_END;
                    }else if("FAILED".equals(successStatesArray[i])){
                        successStates[i] = Schedule.STATE_FAILED;
                    }else if("RETRY".equals(successStatesArray[i])){
                        successStates[i] = Schedule.STATE_RETRY;
                    }else if("DISABLE".equals(successStatesArray[i])){
                        successStates[i] = Schedule.STATE_DISABLE;
                    }else{
                        throw new Exception("Illegal successState format. successState=" + successStatesArray[i]);
                    }
                }
            }
        }finally{
            br.close();
            br = null;
        }
        
        final long waitEndTime = System.currentTimeMillis() + timeout;
        long interval = Math.min(waitInterval, timeout);
        boolean result = true;
        do{
            String id = ((Schedule)waitSchedules.get(0)).getId();
            Schedule schedule = scheduleManager.findSchedule(id);
            if(schedule == null){
                throw new Exception("Schedule not found. id=" + id);
            }
            boolean isNotify = false;
            for(int i = 0; i < waitStates.length; i++){
                if(schedule.getState() == waitStates[i]){
                    boolean isSuccess = false;
                    for(int j = 0; j < successStates.length; j++){
                        if(schedule.getState() == successStates[j]){
                            isSuccess = true;
                            break;
                        }
                    }
                    result &= isSuccess;
                    waitSchedules.remove(0);
                    isNotify = true;
                    break;
                }
            }
            if(!isNotify){
                if(System.currentTimeMillis() >= waitEndTime){
                    List dependsSchedules = scheduleManager.findDependsSchedules(id);
                    throw new Exception("The timeout has been reached due to waiting for the schedule. schedule=" + schedule + ", dependsSchedules=" + dependsSchedules);
                }
                Thread.sleep(interval);
            }
        }while(waitSchedules.size() != 0);
        return result;
    }
}
