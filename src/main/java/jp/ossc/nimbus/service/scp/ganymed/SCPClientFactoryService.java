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
package jp.ossc.nimbus.service.scp.ganymed;

import java.io.File;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.scp.*;

/**
 * SCPクライアントファクトリ。<p>
 * <a href="http://www.ganymed.ethz.ch/ssh2/">Ganymed SSH-2 for Java</a>のSCPライブラリを使用した{@link SCPClientFactory}サービスである。<br>
 *
 * @author M.Takata
 */
public class SCPClientFactoryService extends ServiceBase
 implements SCPClientFactory, SCPClientFactoryServiceMBean{
    
    private static final long serialVersionUID = 2888605574336286830L;
    
    private int connectionTimeout = -1;
    private int keyExchangeTimeout = -1;
    private Boolean isTcpNoDelay;
    
    private String hostName;
    private int port = -1;
    
    private String userName;
    private String password = "";
    private File pemFile;
    
    private String[] serverHostKeyAlgorithms;
    
    private File homeDir;
    
    public void setConnectionTimeout(int timeout){
        connectionTimeout = timeout;
    }
    public int getConnectionTimeout(){
        return connectionTimeout;
    }
    
    public void setKeyExchangeTimeout(int timeout){
        keyExchangeTimeout = timeout;
    }
    public int getKeyExchangeTimeout(){
        return keyExchangeTimeout;
    }
    
    public void setTcpNoDelay(boolean noDelay){
        isTcpNoDelay = noDelay ? Boolean.TRUE : Boolean.FALSE;
    }
    public boolean isTcpNoDelay(){
        return isTcpNoDelay == null ? false : isTcpNoDelay.booleanValue();
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
    
    public void setServerHostKeyAlgorithms(String[] algos){
        serverHostKeyAlgorithms = algos;
    }
    public String[] getServerHostKeyAlgorithms(){
        return serverHostKeyAlgorithms;
    }
    
    public void setHomeDirectory(File dir){
        homeDir = dir;
    }
    public File getHomeDirectory(){
        return homeDir;
    }
    
    public SCPClient createSCPClient() throws SCPException{
        SCPClientImpl client = new SCPClientImpl();
        if(connectionTimeout > 0){
            client.setConnectionTimeout(connectionTimeout);
        }
        if(keyExchangeTimeout > 0){
            client.setKeyExchangeTimeout(keyExchangeTimeout);
        }
        if(isTcpNoDelay != null){
            client.setTcpNoDelay(isTcpNoDelay.booleanValue());
        }
        if(serverHostKeyAlgorithms != null){
            client.setServerHostKeyAlgorithms(serverHostKeyAlgorithms);
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
        if(homeDir != null){
            client.setHomeDirectory(homeDir);
        }
        return client;
    }
}
