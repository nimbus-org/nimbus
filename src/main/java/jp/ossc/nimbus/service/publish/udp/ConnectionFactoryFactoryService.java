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
package jp.ossc.nimbus.service.publish.udp;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceFactoryServiceBase;
import jp.ossc.nimbus.core.ServiceName;

/**
 * 
 * {@link ConnectionFactory}生成サービス。<p>
 * 
 * @author m-ishida
 *
 */
public class ConnectionFactoryFactoryService extends ServiceFactoryServiceBase implements ConnectionFactoryFactoryServiceMBean {
    
    private Map paramMap = new HashMap();
    private int portIncremental = 1;
    private int multicastPort = -1;
    private int unicastPort = -1;
    private int localPort = -1;
    private int serverPort = -1;
    private int rmiPort = -1;
    
    public void setClientBindAddressPropertyName(String name) {
        paramMap.put("clientBindAddressPropertyName", name);
    }
    
    public String getClientBindAddressPropertyName() {
        return (String)paramMap.get("clientBindAddressPropertyName");
    }
    
    public void setClientPortPropertyName(String name) {
        paramMap.put("clientPortPropertyName", name);
    }
    
    public String getClientPortPropertyName() {
        return (String)paramMap.get("clientPortPropertyName");
    }
    
    public void setClientUDPBindAddressPropertyName(String name) {
        paramMap.put("clientUDPBindAddressPropertyName", name);
    }
    
    public String getClientUDPBindAddressPropertyName() {
        return (String)paramMap.get("clientUDPBindAddressPropertyName");
    }
    
    public void setClientUDPNetworkInterfacesPropertyName(String name) {
        paramMap.put("clientUDPNetworkInterfacesPropertyName", name);
    }
    
    public String getClientUDPNetworkInterfacesPropertyName() {
        return (String)paramMap.get("clientUDPNetworkInterfacesPropertyName");
    }
    
    public void setClientReconnectCount(int count) {
        paramMap.put("clientReconnectCount", count);
    }
    
    public int getClientReconnectCount() {
        if(!paramMap.containsKey("clientReconnectCount")) {
            return -1;
        }
        return ((Integer)paramMap.get("clientReconnectCount")).intValue();
    }
    
    public void setClientReconnectInterval(long interval) {
        paramMap.put("clientReconnectInterval", interval);
    }
    
    public long getClientReconnectInterval() {
        if(!paramMap.containsKey("clientReconnectInterval")) {
            return -1;
        }
        return ((Long)paramMap.get("clientReconnectInterval")).longValue();
    }
    
    public void setClientReconnectBufferTime(long interval) {
        paramMap.put("clientReconnectBufferTime", interval);
    }
    
    public long getClientReconnectBufferTime() {
        if(!paramMap.containsKey("clientReconnectBufferTime")) {
            return -1;
        }
        return ((Long)paramMap.get("clientReconnectBufferTime")).longValue();
    }
    
    public void setMissingWindowTimeout(long interval) {
        paramMap.put("missingWindowTimeout", interval);
    }
    
    public long getMissingWindowTimeout() {
        if(!paramMap.containsKey("missingWindowTimeout")) {
            return -1;
        }
        return ((Long)paramMap.get("missingWindowTimeout")).longValue();
    }
    
    public void setMissingWindowCount(int count) {
        paramMap.put("missingWindowCount", count);
    }
    
    public int getMissingWindowCount() {
        if(!paramMap.containsKey("missingWindowCount")) {
            return -1;
        }
        return ((Integer)paramMap.get("missingWindowCount")).intValue();
    }
    
    public void setNewMessagePollingInterval(long interval) {
        paramMap.put("newMessagePollingInterval", interval);
    }
    
    public long getNewMessagePollingInterval() {
        if(!paramMap.containsKey("newMessagePollingInterval")) {
            return -1;
        }
        return ((Long)paramMap.get("newMessagePollingInterval")).longValue();
    }
    
    public void setServerAddress(String address) {
        paramMap.put("serverAddress", address);
    }
    
    public String getServerAddress() {
        return (String)paramMap.get("serverAddress");
    }
    
    public void setServerPort(int port) {
        paramMap.put("serverPort", port);
    }
    
    public int getServerPort() {
        if(!paramMap.containsKey("serverPort")) {
            return -1;
        }
        return ((Integer)paramMap.get("serverPort")).intValue();
    }
    
    public void setNetworkInterfaces(String[] names) {
        paramMap.put("networkInterfaceNames", names);
    }
    
    public String[] getNetworkInterfaces() {
        return (String[])paramMap.get("networkInterfaceNames");
    }
    
    public void setNIO(boolean isNIO) {
        paramMap.put("NIO", isNIO);
    }
    
    public boolean isNIO() {
        if(!paramMap.containsKey("NIO")) {
            return false;
        }
        return ((Boolean)paramMap.get("NIO")).booleanValue();
    }
    
    public void setServerBacklog(int backlog) {
        paramMap.put("serverBacklog", backlog);
    }
    
    public int getServerBacklog() {
        if(!paramMap.containsKey("serverBacklog")) {
            return -1;
        }
        return ((Integer)paramMap.get("serverBacklog")).intValue();
    }
    
    public void setServerSocketFactoryServiceName(ServiceName name) {
        paramMap.put("serverSocketFactoryServiceName", name);
    }
    
    public ServiceName getServerSocketFactoryServiceName() {
        return (ServiceName)paramMap.get("serverSocketFactoryServiceName");
    }
    
    public void setSocketFactoryServiceName(ServiceName name) {
        paramMap.put("socketFactoryServiceName", name);
    }
    
    public ServiceName getSocketFactoryServiceName() {
        return (ServiceName)paramMap.get("socketFactoryServiceName");
    }
    
    public void setNIOSocketFactoryServiceName(ServiceName name) {
        paramMap.put("nioSocketFactoryServiceName", name);
    }
    
    public ServiceName getNIOSocketFactoryServiceName() {
        return (ServiceName)paramMap.get("nioSocketFactoryServiceName");
    }
    
    public void setSendBindAddress(String ip) {
        paramMap.put("sendBindAddress", ip);
    }
    
    public String getSendBindAddress() {
        return (String)paramMap.get("sendBindAddress");
    }
    
    public void setMulticastGroupAddress(String ip) {
        paramMap.put("multicastGroupAddress", ip);
    }
    
    public String getMulticastGroupAddress() {
        return (String)paramMap.get("multicastGroupAddress");
    }
    
    public void setMulticastPort(int port) {
        paramMap.put("multicastPort", port);
    }
    
    public int getMulticastPort() {
        if(!paramMap.containsKey("multicastPort")) {
            return -1;
        }
        return ((Integer)paramMap.get("multicastPort")).intValue();
    }
    
    public void setTimeToLive(int ttl) {
        paramMap.put("timeToLive", ttl);
    }
    
    public int getTimeToLive() {
        if(!paramMap.containsKey("unicastPort")) {
            return -1;
        }
        return ((Integer)paramMap.get("timeToLive")).intValue();
    }
    
    public void setUnicastPort(int port) {
        paramMap.put("unicastPort", port);
    }
    
    public int getUnicastPort() {
        if(!paramMap.containsKey("unicastPort")) {
            return -1;
        }
        return ((Integer)paramMap.get("unicastPort")).intValue();
    }
    
    public void setWindowSize(int bytes) {
        paramMap.put("windowSize", bytes);
    }
    
    public int getWindowSize() {
        if(!paramMap.containsKey("windowSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("windowSize")).intValue();
    }
    
    public void setSendMessageCacheTime(long time) {
        paramMap.put("sendMessageCacheTime", time);
    }
    
    public long getSendMessageCacheTime() {
        if(!paramMap.containsKey("sendMessageCacheTime")) {
            return -1;
        }
        return ((Long)paramMap.get("sendMessageCacheTime")).longValue();
    }
    
    public int getSendMessageCacheBlockSize() {
        if(!paramMap.containsKey("sendMessageCacheBlockSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("sendMessageCacheBlockSize")).intValue();
    }
    
    public void setSendMessageCacheBlockSize(int size) {
        paramMap.put("sendMessageCacheBlockSize", size);
    }

    public void setLocalPort(int port) {
        paramMap.put("localPort", port);
    }
    
    public int getLocalPort() {
        if(!paramMap.containsKey("localPort")) {
            return -1;
        }
        return ((Integer)paramMap.get("localPort")).intValue();
    }
    
    public void setJndiName(String name) {
        paramMap.put("jndiName", name);
    }
    
    public String getJndiName() {
        return (String)paramMap.get("jndiName");
    }
    
    public void setJndiRepositoryServiceName(ServiceName name) {
        paramMap.put("jndiRepositoryServiceName", name);
    }
    
    public ServiceName getJndiRepositoryServiceName() {
        return (ServiceName)paramMap.get("jndiRepositoryServiceName");
    }
    
    public void setRMIPort(int port) {
        paramMap.put("rmiPort", port);
    }
    
    public int getRMIPort() {
        if(!paramMap.containsKey("rmiPort")) {
            return -1;
        }
        return ((Integer)paramMap.get("rmiPort")).intValue();
    }
    
    public void setSendQueueServiceName(ServiceName name) {
        paramMap.put("sendQueueServiceName", name);
    }
    
    public ServiceName getSendQueueServiceName() {
        return (ServiceName)paramMap.get("sendQueueServiceName");
    }
    
    public void setSendThreadSize(int threadSize) {
        paramMap.put("sendThreadSize", threadSize);
    }
    
    public int getSendThreadSize() {
        if(!paramMap.containsKey("sendThreadSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("sendThreadSize")).intValue();
    }
    
    public void setAsynchSendQueueServiceName(ServiceName name) {
        paramMap.put("asynchSendQueueServiceName", name);
    }
    
    public ServiceName getAsynchSendQueueServiceName() {
        return (ServiceName)paramMap.get("asynchSendQueueServiceName");
    }
    
    public void setAsynchSendQueueFactoryServiceName(ServiceName name) {
        paramMap.put("asynchSendQueueFactoryServiceName", name);
    }
    
    public ServiceName getAsynchSendQueueFactoryServiceName() {
        return (ServiceName)paramMap.get("asynchSendQueueFactoryServiceName");
    }
    
    public void setAsynchSendThreadSize(int threadSize) {
        paramMap.put("asynchSendThreadSize", threadSize);
    }
    
    public int getAsynchSendThreadSize() {
        if(!paramMap.containsKey("asynchSendThreadSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("asynchSendThreadSize")).intValue();
    }
    
    public void setRequestHandleQueueServiceName(ServiceName name) {
        paramMap.put("requestHandleQueueServiceName", name);
    }
    
    public ServiceName getRequestHandleQueueServiceName() {
        return (ServiceName)paramMap.get("requestHandleQueueServiceName");
    }
    
    public void setRequestHandleThreadSize(int threadSize) {
        paramMap.put("requestHandleThreadSize", threadSize);
    }
    
    public int getRequestHandleThreadSize() {
        if(!paramMap.containsKey("requestHandleThreadSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("requestHandleThreadSize")).intValue();
    }
    
    public void setExternalizerServiceName(ServiceName name) {
        paramMap.put("externalizerServiceName", name);
    }
    
    public ServiceName getExternalizerServiceName() {
        return (ServiceName)paramMap.get("externalizerServiceName");
    }
    
    public void setServerConnectionListenerServiceNames(ServiceName[] names) {
        paramMap.put("serverConnectionListenerServiceNames", names);
    }
    
    public ServiceName[] getServerConnectionListenerServiceNames() {
        return (ServiceName[])paramMap.get("serverConnectionListenerServiceNames");
    }
    
    public void setMaxSendRetryCount(int count) {
        paramMap.put("maxSendRetryCount", count);
    }
    
    public int getMaxSendRetryCount() {
        if(!paramMap.containsKey("maxSendRetryCount")) {
            return -1;
        }
        return ((Integer)paramMap.get("maxSendRetryCount")).intValue();
    }
    
    public void setAcknowledge(boolean isAck) {
        paramMap.put("Acknowledge", isAck);
    }
    
    public boolean isAcknowledge() {
        if(!paramMap.containsKey("Acknowledge")) {
            return false;
        }
        return ((Boolean)paramMap.get("Acknowledge")).booleanValue();
    }
    
    public void setServerSendErrorMessageId(String id) {
        paramMap.put("serverSendErrorMessageId", id);
    }
    
    public String getServerSendErrorMessageId() {
        return (String)paramMap.get("serverSendErrorMessageId");
    }
    
    public void setServerSendErrorRetryOverMessageId(String id) {
        paramMap.put("serverSendErrorRetryOverMessageId", id);
    }
    
    public String getServerSendErrorRetryOverMessageId() {
        return (String)paramMap.get("serverSendErrorRetryOverMessageId");
    }
    
    public void setServerResponseErrorMessageId(String id) {
        paramMap.put("serverResponseErrorMessageId", id);
    }
    
    public String getServerResponseErrorMessageId() {
        return (String)paramMap.get("serverResponseErrorMessageId");
    }
    
    public void setServerMessageLostErrorMessageId(String id) {
        paramMap.put("serverMessageLostErrorMessageId", id);
    }
    
    public String getServerMessageLostErrorMessageId() {
        return (String)paramMap.get("serverMessageLostErrorMessageId");
    }
    
    public void setServerStartReceiveMessageId(String id){
        paramMap.put("serverStartReceiveMessageId", id);
    }
    public String getServerStartReceiveMessageId(){
        return (String)paramMap.get("serverStartReceiveMessageId");
    }
    
    public void setServerStopReceiveMessageId(String id){
        paramMap.put("serverStopReceiveMessageId", id);
    }
    public String getServerStopReceiveMessageId(){
        return (String)paramMap.get("serverStopReceiveMessageId");
    }
    
    public void setServerClientConnectMessageId(String id) {
        paramMap.put("serverClientConnectMessageId", id);
    }
    
    public String getServerClientConnectMessageId() {
        return (String)paramMap.get("serverClientConnectMessageId");
    }
    
    public void setServerClientCloseMessageId(String id) {
        paramMap.put("serverClientCloseMessageId", id);
    }
    
    public String getServerClientCloseMessageId() {
        return (String)paramMap.get("serverClientCloseMessageId");
    }
    
    public void setServerClientClosedMessageId(String id){
        paramMap.put("serverClientClosedMessageId", id);
    }
    
    public String getServerClientClosedMessageId(){
        return (String)paramMap.get("serverClientClosedMessageId");
    }
    
    public void setClientStartReceiveMessageId(String id){
        paramMap.put("clientStartReceiveMessageId", id);
    }
    public String getClientStartReceiveMessageId(){
        return (String)paramMap.get("clientStartReceiveMessageId");
    }
    
    public void setClientStopReceiveMessageId(String id){
        paramMap.put("clientStopReceiveMessageId", id);
    }
    public String getClientStopReceiveMessageId(){
        return (String)paramMap.get("clientStopReceiveMessageId");
    }
    
    public void setClientConnectMessageId(String id){
        paramMap.put("clientConnectMessageId", id);
    }
    public String getClientConnectMessageId(){
        return (String)paramMap.get("clientConnectMessageId");
    }
    
    public void setClientCloseMessageId(String id){
        paramMap.put("clientCloseMessageId", id);
    }
    public String getClientCloseMessageId(){
        return (String)paramMap.get("clientCloseMessageId");
    }
    
    public void setClientClosedMessageId(String id){
        paramMap.put("clientClosedMessageId", id);
    }
    public String getClientClosedMessageId(){
        return (String)paramMap.get("clientClosedMessageId");
    }
    
    public void setServerMessageRecycleBufferSize(int size) {
        paramMap.put("serverMessageRecycleBufferSize", size);
    }
    
    public int getServerMessageRecycleBufferSize() {
        if(!paramMap.containsKey("serverMessageRecycleBufferSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("serverMessageRecycleBufferSize")).intValue();
    }
    
    public void setServerWindowRecycleBufferSize(int size) {
        paramMap.put("serverWindowRecycleBufferSize", size);
    }
    
    public int getServerWindowRecycleBufferSize() {
        if(!paramMap.containsKey("serverWindowRecycleBufferSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("serverWindowRecycleBufferSize")).intValue();
    }
    
    public void setClientServerCloseMessageId(String id) {
        paramMap.put("clientServerCloseMessageId", id);
    }
    
    public String getClientServerCloseMessageId() {
        return (String)paramMap.get("clientServerCloseMessageId");
    }
    
    public void setClientReceiveWarnMessageId(String id) {
        paramMap.put("clientReceiveWarnMessageId", id);
    }
    
    public String getClientReceiveWarnMessageId() {
        return (String)paramMap.get("clientReceiveWarnMessageId");
    }
    
    public void setClientReceiveErrorMessageId(String id) {
        paramMap.put("clientReceiveErrorMessageId", id);
    }
    
    public String getClientReceiveErrorMessageId() {
        return (String)paramMap.get("clientReceiveErrorMessageId");
    }
    
    public void setClientMessageLostErrorMessageId(String id) {
        paramMap.put("clientMessageLostErrorMessageId", id);
    }
    
    public String getClientMessageLostErrorMessageId() {
        return (String)paramMap.get("clientMessageLostErrorMessageId");
    }
    
    public void setClientResponseTimeout(long timeout) {
        paramMap.put("clientResponseTimeout", timeout);
    }
    
    public long getClientResponseTimeout() {
        if(!paramMap.containsKey("clientResponseTimeout")) {
            return -1;
        }
        return ((Long)paramMap.get("clientResponseTimeout")).longValue();
    }
    
    public void setClientPacketRecycleBufferSize(int size) {
        paramMap.put("clientPacketRecycleBufferSize", size);
    }
    
    public int getClientPacketRecycleBufferSize() {
        if(!paramMap.containsKey("clientPacketRecycleBufferSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("clientPacketRecycleBufferSize")).intValue();
    }
    
    public void setClientWindowRecycleBufferSize(int size) {
        paramMap.put("clientWindowRecycleBufferSize", size);
    }
    
    public int getClientWindowRecycleBufferSize() {
        if(!paramMap.containsKey("clientWindowRecycleBufferSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("clientWindowRecycleBufferSize")).intValue();
    }
    
    public void setClientMessageRecycleBufferSize(int size) {
        paramMap.put("clientMessageRecycleBufferSize", size);
    }
    
    public int getClientMessageRecycleBufferSize() {
        if(!paramMap.containsKey("clientMessageRecycleBufferSize")) {
            return -1;
        }
        return ((Integer)paramMap.get("clientMessageRecycleBufferSize")).intValue();
    }
    
    public int getPortIncremental() {
        return portIncremental;
    }

    public void setPortIncremental(int incremental) {
        if(incremental <= 0) {
            throw new IllegalArgumentException("PortIncremental is illegal value. portIncremental=" + portIncremental);
        }
        portIncremental = incremental;
    }

    protected Service createServiceInstance() throws Exception {
        ConnectionFactoryService connectionFactoryService = new ConnectionFactoryService();
        if(paramMap.containsKey("multicastGroupAddress") && !paramMap.containsKey("multicastPort")) {
            if(multicastPort == -1) {
                multicastPort = connectionFactoryService.getMulticastPort();
            } else {
                multicastPort += portIncremental;
            }
            connectionFactoryService.setMulticastPort(multicastPort);
        }
        Iterator itr = paramMap.entrySet().iterator();
        PropertyAccess pa = new PropertyAccess();
        while(itr.hasNext()) {
            Entry entry = (Entry)itr.next();
            if("multicastPort".equals(entry.getKey())) {
                if(multicastPort == -1) {
                    multicastPort = getMulticastPort();
                } else {
                    multicastPort += portIncremental;
                }
                connectionFactoryService.setMulticastPort(multicastPort);
            } else if("unicastPort".equals(entry.getKey())) {
                    if(unicastPort == -1) {
                        unicastPort = getUnicastPort();
                    } else {
                        unicastPort += portIncremental;
                    }
                    connectionFactoryService.setUnicastPort(unicastPort);
            } else if("localPort".equals(entry.getKey())) {
                if(localPort == -1) {
                    localPort = getLocalPort();
                } else {
                    localPort += portIncremental;
                }
                connectionFactoryService.setLocalPort(localPort);
            } else if("serverPort".equals(entry.getKey())) {
                if(serverPort == -1) {
                    serverPort = getServerPort();
                } else {
                    serverPort += portIncremental;
                }
                connectionFactoryService.setServerPort(serverPort);
            } else if("rmiPort".equals(entry.getKey())) {
                if(rmiPort == -1) {
                    rmiPort = getRMIPort();
                } else {
                    rmiPort += portIncremental;
                }
                connectionFactoryService.setRMIPort(rmiPort);
            } else {
                pa.set(connectionFactoryService, (String)entry.getKey(), entry.getValue());
            }
        }
        return connectionFactoryService;
    }
    
    public void stopService()  throws Exception {
        multicastPort = -1;
        unicastPort = -1;
        localPort = -1;
        serverPort = -1;
        rmiPort = -1;
    }
    
    public long getSendCount() {
        long result = 0;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            result += connectionFactoryService.getSendCount();
        }
        return result;
    }
    
    public long getSendPacketCount() {
        long result = 0;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            result += connectionFactoryService.getSendPacketCount();
        }
        return result;
    }
    
    public int getMaxWindowCount() {
        int result = -1;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result < connectionFactoryService.getMaxWindowCount()) {
                result = connectionFactoryService.getMaxWindowCount();
            }
        }
        return result;
    }
    
    public double getAverageWindowCount() {
        return getSendCount() == 0 ? 0.0d : (double)getSendPacketCount() / (double)getSendCount();
    }
    
    public void resetSendCount() {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.resetSendCount();
        }
    }
    
    public long getAverageSendProcessTime() {
        return -1;
    }
    
    public Set getClients() {
        Set result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getClients();
            } else {
                result.addAll(connectionFactoryService.getClients());
            }
        }
        return result;
    }
    
    public int getClientSize() {
        int result = 0;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            result += connectionFactoryService.getClientSize();
        }
        return result;
    }
    
    public Set getEnabledClients() {
        Set result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getEnabledClients();
            } else {
                result.addAll(connectionFactoryService.getEnabledClients());
            }
        }
        return result;
    }
    
    public Set getDisabledClients() {
        Set result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getDisabledClients();
            } else {
                result.addAll(connectionFactoryService.getDisabledClients());
            }
        }
        return result;
    }
    
    public void enabledClient(String address, int port) {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.enabledClient(address, port);
        }
    }
    
    public void disabledClient(String address, int port) {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.disabledClient(address, port);
        }
    }
    
    public Map getSendCountsByClient() {
        Map result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getSendCountsByClient();
            } else {
                result.putAll(connectionFactoryService.getSendCountsByClient());
            }
        }
        return result;
    }
    
    public Map getAverageSendProcessTimesByClient() {
        Map result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getAverageSendProcessTimesByClient();
            } else {
                result.putAll(connectionFactoryService.getAverageSendProcessTimesByClient());
            }
        }
        return result;
    }
    
    public void resetSendCountsByClient() {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.resetSendCountsByClient();
        }
    }
    
    public Map getNewMessagePollingCountsByClient() {
        Map result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getNewMessagePollingCountsByClient();
            } else {
                result.putAll(connectionFactoryService.getNewMessagePollingCountsByClient());
            }
        }
        return result;
    }
    
    public void resetNewMessagePollingCountsByClient() {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.resetNewMessagePollingCountsByClient();
        }
    }
    
    public Map getInterpolateRequestCountsByClient() {
        Map result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getInterpolateRequestCountsByClient();
            } else {
                result.putAll(connectionFactoryService.getInterpolateRequestCountsByClient());
            }
        }
        return result;
    }
    
    public void resetInterpolateRequestCountsByClient() {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.resetInterpolateRequestCountsByClient();
        }
    }
    
    public Map getLostCountsByClient() {
        Map result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getLostCountsByClient();
            } else {
                result.putAll(connectionFactoryService.getLostCountsByClient());
            }
        }
        return result;
    }
    
    public void resetLostCountsByClient() {
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            connectionFactoryService.resetLostCountsByClient();
        }
    }
    
    public Set getSubjects(String address, int port) {
        Set result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getSubjects(address, port);
            } else {
                result.addAll(connectionFactoryService.getSubjects(address, port));
            }
        }
        return result;
    }
    
    public Set getKeys(String address, int port, String subject) {
        Set result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            if(result == null) {
                result = connectionFactoryService.getKeys(address, port, subject);
            } else {
                result.addAll(connectionFactoryService.getKeys(address, port, subject));
            }
        }
        return result;
    }
    
    public int getMostOldSendMessageCacheSequence() {
        int result = -1;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            int seq = connectionFactoryService.getMostOldSendMessageCacheSequence();
            if(result == -1) {
                result = seq;
            } else if(result > seq){
                result = seq;
            }
        }
        return result;
    }
    
    public Date getMostOldSendMessageCacheTime() {
        Date result = null;
        Iterator itr = managedInstances.iterator();
        while(itr.hasNext()) {
            ConnectionFactoryService connectionFactoryService = (ConnectionFactoryService)itr.next();
            Date date = connectionFactoryService.getMostOldSendMessageCacheTime();
            if(result == null) {
                result = date;
            } else if(result.compareTo(date) > 0){
                result = date;
            }
        }
        return result;
    }
    
    public int getSendMessageCacheSize() {
        return -1;
    }
    
    public long getAverageAsynchSendProcessTime() {
        return -1l;
    }
    
    public long getAverageRequestHandleProcessTime() {
        return -1l;
    }
    
    public double getMessageRecycleRate() {
        return -1d;
    }
    
    public double getWindowRecycleRate() {
        return -1d;
    }

}
