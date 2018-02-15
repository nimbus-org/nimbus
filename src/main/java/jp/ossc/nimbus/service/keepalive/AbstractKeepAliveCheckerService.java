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
package jp.ossc.nimbus.service.keepalive;

import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;

/**
 * {@link KeepAliveChecker}インタフェース抽象サービス。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractKeepAliveCheckerService extends ServiceBase
 implements KeepAliveChecker, AbstractKeepAliveCheckerServiceMBean{
    
    private static final long serialVersionUID = 7701584736922165397L;
    
    /**
     * 定期的に{@link KeepAliveChecker}に生存確認を行う間隔[ms]。<p>
     */
    protected long checkInterval = -1L;
    
    protected long lastCheckTime = -1L;
    
    /**
     * 定期的に生存確認を行うデーモンスレッド。<p>
     */
    protected Daemon keepAliveChecker;
    
    protected String aliveLogMessageId = DEFAULT_ALIVE_LOG_MSG_ID;
    
    protected String deadLogMessageId = DEFAULT_DEAD_LOG_MSG_ID;
    
    protected boolean isOutputAliveLogMessage = true;
    
    protected boolean isOutputDeadLogMessage = true;
    
    protected boolean isAlive = false;
    
    protected List keepAliveListeners;
    
    public void setCheckInterval(long millis){
        checkInterval = millis;
        if(checkInterval > 0
            && keepAliveChecker != null
            && keepAliveChecker.isSusupend()){
            keepAliveChecker.resume();
        }
    }
    public long getCheckInterval(){
        return checkInterval;
    }
    
    public void setAliveLogMessageId(String id){
        aliveLogMessageId = id;
    }
    public String getAliveLogMessageId(){
        return aliveLogMessageId;
    }
    
    public void setDeadLogMessageId(String id){
        deadLogMessageId = id;
    }
    public String getDeadLogMessageId(){
        return deadLogMessageId;
    }
    
    public void setOutputAliveLogMessage(boolean isOutput){
        isOutputAliveLogMessage = isOutput;
    }
    public boolean isOutputAliveLogMessage(){
        return isOutputAliveLogMessage;
    }
    
    public void setOutputDeadLogMessage(boolean isOutput){
        isOutputDeadLogMessage = isOutput;
    }
    public boolean isOutputDeadLogMessage(){
        return isOutputDeadLogMessage;
    }
    
    public boolean isAlive(){
        if(checkInterval > 0 && lastCheckTime >= 0){
            return isAlive;
        }else{
            try{
                return checkAlive();
            }catch(Exception e){
                return false;
            }
        }
    }
    
    public void preCreateService() throws Exception{
        super.preCreateService();
        keepAliveListeners = new ArrayList();
    }
    
    public void postStartService() throws Exception{
        
        if(checkInterval > 0){
            try{
                isAlive = checkAlive();
            }catch(Exception e){
                isAlive = false;
            }
            
            keepAliveChecker = new Daemon(new KeepAliveCheckerRunnable());
            keepAliveChecker.setName("Nimbus KeepAliveChecker " + getServiceNameObject());
            keepAliveChecker.start();
        }
        
        super.postStartService();
    }
    
    public void postStopService() throws Exception{
        if(keepAliveChecker != null){
            // デーモン停止
            keepAliveChecker.stop();
            keepAliveChecker = null;
        }
        super.postStopService();
    }
    
    public void postDestroyService() throws Exception{
        keepAliveListeners = null;
        super.postDestroyService();
    }
    
    public abstract boolean checkAlive() throws Exception;
    
    protected void changeAlive(){
        if(isOutputAliveLogMessage() && getAliveLogMessageId() != null){
            getLogger().write(getAliveLogMessageId(), getTargetInfo());
        }
        synchronized(keepAliveListeners){
            for(int i = 0; i < keepAliveListeners.size(); i++){
                ((KeepAliveListener)keepAliveListeners.get(i)).onRecover(this);
            }
        }
    }
    
    protected void changeDead(Exception exception){
        if(isOutputDeadLogMessage() && getDeadLogMessageId() != null){
            if(exception == null){
                getLogger().write(getDeadLogMessageId(), getTargetInfo());
            }else{
                getLogger().write(getDeadLogMessageId(), getTargetInfo(), exception);
            }
        }
        synchronized(keepAliveListeners){
            for(int i = 0; i < keepAliveListeners.size(); i++){
                ((KeepAliveListener)keepAliveListeners.get(i)).onDead(this);
            }
        }
    }
    
    protected Object getTargetInfo(){
        return getServiceNameObject();
    }
    
    public void addKeepAliveListener(KeepAliveListener listener){
        synchronized(keepAliveListeners){
            keepAliveListeners.add(listener);
        }
    }
    
    public void removeKeepAliveListener(KeepAliveListener listener){
        synchronized(keepAliveListeners){
            keepAliveListeners.remove(listener);
        }
    }
    
    public void clearKeepAliveListener(){
        synchronized(keepAliveListeners){
            keepAliveListeners.clear();
        }
    }
    
    protected class KeepAliveCheckerRunnable implements DaemonRunnable{
        
        public boolean onStart(){
            return true;
        }
        
        public boolean onStop(){
            return true;
        }
        
        public boolean onSuspend(){
            return true;
        }
        
        public boolean onResume(){
            return true;
        }
        
        public Object provide(DaemonControl ctrl) throws Exception{
            if(checkInterval <= 0){
                ctrl.suspend();
            }
            long sleepTime = lastCheckTime < 0 ? 0l : (checkInterval - (System.currentTimeMillis() - lastCheckTime));
            if(sleepTime > 0){
                Thread.sleep(sleepTime);
            }
            return null;
        }
        
        public void consume(Object paramObj, DaemonControl ctrl) throws Exception{
            lastCheckTime = System.currentTimeMillis();
            boolean isPrevStatus = isAlive;
            Exception exception = null;
            try{
                isAlive = checkAlive();
            }catch(Exception e){
                exception = e;
                isAlive = false;
            }
            if(isPrevStatus != isAlive){
                if(isAlive){
                    changeAlive();
                }else{
                    changeDead(exception);
                }
            }
        }
        
        public void garbage(){
        }
    }
}