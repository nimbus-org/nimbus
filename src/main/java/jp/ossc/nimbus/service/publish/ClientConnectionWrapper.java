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
 * {@link ClientConnection}インタフェースのアダプタ実装クラス。<p>
 *
 * @author M.Takata
 */
public abstract class ClientConnectionWrapper implements ClientConnection{
    
    protected ClientConnection clientConnection;
    
    public ClientConnectionWrapper(ClientConnection connection){
        clientConnection = connection;
    }
    
    public void setServiceManagerName(String name){
        if(clientConnection != null){
            clientConnection.setServiceManagerName(name);
        }
    }
    
    public void connect() throws ConnectException{
        if(clientConnection == null){
            throw new ConnectException("ClientConnection is null.");
        }
        clientConnection.connect();
    }
    
    public void connect(Object id) throws ConnectException{
        if(clientConnection == null){
            throw new ConnectException("ClientConnection is null.");
        }
        clientConnection.connect(id);
    }
    
    public void addSubject(String subject) throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.addSubject(subject);
    }
    
    public void addSubject(String subject, String[] keys) throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.addSubject(subject, keys);
    }
    
    public void removeSubject(String subject) throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.removeSubject(subject);
    }
    
    public void removeSubject(String subject, String[] keys) throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.removeSubject(subject, keys);
    }
    
    public void startReceive() throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.startReceive();
    }
    
    public void startReceive(long from) throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.startReceive(from);
    }
    
    public void stopReceive() throws MessageSendException{
        if(clientConnection == null){
            throw new MessageSendException("ClientConnection is null.");
        }
        clientConnection.stopReceive();
    }
    
    public boolean isStartReceive(){
        return clientConnection == null ? false : clientConnection.isStartReceive();
    }
    
    public Set getSubjects(){
        return clientConnection == null ? new HashSet() : clientConnection.getSubjects();
    }
    
    public Set getKeys(String subject){
        return clientConnection == null ? new HashSet() : clientConnection.getKeys(subject);
    }
    
    public void setMessageListener(MessageListener listener){
        if(clientConnection != null){
            clientConnection.setMessageListener(listener);
        }
    }
    
    public boolean isConnected(){
        return clientConnection == null ? false : clientConnection.isConnected();
    }
    
    public boolean isServerClosed(){
        return clientConnection == null ? false : clientConnection.isServerClosed();
    }
    
    public long getLastReceiveTime(){
        return clientConnection == null ? -1 : clientConnection.getLastReceiveTime();
    }
    
    public Object getId(){
        return clientConnection == null ? null : clientConnection.getId();
    }
    
    public void close(){
        if(clientConnection != null){
            clientConnection.close();
            clientConnection = null;
        }
    }
}
