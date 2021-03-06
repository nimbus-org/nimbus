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

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * 間引きを行うメッセージ送信用のサーバコネクション生成サービス。<p>
 * 
 * @author M.Takata
 */
public class ThinOutServerConnectionFactoryService extends ServiceBase implements ServerConnectionFactory, ThinOutServerConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 5594630813249105749L;
    
    private ServiceName serverConnectionFactoryServiceName;
    private ServerConnectionFactory serverConnectionFactory;
    
    private ServiceName[] thinOutFilterServiceNames;
    private ThinOutFilter[] thinOutFilters;
    
    private long thinOutTimeout = 3000;
    private long thinOutTimeoutCheckInterval = 1000;
    
    private ThinOutServerConnectionImpl serverConnection;
    
    public void setServerConnectionFactoryServiceName(ServiceName name){
        serverConnectionFactoryServiceName = name;
    }
    public ServiceName getServerConnectionFactoryServiceName(){
        return serverConnectionFactoryServiceName;
    }
    
    public void setThinOutFilterServiceNames(ServiceName[] names){
        thinOutFilterServiceNames = names;
    }
    public ServiceName[] getThinOutFilterServiceNames(){
        return thinOutFilterServiceNames;
    }
    
    public void setServerConnectionFactory(ServerConnectionFactory factory){
        serverConnectionFactory = factory;
    }
    public ServerConnectionFactory getServerConnectionFactory(){
        return serverConnectionFactory;
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
        if(serverConnectionFactory == null){
            if(serverConnectionFactoryServiceName == null){
                throw new IllegalArgumentException("ServerConnectionFactory is null.");
            }
            serverConnectionFactory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(serverConnectionFactoryServiceName);
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
        serverConnection = new ThinOutServerConnectionImpl(
            serverConnectionFactory.getServerConnection(),
            thinOutFilters,
            thinOutTimeoutCheckInterval,
            thinOutTimeout
        );
    }
    
    public void stopService() throws Exception{
        serverConnection.close();
        serverConnection = null;
    }
    
    public ServerConnection getServerConnection() throws ConnectionCreateException{
        if(getState() != STARTED){
            throw new ConnectionCreateException("Service not started. name=" + getServiceNameObject());
        }
        return serverConnection;
    }
}