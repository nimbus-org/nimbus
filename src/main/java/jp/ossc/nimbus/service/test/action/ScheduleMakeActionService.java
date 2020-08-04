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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.scheduler2.DefaultScheduleMaster;
import jp.ossc.nimbus.service.scheduler2.ScheduleManager;
import jp.ossc.nimbus.service.interpreter.Interpreter;

/**
 * {@link ScheduleManager}を使って、スケジュールを作成するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class ScheduleMakeActionService extends ServiceBase implements TestAction, TestActionEstimation, ScheduleMakeActionServiceMBean{
    
    private static final long serialVersionUID = 7719682717347172076L;
    
    protected ServiceName scheduleManagerServiceName;
    protected ScheduleManager scheduleManager;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    protected double expectedCost = Double.NaN;

    public void setScheduleManagerServiceName(ServiceName name){
        scheduleManagerServiceName = name;
    }
    public ServiceName getScheduleManagerServiceName(){
        return scheduleManagerServiceName;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void startService() throws Exception{
        if(scheduleManagerServiceName != null){
            scheduleManager = (ScheduleManager)ServiceManagerFactory.getServiceObject(scheduleManagerServiceName);
        }
        if(scheduleManager == null){
            throw new IllegalArgumentException("ScheduleManager is null.");
        }
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
    }
    
    /**
     * リソースの内容を読み込んで、指定されたスケジュールマスタから、現在時刻で開始するスケジュールを作成する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * masterId
     * script
     * </pre>
     * masterIdは、作成するスケジュールのマスタIDを指定する。<br>
     * scriptは、{@link ScheduleMaster}を編集するスクリプトを指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}、"master"で{@link ScheduleMaster}が渡される。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 生成されたスケジュールのリスト
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        String masterId = null;
        String script = null;
        try{
            masterId = br.readLine();
            if(masterId == null){
                throw new Exception("Unexpected EOF on masterId");
            }
            String line = null;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            try{
                while((line = br.readLine()) != null){
                    pw.println(line);
                }
                pw.flush();
                script = sw.toString();
                if(script.length() == 0){
                    script = null;
                }
            }finally{
                sw.close();
                pw.close();
            }
            if(script != null){
                if(interpreter == null){
                    throw new UnsupportedOperationException("Interpreter is null.");
                }
            }
        }finally{
            br.close();
            br = null;
        }
        
        Date now = new Date();
        final DefaultScheduleMaster master = (DefaultScheduleMaster)scheduleManager.findScheduleMaster(masterId);
        if(master == null){
            throw new Exception("ScheduleMaster not found. masterId=" + masterId);
        }
        final Calendar cal = Calendar.getInstance();
        Date startTime = master.getStartTime();
        long offset = 0;
        if(startTime != null){
            cal.setTime(now);
            final int year = cal.get(Calendar.YEAR);
            final int day = cal.get(Calendar.DAY_OF_YEAR);
            cal.clear();
            cal.setTime(startTime);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_YEAR, day + (cal.get(Calendar.DAY_OF_YEAR) - 1));
            offset = now.getTime() - cal.getTimeInMillis();
            
            cal.clear();
            cal.setTime(now);
            cal.set(Calendar.DAY_OF_YEAR, 1);
            startTime = cal.getTime();
        }
        if(master.isTemplate()){
            master.setTemplate(false);
        }
        master.setStartTime(startTime);
        final Date endTime = master.getEndTime();
        if(endTime != null){
            master.setEndTime(new Date(endTime.getTime() + offset));
        }
        final Date retryEndTime = master.getRetryEndTime();
        if(retryEndTime != null){
            master.setRetryEndTime(new Date(retryEndTime.getTime() + offset));
        }
        if(script != null){
            final Map params = new HashMap();
            params.put("context", context);
            params.put("master", master);
            interpreter.evaluate(script, params);
        }
        return scheduleManager.makeSchedule(now, master);
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
