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
package jp.ossc.nimbus.service.beancontrol;

import java.util.Date;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;

import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;
import jp.ossc.nimbus.service.queue.BeanFlowAsynchContext;

/**
 * BeanÉtÉçÅ[äƒéãÅB<p>
 * 
 * @author M.Takata
 */
public class BeanFlowMonitorImpl implements BeanFlowMonitor, java.io.Serializable{
    
    private static final long serialVersionUID = 1596031860601490370L;
    
    private boolean isSuspend = false;
    private boolean isStop = false;
    private boolean isStopped = false;
    private boolean isEnd = false;
    private SynchronizeMonitor suspendLock = new WaitSynchronizeMonitor();
    private String flowName;
    private String currentFlowName;
    private String currentStepName;
    private long startTime = -1;
    private long endTime = -1;
    private transient Set asynchContextSet;
    private transient Set monitorSet;
    private transient Thread flowThread;
    
    public BeanFlowMonitorImpl(){
    }
    
    public BeanFlowMonitorImpl(String name){
        flowName = name;
    }
    
    public void suspend(){
        isSuspend = true;
        if(monitorSet != null){
            Iterator monitors = monitorSet.iterator();
            while(monitors.hasNext()){
                BeanFlowMonitor monitor = (BeanFlowMonitor)monitors.next();
                monitor.suspend();
            }
        }
    }
    
    public boolean isSuspend(){
        return isSuspend;
    }
    
    public boolean isSuspended(){
        if(!suspendLock.isWait()){
            return false;
        }
        if(monitorSet != null){
            Iterator monitors = monitorSet.iterator();
            while(monitors.hasNext()){
                BeanFlowMonitor monitor = (BeanFlowMonitor)monitors.next();
                if(!monitor.isSuspended()){
                    return false;
                }
            }
        }
        return true;
    }
    
    protected void checkSuspend() throws InterruptedException{
        if(isSuspend){
            synchronized(suspendLock){
                if(isSuspend){
                    suspendLock.initMonitor();
                    suspendLock.waitMonitor();
                }
            }
        }
    }
    
    public void resume(){
        synchronized(suspendLock){
            isSuspend = false;
            suspendLock.notifyAllMonitor();
            if(monitorSet != null){
                Iterator monitors = monitorSet.iterator();
                while(monitors.hasNext()){
                    BeanFlowMonitor monitor = (BeanFlowMonitor)monitors.next();
                    monitor.resume();
                }
            }
        }
    }
    
    public void cancel(){
        if(monitorSet != null){
            Iterator monitors = monitorSet.iterator();
            while(monitors.hasNext()){
                BeanFlowMonitor monitor = (BeanFlowMonitor)monitors.next();
                monitor.cancel();
            }
        }
        if(asynchContextSet != null){
            Iterator contexts = asynchContextSet.iterator();
            while(contexts.hasNext()){
                BeanFlowAsynchContext context = (BeanFlowAsynchContext)contexts.next();
                context.cancel();
                contexts.remove();
            }
        }
    }
    
    public void stop(){
        isStop = true;
        if(monitorSet != null){
            Iterator monitors = monitorSet.iterator();
            while(monitors.hasNext()){
                BeanFlowMonitor monitor = (BeanFlowMonitor)monitors.next();
                monitor.stop();
            }
        }
        if(flowThread != null && !flowThread.equals(Thread.currentThread())){
            flowThread.interrupt();
        }
    }
    
    public boolean isStop(){
        return isStop;
    }
    
    public boolean isStopped(){
        if(!isStopped){
            return false;
        }
        if(monitorSet != null){
            Iterator monitors = monitorSet.iterator();
            while(monitors.hasNext()){
                BeanFlowMonitor monitor = (BeanFlowMonitor)monitors.next();
                if(!monitor.isStopped()){
                    return false;
                }
            }
        }
        return true;
    }
    
    protected void checkStop() throws BeanFlowMonitorStopException{
        if(isStop){
            isStopped = true;
            throw new BeanFlowMonitorStopException();
        }
    }
    
    public boolean isEnd(){
        return isEnd;
    }
    
    public String getFlowName(){
        return flowName;
    }
    
    protected void setFlowName(String name){
        flowName = name;
    }
    
    public String getCurrentFlowName(){
        return currentFlowName;
    }
    
    protected void setCurrentFlowName(String name){
        currentFlowName = name;
    }
    
    public String getCurrentStepName(){
        return currentStepName;
    }
    
    protected void setCurrentStepName(String name){
        currentStepName = name;
    }
    
    public long getStartTime(){
        return startTime;
    }
    
    protected void setStartTime(long time){
        if(flowName.equals(currentFlowName)){
            startTime = time;
            flowThread = Thread.currentThread();
        }
    }
    
    public long getCurrentProcessTime(){
        if(startTime == -1){
            return 0;
        }
        if(isEnd){
            return endTime - startTime;
        }else{
            return System.currentTimeMillis() - startTime;
        }
    }
    
    protected void end(){
        if(flowName.equals(currentFlowName)){
            endTime = System.currentTimeMillis();
            currentFlowName = null;
            currentStepName = null;
            if(flowThread != null){
                Thread.interrupted();
            }
            flowThread = null;
            isEnd = true;
        }
    }
    
    public void addAsynchContext(BeanFlowAsynchContext context){
        if(asynchContextSet == null){
            asynchContextSet = Collections.synchronizedSet(new LinkedHashSet());
        }
        asynchContextSet.add(context);
    }
    
    public void removeAsynchContext(BeanFlowAsynchContext context){
        if(asynchContextSet == null){
            return;
        }
        asynchContextSet.remove(context);
    }
    
    public void addBeanFlowMonitor(BeanFlowMonitor monitor){
        if(monitorSet == null){
            monitorSet = Collections.synchronizedSet(new LinkedHashSet());
        }
        monitorSet.add(monitor);
    }
    
    public void removeBeanFlowMonitor(BeanFlowMonitor monitor){
        if(monitorSet == null){
            return;
        }
        monitorSet.remove(monitor);
    }
    
    public void clear(){
        resume();
        isStop = false;
        isStopped = false;
        isEnd = false;
        flowName = null;
        currentFlowName = null;
        currentStepName = null;
        asynchContextSet = null;
        monitorSet = null;
        startTime = -1;
        endTime = -1;
        flowThread = null;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("flowName=").append(flowName).append(',');
        buf.append("currentFlowName=").append(currentFlowName).append(',');
        buf.append("currentStepName=").append(currentStepName).append(',');
        buf.append("isSuspend=").append(isSuspend).append(',');
        buf.append("isStop=").append(isStop).append(',');
        buf.append("startTime=").append(new Date(startTime)).append(',');
        buf.append("flowThread=").append(flowThread).append(',');
        buf.append("currentProcessTime=").append(getCurrentProcessTime());
        buf.append('}');
        return buf.toString();
    }
}
