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
import jp.ossc.nimbus.service.cache.*;

/**
 * スケジュール状態管理を行うサービス。<p>
 *
 * @author M.Takata
 */
public class DefaultScheduleStateManagerService extends ServiceBase
 implements ScheduleStateManager, DefaultScheduleStateManagerServiceMBean{
    
    private static final long serialVersionUID = -9005849328563122282L;
    
    protected ServiceName cacheMapServiceName;
    protected CacheMap stateManageMap;
    
    public void setCacheMapServiceName(ServiceName name){
        cacheMapServiceName = name;
    }
    public ServiceName getCacheMapServiceName(){
        return cacheMapServiceName;
    }
    
    public void startService() throws Exception{
        if(stateManageMap == null){
            if(cacheMapServiceName == null){
                MemoryCacheMapService caheMap = new MemoryCacheMapService();
                caheMap.create();
                caheMap.start();
                stateManageMap = caheMap;
            }else {
                stateManageMap = (CacheMap)ServiceManagerFactory
                    .getServiceObject(cacheMapServiceName);
            }
        }
    }
    
    public void setCacheMap(CacheMap map){
        stateManageMap = map;
    }
    public CacheMap getCacheMap(){
        return stateManageMap;
    }
    
    // ScheduleStateManagerのJavaDoc
    public void changeState(String name, int state){
        if(stateManageMap == null){
            return;
        }
        stateManageMap.put(name, new Integer(state));
    }
    
    // ScheduleStateManagerのJavaDoc
    public int getState(String name){
        if(stateManageMap == null){
            return STATE_UNKNOWN;
        }
        if(stateManageMap.containsKey(name)){
            return ((Integer)stateManageMap.get(name)).intValue();
        }else{
            return STATE_UNKNOWN;
        }
    }
    
    // ScheduleStateManagerのJavaDoc
    public void clearState(String name){
        if(stateManageMap == null){
            return;
        }
        stateManageMap.remove(name);
    }
    
    // ScheduleStateManagerのJavaDoc
    public void clearAllStates(){
        if(stateManageMap == null){
            return;
        }
        stateManageMap.clear();
    }
}