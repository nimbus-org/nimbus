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
package jp.ossc.nimbus.service.sftp.jsch;

import java.io.File;
import java.util.Properties;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.sftp.*;
import jp.ossc.nimbus.service.semaphore.Semaphore;

import com.jcraft.jsch.Proxy;

/**
 * SFTPクライアントファクトリ。<p>
 * <a href="http://www.jcraft.com/jsch/">JSch - Java Secure Channel</a>のSFTPライブラリを使用した{@link SFTPClientFactory}サービスである。<br>
 *
 * @author M.Takata
 */
public class SFTPClientFactoryService extends ServiceBase
 implements SFTPClientFactory, SFTPClientFactoryServiceMBean{
    
    private static final long serialVersionUID = 3883321019571577279L;
    
    private int timeout = -1;
    private int serverAliveInterval = -1;
    private int serverAliveCountMax = -1;
    
    private String hostName;
    private int port = -1;
    
    private String userName;
    private String password;
    private File pemFile;
    private Proxy proxy;
    
    private ServiceName semaphoreServiceName;
    private Semaphore semaphore;
    
    private Properties configProperties;
    
    private File homeDir;
    private String fileNameEncoding;
    
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    public int getTimeout(){
        return timeout;
    }
    
    public void setServerAliveInterval(int interval){
        serverAliveInterval = timeout;
    }
    public int getServerAliveInterval(){
        return serverAliveInterval;
    }
    
    public void setServerAliveCountMax(int count){
        serverAliveCountMax = count;
    }
    public int getServerAliveCountMax(){
        return serverAliveCountMax;
    }
    
    public void setHostName(String addr){
        hostName = addr;
    }
    public String getHostName(){
        return hostName;
    }
    
    public void setPort(int port){
        this.port = port;
    }
    public int getPort(){
        return port;
    }
    
    public void setProxy(Proxy proxy){
        this.proxy = proxy;
    }
    
    public void setUserName(String name){
        userName = name;
    }
    public String getUserName(){
        return userName;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    
    public void setPemFile(File path){
        this.pemFile = path;
    }
    public File getPemFile(){
        return pemFile;
    }
    
    public void setConfig(Properties conf){
        configProperties = conf;
    }
    public Properties getConfig(){
        return configProperties;
    }
    
    public void setHomeDirectory(File dir){
        homeDir = dir;
    }
    public File getHomeDirectory(){
        return homeDir;
    }
    
    public void setFileNameEncoding(String encoding){
        fileNameEncoding = encoding;
    }
    public String getFileNameEncoding(){
        return fileNameEncoding;
    }
    
    public void setSemaphoreServiceName(ServiceName name){
        semaphoreServiceName = name;
    }
    public ServiceName getSemaphoreServiceName(){
        return semaphoreServiceName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(semaphoreServiceName != null){
            semaphore = (Semaphore)ServiceManagerFactory.getServiceObject(
                semaphoreServiceName
            );
            semaphore.accept();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止に失敗した場合
     */
    public void stopService() throws Exception{
        if(semaphore != null){
            semaphore.release();
        }
    }
    
    public SFTPClient createSFTPClient() throws SFTPException{
        if(semaphore != null && !semaphore.getResource()){
            throw new SFTPClientCreateTimeoutException();
        }
        SFTPClientImpl client = new SFTPClientImpl();
        if(semaphore != null){
            client.setSemaphore(semaphore);
        }
        if(timeout >= 0){
            client.setTimeout(timeout);
        }
        if(serverAliveInterval >= 0){
            client.setServerAliveInterval(serverAliveInterval);
        }
        if(serverAliveCountMax >= 0){
            client.setServerAliveCountMax(serverAliveCountMax);
        }
        if(configProperties != null){
            client.setConfig(configProperties);
        }
        if(proxy != null){
            client.setProxy(proxy);
        }
        if(homeDir != null){
            client.setHomeDirectory(homeDir);
        }
        if(fileNameEncoding != null){
            client.setFileNameEncoding(fileNameEncoding);
        }
        
        if(userName != null){
            if(pemFile == null){
                if(port > 0){
                    client.connect(userName, hostName, port, password);
                }else{
                    client.connect(userName, hostName, password);
                }
            }else{
                if(port > 0){
                    client.connect(userName, hostName, port, pemFile, password);
                }else{
                    client.connect(userName, hostName, pemFile, password);
                }
            }
        }
        return client;
    }
}
