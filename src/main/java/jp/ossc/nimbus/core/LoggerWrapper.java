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
package jp.ossc.nimbus.core;

import java.util.*;
import java.io.Serializable;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.lang.*;

/**
 * {@link Logger}ÉâÉbÉpÅB<p>
 *
 * @author M.Takata
 */
class LoggerWrapper implements Logger, ServiceStateListener, Serializable{
    
    private static final long serialVersionUID = 6446367171255245828L;
    
    private Logger defaultLog;
    private Logger currentLog;
    
    public LoggerWrapper(Logger defaultLog){
        this(null, null, defaultLog);
    }
    
    public LoggerWrapper(
        Logger log,
        Service logService,
        Logger defaultLog
    ){
        setDefaultLogger(defaultLog);
        setLogger(log, logService);
    }
    
    public void setDefaultLogger(Logger log){
        if(log == this){
            return;
        }
        this.defaultLog = log;
    }
    
    public Logger getDefaultLogger(){
        return defaultLog;
    }
    
    public void setLogger(Logger log){
        setLogger(log, null);
    }
    
    public void setLogger(
        Logger log,
        Service logService
    ){
        if(log == this){
            return;
        }
        if(logService != null){
            if(logService.getState() == Service.STARTED){
                currentLog = log;
            }else{
                currentLog = defaultLog;
            }
            try{
                final ServiceStateBroadcaster broadcaster
                    = ServiceManagerFactory.getServiceStateBroadcaster(
                        logService.getServiceManagerName(),
                        logService.getServiceName()
                    );
                if(broadcaster != null){
                    broadcaster.addServiceStateListener(this);
                }
            }catch(ServiceNotFoundException e){
            }
        }else{
            if(log != null){
                currentLog = log;
            }else{
                currentLog = defaultLog;
            }
        }
    }
    
    public Logger getLogger(){
        return currentLog;
    }
    
    public void write(String logCode, Object[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, byte[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, short[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, char[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, int[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, long[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, float[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, double[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, boolean[] embeds){
        if(currentLog != null){
            currentLog.write(logCode, embeds);
        }
    }
    
    public void write(String logCode, Object embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, byte embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, short embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, char embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, int embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, long embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, float embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, double embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode, boolean embed){
        if(currentLog != null){
            currentLog.write(logCode, embed);
        }
    }
    
    public void write(String logCode){
        if(currentLog != null){
            currentLog.write(logCode);
        }
    }
    
    public void write(String logCode, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, oException);
        }
    }
    
    public void write(String logCode, Object embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, byte embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, short embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, char embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, int embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, long embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, float embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, double embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, boolean embed, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embed, oException);
        }
    }
    
    public void write(String logCode, Object[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, byte[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, short[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, char[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, int[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, long[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, float[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, double[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(String logCode, boolean[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(logCode, embeds, oException);
        }
    }
    
    public void write(AppException e){
        if(currentLog != null){
            currentLog.write(e);
        }
    }
    
    public void write(Locale lo, String logCode, Object[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, byte[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, short[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, char[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, int[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, long[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, float[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, double[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo, String logCode, boolean[] embeds){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds);
        }
    }
    
    public void write(Locale lo,String logCode,Object embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,byte embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,short embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,char embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,int embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,long embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,float embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,double embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode,boolean embed){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed);
        }
    }
    
    public void write(Locale lo,String logCode){
        if(currentLog != null){
            currentLog.write(lo, logCode);
        }
    }
    
    public void write(Locale lo,String logCode,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, oException);
        }
    }
    
    public void write(Locale lo,String logCode, Object embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, byte embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, short embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, char embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, int embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, long embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, float embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, double embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo,String logCode, boolean embed,Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embed, oException);
        }
    }
    
    public void write(Locale lo, String logCode, Object[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, byte[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, short[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, char[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, int[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, long[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, float[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, double[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, String logCode, boolean[] embeds, Throwable oException){
        if(currentLog != null){
            currentLog.write(lo, logCode, embeds, oException);
        }
    }
    
    public void write(Locale lo, AppException e){
        if(currentLog != null){
            currentLog.write(lo, e);
        }
    }
    
    // LoggerÇÃJavaDoc
    public boolean isWrite(String logCode){
        if(currentLog != null){
            return currentLog.isWrite(logCode);
        }
        return false;
    }
    
    public boolean isDebugWrite(){
        if(currentLog != null){
            return currentLog.isDebugWrite();
        }
        return false;
    }
    
    public void debug(Object msg){
        if(currentLog != null){
            currentLog.debug(msg);
        }
    }
    
    public void debug(Object msg, Throwable oException){
        if(currentLog != null){
            currentLog.debug(msg, oException);
        }
    }
    
    public void stateChanged(ServiceStateChangeEvent e) throws Exception{
        final Service service = e.getService();
        final int state = service.getState();
        final String managerName = service.getServiceManagerName();
        final String serviceName = service.getServiceName();
        switch(state){
        case Service.STARTED:
            currentLog = (Logger)ServiceManagerFactory.getServiceObject(
                managerName,
                serviceName
            );
            break;
        case Service.STOPPING:
            currentLog = defaultLog;
            break;
        default:
        }
        
    }
    
    public boolean isEnabledState(int state){
        switch(state){
        case Service.STARTED:
        case Service.STOPPING:
            return true;
        default:
            return false;
        }
    }
    
    public synchronized void start(){
        if(currentLog == null){
            if(defaultLog instanceof Service){
                try{
                    ((Service)defaultLog).start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            currentLog = defaultLog;
        }
    }
    
    public synchronized void stop(){
        if(currentLog == defaultLog){
            currentLog = null;
            if(defaultLog instanceof Service){
                ((Service)defaultLog).stop();
            }
        }else{
            Logger oldLog = currentLog;
            currentLog = defaultLog;
            if(oldLog instanceof Service){
                ((Service)oldLog).stop();
                ((Service)oldLog).destroy();
            }
            currentLog = null;
            if(defaultLog instanceof Service){
                ((Service)defaultLog).stop();
            }
        }
    }
    
    public String toString(){
        return super.toString() + '{' + "currentLog=" + currentLog + "defaultLog=" + defaultLog  + '}';
    }
}
