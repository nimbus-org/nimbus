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
    
    private List connectionList = new ArrayList();
    private Map connctionMap = Collections.synchronizedMap(new HashMap());
    private ServerConnectionBroadcaster broadcaster = new ServerConnectionBroadcaster();
    
    public void addServerConnection(ServerConnection connection){
        connectionList.add(new ServerConnectionImpl(connection));
        connection.addServerConnectionListener(broadcaster);
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        return selectConnection(key).createMessage(subject, key);
    }
    
    public Message castMessage(Message message) throws MessageException{
        return selectConnection(message.getKey()).castMessage(message);
    }
    
    private synchronized ServerConnection selectConnection(String key){
        ServerConnectionImpl connection = (ServerConnectionImpl)connctionMap.get(key);
        if(connection == null){
            for(int i = 0; i < connectionList.size(); i++){
                ((ServerConnectionImpl)connectionList.get(i)).prepareSort();
            }
            
            Collections.sort(connectionList);
            connection = (ServerConnectionImpl)connectionList.get(0);
            connection.addKey(key);
            connctionMap.put(key, connection);
        }
        return connection;
    }
    
    public void send(Message message) throws MessageSendException{
        selectConnection(message.getKey()).send(message);
    }
    
    public void sendAsynch(Message message) throws MessageSendException{
        selectConnection(message.getKey()).sendAsynch(message);
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
        private Map clients = new HashMap();
        private Set startReceives = new HashSet();
        
        public ClientImpl(Object id){
            this.id = id;
        }
        
        protected boolean addClient(Client client){
            boolean add = false;
            if(!clients.containsKey(client)){
                synchronized(clients){
                    if(!clients.containsKey(client)){
                        Map newClients = new HashMap();
                        newClients.putAll(clients);
                        newClients.put(client, new HashMap());
                        clients = newClients;
                        add = true;
                    }
                }
            }
            return add;
        }
        
        protected boolean removeClient(Client client){
            boolean remove = false;
            if(clients.containsKey(client)){
                synchronized(clients){
                    if(clients.containsKey(client)){
                        Map newClients = new HashMap();
                        newClients.putAll(clients);
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
        
        protected boolean addSubject(Client client, String subject, String[] keys){
            Map subjects = (Map)clients.get(client);
            Set keySet = (Set)subjects.get(subject);
            if(keySet == null){
                keySet = Collections.synchronizedSet(new HashSet());
                subjects.put(subject, keySet);
            }
            boolean isAdded = false;
            if(keys == null){
                isAdded = keySet.add(null);
            }else{
                for(int i = 0; i < keys.length; i++){
                    isAdded |= keySet.add(keys[i]);
                }
            }
            if(!isAdded){
                return false;
            }
            Iterator itr = clients.values().iterator();
            while(itr.hasNext()){
                keySet = (Set)((Map)itr.next()).get(subject);
                if(keySet == null){
                    return false;
                }
                if(keys == null){
                    if(!keySet.contains(null)){
                        return false;
                    }
                }else{
                    for(int i = 0; i < keys.length; i++){
                        if(!keySet.contains(keys[i])){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        
        protected boolean removeSubject(Client client, String subject, String[] keys){
            boolean result = true;
            Iterator itr = clients.values().iterator();
            while(itr.hasNext()){
                Set keySet = (Set)((Map)itr.next()).get(subject);
                if(keySet == null){
                    result = false;
                    break;
                }
                if(keys == null){
                    if(!keySet.contains(null)){
                        result = false;
                        break;
                    }
                }else{
                    boolean containsKey = false;
                    for(int i = 0; i < keys.length; i++){
                        if(keySet.contains(keys[i])){
                            containsKey = true;
                            break;
                        }
                    }
                    if(!containsKey){
                        result = false;
                        break;
                    }
                }
            }
            Map subjects = (Map)clients.get(client);
            Set keySet = (Set)subjects.get(subject);
            if(keySet == null){
                return false;
            }
            boolean isRemoved = false;
            if(keys == null){
                isRemoved = keySet.remove(null);
                if(keySet.size() == 0){
                    subjects.remove(subject);
                }
            }else{
                for(int i = 0; i < keys.length; i++){
                    isRemoved |= keySet.remove(keys[i]);
                }
                if(keySet.size() == 0){
                    subjects.remove(subject);
                }
            }
            return isConnected() && result && isRemoved;
        }
        
        protected boolean isLastStartedReceive(){
            return startReceives.size() == clients.size() - 1;
        }
        
        protected boolean isFirstStoppedReceive(){
            return startReceives.size() == clients.size();
        }
        
        protected void startReceive(Client client){
            startReceives.add(client);
        }
        
        protected void stopReceive(Client client){
            startReceives.remove(client);
        }
        
        protected boolean isFirstClosed(){
            return connectionList.size() - 1 == clients.size();
        }
        
        public Set getSubjects(){
            Set result = null;
            Iterator itr = clients.values().iterator();
            while(itr.hasNext()){
                Set subjects = ((Map)itr.next()).keySet();
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
        
        public Set getKeys(String subject){
            Set result = null;
            Iterator itr = clients.values().iterator();
            while(itr.hasNext()){
                Map subjects = (Map)itr.next();
                Set keys = (Set)subjects.get(subject);
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
        
        public boolean isStartReceive(){
            if(clients.size() == 0){
                return false;
            }
            Iterator itr = clients.keySet().iterator();
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
        private Object connectLock = new Object();
        private Object receiveLock = new Object();
        private Object subjectLock = new Object();
        
        public void addServerConnectionListener(ServerConnectionListener listener){
            if(listeners.contains(listener)){
                return;
            }
            Set newListeners = new LinkedHashSet(listeners);
            newListeners.add(listener);
            listeners = newListeners;
        }
        
        public void removeServerConnectionListener(ServerConnectionListener listener){
            if(!listeners.contains(listener)){
                return;
            }
            Set newListeners = new LinkedHashSet(listeners);
            newListeners.remove(listener);
            listeners = newListeners;
        }
        
        public void onConnect(Client client){
            synchronized(connectLock){
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
        }
        
        public void onAddSubject(Client client, String subject, String[] keys){
            synchronized(subjectLock){
                ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
                if(listeners.size() == 0
                    || wrapper == null
                    || !wrapper.isConnected()
                ){
                    return;
                }
                if(!wrapper.addSubject(client, subject, keys)){
                    return;
                }
                Iterator itr = listeners.iterator();
                while(itr.hasNext()){
                    ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                    listener.onAddSubject(wrapper, subject, keys);
                }
            }
        }
        
        public void onRemoveSubject(Client client, String subject, String[] keys){
            synchronized(subjectLock){
                ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
                if(listeners.size() == 0
                    || wrapper == null
                ){
                    return;
                }
                if(!wrapper.removeSubject(client, subject, keys)){
                    return;
                }
                Iterator itr = listeners.iterator();
                while(itr.hasNext()){
                    ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                    listener.onRemoveSubject(wrapper, subject, keys);
                }
            }
        }
        
        public void onStartReceive(Client client, long from){
            synchronized(receiveLock){
                ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
                if(listeners.size() == 0
                    || wrapper == null
                ){
                    return;
                }
                try{
                    if(wrapper.isLastStartedReceive()){
                        Iterator itr = listeners.iterator();
                        while(itr.hasNext()){
                            ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                            listener.onStartReceive(wrapper, from);
                        }
                    }
                }finally{
                    wrapper.startReceive(client);
                }
            }
        }
        
        public void onStopReceive(Client client){
            synchronized(receiveLock){
                ClientImpl wrapper = (ClientImpl)clients.get(client.getId());
                if(listeners.size() == 0
                    || wrapper == null
                ){
                    return;
                }
                try{
                    if(wrapper.isFirstStoppedReceive()){
                        Iterator itr = listeners.iterator();
                        while(itr.hasNext()){
                            ServerConnectionListener listener = (ServerConnectionListener)itr.next();
                            listener.onStopReceive(wrapper);
                        }
                    }
                }finally{
                    wrapper.stopReceive(client);
                }
            }
        }
        
        public void onClose(Client client){
            synchronized(connectLock){
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
}