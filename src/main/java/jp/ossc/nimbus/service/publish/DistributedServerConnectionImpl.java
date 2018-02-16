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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link ServerConnection}を分散するServerConnectionインタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class DistributedServerConnectionImpl implements ServerConnection{
    
    private ServerConnection templateConnection;
    private List connectionList = new ArrayList();
    private Map connctionMap = Collections.synchronizedMap(new HashMap());
    
    public void addServerConnection(ServerConnection connection){
        if(templateConnection == null){
            templateConnection = connection;
        }
        connectionList.add(new ServerConnectionImpl(connection));
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        return templateConnection.createMessage(subject, key);
    }
    
    public Message castMessage(Message message) throws MessageException{
        return templateConnection.castMessage(message);
    }
    
    private synchronized ServerConnection selectConnection(Message message){
        ServerConnectionImpl connection = (ServerConnectionImpl)connctionMap.get(message.getKey());
        if(connection == null){
            Collections.sort(connectionList);
            connection = (ServerConnectionImpl)connectionList.get(0);
            connection.addKey(message.getKey());
            connctionMap.put(message.getKey(), connection);
        }
        return connection;
    }
    
    public void send(Message message) throws MessageSendException{
        selectConnection(message).send(message);
    }
    
    public void sendAsynch(Message message) throws MessageSendException{
        selectConnection(message).sendAsynch(message);
    }
    
    public void addServerConnectionListener(ServerConnectionListener listener){
        templateConnection.addServerConnectionListener(listener);
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener){
        templateConnection.removeServerConnectionListener(listener);
    }
    
    public int getClientCount(){
        int result = 0;
        for(int i = 0; i < connectionList.size(); i++){
            result += ((ServerConnectionImpl)connectionList.get(i)).getClientCount();
            break;
        }
        return result;
    }
    
    public Set getClientIds(){
        Set result = new HashSet();
        for(int i = 0; i < connectionList.size(); i++){
            result.addAll(((ServerConnectionImpl)connectionList.get(i)).getClientIds());
            break;
        }
        return result;
    }
    
    public Set getReceiveClientIds(Message message){
        Set result = new HashSet();
        for(int i = 0; i < connectionList.size(); i++){
            result.addAll(((ServerConnectionImpl)connectionList.get(i)).getReceiveClientIds(message));
        }
        return result;
    }
    
    public Set getSubjects(Object id){
        Set result = null;
        for(int i = 0; i < connectionList.size(); i++){
            Set subjects = ((ServerConnectionImpl)connectionList.get(i)).getSubjects(id);
            if(subjects != null){
                if(result == null){
                    result = new HashSet();
                }
                result.addAll(subjects);
            }
        }
        return result;
    }
    
    public Set getKeys(Object id, String subject){
        Set result = null;
        for(int i = 0; i < connectionList.size(); i++){
            Set keys = ((ServerConnectionImpl)connectionList.get(i)).getKeys(id, subject);
            if(keys != null){
                if(result == null){
                    result = new HashSet();
                }
                result.addAll(keys);
            }
        }
        return result;
    }
    
    public void reset(){
        for(int i = 0; i < connectionList.size(); i++){
            ((ServerConnectionImpl)connectionList.get(i)).reset();
        }
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("connectionList=").append(connectionList);
        buf.append('}');
        return buf.toString();
    }
    
    private static class ServerConnectionImpl implements ServerConnection, Comparable{
        private int count;
        private Set keySet = Collections.synchronizedSet(new HashSet());
        private ServerConnection connection;
        
        public ServerConnectionImpl(ServerConnection connection){
            this.connection = connection;
        }
        
        public void addKey(String key){
            keySet.add(key);
        }
        
        public Message createMessage(String subject, String key) throws MessageCreateException{
            return connection.createMessage(subject, key);
        }
        
        public Message castMessage(Message message) throws MessageException{
            return connection.castMessage(message);
        }
        
        public void send(Message message) throws MessageSendException{
            connection.send(message);
            count++;
        }
        
        public void sendAsynch(Message message) throws MessageSendException{
            connection.sendAsynch(message);
            count++;
        }
        
        public void addServerConnectionListener(ServerConnectionListener listener) {
            connection.addServerConnectionListener(listener);
        }
        
        public void removeServerConnectionListener(ServerConnectionListener listener) {
            connection.removeServerConnectionListener(listener);
        }
        
        public int getClientCount(){
            return connection.getClientCount();
        }
        
        public Set getClientIds(){
            return connection.getClientIds();
        }
        
        public Set getReceiveClientIds(Message message){
            return connection.getReceiveClientIds(message);
        }
        
        public Set getSubjects(Object id){
            return connection.getSubjects(id);
        }
        
        public Set getKeys(Object id, String subject){
            return connection.getKeys(id, subject);
        }
        
        public void reset(){
            connection.reset();
        }
        
        public int compareTo(Object o){
            ServerConnectionImpl cmp = (ServerConnectionImpl)o;
            if(cmp.count > count){
                return -1;
            }else if(cmp.count < count){
                return 1;
            }
            if(cmp.keySet.size() > keySet.size()){
                return -1;
            }else if(cmp.keySet.size() < keySet.size()){
                return 1;
            }
            return 0;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("connection=").append(connection);
            buf.append(", count=").append(count);
            buf.append(", keySet=").append(keySet);
            buf.append('}');
            return buf.toString();
        }
    }
}