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

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;

/**
 * メッセージ送信コンテナクラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class DefaultPublishContainerService extends ServiceBase
 implements DefaultPublishContainerServiceMBean, PublishContainer{
    
    private static final long serialVersionUID = -3800977450032100141L;
    
    private static final String MSG_ID_00001 = "DPC__00001";
    private static final String MSG_ID_00002 = "DPC__00002";
    
    private List servants;
    private Map messageListenerMap;
    
    private volatile int servantNum;
    
    private int maxServantNum;
    
    private long publishCount;
    
    private ServiceName queueServiceName;
    private jp.ossc.nimbus.service.queue.Queue queue;
    
    private MessageReceiver messageReceiver;
    
    private Daemon daemon;
    
    private int threadPriority = -1;
    private long publishTimeout = 1000l;
    
    public void setMaxServantNum(int maxServant){
        maxServantNum = maxServant;
    }
    public int getMaxServantNum(){
        return maxServantNum;
    }
    
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    public void setThreadPriority(int priority){
        threadPriority = priority;
    }
    public int getThreadPriority(){
        return threadPriority;
    }
    
    public void setPublishTimeout(long timeout){
        publishTimeout = timeout;
    }
    public long getPublishTimeout(){
        return publishTimeout;
    }
    
    public void setMessageReceiver(MessageReceiver receiver){
        messageReceiver = receiver;
    }
    
    public void createService() throws Exception{
        servants = new ArrayList();
        messageListenerMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception{
        if(maxServantNum <= 0){
            throw new IllegalArgumentException(
                "maxServantNum is illegal : " + maxServantNum
            );
        }
        if(servants.size() == 0){
            for(int i = 0; i < maxServantNum; i++){
                servants.add(null);
            }
        }
        if(queueServiceName != null){
            queue = (jp.ossc.nimbus.service.queue.Queue)ServiceManagerFactory
                .getServiceObject(queueServiceName);
            queue.accept();
            daemon = new Daemon(new MessageHandler());
            daemon.setName("Nimbus PublishContainerMessageHandlerDaemon " + getServiceNameObject());
            if(threadPriority > 0){
                daemon.setPriority(threadPriority);
            }
            daemon.start();
        }
    }
    
    public void stopService() throws Exception{
        
        if(daemon != null){
            daemon.stop();
            daemon = null;
            queue.release();
        }
        
        for(int i = 0; i < maxServantNum; i++){
            final Servant servant = (Servant)servants.get(i);
            if(servant != null){
                ejectServant(servant, true);
            }
        }
        servantNum = 0;
    }
    
    public void destroyService() throws Exception{
        servants = null;
        messageListenerMap = null;
    }
    
    public int getVacantServantNum(){
        return maxServantNum - servantNum;
    }
    
    public int getServantNum(){
        return servantNum;
    }
    
    public void handleMessage(Object msg){
        if(servantNum == 0){
            return;
        }
        if(queue == null){
            if(msg == null){
                return;
            }
            internalHandleMessage(msg);
        }else{
            queue.push(msg);
        }
    }
    
    protected void internalHandleMessage(Object msg){
        if(servants != null){
            for(int i = 0; i < maxServantNum; i++){
                final Servant sv = (Servant)servants.get(i);
                if(sv != null && sv.isConnect()){
                    try{
                        if(msg == null){
                            sv.onPublishTimeout();
                        }else{
                            sv.sendMessage(msg);
                        }
                    }catch(MessageSendException e){
                        getLogger().write(MSG_ID_00001, e);
                    }
                }
            }
        }
    }
    
    public synchronized boolean entryServant(Servant servant){
        if(servantNum >= maxServantNum){
            return false;
        }else{
            if(servants.contains(servant)){
                return false;
            }
            for(int i = 0; i < maxServantNum; i++){
                if(servants.get(i) == null){
                    servant.setContainer(this);
                    servants.set(i, servant);
                    if(messageReceiver != null){
                        messageListenerMap.put(servant, new MessageListenerImpl(servant));
                    }
                    servantNum++;
                    return true;
                }
            }
            return false;
        }
    }
    
    public synchronized boolean ejectServant(Servant servant){
        return ejectServant(servant, false);
    }
    
    public synchronized boolean ejectServant(Servant servant, boolean isForced){
        final int index = servants.indexOf(servant);
        if(index != -1){
            if(!servant.close(isForced) && !isForced){
                return false;
            }
            publishCount += servant.getPublishCount();
            MessageListener listener = (MessageListener)messageListenerMap.remove(servant);
            if(listener != null && messageReceiver != null){
                try{
                    messageReceiver.removeMessageListener(listener);
                }catch(MessageSendException e){
                }
            }
            servant.setContainer(null);
            servants.set(index, null);
            servantNum--;
        }
        return true;
    }
    
    public Set garbage(){
        Set result = null;
        for(int i = 0; i < maxServantNum; i++){
            final Servant servant = (Servant)servants.get(i);
            if(servant != null && !servant.isAlive()){
                if(ejectServant(servant, true)){
                    getLogger().write(MSG_ID_00002, servant.getID());
                    if(result == null){
                        result = new HashSet();
                    }
                    result.add(servant);
                }
            }
        }
        return result;
    }
    
    public synchronized long getPublishCount(){
        long count = publishCount;
        for(int i = 0; i < maxServantNum; i++){
            final Servant servant = (Servant)servants.get(i);
            if(servant != null){
                count += servant.getPublishCount();
            }
        }
        return count;
    }
    
    public void addSubject(Servant servant, String subject) throws MessageSendException{
        MessageListener listener = (MessageListener)messageListenerMap.get(servant);
        if(listener != null && messageReceiver != null){
            messageReceiver.addSubject(listener, subject);
        }
    }
    
    public void addSubject(Servant servant, String subject, String[] keys) throws MessageSendException{
        MessageListener listener = (MessageListener)messageListenerMap.get(servant);
        if(listener != null && messageReceiver != null){
            messageReceiver.addSubject(listener, subject, keys);
        }
    }
    
    public void removeSubject(Servant servant, String subject) throws MessageSendException{
        MessageListener listener = (MessageListener)messageListenerMap.get(servant);
        if(listener != null && messageReceiver != null){
            messageReceiver.removeSubject(listener, subject);
        }
    }
    
    public void removeSubject(Servant servant, String subject, String[] keys) throws MessageSendException{
        MessageListener listener = (MessageListener)messageListenerMap.get(servant);
        if(listener != null && messageReceiver != null){
            messageReceiver.removeSubject(listener, subject, keys);
        }
    }
    
    protected void handleMessage(Servant servant, Message message){
        if(servant.isConnect()){
            try{
                servant.sendMessage(message.getObject());
            }catch(MessageException e){
                getLogger().write(MSG_ID_00001, e);
            }catch(MessageSendException e){
                getLogger().write(MSG_ID_00001, e);
            }
        }
    }
    
    private class MessageHandler implements DaemonRunnable{
        
        // Daemon のJavaDoc
        public boolean onStart(){
            return true;
        }
        
        // Daemon のJavaDoc
        public boolean onStop(){
            return true;
        }
        
        // Daemon のJavaDoc
        public boolean onSuspend(){
            return true;
        }
        
        // Daemon のJavaDoc
        public boolean onResume(){
            return true;
        }
        
        // Daemon のJavaDoc
        public Object provide(DaemonControl ctrl) throws Throwable{
            if(queue == null){
                return null;
            }
            return queue.get(publishTimeout);
        }
        
        // Daemon のJavaDoc
        public void consume(Object paramObj, DaemonControl ctrl){
            internalHandleMessage(paramObj);
        }
        
        // Daemon のJavaDoc
        public void garbage(){
            if(queue != null){
                while(queue.size() > 0){
                    consume(queue.get(0), daemon);
                }
            }
        }
    }
    
    private class MessageListenerImpl implements MessageListener{
        private Servant servant;
        
        public MessageListenerImpl(Servant servant){
            this.servant = servant;
        }
        
        public void onMessage(Message message){
            handleMessage(servant, message);
        }
    }
}
