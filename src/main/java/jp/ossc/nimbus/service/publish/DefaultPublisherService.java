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
 *      this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
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

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

import javax.jms.*;
import javax.jms.Message;
import javax.jms.MessageListener;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.jms.*;
import jp.ossc.nimbus.service.performance.ResourceUsage;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;


/**
 * パブリッシャーサービス
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class DefaultPublisherService extends ServiceBase
 implements DefaultPublisherServiceMBean, Publisher, ResourceUsage {

    private static final long serialVersionUID = -5493103525911436403L;

    protected static final String MSG_ID_00001 = "DP___00001";
    protected static final String MSG_ID_00002 = "DP___00002";
    protected static final String MSG_ID_00003 = "DP___00003";
    protected static final String MSG_ID_00004 = "DP___00004";
    protected static final String MSG_ID_00005 = "DP___00005";
    protected static final String MSG_ID_00006 = "DP___00006";
    protected static final String MSG_ID_00007 = "DP___00007";
    protected static final String MSG_ID_00008 = "DP___00008";
    protected static final String MSG_ID_00009 = "DP___00009";
    protected static final String MSG_ID_00010 = "DP___00010";
    protected static final String MSG_ID_00011 = "DP___00011";
    protected static final String MSG_ID_00012 = "DP___00012";

    protected String serverBindAddress;

    /**
     * Port番号
     */
    protected int port = 0;

    /**
     * コンテナー数
     */
    protected int containerNum = 0;

    /**
     * セレクター
     */
    protected Selector selector;

    /**
     * デーモンクラスインスタンス
     */
    protected Daemon socketReader;

    /**
     * Servantのマップ。
     */
    protected Map servants;

    /**
     * PublichContainerのリスト。
     */
    protected List containerList;

    /**
     * PublishContainerFactoryサービス名
     */
    protected ServiceName publishContainerFactoryServiceName;

    /**
     * PublishContainerFactoryサービス
     */
    protected PublishContainerFactory publishContainerFactory;

    /**
     * プロトコルサービス名
     */
    protected ServiceName protocolServiceName;

    /** プロトコルサービス */
    protected Protocol protocol;

    protected ServerSocketChannel serverSocketChannel;

    protected boolean isServerSocketChannelBlocking;

    protected boolean isSocketChannelBlocking;

    protected boolean isKeepAlive = true;

    protected long servantGarbageInterval = -1;

    protected Daemon servantGarbager;

    protected ServiceName[] jmsMessageConsumerFactoryServiceNames;

    protected JMSMessageConsumerFactory[] jmsMessageConsumerFactory;

    protected ServiceName[] queueServiceNames;

    protected jp.ossc.nimbus.service.queue.Queue[] queues;

    protected JMSMessageListener[] listeners;

    protected Daemon[] messageHandlers;

    protected Set consumers;

    protected ServiceName[] messageFilterServiceNames;

    protected List messageFilters;

    protected int serverSocketSoTimeout = -1;
    protected int serverSocketReceiveBufferSize = -1;

    protected int socketSoTimeout = -1;
    protected int socketReceiveBufferSize = -1;
    protected int socketSendBufferSize = -1;
    protected Boolean socketTcpNoDelay;
    protected Integer socketSoLinger;

    protected ServiceName analyzeQueueServiceName;
    protected jp.ossc.nimbus.service.queue.Queue analyzeQueue;
    protected int analyzeThreadSize = 1;
    protected Daemon[] analyzeDaemon;

    protected ServiceName messageReceiverServiceName;
    protected MessageReceiver messageReceiver;

    // DefaultPublisherServiceMBean のJavaDoc
    public void setProtocolServiceName(ServiceName name){
        protocolServiceName = name;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName getProtocolServiceName(){
        return protocolServiceName;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName getPublishContainerFactoryServiceName(){
        return publishContainerFactoryServiceName;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setPublishContainerFactoryServiceName(ServiceName name) {
        publishContainerFactoryServiceName = name ;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setJMSMessageConsumerFactoryServiceNames(ServiceName[] names){
        jmsMessageConsumerFactoryServiceNames = names;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName[] getJMSMessageConsumerFactoryServiceNames(){
        return jmsMessageConsumerFactoryServiceNames;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setMessageFilterServiceNames(ServiceName[] names){
        messageFilterServiceNames = names;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName[] getMessageFilterServiceNames(){
        return messageFilterServiceNames;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setServerBindAddress(String address){
        serverBindAddress = address;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public String getServerBindAddress(){
        return serverBindAddress;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setPort(int port){
        this.port = port;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getPort(){
        return port;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setContainerNum(int num){
        containerNum = num;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getContainerNum(){
        return containerNum;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public boolean isServerSocketChannelBlocking(){
        return isServerSocketChannelBlocking;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setServerSocketChannelBlocking(boolean isBlocking){
        isServerSocketChannelBlocking = isBlocking;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public boolean isSocketChannelBlocking(){
        return isSocketChannelBlocking;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setSocketChannelBlocking(boolean isBlocking){
        isSocketChannelBlocking = isBlocking;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setServerSocketSoTimeout(int timeout){
        serverSocketSoTimeout = timeout;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getServerSocketSoTimeout(){
        return serverSocketSoTimeout;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setServerSocketReceiveBufferSize(int size){
        serverSocketReceiveBufferSize = size;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getServerSocketReceiveBufferSize(){
        return serverSocketReceiveBufferSize;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setSocketSoTimeout(int timeout){
        socketSoTimeout = timeout;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getSocketSoTimeout(){
        return socketSoTimeout;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setSocketReceiveBufferSize(int size){
        socketReceiveBufferSize = size;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getSocketReceiveBufferSize(){
        return socketReceiveBufferSize;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setSocketSendBufferSize(int size){
        socketSendBufferSize = size;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getSocketSendBufferSize(){
        return socketSendBufferSize;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setSocketTcpNoDelay(boolean noDelay){
        socketTcpNoDelay = noDelay ? Boolean.TRUE : Boolean.FALSE;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public boolean isSocketTcpNoDelay(){
        return socketTcpNoDelay == null ? false : socketTcpNoDelay.booleanValue();
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setSocketSoLinger(int time){
        socketSoLinger = new Integer(time);
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getSocketSoLinger(){
        return socketSoLinger == null ? -1 : socketSoLinger.intValue();
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public boolean isKeepAlive(){
        return isKeepAlive;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setKeepAlive(boolean isKeepAlive){
        this.isKeepAlive = isKeepAlive;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setServantGarbageInterval(long millis){
        servantGarbageInterval = millis;
    }
    // DefaultPublisherServiceMBean のJavaDoc
    public long getServantGarbageInterval(){
        return servantGarbageInterval;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setQueueServiceNames(ServiceName[] names){
        queueServiceNames = names;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName[] getQueueServiceNames(){
        return queueServiceNames;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setAnalyzeQueueServiceName(ServiceName name){
        analyzeQueueServiceName = name;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName getAnalyzeQueueServiceName(){
        return analyzeQueueServiceName;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setAnalyzeThreadSize(int size){
        analyzeThreadSize = size;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public int getAnalyzeThreadSize(){
        return analyzeThreadSize;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public void setMessageReceiverServiceName(ServiceName name){
        messageReceiverServiceName = name;
    }

    // DefaultPublisherServiceMBean のJavaDoc
    public ServiceName getMessageReceiverServiceName(){
        return messageReceiverServiceName;
    }

    public void setAnalyzeQueue(jp.ossc.nimbus.service.queue.Queue queue){
        analyzeQueue = queue;
    }

    public void setQueues(jp.ossc.nimbus.service.queue.Queue[] queues){
        this.queues = queues;
    }

    public void setPublishContainerFactory(
        PublishContainerFactory publishContainerFactory
    ) {
        this.publishContainerFactory = publishContainerFactory;
    }

    public void setMessageFilters(MessageFilter[] filters){
        if(filters == null || filters.length == 0){
            messageFilters = null;
        }else{
            List filterList = new ArrayList();
            for(int i = 0; i < filters.length; i++){
                filterList.add(filters[i]);
            }
            messageFilters = filterList;
        }
    }

    public List getMessageFilterList(){
        return messageFilters;
    }

    public void setMessageReceiver(MessageReceiver receiver){
        messageReceiver = receiver;
    }

    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        servants = Collections.synchronizedMap(new HashMap());
        consumers = new HashSet();
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(protocolServiceName != null) {
            protocol = (Protocol)ServiceManagerFactory.getServiceObject(protocolServiceName);
        }
        if(protocol==null) {
            throw new IllegalArgumentException("ProtocolServiceName or Protocol must be specified.");
        }

        if(publishContainerFactoryServiceName != null) {
            publishContainerFactory =
                (PublishContainerFactory)ServiceManagerFactory.getServiceObject(publishContainerFactoryServiceName);
        }
        if(publishContainerFactory == null) {
            throw new IllegalArgumentException(
                    "PublishContainerFactoryServiceName or PublishContainerFactory must be specified."
                    );
        }

        if(analyzeQueueServiceName != null){
            analyzeQueue = (jp.ossc.nimbus.service.queue.Queue)ServiceManagerFactory.getServiceObject(analyzeQueueServiceName);
        }
        if(analyzeQueue != null){
            analyzeQueue.accept();
            analyzeDaemon = new Daemon[analyzeThreadSize];
            for(int i = 0; i < analyzeThreadSize; i++){
                analyzeDaemon[i] = new Daemon(new Analyzer());
                analyzeDaemon[i].setName("Nimbus PublisherAnalyzeDaemon " + getServiceNameObject());
                analyzeDaemon[i].start();
            }
        }

        if(messageReceiverServiceName != null){
            messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(messageReceiverServiceName);
        }

        //コンテナ作成
        containerList = new ArrayList(containerNum);
        for(int i = 0; i < containerNum; i++){
            final PublishContainer publishContainer
                 = publishContainerFactory.createContainer();
            if(messageReceiver != null){
                publishContainer.setMessageReceiver(messageReceiver);
            }
            containerList.add(publishContainer);
        }

        if(servantGarbageInterval > 0){
            servantGarbager = new Daemon(new ServantGarbager());
            servantGarbager.setName("Nimbus PublisherServantGarbagerDaemon " + getServiceNameObject());
            servantGarbager.start();
        }

        // Chanelを作成する
        initSelector();
        socketReader = new Daemon(new SocketReader());
        socketReader.setName("Nimbus PublisherSocketReaderDaemon " + getServiceNameObject());
        socketReader.start();

        if(messageReceiver == null){
            if(messageFilterServiceNames != null && messageFilterServiceNames.length != 0){
                messageFilters = new ArrayList();
                for(int i = 0; i < messageFilterServiceNames.length; i++){
                    messageFilters.add(
                        (MessageFilter)ServiceManagerFactory.getServiceObject(
                            messageFilterServiceNames[i]
                        )
                    );
                }
            }

            if(jmsMessageConsumerFactoryServiceNames != null
                    && jmsMessageConsumerFactoryServiceNames.length > 0) {
                jmsMessageConsumerFactory =
                    new JMSMessageConsumerFactory[jmsMessageConsumerFactoryServiceNames.length];
                for(int i = 0; i < jmsMessageConsumerFactoryServiceNames.length; i++){
                    jmsMessageConsumerFactory[i] =
                        (JMSMessageConsumerFactory)ServiceManagerFactory.getServiceObject(
                            jmsMessageConsumerFactoryServiceNames[i]
                            );
                }
            }
            if(jmsMessageConsumerFactory == null ||
                    jmsMessageConsumerFactory.length <= 0) {
                throw new IllegalArgumentException(
                    "JmsMessageConsumerFactoryServiceNames or JmsMessageConsumerFactory must be specified."
                );
            }

            if(queueServiceNames != null) {
                if(queueServiceNames.length == jmsMessageConsumerFactory.length){
                    queues = new jp.ossc.nimbus.service.queue.Queue[queueServiceNames.length];
                    for(int i = 0; i < queueServiceNames.length; i++){
                        queues[i] = (jp.ossc.nimbus.service.queue.Queue)
                            ServiceManagerFactory.getServiceObject(queueServiceNames[i]);
                        queues[i].accept();
                    }
                }else {
                    throw new IllegalArgumentException(
                        "Length of QueueServiceNames and JmsMessageConsumerFactory must equal."
                    );
                }
            }
            if(queues == null) {
                throw new IllegalArgumentException(
                    "QueueServiceNames or Queue must be specified."
                );
            }else if(queues.length != jmsMessageConsumerFactory.length) {
                throw new IllegalArgumentException(
                    "Length of Queue and JmsMessageConsumerFactory must equal."
                );
            }

            messageHandlers = new Daemon[queues.length];

            listeners = new JMSMessageListener[
                jmsMessageConsumerFactoryServiceNames.length
            ];

            for(int i = 0; i < jmsMessageConsumerFactory.length; i++){
                final MessageConsumer consumer
                     = jmsMessageConsumerFactory[i].createConsumer();
                if(queueServiceNames != null){
                    if(queues[i] instanceof QueueHandlerContainer){
                        ((QueueHandlerContainer)queues[i]).setQueueHandler(
                            new JMSMessageQueueHandler()
                        );
                        ((QueueHandlerContainer)queues[i]).start();
                    }else{
                        messageHandlers[i] = new Daemon(new MessageHandler(queues[i]));
                        messageHandlers[i].setName("Nimbus PublisherMessageHandlerDaemon " + getServiceNameObject());
                        messageHandlers[i].start();
                    }
                }
                listeners[i] = new JMSMessageListener(queues[i]);
                consumer.setMessageListener(listeners[i]);
                consumers.add(consumer);
                final Connection con
                     = jmsMessageConsumerFactory[i].getSessionFactory().getConnection();
                con.start();
            }
        }
    }

    protected void initSelector() throws IOException{
        selector = SelectorProvider.provider().openSelector();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(isServerSocketChannelBlocking);
        InetAddress address = null;
        if(serverBindAddress == null){
            address = InetAddress.getLocalHost();
        }else{
            byte[] ip = new byte[4];
            String tmp = serverBindAddress;
            for(int i = 0; i < ip.length; i++){
                if(i != ip.length - 1){
                    int index = tmp.indexOf('.');
                    if(index == -1 || index == tmp.length() - 1){
                        throw new IllegalArgumentException(
                            "Bad serverBindAddress : " + serverBindAddress
                        );
                    }
                    ip[i] = (byte)Integer.parseInt(tmp.substring(0, index));
                    tmp = tmp.substring(index + 1);
                }else{
                    ip[i] = (byte)Integer.parseInt(tmp);
                }
            }
            address = InetAddress.getByAddress(ip);
        }
        final InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        serverSocketChannel.socket().bind(socketAddress);
        if(serverSocketSoTimeout != -1){
            serverSocketChannel.socket().setSoTimeout(serverSocketSoTimeout);
        }
        if(serverSocketReceiveBufferSize != -1){
            serverSocketChannel.socket().setReceiveBufferSize(serverSocketReceiveBufferSize);
        }

        // Selectorへの登録
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    protected void closeSelector(){

        if(serverSocketChannel != null){
            try{
                serverSocketChannel.close();
            }catch(IOException e){
            }
        }
        if(serverSocketChannel != null){
            try{
                serverSocketChannel.close();
            }catch(IOException e){
            }
        }
        if(selector != null){
            try{
                selector.close();
            }catch(IOException e){
            }
        }
    }

    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        final Iterator cons = consumers.iterator();
        while(cons.hasNext()){
            final MessageConsumer consumer
                 = (MessageConsumer)cons.next();
            try{
                consumer.close();
            }catch(JMSException e){
            }
        }
        consumers.clear();
        listeners = null;

        socketReader.stop();
        socketReader = null;

        if(servantGarbager != null){
            servantGarbager.stop();
            servantGarbager = null;
        }

        if(analyzeQueue != null){
            analyzeQueue.release();
            for(int i = 0; i < analyzeDaemon.length; i++){
                analyzeDaemon[i].stop();
            }
            analyzeDaemon = null;
        }

        closeSelector();

        if(messageHandlers != null && messageHandlers.length != 0){
            for(int i = 0; i < messageHandlers.length; i++){
                if(messageHandlers[i] != null){
                    messageHandlers[i].stop();
                }
            }
            messageHandlers = null;
        }

        if(queues != null && queues.length != 0){
            for(int i = 0; i < queues.length; i++){
                if(queues[i] instanceof QueueHandlerContainer){
                    ((QueueHandlerContainer)queues[i]).stop();
                }
                queues[i].release();
            }
        }

        for(int i = 0, imax = containerList.size(); i < imax; i++){
            final PublishContainer container
                 = (PublishContainer)containerList.get(i);
            container.stop();
        }
        containerList.clear();

        servants.clear();

        messageFilters = null;
    }

    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService(){
        protocol = null;
        selector = null;
    }

    protected void handleMessage(Message msg){
        Object obj = null;
        if(msg instanceof ObjectMessage){
            ObjectMessage objMsg = (ObjectMessage)msg;
            try{
                obj = objMsg.getObject();
            }catch(JMSException e){
                getLogger().write(MSG_ID_00007, e);
            }
        }else{
            getLogger().write(MSG_ID_00008, msg);
        }
        if(obj == null){
            return;
        }
        if(messageFilters != null){
            try{
                for(int i = 0; i < messageFilters.size(); i++){
                    MessageFilter messageFilter = (MessageFilter)messageFilters.get(i);
                    obj = messageFilter.filter(obj);
                    if(obj == null){
                        return;
                    }
                }
            }catch(Throwable th){
                getLogger().write(MSG_ID_00011, obj, th);
            }
        }
        try{
            for(int i = 0, imax = containerList.size(); i < imax; i++){
                final PublishContainer container
                     = (PublishContainer)containerList.get(i);
                container.handleMessage(obj);
            }
        }catch(Throwable th){
            getLogger().write(MSG_ID_00012, obj, th);
        }
    }

    // Publisher のJavaDoc
    public synchronized boolean entryServant(Servant svt){
        String key = svt.getID() ;
        int maxVacantNum = 0;
        PublishContainer maxVacantContainer = null;
        for(int i = 0, max = containerList.size(); i < max; i++){
            final PublishContainer container
                 = (PublishContainer)containerList.get(i);
            final int vacantNum = container.getVacantServantNum();
            if(vacantNum > maxVacantNum){
                maxVacantNum = vacantNum;
                maxVacantContainer = container;
            }
        }
        if(maxVacantContainer != null){
            svt.setProtocol(protocol);
            if(maxVacantContainer.entryServant(svt)){
                servants.put(key, svt);
                return true;
            }else{
                return entryServant(svt);
            }
        }else{
            return false;
        }
    }

    public synchronized boolean ejectServant(String key){
        return ejectServant(key, false);
    }

    public synchronized boolean ejectServant(String key, boolean isForced){
        final Servant servant = (Servant)servants.get(key);
        if(servant != null){
            final PublishContainer container = servant.getContainer();
            if(container == null){
                return true;
            }
            if(container.ejectServant(servant, isForced)){
                servants.remove(key);
            }else{
                return false;
            }
        }
        return true;
    }

    // Publisher のJavaDoc
    public Servant findServant(String key){
        return (Servant)servants.get(key);
    }

    public int getServantNum(){
        return servants != null ? servants.size() : 0;
    }

    public long getPublishCount(){
        if(containerList == null){
            return 0;
        }
        long count = 0;
        for(int i = 0, imax = containerList.size(); i < imax; i++){
            final PublishContainer container
                 = (PublishContainer)containerList.get(i);
            count += container.getPublishCount();
        }
        return count;
    }

    public long getReceiveCount(){
        long receiveCount = 0;
        if(listeners != null && listeners.length != 0){
            for(int i = 0; i < listeners.length; i++){
                receiveCount += listeners[i].receiveCount;
            }
        }
        return receiveCount;
    }

    protected class JMSMessageQueueHandler implements QueueHandler{
        // QueueHandler のJavaDoc
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            handleMessage((Message)obj);
        }

        // QueueHandler のJavaDoc
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return true;
        }

        // QueueHandler のJavaDoc
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
        }
    }

    protected class JMSMessageListener implements MessageListener{

        public long receiveCount;

        protected jp.ossc.nimbus.service.queue.Queue queue;

        public JMSMessageListener(jp.ossc.nimbus.service.queue.Queue queue){
            this.queue = queue;
        }

        public void onMessage(Message msg){
            if(getState() != STARTING
                && getState() != STARTED){
                return;
            }
            if(queue == null){
                receiveCount++;
                handleMessage(msg);
            }else{
                receiveCount++;
                queue.push(msg);
            }
        }
    }

    protected class MessageHandler
     implements Serializable, DaemonRunnable{

        private static final long serialVersionUID = 5865323006319211723L;

        protected jp.ossc.nimbus.service.queue.Queue queue;

        public MessageHandler(){
        }

        public MessageHandler(jp.ossc.nimbus.service.queue.Queue queue){
            this.queue = queue;
        }

        // Daemon のJavaDoc
        public boolean onStart(){
            return true;
        }

        // Daemon のJavaDoc
        public boolean onStop(){
            return true;
        }

        // Daemon のJavaDoc
        public boolean onSuspend(){
            return true;
        }

        // Daemon のJavaDoc
        public boolean onResume(){
            return true;
        }

        // Daemon のJavaDoc
        public Object provide(DaemonControl ctrl) throws Throwable{
            if(queue == null){
                return null;
            }
            return queue.get(1000);
        }

        // Daemon のJavaDoc
        public void consume(Object paramObj, DaemonControl ctrl){
            if(paramObj == null){
                return;
            }
            handleMessage((Message)paramObj);
        }

        // Daemon のJavaDoc
        public void garbage(){
            if(queue != null){
                while(queue.size() > 0){
                    consume(queue.get(0), null);
                }
            }
        }
    }

    protected class SocketReader implements Serializable, DaemonRunnable{

        private static final long serialVersionUID = 8199251823294812508L;

        // Daemon のJavaDoc
        public boolean onStart(){
            return true;
        }

        // Daemon のJavaDoc
        public boolean onStop(){
            return true;
        }

        // Daemon のJavaDoc
        public boolean onSuspend(){
            return true;
        }

        // Daemon のJavaDoc
        public boolean onResume(){
            return true;
        }

        // Daemon のJavaDoc
        public Object provide(DaemonControl ctrl) throws Throwable{
            try{
                int count = selector.select(1000);
                if(count > 0){
                    return selector.selectedKeys();
                }else{
                    return null;
                }
            }catch(Throwable e){
                getLogger().write(MSG_ID_00009, e);
                closeSelector();
                try{
                    Thread.sleep(5000);
                }catch(InterruptedException e2){
                }
                try{
                    initSelector();
                }catch(IOException e2){
                    closeSelector();
                    getLogger().write(MSG_ID_00010, e2);
                }
                return null;
            }
        }

        // Daemon のJavaDoc
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null){
                return;
            }
            SelectionKey key = null;
            final Set selected = (Set)paramObj;
            try{
                // セレクトされた SelectionKey オブジェクトをまとめて取得する
                final Iterator keyIterator = selected.iterator();
                while(keyIterator.hasNext()){
                    try{
                        key = (SelectionKey)keyIterator.next();
                        // セレクトされた SelectionKey の状態に応じて処理を決める
                        if(key.isAcceptable()){
                            // accept の場合
                            final ServerSocketChannel serverSocketChannel
                                 = (ServerSocketChannel)key.channel();
                            try{
                                accept(serverSocketChannel);
                            }catch(IOException e){
                                getLogger().write(MSG_ID_00001, e);
                            }
                        }else if(key.isReadable()){
                            // データが送られてきたとき
                            final SocketChannel socketChannel
                                 = (SocketChannel)key.channel();
                            if(analyzeQueue == null){
                                try{
                                    read(key, socketChannel);
                                }catch(AnalyzeProcessException e){
                                    getLogger().write(MSG_ID_00005, e);
                                }catch(IOException e){
                                    getLogger().write(MSG_ID_00003, e);
                                }catch(MessageSendException e){
                                    getLogger().write(MSG_ID_00004, e);
                                }catch(ProtocolMismatchException e){
                                    getLogger().write(MSG_ID_00002, e);
                                }
                            }else{
                                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                                analyzeQueue.push(
                                    new Object[]{key, socketChannel}
                                );
                            }
                        }else if(!key.isValid()){
                            key.cancel();
                        }
                    }catch(CancelledKeyException e){
                    }finally{
                        keyIterator.remove();
                    }
                }
            }catch(Throwable e){
                getLogger().write(MSG_ID_00006, e);
            }
        }

        // Daemon のJavaDoc
        public void garbage(){
        }

        private void accept(ServerSocketChannel serverSocketChannel)
         throws IOException{
            // SocketChannel取得
            final SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(isSocketChannelBlocking);
            socketChannel.socket().setKeepAlive(isKeepAlive);
            if(socketSoTimeout != -1){
                socketChannel.socket().setSoTimeout(socketSoTimeout);
            }
            if(socketReceiveBufferSize != -1){
                socketChannel.socket().setReceiveBufferSize(socketReceiveBufferSize);
            }
            if(socketSendBufferSize != -1){
                socketChannel.socket().setSendBufferSize(socketSendBufferSize);
            }
            if(socketTcpNoDelay != null){
                socketChannel.socket().setTcpNoDelay(socketTcpNoDelay.booleanValue());
            }
            if(socketSoLinger != null){
                socketChannel.socket().setSoLinger(
                    socketSoLinger.intValue() > 0,
                    socketSoLinger.intValue()
                );
            }

            socketChannel.configureBlocking(isServerSocketChannelBlocking);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        private void read(SelectionKey key, SocketChannel socketChanel)
         throws IOException, ProtocolMismatchException, MessageSendException,
                AnalyzeProcessException{

            try{
                protocol.analyze(key, socketChanel, DefaultPublisherService.this);
            }catch(MessageSendException e){
                try{
                    socketChanel.close();
                }catch(IOException e2){
                }
                key.cancel();
                throw e;
            }catch(AnalyzeProcessException e){
                try{
                    socketChanel.close();
                }catch(IOException e2){
                }
                key.cancel();
                throw e;
            }catch(IOException e){
                try{
                    socketChanel.close();
                }catch(IOException e2){
                }
                key.cancel();
                throw e;
            }catch(RuntimeException e){
                try{
                    socketChanel.close();
                }catch(IOException e2){
                }
                key.cancel();
                throw e;
            }catch(Error e){
                try{
                    socketChanel.close();
                }catch(IOException e2){
                }
                key.cancel();
                throw e;
            }
        }
    }

    protected class ServantGarbager implements DaemonRunnable{

        public boolean onStart(){
            return true;
        }

        public boolean onStop(){
            return true;
        }

        public boolean onSuspend(){
            return true;
        }

        public boolean onResume(){
            return true;
        }

        public Object provide(DaemonControl ctrl) throws Throwable{
            //一定時間スリープ
            ctrl.sleep(servantGarbageInterval, true);
            return null;
        }

        public void consume(Object paramObj, DaemonControl ctrl)
         throws Throwable{
            for(int i = 0, imax = containerList.size(); i < imax; i++){
                final PublishContainer container
                     = (PublishContainer)containerList.get(i);
                final Set garbage = container.garbage();
                if(garbage != null){
                    final Iterator itr = garbage.iterator();
                    while(itr.hasNext()){
                        servants.remove(((Servant)itr.next()).getID());
                    }
                }
            }
        }

        public void garbage(){
        }
    }

    protected class Analyzer implements DaemonRunnable{

        public boolean onStart(){
            return true;
        }

        public boolean onStop(){
            return true;
        }

        public boolean onSuspend(){
            return true;
        }

        public boolean onResume(){
            return true;
        }

        public Object provide(DaemonControl ctrl) throws Throwable{
            if(analyzeQueue == null){
                return null;
            }
            return analyzeQueue.get(1000);
        }

        public void consume(Object paramObj, DaemonControl ctrl)
         throws Throwable{
            if(paramObj == null){
                return;
            }
            final Object[] params = (Object[])paramObj;
            final SelectionKey key = (SelectionKey)params[0];
            if(!key.isValid()){
                key.cancel();
                return;
            }
            final SocketChannel socketChannel = (SocketChannel)params[1];
            try{
                protocol.analyze(
                    key,
                    socketChannel,
                    DefaultPublisherService.this
                );
                try{
                    key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                    selector.wakeup();
                }catch(CancelledKeyException e){
                }
            }catch(MessageSendException e){
                getLogger().write(MSG_ID_00004, e);
                try{
                    socketChannel.close();
                }catch(IOException e2){
                }
                key.cancel();
            }catch(AnalyzeProcessException e){
                getLogger().write(MSG_ID_00005, e);
                try{
                    socketChannel.close();
                }catch(IOException e2){
                }
                key.cancel();
            }catch(IOException e){
                getLogger().write(MSG_ID_00003, e);
                try{
                    socketChannel.close();
                }catch(IOException e2){
                }
                key.cancel();
            }catch(ProtocolMismatchException e){
                getLogger().write(MSG_ID_00002, e);
                try{
                    socketChannel.close();
                }catch(IOException e2){
                }
                key.cancel();
            }catch(Throwable e){
                getLogger().write(MSG_ID_00006, e);
                try{
                    socketChannel.close();
                }catch(IOException e2){
                }
                key.cancel();
            }
        }

        public void garbage(){
        }
    }

    public Comparable getUsage() {
        long max = 0;
        long connected = 0;
        if(containerList != null){
            for(int i = 0; i <containerList.size(); i++ ){
                final PublishContainer publishContainer = (PublishContainer)containerList.get(i);
                max += publishContainer.getMaxServantNum();
                connected += publishContainer.getServantNum();
            }
        }
        return new Double((double) connected / (double) max);
    }

    public long getServantsSendMessageParamCreateCountAverage(){
        if(servants == null || servants.isEmpty()){
            return 0;
        }
        long total = 0;
        for (Iterator servantItr = servants.values().iterator(); servantItr.hasNext();) {
            Servant svt = (Servant) servantItr.next();
            total += svt.getSendMessageParamCreateCount();
        }
        return total / servants.size();
    }
}
