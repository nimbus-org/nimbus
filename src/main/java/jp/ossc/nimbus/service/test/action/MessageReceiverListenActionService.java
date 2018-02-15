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
package jp.ossc.nimbus.service.test.action;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.publish.MessageReceiver;
import jp.ossc.nimbus.service.publish.ClientConnection;
import jp.ossc.nimbus.service.publish.Message;

/**
 * {@link MessageReceiver}から{@link Message}を受信するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class MessageReceiverListenActionService extends ServiceBase implements TestAction, TestActionEstimation, MessageReceiverListenActionServiceMBean{
    
    private static final long serialVersionUID = -165396344130216716L;
    protected ServiceName messageReceiverServiceName;
    protected MessageReceiver messageReceiver;
    protected double expectedCost = 0d;
    
    public void setMessageReceiverServiceName(ServiceName name){
        messageReceiverServiceName = name;
    }
    public ServiceName getMessageReceiverServiceName(){
        return messageReceiverServiceName;
    }
    
    public void setMessageReceiver(MessageReceiver receiver){
        messageReceiver = receiver;
    }
    
    public void startService() throws Exception{
        if(messageReceiverServiceName != null){
            messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(messageReceiverServiceName);
        }
        if(messageReceiver == null){
            throw new IllegalArgumentException("MessageReceiver is null.");
        }
    }
    
    /**
     * リソースの内容を読み込んで、{@link ClientConnection}に{@link MessageListener}を登録して、受信登録を行う。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * subject,keys
     * </pre>
     * subjectは、{@link ClientConnection}に受信登録するサブジェクトを指定する。keysは、{@link ClientConnection}に受信登録するキーを指定する。複数のキーを指定する場合は、カンマ区切りで指定する。サブジェクトを複数設定する場合は、改行して指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 登録した{@link MessageListener}
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        final MessageListener messageListener = new MessageListener();
        try{
            String subjectAndKey = br.readLine();
            if(subjectAndKey == null || subjectAndKey.length() == 0){
                throw new Exception("Unexpected EOF on subject and key");
            }
            do{
                String[] subjectAndKeyArray = CSVReader.toArray(
                    subjectAndKey,
                    ',',
                    '\\',
                    null,
                    null,
                    true,
                    false,
                    true,
                    true
                );
                if(subjectAndKeyArray == null || subjectAndKeyArray.length == 0){
                    throw new Exception("Illegal subject and key format. subjectAndKey=" + subjectAndKey);
                }
                if(subjectAndKeyArray.length == 1){
                    messageReceiver.addSubject(messageListener, subjectAndKeyArray[0]);
                }else{
                    String subject = subjectAndKeyArray[0];
                    String[] keys = new String[subjectAndKeyArray.length - 1];
                    System.arraycopy(subjectAndKeyArray, 1, keys, 0, keys.length);
                    messageReceiver.addSubject(messageListener, subject, keys);
                }
            }while((subjectAndKey = br.readLine()) != null && subjectAndKey.length() != 0);
        }finally{
            br.close();
            br = null;
        }
        return messageListener;
    }
    
    public class MessageListener implements jp.ossc.nimbus.service.publish.MessageListener{
        protected List receiveMessageList = new ArrayList();
        protected SynchronizeMonitor waitMonitor = new WaitSynchronizeMonitor();
        protected int waitCount = 1;
        
        public void onMessage(Message message){
            synchronized(receiveMessageList){
                receiveMessageList.add(message);
                if(waitMonitor.isWait() && waitCount <= receiveMessageList.size()){
                    waitMonitor.notifyAllMonitor();
                }
            }
        }
        
        public boolean waitMessage(long timeout) throws InterruptedException{
            return waitMessage(1, timeout);
        }
        
        public boolean waitMessage(int count, long timeout) throws InterruptedException{
            synchronized(receiveMessageList){
                if(count <= receiveMessageList.size()){
                    return true;
                }
                waitCount = count;
                waitMonitor.initMonitor();
            }
            return waitMonitor.waitMonitor(timeout);
        }
        
        public List getReceiveMessageList(){
            List result = new ArrayList();
            synchronized(receiveMessageList){
                for(int i = 0; i < receiveMessageList.size(); i++){
                    result.add(receiveMessageList.get(i));
                }
            }
            return result;
        }
        
        public List getReceiveMessageObjectList() throws Exception{
            List result = new ArrayList();
            synchronized(receiveMessageList){
                for(int i = 0; i < receiveMessageList.size(); i++){
                    result.add(((Message)receiveMessageList.get(i)).getObject());
                }
            }
            return result;
        }
        
        public void close(){
            if(messageReceiver != null){
                try{
                    messageReceiver.removeMessageListener(MessageListener.this);
                }catch(Exception e){}
            }
        }
        
        protected void finalize() throws Throwable{
            close();
        }
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
