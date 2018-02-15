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
package jp.ossc.nimbus.service.publish.tcp;

import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;
import jp.ossc.nimbus.service.publish.ClientConnectionFactory;
import jp.ossc.nimbus.service.publish.ServerConnectionFactory;
import jp.ossc.nimbus.service.publish.ClientConnection;
import jp.ossc.nimbus.service.publish.ServerConnection;
import jp.ossc.nimbus.service.publish.ServerConnectionListener;
import jp.ossc.nimbus.service.publish.RemoteClientConnectionFactory;
import jp.ossc.nimbus.service.publish.ConnectionCreateException;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * TCPプロトコル用の{@link ClientConnectionFactory}及び{@link ServerConnectionFactory}インタフェース実装サービス。<p>
 * 
 * @author M.Takata
 */
public class ConnectionFactoryService extends ServiceBase implements ServerConnectionFactory, ClientConnectionFactory, ConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 4621521654243947901L;
    
    private String clientAddressPropertyName;
    private String clientPortPropertyName;
    private int clientReconnectCount;
    private long clientReconnectInterval;
    private long clientReconnectBufferTime;
    private String serverAddress;
    private int serverPort;
    private int serverBacklog;
    private boolean isNIO;
    private ServiceName nioSocketFactoryServiceName;
    private ServiceName serverSocketFactoryServiceName;
    private ServiceName socketFactoryServiceName;
    private ServiceName jndiRepositoryServiceName;
    private String jndiName = DEFAULT_JNDI_NAME;
    private int rmiPort;
    private ServiceName[] serverConnectionListenerServiceNames;
    private long sendBufferTime;
    private long sendBufferSize;
    private long sendBufferTimeoutInterval = 1000l;
    
    private int sendThreadSize = 1;
    private ServiceName sendQueueServiceName;
    private int asynchSendThreadSize;
    private ServiceName asynchSendQueueServiceName;
    private ServiceName asynchSendQueueFactoryServiceName;
    private ServiceName externalizerServiceName;
    private int maxSendRetryCount;
    private long sendMessageCacheTime = 5000;
    private boolean isAcknowledge;
    private String serverSendErrorMessageId = MSG_ID_SEND_ERROR;
    private String serverSendErrorRetryOverMessageId = MSG_ID_SEND_ERROR_RETRY_OVER;
    private String clientConnectMessageId = MSG_ID_CLIENT_CONNECT;
    private String clientClosedMessageId = MSG_ID_CLIENT_CLOSED;
    private String clientCloseMessageId = MSG_ID_CLIENT_CLOSE;
    private int serverMessageRecycleBufferSize;
    private String clientServerCloseMessageId = MSG_ID_SERVER_CLOSE;
    private String clientReceiveWarnMessageId = MSG_ID_RECEIVE_WARN;
    private String clientReceiveErrorMessageId = MSG_ID_RECEIVE_ERROR;
    private long clientResponseTimeout = 30000;
    private int clientMessageRecycleBufferSize;
   
    private ServerSocketFactory serverSocketFactory;
    private ServerConnectionImpl serverConnection;
    private SocketFactory socketFactory;
    private jp.ossc.nimbus.util.net.SocketFactory nioSocketFactory;
    private Repository jndiRepository;
    private Externalizer externalizer;
    private List serverConnectionListeners;
    private RemoteClientConnectionFactory remoteClientConnectionFactory;
    
    public void setClientAddressPropertyName(String name){
        clientAddressPropertyName = name;
    }
    public String getClientAddressPropertyName(){
        return clientAddressPropertyName;
    }
    
    public void setClientPortPropertyName(String name){
        clientPortPropertyName = name;
    }
    public String getClientPortPropertyName(){
        return clientPortPropertyName;
    }
    
    public void setClientReconnectCount(int count){
        clientReconnectCount = count;
    }
    public int getClientReconnectCount(){
        return clientReconnectCount;
    }
    
    public void setClientReconnectInterval(long interval){
        clientReconnectInterval = interval;
    }
    public long getClientReconnectInterval(){
        return clientReconnectInterval;
    }
    
    public void setClientReconnectBufferTime(long interval){
        clientReconnectBufferTime = interval;
    }
    public long getClientReconnectBufferTime(){
        return clientReconnectBufferTime;
    }
    
    public void setClientMessageRecycleBufferSize(int size){
        clientMessageRecycleBufferSize = size;
    }
    public int getClientMessageRecycleBufferSize(){
        return clientMessageRecycleBufferSize;
    }
    
    public void setServerAddress(String address){
        serverAddress = address;
    }
    public String getServerAddress(){
        return serverAddress;
    }
    
    public void setServerPort(int port){
        serverPort = port;
    }
    public int getServerPort(){
        return serverPort;
    }
    
    public boolean isNIO(){
        return isNIO;
    }
    public void setNIO(boolean isNIO){
        this.isNIO = isNIO;
    }
    
    public void setServerBacklog(int backlog){
        serverBacklog = backlog;
    }
    public int getServerBacklog(){
        return serverBacklog;
    }
    
    public void setServerSocketFactoryServiceName(ServiceName name){
        serverSocketFactoryServiceName = name;
    }
    public ServiceName getServerSocketFactoryServiceName(){
        return serverSocketFactoryServiceName;
    }
    
    public void setSocketFactoryServiceName(ServiceName name){
        socketFactoryServiceName = name;
    }
    public ServiceName getSocketFactoryServiceName(){
        return socketFactoryServiceName;
    }
    
    public void setNIOSocketFactoryServiceName(ServiceName name){
        nioSocketFactoryServiceName = name;
    }
    public ServiceName getNIOSocketFactoryServiceName(){
        return nioSocketFactoryServiceName;
    }
    
    public void setJndiName(String name){
        jndiName = name;
    }
    public String getJndiName(){
        return jndiName;
    }
    
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    public void setRMIPort(int port){
        rmiPort = port;
    }
    public int getRMIPort(){
        return rmiPort;
    }
    
    public void setSendQueueServiceName(ServiceName name){
        sendQueueServiceName = name;
    }
    public ServiceName getSendQueueServiceName(){
        return sendQueueServiceName;
    }
    
    public void setSendThreadSize(int threadSize){
        sendThreadSize = threadSize;
    }
    public int getSendThreadSize(){
        return sendThreadSize;
    }
    
    public void setAsynchSendQueueServiceName(ServiceName name){
        asynchSendQueueServiceName = name;
    }
    public ServiceName getAsynchSendQueueServiceName(){
        return asynchSendQueueServiceName;
    }
    
    public void setAsynchSendQueueFactoryServiceName(ServiceName name){
        asynchSendQueueFactoryServiceName = name;
    }
    public ServiceName getAsynchSendQueueFactoryServiceName(){
        return asynchSendQueueFactoryServiceName;
    }
    
    public void setAsynchSendThreadSize(int threadSize){
        asynchSendThreadSize = threadSize;
    }
    public int getAsynchSendThreadSize(){
        return asynchSendThreadSize;
    }
    
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    public void setServerConnectionListenerServiceNames(ServiceName[] names){
        serverConnectionListenerServiceNames = names;
    }
    public ServiceName[] getServerConnectionListenerServiceNames(){
        return serverConnectionListenerServiceNames;
    }
    
    public void setMaxSendRetryCount(int count){
        maxSendRetryCount = count;
    }
    public int getMaxSendRetryCount(){
        return maxSendRetryCount;
    }
    
    public void setSendMessageCacheTime(long time){
        sendMessageCacheTime = time;
    }
    
    public long getSendMessageCacheTime(){
        return sendMessageCacheTime;
    }
    
    public void setSendBufferTime(long time){
        sendBufferTime = time;
    }
    
    public long getSendBufferTime(){
        return sendBufferTime;
    }
    
    public void setSendBufferSize(long bytes){
        sendBufferSize = bytes;
    }
    
    public long getSendBufferSize(){
        return sendBufferSize;
    }
    
    public void setSendBufferTimeoutInterval(long interval){
        sendBufferTimeoutInterval = interval;
    }
    
    public long getSendBufferTimeoutInterval(){
        return sendBufferTimeoutInterval;
    }
    
    public void setAcknowledge(boolean isAck){
        isAcknowledge = isAck;
    }
    public boolean isAcknowledge(){
        return isAcknowledge;
    }
    
    public void setClientResponseTimeout(long timeout){
        clientResponseTimeout = timeout;
    }
    public long getClientResponseTimeout(){
        return clientResponseTimeout;
    }
    
    public void setServerSendErrorMessageId(String id){
        serverSendErrorMessageId = id;
    }
    public String getServerSendErrorMessageId(){
        return serverSendErrorMessageId;
    }
    
    public void setServerSendErrorRetryOverMessageId(String id){
        serverSendErrorRetryOverMessageId = id;
    }
    public String getServerSendErrorRetryOverMessageId(){
        return serverSendErrorRetryOverMessageId;
    }
    
    public void setClientConnectMessageId(String id){
        clientConnectMessageId = id;
    }
    public String getClientConnectMessageId(){
        return clientConnectMessageId;
    }
    
    public void setClientClosedMessageId(String id){
        clientClosedMessageId = id;
    }
    public String getClientClosedMessageId(){
        return clientClosedMessageId;
    }
    
    public void setClientCloseMessageId(String id){
        clientCloseMessageId = id;
    }
    public String getClientCloseMessageId(){
        return clientCloseMessageId;
    }
    
    public void setServerMessageRecycleBufferSize(int size){
        serverMessageRecycleBufferSize = size;
    }
    public int getServerMessageRecycleBufferSize(){
        return serverMessageRecycleBufferSize;
    }
    
    public void setClientServerCloseMessageId(String id){
        clientServerCloseMessageId = id;
    }
    public String getClientServerCloseMessageId(){
        return clientServerCloseMessageId;
    }
    
    public void setClientReceiveWarnMessageId(String id){
        clientReceiveWarnMessageId = id;
    }
    public String getClientReceiveWarnMessageId(){
        return clientReceiveWarnMessageId;
    }
    
    public void setClientReceiveErrorMessageId(String id){
        clientReceiveErrorMessageId = id;
    }
    public String getClientReceiveErrorMessageId(){
        return clientReceiveErrorMessageId;
    }
    
    public void setServerSocketFactory(ServerSocketFactory factory){
        serverSocketFactory = factory;
    }
    public ServerSocketFactory getServerSocketFactory(){
        return serverSocketFactory;
    }
    
    public void setSocketFactory(SocketFactory factory){
        socketFactory = factory;
    }
    public SocketFactory getSocketFactory(){
        return socketFactory;
    }
    
    public void setNIOSocketFactory(jp.ossc.nimbus.util.net.SocketFactory factory){
        nioSocketFactory = factory;
    }
    public jp.ossc.nimbus.util.net.SocketFactory getNIOSocketFactory(){
        return nioSocketFactory;
    }
    
    public void setExternalizer(Externalizer ext){
        externalizer = ext;
    }
    public Externalizer getExternalizer(){
        return externalizer;
    }
    
    public void addServerConnectionListener(ServerConnectionListener listener){
        if(serverConnectionListeners == null){
            serverConnectionListeners = new ArrayList();
        }
        serverConnectionListeners.add(listener);
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener){
        if(serverConnectionListeners == null){
            return;
        }
        serverConnectionListeners.remove(listener);
    }
    
    public void clearServerConnectionListeners(){
        if(serverConnectionListeners == null){
            return;
        }
        serverConnectionListeners.clear();
    }
    
    public ServerConnectionListener[] getServerConnectionListeners(){
        return serverConnectionListeners == null ? null : (ServerConnectionListener[])serverConnectionListeners.toArray(new ServerConnectionListener[serverConnectionListeners.size()]);
    }
    
    public long getSendCount(){
        return serverConnection == null ? 0 : serverConnection.getSendCount();
    }
    
    public void resetSendCount(){
        if(serverConnection == null){
            return;
        }
        serverConnection.resetSendCount();
    }
    
    public double getAverageSendProcessTime(){
        return serverConnection == null ? 0.0d : serverConnection.getAverageSendProcessTime();
    }
    
    public double getAverageSendBytes(){
        return serverConnection == null ? 0.0d : serverConnection.getAverageSendBytes();
    }
    
    public Set getClients(){
        if(serverConnection == null){
            return new HashSet();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null){
                continue;
            }
            SocketAddress address = socket.getRemoteSocketAddress();
            if(address == null){
                continue;
            }
            result.add(address);
        }
        return result;
    }
    
    public int getClientSize(){
        return serverConnection.getClients().size();
    }
    
    public Set getEnabledClients(){
        if(serverConnection == null){
            return new HashSet();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null || !clientArray[i].isEnabled()){
                continue;
            }
            SocketAddress address = socket.getRemoteSocketAddress();
            if(address == null){
                continue;
            }
            result.add(address);
        }
        return result;
    }
    
    public Set getDisabledClients(){
        if(serverConnection == null){
            return new HashSet();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null || clientArray[i].isEnabled()){
                continue;
            }
            SocketAddress address = socket.getRemoteSocketAddress();
            if(address == null){
                continue;
            }
            result.add(address);
        }
        return result;
    }
    
    public void enabledClient(String address, int port){
        setEnabledClient(address, port, true);
    }
    
    public void disabledClient(String address, int port){
        setEnabledClient(address, port, false);
    }
    
    public Set getSubjects(String address, int port){
        if(serverConnection == null){
            return new HashSet();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null){
                continue;
            }
            InetSocketAddress remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
            if(remoteAddress == null){
                continue;
            }
            if((remoteAddress.getAddress().getHostAddress().equals(address)
                    || remoteAddress.getAddress().getHostName().equalsIgnoreCase(address))
                && port == remoteAddress.getPort()
            ){
                return clientArray[i].getSubjects();
            }
        }
        return new HashSet();
    }
    
    public Set getKeys(String address, int port, String subject){
        if(serverConnection == null){
            return new HashSet();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null){
                continue;
            }
            InetSocketAddress remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
            if(remoteAddress == null){
                continue;
            }
            if((remoteAddress.getAddress().getHostAddress().equals(address)
                    || remoteAddress.getAddress().getHostName().equalsIgnoreCase(address))
                && port == remoteAddress.getPort()
            ){
                return clientArray[i].getKeys(subject);
            }
        }
        return new HashSet();
    }
    
    private void setEnabledClient(String address, int port, boolean isEnabled){
        if(serverConnection == null){
            return;
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null || clientArray[i].isEnabled() == isEnabled){
                continue;
            }
            InetSocketAddress remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
            if(remoteAddress == null){
                continue;
            }
            if((remoteAddress.getAddress().getHostAddress().equals(address)
                    || remoteAddress.getAddress().getHostName().equalsIgnoreCase(address))
                && (port <= 0 || port == remoteAddress.getPort())
            ){
                clientArray[i].setEnabled(isEnabled);
            }
        }
    }
    
    public Map getSendCountsByClient(){
        if(serverConnection == null){
            return new HashMap();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Map result = new HashMap();
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null){
                continue;
            }
            SocketAddress address = socket.getRemoteSocketAddress();
            if(address == null){
                continue;
            }
            result.put(address, new Long(clientArray[i].getSendCount()));
        }
        return result;
    }
    
    public Map getAverageSendProcessTimesByClient(){
        if(serverConnection == null){
            return new HashMap();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Map result = new HashMap();
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null){
                continue;
            }
            SocketAddress address = socket.getRemoteSocketAddress();
            if(address == null){
                continue;
            }
            result.put(address, new Double(clientArray[i].getAverageSendProcessTime()));
        }
        return result;
    }
    
    public Map getAverageSendBytesByClient(){
        if(serverConnection == null){
            return new HashMap();
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Map result = new HashMap();
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null){
                continue;
            }
            SocketAddress address = socket.getRemoteSocketAddress();
            if(address == null){
                continue;
            }
            result.put(address, new Double(clientArray[i].getAverageSendBytes()));
        }
        return result;
    }
    
    public void resetSendCountsByClient(){
        if(serverConnection == null){
            return;
        }
        Set clients = serverConnection.getClients();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            clientArray[i].resetSendCount();
        }
    }
    
    public void startService() throws Exception{
        if(clientReconnectCount > 0 && serverPort == 0){
            throw new IllegalArgumentException("When clientReconnectCount is more than 0, serverPort must not be 0.");
        }
        if(serverAddress == null){
            serverAddress = InetAddress.getLocalHost().getHostAddress();
        }
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory
                .getServiceObject(externalizerServiceName);
        }
        if(isNIO){
            if(nioSocketFactoryServiceName != null){
                nioSocketFactory = (jp.ossc.nimbus.util.net.SocketFactory)ServiceManagerFactory
                    .getServiceObject(nioSocketFactoryServiceName);
            }
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress(serverAddress, serverPort));
            if(serverPort == 0){
                serverPort = serverSocketChannel.socket().getLocalPort();
            }
            serverSocketChannel.configureBlocking(false);
            serverConnection = new ServerConnectionImpl(
                serverSocketChannel,
                externalizer,
                sendThreadSize,
                sendQueueServiceName,
                asynchSendThreadSize,
                asynchSendQueueServiceName,
                asynchSendQueueFactoryServiceName,
                nioSocketFactory,
                sendBufferTime,
                sendBufferSize,
                sendBufferTimeoutInterval
            );
        }else{
        
            ServerSocket serverSocket = null;
            if(serverSocketFactory == null){
                if(serverSocketFactoryServiceName == null){
                    serverSocket = new ServerSocket(serverPort, serverBacklog, InetAddress.getByName(serverAddress)); 
                }else{
                    serverSocketFactory = (ServerSocketFactory)ServiceManagerFactory
                        .getServiceObject(serverSocketFactoryServiceName);
                }
            }
            if(serverSocket == null){
                serverSocket = serverSocketFactory.createServerSocket(
                    serverPort,
                    serverBacklog,
                    InetAddress.getByName(serverAddress)
                );
            }
            if(serverPort == 0){
                serverPort = serverSocket.getLocalPort();
            }
            serverSocket.setReuseAddress(true);
            serverConnection = new ServerConnectionImpl(
                serverSocket,
                externalizer,
                sendThreadSize,
                sendQueueServiceName,
                asynchSendThreadSize,
                asynchSendQueueServiceName,
                asynchSendQueueFactoryServiceName,
                sendBufferTime,
                sendBufferSize,
                sendBufferTimeoutInterval
            );
        }
        serverConnection.setLogger(getLogger());
        serverConnection.setMaxSendRetryCount(maxSendRetryCount);
        serverConnection.setSendMessageCacheTime(sendMessageCacheTime);
        serverConnection.setSendErrorMessageId(serverSendErrorMessageId);
        serverConnection.setSendErrorRetryOverMessageId(serverSendErrorRetryOverMessageId);
        serverConnection.setClientConnectMessageId(clientConnectMessageId);
        serverConnection.setClientClosedMessageId(clientClosedMessageId);
        serverConnection.setClientCloseMessageId(clientCloseMessageId);
        serverConnection.setAcknowledge(isAcknowledge);
        if(serverMessageRecycleBufferSize > 0){
            serverConnection.setMessageRecycleBufferSize(serverMessageRecycleBufferSize);
        }
        if(serverConnectionListenerServiceNames != null){
            for(int i = 0; i < serverConnectionListenerServiceNames.length; i++){
                serverConnection.addServerConnectionListener(
                    (ServerConnectionListener)ServiceManagerFactory
                        .getServiceObject(serverConnectionListenerServiceNames[i])
                );
            }
        }
        if(serverConnectionListeners != null){
            for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                serverConnection.addServerConnectionListener(
                    (ServerConnectionListener)serverConnectionListeners.get(i)
                );
            }
        }
        
        if(socketFactoryServiceName != null){
            socketFactory = (SocketFactory)ServiceManagerFactory
                .getServiceObject(socketFactoryServiceName);
        }
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
            
            remoteClientConnectionFactory = new RemoteClientConnectionFactory(
                this,
                rmiPort
            );
            if(!jndiRepository.register(jndiName, remoteClientConnectionFactory)){
                throw new Exception("Could not register in jndiRepository.");
            }
        }
    }
    
    public void stopService() throws Exception{
        if(jndiRepository != null){
            jndiRepository.unregister(jndiName);
        }
        if(remoteClientConnectionFactory != null){
            try{
                UnicastRemoteObject.unexportObject(remoteClientConnectionFactory, true);
            }catch(NoSuchObjectException e){}
            remoteClientConnectionFactory = null;
        }
        if(serverConnection != null){
            serverConnection.close();
            serverConnection = null;
        }
    }
    
    public ServerConnection getServerConnection() throws ConnectionCreateException{
        return serverConnection;
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException{
        ClientConnectionImpl connection = new ClientConnectionImpl(serverAddress, serverPort, socketFactory, externalizer, getServiceNameObject());
        if(clientAddressPropertyName != null){
            connection.setBindAddressPropertyName(clientAddressPropertyName);
        }
        if(clientPortPropertyName != null){
            connection.setBindPortPropertyName(clientPortPropertyName);
        }
        connection.setServerCloseMessageId(clientServerCloseMessageId);
        connection.setReceiveWarnMessageId(clientReceiveWarnMessageId);
        connection.setReceiveErrorMessageId(clientReceiveErrorMessageId);
        connection.setReconnectCount(clientReconnectCount);
        connection.setReconnectInterval(clientReconnectInterval);
        connection.setReconnectBufferTime(clientReconnectBufferTime);
        connection.setAcknowledge(isAcknowledge);
        connection.setResponseTimeout(clientResponseTimeout);
        if(clientMessageRecycleBufferSize > 0){
            connection.setMessageRecycleBufferSize(clientMessageRecycleBufferSize);
        }
        return connection;
    }
    
    public int getClientCount(){
        return serverConnection == null ? 0 : serverConnection.getClientCount();
    }
}