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
package jp.ossc.nimbus.service.http.proxy;

import java.net.*;
import java.io.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLSocket;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.queue.*;

/**
 * プロキシサーバサービス。<p>
 * 特定のポートで接続を待ち受け、接続されるとスレッドを割り当て{@link Process}サービスに処理を委譲する。<br>
 * 
 * @author M.Takata
 */
public class ProxyServerService extends ServiceBase
 implements ProxyServerServiceMBean, DaemonRunnable, QueueHandler{
    
    private static final long serialVersionUID = 313673219796070087L;
    
    private static final String MSG_ID_00001 = "PSS__00001";
    
    private int port = DEFAULT_PORT;
    private ServiceName serverSocketFactoryServiceName;
    private ServerSocketFactory serverSocketFactory;
    private ServerSocket serverSocket;
    private Daemon serverDaemon;
    private int soTimeout = -1;
    private ServiceName processServiceName;
    private Process process;
    private DefaultQueueService queue;
    private QueueHandlerContainerService queueHandlerContainer;
    private int maxProcessCount = 1;
    private String bindAddress;
    private int backlog;
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setProcessServiceName(ServiceName name){
        processServiceName = name;
    }
    // ProxyServerServiceMBeanのJavaDoc
    public ServiceName getProcessServiceName(){
        return processServiceName;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setServerSocketFactoryServiceName(ServiceName name){
        serverSocketFactoryServiceName = name;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public ServiceName getServerSocketFactoryServiceName(){
        return serverSocketFactoryServiceName;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setBindAddress(String address){
        bindAddress = address;
    }
    // ProxyServerServiceMBeanのJavaDoc
    public String getBindAddress(){
        return bindAddress;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setBacklog(int backlog){
        this.backlog = backlog;
    }
    // ProxyServerServiceMBeanのJavaDoc
    public int getBacklog(){
        return backlog;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setPort(int port){
        this.port = port;
    }
    // ProxyServerServiceMBeanのJavaDoc
    public int getPort(){
        return port;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setSoTimeout(int millis){
        soTimeout = millis;
    }
    // ProxyServerServiceMBeanのJavaDoc
    public int getSoTimeout(){
        return soTimeout;
    }
    
    // ProxyServerServiceMBeanのJavaDoc
    public void setMaxProcessCount(int count){
        maxProcessCount = count;
    }
    // ProxyServerServiceMBeanのJavaDoc
    public int getMaxProcessCount(){
        return maxProcessCount;
    }
    
    public void setServerSocketFactory(ServerSocketFactory factory){
        serverSocketFactory = factory;
    }
    
    public ServerSocketFactory getServerSocketFactory(){
        return serverSocketFactory;
    }
    
    
    
    /**
     * {@link Process}を設定する。<p>
     *
     * @param process 処理を委譲するProcess
     */
    public void setProcess(Process process){
        this.process = process;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(processServiceName != null){
            process = (Process)ServiceManagerFactory
                .getServiceObject(processServiceName);
        }
        if(process == null){
            throw new IllegalArgumentException("Process must be specified.");
        }
        
        if(serverSocketFactoryServiceName != null){
            serverSocketFactory = (ServerSocketFactory)ServiceManagerFactory
                .getServiceObject(serverSocketFactoryServiceName);
        }
        
        queue = new DefaultQueueService();
        queue.create();
        queue.start();
        
        queueHandlerContainer = new QueueHandlerContainerService();
        queueHandlerContainer.create();
        queueHandlerContainer.setQueueService(queue);
        queueHandlerContainer.setQueueHandler(this);
        queueHandlerContainer.setQueueHandlerSize(maxProcessCount);
        queueHandlerContainer.setIgnoreNullElement(true);
        queueHandlerContainer.setWaitTimeout(1000l);
        queueHandlerContainer.start();
        
        InetAddress bindAdr = null;
        if(bindAddress != null){
             bindAdr = InetAddress.getByName(bindAddress);
        }
        
        if(serverSocketFactory != null){
            serverSocket = bindAdr == null ? serverSocketFactory.createServerSocket(port, backlog) : serverSocketFactory.createServerSocket(port, backlog, bindAdr);
        }else{
            serverSocket = bindAdr == null ? new ServerSocket(port, backlog)
                : new ServerSocket(port, backlog, bindAdr);
        }
        if(soTimeout >= 0){
            serverSocket.setSoTimeout(soTimeout);
        }
        serverDaemon = new Daemon(this);
        serverDaemon.setName(
            "Nimbus TestProxyServerDaemon " + getServiceNameObject()
        );
        serverDaemon.start();
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        try{
            serverSocket.close();
        }catch(IOException e){
        }
        if(serverDaemon != null){
            // デーモン停止
            serverDaemon.stop();
        }
        queueHandlerContainer.stop();
        queueHandlerContainer.destroy();
        
        queue.stop();
        queue.destroy();
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onStart(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onStop(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onSuspend(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onResume(){
        return true;
    }
    
    /**
     * サーバソケットへの接続要求を待ち受け、接続されたソケットを返す。<p>
     *
     * @param ctrl デーモン制御
     * @exception Exceptioon 接続要求の待ち受けに失敗した場合
     */
    public Object provide(DaemonControl ctrl) throws Exception{
        Socket socket = null;
        try{
            socket = serverSocket.accept();
            if(socket instanceof SSLSocket){
                ((SSLSocket)socket).startHandshake();
            }
        }catch(SocketTimeoutException e){
            return null;
        }catch(SocketException e){
            if(getState() != Service.STOPPING){
                getLogger().write(MSG_ID_00001, e);
            }
            ctrl.setRunning(false);
        }
        return socket;
    }
    
    /**
     * 接続されたソケットをProcess待ち受けキューに投入する。<p>
     * 
     * @param paramObj 接続されたソケット
     * @param ctrl デーモン制御
     * @exception Exception 発生しない
     */
    public void consume(Object paramObj, DaemonControl ctrl) throws Exception{
        if(paramObj == null){
            return;
        }
        final Socket socket = (Socket)paramObj;
        queue.push(socket);
    }
    
    /**
     * 接続中の全てのソケットを強制的に切断する。<p>
     */
    public void garbage(){
        if(queue != null){
            while(queue.size() > 0){
                final Socket socket = (Socket)queue.get(0);
                if(socket != null){
                    try{
                        socket.close();
                    }catch(IOException e){}
                }
            }
        }
    }
    
    /**
     * Process待ち受けキューから取り出したソケットから入出力ストリームを取り出し、Processに処理を委譲する。<p>
     *
     * @param obj 接続されたソケット
     * @exception Throwable Processの処理に失敗した場合
     */
    public void handleDequeuedObject(Object obj) throws Throwable{
        if(obj == null){
            return;
        }
        process.doProcess((Socket)obj);
    }
    public boolean handleError(Object obj, Throwable th) throws Throwable{
        throw th;
    }
    public void handleRetryOver(Object obj, Throwable th) throws Throwable{
        throw th;
    }
}