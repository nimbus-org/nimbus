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

/**
 * {@link ServerConnection}を分散するServerConnectionインタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class DistributedServerConnectionImpl implements ServerConnection{
    
    private ServerConnection templateConnection;
    private List connectionList = new ArrayList();
    private Map connctionMap = Collections.synchronizedMap(new HashMap());
    private ServerConnectionBroadcaster broadcaster = new ServerConnectionBroadcaster();
    
    public void addServerConnection(ServerConnection connection){
        if(templateConnection == null){
            templateConnection = connection;
        }
        connectionList.add(new ServerConnectionImpl(connection));
        connection.addServerConnectionListener(broadcaster);
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
            for(int i = 0; i < connectionList.size(); i++){
                ((ServerConnectionImpl)connectionList.get(i)).prepareSort();
            }
            
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
        broadcaster.addServerConnectionListener(listener);
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener){
        broadcaster.removeServerConnectionListener(listener);
    }
    
    public int getClientCount(){
        return getClientIds().size();
    }
    
    public Set getClientIds(){
        Set result = null;
        for(int i = 0; i < connectionList.size(); i++){
            Set ids = ((ServerConnectionImpl)connectionList.get(i)).getClientIds();
            if(ids != null){
                if(result == null){
                    result = new HashSet(ids);
                }else{
                    result.retainAll(ids);
                }
            }
        }
        return result == null ? new HashSet() : result;
    }
    
    public Set getReceiveClientIds(Message message){
        Set result = null;
        for(int i = 0; i < connectionList.size(); i++){
            Set ids = ((ServerConnectionImpl)connectionList.get(i)).getReceiveClientIds(message);
            if(ids != null){
                if(result == null){
                    result = new HashSet(ids);
                }else{
                    result.retainAll(ids);
                }
            }
        }
        return result == null ? new HashSet() : result;
    }
    
    public Set getSubjects(Object id){
        Set result = null;
        for(int i = 0; i < connectionList.size(); i++){
            Set subjects = ((ServerConnectionImpl)connectionList.get(i)).getSubjects(id);
            if(subjects != null){
                if(result == null){
                    result = new HashSet(subjects);
                }else{
                    result.retainAll(subjects);
                }
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
                    result = new HashSet(keys);
                }else{
                    result.retainAll(keys);
                }
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
        private int countForSort;
        
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
        
        public void prepareSort(){
            countForSort = count;
        }
        
        public int compareTo(Object o){
            ServerConnectionImpl cmp = (ServerConnectionImpl)o;
            if(cmp.countForSort > countForSort){
                return -1;
            }else if(cmp.countForSort < countForSort){
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
    
    private class ClientImpl implements Client{
        
        private Object id;
        private Set clients = new HashSet();
        
        public ClientImpl(Object id){
            this.id = id;
        }
        
        protected boolean addClient(Client client){
            boolean add = false;
            if(!clients.contains(client)){
                synchronized(clients){
                    if(!clients.contains(client)){
                        Set newClients = new HashSet();
                        newClients.addAll(clients);
                        newClients.add(client);
                        clients = newClients;
                        add = true;
                    }
                }
            }
            return add;
        }
        
        protected boolean removeClient(Client client){
            boolean remove = false;
            if(clients.contains(client)){
                synchronized(clients){
                    if(clients.contains(client)){
                        Set newClients = new HashSet();
                        newClients.addAll(clients);
                        newClients.remove(client);
                        clients = newClients;
                        remove = true;
                    }
                }
            }
            return remove;
        }
        
        protected boolean isConnected(){
            return connectionList.size() == clients.size();
        }
        
        protected boolean isFirstRemovedSubject(String subject, String[] keys){
            int removedCount = 0;
            for(int i = 0; i < connectionList.size(); i++){
                Set keySet = ((ServerConnectionImpl)connectionList.get(i)).getKeys(id, subject);
                if(keySet == null){
                    removedCount++;
                }else if(keys == null){
                    continue;
                }else{
                    boolean isRemoved = true;
                    for(int j = 0; j < keys.length; j++){
                        if(keySet.contains(keys[i])){
                            isRemoved = false;
                            break;
                        }
                    }
                    if(!isRemoved){
                        continue;
                    }
                    removedCount++;
                }
                if(removedCount > 1){
                    return false;
                }
            }
            return true;
        }
        
        protected boolean isFirstStoppedReceive(){
            int stoppedCount = 0;
            Iterator itr = clients.iterator();
            while(itr.hasNext()){
                Client client = (Client)itr.next();
                if(!client.isStartReceive()){
                    stoppedCount++;
                }
                if(stoppedCount > 1){
                    return false;
                }
            }
            return true;
        }
        
        protected boolean isFirstClosed(){
            return connectionList.size() - 1 == clients.size();
        }
        
        public Set getSubjects(){
            return DistributedServerConnectionImpl.this.getSubjects(id);
        }
        
        public Set getKeys(String subject){
            return DistributedServerConnectionImpl.this.getKeys(id, subject);
        }
        
        public boolean isStartReceive(){
            if(clients.size() == 0){
                return false;
            }
            Iterator itr = clients.iterator();
            while(itr.hasNext()){
                Client client = (Client)itr.next();
                if(!client.isStartReceive()){
                    return false;
                }
            }
            return true;
        }
        
        public Object getId(){
            return id;
        }
    }
    
    private class ServerConnectionBroadcaster implements ServerConnectionListener{
        
        private Map clients = new HashMap();
        private Set listeners = new LinkedHashSet();
        
        public void addServerConnectionListener(ServerConnectionListener listener) {
            listeners.add(listener);
        }
        
        public void removeServerConnectionListener(ServerConnectionListener listener) {
            listeners.remove(listener);
        }
        
        public void onConnect(Client client){
            ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
            if(wrapper == null){
                synchronized(clients){
                    wrapper = (ClientImpl)clients.get(client.getId());
                    if(wrapper == null){
                        Map newClients = new HashMap();
                        newClients.putAll(clients);
                        wrapper = new ClientImpl(client.getId());
                        newClients.put(client.getId(), wrapper);
                        clients = newClients;
                    }
                }
            }
            if(wrapper.addClient(client) && wrapper.isConnected() && listeners.size() != 0){
                Iterator itr = listeners.iterator();
                while(itr.hasNext()){
                    ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                    listener.onConnect(wrapper);
                }
            }
        }
        
        public void onAddSubject(Client client, String subject, String[] keys){
            ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
            if(listeners.size() == 0
                || wrapper == null
                || !wrapper.isConnected()
                || !wrapper.getSubjects().contains(subject)
            ){
                return;
            }
            if(keys != null && keys.length != 0){
                Set keySet = wrapper.getKeys(subject);
                if(keySet == null){
                    return;
                }
                for(int i = 0; i < keys.length; i++){
                    if(!keySet.contains(keys[i])){
                        return;
                    }
                }
            }
            Iterator itr = listeners.iterator();
            while(itr.hasNext()){
                ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                listener.onAddSubject(wrapper, subject, keys);
            }
        }
        
        public void onRemoveSubject(Client client, String subject, String[] keys){
            ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
            if(listeners.size() == 0
                || wrapper == null
                || !wrapper.isConnected()
                || !wrapper.isFirstRemovedSubject(subject, keys)
            ){
                return;
            }
            Iterator itr = listeners.iterator();
            while(itr.hasNext()){
                ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                listener.onRemoveSubject(wrapper, subject, keys);
            }
        }
        
        public void onStartReceive(Client client, long from){
            ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
            if(listeners.size() == 0
                || wrapper == null
                || !wrapper.isConnected()
                || !wrapper.isStartReceive()
            ){
                return;
            }
            Iterator itr = listeners.iterator();
            while(itr.hasNext()){
                ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                listener.onStartReceive(wrapper, from);
            }
        }
        
        public void onStopReceive(Client client){
            ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
            if(listeners.size() == 0
                || wrapper == null
                || !wrapper.isConnected()
                || !wrapper.isFirstStoppedReceive()
            ){
                return;
            }
            Iterator itr = listeners.iterator();
            while(itr.hasNext()){
                ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                listener.onStopReceive(wrapper);
            }
        }
        
        public void onClose(Client client){
            ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
            if(listeners.size() == 0
                || wrapper == null
                || !wrapper.removeClient(client)
                || !wrapper.isFirstClosed()
            ){
                return;
            }
            Iterator itr = listeners.iterator();
            while(itr.hasNext()){
                ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                listener.onClose(wrapper);
            }
        }
    }
}