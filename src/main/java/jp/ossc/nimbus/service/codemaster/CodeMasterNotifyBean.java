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
// パッケージ
package jp.ossc.nimbus.service.codemaster;
//インポート
import java.util.*;
import java.io.*;
import javax.jms.*;
import javax.naming.NamingException;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.publish.ServerConnection;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.publish.MessageCreateException;

/**
 * コードマスター管理にマスター入れ替えを指示するBean
 * 
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class CodeMasterNotifyBean extends HashMap{
    
    private static final long serialVersionUID = 3508475057737920813L;
    
    /** TopicをルックアップするFinder */
    private JndiFinder mTopicFinder;
    /** Topicリソース */
    private TopicSession mRes;
    /** フローキー*/
    private String mFlowKey;
    /** 更新日付 */
    private Date mDate;
    /** 更新日付 */
    private Object mData;
    /** Topic 名 */
    private String mTopicName;
    
    private String subject;
    private ServerConnection connection;
    private Set flowNameSet = new LinkedHashSet();
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    
    public void setServerConnection(ServerConnection connection){
        this.connection = connection;
    }
    
    /**
     * 送信先のトピック名を設定する。<p>
     * 
     * @param name トピック名
     */
    public void setTopicName(String name){
        this.mTopicName = name;
    }
    
    /**
     * トピックセッションを設定する。<p>
     * 
     * @param rc トピックセッション
     */
    public void setResource(TopicSession rc){
        mRes = rc;
    }
    
    /**
     * 更新するマスタ名を設定する。<p>
     *
     * @param masterKey マスタ名
     */
    public void setMasterFlowKey(String masterKey){
        mFlowKey = masterKey;
    }
    
    /**
     * 更新するマスタが有効になる開始日時を設定する。<p>
     * 設定しない場合は、トピック通知が到達した日時が、有効開始日時となる。<br>
     *
     * @param effectiveDate 有効開始日時
     */
    public void setDate(Date effectiveDate){
        mDate = effectiveDate;
    }
    
    /**
     * マスタ更新処理への入力情報を設定する。<p>
     *
     * @param data 入力情報
     */
    public void setData(Object data){
        this.mData = data;
    }
    
    /**
     * 送信先トピックを探す{@link JndiFinder}サービスを設定する。<p>
     * 
     * @param finder JndiFinderサービス
     */
    public void setJndiFinder(JndiFinder finder){
        this.mTopicFinder = finder;
    }
    
    /**
     * 送信メッセージを作成する。<p>
     */
    public void addMessage(){
        if(mFlowKey == null){
            return ;
        }
        flowNameSet.add(mFlowKey);
        this.put(mFlowKey, mData) ;
        this.put(
            mFlowKey + CodeMasterService.UPDATE_TIME_KEY,
            mDate == null ? new Date() : mDate
        ) ;
        mDate = null ;
        mFlowKey = null ;
    }
    
    /**
     * メッセージをトピックに送信する。<p>
     * 
     * @exception JMSException 送信に失敗した場合
     * @exception NamingException トピックが見つからない場合
     * @exception MessageCreateException メッセージの生成に失敗した場合
     * @exception MessageSendException 送信に失敗した場合
     * @exception MessageException メッセージの生成に失敗した場合
     */
    public void send() throws JMSException, NamingException, MessageCreateException, MessageSendException, MessageException{
        if(size() == 0){
            return ;
        }
        if(connection == null){
            TopicSession session = mRes ;
            MapMessage msg = session.createMapMessage();
            Iterator flowNames = flowNameSet.iterator();
            while(flowNames.hasNext()){
                String flowName = (String)flowNames.next();
                
                String dateKey = flowName + CodeMasterService.UPDATE_TIME_KEY;
                setObject(msg, dateKey, get(dateKey));
                
                setObject(msg, flowName, get(flowName));
            }
            Topic tp = null ;
            if(this.mTopicName== null){
                tp = (Topic)this.mTopicFinder.lookup() ;
            }else{
                tp = (Topic)this.mTopicFinder.lookup(mTopicName) ;
            }
            TopicPublisher tpub = session.createPublisher(tp) ;
            msg.setJMSDeliveryMode(DeliveryMode.PERSISTENT) ;
            tpub.publish(msg) ;
        }else{
            Iterator flowNames = flowNameSet.iterator();
            while(flowNames.hasNext()){
                String flowName = (String)flowNames.next();
                jp.ossc.nimbus.service.publish.Message msg = connection.createMessage(
                    subject,
                    flowName
                );
                Map map = new HashMap();
                map.put(flowName, get(flowName));
                String dateKey = flowName + CodeMasterService.UPDATE_TIME_KEY;
                map.put(dateKey, get(dateKey));
                msg.setObject(map);
                connection.send(msg);
            }
        }
        flowNameSet.clear();
        this.clear();
    }
    
    private void setObject(MapMessage msg, String key, Object obj) throws JMSException{
        if(obj == null){
            msg.setString(key,null);
        }else if(obj instanceof Date){
            Date dt = (Date)obj ;
            msg.setLong(key,dt.getTime()) ;
        }else if(obj instanceof Boolean
            || obj instanceof Byte
            || obj instanceof byte[]
            || obj instanceof Character
            || obj instanceof Double
            || obj instanceof Float
            || obj instanceof Integer
            || obj instanceof Long
            || obj instanceof Short
            || obj instanceof String
        ){
            msg.setObject(key, obj);
        }else{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try{
                oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                oos.flush();
            }catch(IOException e){
                JMSException ex = new JMSException("Not serializable : " + obj);
                ex.setLinkedException(e);
                throw ex;
            }finally{
                if(oos != null){
                    try{
                        oos.close();
                    }catch(IOException e){
                    }
                }
            }
            msg.setBytes(key, baos.toByteArray());
        }
    }
    
    /**
     * メッセージ作成とトピックへの送信の両方を行う。<p>
     * 
     * @exception JMSException 送信に失敗した場合
     * @exception NamingException トピックが見つからない場合
     * @exception MessageCreateException メッセージの生成に失敗した場合
     * @exception MessageSendException 送信に失敗した場合
     * @exception MessageException メッセージの生成に失敗した場合
     */
    public void addMessageAndSend() throws JMSException, NamingException, MessageCreateException, MessageSendException, MessageException{
        addMessage();
        send();
    }
}
