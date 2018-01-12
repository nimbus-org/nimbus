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
package jp.ossc.nimbus.util.net;

import java.util.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import javax.net.ssl.SSLServerSocket;

import jp.ossc.nimbus.beans.*;

/**
 * SSLサーバソケットラッパー。<p>
 * このサーバソケットでaccept()されたソケットは、{@link #setSocketProperty(String, Object)}で、予め設定されたプロパティが設定される。<br>
 *
 * @author M.Takata
 * @see ServerSocketFactory
 */
public class SSLServerSocketWrapper extends SSLServerSocket{
    
    protected SSLServerSocket serverSocket;
    
    protected Map socketProperties;
    
    public SSLServerSocketWrapper(SSLServerSocket serverSock) throws IOException{
        super();
        serverSocket = serverSock;
    }
    
    public void bind(SocketAddress endpoint) throws IOException{
        serverSocket.bind(endpoint);
    }
    
    public void bind(SocketAddress endpoint, int backlog) throws IOException{
        serverSocket.bind(endpoint, backlog);
    }
    
    public InetAddress getInetAddress(){
        return serverSocket.getInetAddress();
    }
    
    public int getLocalPort(){
        return serverSocket.getLocalPort();
    }
    
    public SocketAddress getLocalSocketAddress(){
        return serverSocket.getLocalSocketAddress();
    }
    
    public void close() throws IOException{
        serverSocket.close();
    }
    
    public ServerSocketChannel getChannel(){
        return serverSocket.getChannel();
    }
    
    public boolean isBound(){
        return serverSocket.isBound();
    }
    
    public boolean isClosed(){
        return serverSocket.isClosed();
    }
    
    public void setSoTimeout(int timeout) throws SocketException{
        serverSocket.setSoTimeout(timeout);
    }
    
    public int getSoTimeout() throws IOException{
        return serverSocket.getSoTimeout();
    }
    
    public void setReuseAddress(boolean on) throws SocketException{
        serverSocket.setReuseAddress(on);
    }
    
    public boolean getReuseAddress() throws SocketException{
        return serverSocket.getReuseAddress();
    }
    
    public void setReceiveBufferSize(int size) throws SocketException{
        serverSocket.setReceiveBufferSize(size);
    }
    
    public int getReceiveBufferSize() throws SocketException{
        return serverSocket.getReceiveBufferSize();
    }
    
    public void setPerformancePreferences(
        int connectionTime,
        int latency,
        int bandwidth
    ){
        try{
            Method method = serverSocket.getClass().getMethod(
                "setPerformancePreferences",
                new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE}
            );
            method.invoke(
                serverSocket,
                new Object[]{
                    new Integer(connectionTime),
                    new Integer(latency),
                    new Integer(bandwidth)
                }
            );
        }catch(NoSuchMethodException e){
            throw new UnsupportedOperationException(e.toString());
        }catch(IllegalAccessException e){
            throw new UnsupportedOperationException(e.toString());
        }catch(InvocationTargetException e){
            Throwable target = e.getTargetException();
            if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            }else if(target instanceof Error){
                throw (Error)target;
            }else{
                throw new UndeclaredThrowableException(target);
            }
        }
    }
    
    public String toString(){
        return serverSocket.toString();
    }
    
    public Socket accept() throws IOException{
        return applySocketProperties(serverSocket.accept());
    }
    
    public String[] getEnabledCipherSuites(){
        return serverSocket.getEnabledCipherSuites();
    }
    
    public void setEnabledCipherSuites(String[] suites){
        serverSocket.setEnabledCipherSuites(suites);
    }
    
    public String[] getSupportedCipherSuites(){
        return serverSocket.getSupportedCipherSuites();
    }
    
    public String[] getSupportedProtocols(){
        return serverSocket.getSupportedProtocols();
    }
    
    public String[] getEnabledProtocols(){
        return serverSocket.getEnabledProtocols();
    }
    
    public void setEnabledProtocols(String[] protocols){
        serverSocket.setEnabledProtocols(protocols);
    }
    
    public void setNeedClientAuth(boolean need){
        serverSocket.setNeedClientAuth(need);
    }
    
    public boolean getNeedClientAuth(){
        return serverSocket.getNeedClientAuth();
    }
    
    public void setWantClientAuth(boolean want){
        serverSocket.setWantClientAuth(want);
    }
    
    public boolean getWantClientAuth(){
        return serverSocket.getWantClientAuth();
    }
    
    public void setUseClientMode(boolean mode){
        serverSocket.setUseClientMode(mode);
    }
    
    public boolean getUseClientMode(){
        return serverSocket.getUseClientMode();
    }
    
    public void setEnableSessionCreation(boolean flag){
        serverSocket.setEnableSessionCreation(flag);
    }
    
    public boolean getEnableSessionCreation(){
        return serverSocket.getEnableSessionCreation();
    }
    
    /**
     * {@link java.net.Socket}にプロパティを設定する。<p>
     *
     * @param props プロパティマップ
     */
    public void setSocketProperties(Map props){
        if(props == null || props.size() == 0){
            if(socketProperties != null){
                socketProperties = null;
            }
            return;
        }
        final Iterator names = props.keySet().iterator();
        while(names.hasNext()){
            String name = (String)names.next();
            setSocketProperty(name, props.get(name));
        }
    }
    
    /**
     * {@link Socket}にプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setSocketProperty(String name, Object value){
        if(socketProperties == null){
            socketProperties = new LinkedHashMap();
        }
        final Property prop = PropertyFactory.createProperty(name);
        socketProperties.put(prop, value);
    }
    
    /**
     * {@link Socket}のプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getSocketProperty(String name){
        if(socketProperties == null){
            return null;
        }
        final Iterator props = socketProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            if(prop.getPropertyName().equals(name)){
                return socketProperties.get(prop);
            }
        }
        return null;
    }
    
    protected Socket applySocketProperties(
        Socket socket
    ) throws IOException{
        try{
            if(socketProperties != null && socketProperties.size() != 0){
                final Iterator props = socketProperties.keySet().iterator();
                while(props.hasNext()){
                    final Property prop = (Property)props.next();
                    prop.setProperty(socket, socketProperties.get(prop));
                }
            }
        }catch(InvocationTargetException e){
            Throwable target = e.getTargetException();
            if(target instanceof IOException){
                throw (IOException)target;
            }else if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            }else if(target instanceof Error){
                throw (Error)target;
            }else{
                throw new UndeclaredThrowableException(target);
            }
        }catch(NoSuchPropertyException e){
            throw new UndeclaredThrowableException(e);
        }
        return socket;
    }
}