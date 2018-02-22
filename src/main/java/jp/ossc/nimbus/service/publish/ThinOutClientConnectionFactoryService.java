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
package jp.ossc.nimbus.service.publish;

import java.rmi.RemoteException;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * 間引きを行うメッセージ受信用のクライアントコネクション生成サービス。<p>
 * 
 * @author M.Takata
 */
public class ThinOutClientConnectionFactoryService extends ServiceBase implements ClientConnectionFactory, ThinOutClientConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = -6262634357594773198L;
    
    private ServiceName clientConnectionFactoryServiceName;
    private ClientConnectionFactory clientConnectionFactory;
    
    private ServiceName[] thinOutFilterServiceNames;
    private ThinOutFilter[] thinOutFilters;
    
    private long thinOutTimeout = 3000;
    private long thinOutTimeoutCheckInterval = 1000;
    
    public void setClientConnectionFactoryServiceName(ServiceName name){
        clientConnectionFactoryServiceName = name;
    }
    public ServiceName getClientConnectionFactoryServiceName(){
        return clientConnectionFactoryServiceName;
    }
    
    public void setThinOutFilterServiceNames(ServiceName[] names){
        thinOutFilterServiceNames = names;
    }
    public ServiceName[] getThinOutFilterServiceNames(){
        return thinOutFilterServiceNames;
    }
    
    public void setClientConnectionFactory(ClientConnectionFactory factory){
        clientConnectionFactory = factory;
    }
    public ClientConnectionFactory getClientConnectionFactory(){
        return clientConnectionFactory;
    }
    
    public void setThinOutFilters(ThinOutFilter[] filters){
        thinOutFilters = filters;
    }
    public ThinOutFilter[] getThinOutFilters(){
        return thinOutFilters;
    }
    
    public void setThinOutTimeoutCheckInterval(long interval){
        thinOutTimeoutCheckInterval = interval;
    }
    public long getThinOutTimeoutCheckInterval(){
        return thinOutTimeoutCheckInterval;
    }
    
    public void setThinOutTimeout(long timeout){
        thinOutTimeout = timeout;
    }
    public long getThinOutTimeout(){
        return thinOutTimeout;
    }
    
    public void startService() throws Exception{
        if(clientConnectionFactory == null){
            if(clientConnectionFactoryServiceName == null){
                throw new IllegalArgumentException("ClientConnectionFactory is null.");
            }
            clientConnectionFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(clientConnectionFactoryServiceName);
        }
        if(thinOutFilters == null || thinOutFilters.length == 0){
            if(thinOutFilterServiceNames == null){
                throw new IllegalArgumentException("ThinOutFilters is null.");
            }
            thinOutFilters = new ThinOutFilter[thinOutFilterServiceNames.length];
            for(int i = 0; i < thinOutFilterServiceNames.length; i++){
                thinOutFilters[i] = (ThinOutFilter)ServiceManagerFactory.getServiceObject(thinOutFilterServiceNames[i]);
            }
        }
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        if(getState() != STARTED){
            throw new ConnectionCreateException("Service not started. name=" + getServiceNameObject());
        }
        ClientConnection clientConnection = new ThinOutClientConnectionImpl(
            clientConnectionFactory.getClientConnection(),
            thinOutFilters,
            thinOutTimeoutCheckInterval,
            thinOutTimeout
        );
        return clientConnection;
    }
    
    public int getClientCount() throws RemoteException{
        return clientConnectionFactory.getClientCount();
    }
}