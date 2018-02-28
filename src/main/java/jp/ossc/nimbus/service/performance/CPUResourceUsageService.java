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
package jp.ossc.nimbus.service.performance;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.system.OperationSystem;
import jp.ossc.nimbus.service.system.CpuTimes;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.daemon.DaemonControl;

/**
 * CPU利用量。<p>
 *
 * @author M.Takata
 */
public class CPUResourceUsageService extends ServiceBase implements ResourceUsage, DaemonRunnable, CPUResourceUsageServiceMBean{
    
    private static final long serialVersionUID = -2693011646692158213L;
    private ServiceName operationSystemServiceName;
    private OperationSystem operationSystem;
    private CpuTimes lastCpuTimesDelta;
    private CpuTimes lastCpuTimes;
    private long interval = 1000;
    private Daemon cpuTimesChecker;
    
    public void setOperationSystemServiceName(ServiceName name){
        operationSystemServiceName = name;
    }
    public ServiceName getOperationSystemServiceName(){
        return operationSystemServiceName;
    }
    
    public void setCpuTimesCheckInterval(long interval){
        this.interval = interval;
    }
    public long getCpuTimesCheckInterval(){
        return interval;
    }
    
    public void setOperationSystem(OperationSystem os){
        operationSystem = os;
    }
    
    public void startService() throws Exception{
        if(operationSystem == null && operationSystemServiceName == null){
            throw new IllegalArgumentException("OperationSystem is null.");
        }
        if(operationSystemServiceName != null){
            operationSystem = (OperationSystem)ServiceManagerFactory
                .getServiceObject(operationSystemServiceName);
        }
        cpuTimesChecker = new Daemon(this);
        cpuTimesChecker.setDaemon(true);
        cpuTimesChecker.setName("Nimbus CPUResourceUsage Checker " + getServiceNameObject());
        cpuTimesChecker.start();
    }
    
    public void stopService() throws Exception{
        if(cpuTimesChecker != null){
            cpuTimesChecker.stopNoWait();
            cpuTimesChecker = null;
        }
    }
    
    public Comparable getUsage(){
        if(operationSystem == null){
            return null;
        }
        CpuTimes cpuTimesDelta = operationSystem.getCpuTimesDelta(lastCpuTimes);
        if(lastCpuTimesDelta != null){
            cpuTimesDelta.add(lastCpuTimesDelta);
        }
        return operationSystem == null ? null : (cpuTimesDelta.getIdleRate() == 1.0d ? null : new Double(1.0d - cpuTimesDelta.getIdleRate()));
    }
    
    public boolean onStart(){return true;}
    public boolean onStop(){return true;}
    public boolean onSuspend(){return true;}
    public boolean onResume(){return true;}
    public Object provide(DaemonControl ctrl) throws Throwable{
        try{
            ctrl.sleep(interval, true);
        }catch(InterruptedException e){
        }
        return null;
    }
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
        lastCpuTimesDelta = operationSystem.getCpuTimesDelta(lastCpuTimes);
        lastCpuTimes = operationSystem.getCpuTimes();
    }
    public void garbage(){}
}
