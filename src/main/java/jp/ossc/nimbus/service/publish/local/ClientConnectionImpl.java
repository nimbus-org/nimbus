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
package jp.ossc.nimbus.service.publish.local;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceManager;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.service.publish.ClientConnection;
import jp.ossc.nimbus.service.publish.MessageListener;
import jp.ossc.nimbus.service.publish.ConnectException;
import jp.ossc.nimbus.service.publish.MessageSendException;

/**
 * ローカルプロトコル用の{@link ClientConnection}インタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class ClientConnectionImpl implements ClientConnection{
    
    private ServerConnectionImpl serverConnection;
    
    private String serviceManagerName;
    private ServiceName serverServiceName;
    private String clientNo;
    private String serverCloseMessageId;
    
    private Map subjects;
    private MessageListener messageListener;
    private boolean isConnected;
    private Object id;
    private ServiceName serviceName;
    private long receiveCount;
    private long onMessageProcessTime;
    private boolean isStartReceive;
    private boolean isServerClosed;
    private long lastReceiveTime = -1;
    
    public ClientConnectionImpl(
        ServerConnectionImpl con,
        ServiceName serverServiceName,
        String no
    ){
        serverConnection = con;
        this.serverServiceName = serverServiceName;
        clientNo = no;
    }
    
    public void setServerCloseMessageId(String id){
        serverCloseMessageId = id;
    }
    public String getServerCloseMessageId(){
        return serverCloseMessageId;
    }
    
    public void setServiceManagerName(String name){
        serviceManagerName = name;
    }
    
    public void connect() throws ConnectException{
        connect(null);
    }
    
    public synchronized void connect(Object id) throws ConnectException{
        if(isConnected){
            return;
        }
        isConnected = false;
        this.id = id == null ? clientNo : id;
        serverConnection.connect(this.id, this);
            if(serverServiceName != null){
                ServiceManager manager = ServiceManagerFactory.findManager(serviceManagerName == null ? serverServiceName.getServiceManagerName() : serviceManagerName);
            if(manager != null){
                final ClientConnectionService ccs = new ClientConnectionService();
                try{
                    String name = serverServiceName.getServiceName() + '$' + clientNo;
                    name = name.replaceAll(":", "\\$");
                    if(!manager.isRegisteredService(name) && manager.registerService(name, ccs)){
                        serviceName = ccs.getServiceNameObject();
                        manager.createService(ccs.getServiceName());
                        manager.startService(ccs.getServiceName());
                    }
                }catch(Exception e){
                    throw new ConnectException(e);
                }
            }
        }
        isConnected = true;
        isServerClosed = false;
    }
    
    public void addSubject(String subject) throws MessageSendException{
        addSubject(subject, null);
    }
    
    public void addSubject(String subject, String[] keys) throws MessageSendException{
        if(!isConnected){
            throw new MessageSendException("Not connected.");
        }
        if(subject == null){
            return;
        }
        serverConnection.addSubject(this.id, subject, keys);
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
    
    public void removeSubject(String subject) throws MessageSendException{
        removeSubject(subject, null);
    }
    
    public void removeSubject(String subject, String[] keys) throws MessageSendException{
        if(!isConnected){
            throw new MessageSendException("Not connected.");
        }
        if(subject == null){
            return;
        }
        serverConnection.removeSubject(this.id, subject, keys);
        if(subjects != null){
            Set keySet = (Set)subjects.get(subject);
            if(keySet != null){
                if(keys == null){
                    keySet.remove(null);
                }else{
                    for(int i = 0; i < keys.length; i++){
                        keySet.remove(keys[i]);
                    }
                }
                if(keySet.size() == 0){
                    subjects.remove(subject);
                }
            }
        }
    }
    
    public void startReceive() throws MessageSendException{
        startReceive(-1);
    }
    
    public void startReceive(long from) throws MessageSendException{
        startReceive(from, false);
    }
    
    private void startReceive(long from, boolean isRestart) throws MessageSendException{
        if(!isConnected){
            throw new MessageSendException("Not connected.");
        }
        if(!isRestart && isStartReceive){
            return;
        }
        serverConnection.startReceive(this.id, from);
    }
    
    public boolean isStartReceive(){
        return isStartReceive;
    }
    
    public void stopReceive() throws MessageSendException{
        if(!isConnected){
            throw new MessageSendException("Not connected.");
        }
        if(!isStartReceive){
            return;
        }
        serverConnection.stopReceive(this.id);
    }
    
    public Set getSubjects(){
        return subjects == null ? new HashSet() : subjects.keySet();
    }
    
    public Set getKeys(String subject){
        if(subjects == null){
            return new HashSet();
        }
        Set keySet = (Set)subjects.get(subject);
        return keySet == null ? new HashSet() : keySet;
    }
    
    public void setMessageListener(MessageListener listener){
        messageListener = listener;
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public boolean isServerClosed(){
        return isServerClosed;
    }
    
    public Object getId(){
        return id;
    }
    
    public long getLastReceiveTime(){
        return lastReceiveTime;
    }
    
    public synchronized void close(){
        if(serviceName != null){
            ServiceManagerFactory.unregisterService(
                serviceName.getServiceManagerName(),
                serviceName.getServiceName()
            );
            serviceName = null;
        }
        if(isConnected){
            serverConnection.close(this.id);
        }
        isConnected = false;
    }
    
    public void onMessage(MessageImpl message){
        if(message != null && message.isServerClose()){
            if(serverCloseMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    serverCloseMessageId,
                    this
                );
            }
            isServerClosed = true;
            close();
            return;
        }
        lastReceiveTime = message.getReceiveTime();
        if(messageListener == null){
            return;
        }
        
        receiveCount++;
        long sTime = System.currentTimeMillis();
        messageListener.onMessage(message);
        onMessageProcessTime += (System.currentTimeMillis() - sTime);
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("factory=").append(serverServiceName);
        buf.append(", id=").append(id);
        buf.append(", server=").append(serverConnection);
        buf.append(", subject=").append(subjects);
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * ローカル用の{@link ClientConnection}の管理サービス。<p>
     *
     * @author M.Takata
     */
    public class ClientConnectionService extends ServiceBase implements ClientConnectionServiceMBean{
        
        private static final long serialVersionUID = 6276595917950435379L;
        
        public Set getSubjects(){
            return ClientConnectionImpl.this.getSubjects();
        }
        
        public Set getKeys(String subject){
            return ClientConnectionImpl.this.getKeys(subject);
        }
        
        public long getReceiveCount(){
            return ClientConnectionImpl.this.receiveCount;
        }
        
        public void resetCount(){
            ClientConnectionImpl.this.receiveCount = 0;
            ClientConnectionImpl.this.onMessageProcessTime = 0;
        }
        
        public long getAverageOnMessageProcessTime(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.onMessageProcessTime / ClientConnectionImpl.this.receiveCount);
        }
        
        public void connect() throws ConnectException{
            ClientConnectionImpl.this.connect();
        }
        
        public void connect(Object id) throws ConnectException{
            ClientConnectionImpl.this.connect(id);
        }
        
        public void startReceive() throws MessageSendException{
            ClientConnectionImpl.this.startReceive();
        }
        
        public void startReceive(long from) throws MessageSendException{
            ClientConnectionImpl.this.startReceive(from);
        }
        
        public void stopReceive() throws MessageSendException{
            ClientConnectionImpl.this.stopReceive();
        }
        
        public boolean isStartReceive(){
            return ClientConnectionImpl.this.isStartReceive();
        }
        
        public void addSubject(String subject) throws MessageSendException{
            ClientConnectionImpl.this.addSubject(subject);
        }
        
        public void addSubject(String subject, String[] keys) throws MessageSendException{
            ClientConnectionImpl.this.addSubject(subject, keys);
        }
        
        public void removeSubject(String subject) throws MessageSendException{
            ClientConnectionImpl.this.removeSubject(subject);
        }
        
        public void removeSubject(String subject, String[] keys) throws MessageSendException{
            ClientConnectionImpl.this.removeSubject(subject, keys);
        }
        
        public boolean isConnected(){
            return ClientConnectionImpl.this.isConnected();
        }
        
        public boolean isServerClosed(){
            return ClientConnectionImpl.this.isServerClosed();
        }
        
        public void close(){
            ClientConnectionImpl.this.close();
        }
    }
    
    /**
     * ローカル用の{@link ClientConnection}の管理サービスのMBeanインタフェース。<p>
     *
     * @author M.Takata
     */
    public interface ClientConnectionServiceMBean extends ServiceBaseMBean{
        
        /**
         * 登録されているサブジェクトを取得する。<p>
         *
         * @return 登録されているサブジェクトの集合
         */
        public Set getSubjects();
        
        /**
         * 指定したサブジェクトに登録されているキーを取得する。<p>
         *
         * @return 登録されているキーの集合
         */
        public Set getKeys(String subject);
        
        /**
         * 受信件数を取得する。<p>
         *
         * @return 受信件数
         */
        public long getReceiveCount();
        
        /**
         * 平均メッセージ処理時間を取得する。<p>
         *
         * @return 平均メッセージ処理時間[ms]
         */
        public long getAverageOnMessageProcessTime();
        
        /**
         * カウントをリセットする。<p>
         */
        public void resetCount();
        
        /**
         * サーバと接続する。<p>
         *
         * @exception ConnectException サーバとの接続に失敗した場合
         */
        public void connect() throws ConnectException;
        
        /**
         * サーバと接続する。<p>
         *
         * @param id クライアントを識別するID
         * @exception ConnectException サーバとの接続に失敗した場合
         */
        public void connect(Object id) throws ConnectException;
        
        /**
         * 配信開始をサーバに要求する。<br>
         *
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void startReceive() throws MessageSendException;
        
        /**
         * 指定した過去の時間のデータから配信開始をサーバに要求する。<br>
         *
         * @param from 開始時間
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void startReceive(long from) throws MessageSendException;
        
        /**
         * 配信停止をサーバに要求する。<br>
         *
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void stopReceive() throws MessageSendException;
        
        /**
         * 配信開始しているかどうかを判定する。<br>
         *
         * @return 配信開始している場合true
         */
        public boolean isStartReceive();
        
        /**
         * 配信して欲しいサブジェクトをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void addSubject(String subject) throws MessageSendException;
        
        /**
         * 配信して欲しいサブジェクトとキーをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @param keys キー
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void addSubject(String subject, String[] keys) throws MessageSendException;
        
        /**
         * 配信を解除して欲しいサブジェクトをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void removeSubject(String subject) throws MessageSendException;
        
        /**
         * 配信を解除して欲しいサブジェクトとキーをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @param keys キー
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void removeSubject(String subject, String[] keys) throws MessageSendException;
        
        /**
         * 接続しているかどうかを判定する。<p>
         *
         * @return 接続している場合true
         */
        public boolean isConnected();
        
        /**
         * サーバ側から切断要求を受けたかどうかを判定する。<p>
         *
         * @return サーバ側から切断要求を受けた場合true
         */
        public boolean isServerClosed();
        
        /**
         * サーバと切断する。<p>
         */
        public void close();
    }
}