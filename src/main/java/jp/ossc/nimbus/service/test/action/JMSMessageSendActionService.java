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

import java.io.*;
import java.util.*;
import javax.jms.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.test.*;
import jp.ossc.nimbus.service.jms.*;
import jp.ossc.nimbus.service.jndi.JndiFinder;
import jp.ossc.nimbus.service.interpreter.*;

/**
 * javax.jms.MessageProducerで、javax.jms.Messageを送信するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class JMSMessageSendActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, JMSMessageSendActionServiceMBean{
    
    protected double expectedCost = Double.NaN;
    protected ServiceName jmsMessageProducerFactoryServiceName;
    protected JMSMessageProducerFactory jmsMessageProducerFactory;
    protected ServiceName destinationFinderServiceName;
    protected JndiFinder destinationFinder;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    
    public void setJMSMessageProducerFactoryServiceName(ServiceName name){
        jmsMessageProducerFactoryServiceName = name;
    }
    public ServiceName getJMSMessageProducerFactoryServiceName(){
        return jmsMessageProducerFactoryServiceName;
    }
    
    public void setDestinationFinderServiceName(ServiceName name){
        destinationFinderServiceName = name;
    }
    public ServiceName getDestinationFinderServiceName(){
        return destinationFinderServiceName;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setExpectedCost(double cost){
        expectedCost = cost;
    }
    public double getExpectedCost(){
        return expectedCost;
    }
    
    public void setJMSMessageProducerFactory(JMSMessageProducerFactory factory){
        jmsMessageProducerFactory = factory;
    }
    
    public void setDestinationFinder(JndiFinder destinationFinder){
        this.destinationFinder = destinationFinder;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void startService() throws Exception{
        if(jmsMessageProducerFactoryServiceName != null){
            jmsMessageProducerFactory = (JMSMessageProducerFactory)ServiceManagerFactory.getServiceObject(jmsMessageProducerFactoryServiceName);
        }
        
        if(jmsMessageProducerFactory == null){
            throw new IllegalArgumentException("JMSMessageProducerFactory is null.");
        }
        
        if(destinationFinderServiceName != null){
            destinationFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(destinationFinderServiceName);
        }
        
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
    }
    
    /**
     * リソースの内容を読み込んで、{@link ServerConnection}に{@link Message}を送信する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * destinationName
     * messageType
     * message
     * </pre>
     * destinationNameは、Messageの宛先であるDestinationのJNDI名を設定する。但し、{@link JMSMessageProducerFactory}に宛先が設定されている場合は、空行を指定する。<br>
     * messageTypeは、Messageの種別を指定するもので、"bytes"（javax.jms.BytesMessage）、"object"（javax.jms.ObjectMessage）、"stream"（javax.jms.StreamMessage）、"text"（javax.jms.TextMessage）のいずれか。<br>
     * messageは、messageTypeの値によって、設定方法が異なる。<br>
     * <ul>
     * <li>messageTypeが"bytes"または"stream"の場合<br>バイナリファイルのファイルパスを指定する。</li>
     * <li>messageTypeが"object"の場合<br>設定するオブジェクトのIDまたは、オブジェクトを生成するスクリプトを指定する。オブジェクトのIDは、同一テストケース中に、このTestActionより前に、オブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、オブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}が渡される。</li>
     * <li>messageTypeが"text"の場合<br>設定する文字列を指すオブジェクトのIDまたは、文字列そのものを指定する。</li>
     * </ul>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 送信したメッセージ
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、{@link ServerConnection}に{@link Message}を送信する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * destinationName
     * messageType
     * message
     * </pre>
     * destinationNameは、Messageの宛先であるDestinationのJNDI名を設定する。但し、{@link JMSMessageProducerFactory}に宛先が設定されている場合は、空行を指定する。<br>
     * messageTypeは、Messageの種別を指定するもので、"bytes"（javax.jms.BytesMessage）、"object"（javax.jms.ObjectMessage）、"stream"（javax.jms.StreamMessage）、"text"（javax.jms.TextMessage）のいずれか。<br>
     * messageは、messageTypeの値によって、設定方法が異なる。<br>
     * <ul>
     * <li>messageTypeが"bytes"または"stream"の場合<br>バイナリファイルのファイルパスを指定する。</li>
     * <li>messageTypeが"object"の場合<br>設定するオブジェクトのIDまたは、オブジェクトを生成するスクリプトを指定する。オブジェクトのIDは、同一テストケース中に、このTestActionより前に、オブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、オブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}が渡される。</li>
     * <li>messageTypeが"text"の場合<br>設定する文字列を指すオブジェクトのIDまたは、文字列そのものを指定する。</li>
     * </ul>
     * messageが指定されていない場合は、preResultを使用する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 更新クエリに対する引数オブジェクト
     * @param resource リソース
     * @return 送信したメッセージ
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        Message message = null;
        BufferedReader br = new BufferedReader(resource);
        try{
            Destination destination = null;
            String destinationName = br.readLine();
            if(destinationName == null || destinationName.length() == 0){
                destination = jmsMessageProducerFactory.getDestination();
                if(destination == null){
                    throw new Exception("Unexpected EOF on destinationName");
                }
            }else{
                if(destinationFinder == null){
                    throw new Exception("DestinationFinder is null.");
                }
                destination = (Destination)destinationFinder.lookup(destinationName);
            }
            
            Session session = jmsMessageProducerFactory.getSession();
            if(session == null){
                JMSSessionFactory sessionFactory = jmsMessageProducerFactory.getSessionFactory();
                session = sessionFactory.getSession();
            }
            MessageProducer messageProducer = jmsMessageProducerFactory.createProducer(session, destination);
            
            String messageType = br.readLine();
            if(messageType == null || messageType.length() == 0){
                throw new Exception("Unexpected EOF on messageType");
            }
            
            if("bytes".equals(messageType)){
                message = session.createBytesMessage();
            }else if("object".equals(messageType)){
                message = session.createObjectMessage();
            }else if("stream".equals(messageType)){
                message = session.createStreamMessage();
            }else if("text".equals(messageType)){
                message = session.createTextMessage();
            }else{
                throw new Exception("Unknown messageType : " + messageType);
            }
            if("bytes".equals(messageType) || "stream".equals(messageType)){
                String filePath = br.readLine();
                byte[] byteArray = null;
                if(filePath == null || filePath.length() == 0){
                    if(preResult == null){
                        throw new Exception("Unexpected EOF on message");
                    }
                    if(!(preResult instanceof byte[])){
                        throw new Exception("Illegal preResult type : " + preResult.getClass().getName());
                    }
                    byteArray = (byte[])preResult;
                }else{
                    File file = new File(filePath);
                    if(!file.exists()){
                        file = new File(context.getCurrentDirectory(), file.getPath());
                        if(!file.exists()){
                            throw new Exception("File not found. filePath=" + filePath);
                        }
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    try{
                        while((length = fis.read(bytes, 0, bytes.length)) > 0){
                            baos.write(bytes, 0, length);
                        }
                    }finally{
                        fis.close();
                    }
                    byteArray = baos.toByteArray();
                    baos.close();
                    baos = null;
                }
                if(message instanceof BytesMessage){
                    ((BytesMessage)message).writeBytes(byteArray);
                }else{
                    ((StreamMessage)message).writeBytes(byteArray);
                }
            }else{
                final String objectId = br.readLine();
                Object actionResult = null;
                if(objectId != null && objectId.length() != 0){
                    if(objectId.indexOf(",") == -1){
                        actionResult = context.getTestActionResult(objectId);
                    }else{
                        String[] ids = objectId.split(",");
                        if(ids.length != 2){
                            throw new Exception("Illegal objectId format. id=" + objectId);
                        }
                        actionResult = context.getTestActionResult(ids[0], ids[1]);
                    }
                }else{
                    if(preResult == null){
                        throw new Exception("Unexpected EOF on message");
                    }
                    actionResult = preResult;
                }
                if("object".equals(messageType)){
                    if(actionResult == null){
                        String objectScript = null;
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        pw.println(objectId);
                        try{
                            String line = null;
                            while((line = br.readLine()) != null){
                                pw.println(line);
                            }
                            pw.flush();
                            objectScript = sw.toString();
                        }finally{
                            sw.close();
                            pw.close();
                        }
                        if(interpreter == null){
                            throw new UnsupportedOperationException("Interpreter is null.");
                        }
                        final Map params = new HashMap();
                        params.put("context", context);
                        params.put("preResult", preResult);
                        actionResult = interpreter.evaluate(objectScript, params);
                    }
                    ((ObjectMessage)message).setObject((Serializable)actionResult);
                }else{
                    String text = objectId;
                    if(actionResult != null){
                        text = actionResult.toString();
                    }
                    ((TextMessage)message).setText(text);
                }
            }
            messageProducer.send(message);
        }finally{
            br.close();
            br = null;
        }
        return message;
    }
}