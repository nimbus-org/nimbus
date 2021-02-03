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
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.TopicSession;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.codemaster.CodeMasterNotifyBean;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.jms.JMSSessionFactory;
import jp.ossc.nimbus.service.jndi.JndiFinder;
import jp.ossc.nimbus.service.publish.ServerConnectionFactory;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}に更新通知を送信するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class CodeMasterNotifyActionService extends ServiceBase implements TestAction, TestActionEstimation, CodeMasterNotifyActionServiceMBean{
    
    private static final long serialVersionUID = 8118616880730155539L;
    
    protected ServiceName jndiFinderServiceName;
    protected JndiFinder jndiFinder;
    protected ServiceName jmsTopicSessionFactoryServiceName;
    protected JMSSessionFactory jmsTopicSessionFactory;
    protected String topicName;
    
    protected ServiceName serverConnectionFactoryServiceName;
    protected ServerConnectionFactory serverConnectionFactory;
    protected String subject;
    protected boolean isSendAsynch = true;
    protected long timeout = 10000l;
    
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    
    protected double expectedCost = Double.NaN;
    
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    public void setJMSTopicSessionFactoryServiceName(ServiceName name){
        jmsTopicSessionFactoryServiceName = name;
    }
    public ServiceName getJMSTopicSessionFactoryServiceName(){
        return jmsTopicSessionFactoryServiceName;
    }
    
    public void setTopicName(String name){
        topicName = name;
    }
    public String getTopicName(){
        return topicName;
    }
    
    public void setServerConnectionFactoryServiceName(ServiceName name){
        serverConnectionFactoryServiceName = name;
    }
    public ServiceName getServerConnectionFactoryServiceName(){
        return serverConnectionFactoryServiceName;
    }
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return subject;
    }
    
    public void setSendAsynch(boolean isAsynch){
        isSendAsynch = isAsynch;
    }
    public boolean isSendAsynch(){
        return isSendAsynch;
    }
    
    public void setTimeout(long timeout){
        this.timeout = timeout;
    }
    public long getTimeout(){
        return timeout;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setJndiFinder(JndiFinder jndiFinder){
        this.jndiFinder = jndiFinder;
    }
    
    public void setJMSTopicSessionFactory(JMSSessionFactory factory){
        jmsTopicSessionFactory = factory;
    }
    
    public void setServerConnectionFactory(ServerConnectionFactory factory){
        serverConnectionFactory = factory;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void startService() throws Exception{
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }
        if(jmsTopicSessionFactoryServiceName != null){
            jmsTopicSessionFactory = (JMSSessionFactory)ServiceManagerFactory.getServiceObject(jmsTopicSessionFactoryServiceName);
        }
        if((jndiFinder == null || jmsTopicSessionFactory == null || topicName == null)
            && ((serverConnectionFactory == null && serverConnectionFactoryServiceName == null) || subject == null)){
            throw new IllegalArgumentException("JndiFinder and JMSTopicSessionFactory and TopicName, or ServerConnectionFactory and Subject must be specified.");
        }
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
    }
    
    /**
     * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}に更新通知を送信する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * masterName
     * dateId
     * dataId
     * dataAndDataBindScript
     * </pre>
     * masterNameは、更新通知の対象となるマスタ名を指定する。<br>
     * dateIdは、{@link CodeMasterNotifyBean}に設定する更新日時のDateオブジェクトを設定するもので、同一テストケース中に、このTestActionより前に、更新日時のDateオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、更新日時のDateオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。空行を指定した場合は、更新日時のDateオブジェクトをTestActionの結果から取得しない。<br>
     * dataIdは、{@link CodeMasterNotifyBean}に設定する更新引数のオブジェクトを設定するもので、同一テストケース中に、このTestActionより前に、更新引数のオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、更新引数のオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。空行を指定した場合は、更新引数のオブジェクトをTestActionの結果から取得しない。<br>
     * dataAndDataBindScriptは、{@link CodeMasterNotifyBean}に設定する更新日時と更新引数を設定するスクリプトを指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"context"で{@link TestContext}、"notifyBean"で{@link CodeMasterNotifyBean}が渡される。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 送信に使用した{@link CodeMasterNotifyBean}
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        CodeMasterNotifyBean notifyBean = new CodeMasterNotifyBean();
        TopicSession session = null;
        if(jndiFinder != null && jmsTopicSessionFactory != null){
            notifyBean.setJndiFinder(jndiFinder);
            session = (TopicSession)jmsTopicSessionFactory.getSession();
            notifyBean.setResource(session);
            notifyBean.setTopicName(topicName);
        }else{
            ServerConnectionFactory scf = serverConnectionFactory;
            if(serverConnectionFactory == null && serverConnectionFactoryServiceName != null){
                scf = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(serverConnectionFactoryServiceName);
            }
            notifyBean.setServerConnection(scf.getServerConnection());
            notifyBean.setSubject(subject);
        }
        try{
            String masterName = br.readLine();
            if(masterName == null || masterName.length() == 0){
                throw new Exception("Unexpected EOF on masterName");
            }
            notifyBean.setMasterFlowKey(masterName);
            final String dateId = br.readLine();
            if(dateId != null && dateId.length() != 0){
                Object actionResult = null;
                if(dateId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(dateId);
                }else{
                    String[] ids = dateId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal dateId format. id=" + dateId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + dateId);
                }
                if(!(actionResult instanceof Date)){
                    throw new Exception("TestActionResult is not instance of Date. type=" + actionResult.getClass());
                }
                notifyBean.setDate((Date)actionResult);
            }
            final String dataId = br.readLine();
            if(dataId != null && dataId.length() != 0){
                Object actionResult = null;
                if(dataId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(dataId);
                }else{
                    String[] ids = dataId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal dataId format. id=" + dataId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + dataId);
                }
                notifyBean.setData(actionResult);
            }
            String dataAndDataBindScript = null;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            try{
                String line = null;
                while((line = br.readLine()) != null){
                    pw.println(line);
                }
                pw.flush();
                dataAndDataBindScript = sw.toString();
                if(dataAndDataBindScript.length() == 0){
                    dataAndDataBindScript = null;
                }
            }finally{
                sw.close();
                pw.close();
            }
            if(dataAndDataBindScript != null){
                if(interpreter == null){
                    throw new UnsupportedOperationException("Interpreter is null.");
                }
                final Map params = new HashMap();
                params.put("context", context);
                params.put("notifyBean", notifyBean);
                interpreter.evaluate(dataAndDataBindScript, params);
            }
            notifyBean.setTimeout(timeout);
            if(isSendAsynch){
                notifyBean.addMessageAndSend();
            }else{
                notifyBean.addMessageAndRequest();
            }
        }finally{
            br.close();
            br = null;
            if(session != null){
                session.close();
            }
        }
        return notifyBean;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}