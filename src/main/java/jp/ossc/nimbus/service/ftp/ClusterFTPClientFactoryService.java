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
package jp.ossc.nimbus.service.ftp;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * クラスタFTPクライアントファクトリ。<p>
 *
 * @author M.Takata
 */
public class ClusterFTPClientFactoryService extends ServiceBase
 implements FTPClientFactory, ClusterFTPClientFactoryServiceMBean{
    
    private static final long serialVersionUID = -8852749736373814643L;
    
    private ServiceName[] ftpClientFactoryServiceNames;
    private FTPClientFactory[] ftpClientFactories;
    private int clusterMode = CLUSTER_MODE_ACTIVE_STANDBY;
    private String connectErrorMessageId = MSG_ID_CONNECT_ERROR;
    private String skipMessageId = MSG_ID_SKIP;
    
    public void setFTPClientFactoryServiceNames(ServiceName[] names){
        ftpClientFactoryServiceNames = names;
    }
    public ServiceName[] getFTPClientFactoryServiceNames(){
        return ftpClientFactoryServiceNames;
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
        if(ftpClientFactoryServiceNames == null || ftpClientFactoryServiceNames.length == 0){
            throw new IllegalArgumentException("FTPClientFactoryServiceNames is null.");
        }
        ftpClientFactories = new FTPClientFactory[ftpClientFactoryServiceNames.length];
        for(int i = 0; i < ftpClientFactoryServiceNames.length; i++){
            ftpClientFactories[i] = (FTPClientFactory)ServiceManagerFactory.getServiceObject(ftpClientFactoryServiceNames[i]);
        }
    }
    
    public FTPClient createFTPClient() throws FTPException{
        return new ClusterFTPClient(clusterMode);
    }
    
    private class ClusterFTPClient implements FTPClient{
        private final int clusterMode;
        private FTPClient client;
        private List clients;
        private List noConnectedClients;
        
        public ClusterFTPClient(int mode) throws FTPException{
            clusterMode = mode;
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                for(int i = 0, imax = ftpClientFactories.length; i < imax; i++){
                    try{
                        client = ftpClientFactories[i].createFTPClient();
                        break;
                    }catch(FTPException e){
                        if(i == imax - 1){
                            throw e;
                        }else if(connectErrorMessageId != null){
                            getLogger().write(
                                connectErrorMessageId,
                                ftpClientFactories[i],
                                e
                            );
                        }
                    }
                }
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                clients = new ArrayList();
                noConnectedClients = new ArrayList();
                for(int i = 0, imax = ftpClientFactories.length; i < imax; i++){
                    try{
                        clients.add(ftpClientFactories[i].createFTPClient());
                    }catch(FTPException e){
                        getLogger().write(
                            connectErrorMessageId,
                            ftpClientFactories[i],
                            e
                        );
                        noConnectedClients.add(ftpClientFactories[i]);
                    }
                }
                if(clients.size() == 0){
                    throw new FTPException(getServiceNameObject(), "I could not connect all the cluster members.");
                }
                break;
            default:
            }
            
        }
        
        public void connect(String host) throws FTPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String host, int port) throws FTPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(
            String host,
            int port,
            String localAddr,
            int localPort
        ) throws FTPException{
            throw new UnsupportedOperationException();
        }
        
        public void login(String user, String password) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.login(user, password);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    client.login(user, password);
                }
                break;
            default:
            }
        }
        
        public void logout() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.logout();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.logout();
                    }catch(FTPException e){
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
        
        public String[] ls() throws FTPException{
            return ls(".");
        }
        
        public String[] ls(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.ls(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                List resultList = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        String[] results = client.ls(path);
                        if(resultList == null){
                            resultList = new ArrayList();
                        }
                        if(results != null){
                            for(int j = 0; j < results.length; j++){
                                String result = results[j];
                                if(resultList.indexOf(result) == -1){
                                    resultList.add(result);
                                }
                            }
                        }
                    }catch(FTPException e){
                        if(i == imax - 1 && resultList == null){
                            throw e;
                        }
                    }
                }
                return resultList == null ? null : (String[])resultList.toArray(new String[resultList.size()]);
            }
        }
        
        public String pwd() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.pwd();
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        return client.pwd();
                    }catch(FTPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File lpwd() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.lpwd();
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                return ((FTPClient)clients.get(0)).lpwd();
            }
        }
        
        public void cd(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.cd(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.cd(path);
                    }catch(FTPException e){
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
        
        public void lcd(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.lcd(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.lcd(path);
                    }catch(FTPException e){
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
        
        public void mkdir(String dir) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.mkdir(dir);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.mkdir(dir);
                    }catch(FTPException e){
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
        
        public void rename(String from, String to) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.rename(from, to);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.rename(from, to);
                    }catch(FTPException e){
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
        
        public File get(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.get(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        return client.get(path);
                    }catch(FTPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File get(String remote, String local) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.get(remote, local);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        return client.get(remote, local);
                    }catch(FTPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File[] mget(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.mget(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                List resultList = null;
                Set resultSet = new HashSet();
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        String[] results = client.ls(path);
                        if(results != null){
                            for(int j = 0; j < results.length; j++){
                                String result = results[j];
                                if(!resultSet.contains(result)){
                                    File file = client.get(result);
                                    if(resultList == null){
                                        resultList = new ArrayList();
                                    }
                                    resultList.add(file);
                                }
                            }
                        }
                    }catch(FTPException e){
                        if(i == imax - 1 && resultList == null){
                            throw e;
                        }
                    }
                }
                return resultList == null ? null : (File[])resultList.toArray(new File[resultList.size()]);
            }
        }
        
        public void put(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.put(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.put(path);
                        isSuccess = true;
                    }catch(FTPException e){
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
        
        public void put(String local, String remote) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.put(local, remote);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.put(local, remote);
                        isSuccess = true;
                    }catch(FTPException e){
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
        
        public void mput(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.mput(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.mput(path);
                        isSuccess = true;
                    }catch(FTPException e){
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
        
        public void delete(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.delete(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.delete(path);
                        isSuccess = true;
                    }catch(FTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "delete"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "delete"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void mdelete(String path) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.mdelete(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.mdelete(path);
                        isSuccess = true;
                    }catch(FTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "mdelete"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "mdelete"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void ascii() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.ascii();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    client.ascii();
                }
                break;
            default:
            }
        }
        
        public void binary() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.binary();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    client.binary();
                }
                break;
            default:
            }
        }
        
        public void setTransferType(int type) throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.setTransferType(type);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    client.setTransferType(type);
                }
                break;
            default:
            }
        }
        
        public int getTransferType() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.getTransferType();
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                return ((FTPClient)clients.get(0)).getTransferType();
            }
        }
        
        public void active() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.active();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    client.active();
                }
                break;
            default:
            }
        }
        
        public void passive() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.passive();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    client.passive();
                }
                break;
            default:
            }
        }
        
        public void close() throws FTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.close();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                FTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    FTPClient client = (FTPClient)clients.get(i);
                    try{
                        client.close();
                    }catch(FTPException e){
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