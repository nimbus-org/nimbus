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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * {@link ServerConnection}をグルーピングするServerConnectionインタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class GroupServerConnectionImpl implements ServerConnection{
    
    private Map connctionMap = Collections.synchronizedMap(new LinkedHashMap());
    
    public void addServerConnection(String subject, Pattern keyPattern, ServerConnection connection){
        List connections = (List)connctionMap.get(subject);
        if(connections == null){
            connections = new ArrayList();
            connctionMap.put(subject, connections);
        }
        connections.add(new ServerConnectionImpl(subject, keyPattern, connection));
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        final List connections = (List)connctionMap.get(subject);
        if(connections == null){
            throw new MessageCreateException("ServerConnection not found. subject=" + subject + ", key=" + key);
        }
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
            if(connection.isMatch(subject, key)){
                return connection.createMessage(subject, key);
            }
        }
        throw new MessageCreateException("ServerConnection not found. subject=" + subject + ", key=" + key);
    }
    
    public Message castMessage(Message message) throws MessageException{
        String subject = message.getSubject();
        String key = message.getKey();
        final List connections = (List)connctionMap.get(subject);
        if(connections == null){
            throw new MessageCreateException("ServerConnection not found. subject=" + subject + ", key=" + key);
        }
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
            if(connection.isMatch(subject, key)){
                return connection.castMessage(message);
            }
        }
        throw new MessageException("ServerConnection not found. subject=" + subject + ", key=" + key);
    }
    
    public void send(Message message) throws MessageSendException{
        final List connections = (List)connctionMap.get(message.getSubject());
        if(connections == null){
            throw new MessageSendException("ServerConnection not found. subject=" + message.getSubject() + ", key=" + message.getKey());
        }
        
        boolean isSend = false;
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
            if(connection.isMatch(message.getSubject(), message.getKey())){
                Message castMessage;
                try {
                    castMessage = connection.castMessage(message);
                } catch (MessageException ex) {
                    throw new MessageSendException("Message Cast Error.", ex);
                }
                connection.send(castMessage);
                isSend = true;
            }
        }
        if(!isSend){
            throw new MessageSendException("ServerConnection not found. subject=" + message.getSubject() + ", key=" + message.getKey());
        }
    }
    
    public void sendAsynch(Message message) throws MessageSendException{
        final List connections = (List)connctionMap.get(message.getSubject());
        if(connections == null){
            throw new MessageSendException("ServerConnection not found. subject=" + message.getSubject() + ", key=" + message.getKey());
        }
        
        boolean isSend = false;
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
            if(connection.isMatch(message.getSubject(), message.getKey())){
                Message castMessage;
                try {
                    castMessage = connection.castMessage(message);
                } catch (MessageException ex) {
                    throw new MessageSendException("Message Cast Error.", ex);
                }
                connection.send(castMessage);
                isSend = true;
            }
        }
        if(!isSend){
            throw new MessageSendException("ServerConnection not found. subject=" + message.getSubject() + ", key=" + message.getKey());
        }
    }
    
    public void addServerConnectionListener(ServerConnectionListener listener) {
        if(connctionMap != null){
            Iterator itr = connctionMap.values().iterator();
            while(itr.hasNext()){
                List connections = (List)itr.next();
                if(connections != null){
                    for(int i = 0, imax = connections.size(); i < imax; i++){
                        ServerConnection connection = (ServerConnection)connections.get(i);
                        connection.addServerConnectionListener(listener);
                    }
                }
            }
        }
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener) {
        if(connctionMap != null){
            Iterator itr = connctionMap.values().iterator();
            while(itr.hasNext()){
                List connections = (List)itr.next();
                if(connections != null){
                    for(int i = 0, imax = connections.size(); i < imax; i++){
                        ServerConnection connection = (ServerConnection)connections.get(i);
                        connection.removeServerConnectionListener(listener);
                    }
                }
            }
        }
    }
    
    public int getClientCount(){
        if(connctionMap.size() == 0){
            return 0;
        }
        Iterator itr = connctionMap.values().iterator();
        int result = 0;
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
                result += connection.getClientCount();
            }
        }
        return result;
    }
    
    public Set getClientIds(){
        Set result = new HashSet();
        Iterator itr = connctionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
                result.addAll(connection.getClientIds());
            }
        }
        return result;
    }
    
    public Set getReceiveClientIds(Message message){
        Set result = new HashSet();
        Iterator itr = connctionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
                result.addAll(connection.getReceiveClientIds(message));
            }
        }
        return result;
    }
    
    public Set getSubjects(Object id){
        Set result = null;
        Iterator itr = connctionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
                Set subjects = connection.getSubjects(id);
                if(subjects != null){
                    if(result == null){
                        result = new HashSet();
                    }
                    result.addAll(subjects);
                }
            }
        }
        return result;
    }
    
    public Set getKeys(Object id, String subject){
        Set result = null;
        Iterator itr = connctionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
                Set keys = connection.getKeys(id, subject);
                if(keys != null){
                    if(result == null){
                        result = new HashSet();
                    }
                    result.addAll(keys);
                }
            }
        }
        return result;
    }
    
    public void reset(){
        Iterator itr = connctionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ServerConnectionImpl connection = (ServerConnectionImpl)connections.get(i);
                connection.reset();
            }
        }
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("connctionMap=").append(connctionMap);
        buf.append('}');
        return buf.toString();
    }
    
    private static class ServerConnectionImpl implements ServerConnection{
        private String subject;
        private Pattern keyPattern;
        private ServerConnection connection;
        
        public ServerConnectionImpl(String subject, Pattern keyPattern, ServerConnection connection){
            this.subject = subject;
            this.keyPattern = keyPattern;
            this.connection = connection;
        }
        
        public boolean isMatch(String subject, String key){
            if((this.subject == null && subject != null)
                || (this.subject != null && subject == null)
                || (this.subject != null && !this.subject.equals(subject))
                || (keyPattern != null && key == null)
                || (keyPattern != null && !keyPattern.matcher(key).matches())
            ){
                return false;
            }
            return true;
        }
        
        public Message createMessage(String subject, String key) throws MessageCreateException{
            return connection.createMessage(subject, key);
        }
        
        public Message castMessage(Message message) throws MessageException{
            return connection.castMessage(message);
        }
        
        public void send(Message message) throws MessageSendException{
            connection.send(message);
        }
        
        public void sendAsynch(Message message) throws MessageSendException{
            connection.sendAsynch(message);
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
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("subject=").append(subject);
            buf.append(", key=").append(keyPattern == null ? null : keyPattern.pattern());
            buf.append(", connection=").append(connection);
            buf.append('}');
            return buf.toString();
        }
    }
}