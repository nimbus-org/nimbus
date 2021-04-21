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
package jp.ossc.nimbus.service.rush;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.core.*;

/**
 * 並列ラッシュサービス。<p>
 *
 * @author M.Takata
 */
public class ParallelRushService extends ServiceBase implements Rush, ParallelRushServiceMBean{
    
    private String scenarioName;
    private ServiceName[] rushServiceNames;
    private boolean isStartRushOnStart;
    private boolean isNoWait = true;
    
    private Rush[] rushes;
    
    public void setScenarioName(String name){
        scenarioName = name;
    }
    public String getScenarioName(){
        return scenarioName;
    }
    
    public void setRushServiceNames(ServiceName[] names){
        rushServiceNames = names;
    }
    public ServiceName[] getRushServiceNames(){
        return rushServiceNames;
    }
    public boolean isStartRushOnStart(){
        return isStartRushOnStart;
    }
    public void setStartRushOnStart(boolean isStart){
        isStartRushOnStart = isStart;
    }
    
    public boolean isNoWait(){
        return isNoWait;
    }
    public void setNoWait(boolean noWait){
        isNoWait = noWait;
    }
    
    public void startService() throws Exception{
        
        if(scenarioName == null){
            scenarioName = getServiceNameObject().toString();
        }
        
        if(rushServiceNames != null && rushServiceNames.length > 0){
            Rush[] services = new Rush[rushServiceNames.length];
            for(int i = 0; i < rushServiceNames.length; i++){
                services[i] = (Rush)ServiceManagerFactory.getServiceObject(rushServiceNames[i]);
            }
            rushes = services;
        }
    }
    
    protected void postStartService() throws Exception{
        super.postStartService();
        if(isStartRushOnStart){
            startRush(isNoWait);
        }
    }
    
    public void startRush(final boolean noWait) throws Exception{
        if(rushes == null || rushes.length == 0){
            return;
        }
        Thread[] threads = new Thread[rushes.length];
        for(int i = 0; i < rushes.length; i++){
            final Rush rush = rushes[i];
            threads[i] = new Thread(
                new Runnable(){
                    public void run(){
                        try{
                            rush.startRush(noWait);
                        }catch(Exception e){
                            getLogger().write("PRS__00001", rush.getScenarioName(), e);
                        }
                    }
                }
            );
            threads[i].setName(getServiceName() + " Rush thread[" + i + "]");
            threads[i].setDaemon(true);
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].start();
        }
        if(!noWait){
            for(int i = 0; i < rushes.length; i++){
                threads[i].join();
            }
        }
    }
    
    public void stopRush(){
        if(rushes == null || rushes.length == 0){
            return;
        }
        for(int i = 0; i < rushes.length; i++){
            final Rush rush = rushes[i];
            rush.stopRush();
        }
    }
}