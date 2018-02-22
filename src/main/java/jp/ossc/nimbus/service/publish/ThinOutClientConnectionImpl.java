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
package jp.ossc.nimbus.service.publish;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;

/**
 * 間引き受信を行う{@link ClientConnection}インタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class ThinOutClientConnectionImpl extends ClientConnectionWrapper{
    
    private ThinOutFilter[] filters;
    private Map lastThinOutMap;
    private Set lastThinOutSet;
    private long thinOutTimeout;
    private long thinOutTimeoutCheckInterval;
    private Daemon thinOutTimeoutChecker;
    private ThinOutMessageListener messageListener;
    
    public ThinOutClientConnectionImpl(
        ClientConnection connection,
        ThinOutFilter[] filters,
        long checkInterval,
        long timeout
    ){
        super(connection);
        this.filters = filters;
        messageListener = new ThinOutMessageListener();
        lastThinOutMap = new HashMap();
        lastThinOutSet = new HashSet();
        thinOutTimeoutCheckInterval = checkInterval;
        thinOutTimeout = timeout;
    }
    
    public void setMessageListener(MessageListener listener){
        if(thinOutTimeoutChecker != null){
            thinOutTimeoutChecker = new Daemon(new ThinOutTimeoutChecker());
            thinOutTimeoutChecker.setName(
                "Nimbus Publish ThinOutClientConnection ThinOutTimeoutChecker " + clientConnection
            );
            thinOutTimeoutChecker.setDaemon(true);
            thinOutTimeoutChecker.start();
        }
        messageListener.setMessageListener(listener);
        if(clientConnection != null){
            clientConnection.setMessageListener(messageListener);
        }
    }
    
    public void close(){
        if(thinOutTimeoutChecker != null){
            thinOutTimeoutChecker.stopNoWait();
            thinOutTimeoutChecker = null;
        }
        super.close();
    }
    
    private class ThinOutMessageListener implements MessageListener{
        
        private MessageListener listener;
        
        public void setMessageListener(MessageListener listener){
            this.listener = listener;
        }
        public void onMessage(Message message){
            Map messageMap = (Map)lastThinOutMap.get(message.getSubject());
            if(messageMap == null){
                messageMap = new HashMap();
                lastThinOutMap.put(message.getSubject(), messageMap);
            }
            boolean isThinOut = true;
            for(int i = 0; i < filters.length; i++){
                isThinOut &= filters[i].isThinOut(message);
                if(!isThinOut){
                    ThinOutMessage msg = (ThinOutMessage)messageMap.get(message.getKey());
                    if(msg != null){
                        synchronized(lastThinOutSet){
                            lastThinOutSet.remove(msg);
                        }
                        msg.clear();
                    }
                    synchronized(listener){
                        listener.onMessage(message);
                    }
                    for(int j = 0; j < filters.length; j++){
                        filters[j].notifySendMessage(message);
                    }
                    return;
                }
            }
            ThinOutMessage msg = (ThinOutMessage)messageMap.get(message.getKey());
            if(msg == null){
                msg = new ThinOutMessage();
                messageMap.put(message.getKey(), msg);
            }
            msg.setMessage(message, listener);
            synchronized(lastThinOutSet){
                lastThinOutSet.add(msg);
            }
        }
    }
    
    private class ThinOutTimeoutChecker implements DaemonRunnable{
        private long lastCheckTime = -1;
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            try{
                ctrl.sleep(thinOutTimeoutCheckInterval, true);
            }catch(InterruptedException e){
                return null;
            }
            synchronized(lastThinOutSet){
                return lastThinOutSet.toArray();
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            lastCheckTime = System.currentTimeMillis();
            if(paramObj == null){
                return;
            }
            Object[] thinOutMessages = (Object[])paramObj;
            for(int i = 0; i < thinOutMessages.length; i++){
                ThinOutMessage thinOutMessage = (ThinOutMessage)thinOutMessages[i];
                if(thinOutMessage.isTimeout(lastCheckTime)){
                    thinOutMessage.onMessage();
                }
            }
        }
        public void garbage(){}
    }
    
    private class ThinOutMessage{
        private Message message;
        private long thinOutTime = -1;
        private MessageListener listener;
        
        public synchronized void setMessage(Message msg, MessageListener listener){
            this.message = msg;
            this.listener = listener;
            thinOutTime = System.currentTimeMillis();
        }
        
        public synchronized void clear(){
            message = null;
            thinOutTime = -1;
        }
        
        public synchronized boolean isTimeout(long currentTime){
            return message != null && (thinOutTimeout <= (currentTime - thinOutTime));
        }
        
        public synchronized void onMessage(){
            if(message == null){
                return;
            }
            synchronized(listener){
                listener.onMessage(message);
            }
            clear();
        }
    }
}
