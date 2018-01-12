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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

public class MessageForwardingService extends MessageReceiverService
 implements MessageForwardingServiceMBean, ServerConnectionListener{
    
    private static final long serialVersionUID = 5179315238481930410L;
    
    private String sendErrorMessageId = MSG_ID_SEND_ERROR;
    private String forwardErrorMessageId = MSG_ID_FORWARD_ERROR;
    
    private ServerConnectionFactory serverConnectionFactory;
    private ServiceName serverConnectionFactoryServiceName;
    private boolean isAsynchSend;
    
    private Map clientMap;
    private ClientMessageListener myMessageListener;
    private Map subjects;
    
    private ServerConnection serverConnection;
    
    public String getSendErrorMessageId(){
        return sendErrorMessageId;
    }
    
    public void setSendErrorMessageId(String id){
        this.sendErrorMessageId = id;
    }
    
    public String getForwardErrorMessageId(){
        return forwardErrorMessageId;
    }
    
    public void setForwardErrorMessageId(String id){
        this.forwardErrorMessageId = id;
    }
    
    public ServiceName getServerConnectionFactoryServiceName() {
        return serverConnectionFactoryServiceName;
    }
    
    public void setServerConnectionFactoryServiceName(ServiceName serverConnectionFactoryServiceName){
        this.serverConnectionFactoryServiceName = serverConnectionFactoryServiceName;
    }
    
    public void setServerConnectionFactory(ServerConnectionFactory serverConnectionFactory){
        this.serverConnectionFactory = serverConnectionFactory;
    }
    
    public ServerConnectionFactory getServerConnectionFactory(){
        return serverConnectionFactory;
    }
    
    public void setAsynchSend(boolean isAsynch){
        isAsynchSend = isAsynch;
    }
    public boolean isAsynchSend(){
        return isAsynchSend;
    }
    
    public void addSubject(String subject){
        addSubject(subject, null);
    }
    
    public void addSubject(String subject, String[] keys){
        if(subjects == null){
            subjects = Collections.synchronizedMap(new HashMap());
        }
        Set keySet = (Set)subjects.get(subject);
        if(keySet == null){
            keySet = Collections.synchronizedSet(new HashSet());
            subjects.put(subject, keySet);
        }
        if(keys == null){
            keySet.add(null);
        }else{
            for(int i = 0; i < keys.length; i++){
                keySet.add(keys[i]);
            }
        }
    }
    
    public void createService() throws Exception{
        super.createService();
        clientMap = Collections.synchronizedMap(new LinkedHashMap());
        myMessageListener = new ClientMessageListener();
    }
    
    public void startService() throws Exception{
        if(serverConnectionFactory != null){
            serverConnection = serverConnectionFactory.getServerConnection();
        }else{
            if(serverConnectionFactoryServiceName != null){
                serverConnectionFactory = (ServerConnectionFactory) ServiceManagerFactory
                        .getServiceObject(serverConnectionFactoryServiceName);
                serverConnection = serverConnectionFactory
                        .getServerConnection();
            }else{
                throw new IllegalArgumentException(
                        "ServerConnectionFactoryServiceName is null.");
            }
        }
        serverConnection.addServerConnectionListener(this);
        
        super.startService();
    }
    
    public void stopService() throws Exception{
        serverConnection.removeServerConnectionListener(this);
        super.stopService();
        serverConnection = null;
    }
    
    public void destroyService() throws Exception{
        clientMap = null;
        myMessageListener = null;
        super.destroyService();
    }
    
    protected void handleMessage(Message message){
        Iterator sbjs = message.getSubjects().iterator();
        while(sbjs.hasNext()){
            String sbj = (String)sbjs.next();
            Subject subject = (Subject)subjectMap.get(sbj);
            if(subject == null){
                continue;
            }
            if(!subject.existsMessageListener(message)){
                continue;
            }
            subject.onMessage(message);
            try{
                if(isAsynchSend){
                    serverConnection.sendAsynch(serverConnection.castMessage(message));
                }else{
                    serverConnection.send(serverConnection.castMessage(message));
                }
            }catch(MessageException e){
                if(forwardErrorMessageId != null){
                    getLogger().write(forwardErrorMessageId, new Object[]{serverConnection, message}, e);
                }
            }catch(MessageSendException e){
                if(forwardErrorMessageId != null){
                    getLogger().write(forwardErrorMessageId, new Object[]{serverConnection, message}, e);
                }
            }
            return;
        }
    }
    
    public void onConnect(Client client){
        synchronized(clientMap){
            try{
                if(!isConnected()){
                    connect();
                }
            }catch(Exception e){
                if(sendErrorMessageId != null){
                    getLogger().write(sendErrorMessageId, new Object[]{client}, e);
                }
            }
            if(clientMap.size() == 0 && subjects != null){
                try{
                    final Iterator entries = subjects.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        String subject = (String)entry.getKey();
                        Set keySet = (Set)entry.getValue();
                        addSubject(myMessageListener, subject, (String[])keySet.toArray(new String[keySet.size()]));
                    }
                }catch(MessageSendException e){
                    if(sendErrorMessageId != null){
                        getLogger().write(sendErrorMessageId, new Object[]{client}, e);
                    }
                }
            }
            if(!clientMap.containsKey(client.getId())){
                clientMap.put(client.getId(), new ClientMessageListener());
            }
        }
    }
    
    public void onAddSubject(Client client, String subject, String[] keys){
        synchronized(clientMap){
            try{
                MessageListener listener = (MessageListener) clientMap.get(client.getId());
                if(listener != null){
                    addSubject(listener, subject, keys);
                }
            }catch(MessageSendException e){
                if(sendErrorMessageId != null){
                    getLogger().write(sendErrorMessageId, new Object[]{client}, e);
                }
            }
        }
    }
    
    public void onRemoveSubject(Client client, String subject, String[] keys){
        synchronized(clientMap){
            try{
                MessageListener listener = (MessageListener) clientMap.get(client.getId());
                if(listener != null){
                    removeSubject(listener, subject, keys);
                }
            }catch(MessageSendException e){
                if(sendErrorMessageId != null){
                    getLogger().write(sendErrorMessageId, new Object[]{client}, e);
                }
            }
        }
    }
    
    public void onStartReceive(Client client, long from){
        synchronized(clientMap){
            try{
                MessageListener listener = (MessageListener) clientMap.get(client.getId());
                if(listener != null){
                    startReceive(from);
                }
            }catch(MessageSendException e){
                if(sendErrorMessageId != null){
                    getLogger().write(sendErrorMessageId, new Object[]{client}, e);
                }
            }
        }
    }
    
    public void onStopReceive(Client client){
    }
    
    public void onClose(Client client){
        synchronized(clientMap){
            try{
                MessageListener listener = (MessageListener) clientMap.get(client.getId());
                if(listener != null){
                    removeMessageListener(listener);
                }
                clientMap.remove(client.getId());
                if(clientMap.size() == 0){
                    removeMessageListener(myMessageListener);
                    stopReceive();
                    serverConnection.reset();
                }
            }catch(MessageSendException e){
                if(sendErrorMessageId != null){
                    getLogger().write(sendErrorMessageId, new Object[]{client}, e);
                }
            }
        }
    }
    
    private class ClientMessageListener implements MessageListener{
        public void onMessage(Message message){
        }
    }
}
