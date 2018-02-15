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
package jp.ossc.nimbus.service.scheduler;

import jp.ossc.nimbus.core.*;

/**
 * 単純スケジュールファクトリ。<p>
 *
 * @author M.Takata
 */
public class SimpleScheduleFactoryService extends ServiceBase
 implements SimpleScheduleFactoryServiceMBean, ScheduleFactory{
    
    private static final long serialVersionUID = -9098012310072379024L;
    
    protected ServiceName[] scheduleServiceNames;
    protected Schedule[] schedules;
    protected ServiceName scheduleStateManagerServiceName;
    
    // SimpleScheduleFactoryServiceMBeanのJavaDoc
    public void setScheduleServiceNames(ServiceName[] names){
        scheduleServiceNames = names;
    }
    // SimpleScheduleFactoryServiceMBeanのJavaDoc
    public ServiceName[] getScheduleServiceNames(){
        return scheduleServiceNames;
    }
    
    // SimpleScheduleFactoryServiceMBeanのJavaDoc
    public void setScheduleStateManagerServiceName(ServiceName name){
        scheduleStateManagerServiceName = name;
    }
    // SimpleScheduleFactoryServiceMBeanのJavaDoc
    public ServiceName getScheduleStateManagerServiceName(){
        return scheduleStateManagerServiceName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(scheduleServiceNames != null){
            ScheduleStateManager scheduleStateManager = null;
            if(scheduleStateManagerServiceName != null){
                scheduleStateManager
                     = (ScheduleStateManager)ServiceManagerFactory
                        .getServiceObject(scheduleStateManagerServiceName);
            }
            schedules = new Schedule[scheduleServiceNames.length];
            for(int i = 0; i < scheduleServiceNames.length; i++){
                schedules[i] = (Schedule)ServiceManagerFactory
                    .getServiceObject(scheduleServiceNames[i]);
                if(scheduleStateManager != null){
                    schedules[i].setScheduleStateManager(scheduleStateManager);
                }
            }
        }
        if(schedules == null || schedules.length == 0){
            throw new IllegalArgumentException("Schedule is null.");
        }
    }
    
    public void setSchedules(Schedule[] schedules){
        this.schedules = schedules;
    }
    
    /**
     * このファクトリが保持しているスケジュールを取得する。<p>
     *
     * @param key 使用しない
     * @return スケジュール配列
     */
    public Schedule[] getSchedules(Object key){
        return schedules;
    }
}