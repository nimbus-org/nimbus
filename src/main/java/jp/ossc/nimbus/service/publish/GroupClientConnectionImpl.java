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
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.io.Serializable;
import java.net.UnknownHostException;

import jp.ossc.nimbus.util.net.GlobalUID;

/**
 * {@link ClientConnection}をグルーピングするClientConnectionインタフェース実装クラス。<p>
 * 
 * @author M.Takata
 */
public class GroupClientConnectionImpl implements ClientConnection, Serializable{
    
    private static final long serialVersionUID = 6130317900119964898L;
    
    private Map connectionMap = Collections.synchronizedMap(new LinkedHashMap());
    
    private Object id;
    private transient String serviceManagerName;
    private transient boolean isStartReceive;
    
    public void addClientConnection(String subject, Pattern keyPattern, ClientConnection connection){
        List connections = (List)connectionMap.get(subject);
        if(connections == null){
            connections = new ArrayList();
            connectionMap.put(subject, connections);
        }
        connections.add(
            new ClientConnectionImpl(
                subject,
                keyPattern,
                connection
            )
        );
    }
    
    public void setServiceManagerName(String name){
        serviceManagerName = name;
    }
    
    public void connect() throws ConnectException{
        connect(null);
    }
    
    public void connect(Object id) throws ConnectException{
        if(id == null){
            try{
                this.id = new GlobalUID();
            }catch(UnknownHostException e){
                throw new ConnectException(e);
            }
        }else{
            this.id = id;
        }
    }
    
    private List connect(String subject, String[] keys) throws ConnectException{
        final List connections = (List)connectionMap.get(subject);
        if(connections == null){
            throw new ConnectException("ClientConnection not found. subject=" + subject);
        }
        List result = new ArrayList();
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
            if(keys == null || keys.length == 0){
                if(connection.isMatch(subject, null)){
                    connection.setServiceManagerName(serviceManagerName);
                    connection.connect(id);
                    if(isStartReceive){
                        try{
                            connection.startReceive();
                        }catch(MessageSendException e){
                            throw new ConnectException(e);
                        }
                    }
                    result.add(connection);
                }
            }else{
                for(int j = 0; j < keys.length; j++){
                    if(connection.isMatch(subject, keys[j])){
                        connection.setServiceManagerName(serviceManagerName);
                        connection.connect(id);
                        if(isStartReceive){
                            try{
                                connection.startReceive();
                            }catch(MessageSendException e){
                                throw new ConnectException(e);
                            }
                        }
                        result.add(connection);
                        break;
                    }
                }
            }
        }
        if(result.size() == 0){
            throw new ConnectException("ClientConnection not found. subject=" + subject + ", keys=" + concatStringArray(keys));
        }
        return result;
    }
    
    private String concatStringArray(String[] array){
        final StringBuffer buf = new StringBuffer();
        if(array == null){
            buf.append((String)null);
        }else{
            buf.append('[');
            for(int i = 0; i < array.length; i++){
                buf.append(array[i]);
                if(i != array.length - 1){
                    buf.append(", ");
                }
            }
            buf.append(']');
        }
        return buf.toString();
    }
    
    public void addSubject(String subject) throws MessageSendException{
        List connections = null;
        try{
            connections = connect(subject, null);
        }catch(ConnectException e){
            throw new MessageSendException(e);
        }
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
            connection.addSubject(subject);
        }
    }
    
    public void addSubject(String subject, String[] keys) throws MessageSendException{
        List connections = null;
        try{
            connections = connect(subject, keys);
        }catch(ConnectException e){
            throw new MessageSendException(e);
        }
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
            connection.addSubject(subject, keys);
        }
    }
    
    public void removeSubject(String subject) throws MessageSendException{
        List connections = null;
        try{
            connections = connect(subject, null);
        }catch(ConnectException e){
            throw new MessageSendException(e);
        }
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
            connection.removeSubject(subject);
        }
    }
    
    public void removeSubject(String subject, String[] keys) throws MessageSendException{
        List connections = null;
        try{
            connections = connect(subject, keys);
        }catch(ConnectException e){
            throw new MessageSendException(e);
        }
        for(int i = 0, imax = connections.size(); i < imax; i++){
            ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
            connection.removeSubject(subject, keys);
        }
    }
    
    public void startReceive() throws MessageSendException{
        startReceive(-1);
    }
    
    public void startReceive(long from) throws MessageSendException{
        List connections = new ArrayList(connectionMap.values());
        for(int i = 0, imax = connections.size(); i < imax; i++){
            List connectionList = (List)connections.get(i);
            for(int j = 0, jmax = connectionList.size(); j < jmax; j++){
                ClientConnection cc = (ClientConnection)connectionList.get(j);
                if(cc.isConnected()){
                    cc.startReceive(from);
                }
            }
        }
        isStartReceive = true;
    }
    
    public boolean isStartReceive(){
        return isStartReceive;
    }
    
    public void stopReceive() throws MessageSendException{
        if(!isStartReceive){
            return;
        }
        List connections = new ArrayList(connectionMap.values());
        for(int i = 0, imax = connections.size(); i < imax; i++){
            List connectionList = (List)connections.get(i);
            for(int j = 0, jmax = connectionList.size(); j < jmax; j++){
                ((ClientConnection)connectionList.get(j)).stopReceive();
            }
        }
        isStartReceive = false;
    }
    
    public Set getSubjects(){
        final Set result = new HashSet();
        List connections = new ArrayList(connectionMap.values());
        for(int i = 0, imax = connections.size(); i < imax; i++){
            List connectionList = (List)connections.get(i);
            for(int j = 0, jmax = connectionList.size(); j < jmax; j++){
                result.addAll(((ClientConnection)connectionList.get(j)).getSubjects());
            }
        }
        return result;
    }
    
    public Set getKeys(String subject){
        final Set result = new HashSet();
        List connections = new ArrayList(connectionMap.values());
        for(int i = 0, imax = connections.size(); i < imax; i++){
            List connectionList = (List)connections.get(i);
            for(int j = 0, jmax = connectionList.size(); j < jmax; j++){
                result.addAll(((ClientConnection)connectionList.get(j)).getKeys(subject));
            }
        }
        return result;
    }
    
    public void setMessageListener(MessageListener listener){
        Iterator itr = connectionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
                connection.setMessageListener(listener);
            }
        }
    }
    
    public boolean isConnected(){
        return id != null;
    }
    
    public boolean isServerClosed(){
        if(connectionMap.size() == 0){
            return false;
        }
        Iterator itr = connectionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
                if(!connection.isServerClosed()){
                    return false;
                }
            }
        }
        return true;
    }
    
    public Object getId(){
        if(connectionMap == null || connectionMap.size() == 0){
            return id;
        }
        List result = new ArrayList();
        Iterator itr = connectionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
                if(connection.getId() != null){
                    result.add(connection.getId());
                }
            }
        }
        return result.size() == 0 ? id : result;
    }
    
    public void close(){
        Iterator itr = connectionMap.values().iterator();
        while(itr.hasNext()){
            List connections = (List)itr.next();
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ClientConnectionImpl connection = (ClientConnectionImpl)connections.get(i);
                try{
                    connection.close();
                }catch(RuntimeException e){
                }
            }
        }
    }
    
    public String toString(){
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(", connectionMap=").append(connectionMap);
        buf.append('}');
        return buf.toString();
    }
    
    private static class ClientConnectionImpl implements ClientConnection, Serializable{
        private static final long serialVersionUID = 6390935385333885179L;
        
        private String subject;
        private Pattern keyPattern;
        private ClientConnection connection;
        private transient String serviceManagerName;
        
        public ClientConnectionImpl(){}
        
        public ClientConnectionImpl(String subject, Pattern keyPattern, ClientConnection connection){
            this.subject = subject;
            this.keyPattern = keyPattern;
            this.connection = connection;
        }
        
        public boolean isMatch(String subject, String key){
            if((this.subject == null && subject != null)
                || (this.subject != null && subject == null)
                || (this.subject != null && !this.subject.equals(subject))
                || (keyPattern != null  && key != null && !keyPattern.matcher(key).matches())
            ){
                return false;
            }
            return true;
        }
        
        public void setServiceManagerName(String name){
            serviceManagerName = name;
        }
        
        public void connect() throws ConnectException{
            connection.setServiceManagerName(serviceManagerName);
            connection.connect();
        }
        
        public void connect(Object id) throws ConnectException{
            connection.setServiceManagerName(serviceManagerName);
            connection.connect(id);
        }
        
        public void addSubject(String subject) throws MessageSendException{
            if(isMatch(subject, null)){
                connection.addSubject(subject);
            }
        }
        
        public void addSubject(String subject, String[] keys) throws MessageSendException{
            if(keys == null){
                if(isMatch(subject, null)){
                    connection.addSubject(subject, keys);
                }
            }else{
                Set keySet = new HashSet();
                for(int i = 0; i < keys.length; i++){
                    if(isMatch(subject, keys[i])){
                        keySet.add(keys[i]);
                    }
                }
                if(keySet.size() != 0){
                    connection.addSubject(subject, (String[])keySet.toArray(new String[keySet.size()]));
                }
            }
        }
        
        public void removeSubject(String subject) throws MessageSendException{
            if(isMatch(subject, null)){
                connection.removeSubject(subject);
            }
        }
        
        public void removeSubject(String subject, String[] keys) throws MessageSendException{
            if(keys == null){
                if(isMatch(subject, null)){
                    connection.removeSubject(subject, keys);
                }
            }else{
                Set keySet = new HashSet();
                for(int i = 0; i < keys.length; i++){
                    if(isMatch(subject, keys[i])){
                        keySet.add(keys[i]);
                    }
                }
                if(keySet.size() != 0){
                    connection.removeSubject(subject, (String[])keySet.toArray(new String[keySet.size()]));
                }
            }
        }
        
        public void startReceive() throws MessageSendException{
            startReceive(-1);
        }
        
        public void startReceive(long from) throws MessageSendException{
            connection.startReceive(from);
        }
        
        public boolean isStartReceive(){
            return connection.isStartReceive();
        }
        
        public void stopReceive() throws MessageSendException{
            connection.stopReceive();
        }
        
        public Set getSubjects(){
            return connection.getSubjects();
        }
        
        public Set getKeys(String subject){
            return connection.getKeys(subject);
        }
        
        public void setMessageListener(MessageListener listener){
            connection.setMessageListener(listener);
        }
        
        public boolean isConnected(){
            return connection.isConnected();
        }
        
        public boolean isServerClosed(){
            return connection.isServerClosed();
        }
        
        public Object getId(){
            return connection.getId();
        }
        
        public void close(){
            connection.close();
        }
        
        public String toString(){
            final StringBuffer buf = new StringBuffer(super.toString());
            buf.append('{');
            buf.append("subject=").append(subject);
            buf.append(", key=").append(keyPattern == null ? null : keyPattern.pattern());
            buf.append(", connection=").append(connection);
            buf.append('}');
            return buf.toString();
        }
    }
}