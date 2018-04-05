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

import java.util.Map;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.publish.*;

/**
 * {@link ServerConnection}に送信する{@link MessageWriter}サービス。
 *
 * @author M.Takata
 */
public class ServerConnectionWriterService extends ServiceBase implements MessageWriter, ServerConnectionWriterServiceMBean{
    
    private ServiceName serverConnectionFactoryServiceName;
    private ServerConnectionFactory serverConnectionFactory;
    private ServerConnection serverConnection;
    private String subjectKey;
    private String subject;
    private String keyKey;
    private String key;
    private boolean isAsynchSend;
    
    public void setServerConnectionFactoryServiceName(ServiceName name){
        serverConnectionFactoryServiceName = name;
    }
    public ServiceName getServerConnectionFactoryServiceName(){
        return serverConnectionFactoryServiceName;
    }
    
    public void setSubjectKey(String key){
        subjectKey = key;
    }
    public String getSubjectKey(){
        return subjectKey;
    }
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return subject;
    }
    
    public void setKeyKey(String key){
        keyKey = key;
    }
    public String getKeyKey(){
        return keyKey;
    }
    
    public void setKey(String key){
        this.key = key;
    }
    public String getKey(){
        return key;
    }
    
    public void setAsynchSend(boolean isAsynch){
        isAsynchSend = isAsynch;
    }
    public boolean isAsynchSend(){
        return isAsynchSend;
    }
    
    public void setServerConnectionFactory(ServerConnectionFactory factory){
        serverConnectionFactory = factory;
    }
    
    public void startService() throws Exception{
        if(serverConnectionFactoryServiceName != null){
            serverConnectionFactory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(serverConnectionFactoryServiceName);
        }
        if(serverConnectionFactory == null){
            throw new IllegalArgumentException("ServerConnectionFactory is null.");
        }
        if(subject == null){
            subject = getServiceNameObject() == null ? null : getServiceNameObject().toString();
        }
        serverConnection = serverConnectionFactory.getServerConnection();
    }
    
    public void stopService() throws Exception{
        serverConnection = null;
    }
    
    public void write(WritableRecord rec) throws MessageWriteException{
        final Map elementMap = rec.getElementMap();
        String subject = this.subject;
        if(subjectKey != null && elementMap.containsKey(subjectKey)){
            subject = elementMap.get(subjectKey).toString();
        }
        String key = this.key;
        if(keyKey != null && elementMap.containsKey(keyKey)){
            key = elementMap.get(keyKey).toString();
        }
        try{
            Message message = serverConnection.createMessage(subject, key);
            message.setObject(rec);
            if(isAsynchSend){
                serverConnection.sendAsynch(message);
            }else{
                serverConnection.send(message);
            }
        }catch(MessageException e){
            throw new MessageWriteException(e);
        }catch(MessageSendException e){
            throw new MessageWriteException(e);
        }
    }
}