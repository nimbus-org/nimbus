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
 * 間引き送信を行う{@link ServerConnection}インタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class ThinOutServerConnectionImpl implements ServerConnection{
    
    private ServerConnection serverConnection;
    private ThinOutFilter[] filters;
    private Map lastThinOutMap;
    private Set lastThinOutSet;
    private long thinOutTimeout;
    private long thinOutTimeoutCheckInterval;
    private Daemon thinOutTimeoutChecker;
    
    public ThinOutServerConnectionImpl(
        ServerConnection connection,
        ThinOutFilter[] filters,
        long checkInterval,
        long timeout
    ){
        serverConnection = connection;
        this.filters = filters;
        lastThinOutMap = new HashMap();
        lastThinOutSet = new HashSet();
        thinOutTimeoutCheckInterval = checkInterval;
        thinOutTimeout = timeout;
        thinOutTimeoutChecker = new Daemon(new ThinOutTimeoutChecker());
        thinOutTimeoutChecker.setName(
            "Nimbus Publish ThinOutServerConnection ThinOutTimeoutChecker " + serverConnection
        );
        thinOutTimeoutChecker.setDaemon(true);
        thinOutTimeoutChecker.start();
    }
    
    public void send(Message message) throws MessageSendException{
        sendInternal(message, false);
    }
    
    public void sendAsynch(Message message) throws MessageSendException{
        sendInternal(message, true);
    }
    
    private synchronized void sendInternal(Message message, boolean isAsynch) throws MessageSendException{
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
                if(msg == null){
                    if(isAsynch){
                        serverConnection.sendAsynch(message);
                    }else{
                        serverConnection.send(message);
                    }
                }else{
                    synchronized(msg){
                        if(isAsynch){
                            serverConnection.sendAsynch(message);
                        }else{
                            serverConnection.send(message);
                        }
                    }
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
        msg.setMessage(message, isAsynch);
        synchronized(lastThinOutSet){
            lastThinOutSet.add(msg);
        }
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        return serverConnection.createMessage(subject, key);
    }
    
    public Message castMessage(Message message) throws MessageException{
        return serverConnection.castMessage(message);
    }
    
    public void addServerConnectionListener(ServerConnectionListener listener){
        serverConnection.addServerConnectionListener(listener);
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener){
        serverConnection.removeServerConnectionListener(listener);
    }
    
    public int getClientCount(){
        return serverConnection.getClientCount();
    }
    
    public Set getClientIds(){
        return serverConnection.getClientIds();
    }
    
    public Set getReceiveClientIds(Message message){
        return serverConnection.getReceiveClientIds(message);
    }
    
    public Set getSubjects(Object id){
        return serverConnection.getSubjects(id);
    }
    
    public Set getKeys(Object id, String subject){
        return serverConnection.getKeys(id, subject);
    }
    
    public void reset(){
        serverConnection.reset();
    }
    
    public void close(){
        if(thinOutTimeoutChecker != null){
            thinOutTimeoutChecker.stopNoWait();
            thinOutTimeoutChecker = null;
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
                    thinOutMessage.send();
                }
            }
        }
        public void garbage(){}
    }
    
    private class ThinOutMessage{
        private Message message;
        private boolean isAsynch;
        private long thinOutTime = -1;
        
        public synchronized void setMessage(Message msg, boolean isAsynch){
            this.message = msg;
            this.isAsynch = isAsynch;
            thinOutTime = System.currentTimeMillis();
        }
        
        public synchronized void clear(){
            message = null;
            thinOutTime = -1;
        }
        
        public synchronized boolean isTimeout(long currentTime){
            return message != null && (thinOutTimeout <= (currentTime - thinOutTime));
        }
        
        public synchronized void send(){
            if(message == null){
                return;
            }
            try{
                if(isAsynch){
                    serverConnection.sendAsynch(message);
                }else{
                    serverConnection.send(message);
                }
            }catch(MessageSendException e){
                // TODO
            }
            clear();
        }
    }
}
