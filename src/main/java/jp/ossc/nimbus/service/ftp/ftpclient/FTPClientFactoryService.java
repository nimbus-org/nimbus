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
package jp.ossc.nimbus.service.ftp.ftpclient;

import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ServerSocketFactory;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.ftp.*;

import org.apache.commons.net.ftp.FTPFileListParser;

/**
 * FTPクライアントファクトリ。<p>
 * <a href="http://commons.apache.org/net/">Jakarta Commons Net</a>のFTPライブラリを使用した{@link FTPClientFactory}サービスである。<br>
 *
 * @author M.Takata
 */
public class FTPClientFactoryService extends ServiceBase
 implements FTPClientFactory, FTPClientFactoryServiceMBean{
    
    private static final long serialVersionUID = 1823861487346954556L;
    
    private int soTimeout = -1;
    private int soLinger = -1;
    private Boolean isTcpNoDelay;
    private ServerSocketFactory serverSocketFactory;
    private SocketFactory socketFactory;
    
    private String hostName;
    private int port = -1;
    
    private String bindAddress;
    private int localPort = -1;
    
    private String userName;
    private String password = "";
    
    private FTPFileListParser ftpFileListParser;
    
    private File homeDir;
    
    private boolean isJavaRegexEnabled = false;
    
    private boolean isPassive = false;
    
    private Map ftpClientProps = Collections.synchronizedMap(new HashMap());
    
    private int connectMaxRetryCount = 0;
    
    public void setSoTimeout(int timeout){
        soTimeout = timeout;
    }
    public int getSoTimeout(){
        return soTimeout;
    }
    
    public void setSoLinger(int time){
        soLinger = time;
    }
    public int getSoLinger(){
        return soLinger;
    }
    
    public void setTcpNoDelay(boolean noDelay){
        isTcpNoDelay = noDelay ? Boolean.TRUE : Boolean.FALSE;
    }
    public boolean isTcpNoDelay(){
        return isTcpNoDelay == null ? false : isTcpNoDelay.booleanValue();
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
    
    public void setBindAddress(String addr){
        bindAddress = addr;
    }
    public String getBindAddress(){
        return bindAddress;
    }
    
    public void setLocalPort(int port){
        localPort = port;
    }
    public int getLocalPort(){
        return localPort;
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
    
    public void setFTPFileListParser(FTPFileListParser parser){
        ftpFileListParser = parser;
    }
    public FTPFileListParser getFTPFileListParser(){
        return ftpFileListParser;
    }
    
    public void setHomeDirectory(File dir){
        homeDir = dir;
    }
    public File getHomeDirectory(){
        return homeDir;
    }
    
    public void setJavaRegexEnabled(boolean isEnabled){
        isJavaRegexEnabled = isEnabled;
    }
    public boolean isJavaRegexEnabled(){
        return isJavaRegexEnabled;
    }
    
    public void setPassive(boolean isPassive){
        this.isPassive = isPassive;
    }
    public boolean isPassive(){
        return isPassive;
    }
    
    public void setConnectMaxRetryCount(int count){
        connectMaxRetryCount = count;
    }
    public int getConnectMaxRetryCount(){
        return connectMaxRetryCount;
    }
    
    public void setFTPClientProperty(String name, Object value){
        ftpClientProps.put(name, value);
    }
    public Object getFTPClientProperty(String name){
        return ftpClientProps.get(name);
    }
    public void removeFTPClientProperty(String name){
        ftpClientProps.remove(name);
    }
    public void clearFTPClientProperties(){
        ftpClientProps.clear();
    }
    public Map getFTPClientProperties(){
        return ftpClientProps;
    }
    
    public void startService() throws Exception{
        if(homeDir != null && !homeDir.exists()){
            if(!homeDir.mkdirs()){
                throw new IllegalArgumentException("HomeDirectory could not make directory.");
            }
        }
    }
    
    public FTPClient createFTPClient() throws FTPException{
        org.apache.commons.net.ftp.FTPClient ftpClient
            = new org.apache.commons.net.ftp.FTPClient();
        if(serverSocketFactory != null || socketFactory != null){
            ftpClient.setSocketFactory(
                new SocketFactoryImpl(socketFactory, serverSocketFactory)
            );
        }
        if(ftpClientProps.size() != 0){
            final PropertyAccess access = PropertyAccess.getInstance(true);
            final String[] names = (String[])ftpClientProps.keySet()
                .toArray(new String[ftpClientProps.size()]);
            try{
                for(int i = 0; i < names.length; i++){
                    access.set(ftpClient, names[i], ftpClientProps.get(names[i]));
                }
            }catch(IllegalArgumentException e){
                throw new FTPException(e);
            }catch(NoSuchPropertyException e){
                throw new FTPException(e);
            }catch(InvocationTargetException e){
                throw new FTPException(e.getTargetException());
            }
        }
        
        FTPClientImpl client = new FTPClientImpl(ftpClient);
        if(ftpFileListParser != null){
            client.setFTPFileListParser(ftpFileListParser);
        }
        if(homeDir != null){
            client.setHomeDirectory(homeDir);
        }else if(System.getProperty("user.home") != null){
            client.setHomeDirectory(new File(System.getProperty("user.home")));
        }
        client.setJavaRegexEnabled(isJavaRegexEnabled);
        client.setConnectMaxRetryCount(connectMaxRetryCount);
        client.setSoTimeout(soTimeout);
        client.setSoLinger(soLinger);
        if(isTcpNoDelay != null){
            client.setTcpNoDelay(isTcpNoDelay.booleanValue());
        }
        
        if(hostName != null){
            if(port >= 0){
                if(bindAddress != null){
                    if(localPort >= 0){
                        client.connect(hostName, port, bindAddress, localPort);
                    }else{
                        client.connect(hostName, port, bindAddress, 0);
                    }
                }else{
                    if(localPort >= 0){
                        client.connect(hostName, port, "localhost", localPort);
                    }else{
                        client.connect(hostName, port);
                    }
                }
            }else{
                if(bindAddress != null){
                    if(localPort >= 0){
                        client.connect(hostName, 21, bindAddress, localPort);
                    }else{
                        client.connect(hostName, 21, bindAddress, 0);
                    }
                }else{
                    if(localPort >= 0){
                        client.connect(hostName, 21, "localhost", localPort);
                    }else{
                        client.connect(hostName, 21);
                    }
                }
            }
            
            if(userName != null){
                client.login(userName, password);
            }
            if(isPassive){
                client.passive();
            }
        }
        
        return client;
    }
    
    private class SocketFactoryImpl
     implements org.apache.commons.net.SocketFactory{
        
        private SocketFactory socketFactory;
        private ServerSocketFactory serverSocketFactory;
        
        public SocketFactoryImpl(
            SocketFactory socketFactory, 
            ServerSocketFactory serverSocketFactory
        ){
            this.socketFactory = socketFactory == null ? SocketFactory.getDefault() : socketFactory;
            this.serverSocketFactory = serverSocketFactory == null ? ServerSocketFactory.getDefault() : serverSocketFactory;
        }
        
        public Socket createSocket(String host, int port)
         throws UnknownHostException, IOException{
            return socketFactory.createSocket(host, port);
        }
        
        public Socket createSocket(InetAddress address, int port)
         throws IOException{
            return socketFactory.createSocket(address, port);
        }
        
        public Socket createSocket(
            String host,
            int port,
            InetAddress localAddr,
            int localPort
        ) throws UnknownHostException, IOException{
            return socketFactory.createSocket(host, port, localAddr, localPort);
        }
        
        public Socket createSocket(
            InetAddress addr,
            int port,
            InetAddress localAddr,
            int localPort
        ) throws IOException{
            return socketFactory.createSocket(addr, port, localAddr, localPort);
        }
        
        public ServerSocket createServerSocket(int port) throws IOException{
            return serverSocketFactory.createServerSocket(port);
        }
        
        public ServerSocket createServerSocket(int port, int backlog)
         throws IOException{
            return serverSocketFactory.createServerSocket(port, backlog);
        }
        
        public ServerSocket createServerSocket(
            int port,
            int backlog,
            InetAddress bindAddr
        ) throws IOException{
            return serverSocketFactory.createServerSocket(port, backlog, bindAddr);
        }
    }
}
