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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import jp.ossc.nimbus.core.*;

/**
 * チェックポイントトレーサー。<p>
 *
 * @author M.Takata
 */
public class CheckPointTracerService extends ServiceBase
 implements CheckPointTracerServiceMBean{
    
    private static final long serialVersionUID = -5613434425756392467L;
    private boolean isEnabled = true;
    private ConcurrentMap checkPointTraceMap;
    
    // CheckPointTracerServiceMBeanのJavaDoc
    public void setEnabled(boolean enabled){
        isEnabled = enabled;
    }
    
    // CheckPointTracerServiceMBeanのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    public void createService() throws Exception{
        checkPointTraceMap = new ConcurrentHashMap();
    }
    public void stopService() throws Exception{
        clear();
    }
    
    // CheckPointTracerServiceMBeanのJavaDoc
    public CheckPoint[] getCheckPointTrace(String threadName){
        if(getState() != STARTED){
            return new CheckPoint[0];
        }
        List checkPointTrace = (List)checkPointTraceMap.get(threadName);
        if(checkPointTrace == null){
            return new CheckPoint[0];
        }
        synchronized(checkPointTrace){
            return (CheckPoint[])checkPointTrace.toArray(new CheckPoint[checkPointTrace.size()]);
        }
    }
    
    // CheckPointTracerServiceMBeanのJavaDoc
    public void clear(String threadName){
        if(getState() != STARTED){
            return;
        }
        List checkPointTrace = (List)checkPointTraceMap.get(threadName);
        if(checkPointTrace == null){
            return;
        }
        synchronized(checkPointTrace){
            checkPointTrace.clear();
        }
    }
    
    // CheckPointTracerServiceMBeanのJavaDoc
    public void clear(){
        if(getState() != STARTED){
            return;
        }
        checkPointTraceMap.clear();
    }
    
    // CheckPointTracerServiceMBeanのJavaDoc
    public void passedCheckPoint(String threadName, CheckPoint point){
        if(getState() != STARTED || !isEnabled){
            return;
        }
        List checkPointTrace = (List)checkPointTraceMap.get(threadName);
        if(checkPointTrace == null){
            checkPointTrace = new ArrayList();
            List old = (List)checkPointTraceMap.putIfAbsent(threadName, checkPointTrace);
            if(old != null){
                checkPointTrace = old;
            }
        }
        synchronized(checkPointTrace){
            checkPointTrace.add(point);
        }
    }
}
