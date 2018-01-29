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
package jp.ossc.nimbus.service.system.javasysmon;

import java.io.Serializable;
import java.util.regex.Pattern;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.system.*;

import com.jezhumble.javasysmon.Monitor;
import com.jezhumble.javasysmon.JavaSysMon;

/**
 * com.jezhumble.javasysmon.JavaSysMon実装の{@link OperationSystem}サービス。<p>
 *
 * @author M.Takata
 */
public class OperationSystemService extends ServiceBase
 implements OperationSystem, OperationSystemServiceMBean{
    
    private static final long serialVersionUID = 2447573678398806223L;
    
    private Monitor monitor;
    private CpuTimes lastCpuTimes;
    
    public void createService() throws Exception{
        monitor = new JavaSysMon();
    }
    
    public void startService() throws Exception{
        lastCpuTimes = getCpuTimes();
    }
    
    public void destroyService() throws Exception{
        monitor = null;
    }
    
    public String getName(){
        return monitor == null ? null : monitor.osName();
    }
    
    public int getCpuNumbers(){
        return monitor == null ? -1 : monitor.numCpus();
    }
    
    public long getCpuFrequency(){
        return monitor == null ? -1L : monitor.cpuFrequencyInHz();
    }
    
    public long getUptimeInSeconds(){
        return monitor == null ? -1L : monitor.uptimeInSeconds();
    }
    
    public CpuTimes getCpuTimes(){
        return new CpuTimesImpl(monitor == null ? null : monitor.cpuTimes());
    }
    
    public MemoryInfo getPhysicalMemoryInfo(){
        return new MemoryInfoImpl(monitor == null ? null : monitor.physical());
    }
    
    public MemoryInfo getSwapMemoryInfo(){
        return new MemoryInfoImpl(monitor == null ? null : monitor.swap());
    }
    
    public int getPid(){
        return monitor == null ? -1 : monitor.currentPid();
    }
    
    public boolean kill(int pid){
        if(getProcessInfo(pid) == null){
            return false;
        }
        monitor.killProcess(pid);
        return true;
    }
    
    public ProcessInfo getProcessInfo(int pid){
        if(monitor == null){
            return null;
        }
        com.jezhumble.javasysmon.ProcessInfo[] procs = monitor.processTable();
        if(procs == null || procs.length == 0){
            return null;
        }
        for(int i = 0; i < procs.length; i++){
            if(procs[i].getPid() == pid){
                return new ProcessInfoImpl(procs[i]);
            }
        }
        return null;
    }
    
    public boolean kill(String command){
        ProcessInfo info = getProcessInfo(command);
        if(info == null){
            return false;
        }
        monitor.killProcess(info.getPid());
        return true;
    }
    
    public ProcessInfo getProcessInfo(String command){
        if(monitor == null){
            return null;
        }
        com.jezhumble.javasysmon.ProcessInfo[] procs = monitor.processTable();
        if(procs == null || procs.length == 0){
            return null;
        }
        for(int i = 0; i < procs.length; i++){
            String cmd = procs[i].getCommand();
            if(command.equals(cmd) || Pattern.matches(command, cmd)){
                return new ProcessInfoImpl(procs[i]);
            }
        }
        return null;
    }
    
    public ProcessInfo[] getProcessInfos(){
        if(monitor == null){
            return new ProcessInfoImpl[0];
        }
        com.jezhumble.javasysmon.ProcessInfo[] procs = monitor.processTable();
        if(procs == null || procs.length == 0){
            return new ProcessInfoImpl[0];
        }
        ProcessInfoImpl[] result = new ProcessInfoImpl[procs.length];
        for(int i = 0; i < procs.length; i++){
            result[i] = new ProcessInfoImpl(procs[i]);
        }
        return result;
    }
    
    public synchronized CpuTimes getCpuTimesDelta(){
        CpuTimes current = getCpuTimes();
        CpuTimes result = getCpuTimesDelta(current, lastCpuTimes);
        lastCpuTimes = current;
        return result;
    }
    
    public CpuTimes getCpuTimesDelta(CpuTimes prev){
        return getCpuTimesDelta(getCpuTimes(), prev);
    }
    
    private CpuTimes getCpuTimesDelta(CpuTimes current, CpuTimes prev){
        CpuTimes result = null;
        if(prev == null){
            result = current;
        }else{
            result = new CpuTimesImpl(prev, current);
        }
        return result;
    }
    
    private static class CpuTimesImpl implements CpuTimes, Serializable{
        
        private static final long serialVersionUID = -1314982067289673847L;
        private long user = -1;
        private long system = -1;
        private long idle = -1;
        private long total = -1;
        
        public CpuTimesImpl(com.jezhumble.javasysmon.CpuTimes cpu){
            if(cpu != null){
                user = cpu.getUserMillis();
                system = cpu.getSystemMillis();
                idle = cpu.getIdleMillis();
                total = cpu.getTotalMillis();
            }
        }
        
        public CpuTimesImpl(CpuTimes prev, CpuTimes current){
            user = current.getUserTimeMillis() - prev.getUserTimeMillis();
            system = current.getSystemTimeMillis() - prev.getSystemTimeMillis();
            idle = current.getIdleTimeMillis() - prev.getIdleTimeMillis();
            total = current.getTotalTimeMillis() - prev.getTotalTimeMillis();
        }
        
        public long getUserTimeMillis(){
            return user;
        }
        
        public long getSystemTimeMillis(){
            return system;
        }
        
        public long getIdleTimeMillis(){
            return idle;
        }
        
        public long getTotalTimeMillis(){
            return total;
        }
        
        public float getUserRate(){
            return (float)user / (float)total;
        }
        
        public float getSystemRate(){
            return (float)system / (float)total;
        }
        
        public float getIdleRate(){
            return (float)idle / (float)total;
        }
        
        public void add(CpuTimes times){
            user += times.getUserTimeMillis();
            system += times.getSystemTimeMillis();
            idle += times.getIdleTimeMillis();
            total += times.getTotalTimeMillis();
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("total=").append(total);
            buf.append(", user=").append(user)
               .append('(').append((int)(getUserRate() * 100.0)).append(')');
            buf.append(", system=").append(system)
               .append('(').append((int)(getSystemRate() * 100.0)).append(')');
            buf.append(", idle=").append(idle)
               .append('(').append(100 - (int)(getUserRate() * 100.0) - (int)(getSystemRate() * 100.0)).append(')');
            buf.append('}');
            return buf.toString();
        }
    }
    
    private static class MemoryInfoImpl implements MemoryInfo, Serializable{
        
        private static final long serialVersionUID = -3359110331566582696L;
        private long free = -1;
        private long total = -1;
        
        public MemoryInfoImpl(com.jezhumble.javasysmon.MemoryStats memory){
            if(memory != null){
                free = memory.getFreeBytes();
                total = memory.getTotalBytes();
            }
        }
        
        public long getFreeBytes(){
            return free;
        }
        
        public long getUsedBytes(){
            return total - free;
        }
        
        public long getTotalBytes(){
            return total;
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("total=").append(total);
            buf.append(", used=").append(getUsedBytes());
            buf.append(", free=").append(free);
            buf.append('}');
            return buf.toString();
        }
    }
    
    private static class ProcessInfoImpl implements ProcessInfo, Serializable{
        
        private static final long serialVersionUID = -2431120493724542097L;
        private int pid = -1;
        private int parentPid = -1;
        private String name;
        private String command;
        private String owner;
        private long userCpuTime = -1;
        private long systemCpuTime = -1;
        private long currentMemory = -1;
        private long totalMemory = -1;
        
        public ProcessInfoImpl(com.jezhumble.javasysmon.ProcessInfo proc){
            if(proc != null){
                pid = proc.getPid();
                parentPid = proc.getParentPid();
                name = proc.getName();
                command = proc.getCommand();
                owner = proc.getOwner();
                userCpuTime = proc.getUserMillis();
                systemCpuTime = proc.getSystemMillis();
                currentMemory = proc.getResidentBytes();
                totalMemory = proc.getTotalBytes();
            }
        }
        
        public int getPid(){
            return pid;
        }
        
        public int getParentPid(){
            return parentPid;
        }
        
        public String getName(){
            return name;
        }
        
        public String getCommand(){
            return command;
        }
        
        public String getOwner(){
            return owner;
        }
        
        public long getUserTimeMillis(){
            return userCpuTime;
        }
        
        public long getSystemTimeMillis(){
            return systemCpuTime;
        }
        
        public long getCurrentMemoryBytes(){
            return currentMemory;
        }
        
        public long getTotalMemoryBytes(){
            return totalMemory;
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("pid=").append(pid);
            buf.append(", parentPid=").append(parentPid);
            buf.append(", name=").append(name);
            buf.append(", command=").append(command);
            buf.append(", owner=").append(owner);
            buf.append(", user=").append(userCpuTime);
            buf.append(", system=").append(systemCpuTime);
            buf.append(", current=").append(currentMemory);
            buf.append(", total=").append(totalMemory);
            buf.append('}');
            return buf.toString();
        }
    }
}
