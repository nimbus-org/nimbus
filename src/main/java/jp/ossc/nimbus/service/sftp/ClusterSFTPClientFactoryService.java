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
package jp.ossc.nimbus.service.sftp;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * クラスタSFTPクライアントファクトリ。<p>
 *
 * @author M.Takata
 */
public class ClusterSFTPClientFactoryService extends ServiceBase
 implements SFTPClientFactory, ClusterSFTPClientFactoryServiceMBean{
    
    private static final long serialVersionUID = -2309390745942004716L;
    
    private ServiceName[] sftpClientFactoryServiceNames;
    private SFTPClientFactory[] sftpClientFactories;
    private int clusterMode = CLUSTER_MODE_ACTIVE_STANDBY;
    private String connectErrorMessageId = MSG_ID_CONNECT_ERROR;
    private String skipMessageId = MSG_ID_SKIP;
    
    public void setSFTPClientFactoryServiceNames(ServiceName[] names){
        sftpClientFactoryServiceNames = names;
    }
    public ServiceName[] getSFTPClientFactoryServiceNames(){
        return sftpClientFactoryServiceNames;
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
        if(sftpClientFactoryServiceNames == null || sftpClientFactoryServiceNames.length == 0){
            throw new IllegalArgumentException("SFTPClientFactoryServiceNames is null.");
        }
        sftpClientFactories = new SFTPClientFactory[sftpClientFactoryServiceNames.length];
        for(int i = 0; i < sftpClientFactoryServiceNames.length; i++){
            sftpClientFactories[i] = (SFTPClientFactory)ServiceManagerFactory.getServiceObject(sftpClientFactoryServiceNames[i]);
        }
    }
    
    public SFTPClient createSFTPClient() throws SFTPException{
        return new ClusterSFTPClient(clusterMode);
    }
    
    private class ClusterSFTPClient implements SFTPClient{
        private final int clusterMode;
        private SFTPClient client;
        private List clients;
        private List noConnectedClients;
        
        public ClusterSFTPClient(int mode) throws SFTPException{
            clusterMode = mode;
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                for(int i = 0, imax = sftpClientFactories.length; i < imax; i++){
                    try{
                        client = sftpClientFactories[i].createSFTPClient();
                        break;
                    }catch(SFTPException e){
                        if(i == imax - 1){
                            throw e;
                        }else if(connectErrorMessageId != null){
                            getLogger().write(
                                connectErrorMessageId,
                                sftpClientFactories[i],
                                e
                            );
                        }
                    }
                }
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                clients = new ArrayList();
                noConnectedClients = new ArrayList();
                for(int i = 0, imax = sftpClientFactories.length; i < imax; i++){
                    try{
                        clients.add(sftpClientFactories[i].createSFTPClient());
                    }catch(SFTPException e){
                        getLogger().write(
                            connectErrorMessageId,
                            sftpClientFactories[i],
                            e
                        );
                        noConnectedClients.add(sftpClientFactories[i]);
                    }
                }
                if(clients.size() == 0){
                    throw new SFTPException(getServiceNameObject(), "I could not connect all the cluster members.");
                }
                break;
            default:
            }
            
        }
        
        public void connect(String user, String host, String password) throws SFTPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String user, String host, int port, String password) throws SFTPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String user, String host, File pemFile, String passphrase) throws SFTPException{
            throw new UnsupportedOperationException();
        }
        
        public void connect(String user, String host, int port, File pemFile, String passphrase) throws SFTPException{
            throw new UnsupportedOperationException();
        }
        
        public String[] ls() throws SFTPException{
            return ls(".");
        }
        
        public String[] ls(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.ls(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                List resultList = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
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
                    }catch(SFTPException e){
                        if(i == imax - 1 && resultList == null){
                            throw e;
                        }
                    }
                }
                return resultList == null ? null : (String[])resultList.toArray(new String[resultList.size()]);
            }
        }
        
        public SFTPFile[] lsFile() throws SFTPException{
            return lsFile(".");
        }
        
        public SFTPFile[] lsFile(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.lsFile(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                List resultList = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        SFTPFile[] results = client.lsFile(path);
                        if(resultList == null){
                            resultList = new ArrayList();
                        }
                        if(results != null){
                            for(int j = 0; j < results.length; j++){
                                SFTPFile result = results[j];
                                if(resultList.indexOf(result) == -1){
                                    resultList.add(result);
                                }
                            }
                        }
                    }catch(SFTPException e){
                        if(i == imax - 1 && resultList == null){
                            throw e;
                        }
                    }
                }
                return resultList == null ? null : (SFTPFile[])resultList.toArray(new SFTPFile[resultList.size()]);
            }
        }
        
        public String pwd() throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.pwd();
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        return client.pwd();
                    }catch(SFTPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File lpwd() throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.lpwd();
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                return ((SFTPClient)clients.get(0)).lpwd();
            }
        }
        
        public void cd(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.cd(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.cd(path);
                    }catch(SFTPException e){
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
        
        public void lcd(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.lcd(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.lcd(path);
                    }catch(SFTPException e){
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
        
        public void mkdir(String dir) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.mkdir(dir);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.mkdir(dir);
                    }catch(SFTPException e){
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
        
        public void rename(String from, String to) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.rename(from, to);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.rename(from, to);
                    }catch(SFTPException e){
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
        
        public File get(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.get(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        return client.get(path);
                    }catch(SFTPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File get(String remote, String local) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.get(remote, local);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        return client.get(remote, local);
                    }catch(SFTPException e){
                        if(i == imax - 1){
                            throw e;
                        }
                    }
                }
                return null;
            }
        }
        
        public File[] mget(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.mget(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
            default:
                List resultList = null;
                Set resultSet = new HashSet();
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        String[] results = client.ls(path);
                        for(int j = 0; j < results.length; j++){
                            String result = results[j];
                            if(!resultSet.contains(result)){
                                resultSet.add(result);
                                File file = client.get(result);
                                if(resultList == null){
                                    resultList = new ArrayList();
                                }
                                resultList.add(file);
                            }
                        }
                    }catch(SFTPException e){
                        if(i == imax - 1 && resultList == null){
                            throw e;
                        }
                    }
                }
                return resultList == null ? null : (File[])resultList.toArray(new File[resultList.size()]);
            }
        }
        
        public void put(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.put(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.put(path);
                        isSuccess = true;
                    }catch(SFTPException e){
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
        
        public void put(String local, String remote) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.put(local, remote);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.put(local, remote);
                        isSuccess = true;
                    }catch(SFTPException e){
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
        
        public void mput(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.mput(path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.mput(path);
                        isSuccess = true;
                    }catch(SFTPException e){
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
        
        public boolean rm(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.rm(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                boolean isRemove = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        isRemove |= client.rm(path);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "rm"});
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "rm"});
                }
                if(!isSuccess){
                    throw ex;
                }
                return isRemove;
            default:
                return false;
            }
        }
        
        public boolean rmdir(String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                return client.rmdir(path);
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                boolean isRemove = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        isRemove |= client.rmdir(path);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "rmdir"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "rmdir"});
                }
                if(!isSuccess){
                    throw ex;
                }
                return isRemove;
            default:
                return false;
            }
        }
        
        public void chmod(String mode, String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.chmod(mode, path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.chmod(mode, path);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "chmod"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "chmod"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void chown(String uid, String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.chown(uid, path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.chown(uid, path);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "chown"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "chown"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void chgrp(String gid, String path) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.chgrp(gid, path);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.chgrp(gid, path);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "chgrp"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "chgrp"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void symlink(String path, String link) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.symlink(path, link);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.symlink(path, link);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "symlink"} ,e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "symlink"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void ln(String path, String link) throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.ln(path, link);
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                boolean isSuccess = false;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.ln(path, link);
                        isSuccess = true;
                    }catch(SFTPException e){
                        if(skipMessageId != null){
                            getLogger().write(skipMessageId, new Object[]{client, "ln"}, e);
                        }
                        ex = e;
                    }
                }
                if(skipMessageId != null && noConnectedClients.size() > 0){
                    getLogger().write(skipMessageId, new Object[]{noConnectedClients, "ln"});
                }
                if(!isSuccess){
                    throw ex;
                }
                break;
            default:
            }
        }
        
        public void close() throws SFTPException{
            switch(clusterMode){
            case CLUSTER_MODE_ACTIVE_STANDBY:
                client.close();
                break;
            case CLUSTER_MODE_ACTIVE_ACTIVE:
                SFTPException ex = null;
                for(int i = 0, imax = clients.size(); i < imax; i++){
                    SFTPClient client = (SFTPClient)clients.get(i);
                    try{
                        client.close();
                    }catch(SFTPException e){
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