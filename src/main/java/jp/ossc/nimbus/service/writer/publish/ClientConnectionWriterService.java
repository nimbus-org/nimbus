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
package jp.ossc.nimbus.service.writer.publish;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.publish.*;
import jp.ossc.nimbus.service.context.*;

/**
 * {@link ClientConnection}から受信して{@link MessageWriter}に書き出す{@link MessageWriter}サービス。
 *
 * @author M.Takata
 */
public class ClientConnectionWriterService extends ServiceBase implements MessageListener, ClientConnectionWriterServiceMBean{
    
    private ServiceName clientConnectionFactoryServiceName;
    private ClientConnectionFactory clientConnectionFactory;
    private ClientConnection clientConnection;
    private ServiceName messageReceiverServiceName;
    private MessageReceiver messageReceiver;
    private Map subjectMap;
    private ServiceName messageWriterServiceName;
    private MessageWriter messageWriter;
    private ServiceName contextServiceName;
    private Context context;
    private String subjectContextKey;
    private String keyContextKey;
    
    public void setClientConnectionFactoryServiceName(ServiceName name){
        clientConnectionFactoryServiceName = name;
    }
    public ServiceName getClientConnectionFactoryServiceName(){
        return clientConnectionFactoryServiceName;
    }
    
    public void setMessageReceiverServiceName(ServiceName name){
        messageReceiverServiceName = name;
    }
    public ServiceName getMessageReceiverServiceName(){
        return messageReceiverServiceName;
    }
    
    public void setSubject(String subject, String[] keys){
        subjectMap.put(subject, keys);
    }
    public Map getSubjectMap(){
        return subjectMap;
    }
    
    public void setMessageWriterServiceName(ServiceName name){
        messageWriterServiceName = name;
    }
    public ServiceName getMessageWriterServiceName(){
        return messageWriterServiceName;
    }
    
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    public void setSubjectContextKey(String key){
        subjectContextKey = key;
    }
    public String getSubjectContextKey(){
        return subjectContextKey;
    }
    
    public void setKeyContextKey(String key){
        keyContextKey = key;
    }
    public String getKeyContextKey(){
        return keyContextKey;
    }
    
    public void setClientConnectionFactory(ClientConnectionFactory factory){
        clientConnectionFactory = factory;
    }
    
    public void setMessageReceiver(MessageReceiver receiver){
        messageReceiver = receiver;
    }
    
    public void setMessageWriter(MessageWriter writer){
        messageWriter = writer;
    }
    
    public void setContext(Context context){
        this.context = context;
    }
    
    public void createService() throws Exception{
        subjectMap = new HashMap();
    }
    public void startService() throws Exception{
        if(messageWriterServiceName != null){
            messageWriter = (MessageWriter)ServiceManagerFactory.getServiceObject(messageWriterServiceName);
        }
        if(messageWriter == null){
            throw new IllegalArgumentException("MessageWriter is null.");
        }
        if(subjectMap.size() == 0){
            throw new IllegalArgumentException("Subject is null.");
        }
        if(clientConnectionFactoryServiceName != null){
            clientConnectionFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(clientConnectionFactoryServiceName);
        }
        if(clientConnectionFactory == null){
            if(messageReceiverServiceName != null){
                messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(messageReceiverServiceName);
            }
            if(messageReceiver == null){
                throw new IllegalArgumentException("ClientConnectionFactory and MessageReceiver is null.");
            }else{
                Iterator entries = subjectMap.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    messageReceiver.addSubject(this, (String)entry.getKey(), (String[])entry.getValue());
                }
            }
        }else{
            clientConnection = clientConnectionFactory.getClientConnection();
            clientConnection.connect();
            clientConnection.setMessageListener(this);
            Iterator entries = subjectMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                clientConnection.addSubject((String)entry.getKey(), (String[])entry.getValue());
            }
            clientConnection.startReceive();
        }
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
        }
    }
    
    public void stopService() throws Exception{
        if(clientConnection != null){
            clientConnection.close();
            clientConnection = null;
        }
        if(messageReceiver != null){
            messageReceiver.removeMessageListener(this);
            messageReceiver = null;
        }
    }
    
    public void onMessage(Message message){
        if(context != null){
            if(subjectContextKey != null){
                context.put(subjectContextKey, message.getSubject());
            }
            if(keyContextKey != null){
                context.put(keyContextKey, message.getKey());
            }
        }
        try{
            messageWriter.write((WritableRecord)message.getObject());
        }catch(Exception e){
        }
    }
}
