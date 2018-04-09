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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * {@link JMSMessageReceiverListenActionService.MessageListener}が受信した{@link java.jms.Message Message}を取得するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 *
 * @author Y.Nakashima
 * @see JMSMessageReceiverListenActionService
 */
public class JMSMessageReceiverGetActionService extends ServiceBase implements TestAction, TestActionEstimation, JMSMessageReceiverGetActionServiceMBean{

    private static final long serialVersionUID = 6779163909892607718L;
    protected double expectedCost = Double.NaN;

    /* デフォルトを外から設定できる */
    protected String defaultGetType = "message";
    protected long defaultTimeout = 10000;
    protected int defaultCount = 1;
    protected boolean defaultIsClose = true;

    public void setDefaultGetType(String defaultGetType) {
        this.defaultGetType = defaultGetType;
    }

    public void setDefaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
    }

    public void setDefaultIsClose(boolean defaultClose) {
        this.defaultIsClose = defaultClose;
    }

    /**
     * リソースの内容を読み込んで、{@link JMSMessageReceiverListenActionService.MessageListener}が受信した{@link ava.jms.Message Message}を取得する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * listenerId
     * getType
     * timeout
     * count
     * close
     * </pre>
     * listenerIdは、メッセージを受信している{@link JMSMessageReceiverListenActionService}のアクションIDを指定するもので、同一テストケース中に、このTestActionより前に、{@link JMSMessageReceiverListenActionService.MessageListener}を戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、{@link JMSMessageReceiverListenActionService.MessageListener}を戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。<br>
     * getTypeは、"message","text","object"または"map"。デフォルト指定可能。デフォルトは"message"。<br>
     * getType毎の変換は以下の通り<br>
     *   "message"はjavax.jms.Messageを取得し、そのまま返す。<br>
     *   "text"はjavax.jms.TextMessageを取得し、Stringに変換して返す<br>
     *   "object"はjavax.jms.ObjectMessageを取得し、Objectに変換して返す<br>
     *   "map"はjavax.jms.MapMessageを取得し、Mapに変換して返す<br>
     * timeoutは、メッセージの受信待ちタイムアウト[ms]を指定する。デフォルト指定可能。デフォルトは10,000[ms]<br>
     * countは、取得するメッセージ数を指定する。デフォルト指定可能。デフォルトは1。<br>
     * closeは、{@link JMSMessageReceiverListenActionService.MessageListener#close()}を呼び出すかどうかを、trueまたはfalseで指定する。デフォルト指定可能。デフォルトはtrue。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return javax.jms.MessageをgetType毎に変換したリスト
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        JMSMessageReceiverListenActionService.MessageListener listener = null;
        String getType = null;
        long timeout = 0;
        int count = 1;
        boolean isClose = true;

        // resourceのパース
        try{
            final String listenerId = br.readLine();
            if(listenerId == null || listenerId.length() == 0){
                throw new Exception("Unexpected EOF on listenerId");
            }
            Object actionResult = null;
            if(listenerId.indexOf(",") == -1){
                actionResult = context.getTestActionResult(listenerId);
            }else{
                String[] ids = listenerId.split(",");
                if(ids.length != 2){
                    throw new Exception("Illegal listenerId format. id=" + listenerId);
                }
                actionResult = context.getTestActionResult(ids[0], ids[1]);
            }
            if(actionResult == null){
                throw new Exception("TestActionResult not found. id=" + listenerId);
            }
            if(!(actionResult instanceof JMSMessageReceiverListenActionService.MessageListener)){
                throw new Exception("TestActionResult is not JMSMessageReceiverListenActionService.MessageListener. type=" + actionResult.getClass());
            }
            listener = (JMSMessageReceiverListenActionService.MessageListener)actionResult;
            getType = br.readLine();
            if(getType == null || getType.length() == 0){
                getType = defaultGetType;
            }

            // streamとbyteはサポートしない
            if(!"message".equals(getType) && !"text".equals(getType) && !"map".equals(getType) && !"object".equals(getType)){
                throw new UnsupportedOperationException("Illegal getType : " + getType);
            }

            final String timeoutStr = br.readLine();
            if(timeoutStr == null || timeoutStr.length() == 0){
                timeout = defaultTimeout;
            } else {
                try{
                    timeout = Long.parseLong(timeoutStr);
                }catch(NumberFormatException e){
                    throw new Exception("Illegal timeout format. timeout=" + timeoutStr);
                }
            }

            final String countStr = br.readLine();
            if(countStr == null || countStr.length() == 0){
                count = defaultCount;
            } else {
                try{
                    count = Integer.parseInt(countStr);
                }catch(NumberFormatException e){
                    throw new Exception("Illegal timeout format. count=" + countStr);
                }
            }

            final String close = br.readLine();
            if(close == null || close.length() == 0){
                isClose = defaultIsClose;
            } else {
                isClose = Boolean.valueOf(close).booleanValue();
            }
        }finally{
            br.close();
            br = null;
        }

        // Messageを取得し、Listに追加して返す
        // 以下のようにgetType毎に返し方を変える
        //   text: TextMessage ⇒ List<String>
        //   object: ObjectMessage ⇒ List<Object>
        //   map: MapMessage ⇒ List<Map>

        List result = null;
        List msgList = null;

        try{
            if(listener.waitMessage(count, timeout)){
                msgList = listener.getReceiveMessageList();
            }

            if(msgList != null && msgList.size() != 0) {
                result = new ArrayList();
                for(int i=0; i<msgList.size(); i++) {
                    Message message = (Message) msgList.get(i);
                    if("text".equals(getType)) {
                        result.add(parseTextMessageToString(message));
                    } else if("map".equals(getType)) {
                        result.add(parseMapMessageToMap(message));
                    } else if ("object".equals(getType)) {
                        result.add(parseObjectMessageToObject(message));
                    } else {
                        result.add(message);
                    }
                }
            }

        }finally{
            if(isClose){
                listener.close();
            }
        }
        return result;
    }

    private static String parseTextMessageToString(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        return textMessage.getText();
    }

    private static Object parseObjectMessageToObject(Message message) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        return objectMessage.getObject();
    }

    private static Map parseMapMessageToMap(Message message) throws JMSException {
        Map ret = new LinkedHashMap();

        MapMessage mapMessage = (MapMessage) message;
        Enumeration keys =  mapMessage.getMapNames();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            Object value =  mapMessage.getObject(key);
            ret.put(key, value);
        }

        return ret;
    }

    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }

    public double getExpectedCost() {
        return expectedCost;
    }
}
