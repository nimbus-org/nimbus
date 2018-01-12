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
package jp.ossc.nimbus.service.scp;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * クラスタSCPクライアントファクトリ。<p>
 *
 * @author M.Takata
 */
public class ClusterSCPClientFactoryService extends ServiceBase
 implements SCPClientFactory, ClusterSCPClientFactoryServiceMBean{
    
    private static final long serialVersionUID = 8518162690689754186L;
    
    private ServiceName[] scpClientFactoryServiceNames;
    private SCPClientFactory[] scpClientFactories;
    private int clusterMode = CLUSTER_MODE_ACTIVE_STANDBY;
    private String connectErrorMessageId = MSG_ID_CONNECT_ERROR;
    private String skipMessageId = MSG_ID_SKIP;
    
    public void setSCPClientFactoryServiceNames(ServiceName[] names){
        scpClientFactoryServiceNames = names;
    }
    public ServiceName[] getSCPClientFactoryServiceNames(){
        return scpClientFactoryServiceNames;
    }
    
    public void setClusterMode(int mode) throws IllegalArgumentException{
        switch(clusterMode){
        case CLUSTER_MODE_ACTIVE_STANDBY:
            break;
        case CLUSTER_MODE_ACTIVE_ACTIVE:
            break;
        default:
            throw new IllegalArgumentException("Unknown mode : " + mode);
        }
        clusterMode = mode;
    }
    public int getClusterMode(){
        return clusterMode;
    }
    
    public void setConnectErrorMessageId(String id){
        connectErrorMessageId = id;
    }
    public String getConnectErrorMessageId(){
        return connectErrorMessageId;
    }
    
    public void setSkipMessageId(String id){
        skipMessageId = id;
    }
    public String getSkipMessageId(){
        return skipMessageId;
    }
    
    public void startService() throws Exception{
        if(scpClientFactoryServiceNames == null || scpClientFactoryServiceNames.length == 0){
            throw new IllegalArgumentException("SCPClientFactoryServiceNames is null.");
        }
        scpClientFactories = new SCPClientFactory[scpClientFactoryServiceNames.length];
        for(int i = 0; i < scpClientFactoryServiceNames.length; i++){
            scpClientFactories[i] = (SCPClientFactory)ServiceManagerFactory.getServiceObject(scpClientFactoryServiceNames[i]);
        }
    }
    
    public SCPClient createSCPClient() throws SCPException{
        return new ClusterSCPClient(clusterMode);
    }
    
    private class ClusterSCPClient implements SCPClient{
        private final int clusterMode;
        private SCPClient client;
        private List clients;
        private List noConnectedClients;
        
        public ClusterSCPClient(int mode) throws SCPException{
            clusterMode = mode;
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                for(int i = 0, imax = scpClientFactories.length; i < imax; i++){
                    try{
                        client = scpClientFactories[i].createSCPClient();
                        break;
                    }catch(SCPException e){
                        if(i == imax - 1){
                            throw e;
                        }else if(connectErrorMessageId != null){
                            getLogger().write(
                                connectErrorMessageId,
                                scpClientFactories[i],
                                e
                            );
                        }
                    }
                }
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                clients = new ArrayList();
                noConnectedClients = new ArrayList();
                for(int i = 0, imax = scpClientFactories.length; i < imax; i++){
                    try{
                        clients.add(scpClientFactories[i].createSCPClient());
                    }catch(SCPException e){
                        getLogger().write(
                            connectErrorMessageId,
                            scpClientFactories[i],
                            e
                        );
                        noConnectedClients.add(scpClientFactories[i]);
                    }
                }
                if(clients.size() == 0){
                    throw new SCPException("I could not connect all the cluster members.");
                }
                break;
            default:
            }
            
        }
        
        public void connect(String user, String host, String password) throws SCPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String user, String host, int port, String password) throws SCPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String user, String host, File pemFile, String passphrase) throws SCPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String user, String host, int port, File pemFile, String passphrase) throws SCPException{
            throw new UnsupportedOperationException();
        }
        
        public File get(String path) throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.get(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        return client.get(path);
                    }catch(SCPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File get(String remote, String local) throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.get(remote, local);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        return client.get(remote, local);
                    }catch(SCPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File[] mget(String remote) throws SCPException{
            return mget(remote, null);
        }
        
        public File[] mget(String remote, String localDir) throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.mget(remote, localDir);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                List resultList = null;
                Set resultSet = new HashSet();
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        File[] results = client.mget(remote, localDir);
                        for(int j = 0; j < results.length; j++){
                            File result = results[j];
                            if(!resultSet.contains(result)){
                                resultSet.add(result);
                                if(resultList == null){
                                    resultList = new ArrayList();
                                }
                                resultList.add(result);
                            }
                        }
                    }catch(SCPException e){
                        if(i == imax - 1 && resultList == null){
                            throw e;
                        }
                    }
                }
                if(resultList == null){
                    return null;
                }else{
                    Collections.sort(resultList);
                }
                return (File[])resultList.toArray(new File[resultList.size()]);
            }
        }
        
        public void put(String path) throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.put(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SCPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        client.put(path);
                        isSuccess = true;
                    }catch(SCPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "put"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "put"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void put(String local, String remote) throws SCPException{
            put(local, remote, null);
        }
        
        public void put(String local, String remote, String mode) throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.put(local, remote, mode);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SCPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        client.put(local, remote, mode);
                        isSuccess = true;
                    }catch(SCPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "put"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "put"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void mput(String local) throws SCPException{
            mput(local, ".");
        }
        
        public void mput(String local, String remoteDir) throws SCPException{
            mput(local, remoteDir, null);
        }
        
        public void mput(String local, String remoteDir, String mode) throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.mput(local, remoteDir, mode);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SCPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        client.mput(local, remoteDir, mode);
                        isSuccess = true;
                    }catch(SCPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "mput"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "mput"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void close() throws SCPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.close();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SCPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SCPClient client = (SCPClient)clients.get(i);
                    try{
                        client.close();
                    }catch(SCPException e){
                        ex = e;
                    }
                }
                if(ex != null){
                    throw ex;
                }
                break;
            default:
            }
        }
    }
}